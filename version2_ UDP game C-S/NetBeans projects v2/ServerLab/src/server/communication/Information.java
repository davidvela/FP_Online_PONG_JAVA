package server.communication;

import java.io.*;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.Formatter;
import java.util.Date;

public class Information 
{
   
    public enum Connection {OFFLINE, ONLINE};
    public enum Status {OFF, ON, RUNNING};
    public enum Mode {TWO_PLAYERS, FOUR_PLAYERS };   
    public enum Type_pck {  HELLO,EXIT,CHAT, NEW_GAME, JOIN_GAME, ACK, NACK, 
                            UPDATE };
    
    public PacketChat chat_pck;
    public NewGame game_pck;
    public JoinGame join_pck;
    public AckServer ack_pck;
    
    final int max_users = 50;
    final int max_games = 20;
    
    public int number_Games; 
    public int number_Users; 
    public GameCreated [] list_games ;
    public User_udp [] list_users;
   
    public String  buffer_out;
    public String buffer_in;
    private Formatter output;
    private Scanner input;

    
    
        public Information() 
        {

            number_Games = 0;
            number_Users = 0; 

            chat_pck    = new PacketChat();
            ack_pck     = new AckServer();
            game_pck    = new NewGame();
            join_pck    = new JoinGame();
            
            list_users  = new User_udp [max_users];
            list_games  = new GameCreated[max_games];
           // list_games_client= new GameCreated[max_games];

            for(int i=0; i < max_games; i++)
            {
                list_games[i] = new GameCreated(i);
            }
            
        }


        //****************************server*************************//

        public int newGameCreated( int port)
        {
            //search for the last games 
            
            for(int i=0; i < list_games.length; i++)
            {
                if(list_games[i].status == Status.OFF)
                {
                    list_games[i].status = Status.ON;
                    list_games[i].nameGame = game_pck.nameGame;
                    list_games[i].numberPlayers = game_pck.numberPlayers;
                    list_games[i].duration = game_pck.duration;
                    
                    list_games[i].time = new Date();
                    list_games[i].currentNumberPlayers = 1;
                    list_games[i].port_connect = port;
                    //list_games[i].ID = number_Games;
                    number_Games++;
                    return (list_games[i].ID);
                }
            }
 
            return 0;
        }

        public int SuspendUser (DatagramPacket receivePacket)
        {
            InetAddress ipNewUser =receivePacket.getAddress();
            int portNewUser = receivePacket.getPort();
        
             for(int i=0; i < list_users.length; i++)
            {
                if(list_users[i]!= null)
                if( (list_users[i].port_connect == portNewUser) && 
                    list_users[i].ip_user.equals(ipNewUser) )
                {
                    list_users[i].status = Status.OFF;
                    number_Users--;
                    return list_users[i].ID;
                    
                }
            }
             return -1;
            
        }

        public Type_pck newUser( DatagramPacket receivePacket)
        {
            boolean new_user = false;
            int ID;
            InetAddress ipNewUser =receivePacket.getAddress();
            int portNewUser = receivePacket.getPort();

           
            //Looking for a place to insert it.
            //if(list_users!= null)
            for(int i=0; i < list_users.length; i++)
            {
                if(list_users[i]!= null)
                if( (list_users[i].status == Status.OFF) || 
                    list_users[i].checkTime() )
                {
                    list_users[i].status = Status.ON;
                    list_users[i].time = new Date();
                    list_users[i].port_connect = portNewUser;
                    list_users[i].ip_user = ipNewUser;
                    new_user = true;
                     break;
                }
            }
            
            if (new_user == false)
            {

                list_users[number_Users] = new User_udp(
                        receivePacket.getAddress(), 
                        number_Users, Status.ON, new Date(), 
                        receivePacket.getPort());
                
                number_Users++;  
            }
            //return ack and in the information the list of current games
            ack_pck.setInReplyTo(Type_pck.HELLO);
            ack_pck.setInfo(current_Games());
            return Type_pck.ACK;
        }

        public String current_Games()
        {
            Formatter output = new Formatter();            
            output.format(Integer.toString(number_Games) + "\n");
            
            for(int i=0; i < number_Games; i++)
            {
     
                if(list_games[i].status == Status.ON)
                {                   
                    output.format(list_games[i].nameGame 
                            + "\n" + 
                            Integer.toString(list_games[i].numberPlayers)
                            + "\n" + 
                            Integer.toString(list_games[i].currentNumberPlayers)
                            + "\n" +
                            Integer.toString(list_games[i].ID) + "\n" );
                }
            }
            String return_S = output.toString();
            return return_S;

        }
        
        public Type_pck join_to_Game() //return the port
        {
            int Id = join_pck.getID();
            this.ack_pck.inReplyTo = Type_pck.JOIN_GAME;
            
            if (list_games[Id].status == Status.ON)
            {  
                if(list_games[Id].currentNumberPlayers <  list_games[Id].numberPlayers)
                {
                    list_games[Id].currentNumberPlayers ++;
                    
                    if( (list_games[Id].currentNumberPlayers +1) == list_games[Id].numberPlayers )
                        list_games[Id].status = Status.RUNNING;
                                       
                    ack_pck.setInfo(Integer.toString
                            (list_games[Id].getPort_connect()) + "\n info \n");
                    
                    return Type_pck.ACK; 
                }
                else ack_pck.setInfo("the number of players is complete");
            }
            else ack_pck.setInfo("the game is not available");
                
            return Type_pck.NACK;
        }

    
       //******************class******************************//
        public class User_udp 
        {    // Orden HELLO 
        
            private java.net.InetAddress ip_user;
            private int port_connect;

            private int ID;
            private Status status; 
            private Date time;


            public User_udp( InetAddress ip, int ID, Status status,  Date time, 
                             int port) 
            {
                this.ip_user = ip;
                this.port_connect = port;

                this.ID = ID;
                this.status = status;
                this.time = time;
            }

            private boolean checkTime()   
            {
                //return true if time is higher than 20min
                return false;
            }

            public int getID() 
            {
                return ID;
            }

            public InetAddress getIp_user() 
            {
                return ip_user;
            }

            public int getPort_connect() 
            {
                return port_connect;
            }

            public Status getStatus() 
            {
                return status;
            }

            public Date getTime() 
            {
                return time;
            }
        

    } 
            
        public class GameCreated 
        {          
            //information about name game and number of users
            public String nameGame;
            public  int numberPlayers;
            public int currentNumberPlayers;
            public int ID;
            public int duration;
            public Status status;


            public Date time;
            private int port_connect;

            public GameCreated()
            {
                nameGame = "";
                numberPlayers = 0;
                currentNumberPlayers = 0;
                ID = -1;
                duration = 5;
                status = Status.OFF; 
                time = null;
                port_connect = -1;
            }
            
            public GameCreated(int i)
            {
                nameGame = "";
                numberPlayers = 0;
                currentNumberPlayers = 0;
                ID = i;
                duration = 5;
                status = Status.OFF; 
                time = null;
                port_connect = -1;
            }

            public GameCreated( String name, int players, int ID, Status status, 
                                Date time, int port) 
            {
                this.nameGame = name;
                this.numberPlayers = players;
                this.currentNumberPlayers = 1;
                this.ID = ID;
                this.status = status;
                this.time = time;
                this.port_connect = port;
            }

            public void setPort_connect(int port_connect) 
            {
                 this.port_connect = port_connect;
            }

            public int getPort_connect() 
            {   
                 return port_connect;
            }
            
            public void Desconnection()
            {
                status = Status.OFF;
                number_Games --;
                //Counter();
            }


        } 
    

   //******************************Packets**********************************//
           

       public class NewGame 
        {    
            private String nameGame;
            private int duration; 
            private int numberPlayers;
            private int fqBall;

            public NewGame() { }

            public String getNameGame() 
            {
                return nameGame;
            }

            public int getNumberPlayers() 
            {
                return numberPlayers;
            }

            public void setNameGame(String nameGame) 
            {
                this.nameGame = nameGame;
            }

            public void setNumberPlayers(int numberPlayers) 
            {
                this.numberPlayers = numberPlayers;
            }
            
            public int getDuration() 
            {
                return duration;
            }

            public void setDuration(int duration) 
            {
                this.duration = duration;
            }

           public void setFqBall(int fqBall) 
           {
            this.fqBall = fqBall;
           }

            public int getFqBall() 
            {
            return fqBall;
            }
            
            
        
        } 
        
        
        public class JoinGame 
        {    
            private int ID;

            public JoinGame() { }

            public int getID() 
            {
                return ID;
            }

            public void setID(int ID)
            {
                this.ID = ID;
            }
            
        } 

        public  class PacketChat 
        {     
            
            private String name;
            private String message;

            public PacketChat() 
            { }

            public String getMessage() 
            {
            return message;
        }

            public String getName() 
            {
            return name;
        }

            public void setMessage(String message) 
            {
            this.message = message;
        }

            public void setName(String name) 
            {
            this.name = name;
        }
        
            private void reset ()
            {
            setMessage(""); 
            setName("");
        }
            
        } 

        public class AckServer
        {    // Order ACK ó NACK 
            private Type_pck inReplyTo;      // Type package that assented 
            private String info;            // Additional information

            public AckServer() {}

            public Type_pck getInReplyTo() 
            {
            return inReplyTo;
        }

            public String getInfo() 
            {
            return info;
        }

            public void setInReplyTo(Type_pck inReplyTo) 
            {
            this.inReplyTo = inReplyTo;
        }

            public void setInfo(String info) 
            {
            this.info = info;
            }
            
            private void reset ()
            {
                setInfo("");
            }
        
        
        }
          

    


     //************************** Sending/Reception ***********************/

        public void SerializationPck (Type_pck type_pck)
        {
            
          output = new Formatter();
          buffer_out ="";  
          output.format( "%s\n", type_pck.toString() ); 
          
          switch (type_pck)
              {


                    case CHAT:
                            output.format( chat_pck.name + "\n"
                                  + chat_pck.message + "\n" );
                             break;
                    case ACK:
                           output.format( ack_pck.getInReplyTo() + "\n" 
                                  + ack_pck.getInfo() + "\n" );
                             break; 
                    
                    case NACK: 
                            output.format( ack_pck.getInReplyTo() + "\n" 
                                  + ack_pck.getInfo() + "\n" );
                             break;
                        
                    case UPDATE:
                            output.format( current_Games() + "\n" );
                            break;
                        


            }
         buffer_out = output.toString();
         output.close();
        }
    
        public Type_pck DeserializationPck (String message)
        {
            buffer_in = "";
            chat_pck.reset(); ack_pck.reset();
            
            buffer_in = message;
            input = new Scanner(buffer_in);

            Type_pck type_pck = Type_pck.valueOf(input.nextLine());
                switch (type_pck)
              {
                    case HELLO:
                        break;

                    case EXIT:
                        break;   
                        
                    case UPDATE:
                        break;  

                    case CHAT:
                        chat_pck.setName(input.nextLine());
                        chat_pck.setMessage(input.nextLine());
                        break;

                    case NEW_GAME:
                        game_pck.setNameGame(input.nextLine());
                        game_pck.setNumberPlayers(input.nextInt());
                        game_pck.setDuration(input.nextInt());
                        game_pck.setFqBall(input.nextInt());
                        break;

                    case JOIN_GAME:
                        join_pck.setID(Integer.parseInt(input.nextLine()));
                        break;

                    default: return null;
                }
         input.close();
         return type_pck;
        }

    
        public void Marshaling (Type_pck type_pck)
        {
        }
        public void Unmarshaling (BufferedReader buffer)
        {
        }
      
    
}
