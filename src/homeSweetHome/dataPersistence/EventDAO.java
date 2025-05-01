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
     * Marca un evento como completado en la base de datos por su ID.
     *
     * @param eventId ID del evento que se desea completar.
     * @param groupId ID del grupo del usuario actual, para asegurar que el
     * evento pertenece al grupo correcto.
     * @return true si la operación fue exitosa, false si ocurrió algún error.
     * @throws IllegalArgumentException si el ID del evento o del grupo no son
     * válidos.
     */
    public boolean markEventAsCompleted(int eventId, int groupId) {
        if (eventId <= 0 || groupId <= 0) {
            throw new IllegalArgumentException("El ID del evento o del grupo no son válidos.");
        }

        String query = "UPDATE Eventos SET evento_completado = ? WHERE id = ? AND id_grupo = ?";

        try (Connection connection = MySQLConnection.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setBoolean(1, true); // Marca como completado
            preparedStatement.setInt(2, eventId); // ID del evento
            preparedStatement.setInt(3, groupId); // Valida el grupo

            int rowsUpdated = preparedStatement.executeUpdate();
            return rowsUpdated > 0; // Retorna true si se actualizó al menos una fila

        } catch (SQLException e) {
            e.printStackTrace();
            return false; // Retorna false en caso de error
        }
    }

    /**
     * Mueve un evento desde la tabla principal "Eventos" a la tabla
     * "EventosHistorico", marcándolo como completado. Primero inserta los datos
     * del evento en la tabla de histórico, y luego elimina el evento de la
     * tabla original.
     *
     * @param eventId ID del evento que se desea mover al historial.
     * @param groupId ID del grupo del usuario actual, para asegurar que el
     * evento pertenece al grupo correcto.
     * @return true si el evento fue movido exitosamente al historial, false si
     * ocurrió algún error.
     */
    public boolean moveEventToHistory(int eventId, int groupId) {
        String insertQuery = "INSERT INTO EventosHistorico (id, nombre_evento, descripcion, fecha_evento, hora_evento, recordatorio_enviado, id_grupo, fecha_completado) "
                + "SELECT id, nombre_evento, descripcion, fecha_evento, hora_evento, recordatorio_enviado, id_grupo, NOW() "
                + "FROM Eventos WHERE id = ? AND id_grupo = ?";
        String deleteQuery = "DELETE FROM Eventos WHERE id = ? AND id_grupo = ?";

        try (Connection connection = MySQLConnection.getConnection(); PreparedStatement insertStmt = connection.prepareStatement(insertQuery); PreparedStatement deleteStmt = connection.prepareStatement(deleteQuery)) {

            // Inserta el evento en la tabla de histórico
            insertStmt.setInt(1, eventId);
            insertStmt.setInt(2, groupId);
            int rowsInserted = insertStmt.executeUpdate();

            if (rowsInserted > 0) {
                // Elimina el evento de la tabla original
                deleteStmt.setInt(1, eventId);
                deleteStmt.setInt(2, groupId);
                int rowsDeleted = deleteStmt.executeUpdate();
                return rowsDeleted > 0;
            }

        } catch (SQLException e) {
            System.err.println("Error al mover el evento al historial: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Obtiene todos los eventos históricos asociados al grupo del usuario
     * actual.
     *
     * @return Lista de eventos completados del grupo del usuario actual.
     * @throws IllegalStateException si el ID del grupo en la sesión no es
     * válido.
     */
    public List<Event> getHistoricalEvents() {
        int groupId = CurrentSession.getInstance().getUserGroupId();

        if (groupId <= 0) {
            throw new IllegalStateException("El ID del grupo actual en la sesión no es válido: " + groupId);
        }

        String query = "SELECT * FROM EventosHistorico WHERE id_grupo = ?";
        List<Event> historicalEvents = new ArrayList<>();

        try (Connection connection = MySQLConnection.getConnection(); PreparedStatement stmt = connection.prepareStatement(query)) {

            stmt.setInt(1, groupId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Event event = new Event(
                        rs.getInt("id"),
                        rs.getString("nombre_evento"),
                        rs.getString("descripcion"),
                        rs.getDate("fecha_evento"),
                        rs.getTime("hora_evento"),
                        rs.getBoolean("recordatorio_enviado"),
                        rs.getInt("id_grupo")
                );
                historicalEvents.add(event);
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener eventos históricos: " + e.getMessage());
            e.printStackTrace(); // Ayuda a depurar si ocurre un error
        }

        return historicalEvents;
    }

    /**
     * Cierra la conexión a la base de datos.
     */
    public void closeConnection() {
        MySQLConnection.closeConnection(connection); // Usar tu método de cierre de conexión
    }
}
