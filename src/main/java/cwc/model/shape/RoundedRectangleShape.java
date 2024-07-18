package cwc.model.shape;

import java.awt.*;

public class RoundedRectangleShape extends Shape {
    private int arcWidth;
    private int arcHeight;

    public RoundedRectangleShape(int x, int y, int width, int height, int arcWidth, int arcHeight) {
        super(x, y, width, height);
        this.arcWidth = arcWidth;
        this.arcHeight = arcHeight;
    }

    @Override
    public void draw(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(color);
        g2d.fillRoundRect(x, y, width, height, arcWidth, arcHeight);
        g2d.setColor(borderColor);
        g2d.setStroke(new BasicStroke(borderWidth));
        g2d.drawRoundRect(x, y, width, height, arcWidth, arcHeight);
        g2d.setColor(textColor);
        FontMetrics fm = g.getFontMetrics();
        int stringWidth = fm.stringWidth(text);
        g.drawString(text, x + (width - stringWidth) / 2, y + (fm.getAscent() + height) / 2);
    }

    @Override
    public boolean contains(int x, int y) {
        return x >= this.x && x <= this.x + width && y >= this.y && y <= this.y + height;
    }

    public int getArcWidth() {
        return arcWidth;
    }

    public void setArcWidth(int arcWidth) {
        this.arcWidth = arcWidth;
    }

    public int getArcHeight() {
        return arcHeight;
    }

    public void setArcHeight(int arcHeight) {
        this.arcHeight = arcHeight;
    }
}
