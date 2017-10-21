package Game;

import Game.Ball;
import Game.Pallet;
import Communication.*;
import Communication.Information_game.Type_pckg;

import java.applet.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import javax.swing.JFrame;
import java.net.*;
import java.text.DecimalFormat;
import java.util.Formatter;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.swing.*;
//import sun.awt.HorizBagLayout;
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
        int tempus = 0;
        int minutes = 0;
        int seconds = 0;
        private  DecimalFormat df;
        private JLabel time_l;
        private JLabel inf_l;
	private JLabel [] lScores;
        
        private Socket connection; // connection to server
        private Scanner input; // input from server
        private Formatter output; // output to server
        private String serverHost; // = "127.0.0.1";    
        //private String serverHost = "148.197.34.67";
        private int serverPort_tcp;
        private Information_game inf_g;
        private ExecutorService worker;
        private int playerNumber;
        private int num_players;
        private AudioClip soundBg, sHit, sGoal;
   
        public  GameOnline (int port,int num, String host, int mode)
        {
            super("Game");
            serverPort_tcp = port; 
            num_players = num;
            serverHost = host;
            inf_g = new Information_game();
            JPanel panelGame = new JPanel();
            //top left corner (x,y); dimmension x, y 
            setBounds(0,0,GAME_WIDTH,GAME_HEIGHT); 
            initialitionOfElements();
            game = new GameOn(ball,pallets[0],pallets[1], mode);
            worker = Executors.newFixedThreadPool(1);
            
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
            try // connect to server and get streams
            {
                connection = new Socket( InetAddress.getByName( serverHost ), 
                        serverPort_tcp );
                // get streams for input and output
                input = new Scanner( connection.getInputStream() );
                output = new Formatter( connection.getOutputStream() );
            } 
            catch ( IOException ioException )
            {
                ioException.printStackTrace();         
            } 

            playerNumber = input.nextInt();
            String tex = new String("Player" + playerNumber + " Information.");
            inf_l.setText(tex);
            // create and start worker thread for this client
            worker.execute( new Runnable()
                {   public void run()  
                    {  run_reading();    }  }     );
 
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

        public void run_reading()
        {
            // receive messages sent to client and output them
            while ( true )
            {
                if ( input.hasNextLine() )
                    processMessage();
            } 
        } 

        // process messages received by server
        private void processMessage( )//String data )
        {
            Type_pckg type_pck = null;
            try
            {
                type_pck = inf_g.DeserializationGame(input);
            }catch(java.lang.NullPointerException ex)
            {
                inf_l.setText("4 time mistake");
                type_pck = inf_g.DeserializationGame(input);
            }
            try
            {
            switch (type_pck)
            {
                 
                   case BALL:
                        game.ball.setX(inf_g.ball_pck.getPositionX());
                        game.ball.setY(inf_g.ball_pck.getPositionY());
                        game.update();
                         if( (inf_g.ball_pck.getHit() == 1) && (sHit != null) )
                            sHit.play();
                        break;
                       
                   case MOV_O:
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
                        minutes = input.nextInt();
                        timer_game = new java.util.Timer();
                        TimerTask timertask_game = new TimerTask()
                            {  public void run(){ timer_game();} };
                        timer_game.scheduleAtFixedRate(timertask_game, 0, 1000);
                        break; 
                        
                   case EXIT: 
                       playerNumber = -1; //lock the send of movements 
                       timer_game.cancel();
                       //worker.shutdown();
                       break;
                       
                   /*case TIMER: 
                        time_l.setText( df.format(inf_g.timer_pck.getMin())
                        + ":" + df.format(inf_g.timer_pck.getSec()));
                       break;*/
                       
                   case CONTROL:    //just display information 
                       inf_l.setText("Information: " + inf_g.control_pck.info);
                       break;                       
                        
                   default: inf_l.setText(input.nextLine());  
            }
             }
            catch(java.lang.NullPointerException ex)
            {
               
            }
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
                               inf_g.SerializationGame(Type_pckg.MOV, output);
                               output.flush();
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
                                inf_g.SerializationGame(Type_pckg.MOV, output);
                                output.flush();                                
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
                                inf_g.SerializationGame(Type_pckg.MOV, output);
                                output.flush(); 
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
                                inf_g.SerializationGame(Type_pckg.MOV, output);
                                output.flush();
                                game.update();
                        }  
                    } break;
            }

        }
                
        class EventHandler extends WindowAdapter
        {
            public void windowClosing(WindowEvent e)
            {
                    setVisible(false);
                    inf_g.SerializationGame(Type_pckg.EXIT, output);
                    output.flush();
                    output.close();
                    input.close();
                   try{
                    connection.close();
                    if(timer_game != null) timer_game.cancel();
                    if (soundBg != null) soundBg.stop();
                   } catch( IOException o){o.printStackTrace();}
                   catch( NullPointerException o){o.printStackTrace();}
            }
        }    
        
        private void timer_game()
        {
            //implement a clock
            //if(tempus == 0)  {
            if(seconds == 0)
            {
                if(minutes == 0)    timer_game.cancel();
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
        
        
}
