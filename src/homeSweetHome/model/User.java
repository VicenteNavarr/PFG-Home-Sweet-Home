package homeSweetHome.model;

import java.sql.Blob;
import java.util.HashMap;
import java.util.Map;

/**
 * Clase modelo para representar un Usuario.
 */
public class User {

    private int id;
    private String nombre;
    private String apellidos;
    private String correoElectronico;
    private String contrasenia;
    private int idRol;
    private Blob fotoPerfil;
    private int idGrupo; // Nuevo campo para el ID del grupo

    /**
     * Constructor vacío.
     */
    public User() {
    }

    /**
     * Constructor completo.
     *
     * @param id - int
     * @param nombre - String
     * @param apellidos - String
     * @param correoElectronico - String
     * @param contrasenia - String
     * @param idRol - int
     * @param fotoPerfil - Blob
     * @param idGrupo - int
     */
    public User(int id, String nombre, String apellidos, String correoElectronico, String contrasenia, int idRol, Blob fotoPerfil, int idGrupo) {
        this.id = id;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.correoElectronico = correoElectronico;
        this.contrasenia = contrasenia;
        this.idRol = idRol;
        this.fotoPerfil = fotoPerfil;
        this.idGrupo = idGrupo;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getCorreoElectronico() {
        return correoElectronico;
    }

    public void setCorreoElectronico(String correoElectronico) {
        this.correoElectronico = correoElectronico;
    }

    public String getContrasenia() {
        return contrasenia;
    }

    public void setContrasenia(String contrasenia) {
        this.contrasenia = contrasenia;
    }

    public int getIdRol() {
        return idRol;
    }

    public void setIdRol(int idRol) {
        this.idRol = idRol;
    }

    public Blob getFotoPerfil() {
        return fotoPerfil;
    }

    public void setFotoPerfil(Blob fotoPerfil) {
        this.fotoPerfil = fotoPerfil;
    }

    public int getIdGrupo() {
        return idGrupo;
    }

    public void setIdGrupo(int idGrupo) {
        this.idGrupo = idGrupo;
    }
    
    
    // Mapa estático para la conversión de idRol a nombreRol
    private static final Map<Integer, String> ROLES_MAP = new HashMap<>();
    
    static {
        
        ROLES_MAP.put(1, "Administrador");
        ROLES_MAP.put(2, "Consultor");
        // Agrega más roles aquí según sea necesario
    }

    
    /**
     * Método para obtener el nombre del rol desde mapa
     * 
     * @return 
     */
    public String getNombreRol() {
        
        return ROLES_MAP.getOrDefault(this.idRol, "Rol desconocido");
    }
    
    

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", apellidos='" + apellidos + '\'' +
                ", correoElectronico='" + correoElectronico + '\'' +
                ", contrasenia='" + contrasenia + '\'' +
                ", idRol=" + idRol +
                ", idGrupo=" + idGrupo +
                '}';
    }
}
