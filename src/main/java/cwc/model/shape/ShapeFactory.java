package cwc.model.shape;

import cwc.enums.ShapeType;

public class ShapeFactory {

    public static Shape createShape(ShapeType shapeType, int x, int y) {
        switch (shapeType) {
            case RECTANGLE:
                return new RectangleShape(x, y, 100, 50);
            case DIAMOND:
                return new DiamondShape(x, y, 100, 50);
            case ELLIPSE:
                return new EllipseShape(x, y, 100, 50);
            case ROUND_RECTANGLE:
                return new RoundedRectangleShape(x, y, 100, 50, 50, 50);
            case PARALLELOGRAM:
                return new ParallelogramShape(x, y, 100, 50);
            default:
                throw new IllegalArgumentException("Unknown shape type: " + shapeType);
        }
    }
}