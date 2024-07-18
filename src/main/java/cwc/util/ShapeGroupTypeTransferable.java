package cwc.util;

import cwc.enums.ShapeGroupType;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

public class ShapeGroupTypeTransferable implements Transferable {
    private ShapeGroupType shapeGroupType;

    public static final DataFlavor SHAPE_GROUP_TYPE_FLAVOR = new DataFlavor(ShapeGroupType.class, "ShapeGroupType");

    private static final DataFlavor[] FLAVORS = {SHAPE_GROUP_TYPE_FLAVOR};

    public ShapeGroupTypeTransferable(ShapeGroupType shapeGroupType) {
        this.shapeGroupType = shapeGroupType;
    }

    @Override
    public DataFlavor[] getTransferDataFlavors() {
        return FLAVORS;
    }

    @Override
    public boolean isDataFlavorSupported(DataFlavor flavor) {
        return SHAPE_GROUP_TYPE_FLAVOR.equals(flavor);
    }

    @Override
    public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
        if (!isDataFlavorSupported(flavor)) {
            throw new UnsupportedFlavorException(flavor);
        }
        return shapeGroupType;
    }
}
