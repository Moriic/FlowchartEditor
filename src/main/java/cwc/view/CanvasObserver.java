package cwc.view;

import cwc.model.shape.Shape;
import cwc.model.connection.Connection;

import java.util.List;

public interface CanvasObserver {
    void onShapeSelected(Shape shape);
    void onShapeDeselected();
    void onShapesSelected(List<Shape> shapes);
    void onShapesDeselected();
    void onConnectionSelected(Connection connection);
    void onConnectionDeselected();
    void onConnectionsSelected(List<Connection> connections);
    void onConnectionsDeselected();
}
