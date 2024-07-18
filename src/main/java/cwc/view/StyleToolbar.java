package cwc.view;

import cwc.enums.ArrowType;
import cwc.enums.LineType;
import cwc.model.connection.Connection;
import cwc.model.shape.Shape;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class StyleToolbar extends JPanel implements CanvasObserver {
    // 初始化单例
    private static StyleToolbar instance = new StyleToolbar();
    // 私有化构造器
    private StyleToolbar() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        // 图形相关
        colorButton = new JButton("选择图形颜色");
        textColorButton = new JButton("选择文本颜色");
        borderColorButton = new JButton("选择边框颜色");
        borderThicknessSlider = new JSlider(1, 10, 1);
        // 连接线相关
        lineStyleComboBox = new JComboBox<>(LineType.values());
        arrowComboBox = new JComboBox<>(ArrowType.values());
        thicknessSpinner = new JSlider(1, 10, 1);
        conColorButton = new JButton("选择连线颜色");
        // 设置 JComboBox 高度
        Dimension comboBoxSize = new Dimension(200, 30);
        lineStyleComboBox.setPreferredSize(comboBoxSize);
        lineStyleComboBox.setMaximumSize(comboBoxSize);
        arrowComboBox.setPreferredSize(comboBoxSize);
        arrowComboBox.setMaximumSize(comboBoxSize);
        // 创建标签
        JLabel borderLabel = new JLabel("边框粗细:");
        JLabel lineStyleLabel = new JLabel("连线样式:");
        JLabel arrowLabel = new JLabel("箭头样式:");
        JLabel thicknessLabel = new JLabel("连线粗细:");

        // Set alignment to left
        colorButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        textColorButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        borderColorButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        borderThicknessSlider.setAlignmentX(Component.LEFT_ALIGNMENT);
        lineStyleComboBox.setAlignmentX(Component.LEFT_ALIGNMENT);
        arrowComboBox.setAlignmentX(Component.LEFT_ALIGNMENT);
        thicknessSpinner.setAlignmentX(Component.LEFT_ALIGNMENT);
        conColorButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        borderLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        lineStyleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        arrowLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        thicknessLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // 图形 Panel
        shapeOptionsPanel = new JPanel();
        shapeOptionsPanel.setLayout(new BoxLayout(shapeOptionsPanel, BoxLayout.Y_AXIS)); // 设置纵向排列布局
        shapeOptionsPanel.add(colorButton);
        shapeOptionsPanel.add(Box.createRigidArea(new Dimension(0, 10))); // Add vertical spacing
        shapeOptionsPanel.add(textColorButton);
        shapeOptionsPanel.add(Box.createRigidArea(new Dimension(0, 10))); // Add vertical spacing
        shapeOptionsPanel.add(borderColorButton);
        shapeOptionsPanel.add(Box.createRigidArea(new Dimension(0, 10))); // Add vertical spacing
        shapeOptionsPanel.add(borderLabel);
        shapeOptionsPanel.add(borderThicknessSlider);

        // 连接线相关
        connectionOptionsPanel = new JPanel();
        connectionOptionsPanel.setLayout(new BoxLayout(connectionOptionsPanel, BoxLayout.Y_AXIS)); // 设置纵向排列布局
        connectionOptionsPanel.add(conColorButton);
        connectionOptionsPanel.add(Box.createRigidArea(new Dimension(0, 10))); // Add vertical spacing
        connectionOptionsPanel.add(lineStyleLabel);
        connectionOptionsPanel.add(lineStyleComboBox);
        connectionOptionsPanel.add(Box.createRigidArea(new Dimension(0, 10))); // Add vertical spacing
        connectionOptionsPanel.add(arrowLabel);
        connectionOptionsPanel.add(arrowComboBox);
        connectionOptionsPanel.add(Box.createRigidArea(new Dimension(0, 10))); // Add vertical spacing
        connectionOptionsPanel.add(thicknessLabel);
        connectionOptionsPanel.add(thicknessSpinner);

        add(shapeOptionsPanel);
        add(connectionOptionsPanel);

        colorButton.addActionListener(e -> {
            if (selectedShape != null) {
                Color newColor = JColorChooser.showDialog(null, "Choose Color", selectedShape.getColor());
                if (newColor != null) {
                    selectedShape.setColor(newColor);
                }
            }
            if (selectedShapes != null && !selectedShapes.isEmpty()) {
                Color newColor = JColorChooser.showDialog(null, "Choose Color", selectedShapes.getFirst().getColor());
                if (newColor != null) {
                    for (Shape shape : selectedShapes) {
                        shape.setColor(newColor);
                    }
                }
            }
            repaintAll();
        });

        textColorButton.addActionListener(e -> {
            if (selectedShape != null) {
                Color newColor = JColorChooser.showDialog(null, "Choose Text Color", selectedShape.getTextColor());
                if (newColor != null) {
                    selectedShape.setTextColor(newColor);
                }
            }
            if (selectedShapes != null && !selectedShapes.isEmpty()) {
                Color newColor = JColorChooser.showDialog(null, "Choose Color", selectedShapes.getFirst().getColor());
                if (newColor != null) {
                    for (Shape shape : selectedShapes) {
                        shape.setTextColor(newColor);
                    }
                }
            }
            repaintAll();
        });

        borderColorButton.addActionListener(e -> {
            if (selectedShape != null) {
                Color newBorderColor = JColorChooser.showDialog(null, "Choose Border Color", selectedShape.getBorderColor());
                if (newBorderColor != null) {
                    selectedShape.setBorderColor(newBorderColor);
                }
            }
            if (selectedShapes != null && !selectedShapes.isEmpty()) {
                Color newBorderColor = JColorChooser.showDialog(null, "Choose Border Color", selectedShapes.getFirst().getColor());
                if (newBorderColor != null) {
                    for (Shape shape : selectedShapes) {
                        shape.setBorderColor(newBorderColor);
                    }
                }
            }
            repaintAll();
        });

        borderThicknessSlider.addChangeListener(e -> {
            if (selectedShape != null) {
                int thickness = borderThicknessSlider.getValue();
                selectedShape.setBorderWidth(thickness);
            }
            if (selectedShapes != null && !selectedShapes.isEmpty()) {
                int thickness = borderThicknessSlider.getValue();
                for (Shape shape : selectedShapes) {
                    shape.setBorderWidth(thickness);
                }
            }
            repaintAll();
        });

        lineStyleComboBox.addActionListener(e -> {
            if (selectedConnection != null) {
                selectedConnection.setLineStyle((LineType) lineStyleComboBox.getSelectedItem());
            }
            if (selectedConnections != null && !selectedConnections.isEmpty()) {
                for (Connection connection : selectedConnections) {
                    connection.setLineStyle((LineType) lineStyleComboBox.getSelectedItem());
                }
            }
            repaintAll();
        });

        arrowComboBox.addActionListener(e -> {
            if (selectedConnection != null) {
                selectedConnection.setArrowStyle((ArrowType) arrowComboBox.getSelectedItem());
            }
            if (selectedConnections != null && !selectedConnections.isEmpty()) {
                for (Connection connection : selectedConnections) {
                    connection.setArrowStyle((ArrowType) arrowComboBox.getSelectedItem());
                }
            }
            repaintAll();
        });

        thicknessSpinner.addChangeListener(e -> {
            if (selectedConnection != null) {
                int thickness = thicknessSpinner.getValue();
                selectedConnection.setThickness(thickness);
            }
            if (selectedConnections != null && !selectedConnections.isEmpty()) {
                int thickness = thicknessSpinner.getValue();
                for (Connection connection : selectedConnections) {
                    connection.setThickness(thickness);
                }
            }
            repaintAll();
        });

        conColorButton.addActionListener(e -> {
            if (selectedConnection != null) {
                Color newColor = JColorChooser.showDialog(null, "Choose Line Color", selectedConnection.getColor());
                if (newColor != null) {
                    selectedConnection.setColor(newColor);
                }
            }
            if (selectedConnections != null && !selectedConnections.isEmpty()) {
                Color newColor = JColorChooser.showDialog(null, "Choose Line Color", selectedConnections.getFirst().getColor());
                for (Connection connection : selectedConnections) {
                    connection.setColor(newColor);
                }
            }
            repaintAll();
        });

        hideAllComponents();
    }
    // 获取单例
    public static StyleToolbar getInstance() {
        return instance;
    }
    private Shape selectedShape;                       // 当前选择的图形
    private Connection selectedConnection;             // 当前选择的连线
    private List<Shape> selectedShapes;                // 当前选择的多选图形
    private List<Connection> selectedConnections;      // 当前选择的多个连接线

    // 修改图形属性界面
    private final JButton colorButton;
    private final JButton borderColorButton;
    private final JButton textColorButton;
    private final JSlider borderThicknessSlider;
    // 修改连线界面
    private final JComboBox<LineType> lineStyleComboBox;
    private final JComboBox<ArrowType> arrowComboBox;
    private final JSlider thicknessSpinner;
    private final JButton conColorButton;
    // 整体界面
    private final JPanel shapeOptionsPanel;
    private final JPanel connectionOptionsPanel;



    private void repaintAll() {
        Container parent = getParent();
        while (parent != null && !(parent instanceof JFrame)) {
            parent = parent.getParent();
        }
        if (parent != null) {
            ((JFrame) parent).getContentPane().getComponent(1).repaint();
        }
    }

    public void setSelectedShape(Shape shape) {
        this.selectedShape = shape;
        showAllComponents();
    }

    public void setSelectedConnection(Connection connection) {
        this.selectedConnection = connection;
        showAllComponents();
    }

    public void setSelectedShapes(List<Shape> selectedShapes) {
        this.selectedShapes = selectedShapes;
        showAllComponents();
    }

    public void setSelectedConnections(List<Connection> selectedConnections) {
        this.selectedConnections = selectedConnections;
        showAllComponents();
    }

    public void hideAllComponents() {
        shapeOptionsPanel.setVisible(false);
        connectionOptionsPanel.setVisible(false);
    }

    public void showAllComponents() {
        shapeOptionsPanel.setVisible(true);
        connectionOptionsPanel.setVisible(true);
    }


    @Override
    public void onShapeSelected(Shape shape) {
        setSelectedShape(shape);
        showAllComponents();
    }

    @Override
    public void onShapeDeselected() {
        setSelectedShape(null);
    }

    @Override
    public void onShapesSelected(List<Shape> shapes) {
        setSelectedShapes(shapes);
    }

    @Override
    public void onShapesDeselected() {
        setSelectedShapes(null);
    }

    @Override
    public void onConnectionSelected(Connection connection) {
        setSelectedConnection(connection);
    }

    @Override
    public void onConnectionDeselected() {
        setSelectedConnection(null);
    }

    @Override
    public void onConnectionsSelected(List<Connection> connections) {
        setSelectedConnections(connections);
    }

    @Override
    public void onConnectionsDeselected() {
        setSelectedConnections(null);
    }

}
