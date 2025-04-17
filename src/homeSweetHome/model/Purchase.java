package homeSweetHome.model;

/**
 * Clase que representa un elemento en la tabla ListaCompra de la base de datos.
 */
public class Purchase {

    private int id; // ID único de la compra
    private int idInventario; // ID del inventario relacionado
    private int cantidadNecesaria; // Cantidad necesaria para comprar
    private String fecha; // Fecha de la compra
    private int idGrupo; // ID del grupo al que pertenece la compra
    private String nombreProducto; // Nombre del producto
    private String tipo; // Tipo de cantidad: "Cantidad" o "Gramos"

    // Constructor completo
    public Purchase(int id, int idInventario, int cantidadNecesaria, String fecha, int idGrupo, String nombreProducto, String tipo) {
        this.id = id;
        this.idInventario = idInventario;
        this.cantidadNecesaria = cantidadNecesaria;
        this.fecha = fecha;
        this.idGrupo = idGrupo;
        this.nombreProducto = nombreProducto;
        this.tipo = tipo;
    }

    // Constructor vacío
    public Purchase() {
    }

    // Getter y Setter para tipo
    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    // Getter y Setter para nombreProducto
    public String getNombreProducto() {
        return nombreProducto;
    }

    public void setNombreProducto(String nombreProducto) {
        this.nombreProducto = nombreProducto;
    }

    // Otros getters y setters...
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdInventario() {
        return idInventario;
    }

    public void setIdInventario(int idInventario) {
        this.idInventario = idInventario;
    }

    public int getCantidadNecesaria() {
        return cantidadNecesaria;
    }

    public void setCantidadNecesaria(int cantidadNecesaria) {
        this.cantidadNecesaria = cantidadNecesaria;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
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
        return "Purchase{"
                + "id=" + id
                + ", idInventario=" + idInventario
                + ", cantidadNecesaria=" + cantidadNecesaria
                + ", fecha='" + fecha + '\''
                + ", idGrupo=" + idGrupo
                + ", nombreProducto='" + nombreProducto + '\''
                + ", tipo='" + tipo + '\''
                + '}';
    }

}
