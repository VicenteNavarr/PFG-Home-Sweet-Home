
package homeSweetHome.dataPersistence;

import homeSweetHome.model.Product;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para gestionar las operaciones sobre la tabla ListaCompra en la base de
 * datos.
 */
public class PurchaseDAO {

    /**
     * Recupera todos los elementos de la lista de la compra para un grupo
     * específico.
     *
     * @param idGrupo El ID del grupo cuyos elementos se recuperarán.
     * @return Una lista de elementos de la lista de la compra.
     */
    public List<Product> getPurchasesByGroup(int idGrupo) {
        
        List<Product> purchases = new ArrayList<>();
        
        String query = """
                SELECT ListaCompra.id, ListaCompra.id_inventario, ListaCompra.cantidad_necesaria,
                       ListaCompra.fecha, ListaCompra.id_grupo, Inventario.nombre_producto, 
                       Inventario.categoria, Inventario.tipo
                FROM ListaCompra
                INNER JOIN Inventario ON ListaCompra.id_inventario = Inventario.id
                WHERE ListaCompra.id_grupo = ?
                """;

        try (Connection conn = MySQLConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, idGrupo);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                
                Product product = new Product(
                        rs.getInt("id"), // ID de ListaCompra
                        rs.getString("nombre_producto"),
                        rs.getInt("cantidad_necesaria"), // Cantidad necesaria
                        0, // Cantidad mínima no es relevante para lista de compras
                        0, // Cantidad máxima no es relevante para lista de compras
                        rs.getString("tipo"),
                        rs.getString("categoria"),
                        rs.getInt("id_grupo"),
                        rs.getString("fecha") // Fecha de la lista de compras
                );
                
                purchases.add(product);
            }

        } catch (SQLException e) {
            
            e.printStackTrace();
            System.err.println("Error al recuperar la lista de la compra.");
        }
        
        return purchases;
    }

    /**
     * Agrega un nuevo elemento a la lista de la compra.
     *
     * @param product El objeto Product que se quiere agregar.
     * @return True si se agregó correctamente, False en caso contrario.
     */
    public boolean addPurchase(Product product) {
        
        String query = "INSERT INTO ListaCompra (id_inventario, cantidad_necesaria, tipo, fecha, id_grupo) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = MySQLConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, product.getId()); // ID de inventario
            stmt.setInt(2, product.getCantidad()); // Cantidad necesaria
            stmt.setString(3, product.getTipo());
            stmt.setString(4, product.getFecha());
            stmt.setInt(5, product.getIdGrupo());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            
            e.printStackTrace();
            System.err.println("Error al agregar un elemento a la lista de la compra.");
        }
        
        return false;
    }
    

    /**
     * Actualiza un elemento existente en la lista de la compra.
     *
     * @param product El objeto Product que se quiere actualizar.
     * @return True si se actualizó correctamente, False en caso contrario.
     */
    public boolean updatePurchase(Product product) {
        
        String query = "UPDATE ListaCompra SET cantidad_necesaria = ?, tipo = ?, fecha = ? WHERE id = ?";

        try (Connection conn = MySQLConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, product.getCantidad()); // Cantidad necesaria
            stmt.setString(2, product.getTipo());
            stmt.setString(3, product.getFecha());
            stmt.setInt(4, product.getId()); // ID de ListaCompra

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            
            e.printStackTrace();
            System.err.println("Error al actualizar un elemento de la lista de la compra.");
        }
        
        return false;
    }

    /**
     * Elimina un elemento de la lista de la compra por su ID.
     *
     * @param id El ID del elemento que se quiere eliminar.
     * @return True si se eliminó correctamente, False en caso contrario.
     */
    public boolean deletePurchase(int id) {
        
        String query = "DELETE FROM ListaCompra WHERE id = ?";

        try (Connection conn = MySQLConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, id);

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            
            e.printStackTrace();
            System.err.println("Error al eliminar un elemento de la lista de la compra.");
        }
        
        return false;
    }

    /**
     * Elimina todos los productos de la lista de compras para un grupo
     * específico.
     *
     * @param groupId El ID del grupo cuyos productos se eliminarán de la lista
     * de compras.
     * @return True si la operación se ejecutó correctamente, incluso si no
     * había filas para eliminar. False si ocurre un error.
     */
    public boolean clearShoppingList(int groupId) {
        
        String query = "DELETE FROM listacompra WHERE id_grupo = ?";

        try (Connection conn = MySQLConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, groupId);
            int rowsAffected = stmt.executeUpdate();

            System.out.println("Productos eliminados de la lista de compras: " + rowsAffected);

            // Devuelve true incluso si no había filas afectadas
            return true;
            
        } catch (SQLException e) {
            
            e.printStackTrace();
            System.err.println("Error al eliminar los productos de la lista de compras.");
        }

        return false; // Devuelve false solo si ocurre un error
    }

}
