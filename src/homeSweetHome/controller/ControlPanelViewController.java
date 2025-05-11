package homeSweetHome.controller;

import homeSweetHome.dataPersistence.CurrentSession;
import homeSweetHome.dataPersistence.MySQLConnection;
import homeSweetHome.model.Event;
import homeSweetHome.utils.ImageUtils;
import homeSweetHome.model.User;
import java.net.URL;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import homeSweetHome.dataPersistence.EventDAO;
import homeSweetHome.dataPersistence.TaskDAO;
import homeSweetHome.model.Task;
import homeSweetHome.utils.LanguageManager;
import javafx.application.Platform;
import javafx.scene.control.Label;

public class ControlPanelViewController implements Initializable {

    @FXML
    private TableView<User> usersTable;
    @FXML
    private TableColumn<User, Integer> colUserId;
    @FXML
    private TableColumn<User, String> colUserName;
    @FXML
    private TableColumn<User, String> colUserApellidos;
    @FXML
    private TableColumn<User, String> colUserEmail;
    @FXML
    private TableColumn<User, String> colUserRol;
    @FXML
    private TableColumn<User, ImageView> colUserFoto;
    @FXML
    private TableView<Event> eventHistoryTable;
    @FXML
    private TableColumn<Event, String> colEventName;
    @FXML
    private TableColumn<Event, String> colEventDescription;
    @FXML
    private TableColumn<Event, String> colEventDate;
    @FXML
    private TableColumn<Event, String> colEventTime;
    @FXML
    private TableView<Task> taskHistoryTable;
    @FXML
    private TableColumn<Task, String> colTaskName;
    @FXML
    private TableColumn<Task, String> colTaskDescription;
    @FXML
    private TableColumn<Task, String> colTaskDeadline;
    @FXML
    private TableColumn<Task, String> colTaskAssignedTo;
    @FXML
    private Label controlPanelTitle;
    @FXML
    private Label lblEventHistory;
    @FXML
    private Label lblTaskHistory;
       
    private final EventDAO eventDAO = new EventDAO();
    
    private final TaskDAO taskDAO = new TaskDAO();

    /**
     * Métodos Inicio
     * @param url
     * @param rb 
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        int groupId = CurrentSession.getInstance().getUserGroupId();
        System.out.println("Grupo de usuario actual: " + groupId); //depuración

/////////////////////////////////IDIOMAS/////////////////////////////////////////////

        // Registra este controlador como listener del LanguageManager
        LanguageManager.getInstance().addListener(() -> Platform.runLater(this::updateTexts));
        updateTexts(); // Actualiza los textos inicialmente

/////////////////////////////////FIN IDIOMAS/////////////////////////////////////////////   

        //Carga de tablas
        setupTable();
        loadUsers();
        setupEventTable();
        loadHistoricalEvents();
        setupTaskTable();
        loadHistoricalTasks();
    }

/////////////////////////////////IDIOMAS/////////////////////////////////////////////
    

    /**
     * Actualiza los textos de la interfaz en función del idioma.
     */
    private void updateTexts() {
        LanguageManager languageManager = LanguageManager.getInstance();

        if (languageManager == null) {
            
            System.err.println("LanguageManager no está disponible.");
            return;
        }

        // Actualiza títulos y botones
        controlPanelTitle.setText(languageManager.getTranslation("controlPanelTitle"));
        lblEventHistory.setText(languageManager.getTranslation("lblEventHistory"));
        lblTaskHistory.setText(languageManager.getTranslation("lblTaskHistory"));

        // Actualiza encabezados de la tabla de usuarios
        colUserId.setText(languageManager.getTranslation("userId"));
        colUserName.setText(languageManager.getTranslation("userName"));
        colUserApellidos.setText(languageManager.getTranslation("userSurname"));
        colUserEmail.setText(languageManager.getTranslation("userEmail"));
        colUserRol.setText(languageManager.getTranslation("userRole"));

        // Actualiza encabezados de la tabla de eventos
        colEventName.setText(languageManager.getTranslation("eventName"));
        colEventDescription.setText(languageManager.getTranslation("eventDescription"));
        colEventDate.setText(languageManager.getTranslation("eventDate"));
        colEventTime.setText(languageManager.getTranslation("eventTime"));

        // Actualiza encabezados de la tabla de tareas históricas
        colTaskName.setText(languageManager.getTranslation("taskName"));
        colTaskDescription.setText(languageManager.getTranslation("taskDescription"));
        colTaskDeadline.setText(languageManager.getTranslation("taskDeadline"));
        colTaskAssignedTo.setText(languageManager.getTranslation("taskAssignedTo"));

        System.out.println("Textos de la interfaz actualizados al idioma seleccionado.");
    }

/////////////////////////////////FIN IDIOMAS/////////////////////////////////////////////  
    
    /**
     * Configuración tabla usuario
     */
    private void setupTable() {
        
        colUserId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colUserName.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colUserApellidos.setCellValueFactory(new PropertyValueFactory<>("apellidos"));
        colUserEmail.setCellValueFactory(new PropertyValueFactory<>("correoElectronico"));
        //colUserRol.setCellValueFactory(new PropertyValueFactory<>("idRol"));

        // Configuración de la columna de rol para mostrar "Administrador" y "Consultor"
        colUserRol.setCellValueFactory(cellData -> {
            
            int rol = cellData.getValue().getIdRol();
            String rolNombre = switch (rol) {
                case 1 ->
                    "Administrador";
                case 2 ->
                    "Consultor";
                default ->
                    "Desconocido"; // Manejo para otros roles
            };
            
            return new javafx.beans.property.SimpleStringProperty(rolNombre);
        });

        // Configuración especial para la columna de fotos
        colUserFoto.setCellValueFactory(cellData -> {
            
            Blob blob = cellData.getValue().getFotoPerfil();
            ImageView imageView = new ImageView();

            if (blob != null) {
                
                try {
                    
                    InputStream inputStream = blob.getBinaryStream();
                    Image image = new Image(inputStream);
                    imageView.setImage(image);
                    ImageUtils.setClipToCircle(imageView); // Aplica efecto circular
                    
                } catch (SQLException e) {
                    
                    System.err.println("Error al convertir Blob a Image: " + e.getMessage());
                }
            }

            return new javafx.beans.property.SimpleObjectProperty<>(imageView);
        });
    }

    /**
     * Carga usuarios
     */
    private void loadUsers() {
        
        int groupId = CurrentSession.getInstance().getUserGroupId(); // Obtener el grupo actual
        ObservableList<User> usersData = FXCollections.observableArrayList(getUsersByGroup(groupId));
        usersTable.setItems(usersData);
    }

    /**
     * Toma los usuarios por id de grupo
     * @param groupId
     * @return 
     */
    private List<User> getUsersByGroup(int groupId) {
        
        List<User> users = new ArrayList<>();
        String sql = "SELECT u.id, u.nombre, u.apellidos, u.correo_electronico, u.id_rol, u.foto_perfil FROM Usuarios u WHERE u.id_grupo = ?";

        try (Connection conn = MySQLConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, groupId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setNombre(rs.getString("nombre"));
                user.setApellidos(rs.getString("apellidos"));
                user.setCorreoElectronico(rs.getString("correo_electronico"));
                user.setIdRol(rs.getInt("id_rol"));
                user.setFotoPerfil(rs.getBlob("foto_perfil")); // Se mantiene como Blob

                users.add(user);
            }

        } catch (SQLException e) {
            
            System.err.println("Error al obtener usuarios por grupo: " + e.getMessage());
        }

        return users;
    }

    /**
     * Configuración tabla eventos
     */
    private void setupEventTable() {
        
        colEventName.setCellValueFactory(new PropertyValueFactory<>("nombreEvento"));
        colEventDescription.setCellValueFactory(new PropertyValueFactory<>("descripcion"));
        colEventDate.setCellValueFactory(new PropertyValueFactory<>("fechaEvento"));
        colEventTime.setCellValueFactory(new PropertyValueFactory<>("horaEvento"));
    }

    /**
     * Carga tabla eventos
     */
    private void loadHistoricalEvents() {
        
        List<Event> eventsList = eventDAO.getHistoricalEvents();
        ObservableList<Event> historicalEventsData = FXCollections.observableArrayList(eventsList);
        eventHistoryTable.setItems(historicalEventsData);

        System.out.println("Se han asignado " + historicalEventsData.size() + " eventos a la tabla.");
    }

    /**
     * Configuración tabla tareas
     */
    private void setupTaskTable() {
        
        colTaskName.setCellValueFactory(new PropertyValueFactory<>("nombreTarea"));
        colTaskName.setCellValueFactory(new PropertyValueFactory<>("nombreTarea"));
        colTaskDescription.setCellValueFactory(new PropertyValueFactory<>("descripcion"));
        colTaskDeadline.setCellValueFactory(new PropertyValueFactory<>("fechaLimite"));
        colTaskAssignedTo.setCellValueFactory(new PropertyValueFactory<>("asignadoANombre"));

    }

    /**
     * Carga tabla tareas
     */
    private void loadHistoricalTasks() {
        
        List<Task> tasksList = taskDAO.getHistoricalTasks();
        ObservableList<Task> historicalTasksData = FXCollections.observableArrayList(tasksList);
        taskHistoryTable.setItems(historicalTasksData);

        System.out.println("Se han asignado " + historicalTasksData.size() + " tareas al historial.");
    }

}
