package cwc.model.shapeGroup;


import cwc.enums.ShapeGroupType;

public class ShapeGroupFactory {
    public static ShapeGroup createShapeGroup(ShapeGroupType groupType, int x, int y) {
        switch (groupType) {
            case START_END:
                return new StartEndGroup(x, y);
            case INPUT_OUTPUT:
                return new InputOutputGroup(x, y);
            default:
                throw new IllegalArgumentException("Unknown shape group type: " + groupType);
        }
    }
}
