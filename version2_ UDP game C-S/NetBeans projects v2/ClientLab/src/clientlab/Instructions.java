
package clientlab;

import java.awt.*;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.text.*;
import java.util.*;
import javax.swing.*;
import javax.swing.text.*;


public class Instructions extends javax.swing.JFrame 
{
    public Instructions() 
    {
        super("Instructions");       
        initComponents();     
        displayArea.setText("Welcome to the instructions panel of MULTI PONG"
                + "\n\n This panel provides information about:"
                + "\n\t-Play Pong Online"
                + "\n\t-Play Pong Offline"
                + "\n"
                + "\n"
                + "Moreover, this application allows you to send messages the other "
                + "users writing in the input box and pulse 'enter' once finished "
                + "your message"
                + "\n"
                + "\n"
                + "\nThank you for using MULTI PONG.");

        //http://stackoverflow.com/questions/4059198/jtextpane-appending-a-new-string
        SimpleAttributeSet keyWord = new SimpleAttributeSet();
        StyleConstants.setForeground(keyWord, Color.RED);
        StyleConstants.setBackground(keyWord, Color.YELLOW);
        StyleConstants.setBold(keyWord, true);
       
        Document doc = displayArea.getDocument();
        
       /* try 
        {
          doc.insertString(doc.getLength(), "TEXT", keyWord);
        } catch(BadLocationException exc) { 
                exc.printStackTrace(); }*/
    }


    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane2 = new javax.swing.JScrollPane();
        jTextPane1 = new javax.swing.JTextPane();
        jPanel1 = new javax.swing.JPanel();
        mainTitle = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        displayArea = new javax.swing.JTextPane();
        mainPageButton = new javax.swing.JButton();
        gamesButton = new javax.swing.JButton();
        offlineButton = new javax.swing.JButton();
        onlineButton = new javax.swing.JButton();
        othersButton = new javax.swing.JButton();
        titleLabel = new javax.swing.JLabel();

        jScrollPane2.setViewportView(jTextPane1);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);

        jPanel1.setBackground(new java.awt.Color(13, 10, 177));

        mainTitle.setFont(new java.awt.Font("Monospaced", 1, 24)); // NOI18N
        mainTitle.setForeground(new java.awt.Color(255, 255, 255));
        mainTitle.setText("MULTI  PONG ");

        displayArea.setEditable(false);
        displayArea.setFont(new java.awt.Font("Monospaced", 0, 11)); // NOI18N
        jScrollPane3.setViewportView(displayArea);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 360, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(98, 98, 98)
                        .addComponent(mainTitle, javax.swing.GroupLayout.PREFERRED_SIZE, 185, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 89, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(13, 13, 13)
                .addComponent(mainTitle, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 220, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(42, Short.MAX_VALUE))
        );

        mainPageButton.setFont(new java.awt.Font("Monospaced", 0, 11)); // NOI18N
        mainPageButton.setText("Main page");
        mainPageButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mainPageButtonActionPerformed(evt);
            }
        });

        gamesButton.setFont(new java.awt.Font("Monospaced", 0, 11)); // NOI18N
        gamesButton.setText("Kind of games");
        gamesButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                gamesButtonActionPerformed(evt);
            }
        });

        offlineButton.setFont(new java.awt.Font("Monospaced", 0, 11)); // NOI18N
        offlineButton.setText("Game Offline");
        offlineButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                offlineButtonActionPerformed(evt);
            }
        });

        onlineButton.setFont(new java.awt.Font("Monospaced", 0, 11)); // NOI18N
        onlineButton.setText("Game Online");
        onlineButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                onlineButtonActionPerformed(evt);
            }
        });

        othersButton.setFont(new java.awt.Font("Monospaced", 0, 11)); // NOI18N
        othersButton.setText("Others");
        othersButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                othersButtonActionPerformed(evt);
            }
        });

        titleLabel.setFont(new java.awt.Font("Monospaced", 1, 11)); // NOI18N
        titleLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        titleLabel.setText("Menu");
        titleLabel.setToolTipText("");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(mainPageButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(gamesButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(offlineButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(onlineButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(othersButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(39, 39, 39)
                        .addComponent(titleLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(26, 26, 26)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(titleLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 51, Short.MAX_VALUE)
                        .addComponent(mainPageButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(gamesButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(offlineButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(onlineButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(othersButton)
                        .addGap(92, 92, 92))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap())))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void mainPageButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mainPageButtonActionPerformed
        displayArea.setText("Welcome to the instructions panel of MULTI PONG"
                + "\n\nThis panel teach you to:"
                + "\n\t-Play Pong Online"
                + "\n\t-Play Pong Offline"
                + "\n"
                + "\n"
                + "Furthermore, this application allows to send messages with other users "
                + "through writing the message in the input box and pulse 'enter' once finished "
                + "your message"
                + "\n"
                + "\n"
                + "\n\tThank you for using MULTI PONG."
                );        
        
    }//GEN-LAST:event_mainPageButtonActionPerformed

    private void gamesButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_gamesButtonActionPerformed
        displayArea.setText("KIND OF GAME"
                + "\n\nThere are two kind of games:"
                + "\n\t-Pong Online"
                + "\n\t-Pong Offline"
                + "\nEach one with 3 modes of games: simple, football design and water design."
                + "However, the controls are always the same, if you are: "
                + "\n\tthe player 1: (left side)\nyour keys are up and down"
                + "\n\tthe player 0: (right side)\nyour keys will be w to up and s to down. "
                + "\n"
                + "\n"
                + "\n\tEnjoy your Game."
                );     }//GEN-LAST:event_gamesButtonActionPerformed

    private void offlineButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_offlineButtonActionPerformed
        displayArea.setText("PONG OFFLINE"
                + "\n\nTo play Pong offline you don't need any other person,"
                + "the only thing that is neccessary is choose a game time duration and "
                + "a max number of goals or cero if your choice is to play "
                + "freely without goals limitation."
                + "\n\n"
                + "\n\tEnjoy your Game."
                );
    }//GEN-LAST:event_offlineButtonActionPerformed

    private void onlineButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_onlineButtonActionPerformed
        displayArea.setText("PONG ONLINE"
                + "\n\nTo play Pong online the first thing that is neccesary to "
                + "connect to a server running, through writing the IP address of the server "
                + "in the panel which is showed you when you click in the option connect"
                + ". For instance, this project is using "
                + "the server with the IP address: \n\t148.197.34.67"
                + "\n"
                +"\nThe next thing to do is to create a game and wait to another player."
                +"However if the number of players is not complete in 30 seconds it is neccessary to create "
                + "another game. Furthermore, the player who create the game is the number cero (left) w/s and the "
                + "other player the number 1 (right) up/down "
                + "\n"
                + "\n\n"
                + "\n\tEnjoy your Game."
                );
    }//GEN-LAST:event_onlineButtonActionPerformed

    private void othersButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_othersButtonActionPerformed
               displayArea.setText("OTHER"
                + "\n\nIf you have any question or sugestion the "
                       + "administrator email is:"
                       + "\nUP634033@myport.ac.uk"
                       + "\n\n"
                + "\n\tEnjoy your Game."
                );
    }//GEN-LAST:event_othersButtonActionPerformed


    public static void main(String args[]) {
 
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /*
         * If Nimbus (introduced in Java SE 6) is not available, stay with the
         * default look and feel. For details see
         * http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Instructions.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Instructions.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Instructions.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Instructions.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() 
            {
                new Instructions().setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextPane displayArea;
    private javax.swing.JButton gamesButton;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTextPane jTextPane1;
    private javax.swing.JButton mainPageButton;
    private javax.swing.JLabel mainTitle;
    private javax.swing.JButton offlineButton;
    private javax.swing.JButton onlineButton;
    private javax.swing.JButton othersButton;
    private javax.swing.JLabel titleLabel;
    // End of variables declaration//GEN-END:variables
}
