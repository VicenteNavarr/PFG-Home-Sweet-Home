package homeSweetHome.dataPersistence;

import homeSweetHome.model.Product;
import homeSweetHome.model.Recipe;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para gestionar las operaciones CRUD de la tabla Recetas y su relación con
 * productos.
 */
public class RecipeDAO {

    /**
     * Inserta una nueva receta en la tabla Recetas y asocia los ingredientes
     * necesarios en Receta_producto.
     *
     * @param recipe La receta a insertar.
     * @return True si la operación fue exitosa, False en caso contrario.
     */
    public boolean addRecipe(Recipe recipe) {
        String queryRecipe = "INSERT INTO Recetas (nombre, instrucciones, tipo, foto, id_grupo) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = MySQLConnection.getConnection(); PreparedStatement stmtRecipe = conn.prepareStatement(queryRecipe, Statement.RETURN_GENERATED_KEYS)) {

            stmtRecipe.setString(1, recipe.getNombre());
            stmtRecipe.setString(2, recipe.getInstrucciones());
            stmtRecipe.setString(3, recipe.getTipo());
            stmtRecipe.setBytes(4, recipe.getFoto());
            stmtRecipe.setInt(5, recipe.getIdGrupo());

            int rowsAffected = stmtRecipe.executeUpdate();
            if (rowsAffected > 0) {
                ResultSet generatedKeys = stmtRecipe.getGeneratedKeys();
                if (generatedKeys.next()) {
                    recipe.setId(generatedKeys.getInt(1)); // Asignar el ID generado
                }

                // Añadir los productos asociados a la receta en Receta_producto
                return addProductsToRecipe(recipe.getId(), recipe.getIngredientes());
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error al insertar la receta.");
        }
        return false;
    }

    /**
     * Guarda una receta en la base de datos y registra los ingredientes en la
     * tabla `Receta_producto`.
     *
     * @param recipe La receta que se desea guardar.
     * @return True si la operación fue exitosa, False en caso contrario.
     */
    /**
     * Guarda una receta en la base de datos y registra los ingredientes en la
     * tabla `Receta_producto`.
     *
     * @param recipe La receta que se desea guardar.
     * @return True si la operación fue exitosa, False en caso contrario.
     */
//    public boolean addRecipe(Recipe recipe) {
//        String recipeQuery = "INSERT INTO Recetas (nombre, tipo, descripcion, foto, id_grupo) VALUES (?, ?, ?, ?, ?)";
//        String ingredientQuery = "INSERT INTO Receta_producto (id_receta, id_producto, cantidad_necesaria) VALUES (?, ?, ?)";
//
//        try (Connection conn = MySQLConnection.getConnection()) {
//            // Guardar la receta
//            try (PreparedStatement recipeStmt = conn.prepareStatement(recipeQuery, Statement.RETURN_GENERATED_KEYS)) {
//                recipeStmt.setString(1, recipe.getNombre());
//                recipeStmt.setString(2, recipe.getTipo());
//                recipeStmt.setString(3, recipe.getInstrucciones());
//                recipeStmt.setBytes(4, recipe.getFoto());
//                recipeStmt.setInt(5, recipe.getIdGrupo());
//                recipeStmt.executeUpdate();
//
//                // Obtener el ID generado de la receta
//                ResultSet rs = recipeStmt.getGeneratedKeys();
//                if (rs.next()) {
//                    int recipeId = rs.getInt(1);
//
//                    // Registrar los ingredientes en `Receta_producto`
//                    try (PreparedStatement ingredientStmt = conn.prepareStatement(ingredientQuery)) {
//                        for (Product ingredient : recipe.getIngredientes()) {
//                            ingredientStmt.setInt(1, recipeId);
//                            ingredientStmt.setInt(2, ingredient.getId());
//                            ingredientStmt.setInt(3, ingredient.getCantidad());
//                            ingredientStmt.addBatch();
//                        }
//                        ingredientStmt.executeBatch();
//                    }
//                }
//            }
//            return true;
//        } catch (SQLException e) {
//            e.printStackTrace();
//            System.err.println("Error al guardar la receta y sus ingredientes.");
//            return false;
//        }
//    }
    /**
     * Recupera todas las recetas de la tabla Recetas.
     *
     * @return Lista de recetas.
     */
    public List<Recipe> getAllRecipes() {
        String query = "SELECT * FROM Recetas";
        List<Recipe> recipes = new ArrayList<>();

        try (Connection conn = MySQLConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(query); ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Recipe recipe = new Recipe(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getString("instrucciones"),
                        rs.getString("tipo"),
                        rs.getBytes("foto"),
                        rs.getInt("id_grupo"),
                        null // Los ingredientes se pueden cargar por separado
                );
                recipes.add(recipe);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error al recuperar las recetas.");
        }
        return recipes;
    }

    /**
     * Recupera una receta específica por su ID.
     *
     * @param recipeId El ID de la receta.
     * @return La receta correspondiente, o null si no se encuentra.
     */
    public Recipe getRecipeById(int recipeId) {
        String query = "SELECT * FROM Recetas WHERE id = ?";
        try (Connection conn = MySQLConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, recipeId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Recipe(
                            rs.getInt("id"),
                            rs.getString("nombre"),
                            rs.getString("instrucciones"),
                            rs.getString("tipo"),
                            rs.getBytes("foto"),
                            rs.getInt("id_grupo"),
                            getProductsByRecipeId(recipeId) // Cargar los ingredientes
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error al recuperar la receta por ID.");
        }
        return null;
    }

//    /**
//     * Actualiza los datos de una receta existente y sus ingredientes asociados.
//     *
//     * @param recipe La receta con los datos actualizados.
//     * @return True si la operación fue exitosa, False en caso contrario.
//     */
//    public boolean updateRecipe(Recipe recipe) {
//        String query = "UPDATE Recetas SET nombre = ?, instrucciones = ?, tipo = ?, foto = ?, id_grupo = ? WHERE id = ?";
//
//        try (Connection conn = MySQLConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
//
//            stmt.setString(1, recipe.getNombre());
//            stmt.setString(2, recipe.getInstrucciones());
//            stmt.setString(3, recipe.getTipo());
//            stmt.setBytes(4, recipe.getFoto());
//            stmt.setInt(5, recipe.getIdGrupo());
//            stmt.setInt(6, recipe.getId());
//
//            int rowsAffected = stmt.executeUpdate();
//            if (rowsAffected > 0) {
//                // Primero elimina los productos asociados actuales
//                deleteProductsFromRecipe(recipe.getId());
//                // Luego añade los productos actualizados
//                return addProductsToRecipe(recipe.getId(), recipe.getIngredientes());
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//            System.err.println("Error al actualizar la receta.");
//        }
//        return false;
//    }
//    public boolean updateRecipe(Recipe recipe) {
//        String queryUpdateRecipe = "UPDATE Recetas SET nombre = ?, instrucciones = ?, tipo = ?, foto = ?, id_grupo = ? WHERE id = ?";
//        String queryRecipeProduct = "INSERT INTO Receta_producto (id_receta, nombre_producto, cantidad_necesaria, tipo, categoria) VALUES (?, ?, ?, ?, ?)";
//
//        Connection conn = null;
//        PreparedStatement stmtRecipe = null;
//        PreparedStatement stmtProduct = null;
//
//        try {
//            conn = MySQLConnection.getConnection();
//            conn.setAutoCommit(false); // Iniciar transacción
//
//            // Actualizar la receta en la tabla Recetas
//            stmtRecipe = conn.prepareStatement(queryUpdateRecipe);
//            stmtRecipe.setString(1, recipe.getNombre());
//            stmtRecipe.setString(2, recipe.getInstrucciones());
//            stmtRecipe.setString(3, recipe.getTipo());
//            stmtRecipe.setBytes(4, recipe.getFoto());
//            stmtRecipe.setInt(5, recipe.getIdGrupo());
//            stmtRecipe.setInt(6, recipe.getId());
//            int rowsAffected = stmtRecipe.executeUpdate();
//
//            System.out.println("Filas actualizadas en Recetas: " + rowsAffected);
//
//            // Insertar ingredientes nuevos en Receta_producto
//            stmtProduct = conn.prepareStatement(queryRecipeProduct);
//            for (Product product : recipe.getIngredientes()) {
//                stmtProduct.setInt(1, recipe.getId()); // ID existente de la receta
//                stmtProduct.setString(2, product.getNombreProducto());
//                stmtProduct.setInt(3, product.getCantidad());
//                stmtProduct.setString(4, product.getTipo());
//                stmtProduct.setString(5, product.getCategoria());
//                stmtProduct.addBatch(); // Añadir al batch para una inserción más eficiente
//                System.out.println("Producto preparado para insertar: " + product.getNombreProducto());
//            }
//            stmtProduct.executeBatch(); // Ejecutar las inserciones
//
//            conn.commit(); // Confirmar la transacción
//            return true;
//
//        } catch (SQLException e) {
//            e.printStackTrace();
//            if (conn != null) {
//                try {
//                    conn.rollback(); // Revertir la transacción en caso de error
//                } catch (SQLException rollbackEx) {
//                    rollbackEx.printStackTrace();
//                }
//            }
//            return false;
//        } finally {
//            try {
//                if (stmtRecipe != null) {
//                    stmtRecipe.close();
//                }
//                if (stmtProduct != null) {
//                    stmtProduct.close();
//                }
//                if (conn != null) {
//                    conn.close();
//                }
//            } catch (SQLException closeEx) {
//                closeEx.printStackTrace();
//            }
//        }
//    }
    public boolean updateRecipe(Recipe recipe) {
        String queryUpdateRecipe = "UPDATE Recetas SET nombre = ?, instrucciones = ?, tipo = ?, foto = ?, id_grupo = ? WHERE id = ?";
        String queryCheckProduct = "SELECT COUNT(*) FROM Receta_producto WHERE id_receta = ? AND nombre_producto = ?";
        String queryUpdateProduct = "UPDATE Receta_producto SET cantidad_necesaria = ?, tipo = ?, categoria = ? WHERE id_receta = ? AND nombre_producto = ?";
        String queryInsertProduct = "INSERT INTO Receta_producto (id_receta, nombre_producto, cantidad_necesaria, tipo, categoria) VALUES (?, ?, ?, ?, ?)";

        Connection conn = null;
        PreparedStatement stmtRecipe = null;
        PreparedStatement stmtCheck = null;
        PreparedStatement stmtUpdateProduct = null;
        PreparedStatement stmtInsertProduct = null;
        ResultSet rsCheck = null;

        try {
            conn = MySQLConnection.getConnection();
            conn.setAutoCommit(false); // Iniciar transacción

            // Actualizar la receta en la tabla Recetas
            stmtRecipe = conn.prepareStatement(queryUpdateRecipe);
            stmtRecipe.setString(1, recipe.getNombre());
            stmtRecipe.setString(2, recipe.getInstrucciones());
            stmtRecipe.setString(3, recipe.getTipo());
            stmtRecipe.setBytes(4, recipe.getFoto());
            stmtRecipe.setInt(5, recipe.getIdGrupo());
            stmtRecipe.setInt(6, recipe.getId());
            int rowsAffected = stmtRecipe.executeUpdate();

            System.out.println("Filas actualizadas en Recetas: " + rowsAffected);

            // Procesar cada ingrediente
            for (Product product : recipe.getIngredientes()) {
                // Verificar si el producto ya existe
                stmtCheck = conn.prepareStatement(queryCheckProduct);
                stmtCheck.setInt(1, recipe.getId());
                stmtCheck.setString(2, product.getNombreProducto());
                rsCheck = stmtCheck.executeQuery();

                if (rsCheck.next() && rsCheck.getInt(1) > 0) {
                    // Asignar valor predeterminado para 'tipo' si está vacío o nulo
                    if (product.getTipo() == null || product.getTipo().trim().isEmpty()) {
                        product.setTipo("Alimentación");
                        System.out.println("Asignado 'Alimentación' como tipo para el producto actualizado: " + product.getNombreProducto());
                    }
                    // Actualizar el producto existente
                    stmtUpdateProduct = conn.prepareStatement(queryUpdateProduct);
                    stmtUpdateProduct.setInt(1, product.getCantidad());
                    stmtUpdateProduct.setString(2, product.getTipo());
                    stmtUpdateProduct.setString(3, product.getCategoria());
                    stmtUpdateProduct.setInt(4, recipe.getId());
                    stmtUpdateProduct.setString(5, product.getNombreProducto());
                    stmtUpdateProduct.executeUpdate();
                    System.out.println("Producto actualizado: " + product.getNombreProducto());
                } else {
                    // Asignar valor predeterminado para 'tipo' si está vacío o nulo
                    if (product.getTipo() == null || product.getTipo().trim().isEmpty()) {
                        product.setTipo("Alimentación");
                        System.out.println("Asignado 'Alimentación' como tipo para el producto nuevo: " + product.getNombreProducto());
                    }
                    // Insertar el nuevo producto
                    stmtInsertProduct = conn.prepareStatement(queryInsertProduct);
                    stmtInsertProduct.setInt(1, recipe.getId());
                    stmtInsertProduct.setString(2, product.getNombreProducto());
                    stmtInsertProduct.setInt(3, product.getCantidad());
                    stmtInsertProduct.setString(4, product.getTipo());
                    stmtInsertProduct.setString(5, product.getCategoria());
                    stmtInsertProduct.executeUpdate();
                    System.out.println("Producto insertado: " + product.getNombreProducto());
                }
            }

            conn.commit(); // Confirmar la transacción
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            if (conn != null) {
                try {
                    conn.rollback(); // Revertir la transacción en caso de error
                } catch (SQLException rollbackEx) {
                    rollbackEx.printStackTrace();
                }
            }
            return false;
        } finally {
            try {
                if (stmtRecipe != null) {
                    stmtRecipe.close();
                }
                if (stmtCheck != null) {
                    stmtCheck.close();
                }
                if (stmtUpdateProduct != null) {
                    stmtUpdateProduct.close();
                }
                if (stmtInsertProduct != null) {
                    stmtInsertProduct.close();
                }
                if (rsCheck != null) {
                    rsCheck.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException closeEx) {
                closeEx.printStackTrace();
            }
        }
    }

    /**
     * Elimina una receta de la tabla Recetas y sus asociaciones en
     * Receta_producto.
     *
     * @param recipeId El ID de la receta a eliminar.
     * @return True si la operación fue exitosa, False en caso contrario.
     */
//    public boolean deleteRecipe(int recipeId) {
//        String query = "DELETE FROM Recetas WHERE id = ?";
//
//        try (Connection conn = MySQLConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
//
//            stmt.setInt(1, recipeId);
//            return stmt.executeUpdate() > 0;
//        } catch (SQLException e) {
//            e.printStackTrace();
//            System.err.println("Error al eliminar la receta.");
//        }
//        return false;
//    }
    public boolean deleteRecipe(int recipeId) {
        MealDAO mealDAO = new MealDAO();

        // Eliminar comidas que referencian esta receta
        if (!mealDAO.deleteMealsByRecipeId(recipeId)) {
            System.err.println("Error al eliminar las comidas relacionadas con la receta.");
            return false; // Detener si no se pudieron borrar las referencias
        }

        String query = "DELETE FROM Recetas WHERE id = ?";
        try (Connection conn = MySQLConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, recipeId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error al eliminar la receta.");
        }
        return false;
    }

    public boolean deleteRecipeCascade(int recipeId) {
        String deleteMealsQuery = "DELETE FROM Comidas WHERE id_receta = ?";
        String deleteRecipeQuery = "DELETE FROM Recetas WHERE id = ?";
        try (Connection conn = MySQLConnection.getConnection()) {
            conn.setAutoCommit(false); // Inicia la transacción

            // Eliminar comidas relacionadas
            try (PreparedStatement deleteMealsStmt = conn.prepareStatement(deleteMealsQuery)) {
                deleteMealsStmt.setInt(1, recipeId);
                int mealsDeleted = deleteMealsStmt.executeUpdate();
                System.out.println("Comidas eliminadas: " + mealsDeleted);
            }

            // Eliminar la receta
            try (PreparedStatement deleteRecipeStmt = conn.prepareStatement(deleteRecipeQuery)) {
                deleteRecipeStmt.setInt(1, recipeId);
                int recipeDeleted = deleteRecipeStmt.executeUpdate();
                System.out.println("Receta eliminada: " + recipeDeleted);

                conn.commit(); // Confirma la transacción
                return recipeDeleted > 0;
            } catch (SQLException e) {
                conn.rollback(); // Revertir la transacción en caso de error
                System.err.println("Error al eliminar la receta: " + e.getMessage());
                e.printStackTrace();
                return false;
            }
        } catch (SQLException e) {
            System.err.println("Error en la conexión o transacción: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Recupera los productos (ingredientes) asociados a una receta específica.
     *
     * @param recipeId El ID de la receta.
     * @return Lista de productos necesarios para la receta.
     */
//    public List<Product> getProductsByRecipeId(int recipeId) {
//        String query = "SELECT p.id, p.nombreProducto, rp.cantidad_necesaria, p.categoria, p.medida "
//                + "FROM inventario p "
//                + "JOIN Receta_producto rp ON p.id = rp.id_producto "
//                + "WHERE rp.id_receta = ?";
//        List<Product> products = new ArrayList<>();
//
//        try (Connection conn = MySQLConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
//
//            stmt.setInt(1, recipeId);
//            try (ResultSet rs = stmt.executeQuery()) {
//                while (rs.next()) {
//                    Product product = new Product(
//                            rs.getInt("id"),
//                            rs.getString("nombreProducto"),
//                            rs.getInt("cantidad_necesaria"), // Cantidad requerida para la receta
//                            0, // Cantidad mínima no relevante
//                            0, // Cantidad máxima no relevante
//                            rs.getString("medida"),
//                            rs.getString("categoria"),
//                            0, // ID del grupo no relevante aquí
//                            null // Fecha no relevante
//                    );
//                    products.add(product);
//                }
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//            System.err.println("Error al recuperar los productos de la receta.");
//        }
//        return products;
//    }
//    public List<Product> getProductsByRecipeId(int recipeId) {
//        String query = "SELECT p.id, p.nombre_producto, rp.cantidad_necesaria, p.categoria, p.tipo "
//                + "FROM inventario p "
//                + "JOIN Receta_producto rp ON p.id = rp.id_producto "
//                + "WHERE rp.id_receta = ?";
//        List<Product> products = new ArrayList<>();
//
//        try (Connection conn = MySQLConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
//            stmt.setInt(1, recipeId);
//            try (ResultSet rs = stmt.executeQuery()) {
//                while (rs.next()) {
//                    Product product = new Product(
//                            rs.getInt("id"),
//                            rs.getString("nombre_producto"),
//                            rs.getInt("cantidad_necesaria"),
//                            0, // Cantidad mínima por defecto
//                            0, // Cantidad máxima por defecto
//                            null, // Columna 'medida' eliminada o no utilizada
//                            rs.getString("categoria"),
//                            0, // ID del grupo por defecto
//                            null // Fecha por defecto
//                    );
//                    products.add(product);
//                }
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//            System.err.println("Error al recuperar los productos de la receta.");
//        }
//        return products;
//    }
    public List<Product> getProductsByRecipeId(int recipeId) {
        String query = "SELECT rp.nombre_producto, rp.cantidad_necesaria, rp.categoria, rp.tipo "
                + "FROM Receta_producto rp "
                + "WHERE rp.id_receta = ?";
        List<Product> products = new ArrayList<>();

        try (Connection conn = MySQLConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, recipeId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Product product = new Product(
                            0, // ID no disponible en la tabla Receta_producto
                            rs.getString("nombre_producto"),
                            rs.getInt("cantidad_necesaria"),
                            0, // Cantidad mínima por defecto
                            0, // Cantidad máxima por defecto
                            null, // Columna 'medida' no utilizada
                            rs.getString("categoria"),
                            0, // ID del grupo por defecto
                            null // Fecha no aplicable
                    );
                    products.add(product);
                    System.out.println("Producto cargado: " + product.getNombreProducto() + " (Cantidad: " + product.getCantidad() + ")");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error al recuperar los productos de la receta.");
        }

        System.out.println("Productos totales encontrados para receta con ID " + recipeId + ": " + products.size());
        return products;
    }

    /**
     * Recupera los productos necesarios para una receta específica desde la
     * tabla `Receta_producto`.
     *
     * Este método consulta la tabla `Receta_producto` y utiliza datos del
     * inventario relacionado.
     *
     * @param recipeId El ID de la receta.
     * @return Lista de productos necesarios para la receta.
     */
//    public List<Product> getProductsFromRecipe(int recipeId) {
//        String query = "SELECT rp.id_producto, i.nombre_producto, rp.cantidad_necesaria, i.categoria, i.tipo "
//                + "FROM Receta_producto rp "
//                + "JOIN Inventario i ON rp.id_producto = i.id "
//                + "WHERE rp.id_receta = ?";
//        List<Product> products = new ArrayList<>();
//
//        try (Connection conn = MySQLConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
//            stmt.setInt(1, recipeId);
//            try (ResultSet rs = stmt.executeQuery()) {
//                while (rs.next()) {
//                    Product product = new Product(
//                            rs.getInt("id_producto"),
//                            rs.getString("nombre_producto"),
//                            rs.getInt("cantidad_necesaria"),
//                            0, // Cantidad mínima por defecto
//                            0, // Cantidad máxima por defecto
//                            null, // Medida no relevante
//                            rs.getString("categoria"),
//                            0, // ID del grupo por defecto
//                            null // Fecha por defecto
//                    );
//                    products.add(product);
//                }
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//            System.err.println("Error al recuperar los productos de la receta.");
//        }
//        return products;
//    }
    public List<Product> getProductsFromRecipe(int recipeId) {
        String query = "SELECT rp.nombre_producto, rp.cantidad_necesaria, rp.tipo, rp.categoria "
                + "FROM Receta_producto rp "
                + "WHERE rp.id_receta = ?";
        List<Product> products = new ArrayList<>();

        try (Connection conn = MySQLConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, recipeId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Product product = new Product();
                    product.setNombreProducto(rs.getString("nombre_producto")); // Nombre del producto
                    product.setCantidad(rs.getInt("cantidad_necesaria")); // Cantidad necesaria
                    product.setTipo(rs.getString("tipo")); // Tipo ("Gramos", "Cantidad", etc.)
                    product.setCategoria(rs.getString("categoria")); // Categoría del producto
                    products.add(product);
                    System.out.println("Producto recuperado: Nombre=" + product.getNombreProducto());
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error al recuperar los productos de la receta.");
        }
        return products;
    }

    /**
     * Elimina todos los productos asociados a una receta en la tabla
     * Receta_producto.
     *
     * @param recipeId El ID de la receta.
     * @return True si la operación fue exitosa, False en caso contrario.
     */
    public boolean deleteProductsFromRecipe(int recipeId) {
        String query = "DELETE FROM Receta_producto WHERE id_receta = ?";

        try (Connection conn = MySQLConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, recipeId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error al eliminar los productos asociados a la receta.");
        }
        return false;
    }

    /**
     * Asocia productos a una receta en la tabla Receta_producto.
     *
     * @param recipeId El ID de la receta.
     * @param productos Lista de productos necesarios para la receta.
     * @return True si la operación fue exitosa, False en caso contrario.
     */
//    public boolean addProductsToRecipe(int recipeId, List<Product> productos) {
//        String query = "INSERT INTO Receta_producto (id_receta, id_producto, cantidad_necesaria) VALUES (?, ?, ?)";
//
//        try (Connection conn = MySQLConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
//            for (Product product : productos) {
//                stmt.setInt(1, recipeId);
//                stmt.setInt(2, product.getId());
//                stmt.setInt(3, product.getCantidad());
//                stmt.addBatch(); // Añadir al lote
//            }
//
//            stmt.executeBatch(); // Ejecutar lote
//            return true;
//        } catch (SQLException e) {
//            e.printStackTrace();
//            System.err.println("Error al añadir los ingredientes a la receta.");
//        }
//        return false;
//    }
    public boolean addProductsToRecipe(int recipeId, List<Product> productos) {
        String query = "INSERT INTO Receta_producto (id_receta, id_producto, cantidad_necesaria) VALUES (?, ?, ?)";

        try (Connection conn = MySQLConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            for (Product product : productos) {
                if (product.getId() == 0) { // Verifica que el ID del producto no sea nulo o inválido
                    System.err.println("Producto inválido: " + product.getNombreProducto());
                    continue; // Salta el producto que no tiene ID válido
                }

                stmt.setInt(1, recipeId); // ID de la receta
                stmt.setInt(2, product.getId()); // ID del producto (debe estar en inventario)
                stmt.setInt(3, product.getCantidad()); // Cantidad necesaria
                stmt.addBatch();
            }

            stmt.executeBatch(); // Ejecutar lote
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error al añadir los ingredientes a la receta.");
        }
        return false;
    }

    public boolean addRecipeAndProducts(Recipe recipe) {
        String queryRecipe = "INSERT INTO Recetas (nombre, instrucciones, tipo, foto, id_grupo) VALUES (?, ?, ?, ?, ?)";
        String queryRecipeProduct = "INSERT INTO Receta_producto (id_receta, nombre_producto, cantidad_necesaria, tipo, categoria) VALUES (?, ?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement stmtRecipe = null;
        PreparedStatement stmtProduct = null;

        try {
            conn = MySQLConnection.getConnection();
            conn.setAutoCommit(false); // Iniciar transacción

            // Guardar la receta en la tabla Recetas
            stmtRecipe = conn.prepareStatement(queryRecipe, Statement.RETURN_GENERATED_KEYS);
            stmtRecipe.setString(1, recipe.getNombre());
            stmtRecipe.setString(2, recipe.getInstrucciones());
            stmtRecipe.setString(3, recipe.getTipo());
            stmtRecipe.setBytes(4, recipe.getFoto());
            stmtRecipe.setInt(5, recipe.getIdGrupo());
            stmtRecipe.executeUpdate();

            // Obtener el ID generado para la receta
            ResultSet rs = stmtRecipe.getGeneratedKeys();
            int recipeId = 0;
            if (rs.next()) {
                recipeId = rs.getInt(1);
            }
            System.out.println("Receta insertada con ID: " + recipeId);

            // Insertar los productos en Receta_producto sin el ID del producto
            stmtProduct = conn.prepareStatement(queryRecipeProduct);
            for (Product product : recipe.getIngredientes()) {
                stmtProduct.setInt(1, recipeId); // ID de la receta
                stmtProduct.setString(2, product.getNombreProducto()); // Nombre del producto
                stmtProduct.setInt(3, product.getCantidad()); // Cantidad necesaria
                stmtProduct.setString(4, product.getTipo()); // Tipo
                stmtProduct.setString(5, product.getCategoria()); // Categoría
                stmtProduct.addBatch(); // Añadir al batch
                System.out.println("Producto preparado para insertar: " + product.getNombreProducto());
            }
            stmtProduct.executeBatch(); // Ejecutar las inserciones

            conn.commit(); // Confirmar la transacción
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            if (conn != null) {
                try {
                    conn.rollback(); // Revertir la transacción en caso de error
                } catch (SQLException rollbackEx) {
                    rollbackEx.printStackTrace();
                }
            }
            return false;
        } finally {
            try {
                if (stmtRecipe != null) {
                    stmtRecipe.close();
                }
                if (stmtProduct != null) {
                    stmtProduct.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException closeEx) {
                closeEx.printStackTrace();
            }
        }
    }

}
