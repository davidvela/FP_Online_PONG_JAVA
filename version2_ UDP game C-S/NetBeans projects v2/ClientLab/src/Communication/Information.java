package Communication;

import java.io.*;
import java.net.DatagramPacket;
import java.util.*;

public class Information 
{

    public enum Connection {OFFLINE, ONLINE};
    public enum Status {OFF, ON, RUNNING};
    public enum Mode {TWO_PLAYERS, FOUR_PLAYERS };
    public enum Type_pck {  HELLO,EXIT,CHAT, NEW_GAME, JOIN_GAME, ACK, NACK, 
                            UPDATE };

    public Connection connection;
    public PacketChat chat_pck;
    public UpdateGames update_pck;
    public AckServer ack_pck;
    public NewGame game_pck;
    public JoinGame join_pck;
    public int port_game;
    
    public int number_Games; 
    final int max_games = 50;
    public GameCreated [] list_games ;


    public String  buffer_out;
    public String buffer_in;
    private Formatter output;
    private Scanner input;

    
    
        public Information() 
        {
              
            chat_pck = new PacketChat();
            update_pck = new UpdateGames();
            ack_pck = new AckServer();
            game_pck = new NewGame();
            join_pck = new JoinGame();
            update_pck = new UpdateGames();
            
            list_games = new GameCreated[max_games];
            connection = Connection.OFFLINE;
        }
    
        public class GameCreated 
        {          
         //information about name game and number of users
                private String nameGame;
                private int numberPlayers;
                private int currentNumberPlayers;
                private int ID;
                
                public GameCreated( String name, int players, int currentP, 
                                    int ID) 
                {
                      this.ID = ID;
                      this.nameGame = name;
                      this.numberPlayers = players;
                      this.currentNumberPlayers = currentP;
                }
         
                public String CreateName()
                {
                    return new String(nameGame + " (" + currentNumberPlayers + 
                            "/" + numberPlayers + ")");
                }
                
                public int getId()
                {
                    return ID;
                }

                public int getNumberPlayers() 
                {
                    return numberPlayers;
                }
                
        } 
              
        public int createList( Scanner input)
        {
            
            number_Games = Integer.parseInt(input.nextLine()); //input.nextInt();
            for(int i=0; i<number_Games; i++)
            {
                try{
                     String name    = input.nextLine();
                    int nPlayers   = Integer.parseInt(input.nextLine());
                    int cPlayers   = Integer.parseInt(input.nextLine());               
                    int ID         = Integer.parseInt(input.nextLine()); 
                    list_games[i]  = new GameCreated(name, nPlayers, cPlayers, ID);
                }catch(NoSuchElementException e){e.printStackTrace();}
                 catch(NullPointerException e){e.printStackTrace();}
            }
            return number_Games;

        }
    

    
   //******************************Packets**********************************//
           

        public class NewGame 
        {    
            private String nameGame;
            private int numberPlayers;
            private int duration; 
            private int gameId;
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

            public int getGameId() 
            {
                return gameId;
            }

            public void setGameId(int gameId) 
            {
                this.gameId = gameId;
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

        public class UpdateGames 
        {    
               // private GameCreated [] list_games_on;
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
            public Type_pck inReplyTo;      // Type package that assented 
            public String info;            // Additional information

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
        
        
        }
          
        //************************** Sending/Reception ***********************/

        public void SerializationPck (Type_pck type_pck)
        {
            
          output = new Formatter();

          buffer_out ="";
          output.format( "%s\n", type_pck.toString() ); 
              switch (type_pck)
              {
                    case HELLO:
                        break;

                    case EXIT:
                        break; 
                        
                    case UPDATE:
                        break; 
                        
                    case CHAT:
                            output.format( chat_pck.name + "\n>>> " 
                                  + chat_pck.message + "\n" );
                             break;

                    case NEW_GAME:
                            output.format(game_pck.nameGame + "\n" +
                                    game_pck.getNumberPlayers() );

                        break;

                    case JOIN_GAME: 
                        output.format(Integer.toString(join_pck.ID) + "\n");
                        break;


            }
         buffer_out = output.toString();
         output.close();
        }
    
        public Type_pck DeserializationPck (String message)
        {
            buffer_in = "";
            chat_pck.reset();
            
            buffer_in = message;
            input = new Scanner(buffer_in);

            Type_pck type_pck = Type_pck.valueOf(input.nextLine());
            switch (type_pck)
              {
 
                   case CHAT:
                            chat_pck.setName(input.nextLine());
                            chat_pck.setMessage(input.nextLine());
                            break;
                   
                   case UPDATE:
                       createList(input); 
                       break;
                        
                    case ACK:
                        ack_pck.inReplyTo = Type_pck.valueOf(input.nextLine());
                        switch(ack_pck.inReplyTo)
                            {
                                case HELLO: createList(input); break;
                                    
                                case NEW_GAME: port_game = input.nextInt(); 
                                               break;
                                    
                                case JOIN_GAME:  
                                    port_game = Integer.parseInt
                                            (input.nextLine());
                                    break;                                                                      
                            }
                        ack_pck.info = input.nextLine();
                        break; 
                    
                    case NACK: 
                        ack_pck.inReplyTo = Type_pck.valueOf(input.nextLine());
                        ack_pck.info = input.nextLine();
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
