
package homeSweetHome.socket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javax.swing.JOptionPane;

public class Server implements Runnable {

    private int puerto;
  



    public Server(int puerto) {
        this.puerto = puerto;

    }


    @Override
    public void run() {

        ServerSocket servidor = null;
        Socket sc = null;

        DataInputStream dis;
        DataOutputStream dos;

        try {

            //Socket del servidor
            servidor = new ServerSocket(puerto);

            System.out.println("Servidor iniciado");

            //Siempre escuchando peticiones
            while (true) {

                //Espero a que un cliente se conecte
                sc = servidor.accept();

                dis = new DataInputStream(sc.getInputStream());
                dos = new DataOutputStream(sc.getOutputStream());

                System.out.println("Cliente conectado");


               

                sc.close();

            }

        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);

            JOptionPane.showMessageDialog(null, "Error de conexión");
            
        }
        
        
        
        

    }
    
    
}
