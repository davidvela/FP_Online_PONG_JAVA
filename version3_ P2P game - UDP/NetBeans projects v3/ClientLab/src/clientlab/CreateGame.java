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


public class CreateGame extends javax.swing.JFrame 
{
    private DatagramSocket client; 
    private Information information_udp;
    private InetAddress server_address;
    private String hostServer; // = "127.0.0.1"; "148.197.34.67";
    private int serverPort_udp = 5003;

    //receive the socket udp and the information.
    public CreateGame(DatagramSocket socket, Information information_udp, String host)
    {
        super( "New Game" );
        this.information_udp = information_udp;
        client = socket;
        hostServer = host;
        try //www.cafealuait.org/books/jnp3/examples/09/WhoisClient.java
        {server_address = InetAddress.getByName(hostServer);  }     
        catch (UnknownHostException ex)
        { System.exit(1);  }
         addWindowListener(new EventHandler()); 

        initComponents();
    }

    private void sendData( Type_pck type_pck )
    {
       
            String message = new String(Type_pck.NEW_GAME.toString() + "\n"
            + information_udp.game_pck.getNameGame() + "\n" 
            + information_udp.game_pck.getNumberPlayers() + "\n"
            + information_udp.game_pck.getDuration() + "\n"  );    
            byte[] data = message.getBytes();
                           
            try 
            {  
                DatagramPacket sendPacket = new DatagramPacket( data, 
                data.length, server_address, serverPort_udp );
                client.send( sendPacket ); 
            }
            catch ( IOException ioException ) 
            { ioException.printStackTrace();  } 
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
        labelPlayers = new javax.swing.JLabel();
        playersText = new javax.swing.JTextField();
        nameText = new javax.swing.JTextField();
        labelName = new javax.swing.JLabel();
        buttonCreate = new javax.swing.JButton();
        labelMinutes = new javax.swing.JLabel();
        freqBall = new javax.swing.JTextField();
        labelms = new javax.swing.JLabel();
        labelballUpdate = new javax.swing.JLabel();

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

        labelPlayers.setFont(new java.awt.Font("Monospaced", 0, 11)); // NOI18N
        labelPlayers.setText("Players");

        playersText.setFont(new java.awt.Font("Monospaced", 0, 11)); // NOI18N
        playersText.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        playersText.setText("2");

        nameText.setFont(new java.awt.Font("Monospaced", 0, 11)); // NOI18N
        nameText.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        nameText.setText("Game_number1");

        labelName.setFont(new java.awt.Font("Monospaced", 0, 11)); // NOI18N
        labelName.setText("Name");

        buttonCreate.setFont(new java.awt.Font("Monospaced", 0, 11)); // NOI18N
        buttonCreate.setText("Create");
        buttonCreate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonCreateActionPerformed(evt);
            }
        });

        labelMinutes.setFont(new java.awt.Font("Monospaced", 0, 11)); // NOI18N
        labelMinutes.setText("minutes");

        freqBall.setFont(new java.awt.Font("Monospaced", 0, 11)); // NOI18N
        freqBall.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        freqBall.setText("100");

        labelms.setFont(new java.awt.Font("Monospaced", 0, 11)); // NOI18N
        labelms.setText("ms");

        labelballUpdate.setFont(new java.awt.Font("Monospaced", 0, 11)); // NOI18N
        labelballUpdate.setText("Ball speed");

        javax.swing.GroupLayout optionPanelLayout = new javax.swing.GroupLayout(optionPanel);
        optionPanel.setLayout(optionPanelLayout);
        optionPanelLayout.setHorizontalGroup(
            optionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(optionPanelLayout.createSequentialGroup()
                .addGroup(optionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(optionPanelLayout.createSequentialGroup()
                        .addGap(19, 19, 19)
                        .addGroup(optionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, optionPanelLayout.createSequentialGroup()
                                .addGroup(optionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(labelPlayers)
                                    .addComponent(labelTime))
                                .addGap(32, 32, 32)
                                .addComponent(playersText, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, optionPanelLayout.createSequentialGroup()
                                .addGap(80, 80, 80)
                                .addComponent(timeText, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(labelMinutes, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, optionPanelLayout.createSequentialGroup()
                                .addComponent(labelName)
                                .addGap(31, 31, 31)
                                .addGroup(optionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(buttonCreate)
                                    .addComponent(nameText, javax.swing.GroupLayout.PREFERRED_SIZE, 148, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                    .addGroup(optionPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(labelballUpdate)
                        .addGap(18, 18, 18)
                        .addComponent(freqBall, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(labelms, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(30, Short.MAX_VALUE))
        );
        optionPanelLayout.setVerticalGroup(
            optionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(optionPanelLayout.createSequentialGroup()
                .addGroup(optionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(optionPanelLayout.createSequentialGroup()
                        .addGap(46, 46, 46)
                        .addGroup(optionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(labelPlayers)
                            .addComponent(playersText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(optionPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(optionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(labelName)
                            .addComponent(nameText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(16, 16, 16)
                .addGroup(optionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelTime)
                    .addComponent(timeText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(labelMinutes))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(optionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelballUpdate)
                    .addComponent(freqBall, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(labelms))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(buttonCreate)
                .addContainerGap())
        );

        javax.swing.GroupLayout ExternalPanelLayout = new javax.swing.GroupLayout(ExternalPanel);
        ExternalPanel.setLayout(ExternalPanelLayout);
        ExternalPanelLayout.setHorizontalGroup(
            ExternalPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ExternalPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(optionPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        ExternalPanelLayout.setVerticalGroup(
            ExternalPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ExternalPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(optionPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(ExternalPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 276, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(ExternalPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 192, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void buttonCreateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonCreateActionPerformed

        information_udp.game_pck.setNameGame(nameText.getText());
        information_udp.game_pck.setNumberPlayers
                (Integer.parseInt(playersText.getText()));
        information_udp.game_pck.setDuration
                (Integer.parseInt(timeText.getText()));
        information_udp.game_pck.setFqBall
                 (Integer.parseInt(freqBall.getText()));
        
        sendData(Type_pck.NEW_GAME);

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
    private javax.swing.JLabel labelMinutes;
    private javax.swing.JLabel labelName;
    private javax.swing.JLabel labelPlayers;
    private javax.swing.JLabel labelTime;
    private javax.swing.JLabel labelballUpdate;
    private javax.swing.JLabel labelms;
    private javax.swing.JTextField nameText;
    private javax.swing.JPanel optionPanel;
    private javax.swing.JTextField playersText;
    private javax.swing.JTextField timeText;
    // End of variables declaration//GEN-END:variables
}
