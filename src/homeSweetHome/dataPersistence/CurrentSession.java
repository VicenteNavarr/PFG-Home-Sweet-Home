package homeSweetHome.dataPersistence;

///**
// * Clase que gestiona la sesión actual usando el patrón Singleton. Mantiene el
// * ID del usuario, del grupo y de la tarea actual.
// */
//public class CurrentSession {
//
//    private static CurrentSession instancia;
//    private int userId;        // ID del usuario logueado
//    private int userGroupId;   // ID del grupo al que pertenece el usuario
//    private int currentTaskId; // ID de la tarea seleccionada o actual
//
//    // Constructor privado para el patrón Singleton
//    private CurrentSession() {
//    }
//
//    /**
//     * Obtiene la instancia única de CurrentSession.
//     *
//     * @return CurrentSession - Instancia única del Singleton
//     */
//    public static CurrentSession getInstance() {
//        if (instancia == null) {
//            instancia = new CurrentSession();
//        }
//        return instancia;
//    }
//
////////Métodos para gestionar el ID del usuario
//    
//    public int getUserId() {
//        return userId;
//    }
//
//    public void setUserId(int userId) {
//        this.userId = userId;
//    }
//
////////// Métodos para gestionar el ID del grupo
//    public int getUserGroupId() {
//        return userGroupId;
//    }
//
//    public void setUserGroupId(int userGroupId) {
//        this.userGroupId = userGroupId;
//    }
//
////////// Métodos para gestionar el ID de la tarea actual
//    public int getCurrentTaskId() {
//        return currentTaskId;
//    }
//
//    public void setCurrentTaskId(int currentTaskId) {
//        this.currentTaskId = currentTaskId;
//    }
//
//    /**
//     * Limpia la sesión al cerrar sesión.
//     */
//    public void cerrarSesion() {
//        instancia = null; 
//    }
//}


/**
 * Clase que gestiona la sesión actual usando el patrón Singleton. Mantiene el
 * ID del usuario, del grupo y de la tarea actual, así como el nombre del usuario.
 */
public class CurrentSession {

    private static CurrentSession instancia;
    private int userId;        // ID del usuario logueado
    private int userGroupId;   // ID del grupo al que pertenece el usuario
    private int currentTaskId; // ID de la tarea seleccionada o actual
    private String userName;   // Nombre del usuario logueado

    // Constructor privado para el patrón Singleton
    private CurrentSession() {
    }

    /**
     * Obtiene la instancia única de CurrentSession.
     *
     * @return CurrentSession - Instancia única del Singleton
     */
    public static CurrentSession getInstance() {
        if (instancia == null) {
            instancia = new CurrentSession();
        }
        return instancia;
    }

    ////// Métodos para gestionar el ID del usuario
    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    ////// Métodos para gestionar el nombre del usuario
    public String getUserName() {
        return userName != null ? userName : "Usuario"; // Devuelve "Usuario" si no hay un nombre definido
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    ////// Métodos para gestionar el ID del grupo
    public int getUserGroupId() {
        return userGroupId;
    }

    public void setUserGroupId(int userGroupId) {
        this.userGroupId = userGroupId;
    }

    ////// Métodos para gestionar el ID de la tarea actual
    public int getCurrentTaskId() {
        return currentTaskId;
    }

    public void setCurrentTaskId(int currentTaskId) {
        this.currentTaskId = currentTaskId;
    }

    /**
     * Limpia la sesión al cerrar sesión.
     */
    public void cerrarSesion() {
        instancia = null;
    }
}
