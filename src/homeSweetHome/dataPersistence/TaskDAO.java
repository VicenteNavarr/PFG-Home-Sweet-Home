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
            // Asignar los datos de la tarea a los parámetros de la consulta
            pstmt.setString(1, task.getNombreTarea());
            pstmt.setString(2, task.getDescripcion());
            pstmt.setDate(3, task.getFechaLimite() != null ? new java.sql.Date(task.getFechaLimite().getTime()) : null);
            pstmt.setInt(4, task.getAsignadoAId());
            pstmt.setString(5, task.getEstado());
            pstmt.setInt(6, task.getIdGrupo());

            // Ejecutar la consulta y verificar el resultado
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            // Registrar cualquier error que ocurra
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
            pstmt.setString(1, nombreTarea); // Establecer el nombre como parámetro
            ResultSet rs = pstmt.executeQuery(); // Ejecutar la consulta

            if (rs.next()) {
                return rs.getInt(1) > 0; // Retornar true si el conteo es mayor a 0
            }
        } catch (SQLException e) {
            // Registrar el error en caso de fallo
            System.err.println("Error al comprobar si la tarea existe: " + e.getMessage());
        }

        return false; // Retornar false si ocurre un error o no se encuentra la tarea
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

            // Iterar sobre los resultados y llenar la lista de tareas
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
            // Registrar cualquier error durante la consulta
            System.err.println("Error al obtener tareas por grupo: " + e.getMessage());
        }

        // Retornar la lista de tareas (vacía si no se encontraron registros)
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
            pstmt.setInt(1, taskId); // Asignar el ID de la tarea al parámetro de la consulta

            int rowsAffected = pstmt.executeUpdate(); // Ejecutar la consulta
            return rowsAffected > 0; // Retornar `true` si se afectó al menos una fila
        } catch (SQLException e) {
            System.err.println("Error al eliminar la tarea con ID " + taskId + ": " + e.getMessage());
            return false; // Retornar `false` en caso de error
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
            // Configurar los valores de la actualización
            pstmt.setString(1, task.getNombreTarea()); // Nombre de la tarea
            pstmt.setString(2, task.getDescripcion()); // Descripción
            pstmt.setDate(3, task.getFechaLimite() != null ? new java.sql.Date(task.getFechaLimite().getTime()) : null); // Fecha límite
            pstmt.setInt(4, task.getAsignadoAId()); // ID del usuario asignado
            pstmt.setString(5, task.getEstado()); // Estado de la tarea (Pendiente, Completada, etc.)
            pstmt.setInt(6, task.getId()); // ID de la tarea

            // Ejecutar la consulta de actualización
            int rowsAffected = pstmt.executeUpdate();
            System.out.println("Filas afectadas en la actualización: " + rowsAffected);
            return rowsAffected > 0; // Retornar `true` si se actualizó al menos una fila
        } catch (SQLException e) {
            System.err.println("Error al actualizar la tarea con ID " + task.getId() + ": " + e.getMessage());
            return false; // Retornar `false` en caso de error
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
            pstmt.setInt(1, taskId); // Asignar el ID de la tarea al parámetro

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                // Recuperar datos de la tarea desde el ResultSet
                int id = rs.getInt("id");
                String nombreTarea = rs.getString("nombre_tarea");
                String descripcion = rs.getString("descripcion");
                java.sql.Date fechaLimite = rs.getDate("fecha_limite");
                int asignadoAId = rs.getInt("asignado_a_id");
                String estado = rs.getString("estado");
                int idGrupo = rs.getInt("id_grupo");

                // Crear y retornar el objeto `Task`
                return new Task(id, nombreTarea, descripcion, fechaLimite, asignadoAId, estado, idGrupo);
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener la tarea con ID " + taskId + ": " + e.getMessage());
        }

        return null; // Retornar null si la tarea no se encuentra o ocurre un error
    }

}
