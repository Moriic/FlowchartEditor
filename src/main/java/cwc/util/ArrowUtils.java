package cwc.util;


import cwc.enums.ArrowType;

import java.awt.*;
import java.awt.geom.Path2D;

public class ArrowUtils {
    public static void drawArrow(Graphics2D g2d, int[] startPoint, int[] endPoint, double thetaStart, double thetaEnd, boolean horizontal, int thickness, ArrowType arrowStyle) {
        double phi = Math.toRadians(20);
        int barb = (thickness - 1) * 5 + 15;
        // 开始位置/两头都有绘制
        if (arrowStyle.equals(ArrowType.START) || arrowStyle.equals(ArrowType.BOTH)) {
            double x1 = startPoint[0] + barb * Math.cos(thetaStart + phi);
            double y1 = startPoint[1] + barb * Math.sin(thetaStart + phi);
            double x2 = startPoint[0] + barb * Math.cos(thetaStart - phi);
            double y2 = startPoint[1] + barb * Math.sin(thetaStart - phi);
            Path2D arrowHead = new Path2D.Double();
            arrowHead.moveTo(startPoint[0], startPoint[1]);
            arrowHead.lineTo(x1, y1);
            arrowHead.lineTo(x2, y2);
            arrowHead.closePath();
            g2d.fill(arrowHead);
        }
        // 结束位置/两头都有绘制
        if (arrowStyle.equals(ArrowType.END) || arrowStyle.equals(ArrowType.BOTH)) {
            double x1 = endPoint[0] - barb * Math.cos(thetaEnd + phi);
            double y1 = endPoint[1] - barb * Math.sin(thetaEnd + phi);
            double x2 = endPoint[0] - barb * Math.cos(thetaEnd - phi);
            double y2 = endPoint[1] - barb * Math.sin(thetaEnd - phi);
            Path2D arrowHead = new Path2D.Double();
            arrowHead.moveTo(endPoint[0], endPoint[1]);
            arrowHead.lineTo(x1, y1);
            arrowHead.lineTo(x2, y2);
            arrowHead.closePath();
            g2d.fill(arrowHead);
        }
    }
}

