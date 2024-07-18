// src/view/ShapeLibraryPanel.java
package cwc.view;

import cwc.enums.ShapeGroupType;
import cwc.enums.ShapeType;
import cwc.util.ShapeGroupTypeTransferable;
import cwc.util.ShapeTypeTransferable;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Transferable;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ShapeLibraryPanel extends JPanel {

    public ShapeLibraryPanel() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(Color.LIGHT_GRAY);

        add(createShapeButton("矩形", ShapeType.RECTANGLE));
        add(createShapeButton("菱形", ShapeType.DIAMOND));
        add(createShapeButton("椭圆", ShapeType.ELLIPSE));
        add(createShapeButton("开始/结束", ShapeType.ROUND_RECTANGLE));
        add(createShapeButton("输入/输出", ShapeType.PARALLELOGRAM));

        // 图形组
        add(new JLabel("Flowchart group:"));
        add(createGroupButton("开始-结束流程组", ShapeGroupType.START_END));
        add(createGroupButton("输入-输出流程组", ShapeGroupType.INPUT_OUTPUT));
    }
    // 创建按钮并添加监听器
    private JButton createShapeButton(String name, ShapeType shapeType) {
        JButton button = new JButton(name);
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setTransferHandler(new ValueExportTransferHandler(shapeType));

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                JComponent comp = (JComponent) e.getSource();
                TransferHandler handler = comp.getTransferHandler();
                handler.exportAsDrag(comp, e, TransferHandler.COPY);
            }
        });
        return button;
    }

    private JButton createGroupButton(String name, ShapeGroupType groupType) {
        JButton button = new JButton(name);
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);

        // 设置拖放功能
        button.setTransferHandler(new GroupExportTransferHandler(groupType));

        // 添加鼠标监听器以处理拖放事件
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                JComponent comp = (JComponent) e.getSource();
                TransferHandler handler = comp.getTransferHandler();
                handler.exportAsDrag(comp, e, TransferHandler.COPY);
            }
        });

        return button;
    }


    private class ValueExportTransferHandler extends TransferHandler {
        private ShapeType shapeType;

        public ValueExportTransferHandler(ShapeType shapeType) {
            this.shapeType = shapeType;
        }

        @Override
        protected Transferable createTransferable(JComponent c) {
            return new ShapeTypeTransferable(shapeType);
        }

        @Override
        public int getSourceActions(JComponent c) {
            return COPY;
        }
    }

    private class GroupExportTransferHandler extends TransferHandler {
        private ShapeGroupType groupType;

        public GroupExportTransferHandler(ShapeGroupType groupType) {
            this.groupType = groupType;
        }

        @Override
        protected Transferable createTransferable(JComponent c) {
            return new ShapeGroupTypeTransferable(groupType);
        }

        @Override
        public int getSourceActions(JComponent c) {
            return COPY;
        }
    }

}
