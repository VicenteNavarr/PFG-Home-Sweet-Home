package homeSweetHome.controller.task;

import homeSweetHome.dataPersistence.CurrentSession;
import homeSweetHome.dataPersistence.TaskDAO;
import homeSweetHome.model.Task;
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
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ButtonType;
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
    @FXML
    private Label lbNombreTarea;
    @FXML
    private Label lblQuien;
    @FXML
    private Label lblFechaLimite;

    private int taskId;

    private TaskViewController taskViewController;

    int role = CurrentSession.getInstance().getUserRole(); // Tomamos rol para control de permisos

    /**
     * Inizializador controlador
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        //Si el usuario tiene rol consultor, desactivamos botones
        if (role == 2) {

            btnDelete.setDisable(true);
            btnComplete.setDisable(true);
            btnOpenUpdateTask.setDisable(true);

        }

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

        // Obtiene la instancia única del Singleton
        LanguageManager languageManager = LanguageManager.getInstance();

        if (languageManager == null) {

            System.err.println("Error: LanguageManager es nulo. Traducción no aplicada.");
            return;
        }

        // Verificación del idioma activo
        String idiomaActivo = languageManager.getLanguageCode();
        System.out.println("Idioma activo en updateTexts(): " + idiomaActivo);

        // Configurar textos de los botones
        btnOpenUpdateTask.setText(languageManager.getTranslation("updateTask"));
        btnDelete.setText(languageManager.getTranslation("deleteTask"));
        btnComplete.setText(languageManager.getTranslation("completeTask"));

        System.out.println("Botón 'updateTask': " + btnOpenUpdateTask.getText());
        System.out.println("Botón 'deleteTask': " + btnDelete.getText());
        System.out.println("Botón 'completeTask': " + btnComplete.getText());

        // Configurar etiquetas dinámicas (corrigiendo las que deben traducirse)
        lbNombreTarea.setText(languageManager.getTranslation("taskNameLabel"));
        lblQuien.setText(languageManager.getTranslation("taskAssignedToLabel"));
        lblFechaLimite.setText(languageManager.getTranslation("taskDueDateLabel"));

        System.out.println("Etiqueta 'taskNameLabel': " + lbNombreTarea.getText());
        System.out.println("Etiqueta 'taskAssignedToLabel': " + lblQuien.getText());
        System.out.println("Etiqueta 'taskDueDateLabel': " + lblFechaLimite.getText());

        // Configurar `TextArea` de descripción de la tarea con `promptText`
        txtDescription.setPromptText(languageManager.getTranslation("promptTaskDescription"));

        System.out.println("Descripción 'promptTaskDescription': " + txtDescription.getPromptText());

        // Refrescar UI para aplicar los cambios visualmente
        //Platform.runLater(() -> lbNombreTarea.getScene().getWindow().sizeToScene());
        Platform.runLater(() -> {
            if (lbNombreTarea.getScene() != null && lbNombreTarea.getScene().getWindow() != null) {
                lbNombreTarea.getScene().getWindow().sizeToScene();
            }
        });

        System.out.println("Traducciones aplicadas correctamente en TaskItemViewController.");
    }

/////////////////////////////////FIN IDIOMAS/////////////////////////////////////////////   
    
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
            stage.setResizable(false);
            stage.setScene(new Scene(root));
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(btnOpenUpdateTask.getScene().getWindow());
            stage.showAndWait(); // Muestra la ventana y espera a que se cierre.

        } catch (IOException e) {

            System.err.println("Error al cargar la vista UpdateTaskView: " + e.getMessage());
        }
    }

    /**
     * Elimina una tarea.
     *
     * @param event - Evento activado al presionar el botón "Eliminar".
     */
    @FXML
    private void deleteTask(ActionEvent event) {
        
        // Crea la alerta de confirmación sin mensaje inicial
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "");

        // Aplica la hoja de estilos
        alert.getDialogPane().getStylesheets().add(getClass().getResource("/homeSweetHome/utils/alertsCss.css").toExternalForm());

        // Define mensaje según idioma
        String message = LanguageManager.getInstance().getLanguageCode().equals("es")
                ? "¿Seguro que desea eliminar esta tarea?"
                : "Are you sure you want to delete this task?";

        String title = LanguageManager.getInstance().getLanguageCode().equals("es")
                ? "Confirmación"
                : "Confirmation";

        String header = LanguageManager.getInstance().getLanguageCode().equals("es")
                ? "Eliminar tarea"
                : "Delete Task";

        alert.setContentText(message);
        alert.setTitle(title);
        alert.setHeaderText(header);

        // Captura la respuesta del usuario
        Optional<ButtonType> result = alert.showAndWait();
        
        if (result.isPresent() && result.get() == ButtonType.OK) {
            
            // Instancia del DAO para gestionar la eliminación en la base de datos
            TaskDAO taskDAO = new TaskDAO();

            // Intenta eliminar la tarea usando su ID
            boolean success = taskDAO.deleteTask(taskId);

            if (success) {
                
                // Elimina visualmente la tarea del contenedor
                taskViewController.getTaskContainer().getChildren().remove(borderPaneContainer);

                String deleteSuccessTitle = LanguageManager.getInstance().getLanguageCode().equals("es")
                        ? "Tarea Eliminada"
                        : "Task Deleted";

                String deleteSuccessMessage = LanguageManager.getInstance().getLanguageCode().equals("es")
                        ? "La tarea se eliminó correctamente."
                        : "The task was successfully deleted.";

                AlertUtils.showAlert(Alert.AlertType.INFORMATION, deleteSuccessTitle, deleteSuccessMessage);
                
            } else {
                
                String errorTitle = LanguageManager.getInstance().getLanguageCode().equals("es")
                        ? "Error"
                        : "Error";

                String errorMessage = LanguageManager.getInstance().getLanguageCode().equals("es")
                        ? "No se pudo eliminar la tarea."
                        : "The task could not be deleted.";

                AlertUtils.showAlert(Alert.AlertType.ERROR, errorTitle, errorMessage);
            }
            
        } else {
            
            System.out.println(LanguageManager.getInstance().getLanguageCode().equals("es")
                    ? "Eliminación cancelada por el usuario."
                    : "Deletion canceled by user.");
        }
    }

    /**
     * Marca una tarea como completada y la mueve a la tabla de histórico en la
     * base de datos. Luego actualiza la interfaz gráfica eliminando la tarea de
     * la vista actual.
     *
     * @param event Evento activado al presionar el botón "Completar Tarea".
     */
    @FXML
    private void completeTask(ActionEvent event) {

        TaskDAO taskDAO = new TaskDAO();
        int groupId = CurrentSession.getInstance().getUserGroupId();

        boolean success = taskDAO.moveTaskToHistory(taskId, groupId);

        if (success) {

            // Elimina visualmente la tarea completada de la vista actual
            taskViewController.getTaskContainer().getChildren().remove(borderPaneContainer);
            if (LanguageManager.getInstance().getLanguageCode().equals("es")) {

                // Muestra un mensaje de éxito
                AlertUtils.showAlert(Alert.AlertType.INFORMATION, "Tarea Completada", "La tarea ha sido movida al historial.");

            } else if (LanguageManager.getInstance().getLanguageCode().equals("en")) {

                // Muestra un mensaje de éxito
                AlertUtils.showAlert(Alert.AlertType.INFORMATION, "Task Completed", "The task has been moved to the history.");

            }

        } else {

            // Muestra un mensaje de error si algo falla
            AlertUtils.showAlert(Alert.AlertType.ERROR, "Error", "No se pudo completar la tarea. Puede que no pertenezca a tu grupo.");
        }
    }

}
