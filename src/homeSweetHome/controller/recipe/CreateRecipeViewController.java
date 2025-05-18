package homeSweetHome.controller.recipe;

import homeSweetHome.dataPersistence.CurrentSession;
import homeSweetHome.dataPersistence.MySQLConnection;
import homeSweetHome.dataPersistence.RecipeDAO;
import homeSweetHome.model.Product;
import homeSweetHome.model.Recipe;
import homeSweetHome.utils.AlertUtils;
import homeSweetHome.utils.LanguageManager;
import java.io.File;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * Controlador para la vista de creación de recetas.
 */
public class CreateRecipeViewController implements Initializable {

    @FXML
    private TextField fieldRecipeName;
    @FXML
    private ComboBox<String> cmbRecipeCategory;
    @FXML
    private Button btnAddIngredient;
    @FXML
    private TextArea txtAreaInstructions;
    @FXML
    private Button btnLoadImage;
    @FXML
    private Button btnCreateNewRecipe;
    @FXML
    private Button btnCancel;
    @FXML
    private TableView<Product> tableIngredients;
    @FXML
    private TableColumn<Product, String> colIngredientName;
    @FXML
    private TableColumn<Product, Integer> colIngredientQuantity;
    @FXML
    private TableColumn<Product, String> colIngredientUnit;
    @FXML
    private TableColumn<Product, Void> deleteColumn;
    @FXML
    private ImageView imgRecipe;
    @FXML
    private Label newRecipeTitle;

    private RecipeViewController recipeViewController;

    private Recipe recipe;

    private byte[] recipeImage; // Para almacenar la imagen de la receta

    /**
     * Inicializa el controlador.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

/////////////////////////////////IDIOMAS/////////////////////////////////////////////

        // Registra este controlador como listener del LanguageManager
        LanguageManager.getInstance().addListener(() -> Platform.runLater(this::updateTexts));
        updateTexts(); // Actualiza los textos inicialmente

/////////////////////////////////FIN IDIOMAS/////////////////////////////////////////////  

        // Configura las columnas de la tabla
        colIngredientName.setCellValueFactory(new PropertyValueFactory<>("nombreProducto"));
        colIngredientQuantity.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
        colIngredientUnit.setCellValueFactory(new PropertyValueFactory<>("tipo"));

        // Configura opciones para la categoría de recetas
        //cmbRecipeCategory.getItems().addAll("Carnes", "Pescados", "Verduras", "Legumbres", "Postres", "Otros");
        deleteColumn.setCellFactory(param -> new TableCell<>() {
            
            private final Button btnDelete = new Button("Eliminar");

            {
                btnDelete.setOnAction(event -> {
                    Product product = getTableView().getItems().get(getIndex());
                    System.out.println("Producto seleccionado desde botón: " + product.getNombreProducto());

                    // Lógica de eliminación
                    deleteProductFromTable(product);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(btnDelete);
                }
            }
        });
    }

/////////////////////////////////IDIOMAS/////////////////////////////////////////////
    
    /**
     * Actualiza los textos de la interfaz en función del idioma.
     */
    private void updateTexts() {

        // Accede al Singleton del LanguageManager
        LanguageManager languageManager = LanguageManager.getInstance();

        if (languageManager == null) {

            System.err.println("Error: LanguageManager no está disponible.");
            return;
        }

        System.out.println("Actualizando textos de la interfaz...");

        // Traducción de los textos de los botones
        btnAddIngredient.setText(languageManager.getTranslation("addIngredient"));
        btnLoadImage.setText(languageManager.getTranslation("loadImage"));
        btnCreateNewRecipe.setText(languageManager.getTranslation("createRecipe"));
        btnCancel.setText(languageManager.getTranslation("cancel"));

        newRecipeTitle.setText(languageManager.getTranslation("newRecipeTitle"));

        // Traducción de los campos de texto
        fieldRecipeName.setPromptText(languageManager.getTranslation("recipeNamePrompt"));
        txtAreaInstructions.setPromptText(languageManager.getTranslation("instructionsPrompt"));

        // Traducción de encabezados de las columnas en la tabla de ingredientes
        colIngredientName.setText(languageManager.getTranslation("ingredientName"));
        colIngredientQuantity.setText(languageManager.getTranslation("ingredientQuantity"));
        colIngredientUnit.setText(languageManager.getTranslation("ingredientUnit"));
        deleteColumn.setText(languageManager.getTranslation("delete"));

        // Traducción del ComboBox de categorías de recetas
        cmbRecipeCategory.setPromptText(languageManager.getTranslation("recipeCategoryPrompt"));
        cmbRecipeCategory.getItems().clear();
        cmbRecipeCategory.getItems().addAll(
                languageManager.getTranslation("recipeCategoryMeat"),
                languageManager.getTranslation("recipeCategoryFish"),
                languageManager.getTranslation("recipeCategoryVegetables"),
                languageManager.getTranslation("recipeCategoryLegumes"),
                languageManager.getTranslation("recipeCategoryDesserts"),
                languageManager.getTranslation("recipeCategoryOthers")
        );

        System.out.println("Traducciones aplicadas correctamente en idioma: " + languageManager.getTranslation("currentLanguage"));
    }

/////////////////////////////////FIN IDIOMAS/////////////////////////////////////////////  
    
    /**
     * Establece la referencia al controlador principal de la vista de recetas.
     *
     * @param recipeViewController El controlador principal.
     */
    public void setRecipeViewController(RecipeViewController recipeViewController) {

        this.recipeViewController = recipeViewController;
    }

    /**
     * Abre la ventana para añadir un nuevo ingrediente y lo añade a la tabla.
     *
     * @param event Evento del botón.
     */
    @FXML
    private void addIngredient(ActionEvent event) {

        try {

            // Carga la vista para añadir ingrediente
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/homeSweetHome/view/recipe/AddIngredientView.fxml"));
            Parent root = loader.load();

            // Obtiene el controlador de la vista de añadir ingrediente
            AddIngredientViewController addIngredientController = loader.getController();
            addIngredientController.setCreateRecipeController(this); // Pasa referencia al controlador principal

            // Configura la ventana
            Stage stage = new Stage();
            stage.setTitle("Añadir Ingrediente");
            stage.setResizable(false);
            stage.getIcons().add(new Image(getClass().getResourceAsStream("/images/recipeIconBlue.png")));
            stage.setScene(new Scene(root));
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(btnAddIngredient.getScene().getWindow());
            stage.showAndWait(); // Espera hasta que se cierre la ventana

        } catch (Exception e) {

            System.err.println("Error al cargar la vista de añadir ingrediente: " + e.getMessage());
        }
    }

    /**
     * Añade un ingrediente a la tabla de ingredientes.
     *
     * @param ingredient El objeto Product que representa el ingrediente(Siempre
     * trabaja con la clase product).
     */
    public void addIngredientToTable(Product ingredient) {

        tableIngredients.getItems().add(ingredient); // Añade el ingrediente a la tabla
    }

    /**
     * Carga una imagen desde el sistema de archivos y la muestra en el
     * ImageView de la receta. También almacena la imagen como un byte array
     * para su guardado en la base de datos.
     *
     * @param event Evento del botón "Cargar Imagen".
     */
    @FXML
    private void loadImage(ActionEvent event) {
        
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar Imagen");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos de Imagen", "*.png", "*.jpg", "*.jpeg"));

        File file = fileChooser.showOpenDialog(btnLoadImage.getScene().getWindow());

        try {
            
            if (file != null) {
                
                // Carga la imagen seleccionada y la muestra en el ImageView
                Image image = new Image(file.toURI().toString());
                imgRecipe.setImage(image);

                // Convierte la imagen a byte array
                recipeImage = java.nio.file.Files.readAllBytes(file.toPath());
                System.out.println("Imagen cargada correctamente: " + file.getName());
                
            } else {
                
                // Carga la imagen por defecto
                System.out.println("No se seleccionó ninguna imagen. Se usará la imagen por defecto.");
                Image defaultImage = new Image(getClass().getResource("/images/default_recipe.png").toExternalForm());
                imgRecipe.setImage(defaultImage);

                // Opcional: Cargar la imagen por defecto en formato byte array
                recipeImage = java.nio.file.Files.readAllBytes(new File(getClass().getResource("/../images/settings.png").toURI()).toPath());
            }
            
        } catch (Exception e) {
            
            AlertUtils.showAlert(Alert.AlertType.ERROR, "Error al Cargar Imagen", "No se pudo cargar la imagen. Inténtalo de nuevo.");
            System.err.println("Error al cargar la imagen: " + e.getMessage());
        }
    }

    /**
     * Guarda la receta en la base de datos junto con los ingredientes.
     *
     * @param event Evento del botón.
     */
    @FXML
    private void createNewRecipe(ActionEvent event) {

        // Valida los campos de la receta
        String recipeName = fieldRecipeName.getText().trim();
        String recipeCategory = cmbRecipeCategory.getValue();
        String recipeInstructions = txtAreaInstructions.getText().trim();

        if (recipeName.isEmpty() || recipeCategory == null || recipeInstructions.isEmpty()) {
            
            if (LanguageManager.getInstance().getLanguageCode().equals("es")) {
                
                AlertUtils.showAlert(Alert.AlertType.WARNING, "Campos Vacíos", "Por favor, completa todos los campos.");
                
            } else if (LanguageManager.getInstance().getLanguageCode().equals("en")) {
                
                AlertUtils.showAlert(Alert.AlertType.WARNING, "Empty Fields", "Please complete all fields.");
            }
            
            return;
        }

        // Verifica que haya al menos un ingrediente
        if (tableIngredients.getItems().isEmpty()) {
            
            if (LanguageManager.getInstance().getLanguageCode().equals("es")) {
                
                AlertUtils.showAlert(Alert.AlertType.WARNING, "Sin Ingredientes", "Por favor, añade al menos un ingrediente.");
                
            } else if (LanguageManager.getInstance().getLanguageCode().equals("en")) {
                
                AlertUtils.showAlert(Alert.AlertType.WARNING, "No Ingredients", "Please add at least one ingredient.");
            }
            return;
        }

        // Verifica que la imagen esté seleccionada
        if (recipeImage == null) {
            
            if (LanguageManager.getInstance().getLanguageCode().equals("es")) {
                
                AlertUtils.showAlert(Alert.AlertType.WARNING, "Imagen Requerida", "Por favor, selecciona una imagen para la receta.");
                
            } else if (LanguageManager.getInstance().getLanguageCode().equals("en")) {
                
                AlertUtils.showAlert(Alert.AlertType.WARNING, "Image Required", "Please select an image for the recipe.");
            }
            
            return;
        }

        // Verifica si la receta ya existe en la base de datos
        RecipeDAO recipeDAO = new RecipeDAO();
        
        if (recipeDAO.existsRecipe(recipeName)) {
            
            if (LanguageManager.getInstance().getLanguageCode().equals("es")) {
                
                AlertUtils.showAlert(Alert.AlertType.WARNING, "Receta Duplicada", "Ya existe una receta con este nombre.");
                
            } else if (LanguageManager.getInstance().getLanguageCode().equals("en")) {
                
                AlertUtils.showAlert(Alert.AlertType.WARNING, "Duplicate Recipe", "A recipe with this name already exists.");
            }
           
            return;
        }

        // Crea el objeto Recipe y asigna los datos
        Recipe recipe = new Recipe();
        recipe.setNombre(recipeName);
        recipe.setTipo(recipeCategory);
        recipe.setInstrucciones(recipeInstructions);
        recipe.setFoto(recipeImage); // Foto cargada previamente
        recipe.setIdGrupo(CurrentSession.getInstance().getUserGroupId());
        recipe.setIngredientes(new ArrayList<>(tableIngredients.getItems()));

        // Guarda la receta en la base de datos
        boolean success = recipeDAO.addRecipeAndProducts(recipe);

        if (success) {
            
            if (LanguageManager.getInstance().getLanguageCode().equals("es")) {
                
                AlertUtils.showAlert(Alert.AlertType.INFORMATION, "Receta Creada", "La receta y sus productos se crearon exitosamente.");
                
            } else if (LanguageManager.getInstance().getLanguageCode().equals("en")) {
                
                AlertUtils.showAlert(Alert.AlertType.INFORMATION, "Recipe Created", "The recipe and its ingredients were successfully created.");
            }
            
            ((Stage) btnCreateNewRecipe.getScene().getWindow()).close();
            
        } else {
            
            AlertUtils.showAlert(Alert.AlertType.ERROR, "Error al Guardar", "No se pudo guardar la receta. Por favor, inténtalo de nuevo.");
        }
    }

    /**
     * Elimina productos de la tabla
     * @param product 
     */
    private void deleteProductFromTable(Product product) {

        if (product == null) {

            System.out.println("No se seleccionó ningún producto para eliminar.");
            return;
        }

        System.out.println("Eliminando producto de la tabla visual: " + product.getNombreProducto());

        // Elimina el producto de la tabla visual
        tableIngredients.getItems().remove(product);
        tableIngredients.refresh(); // Refresca la tabla para sincronizar los datos
        System.out.println("Producto eliminado de la tabla visual.");
    }

    /**
     * Actualiza la tabla
     */
    private void refreshTableView() {

        ObservableList<Product> currentItems = tableIngredients.getItems();
        tableIngredients.setItems(null); // Rompe el enlace con la tabla
        tableIngredients.setItems(currentItems); // Vuelve a enlazar los datos con la tabla
        System.out.println("Tabla visual refrescada manualmente.");
    }

    /**
     * Cancela la creación de la receta y cierra la ventana.
     *
     * @param event Evento del botón.
     */
    @FXML
    private void cancel(ActionEvent event) {

        ((Stage) btnCancel.getScene().getWindow()).close(); // Cierra la ventana actual
    }
}
