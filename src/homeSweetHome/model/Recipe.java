package homeSweetHome.model;

import java.util.List;

/**
 * Modelo que representa una receta.
 */
public class Recipe {

    private int id; // ID único de la receta
    private String nombre; // Nombre de la receta
    private String instrucciones; // Descripción de cómo preparar la receta
    private String tipo; // Categoría de la receta (legumbres, carnes, etc.)
    private byte[] foto; // Imagen de la receta (almacenada como BLOB)
    private int idGrupo; // ID del grupo al que pertenece la receta
    private List<Product> ingredientes; // Lista de productos necesarios para preparar la receta

    // Constructor vacío
    public Recipe() {
    }

    // Constructor completo
    public Recipe(int id, String nombre, String instrucciones, String tipo, byte[] foto, int idGrupo, List<Product> ingredientes) {
        this.id = id;
        this.nombre = nombre;
        this.instrucciones = instrucciones;
        this.tipo = tipo;
        this.foto = foto;
        this.idGrupo = idGrupo;
        this.ingredientes = ingredientes;
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

    public String getInstrucciones() {
        return instrucciones;
    }

    public void setInstrucciones(String instrucciones) {
        this.instrucciones = instrucciones;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public byte[] getFoto() {
        return foto;
    }

    public void setFoto(byte[] foto) {
        this.foto = foto;
    }

    public int getIdGrupo() {
        return idGrupo;
    }

    public void setIdGrupo(int idGrupo) {
        this.idGrupo = idGrupo;
    }

    public List<Product> getIngredientes() {
        return ingredientes;
    }

    public void setIngredientes(List<Product> ingredientes) {
        this.ingredientes = ingredientes;
    }

    @Override
    public String toString() {
        return "Recipe{"
                + "id=" + id
                + ", nombre='" + nombre + '\''
                + ", instrucciones='" + instrucciones + '\''
                + ", tipo='" + tipo + '\''
                + ", idGrupo=" + idGrupo
                + ", ingredientes=" + ingredientes
                + '}';
    }
}
