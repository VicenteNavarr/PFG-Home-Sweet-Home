package homeSweetHome.controller.event;

import homeSweetHome.dataPersistence.CurrentSession;
import homeSweetHome.dataPersistence.EventDAO;
import homeSweetHome.model.Event;
import homeSweetHome.utils.AlertUtils;
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

/**
 * Controlador para la vista de ítem de evento.
 */
public class EventItemViewController implements Initializable {

    @FXML
    private BorderPane borderPaneContainer;
    @FXML
    private Button btnDelete;
    //@FXML
    //private Button btnComplete;
    @FXML
    private TextArea fieldEventDescription;

    private int eventId;

    private EventViewController eventViewController;
    @FXML
    private Label lblEventName;
    @FXML
    private Label lblDate;
    @FXML
    private Label lblTime;
    @FXML
    private Button btnOpenUpdateEvent;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Configuración inicial si es necesaria
    }

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

            // Obtén el controlador asociado a la vista de actualización
            UpdateEventViewController updateEventController = loader.getController();

            // Configura la referencia al controlador principal
            if (eventViewController != null) {
                updateEventController.setEventViewController(eventViewController);
            }

            // Validar y convertir tiempo
            String timeText = lblTime.getText();
            Time timeValue = null;

            if (timeText != null && !timeText.isEmpty() && timeText.matches("\\d{2}:\\d{2}:\\d{2}")) {
                timeValue = Time.valueOf(timeText);
            } else if (timeText != null && !timeText.isEmpty() && timeText.matches("\\d{2}:\\d{2}")) {
                timeValue = Time.valueOf(timeText + ":00");
            } else {
                System.err.println("El formato de tiempo no es válido: " + timeText);
            }

            // Crear un objeto Event con los datos actuales
            Event currentEvent = new Event(
                    eventId, // ID del evento
                    lblEventName.getText(), // Nombre del evento
                    fieldEventDescription.getText(), // Descripción del evento
                    lblDate.getText() != null ? Date.valueOf(lblDate.getText()) : null, // Fecha del evento
                    timeValue, // Hora del evento
                    false, // Estado del recordatorio enviado (puedes ajustar si es necesario)
                    CurrentSession.getInstance().getUserGroupId() // ID del grupo del usuario actual
            );

            // Pasar el objeto Event al controlador de la vista de actualización
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
            e.printStackTrace(); // Ayuda a depurar errores
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
}
