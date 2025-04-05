/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package homeSweetHome.controller.event;

import homeSweetHome.dataPersistence.EventDAO;
import homeSweetHome.model.Event;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author Usuario
 */
public class EventViewController implements Initializable {

    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox eventContainer; // Renombrar si es necesario
    @FXML
    private Button btnOpenCreateNewEvent;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Inicializa la vista cargando los eventos existentes
        loadEvents();
    }

    /**
     * Abre la ventana para crear un nuevo evento.
     *
     * @param event - Evento activado al presionar el botón "Nuevo evento".
     */
    @FXML
    private void openCreateNewEvent(ActionEvent event) {
        try {
            // Carga la vista CreateEventView desde el archivo FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/homeSweetHome/view/event/CreateEventView.fxml"));
            Parent root = loader.load();

            // Obtén el controlador de la nueva vista
            CreateEventViewController createEventController = loader.getController();

            // Pasa la referencia del controlador principal
            createEventController.setEventViewController(this);

            // Configura una nueva ventana para la vista de creación
            Stage stage = new Stage();
            stage.setTitle("Crear Nuevo Evento"); // Título de la ventana
            stage.setScene(new Scene(root)); // Configura la escena
            stage.initModality(Modality.WINDOW_MODAL); // Establece la ventana como modal
            stage.initOwner(btnOpenCreateNewEvent.getScene().getWindow()); // Asocia la ventana actual como propietaria
            stage.showAndWait(); // Muestra la ventana y espera a que se cierre
        } catch (IOException e) {
            // Registra un error en caso de problemas al cargar la vista
            System.err.println("Error al cargar la vista CreateEventView: " + e.getMessage());
        }
    }

    /**
     * Carga y muestra todos los eventos del grupo actual en la vista.
     */
    public void loadEvents() {
        EventDAO eventDAO = new EventDAO();

        // Obtiene el ID del grupo del usuario actual desde la sesión
        int userGroupId = homeSweetHome.dataPersistence.CurrentSession.getInstance().getUserGroupId();

        // Obtiene la lista de eventos asociados al grupo
        List<Event> events = eventDAO.getEventsByGroup(userGroupId);

        // Limpia el contenedor antes de cargar nuevos eventos
        eventContainer.getChildren().clear();

        // Itera sobre los eventos para cargarlos en la vista
        for (Event event : events) {
            try {
                // Carga la vista del ítem de evento desde el archivo FXML
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/homeSweetHome/view/event/EventItemView.fxml"));
                Node eventItemNode = loader.load();

                // Obtiene el controlador asociado a la vista y asigna los datos del evento
                EventItemViewController controller = loader.getController();
                controller.setEventData(
                        event.getId(),
                        event.getNombreEvento(),
                        event.getFechaEvento() != null ? event.getFechaEvento().toString() : "Sin Fecha",
                        event.getHoraEvento() != null ? event.getHoraEvento().toString() : "Sin Hora",
                        event.getDescripcion()
                );

                // Pasa la referencia del controlador principal al controlador del ítem
                controller.setEventViewController(this);

                // Agrega el evento al contenedor visual
                eventContainer.getChildren().add(eventItemNode);

            } catch (IOException e) {
                // Muestra un error si no se puede cargar la vista del ítem de evento
                System.err.println("No se pudo cargar la vista EventItemView para el evento: " + event.getNombreEvento());
                e.printStackTrace();
            }
        }
    }

    public VBox getEventContainer() {
        return eventContainer;
    }

}
