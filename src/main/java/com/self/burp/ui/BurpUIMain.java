package com.self.burp.ui;

import com.self.burp.BurpExtender;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class BurpUIMain {
    private JPanel root;
    private JLabel title;
    public  JTextArea textArea;
    private JScrollPane JScrollPanel;
    private JRadioButton RadioButton;
    private JButton clearButton;
    private JButton saveButton;
    private JTextField hostTextField;
    private JLabel hostlabel;
    public static String textAreaValue = "";

    public BurpUIMain() {
        RadioButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(RadioButton.isSelected()){
                    RadioButton.setText("插件已激活");
                    RadioButton.setForeground(Color.green);
                    BurpExtender.pluginState = true;
                    textArea.setText("插件已激活...");
                    textArea.append("\n");
                }else {
                    RadioButton.setText("插件未激活");
                    RadioButton.setForeground(Color.red);
                    BurpExtender.pluginState = false;
                    textArea.setText("插件已关闭...");
                }
            }
        });
        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                textArea.setText("");
            }
        });

        hostTextField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                super.keyReleased(e);
                BurpExtender.filteHost = hostTextField.getText();
            }
        });
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser jFileChooser = new JFileChooser();
                FileNameExtensionFilter fileNameExtensionFilter = new FileNameExtensionFilter("后缀类型（*.txt、*.log）","txt","log");
                jFileChooser.setFileFilter(fileNameExtensionFilter);
                int option = jFileChooser.showSaveDialog(null);
                if (option == JFileChooser.APPROVE_OPTION) {
                    File file = jFileChooser.getSelectedFile();
                    String fname = jFileChooser.getName(file);
                    if (fname.contains(".txt")){
                        file = new File(jFileChooser.getCurrentDirectory(),fname);
                    }else if (fname.contains(".log")){
                        file = new File(jFileChooser.getCurrentDirectory(),fname);
                    }else {
                        file = new File(jFileChooser.getCurrentDirectory(),fname+".txt");
                    }
                    try {
                        FileOutputStream fileOutputStream = new FileOutputStream(file,true);
                        fileOutputStream.write(textArea.getText().getBytes());
                        fileOutputStream.close();
                    }catch (IOException ioException){
                        ioException.printStackTrace();
                        System.out.println("IO异常");
                    }
                    
                }
            }
        });
    }

    public JPanel getRoot() {
        return root;
    }

    public JTextArea getTextArea() {
        return textArea;
    }
}
