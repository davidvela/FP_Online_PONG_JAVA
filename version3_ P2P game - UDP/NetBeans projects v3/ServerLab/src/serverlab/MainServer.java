
package serverlab;
import server.communication.Information;
import server.communication.Information.*;
import serverlab.Pong_Server;


import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Formatter;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class MainServer extends JFrame 
{
   private JTextArea displayArea; // display information to user
   private ExecutorService executor; // Multithreading 

   
   private DatagramSocket socket; // socket to connect to client
   private int serverPort_udp = 5003;
   private Information information_udp; 
   private boolean broadcast_pck = false;

   private int serverPort_udp2 = 0;
   Pong_Server app;

  
   
   public MainServer()
   {
      super( "Server" );
      displayArea = new JTextArea();  
      add( new JScrollPane( displayArea ), BorderLayout.CENTER );
      setSize( 300, 300 );  
      setVisible( true );

      information_udp = new Information();
      createGameDatagramSocket();
      //createStreamSocket();

      executor = Executors.newFixedThreadPool(1);
      executor.execute(  new Runnable() //listening UDP request
       {   public void run()  
       {     waitForPackets();    }  }     );

   } 

   public void createGameDatagramSocket ()
   {
        // create DatagramSocket for sending and receiving packets
        try 
        {
             socket = new DatagramSocket( serverPort_udp );
        } 
        catch ( SocketException socketException ) 
        {
            socketException.printStackTrace();
            System.exit( 1 );
        } 
   
  }
   
   public DatagramSocket createGameDatagramSocket (int num_players)
   {
        try // set up server to receive connections; process connections
        {
            //ServerSocket ( port , backlog)
            DatagramSocket server = new DatagramSocket( serverPort_udp2); 
            return server;             
        }   
        catch ( IOException ioException ) 
        {
            ioException.printStackTrace();
            return null;
        }          
        
   }
   
   // wait for packets to arrive, display data and echo packet to client
   public void waitForPackets()
   {
      while ( true ) 
      {
         try // receive packet, display contents, return copy to client
         {
            byte[] data = new byte[ 100 ]; // set up packet
            DatagramPacket receivePacket = 
               new DatagramPacket( data, data.length );

            socket.receive( receivePacket ); // wait to receive packet

            // display information from received packet 
            displayMessage( "\nPacket received:" + 
               "\nFrom host: " + receivePacket.getAddress() +   //***********
               "\nHost port: " + receivePacket.getPort() +      //**********
               "\nLength: " + receivePacket.getLength() );
              // + "\nContaining:\n\t" + new String( receivePacket.getData(), 
              // 0, receivePacket.getLength() ) );
                 
            
             String buffer = new String( receivePacket.getData(), 
                  0, receivePacket.getLength() );
           // displayMessage(buffer);
            Type_pck type_pck = information_udp.DeserializationPck(buffer);
            Type_pck type_pck_answer;
            boolean respond = true;
            broadcast_pck =false;
            switch (type_pck)
            {
                case HELLO:
                    type_pck_answer = information_udp.newUser(receivePacket);
                    information_udp.SerializationPck(type_pck_answer);
                    break;
                    
                case UPDATE:
                   information_udp.SerializationPck(type_pck);
                    break;

                case EXIT:
                    int identity = information_udp.SuspendUser(receivePacket);
                    displayMessage("\n user:" + identity + "is off");
                    respond = false;
                    break;   

                case CHAT:
                    displayMessage( "\nChatShow:" 
                            + information_udp.chat_pck.getName() 
                            + information_udp.chat_pck.getMessage() );
                    //send the same information which is received
                    type_pck_answer=Type_pck.CHAT;
                    information_udp.SerializationPck(type_pck_answer);
                    broadcast_pck = true;
                    break;

                case NEW_GAME:
                    // create of socket for the game
                    DatagramSocket server = createGameDatagramSocket
                            (information_udp.game_pck.getNumberPlayers());
                    // save the port and create a new game
                    int port =  server.getLocalPort();
                    int index = information_udp.newGameCreated(port);
                    // send the socket and number of players
                    information_udp.ack_pck.setInReplyTo(Type_pck.NEW_GAME);
                    information_udp.ack_pck.setInfo(Integer.toString(port)); 
                    information_udp.SerializationPck(Type_pck.ACK);
                    //Pong_Server 
                    app = new Pong_Server(server,information_udp,index);
                            //information_udp.game_pck.getNumberPlayers());
                    app.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                  /*  executor.execute(new Runnable()
                    {   public void run()  
                    {  app.execute();    }  }    );*/
                    break;


                case JOIN_GAME: 
                    type_pck_answer = information_udp.join_to_Game();
                    //answer ack or nack
                    information_udp.SerializationPck(type_pck_answer);
                    //Update the list of games
                    break;

                default: displayMessage("Error");

            }

            if(respond)
            {
                // send packet to client
                data = information_udp.buffer_out.getBytes();  
                DatagramPacket sendPacket = 
                    new DatagramPacket( data, data.length, 
                    receivePacket.getAddress(), receivePacket.getPort() );
                
                if(broadcast_pck) broadcasting(sendPacket);
                else  
                {
                    sendPacketToClient( sendPacket );
                    if(type_pck == Type_pck.NEW_GAME)   updateListGames();
                    if(type_pck == Type_pck.JOIN_GAME)  updateListGames();
                }
           }
        } 
        catch ( IOException ioException )
        {
            displayMessage( ioException + "\n" );
            ioException.printStackTrace();
        } 
    } 
 } 

   public void broadcasting(DatagramPacket sendPacket)
           throws IOException
   {
      for(int i=0; i < information_udp.list_users.length; i++)
      {
          if(information_udp.list_users[i] != null)
          if(information_udp.list_users[i].getStatus()== Status.ON)
          {
            sendPacket.setAddress(information_udp.list_users[i].getIp_user());
            sendPacket.setPort(information_udp.list_users[i].getPort_connect());  

            displayMessage( "\nBroadcast:  " );            
            sendPacketToClient( sendPacket );
          }
      }
   }

   private void sendPacketToClient( DatagramPacket sendPacket ) 
      throws IOException
   {
      socket.send( sendPacket );
      displayMessage( "\nPacket sent\n" );
                  displayMessage( "\nPacket sent:" + 
               "\nFrom host: " + sendPacket.getAddress() +   //***********
               "\nHost port: " + sendPacket.getPort() +      //**********
               "\nLength: " + sendPacket.getLength() //);
               + "\nContaining:\n\t" + new String( sendPacket.getData(), 
               0, sendPacket.getLength() ));
   } 
   
   
   private void updateListGames()
   {
       //String message = new String(Type_pck.UPDATE.toString() + "\n" + 
         //      information_udp.current_Games());
       information_udp.SerializationPck(Type_pck.UPDATE);
       
       byte[] data_up = information_udp.buffer_out.getBytes();  
       DatagramPacket updatePacket = 
       new DatagramPacket( data_up, data_up.length );
       
       try
       {
           broadcasting(updatePacket);
       }
       catch ( IOException ioException )
       {    
           displayMessage( ioException + "\n" );
            ioException.printStackTrace();
       } 
       
   }  
   
   // manipulates displayArea in the event-dispatch thread
   private void displayMessage( final String messageToDisplay )
   {
      SwingUtilities.invokeLater(
         new Runnable() 
         {
            public void run() 
            {
               displayArea.append( messageToDisplay ); 
            } 
         }  
      );  
   }  

   
}