package homeSweetHome.controller.task;

import homeSweetHome.dataPersistence.CurrentSession;
import homeSweetHome.dataPersistence.TaskDAO;
import homeSweetHome.model.Task;
import homeSweetHome.utils.LanguageManager;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * FXML Controller class
 */
public class TaskViewController implements Initializable {

    @FXML
    private ScrollPane scrollPane; // Contenedor con scroll
    @FXML
    private VBox taskContainer; // Contenedor de las tareas dinámicas
    @FXML
    private Button btnOpenCreateNewTask;
    @FXML
    private Label tasksTitle;

    int role = CurrentSession.getInstance().getUserRole(); // Tomamos rol para control de permisos

    /**
     * Inicializa la vista de tareas y configura los elementos necesarios.
     *
     * Ajusta el ancho del ScrollPane, verifica que el contenedor de tareas esté
     * inicializado, y carga las tareas en la interfaz al comenzar.
     *
     * @param url la ubicación utilizada para resolver recursos relacionados
     * @param rb el paquete de recursos utilizado para localizar elementos de la
     * interfaz
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        //Si el usuario tiene rol consultor, desactivamos botones
        if (role == 2) {

            btnOpenCreateNewTask.setDisable(true);

        }

/////////////////////////////////IDIOMAS/////////////////////////////////////////////

        // Registra este controlador como listener del LanguageManager
        LanguageManager.getInstance().addListener(() -> Platform.runLater(this::updateTexts));
        updateTexts(); // Actualiza los textos inicialmente

/////////////////////////////////FIN IDIOMAS///////////////////////////////////////////// 

        // Ajusta el ScrollPane para que el VBox se ajuste al ancho del ScrollPane
        //scrollPane.setFitToWidth(true);
//        if (taskContainer == null) {
//            System.err.println("Error: taskContainer no está inicializado. Verifica el archivo TaskView.fxml.");
//            return;
//        }
        loadTasks(); // Carga las tareas al inicializar la vista
        taskContainer.translateXProperty().bind(
                scrollPane.widthProperty().subtract(taskContainer.widthProperty()).divide(2)
        );

    }

/////////////////////////////////IDIOMAS/////////////////////////////////////////////
    
    /**
     * Actualiza los textos de la interfaz en función del idioma.
     */
    private void updateTexts() {

        LanguageManager languageManager = LanguageManager.getInstance();

        if (languageManager == null) {
            System.err.println("Error: LanguageManager no está disponible.");
            return;
        }

        // Actualización del texto del botón
        btnOpenCreateNewTask.setText(languageManager.getTranslation("createTask"));

        tasksTitle.setText(languageManager.getTranslation("tasksTitle"));

        // Actualización dinámica del contenido del ScrollPane si aplica
        scrollPane.setContent(taskContainer); // Manteniene el contenedor dinámico

        // Depuración
        System.out.println("Traducciones aplicadas correctamente en EventViewController.");
    }

    
/////////////////////////////////FIN IDIOMAS/////////////////////////////////////////////  
    /**
     * Abre la ventana para crear una nueva tarea.
     *
     * @param event - Evento activado al presionar el botón "Nueva Tarea".
     */
    @FXML
    private void openCreateNewTask(ActionEvent event) {

        try {

            // Carga la vista CreateTaskView desde su archivo FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/homeSweetHome/view/task/CreateTaskView.fxml"));
            Parent root = loader.load();

            // Obtiene el controlador de la vista creada
            CreateTaskViewController createTaskController = loader.getController();

            // Pasa la referencia del TaskViewController actual al controlador de creación
            createTaskController.setTaskViewController(this);

            // Crea una nueva ventana para la vista de creación
            Stage stage = new Stage();
            stage.setTitle("Crear Nueva Tarea");
            stage.setResizable(false);
            stage.getIcons().add(new Image(getClass().getResourceAsStream("/images/taskIconBlue.png")));
            stage.setScene(new Scene(root));
            stage.initModality(Modality.WINDOW_MODAL); // Establece la ventana como modal
            stage.initOwner(btnOpenCreateNewTask.getScene().getWindow()); // Asocia la ventana actual como propietaria
            stage.showAndWait(); // Muestra la ventana y espera a que se cierre

        } catch (IOException e) {

            // Muestra un error si ocurre un problema al cargar la vista
            System.err.println("Error al cargar la vista CreateTaskView: " + e.getMessage());
        }
    }

    /**
     * Carga y muestra todas las tareas del grupo actual en la vista.
     */
    public void loadTasks() {

        TaskDAO taskDAO = new TaskDAO();

        // Obtiene el ID del grupo del usuario actual desde la sesión
        int userGroupId = homeSweetHome.dataPersistence.CurrentSession.getInstance().getUserGroupId();

        // Obtiene la lista de tareas asociadas al grupo
        List<Task> tasks = taskDAO.getTasksByGroup(userGroupId);

        // Limpia el contenedor antes de cargar nuevas tareas
        taskContainer.getChildren().clear();

        // Itera sobre las tareas para cargarlas en la vista
        for (Task task : tasks) {

            try {

                // Carga la vista del ítem de tarea desde el archivo FXML
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/homeSweetHome/view/task/TaskItemView.fxml"));
                Node taskItemNode = loader.load();

                // Obtiene el controlador asociado a la vista y asigna los datos de la tarea
                TaskItemViewController controller = loader.getController();
                controller.setTaskData(
                        task.getId(),
                        task.getNombreTarea(),
                        task.getAsignadoANombre(),
                        task.getFechaLimite() != null ? task.getFechaLimite().toString() : "Sin Fecha",
                        task.getDescripcion()
                );

                // Pasa la referencia del controlador principal al controlador del ítem
                controller.setTaskViewController(this);

                // Agrega la tarea al contenedor visual
                taskContainer.getChildren().add(taskItemNode);

            } catch (IOException e) {

                // Muestra un error si no se puede cargar la vista del ítem de tarea
                System.err.println("No se pudo cargar la vista TaskItemView para la tarea: " + task.getNombreTarea());
                e.printStackTrace();
            }
        }
    }

    public VBox getTaskContainer() {

        return taskContainer;
    }
}
