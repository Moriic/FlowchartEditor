package cwc.model.shapeGroup;

import cwc.model.connection.Connection;
import cwc.model.shape.Shape;

import java.awt.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ShapeGroup implements Serializable {
    private List<Shape> shapes;             // 基本图形组
    private List<Connection> connections;   // 连接点集合

    public ShapeGroup() {
        shapes = new ArrayList<>();
        connections = new ArrayList<>();
    }
    // 添加图形
    public void addShape(Shape shape) {
        shapes.add(shape);
    }
    // 添加连接点
    public void addConnection(Connection connection) {
        connections.add(connection);
    }
    // 绘制函数
    public void draw(Graphics g) {
        for (Shape shape : shapes) {
            shape.draw(g);
        }
        for (Connection connection : connections) {
            connection.draw(g);
        }
    }

    // Getters and setters
    public List<Shape> getShapes() {
        return shapes;
    }

    public void setShapes(List<Shape> shapes) {
        this.shapes = shapes;
    }

    public List<Connection> getConnections() {
        return connections;
    }

    public void setConnections(List<Connection> connections) {
        this.connections = connections;
    }
}

