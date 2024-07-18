package cwc.util;

import cwc.enums.ShapeType;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

public class ShapeTypeTransferable implements Transferable {
    private ShapeType shapeType;

    public static final DataFlavor SHAPE_TYPE_FLAVOR = new DataFlavor(ShapeType.class, "ShapeType");

    private static final DataFlavor[] FLAVORS = {SHAPE_TYPE_FLAVOR};

    public ShapeTypeTransferable(ShapeType shapeType) {
        this.shapeType = shapeType;
    }

    @Override
    public DataFlavor[] getTransferDataFlavors() {
        return FLAVORS;
    }

    @Override
    public boolean isDataFlavorSupported(DataFlavor flavor) {
        return SHAPE_TYPE_FLAVOR.equals(flavor);
    }

    @Override
    public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
        if (!isDataFlavorSupported(flavor)) {
            throw new UnsupportedFlavorException(flavor);
        }
        return shapeType;
    }
}
