package homeSweetHome.controller.recipe;

import homeSweetHome.dataPersistence.RecipeDAO;
import homeSweetHome.model.Recipe;
import homeSweetHome.model.Product;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

public class ViewRecipeViewController {

    @FXML
    private Label labelRecipeName;

    @FXML
    private Label labelRecipeCategory;

    @FXML
    private Label labelIngredients;

    @FXML
    private Label labelInstructions;

    @FXML
    private ImageView imgRecipe;

    @FXML
    private Button btnClose;

    private Recipe recipe;

    private final RecipeDAO recipeDAO = new RecipeDAO();

    @FXML
    public void initialize() {
        System.out.println("Inicializando vista de Ver Receta...");
    }

    public void setRecipe(Recipe recipe) {
        this.recipe = recipe;
        loadRecipeDetails();
    }

//    private void loadRecipeDetails() {
//        if (recipe != null) {
//            // Mostrar el nombre de la receta
//            System.out.println("Cargando detalles para la receta: " + recipe.getNombre());
//            labelRecipeName.setText(recipe.getNombre());
//
//            // Mostrar la categoría de la receta
//            labelRecipeCategory.setText(recipe.getTipo());
//
//            // Mostrar las instrucciones
//            labelInstructions.setText(recipe.getInstrucciones() != null ? recipe.getInstrucciones() : "No hay instrucciones disponibles.");
//
//            // Cargar los ingredientes desde el DAO
//            List<Product> ingredientes = recipeDAO.getProductsByRecipeId(recipe.getId());
//            if (ingredientes == null || ingredientes.isEmpty()) {
//                System.out.println("No hay ingredientes disponibles para esta receta.");
//                labelIngredients.setText("No hay ingredientes.");
//            } else {
//                // Combinar los nombres de los productos en un solo texto para mostrarlos
//                labelIngredients.setText(
//                        ingredientes.stream()
//                                .map(Product::getNombreProducto)
//                                .reduce((a, b) -> a + "\n" + b)
//                                .orElse("No hay ingredientes.")
//                );
//                System.out.println("Ingredientes cargados: " + ingredientes);
//            }
//
//            // Cargar imagen si existe
//            if (recipe.getFoto() != null) {
//                try {
//                    Image image = new Image(new ByteArrayInputStream(recipe.getFoto()));
//                    imgRecipe.setImage(image);
//                    imgRecipe.setFitHeight(150);
//                    imgRecipe.setFitWidth(150);
//                    imgRecipe.setPreserveRatio(true);
//                } catch (Exception e) {
//                    System.out.println("Error al cargar la imagen de la receta.");
//                    e.printStackTrace();
//                }
//            } else {
//                System.out.println("No hay imagen asociada a esta receta.");
//            }
//        } else {
//            System.out.println("La receta es nula, no se pueden cargar detalles.");
//        }
//    }
    private void loadRecipeDetails() {
        if (recipe != null) {
            // Mostrar el nombre de la receta
            System.out.println("Cargando detalles para la receta: " + recipe.getNombre());
            labelRecipeName.setText(recipe.getNombre());

            // Mostrar la categoría de la receta
            labelRecipeCategory.setText(recipe.getTipo());

            // Mostrar las instrucciones
            labelInstructions.setText(recipe.getInstrucciones() != null ? recipe.getInstrucciones() : "No hay instrucciones disponibles.");

            // Cargar los ingredientes desde el DAO
            List<Product> ingredientes = recipeDAO.getProductsByRecipeId(recipe.getId());
            if (ingredientes == null || ingredientes.isEmpty()) {
                System.out.println("No hay ingredientes disponibles para esta receta.");
                labelIngredients.setText("No hay ingredientes.");
            } else {
                // Mostrar nombre y cantidad de cada ingrediente
                StringBuilder ingredientesBuilder = new StringBuilder();
                for (Product ingrediente : ingredientes) {
                    ingredientesBuilder.append(ingrediente.getNombreProducto())
                            .append(" - Cantidad: ")
                            .append(ingrediente.getCantidad())
                            .append("\n");
                }
                labelIngredients.setText(ingredientesBuilder.toString());
                System.out.println("Ingredientes cargados: " + ingredientes);
            }

            // Cargar imagen si existe
            if (recipe.getFoto() != null) {
                try {
                    Image image = new Image(new ByteArrayInputStream(recipe.getFoto()));
                    imgRecipe.setImage(image);
                    imgRecipe.setFitHeight(150);
                    imgRecipe.setFitWidth(150);
                    imgRecipe.setPreserveRatio(true);
                } catch (Exception e) {
                    System.out.println("Error al cargar la imagen de la receta.");
                    e.printStackTrace();
                }
            } else {
                System.out.println("No hay imagen asociada a esta receta.");
            }
        } else {
            System.out.println("La receta es nula, no se pueden cargar detalles.");
        }
    }

    private void adjustImage() {
        if (recipe.getFoto() != null) {
            imgRecipe.setImage(new Image(new ByteArrayInputStream(recipe.getFoto())));
            imgRecipe.setFitHeight(150);
            imgRecipe.setFitWidth(150);
            imgRecipe.setPreserveRatio(true);
        } else {
            System.out.println("No hay imagen asociada a esta receta.");
        }
    }

    private void debugRecipeDetails() {
        System.out.println("Detalles de la receta:");
        System.out.println("Nombre: " + recipe.getNombre());
        System.out.println("Categoría: " + recipe.getTipo());
        System.out.println("Ingredientes: " + recipe.getIngredientes());
        System.out.println("Instrucciones: " + recipe.getInstrucciones());
    }

    @FXML
    private void closeView(ActionEvent event) {
        ((Stage) btnClose.getScene().getWindow()).close();
        System.out.println("Vista de Ver Receta cerrada.");
    }
}
