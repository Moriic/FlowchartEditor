package cwc.model.shape;

import java.awt.*;

public class DiamondShape extends Shape {
    public DiamondShape(int x, int y, int width, int height) {
        super(x, y, width, height);
    }

    @Override
    public void draw(Graphics g) {
        int[] xPoints = {x, x + width / 2, x + width, x + width / 2};
        int[] yPoints = {y + height / 2, y, y + height / 2, y + height};
        g.setColor(color);
        g.fillPolygon(xPoints, yPoints, 4);
        g.setColor(borderColor); // 使用边框颜色绘制边框
        Graphics2D g2d = (Graphics2D) g;
        g2d.setStroke(new BasicStroke(borderWidth)); // 设置边框粗细
        g.drawPolygon(xPoints, yPoints, 4);
        g.setColor(textColor);
        // 获取字体相关的信息
        FontMetrics fm = g.getFontMetrics();
        int stringWidth = fm.stringWidth(text);
        g.drawString(text, x + (width - stringWidth) / 2, y + (fm.getAscent() + height) / 2);
    }

    @Override
    public boolean contains(int x, int y) {
        // 创建一个稍微比实际菱形大的矩形区域
        int buffer = 10; // 可以调整这个缓冲区大小，使得判定更宽松
        Rectangle boundingRect = new Rectangle(this.x - buffer, this.y - buffer, width + 2 * buffer, height + 2 * buffer);

        return boundingRect.contains(x, y);
    }

}