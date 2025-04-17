/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package homeSweetHome.model;

import java.time.LocalDate;

/**
 *
 * @author Usuario
 */
public class Budget {

    private int id;                    // Identificador único del gasto
    private String nombre;             // Nombre del gasto
    private String categoria;          // Categoría del gasto
    private double monto;              // Importe en euros
    private String metodoPago;         // Método de pago: "Efectivo" o "Tarjeta"
    private LocalDate fecha;           // Fecha del gasto
    private String descripcion;        // Descripción opcional
    private int idGrupo;               // ID del grupo asociado

    // Constructor por defecto
    public Budget() {
    }

    // Constructor con parámetros
    public Budget(int id, String nombre, String categoria, double monto, String metodoPago, LocalDate fecha, String descripcion, int idGrupo) {
        this.id = id;
        this.nombre = nombre;
        this.categoria = categoria;
        this.monto = monto;
        this.metodoPago = metodoPago;
        this.fecha = fecha;
        this.descripcion = descripcion;
        this.idGrupo = idGrupo;
    }

    // Getters y Setters
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

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public double getMonto() {
        return monto;
    }

    public void setMonto(double monto) {
        this.monto = monto;
    }

    public String getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(String metodoPago) {
        this.metodoPago = metodoPago;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public int getIdGrupo() {
        return idGrupo;
    }

    public void setIdGrupo(int idGrupo) {
        this.idGrupo = idGrupo;
    }

    // Método toString para depuración
    @Override
    public String toString() {
        return "Budget{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", categoria='" + categoria + '\'' +
                ", monto=" + monto +
                ", metodoPago='" + metodoPago + '\'' +
                ", fecha=" + fecha +
                ", descripcion='" + descripcion + '\'' +
                ", idGrupo=" + idGrupo +
                '}';
    }
}

