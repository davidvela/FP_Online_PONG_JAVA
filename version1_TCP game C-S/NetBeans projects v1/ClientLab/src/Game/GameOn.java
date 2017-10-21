package Game;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.*; 
import java.awt.image.BufferedImage;
import javax.imageio.*;
import java.io.*;
import javax.swing.*;
import javax.swing.WindowConstants;
import javax.swing.JFrame;


public class GameOn extends javax.swing.JPanel 
{
        private final int GAME_WIDTH = 640;
        private final int GAME_HEIGHT = 480;

	public enum Mode {SIMPLE, COMPLETE };
        
        public  Ball ball;
	private Pallet pallet1;
	private Pallet pallet2;
        private Mode modeG;
        private BufferedImage imgBall,imgP1,imgP2,imgbg;
        
        
	public GameOn(Ball ball, Pallet pallet1, Pallet pallet2, int mode) 
        {
		this.ball = ball;
		this.pallet1 = pallet1;
                this.pallet2 = pallet2;
                
                switch (mode)
                    {
                        case 0:     modeG = Mode.SIMPLE;
                                    break;
                        case 1:     modeG = Mode.COMPLETE;  //FOOTBALL CASE
                             try {
                             imgBall = ImageIO.read(getClass().getResource("/Resources/blue_ball.png"));
                             imgP1 = ImageIO.read(getClass().getResource("/Resources/blue_boot0.png"));
                             imgP2 = ImageIO.read(getClass().getResource("/Resources/blue_boot1.png"));
                             imgbg = ImageIO.read(getClass().getResource("/Resources/bl_sport.jpg"));
                             
                             } catch (IOException e) {  e.printStackTrace(); }
                             break;
                        case 2:
                            modeG = modeG = Mode.COMPLETE;  //WATER CASE
                            try {
                             imgBall = ImageIO.read(getClass().getResource("/Resources/dball.png"));
                             imgP1 = ImageIO.read(getClass().getResource("/Resources/del0.png"));
                             imgP2 = ImageIO.read(getClass().getResource("/Resources/del1.png"));
                             imgbg = ImageIO.read(getClass().getResource("/Resources/waterBK.jpg"));
                             } catch (IOException e) {  e.printStackTrace(); }
                             break;
                            
                    }
                
                
                initGUI();
	}
        
	public static void main(String[] args) 
        {
		JFrame frame = new JFrame();
		frame.getContentPane().add(new GameOn());
		frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);
	}

	public GameOn() 
        {
		initGUI();
	}

	private void initGUI() 
        {
		try 
                {
                    setPreferredSize(new Dimension(GAME_WIDTH, GAME_HEIGHT));
		} 
                catch (Exception e) 
                {
			e.printStackTrace();
		}
	}

	public void paint(Graphics g)
        {
                super.paintComponent(g); 
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

	public void update()
        {
		this.repaint();
	}

}
