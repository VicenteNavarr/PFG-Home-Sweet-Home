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
import java.time.ZoneId;

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
    private TextField fieldTaskDescription;
    @FXML
    private Button btnUpdateTask;
    @FXML
    private Button btnCancelTask;

    private TaskViewController taskViewController; // Referencia al controlador principal

    private Map<String, Integer> userMap = new HashMap<>(); // Map para usuarios: nombre -> ID

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
                AlertUtils.showAlert(Alert.AlertType.WARNING, "Campos incompletos", "Por favor completa todos los campos, incluida la fecha límite.");
                return;
            }

            // Valida el usuario seleccionado
            int asignadoAId = userMap.getOrDefault(usuarioSeleccionado, -1);
            if (asignadoAId == -1) {
                AlertUtils.showAlert(Alert.AlertType.ERROR, "Error", "No se encontró el usuario seleccionado.");
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
                // Muestra un mensaje de éxito
                AlertUtils.showAlert(Alert.AlertType.INFORMATION, "Éxito", "La tarea se ha actualizado correctamente.");

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
