package serverlab;

import Game.Ball;
import Game.Pallet;
import server.communication.Information;
import server.communication.Information_game;
import server.communication.Information.*;
import server.communication.Information_game.Type_pckg;

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

    private JTextArea outputArea; // for outputting moves

    private Information_game inf_g;
    private Information inf;
    private Player[] players; // array of Players
    private Formatter [] output; // output to client
    

    private ServerSocket server; // server socket to connect with clients
    private java.net.SocketPermission test;
    private int serverPort_tcp = 0;
    private int num_players = 2;
    private int current_players = 0;
    private int index;
    private int duration;
    private int desconnection = 0;
    
        private final int GAME_WIDTH = 640;
        private final int GAME_HEIGHT = 480;
        private final int PADDLE_WIDTH = 20;
        private final int PADDLE_HEIGHT = 100;        
        private final static int PLAYER_1 = 0; 
        private final static int PLAYER_2 = 1;
	private Ball ball;

        private Pallet [] pallets;
        private int [] score;
        private java.util.Timer timer;
        int speed_right =  5;
        int speed_left  = -5;
        private java.util.Timer timer_game;
        int minutes = 0;
        int seconds = 0;
        int fqBall;


        
        private ExecutorService runGame;     
        private Lock gameLock; // to lock game for synchronization
        private Condition otherPlayerConnected; // to wait for other player


    public Pong_Server(ServerSocket sock, Information info, int index_game, int fq) 
    {
        super("Pong Server");
        inf = info;
        index = index_game;
        fqBall = fq;
        num_players = inf.list_games[index_game].numberPlayers;
        duration = inf.list_games[index_game].duration;
        
        // create ExecutorService with a thread for each player
        runGame = Executors.newFixedThreadPool(num_players);
        players = new Player[num_players];
        inf_g = new Information_game();
        
        gameLock = new ReentrantLock(); // create lock for game
        otherPlayerConnected = gameLock.newCondition();       
        server = sock; 
        output = new Formatter[num_players];
        serverPort_tcp = server.getLocalPort();
               
        outputArea = new JTextArea(); // create JTextArea for output
        add( new JScrollPane( outputArea ), BorderLayout.CENTER );
        outputArea.setText("Server awaiting connections\n");
        initialitionOfElements(); 
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

    public void execute() 
    {
        // wait for each client to connect
        for (int i = 0; i < players.length; i++) 
        {
            try // wait for connection, create Player, start runnable
            {
                server.setSoTimeout(25000); //25s
                players[i] = new Player(server.accept(),i);
                runGame.execute(players[i]); // execute player runnable
            } 
            catch (IOException ioException) 
            {
                ioException.printStackTrace();
                Game_Over();
                //System.exit(1);
            }
        }
        
        gameLock.lock(); // lock game to signal player X's thread

        try 
        {
            inf_g.status_game = Information_game.Status.ON;
            players[PLAYER_1].setSuspended(false); // resume player X
            otherPlayerConnected.signal(); // wake up player X's thread
        } 
        finally 
        {
            gameLock.unlock(); // unlock game after signalling player X
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
   
    private class Player implements Runnable 
    {
        private Socket      connection; // connection to client
        private Scanner     input; // input from client
        private int i;
        //private Formatter   output; // output to client
        
        private int playerNumber; 
        private boolean suspended = true; // whether thread is suspended

        public Player(Socket socket, int number) 
        {
            playerNumber = number;
            connection = socket; 
            i=0;    //counter
            try 
            {
                input = new Scanner(connection.getInputStream());
                output[playerNumber] = new Formatter
                        (connection.getOutputStream());
            } 
            catch (IOException ioException) 
            {
                ioException.printStackTrace();
                System.exit(1);
            }
        } 

        // control thread's execution
        public void run() 
        {   //*******conect players************************************//
            // send client its position and control the game
            try 
            {
                displayMessage("Player " + playerNumber + " connected\n");
                output[playerNumber].format("%d\n", playerNumber); 
                output[playerNumber].flush(); 
                // if player 1, wait for another player to arrive
                if (playerNumber == PLAYER_1) 
                {
                    displayMessage( "\nWaiting for another player\n"); 
                    displayMessage( "\nif in 30 seconds other player is not join, "
                            + "the game will close\n");                                   
                    gameLock.lock(); 
                    try 
                    {   while (inf_g.status_game == Information_game.Status.WAITING) 
                        {   
                            otherPlayerConnected.await(1, TimeUnit.SECONDS);
                            displayMessage(Integer.toString(i));
                            if(i == 30) displayMessage("30seconds");
                            else i++;
                        }
                   }  catch (InterruptedException exception) 
                        {   exception.printStackTrace();     } 
                        finally 
                        {   gameLock.unlock();               }
                 inf_g.duration = duration;                   
                 inf_g.SerializationGame(Type_pckg.START, output[playerNumber]);
                 output[playerNumber].flush();                 
                } 
                else 
                {
                    //Other players... 
                    if( (playerNumber == PLAYER_2)&&((playerNumber+1) == num_players))
                    {
                        inf_g.SerializationGame(Type_pckg.START, output[playerNumber]);
                        output[playerNumber].flush(); 
                        StartGame();
                    }
                    
                } 

                //******************** START GAME *********************//
                
                
                Information_game.Type_pckg  type_pck;
                while (!isGameOver()) 
                {                           
                    if ( input.hasNextLine() )
                    {
                        Information_game.Type_pckg type_gamepck = //input.next();
                                inf_g.DeserializationGame(input); 
                        //displayMessage("1" + input.next());
                        //displayMessage(Integer.toString(input.nextInt()));
                       try{ 
                        switch (type_gamepck)
                        {
                                case MOV:
                                    pallets[playerNumber].setPositionX
                                            (inf_g.mov_pck.getPositionX());
                                    pallets[playerNumber].setPositionY
                                            (inf_g.mov_pck.getPositionY());
                                    inf_g.mov_pck.numberPl = playerNumber;
                                    broadcasting_Except(playerNumber, 
                                            Type_pckg.MOV_O);
                                    break; 

                                case EXIT: 
                                    inf_g.control_pck.info = "the other player"
                                            + "has left the game. You win";
                                    broadcasting_Except(playerNumber, 
                                            Type_pckg.CONTROL);
                                    output[playerNumber].close();  
                                    input.close();  
                                    connection.close();
                                    Game_Over();
                                    break;
                                   
                                default: displayMessage(input.nextLine()); 
                                break;
                        }
                    }catch( NullPointerException e){e.printStackTrace();}
                       catch( IOException e){e.printStackTrace();}
                    }
                } 
            } 
            
            finally 
            {
                try 
                {
                    connection.close(); 
                } 
                catch (IOException ioException) {
                    ioException.printStackTrace();
                    System.exit(1);
                } 
            } 
        } 
               
        public void setSuspended(boolean status) 
        {
            suspended = status; // set value of suspended
        } 
        
    } 
    
    public void StartGame()
    {
        //update of the ball
      displayMessage("\nGame started");

      timer = new java.util.Timer();
      TimerTask timertask = new TimerTask()
        {  public void run(){ timer();} };
      timer.scheduleAtFixedRate(timertask, 0, fqBall); 
      
      minutes = duration;
      timer_game = new java.util.Timer();
      TimerTask timertask_game = new TimerTask()
        {  public void run(){ timer_game();} };
      timer_game.scheduleAtFixedRate(timertask_game, 0, 1000); 
      
      //control of the time.
    }
    
    private void timer()
    {
        int max_speed_right = 20;
        int max_speed_left  = -20;
        int initial_speed_right = 5;
        int initial_speed_left  = -5;
        
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
                broadcasting_allP(Type_pckg.GOAL);

        }

        if((this.ball.getX()+ball.getDiameter()) >= GAME_WIDTH)  //Goal Player 1
        {
                this.ball.setX((GAME_WIDTH-ball.getDiameter())/2);
                this.ball.setY((GAME_HEIGHT-ball.getDiameter())/2);
                speed_left = -5;

                score[0] = score[0] + 1;
                inf_g.goal_pck.player = 0;
                broadcasting_allP(Type_pckg.GOAL);

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
        broadcasting_allP(Type_pckg.BALL);
        //this.game.actualizar();
        
    }
    
    private void timer_game()
    {
        //implement a clock
        if(seconds == 0)
        {
            if(minutes == 0)
            {
                //end the game
                broadcasting_allP(Type_pckg.EXIT);      
                if(score[0] > score[1])
                inf_g.control_pck.info = "The player 0 won";
                else 
                inf_g.control_pck.info = "The player 1 won";
                
                broadcasting_allP(Type_pckg.CONTROL);
                
                Game_Over();
            }
            else 
            {
                seconds = 60;
                minutes --;
            }   
        }
        else seconds --;
        
        //inf_g.timer_pck.setMin(minutes);
        //inf_g.timer_pck.setSec(seconds);
        //broadcasting_allP(Type_pckg.TIMER);
        
    }
    
    private void broadcasting_allP(Type_pckg type)
    {
        displayMessage("\nBroadband");
        for(int i=0; i < num_players; i++)
        {
            inf_g.SerializationGame(type, output[i]);
            //displayMessage(output[i].toString());
            output[i].flush();
        }
    }
    
    private void broadcasting_Except(int index,Type_pckg type)
    {
        for(int i=0; i < num_players; i++)
        {
            if(index == i) continue;
            inf_g.SerializationGame(type, output[i]);
            displayMessage("Broadcast position other player");
            output[i].flush();
        }
    
    }
    
    public boolean isGameOver()
    {
        return false; 
    } 
    private void Game_Over()
    {  
        gameLock.lock();
        if(desconnection == 0)
        {
            desconnection = 1;
        if(inf_g.status_game != Information_game.Status.WAITING)
        {
           timer.cancel();
           timer_game.cancel();
        }
        inf.list_games[index].Desconnection();
        setVisible(false);
        //broadcasting_Except(playerNumber,Type_pckg.CONTROL);
         try 
         { 

             server.close(); //connection.close(); 
         } 
            catch (IOException ioException) 
            {   ioException.printStackTrace(); 
                System.exit(1); 
            }
        runGame.shutdown();
        this.dispose();
        }
    
    }

}
