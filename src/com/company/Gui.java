package com.company;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;


/**
 * Created by Soudabeh on 8/31/2017.
 */
public class Gui extends JFrame {


    JLabel jLabel1 = new JLabel();
    JLabel jLabel2 = new JLabel();
    ImageIcon lock = new ImageIcon("lock.png");
    JButton jButton= new JButton(lock);
    JTextField jTextField1= new JTextField();
    JTextArea jTextArea1= new JTextArea();

    JTextField jTextField2= new JTextField();
    JTextArea jTextArea2= new JTextArea();

    JTextField jTextField3= new JTextField();
    JTextArea jTextArea3= new JTextArea();

    JTextField jTextField4= new JTextField();
    JTextArea jTextArea4= new JTextArea();

    String fileName = new Date(System.currentTimeMillis()).toString();
    JTextArea jTextAreaDate= new JTextArea();



    public Gui(){

        JFrame jFrame= new JFrame();
        setLayout(null);
        jFrame.setBounds(0,0,1200,800);
        jFrame.setLayout(new GridLayout(1,2));
        jFrame.setTitle("Compute DUKPT");
        jFrame.add(jLabel1);
        jFrame.add(jLabel2);
        jLabel1.setBounds(0,0,600,800);
        jLabel2.setBounds(603,0,600,800);
       /* jLabel1.setBackground(Color.GRAY);
        jLabel1.setOpaque(true);
        jLabel2.setBackground(Color.GRAY);
        jLabel2.setOpaque(true);*/



        jLabel1.setBorder(BorderFactory.createLineBorder(Color.GRAY, 3));
        jLabel2.setBorder(BorderFactory.createLineBorder(Color.GRAY, 3));
        jLabel1.add(jButton);

        jTextAreaDate.setFont(new Font("SansSerif", Font.ITALIC, 20));
        jTextAreaDate.setBounds(60,70,300,100);
        jLabel2.add(jTextAreaDate);


        jTextField1.setBounds(120,200,420,50);
        jTextField1.setFont( new Font("SansSerif", Font.PLAIN, 20));
        jLabel1.add(jTextField1);


        jTextArea1.setText("BDK:");
        jTextArea1.setFont(new Font("SansSerif", Font.LAYOUT_NO_START_CONTEXT, 20));
        jTextArea1.setBounds(60,212,100,30);
        jLabel1.add(jTextArea1);
        jTextArea1.setForeground(Color.BLACK);

        jTextField2.setBounds(120,400,420,50);
        jTextField2.setFont( new Font("SansSerif", Font.PLAIN, 20));
        jLabel1.add(jTextField2);

        jTextArea2.setText("KSN:");
        jTextArea2.setFont(new Font("SansSerif", Font.LAYOUT_NO_START_CONTEXT, 20));
        jTextArea2.setBounds(60,412,100,30);
        jLabel1.add(jTextArea2);
        jTextArea2.setForeground(Color.BLACK);

        jTextField3.setBounds(120,200,420,50);
        jTextField3.setFont( new Font("SansSerif", Font.PLAIN, 20));
        jLabel2.add(jTextField3);


        jTextArea3.setText("IPEK:");
        jTextArea3.setFont(new Font("SansSerif", Font.LAYOUT_NO_START_CONTEXT, 20));
        jTextArea3.setForeground(Color.BLACK);

        jTextArea3.setBounds(60,212,100,30);
        jLabel2.add(jTextArea3);

        jTextField4.setBounds(120,400,420,50);
        jTextField4.setFont( new Font("SansSerif", Font.PLAIN, 20));
        jLabel2.add(jTextField4);

        jTextArea4.setText("PEK:");
        jTextArea4.setFont(new Font("SansSerif", Font.LAYOUT_NO_START_CONTEXT, 20));
        jTextArea4.setBounds(60,412,100,30);
        jLabel2.add(jTextArea4);
        jTextArea4.setForeground(Color.BLACK);

        jButton.setLayout(null);
        jButton.setBounds(440,550,60,63);
        jFrame.setVisible(true);
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jTextAreaDate.setText(fileName +"\n Calculator is ready \n *************************");


        jButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {


                String bdkHexString =jTextField1.getText();
                String ksnHexString =jTextField2.getText();

                byte[] bdk = ComputeDukpt.toByteArray(bdkHexString);
                byte[] ksn = ComputeDukpt.toByteArray(ksnHexString);
                String[] array = new String[2];

                 try {
                   array = ComputeDukpt.computeKey(bdk,ksn);
                } catch (Exception e1) {
                    e1.printStackTrace();
                }

                jTextField3.setText(array[0]);
                jTextField4.setText(array[1]);
            }
        });

    }

}
