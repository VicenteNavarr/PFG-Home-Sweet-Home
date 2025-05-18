package homeSweetHome.controller;

import homeSweetHome.dataPersistence.CurrentSession;
import homeSweetHome.dataPersistence.MySQLConnection;
import homeSweetHome.model.Event;
import homeSweetHome.utils.ImageUtils;
import homeSweetHome.model.User;
import java.net.URL;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import homeSweetHome.dataPersistence.EventDAO;
import homeSweetHome.dataPersistence.TaskDAO;
import homeSweetHome.model.Task;
import homeSweetHome.utils.LanguageManager;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

public class HelpViewController implements Initializable {

    @FXML
    private ScrollPane helpScrollPane;
    @FXML
    private VBox helpContentContainer;
    @FXML
    private Label helpTitle;

    /**
     * Métodos Inicio
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        //exportAllText();

    }

    public void exportAllText() {
        StringBuilder extractedText = new StringBuilder();

        // Recorre todos los nodos dentro del contenedor principal
        for (Node node : helpContentContainer.getChildren()) {
            if (node instanceof TextFlow) {
                for (Node textNode : ((TextFlow) node).getChildren()) {
                    if (textNode instanceof Text) {
                        extractedText.append(((Text) textNode).getText()).append("\n");
                    }
                }
            }
        }

        // Copiar al portapapeles
        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent content = new ClipboardContent();
        content.putString(extractedText.toString());
        clipboard.setContent(content);

        System.out.println("✅ Todos los textos han sido copiados al portapapeles!");
    }

}
