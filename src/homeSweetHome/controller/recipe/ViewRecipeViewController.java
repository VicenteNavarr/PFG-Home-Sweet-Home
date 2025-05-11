package homeSweetHome.controller.recipe;

import homeSweetHome.dataPersistence.RecipeDAO;
import homeSweetHome.model.Recipe;
import homeSweetHome.model.Product;
import homeSweetHome.utils.LanguageManager;
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
import javafx.application.Platform;
import javafx.scene.control.TextArea;

public class ViewRecipeViewController {

    @FXML
    private Label labelRecipeName;
    @FXML
    private Label labelRecipeCategory;
    @FXML
    private Label labelIngredients;
    @FXML
    private TextArea labelInstructions;
    @FXML
    private ImageView imgRecipe;
    @FXML
    private Button btnClose;

    private Recipe recipe;

    private final RecipeDAO recipeDAO = new RecipeDAO();

    /**
     * Inicializa la vista de Ver Receta al cargarla.
     *
     * Configura cualquier operación inicial requerida para esta vista.
     */
    @FXML
    public void initialize() {

/////////////////////////////////IDIOMAS/////////////////////////////////////////////

        // Registra este controlador como listener del LanguageManager
        LanguageManager.getInstance().addListener(() -> Platform.runLater(this::updateTexts));
        updateTexts(); // Actualiza los textos inicialmente

/////////////////////////////////FIN IDIOMAS/////////////////////////////////////////////   

        System.out.println("Inicializando vista de Ver Receta...");
    }
    
/////////////////////////////////IDIOMAS/////////////////////////////////////////////

    /**
     * TRaduccion textos
     */
    private void updateTexts() {

        // Obtiene la instancia única del Singleton
        LanguageManager languageManager = LanguageManager.getInstance();

        if (languageManager == null) {

            System.err.println("Error: LanguageManager es nulo. Traducción no aplicada.");
            return;
        }

        // Verificación del idioma activo
        String idiomaActivo = languageManager.getLanguageCode();
        System.out.println("Idioma activo en updateTexts(): " + idiomaActivo);

        // Traducción de etiquetas principales
        labelRecipeName.setText(languageManager.getTranslation("recipeName"));
        labelRecipeCategory.setText(languageManager.getTranslation("recipeCategory"));
        labelIngredients.setText(languageManager.getTranslation("ingredients"));
        labelInstructions.setText(languageManager.getTranslation("instructions"));
        btnClose.setText(languageManager.getTranslation("close"));

        // Depuración de etiquetas traducidas
        System.out.println("Etiqueta 'recipeName': " + labelRecipeName.getText());
        System.out.println("Etiqueta 'recipeCategory': " + labelRecipeCategory.getText());
        System.out.println("Etiqueta 'ingredients': " + labelIngredients.getText());
        System.out.println("Etiqueta 'instructions': " + labelInstructions.getText());
        System.out.println("Botón 'close': " + btnClose.getText());

        System.out.println("Traducción aplicada correctamente en ViewRecipeViewController.");

        // Forzar actualización de la UI para aplicar los cambios visualmente
        Platform.runLater(() -> labelRecipeName.getScene().getWindow().sizeToScene());
    }

/////////////////////////////////FIN IDIOMAS/////////////////////////////////////////////   
    
    /**
     * Asigna una receta y carga sus detalles en la vista.
     *
     * @param recipe la receta a establecer
     */
    public void setRecipe(Recipe recipe) {

        this.recipe = recipe;
        loadRecipeDetails();
    }

    /**
     * Carga y muestra los detalles de la receta actual.
     *
     * Configura los campos de nombre, categoría, instrucciones, ingredientes e
     * imagen de la receta en la interfaz de usuario.
     */
    private void loadRecipeDetails() {

        if (recipe != null) {

            // Muestra el nombre de la receta
            System.out.println("Cargando detalles para la receta: " + recipe.getNombre());
            labelRecipeName.setText(recipe.getNombre());

            // Muestrar la categoría de la receta
            labelRecipeCategory.setText(recipe.getTipo());

            // Muestra las instrucciones
            labelInstructions.setText(recipe.getInstrucciones() != null ? recipe.getInstrucciones() : "No hay instrucciones disponibles.");

            // Carga los ingredientes desde el DAO
            List<Product> ingredientes = recipeDAO.getProductsByRecipeId(recipe.getId());

            if (ingredientes == null || ingredientes.isEmpty()) {

                System.out.println("No hay ingredientes disponibles para esta receta.");
                labelIngredients.setText("No hay ingredientes.");

            } else {

                // Muestra nombre y cantidad de cada ingrediente
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

            // Carga imagen si existe
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

    /**
     * Ajusta la imagen de la receta en la interfaz.
     *
     * Carga y configura la imagen asociada a la receta, ajustando su tamaño y
     * manteniendo la relación de aspecto. Si no hay imagen, lo indica en
     * consola.
     */
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

    /**
     * Metodo para debugear con trazas
     */
    private void debugRecipeDetails() {

        System.out.println("Detalles de la receta:");
        System.out.println("Nombre: " + recipe.getNombre());
        System.out.println("Categoría: " + recipe.getTipo());
        System.out.println("Ingredientes: " + recipe.getIngredientes());
        System.out.println("Instrucciones: " + recipe.getInstrucciones());
    }

    /**
     * Cierra vista
     *
     * @param event
     */
    @FXML
    private void closeView(ActionEvent event) {

        ((Stage) btnClose.getScene().getWindow()).close();
        System.out.println("Vista de Ver Receta cerrada.");
    }
}
