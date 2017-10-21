package clientlab;

import Game.GameOnline; 
import Game.GameOffline; 
import Game.GameOn; 
import Communication.Information.Connection;
import Communication.Information.Type_pck;
import Communication.Information;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;


public class CreateGame_Offline extends javax.swing.JFrame 
{

    public CreateGame_Offline()
    {
        super( "New Game" );


        initComponents();
    }
    
    class EventHandler extends WindowAdapter
    {
            public void windowClosing(WindowEvent e)
            {
                setVisible(false);
            }
     }   
    
    
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        ExternalPanel = new javax.swing.JPanel();
        optionPanel = new javax.swing.JPanel();
        timeText = new javax.swing.JTextField();
        labelTime = new javax.swing.JLabel();
        buttonCreate = new javax.swing.JButton();
        labelMinutes = new javax.swing.JLabel();
        gameMode = new javax.swing.JComboBox();
        labelMode = new javax.swing.JLabel();
        labelgoals1 = new javax.swing.JLabel();
        goalsText = new javax.swing.JTextField();
        labelgoals = new javax.swing.JLabel();
        labelgoals2 = new javax.swing.JLabel();
        labelballUpdate = new javax.swing.JLabel();
        freqBall = new javax.swing.JTextField();
        labelms = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);

        ExternalPanel.setBackground(new java.awt.Color(255, 255, 255));
        ExternalPanel.setMaximumSize(new java.awt.Dimension(400, 400));
        ExternalPanel.setPreferredSize(new java.awt.Dimension(280, 280));

        optionPanel.setBackground(new java.awt.Color(0, 102, 255));
        optionPanel.setForeground(new java.awt.Color(0, 255, 255));

        timeText.setFont(new java.awt.Font("Monospaced", 0, 11)); // NOI18N
        timeText.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        timeText.setText("5");

        labelTime.setFont(new java.awt.Font("Monospaced", 0, 11)); // NOI18N
        labelTime.setText("Time");

        buttonCreate.setFont(new java.awt.Font("Monospaced", 0, 11)); // NOI18N
        buttonCreate.setText("Create");
        buttonCreate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonCreateActionPerformed(evt);
            }
        });

        labelMinutes.setFont(new java.awt.Font("Monospaced", 0, 11)); // NOI18N
        labelMinutes.setText("minutes");

        gameMode.setEditable(true);
        gameMode.setFont(new java.awt.Font("Monospaced", 0, 11)); // NOI18N
        gameMode.setMaximumRowCount(3);
        gameMode.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Simple", "Complex Football", "Complex Water" }));

        labelMode.setFont(new java.awt.Font("Monospaced", 0, 11)); // NOI18N
        labelMode.setText("Game mode");

        labelgoals1.setFont(new java.awt.Font("Monospaced", 0, 11)); // NOI18N
        labelgoals1.setText("Goals to win");

        goalsText.setFont(new java.awt.Font("Monospaced", 0, 11)); // NOI18N
        goalsText.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        goalsText.setText("5");

        labelgoals.setFont(new java.awt.Font("Monospaced", 0, 11)); // NOI18N
        labelgoals.setText("goals");

        labelgoals2.setFont(new java.awt.Font("Monospaced", 0, 11)); // NOI18N
        labelgoals2.setText("No limit of goals => 0 goals");

        labelballUpdate.setFont(new java.awt.Font("Monospaced", 0, 11)); // NOI18N
        labelballUpdate.setText("Ball speed");

        freqBall.setFont(new java.awt.Font("Monospaced", 0, 11)); // NOI18N
        freqBall.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        freqBall.setText("100");

        labelms.setFont(new java.awt.Font("Monospaced", 0, 11)); // NOI18N
        labelms.setText("ms");

        javax.swing.GroupLayout optionPanelLayout = new javax.swing.GroupLayout(optionPanel);
        optionPanel.setLayout(optionPanelLayout);
        optionPanelLayout.setHorizontalGroup(
            optionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(optionPanelLayout.createSequentialGroup()
                .addGroup(optionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(optionPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(labelgoals2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(optionPanelLayout.createSequentialGroup()
                        .addGroup(optionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(optionPanelLayout.createSequentialGroup()
                                .addGap(75, 75, 75)
                                .addComponent(buttonCreate))
                            .addGroup(optionPanelLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(optionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(labelTime)
                                    .addGroup(optionPanelLayout.createSequentialGroup()
                                        .addComponent(labelMode)
                                        .addGap(27, 27, 27)
                                        .addGroup(optionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(gameMode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addGroup(optionPanelLayout.createSequentialGroup()
                                                .addComponent(timeText, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addComponent(labelMinutes, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE))))
                                    .addComponent(labelgoals1)
                                    .addGroup(optionPanelLayout.createSequentialGroup()
                                        .addGap(90, 90, 90)
                                        .addComponent(goalsText, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(labelgoals, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE))))
                            .addGroup(optionPanelLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(optionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(labelballUpdate)
                                    .addGroup(optionPanelLayout.createSequentialGroup()
                                        .addGap(90, 90, 90)
                                        .addComponent(freqBall, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(labelms, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                        .addGap(0, 19, Short.MAX_VALUE)))
                .addContainerGap())
        );
        optionPanelLayout.setVerticalGroup(
            optionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(optionPanelLayout.createSequentialGroup()
                .addGap(26, 26, 26)
                .addGroup(optionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelTime)
                    .addComponent(timeText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(labelMinutes))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(optionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelMode)
                    .addComponent(gameMode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(optionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelgoals1)
                    .addComponent(goalsText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(labelgoals))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(labelgoals2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(optionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelballUpdate)
                    .addComponent(freqBall, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(labelms))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(buttonCreate)
                .addContainerGap())
        );

        javax.swing.GroupLayout ExternalPanelLayout = new javax.swing.GroupLayout(ExternalPanel);
        ExternalPanel.setLayout(ExternalPanelLayout);
        ExternalPanelLayout.setHorizontalGroup(
            ExternalPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ExternalPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(optionPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        ExternalPanelLayout.setVerticalGroup(
            ExternalPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ExternalPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(optionPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(ExternalPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 289, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(ExternalPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 235, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void buttonCreateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonCreateActionPerformed

  
              //  (Integer.parseInt(timeText.getText()))
        int duration = (Integer.parseInt(timeText.getText()));
        int goals = (Integer.parseInt(goalsText.getText()));
        int fqBall = (Integer.parseInt(freqBall.getText()));
        GameOffline game = new GameOffline(gameMode.getSelectedIndex(),duration, goals, fqBall);
        game.setSize(700,700);
        game.setVisible(true);

    }//GEN-LAST:event_buttonCreateActionPerformed


    public static void main(String args[]) 
    {
      
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) 
                {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(CreateGame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(CreateGame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(CreateGame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(CreateGame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
               
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel ExternalPanel;
    private javax.swing.JButton buttonCreate;
    private javax.swing.JTextField freqBall;
    private javax.swing.JComboBox gameMode;
    private javax.swing.JTextField goalsText;
    private javax.swing.JLabel labelMinutes;
    private javax.swing.JLabel labelMode;
    private javax.swing.JLabel labelTime;
    private javax.swing.JLabel labelballUpdate;
    private javax.swing.JLabel labelgoals;
    private javax.swing.JLabel labelgoals1;
    private javax.swing.JLabel labelgoals2;
    private javax.swing.JLabel labelms;
    private javax.swing.JPanel optionPanel;
    private javax.swing.JTextField timeText;
    // End of variables declaration//GEN-END:variables
}
