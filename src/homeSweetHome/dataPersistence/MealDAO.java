package homeSweetHome.dataPersistence;

import homeSweetHome.model.Meal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para gestionar las operaciones CRUD de la tabla Comidas y la vista
 * Vista_Comidas_Semanales.
 */
public class MealDAO {

    /**
     * Inserta una nueva comida semanal en la tabla Comidas.
     *
     * @param meal Objeto Meal que contiene los datos de la comida.
     * @return True si la operación fue exitosa, False en caso contrario.
     */
    public boolean addMeal(Meal meal) {
        String query = "INSERT INTO Comidas (dia_semana, id_receta, id_grupo) VALUES (?, ?, ?)";

        try (Connection conn = MySQLConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, meal.getDayOfWeek());
            stmt.setInt(2, meal.getRecipeId());
            stmt.setInt(3, meal.getGroupId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error al insertar la comida.");
        }
        return false;
    }

    /**
     * Recupera todas las comidas semanales desde la vista
     * Vista_Comidas_Semanales.
     *
     * @return Lista de objetos Meal que representan las comidas semanales.
     */
    public List<Meal> getAllMeals() {
        String query = "SELECT * FROM Vista_Comidas_Semanales";
        List<Meal> meals = new ArrayList<>();

        try (Connection conn = MySQLConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(query); ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Meal meal = new Meal(
                        rs.getInt("id"),
                        rs.getString("dia_semana"),
                        rs.getInt("id_receta"),
                        rs.getString("nombre_receta"),
                        rs.getString("tipo"), // Categoría
                        rs.getBytes("foto"),
                        rs.getInt("id_grupo")
                );
                meals.add(meal);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error al recuperar las comidas.");
        }
        return meals;
    }

    /**
     * Actualiza una comida existente en la tabla Comidas.
     *
     * @param meal Objeto Meal con los datos actualizados.
     * @return True si la operación fue exitosa, False en caso contrario.
     */
    public boolean updateMeal(Meal meal) {
        String query = "UPDATE Comidas SET dia_semana = ?, id_receta = ?, id_grupo = ? WHERE id = ?";

        try (Connection conn = MySQLConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, meal.getDayOfWeek());
            stmt.setInt(2, meal.getRecipeId());
            stmt.setInt(3, meal.getGroupId());
            stmt.setInt(4, meal.getId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error al actualizar la comida.");
        }
        return false;
    }

    /**
     * Elimina una comida semanal de la tabla Comidas.
     *
     * @param mealId ID de la comida a eliminar.
     * @return True si la operación fue exitosa, False en caso contrario.
     */
    public boolean deleteMeal(int mealId) {
        String query = "DELETE FROM Comidas WHERE id = ?";

        try (Connection conn = MySQLConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, mealId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error al eliminar la comida.");
        }
        return false;
    }

    /**
     * Recupera las comidas semanales para un día específico.
     *
     * @param dayOfWeek El día de la semana para filtrar (Ej: "Lunes").
     * @return Lista de objetos Meal para ese día.
     */
    public List<Meal> getMealsByDay(String dayOfWeek) {
        String query = "SELECT * FROM Vista_Comidas_Semanales WHERE dia_semana = ?";
        List<Meal> meals = new ArrayList<>();

        try (Connection conn = MySQLConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, dayOfWeek);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Meal meal = new Meal(
                            rs.getInt("id"),
                            rs.getString("dia_semana"),
                            rs.getInt("id_receta"),
                            rs.getString("nombre_receta"),
                            rs.getString("tipo"),
                            rs.getBytes("foto"),
                            rs.getInt("id_grupo")
                    );
                    meals.add(meal);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error al recuperar comidas por día.");
        }
        return meals;
    }

    /**
     * Elimina todas las comidas semanales de la tabla "Comidas".
     *
     * Este método utiliza una consulta SQL DELETE para eliminar todos los
     * registros de la tabla "Comidas", asegurando que se puedan generar nuevas
     * comidas desde cero.
     *
     * @return true si la operación fue exitosa, false si ocurrió algún error.
     */
    public boolean deleteAllMeals() {
        String query = "DELETE FROM Comidas";
        try (Connection conn = MySQLConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.executeUpdate();
            return true; // Operación exitosa
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error al eliminar todas las comidas.");
            return false; // Operación fallida
        }
    }

//    public boolean deleteMealsByRecipeId(int recipeId) {
//        String query = "DELETE FROM Comidas WHERE id_receta = ?";
//        try (Connection conn = MySQLConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
//            stmt.setInt(1, recipeId);
//            return stmt.executeUpdate() > 0;
//        } catch (SQLException e) {
//            e.printStackTrace();
//            System.err.println("Error al eliminar las comidas relacionadas con la receta.");
//        }
//        return false;
//    }
    public boolean deleteMealsByRecipeId(int recipeId) {
        String query = "DELETE FROM Comidas WHERE id_receta = ?";
        try (Connection conn = MySQLConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, recipeId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Meal> getMealsByRecipeId(int recipeId) {
        List<Meal> meals = new ArrayList<>();
        String query = "SELECT * FROM Comidas WHERE id_receta = ?";

        try (Connection conn = MySQLConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, recipeId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Meal meal = new Meal();
                    meal.setId(rs.getInt("id"));
                    meal.setRecipeId(rs.getInt("id_receta"));
                    meal.setDayOfWeek(rs.getString("dia_semana")); // Ajusta según el esquema de tu base de datos
                    meals.add(meal);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error al recuperar las comidas relacionadas con la receta ID: " + recipeId);
        }
        return meals;
    }

}
