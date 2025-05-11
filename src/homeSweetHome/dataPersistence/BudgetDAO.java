/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package homeSweetHome.dataPersistence;

import homeSweetHome.model.Budget;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BudgetDAO {

    /**
     * Agrega un nuevo gasto a la base de datos.
     *
     * @param budget El objeto Budget que contiene los datos del gasto a
     * agregar.
     * @return True si el gasto fue agregado correctamente, False en caso
     * contrario.
     */
    public boolean addBudget(Budget budget) {
        String query = "INSERT INTO Gastos (nombre, categoria, monto, metodo_pago, fecha, descripcion, id_grupo) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = MySQLConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, budget.getNombre());
            stmt.setString(2, budget.getCategoria());
            stmt.setDouble(3, budget.getMonto());
            stmt.setString(4, budget.getMetodoPago());
            stmt.setDate(5, Date.valueOf(budget.getFecha()));
            stmt.setString(6, budget.getDescripcion());
            stmt.setInt(7, budget.getIdGrupo());

            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            
            e.printStackTrace();
            System.err.println("Error al agregar el gasto.");
        }
        
        return false;
    }

    /**
     * Recupera todos los gastos de la base de datos asociados a un grupo
     * específico.
     *
     * @param groupId El ID del grupo cuyos gastos se desean recuperar.
     * @return Una lista de objetos Budget representando los gastos.
     */
    public List<Budget> getBudgetsByGroup(int groupId) {
        
        String query = "SELECT * FROM Gastos WHERE id_grupo = ?";
        List<Budget> budgets = new ArrayList<>();

        try (Connection conn = MySQLConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, groupId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Budget budget = new Budget(
                            rs.getInt("id"),
                            rs.getString("nombre"),
                            rs.getString("categoria"),
                            rs.getDouble("monto"),
                            rs.getString("metodo_pago"),
                            rs.getDate("fecha").toLocalDate(),
                            rs.getString("descripcion"),
                            rs.getInt("id_grupo")
                    );
                    
                    budgets.add(budget);
                }
            }
            
        } catch (SQLException e) {
            
            e.printStackTrace();
            System.err.println("Error al recuperar los gastos.");
        }
        return budgets;
    }

    /**
     * Actualiza un gasto existente en la base de datos.
     *
     * @param budget El objeto Budget con los datos actualizados.
     * @return True si el gasto fue actualizado correctamente, False en caso
     * contrario.
     */
    public boolean updateBudget(Budget budget) {
        
        String query = "UPDATE Gastos SET nombre = ?, categoria = ?, monto = ?, metodo_pago = ?, fecha = ?, descripcion = ? WHERE id = ?";

        try (Connection conn = MySQLConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, budget.getNombre());
            stmt.setString(2, budget.getCategoria());
            stmt.setDouble(3, budget.getMonto());
            stmt.setString(4, budget.getMetodoPago());
            stmt.setDate(5, Date.valueOf(budget.getFecha()));
            stmt.setString(6, budget.getDescripcion());
            stmt.setInt(7, budget.getId());

            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            
            e.printStackTrace();
            System.err.println("Error al actualizar el gasto.");
        }
        
        return false;
    }

    /**
     * Elimina un gasto de la base de datos utilizando su ID.
     *
     * @param budgetId El ID del gasto a eliminar.
     * @return True si el gasto fue eliminado correctamente, False en caso
     * contrario.
     */
    public boolean deleteBudget(int budgetId) {
        
        String query = "DELETE FROM Gastos WHERE id = ?";

        try (Connection conn = MySQLConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, budgetId);
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            
            e.printStackTrace();
            System.err.println("Error al eliminar el gasto.");
        }
        
        return false;
    }
}
