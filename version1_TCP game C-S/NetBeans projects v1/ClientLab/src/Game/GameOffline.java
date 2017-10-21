package Game;

import Communication.Information_game;
import java.applet.*;
import java.awt.*;
import java.awt.Image.*;
import java.awt.event.*; 
import java.awt.image.BufferedImage;
import java.io.*;
import java.text.DecimalFormat;
import java.util.TimerTask;
import javax.swing.*;
import javax.imageio.*;
import sun.applet.AppletAudioClip;

public class GameOffline extends javax.swing.JFrame 
{
    public enum Mode {SIMPLE, COMPLETE };
    
        private final int GAME_WIDTH = 640;
        private final int GAME_HEIGHT = 480;
        private final int PADDLE_WIDTH = 20;
        private final int PADDLE_HEIGHT = 100;        
    
        private Game game;
	private Ball ball;
        private int num_players = 2;
        private int speed_right =  5;
        private int speed_left  = -5;
        private Pallet[] pallets; 
        private int [] score;
        private JLabel inf_l;
	private JLabel [] lScores;
        
        private AudioClip soundBg, sHit, sGoal;
        private BufferedImage imgBall,imgP1,imgP2,imgbg;
        private int goals_to_win;

        private Mode modeG;
        int tempus = 0;
        int minutes = 0;
        int seconds = 0;
        private  DecimalFormat df;
        private JLabel time_l;
        private java.util.Timer timer_game;
        private java.util.Timer timer;
        private int fqBall;
                  
        public  GameOffline (int mode_game, int duration, int goalsToWin, int fq)
        {
          
                    JPanel panelGame = new JPanel(); 
                    addWindowListener(new EventHandler()); 
                    //top left corner (x,y); dimmension x, y 
                    setBounds(0,0,GAME_WIDTH,GAME_HEIGHT);                  
                    addKeyListener(new KeyAdapter() {
                            public void keyPressed(KeyEvent evt) {
                            formKeyPressed(evt);}});                         
                    fqBall = fq;
                    
                    score = new int [num_players];
                    for(int i=0; i < num_players; i++)
                    {   score[i]=0;              }
                    
                    
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
                    goals_to_win = goalsToWin;              
                    game = new Game(ball,pallets[0],pallets[1]);
                    //game = new Game();                
                    panelGame.add(game);
                    panelGame.requestFocus();
 
                    switch (mode_game)
                    {
                        case 0:     modeG = Mode.SIMPLE;
                                    break;
                        case 1:     modeG = Mode.COMPLETE;  //FOOTBALL CASE
                                    sHit = Applet.newAudioClip(getClass().getResource
                                            ("/Resources/bhit_sport.wav"));   
                                    sGoal = Applet.newAudioClip(getClass().getResource
                                            ("/Resources/bGoal.wav"));   
                                    soundBg = Applet.newAudioClip(getClass().getResource
                                            ("/Resources/bk_sport.wav"));   
                                    soundBg.loop();
                                    try {
                             imgBall = ImageIO.read(getClass().getResource("/Resources/blue_ball.png"));
                             imgP1 = ImageIO.read(getClass().getResource("/Resources/blue_boot0.png"));
                             imgP2 = ImageIO.read(getClass().getResource("/Resources/blue_boot1.png"));
                             imgbg = ImageIO.read(getClass().getResource("/Resources/bl_sport.jpg"));
                             
                                   } catch (IOException e) {  e.printStackTrace(); }
                             break;
                        case 2:
                            modeG = modeG = Mode.COMPLETE;  //WATER CASE
                             sHit = Applet.newAudioClip(getClass().getResource
                                            ("/Resources/w_hit.wav"));   
                             sGoal = Applet.newAudioClip(getClass().getResource
                                            ("/Resources/w_score.wav"));   
                             soundBg = Applet.newAudioClip(getClass().getResource
                                            ("/Resources/w_ocean.wav"));   
                             soundBg.loop();
                            try {
                             imgBall = ImageIO.read(getClass().getResource("/Resources/dball.png"));
                             imgP1 = ImageIO.read(getClass().getResource("/Resources/del0.png"));
                             imgP2 = ImageIO.read(getClass().getResource("/Resources/del1.png"));
                             imgbg = ImageIO.read(getClass().getResource("/Resources/waterBK.jpg"));
                             
                                   } catch (IOException e) {  e.printStackTrace(); }
                             break;
                            
                    }
                    
                    
                    
                    
                    timer = new java.util.Timer();
                    TimerTask timertask = new TimerTask()
                    {  public void run(){ timer();} };
                    timer.scheduleAtFixedRate(timertask, 0, fqBall);
         
                    minutes = duration;
                    timer_game = new java.util.Timer();
                    TimerTask timertask_game = new TimerTask()
                    {  public void run(){ timer_game();} };
                    timer_game.scheduleAtFixedRate(timertask_game, 0, 1000);                          
                    
                    add( panelGame);
         }
        
        private void timer()
        {
            int max_speed_right = 20;
            int max_speed_left  = -20;
            int initial_speed_right = 5;
            int initial_speed_left  = -5;
            
            
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
            
            if((this.ball.getX())<=0)   // Gol of Plater 2 
            {
                    this.ball.setX((GAME_WIDTH-ball.getDiameter())/2);
                    this.ball.setY((GAME_HEIGHT-ball.getDiameter())/2);
                    speed_right = 5;
                    if (sGoal != null) sGoal.play();
                    score[1] += 1;
                    lScores[1].setText("Player 1:"  + score[1]);
                      if(goals_to_win != 0)
                            if(score[1] >= goals_to_win)
                            {
                            JOptionPane.showMessageDialog(this, "The player 1 has won", 
                                    "Congratulations", JOptionPane.INFORMATION_MESSAGE);
                            this.timer.cancel();
                            timer_game.cancel();
                            }
                }
            
            if((this.ball.getX()+ball.getDiameter()) >= GAME_WIDTH)  //Gol Player 1
            {
                    this.ball.setX((GAME_WIDTH-ball.getDiameter())/2);
                    this.ball.setY((GAME_HEIGHT-ball.getDiameter())/2);
                    speed_left = -5;
                    if (sGoal != null) sGoal.play();
                    score[0] = score[0] + 1;
                    lScores[0].setText("Player 0: " + score[0]);
                    if(goals_to_win != 0)
                            if(score[0] >= goals_to_win)
                            {
                            JOptionPane.showMessageDialog(this, "The player 0 has won", 
                                    "Congratulations", JOptionPane.INFORMATION_MESSAGE);
                            this.timer.cancel();
                            timer_game.cancel();
                            }

            }
            
            for(int i=0;i<100;i++)
            {
                    if((this.ball.getY()+ pallets[0].getDimX()) == (this.pallets[0].getPositionY()+i)
                            &&(this.ball.getX())<=(this.pallets[0].getPositionX()+ pallets[0].getDimX()))
                    {
                            if (sHit != null) sHit.play();
                            this.ball.setDesx(speed_right);
                            if(speed_right <= max_speed_right)
                            {
                                    speed_right++;
                            }
                    }
                    if((this.ball.getY()+pallets[1].getDimX())==(this.pallets[1].getPositionY()+i)&&
                            (this.ball.getX())>=(this.pallets[1].getPositionX()-ball.getDiameter()))
                    {
                            if (sHit != null) sHit.play(); 
                            this.ball.setDesx(speed_left);
                            if(speed_left >= max_speed_left)
                            {
                                    speed_left--;
                            }
                          
                    }
            }
            this.ball.setY(this.ball.getY()+this.ball.getDesy());
            this.ball.setX(this.ball.getX()+this.ball.getDesx());
            this.game.actualizar();
        }
        
        private void formKeyPressed(KeyEvent evt) 
        {

            switch(evt.getKeyCode())
            {
                case KeyEvent.VK_W:
                        if(pallets[0].getPositionY()>0)
                        {
                                pallets[0].setPositionY(pallets[0].getPositionY()-15);
                                game.actualizar();
                        } break;

                case KeyEvent.VK_S:
                        if(pallets[0].getPositionY()+100 < GAME_HEIGHT)
                        {
                                pallets[0].setPositionY(pallets[0].getPositionY()+ 15);
                                game.actualizar();
                        } break;

                case KeyEvent.VK_UP:
                        if(pallets[1].getPositionY()>0)
                        {
                                pallets[1].setPositionY(pallets[1].getPositionY()- 15);
                                game.actualizar();
                        } break;

                case KeyEvent.VK_DOWN:
                        if(pallets[1].getPositionY()+100 < GAME_HEIGHT)
                        {
                                pallets[1].setPositionY(pallets[1].getPositionY()+ 15);
                                game.actualizar();
                        }  break;
            }

        }



        private void timer_game()
        {
            //implement a clock
            if(seconds == 0)
            {
                if(minutes == 0)
                {
                    if(score[0] > score[1])
                    JOptionPane.showMessageDialog(this, "The player 0 has won", 
                                        "Congratulations", JOptionPane.INFORMATION_MESSAGE);

                    else 
                    {
                    if(score[1]>score[0])
                        JOptionPane.showMessageDialog(this, "The player 1 has won", 
                                        "Congratulations", JOptionPane.INFORMATION_MESSAGE);
                    else JOptionPane.showMessageDialog(this, "Score tied. Congratulations to both", 
                                        "Congratulations", JOptionPane.INFORMATION_MESSAGE);
                    }
                    this.timer.cancel(); 
                    timer_game.cancel();
                }
                else 
                {
                    seconds = 60;
                    minutes --;
                }   
            }
            else { 
                seconds --;
                time_l.setText( df.format(minutes) + ":" + df.format(seconds));
            }  
                 

        }
        
                    
                                        
                    
                    
        class EventHandler extends WindowAdapter
        {
            public void windowClosing(WindowEvent e)
            {
                if (soundBg != null) soundBg.stop();
                setVisible(false);
                timer.cancel();
                //System.exit( 0 );
            }
        } 
            
        public class Game extends javax.swing.JPanel 
        {
            private Graphics dbg;
            private Image dbImage = null;

            private Ball ball;
            private Pallet pallet1;
            private Pallet pallet2;

            public Game(Ball ball, Pallet pallet1, Pallet pallet2) 
            {
                    this.ball = ball;
                    this.pallet1 = pallet1;
                    this.pallet2 = pallet2;
                   
                    
                    initGUI();
                    
            }

            public  void main(String[] args) 
            {
                    JFrame frame = new JFrame();
                    frame.getContentPane().add(new Game());
                    frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
                    frame.pack();
                    frame.setVisible(true);
                    
                    
            }

            public Game() 
            {
                    initGUI();
            }

            private void initGUI() 
            {
                    try 
                    {
                      setPreferredSize(new Dimension(GAME_WIDTH, GAME_HEIGHT));
                    } catch (Exception e) 
                    {
                            e.printStackTrace();
                    }
            }

            public void paint(Graphics g)
            {
                    super.paintComponent(g); 
                    //if (dbImage != null) g.drawImage(dbImage, 0, 0, null);
                    switch (modeG)
                    {
                        case SIMPLE:
                            g.setColor(Color.WHITE);
                            g.fillRect(0, 0, GAME_WIDTH, GAME_HEIGHT);
                            g.setColor(Color.CYAN);
                            g.drawLine(GAME_WIDTH/2 , 0, GAME_WIDTH/2, GAME_HEIGHT);
                            g.fillRect(pallet1.getPositionX(), pallet1.getPositionY(), pallet1.getDimX(), pallet1.getDimY());
                            g.fillRect(pallet2.getPositionX(), pallet2.getPositionY(), pallet2.getDimX(), pallet2.getDimY());
                            g.setColor(Color.BLUE);
                            g.fillOval(ball.getX(), ball.getY(), ball.getDiameter(), ball.getDiameter());
                            break;
                            
                        case COMPLETE:
                            g.drawImage(imgbg,0,0,null);          
                            g.drawImage(imgBall, ball.getX(), ball.getY(), ball.getDiameter(), ball.getDiameter(), this);
                            g.drawImage(imgP2, pallet2.getPositionX(), pallet2.getPositionY(), this);
                            g.drawImage(imgP1, pallet1.getPositionX()-15, pallet1.getPositionY(), this);
                            break;
                    }
                                       
            }

             

            private void gameRender() // draw the current frame to an image buffer
            {
                if (dbImage == null)
                    {  // create the buffer
                        dbImage = createImage(GAME_WIDTH, GAME_HEIGHT);
                        if (dbImage == null) 
                            {
                            System.out.println("dbImage is null");
                            return;
                            }
                            else
                            dbg = dbImage.getGraphics( );
                    }

                // clear the background
                dbg.setColor(Color.white);
                dbg.fillRect (0, 0, GAME_WIDTH, GAME_HEIGHT);

                // draw game elements
                  switch (modeG)
                    {
                        case SIMPLE:
                            dbg.setColor(Color.CYAN);
                            dbg.drawLine(GAME_WIDTH/2 , 0, GAME_WIDTH/2, GAME_HEIGHT);
                            dbg.fillRect(pallet1.getPositionX(), pallet1.getPositionY(), pallet1.getDimX(), pallet1.getDimY());
                            dbg.fillRect(pallet2.getPositionX(), pallet2.getPositionY(), pallet2.getDimX(), pallet2.getDimY());
                            dbg.setColor(Color.BLUE);
                            dbg.fillOval(ball.getX(), ball.getY(), ball.getDiameter(), ball.getDiameter());
                            break;
                            
                        case COMPLETE:
                            dbg.drawImage(imgbg,0,0,null);          
                            dbg.drawImage(imgBall, ball.getX(), ball.getY(), ball.getDiameter(), ball.getDiameter(), this);
                            dbg.drawImage(imgP2, pallet2.getPositionX(), pallet2.getPositionY(), this);
                            dbg.drawImage(imgP1, pallet1.getPositionX()-15, pallet1.getPositionY(), this);
                            break;
                    }
            }  

            
            public void actualizar()
            {
                    //gameRender( );   
                    this.repaint();
            }
                       
            
            

    }
        
        
    
}
