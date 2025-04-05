package homeSweetHome.model;

import java.sql.Date;
import java.sql.Time;

public class Event {
    private int id; 
    private String nombreEvento; 
    private String descripcion; 
    private Date fechaEvento; 
    private Time horaEvento; 
    private boolean recordatorioEnviado; 
    private int idGrupo; 

    // Constructor vacío 
    public Event() {
    }

    
    /**
     * Constructor completo
     * 
     * @param id
     * @param nombreEvento
     * @param descripcion
     * @param fechaEvento
     * @param horaEvento
     * @param recordatorioEnviado
     * @param idGrupo 
     */
    public Event(int id, String nombreEvento, String descripcion, Date fechaEvento, Time horaEvento, boolean recordatorioEnviado, int idGrupo) {
        this.id = id;
        this.nombreEvento = nombreEvento;
        this.descripcion = descripcion;
        this.fechaEvento = fechaEvento;
        this.horaEvento = horaEvento;
        this.recordatorioEnviado = recordatorioEnviado;
        this.idGrupo = idGrupo;
    }

    // Getters y Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombreEvento() {
        return nombreEvento;
    }

    public void setNombreEvento(String nombreEvento) {
        this.nombreEvento = nombreEvento;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Date getFechaEvento() {
        return fechaEvento;
    }

    public void setFechaEvento(Date fechaEvento) {
        this.fechaEvento = fechaEvento;
    }

    public Time getHoraEvento() {
        return horaEvento;
    }

    public void setHoraEvento(Time horaEvento) {
        this.horaEvento = horaEvento;
    }

    public boolean isRecordatorioEnviado() {
        return recordatorioEnviado;
    }

    public void setRecordatorioEnviado(boolean recordatorioEnviado) {
        this.recordatorioEnviado = recordatorioEnviado;
    }

    public int getIdGrupo() {
        return idGrupo;
    }

    public void setIdGrupo(int idGrupo) {
        this.idGrupo = idGrupo;
    }

    @Override
    public String toString() {
        return "Evento{" +
                "id=" + id +
                ", nombreEvento='" + nombreEvento + '\'' +
                ", descripcion='" + descripcion + '\'' +
                ", fechaEvento=" + fechaEvento +
                ", horaEvento=" + horaEvento +
                ", recordatorioEnviado=" + recordatorioEnviado +
                ", idGrupo=" + idGrupo +
                '}';
    }
}

