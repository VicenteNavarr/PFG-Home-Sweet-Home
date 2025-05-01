package homeSweetHome.controller.event;

import homeSweetHome.dataPersistence.CurrentSession;
import homeSweetHome.dataPersistence.EventDAO;
import homeSweetHome.dataPersistence.UserDAO;
import homeSweetHome.model.Event;
import homeSweetHome.model.User;
import homeSweetHome.utils.AlertUtils;
import homeSweetHome.utils.LanguageManager;
import homeSweetHome.utils.TimeUtils;
import java.net.URL;
import java.sql.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.sql.Time;
import java.time.LocalDate;
import javafx.application.Platform;

/**
 * Controlador de la vista para la creación de eventos.
 */
public class CreateEventViewController implements Initializable {

    @FXML
    private Button btnCancelEvent; // Botón para cancelar la creación del evento
    @FXML
    private TextField fieldEventName; // Campo de texto para el nombre del evento
    @FXML
    private DatePicker fieldEventDate; // Selector de fecha para el evento
    @FXML
    private TextField fieldEventDescription; // Campo de texto para la descripción del evento
    @FXML
    private Button btnCreateEvent; // Botón para crear el evento
    @FXML
    private TextField fieldEventTime;
    @FXML
    private ComboBox<String> hourPicker; // Selector de horas
    @FXML
    private ComboBox<String> minutePicker; // Selector de minutos

    //private Map<String, Integer> userMap = new HashMap<>(); // Map para usuarios: nombre -> ID
    private EventViewController eventViewController; // Referencia al controlador principal

    /**
     * Método para inicializar el controlador.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

/////////////////////////////////IDIOMAS/////////////////////////////////////////////
        // Registra este controlador como listener del LanguageManager
        LanguageManager.getInstance().addListener(() -> Platform.runLater(this::updateTexts));
        updateTexts(); // Actualiza los textos inicialmente

/////////////////////////////////FIN IDIOMAS/////////////////////////////////////////////        
        int groupId = CurrentSession.getInstance().getUserGroupId(); // Obtiene el ID del grupo actual
        UserDAO userDAO = new UserDAO();
        List<User> usuarios = userDAO.getUsersByGroup(groupId); // Recupera los usuarios del grupo

        // Rellena las opciones del ComboBox para las horas (00-23)
        for (int i = 0; i < 24; i++) {
            hourPicker.getItems().add(String.format("%02d", i));
        }

        // Rellena las opciones del ComboBox para los minutos (00-59)
        for (int i = 0; i < 60; i += 5) { // Intervalo de 5 minutos
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
        btnCreateEvent.setText(languageManager.getTranslation("createEvent")); 

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
     * Método para establecer la referencia al EventViewController principal.
     *
     * @param eventViewController - Controlador principal
     */
    public void setEventViewController(EventViewController eventViewController) {

        this.eventViewController = eventViewController;
    }

    /**
     * Crea un nuevo evento y lo guarda en la base de datos.
     *
     * @param event - Evento activado al presionar el botón "Crear Evento".
     */
    @FXML
    private void createNewEvent(ActionEvent event) {

        try {

            // Recupera los valores de los campos de entrada
            String nombreEvento = fieldEventName.getText().trim();
            String descripcion = fieldEventDescription.getText().trim();
            java.time.LocalDate fechaActual = java.time.LocalDate.now(); // Fecha actual del sistema
            java.time.LocalTime horaActual = java.time.LocalTime.now(); // Hora actual del sistema

            Date fechaEvento = fieldEventDate.getValue() != null
                    ? Date.valueOf(fieldEventDate.getValue())
                    : null;

            // Valida que los campos no estén vacíos
            if (nombreEvento.isEmpty() || descripcion.isEmpty() || fechaEvento == null) {
                
                AlertUtils.showAlert(Alert.AlertType.WARNING, "Campos incompletos", "Por favor completa todos los campos, incluida la fecha y hora del evento.");
                return;
            }

            // Valida que la fecha no sea anterior a la fecha actual
            if (fieldEventDate.getValue().isBefore(fechaActual)) {
                
                AlertUtils.showAlert(Alert.AlertType.WARNING, "Fecha no válida", "La fecha del evento no puede ser anterior a la fecha actual.");
                return;
            }

            // Recupera la hora seleccionada del TimePicker
            String selectedHour = hourPicker.getSelectionModel().getSelectedItem();
            String selectedMinute = minutePicker.getSelectionModel().getSelectedItem();

            // Valida que se haya seleccionado la hora completa
            if (selectedHour == null || selectedMinute == null) {
                
                AlertUtils.showAlert(Alert.AlertType.WARNING, "Hora incompleta", "Por favor selecciona tanto la hora como los minutos.");
                return;
            }

            // Convierte la hora usando el utilitario
            Time horaEvento;

            try {

                horaEvento = TimeUtils.parseTime(selectedHour, selectedMinute);

            } catch (IllegalArgumentException e) {

                AlertUtils.showAlert(Alert.AlertType.WARNING, "Hora inválida", e.getMessage());
                return;
            }

            // Si la fecha del evento es hoy, validar que la hora no sea anterior o igual a la hora actual
            if (fieldEventDate.getValue().isEqual(fechaActual)) {
                
                java.time.LocalTime horaSeleccionada = java.time.LocalTime.of(Integer.parseInt(selectedHour), Integer.parseInt(selectedMinute));
                if (!horaSeleccionada.isAfter(horaActual)) {
                    AlertUtils.showAlert(Alert.AlertType.WARNING, "Hora no válida", "Si la fecha del evento es hoy, la hora debe ser posterior a la hora actual.");
                    return;
                }
            }

            // Crea el nuevo evento
            Event nuevoEvento = new Event(
                    0, // ID (se genera automáticamente)
                    nombreEvento,
                    descripcion,
                    fechaEvento,
                    horaEvento, // Hora procesada
                    false, // Recordatorio enviado (por defecto falso)
                    CurrentSession.getInstance().getUserGroupId()
            );

            // Guarda el evento en la base de datos
            EventDAO eventDAO = new EventDAO();
            boolean success = eventDAO.addEvent(nuevoEvento);

            if (success) {

                AlertUtils.showAlert(Alert.AlertType.INFORMATION, "Éxito", "El evento se ha creado correctamente.");
                
                if (eventViewController != null) {
                    
                    eventViewController.loadEvents();
                }

                ((Stage) btnCreateEvent.getScene().getWindow()).close();

            } else {

                AlertUtils.showAlert(Alert.AlertType.ERROR, "Error", "Hubo un problema al intentar crear el evento.");
            }

        } catch (Exception e) {
            
            e.printStackTrace();
            AlertUtils.showAlert(Alert.AlertType.ERROR, "Error", "Se produjo un error al procesar el evento.");
        }
    }

    /**
     * Método para cancelar y cerrar la ventana.
     *
     * @param event - Evento del botón
     */
    @FXML
    private void cancel(ActionEvent event) {

        ((Stage) btnCancelEvent.getScene().getWindow()).close(); // Cierra la ventana actual
    }
}
