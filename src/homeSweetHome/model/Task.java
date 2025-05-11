package homeSweetHome.model;

import java.util.Date;

/**
 * Modelo de Tarea
 */
public class Task {

    private int id;
    private String nombreTarea;
    private String descripcion; // Descripción de la tarea
    private Date fechaLimite;
    private int asignadoAId;
    private String asignadoANombre; // Nombre del usuario asignado
    private String estado;
    private int idGrupo; // ID del grupo al que pertenece la tarea

    // Constructor vacío
    public Task() {
    }

    // Constructor con parámetros
    public Task(int id, String nombreTarea, String descripcion, Date fechaLimite, int asignadoAId, String estado, int idGrupo) {
        this.id = id;
        this.nombreTarea = nombreTarea;
        this.descripcion = descripcion;
        this.fechaLimite = fechaLimite;
        this.asignadoAId = asignadoAId;
        this.estado = estado;
        this.idGrupo = idGrupo;
    }

    // Constructor adicional para incluir el nombre del usuario asignado
    public Task(int id, String nombreTarea, String descripcion, Date fechaLimite, int asignadoAId, String asignadoANombre, String estado, int idGrupo) {
        this.id = id;
        this.nombreTarea = nombreTarea;
        this.descripcion = descripcion;
        this.fechaLimite = fechaLimite;
        this.asignadoAId = asignadoAId;
        this.asignadoANombre = asignadoANombre;
        this.estado = estado;
        this.idGrupo = idGrupo;
    }

    // Nuevo constructor para `getHistoricalTasks()`
    public Task(int id, String nombreTarea, String descripcion, Date fechaLimite, int asignadoAId, int idGrupo) {
        this.id = id;
        this.nombreTarea = nombreTarea;
        this.descripcion = descripcion;
        this.fechaLimite = fechaLimite;
        this.asignadoAId = asignadoAId;
        this.idGrupo = idGrupo;
    }

    // Getters y setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombreTarea() {
        return nombreTarea;
    }

    public void setNombreTarea(String nombreTarea) {
        this.nombreTarea = nombreTarea;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Date getFechaLimite() {
        return fechaLimite;
    }

    public void setFechaLimite(Date fechaLimite) {
        this.fechaLimite = fechaLimite;
    }

    public int getAsignadoAId() {
        return asignadoAId;
    }

    public void setAsignadoAId(int asignadoAId) {
        this.asignadoAId = asignadoAId;
    }

    public String getAsignadoANombre() {
        return asignadoANombre;
    }

    public void setAsignadoANombre(String asignadoANombre) {
        this.asignadoANombre = asignadoANombre;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public int getIdGrupo() {
        return idGrupo;
    }

    public void setIdGrupo(int idGrupo) {
        this.idGrupo = idGrupo;
    }

    @Override
    public String toString() {
        return "Task{"
                + "id=" + id
                + ", nombreTarea='" + nombreTarea + '\''
                + ", descripcion='" + descripcion + '\''
                + ", fechaLimite=" + fechaLimite
                + ", asignadoAId=" + asignadoAId
                + ", asignadoANombre='" + asignadoANombre + '\''
                + ", estado='" + estado + '\''
                + ", idGrupo=" + idGrupo
                + '}';
    }
}
