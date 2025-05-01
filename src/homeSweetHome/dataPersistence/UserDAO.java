package homeSweetHome.dataPersistence;

import homeSweetHome.model.User;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para la gestión de Usuarios con soporte para Grupos (id_grupo).
 */
public class UserDAO {

    /**
     * Método para añadir el primer usuario al sistema. Este usuario será un
     * administrador y se asociará con un nuevo grupo. Este proceso incluye la
     * creación de un nuevo grupo en la base de datos, seguido por la inserción
     * del usuario y la asociación de este al grupo recién creado.
     *
     * @param user - Objeto de tipo User que contiene los datos del usuario a
     * registrar.
     * @return boolean - `true` si el usuario fue añadido exitosamente, `false`
     * si ocurrió un error.
     */
    public boolean addFirstUser(User user) {
        // Sentencia SQL para insertar un nuevo grupo
        String sqlGroup = "INSERT INTO Grupos (nombre_grupo) VALUES (?)";

        // Sentencia SQL para insertar un nuevo usuario
        String sqlUser = "INSERT INTO Usuarios (nombre, apellidos, correo_electronico, contrasenia, id_rol, id_grupo) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = MySQLConnection.getConnection()) {

            // Inicia una transacción para asegurar que las operaciones se realicen en conjunto
            conn.setAutoCommit(false);

            // Crea un nuevo grupo en la base de datos
            PreparedStatement groupStmt = conn.prepareStatement(sqlGroup, PreparedStatement.RETURN_GENERATED_KEYS);
            groupStmt.setString(1, "Grupo de " + user.getNombre()); // Nombre del grupo basado en el nombre del usuario
            groupStmt.executeUpdate();

            // Obtiene el ID del grupo recién creado
            ResultSet rs = groupStmt.getGeneratedKeys();
            int groupId = 0;
            if (rs.next()) {
                groupId = rs.getInt(1); // Recupera el ID generado por la base de datos
            }

            // Inserta el usuario y asocia al grupo recién creado
            PreparedStatement userStmt = conn.prepareStatement(sqlUser);
            userStmt.setString(1, user.getNombre()); // Nombre del usuario
            userStmt.setString(2, user.getApellidos()); // Apellidos del usuario
            userStmt.setString(3, user.getCorreoElectronico()); // Correo electrónico
            userStmt.setString(4, user.getContrasenia()); // Contraseña
            userStmt.setInt(5, 1); // Rol del usuario: Administrador
            userStmt.setInt(6, groupId); // ID del grupo creado anteriormente

            int rowsAffected = userStmt.executeUpdate(); // Ejecuta la inserción del usuario

            conn.commit(); // Confirma la transacción para aplicar los cambios
            return rowsAffected > 0; // Retorna `true` si se afectaron filas

        } catch (SQLException e) {
            // Si ocurre un error, imprime el mensaje y retorna `false`
            System.err.println("Error al registrar el primer usuario: " + e.getMessage());
            return false;
        }
    }

    /**
     * Método para añadir un nuevo usuario (registro o agregado por un
     * administrador) y asociarlo al grupo del administrador. Este método guarda
     * los datos del usuario en la base de datos, incluyendo su información
     * personal, credenciales y el grupo al que pertenece.
     *
     * @param user - Objeto de tipo User que contiene los datos del usuario a
     * registrar.
     * @return boolean - `true` si el usuario fue añadido exitosamente, `false`
     * si ocurrió un error.
     */
    public boolean addUser(User user) {
        // Log para depuración: mostrar el ID del grupo al que se asociará el usuario
        System.out.println("ID del grupo del usuario: " + user.getIdGrupo());

        // Sentencia SQL para insertar un nuevo usuario en la tabla Usuarios
        String sql = "INSERT INTO Usuarios (nombre, apellidos, correo_electronico, contrasenia, id_rol, foto_Perfil, id_grupo) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = MySQLConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // Asigna parámetros al PreparedStatement
            pstmt.setString(1, user.getNombre()); // Nombre del usuario
            pstmt.setString(2, user.getApellidos()); // Apellidos del usuario
            pstmt.setString(3, user.getCorreoElectronico()); // Correo electrónico del usuario
            pstmt.setString(4, user.getContrasenia()); // Contraseña del usuario
            pstmt.setInt(5, user.getIdRol()); // ID del rol del usuario (por ejemplo: administrador, miembro)
            pstmt.setBlob(6, user.getFotoPerfil()); // Foto de perfil (almacenada como Blob en la base de datos)
            pstmt.setInt(7, user.getIdGrupo()); // ID del grupo al que pertenece el usuario

            // Ejecuta la consulta y verificar si se afectaron filas
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0; // Retorna true si la operación fue exitosa

        } catch (SQLException e) {
            // Captura y muestra cualquier error que ocurra durante la operación
            System.err.println("Error al registrar usuario: " + e.getMessage());
            return false; // Retorna false si ocurre un error
        }
    }

    /**
     * Método para eliminar un usuario de la base de datos utilizando su ID.
     *
     * @param userId - ID único del usuario que se desea eliminar.
     * @return boolean - `true` si el usuario fue eliminado correctamente,
     * `false` si ocurrió un error.
     */
    public boolean deleteUserById(int userId) {
        // Sentencia SQL para eliminar un usuario por su ID
        String sql = "DELETE FROM Usuarios WHERE id = ?";

        try (Connection conn = MySQLConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // Asigna el ID del usuario al parámetro de la consulta
            pstmt.setInt(1, userId);

            // Ejecuta la consulta SQL
            int rowsAffected = pstmt.executeUpdate();

            // Retorna true si se eliminó al menos una fila
            return rowsAffected > 0;

        } catch (SQLException e) {
            // Captura y registra cualquier error que ocurra
            System.err.println("Error al eliminar el usuario: " + e.getMessage());
            return false; // Retorna false si ocurrió un error
        }
    }

    /**
     * Método para actualizar los datos de un usuario en la base de datos. Este
     * proceso verifica si el correo electrónico ingresado por el usuario ya
     * está en uso por otro usuario (excluyendo al propio usuario que se está
     * actualizando). Si no hay conflictos, procede a realizar la actualización.
     *
     * @param user - Objeto de tipo User que contiene los datos actualizados del
     * usuario.
     * @return boolean - `true` si el usuario fue actualizado correctamente,
     * `false` si ocurrió un error o el correo electrónico ya está en uso.
     */
    public boolean updateUser(User user) {
        // Sentencia SQL para verificar si el correo electrónico ya está en uso
        String sqlCheck = "SELECT id FROM Usuarios WHERE correo_electronico = ? AND id != ?";

        // Sentencia SQL para actualizar los datos del usuario
        String sqlUpdate = "UPDATE Usuarios SET nombre = ?, apellidos = ?, correo_electronico = ?, contrasenia = ?, id_rol = ?, foto_perfil = ? WHERE id = ?";

        try (Connection conn = MySQLConnection.getConnection()) {
            // Sentencia SQL para obtener el correo actual del usuario desde la base de datos
            String sqlCurrentEmail = "SELECT correo_electronico FROM Usuarios WHERE id = ?";
            PreparedStatement currentEmailStmt = conn.prepareStatement(sqlCurrentEmail);
            currentEmailStmt.setInt(1, user.getId()); // Establecer el ID del usuario
            ResultSet currentEmailRs = currentEmailStmt.executeQuery();

            if (currentEmailRs.next()) {
                // Recupera el correo actual y el nuevo correo ingresado
                String currentEmail = currentEmailRs.getString("correo_electronico").trim();
                String newEmail = user.getCorreoElectronico().trim();

                // LOG: Muestra correos para depuración
                System.out.println("Correo actual en la base de datos: " + currentEmail);
                System.out.println("Correo ingresado: " + newEmail);

                if (!currentEmail.equalsIgnoreCase(newEmail)) {
                    // Verifica si el nuevo correo ya está en uso por otro usuario
                    PreparedStatement checkStmt = conn.prepareStatement(sqlCheck);
                    checkStmt.setString(1, newEmail); // Nuevo correo ingresado
                    checkStmt.setInt(2, user.getId()); // Excluir al usuario actual
                    ResultSet rs = checkStmt.executeQuery();

                    if (rs.next()) {
                        // LOG: El correo ya está en uso
                        System.err.println("El correo ya está en uso por otro usuario.");
                        return false; // Retorna false si hay conflicto con el correo
                    }
                }
            }

            // Procede con la actualización si no hay conflictos
            PreparedStatement updateStmt = conn.prepareStatement(sqlUpdate);
            updateStmt.setString(1, user.getNombre()); // Nombre del usuario
            updateStmt.setString(2, user.getApellidos()); // Apellidos
            updateStmt.setString(3, user.getCorreoElectronico()); // Correo electrónico
            updateStmt.setString(4, user.getContrasenia()); // Contraseña
            updateStmt.setInt(5, user.getIdRol()); // ID del rol del usuario
            updateStmt.setBlob(6, user.getFotoPerfil()); // Foto de perfil (Blob)
            updateStmt.setInt(7, user.getId()); // ID del usuario

            // Ejecuta la consulta SQL de actualización
            int rowsAffected = updateStmt.executeUpdate();
            // LOG: Muestra el número de filas afectadas
            System.out.println("Filas afectadas en la actualización: " + rowsAffected);

            return rowsAffected > 0; // Retorna true si se afectaron filas

        } catch (SQLException e) {
            // Captura y registra cualquier error que ocurra
            System.err.println("Error al actualizar el usuario: " + e.getMessage());
            return false; // Retornar false si ocurre un error
        }
    }

    /**
     * Método para autenticar un usuario en el sistema utilizando su correo
     * electrónico y contraseña. Si las credenciales son válidas, retorna un
     * objeto `User` con la información del usuario autenticado; de lo
     * contrario, retorna `null`.
     *
     * @param username - Correo electrónico del usuario.
     * @param password - Contraseña del usuario.
     * @return User - Objeto `User` con los datos del usuario autenticado, o
     * `null` si no se encuentra.
     */
    public User authenticateUser(String username, String password) {
        // Consulta SQL para buscar al usuario con el correo electrónico y contraseña proporcionados
        String sql = "SELECT * FROM Usuarios WHERE correo_electronico = ? AND contrasenia = ?";

        try (Connection connection = MySQLConnection.getConnection(); PreparedStatement pstmt = connection.prepareStatement(sql)) {

            // Asigna parámetros al PreparedStatement
            pstmt.setString(1, username); // Correo electrónico
            pstmt.setString(2, password); // Contraseña

            // Ejecuta la consulta
            ResultSet resultSet = pstmt.executeQuery();

            if (resultSet.next()) {
                // Crea un objeto `User` con los datos del usuario autenticado
                User user = new User();
                user.setId(resultSet.getInt("id")); // ID del usuario
                user.setNombre(resultSet.getString("nombre")); // Nombre del usuario
                user.setApellidos(resultSet.getString("apellidos")); // Apellidos
                user.setCorreoElectronico(resultSet.getString("correo_electronico")); // Correo electrónico
                user.setIdRol(resultSet.getInt("id_rol")); // ID del rol
                user.setIdGrupo(resultSet.getInt("id_grupo")); // ID del grupo al que pertenece el usuario
                return user; // Retorna el usuario autenticado
            }

        } catch (SQLException e) {
            // Captura y registra cualquier error que ocurra 
            System.err.println("Error al autenticar usuario: " + e.getMessage());
        }

        return null; // Retorna null si el usuario no fue encontrado o ocurrió un error
    }

    /**
     * Método para obtener todos los usuarios que pertenecen a un grupo
     * específico. Este método recupera la información de los usuarios desde la
     * base de datos, incluyendo su nombre, apellidos, correo electrónico, rol y
     * foto de perfil.
     *
     * @param groupId - ID único del grupo al que pertenecen los usuarios.
     * @return List<User> - Lista de objetos `User` que representan los usuarios
     * del grupo. Retorna una lista vacía si no se encuentran usuarios.
     */
    public List<User> getUsersByGroup(int groupId) {
        // Consulta SQL para recuperar los usuarios que pertenecen al grupo especificado
        String sql = "SELECT u.id, u.nombre, u.apellidos, u.correo_electronico, u.id_rol, u.foto_perfil FROM Usuarios u WHERE u.id_grupo = ?";

        // Inicializa una lista para almacenar los usuarios
        List<User> users = new ArrayList<>();

        try (Connection conn = MySQLConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            // Asigna el ID del grupo como parámetro de la consulta SQL
            pstmt.setInt(1, groupId);

            // Ejecuta la consulta y obtiene los resultados
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                // Crea un objeto `User` para cada registro obtenido
                User user = new User();
                user.setId(rs.getInt("id")); // ID del usuario
                user.setNombre(rs.getString("nombre")); // Nombre del usuario
                user.setApellidos(rs.getString("apellidos")); // Apellidos del usuario
                user.setCorreoElectronico(rs.getString("correo_electronico")); // Correo electrónico del usuario
                user.setIdRol(rs.getInt("id_rol")); // Rol del usuario
                user.setFotoPerfil(rs.getBlob("foto_perfil")); // Foto de perfil (almacenada como Blob)

                // Agrega el objeto `User` a la lista de usuarios
                users.add(user);
            }

        } catch (SQLException e) {
            // Captura y registra cualquier error que ocurra durante la operación
            System.err.println("Error al obtener usuarios por grupo: " + e.getMessage());
        }

        // Retorna la lista de usuarios (vacía si no se encontraron registros)
        return users;
    }

    /**
     * Método para comprobar si existe un usuario en la base de datos utilizando
     * su correo electrónico. Este método realiza una consulta para contar el
     * número de usuarios con el correo proporcionado.
     *
     * @param correoElectronico - Correo electrónico del usuario a comprobar.
     * @return boolean - `true` si el usuario existe, `false` si no existe o
     * ocurre un error.
     */
    public static boolean userExists(String correoElectronico) {
        // Consulta SQL para contar usuarios con el correo electrónico proporcionado
        String sql = "SELECT COUNT(*) FROM Usuarios WHERE correo_electronico = ?";

        try (Connection conn = MySQLConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            // Asigna el correo electrónico como parámetro para la consulta
            pstmt.setString(1, correoElectronico);

            // Ejecutala consulta y obtener el resultado
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                // Retorna true si el conteo de usuarios es mayor que 0
                return rs.getInt(1) > 0;
            }

        } catch (SQLException e) {
            // Capturar y registrar cualquier error que ocurra
            System.err.println("Error al comprobar si el usuario existe: " + e.getMessage());
        }

        // Retorna false por defecto si ocurre un error o el usuario no existe
        return false;
    }

    /**
     * Método para obtener el nombre de un rol a partir de su ID. Este método
     * realiza una consulta a la base de datos para buscar el nombre del rol
     * correspondiente. Si no se encuentra un rol con el ID proporcionado, se
     * retorna el valor por defecto "Desconocido".
     *
     * @param idRol - ID único del rol en la base de datos.
     * @return String - Nombre del rol si se encuentra, o "Desconocido" si no
     * existe o ocurre un error.
     */
    public String getRoleNameById(int idRol) {
        // Consulta SQL para recuperar el nombre del rol por su ID
        String sql = "SELECT nombre_rol FROM Roles WHERE id = ?";

        try (Connection conn = MySQLConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            // Asigna el ID del rol como parámetro de la consulta
            pstmt.setInt(1, idRol);

            // Ejecuta la consulta y obtener el resultado
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                // Retorna el nombre del rol encontrado
                return rs.getString("nombre_rol");
            }

        } catch (SQLException e) {
            // Captura y registra cualquier error que ocurra durante la operación
            System.err.println("Error al obtener el nombre del rol: " + e.getMessage());
        }

        // Retorna "Desconocido" si no se encuentra el rol o ocurre un error
        return "Desconocido";
    }

    /**
     * Método para obtener los datos de un usuario desde la base de datos
     * utilizando su ID. Si el usuario existe, retorna un objeto `User` con la
     * información del usuario; de lo contrario, retorna `null`.
     *
     * @param userId - ID único del usuario a buscar en la base de datos.
     * @return User - Objeto con los datos del usuario, o `null` si no se
     * encuentra.
     */
    public User getUserById(int userId) {
        // Consulta SQL para obtener los datos del usuario por su ID
        String sql = "SELECT id, nombre, apellidos, correo_electronico, contrasenia, id_rol, foto_perfil FROM Usuarios WHERE id = ?";

        try (Connection conn = MySQLConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // Asigna el ID del usuario al parámetro de la consulta
            pstmt.setInt(1, userId);

            // Ejecuta la consulta y obtener los resultados
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                // Crea un objeto `User` y asignar los datos obtenidos del ResultSet
                User user = new User();
                user.setId(rs.getInt("id")); // ID del usuario
                user.setNombre(rs.getString("nombre")); // Nombre del usuario
                user.setApellidos(rs.getString("apellidos")); // Apellidos
                user.setCorreoElectronico(rs.getString("correo_electronico")); // Correo electrónico
                user.setContrasenia(rs.getString("contrasenia")); // Contraseña
                // LOG: Muestra la contraseña recuperada para verificar
                System.out.println("Contraseña recuperada: " + rs.getString("contrasenia"));
                user.setIdRol(rs.getInt("id_rol")); // Rol del usuario
                user.setFotoPerfil(rs.getBlob("foto_perfil")); // Foto de perfil en formato Blob
                return user; // Retorna el objeto `User` con los datos del usuario
            }

        } catch (SQLException e) {
            // Captura y registra cualquier error que ocurra durante la operación
            System.err.println("Error al obtener el usuario por ID: " + e.getMessage());
        }

        // Retorna null si no se encuentra al usuario o ocurre un error
        return null;
    }

}
