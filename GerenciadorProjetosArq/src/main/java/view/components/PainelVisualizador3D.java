/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package view.components;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;       
import javafx.scene.shape.Cylinder;  
import javafx.scene.shape.DrawMode;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.TriangleMesh;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import javafx.scene.SceneAntialiasing;

// IMPORTS SWING/AWT/IO
import javax.swing.JPanel;
import javax.swing.JOptionPane;
import java.awt.BorderLayout;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
/**
 *
 * @author Viktin
 */
public class PainelVisualizador3D extends JPanel {

    static {
        Platform.setImplicitExit(false);
    }

    private final JFXPanel jfxPanel;
    private Group root3D;       // Raiz da cena
    private Group modelGroup;   // Grupo específico do modelo (para rotacionar só ele)
    private PerspectiveCamera camera;
    
    // Controles de Câmera e Mouse
    private double mouseOldX, mouseOldY;
    private final Rotate rotateX = new Rotate(-20, Rotate.X_AXIS);
    private final Rotate rotateY = new Rotate(-45, Rotate.Y_AXIS);
    private final Translate translateZ = new Translate(0, 0, -800); // Zoom inicial

    public PainelVisualizador3D() {
        setLayout(new BorderLayout());
        jfxPanel = new JFXPanel(); // Ponte Swing <-> JavaFX
        add(jfxPanel, BorderLayout.CENTER);
        
        // Inicia a thread gráfica
        Platform.runLater(this::criarCena3D);
    }

    private void criarCena3D() {
        root3D = new Group();
        modelGroup = new Group(); 
        
        // Aplica rotação no GRUPO DO MODELO (mais intuitivo que girar a câmera)
        modelGroup.getTransforms().addAll(rotateY, rotateX);
        
        root3D.getChildren().add(modelGroup);

        // Configuração da Câmera
        camera = new PerspectiveCamera(true);
        camera.setNearClip(0.1);
        camera.setFarClip(100000.0); // Alcance longo para projetos grandes
        camera.getTransforms().add(translateZ); // Zoom move a câmera

        // Cena com fundo cinza escuro (melhor contraste para 3D)
        Scene scene = new Scene(root3D, 800, 600, true, SceneAntialiasing.BALANCED);
        scene.setFill(Color.rgb(50, 50, 50)); 
        scene.setCamera(camera);

        configurarControlesMouse(scene);
        
        // Inicia com a simulação (para não ficar tela preta)
        carregarSimulacao();

        jfxPanel.setScene(scene);
    }

   public void carregarModelo(String caminhoArquivo) {
        if (caminhoArquivo == null || caminhoArquivo.isEmpty()) return;

        // Mostra cursor de espera
        this.setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.WAIT_CURSOR));

        // Roda em uma thread separada para não travar a tela
        new Thread(() -> {
            try {
                System.out.println("Iniciando leitura do arquivo: " + caminhoArquivo);
                File arquivo = new File(caminhoArquivo);

                if (arquivo.exists() && arquivo.getName().toLowerCase().endsWith(".obj")) {
                    
                    // Processamento pesado (Parser)
                    Group modeloImportado;
                    try {
                         modeloImportado = ImportadorObjAvancado.carregar(caminhoArquivo);
                    } catch (Exception e) {
                        e.printStackTrace();
                        throw new RuntimeException("Falha ao interpretar o arquivo OBJ: " + e.getMessage());
                    }
                    
                    // Atualização da UI (Volta para a thread gráfica)
                    Platform.runLater(() -> {
                        try {
                            modelGroup.getChildren().clear();
                           
                            modelGroup.getChildren().add(modeloImportado);
                            
                            // Reset Câmera
                            translateZ.setZ(-800);
                            rotateX.setAngle(-20);
                            rotateY.setAngle(-45);
                            
                            System.out.println("Modelo carregado e adicionado à cena.");
                            
                        } catch (Exception e) {
                            e.printStackTrace();
                            JOptionPane.showMessageDialog(this, "Erro ao renderizar modelo: " + e.getMessage());
                        }
                    });
                    
                } else {
                    Platform.runLater(() -> {
                        JOptionPane.showMessageDialog(this, "Arquivo inválido ou não encontrado: " + caminhoArquivo);
                        carregarSimulacao();
                    });
                }
            } catch (Exception e) {
                e.printStackTrace(); // Mostra o erro no console do NetBeans
                Platform.runLater(() -> {
                    JOptionPane.showMessageDialog(this, "Erro crítico ao carregar 3D:\n" + e.getMessage());
                    carregarSimulacao();
                });
            } finally {
                // Restaura o cursor
                this.setCursor(java.awt.Cursor.getDefaultCursor());
            }
        }).start();
    }
    private void carregarSimulacao() {
        modelGroup.getChildren().clear();
       
        modelGroup.getChildren().add(criarCasaExemplo());
    }
    
    

    private Group criarCasaExemplo() {
        Group casa = new Group();
        // Cubo Base
        Box base = new Box(150, 150, 150);
        base.setMaterial(new PhongMaterial(Color.CADETBLUE));
        // Telhado
        Cylinder telhado = new Cylinder(120, 80); 
        telhado.setMaterial(new PhongMaterial(Color.BROWN));
        telhado.setTranslateY(-115); 
        
        casa.getChildren().addAll(base, telhado);
        return casa;
    }

    private void configurarControlesMouse(Scene scene) {
        scene.setOnMousePressed((MouseEvent event) -> {
            mouseOldX = event.getSceneX();
            mouseOldY = event.getSceneY();
        });

        scene.setOnMouseDragged((MouseEvent event) -> {
            // Botão Esquerdo: Rotaciona o objeto
            if (event.isPrimaryButtonDown()) {
                rotateY.setAngle(rotateY.getAngle() + (event.getSceneX() - mouseOldX));
                rotateX.setAngle(rotateX.getAngle() - (event.getSceneY() - mouseOldY));
            }
            // Botão Direito: Pan (Opcional - mover câmera lateralmente)
            // else if (event.isSecondaryButtonDown()) { ... }
            
            mouseOldX = event.getSceneX();
            mouseOldY = event.getSceneY();
        });

        scene.setOnScroll((ScrollEvent event) -> {
            double zoomFactor = 1.05;
            if (event.getDeltaY() < 0) {
                translateZ.setZ(translateZ.getZ() * zoomFactor); // Afasta
            } else {
                translateZ.setZ(translateZ.getZ() / zoomFactor); // Aproxima
            }
        });
    }
}