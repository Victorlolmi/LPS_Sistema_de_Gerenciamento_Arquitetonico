package view.components;

import java.awt.*;
import java.awt.geom.GeneralPath;
import javax.swing.border.Border;

public class RoundedBorder implements Border {

    private int radius;
    private Color color;
    private int thickness;
    private boolean topLeft, topRight, bottomLeft, bottomRight;

    public RoundedBorder(Color color, int thickness, int radius, boolean tl, boolean tr, boolean bl, boolean br) {
        this.color = color;
        this.thickness = thickness;
        this.radius = radius;
        this.topLeft = tl;
        this.topRight = tr;
        this.bottomLeft = bl;
        this.bottomRight = br;
    }

    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(color);
        g2.setStroke(new BasicStroke(thickness));

        GeneralPath path = createPath(x, y, width, height);
        g2.draw(path); // Desenha o caminho
        g2.dispose();
    }

    // Método auxiliar para criar o caminho da borda
    private GeneralPath createPath(int x, int y, int width, int height) {
        GeneralPath path = new GeneralPath();
        int r = this.radius;
        int t = this.thickness;
        int w = width - t;
        int h = height - t;
        int off = t / 2;

        path.moveTo(x + off + (topLeft ? r : 0), y + off);
        path.lineTo(x + w - (topRight ? r : 0), y + off);
        if (topRight) path.quadTo(x + w, y + off, x + w, y + off + r);

        path.lineTo(x + w, y + h - (bottomRight ? r : 0));
        if (bottomRight) path.quadTo(x + w, y + h, x + w - r, y + h);

        path.lineTo(x + off + (bottomLeft ? r : 0), y + h);
        if (bottomLeft) path.quadTo(x + off, y + h, x + off, y + h - r);

        path.lineTo(x + off, y + off + (topLeft ? r : 0));
        if (topLeft) path.quadTo(x + off, y + off, x + off + r, y + off);

        path.closePath();
        return path;
    }

    @Override
    public Insets getBorderInsets(Component c) {
        return new Insets(this.thickness, this.thickness, this.thickness, this.thickness);
    }

    @Override
    public boolean isBorderOpaque() {
        return false;
    }
}