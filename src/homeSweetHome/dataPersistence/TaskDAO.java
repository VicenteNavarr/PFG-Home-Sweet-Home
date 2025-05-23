package homeSweetHome.dataPersistence;

import homeSweetHome.model.Task;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para la gestión de Tareas con soporte para Grupos (id_grupo).
 */
public class TaskDAO {

    /**
     * Método para añadir una nueva tarea y asociarla a un grupo.
     *
     * @param task - Tarea a registrar
     * @return boolean indicando si la operación fue exitosa
     */
    public boolean addTask(Task task) {
        
        // Log para depuración
        System.out.println("ID del grupo de la tarea: " + task.getIdGrupo());

        // Sentencia SQL para registrar una nueva tarea
        String sql = "INSERT INTO Tareas (nombre_tarea, descripcion, fecha_limite, asignado_a_id, estado, id_grupo) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = MySQLConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            // Asigna los datos de la tarea a los parámetros de la consulta
            pstmt.setString(1, task.getNombreTarea());
            pstmt.setString(2, task.getDescripcion());
            pstmt.setDate(3, task.getFechaLimite() != null ? new java.sql.Date(task.getFechaLimite().getTime()) : null);
            pstmt.setInt(4, task.getAsignadoAId());
            pstmt.setString(5, task.getEstado());
            pstmt.setInt(6, task.getIdGrupo());

            // Ejecuta la consulta y verificar el resultado
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            
            // Registra cualquier error que ocurra
            System.err.println("Error al registrar tarea: " + e.getMessage());
            return false;
        }
    }

    /**
     * Método para comprobar si existe una tarea con un nombre específico en la
     * base de datos.
     *
     * @param nombreTarea - Nombre de la tarea a buscar.
     * @return boolean - `true` si la tarea existe, `false` en caso contrario.
     */
    public static boolean taskExists(String nombreTarea) {
        
        // Consulta SQL para contar tareas con un nombre específico
        String sql = "SELECT COUNT(*) FROM Tareas WHERE nombre_tarea = ?";

        try (Connection conn = MySQLConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, nombreTarea); // Establece el nombre como parámetro
            ResultSet rs = pstmt.executeQuery(); // Ejecuta la consulta

            if (rs.next()) {
                
                return rs.getInt(1) > 0; // Retorna true si el conteo es mayor a 0
            }
            
        } catch (SQLException e) {
            
            // Registra el error en caso de fallo
            System.err.println("Error al comprobar si la tarea existe: " + e.getMessage());
        }

        return false; // Retorna false si ocurre un error o no se encuentra la tarea
    }

    /**
     * Método para obtener todas las tareas asociadas a un grupo específico.
     *
     * @param groupId - ID único del grupo.
     * @return List<Task> - Lista de tareas asociadas al grupo.
     */
    public List<Task> getTasksByGroup(int groupId) {
        
        // Lista para almacenar las tareas recuperadas
        List<Task> tasks = new ArrayList<>();

        // Consulta SQL para recuperar las tareas del grupo, incluyendo el nombre del usuario asignado
        String sql = """
    SELECT t.id, t.nombre_tarea, t.descripcion, t.fecha_limite, t.asignado_a_id, u.nombre AS asignado_a_nombre, t.estado
    FROM Tareas t
    LEFT JOIN Usuarios u ON t.asignado_a_id = u.id
    WHERE t.id_grupo = ?
    """;

        try (Connection conn = MySQLConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, groupId); // Establecer el ID del grupo como parámetro
            ResultSet rs = pstmt.executeQuery(); // Ejecutar la consulta

            // Itera sobre los resultados y llena la lista de tareas
            while (rs.next()) {
                
                Task task = new Task();
                task.setId(rs.getInt("id"));
                task.setNombreTarea(rs.getString("nombre_tarea"));
                task.setDescripcion(rs.getString("descripcion"));
                task.setFechaLimite(rs.getDate("fecha_limite"));
                task.setAsignadoAId(rs.getInt("asignado_a_id"));
                task.setAsignadoANombre(rs.getString("asignado_a_nombre"));
                task.setEstado(rs.getString("estado"));

                tasks.add(task);
            }
            
        } catch (SQLException e) {
            
            // Registra cualquier error durante la consulta
            System.err.println("Error al obtener tareas por grupo: " + e.getMessage());
        }

        // Retorna la lista de tareas (vacía si no se encontraron registros)
        return tasks;
    }

    /**
     * Método para eliminar una tarea por su ID.
     *
     * @param taskId - ID único de la tarea a eliminar
     * @return boolean - `true` si la tarea fue eliminada con éxito, `false` en
     * caso contrario
     */
    public boolean deleteTask(int taskId) {
        
        String sql = "DELETE FROM Tareas WHERE id = ?"; // Consulta SQL para eliminar la tarea

        try (Connection conn = MySQLConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, taskId); // Asigna el ID de la tarea al parámetro de la consulta

            int rowsAffected = pstmt.executeUpdate(); // Ejecutar la consulta
            return rowsAffected > 0; // Retorna `true` si se afectó al menos una fila
            
        } catch (SQLException e) {
            
            System.err.println("Error al eliminar la tarea con ID " + taskId + ": " + e.getMessage());
            return false; // Retorna `false` en caso de error
        }
    }

    /**
     * Método para actualizar una tarea en la base de datos.
     *
     * @param task - Objeto `Task` con los datos actualizados
     * @return boolean - `true` si la tarea fue actualizada con éxito, `false`
     * en caso contrario
     */
    public boolean updateTask(Task task) {
        
        String sqlUpdate = "UPDATE Tareas SET nombre_tarea = ?, descripcion = ?, fecha_limite = ?, asignado_a_id = ?, estado = ? WHERE id = ?";

        try (Connection conn = MySQLConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sqlUpdate)) {
            
            // Configura los valores de la actualización
            pstmt.setString(1, task.getNombreTarea()); // Nombre de la tarea
            pstmt.setString(2, task.getDescripcion()); // Descripción
            pstmt.setDate(3, task.getFechaLimite() != null ? new java.sql.Date(task.getFechaLimite().getTime()) : null); // Fecha límite
            pstmt.setInt(4, task.getAsignadoAId()); // ID del usuario asignado
            pstmt.setString(5, task.getEstado()); // Estado de la tarea (Pendiente, Completada, etc.)
            pstmt.setInt(6, task.getId()); // ID de la tarea

            // Ejecuta la consulta de actualización
            int rowsAffected = pstmt.executeUpdate();
            System.out.println("Filas afectadas en la actualización: " + rowsAffected);
            return rowsAffected > 0; // Retornar `true` si se actualizó al menos una fila
            
        } catch (SQLException e) {
            
            System.err.println("Error al actualizar la tarea con ID " + task.getId() + ": " + e.getMessage());
            return false; // Retorna `false` en caso de error
        }
    }

    /**
     * Método para obtener una tarea por su ID.
     *
     * @param taskId - ID único de la tarea
     * @return Task - Objeto `Task` con los datos de la tarea, o `null` si no se
     * encuentra
     */
    public Task getTaskById(int taskId) {
        
        String sql = "SELECT id, nombre_tarea, descripcion, fecha_limite, asignado_a_id, estado, id_grupo FROM Tareas WHERE id = ?";

        try (Connection conn = MySQLConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, taskId); // Asigna el ID de la tarea al parámetro

            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                
                // Recupera datos de la tarea desde el ResultSet
                int id = rs.getInt("id");
                String nombreTarea = rs.getString("nombre_tarea");
                String descripcion = rs.getString("descripcion");
                java.sql.Date fechaLimite = rs.getDate("fecha_limite");
                int asignadoAId = rs.getInt("asignado_a_id");
                String estado = rs.getString("estado");
                int idGrupo = rs.getInt("id_grupo");

                // Crea y retorna el objeto `Task`
                return new Task(id, nombreTarea, descripcion, fechaLimite, asignadoAId, estado, idGrupo);
            }

        } catch (SQLException e) {
            
            System.err.println("Error al obtener la tarea con ID " + taskId + ": " + e.getMessage());
        }

        return null; // Retorna null si la tarea no se encuentra o ocurre un error
    }

    /**
     * Mueve una tarea desde la tabla principal "Tareas" al histórico
     * "TareasHistorico". Primero actualiza el estado de la tarea a 'Completada'
     * en la tabla principal.
     *
     * @param tareaId ID de la tarea que se desea mover.
     * @param groupId ID del grupo del usuario actual, para asegurar que
     * pertenece al grupo correcto.
     * @return true si la tarea fue movida exitosamente, false si ocurrió algún
     * error.
     */
    public boolean moveTaskToHistory(int tareaId, int groupId) {
        
        // Consulta para actualizar el estado de la tarea a 'Completada'
        String updateStatusQuery = "UPDATE Tareas SET estado = 'Completada' WHERE id = ? AND id_grupo = ?";
        // Consulta para insertar la tarea en la tabla de histórico
        String insertQuery = "INSERT INTO TareasHistorico (id, nombre_tarea, descripcion, fecha_limite, asignado_a_id, id_grupo, fecha_completada) "
                + "SELECT id, nombre_tarea, descripcion, fecha_limite, asignado_a_id, id_grupo, NOW() "
                + "FROM Tareas WHERE id = ? AND id_grupo = ? AND estado = 'Completada'";
        // Consulta para eliminar la tarea de la tabla principal
        String deleteQuery = "DELETE FROM Tareas WHERE id = ? AND id_grupo = ? AND estado = 'Completada'";

        try (Connection connection = MySQLConnection.getConnection(); PreparedStatement updateStatusStmt = connection.prepareStatement(updateStatusQuery); PreparedStatement insertStmt = connection.prepareStatement(insertQuery); PreparedStatement deleteStmt = connection.prepareStatement(deleteQuery)) {

            // Actualiza el estado de la tarea a 'Completada'
            updateStatusStmt.setInt(1, tareaId);
            updateStatusStmt.setInt(2, groupId);
            int rowsUpdated = updateStatusStmt.executeUpdate();

            if (rowsUpdated > 0) {
                
                // Inserta la tarea en la tabla de histórico
                insertStmt.setInt(1, tareaId);
                insertStmt.setInt(2, groupId);
                int rowsInserted = insertStmt.executeUpdate();

                if (rowsInserted > 0) {
                    
                    // Elimina la tarea de la tabla principal
                    deleteStmt.setInt(1, tareaId);
                    deleteStmt.setInt(2, groupId);
                    int rowsDeleted = deleteStmt.executeUpdate();
                    return rowsDeleted > 0;
                }
            }
            
        } catch (SQLException e) {
            
            System.err.println("Error al mover la tarea al histórico: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public List<Task> getHistoricalTasks() {
        
        int groupId = CurrentSession.getInstance().getUserGroupId();

        if (groupId <= 0) {
            
            throw new IllegalStateException("El ID del grupo actual en la sesión no es válido: " + groupId);
        }

        String query = "SELECT id, nombre_tarea, descripcion, fecha_limite, hora_limite, asignado_a_id, id_grupo FROM TareasHistorico WHERE id_grupo = ?";
        List<Task> historicalTasks = new ArrayList<>();

        try (Connection connection = MySQLConnection.getConnection(); PreparedStatement stmt = connection.prepareStatement(query)) {

            stmt.setInt(1, groupId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                
                java.util.Date fechaLimite = rs.getDate("fecha_limite");

                Task task = new Task(
                        rs.getInt("id"),
                        rs.getString("nombre_tarea"),
                        rs.getString("descripcion"),
                        fechaLimite,
                        rs.getInt("asignado_a_id"),
                        rs.getInt("id_grupo") // Ya sin `estado`
                );

                historicalTasks.add(task);
            }

        } catch (SQLException e) {
            
            System.err.println("Error al obtener tareas históricas: " + e.getMessage());
            e.printStackTrace();
        }

        return historicalTasks;
    }

}
