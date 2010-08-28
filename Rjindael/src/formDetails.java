import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.Font;

public class formDetails extends JFrame {

    private int position = 0;
    private int count = 0;
    private String initMsg;
    JPanel panel;
    JLabel labelStatus;
    JTextArea[] textArea;
    JButton buttonPrev;
    JButton buttonNext;
    logMaker logger = new logMaker();

    public formDetails() {
        super("Ход работы");
        createGUI();
    }

    public void createGUI() {
        panel = new JPanel();
        panel.setBounds(0, 0, 750, 300);
        panel.setLayout(null);

        textArea = new JTextArea[120];
        Font font = new Font("monospaced", Font.PLAIN, 16);

        for (int i = 0; i < 120; i++) {
            textArea[i] = new JTextArea("");
            textArea[i].setFont(font);
        }

        position = drawTable(0, 20, 100);
        position = drawTable(16, 140, 100);
        position = drawTable(32, 260, 100);
        position = drawTable(64, 380, 100);
        position = drawTable(80, 500, 100);
        position = drawTable(96, 620, 100);

        labelStatus = new JLabel("Исходный текст");
        labelStatus.setBounds(200, 20, 340, 20);
        labelStatus.setHorizontalAlignment(JLabel.CENTER);
        panel.add(labelStatus);

        JLabel labelFirst = new JLabel("State");
        labelFirst.setBounds(20, 70, 95, 20);
        labelFirst.setToolTipText("На начало раунда");
        labelFirst.setHorizontalAlignment(JLabel.CENTER);
        panel.add(labelFirst);

        JLabel labelSecond = new JLabel("SubBytes");
        labelSecond.setBounds(140, 70, 95, 20);
        labelSecond.setToolTipText("<html>Замена байт<br><img src=\""
                + formDetails.class.getResource("img/subbytes.jpg")+"\">");
        labelSecond.setHorizontalAlignment(JLabel.CENTER);
        panel.add(labelSecond);

        JLabel labelThird = new JLabel("ShiftRows");
        labelThird.setBounds(260, 70, 95, 20);
        labelThird.setToolTipText("<html>Сдвиг строк<br><img src=\""
                + formDetails.class.getResource("img/shiftrows.jpg")+"\">");
        labelThird.setHorizontalAlignment(JLabel.CENTER);
        panel.add(labelThird);

        JLabel labelFourth = new JLabel("MixColumns");
        labelFourth.setBounds(380, 70, 95, 20);
        labelFourth.setToolTipText("<html>Перемешивание столбцов<br><img src=\""
                + formDetails.class.getResource("img/mixcolumns.jpg")+"\">");
        labelFourth.setHorizontalAlignment(JLabel.CENTER);
        panel.add(labelFourth);

        JLabel labelFifth = new JLabel("RoundKey");
        labelFifth.setBounds(500, 70, 95, 20);
        labelFifth.setToolTipText("Раундовый ключ");
        labelFifth.setHorizontalAlignment(JLabel.CENTER);
        panel.add(labelFifth);

        JLabel labelSixth = new JLabel("AddRoundKey");
        labelSixth.setBounds(620, 70, 95, 20);
        labelSixth.setToolTipText("<html>Добавление ключа<br><img src=\""
                + formDetails.class.getResource("img/addroundkey.jpg")+"\">");
        labelSixth.setHorizontalAlignment(JLabel.CENTER);
        panel.add(labelSixth);

        ActionListener aListener = new actionListener();

        buttonPrev = new JButton("<= Назад");
        buttonPrev.setBounds(100, 20, 100, 25);
        buttonPrev.addActionListener(aListener);
        panel.add(buttonPrev);

        buttonNext = new JButton("Вперёд =>");
        buttonNext.setBounds(540, 20, 100, 25);
        buttonNext.addActionListener(aListener);
        panel.add(buttonNext);

        buttonPrev.doClick();

        getContentPane().add(panel);
        setPreferredSize(new Dimension(750, 300));
    }

    public class actionListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == buttonPrev) {
                initMsg = logger.getInit();
                count-=4;

                if (count < 0) {
                    count = 0;
                }

                int rnd = count/4+1;

                if (count == 0) {
                    labelStatus.setText("Исходный текст");
                    fillWith(initMsg, 0);
                    fillWith("", 16);
                    fillWith("", 32);
                    fillWith("", 64);
                    fillWith(logger.getKey(rnd-1), 80);
                    fillWith(logger.getState(0), 96);

                    /*for (int i=0;i<16;i++) {
                        textArea[i].setToolTipText(hexToString(textArea[i].getText()));
                    }*/

                } else {
                    labelStatus.setText("Раунд " + rnd);
                    fillWith(logger.getState(count), 0);
                    fillWith(logger.getState(count+1), 16);
                    fillWith(logger.getState(count+2), 32);
                    fillWith(logger.getState(count+3), 64);
                    fillWith(logger.getKey(rnd), 80);
                    fillWith(logger.getState(count+4), 96);
                }
                JOptionPane.showMessageDialog(null, count);
            } else if (e.getSource() == buttonNext) {
                int rnd = count/4+1;

                if (rnd > logger.getNR()) {

                }

                else if (rnd == logger.getNR()) {
                    labelStatus.setText("Финальный раунд");
                    fillWith(logger.getState(count), 0);
                    fillWith(logger.getState(count+1), 16);
                    fillWith(logger.getState(count+2), 32);
                    fillWith("", 64);
                    fillWith(logger.getKey(rnd), 80);
                    fillWith(logger.getState(count+3), 96);
                }

                else {
                    labelStatus.setText("Раунд " + rnd);
                    fillWith(logger.getState(count), 0);
                    fillWith(logger.getState(count+1), 16);
                    fillWith(logger.getState(count+2), 32);
                    fillWith(logger.getState(count+3), 64);
                    fillWith(logger.getKey(rnd), 80);
                    fillWith(logger.getState(count+4), 96);
                    count+=4;
                }
                JOptionPane.showMessageDialog(null, count);
            }
        }
    }

    public int drawTable(int index, int x, int y) {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                textArea[index].setBounds(x + 25 * i, y + 25 * j, 20, 20);
                textArea[index].setEditable(false);
                panel.add(textArea[index]);

                index++;
            }
        }
        return index;
    }

    public void fillWith(String str, int start) {
        if (str.equals(""))
            str="                                ";

        for (int i = 0; i <= 30; i += 2) {
            textArea[start + i / 2].setText(str.substring(i, i + 2));
            //textArea[start + i / 2].setToolTipText(hexToString(str.substring(i, i + 2)));
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
}