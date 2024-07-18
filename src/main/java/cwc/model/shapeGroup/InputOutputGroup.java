package cwc.model.shapeGroup;

import cwc.enums.ShapeType;
import cwc.model.connection.Connection;
import cwc.model.shape.Shape;
import cwc.model.shape.ShapeFactory;

public class InputOutputGroup extends ShapeGroup {
    Shape start, input, process, output, end;

    public InputOutputGroup(int x, int y) {
        addShapes(x, y);
        addConnections();
    }

    private void addShapes(int x, int y) {
        start = ShapeFactory.createShape(ShapeType.ROUND_RECTANGLE, x, y);
        start.setText("开始");

        input = ShapeFactory.createShape(ShapeType.PARALLELOGRAM, start.getX(), start.getY() + 100);
        input.setText("输入");

        process = ShapeFactory.createShape(ShapeType.RECTANGLE, input.getX(), input.getY() + 100);
        process.setText("流程");

        output = ShapeFactory.createShape(ShapeType.PARALLELOGRAM, process.getX(), process.getY() + 100);
        output.setText("输出");

        end = ShapeFactory.createShape(ShapeType.ROUND_RECTANGLE, output.getX(), output.getY() + 100);
        end.setText("结束");

        addShape(start);
        addShape(input);
        addShape(process);
        addShape(output);
        addShape(end);
    }

    private void addConnections() {
        Connection connection = new Connection(start, input, 2, 0);
        addConnection(connection);

        connection = new Connection(input, process, 2, 0);
        addConnection(connection);

        connection = new Connection(process, output, 2, 0);
        addConnection(connection);

        connection = new Connection(output, end, 2, 0);
        addConnection(connection);
    }
}
