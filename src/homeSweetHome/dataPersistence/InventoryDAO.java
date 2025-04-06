package homeSweetHome.dataPersistence;

import homeSweetHome.model.Inventory;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object (DAO) para gestionar el inventario en la base de datos.
 * Proporciona métodos para realizar operaciones CRUD y recuperar datos
 * específicos del inventario.
 */
public class InventoryDAO {

    /**
     * Recupera todos los productos del inventario de un grupo específico.
     *
     * @param groupId El ID del grupo cuyos productos del inventario se quieren
     * recuperar.
     * @return Una lista de productos del inventario pertenecientes al grupo
     * especificado.
     */
    public List<Inventory> getAllInventoryProducts(int groupId) {
        List<Inventory> inventories = new ArrayList<>();
        String query = "SELECT * FROM Inventario WHERE id_grupo = ?";

        try (Connection conn = MySQLConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, groupId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Inventory inventory = new Inventory(
                        rs.getInt("id"),
                        rs.getString("nombre_producto"),
                        rs.getInt("cantidad"),
                        rs.getInt("cantidad_minima"),
                        rs.getInt("cantidad_maxima"),
                        rs.getString("categoria"),
                        rs.getInt("id_grupo")
                );
                inventories.add(inventory);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error al recuperar los productos del inventario.");
        }
        return inventories;
    }

    /**
     * Agrega un nuevo producto al inventario.
     *
     * @param inventory El objeto del producto que se quiere agregar al
     * inventario.
     * @return True si el producto fue agregado correctamente, False en caso
     * contrario.
     */
    public boolean addInventoryProduct(Inventory inventory) {
        String query = "INSERT INTO Inventario (nombre_producto, cantidad, cantidad_minima, cantidad_maxima, categoria, id_grupo) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = MySQLConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, inventory.getNombreProducto());
            stmt.setInt(2, inventory.getCantidad());
            stmt.setInt(3, inventory.getCantidadMinima());
            stmt.setInt(4, inventory.getCantidadMaxima());
            stmt.setString(5, inventory.getCategoria());
            stmt.setInt(6, inventory.getIdGrupo());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error al agregar el producto al inventario.");
        }
        return false;
    }

    /**
     * Actualiza un producto existente en el inventario.
     *
     * @param inventory El objeto del producto que se quiere actualizar.
     * @return True si el producto fue actualizado correctamente, False en caso
     * contrario.
     */
    public boolean updateInventoryProduct(Inventory inventory) {
        String query = "UPDATE Inventario SET nombre_producto = ?, cantidad = ?, cantidad_minima = ?, cantidad_maxima = ?, categoria = ? WHERE id = ?";

        try (Connection conn = MySQLConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, inventory.getNombreProducto());
            stmt.setInt(2, inventory.getCantidad());
            stmt.setInt(3, inventory.getCantidadMinima());
            stmt.setInt(4, inventory.getCantidadMaxima());
            stmt.setString(5, inventory.getCategoria());
            stmt.setInt(6, inventory.getId());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error al actualizar el producto en el inventario.");
        }
        return false;
    }

    /**
     * Elimina un producto del inventario por su ID.
     *
     * @param id El ID del producto que se quiere eliminar.
     * @return True si el producto fue eliminado correctamente, False en caso
     * contrario.
     */
    public boolean deleteInventoryProduct(int id) {
        String query = "DELETE FROM Inventario WHERE id = ?";

        try (Connection conn = MySQLConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, id);

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error al eliminar el producto del inventario.");
        }
        return false;
    }

    /**
     * Recupera los productos que necesitan reabastecimiento para un grupo
     * específico.
     *
     * @param groupId El ID del grupo cuyos productos necesitan
     * reabastecimiento.
     * @return Una lista de productos del inventario que requieren
     * reabastecimiento.
     */
    public List<Inventory> getReplenishmentProducts(int groupId) {
        List<Inventory> inventories = new ArrayList<>();
        String query = "SELECT * FROM Inventario WHERE cantidad < cantidad_minima AND id_grupo = ?";

        try (Connection conn = MySQLConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, groupId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Inventory inventory = new Inventory(
                        rs.getInt("id"),
                        rs.getString("nombre_producto"),
                        rs.getInt("cantidad"),
                        rs.getInt("cantidad_minima"),
                        rs.getInt("cantidad_maxima"),
                        rs.getString("categoria"),
                        rs.getInt("id_grupo")
                );
                inventories.add(inventory);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error al recuperar los productos que necesitan reabastecimiento.");
        }
        return inventories;
    }
}
