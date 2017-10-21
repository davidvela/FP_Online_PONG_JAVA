package clientlab;
import Game.GameOnline; 
import Game.GameOffline; 

import Communication.Information.Connection;
import Communication.Information.Type_pck;
import Communication.Information;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.swing.*;
import javax.swing.text.*;



public class ContainerWindow extends javax.swing.JFrame {

    private ExecutorService executor; // Multithreading 

    private Socket client; // socket to communicate with server
    private DatagramSocket socket;
    private Information information_udp;
    private int select_elem;
    Type_pck type_pck;


    private int serverPort_udp = 5001;
    private InetAddress server_address;
    private String hostServer ; ///= "127.0.0.1"; = "148.197.34.67";
    private String message = ""; 
    DefaultListModel model;
    
    public GameOnline app ;    
    
   public ContainerWindow() 
   {
              super( "Portal" );
              enterField = new JTextField(); 
              enterField.setEditable( false );
              information_udp = new Information();
              executor = Executors.newFixedThreadPool(3);
              model = new DefaultListModel();
              addWindowListener(new EventHandler()); 
              initComponents();

            
    }
    
   public void runClient() 
   {
             try //www.cafealuait.org/books/jnp3/examples/09/WhoisClient.java
               {
                    server_address = InetAddress.getByName(hostServer);
               }
             catch (UnknownHostException ex)
               {
                   displayMessage(  "\nError: Could not locate that server" 
                                    + hostServer );
                   System.exit(1);
               }
            try // create DatagramSocket for sending and receiving packets
                {
                    socket = new DatagramSocket();
                    executor.execute(  new Runnable()
                    {   public void run()  
                        {  waitForPackets();    }  }     ); 
                    
                    sendData(Type_pck.HELLO); 
                } 
            catch ( SocketException socketException ) 
            {                
                displayMessage( "\nFailure in the connection of the socket" );
                socketException.printStackTrace();
                System.exit( 1 );

            }       
   }  
   
   public void waitForPackets()
   {
        while ( true ) 
        {
            try // receive packet 
            {
                byte[] data = new byte[ 100 ]; // set up packet  
                DatagramPacket receivePacket = new DatagramPacket( 
                data, data.length );
                socket.receive( receivePacket ); // wait for packet

                // display packet contents
                //displayMessage( "\nPacket received" ); 
                // + "\nFrom host: " + receivePacket.getAddress()
                // + "\nHost port: " + receivePacket.getPort() 
                // + "\nLength: " + receivePacket.getLength() + "\n");
                // +   "\nContaining:\n\t" + new String( receivePacket.getData(), 
                // 0, receivePacket.getLength() ) + "\n" );
                               
                String data_containing = new String( receivePacket.getData(), 
                    0, receivePacket.getLength());
                processPackets(data_containing);     
            } 

            catch ( IOException exception ) 
            {
                displayMessage( exception + "\n" );
                exception.printStackTrace();
            } 
        } 
   } 
      
   // process connection with server
   private void processPackets(String data) throws IOException
   {
        // enable enterField so client user can send messages
        int top;
        setTextFieldEditable( true );
        type_pck = information_udp.DeserializationPck(data);
        //  Type_pck type_pck = information_udp.DeserializationPck();

            switch (type_pck)
            {
                case CHAT:  
                        displayMessage( "\n" + information_udp.chat_pck.getName() 
                        + information_udp.chat_pck.getMessage() );
                        break;

                case UPDATE:  //Update the list of games 
                        model.clear();
                        top = information_udp.number_Games;
                        for(int i=0; i<top; i++)
                        {
                            model.add(information_udp.list_games[i].getId(), 
                                    information_udp.list_games[i].CreateName());
                        }
                        jListGames.setModel(model);                        
                        break;

                case ACK: 
                        switch (information_udp.ack_pck.getInReplyTo())
                        {
                            case HELLO: // update the list of games
                                model.clear();
                                top = information_udp.number_Games;
                                for(int i=0; i<top; i++)
                                {
                                    model.add(  information_udp.list_games[i].
                                                    getId(), 
                                                information_udp.list_games[i].
                                                    CreateName());
                                }
                                jListGames.setModel(model);
                                displayMessage( "\nsuccessful connection" );
                                enterField.addActionListener(
                                new ActionListener() 
                                { public void actionPerformed( ActionEvent event )
                                    {
                                        // Message for chat 
                                        information_udp.chat_pck.setMessage
                                                (event.getActionCommand());
                                        if( fieldName.getText().equals(""))
                                            fieldName.setText("unknown");
                                        information_udp.chat_pck.setName(fieldName.getText());

                                        sendData( Type_pck.CHAT );
                                        enterField.setText( "" );
                                    }  }   );
                                information_udp.connection = Connection.ONLINE;
                                break;     

                            case EXIT: 
                                displayMessage("\ndisconnection successfully\n"); 
                                closeConnection();
                                break; 

                            //*****************important cases***********1

                            case NEW_GAME:  
                                app = new GameOnline(information_udp.port_game,
                                    information_udp.game_pck.getNumberPlayers(),
                                        hostServer, gameMode.getSelectedIndex());
                                
                                displayMessage("\ngame of " + 
                                 information_udp.game_pck.getNumberPlayers()
                                        + " players started"); 
                                break;

                            case JOIN_GAME:  
                                app = new GameOnline(information_udp.port_game,
                                        information_udp.list_games
                                            [information_udp.join_pck.getID()].
                                            getNumberPlayers() //2
                                            ,hostServer, 
                                            gameMode.getSelectedIndex());
                                displayMessage("\ngame linked"); 
                                break;              
                        }
                        break;
                 case NACK: 
                        displayMessage( "\nError in the packet: " 
                        + information_udp.ack_pck.getInReplyTo().toString() +
                                "\n" + information_udp.ack_pck.getInfo() ); 
                        break;
            }
   }  

   private void sendData( Type_pck type_pck )
   {
       
            information_udp.SerializationPck(type_pck);
            //displayMessage ("\nSending packet");
            // + "containing: " + information_udp.buffer_out + "\n" );

            byte[] data = information_udp.buffer_out.getBytes();  
            try 
            {  
                // create sendPacket
                DatagramPacket sendPacket = new DatagramPacket( data, 
                data.length, server_address, serverPort_udp );
                socket.send( sendPacket ); 
                //displayArea.append( "\nPacket sent" );
                //displayArea.setCaretPosition(displayArea.getText().length()); 
            }
            catch ( IOException ioException ) 
            {
                displayMessage( ioException + "\n" );
                ioException.printStackTrace();              
            } 
      
   } 
    
   private void displayMessage( final String messageToDisplay )
   {
            SwingUtilities.invokeLater(
                new Runnable()
                {
                    public void run() // updates displayArea
                    {
                        
                        SimpleAttributeSet keyWord = new SimpleAttributeSet();
                     if(type_pck == null)  StyleConstants.setForeground(keyWord, Color.BLACK);
                     else
                        { 
                           switch (type_pck)
                            {
                             case ACK:
                               StyleConstants.setForeground(keyWord, Color.GREEN);
                               break;
                             case CHAT:
                               StyleConstants.setForeground(keyWord, Color.BLUE);
                               break;
                             default: 
                               StyleConstants.setForeground(keyWord, Color.BLACK);
                               break;
                            }
                        
                        }
                        //StyleConstants.setBackground(keyWord, Color.YELLOW);
                        //StyleConstants.setBold(keyWord, true);
                        Document doc = displayArea.getDocument();

                        try 
                        {
                        doc.insertString(doc.getLength(), messageToDisplay, keyWord);
                        } catch(BadLocationException exc) { 
                                exc.printStackTrace(); }
                        
                        
                    //displayArea.append( messageToDisplay );
                    }  }    ); 
   }
   
   private void closeConnection() 
   {
            displayMessage( "\nClosing connection" );
            setTextFieldEditable( false ); // disable enterField
            information_udp.connection = Connection.OFFLINE;
   } 
       
   // manipulates enterField in the event-dispatch thread
   private void setTextFieldEditable( final boolean editable )
   {
            SwingUtilities.invokeLater(
                new Runnable() 
                {
                    public void run() // sets enterField's editability
                    {
                    enterField.setEditable( editable );
                    }     }   );  
   } 


    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        main_Panel = new javax.swing.JPanel();
        enterField = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        jListGames = new javax.swing.JList();
        buttonJoin = new javax.swing.JButton();
        mainTitle = new javax.swing.JLabel();
        fieldName = new javax.swing.JTextField();
        jLabelName = new javax.swing.JLabel();
        jLabelInformation = new javax.swing.JLabel();
        jLabelInput = new javax.swing.JLabel();
        Logo = new javax.swing.JLabel();
        Logo1 = new javax.swing.JLabel();
        Logo2 = new javax.swing.JLabel();
        jLabelInformationGames = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        displayArea = new javax.swing.JTextPane();
        gameMode = new javax.swing.JComboBox();
        jLabelInformationGames1 = new javax.swing.JLabel();
        buttonUpdate = new javax.swing.JButton();
        jMenuBar1 = new javax.swing.JMenuBar();
        menu_File = new javax.swing.JMenu();
        menuItem_Exit = new javax.swing.JMenuItem();
        menuItem_Instructions = new javax.swing.JMenuItem();
        menu_Game = new javax.swing.JMenu();
        menuItem_disconnect = new javax.swing.JMenuItem();
        menuItem_Connect = new javax.swing.JMenuItem();
        menuItem_NewGame = new javax.swing.JMenuItem();
        menuItem_NewGameOnline = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);

        main_Panel.setBackground(new java.awt.Color(0, 153, 204));
        main_Panel.setMaximumSize(new java.awt.Dimension(900, 900));
        main_Panel.setPreferredSize(new java.awt.Dimension(800, 700));

        enterField.setFont(new java.awt.Font("Monospaced", 0, 11)); // NOI18N

        jListGames.setFont(new java.awt.Font("Monospaced", 0, 11)); // NOI18N
        jListGames.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jListGames.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane1.setViewportView(jListGames);

        buttonJoin.setFont(new java.awt.Font("Monospaced", 0, 11)); // NOI18N
        buttonJoin.setText("Join");
        buttonJoin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonJoinActionPerformed(evt);
            }
        });

        mainTitle.setFont(new java.awt.Font("Monospaced", 1, 24)); // NOI18N
        mainTitle.setForeground(new java.awt.Color(255, 255, 255));
        mainTitle.setText("MULTI  PONG ");

        fieldName.setFont(new java.awt.Font("Monospaced", 0, 11)); // NOI18N

        jLabelName.setFont(new java.awt.Font("Monospaced", 0, 11)); // NOI18N
        jLabelName.setText("Insert your name");

        jLabelInformation.setFont(new java.awt.Font("Monospaced", 0, 11)); // NOI18N
        jLabelInformation.setText("Chat and information");

        jLabelInput.setFont(new java.awt.Font("Monospaced", 0, 11)); // NOI18N
        jLabelInput.setText("Input");

        Logo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        Logo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Resources/logo_blue.png"))); // NOI18N
        Logo.setLabelFor(displayArea);

        Logo1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        Logo1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Resources/logo_porterias.png"))); // NOI18N
        Logo1.setLabelFor(displayArea);

        Logo2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        Logo2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Resources/logo_porteria.png"))); // NOI18N
        Logo2.setLabelFor(displayArea);

        jLabelInformationGames.setFont(new java.awt.Font("Monospaced", 0, 11)); // NOI18N
        jLabelInformationGames.setText("List of games");

        displayArea.setEditable(false);
        displayArea.setFont(new java.awt.Font("Monospaced", 0, 11)); // NOI18N
        jScrollPane2.setViewportView(displayArea);

        gameMode.setEditable(true);
        gameMode.setFont(new java.awt.Font("Monospaced", 0, 11)); // NOI18N
        gameMode.setMaximumRowCount(3);
        gameMode.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Simple", "Complex Football", "Complex Water" }));

        jLabelInformationGames1.setFont(new java.awt.Font("Monospaced", 0, 11)); // NOI18N
        jLabelInformationGames1.setText("Select the game mode");

        buttonUpdate.setFont(new java.awt.Font("Monospaced", 0, 11)); // NOI18N
        buttonUpdate.setText("Update");
        buttonUpdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonUpdateActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout main_PanelLayout = new javax.swing.GroupLayout(main_Panel);
        main_Panel.setLayout(main_PanelLayout);
        main_PanelLayout.setHorizontalGroup(
            main_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(main_PanelLayout.createSequentialGroup()
                .addGroup(main_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(main_PanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(main_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(Logo2, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(mainTitle)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 164, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(main_PanelLayout.createSequentialGroup()
                                .addGap(10, 10, 10)
                                .addGroup(main_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(gameMode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabelInformationGames1, javax.swing.GroupLayout.PREFERRED_SIZE, 164, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                    .addGroup(main_PanelLayout.createSequentialGroup()
                        .addGap(40, 40, 40)
                        .addComponent(jLabelInformationGames, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(main_PanelLayout.createSequentialGroup()
                        .addGap(33, 33, 33)
                        .addComponent(buttonJoin, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addGroup(main_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(main_PanelLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(Logo1, javax.swing.GroupLayout.PREFERRED_SIZE, 218, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())
                    .addGroup(main_PanelLayout.createSequentialGroup()
                        .addGroup(main_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(Logo, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(buttonUpdate, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(main_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(main_PanelLayout.createSequentialGroup()
                                .addGap(40, 40, 40)
                                .addGroup(main_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(enterField, javax.swing.GroupLayout.DEFAULT_SIZE, 290, Short.MAX_VALUE)
                                    .addComponent(jLabelInput, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(main_PanelLayout.createSequentialGroup()
                                        .addComponent(jLabelName, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addGap(18, 18, 18)
                                        .addComponent(fieldName, javax.swing.GroupLayout.PREFERRED_SIZE, 154, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(jScrollPane2))
                                .addGap(0, 22, Short.MAX_VALUE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, main_PanelLayout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabelInformation, javax.swing.GroupLayout.PREFERRED_SIZE, 155, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(85, 85, 85))))))
        );
        main_PanelLayout.setVerticalGroup(
            main_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(main_PanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(main_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(main_PanelLayout.createSequentialGroup()
                        .addGroup(main_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(main_PanelLayout.createSequentialGroup()
                                .addComponent(mainTitle, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(Logo2, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 24, Short.MAX_VALUE)
                                .addComponent(jLabelInformationGames))
                            .addGroup(main_PanelLayout.createSequentialGroup()
                                .addComponent(Logo1, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabelInformation)))
                        .addGap(6, 6, 6)
                        .addGroup(main_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 155, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 155, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(main_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(main_PanelLayout.createSequentialGroup()
                                .addGroup(main_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(fieldName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(main_PanelLayout.createSequentialGroup()
                                        .addGap(6, 6, 6)
                                        .addComponent(jLabelName, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabelInput)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(enterField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(main_PanelLayout.createSequentialGroup()
                                .addComponent(jLabelInformationGames1, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(main_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(gameMode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(buttonUpdate))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(buttonJoin)))
                        .addContainerGap(19, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, main_PanelLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(Logo, javax.swing.GroupLayout.PREFERRED_SIZE, 179, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(150, 150, 150))))
        );

        jMenuBar1.setToolTipText("Client");

        menu_File.setText("File ");
        menu_File.setFont(new java.awt.Font("Monospaced", 0, 11)); // NOI18N

        menuItem_Exit.setFont(new java.awt.Font("Monospaced", 0, 11)); // NOI18N
        menuItem_Exit.setText("Exit");
        menuItem_Exit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItem_ExitActionPerformed(evt);
            }
        });
        menu_File.add(menuItem_Exit);

        menuItem_Instructions.setFont(new java.awt.Font("Monospaced", 0, 11)); // NOI18N
        menuItem_Instructions.setText("Instructions");
        menuItem_Instructions.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItem_InstructionsActionPerformed(evt);
            }
        });
        menu_File.add(menuItem_Instructions);

        jMenuBar1.add(menu_File);

        menu_Game.setText("Game");
        menu_Game.setFont(new java.awt.Font("Monospaced", 0, 11)); // NOI18N

        menuItem_disconnect.setFont(new java.awt.Font("Monospaced", 0, 11)); // NOI18N
        menuItem_disconnect.setText("Disconnect");
        menuItem_disconnect.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItem_disconnectActionPerformed(evt);
            }
        });
        menu_Game.add(menuItem_disconnect);

        menuItem_Connect.setFont(new java.awt.Font("Monospaced", 0, 11)); // NOI18N
        menuItem_Connect.setText("Connect");
        menuItem_Connect.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItem_ConnectActionPerformed(evt);
            }
        });
        menu_Game.add(menuItem_Connect);

        menuItem_NewGame.setFont(new java.awt.Font("Monospaced", 0, 11)); // NOI18N
        menuItem_NewGame.setText("New Game offline");
        menuItem_NewGame.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItem_NewGameActionPerformed(evt);
            }
        });
        menu_Game.add(menuItem_NewGame);

        menuItem_NewGameOnline.setFont(new java.awt.Font("Monospaced", 0, 11)); // NOI18N
        menuItem_NewGameOnline.setText("New Game online");
        menuItem_NewGameOnline.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItem_NewGameOnlineActionPerformed(evt);
            }
        });
        menu_Game.add(menuItem_NewGameOnline);

        jMenuBar1.add(menu_Game);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(main_Panel, javax.swing.GroupLayout.PREFERRED_SIZE, 806, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(main_Panel, javax.swing.GroupLayout.PREFERRED_SIZE, 495, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 8, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void menuItem_NewGameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItem_NewGameActionPerformed
        
                CreateGame_Offline create = new CreateGame_Offline();
                create.setSize(300,300);
                create.setLocation(400,200);
                create.setDefaultCloseOperation(DISPOSE_ON_CLOSE); 
                create.setVisible(true);
        
             
    }//GEN-LAST:event_menuItem_NewGameActionPerformed

    private void menuItem_ExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItem_ExitActionPerformed
        System.exit(0);
    }//GEN-LAST:event_menuItem_ExitActionPerformed

    private void menuItem_ConnectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItem_ConnectActionPerformed
    
        if(information_udp.connection == Connection.OFFLINE)
        {            
            //Get IP of the server
            /*int answer = JOptionPane.showConfirmDialog(null, "Connection with the server: \n" 
                    + "Click 'Yes' to connect to the server; or 'No' to connect in localhost: ",  
                    "Server Connection", 
                    JOptionPane.YES_NO_OPTION);
            if(answer != -1)
            {
            if (answer == JOptionPane.YES_OPTION) hostServer = "148.197.34.67";
            else if(answer== JOptionPane.NO_OPTION) hostServer = "127.0.0.1";*/
         
            hostServer = JOptionPane.showInputDialog(null, "Write the IP address of the server \n" 
                    + "follow the next format: 148.197.34.67", "127.0.0.1");
        
            if(hostServer == null) {}
            else 
            {       
                runClient();
            }
        }
        else displayMessage("\nthe user is already connect");

    }//GEN-LAST:event_menuItem_ConnectActionPerformed

    private void menuItem_disconnectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItem_disconnectActionPerformed
       if(information_udp.connection == Connection.ONLINE)
       { 
           sendData(Type_pck.EXIT);
            closeConnection();
       }
       else displayMessage("\nYou are disconnect");
    }//GEN-LAST:event_menuItem_disconnectActionPerformed

    private void menuItem_NewGameOnlineActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItem_NewGameOnlineActionPerformed
            
        if(information_udp.connection == Connection.OFFLINE)
            displayMessage("\nYou are not connected");
        else
        {
                CreateGame create = new CreateGame(this.socket, 
                        this.information_udp, hostServer);
                create.setSize(350,300);
                create.setLocation(400,200);
                create.setDefaultCloseOperation(DISPOSE_ON_CLOSE); 
                create.setVisible(true);
        }
        
    }//GEN-LAST:event_menuItem_NewGameOnlineActionPerformed

    private void buttonJoinActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonJoinActionPerformed
        
        if(information_udp.connection == Connection.OFFLINE)
            displayMessage("\nYou are not connected");
        else 
            if(jListGames.getSelectedIndex()== -1)
                displayMessage("\nSelect a game for playing");
            else 
            { 
                displayMessage( "\nThe element selected is: " + 
                                jListGames.getSelectedIndex());
                information_udp.join_pck.setID(jListGames.getSelectedIndex());
                sendData(Type_pck.JOIN_GAME);
            }
        
        
        
    }//GEN-LAST:event_buttonJoinActionPerformed

    private void menuItem_InstructionsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItem_InstructionsActionPerformed
        Instructions instructions = new Instructions();
        instructions.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        //instructions.setSize(700,700);
        instructions.setVisible(true);
        

    }//GEN-LAST:event_menuItem_InstructionsActionPerformed

    private void buttonUpdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonUpdateActionPerformed
        if(information_udp.connection == Connection.OFFLINE)
            displayMessage("\nYou are not connected");
        else 
        {
                
                sendData(Type_pck.UPDATE);
            }
        
        
    }//GEN-LAST:event_buttonUpdateActionPerformed
    
    public static void main(String args[]) 
    {
     

        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(ContainerWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ContainerWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ContainerWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ContainerWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                new ContainerWindow().setVisible(true);
            }
        });
    }
   
    class EventHandler extends WindowAdapter
    {
            public void windowClosing(WindowEvent e)
            {
                setVisible(false);
                if(information_udp.connection == Connection.ONLINE)
                    sendData(Type_pck.EXIT);
                System.exit( 0 );
            }
        }   
    
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel Logo;
    private javax.swing.JLabel Logo1;
    private javax.swing.JLabel Logo2;
    private javax.swing.JButton buttonJoin;
    private javax.swing.JButton buttonUpdate;
    private javax.swing.JTextPane displayArea;
    private javax.swing.JTextField enterField;
    private javax.swing.JTextField fieldName;
    private javax.swing.JComboBox gameMode;
    private javax.swing.JLabel jLabelInformation;
    private javax.swing.JLabel jLabelInformationGames;
    private javax.swing.JLabel jLabelInformationGames1;
    private javax.swing.JLabel jLabelInput;
    private javax.swing.JLabel jLabelName;
    private javax.swing.JList jListGames;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel mainTitle;
    private javax.swing.JPanel main_Panel;
    private javax.swing.JMenuItem menuItem_Connect;
    private javax.swing.JMenuItem menuItem_Exit;
    private javax.swing.JMenuItem menuItem_Instructions;
    private javax.swing.JMenuItem menuItem_NewGame;
    private javax.swing.JMenuItem menuItem_NewGameOnline;
    private javax.swing.JMenuItem menuItem_disconnect;
    private javax.swing.JMenu menu_File;
    private javax.swing.JMenu menu_Game;
    // End of variables declaration//GEN-END:variables
}
