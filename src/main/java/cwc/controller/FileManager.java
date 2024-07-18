package cwc.controller;


import cwc.model.shape.Shape;
import cwc.model.connection.Connection;

import javax.swing.*;
import java.io.*;
import java.util.Arrays;
import java.util.List;

public class FileManager {
    public static void saveShapes(List<Shape> shapes, List<Connection> connections, File file) {
        // 确保文件名以 .fc 结尾
        if (!file.getName().endsWith(".fc")) {
            file = new File(file.getAbsolutePath() + ".fc");
        }
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file))) {
            out.writeObject(shapes);
            out.writeObject(connections); // 确保连接也被保存
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error saving file");
        }
    }

    public static List<Object> loadShapes(File file) {
        if (!file.getName().endsWith(".fc")) {
            JOptionPane.showMessageDialog(null, "Invalid file extension. Please select a .fc file.");
            return null;
        }

        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
            List<Shape> shapes = (List<Shape>) in.readObject();
            List<Connection> connections = (List<Connection>) in.readObject(); // 加载连接对象
            return Arrays.asList(shapes, connections); // 返回包含两个列表的单一列表
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error loading file");
            return null;
        }
    }
}