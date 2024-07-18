package cwc.model.shape;

import java.awt.*;

public class EllipseShape extends Shape {
    public EllipseShape(int x, int y, int width, int height) {
        super(x, y, width, height);
    }

    @Override
    public void draw(Graphics g) {
        g.setColor(color);
        g.fillOval(x, y, width, height);
        g.setColor(borderColor); // 使用边框颜色绘制边框
        Graphics2D g2d = (Graphics2D) g;
        g2d.setStroke(new BasicStroke(borderWidth)); // 设置边框粗细
        g.drawOval(x, y, width, height);
        g.setColor(textColor);
        // 获取字体相关的信息
        FontMetrics fm = g.getFontMetrics();
        int stringWidth = fm.stringWidth(text);
        g.drawString(text,  x + (width - stringWidth) / 2, y + (fm.getAscent() + height) / 2);
    }

    @Override
    public boolean contains(int x, int y) {
        int dx = x - (this.x + width / 2);
        int dy = y - (this.y + height / 2);
        return (dx * dx) / (width * width / 4.0) + (dy * dy) / (height * height / 4.0) <= 2;
    }
}
