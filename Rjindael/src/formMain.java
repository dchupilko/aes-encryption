import java.io.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.Font;
import java.net.URL;
import javax.swing.*;

public class formMain extends JFrame {

    private int textSize;
    private int keySize;
    private String source;
    private String key;
    public JTextArea textArea;
    JScrollPane scrollLog;
    JTextField textSource;
    JTextField textKey;
    JTextField textCipher;
    JComboBox comboTextSize;
    JComboBox comboKeySize;
    JCheckBox checkHexText;
    JCheckBox checkHexKey;
    JButton buttonEncrypt;
    JButton buttonDecrypt;
    JButton buttonOpenText;
    JButton buttonOpenKey;
    JButton buttonOpenCipher;
    JButton buttonDetails;
    JButton buttonSaveLog;
    JButton buttonAbout;
    ImageIcon iconOpen = createIcon("img/open.png");
    ImageIcon iconSave = createIcon("img/save.png");
    logMaker logger = new logMaker();
    JFileChooser fc = new JFileChooser();

    public formMain() {
        super("Криптографический алгоритм AES");
        createGUI();
    }

    public void createGUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setBounds(0, 0, 600, 500);
        panel.setLayout(null);

        Font font = new Font("monospaced", Font.PLAIN, 14);

        JLabel labelInput = new JLabel("Исходный текст");
        labelInput.setBounds(50, 20, 210, 20);
        panel.add(labelInput);

        JLabel labelTextSize = new JLabel("Размер блока:");
        labelTextSize.setBounds(410, 20, 100, 20);
        panel.add(labelTextSize);

        JLabel labelKey = new JLabel("Ключ");
        labelKey.setBounds(50, 100, 150, 20);
        panel.add(labelKey);

        JLabel labelKeySize = new JLabel("Размер ключа:");
        labelKeySize.setBounds(410, 100, 100, 20);
        panel.add(labelKeySize);

        JLabel labelOutput = new JLabel("Шифрованный текст");
        labelOutput.setBounds(50, 180, 150, 20);
        panel.add(labelOutput);

        JLabel labelLog = new JLabel("Ход работы");
        labelLog.setBounds(50, 300, 150, 20);
        panel.add(labelLog);

        textSource = new JTextField();
        textSource.setBounds(20, 50, 540, 30);
        textSource.setFont(font);
        panel.add(textSource);

        textKey = new JTextField();
        textKey.setBounds(20, 130, 540, 30);
        textKey.setFont(font);
        panel.add(textKey);

        textCipher = new JTextField();
        textCipher.setBounds(20, 210, 540, 30);
        textCipher.setFont(font);
        panel.add(textCipher);

        checkHexText = new JCheckBox("Hex");
        checkHexText.setBounds(150, 20, 50, 20);
        //panel.add(checkHexText);

        checkHexKey = new JCheckBox("Hex");
        checkHexKey.setBounds(150, 100, 50, 20);
        //panel.add(checkHexKey);

        comboTextSize = new JComboBox();
        comboTextSize.setBounds(510, 20, 50, 20);
        comboTextSize.addItem("128");
        comboTextSize.addItem("192");
        comboTextSize.addItem("256");
        comboTextSize.setEnabled(false);
        panel.add(comboTextSize);

        comboKeySize = new JComboBox();
        comboKeySize.setBounds(510, 100, 50, 20);
        comboKeySize.addItem("128");
        comboKeySize.addItem("192");
        comboKeySize.addItem("256");
        panel.add(comboKeySize);

        textArea = new JTextArea("");
        textArea.setFont(font);
        textArea.setEditable(false);

        scrollLog = new JScrollPane(textArea);
        scrollLog.setBounds(20, 330, 540, 100);
        scrollLog.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        panel.add(scrollLog);

        ActionListener aListener = new actionListener();

        buttonEncrypt = new JButton("Зашифровать");
        buttonEncrypt.setBounds(240, 260, 150, 30);
        buttonEncrypt.addActionListener(aListener);
        panel.add(buttonEncrypt);

        buttonDecrypt = new JButton("Расшифровать");
        buttonDecrypt.setBounds(410, 260, 150, 30);
        buttonDecrypt.addActionListener(aListener);
        panel.add(buttonDecrypt);

        buttonOpenText = new JButton();
        buttonOpenText.setIcon(iconOpen);
        buttonOpenText.setBounds(20, 20, 20, 20);
        buttonOpenText.addActionListener(aListener);
        panel.add(buttonOpenText);

        buttonOpenKey = new JButton();
        buttonOpenKey.setIcon(iconOpen);
        buttonOpenKey.setBounds(20, 100, 20, 20);
        buttonOpenKey.addActionListener(aListener);
        panel.add(buttonOpenKey);

        buttonOpenCipher = new JButton();
        buttonOpenCipher.setIcon(iconOpen);
        buttonOpenCipher.setBounds(20, 180, 20, 20);
        buttonOpenCipher.addActionListener(aListener);
        panel.add(buttonOpenCipher);

        buttonDetails = new JButton("Подробнее");
        buttonDetails.setBounds(440, 440, 120, 20);
        buttonDetails.addActionListener(aListener);
        buttonDetails.setEnabled(false);
        panel.add(buttonDetails);

        buttonAbout = new JButton("О программе");
        buttonAbout.setBounds(20, 440, 120, 20);
        buttonAbout.addActionListener(aListener);
        panel.add(buttonAbout);

        buttonSaveLog = new JButton();
        buttonSaveLog.setIcon(iconSave);
        buttonSaveLog.setBounds(20, 300, 20, 20);
        buttonSaveLog.addActionListener(aListener);
        panel.add(buttonSaveLog);

        getContentPane().add(panel);
        setPreferredSize(new Dimension(600, 520));
    }

    public class actionListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == buttonEncrypt) {
                if (checkHexText.isSelected()) {
                    source = hexToString(textSource.getText());
                } else {
                    source = textSource.getText();
                }

                if (checkHexKey.isSelected()) {
                    key = hexToString(textKey.getText());
                } else {
                    key = textKey.getText();
                }

                if (source.equals("") || key.equals("")) {
                    JOptionPane.showMessageDialog(null, "Введите исходные данные");

                } else {
                    textSize = getItem(comboTextSize.getSelectedIndex());
                    keySize = getItem(comboKeySize.getSelectedIndex());
                    rijnMaker rm = new rijnMaker(textSize, keySize);
                    textCipher.setText(rm.encrypt(source, key));

                    textArea.setText("");
                    textArea.append("enc[0].input: " + logger.getInit()+"\n");
                    textArea.append("enc[0].k_sch: " + logger.getKey(0)+"\n");

                    for (int i = 0; i < 60; i++) {
                        if (!logger.getState(i).equals("")) {
                            textArea.append("enc["+(i/4+1)+"].");
                            textArea.append(logger.getDescr(i));
                            textArea.append(logger.getState(i) + "\n");

                            if (i%4==3) {
                                textArea.append("enc["+(i/4+1)+"].k_sch: ");
                                textArea.append(logger.getKey(i/4+1)+"\n");
                            }
                        }
                    }
                    textArea.setCaretPosition(0);
                    
                    buttonDetails.setEnabled(true);
                }
            } else if (e.getSource() == buttonDecrypt) {
                source = textCipher.getText();

                if (checkHexKey.isSelected()) {
                    key = hexToString(textKey.getText());
                } else {
                    key = textKey.getText();
                }

                if (source.equals("") || key.equals("")) {
                    JOptionPane.showMessageDialog(null, "Введите исходные данные");

                } else {
                    textSize = getItem(comboTextSize.getSelectedIndex());
                    keySize = getItem(comboKeySize.getSelectedIndex());
                    rijnMaker rm = new rijnMaker(textSize, keySize);
                    textSource.setText(rm.decrypt(textCipher.getText(), textKey.getText()));

                    textArea.setText("");
                    textArea.append("inv[0].input: " + logger.getInit()+"\n");
                    textArea.append("inv[0].k_sch: " + logger.getKey(logger.getNR())+"\n");

                    for (int i = 0; i < 60; i++) {
                        if (!logger.getState(i).equals("")) {
                            if (i>2) {
                                textArea.append("inv["+((i+1)/4+1)+"].");
                            }
                            else {
                                textArea.append("inv["+(i/4+1)+"].");
                            }
                            textArea.append(logger.getDescr(i));
                            textArea.append(logger.getState(i) + "\n");
                        }
                    }
                    textArea.setCaretPosition(0);

                    buttonDetails.setEnabled(false);
                }

            } else if (e.getSource() == buttonOpenText) {
                int ret = fc.showOpenDialog(null);

                if (ret == JFileChooser.APPROVE_OPTION) {
                    textSource.setText(readFile(fc.getSelectedFile()));
                }
            } else if (e.getSource() == buttonOpenKey) {
                int ret = fc.showOpenDialog(null);

                if (ret == JFileChooser.APPROVE_OPTION) {
                    textKey.setText(readFile(fc.getSelectedFile()));
                }
            } else if (e.getSource() == buttonOpenCipher) {
                int ret = fc.showOpenDialog(null);

                if (ret == JFileChooser.APPROVE_OPTION) {
                    textCipher.setText(readFile(fc.getSelectedFile()));
                }
            } else if (e.getSource() == buttonDetails) {
                formDetails frame = new formDetails();
                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            } else if (e.getSource() == buttonAbout) {
                JOptionPane.showMessageDialog(null, "Программная реализация \n" +
                        "криптографического алгоритма AES \n" +
                        "в рамках курсовой работы \n" +
                        "по дисциплине \"Методы и средства \n" +
                        "компьютерных информационных технологий\" \n" +
                        "ст. гр. АН-064 \nЧупилко Дениса Витальевича");
            } else if (e.getSource() == buttonSaveLog) {
                int ret = fc.showSaveDialog(null);

                if (ret == JFileChooser.APPROVE_OPTION) {
                    writeFile(fc.getSelectedFile());
                }
            }
        }
    }

    public int getItem(int index) {
        int ret = 0;
        switch (index) {
            case 0:
                ret = 16;
                break;
            case 1:
                ret = 24;
                break;
            case 2:
                ret = 32;
                break;
        }
        return ret;
    }

    public String readFile(File file) {
        String ret = "";

        try {
            FileInputStream in = new FileInputStream(file);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));

            ret = br.readLine();

            in.close();
            br.close();
        } catch (FileNotFoundException err) {
            err.printStackTrace();
        } catch (IOException err) {
            err.printStackTrace();
        }

        return ret;
    }

    public void writeFile(File file) {
        try {
            if (!file.exists()) {
                file.createNewFile();
            }

            FileOutputStream out = new FileOutputStream(file);
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(out));

            bw.write(textArea.getText());

            out.close();
            bw.close();
        } catch (FileNotFoundException err) {
            err.printStackTrace();
        } catch (IOException err) {
            err.printStackTrace();
        }
    }

    public String hexToString(String s) {
        StringBuffer sb = new StringBuffer();
        int[] t = new int[s.length() / 2];

        for (int i = 0; i < s.length(); i = i + 2) {
            t[i / 2] = Integer.valueOf(s.substring(i, i + 2), 16).intValue();
        }

        for (int i = 0; i < t.length; i++) {
            sb.append((char) t[i]);
        }

        return sb.toString();
    }

    protected static ImageIcon createIcon(String path) {
        URL imgURL = formMain.class.getResource(path);
        return new ImageIcon(imgURL);
    }

    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                formMain frame = new formMain();
                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        });
    }
}