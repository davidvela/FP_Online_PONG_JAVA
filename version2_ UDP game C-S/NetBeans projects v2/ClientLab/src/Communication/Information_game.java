package Communication;

import java.io.*;
import java.net.DatagramPacket;
import java.net.Socket;
import java.util.*;

public class Information_game 
{    
    public enum Status {OFF, ON, RUNNING};
    public enum Type_pckg {MOV, MOV_O, BALL, GOAL, EXIT,CONTROL, START, PAUSE, 
    RESTART,TIMER, HELLO};
       
    public BallPos ball_pck;
    public Movement mov_pck;
    public Goal goal_pck;
    public ControlGame control_pck;
    public TimerGame timer_pck;
    public AckServer ack_pck;

    public Status st;
    public String  buffer_out;
    public String buffer_in;
    private Formatter output;
    private Scanner input;
     
    public int duration;
    public int playerNumber;


        public Information_game() 
        {
              
            ball_pck = new BallPos();
            mov_pck = new Movement();
            control_pck = new ControlGame();
            timer_pck = new TimerGame();
            goal_pck = new Goal();
            st = Status.OFF;
            

        }
    
   //******************************Packets**********************************//
 
        public class Movement 
        {    
            private int positionX   = 25;
            private int positionY   = 215;
            public int numberPl;

            public Movement() { }

            public int getPositionX() 
            {
                return positionX;
            }

            public int getPositionY() 
            {
                return positionY;
            }

            public void setPositionX(int positionX) 
            {
                this.positionX = positionX;
            }

            public void setPositionY(int positionY) 
            {
                this.positionY = positionY;
            }

        
        }  
        
        public class BallPos 
        {    
             private int positionX;   
             private int positionY;
             private int hit;
            
            public BallPos() { }

    
            public int getPositionX() 
            {
                return positionX;
            }

            public int getPositionY() 
            {
                return positionY;
            }

            public void setPositionX(int positionX) 
            {
                this.positionX = positionX;
            }

            public void setPositionY(int positionY) 
            {
                this.positionY = positionY;
            }
            
            public int getHit() 
            {
            return hit;
            }
            
            public void setHit(int hit) 
            {
                this.hit = hit;
            }

            
        } 
        
        public class TimerGame 
        {    
             private int min;   
             private int sec;      
            
            public TimerGame() { }
            
            public int getMin() 
            {
                return min;
            }

            public int getSec() 
            {
                return sec;
            }

            public void setMin(int minutes) 
            {
                this.min = minutes;
            }

            public void setSec(int seconds) 
            {
                this.sec = seconds;
            }

            
        }

        public class Goal 
        {    
           public int player;
           Goal()
           {
               player = -1;
           }
        } 
        
        public class ControlGame
        {
            public String info;
            public void ControlGame()
            {
                info = new String();
            }
        }
          
        public class AckServer
        {    // Order ACK ó NACK 
            public Type_pckg inReplyTo;      // Type package that assented 
            public String info;            // Additional information

            public AckServer() {}

            public Type_pckg getInReplyTo() 
            {
                return inReplyTo;
            }

            public String getInfo() 
            {
                return info;
            }

            public void setInReplyTo(Type_pckg inReplyTo) 
            {
                this.inReplyTo = inReplyTo;
            }

            public void setInfo(String info) 
            {
                this.info = info;
            }
        
        
        }
          
     //************************** Sending/Reception ***********************/

        public void SerializationGame (Type_pckg type_pck) //Socket connect)
        {
            output = new Formatter();
            buffer_out ="";
            output.format( "%s\n", type_pck.toString() ); 
              switch (type_pck)
                {
                    case MOV:
                        output.format("%d\n%d\n%d\n", 
                                mov_pck.positionX,mov_pck.positionY,
                                mov_pck.numberPl);
                        break; 
                     case EXIT:
                        output.format("%d\n",playerNumber);
                        break; 
                }
            
              buffer_out = output.toString();
              output.flush();
              output.close();
        }
    
        public Type_pckg DeserializationGame (String message)
        {
            buffer_in = "";
            buffer_in = message;
            input = new Scanner(buffer_in);
            
            Type_pckg type_pck = null;
            String type;
            for(int i = 0; i<2; i++)
            {
                try
                {
                    type = input.nextLine();
                    type_pck = Type_pckg.valueOf(type);
                    break;
                }
                catch(java.util.NoSuchElementException ex)
                {
                    // ex.printStackTrace();
                     type_pck = null;
                }
                catch(IllegalArgumentException e)
                {
                     //e.printStackTrace();
                     type_pck = null;
                } 
            }
            
            if (type_pck == null) return null;
           
            try{
              switch (type_pck)
                {     
 
                   case BALL:
                        ball_pck.setPositionX(input.nextInt());
                        ball_pck.setPositionY(input.nextInt());
                        ball_pck.setHit(input.nextInt());
                        break;
                       
                   case MOV_O:
                        mov_pck.positionX = input.nextInt();
                        mov_pck.positionY = input.nextInt();
                        mov_pck.numberPl = input.nextInt();
                        break; 
                        
                    case GOAL:
                        goal_pck.player = input.nextInt();
                        break; 
                        
                    case START:
                        duration = input.nextInt();
                        break; 
                        
                    case TIMER:
                        timer_pck.setMin(input.nextInt());
                        timer_pck.setSec(input.nextInt());
                        break; 
                        
                    case CONTROL:
                        control_pck.info = input.nextLine();
                        break;  
                        
                        
                }
        } 
                       catch (IllegalArgumentException e)
                         {
                            //e.printStackTrace();
                            //type_pck = null;
                        } 
                       catch (InputMismatchException e)
                         {
                            //e.printStackTrace();
                            //type_pck = null;
                        } 
                        catch(NullPointerException e){}
            
          input.close();
          return type_pck;
        }

    
        
        public void Marshaling (Type_pckg type_pck)
        { }     
        public void Unmarshaling (BufferedReader buffer)
        { }
      
     
    
}
