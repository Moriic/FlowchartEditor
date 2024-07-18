package cwc.model.shape;

import java.awt.*;

public class ParallelogramShape extends Shape {
    public ParallelogramShape(int x, int y, int width, int height) {
        super(x, y, width, height);
    }

    @Override
    public void draw(Graphics g) {
        int offsetX = width / 4; // Horizontal offset for the parallelogram slant
        int[] xPoints = {x + offsetX, x, x + width - offsetX, x + width};
        int[] yPoints = {y, y + height, y + height, y};

        g.setColor(color);
        g.fillPolygon(xPoints, yPoints, 4);
        g.setColor(borderColor); // Use border color to draw the border
        Graphics2D g2d = (Graphics2D) g;
        g2d.setStroke(new BasicStroke(borderWidth)); // Set border width
        g.drawPolygon(xPoints, yPoints, 4);

        g.setColor(textColor);
        // Get font-related information
        FontMetrics fm = g.getFontMetrics();
        int stringWidth = fm.stringWidth(text);
        g.drawString(text, x + (width - stringWidth) / 2, y + (fm.getAscent() + height) / 2);
    }

    @Override
    public boolean contains(int x, int y) {
        // Create a slightly larger bounding rectangle for more lenient hit detection
        int buffer = 10; // Adjust this buffer size to make detection more lenient
        Rectangle boundingRect = new Rectangle(this.x - buffer, this.y - buffer, width + 2 * buffer, height + 2 * buffer);

        return boundingRect.contains(x, y);
    }
}
