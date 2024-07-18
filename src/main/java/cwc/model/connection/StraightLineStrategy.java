package cwc.model.connection;

import cwc.enums.ArrowType;
import cwc.model.connection.LineStyleStrategy;
import cwc.util.ArrowUtils;

import java.awt.*;
import java.awt.geom.Line2D;
import java.io.Serializable;

public class StraightLineStrategy implements LineStyleStrategy, Serializable {
    // 绘制直线
    @Override
    public void drawLine(Graphics2D g2d, int[] startPoint, int[] endPoint, int thickness, Color color, ArrowType arrowStyle) {
        Line2D line = new Line2D.Float(startPoint[0], startPoint[1], endPoint[0], endPoint[1]);
        g2d.setColor(color);
        g2d.setStroke(new BasicStroke(thickness));
        g2d.draw(line);

        double dx = endPoint[0] - startPoint[0];
        double dy = endPoint[1] - startPoint[1];
        double theta = Math.atan2(dy, dx);

        ArrowUtils.drawArrow(g2d, startPoint, endPoint, theta, theta, false, thickness, arrowStyle);
    }

    @Override
    public boolean isPointNearLine(int mouseX, int mouseY, int[] startPoint, int[] endPoint) {
        Line2D line = new Line2D.Float(startPoint[0], startPoint[1], endPoint[0], endPoint[1]);
        return line.ptSegDist(mouseX, mouseY) <= 5;
    }
}
