package homeSweetHome.controller.recipe;

import homeSweetHome.dataPersistence.MealDAO;
import homeSweetHome.model.Recipe;
import homeSweetHome.dataPersistence.RecipeDAO;
import homeSweetHome.model.Meal;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.geometry.Pos;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.io.ByteArrayInputStream;
import java.util.Optional;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * Controlador para la vista principal de recetas.
 */
public class RecipeViewController implements Initializable {

    @FXML
    private Button btnOpenAddNewRecipe;

    @FXML
    private GridPane recipeGrid; // Contenedor para las recetas

    private RecipeDAO recipeDAO; // DAO para interactuar con la base de datos

    private MealDAO mealDAO = new MealDAO();

    /**
     * Inicializa el controlador.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        recipeDAO = new RecipeDAO(); // Inicializa el DAO
        loadRecipes(); // Carga las recetas al inicio

        mealDAO = new MealDAO(); // Inicialización de MealDAO
        System.out.println("MealDAO inicializado correctamente.");
    }

    /**
     * Carga todas las recetas desde la base de datos y las muestra en el
     * GridPane.
     */
    private void loadRecipes() {
        List<Recipe> recipes = recipeDAO.getAllRecipes(); // Obtén todas las recetas
        recipeGrid.getChildren().clear(); // Limpia el GridPane

        int row = 0;
        int col = 0;

        for (Recipe recipe : recipes) {
            addRecipeToGrid(recipe, row, col);
            col++;
            if (col == 4) { // Cambia de fila después de 3 columnas
                col = 0;
                row++;
            }
        }
    }

//    /**
//     * Añade una receta al GridPane como una tarjeta.
//     *
//     * @param recipe La receta a mostrar.
//     * @param row La fila en la que se añadirá.
//     * @param col La columna en la que se añadirá.
//     */
//    private void addRecipeToGrid(Recipe recipe, int row, int col) {
//        VBox recipeCard = new VBox();
//        recipeCard.setAlignment(Pos.CENTER);
//        recipeCard.setSpacing(10);
//
//        // Imagen de la receta
//        ImageView imageView = new ImageView();
//        if (recipe.getFoto() != null) {
//            imageView.setImage(new Image(new ByteArrayInputStream(recipe.getFoto())));
//        } else {
//            imageView.setImage(new Image("ruta/a/imagen_por_defecto.jpg")); // Imagen por defecto
//        }
//        imageView.setFitWidth(100);
//        imageView.setFitHeight(100);
//
//        // Nombre de la receta
//        Label recipeName = new Label(recipe.getNombre());
//        recipeName.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
//
//        // Botones de acción
//        Button btnModify = new Button("Modificar");
//        btnModify.setOnAction(e -> modifyRecipe(recipe));
//        Button btnView = new Button("Ver");
//        btnView.setOnAction(e -> viewRecipe(recipe));
//        Button btnDelete = new Button("Eliminar");
//        btnDelete.setOnAction(e -> deleteRecipe(recipe));
//
//        HBox buttonBox = new HBox(btnModify, btnView, btnDelete);
//        buttonBox.setSpacing(5);
//        buttonBox.setAlignment(Pos.CENTER);
//
//        // Añadir todos los elementos a la "card"
//        recipeCard.getChildren().addAll(imageView, recipeName, buttonBox);
//
//        // Añadir la "card" al GridPane
//        recipeGrid.add(recipeCard, col, row);
//    }
//    private void addRecipeToGrid(Recipe recipe, int row, int col) {
//        VBox recipeCard = new VBox();
//        recipeCard.setAlignment(Pos.CENTER);
//        recipeCard.setSpacing(10);
//
//        // Imagen de la receta
//        ImageView imageView = new ImageView();
//        try {
//            if (recipe.getFoto() != null && recipe.getFoto().length > 0) {
//                imageView.setImage(new Image(new ByteArrayInputStream(recipe.getFoto())));
//            } else {
//                imageView.setImage(new Image(getClass().getResource("/homeSweetHome/view/images/add-image.jpg").toExternalForm())); // Imagen por defecto
//            }
//        } catch (Exception e) {
//            System.err.println("Error al cargar la imagen: " + e.getMessage());
//            imageView.setImage(new Image(getClass().getResource("/homeSweetHome/view/images/add-image.jpg").toExternalForm()));
//        }
//        imageView.setFitWidth(100);
//        imageView.setFitHeight(100);
//
//        // Nombre de la receta
//        Label recipeName = new Label(recipe.getNombre());
//        recipeName.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
//
//        // Botones de acción
//        Button btnModify = new Button("Modificar");
//        btnModify.setOnAction(e -> modifyRecipe(recipe));
//        Button btnView = new Button("Ver");
//        btnView.setOnAction(e -> viewRecipe(recipe));
//        Button btnDelete = new Button("Eliminar");
//        btnDelete.setOnAction(e -> deleteRecipe(recipe));
//
//        HBox buttonBox = new HBox(btnModify, btnView, btnDelete);
//        buttonBox.setSpacing(5);
//        buttonBox.setAlignment(Pos.CENTER);
//
//        // Añadir todos los elementos a la "card"
//        recipeCard.getChildren().addAll(imageView, recipeName, buttonBox);
//
//        // Añadir la "card" al GridPane
//        recipeGrid.add(recipeCard, col, row);
//    }
    private void addRecipeToGrid(Recipe recipe, int row, int col) {
        VBox recipeCard = new VBox();
        recipeCard.setAlignment(Pos.CENTER);
        recipeCard.setSpacing(10);
        recipeCard.setStyle("-fx-border-color: lightgray; -fx-padding: 10; -fx-background-color: #f9f9f9; -fx-border-radius: 5;");

        // Imagen de la receta
        ImageView imageView = new ImageView();
        try {
            if (recipe.getFoto() != null && recipe.getFoto().length > 0) {
                imageView.setImage(new Image(new ByteArrayInputStream(recipe.getFoto())));
            } else {
                imageView.setImage(new Image(getClass().getResource("/homeSweetHome/view/images/add-image.jpg").toExternalForm())); // Imagen por defecto
            }
        } catch (Exception e) {
            System.err.println("Error al cargar la imagen: " + e.getMessage());
            imageView.setImage(new Image(getClass().getResource("/homeSweetHome/view/images/add-image.jpg").toExternalForm()));
        }
        imageView.setFitWidth(100);
        imageView.setFitHeight(100);

        // Nombre de la receta
        Label recipeName = new Label(recipe.getNombre());
        recipeName.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        // Categoría o tipo de la receta
        Label recipeCategory = new Label(recipe.getTipo()); // Cambia `getTipo` si el atributo es diferente
        recipeCategory.setStyle("-fx-font-size: 12px; -fx-text-fill: #555;");

        // Botones de acción
        Button btnModify = new Button("Modificar");
        btnModify.setOnAction(e -> modifyRecipe(recipe));
        Button btnView = new Button("Ver");
        btnView.setOnAction(e -> viewRecipe(recipe));
        Button btnDelete = new Button("Eliminar");
        btnDelete.setOnAction(e -> deleteRecipe(recipe));

        HBox buttonBox = new HBox(btnModify, btnView, btnDelete);
        buttonBox.setSpacing(5);
        buttonBox.setAlignment(Pos.CENTER);

        // Añadir todos los elementos a la "card"
        recipeCard.getChildren().addAll(imageView, recipeName, recipeCategory, buttonBox);

        // Añadir la "card" al GridPane
        recipeGrid.add(recipeCard, col, row);
    }

    /**
     * Acción para el botón "Modificar".
     *
     * @param recipe La receta a modificar.
     */
    private void modifyRecipe(Recipe recipe) {
        try {
            // Cargar la vista de modificación
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/homeSweetHome/view/recipe/ModifyRecipeView.fxml"));
            Parent root = loader.load();

            // Obtener el controlador de la vista de modificación
            ModifyRecipeViewController modifyRecipeController = loader.getController();
            modifyRecipeController.setRecipe(recipe);
            modifyRecipeController.setRecipeViewController(this);

            // Configurar la ventana
            Stage stage = new Stage();
            stage.setTitle("Modificar Receta");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(btnOpenAddNewRecipe.getScene().getWindow());
            stage.showAndWait(); // Espera a que se cierre la ventana

            // Actualizar las recetas después de la modificación
            loadRecipes();
        } catch (Exception e) {
            System.err.println("Error al cargar la vista de modificación: " + e.getMessage());
        }
    }

    /**
     * Acción para el botón "Ver".
     *
     * @param recipe La receta a visualizar.
     */
    private void viewRecipe(Recipe recipe) {
        try {
            // Cargar la vista de detalles
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/homeSweetHome/view/recipe/ViewRecipeView.fxml"));
            Parent root = loader.load();

            // Obtener el controlador de la vista de detalles
            ViewRecipeViewController viewRecipeController = loader.getController();
            viewRecipeController.setRecipe(recipe);

            // Configurar la ventana
            Stage stage = new Stage();
            stage.setTitle("Ver Receta");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(btnOpenAddNewRecipe.getScene().getWindow());
            stage.showAndWait(); // Espera a que se cierre la ventana
        } catch (Exception e) {
            System.err.println("Error al cargar la vista de detalles: " + e.getMessage());
        }
    }

//    /**
//     * Acción para el botón "Eliminar".
//     *
//     * @param recipe La receta a eliminar.
//     */
//    private void deleteRecipe(Recipe recipe) {
//        System.out.println("Eliminar receta: " + recipe.getNombre());
//        if (recipeDAO.deleteRecipe(recipe.getId())) {
//            loadRecipes(); // Actualiza el GridPane
//        } else {
//            System.err.println("Error al eliminar la receta.");
//        }
//    }
    private void deleteRecipe(Recipe recipe) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar Eliminación");
        alert.setHeaderText("¿Estás seguro de que deseas eliminar esta receta?");
        alert.setContentText("Receta: " + recipe.getNombre());

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            System.out.println("Confirmación de eliminación recibida para receta: " + recipe.getNombre());

            if (recipeDAO.deleteRecipeCascade(recipe.getId())) {
                System.out.println("Receta eliminada correctamente: " + recipe.getNombre());
                loadRecipes(); // Refrescar el GridPane o lista
            } else {
                System.err.println("Error al eliminar la receta y sus relaciones.");
            }
        } else {
            System.out.println("Eliminación cancelada para receta: " + recipe.getNombre());
        }
    }

    @FXML
    private void openAddNewRecipe(ActionEvent event) {
        try {
            // Cargar la vista para añadir nueva receta
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/homeSweetHome/view/recipe/CreateRecipeView.fxml"));
            Parent root = loader.load();

            // Obtener el controlador de la nueva vista
            CreateRecipeViewController createRecipeController = loader.getController();
            createRecipeController.setRecipeViewController(this);

            // Configurar la ventana
            Stage stage = new Stage();
            stage.setTitle("Crear Nueva Receta");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(btnOpenAddNewRecipe.getScene().getWindow());
            stage.showAndWait(); // Espera a que se cierre la ventana

            // Actualizar las recetas después de añadir una nueva
            loadRecipes();
        } catch (Exception e) {
            System.err.println("Error al cargar la vista para añadir nueva receta: " + e.getMessage());
        }
    }
}
