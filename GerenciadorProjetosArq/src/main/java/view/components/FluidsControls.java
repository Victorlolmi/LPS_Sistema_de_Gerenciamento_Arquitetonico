/*
 * Classe de controle de movimentação fluida para Java Swing
 */
package view.components;

import java.awt.Component;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * @author Viktin
 */
public class FluidsControls extends KeyAdapter {
    
    // Configurações de Física
    private final double speed = 0.5;      // Aceleração
    private final double friction = 0.92;  // Atrito (0.90 a 0.95 é bom)
    
    // Estado de Velocidade (usamos double para precisão, depois convertemos pra int)
    private double velX = 0;
    private double velY = 0;
    
    // Posição sub-pixel (necessário para suavidade antes de renderizar no pixel inteiro)
    private double currentX = 0;
    private double currentY = 0;

    // Estado das Teclas
    private boolean isW, isA, isS, isD;

    /**
     * Construtor: Vincula o ouvinte de teclado ao componente
     * @param target O componente que receberá o foco (ex: o JPanel do jogo ou o JFrame)
     */
    public FluidsControls(Component target) {
        target.addKeyListener(this);
        // Garante que o componente possa ouvir o teclado
        target.setFocusable(true);
        target.requestFocusInWindow();
        
        // Pega a posição inicial do componente
        this.currentX = target.getX();
        this.currentY = target.getY();
    }

    /**
     * Método Update: Deve ser chamado dentro de um javax.swing.Timer
     * @param target O objeto visual que vai se mover
     */
    public void update(Component target) {
        // 1. Aceleração baseada no input
        if (isW) velY -= speed; // No Swing, Y diminui pra cima
        if (isS) velY += speed;
        if (isA) velX -= speed;
        if (isD) velX += speed;

        // 2. Aplicar Física (Atrito)
        velX *= friction;
        velY *= friction;

        // 3. Otimização: Parar totalmente se a velocidade for irrelevante
        if (Math.abs(velX) < 0.05) velX = 0;
        if (Math.abs(velY) < 0.05) velY = 0;

        // 4. Atualizar Posição Matemática
        currentX += velX;
        currentY += velY;

        // 5. Aplicar visualmente no componente (Swing usa Inteiros para pixels)
        target.setLocation((int) Math.round(currentX), (int) Math.round(currentY));
    }

    // --- Tratamento de Eventos de Teclado (Nativo do Java) ---

    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        
        if (key == KeyEvent.VK_W) isW = true;
        if (key == KeyEvent.VK_S) isS = true;
        if (key == KeyEvent.VK_A) isA = true;
        if (key == KeyEvent.VK_D) isD = true;
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();
        
        if (key == KeyEvent.VK_W) isW = false;
        if (key == KeyEvent.VK_S) isS = false;
        if (key == KeyEvent.VK_A) isA = false;
        if (key == KeyEvent.VK_D) isD = false;
    }
}