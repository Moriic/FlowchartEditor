package cwc.model.connection;

import cwc.enums.ArrowType;

import java.awt.*;

public interface LineStyleStrategy {
    void drawLine(Graphics2D g2d, int[] startPoint, int[] endPoint, int thickness, Color color, ArrowType arrowStyle);
    boolean isPointNearLine(int mouseX, int mouseY, int[] startPoint, int[] endPoint);
}
