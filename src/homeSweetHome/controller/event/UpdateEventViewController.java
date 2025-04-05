package homeSweetHome.controller.event;

import homeSweetHome.dataPersistence.EventDAO;
import homeSweetHome.model.Event;
import homeSweetHome.utils.AlertUtils;
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

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Rellenar las opciones del ComboBox para las horas (00-23)
        for (int i = 0; i < 24; i++) {
            hourPicker.getItems().add(String.format("%02d", i));
        }

        // Rellenar las opciones del ComboBox para los minutos (00-59)
        for (int i = 0; i < 60; i += 5) { // Intervalos de 5 minutos
            minutePicker.getItems().add(String.format("%02d", i));
        }
    }

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
            // Recuperar valores de los campos de entrada
            String nombreEvento = fieldEventName.getText().trim();
            String descripcion = fieldEventDescription.getText().trim();
            LocalDate fechaEvento = fieldEventDate.getValue();

            // Validar que los campos no estén vacíos
            if (nombreEvento.isEmpty() || descripcion.isEmpty() || fechaEvento == null) {
                AlertUtils.showAlert(Alert.AlertType.WARNING, "Campos incompletos", "Por favor completa todos los campos, incluida la fecha del evento.");
                return;
            }

            // Validar la hora seleccionada
            String selectedHour = hourPicker.getSelectionModel().getSelectedItem();
            String selectedMinute = minutePicker.getSelectionModel().getSelectedItem();
            if (selectedHour == null || selectedMinute == null) {
                AlertUtils.showAlert(Alert.AlertType.WARNING, "Hora incompleta", "Por favor selecciona tanto la hora como los minutos.");
                return;
            }

            // Construir la hora en formato Time
            Time horaEvento = Time.valueOf(selectedHour + ":" + selectedMinute + ":00");

            // Crear el evento actualizado
            Event updatedEvent = new Event(
                    currentEvent.getId(), // Mantén el ID del evento actual
                    nombreEvento, // Nombre actualizado
                    descripcion, // Descripción actualizada
                    Date.valueOf(fechaEvento), // Fecha convertida a java.sql.Date
                    horaEvento, // Hora actualizada
                    currentEvent.isRecordatorioEnviado(), // Estado del recordatorio
                    currentEvent.getIdGrupo() // ID del grupo asociado
            );

            // Actualizar el evento en la base de datos
            EventDAO eventDAO = new EventDAO();
            boolean success = eventDAO.updateEvent(updatedEvent);

            if (success) {
                // Mostrar un mensaje de éxito
                AlertUtils.showAlert(Alert.AlertType.INFORMATION, "Éxito", "El evento se ha actualizado correctamente.");

                // Refrescar la lista de eventos en el controlador principal
                if (eventViewController != null) {
                    eventViewController.loadEvents();
                }

                // Cerrar la ventana actual
                ((Stage) btnUpdateEvent.getScene().getWindow()).close();
            } else {
                AlertUtils.showAlert(Alert.AlertType.ERROR, "Error", "Ocurrió un problema al intentar actualizar el evento.");
            }
        } catch (Exception e) {
            // Manejar cualquier excepción inesperada
            e.printStackTrace();
            AlertUtils.showAlert(Alert.AlertType.ERROR, "Error", "Se produjo un error al procesar la actualización.");
        }
    }
}
