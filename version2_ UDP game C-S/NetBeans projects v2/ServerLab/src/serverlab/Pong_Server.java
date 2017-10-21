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
    
        private final int GAME_WIDTH = 640;
        private final int GAME_HEIGHT = 480;
        private final int PADDLE_WIDTH = 20;
        private final int PADDLE_HEIGHT = 100;        
        private final static int PLAYER_1 = 0; 
        private final static int PLAYER_2 = 1;
	private Ball ball;
        private int counter =0;
        private Pallet [] pallets;
         public Player_udp [] list_players;
        private int [] score;
        private java.util.Timer timer;
        private java.util.Timer timer_game;
        private java.util.Timer timer_init;
        int speed_right =  5;
        int speed_left  = -5;
        int minutes = 0;
        int seconds = 0;
        private int fqBall;
        private ExecutorService runGame;     


    public Pong_Server(DatagramSocket sock, Information info, int index_game, int fq) 
    {
        super("Pong Server");
        inf = info;
        index = index_game;
        fqBall = fq;
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
        initialitionOfElements(); 
        list_players  = new Player_udp [num_players];
        inf_g.status_game = InformationGame.Status.WAITING;
        isclose = false;
        runGame.execute(new Runnable()
                    {   public void run()  
                    {  execute();    }  }    );
        
           
        setSize(300, 300);
        setVisible(true);
    } 
    

    private void initialitionOfElements()
    {
        score = new int [num_players];
        for(int i=0; i < num_players; i++)
        {
            score[i]=0;
        }

        ball = new Ball();
        pallets = new Pallet [num_players];
        
        if(num_players == 2)
        {
            pallets[0] = new Pallet(PADDLE_WIDTH,(GAME_HEIGHT-PADDLE_HEIGHT)/2,
                PADDLE_WIDTH, PADDLE_HEIGHT);     
            pallets[1] = new Pallet(GAME_WIDTH-50,(GAME_HEIGHT-PADDLE_HEIGHT)/2,
                PADDLE_WIDTH, PADDLE_HEIGHT);
        }


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
                    inf_g.status_game = InformationGame.Status.ON;
                    inf_g.SerializationGame(Type_pckg.START);
                    broadcasting_allP();
                    startGame();
                }
                
            }
   //*******************START GAME ****************************************//         
            else if(inf_g.status_game == InformationGame.Status.ON)
            {
                            boolean respond = true;
                            boolean broadcast_pck =false;
                       int playerNumber = -1;
                       switch (type_gamepck)
                        {
                                case MOV:
                                    playerNumber = inf_g.mov_pck.numberPl;
                                    pallets[playerNumber].setPositionX
                                            (inf_g.mov_pck.getPositionX());
                                    pallets[playerNumber].setPositionY
                                            (inf_g.mov_pck.getPositionY());
                                    //inf_g.mov_pck.numberPl = playerNumber;
                                    inf_g.SerializationGame(type_gamepck.MOV_O);
                                    
                                    broadcasting_Except(playerNumber);
                                    break; 

                                case EXIT: 
                                    inf_g.control_pck.info = "the other player"
                                            + "has left the game. You win";
                                    inf_g.SerializationGame(Type_pckg.CONTROL);
                                    broadcasting_Except(inf_g.temp);
                                    inf_g.status_game = InformationGame.Status.OFF;
                                    Game_Over();
                                    break;
                                   
                                default: break;
                        }                                  
            }
           /*if (inf_g.status_game == InformationGame.Status.OFF)
            {
                server.disconnect();
                //server.close();
                //Game_Over();
                
            }*/
            
            
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
        timer_init.cancel();
        displayMessage("\nGame started\n");

      timer = new java.util.Timer();
      TimerTask timertask = new TimerTask()
        {  public void run(){ timer();} };
      timer.scheduleAtFixedRate(timertask, 0, fqBall); 
      
      minutes = duration;
      timer_game = new java.util.Timer();
      TimerTask timertask_game = new TimerTask()
        {  public void run(){ timer_game();} };
      timer_game.scheduleAtFixedRate(timertask_game, 0, 1000);       
    }
    
    private void timer()
    {
        int max_speed_right = 20;
        int max_speed_left  = -20;
        int initial_speed_right = 5;
        int initial_speed_left  = -5;

        try
        {
            inf_g.ball_pck.setHit(0);
        if((this.ball.getY())<=0)
        {
                this.ball.setDesy(speed_right);
                if(speed_right <= max_speed_right)
                {
                        speed_right++;
                }
        }

        if((this.ball.getY()+50)>= GAME_HEIGHT)
        {
                this.ball.setDesy(speed_left);
                if(speed_left >= max_speed_left)
                {
                        speed_left--;
                }
        }

        if((this.ball.getX())<=0)   // Goal of Plater 2 
        {
                this.ball.setX((GAME_WIDTH-ball.getDiameter())/2);
                this.ball.setY((GAME_HEIGHT-ball.getDiameter())/2);
                speed_right = 5;

                score[1] += 1;
                inf_g.goal_pck.player = 1;
                inf_g.SerializationGame(Type_pckg.GOAL);
                broadcasting_allP();

        }

        if((this.ball.getX()+ball.getDiameter()) >= GAME_WIDTH)  //Goal Player 1
        {
                this.ball.setX((GAME_WIDTH-ball.getDiameter())/2);
                this.ball.setY((GAME_HEIGHT-ball.getDiameter())/2);
                speed_left = -5;

                score[0] = score[0] + 1;
                inf_g.goal_pck.player = 0;
                inf_g.SerializationGame(Type_pckg.GOAL);
                broadcasting_allP();

        }

        for(int i=0; i<PADDLE_HEIGHT; i++)
        {
                if((this.ball.getY()+ pallets[1].getDimX()) == 
                        (this.pallets[0].getPositionY()+i)
                            &&(this.ball.getX()) <= 
                        (this.pallets[0].getPositionX()+ pallets[0].getDimX()))
                {
                        inf_g.ball_pck.setHit(1);
                        this.ball.setDesx(speed_right);
                        if(speed_right <= max_speed_right)
                        {
                                speed_right++;
                        }
                }
                if((this.ball.getY()+pallets[1].getDimX())==
                        (this.pallets[1].getPositionY()+i)
                            &&(this.ball.getX())>=
                        (this.pallets[1].getPositionX()-ball.getDiameter()))
                {
                        inf_g.ball_pck.setHit(1);
                        this.ball.setDesx(speed_left);
                        if(speed_left >= max_speed_left)
                        {
                                speed_left--;
                        }
                }
        }
        this.ball.setY(this.ball.getY()+this.ball.getDesy());
        this.ball.setX(this.ball.getX()+this.ball.getDesx());
        inf_g.ball_pck.setPositionX(ball.getX());
        inf_g.ball_pck.setPositionY(ball.getY());
        if(inf_g.SerializationGame(Type_pckg.BALL) != null )
        broadcasting_allP();
        }
        catch (IOException i){ System.exit(0);}
        
    }
    
    private void timer_game()
    {
        //implement a clock
        if(seconds == 0)
        {
            if(minutes == 0)
            {
                try
                {
                //end the game
                inf_g.SerializationGame(Type_pckg.EXIT);
                broadcasting_allP();      
                
                if(score[0] > score[1])
                inf_g.control_pck.info = "The player 0 won";
                
                else 
                {
                 if(score[1]>score[0])
                     inf_g.control_pck.info = "The player 1 won";
                 else inf_g.control_pck.info = "Score tied. Congratulations to both";
                }
                
                inf_g.SerializationGame(Type_pckg.CONTROL);
                broadcasting_allP();
                }
                catch ( IOException i){ System.exit(0);}
                
                Game_Over();
            }
            else 
            {
                seconds = 60;
                minutes --;
            }   
        }
        else seconds --;        
    }
    
    private void broadcasting_allP()throws IOException
    {
        byte[] data = inf_g.buffer_out.getBytes();            
      
        for(int i=0; i < num_players; i++)
        {
            DatagramPacket sendPacket = 
                    new DatagramPacket( data, data.length, 
                    list_players[i].ip_user, 
                    list_players[i].port_connect );
            displayMessage("Broadcast \n");
            server.send( sendPacket );
        }
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
            server.send( sendPacket );
        }
    
    }
    
    public boolean isGameOver()
    {
        return false; 
    } 
    
    private void Game_Over()
    {  
        if(isclose == false)
        {
            isclose = true;
            if(timer != null)
            {
                timer.cancel();
                timer_game.cancel();
            }
        
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
