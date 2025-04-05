package homeSweetHome.dataPersistence;

import homeSweetHome.model.Event;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EventDAO {

    private Connection connection;

    // Constructor para inicializar la conexión
    public EventDAO() {
        try {
            this.connection = MySQLConnection.getConnection(); // Usar tu clase de conexión
        } catch (SQLException e) {
            System.err.println("Error al conectar a la base de datos: " + e.getMessage());
        }
    }

    /**
     * Agrega un nuevo evento a la base de datos.
     *
     * @param event - Objeto Event que se desea agregar.
     * @return boolean - Indica si la operación fue exitosa.
     */
    public boolean addEvent(Event event) {
        String query = "INSERT INTO Eventos (nombre_evento, descripcion, fecha_evento, hora_evento, recordatorio_enviado, id_grupo) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, event.getNombreEvento());
            statement.setString(2, event.getDescripcion());
            statement.setDate(3, event.getFechaEvento());
            statement.setTime(4, event.getHoraEvento());
            statement.setBoolean(5, event.isRecordatorioEnviado());
            statement.setInt(6, event.getIdGrupo());

            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al agregar el evento: " + e.getMessage());
            return false;
        }
    }

    /**
     * Obtiene todos los eventos de un grupo específico.
     *
     * @param idGrupo - ID del grupo cuyos eventos se desean obtener.
     * @return List<Event> - Lista de eventos del grupo.
     */
    public List<Event> getEventsByGroup(int idGrupo) {
        List<Event> events = new ArrayList<>();
        String query = "SELECT * FROM Eventos WHERE id_grupo = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, idGrupo);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Event event = new Event(
                        resultSet.getInt("id"),
                        resultSet.getString("nombre_evento"),
                        resultSet.getString("descripcion"),
                        resultSet.getDate("fecha_evento"),
                        resultSet.getTime("hora_evento"),
                        resultSet.getBoolean("recordatorio_enviado"),
                        resultSet.getInt("id_grupo")
                );
                events.add(event);
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener los eventos: " + e.getMessage());
        }
        return events;
    }

    /**
     * Actualiza los datos de un evento existente.
     *
     * @param event - Objeto Event con los datos actualizados.
     * @return boolean - Indica si la operación fue exitosa.
     */
    public boolean updateEvent(Event event) {
        String query = "UPDATE Eventos SET nombre_evento = ?, descripcion = ?, fecha_evento = ?, hora_evento = ?, recordatorio_enviado = ? WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, event.getNombreEvento());
            statement.setString(2, event.getDescripcion());
            statement.setDate(3, event.getFechaEvento());
            statement.setTime(4, event.getHoraEvento());
            statement.setBoolean(5, event.isRecordatorioEnviado());
            statement.setInt(6, event.getId());

            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al actualizar el evento: " + e.getMessage());
            return false;
        }
    }

    /**
     * Elimina un evento de la base de datos por su ID.
     *
     * @param eventId - ID del evento que se desea eliminar.
     * @return boolean - Indica si la operación fue exitosa.
     */
    public boolean deleteEvent(int eventId) {
        String query = "DELETE FROM Eventos WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, eventId);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al eliminar el evento: " + e.getMessage());
            return false;
        }
    }

    /**
     * Cierra la conexión a la base de datos.
     */
    public void closeConnection() {
        MySQLConnection.closeConnection(connection); // Usar tu método de cierre de conexión
    }
}
