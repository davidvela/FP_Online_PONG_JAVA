package Game;
import Communication.Information_game;

public class Ball 
{
        private int diameter    = 50;
        //Global varibles of the game: 
        static final int GAME_WIDTH = 640;
        public final int GAME_HEIGH = 480;

        private int positionX   = (GAME_WIDTH-diameter)/2;
	private int positionY   = (GAME_HEIGH-diameter)/2;
        //displacement, movement => dir + speed
	private int desx        = -5;  
	private int desy        = -5;

	public int getDesx() 
        {
            return desx;
	}
        
	public void setDesx(int desx) 
        {
		this.desx = desx;
	}
        
	public int getDesy() 
        {
		return desy;
	}
        
	public void setDesy(int desy) 
        {
		this.desy = desy;
	}
        
	public Ball(int x, int y, int diam) 
        {
		super();
                positionX = x; 
                positionY = y;
                diameter = diam;
	}
        
        public Ball() 
        {
		super();
       	}
	
	public int getDiameter() 
        {
		return diameter;
	}
	
        public void setDiameter(int diam) 
        {
		diameter = diam;
	}
        
	public int getX() 
        {
		return this.positionX;
	}
	
        public void setX(int x) 
        {
		this.positionX = x;
	}
	
        public int getY() 
        {
		return positionY; 
	}
	
        public void setY(int y) 
        {
		this.positionY = y;
	}
    
    
    
}
