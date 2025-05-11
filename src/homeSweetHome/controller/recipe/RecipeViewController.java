package homeSweetHome.controller.recipe;

import homeSweetHome.dataPersistence.CurrentSession;
import homeSweetHome.dataPersistence.MealDAO;
import homeSweetHome.dataPersistence.MySQLConnection;
import homeSweetHome.model.Recipe;
import homeSweetHome.dataPersistence.RecipeDAO;
import homeSweetHome.model.Meal;
import homeSweetHome.utils.LanguageManager;
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
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

/**
 * Controlador para la vista principal de recetas.
 */
public class RecipeViewController implements Initializable {

    @FXML
    private Button btnOpenAddNewRecipe;
    @FXML
    private GridPane recipeGrid; // Contenedor para las recetas
    @FXML
    private Label recipesTitle;

    private RecipeDAO recipeDAO; // DAO para interactuar con la base de datos

    private MealDAO mealDAO = new MealDAO();

    int role = CurrentSession.getInstance().getUserRole(); // Tomamos rol para control de permisos

    //private LanguageManager languageManager;
    
    /**
     * Inicializa el controlador.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        //Si el usuario tiene rol consultor, desactivamos botones
        if (role == 2) {

            btnOpenAddNewRecipe.setDisable(true);

        }

        recipeDAO = new RecipeDAO(); // Inicializa el DAO
        loadRecipes(); // Carga las recetas al inicio

        mealDAO = new MealDAO(); // Inicialización de MealDAO
        System.out.println("MealDAO inicializado correctamente.");

/////////////////////////////////IDIOMAS/////////////////////////////////////////////

        LanguageManager.getInstance().addListener(() -> Platform.runLater(this::updateTexts));
        updateTexts(); // Asegura que los textos se actualicen desde el inicio
        System.out.println("Ejecutando initialize() en RecipeViewController. Idioma activo: " + LanguageManager.getInstance().getLanguageCode());

/////////////////////////////////FIN IDIOMAS///////////////////////////////////////////// 

    }

/////////////////////////////////IDIOMAS/////////////////////////////////////////////
    
    /**
     * Actualiza los textos de la interfaz en función del idioma.
     */
    private void updateTexts() {

        // Obtiene la instancia única del Singleton
        LanguageManager languageManager = LanguageManager.getInstance();

        if (languageManager == null) {

            System.err.println("Error: LanguageManager es nulo. Traducción no aplicada.");
            return;
        }

        // Traducción del botón principal
        btnOpenAddNewRecipe.setText(languageManager.getTranslation("addNewRecipe"));

        recipesTitle.setText(languageManager.getTranslation("recipesTitle"));

        // Actualizar los textos dentro del GridPane
        recipeGrid.getChildren().forEach(node -> {

            if (node instanceof VBox) {

                VBox recipeCard = (VBox) node;

                recipeCard.getChildren().forEach(child -> {

                    if (child instanceof Label) {

                        Label label = (Label) child;

                        // Traducción de etiquetas (nombre, categoría, etc.)
                        String translatedText = languageManager.getTranslation(label.getText());
                        label.setText(translatedText != null ? translatedText : label.getText());

                    } else if (child instanceof HBox) {

                        HBox buttonBox = (HBox) child;

                        buttonBox.getChildren().forEach(buttonChild -> {

                            if (buttonChild instanceof Button) {

                                Button button = (Button) buttonChild;

                                // Traducción de botones basados en textos iniciales
                                switch (button.getText()) {
                                    case "Modificar":
                                    case "Modify":
                                        button.setText(languageManager.getTranslation("modify"));
                                        break;
                                    case "Ver":
                                    case "View":
                                        button.setText(languageManager.getTranslation("view"));
                                        break;
                                    case "Eliminar":
                                    case "Delete":
                                        button.setText(languageManager.getTranslation("delete"));
                                        break;
                                    default:
                                        System.out.println("Texto no reconocido para traducción: " + button.getText());
                                        break;
                                }
                            }
                        });
                    }
                });
            }
        });

        System.out.println("Traducciones aplicadas correctamente en RecipeViewController.");
    }

/////////////////////////////////FIN IDIOMAS/////////////////////////////////////////////  
    

    /**
     * Carga solo las recetas del grupo del usuario desde la base de datos y las
     * muestra en el GridPane.
     */
    private void loadRecipes() {
        int groupId = CurrentSession.getInstance().getUserGroupId(); // Obtiene el ID del grupo del usuario actual
        List<Recipe> recipes = recipeDAO.getRecipesByGroup(groupId); // Filtra recetas por grupo
        recipeGrid.getChildren().clear(); // Limpia el GridPane

        int row = 0;
        int col = 0;

        for (Recipe recipe : recipes) {
            addRecipeToGrid(recipe, row, col);
            col++;

            if (col == 3) { // Cambia de fila después de 3 columnas
                col = 0;
                row++;
            }
        }
    }


    /**
     * Añade una receta al GridPane como una tarjeta.
     *
     * @param recipe La receta a mostrar.
     * @param row La fila en la que se añadirá.
     * @param col La columna en la que se añadirá.
     */
    private void addRecipeToGrid(Recipe recipe, int row, int col) {

        updateTexts();

        // Contenedor de la tarjeta
        VBox recipeCard = new VBox();
        recipeCard.setAlignment(Pos.CENTER);
        recipeCard.setSpacing(10);
        recipeCard.setStyle("-fx-border-color: lightgray; -fx-padding: 10; -fx-background-color: #ffffff; -fx-border-radius: 5;");

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

        // Aplica bordes redondeados con un radio de 8
        Rectangle clip = new Rectangle(imageView.getFitWidth(), imageView.getFitHeight());
        clip.setArcWidth(16); // Doble del radio deseado para suavizar bordes
        clip.setArcHeight(16);
        imageView.setClip(clip);

        // Nombre de la receta
        Label recipeName = new Label(recipe.getNombre());
        recipeName.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        // Categoría o tipo de la receta
        Label recipeCategory = new Label(recipe.getTipo()); // Ajustar si el atributo es diferente
        recipeCategory.setStyle("-fx-font-size: 12px; -fx-text-fill: #555;");

        // Botones de acción con colores pastel
        Button btnModify = new Button("Modificar");
        btnModify.setStyle("-fx-background-color: #E58E00; -fx-text-fill: #FFFFFF; -fx-background-radius: 8;");
        btnModify.setOnAction(e -> modifyRecipe(recipe));
        btnModify.setOnMouseEntered(e -> btnModify.setStyle("-fx-background-color: #F6A600; -fx-text-fill: #FFFFFF; -fx-background-radius: 8;"));
        btnModify.setOnMouseExited(e -> btnModify.setStyle("-fx-background-color: #E58E00; -fx-text-fill: #FFFFFF; -fx-background-radius: 8;"));

        Button btnView = new Button("Ver");
        btnView.setStyle("-fx-background-color: #778D45; -fx-text-fill: #FFFFFF; -fx-background-radius: 8;");
        btnView.setOnAction(e -> viewRecipe(recipe));
        btnView.setOnMouseEntered(e -> btnView.setStyle("-fx-background-color: #93A664; -fx-text-fill: #FFFFFF; -fx-background-radius: 8;"));
        btnView.setOnMouseExited(e -> btnView.setStyle("-fx-background-color: #778D45; -fx-text-fill: #FFFFFF; -fx-background-radius: 8;"));

        Button btnDelete = new Button("Eliminar");
        btnDelete.setStyle("-fx-background-color: #AA1803; -fx-text-fill: #FFFFFF; -fx-background-radius: 8;");
        btnDelete.setOnAction(e -> deleteRecipe(recipe));
        btnDelete.setOnMouseEntered(e -> btnDelete.setStyle("-fx-background-color: #C32C19; -fx-text-fill: #FFFFFF; -fx-background-radius: 8;"));
        btnDelete.setOnMouseExited(e -> btnDelete.setStyle("-fx-background-color: #AA1803; -fx-text-fill: #FFFFFF; -fx-background-radius: 8;"));

        //Si el usuario tiene rol consultor, desactivamos botones
        if (role == 2) {

            btnModify.setDisable(true);
            btnDelete.setDisable(true);

        }

        // Contenedor para los botones
        HBox buttonBox = new HBox(btnModify, btnView, btnDelete);
        buttonBox.setSpacing(5);
        buttonBox.setAlignment(Pos.CENTER);

        // Añadir elementos a la tarjeta
        recipeCard.getChildren().addAll(imageView, recipeName, recipeCategory, buttonBox);

        // Añadir la tarjeta al GridPane
        recipeGrid.add(recipeCard, col, row);
        System.out.println("Recipe Grid Size: " + recipeGrid.getChildren().size());

    }

    /**
     * Acción para el botón "Modificar".
     *
     * @param recipe La receta a modificar.
     */
    private void modifyRecipe(Recipe recipe) {

        try {

            // Carga la vista de modificación
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/homeSweetHome/view/recipe/ModifyRecipeView.fxml"));
            Parent root = loader.load();

            // Obtiener el controlador de la vista de modificación
            ModifyRecipeViewController modifyRecipeController = loader.getController();
            modifyRecipeController.setRecipe(recipe);
            modifyRecipeController.setRecipeViewController(this);

            // Configura la ventana
            Stage stage = new Stage();
            stage.setTitle("Modificar Receta");
            stage.setResizable(false);
            stage.setScene(new Scene(root));
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(btnOpenAddNewRecipe.getScene().getWindow());
            stage.showAndWait(); // Espera a que se cierre la ventana

            // Actualiza las recetas después de la modificación
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

            // Carga la vista de detalles
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/homeSweetHome/view/recipe/ViewRecipeView.fxml"));
            Parent root = loader.load();

            // Obtiene el controlador de la vista de detalles
            ViewRecipeViewController viewRecipeController = loader.getController();
            viewRecipeController.setRecipe(recipe);

            // Configura la ventana
            Stage stage = new Stage();
            stage.setTitle("Ver Receta");
            stage.setResizable(false);
            stage.setScene(new Scene(root));
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(btnOpenAddNewRecipe.getScene().getWindow());
            stage.showAndWait(); // Espera a que se cierre la ventana

        } catch (Exception e) {

            System.err.println("Error al cargar la vista de detalles: " + e.getMessage());
        }
    }

    /**
     * Acción para el botón "Eliminar".
     *
     * @param recipe La receta a eliminar.
     */
    private void deleteRecipe(Recipe recipe) {

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.getDialogPane().getStylesheets().add(getClass().getResource("/homeSweetHome/utils/alertsCss.css").toExternalForm());//Revisar!!

        if (LanguageManager.getInstance().getLanguageCode().equals("es")) {

            alert.setTitle("Confirmar Eliminación");
            alert.setHeaderText("¿Estás seguro de que deseas eliminar esta receta?");
            alert.setContentText("Receta: " + recipe.getNombre());

        } else if (LanguageManager.getInstance().getLanguageCode().equals("en")) {

            alert.setTitle("Confirm Deletion");
            alert.setHeaderText("Are you sure you want to delete this recipe?");
            alert.setContentText("Recipe: " + recipe.getNombre());

        }

//        alert.setTitle("Confirmar Eliminación");
//        alert.setHeaderText("¿Estás seguro de que deseas eliminar esta receta?");
//        alert.setContentText("Receta: " + recipe.getNombre());
        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {

            System.out.println("Confirmación de eliminación recibida para receta: " + recipe.getNombre());

            if (recipeDAO.deleteRecipeCascade(recipe.getId())) {

                System.out.println("Receta eliminada correctamente: " + recipe.getNombre());
                loadRecipes(); // Refresca el GridPane o lista

            } else {

                System.err.println("Error al eliminar la receta y sus relaciones.");
            }

        } else {

            System.out.println("Eliminación cancelada para receta: " + recipe.getNombre());
        }
    }

    /**
     * Abre una ventana para crear una nueva receta.
     *
     * Configura y muestra una nueva vista para añadir recetas. Actualiza la
     * lista de recetas al cerrar la ventana.
     *
     * @param event el evento asociado al clic del botón para añadir nueva
     * receta
     */
    @FXML
    private void openAddNewRecipe(ActionEvent event) {

        try {

            // Carga la vista para añadir nueva receta
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/homeSweetHome/view/recipe/CreateRecipeView.fxml"));
            Parent root = loader.load();

            // Obtiene el controlador de la nueva vista
            CreateRecipeViewController createRecipeController = loader.getController();
            createRecipeController.setRecipeViewController(this);

            // Configura la ventana
            Stage stage = new Stage();
            stage.setTitle("Crear Nueva Receta");
            stage.setResizable(false);
            stage.setScene(new Scene(root));
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(btnOpenAddNewRecipe.getScene().getWindow());
            stage.showAndWait(); // Espera a que se cierre la ventana

            // Actualiza las recetas después de añadir una nueva
            loadRecipes();

        } catch (Exception e) {

            System.err.println("Error al cargar la vista para añadir nueva receta: " + e.getMessage());
        }
    }
}
