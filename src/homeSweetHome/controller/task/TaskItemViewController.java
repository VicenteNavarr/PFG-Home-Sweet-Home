package homeSweetHome.controller.task;

import homeSweetHome.dataPersistence.CurrentSession;
import homeSweetHome.dataPersistence.TaskDAO;
import homeSweetHome.model.Task;
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

/**
 * FXML Controller class for TaskItemView
 */
public class TaskItemViewController implements Initializable {

    @FXML
    private BorderPane borderPaneContainer;
    @FXML
    private Button btnOpenUpdateTask;
    @FXML
    private Button btnDelete;
    @FXML
    private Button btnComplete;
    @FXML
    private Label lblTaskName;
    @FXML
    private Label lblAssignedTo;
    @FXML
    private Label lblDueDate;
    @FXML
    private TextArea txtDescription;

    private int taskId;

    private TaskViewController taskViewController;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Configuración inicial si es necesaria
    }

    /**
     * Configura los datos de una tarea en los elementos de la vista.
     *
     * @param id - ID de la tarea.
     * @param nombre - Nombre de la tarea.
     * @param asignadoA - Nombre de la persona asignada.
     * @param fechaLimite - Fecha límite de la tarea.
     * @param descripcion - Descripción de la tarea.
     */
    public void setTaskData(int id, String nombre, String asignadoA, String fechaLimite, String descripcion) {
        this.taskId = id;
        lblTaskName.setText(nombre); // Muestra el nombre de la tarea.
        lblAssignedTo.setText(asignadoA); // Muestra el usuario asignado.
        lblDueDate.setText(fechaLimite); // Muestra la fecha límite.
        txtDescription.setText(descripcion); // Muestra la descripción.
    }

    /**
     * Establece el controlador principal de la vista de tareas.
     *
     * @param taskViewController - Referencia al controlador principal.
     */
    public void setTaskViewController(TaskViewController taskViewController) {
        this.taskViewController = taskViewController; // Guarda la referencia al controlador principal.
    }

    /**
     * Abre la ventana para actualizar una tarea.
     *
     * @param event - Evento activado al presionar el botón "Actualizar Tarea".
     */
    @FXML
    private void openUpdateTask(ActionEvent event) {
        try {
            TaskDAO taskDAO = new TaskDAO();
            // Recupera los datos completos de la tarea por su ID.
            Task updatedTask = taskDAO.getTaskById(taskId);

            if (updatedTask == null) {
                AlertUtils.showAlert(Alert.AlertType.ERROR, "Error", "La tarea no se pudo cargar.");
                return;
            }

            // Establece el ID de la tarea actual en la sesión.
            CurrentSession.getInstance().setCurrentTaskId(taskId);

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/homeSweetHome/view/task/UpdateTaskView.fxml"));
            Parent root = loader.load();

            // Obtiene el controlador asociado a la vista de actualización.
            UpdateTaskViewController updateTaskController = loader.getController();

            // Enlaza el controlador principal y los datos de la tarea.
            if (taskViewController != null) {
                updateTaskController.setTaskViewController(taskViewController);
            }
            updateTaskController.setTaskData(updatedTask);

            // Crea y configura una nueva ventana para la vista de actualización.
            Stage stage = new Stage();
            stage.setTitle("Actualizar Tarea");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(btnOpenUpdateTask.getScene().getWindow());
            stage.showAndWait(); // Muestra la ventana y espera a que se cierre.
        } catch (IOException e) {
            System.err.println("Error al cargar la vista UpdateTaskView: " + e.getMessage());
        }
    }

    @FXML
    private void markAsCompleted(ActionEvent event) {
    }

    /**
     * Elimina una tarea.
     *
     * @param event - Evento activado al presionar el botón "Eliminar".
     */
    @FXML
    private void deleteTask(ActionEvent event) {
        // Instancia del DAO para gestionar la eliminación en la base de datos.
        TaskDAO taskDAO = new TaskDAO();

        // Intenta eliminar la tarea usando su ID.
        boolean success = taskDAO.deleteTask(taskId);

        if (success) {
            // Elimina visualmente la tarea del contenedor.
            taskViewController.getTaskContainer().getChildren().remove(borderPaneContainer);

            // Muestra un mensaje de éxito.
            AlertUtils.showAlert(Alert.AlertType.INFORMATION, "Tarea Eliminada", "La tarea se eliminó correctamente.");
        } else {
            // Muestra un mensaje de error si algo falla.
            AlertUtils.showAlert(Alert.AlertType.ERROR, "Error", "No se pudo eliminar la tarea.");
        }
    }

}
