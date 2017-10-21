
package server.communication;

import java.io.*;
import java.net.DatagramPacket;
import java.util.*;


public class Information_game 
{    
    public enum Status {WAITING, ON, OFF };
    public enum Type_pckg {MOV, MOV_O, BALL, GOAL, EXIT,CONTROL, START, PAUSE, 
    RESTART,TIMER};
   
    public BallPos ball_pck;
    public Movement mov_pck;
    public Goal goal_pck;
    public ControlGame control_pck;
    public TimerGame timer_pck;
    public AckServer ack_pck;
    public Status status_game;
    public int duration;
    
    //public String  buffer_out;
    //public String  buffer_in;
    //private Formatter output;
    //private Scanner input;
    
        public Information_game() 
        {
              
            ball_pck = new BallPos();
            mov_pck = new Movement();
            control_pck = new ControlGame();
            goal_pck = new Goal();
            timer_pck = new TimerGame();
            status_game = Status.WAITING;
            
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
        
        public class ControlGame    // to send text information to the players
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

        public void SerializationGame (Type_pckg type_pck, Formatter output)
        {
            //output = new Formatter();
            //buffer_out ="";
            try
            {
                
            output.format( "%s\n", type_pck.toString() ); 
              switch (type_pck)
                {
                  case START:
                        output.format("%d\n",duration);
                        break;

                    
                    case MOV_O:
                        output.format("%d\n%d\n%d\n", 
                                mov_pck.positionX,mov_pck.positionY,
                                mov_pck.numberPl);                                                  
                        break; 
                    
                    case BALL:
                        output.format("%d\n%d\n%d\n", ball_pck.getPositionX(), 
                                                      ball_pck.getPositionY(),
                                                      ball_pck.getHit());
                        break;
                        
                    case TIMER:
                        output.format("%d\n%d\n",   timer_pck.getMin(), 
                                                    timer_pck.getSec());
                        break;

                    case GOAL:
                        output.format("%d\n",goal_pck.player);
                        break;
                        
                    case CONTROL:
                        output.format("%s\n",control_pck.info);
                        break;  

                }
                          }
            catch(FormatterClosedException i ){ }
            catch(NullPointerException i ){ }
            
              //buffer_out = output.toString();
              //output.close();
        }
    
        public Type_pckg DeserializationGame ( Scanner input)
        {
            //buffer_in = "";
            //buffer_in = message;
            //input = new Scanner(buffer_in);
            Type_pckg type_pck = null;
            String type;
            for(int i = 0; i<2; i++)
            {
                try
                {
                    type = input.next();
                    type_pck = Type_pckg.valueOf(type);
                    break;
                }
                catch(java.util.NoSuchElementException ex)
                {
                     ex.printStackTrace();
                     type_pck = null;
                }
                catch(IllegalArgumentException e)
                {
                     e.printStackTrace();
                     type_pck = null;
                } 
                
            }
            if (type_pck == null) return null;
              switch (type_pck)
              {     
                   case MOV:
                       mov_pck.positionX = input.nextInt();
                       mov_pck.positionY = input.nextInt();
                       mov_pck.numberPl  = input.nextInt();
                       break; 
  
             }
            return type_pck;
               
            //return null;
          //input.close();

        }

    
        
        public void Marshaling (Type_pckg type_pck)
        { }     
        public void Unmarshaling (BufferedReader buffer)
        { }
      
     
    
}
