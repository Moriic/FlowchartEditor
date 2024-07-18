package cwc.model.connection;

import cwc.enums.ArrowType;
import cwc.enums.LineType;
import cwc.model.shape.Shape;

import java.awt.*;
import java.io.Serializable;

public class Connection implements Serializable, Cloneable {
    private Shape startShape;               // 起始图形
    private Shape endShape;                 // 终止图形
    private int startConnectorIndex;        // 起始图形的连接点
    private int endConnectorIndex;          // 终止图形的连接点
    private LineType lineStyle = LineType.STRAIGHT;     // 连线样式
    private ArrowType arrowStyle = ArrowType.END;       // 箭头样式
    private LineStyleStrategy lineStyleStrategy;        // 连线策略
    private int thickness = 2;                          // 连线粗细
    private Color color = Color.BLACK;                  // 连线颜色


    public Connection(Shape startShape, Shape endShape, Integer startConnectorIndex, Integer endConnectorIndex) {
        this.startShape = startShape;
        this.endShape = endShape;
        this.startConnectorIndex = startConnectorIndex;
        this.endConnectorIndex = endConnectorIndex;
        setLineStyle(lineStyle);
    }

    @Override
    public Connection clone() {
        try {
            Connection cloned = (Connection) super.clone();
            cloned.startShape = startShape.clone();
            cloned.endShape = endShape.clone();
            return cloned;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
    // 绘制连接线
    public void draw(Graphics g) {
        if (startShape != null && endShape != null) {
            int[] startPoint = startShape.getFourPoints()[startConnectorIndex];
            int[] endPoint = endShape.getFourPoints()[endConnectorIndex];
            Graphics2D g2d = (Graphics2D) g.create();

            lineStyleStrategy.drawLine(g2d, startPoint, endPoint, thickness, color, arrowStyle);

            g2d.dispose();
        }
    }
    //
    public boolean contains(int mouseX, int mouseY) {
        int[] startPoint = startShape.getFourPoints()[startConnectorIndex];
        int[] endPoint = endShape.getFourPoints()[endConnectorIndex];
        return lineStyleStrategy.isPointNearLine(mouseX, mouseY, startPoint, endPoint);
    }
    // 线条样式
    public void setLineStyle(LineType lineStyle) {
        this.lineStyle = lineStyle;
        switch (lineStyle) {
            case POLYLINE:
                lineStyleStrategy = new PolylineStrategy();
                break;
            case CURVE:
                lineStyleStrategy = new CurveStrategy();
                break;
            default:
                lineStyleStrategy = new StraightLineStrategy();
        }
    }

    public Rectangle getBounds() {
        int[] startPoint = startShape.getFourPoints()[startConnectorIndex];
        int[] endPoint = endShape.getFourPoints()[endConnectorIndex];

        // Find the bounding box of the connection line
        int minX = Math.min(startPoint[0], endPoint[0]);
        int minY = Math.min(startPoint[1], endPoint[1]);
        int width = Math.abs(startPoint[0] - endPoint[0]) + 1;
        int height = Math.abs(startPoint[1] - endPoint[1]) + 1;
        return new Rectangle(minX, minY, width, height);
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public Shape getStartShape() {
        return startShape;
    }

    public void setStartShape(Shape startShape) {
        this.startShape = startShape;
    }

    public Shape getEndShape() {
        return endShape;
    }

    public void setEndShape(Shape endShape) {
        this.endShape = endShape;
    }

    public int getStartConnectorIndex() {
        return startConnectorIndex;
    }

    public void setStartConnectorIndex(int startConnectorIndex) {
        this.startConnectorIndex = startConnectorIndex;
    }

    public int getEndConnectorIndex() {
        return endConnectorIndex;
    }

    public void setEndConnectorIndex(int endConnectorIndex) {
        this.endConnectorIndex = endConnectorIndex;
    }

    public LineType getLineStyle() {
        return lineStyle;
    }

    public ArrowType getArrowStyle() {
        return arrowStyle;
    }

    public void setArrowStyle(ArrowType arrowStyle) {
        this.arrowStyle = arrowStyle;
    }

    public int getThickness() {
        return thickness;
    }

    public void setThickness(int thickness) {
        this.thickness = thickness;
    }

    public LineStyleStrategy getLineStyleStrategy() {
        return lineStyleStrategy;
    }

    public void setLineStyleStrategy(LineStyleStrategy lineStyleStrategy) {
        this.lineStyleStrategy = lineStyleStrategy;
    }
}
