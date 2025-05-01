package homeSweetHome.controller.event;

import homeSweetHome.dataPersistence.CurrentSession;
import homeSweetHome.dataPersistence.EventDAO;
import homeSweetHome.model.Event;
import homeSweetHome.utils.AlertUtils;
import homeSweetHome.utils.LanguageManager;
import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import javafx.application.Platform;

/**
 * Controlador para la vista de ítem de evento.
 */
public class EventItemViewController implements Initializable {

    @FXML
    private BorderPane borderPaneContainer;
    @FXML
    private Button btnDelete;
    @FXML
    private TextArea fieldEventDescription;
    @FXML
    private Label lblEventName;
    @FXML
    private Label lblDate;
    @FXML
    private Label lblTime;
    @FXML
    private Label lblHora;
    @FXML
    private Label lblFecha;
    @FXML
    private Label lblNombreEvento;
    @FXML
    private Button btnOpenUpdateEvent;
    @FXML
    private Button btnComplete;

    private int eventId;

    private EventViewController eventViewController;

    //private LanguageManager languageManager;
    @Override
    public void initialize(URL url, ResourceBundle rb) {

/////////////////////////////////IDIOMAS/////////////////////////////////////////////

        // Registra este controlador como listener del LanguageManager
        LanguageManager.getInstance().addListener(() -> Platform.runLater(this::updateTexts));
        updateTexts(); // Actualiza los textos inicialmente

/////////////////////////////////FIN IDIOMAS/////////////////////////////////////////////
    }

/////////////////////////////////IDIOMAS/////////////////////////////////////////////
    
    /**
     * Actualiza los textos de la interfaz en función del idioma.
     */
    private void updateTexts() {
        
        // Accede directamente al Singleton del LanguageManager
        LanguageManager languageManager = LanguageManager.getInstance();

        if (languageManager == null) {
            System.err.println("Error: LanguageManager no está disponible.");
            return;
        }

        // Configurar textos de los botones
        btnDelete.setText(languageManager.getTranslation("deleteEvent")); 
        btnOpenUpdateEvent.setText(languageManager.getTranslation("updateEvent")); 
        btnComplete.setText(languageManager.getTranslation("completeEvent")); 

        // Configurar el PromptText de la descripción del evento
        fieldEventDescription.setPromptText(languageManager.getTranslation("promptEventDescription")); 

        // Configurar etiquetas dinámicas
        lblNombreEvento.setText(languageManager.getTranslation("eventNameLabel")); 
        lblFecha.setText(languageManager.getTranslation("eventDateLabel")); 
        lblHora.setText(languageManager.getTranslation("eventTimeLabel")); 
        fieldEventDescription.setPromptText(languageManager.getTranslation("promptEventDescription"));

        // Depuración
        System.out.println("Traducciones aplicadas correctamente en EventItemViewController.");
    }

/////////////////////////////////FIN IDIOMAS/////////////////////////////////////////////
    
    /**
     * Configura los datos de un evento en los elementos de la vista.
     *
     * @param id - ID del evento.
     * @param nombre - Nombre del evento.
     * @param fecha - Fecha del evento.
     * @param hora - Hora del evento.
     * @param descripcion - Descripción del evento.
     */
    public void setEventData(int id, String nombre, String fecha, String hora, String descripcion) {

        this.eventId = id;
        lblEventName.setText(nombre); // Muestra el nombre del evento.
        lblDate.setText(fecha); // Muestra la fecha del evento.
        lblTime.setText(hora); // Muestra la hora del evento.
        fieldEventDescription.setText(descripcion); // Muestra la descripción del evento.
    }

    /**
     * Establece el controlador principal de la vista de eventos.
     *
     * @param eventViewController - Referencia al controlador principal.
     */
    public void setEventViewController(EventViewController eventViewController) {

        this.eventViewController = eventViewController; // Guarda la referencia al controlador principal.
    }

    /**
     * Abre la ventana para actualizar un evento.
     *
     * @param event - Evento activado al presionar el botón "Modificar".
     */
    @FXML
    private void openUpdateEvent(ActionEvent event) {

        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/homeSweetHome/view/event/UpdateEventView.fxml"));
            Parent root = loader.load();

            // Obtiene el controlador asociado a la vista de actualización
            UpdateEventViewController updateEventController = loader.getController();

            // Configura la referencia al controlador principal
            if (eventViewController != null) {
                
                updateEventController.setEventViewController(eventViewController);
            }

            // Valida y convierte tiempo
            String timeText = lblTime.getText();
            Time timeValue = null;

            if (timeText != null && !timeText.isEmpty() && timeText.matches("\\d{2}:\\d{2}:\\d{2}")) {
                
                timeValue = Time.valueOf(timeText);
                
            } else if (timeText != null && !timeText.isEmpty() && timeText.matches("\\d{2}:\\d{2}")) {
                
                timeValue = Time.valueOf(timeText + ":00");
                
            } else {
                
                System.err.println("El formato de tiempo no es válido: " + timeText);
            }

            // Crea un objeto Event con los datos actuales
            Event currentEvent = new Event(
                    eventId, // ID del evento
                    lblEventName.getText(), // Nombre del evento
                    fieldEventDescription.getText(), // Descripción del evento
                    lblDate.getText() != null ? Date.valueOf(lblDate.getText()) : null, // Fecha del evento
                    timeValue, // Hora del evento
                    false, // Estado del recordatorio enviado (puedes ajustar si es necesario)
                    CurrentSession.getInstance().getUserGroupId() // ID del grupo del usuario actual
            );

            // Pasa el objeto Event al controlador de la vista de actualización
            updateEventController.setEventData(currentEvent);

            // Crea y configura una nueva ventana para la vista de actualización
            Stage stage = new Stage();
            stage.setTitle("Actualizar Evento");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(btnOpenUpdateEvent.getScene().getWindow());
            stage.showAndWait(); // Muestra la ventana y espera a que se cierre

        } catch (IOException e) {

            System.err.println("Error al cargar la vista UpdateEventView: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Elimina un evento.
     *
     * @param event - Evento activado al presionar el botón "Eliminar".
     */
    @FXML
    private void deleteEvent(ActionEvent event) {
        
        EventDAO eventDAO = new EventDAO();
        boolean success = eventDAO.deleteEvent(eventId);

        if (success) {
            
            // Elimina visualmente el evento del contenedor
            eventViewController.getEventContainer().getChildren().remove(borderPaneContainer);

            // Muestra un mensaje de éxito
            AlertUtils.showAlert(Alert.AlertType.INFORMATION, "Evento Eliminado", "El evento se eliminó correctamente.");
            
        } else {
            
            // Muestra un mensaje de error si algo falla
            AlertUtils.showAlert(Alert.AlertType.ERROR, "Error", "No se pudo eliminar el evento.");
        }
    }

    /**
     * Marca un evento como completado y lo mueve a la tabla de histórico en la
     * base de datos. Luego actualiza la interfaz gráfica eliminando el evento
     * de la vista actual.
     *
     * @param event Evento activado al presionar el botón "Completar Evento".
     */
    @FXML
    private void completeEvent(ActionEvent event) {
        
        EventDAO eventDAO = new EventDAO();
        int groupId = CurrentSession.getInstance().getUserGroupId();

        boolean success = eventDAO.moveEventToHistory(eventId, groupId);

        if (success) {

            // Elimina visualmente el evento completado de la vista actual
            eventViewController.getEventContainer().getChildren().remove(borderPaneContainer);

            // Muestra un mensaje de éxito
            AlertUtils.showAlert(Alert.AlertType.INFORMATION, "Evento Completado", "El evento ha sido movido al historial.");

        } else {

            // Muestra un mensaje de error si algo falla
            AlertUtils.showAlert(Alert.AlertType.ERROR, "Error", "No se pudo completar el evento. Puede que no pertenezca a tu grupo.");
        }
    }

}
