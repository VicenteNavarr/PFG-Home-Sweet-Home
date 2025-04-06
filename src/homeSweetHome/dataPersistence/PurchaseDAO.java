package homeSweetHome.dataPersistence;

import homeSweetHome.model.Purchase;
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
    public List<Purchase> getPurchasesByGroup(int idGrupo) {
        List<Purchase> purchases = new ArrayList<>();
        String query = """
                SELECT ListaCompra.id, ListaCompra.id_inventario, ListaCompra.cantidad_necesaria,
                       ListaCompra.fecha, ListaCompra.id_grupo, Inventario.nombre_producto
                FROM ListaCompra
                INNER JOIN Inventario ON ListaCompra.id_inventario = Inventario.id
                WHERE ListaCompra.id_grupo = ?
                """;

        try (Connection conn = MySQLConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, idGrupo);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Purchase purchase = new Purchase(
                        rs.getInt("id"),
                        rs.getInt("id_inventario"),
                        rs.getInt("cantidad_necesaria"),
                        rs.getString("fecha"),
                        rs.getInt("id_grupo"),
                        rs.getString("nombre_producto") // Agrega nombre del producto
                );
                purchases.add(purchase);
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
     * @param purchase El objeto Purchase que se quiere agregar.
     * @return True si se agregó correctamente, False en caso contrario.
     */
    public boolean addPurchase(Purchase purchase) {
        String query = "INSERT INTO ListaCompra (id_inventario, cantidad_necesaria, fecha, id_grupo) VALUES (?, ?, ?, ?)";

        try (Connection conn = MySQLConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, purchase.getIdInventario());
            stmt.setInt(2, purchase.getCantidadNecesaria());
            stmt.setString(3, purchase.getFecha());
            stmt.setInt(4, purchase.getIdGrupo());

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
     * @param purchase El objeto Purchase que se quiere actualizar.
     * @return True si se actualizó correctamente, False en caso contrario.
     */
    public boolean updatePurchase(Purchase purchase) {
        String query = "UPDATE ListaCompra SET cantidad_necesaria = ?, fecha = ? WHERE id = ?";

        try (Connection conn = MySQLConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, purchase.getCantidadNecesaria());
            stmt.setString(2, purchase.getFecha());
            stmt.setInt(3, purchase.getId());

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
}
