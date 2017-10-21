
package serverlab;

import javax.swing.JFrame;


public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args)
    {
        /*Pong_Server app = new Pong_Server();
        app.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        app.execute();*/

        MainServer mainWindow = new MainServer();
        mainWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainWindow.setSize(500,500);
        mainWindow.setVisible(true);



    }
}
