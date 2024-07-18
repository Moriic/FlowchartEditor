package cwc.model.shape;

import cwc.enums.ResizeDirection;

import java.awt.*;
import java.io.Serializable;

public abstract class Shape implements Cloneable, Serializable {
    protected int x, y;                         // 图形位置
    protected int width, height;                // 图形宽高
    protected Color color = Color.WHITE;        // 图形颜色
    protected Color borderColor = Color.BLACK;  // 边框颜色
    protected Color textColor = Color.BLACK;    // 文本颜色
    protected String text = "";                 // 文本内容
    protected int borderWidth = 2;              // 边框粗细

    public Shape(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }
    // 绘制图形的抽象方法
    public abstract void draw(Graphics g);
    // 克隆方法
    public Shape clone() {
        try {
            return (Shape) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
    // 判断点击点是否包含在图形内
    public abstract boolean contains(int x, int y);
    // 获取边界以判断选择框内是否包含图形
    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }
    // 获取图形四周的九个点
    public int[][] getNinePoints() {
        return new int[][]{
                {x, y}, {x + width / 2, y}, {x + width, y},
                {x + width, y + height / 2}, {x + width, y + height},
                {x + width / 2, y + height}, {x, y + height}, {x, y + height / 2}
        };
    }
    // 获取图形的四个连接点
    public int[][] getFourPoints() {
        return new int[][]{
                {x + width / 2, y}, {x + width, y + height / 2},
                {x + width / 2, y + height}, {x, y + height / 2}
        };
    }
    // 绘制连接点
    public void drawConnectors(Graphics g) {
        g.setColor(Color.decode("#29B6F2"));
        int[][] points = getNinePoints();
        for (int[] point : points) {
            g.fillOval(point[0] - 4, point[1] - 4, 8, 8); // 绘制九个连接点
        }

        // 绘制连接这些点的虚线
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setColor(Color.decode("#29B6F2"));
        float[] dashPattern = {5, 5}; // 虚线的模式，每条实线长度为5，间隔长度为5
        g2d.setStroke(new BasicStroke(2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10, dashPattern, 0)); // 设置虚线样式
        for (int i = 0; i < points.length; i++) {
            int[] startPoint = points[i];
            int[] endPoint = points[(i + 1) % points.length];
            g2d.drawLine(startPoint[0], startPoint[1], endPoint[0], endPoint[1]);
        }
        g2d.dispose();
    }
    // 获取修改大小方向
    public ResizeDirection getResizeDirection(int mx, int my) {
        if (Math.abs(mx - x) < 5 && Math.abs(my - y) < 5) {
            return ResizeDirection.TOP_LEFT;
        } else if (Math.abs(mx - (x + width)) < 5 && Math.abs(my - y) < 5) {
            return ResizeDirection.TOP_RIGHT;
        } else if (Math.abs(mx - x) < 5 && Math.abs(my - (y + height)) < 5) {
            return ResizeDirection.BOTTOM_LEFT;
        } else if (Math.abs(mx - (x + width)) < 5 && Math.abs(my - (y + height)) < 5) {
            return ResizeDirection.BOTTOM_RIGHT;
        } else if (Math.abs(mx - x) < 5 && my >= y && my <= y + height) {
            return ResizeDirection.LEFT;
        } else if (Math.abs(mx - (x + width)) < 5 && my >= y && my <= y + height) {
            return ResizeDirection.RIGHT;
        } else if (Math.abs(my - y) < 5 && mx >= x && mx <= x + width) {
            return ResizeDirection.TOP;
        } else if (Math.abs(my - (y + height)) < 5 && mx >= x && mx <= x + width) {
            return ResizeDirection.BOTTOM;
        }
        return null;
    }
    // 修改大小
    public void resize(int dx, int dy, ResizeDirection direction) {
        switch (direction) {
            case TOP_LEFT:
                x += dx;
                y += dy;
                width -= dx;
                height -= dy;
                break;
            case TOP_RIGHT:
                y += dy;
                width += dx;
                height -= dy;
                break;
            case BOTTOM_LEFT:
                x += dx;
                width -= dx;
                height += dy;
                break;
            case BOTTOM_RIGHT:
                width += dx;
                height += dy;
                break;
            case LEFT:
                x += dx;
                width -= dx;
                break;
            case RIGHT:
                width += dx;
                break;
            case TOP:
                y += dy;
                height -= dy;
                break;
            case BOTTOM:
                height += dy;
                break;
        }
        if (width < 0) {
            width = -width;
            x -= width;
        }
        if (height < 0) {
            height = -height;
            y -= height;
        }
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Color getColor() {
        return color;
    }

    public String getText() {
        return text;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    // 设置边框粗细
    public void setBorderWidth(int width) {
        this.borderWidth = width;
    }

    // 获取边框粗细
    public int getBorderWidth() {
        return borderWidth;
    }

    public Color getBorderColor() {
        return borderColor;
    }


    public void setBorderColor(Color borderColor) {
        this.borderColor = borderColor;
    }

    public void setTextColor(Color textColor) {
        this.textColor = textColor;
    }

    public Color getTextColor() {
        return textColor;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }
}

