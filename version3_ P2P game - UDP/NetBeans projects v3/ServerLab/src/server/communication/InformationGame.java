package server.communication;

import java.io.*;
import java.net.*;
import java.util.*;

public class InformationGame 
{
public enum Status {WAITING, ON, OFF };
    public enum Type_pckg { BALL, GOAL, EXIT,CONTROL, START, PAUSE, RESTART,TIMER,HELLO};
   
   
    public ControlGame control_pck;
    public AckServer ack_pck;
    public Status status_game;
    public int duration;
    public int current_players;
    public int total_Users;

    public String  buffer_out;
    public String  buffer_in;
    private Formatter output;
    private Scanner input;
    public int temp;
    public String ipTemp;
    public int portTemp;
    
        public InformationGame(int num_players) 
        {
              
            total_Users = num_players;
            control_pck = new ControlGame();
            status_game = Status.WAITING;
            ipTemp = new String();
            duration = 0;
        }
        
        public Type_pckg newUser( DatagramPacket receivePacket)
        {
            
            //return ack and in the information the list of current games
            control_pck.info = String.valueOf(current_players);
            current_players++;  
            return Type_pckg.CONTROL;
        }
        
   //******************************Packets**********************************//
 
       
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

        public Type_pckg SerializationGame (Type_pckg type_pck)
        {
            output = new Formatter();
            buffer_out ="";
            try
            {
            output.format( "%s\n", type_pck.toString() ); 
              switch (type_pck)
                {
                  case START:
                        output.format("%d\n%s\n%d\n",duration,ipTemp,portTemp);
                        break;
                        
                    case CONTROL:
                        output.format("%s\n",control_pck.info);
                        break;  

                }
            }
            catch(FormatterClosedException i ){ return null;}
            
              buffer_out = output.toString();
              output.close();
              return type_pck;
        }
    
        public Type_pckg DeserializationGame ( String message)
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
                  case HELLO:
                        break;
                      
                   case EXIT:
                     temp = input.nextInt();
             }
            
              input.close();
            return type_pck;
               
           //return null;

        }    
}
