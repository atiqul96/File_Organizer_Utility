/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author USER
 */
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;

public class FileOrganizerGUI extends JFrame {

    private JTextArea outputArea;

    public FileOrganizerGUI() {
        setTitle("File Organizer Utility");
        setSize(900, 450);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // UI Layout
        JButton selectButton = new JButton("Select Folder to Organize");
        outputArea = new JTextArea();
        outputArea.setEditable(false);

        JScrollPane scrollPane = new JScrollPane(outputArea);

        selectButton.addActionListener(this::selectFolder);

        add(selectButton, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void selectFolder(ActionEvent e) {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        int choice = chooser.showOpenDialog(this);

        if (choice == JFileChooser.APPROVE_OPTION) {
            Path folder = chooser.getSelectedFile().toPath();
            outputArea.append("Selected: " + folder + "\n");

            try {
                organizeFiles(folder);
            } catch (IOException ex) {
                outputArea.append("Error: " + ex.getMessage() + "\n");
            }
        }
    }

    private void organizeFiles(Path folder) throws IOException {
        Map<String, String> categories = new HashMap<>();
        categories.put("Images", "jpg,jpeg,png,gif,bmp");
        categories.put("Documents", "pdf,doc,docx,txt,xls,xlsx,ppt,pptx");
        categories.put("Archives", "zip,rar,7z,tar,gz");
        categories.put("Audio", "mp3,wav,aac,flac");
        categories.put("Video", "mp4,avi,mkv,flv,wmv,MOV,mov");

       
        for (String category : categories.keySet()) {
            Files.createDirectories(folder.resolve(category));
        }

        
        Files.walk(folder).forEach(path -> {
            if (Files.isRegularFile(path)) {
                String fileName = path.getFileName().toString();
                String ext = getFileExtension(fileName).toLowerCase();

                for (String category : categories.keySet()) {
                    if (categories.get(category).contains(ext)) {
                        try {
                            Path target = folder.resolve(category).resolve(fileName);
                            Files.move(path, target, StandardCopyOption.REPLACE_EXISTING);

                            outputArea.append("Moved: " + fileName + " → " + category + "\n");
                        } catch (IOException e) {
                            outputArea.append("Failed: " + fileName + " (" + e.getMessage() + ")\n");
                        }
                        break;
                    }
                }
            }
        });

        outputArea.append("✔ Sorting Completed!\n\n");
    }

    private String getFileExtension(String fileName) {
        int i = fileName.lastIndexOf(".");
        if (i > 0 && i < fileName.length() - 1)
            return fileName.substring(i + 1);
        return "";
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new FileOrganizerGUI().setVisible(true));
    }
}
