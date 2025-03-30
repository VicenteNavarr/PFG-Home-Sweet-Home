
package homeSweetHome.socket;


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javax.swing.JOptionPane;

public class Client implements Runnable {

    private int puerto;


    public Client(int puerto) {
        this.puerto = puerto;
  
    }

    @Override
    public void run() {

        //Host del servidor
        final String HOST = "127.0.0.1";

        DataInputStream dis;
        DataOutputStream dos;

        try {
            
            //Socket para conexión con cliente
            Socket sc = new Socket(HOST, puerto);

            dis = new DataInputStream(sc.getInputStream());
            dos = new DataOutputStream(sc.getOutputStream());

           
            sc.close();
            

        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null, "Error de conexión");
        }

    }

}
