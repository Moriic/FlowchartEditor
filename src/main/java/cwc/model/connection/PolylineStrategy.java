package cwc.model.connection;

import cwc.enums.ArrowType;
import cwc.util.ArrowUtils;


import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.io.Serializable;

public class PolylineStrategy implements LineStyleStrategy, Serializable {
    // 绘制折线
    @Override
    public void drawLine(Graphics2D g2d, int[] startPoint, int[] endPoint, int thickness, Color color, ArrowType arrowStyle) {
        Path2D path = new Path2D.Float();
        path.moveTo(startPoint[0], startPoint[1]);
        int midX = (startPoint[0] + endPoint[0]) / 2;
        path.lineTo(midX, startPoint[1]);
        path.lineTo(midX, endPoint[1]);
        path.lineTo(endPoint[0], endPoint[1]);
        g2d.setColor(color);
        g2d.setStroke(new BasicStroke(thickness));
        g2d.draw(path);

        double dx = endPoint[0] - midX;
        double dy = endPoint[1] - endPoint[1];
        double theta = Math.atan2(dy, dx);

        ArrowUtils.drawArrow(g2d, startPoint, endPoint, theta, theta, true, thickness, arrowStyle);
    }

    @Override
    public boolean isPointNearLine(int mouseX, int mouseY, int[] startPoint, int[] endPoint) {
        int midX = (startPoint[0] + endPoint[0]) / 2;
        Line2D segment1 = new Line2D.Float(startPoint[0], startPoint[1], midX, startPoint[1]);
        Line2D segment2 = new Line2D.Float(midX, startPoint[1], midX, endPoint[1]);
        Line2D segment3 = new Line2D.Float(midX, endPoint[1], endPoint[0], endPoint[1]);
        return segment1.ptSegDist(mouseX, mouseY) <= 5 ||
                segment2.ptSegDist(mouseX, mouseY) <= 5 ||
                segment3.ptSegDist(mouseX, mouseY) <= 5;
    }
}
