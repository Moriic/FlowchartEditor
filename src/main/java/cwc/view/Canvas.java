package cwc.view;


import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.PdfWriter;
import cwc.enums.ResizeDirection;
import cwc.enums.ShapeGroupType;
import cwc.enums.ShapeType;
import cwc.model.connection.Connection;
import cwc.model.shape.Shape;
import cwc.model.shape.ShapeFactory;
import cwc.model.shapeGroup.ShapeGroup;
import cwc.model.shapeGroup.ShapeGroupFactory;
import cwc.util.ShapeGroupTypeTransferable;
import cwc.util.ShapeTypeTransferable;
import org.apache.batik.dom.GenericDOMImplementation;
import org.apache.batik.svggen.SVGGraphics2D;
import org.apache.batik.util.SVGConstants;
import org.w3c.dom.DOMImplementation;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.util.List;
import java.util.*;


public class Canvas extends JPanel {
    private static Canvas instance = new Canvas();

    public static Canvas getInstance() {
        return instance;
    }

    // 包含的图形/连接线
    private List<Shape> shapes = new ArrayList<>();
    private List<Connection> connections = new ArrayList<>();
    // 当前选中的图形/连接线
    private Shape selectedShape = null;
    private Shape highlightedShape = null;
    private Connection selectedConnection = null;
    private final List<Shape> selectedShapes = new ArrayList<>();
    private final List<Connection> selectedConnections = new ArrayList<>();

    private int offsetX, offsetY;
    private ResizeDirection resizeDirection = null;

    private Shape copiedShape = null;
    private final List<Shape> copiedShapes = new ArrayList<>();

    private final JTextField textField = new JTextField();
    private boolean isInput = false;

    private Integer startConnectorIndex = -1;
    private Integer endConnectorIndex = -1;
    private Shape startShape = null;
    private Shape endShape = null;

    private Point selectionStartPoint = null;
    private Rectangle selectionRectangle = null;
    private Rectangle selectionBorder; // 用于存储边框的 Rectangle 对象
    // 撤销重做栈
    private final Stack<CanvasState> undoStack = new Stack<>();
    private final Stack<CanvasState> redoStack = new Stack<>();

    private final List<CanvasObserver> observers = new ArrayList<>();
    private StyleToolbar styleToolbar = StyleToolbar.getInstance();

    private Canvas() {
        addObserver(styleToolbar);
        setBackground(Color.WHITE);
        setLayout(null);
        textField.setVisible(false);
        add(textField);
        new DropTarget(this, DnDConstants.ACTION_COPY, new DropTargetAdapter() {
            @Override
            public void drop(DropTargetDropEvent dtde) {
                try {
                    Transferable t = dtde.getTransferable();
                    if (t.isDataFlavorSupported(ShapeTypeTransferable.SHAPE_TYPE_FLAVOR)) {
                        dtde.acceptDrop(DnDConstants.ACTION_COPY);
                        ShapeType shapeType = (ShapeType) t.getTransferData(ShapeTypeTransferable.SHAPE_TYPE_FLAVOR);
                        Shape shape = ShapeFactory.createShape(shapeType, dtde.getLocation().x, dtde.getLocation().y);
                        addShape(shape);
                        dtde.dropComplete(true);
                    } else if (t.isDataFlavorSupported(ShapeGroupTypeTransferable.SHAPE_GROUP_TYPE_FLAVOR)) {
                        dtde.acceptDrop(DnDConstants.ACTION_COPY);
                        ShapeGroupType groupType = (ShapeGroupType) t.getTransferData(ShapeGroupTypeTransferable.SHAPE_GROUP_TYPE_FLAVOR);
                        ShapeGroup group = ShapeGroupFactory.createShapeGroup(groupType, dtde.getLocation().x, dtde.getLocation().y);
                        addShapeGroup(group);
                        dtde.dropComplete(true);
                    } else {
                        dtde.rejectDrop();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    dtde.rejectDrop();
                }
            }
        });

        // 点击 -> 判断处于多选状态 -> 判断连接点 -> 判断选择图形/线条
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                // 选中多个图形状态
                if (!selectedShapes.isEmpty() && selectionBorder.contains(e.getPoint())) {
                    offsetX = e.getX();
                    offsetY = e.getY();
                    return;
                }
                // 判断点击连接点
                if (selectedShape == null) {
                    for (Shape shape : shapes) {
                        int[][] connectors = shape.getFourPoints();
                        for (int i = 0; i < connectors.length; i++) {
                            int[] connector = connectors[i];
                            if (isMouseNearConnector(e.getX(), e.getY(), connector[0], connector[1])) {
                                startConnectorIndex = i;
                                startShape = shape;
                                return;
                            }
                        }
                    }
                }
                // 是否改变大小
                boolean directionClicked = false;
                if (selectedShape != null) {
                    ResizeDirection direction = selectedShape.getResizeDirection(e.getX(), e.getY());
                    if (direction != null) {
                        resizeDirection = direction;
                        offsetX = e.getX();
                        offsetY = e.getY();
                        notifyShapeSelected(selectedShape); // 通知观察者
                        directionClicked = true;
                        repaint();
                    }
                }

                // 判断是否点击图形
                boolean shapeClicked = false;

                requestFocus();
                if (!directionClicked) {
                    for (Shape shape : shapes) {
                        if (shape.contains(e.getX(), e.getY())) {
                            selectedShape = shape;
                            selectedConnection = null;
                            offsetX = e.getX() - shape.getX();
                            offsetY = e.getY() - shape.getY();
                            notifyShapeSelected(selectedShape); // 通知观察者
                            notifyConnectionDeselected();
                            highlightedShape = shape;
                            shapeClicked = true;
                            selectedShapes.clear();
                            repaint();
                        }
                    }
                }

                // 是否点击连接线
                boolean connectionClicked = false;
                if (!shapeClicked && !directionClicked) {
                    for (Connection connection : connections) {
                        if (connection.contains(e.getX(), e.getY())) {
                            selectedConnection = connection;
                            selectedShape = null;
                            notifyConnectionSelected(selectedConnection); // 通知观察者
                            notifyShapeDeselected();
                            connectionClicked = true;
                            repaint();
                            selectedShapes.clear();
                            break;
                        }
                    }
                }

                if (!shapeClicked && !connectionClicked && !isInput && !directionClicked) {
                    selectedShape = null;
                    selectedConnection = null;
                    highlightedShape = null;
                    resizeDirection = null;

                    notifyShapeDeselected();
                    notifyShapesDeselected();
                    notifyConnectionDeselected();

                    styleToolbar.hideAllComponents();

                    // Start new selection
                    selectionStartPoint = e.getPoint();
                    selectionRectangle = new Rectangle();
                    selectedShapes.clear();

                    repaint();
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                selectionStartPoint = null;
                selectionRectangle = null;

                if (startConnectorIndex != -1) {
                    boolean flag = false;
                    for (Shape shape : shapes) {
                        if (shape == startShape) continue;
                        int[][] connectors = shape.getFourPoints();
                        for (int i = 0; i < connectors.length; i++) {
                            int[] connector = connectors[i];
                            if (isMouseNearConnector(e.getX(), e.getY(), connector[0], connector[1])) {
                                endConnectorIndex = i;
                                endShape = shape;
                                flag = true;
                            }
                            if (flag) break;
                        }
                        if (flag) break;
                    }
                    if (flag) {
                        connectShapes();
                    }
                    startShape = null;
                    endShape = null;
                    startConnectorIndex = -1;
                    endConnectorIndex = -1;
                }

                resizeDirection = null;

                repaint();
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                if (!e.isShiftDown() && e.getClickCount() == 2 && selectedShape != null) {
                    isInput = true;
                    showTextFieldForEditing(e.getX(), e.getY());
                }
            }
        });

        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (selectionStartPoint != null) {
                    // 绘制选择框
                    int x = Math.min(selectionStartPoint.x, e.getX());
                    int y = Math.min(selectionStartPoint.y, e.getY());
                    int width = Math.abs(selectionStartPoint.x - e.getX());
                    int height = Math.abs(selectionStartPoint.y - e.getY());
                    selectionRectangle.setBounds(x, y, width, height);
                    // 判断是否包含图形
                    selectedShapes.clear();
                    for (Shape shape : shapes) {
                        if (selectionRectangle.intersects(shape.getBounds()) && shape != selectedShape) {
                            selectedShapes.add(shape);
                        }
                    }
                    // 判断是否包含连接线
                    selectedConnections.clear();
                    for (Connection connection : connections) {
                        if (selectionRectangle.intersects(connection.getBounds())) {
                            selectedConnections.add(connection);
                        }
                    }
                    notifyShapesSelected(selectedShapes);
                    notifyConnectionsSelected(selectedConnections);
                    repaint();
                } else if (!selectedShapes.isEmpty()) {
                    // 多选移动
                    int dx = e.getX() - offsetX;
                    int dy = e.getY() - offsetY;
                    for (Shape shape : selectedShapes) {
                        shape.setX(shape.getX() + dx);
                        shape.setY(shape.getY() + dy);
                    }
                    offsetX = e.getX();
                    offsetY = e.getY();
                    repaint();
                } else if (selectedShape == null) {         // 连接线
                    Shape connectShape = null;
                    // 倒序遍历 shapes
                    for (int i = shapes.size() - 1; i >= 0; i--) {
                        Shape shape = shapes.get(i);
                        if (shape.contains(e.getX(), e.getY())) {
                            connectShape = shape;
                            break; // 找到最后一个包含点的 Shape 后立即退出循环
                        }
                    }

                    if (connectShape != null) {
                        int[][] connectors = connectShape.getFourPoints();
                        boolean isNearConnector = false;
                        for (int[] connector : connectors) {
                            int connectorX = connector[0];
                            int connectorY = connector[1];
                            Graphics g = getGraphics();
                            g.setColor(Color.decode("#29B6F2"));
                            g.fillRect(connectorX - 4, connectorY - 4, 8, 8); // 绘制蓝色正方形点
                            g.dispose();
                            if (isMouseNearConnector(e.getX(), e.getY(), connectorX, connectorY)) {
                                setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                                isNearConnector = true;
                            }
                        }
                        if (!isNearConnector) {
                            setCursor(Cursor.getDefaultCursor());
                        }
                    } else {
                        repaint();
                        setCursor(Cursor.getDefaultCursor());
                    }
                } else {
                    if (resizeDirection != null) {
                        int dx = e.getX() - offsetX;
                        int dy = e.getY() - offsetY;
                        selectedShape.resize(dx, dy, resizeDirection);
                        offsetX = e.getX();
                        offsetY = e.getY();
                    } else {
                        selectedShape.setX(e.getX() - offsetX);
                        selectedShape.setY(e.getY() - offsetY);
                    }
                    repaint();
                }

                if (selectedShape != null) {
                    saveState();
                }
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                if (selectedShape == null) {
                    Shape connectShape = null;
                    // 遍历判断是否包含在shape中
                    for (int i = shapes.size() - 1; i >= 0; i--) {
                        Shape shape = shapes.get(i);
                        if (shape.contains(e.getX(), e.getY())) {
                            connectShape = shape;
                            break; // 找到最后一个包含点的 Shape 后立即退出循环
                        }
                    }
                    // 绘制图形的四个点
                    if (connectShape != null) {
                        int[][] connectors = connectShape.getFourPoints();
                        boolean isNearConnector = false;
                        for (int[] connector : connectors) {
                            int connectorX = connector[0];
                            int connectorY = connector[1];
                            Graphics g = getGraphics();
                            g.setColor(Color.decode("#29B6F2"));
                            g.fillRect(connectorX - 4, connectorY - 4, 8, 8); // 绘制蓝色正方形点
                            g.dispose();
                            if (isMouseNearConnector(e.getX(), e.getY(), connectorX, connectorY)) {
                                setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                                isNearConnector = true;
                            }
                        }
                        if (!isNearConnector) {
                            setCursor(Cursor.getDefaultCursor());
                        }
                    } else {
                        repaint();
                        setCursor(Cursor.getDefaultCursor());
                    }
                } else {
                    boolean cursorSet = false;
                    for (Shape shape : shapes) {
                        if (selectedShape != shape) continue;
                        ResizeDirection direction = shape.getResizeDirection(e.getX(), e.getY());
                        if (direction != null) {
                            Cursor cursor = getCursorForResizeDirection(direction);
                            setCursor(cursor);
                            cursorSet = true;
                            break;
                        }
                    }
                    if (!cursorSet) {
                        setCursor(Cursor.getDefaultCursor());
                    }
                }
            }
        });

        // 添加键盘监听器
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if ((e.getKeyCode() == KeyEvent.VK_C) && ((e.getModifiersEx() & KeyEvent.CTRL_DOWN_MASK) != 0)) {
                    copyShape();        // 复制
                }
                if ((e.getKeyCode() == KeyEvent.VK_V) && ((e.getModifiersEx() & KeyEvent.CTRL_DOWN_MASK) != 0)) {
                    pasteShape();       // 粘贴
                }
                if ((e.getKeyCode() == KeyEvent.VK_Z) && ((e.getModifiersEx() & KeyEvent.CTRL_DOWN_MASK) != 0)) {
                    undo();     // 撤销
                }
                if ((e.getKeyCode() == KeyEvent.VK_Y) && ((e.getModifiersEx() & KeyEvent.CTRL_DOWN_MASK) != 0)) {
                    redo();     // 重做
                }
                if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
                    deleteSelectedShapeOrConnection();
                }
            }
        });

        setFocusable(true);
    }

    private void deleteSelectedShapeOrConnection() {
        // 删除图形
        if (selectedShape != null) {
            // 删除与选定图形相关的所有连接线
            connections.removeIf(connection ->
                    connection.getStartShape() == selectedShape || connection.getEndShape() == selectedShape);
            shapes.remove(selectedShape);
        }
        // 删除选择的连接线
        if (selectedConnection != null) {
            connections.remove(selectedConnection);
        }
        if (selectedShapes != null) {
            for (Shape shape : selectedShapes) {
                shapes.remove(shape);
            }
        }
        if (selectedConnections != null) {
            for (Connection connection : selectedConnections) {
                connections.remove(connection);
            }
        }
        selectedShape = null;
        highlightedShape = null;
        selectedConnection = null;
        assert selectedConnections != null;
        selectedConnections.clear();
        assert selectedShapes != null;
        selectedShapes.clear();

        notifyShapeDeselected();
        notifyShapesDeselected();
        notifyConnectionDeselected();
        notifyShapesDeselected();
        styleToolbar.hideAllComponents();
        repaint();
    }

    // 复制操作
    private void copyShape() {
        copiedShapes.clear();
        if (selectedShape != null) {
            copiedShape = selectedShape;
        }
        if (!selectedShapes.isEmpty()) {
            copiedShapes.addAll(selectedShapes);
        }
    }

    // 粘贴操作
    private void pasteShape() {
        if (copiedShape != null && selectedShape != null) {
            Shape newShape = copiedShape.clone();
            newShape.setX(newShape.getX() + 10);
            newShape.setY(newShape.getY() + 10);
            addShape(newShape);
            copiedShape = newShape;
        }
        if (!copiedShapes.isEmpty() && !selectedShapes.isEmpty()) {
            selectedShapes.clear();
            Map<Shape, Shape> originalToCopyMap = new HashMap<>();
            // 复制图形
            for (Shape copiedShape : copiedShapes) {
                Shape newShape = copiedShape.clone();
                newShape.setX(newShape.getX() + 10);
                newShape.setY(newShape.getY() + 10);
                addShape(newShape);
                originalToCopyMap.put(copiedShape, newShape);
                selectedShapes.add(newShape);
            }
            copiedShapes.clear();
            copiedShapes.addAll(selectedShapes);
            // 复制连接线：对于连接线需要注意保存原有的连接关系
            List<Connection> temp = new ArrayList<>();
            for (Connection connection : connections) {
                Shape newStartShape = originalToCopyMap.get(connection.getStartShape());
                Shape newEndShape = originalToCopyMap.get(connection.getEndShape());
                if (newStartShape != null && newEndShape != null) {
                    Connection newConnection = new Connection(newStartShape, newEndShape, connection.getStartConnectorIndex(), connection.getEndConnectorIndex());
                    newConnection.setLineStyle(connection.getLineStyle());
                    newConnection.setArrowStyle(connection.getArrowStyle());
                    temp.add(newConnection);
                }
            }
            connections.addAll(temp);
            selectedConnections.addAll(temp);
        }
        repaint();
    }

    // 保存画布状态
    private void saveState() {
        undoStack.push(new CanvasState(shapes, connections));
        redoStack.clear();
    }

    // 撤销操作
    private void undo() {
        if (!undoStack.isEmpty()) {
            redoStack.push(new CanvasState(shapes, connections));
            CanvasState previousState = undoStack.pop();
            shapes = previousState.getShapes();
            connections = previousState.getConnections();

            selectedShape = null;
            selectedConnection = null;
            selectedShapes.clear();
            selectedConnections.clear();
            highlightedShape = null;
            repaint();
        }
    }

    private void redo() {
        if (!redoStack.isEmpty()) {
            undoStack.push(new CanvasState(shapes, connections));
            CanvasState nextState = redoStack.pop();
            shapes = nextState.getShapes();
            connections = nextState.getConnections();

            selectedShape = null;
            selectedConnection = null;
            selectedShapes.clear();
            selectedConnections.clear();
            highlightedShape = null;
            repaint();
        }
    }


    private void showTextFieldForEditing(int x, int y) {
        textField.setBounds(selectedShape.getX(), selectedShape.getY(), selectedShape.getWidth(), selectedShape.getHeight());
        textField.setText(selectedShape.getText());
        textField.setVisible(true);
        textField.requestFocus();

        textField.addActionListener(e -> {
            selectedShape.setText(textField.getText());
            textField.setVisible(false);
            isInput = false;
            repaint();
        });
    }

    private Cursor getCursorForResizeDirection(ResizeDirection direction) {
        switch (direction) {
            case TOP_LEFT:
            case BOTTOM_RIGHT:
                return Cursor.getPredefinedCursor(Cursor.NW_RESIZE_CURSOR);
            case TOP_RIGHT:
            case BOTTOM_LEFT:
                return Cursor.getPredefinedCursor(Cursor.NE_RESIZE_CURSOR);
            case LEFT:
            case RIGHT:
                return Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR);
            case TOP:
            case BOTTOM:
                return Cursor.getPredefinedCursor(Cursor.S_RESIZE_CURSOR);
            default:
                return Cursor.getDefaultCursor();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        for (Shape shape : shapes) {
            shape.draw(g);
        }
        for (Connection connection : connections) {
            connection.draw(g);
        }
        if (highlightedShape != null && selectedConnection == null) {
            highlightedShape.drawConnectors(g);
        }
        if (selectedConnection != null) {
            drawSelectionBorder(g, selectedConnection);
        }
        if (!selectedShapes.isEmpty()) {
            drawCombinedSelectionBorder(g);
            for (Shape shape : selectedShapes) {
                shape.drawConnectors(g);
            }
        }
        if (selectionRectangle != null && selectedShape == null) {
            g.setColor(Color.BLUE);
            ((Graphics2D) g).draw(selectionRectangle);
        }
    }

    private void drawCombinedSelectionBorder(Graphics g) {
        if (selectedShapes.isEmpty()) return;

        // Calculate the bounding box of all selected shapes
        int minX = Integer.MAX_VALUE;
        int minY = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int maxY = Integer.MIN_VALUE;

        for (Shape shape : selectedShapes) {
            Rectangle bounds = shape.getBounds();
            minX = Math.min(minX, bounds.x) - 5;
            minY = Math.min(minY, bounds.y) - 5;
            maxX = Math.max(maxX, bounds.x + bounds.width) + 5;
            maxY = Math.max(maxY, bounds.y + bounds.height) + 5;
        }
        // 创建并赋值给 selectionBorder
        selectionBorder = new Rectangle(minX, minY, maxX - minX, maxY - minY);

        // Draw the dashed border
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setColor(Color.RED); // You can change the border color here
        float dash1[] = {10.0f};
        BasicStroke dashed = new BasicStroke(1.0f, BasicStroke.CAP_BUTT,
                BasicStroke.JOIN_MITER, 10.0f, dash1, 0.0f);
        g2d.setStroke(dashed);
        g2d.drawRect(minX - 5, minY - 5, (maxX - minX) + 10, (maxY - minY) + 10); // Adjust border size as needed
        g2d.dispose();
    }

    private void drawSelectionBorder(Graphics g, Connection connection) {
        if (connection != null) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setColor(Color.decode("#29B6F2"));
            float dash1[] = {10.0f};
            BasicStroke dashed = new BasicStroke(1.0f, BasicStroke.CAP_BUTT,
                    BasicStroke.JOIN_MITER, 10.0f, dash1, 0.0f);
            g2d.setStroke(dashed);

            int[] startPoint = connection.getStartShape().getFourPoints()[connection.getStartConnectorIndex()];
            int[] endPoint = connection.getEndShape().getFourPoints()[connection.getEndConnectorIndex()];

            // Find the bounding box of the connection line
            int minX = Math.min(startPoint[0], endPoint[0]);
            int minY = Math.min(startPoint[1], endPoint[1]);
            int width = Math.abs(startPoint[0] - endPoint[0]);
            int height = Math.abs(startPoint[1] - endPoint[1]);

            // Draw dashed border around the bounding box
            g2d.drawRect(minX - 5, minY - 5, width + 10, height + 10); // Adjust border size as needed

            g2d.dispose();
        }
    }


    public void addShape(Shape shape) {
        saveState();
        shapes.add(shape);
        selectedShape = shape;
        highlightedShape = shape;
        notifyShapeSelected(selectedShape);
        repaint();
    }

    public void addShapeGroup(ShapeGroup shapeGroup) {
        saveState();
        shapes.addAll(shapeGroup.getShapes());
        connections.addAll(shapeGroup.getConnections());
        repaint();
    }

    public List<Shape> getShapes() {
        return shapes;
    }

    public void setShapes(List<Shape> shapes) {
        this.shapes = shapes;
        highlightedShape = null;
        selectedShape = null;
        repaint();
    }

    private boolean isMouseNearConnector(int mouseX, int mouseY, int connectorX, int connectorY) {
        int distanceThreshold = 8; // 设置一个距离阈值，表示鼠标和连接点的距离小于该值时认为鼠标在连接点附近
        int dx = mouseX - connectorX;
        int dy = mouseY - connectorY;
        return Math.sqrt(dx * dx + dy * dy) < distanceThreshold;
    }

    private void connectShapes() {
        Connection connection = new Connection(startShape, endShape, startConnectorIndex, endConnectorIndex);
        connections.add(connection);
    }

    public List<Connection> getConnections() {
        return connections;
    }

    public void setConnections(List<Connection> connections) {
        this.connections = connections;
        selectedConnection = null;
        repaint();
    }

    public void exportAsImage(File file) {
        try {
            BufferedImage image = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = image.createGraphics();
            paint(g2);
            g2.dispose();
            ImageIO.write(image, "png", file);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error exporting image: " + e.getMessage());
        }
    }

    public void exportAsSVG(File file) {
        try {
            DOMImplementation domImpl = GenericDOMImplementation.getDOMImplementation();
            String svgNS = SVGConstants.SVG_NAMESPACE_URI;
            org.w3c.dom.Document document = domImpl.createDocument(svgNS, "svg", null);
            SVGGraphics2D svgGenerator = new SVGGraphics2D(document);
            paint(svgGenerator);
            boolean useCSS = true; // we want to use CSS style attribute
            try (FileOutputStream outputStream = new FileOutputStream(file);
                 OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream)) {
                svgGenerator.stream(outputStreamWriter, useCSS);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error exporting SVG: " + e.getMessage());
        }
    }

    // 导出 pdf
    public void exportAsPDF(File file) {
        try {
            exportAsImage(new File("Chart.png"));
            Document document = new Document(new com.itextpdf.text.Rectangle(getWidth(), getHeight()));
            com.itextpdf.text.Image image = com.itextpdf.text.Image.getInstance("Chart.png");
            PdfWriter.getInstance(document, Files.newOutputStream(file.toPath()));
            document.open();
            document.add(image);
            document.close();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error exporting PDF: " + e.getMessage());
        }
    }

    public void addObserver(CanvasObserver observer) {
        observers.add(observer);
    }

    public void removeObserver(CanvasObserver observer) {
        observers.remove(observer);
    }

    private void notifyShapeSelected(Shape shape) {
        for (CanvasObserver observer : observers) {
            observer.onShapeSelected(shape);
        }
    }

    private void notifyShapeDeselected() {
        for (CanvasObserver observer : observers) {
            observer.onShapeDeselected();
        }
    }

    private void notifyShapesSelected(List<Shape> shapes) {
        for (CanvasObserver observer : observers) {
            observer.onShapesSelected(shapes);
        }
    }

    private void notifyShapesDeselected() {
        for (CanvasObserver observer : observers) {
            observer.onShapesDeselected();
        }
    }

    private void notifyConnectionSelected(Connection connection) {
        for (CanvasObserver observer : observers) {
            observer.onConnectionSelected(connection);
        }
    }

    private void notifyConnectionDeselected() {
        for (CanvasObserver observer : observers) {
            observer.onConnectionDeselected();
        }
    }

    private void notifyConnectionsSelected(List<Connection> connections) {
        for (CanvasObserver observer : observers) {
            observer.onConnectionsSelected(connections);
        }
    }

    private void notifyConnectionsDeselected() {
        for (CanvasObserver observer : observers) {
            observer.onConnectionsDeselected();
        }
    }
}
