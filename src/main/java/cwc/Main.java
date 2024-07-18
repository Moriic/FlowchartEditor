package cwc;

import cwc.controller.FileManager;
import cwc.model.connection.Connection;
import cwc.model.shape.Shape;
import cwc.view.ShapeLibraryPanel;
import cwc.view.StyleToolbar;
import cwc.view.Canvas;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Flowchart Editor");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1500, 1000);

        ShapeLibraryPanel shapeLibraryPanel = new ShapeLibraryPanel();
        Canvas canvas = Canvas.getInstance();

        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenuItem saveItem = new JMenuItem("Save");
        saveItem.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            if (fileChooser.showSaveDialog(frame) == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                FileManager.saveShapes(canvas.getShapes(), canvas.getConnections(), file);
            }
        });

        JMenuItem loadItem = new JMenuItem("Load");
        loadItem.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            if (fileChooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                List<Object> objects = FileManager.loadShapes(file);
                canvas.setShapes((List<Shape>) objects.get(0));
                canvas.setConnections((List<Connection>) objects.get(1));
            }
        });

        JMenuItem exportItem = new JMenuItem("Export");
        exportItem.addActionListener(e -> {
            String[] options = {"PNG", "SVG", "PDF"};
            String format = (String) JOptionPane.showInputDialog(frame, "Choose format:", "Export", JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
            if (format != null) {
                JFileChooser fileChooser = new JFileChooser();
                if (fileChooser.showSaveDialog(frame) == JFileChooser.APPROVE_OPTION) {
                    File file = fileChooser.getSelectedFile();
                    // Ensure the file has the correct extension
                    switch (format) {
                        case "PNG":
                            if (!file.getName().toLowerCase().endsWith(".png")) {
                                file = new File(file.getAbsolutePath() + ".png");
                            }
                            canvas.exportAsImage(file);
                            break;
                        case "SVG":
                            if (!file.getName().toLowerCase().endsWith(".svg")) {
                                file = new File(file.getAbsolutePath() + ".svg");
                            }
                            canvas.exportAsSVG(file);
                            break;
                        case "PDF":
                            if (!file.getName().toLowerCase().endsWith(".pdf")) {
                                file = new File(file.getAbsolutePath() + ".pdf");
                            }
                            canvas.exportAsPDF(file);
                            break;
                    }
                }
            }
        });


        fileMenu.add(saveItem);
        fileMenu.add(loadItem);
        menuBar.add(fileMenu);
        fileMenu.add(exportItem);
        frame.setJMenuBar(menuBar);

        frame.getContentPane().add(BorderLayout.WEST, shapeLibraryPanel);
        frame.getContentPane().add(BorderLayout.CENTER, canvas);
        frame.getContentPane().add(BorderLayout.EAST, StyleToolbar.getInstance());

        frame.setVisible(true);
    }
}
