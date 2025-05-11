package homeSweetHome.controller.task;

import homeSweetHome.dataPersistence.CurrentSession;
import homeSweetHome.dataPersistence.TaskDAO;
import homeSweetHome.dataPersistence.UserDAO;
import homeSweetHome.model.Task;
import homeSweetHome.model.User;
import homeSweetHome.utils.AlertUtils;
import homeSweetHome.utils.LanguageManager;
import java.net.URL;
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
import java.time.ZoneId;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;

/**
 * FXML Controller class for the Update Task View.
 */
public class UpdateTaskViewController implements Initializable {

    @FXML
    private TextField fieldTaskName;
    @FXML
    private ComboBox<String> cmbUsuario;
    @FXML
    private DatePicker fieldDateLimit;
    @FXML
    private TextArea fieldTaskDescription;
    @FXML
    private Button btnUpdateTask;
    @FXML
    private Button btnCancelTask;
    @FXML
    private Label updateTaskTitle;

    private TaskViewController taskViewController; // Referencia al controlador principal

    private Map<String, Integer> userMap = new HashMap<>(); // Map para usuarios: nombre -> ID

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

        int groupId = CurrentSession.getInstance().getUserGroupId(); // ID del grupo actual
        UserDAO userDAO = new UserDAO();
        List<User> usuarios = userDAO.getUsersByGroup(groupId);

        for (User usuario : usuarios) {
            String nombre = usuario.getNombre();
            cmbUsuario.getItems().add(nombre); // Agrega nombres al ComboBox
            userMap.put(nombre, usuario.getId()); // Mapea nombre con ID
        }
    }

/////////////////////////////////IDIOMAS/////////////////////////////////////////////
    
    /**
     * Actualiza los textos de la interfaz en función del idioma.
     */
    private void updateTexts() {

        // Obtiene la instancia única del Singleton
        LanguageManager languageManager = LanguageManager.getInstance();

        if (languageManager == null) {

            System.err.println("Error: LanguageManager es nulo. Traducción no aplicada.");
            return;
        }

        // Verificación del idioma activo
        String idiomaActivo = languageManager.getLanguageCode();
        System.out.println("Idioma activo en updateTexts(): " + idiomaActivo);

        // Traducción de botones
        btnUpdateTask.setText(languageManager.getTranslation("updateTask"));
        btnCancelTask.setText(languageManager.getTranslation("cancel"));

        updateTaskTitle.setText(languageManager.getTranslation("updateTaskTitle"));

        System.out.println("Botón 'updateTask': " + btnUpdateTask.getText());
        System.out.println("Botón 'cancel': " + btnCancelTask.getText());

        // Traducción de campos de texto (`promptText`)
        fieldTaskName.setPromptText(languageManager.getTranslation("promptTaskName"));
        fieldTaskDescription.setPromptText(languageManager.getTranslation("promptTaskDescription"));

        System.out.println("Etiqueta 'promptTaskName': " + fieldTaskName.getPromptText());
        System.out.println("Etiqueta 'promptTaskDescription': " + fieldTaskDescription.getPromptText());

        // Traducción del `DatePicker`
        fieldDateLimit.setPromptText(languageManager.getTranslation("promptDateLimit"));

        System.out.println("Fecha límite 'promptDateLimit': " + fieldDateLimit.getPromptText());

        // Traducción del `ComboBox` de usuarios
        cmbUsuario.setPromptText(languageManager.getTranslation("promptUserSelection"));

        System.out.println("ComboBox de usuarios 'promptUserSelection': " + cmbUsuario.getPromptText());

        // Refresca UI para aplicar los cambios visualmente
        Platform.runLater(() -> fieldTaskName.getScene().getWindow().sizeToScene());

        System.out.println("Traducciones aplicadas correctamente en UpdateTaskViewController.");
    }

/////////////////////////////////FIN IDIOMAS/////////////////////////////////////////////   
    
    /**
     * Método para establecer la referencia al TaskViewController principal.
     *
     * @param taskViewController - Controlador principal
     */
    public void setTaskViewController(TaskViewController taskViewController) {

        this.taskViewController = taskViewController;
    }

    /**
     * Método que recibe los datos de la tarea y los setea en la vista (viene de
     * TaskItemViewController).
     *
     * @param task - Objeto Task que contiene los datos de la tarea
     */
    public void setTaskData(Task task) {

        System.out.println("ID recibido en setTaskData: " + task.getId());

        // Asigna los valores a los campos de entrada
        fieldTaskName.setText(task.getNombreTarea());
        fieldTaskDescription.setText(task.getDescripcion());

        // Configura la fecha límite en el DatePicker
        if (task.getFechaLimite() != null) {

            // Verifica si es una instancia de java.sql.Date
            if (task.getFechaLimite() instanceof java.sql.Date) {

                fieldDateLimit.setValue(((java.sql.Date) task.getFechaLimite()).toLocalDate());

            } else {

                // Si es java.util.Date u otra, conveierte correctamente
                fieldDateLimit.setValue(task.getFechaLimite().toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate());
            }

        } else {

            fieldDateLimit.setValue(null); // En caso de que no haya fecha límite, se deja vacío
        }

        // Selecciona el usuario asignado en el ComboBox
        if (task.getAsignadoAId() != 0) {

            for (String usuario : cmbUsuario.getItems()) {
                if (getUserNameById(task.getAsignadoAId()).equals(usuario)) {
                    cmbUsuario.getSelectionModel().select(usuario);
                    break;
                }
            }

        } else {

            cmbUsuario.getSelectionModel().clearSelection(); // Si no hay usuario asignado
        }
    }

    /**
     * Método auxiliar para obtener el nombre de un usuario dado su ID.
     *
     * @param id - ID del usuario
     * @return String - Nombre del usuario o "Usuario desconocido" si no se
     * encuentra
     */
    private String getUserNameById(int id) {

        for (Map.Entry<String, Integer> entry : userMap.entrySet()) {

            if (entry.getValue().equals(id)) {

                return entry.getKey(); // Retorna el nombre del usuario
            }
        }

        return "Usuario desconocido"; // Retorna un valor predeterminado si no se encuentra
    }

    /**
     * Método para cancelar y cerrar la ventana.
     *
     * @param event - Evento del botón
     */
    @FXML
    private void cancel(ActionEvent event) {

        ((Stage) btnCancelTask.getScene().getWindow()).close();
    }

    /**
     * Actualiza los datos de una tarea seleccionada en la base de datos.
     *
     * @param event - Evento activado al presionar el botón "Actualizar Tarea".
     */
    @FXML
    private void updateTaskTask(ActionEvent event) {

        try {

            // Recupera valores de los campos de entrada
            String nombreTarea = fieldTaskName.getText().trim(); // Nombre de la tarea
            String descripcion = fieldTaskDescription.getText().trim(); // Descripción de la tarea
            String usuarioSeleccionado = cmbUsuario.getSelectionModel().getSelectedItem(); // Usuario asignado
            java.sql.Date fechaLimite = fieldDateLimit.getValue() != null
                    ? java.sql.Date.valueOf(fieldDateLimit.getValue())
                    : null; // Fecha límite seleccionada

            // Traza: Verifica los datos recuperados de la interfaz
            System.out.println("Datos recuperados de la interfaz:");
            System.out.println("Nombre de la tarea: " + nombreTarea);
            System.out.println("Descripción: " + descripcion);
            System.out.println("Usuario seleccionado: " + usuarioSeleccionado);
            System.out.println("Fecha límite: " + fechaLimite);

            // Valida que no haya campos vacíos
            if (nombreTarea.isEmpty() || descripcion.isEmpty() || usuarioSeleccionado == null || fechaLimite == null) {

                if (LanguageManager.getInstance().getLanguageCode().equals("es")) {

                    AlertUtils.showAlert(Alert.AlertType.WARNING, "Campos incompletos", "Por favor completa todos los campos, incluida la fecha límite.");

                } else if (LanguageManager.getInstance().getLanguageCode().equals("en")) {

                    AlertUtils.showAlert(Alert.AlertType.WARNING, "Incomplete Fields", "Please complete all fields, including the deadline.");

                }

                return;
            }

            // Valida el usuario seleccionado
            int asignadoAId = userMap.getOrDefault(usuarioSeleccionado, -1);

            if (asignadoAId == -1) {

                if (LanguageManager.getInstance().getLanguageCode().equals("es")) {

                    AlertUtils.showAlert(Alert.AlertType.ERROR, "Error", "No se encontró el usuario seleccionado.");

                } else if (LanguageManager.getInstance().getLanguageCode().equals("en")) {

                    AlertUtils.showAlert(Alert.AlertType.ERROR, "Error", "The selected user was not found.");

                }

                return;
            }

            // Traza: Verifica el ID del usuario asignado
            System.out.println("ID del usuario asignado: " + asignadoAId);

            // Crea la tarea actualizada con los datos recopilados
            Task tareaActualizada = new Task(
                    CurrentSession.getInstance().getCurrentTaskId(), // ID de la tarea actual
                    nombreTarea, // Nombre de la tarea
                    descripcion, // Descripción
                    fechaLimite, // Fecha límite
                    asignadoAId, // ID del usuario asignado
                    "Pendiente", // Estado actualizado
                    CurrentSession.getInstance().getUserGroupId() // ID del grupo
            );

            // Traza: Verifica los datos antes de enviarlos al DAO
            System.out.println("Datos enviados al DAO:");
            System.out.println("ID de la tarea: " + tareaActualizada.getId());
            System.out.println("Nombre de la tarea: " + tareaActualizada.getNombreTarea());
            System.out.println("Descripción: " + tareaActualizada.getDescripcion());
            System.out.println("Fecha límite: " + tareaActualizada.getFechaLimite());
            System.out.println("ID del usuario asignado: " + tareaActualizada.getAsignadoAId());
            System.out.println("Estado: " + tareaActualizada.getEstado());
            System.out.println("ID del grupo: " + tareaActualizada.getIdGrupo());

            // Actualiza la tarea en la base de datos
            TaskDAO taskDAO = new TaskDAO();
            boolean success = taskDAO.updateTask(tareaActualizada);

            if (success) {

                if (LanguageManager.getInstance().getLanguageCode().equals("es")) {

                    // Muestra un mensaje de éxito
                    AlertUtils.showAlert(Alert.AlertType.INFORMATION, "Éxito", "La tarea se ha actualizado correctamente.");

                } else if (LanguageManager.getInstance().getLanguageCode().equals("en")) {

                    // Muestra un mensaje de éxito
                    AlertUtils.showAlert(Alert.AlertType.INFORMATION, "Success", "The task has been updated successfully.");

                }

                // Refresca la lista de tareas en el controlador principal
                if (taskViewController != null) {

                    taskViewController.loadTasks();
                }

                // Cierra la ventana actual
                ((Stage) btnUpdateTask.getScene().getWindow()).close();

            } else {

                // Muestra un mensaje de error si la actualización falla
                AlertUtils.showAlert(Alert.AlertType.ERROR, "Error", "Ocurrió un problema al intentar actualizar la tarea.");
            }

        } catch (Exception e) {

            // Maneja cualquier excepción no prevista
            e.printStackTrace();
            AlertUtils.showAlert(Alert.AlertType.ERROR, "Error", "Se produjo un error al procesar la actualización.");
        }
    }

}
