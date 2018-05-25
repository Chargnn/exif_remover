package com.chargnn.image;

import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class App 
{
    static String path;
    static JFrame frame = new JFrame();
    static JPanel panel = new JPanel();
    static File file;
    static String filefolder;
    static JLabel header = new JLabel(MessageString.SELECT_FILE);

    public static void main( String[] args )
    {
        initGui();
    }

    /**
     *  Creating / adding things to the panel
     *  with button listener
     */
    public static void initGui() {
        panel = new JPanel();
        frame = new JFrame();
        header = new JLabel(MessageString.SELECT_FILE);

        JButton removeButton = new JButton(MessageString.REMOVE_EXIF);

        removeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                removeEXIF();
            }
        });

        JButton selectFileButton = new JButton(MessageString.SELECT_FILE);

        selectFileButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                JFileChooser chooser = new JFileChooser();
                if(filefolder == null) {
                    chooser.setCurrentDirectory(new java.io.File("."));
                } else {
                    chooser.setCurrentDirectory(new java.io.File(filefolder));
                }
                chooser.setDialogTitle("");
                chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                chooser.setAcceptAllFileFilterUsed(false);

                if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                    path = chooser.getSelectedFile().toString();
                    file = chooser.getSelectedFile();

                    try {
                        Metadata metadata = ImageMetadataReader.readMetadata(file);
                        String data = "";

                        for (Directory directory : metadata.getDirectories()) {
                            for (Tag tag : directory.getTags()) {
                                data += tag + "\n";
                            }
                        }

                        JOptionPane.showMessageDialog(null, data, "Readable data:", JOptionPane.INFORMATION_MESSAGE);
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(null, MessageString.ERR_FORMAT, "Error", JOptionPane.ERROR_MESSAGE);
                        initGui();
                        return;
                    }

                    header.setText(MessageString.READY_TO_REMOVE);
                } else {
                    header.setText(MessageString.ERR_NO_SELECTION);
                }
            }
        });

        panel.setPreferredSize(new Dimension(500, 50));
        panel.add(header);
        panel.add(selectFileButton);
        panel.add(removeButton);
        frame.setContentPane(panel);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        frame.setTitle("EXIF Remover (.jpg, .jpeg, .png)");
    }

    /**
     * Remove any EXIF data from the seletec image
     * (basically, create a new empty image)
     */
    public static void removeEXIF() {
        if(path == null) {
            JOptionPane.showMessageDialog(null, MessageString.ERR_NO_SELECTION, "Error", JOptionPane.WARNING_MESSAGE);
        } else {
            String extension = "";
            int i = file.getName().lastIndexOf('.');
            if (i > 0) {
                extension = file.getName().substring(i+1);

                if(!extension.matches("png|jpg|jpeg")){
                    JOptionPane.showMessageDialog(null, MessageString.ERR_FORMAT, "Error", JOptionPane.ERROR_MESSAGE);
                    initGui();
                    return;
                }

                BufferedImage image;
                try {
                    image = ImageIO.read(file);
                    ImageIO.write(image, extension, new File(path));
                    success();
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(null, MessageString.ERR_UNKNOWN, "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(null,  MessageString.ERR_FORMAT, "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Read method name...
     */
    public static void success() {
        header.setText(MessageString.SUCC_REMOVE);
        path = "";
        filefolder = "";
        file = null;
    }
}
