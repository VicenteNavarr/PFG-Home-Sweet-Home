package homeSweetHome.model;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Clase genérica que representa un producto. Puede utilizarse tanto para el
 * inventario como para la lista de compras.
 */
public class Product {

    private int id; // ID único del producto
    private String nombreProducto; // Nombre del producto
    private int cantidad; // Cantidad actual en inventario o necesaria en la lista de compras
    private int cantidadMinima; // Cantidad mínima (relevante para el inventario)
    private int cantidadMaxima; // Cantidad máxima (opcional)
    private String tipo; // Tipo de medida: "Cantidad" o "Gramos"
    private String categoria; // Categoría del producto: "Alimentación", "Bebidas", etc.
    private int idGrupo; // ID del grupo al que pertenece el producto
    private String fecha; // Fecha (relevante para la lista de compras)

    /**
     * Constructor completo para inicializar todos los atributos de un producto.
     *
     * @param id ID único del producto.
     * @param nombreProducto Nombre del producto.
     * @param cantidad Cantidad actual en inventario o necesaria en la lista de
     * compras.
     * @param cantidadMinima Cantidad mínima (para inventario).
     * @param cantidadMaxima Cantidad máxima (opcional).
     * @param tipo Tipo de medida: "Cantidad" o "Gramos".
     * @param categoria Categoría del producto: "Alimentación", "Bebidas", etc.
     * @param idGrupo ID del grupo al que pertenece el producto.
     * @param fecha Fecha relevante (para lista de compras).
     */
    public Product(int id, String nombreProducto, int cantidad, int cantidadMinima, int cantidadMaxima,
            String tipo, String categoria, int idGrupo, String fecha) {
        this.id = id;
        this.nombreProducto = nombreProducto;
        this.cantidad = cantidad;
        this.cantidadMinima = cantidadMinima;
        this.cantidadMaxima = cantidadMaxima;
        this.tipo = tipo;
        this.categoria = categoria;
        this.idGrupo = idGrupo;
        this.fecha = fecha;
    }

    /**
     * Constructor vacío para inicializar un producto sin atributos definidos.
     */
    public Product() {
    }

    // Getters y Setters para todos los atributos
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

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public int getIdGrupo() {
        return idGrupo;
    }

    public void setIdGrupo(int idGrupo) {
        this.idGrupo = idGrupo;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    @Override
    public String toString() {
        return "Product{"
                + "id=" + id
                + ", nombreProducto='" + nombreProducto + '\''
                + ", cantidad=" + cantidad
                + ", cantidadMinima=" + cantidadMinima
                + ", cantidadMaxima=" + cantidadMaxima
                + ", tipo='" + tipo + '\''
                + ", categoria='" + categoria + '\''
                + ", idGrupo=" + idGrupo
                + ", fecha='" + fecha + '\''
                + '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Product product = (Product) obj;
        return id == product.id; // Compara por ID único
    }

    @Override
    public int hashCode() {
        return Objects.hash(id); // Genera un hash basado en el ID
    }

}
