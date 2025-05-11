package homeSweetHome.controller.meal;

import homeSweetHome.dataPersistence.CurrentSession;
import homeSweetHome.dataPersistence.InventoryDAO;
import homeSweetHome.dataPersistence.MealDAO;
import homeSweetHome.dataPersistence.RecipeDAO;
import homeSweetHome.model.Meal;
import homeSweetHome.model.Product;
import homeSweetHome.model.Recipe;
import homeSweetHome.utils.AlertUtils;
import homeSweetHome.utils.LanguageManager;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.event.ActionEvent;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Rectangle;

/**
 * Controlador para la vista del menú semanal.
 */
public class MealViewController implements Initializable {

    @FXML
    private GridPane MealGrid;
    @FXML
    private Button btnGenerateMeal;
    @FXML
    private Button btnConfirmMeal;
    @FXML
    private Label mealsTitle;

    private MealDAO mealDAO;

    private Button btnDelete;

    private LanguageManager languageManager;

    int role = CurrentSession.getInstance().getUserRole(); // Tomamos rol para control de permisos

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        //Si el usuario tiene rol consultor, desactivamos botones
        if (role == 2) {

            btnGenerateMeal.setDisable(true);
            btnConfirmMeal.setDisable(true);
        }

        mealDAO = new MealDAO(); // Inicializa el DAO
        loadWeeklyMeals(); // Carga las comidas al inicio
        btnConfirmMeal.setVisible(false); // Ocultar botón inicialmente

/////////////////////////////////IDIOMAS/////////////////////////////////////////////

        // Registra este controlador como listener del LanguageManager
        LanguageManager.getInstance().addListener(() -> Platform.runLater(this::updateTexts));
        updateTexts(); // Actualiza los textos inicialmente

/////////////////////////////////FIN IDIOMAS/////////////////////////////////////////////  

    }

/////////////////////////////////IDIOMAS/////////////////////////////////////////////
    
    /**
     * Actualiza los textos de la interfaz en función del idioma.
     */
    private void updateTexts() {

        // Accede directamente al Singleton del LanguageManager
        LanguageManager languageManager = LanguageManager.getInstance();

        if (languageManager == null) {
            System.err.println("Error: LanguageManager no está disponible.");
            return;
        }

        // Traducir los textos de los botones principales
        btnGenerateMeal.setText(languageManager.getTranslation("generateMeal"));
        btnConfirmMeal.setText(languageManager.getTranslation("confirmMeal"));
        mealsTitle.setText(languageManager.getTranslation("mealsTitle"));

        // Actualizar los textos dentro de MealGrid
        MealGrid.getChildren().forEach(node -> {

            if (node instanceof VBox) {

                VBox mealCard = (VBox) node;

                mealCard.getChildren().forEach(child -> {

                    if (child instanceof Label) {

                        Label label = (Label) child;

                        // Traducir días de la semana basados en claves del LanguageManager
                        if ("Lunes".equals(label.getText()) || "Monday".equals(label.getText())) {

                            label.setText(languageManager.getTranslation("monday"));

                        } else if ("Martes".equals(label.getText()) || "Tuesday".equals(label.getText())) {

                            label.setText(languageManager.getTranslation("tuesday"));

                        } else if ("Miércoles".equals(label.getText()) || "Wednesday".equals(label.getText())) {

                            label.setText(languageManager.getTranslation("wednesday"));

                        } else if ("Jueves".equals(label.getText()) || "Thursday".equals(label.getText())) {

                            label.setText(languageManager.getTranslation("thursday"));

                        } else if ("Viernes".equals(label.getText()) || "Friday".equals(label.getText())) {

                            label.setText(languageManager.getTranslation("friday"));

                        } else if ("Sábado".equals(label.getText()) || "Saturday".equals(label.getText())) {

                            label.setText(languageManager.getTranslation("saturday"));

                        } else if ("Domingo".equals(label.getText()) || "Sunday".equals(label.getText())) {

                            label.setText(languageManager.getTranslation("sunday"));

                        } else {

                            System.out.println("Texto no reconocido para traducción: " + label.getText());
                        }

                    } else if (child instanceof Button) {

                        Button button = (Button) child;

                        // Traducir el texto del botón de eliminar
                        if ("Eliminar".equals(button.getText()) || "Delete".equals(button.getText())) {

                            button.setText(languageManager.getTranslation("deleteMeal"));
                        }
                    }
                });
            }
        });

        // Depuración
        System.out.println("Traducciones aplicadas correctamente en MealViewController.");
    }

/////////////////////////////////FIN IDIOMAS///////////////////////////////////////////// 
    

    
    /**
     * Carga las comidas semanales del grupo del usuario desde la base de datos
     * y las muestra en el GridPane.
     */
    private void loadWeeklyMeals() {

        int groupId = CurrentSession.getInstance().getUserGroupId(); // Obtiene el ID del grupo actual
        List<Meal> meals = mealDAO.getAllMeals(groupId); // Filtra comidas por grupo
        MealGrid.getChildren().clear(); // Limpia el GridPane

        int col = 0;
        int row = 0; // Comenzamos en la fila 0

        for (Meal meal : meals) {
            
            addMealToGrid(meal, row, col); // Pasamos fila y columna
            col++; // Incrementamos la columna

            // Si llegamos a la columna 4, saltamos a la siguiente fila
            if (col == 3) {
                
                col = 0; // Reiniciamos la columna
                row++; // Pasamos a la siguiente fila
            }
        }
    }

    /**
     * Metodo que añade las comidas al grid
     *
     * @param meal
     * @param row
     * @param col
     */
    private void addMealToGrid(Meal meal, int row, int col) {

//        VBox mealCard = new VBox();
//        mealCard.setAlignment(Pos.CENTER);
//        mealCard.setSpacing(10); // Espaciado suficiente entre elementos
//        mealCard.setStyle("-fx-border-color: lightgray; -fx-padding: 10; -fx-background-color: #f9f9f9; -fx-border-radius: 5;");
        updateTexts();

        VBox mealCard = new VBox();
        mealCard.setAlignment(Pos.CENTER);
        mealCard.setSpacing(10);
        mealCard.setStyle("-fx-border-color: lightgray; -fx-padding: 10; -fx-background-color: #ffffff; -fx-border-radius: 5;");
        mealCard.setPrefWidth(250); // Igual al recipe-card
        mealCard.setPrefHeight(200);

        // Día de la semana
        Label dayLabel = new Label(meal.getDayOfWeek());
        dayLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #333;");

        // Imagen de la receta
        ImageView imageView = new ImageView();

        if (meal.getPhoto() != null) {

            imageView.setImage(new Image(new ByteArrayInputStream(meal.getPhoto())));

        } else {

            imageView.setImage(new Image("/homeSweetHome/view/images/add-image.jpg")); // Imagen por defecto
        }

        imageView.setFitWidth(100);
        imageView.setFitHeight(100);

        // Nombre de la receta
        Label recipeName = new Label(meal.getRecipeName());
        recipeName.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #555;");

        // Categoría de la receta
        Label recipeCategory = new Label(meal.getCategory());
        recipeCategory.setStyle("-fx-font-size: 12px; -fx-text-fill: #777;");

        // Botón de eliminar
        Button deleteButton = new Button("Eliminar");
        deleteButton.setStyle("-fx-background-color: #AA1803; -fx-text-fill: #FFFFFF; -fx-background-radius: 8;");
        deleteButton.setOnAction(event -> deleteMeal(meal)); // Llama al método deleteMeal al hacer clic
        deleteButton.setOnMouseEntered(e -> deleteButton.setStyle("-fx-background-color: #C32C19; -fx-text-fill: #FFFFFF; -fx-background-radius: 8;"));
        deleteButton.setOnMouseExited(e -> deleteButton.setStyle("-fx-background-color: #AA1803; -fx-text-fill: #FFFFFF; -fx-background-radius: 8;"));

        //Si es consultor, desactivamos boton
        if (role == 2) {

            deleteButton.setDisable(true);
        }

        // Añade todos los elementos a la tarjeta
        mealCard.getChildren().addAll(dayLabel, imageView, recipeName, recipeCategory, deleteButton);

        // Añade la tarjeta al GridPane
        MealGrid.add(mealCard, col, row); // Columna y fila dinámicas
    }

    
    /**
     * Metodo para eliminar comida
     *
     * @param meal
     */
    private void deleteMeal(Meal meal) {

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.getDialogPane().getStylesheets().add(getClass().getResource("/homeSweetHome/utils/alertsCss.css").toExternalForm());//Revisar!!

        if (LanguageManager.getInstance().getLanguageCode().equals("es")) {

            alert.setTitle("Confirmar Eliminación");
            alert.setHeaderText("¿Estás seguro de que deseas eliminar esta receta del menú semanal?");
            alert.setContentText("Receta: " + meal.getRecipeName() + "\nDía: " + meal.getDayOfWeek());

        } else if (LanguageManager.getInstance().getLanguageCode().equals("en")) {

            alert.setTitle("Confirm Delete");
            alert.setHeaderText("Are you sure you want to delete this recipe from the weekly menu?");
            alert.setContentText("Recipe: " + meal.getRecipeName() + "\nDay: " + meal.getDayOfWeek());

        }
//        alert.setTitle("Confirmar Eliminación");
//        alert.setHeaderText("¿Estás seguro de que deseas eliminar esta receta del menú semanal?");
//        alert.setContentText("Receta: " + meal.getRecipeName() + "\nDía: " + meal.getDayOfWeek());

        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {

            // Elimina la receta del menú en la base de datos
            boolean success = mealDAO.deleteMeal(meal.getId());

            if (success) {

                System.out.println("Receta eliminada del menú semanal: " + meal.getRecipeName());
                refreshGrid(); // Refresca el GridPane tras eliminar la receta

                if (LanguageManager.getInstance().getLanguageCode().equals("es")) {

                    AlertUtils.showAlert(Alert.AlertType.INFORMATION, "Eliminación Exitosa", "La receta fue eliminada correctamente.");

                } else if (LanguageManager.getInstance().getLanguageCode().equals("en")) {

                    AlertUtils.showAlert(Alert.AlertType.INFORMATION, "Successful Deletion", "The recipe was successfully deleted.");

                }

            } else {

                System.err.println("Error al eliminar la receta del menú semanal: " + meal.getRecipeName());
                //AlertUtils.showAlert(Alert.AlertType.ERROR, "Error", "No se pudo eliminar la receta. Por favor, inténtalo nuevamente.");
            }
        }
    }


    /**
     * Refresca el grid pane
     */
    private void refreshGrid() {
        MealGrid.getChildren().clear(); // Limpia el grid
        int column = 0;
        int row = 0;

        int groupId = CurrentSession.getInstance().getUserGroupId(); // Obtiene el grupo del usuario actual
        List<Meal> weeklyMeals = mealDAO.getAllMeals(groupId); // Ahora pasa el ID de grupo como argumento

        for (Meal meal : weeklyMeals) {
            addMealToGrid(meal, row, column);
            column++;

            if (column == 3) { // Máximo 3 columnas por fila
                column = 0;
                row++;
            }
        }
    }

    /**
     * Acción para generar el menú semanal dinámicamente.
     *
     * @param event Evento del botón.
     */
    @FXML
    private void generateMeal(ActionEvent event) {

        // Elimina el menú semanal existente
        if (!mealDAO.deleteAllMeals()) {

            AlertUtils.showAlert(Alert.AlertType.ERROR, "Error al borrar", "No se pudo borrar el menú semanal actual.");
            return; // Si falla, detenemos la ejecución
        }

        RecipeDAO recipeDAO = new RecipeDAO();
        int groupId = CurrentSession.getInstance().getUserGroupId(); // Obtiene el ID de grupo actual

        // Obtiene recetas disponibles desde la base de datos
        List<Recipe> availableRecipes = recipeDAO.getRecipesByGroup(groupId);

        // Traducción estática de días de la semana (inglés -> español)
        Map<String, String> daysMap = Map.of(
                "Monday", "Lunes",
                "Tuesday", "Martes",
                "Wednesday", "Miércoles",
                "Thursday", "Jueves",
                "Friday", "Viernes",
                "Saturday", "Sábado",
                "Sunday", "Domingo"
        );

        // Días en inglés para iterar
        String[] daysOfWeek = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};

        if (availableRecipes.isEmpty()) {

            if (LanguageManager.getInstance().getLanguageCode().equals("es")) {

                AlertUtils.showAlert(Alert.AlertType.WARNING, "Sin Recetas", "No hay recetas disponibles para generar el menú semanal.");

            } else if (LanguageManager.getInstance().getLanguageCode().equals("en")) {

                AlertUtils.showAlert(Alert.AlertType.WARNING, "No Recipes", "There are no available recipes to generate the weekly menu.");

            }

            return;
        }

        if (availableRecipes.size() < daysOfWeek.length) {

            if (LanguageManager.getInstance().getLanguageCode().equals("es")) {

                AlertUtils.showAlert(Alert.AlertType.WARNING, "Recetas Insuficientes",
                        "No hay suficientes recetas para generar un menú semanal.");

            } else if (LanguageManager.getInstance().getLanguageCode().equals("en")) {

                AlertUtils.showAlert(Alert.AlertType.WARNING, "Insufficient Recipes",
                        "There are not enough recipes to generate a weekly menu.");

            }

            return;
        }

        // Crea el nuevo menú semanal con recetas aleatorias
        Random random = new Random();
        Set<Integer> usedIndexes = new HashSet<>(); // Para evitar recetas repetidas

        for (String day : daysOfWeek) {

            int randomIndex;

            do {

                randomIndex = random.nextInt(availableRecipes.size());
            } while (usedIndexes.contains(randomIndex)); // Asegura que no se repita la receta

            usedIndexes.add(randomIndex);

            Recipe recipe = availableRecipes.get(randomIndex);

            Meal meal = new Meal();
            meal.setDayOfWeek(daysMap.get(day)); // Traducción garantizada aquí
            meal.setRecipeId(recipe.getId());
            meal.setRecipeName(recipe.getNombre());
            meal.setCategory(recipe.getTipo());
            meal.setPhoto(recipe.getFoto());
            meal.setGroupId(CurrentSession.getInstance().getUserGroupId());

            mealDAO.addMeal(meal); // Agrega la comida a la base de datos
            System.out.println("Comida asignada: Día " + daysMap.get(day) + " - Receta: " + recipe.getNombre());
        }

        // Actualiza el GridPane después de generar el menú
        loadWeeklyMeals(); // Recarga la vista

        // Muestra el botón "Confirmar Menú"
        btnConfirmMeal.setVisible(true);
    }

    /**
     * Evento que confirma la eleccion del menu semanal -> pasa a inventario
     * inf¡gredientes si necesario
     *
     * @param event
     */
    @FXML
    private void confirmMeal(ActionEvent event) {

        RecipeDAO recipeDAO = new RecipeDAO();
        InventoryDAO inventoryDAO = new InventoryDAO();
        int groupId = CurrentSession.getInstance().getUserGroupId(); // Obtiene el ID del grupo actual

        // Mapa para acumular las cantidades necesarias por producto
        Map<String, Integer> productoCantidadNecesaria = new HashMap<>();

        // Listas para almacenar los productos nuevos y los productos actualizados
        List<String> nuevosProductos = new ArrayList<>();
        List<String> productosModificados = new ArrayList<>();

        // Recupera todas las comidas del menú semanal
        List<Meal> weeklyMeals = mealDAO.getAllMeals(groupId);
        System.out.println("Menú semanal recuperado: " + weeklyMeals.size() + " comidas.");
        System.out.println("Grupo de usuario: " + groupId);

        for (Meal meal : weeklyMeals) {

            System.out.println("Procesando comida: " + meal.getId() + " - Receta: " + meal.getRecipeId());

            // Obtiene los productos necesarios para la receta
            List<Product> products = recipeDAO.getProductsFromRecipe(meal.getRecipeId());
            System.out.println("Productos asociados a la receta " + meal.getRecipeId() + ": " + products.size());

            for (Product product : products) {

                String nombreProducto = product.getNombreProducto();
                int cantidadNecesaria = product.getCantidad();

                // Verifica si el producto ya existe en el mapa y sumar cantidades
                if (productoCantidadNecesaria.containsKey(nombreProducto)) {

                    int cantidadAcumulada = productoCantidadNecesaria.get(nombreProducto);
                    productoCantidadNecesaria.put(nombreProducto, cantidadAcumulada + cantidadNecesaria);

                } else {

                    productoCantidadNecesaria.put(nombreProducto, cantidadNecesaria);
                }
            }
        }

        // Gestiona acumulaciones y actualiza el inventario
        for (Map.Entry<String, Integer> entry : productoCantidadNecesaria.entrySet()) {

            String nombreProducto = entry.getKey();
            int cantidadTotalNecesaria = entry.getValue();
            String categoria = "Alimentación"; // Asigna una categoría predeterminada si es necesario

            System.out.println("Procesando producto acumulado: " + nombreProducto + ", Cantidad total necesaria: " + cantidadTotalNecesaria);

            if (inventoryDAO.isProductInInventory(nombreProducto, groupId)) {

                // Si el producto ya existe, verifica cantidad
                int productId = inventoryDAO.getInventoryProductIdByName(nombreProducto, groupId);
                int currentQuantity = inventoryDAO.getCurrentQuantityById(productId);
                int minQuantity = inventoryDAO.getMinQuantity(productId);

                System.out.println("Cantidad actual: " + currentQuantity + ", Cantidad mínima: " + minQuantity);

                if (currentQuantity >= cantidadTotalNecesaria) {

                    // Hay suficiente cantidad, no se modifica
                    System.out.println("Suficientes " + nombreProducto + ". No se requiere modificación.");

                } else {

                    // Ajusta cantidad mínima
                    int newMinQuantity = Math.max(minQuantity, cantidadTotalNecesaria);
                    System.out.println("Déficit de " + nombreProducto + ". Ajustando cantidad mínima a: " + newMinQuantity);

                    Product updatedProduct = new Product();
                    updatedProduct.setId(productId);
                    updatedProduct.setNombreProducto(nombreProducto);
                    updatedProduct.setCantidad(currentQuantity);
                    updatedProduct.setCantidadMinima(newMinQuantity);
                    updatedProduct.setCategoria(categoria);
                    updatedProduct.setTipo("Cantidad");
                    updatedProduct.setIdGrupo(groupId);

                    if (inventoryDAO.updateInventoryProduct(updatedProduct)) {

                        productosModificados.add(nombreProducto + " - Nueva cantidad mínima: " + newMinQuantity);

                    } else {

                        System.err.println("Error al ajustar la cantidad mínima de " + nombreProducto);
                    }
                }

            } else {

                // Añade producto nuevo al inventario
                System.out.println("Producto no existe en inventario, añadiéndolo: " + nombreProducto);

                Product newProduct = new Product();
                newProduct.setNombreProducto(nombreProducto);
                newProduct.setCantidad(0);
                newProduct.setCantidadMinima(cantidadTotalNecesaria);
                newProduct.setCategoria(categoria);
                newProduct.setTipo("Cantidad");
                newProduct.setIdGrupo(groupId);

                if (inventoryDAO.addInventoryProduct(newProduct)) {

                    System.out.println("Producto añadido exitosamente: " + nombreProducto);
                    nuevosProductos.add(nombreProducto + " - Cantidad mínima: " + cantidadTotalNecesaria);

                } else {

                    System.err.println("Error al añadir el producto: " + nombreProducto);
                }
            }
        }

        // Genera mensajes de confirmación con productos añadidos y modificados
        StringBuilder mensaje = new StringBuilder();

        if (!nuevosProductos.isEmpty()) {

            mensaje.append("Se han añadido nuevos productos al inventario:\n")
                    .append(String.join("\n", nuevosProductos))
                    .append("\n\n");
        }

        if (!productosModificados.isEmpty()) {

            mensaje.append("Se han modificado los siguientes productos:\n")
                    .append(String.join("\n", productosModificados));
        }

        if (mensaje.length() > 0) {

            AlertUtils.showAlert(Alert.AlertType.INFORMATION, "Confirmación Exitosa", mensaje.toString());

        } else {

            AlertUtils.showAlert(Alert.AlertType.INFORMATION, "Confirmación Exitosa",
                    "No hubo cambios en el inventario.");
        }
    }

}
