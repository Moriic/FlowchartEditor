package cwc.model.connection;

import cwc.enums.ArrowType;
import cwc.util.ArrowUtils;

import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.QuadCurve2D;
import java.io.Serializable;

public class CurveStrategy implements LineStyleStrategy, Serializable {
    // 绘制曲线
    public void drawLine(Graphics2D g2d, int[] startPoint, int[] endPoint, int thickness, Color color, ArrowType arrowStyle) {
        int controlPointX = (startPoint[0] + endPoint[0]) / 2;
        int controlPointY = (startPoint[1] + endPoint[1]) / 2 - 50;

        Path2D path = new Path2D.Float();
        path.moveTo(startPoint[0], startPoint[1]);
        path.quadTo(controlPointX, controlPointY, endPoint[0], endPoint[1]);
        g2d.setColor(color);
        g2d.setStroke(new BasicStroke(thickness));
        g2d.draw(path);

        double tStart = 0.01;
        double dxStart = (1 - tStart) * (controlPointX - startPoint[0]) + tStart * (endPoint[0] - controlPointX);
        double dyStart = (1 - tStart) * (controlPointY - startPoint[1]) + tStart * (endPoint[1] - controlPointY);
        double thetaStart = Math.atan2(dyStart, dxStart);

        double tEnd = 0.99;
        double dxEnd = (1 - tEnd) * (controlPointX - startPoint[0]) + tEnd * (endPoint[0] - controlPointX);
        double dyEnd = (1 - tEnd) * (controlPointY - startPoint[1]) + tEnd * (endPoint[1] - controlPointY);
        double thetaEnd = Math.atan2(dyEnd, dxEnd);

        ArrowUtils.drawArrow(g2d, startPoint, endPoint, thetaStart, thetaEnd, false, thickness, arrowStyle);
    }

    @Override
    public boolean isPointNearLine(int mouseX, int mouseY, int[] startPoint, int[] endPoint) {
        QuadCurve2D curve = new QuadCurve2D.Float();
        int controlPointX = (startPoint[0] + endPoint[0]) / 2;
        int controlPointY = (startPoint[1] + endPoint[1]) / 2 - 50;
        curve.setCurve(startPoint[0], startPoint[1], controlPointX, controlPointY, endPoint[0], endPoint[1]);

        int numPoints = 100;
        double[] coords = new double[2];
        double prevX = startPoint[0];
        double prevY = startPoint[1];

        for (int i = 1; i <= numPoints; i++) {
            double t = (double) i / numPoints;
            coords[0] = Math.pow(1 - t, 2) * startPoint[0] + 2 * (1 - t) * t * controlPointX + Math.pow(t, 2) * endPoint[0];
            coords[1] = Math.pow(1 - t, 2) * startPoint[1] + 2 * (1 - t) * t * controlPointY + Math.pow(t, 2) * endPoint[1];

            Line2D line = new Line2D.Float((float) prevX, (float) prevY, (float) coords[0], (float) coords[1]);
            if (line.ptSegDist(mouseX, mouseY) <= 5) {
                return true;
            }

            prevX = coords[0];
            prevY = coords[1];
        }

        return false;
    }
}