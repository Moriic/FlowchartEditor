package cwc.view;

import cwc.model.connection.Connection;
import cwc.model.shape.Shape;

import java.util.ArrayList;
import java.util.List;

public class CanvasState {
    private final List<Shape> shapes;               // 画布的图形
    private final List<Connection> connections;     // 画布的连接线

    // 对当前画布的状态进行深克隆，加入栈中保存
    public CanvasState(List<Shape> shapes, List<Connection> connections) {
        this.shapes = new ArrayList<>();
        for (Shape shape : shapes) {
            this.shapes.add(shape.clone());
        }

        this.connections = new ArrayList<>();
        for (Connection connection : connections) {
            this.connections.add(connection.clone());
        }
    }

    public List<Shape> getShapes() {
        return shapes;
    }

    public List<Connection> getConnections() {
        return connections;
    }
}
