package homeSweetHome.controller.event;

import homeSweetHome.dataPersistence.EventDAO;
import homeSweetHome.model.Event;
import homeSweetHome.utils.AlertUtils;
import homeSweetHome.utils.LanguageManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.net.URL;
import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.scene.control.Label;

/**
 * Controlador para la vista de actualización de eventos.
 */
public class UpdateEventViewController implements Initializable {

    @FXML
    private Button btnCancelEvent;
    @FXML
    private Button btnUpdateEvent;
    @FXML
    private TextField fieldEventName;
    @FXML
    private DatePicker fieldEventDate;
    @FXML
    private TextArea fieldEventDescription;
    @FXML
    private ComboBox<String> hourPicker;
    @FXML
    private ComboBox<String> minutePicker;

    private EventViewController eventViewController;

    private Event currentEvent; // Evento actual a ser actualizado
    @FXML
    private Label updateEventTitle;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

/////////////////////////////////IDIOMAS/////////////////////////////////////////////

        // Registra este controlador como listener del LanguageManager
        LanguageManager.getInstance().addListener(() -> Platform.runLater(this::updateTexts));
        updateTexts(); // Actualiza los textos inicialmente

/////////////////////////////////FIN IDIOMAS/////////////////////////////////////////////    

        // Rellena las opciones del ComboBox para las horas (00-23)
        for (int i = 0; i < 24; i++) {
            hourPicker.getItems().add(String.format("%02d", i));
        }

        // Rellena las opciones del ComboBox para los minutos (00-59)
        for (int i = 0; i < 60; i += 5) { // Intervalos de 5 minutos
            minutePicker.getItems().add(String.format("%02d", i));
        }
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

        // Configuración de los botones
        btnCancelEvent.setText(languageManager.getTranslation("cancelEvent"));
        btnUpdateEvent.setText(languageManager.getTranslation("updateEvent"));

        updateEventTitle.setText(languageManager.getTranslation("updateEventTitle"));

        // Configuración de los campos de texto
        fieldEventName.setPromptText(languageManager.getTranslation("promptEventName"));
        fieldEventDescription.setPromptText(languageManager.getTranslation("promptEventDescription"));

        // Configuración del DatePicker
        fieldEventDate.setPromptText(languageManager.getTranslation("promptEventDate"));

        // Configuración de los ComboBox: hora y minutos
        hourPicker.setPromptText(languageManager.getTranslation("promptHour"));
        minutePicker.setPromptText(languageManager.getTranslation("promptMinutes"));

        // Depuración
        System.out.println("Traducciones aplicadas correctamente en CreateEventViewController.");
    }

/////////////////////////////////FIN IDIOMAS/////////////////////////////////////////////    
    
    /**
     * Establece la referencia al controlador principal.
     *
     * @param eventViewController - Controlador principal
     */
    public void setEventViewController(EventViewController eventViewController) {

        this.eventViewController = eventViewController;
    }

    /**
     * Método para recibir los datos del evento y configurarlos en la vista.
     *
     * @param event - Objeto Event que contiene los datos del evento.
     */
    public void setEventData(Event event) {

        this.currentEvent = event; // Guarda el evento actual para actualizarlo

        // Asigna los valores a los campos de entrada
        fieldEventName.setText(event.getNombreEvento());
        fieldEventDescription.setText(event.getDescripcion());

        // Configura la fecha en el DatePicker
        if (event.getFechaEvento() != null) {

            fieldEventDate.setValue(event.getFechaEvento().toLocalDate());
        }

        // Configura la hora en los ComboBox
        if (event.getHoraEvento() != null) {

            String[] timeParts = event.getHoraEvento().toString().split(":");
            hourPicker.setValue(timeParts[0]);
            minutePicker.setValue(timeParts[1]);
        }
    }

    /**
     * Método para cancelar y cerrar la ventana.
     *
     * @param event - Evento del botón
     */
    @FXML
    private void cancel(ActionEvent event) {

        ((Stage) btnCancelEvent.getScene().getWindow()).close();
    }

    /**
     * Actualiza los datos del evento en la base de datos.
     *
     * @param event - Evento activado al presionar el botón "Actualizar Evento".
     */
    @FXML
    private void updateEvent(ActionEvent event) {

        try {

            // Recupera valores de los campos de entrada
            String nombreEvento = fieldEventName.getText().trim();
            String descripcion = fieldEventDescription.getText().trim();
            LocalDate fechaEvento = fieldEventDate.getValue();

            // Valida que los campos no estén vacíos
            if (nombreEvento.isEmpty() || descripcion.isEmpty() || fechaEvento == null) {

                if (LanguageManager.getInstance().getLanguageCode().equals("es")) {

                    AlertUtils.showAlert(Alert.AlertType.WARNING, "Campos incompletos", "Por favor completa todos los campos, incluida la fecha del evento.");

                } else if (LanguageManager.getInstance().getLanguageCode().equals("en")) {

                    AlertUtils.showAlert(Alert.AlertType.WARNING, "Incomplete Fields", "Please complete all fields, including the event date.");

                }

                return;
            }

            // Valida la hora seleccionada
            String selectedHour = hourPicker.getSelectionModel().getSelectedItem();
            String selectedMinute = minutePicker.getSelectionModel().getSelectedItem();

            if (selectedHour == null || selectedMinute == null) {

                if (LanguageManager.getInstance().getLanguageCode().equals("es")) {

                    AlertUtils.showAlert(Alert.AlertType.WARNING, "Hora incompleta", "Por favor selecciona tanto la hora como los minutos.");

                } else if (LanguageManager.getInstance().getLanguageCode().equals("en")) {

                    AlertUtils.showAlert(Alert.AlertType.WARNING, "Incomplete Time", "Please select both the hour and the minutes.");

                }

                return;
            }

            // Construye la hora en formato Time
            Time horaEvento = Time.valueOf(selectedHour + ":" + selectedMinute + ":00");

            // Crea el evento actualizado
            Event updatedEvent = new Event(
                    currentEvent.getId(), // Mantiene el ID del evento actual
                    nombreEvento, // Nombre actualizado
                    descripcion, // Descripción actualizada
                    Date.valueOf(fechaEvento), // Fecha convertida a java.sql.Date
                    horaEvento, // Hora actualizada
                    currentEvent.isRecordatorioEnviado(), // Estado del recordatorio
                    currentEvent.getIdGrupo() // ID del grupo asociado
            );

            // Actualiza el evento en la base de datos
            EventDAO eventDAO = new EventDAO();
            boolean success = eventDAO.updateEvent(updatedEvent);

            if (success) {

                if (LanguageManager.getInstance().getLanguageCode().equals("es")) {

                    // Muestra un mensaje de éxito
                    AlertUtils.showAlert(Alert.AlertType.INFORMATION, "Éxito", "El evento se ha actualizado correctamente.");

                } else if (LanguageManager.getInstance().getLanguageCode().equals("en")) {

                    // Muestra un mensaje de éxito
                    AlertUtils.showAlert(Alert.AlertType.INFORMATION, "Success", "The event has been updated successfully.");
                }

                // Refresca la lista de eventos en el controlador principal
                if (eventViewController != null) {

                    eventViewController.loadEvents();
                }

                // Cierra la ventana actual
                ((Stage) btnUpdateEvent.getScene().getWindow()).close();

            } else {

                AlertUtils.showAlert(Alert.AlertType.ERROR, "Error", "Ocurrió un problema al intentar actualizar el evento.");
            }

        } catch (Exception e) {

            // Maneja cualquier excepción inesperada
            e.printStackTrace();
            AlertUtils.showAlert(Alert.AlertType.ERROR, "Error", "Se produjo un error al procesar la actualización.");
        }
    }
}
