package cwc.model.shapeGroup;


import cwc.enums.LineType;
import cwc.enums.ShapeType;
import cwc.model.connection.Connection;
import cwc.model.shape.Shape;
import cwc.model.shape.ShapeFactory;

public class StartEndGroup extends ShapeGroup {
    // 五个基本图形
    Shape start, process, judge, judgeProcess, end;
    // 构造器
    public StartEndGroup(int x, int y) {
        addShapes(x, y);
        addConnections();
    }
    // 添加基本图形
    private void addShapes(int x, int y) {
        start = ShapeFactory.createShape(ShapeType.ROUND_RECTANGLE, x, y);
        start.setText("开始");

        process = ShapeFactory.createShape(ShapeType.RECTANGLE, start.getX() + 180, y);
        process.setText("过程");

        judge = ShapeFactory.createShape(ShapeType.DIAMOND, process.getX() + 180, process.getY());
        judge.setText("判断");

        judgeProcess = ShapeFactory.createShape(ShapeType.RECTANGLE, judge.getX() + 180, judge.getY() - 100);
        judgeProcess.setText("True 流程");

        end = ShapeFactory.createShape(ShapeType.ROUND_RECTANGLE, judge.getX() + 180, judge.getY());
        end.setText("结束");

        addShape(start);
        addShape(process);
        addShape(judge);
        addShape(judgeProcess);
        addShape(end);
    }
    // 添加连接线
    private void addConnections() {
        addConnection(new Connection(start, process, 1, 3));

        addConnection(new Connection(process, judge, 1, 3));

        Connection connection = new Connection(judge, judgeProcess, 0, 3);
        connection.setLineStyle(LineType.POLYLINE);
        addConnection(connection);

        addConnection(new Connection(judge, end, 1, 3));

        addConnection(new Connection(judgeProcess, end, 2, 0));
    }
}
