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

    /**
     * Actualiza los datos de una receta existente y sus ingredientes asociados.
     *
     * @param recipe La receta con los datos actualizados.
     * @return True si la operación fue exitosa, False en caso contrario.
     */
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

            // Actualiza la receta en la tabla Recetas
            stmtRecipe = conn.prepareStatement(queryUpdateRecipe);
            stmtRecipe.setString(1, recipe.getNombre());
            stmtRecipe.setString(2, recipe.getInstrucciones());
            stmtRecipe.setString(3, recipe.getTipo());
            stmtRecipe.setBytes(4, recipe.getFoto());
            stmtRecipe.setInt(5, recipe.getIdGrupo());
            stmtRecipe.setInt(6, recipe.getId());
            int rowsAffected = stmtRecipe.executeUpdate();

            System.out.println("Filas actualizadas en Recetas: " + rowsAffected);

            // Procesa cada ingrediente
            for (Product product : recipe.getIngredientes()) {
                // Verifica si el producto ya existe
                stmtCheck = conn.prepareStatement(queryCheckProduct);
                stmtCheck.setInt(1, recipe.getId());
                stmtCheck.setString(2, product.getNombreProducto());
                rsCheck = stmtCheck.executeQuery();

                if (rsCheck.next() && rsCheck.getInt(1) > 0) {
                    // Asigna valor predeterminado para 'tipo' si está vacío o nulo
                    if (product.getTipo() == null || product.getTipo().trim().isEmpty()) {
                        product.setTipo("Alimentación");
                        System.out.println("Asignado 'Alimentación' como tipo para el producto actualizado: " + product.getNombreProducto());
                    }
                    // Actualiza el producto existente
                    stmtUpdateProduct = conn.prepareStatement(queryUpdateProduct);
                    stmtUpdateProduct.setInt(1, product.getCantidad());
                    stmtUpdateProduct.setString(2, product.getTipo());
                    stmtUpdateProduct.setString(3, product.getCategoria());
                    stmtUpdateProduct.setInt(4, recipe.getId());
                    stmtUpdateProduct.setString(5, product.getNombreProducto());
                    stmtUpdateProduct.executeUpdate();
                    System.out.println("Producto actualizado: " + product.getNombreProducto());
                } else {
                    // Asigna valor predeterminado para 'tipo' si está vacío o nulo
                    if (product.getTipo() == null || product.getTipo().trim().isEmpty()) {
                        product.setTipo("Alimentación");
                        System.out.println("Asignado 'Alimentación' como tipo para el producto nuevo: " + product.getNombreProducto());
                    }
                    // Inserta el nuevo producto
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

            conn.commit(); // Confirma la transacción
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            if (conn != null) {
                try {
                    conn.rollback(); // Revertir!!
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
    public boolean deleteRecipe(int recipeId) {
        MealDAO mealDAO = new MealDAO();

        // Elimina comidas que referencian esta receta
        if (!mealDAO.deleteMealsByRecipeId(recipeId)) {
            System.err.println("Error al eliminar las comidas relacionadas con la receta.");
            return false; // Detienesi no se pudieron borrar las referencias
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

    /**
     * Elimina una receta y todas sus comidas asociadas de forma cascada.
     *
     * Realiza una eliminación en la base de datos para garantizar que tanto las
     * comidas como la receta sean eliminadas correctamente. Si ocurre un error,
     * revierte.
     *
     * @param recipeId el ID de la receta a eliminar
     * @return {@code true} si la receta se eliminó correctamente, {@code false}
     * en caso de error
     */
    public boolean deleteRecipeCascade(int recipeId) {
        String deleteMealsQuery = "DELETE FROM Comidas WHERE id_receta = ?";
        String deleteRecipeQuery = "DELETE FROM Recetas WHERE id = ?";
        try (Connection conn = MySQLConnection.getConnection()) {
            conn.setAutoCommit(false); // Inicia la transacción

            // Elimina comidas relacionadas
            try (PreparedStatement deleteMealsStmt = conn.prepareStatement(deleteMealsQuery)) {
                deleteMealsStmt.setInt(1, recipeId);
                int mealsDeleted = deleteMealsStmt.executeUpdate();
                System.out.println("Comidas eliminadas: " + mealsDeleted);
            }

            // Elimina la receta
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

    /**
     * Inserta una receta y sus productos asociados en la base de datos.
     *
     * Utiliza una transacción para insertar los datos de la receta en la tabla
     * `Recetas` y los ingredientes en la tabla `Receta_producto`. Revierte la
     * transacción si ocurre un error.
     *
     * @param recipe el objeto con la información de la receta y
     * sus ingredientes
     * @return  true si la receta y los productos fueron insertados
     * correctamente, false si ocurrió algún error
     */
    public boolean addRecipeAndProducts(Recipe recipe) {
        String queryRecipe = "INSERT INTO Recetas (nombre, instrucciones, tipo, foto, id_grupo) VALUES (?, ?, ?, ?, ?)";
        String queryRecipeProduct = "INSERT INTO Receta_producto (id_receta, nombre_producto, cantidad_necesaria, tipo, categoria) VALUES (?, ?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement stmtRecipe = null;
        PreparedStatement stmtProduct = null;

        try {
            conn = MySQLConnection.getConnection();
            conn.setAutoCommit(false); // Iniciar transacción

            // Guarda la receta en la tabla Recetas
            stmtRecipe = conn.prepareStatement(queryRecipe, Statement.RETURN_GENERATED_KEYS);
            stmtRecipe.setString(1, recipe.getNombre());
            stmtRecipe.setString(2, recipe.getInstrucciones());
            stmtRecipe.setString(3, recipe.getTipo());
            stmtRecipe.setBytes(4, recipe.getFoto());
            stmtRecipe.setInt(5, recipe.getIdGrupo());
            stmtRecipe.executeUpdate();

            // Obtiene el ID generado para la receta
            ResultSet rs = stmtRecipe.getGeneratedKeys();
            int recipeId = 0;
            if (rs.next()) {
                recipeId = rs.getInt(1);
            }
            System.out.println("Receta insertada con ID: " + recipeId);

            // Inserta los productos en Receta_producto sin el ID del producto
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
            stmtProduct.executeBatch(); // Ejecuta las inserciones

            conn.commit(); // Confirma la transacción
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            if (conn != null) {
                try {
                    conn.rollback(); // Revertir
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
