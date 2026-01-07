/*
 * Classe PainelVisualizador3D.java
 * Funcionalidades: 
 * - WASD: Move a câmera (Anda)
 * - Setas CIMA/BAIXO: Move a câmera verticalmente (Levita)
 * - Setas ESQUERDA/DIREITA: Gira o mundo na Horizontal (Y)
 * - Q e E: Gira o mundo na Vertical (X) -> Vira de cabeça pra baixo
 */
package view.components;

import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.Cylinder;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.io.File;

// Importe sua classe utils se necessário
// import utils.ImportadorObjAvancado;

/**
 * Visualizador 3D com Rotação de Mundo em 2 Eixos.
 * @author Viktin
 */
public class PainelVisualizador3D extends JPanel {

    static {
        Platform.setImplicitExit(false);
    }

    private final JFXPanel jfxPanel;
    private Group root3D;
    private PerspectiveCamera camera;
    
    // --- TRANSFORMAÇÕES DA CÂMERA ---
    private final Rotate cameraRotateX = new Rotate(0, Rotate.X_AXIS);
    private final Rotate cameraRotateY = new Rotate(0, Rotate.Y_AXIS);
    private final Translate cameraTranslate = new Translate(0, -50, -500);

    // --- TRANSFORMAÇÕES DO MUNDO (NOVO) ---
    // Rotate Y = Esquerda/Direita
    // Rotate X = Q/E (Cambalhota)
    private final Rotate worldRotateY = new Rotate(0, Rotate.Y_AXIS);
    private final Rotate worldRotateX = new Rotate(0, Rotate.X_AXIS);

    // --- CONTROLES ---
    private double mouseOldX, mouseOldY;
    
    // Flags de teclas
    private boolean isW, isS, isA, isD, isShift;
    private boolean isUp, isDown;       
    private boolean isLeft, isRight; // Rotação Y
    private boolean isQ, isE;        // Rotação X

    // --- FÍSICA ---
    private static final double SENSIBILIDADE_MOUSE = 0.2;
    private static final double ACELERACAO_MOVIMENTO = 0.7;        
    private static final double ACELERACAO_ROTACAO_MUNDO = 2.0; 
    private static final double ATRITO = 0.90;            
    private static final double VELOCIDADE_MAX = 0.7;    
    
    // Velocidades atuais
    private double velX = 0, velZ = 0, velY = 0; 
    private double velWorldRotY = 0;              
    private double velWorldRotX = 0; // Velocidade da nova rotação

    public PainelVisualizador3D() {
        setLayout(new BorderLayout());
        jfxPanel = new JFXPanel();
        add(jfxPanel, BorderLayout.CENTER);
        
        Platform.runLater(this::criarCena3D);
    }

    private void criarCena3D() {
        root3D = new Group();
        
        // Aplica as rotações ao MUNDO (Ordem importa: Y depois X funciona bem para inspeção)
        root3D.getTransforms().addAll(worldRotateY, worldRotateX);
        
        // Câmera
        camera = new PerspectiveCamera(true);
        camera.setNearClip(0.1);
        camera.setFarClip(100000.0);
        camera.getTransforms().addAll(cameraTranslate, cameraRotateY, cameraRotateX);

        // Cena
        Scene scene = new Scene(root3D, 800, 600, true, SceneAntialiasing.BALANCED);
        scene.setFill(Color.rgb(40, 40, 40)); 
        scene.setCamera(camera);

        configurarControles(scene);
        iniciarLoopFisica();
        carregarSimulacao();

        jfxPanel.setScene(scene);
    }

    private void configurarControles(Scene scene) {
        
        // Mouse Look 
        scene.setOnMousePressed((MouseEvent event) -> {
            mouseOldX = event.getSceneX();
            mouseOldY = event.getSceneY();
            this.requestFocusInWindow(); 
            jfxPanel.requestFocus();     
        });

        scene.setOnMouseDragged((MouseEvent event) -> {
            double deltaX = (event.getSceneX() - mouseOldX);
            double deltaY = (event.getSceneY() - mouseOldY);

            cameraRotateY.setAngle(cameraRotateY.getAngle() + deltaX * SENSIBILIDADE_MOUSE);
            cameraRotateX.setAngle(cameraRotateX.getAngle() - deltaY * SENSIBILIDADE_MOUSE);

            mouseOldX = event.getSceneX();
            mouseOldY = event.getSceneY();
        });

        // --- TECLADO ---
        scene.setOnKeyPressed((KeyEvent event) -> {
            switch (event.getCode()) {
                // Movimento Câmera
                case W -> isW = true;
                case S -> isS = true;
                case A -> isA = true;
                case D -> isD = true;
                case SHIFT -> isShift = true;
                
                // Altitude Câmera
                case UP, SPACE -> isUp = true;       
                case DOWN, CONTROL -> isDown = true; 
                
                // Rotação Mundo Horizontal
                case LEFT -> isLeft = true;
                case RIGHT -> isRight = true;

                // Rotação Mundo Vertical (Q/E)
                case Q -> isQ = true;
                case E -> isE = true;
            }
        });

        scene.setOnKeyReleased((KeyEvent event) -> {
            switch (event.getCode()) {
                case W -> isW = false;
                case S -> isS = false;
                case A -> isA = false;
                case D -> isD = false;
                case SHIFT -> isShift = false;
                
                case UP, SPACE -> isUp = false;
                case DOWN, CONTROL -> isDown = false;
                
                case LEFT -> isLeft = false;
                case RIGHT -> isRight = false;

                case Q -> isQ = false;
                case E -> isE = false;
            }
        });
        
        // Scroll 
        scene.setOnScroll((ScrollEvent event) -> {
            double forca = (event.getDeltaY() > 0 ? 1 : -1) * 50.0;
            aplicarForcaFrente(forca);
        });
    }

    private void iniciarLoopFisica() {
        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                atualizarFisica();
            }
        };
        timer.start();
    }

    private void atualizarFisica() {
        double accelAtual = isShift ? ACELERACAO_MOVIMENTO * 1 : ACELERACAO_MOVIMENTO;

        // 1. ROTAÇÃO DO MUNDO HORIZONTAL (Setas)
        if (isLeft)  velWorldRotY += ACELERACAO_ROTACAO_MUNDO;
        if (isRight) velWorldRotY -= ACELERACAO_ROTACAO_MUNDO;

        // 2. ROTAÇÃO DO MUNDO VERTICAL (Q/E)
        if (isQ) velWorldRotX += ACELERACAO_ROTACAO_MUNDO; // Gira pra frente
        if (isE) velWorldRotX -= ACELERACAO_ROTACAO_MUNDO; // Gira pra trás

        // 3. MOVIMENTO HORIZONTAL DA CÂMERA (WASD)
        if (isW) aplicarForcaFrente(accelAtual);
        if (isS) aplicarForcaFrente(-accelAtual);
        if (isA) aplicarForcaLateral(-accelAtual);
        if (isD) aplicarForcaLateral(accelAtual);
        
        // 4. MOVIMENTO VERTICAL DA CÂMERA (Setas Cima/Baixo)
        double forcaVertical = accelAtual * 0.3; 
        if (isUp)   velY -= forcaVertical; 
        if (isDown) velY += forcaVertical;

        // --- APLICAÇÃO DE ATRITO ---
        velX *= ATRITO;
        velZ *= ATRITO;
        velY *= ATRITO;
        velWorldRotY *= ATRITO;
        velWorldRotX *= ATRITO;

        // Deadzone
        if (Math.abs(velX) < 0.01) velX = 0;
        if (Math.abs(velZ) < 0.01) velZ = 0;
        if (Math.abs(velY) < 0.01) velY = 0;
        if (Math.abs(velWorldRotY) < 0.01) velWorldRotY = 0;
        if (Math.abs(velWorldRotX) < 0.01) velWorldRotX = 0;

        // --- ATUALIZA POSIÇÕES ---
        // Câmera
        cameraTranslate.setX(cameraTranslate.getX() + velX);
        cameraTranslate.setZ(cameraTranslate.getZ() + velZ);
        cameraTranslate.setY(cameraTranslate.getY() + velY);
        
        // Mundo
        worldRotateY.setAngle(worldRotateY.getAngle() + velWorldRotY * 0.1); 
        worldRotateX.setAngle(worldRotateX.getAngle() + velWorldRotX * 0.1); 
    }

    private void aplicarForcaFrente(double forca) {
        double anguloRadianos = Math.toRadians(cameraRotateY.getAngle());
        velX += Math.sin(anguloRadianos) * forca;
        velZ += Math.cos(anguloRadianos) * forca;
        limitarVelocidade();
    }

    private void aplicarForcaLateral(double forca) {
        double anguloRadianos = Math.toRadians(cameraRotateY.getAngle());
        velX += Math.cos(anguloRadianos) * forca;
        velZ += -Math.sin(anguloRadianos) * forca;
        limitarVelocidade();
    }
    
    private void limitarVelocidade() {
        double max = isShift ? VELOCIDADE_MAX * 1 : VELOCIDADE_MAX;
        if (velX > max) velX = max;
        if (velX < -max) velX = -max;
        if (velZ > max) velZ = max;
        if (velZ < -max) velZ = -max;
    }

    // --- CARREGAMENTO ---
    public void carregarModelo(String caminhoArquivo) {
        if (caminhoArquivo == null || caminhoArquivo.isEmpty()) {
            Platform.runLater(this::carregarSimulacao);
            return;
        }

        this.setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.WAIT_CURSOR));

        new Thread(() -> {
            try {
                File arquivo = new File(caminhoArquivo);
                if (arquivo.exists() && arquivo.getName().toLowerCase().endsWith(".obj")) {
                    Group modelo = ImportadorObjAvancado.carregar(caminhoArquivo);
                    
                    Platform.runLater(() -> {
                        root3D.getChildren().clear();
                        root3D.getChildren().add(modelo);
                        resetarCena();
                    });
                } else {
                    Platform.runLater(() -> {
                        JOptionPane.showMessageDialog(this, "Arquivo inválido.");
                        carregarSimulacao();
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
                Platform.runLater(() -> {
                    JOptionPane.showMessageDialog(this, "Erro: " + e.getMessage());
                    carregarSimulacao();
                });
            } finally {
                this.setCursor(java.awt.Cursor.getDefaultCursor());
            }
        }).start();
    }

    private void carregarSimulacao() {
        root3D.getChildren().clear();
        root3D.getChildren().add(criarCasaExemplo());
        resetarCena();
    }
    
    private void resetarCena() {
        // Reseta câmera
        cameraTranslate.setX(0); 
        cameraTranslate.setY(-100); 
        cameraTranslate.setZ(-500);
        cameraRotateX.setAngle(-15); 
        cameraRotateY.setAngle(-30);
        
        // Reseta rotações do mundo
        worldRotateY.setAngle(0);
        worldRotateX.setAngle(0);
        
        // Zera velocidades
        velX = 0; velZ = 0; velY = 0; 
        velWorldRotY = 0; velWorldRotX = 0;
    }

    private Group criarCasaExemplo() {
        Group casa = new Group();
        Box chao = new Box(1000, 2, 1000);
        chao.setMaterial(new PhongMaterial(Color.DARKGRAY));
        chao.setTranslateY(75);
        
        Box base = new Box(150, 150, 150);
        base.setMaterial(new PhongMaterial(Color.CADETBLUE));
        
        Cylinder telhado = new Cylinder(120, 80);  
        telhado.setMaterial(new PhongMaterial(Color.BROWN));
        telhado.setTranslateY(-115);  
        
        casa.getChildren().addAll(chao, base, telhado);
        return casa;
    }
}