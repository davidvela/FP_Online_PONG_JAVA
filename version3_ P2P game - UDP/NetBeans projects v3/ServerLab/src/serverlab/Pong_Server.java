package serverlab;

import Game.Ball;
import Game.Pallet;
import server.communication.Information;
import server.communication.InformationGame;
import server.communication.InformationGame.Type_pckg;

import java.awt.BorderLayout;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.IOException;
import java.lang.*;
import java.net.*;
import java.util.Formatter;
import java.util.Scanner;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.Condition;
import javax.swing.*;

public class Pong_Server extends JFrame 
{

    private JTextArea outputArea; 
    private InformationGame inf_g;
    private Information inf;   
    public int duration;

    private DatagramSocket server; 
    private java.net.SocketPermission test;
    private int serverPort_udp = 0;
    private int num_players = 2;
    private int current_players;
    private int index;
    private boolean isclose;
    
      
        private final static int PLAYER_1 = 0; 
        private final static int PLAYER_2 = 1;
        private int counter =0;
        private Pallet [] pallets;
         public Player_udp [] list_players;
        private java.util.Timer timer_init;
        private ExecutorService runGame;     


    public Pong_Server(DatagramSocket sock, Information info, int index_game) 
    {
        super("Pong Server");
        inf = info;
        index = index_game;
        current_players = 0;
        num_players = inf.list_games[index_game].numberPlayers;
        duration = inf.list_games[index_game].duration;  
       // create ExecutorService with a thread for each player
        runGame = Executors.newFixedThreadPool(1);

        inf_g = new InformationGame(num_players);
        server = sock; 
        serverPort_udp = server.getLocalPort();
        inf_g.duration = duration;
        outputArea = new JTextArea(); // create JTextArea for output
        add( new JScrollPane( outputArea ), BorderLayout.CENTER );
        outputArea.setText("Server awaiting connections\n");
        displayMessage( "\nif in 30 seconds other player is not join the game will close\n"); 
        list_players  = new Player_udp [num_players];
        inf_g.status_game = InformationGame.Status.WAITING;
        isclose = false;
        runGame.execute(new Runnable()
                    {   public void run()  
                    {  execute();    }  }    );
        
           
        setSize(300, 300);
        setVisible(true);
    } 
    


    private void displayMessage(final String messageToDisplay) 
    {
        SwingUtilities.invokeLater(
                new Runnable() {
                    public void run() {
                        outputArea.append(messageToDisplay); // add message
                    }   }  );
    }
    
     private void timer_init()
    {       
        displayMessage( String.valueOf(counter) + " ");
        if (counter == 30) 
        {
             timer_init.cancel();
             displayMessage("Game over");
             inf_g.status_game = InformationGame.Status.OFF;
             Game_Over();
        }  
        else counter ++;
              
    }
   
    public void execute()
    {  
        timer_init = new java.util.Timer();
        TimerTask timertask_init = new TimerTask()
        {  public void run(){ timer_init();} };
        timer_init.scheduleAtFixedRate(timertask_init, 0, 1000);
        
      while ( true ) 
      {
         try // receive packet, display contents, return copy to client
         {
            byte[] data = new byte[ 100 ]; // set up packet
            DatagramPacket receivePacket = 
               new DatagramPacket( data, data.length );

            server.receive( receivePacket ); // wait to receive packet

            // display information from received packet 
            displayMessage( "\nPacket received:" + 
               "\nFrom host: " + receivePacket.getAddress() +   //***********
               "\nHost port: " + receivePacket.getPort() +      //**********
               "\nLength: " + receivePacket.getLength() );

                 
             String buffer = new String( receivePacket.getData(), 
                  0, receivePacket.getLength() );
            InformationGame.Type_pckg type_gamepck = inf_g.DeserializationGame(buffer); 
            
     //********************* WAITING PLAYERS *******************************//      
            if(inf_g.status_game == InformationGame.Status.WAITING)
            {
                switch (type_gamepck)
                {
                    case HELLO: 
                        inf_g.newUser(receivePacket);
                        list_players[current_players] = new Player_udp(
                            receivePacket.getAddress(),  current_players,receivePacket.getPort());
                        current_players++;
                        
                        inf_g.SerializationGame(type_gamepck.CONTROL);
                        data = inf_g.buffer_out.getBytes();  
                        displayMessage("Player " + inf_g.current_players + " connected\n");
                        displayMessage("Current players: " + current_players + "\n");
                        DatagramPacket sendPacket1 = new DatagramPacket( data, data.length, 
                            receivePacket.getAddress(), receivePacket.getPort() );
                        server.send( sendPacket1 );
                        break;
                    case EXIT:
                        inf_g.status_game = InformationGame.Status.OFF;
                        break;

                            
                } 
                if(current_players == num_players)
                {
                    //inf_g.status_game = InformationGame.Status.ON;
                    //player1
                    inf_g.ipTemp = list_players[0].ip_user.toString();
                    inf_g.portTemp =  list_players[0].port_connect;   
                    inf_g.SerializationGame(Type_pckg.START);
                    broadcasting_Except(0);
                    //player0
                    inf_g.ipTemp = list_players[1].ip_user.toString();
                    inf_g.portTemp =  list_players[1].port_connect; 
                    inf_g.SerializationGame(Type_pckg.START);
                    broadcasting_Except(1);
                    startGame();
                }
                
            }
            
   //*******************START GAME ****************************************//         
            else if(inf_g.status_game == InformationGame.Status.ON)
            {                           
                       switch (type_gamepck)
                        {
                           case EXIT: 
                                    inf_g.control_pck.info = "the other player"
                                            + "has left the game. You win";                             
                                default: break;
                        }                                  
            }

            
        } 
        catch ( IOException ioException )
        {
            displayMessage( ioException + "\n" );
            ioException.printStackTrace();
            
        }
        catch( NullPointerException e){e.printStackTrace();}

    } 
 } 
     
    
    public void startGame()
    {
        //update of the ball
        //timer_init.cancel();
        displayMessage("\nGame started\n");
    
    }
    
 
    
    
    private void broadcasting_Except(int index)throws IOException
    {
        byte[] data = inf_g.buffer_out.getBytes();            
      
        for(int i=0; i < num_players; i++)
        {
            if(index == i) continue;
            DatagramPacket sendPacket = 
                    new DatagramPacket( data, data.length, 
                    list_players[i].ip_user, 
                    list_players[i].port_connect );
            displayMessage("Broadcast position other player\n");
             displayMessage( "\nPacket received:" + 
               "\nFrom host: " + sendPacket.getAddress() +   //***********
               "\nHost port: " + sendPacket.getPort() +      //**********
               "\nLength: " + sendPacket.getLength() //);
               + "\nContaining:\n\t" + new String( sendPacket.getData(), 
               0, sendPacket.getLength() ) );
            
            server.send( sendPacket );
        }
    
    }
    
    private void Game_Over()
    {  
        if(isclose == false)
        {
            isclose = true;

            inf.list_games[index].Desconnection();
            setVisible(false);
            //broadcasting_Except(playerNumber,Type_pckg.CONTROL)
            //server.close();
            inf_g = null;
            list_players[0]= null;list_players[1]= null; list_players= null;
            runGame.shutdown(); 
           
            this.dispose();
        }
    }
    
    public class Player_udp 
    {            
            private java.net.InetAddress ip_user;
            private int port_connect;
            private int ID;


            public Player_udp( InetAddress ip, int ID,   int port) 
            {
                this.ip_user = ip;
                this.port_connect = port;
                this.ID = ID;
            }

        } 
}
