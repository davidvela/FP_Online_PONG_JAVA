package Game;

import Game.Ball;
import Game.Pallet;
import Communication.*;
import Communication.Information_game.Type_pckg;

import java.applet.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.JFrame;
import java.lang.*;
import java.net.*;
import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.swing.*;
import sun.awt.HorizBagLayout;
import sun.applet.AppletAudioClip;

public class GameOnline extends javax.swing.JFrame 
{

        private final int GAME_WIDTH = 640;
        private final int GAME_HEIGHT = 480;
        private final int PADDLE_WIDTH = 20;
        private final int PADDLE_HEIGHT = 100; 
        
        private GameOn game;
	private Ball ball;
        private Pallet[] pallets; 
        private int [] score;
 
        private java.util.Timer timer_game;
        private java.util.Timer timer;
        int speed_right =  5;
        int speed_left  = -5;
        int tempus = 0;
        int minutes = 0;
        int seconds = 0;
        private  DecimalFormat df;
        private JLabel time_l;
        private JLabel inf_l;
	private JLabel [] lScores;
        
        private DatagramSocket socket;
        private String hostServer; 
        private String hostOtherClient; 
        private InetAddress server_address;
        private InetAddress otherClient_address;
        private int serverPort_udp;
        private int otherClientPort;
        private Information_game inf_g;
        private ExecutorService worker;
        private int playerNumber;
        private int num_players;
        private AudioClip soundBg, sHit, sGoal;
        private int fqBall;
   
        public  GameOnline (int port,int num, String host, int mode, int fq)
        {
            super("Game");
            serverPort_udp = port; 
            fqBall = fq;
            num_players = num;
            hostServer = host;
            inf_g = new Information_game();
            JPanel panelGame = new JPanel();
            //top left corner (x,y); dimmension x, y 
            setBounds(0,0,GAME_WIDTH,GAME_HEIGHT); 
            initialitionOfElements();
            game = new GameOn(ball,pallets[0],pallets[1], mode);
            worker = Executors.newFixedThreadPool(1);
            playerNumber = -1;
            
            panelGame.add(game);
            panelGame.requestFocus();
            add(panelGame);
            inf_l = new JLabel("Information: ");
            add(inf_l, BorderLayout.PAGE_END);
            addWindowListener(new EventHandler());
            switch (mode)
                    {
                        case 0:     break;
                        case 1:     
                                    sHit = Applet.newAudioClip(getClass().getResource
                                            ("/Resources/bhit_sport.wav"));   
                                    sGoal = Applet.newAudioClip(getClass().getResource
                                            ("/Resources/bGoal.wav"));   
                                    soundBg = Applet.newAudioClip(getClass().getResource
                                            ("/Resources/bk_sport.wav"));   
                                    soundBg.loop();
                                    break;
                        case 2:
                             sHit = Applet.newAudioClip(getClass().getResource
                                            ("/Resources/w_hit.wav"));   
                             sGoal = Applet.newAudioClip(getClass().getResource
                                            ("/Resources/w_score.wav"));   
                             soundBg = Applet.newAudioClip(getClass().getResource
                                            ("/Resources/w_ocean.wav"));   
                             soundBg.loop();
                            
                    }
            
            
            
            
            setSize( 700, 700 ); 
            setResizable(false);
            setVisible( true );                
            startClient(); 
         }
        
        public void startClient()
        {
            try //www.cafealuait.org/books/jnp3/examples/09/WhoisClient.java
               {
                    server_address = InetAddress.getByName(hostServer);
               }
             catch (UnknownHostException ex)
               {    System.exit(1);   }
            try // create DatagramSocket for sending and receiving packets
                {
                    socket = new DatagramSocket();
                    worker.execute(  new Runnable()
                    {   public void run()  
                        {  waitForPackets();    }  }     ); 
                    
                    sendData(Type_pckg.HELLO); 
                } 
            catch ( SocketException socketException ) 
            {                
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
                String data_containing = new String( receivePacket.getData(), 
                    0, receivePacket.getLength());
            
            Type_pckg type_pck = null;
            try
            {
                type_pck = inf_g.DeserializationGame(data_containing);
            }
            catch(java.lang.NullPointerException ex)
            {
                inf_l.setText("time mistake");
                type_pck = inf_g.DeserializationGame(data_containing);
            }
            catch( NoSuchElementException i) { type_pck = null;}

            switch (type_pck)
            {                      
                   case BALL:
                        game.ball.setX(inf_g.ball_pck.getPositionX());
                        game.ball.setY(inf_g.ball_pck.getPositionY());
                        game.update();
                        if( (inf_g.ball_pck.getHit() == 1) && (sHit != null) )
                            sHit.play();
                        break;
                       
                   case MOV:
                       pallets[inf_g.mov_pck.numberPl].setPositionY(
                               inf_g.mov_pck.getPositionY());                     
                        break; 
                        
                    case GOAL:
                        int i = inf_g.goal_pck.player;
                        score[i]++;
                        if (sGoal != null) sGoal.play();
                        lScores[i].setText("Player" + i + " : "+ score[i]);
                        break; 
                   
                   case START:
                        addKeyListener(new KeyAdapter()
                             { public void keyPressed(KeyEvent evt) 
                             {  formKeyPressed(evt);}});
                        minutes = inf_g.duration;
                        otherClientPort = inf_g.portTemp;
                        hostOtherClient = inf_g.ipTemp.substring(1);
                        otherClient_address = InetAddress.getByName(hostOtherClient);
                                                
                        timer_game = new java.util.Timer();
                        TimerTask timertask_game = new TimerTask()
                            {  public void run(){ timer_game();} };
                        timer_game.scheduleAtFixedRate(timertask_game, 0, 1000);
                        inf_g.st = Information_game.Status.ON;
                       
                        if(playerNumber == 0)
                        {
                            //manage the ball
                                  timer = new java.util.Timer();
                                  TimerTask timertask = new TimerTask()
                                  {  public void run(){ timer();} };
                                  timer.scheduleAtFixedRate(timertask, 0, fqBall);   
                        }
                        
                        
                        break; 
                        
                   case EXIT: 
                       if(playerNumber == 0) 
                       { timer.cancel();     }
                       playerNumber = -2; //lock the send of movements 
                       timer_game.cancel();
                       inf_l.setText("Exit");
                        
                         worker.shutdown();
                        
                       break;
                                             
                   case CONTROL:    
                       if(playerNumber == -1) 
                       {
                           playerNumber = Integer.parseInt(inf_g.control_pck.info);
                           inf_g.playerNumber = playerNumber;
                           String tex = new String("Player " + playerNumber + " Information.");
                           inf_l.setText(tex);
                       }
                       else inf_l.setText("Information: " + inf_g.control_pck.info);
                       break;                       
                        
                   default: break;  
                }
       
            
            }    catch(java.lang.NullPointerException ex)
            {
               
            }

            catch ( IOException exception ) 
            {
                exception.printStackTrace();
            } 
        } 
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
                    lScores = new JLabel [num_players];
                    
                    if(num_players == 2)
                    {
                        pallets[0] = new Pallet(PADDLE_WIDTH,
                                               (GAME_HEIGHT-PADDLE_HEIGHT)/2,
                                                PADDLE_WIDTH, PADDLE_HEIGHT);     
                        pallets[1] = new Pallet(GAME_WIDTH-50,
                                                (GAME_HEIGHT-PADDLE_HEIGHT)/2,
                                                 PADDLE_WIDTH, PADDLE_HEIGHT);
                        lScores[1] = new JLabel();
                        lScores[1].setText("Player 1: "+ score[1]);
                        lScores[1].setBounds(GAME_WIDTH-20, GAME_HEIGHT + 20, 100, 20); 
                        add(lScores[1]);
                        
                        lScores[0] = new JLabel();
                        lScores[0].setText("Player 0: " + score[0]);
                        lScores[0].setBounds(20, GAME_HEIGHT + 20, 100, 20);
                        add(lScores[0]);
                        
                        time_l = new JLabel();
                        time_l.setText("00:00");
                        time_l .setBounds(GAME_WIDTH/2, GAME_HEIGHT + 20, 100, 20);
                        add(time_l);
                        
                        //p.web: http://answers.yahoo.com/question/index?qid=20070627085404AAsBBXk
                        df = new DecimalFormat("00");    
                        
                    }
       }

        private void sendData( Type_pckg type_pck )
        {
         try 
            {
                
                inf_g.SerializationGame(type_pck);
                byte[] data = inf_g.buffer_out.getBytes();  
           
                DatagramPacket sendPacket;
                
                if(type_pck == Type_pckg.HELLO)
                {
                    sendPacket = new DatagramPacket( data, 
                    data.length, server_address, serverPort_udp );
                } 
                else
                {
                    sendPacket = new DatagramPacket( data, 
                    data.length, otherClient_address, otherClientPort );
                }             
                
                socket.send( sendPacket ); 
                
            }
            catch ( IOException ioException ) 
            {
                ioException.printStackTrace();              
            } catch(FormatterClosedException e){e.printStackTrace();}
        } 

            
        private void formKeyPressed(KeyEvent evt) 
        {

            switch(evt.getKeyCode())
            {
                
                case KeyEvent.VK_W:
                    if(playerNumber == 0)
                    {
                        if(pallets[0].getPositionY()>0)
                        {
                                pallets[0].setPositionY
                                        (pallets[0].getPositionY()-15);
                               inf_g.mov_pck.setPositionY
                                       (pallets[0].getPositionY());
                               inf_g.mov_pck.setPositionX
                                       (pallets[0].getPositionX());
                               inf_g.mov_pck.numberPl = playerNumber;
                               sendData(Type_pckg.MOV);
                               game.update();
                        } 
                    } break;

                case KeyEvent.VK_S:
                    if(playerNumber == 0)
                    {
                        if(pallets[0].getPositionY()+100 < GAME_HEIGHT)
                        {
                                pallets[0].setPositionY
                                        (pallets[0].getPositionY()+ 15);
                                inf_g.mov_pck.setPositionY
                                        (pallets[0].getPositionY());
                                inf_g.mov_pck.setPositionX
                                        (pallets[0].getPositionX());
                                inf_g.mov_pck.numberPl = playerNumber;
                                sendData(Type_pckg.MOV);                              
                                game.update();
                        } 
                    } break;
                 
                case KeyEvent.VK_UP:
                   if(playerNumber == 1)
                    {
                        if(pallets[1].getPositionY()>0)
                        {
                                pallets[1].setPositionY
                                        (pallets[1].getPositionY()- 15);
                                inf_g.mov_pck.setPositionY
                                        (pallets[1].getPositionY());
                                inf_g.mov_pck.setPositionX
                                        (pallets[1].getPositionX());
                                inf_g.mov_pck.numberPl = playerNumber;
                                sendData(Type_pckg.MOV); 
                                game.update();
                        } 
                    } break;

                case KeyEvent.VK_DOWN:
                   if(playerNumber == 1)
                    {
                        if(pallets[1].getPositionY()+100 < GAME_HEIGHT)
                        {
                                pallets[1].setPositionY
                                        (pallets[1].getPositionY()+ 15);
                                inf_g.mov_pck.setPositionY
                                        (pallets[1].getPositionY());
                                inf_g.mov_pck.setPositionX
                                        (pallets[1].getPositionX());
                                inf_g.mov_pck.numberPl = playerNumber;
                                sendData(Type_pckg.MOV);
                                game.update();
                        }  
                    } break;
            }

        }
                
        class EventHandler extends WindowAdapter
        {
            public void windowClosing(WindowEvent e)
            {
                if(inf_g.st != Information_game.Status.OFF)
                {
                    sendData(Type_pckg.EXIT);
                    if(playerNumber == 0) 
                    {
                        timer.cancel();
                    }
                    timer_game.cancel();                    
                    if (soundBg != null) soundBg.stop();
                }
                worker.shutdown();
                //socket.close();
                setVisible(false);
            }
        }    
        
        private void timer_game()
        {
            //implement a clock
            //if(tempus == 0)  {
            if(seconds == 0)
            {
               if(minutes == 0)
                {
                
                //end the game
                    timer.cancel();
                    timer_game.cancel();
                    
                sendData(Type_pckg.EXIT);
                
                if(score[0] > score[1])
                {
                    inf_g.control_pck.info = "The player 0 won";
                    inf_l.setText("Information: The player 0 won");
                }
                else 
                {
                 if(score[1]>score[0])
                 {
                     inf_g.control_pck.info = "The player 1 won";                    
                     inf_l.setText("Information: The player 1 won");
                 }
                     
                 else 
                 {
                     inf_g.control_pck.info = "Score tied. Congratulations to both";
                     inf_l.setText("Score tied. Congratulations to both");
                 }
                }
                    sendData(Type_pckg.CONTROL);
                    
                }               
               
                else 
                {
                    seconds = 60;
                    minutes --;
                } 
            }
            else seconds --;

            time_l.setText( df.format(minutes) + ":" + df.format(seconds));
            
            //tempus = 1; } else { tempus = 0; }
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
                    lScores[1].setText("Player 1: "+ score[1]);
                    inf_g.goal_pck.player = 1;
                    sendData(Type_pckg.GOAL);
                    if (sGoal != null) sGoal.play();

            }

            if((this.ball.getX()+ball.getDiameter()) >= GAME_WIDTH)  //Goal Player 1
            {
                    this.ball.setX((GAME_WIDTH-ball.getDiameter())/2);
                    this.ball.setY((GAME_HEIGHT-ball.getDiameter())/2);
                    speed_left = -5;

                    score[0] = score[0] + 1;
                    lScores[0].setText("Player 0: "+ score[0]);
                    inf_g.goal_pck.player = 0;
                    sendData(Type_pckg.GOAL);
                    if (sGoal != null) sGoal.play();


            }

            for(int i=0; i<PADDLE_HEIGHT; i++)
            {
                    if((this.ball.getY()+ pallets[1].getDimX()) == 
                            (this.pallets[0].getPositionY()+i)
                                &&(this.ball.getX()) <= 
                            (this.pallets[0].getPositionX()+ pallets[0].getDimX()))
                    {
                            if(sHit != null) sHit.play();
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
                            if(sHit != null) sHit.play();
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
            game.update();
            sendData(Type_pckg.BALL);


        }    
        
    
     
}
