package homeSweetHome.controller.task;

import homeSweetHome.dataPersistence.CurrentSession;
import homeSweetHome.dataPersistence.TaskDAO;
import homeSweetHome.dataPersistence.UserDAO;
import homeSweetHome.model.Task;
import homeSweetHome.model.User;
import homeSweetHome.utils.AlertUtils;
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

/**
 * FXML Controller class for the Create Task View.
 */
public class CreateTaskViewController implements Initializable {

    private TextField fieldMail;
    @FXML
    private TextField fieldTaskName;
    @FXML
    private ComboBox<String> cmbUsuario;
    @FXML
    private Button btnCreateTask;
    @FXML
    private Button btnCancelTask;

    private TaskViewController taskViewController; // Referencia al controlador principal
    private Map<String, Integer> userMap = new HashMap<>(); // Map para usuarios: nombre -> ID
    @FXML
    private DatePicker fieldDateLimit;
    @FXML
    private TextField fieldTaskDescription;

    /**
     * Método para inicializar el controlador.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        int groupId = CurrentSession.getInstance().getUserGroupId(); // ID del grupo actual
        UserDAO userDAO = new UserDAO();
        List<User> usuarios = userDAO.getUsersByGroup(groupId);

        for (User usuario : usuarios) {
            String nombre = usuario.getNombre();
            cmbUsuario.getItems().add(nombre); // Agrega nombres al ComboBox
            userMap.put(nombre, usuario.getId()); // Mapea nombre con ID
        }
    }

    /**
     * Método para establecer la referencia al TaskViewController principal.
     *
     * @param taskViewController - Controlador principal
     */
    public void setTaskViewController(TaskViewController taskViewController) {
        this.taskViewController = taskViewController;
    }

    /**
     * Crea una nueva tarea y la guarda en la base de datos.
     *
     * @param event - Evento activado al presionar el botón "Crear Tarea".
     */
    @FXML
    private void createNewTask(ActionEvent event) {
        try {
            // Recupera los valores de los campos de entrada
            String nombreTarea = fieldTaskName.getText().trim(); // Nombre de la tarea
            String descripcion = fieldTaskDescription.getText().trim(); // Descripción de la tarea
            String usuarioSeleccionado = cmbUsuario.getSelectionModel().getSelectedItem(); // Usuario asignado
            java.sql.Date fechaLimite = fieldDateLimit.getValue() != null
                    ? java.sql.Date.valueOf(fieldDateLimit.getValue())
                    : null; // Fecha límite seleccionada

            // Valida que los campos no estén vacíos
            if (nombreTarea.isEmpty() || descripcion.isEmpty() || usuarioSeleccionado == null || fechaLimite == null) {
                AlertUtils.showAlert(Alert.AlertType.WARNING, "Campos incompletos", "Por favor completa todos los campos, incluida la fecha límite.");
                return;
            }

            // Valida el usuario seleccionado
            int asignadoAId = userMap.getOrDefault(usuarioSeleccionado, -1);
            if (asignadoAId == -1) {
                AlertUtils.showAlert(Alert.AlertType.ERROR, "Error", "No se encontró el usuario seleccionado.");
                return;
            }

            // Crea la nueva tarea con los datos recopilados
            Task nuevaTarea = new Task(
                    0, // ID (se generará automáticamente en la base de datos)
                    nombreTarea,
                    descripcion,
                    fechaLimite,
                    asignadoAId,
                    "Pendiente", // Estado inicial
                    CurrentSession.getInstance().getUserGroupId()
            );

            // Guarda la tarea en la base de datos
            TaskDAO taskDAO = new TaskDAO();
            boolean success = taskDAO.addTask(nuevaTarea);

            if (success) {
                // Muestra un mensaje de éxito
                AlertUtils.showAlert(Alert.AlertType.INFORMATION, "Éxito", "La tarea se ha creado correctamente.");

                // Refresca la lista de tareas en el controlador principal
                if (taskViewController != null) {
                    taskViewController.loadTasks();
                }

                // Cierra la ventana actual
                ((Stage) btnCreateTask.getScene().getWindow()).close();
            } else {
                AlertUtils.showAlert(Alert.AlertType.ERROR, "Error", "Ocurrió un problema al intentar crear la tarea.");
            }
        } catch (Exception e) {
            // Maneja cualquier excepción no prevista
            e.printStackTrace();
            AlertUtils.showAlert(Alert.AlertType.ERROR, "Error", "Se produjo un error al procesar la tarea.");
        }
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

}
