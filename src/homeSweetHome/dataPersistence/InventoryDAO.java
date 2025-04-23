package homeSweetHome.dataPersistence;

import homeSweetHome.model.Product;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object (DAO) para gestionar el inventario en la base de datos.
 * Proporciona métodos para realizar operaciones CRUD y recuperar datos
 * específicos del inventario utilizando el modelo `Product`.
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
    public List<Product> getAllInventoryProducts(int groupId) {
        List<Product> products = new ArrayList<>();
        String query = "SELECT * FROM Inventario WHERE id_grupo = ?";

        try (Connection conn = MySQLConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, groupId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Product product = new Product(
                        rs.getInt("id"),
                        rs.getString("nombre_producto"),
                        rs.getInt("cantidad"),
                        rs.getInt("cantidad_minima"),
                        rs.getInt("cantidad_maxima"),
                        rs.getString("tipo"),
                        rs.getString("categoria"),
                        rs.getInt("id_grupo"),
                        null // Fecha no aplica para inventario
                );
                products.add(product);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error al recuperar los productos del inventario.");
        }
        return products;
    }

    /**
     * Verifica si un producto ya existe en el inventario, ignorando el caso.
     *
     * @param nombreProducto El nombre del producto a verificar.
     * @param groupId El ID del grupo del usuario.
     * @return True si el producto ya existe, False en caso contrario.
     */
    public boolean isProductInInventory(String nombreProducto, int groupId) {
        String query = "SELECT COUNT(*) FROM Inventario WHERE LOWER(nombre_producto) = LOWER(?) AND id_grupo = ?";

        try (Connection conn = MySQLConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, nombreProducto);
            stmt.setInt(2, groupId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0; // Si el conteo es mayor que 0, el producto ya existe
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error al verificar si el producto ya existe en el inventario.");
        }
        return false;
    }

    /**
     * Agrega un nuevo producto al inventario.
     *
     * @param product El objeto del producto que se quiere agregar al
     * inventario.
     * @return True si el producto fue agregado correctamente, False en caso
     * contrario.
     */
//    public boolean addInventoryProduct(Product product) {
//        String query = "INSERT INTO Inventario (nombre_producto, cantidad, cantidad_minima, cantidad_maxima, categoria, tipo, id_grupo) VALUES (?, ?, ?, ?, ?, ?, ?)";
//
//        try (Connection conn = MySQLConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
//
//            stmt.setString(1, product.getNombreProducto());
//            stmt.setInt(2, product.getCantidad());
//            stmt.setInt(3, product.getCantidadMinima());
//            stmt.setInt(4, product.getCantidadMaxima());
//            stmt.setString(5, product.getCategoria());
//            stmt.setString(6, product.getTipo());
//            stmt.setInt(7, product.getIdGrupo());
//
//            return stmt.executeUpdate() > 0;
//
//        } catch (SQLException e) {
//            e.printStackTrace();
//            System.err.println("Error al agregar el producto al inventario.");
//        }
//        return false;
//    }
    public boolean addInventoryProduct(Product product) {
        String query = "INSERT INTO Inventario (nombre_producto, cantidad, cantidad_minima, cantidad_maxima, categoria, tipo, id_grupo) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = MySQLConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {

            // Trazas para depuración
            System.out.println("INSERTANDO EN INVENTARIO:");
            System.out.println("Nombre Producto: " + product.getNombreProducto());
            System.out.println("Cantidad: " + product.getCantidad());
            System.out.println("Cantidad Mínima: " + product.getCantidadMinima());
            System.out.println("Cantidad Máxima: " + product.getCantidadMaxima());
            System.out.println("Categoría: " + product.getCategoria());
            System.out.println("Tipo: " + product.getTipo());
            System.out.println("ID Grupo: " + product.getIdGrupo());

            stmt.setString(1, product.getNombreProducto());
            stmt.setInt(2, product.getCantidad());
            stmt.setInt(3, product.getCantidadMinima());
            stmt.setInt(4, product.getCantidadMaxima());
            stmt.setString(5, product.getCategoria());
            stmt.setString(6, product.getTipo());
            stmt.setInt(7, product.getIdGrupo());

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
     * @param product El objeto del producto que se quiere actualizar.
     * @return True si el producto fue actualizado correctamente, False en caso
     * contrario.
     */
    public boolean updateInventoryProduct(Product product) {
        String query = "UPDATE Inventario SET nombre_producto = ?, cantidad = ?, cantidad_minima = ?, cantidad_maxima = ?, categoria = ?, tipo = ? WHERE id = ?";

        try (Connection conn = MySQLConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, product.getNombreProducto());
            stmt.setInt(2, product.getCantidad());
            stmt.setInt(3, product.getCantidadMinima());
            stmt.setInt(4, product.getCantidadMaxima());
            stmt.setString(5, product.getCategoria());
            stmt.setString(6, product.getTipo());
            stmt.setInt(7, product.getId());

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
    public List<Product> getReplenishmentProducts(int groupId) {
        List<Product> products = new ArrayList<>();
        String query = "SELECT * FROM Inventario WHERE cantidad < cantidad_minima AND id_grupo = ?";

        try (Connection conn = MySQLConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, groupId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Product product = new Product(
                        rs.getInt("id"),
                        rs.getString("nombre_producto"),
                        rs.getInt("cantidad"),
                        rs.getInt("cantidad_minima"),
                        rs.getInt("cantidad_maxima"),
                        rs.getString("tipo"),
                        rs.getString("categoria"),
                        rs.getInt("id_grupo"),
                        null // Fecha no aplica para inventario
                );
                products.add(product);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error al recuperar los productos que necesitan reabastecimiento.");
        }
        return products;
    }

    /**
     * Recupera el ID de un producto en el inventario basado en su nombre.
     *
     * @param nombreProducto El nombre del producto.
     * @param groupId El ID del grupo.
     * @return El ID del producto si existe, o 0 si no se encuentra.
     */
    public int getInventoryProductIdByName(String nombreProducto, int groupId) {
        String query = "SELECT id FROM Inventario WHERE LOWER(nombre_producto) = LOWER(?) AND id_grupo = ?";
        try (Connection conn = MySQLConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, nombreProducto);
            stmt.setInt(2, groupId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id"); // Retorna el ID del producto si existe
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error al obtener el ID del producto del inventario.");
        }
        return 0; // Retorna 0 si no se encuentra el producto
    }

    /**
     * Genera un nuevo ID único para el inventario.
     *
     * @return Un nuevo ID único que no esté en uso actualmente en la tabla
     * Inventario.
     */
    public int generateNewInventoryId() {
        String query = "SELECT MAX(id) FROM Inventario";
        try (Connection conn = MySQLConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(query); ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1) + 1; // Devuelve el ID siguiente al máximo actual
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error al generar un nuevo ID para el inventario.");
        }
        return 1; // Si no hay productos en la tabla, retorna 1 como el primer ID
    }

    /**
     * Actualiza la cantidad actual de un producto en el inventario.
     *
     * @param idInventario El ID del producto en el inventario.
     * @param nuevaCantidad La nueva cantidad que se establecerá.
     * @return True si la actualización fue exitosa, False en caso contrario.
     */
    public boolean updateInventoryQuantity(int idInventario, int nuevaCantidad) {
        String query = "UPDATE inventario SET cantidad = ? WHERE id = ?"; // Usamos la columna 'cantidad'

        try (Connection conn = MySQLConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            System.out.println("Ejecutando actualización para ID: " + idInventario + ", nueva cantidad: " + nuevaCantidad);

            stmt.setInt(1, nuevaCantidad);
            stmt.setInt(2, idInventario);

            int rowsAffected = stmt.executeUpdate();
            System.out.println("Filas afectadas: " + rowsAffected);
            return rowsAffected > 0; // Devuelve True si al menos una fila fue actualizada
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error al actualizar la cantidad del producto en el inventario.");
        }

        return false; // Devuelve False si ocurrió un error
    }

    /**
     * Obtiene la cantidad actual de un producto en el inventario según su ID.
     *
     * @param idInventario El ID del producto en el inventario.
     * @return La cantidad actual del producto, o -1 si ocurre un error.
     */
    public int getCurrentQuantityById(int idInventario) {
        String query = "SELECT cantidad FROM inventario WHERE id = ?";

        try (Connection conn = MySQLConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, idInventario);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("cantidad"); // Usamos la columna 'cantidad'
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error al obtener la cantidad actual del producto con ID: " + idInventario);
        }

        return -1; // Devuelve -1 si ocurre un error o no se encuentra el producto
    }

    /**
     * Verifica si un ID de inventario existe en la tabla `inventario`.
     *
     * @param inventoryId El ID del inventario que se desea verificar.
     * @return True si el ID existe, False en caso contrario.
     */
    public boolean isInventoryIdExists(int inventoryId) {
        String query = "SELECT COUNT(*) FROM inventario WHERE id = ?";
        try (Connection conn = MySQLConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, inventoryId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0; // Verifica si hay al menos una coincidencia
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error al verificar si el ID de inventario existe: " + inventoryId);
        }
        return false;
    }

    /**
     * Verifica si un producto específico existe en el inventario.
     *
     * Este método consulta la tabla `Inventario` para comprobar si existe un
     * registro asociado al ID del producto proporcionado.
     *
     * @param productId El ID del producto a verificar.
     * @return true si el producto existe en el inventario, false en caso
     * contrario.
     */
    public boolean productExistsInInventory(int productId) {
        String query = "SELECT COUNT(*) FROM Inventario WHERE id = ?";
        try (Connection conn = MySQLConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, productId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    return true; // El producto existe
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error al verificar si el producto existe en el inventario: " + productId);
        }
        return false; // El producto no existe
    }

    /**
     * Recupera la cantidad actual de un producto en el inventario.
     *
     * @param productId El ID del producto.
     * @return La cantidad actual si el producto existe, o 0 si no se encuentra.
     */
    public int getProductQuantity(int productId) {
        String query = "SELECT cantidad FROM inventario WHERE id_producto = ?";
        try (Connection conn = MySQLConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, productId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("cantidad"); // Devuelve la cantidad encontrada
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error al obtener la cantidad del producto en el inventario.");
        }
        return 0; // Devuelve 0 si el producto no existe o si ocurre un error
    }

    public int getMinQuantity(int productId) {
        String query = "SELECT cantidad_minima FROM Inventario WHERE id = ?";
        try (Connection conn = MySQLConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, productId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("cantidad_minima");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error al obtener la cantidad mínima para el producto con ID: " + productId);
        }
        return 0; // Valor por defecto si no se encuentra el producto
    }

}
