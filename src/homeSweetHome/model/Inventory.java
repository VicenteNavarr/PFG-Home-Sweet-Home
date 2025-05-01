package homeSweetHome.model;


/**
 * Modelo para representar los elementos del inventario.
 */
public class Inventory {

    private int id; // Identificador único del producto
    private String nombreProducto; // Nombre del producto
    private int cantidad; // Cantidad actual del producto
    private int cantidadMinima; // Cantidad mínima requerida
    private int cantidadMaxima; // Cantidad máxima permitida
    private String categoria; // Categoría del producto
    private String tipo; // Tipo de cantidad: "Cantidad" o "Gramos"
    private int idGrupo; // ID del grupo al que pertenece el producto

    // Constructor vacío
    public Inventory() {
    }

    // Constructor completo
    public Inventory(int id, String nombreProducto, int cantidad, int cantidadMinima, int cantidadMaxima, String categoria, String tipo, int idGrupo) {
        this.id = id;
        this.nombreProducto = nombreProducto;
        this.cantidad = cantidad;
        this.cantidadMinima = cantidadMinima;
        this.cantidadMaxima = cantidadMaxima;
        this.categoria = categoria;
        this.tipo = tipo;
        this.idGrupo = idGrupo;
    }

    // Getters y Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombreProducto() {
        return nombreProducto;
    }

    public void setNombreProducto(String nombreProducto) {
        this.nombreProducto = nombreProducto;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public int getCantidadMinima() {
        return cantidadMinima;
    }

    public void setCantidadMinima(int cantidadMinima) {
        this.cantidadMinima = cantidadMinima;
    }

    public int getCantidadMaxima() {
        return cantidadMaxima;
    }

    public void setCantidadMaxima(int cantidadMaxima) {
        this.cantidadMaxima = cantidadMaxima;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
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
        return "Inventory{"
                + "id=" + id
                + ", nombreProducto='" + nombreProducto + '\''
                + ", cantidad=" + cantidad
                + ", cantidadMinima=" + cantidadMinima
                + ", cantidadMaxima=" + cantidadMaxima
                + ", categoria='" + categoria + '\''
                + ", tipo='" + tipo + '\''
                + ", idGrupo=" + idGrupo
                + '}';
    }

}
