
package clientlab;

import javax.swing.JFrame;

public class Main
{

    public static void main(String[] args)
    {
        ContainerWindow mainWindow = new ContainerWindow();
        mainWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainWindow.setSize(834,564);
        mainWindow.setLocation(180,100);
        mainWindow.setVisible(true);

     /* PongClient app ;
        if ( args.length == 0 )
           app = new PongClient("127.0.0.1");
        else
            app = new PongClient(args[0]);
        app.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
     */
    }
}
