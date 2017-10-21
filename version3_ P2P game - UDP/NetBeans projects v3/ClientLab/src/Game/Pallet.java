package Game;

public class Pallet 
{
        private int positionX   = 25;
	private int positionY   = 215;
	private int dimX        = 25;
	private int dimY        = 100;
        
        
    public Pallet() 
    {
	super();
    }
	
    public Pallet (int x, int y, int dimx, int dimy) 
    {		
        super();
    	this.positionX  = x;
	this.positionY  = y;
	this.dimX       = dimx;
	this.dimY       = dimy;
    }
                
    public void setDimX(int dimX) 
    {
        this.dimX = dimX;
    }

    public void setDimY(int dimY) 
    {
        this.dimY = dimY;
    }

    public void setPositionX(int positionX) 
    {
        this.positionX = positionX;
    }

    public void setPositionY(int positionY) 
    {
        this.positionY = positionY;
    }

    public int getDimX() 
    {
        return dimX;
    }

    public int getDimY() 
    {
        return dimY;
    }

    public int getPositionX() 
    {
        return positionX;
    }

    public int getPositionY() 
    {
        return positionY;
    }

    
    
    
    
}
        
        
	