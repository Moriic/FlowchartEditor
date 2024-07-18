package cwc.model.shape;

import java.awt.*;

public class RectangleShape extends Shape {
    public RectangleShape(int x, int y, int width, int height) {
        super(x, y, width, height);
    }

    @Override
    public void draw(Graphics g) {
        g.setColor(color);
        g.fillRect(x, y, width, height);
        g.setColor(borderColor); // 使用边框颜色绘制边框
        Graphics2D g2d = (Graphics2D) g;
        g2d.setStroke(new BasicStroke(borderWidth)); // 设置边框粗细
        g.drawRect(x, y, width, height);
        g.setColor(textColor);
        // 获取字体相关的信息
        FontMetrics fm = g.getFontMetrics();
        int stringWidth = fm.stringWidth(text);
        g.drawString(text,  x + (width - stringWidth) / 2, y + (fm.getAscent() + height) / 2);
    }
    @Override
    public boolean contains(int x, int y) {
        return x >= this.x - 10 && x <= this.x + width + 5 && y >= this.y - 5 && y <= this.y + height + 5;
    }

}


