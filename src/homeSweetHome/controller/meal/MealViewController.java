package homeSweetHome.controller.meal;

import homeSweetHome.dataPersistence.CurrentSession;
import homeSweetHome.dataPersistence.InventoryDAO;
import homeSweetHome.dataPersistence.MealDAO;
import homeSweetHome.dataPersistence.RecipeDAO;
import homeSweetHome.model.Meal;
import homeSweetHome.model.Product;
import homeSweetHome.model.Recipe;
import homeSweetHome.utils.AlertUtils;
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
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

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

    private MealDAO mealDAO;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        mealDAO = new MealDAO(); // Inicializa el DAO
        loadWeeklyMeals(); // Carga las comidas al inicio
        btnConfirmMeal.setVisible(false); // Ocultar botón inicialmente
    }

    /**
     * Carga las comidas semanales desde la base de datos y las muestra en el
     * GridPane.
     */
    private void loadWeeklyMeals() {
        List<Meal> meals = mealDAO.getAllMeals(); // Recupera todas las comidas semanales
        MealGrid.getChildren().clear(); // Limpia el GridPane

        int col = 0;
        int row = 0; // Comenzamos en la fila 0

        for (Meal meal : meals) {
            addMealToGrid(meal, row, col); // Pasamos tanto fila como columna
            col++; // Incrementamos la columna

            // Si llegamos a la columna 4, saltamos a la siguiente fila
            if (col == 4) {
                col = 0; // Reiniciamos la columna
                row++; // Pasamos a la siguiente fila
            }
        }
    }

//    /**
//     * Añade una comida semanal al GridPane como una tarjeta.
//     *
//     * @param meal La comida a mostrar.
//     * @param row La fila en la que se añadirá.
//     */
//    private void addMealToGrid(Meal meal, int row, int col) {
//        VBox mealCard = new VBox();
//        mealCard.setAlignment(Pos.CENTER);
//        mealCard.setSpacing(15); // Espaciado suficiente entre elementos
//        mealCard.setStyle("-fx-border-color: lightgray; -fx-padding: 10;");
//
//        // Día de la semana
//        Label dayLabel = new Label(meal.getDayOfWeek());
//        dayLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #333;");
//
//        // Imagen de la receta
//        ImageView imageView = new ImageView();
//        if (meal.getPhoto() != null) {
//            imageView.setImage(new Image(new ByteArrayInputStream(meal.getPhoto())));
//        } else {
//            imageView.setImage(new Image("/homeSweetHome/view/images/add-image.jpg")); // Imagen por defecto
//        }
//        imageView.setFitWidth(100);
//        imageView.setFitHeight(100);
//
//        // Nombre de la receta
//        Label recipeName = new Label(meal.getRecipeName());
//        recipeName.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #555;");
//
//        // Categoría de la receta
//        Label recipeCategory = new Label(meal.getCategory());
//        recipeCategory.setStyle("-fx-font-size: 12px; -fx-text-fill: #777;");
//
//        // Añadir todos los elementos a la tarjeta
//        mealCard.getChildren().addAll(dayLabel, imageView, recipeName, recipeCategory);
//
//        // Añadir la tarjeta al GridPane
//        MealGrid.add(mealCard, col, row); // Columna y fila dinámicas
//    }
    private void addMealToGrid(Meal meal, int row, int col) {
        VBox mealCard = new VBox();
        mealCard.setAlignment(Pos.CENTER);
        mealCard.setSpacing(15); // Espaciado suficiente entre elementos
        mealCard.setStyle("-fx-border-color: lightgray; -fx-padding: 10; -fx-background-color: #f9f9f9; -fx-border-radius: 5;");

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
        deleteButton.setStyle("-fx-background-color: #D35C37; -fx-text-fill: white; -fx-font-size: 12px; -fx-background-radius: 5;");
        deleteButton.setOnAction(event -> deleteMeal(meal)); // Llamar al método deleteMeal al hacer clic

        // Añadir todos los elementos a la tarjeta
        mealCard.getChildren().addAll(dayLabel, imageView, recipeName, recipeCategory, deleteButton);

        // Añadir la tarjeta al GridPane
        MealGrid.add(mealCard, col, row); // Columna y fila dinámicas
    }

    private void deleteMeal(Meal meal) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar Eliminación");
        alert.setHeaderText("¿Estás seguro de que deseas eliminar esta receta del menú semanal?");
        alert.setContentText("Receta: " + meal.getRecipeName() + "\nDía: " + meal.getDayOfWeek());

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            // Eliminar la receta del menú en la base de datos
            boolean success = mealDAO.deleteMeal(meal.getId());
            if (success) {
                System.out.println("Receta eliminada del menú semanal: " + meal.getRecipeName());
                refreshGrid(); // Refrescar el GridPane tras eliminar la receta
                AlertUtils.showAlert(Alert.AlertType.INFORMATION, "Eliminación Exitosa", "La receta fue eliminada correctamente.");
            } else {
                System.err.println("Error al eliminar la receta del menú semanal: " + meal.getRecipeName());
                AlertUtils.showAlert(Alert.AlertType.ERROR, "Error", "No se pudo eliminar la receta. Por favor, inténtalo nuevamente.");
            }
        }
    }

    private void refreshGrid() {
        MealGrid.getChildren().clear(); // Limpiar el grid
        int column = 0;
        int row = 0;

        List<Meal> weeklyMeals = mealDAO.getAllMeals(); // Recuperar todas las comidas
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
//    @FXML
//    private void generateMeal(ActionEvent event) {
//        // Eliminar el menú semanal existente
//        if (!mealDAO.deleteAllMeals()) {
//            AlertUtils.showAlert(Alert.AlertType.ERROR, "Error al borrar", "No se pudo borrar el menú semanal actual.");
//            return; // Si falla, detenemos la ejecución
//        }
//
//        RecipeDAO recipeDAO = new RecipeDAO();
//
//        // Obtener recetas disponibles desde la base de datos
//        List<Recipe> availableRecipes = recipeDAO.getAllRecipes();
//        String[] daysOfWeek = {"Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo"};
//
//        if (availableRecipes.size() < daysOfWeek.length) {
//            AlertUtils.showAlert(Alert.AlertType.WARNING, "Recetas Insuficientes",
//                    "No hay suficientes recetas para generar un menú semanal.");
//            return; // Detenemos si no hay suficientes recetas
//        }
//
//        // Generar el nuevo menú semanal
//        for (int i = 0; i < daysOfWeek.length; i++) {
//            Recipe recipe = availableRecipes.get(i);
//
//            Meal meal = new Meal();
//            meal.setDayOfWeek(daysOfWeek[i]);
//            meal.setRecipeId(recipe.getId());
//            meal.setRecipeName(recipe.getNombre());
//            meal.setCategory(recipe.getTipo());
//            meal.setPhoto(recipe.getFoto());
//            meal.setGroupId(CurrentSession.getInstance().getUserGroupId());
//
//            mealDAO.addMeal(meal);
//        }
//
//        // Actualizar el GridPane después de generar el menú
//        loadWeeklyMeals(); // Recargar la vista
//
//        // Mostrar el botón "Confirmar Menú"
//        btnConfirmMeal.setVisible(true);
//    }
    @FXML
    private void generateMeal(ActionEvent event) {
        // Eliminar el menú semanal existente
        if (!mealDAO.deleteAllMeals()) {
            AlertUtils.showAlert(Alert.AlertType.ERROR, "Error al borrar", "No se pudo borrar el menú semanal actual.");
            return; // Si falla, detenemos la ejecución
        }

        RecipeDAO recipeDAO = new RecipeDAO();

        // Obtener recetas disponibles desde la base de datos
        List<Recipe> availableRecipes = recipeDAO.getAllRecipes();
        String[] daysOfWeek = {"Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo"};

        if (availableRecipes.isEmpty()) {
            AlertUtils.showAlert(Alert.AlertType.WARNING, "Sin Recetas", "No hay recetas disponibles para generar el menú semanal.");
            return;
        }

        if (availableRecipes.size() < daysOfWeek.length) {
            AlertUtils.showAlert(Alert.AlertType.WARNING, "Recetas Insuficientes",
                    "No hay suficientes recetas para generar un menú semanal.");
            return;
        }

        // Crear el nuevo menú semanal con recetas aleatorias
        Random random = new Random();
        Set<Integer> usedIndexes = new HashSet<>(); // Para evitar recetas repetidas

        for (String day : daysOfWeek) {
            int randomIndex;
            do {
                randomIndex = random.nextInt(availableRecipes.size());
            } while (usedIndexes.contains(randomIndex)); // Asegurar que no se repita la receta
            usedIndexes.add(randomIndex);

            Recipe recipe = availableRecipes.get(randomIndex);

            Meal meal = new Meal();
            meal.setDayOfWeek(day);
            meal.setRecipeId(recipe.getId());
            meal.setRecipeName(recipe.getNombre());
            meal.setCategory(recipe.getTipo());
            meal.setPhoto(recipe.getFoto());
            meal.setGroupId(CurrentSession.getInstance().getUserGroupId());

            mealDAO.addMeal(meal); // Agregar la comida a la base de datos
            System.out.println("Comida asignada: Día " + day + " - Receta: " + recipe.getNombre());
        }

        // Actualizar el GridPane después de generar el menú
        loadWeeklyMeals(); // Recargar la vista

        // Mostrar el botón "Confirmar Menú"
        btnConfirmMeal.setVisible(true);
    }

//    @FXML
//    private void confirmMeal(ActionEvent event) {
//        RecipeDAO recipeDAO = new RecipeDAO();
//        InventoryDAO inventoryDAO = new InventoryDAO();
//        int groupId = CurrentSession.getInstance().getUserGroupId(); // Obtener el ID del grupo actual
//
//        // Listas para almacenar los productos nuevos y los productos actualizados
//        List<String> nuevosProductos = new ArrayList<>();
//        List<String> productosModificados = new ArrayList<>();
//
//        // Recuperar todas las comidas del menú semanal
//        List<Meal> weeklyMeals = mealDAO.getAllMeals();
//        System.out.println("Menú semanal recuperado: " + weeklyMeals.size() + " comidas.");
//        System.out.println("Grupo de usuario: " + groupId);
//
//        for (Meal meal : weeklyMeals) {
//            System.out.println("Procesando comida: " + meal.getId() + " - Receta: " + meal.getRecipeId());
//
//            // Obtener los productos necesarios para la receta
//            List<Product> products = recipeDAO.getProductsFromRecipe(meal.getRecipeId());
//            System.out.println("Productos asociados a la receta " + meal.getRecipeId() + ": " + products.size());
//
//            for (Product product : products) {
//                System.out.println("Producto: " + product.getNombreProducto() + ", Cantidad necesaria: " + product.getCantidad());
//
//                if (inventoryDAO.isProductInInventory(product.getNombreProducto(), groupId)) {
//                    // Si el producto ya existe, verificar cantidad
//                    int productId = inventoryDAO.getInventoryProductIdByName(product.getNombreProducto(), groupId);
//                    int currentQuantity = inventoryDAO.getCurrentQuantityById(productId);
//                    int minQuantity = inventoryDAO.getMinQuantity(productId);
//
//                    System.out.println("Cantidad actual: " + currentQuantity + ", Cantidad mínima: " + minQuantity);
//
//                    if (currentQuantity >= product.getCantidad()) {
//                        // Hay suficiente cantidad, no se modifica
//                        System.out.println("Suficientes " + product.getNombreProducto() + ". No se requiere modificación.");
//                    } else {
//                        // No hay suficiente cantidad, ajustar la cantidad mínima
//                        int deficit = product.getCantidad() - currentQuantity;
//                        int newMinQuantity = Math.max(minQuantity, product.getCantidad());
//                        System.out.println("Déficit de " + product.getNombreProducto() + ": " + deficit + ". Ajustando cantidad mínima a: " + newMinQuantity);
//
//                        Product updatedProduct = new Product(); // Usamos constructor vacío
//                        updatedProduct.setId(productId);
//                        updatedProduct.setNombreProducto(product.getNombreProducto());
//                        updatedProduct.setCantidad(currentQuantity); // No se suma la cantidad
//                        updatedProduct.setCantidadMinima(newMinQuantity); // Nueva cantidad mínima
//                        updatedProduct.setCantidadMaxima(0); // Sin cantidad máxima
//                        updatedProduct.setTipo(product.getTipo());
//                        updatedProduct.setCategoria(product.getCategoria());
//                        updatedProduct.setIdGrupo(groupId);
//                        updatedProduct.setFecha(null); // Usamos null para el campo `fecha`
//
//                        if (inventoryDAO.updateInventoryProduct(updatedProduct)) {
//                            productosModificados.add(product.getNombreProducto() + " - Nueva cantidad mínima: " + newMinQuantity);
//                        } else {
//                            System.err.println("Error al ajustar la cantidad mínima de " + product.getNombreProducto());
//                        }
//                    }
//                } else {
//                    // Producto no existe, añadirlo
//                    System.out.println("Producto no existe en inventario, añadiéndolo: " + product.getNombreProducto());
//                    product.setCategoria("Alimentación"); // Valor por defecto
//                    product.setCantidadMinima(product.getCantidad()); // Ajustar cantidad mínima al necesario
//                    product.setIdGrupo(groupId); // Asignar grupo actual
//                    product.setCantidadMaxima(0); // Sin cantidad máxima
//                    product.setFecha(null); // Usamos null para `fecha`
//
//                    if (inventoryDAO.addInventoryProduct(product)) {
//                        System.out.println("Producto añadido exitosamente: " + product.getNombreProducto());
//                        nuevosProductos.add(product.getNombreProducto() + " - Cantidad: " + product.getCantidad());
//                    } else {
//                        System.err.println("Error al añadir el producto: " + product.getNombreProducto());
//                    }
//                }
//            }
//        }
//
//        // Generar mensajes de confirmación con productos añadidos y modificados
//        StringBuilder mensaje = new StringBuilder();
//        if (!nuevosProductos.isEmpty()) {
//            mensaje.append("Se han añadido nuevos productos al inventario:\n")
//                    .append(String.join("\n", nuevosProductos))
//                    .append("\n\n");
//        }
//        if (!productosModificados.isEmpty()) {
//            mensaje.append("Se han modificado los siguientes productos:\n")
//                    .append(String.join("\n", productosModificados));
//        }
//
//        if (mensaje.length() > 0) {
//            AlertUtils.showAlert(Alert.AlertType.INFORMATION, "Confirmación Exitosa", mensaje.toString());
//        } else {
//            AlertUtils.showAlert(Alert.AlertType.INFORMATION, "Confirmación Exitosa",
//                    "No hubo cambios en el inventario.");
//        }
//    }
//    @FXML
//    private void confirmMeal(ActionEvent event) {
//        RecipeDAO recipeDAO = new RecipeDAO();
//        InventoryDAO inventoryDAO = new InventoryDAO();
//        int groupId = CurrentSession.getInstance().getUserGroupId(); // Obtener el ID del grupo actual
//
//        // Listas para almacenar los productos nuevos y los productos actualizados
//        List<String> nuevosProductos = new ArrayList<>();
//        List<String> productosModificados = new ArrayList<>();
//
//        // Recuperar todas las comidas del menú semanal
//        List<Meal> weeklyMeals = mealDAO.getAllMeals();
//        System.out.println("Menú semanal recuperado: " + weeklyMeals.size() + " comidas.");
//        System.out.println("Grupo de usuario: " + groupId);
//
//        for (Meal meal : weeklyMeals) {
//            System.out.println("Procesando comida: " + meal.getId() + " - Receta: " + meal.getRecipeId());
//
//            // Obtener los productos necesarios para la receta
//            List<Product> products = recipeDAO.getProductsFromRecipe(meal.getRecipeId());
//            System.out.println("Productos asociados a la receta " + meal.getRecipeId() + ": " + products.size());
//
//            for (Product product : products) {
//                System.out.println("Producto: " + product.getNombreProducto() + ", Cantidad necesaria: " + product.getCantidad());
//
//                if (inventoryDAO.isProductInInventory(product.getNombreProducto(), groupId)) {
//                    // Si el producto ya existe, verificar cantidad
//                    int productId = inventoryDAO.getInventoryProductIdByName(product.getNombreProducto(), groupId);
//                    int currentQuantity = inventoryDAO.getCurrentQuantityById(productId);
//                    int minQuantity = inventoryDAO.getMinQuantity(productId);
//
//                    System.out.println("Cantidad actual: " + currentQuantity + ", Cantidad mínima: " + minQuantity);
//
//                    if (currentQuantity >= product.getCantidad()) {
//                        // Hay suficiente cantidad, no se modifica
//                        System.out.println("Suficientes " + product.getNombreProducto() + ". No se requiere modificación.");
//                    } else {
//                        // No hay suficiente cantidad, ajustar la cantidad mínima
//                        int deficit = product.getCantidad() - currentQuantity;
//                        int newMinQuantity = Math.max(minQuantity, product.getCantidad());
//                        System.out.println("Déficit de " + product.getNombreProducto() + ": " + deficit + ". Ajustando cantidad mínima a: " + newMinQuantity);
//
//                        Product updatedProduct = new Product(); // Usamos constructor vacío
//                        updatedProduct.setId(productId);
//                        updatedProduct.setNombreProducto(product.getNombreProducto());
//                        updatedProduct.setCantidad(currentQuantity); // No se suma la cantidad
//                        updatedProduct.setCantidadMinima(newMinQuantity); // Nueva cantidad mínima
//                        updatedProduct.setCantidadMaxima(0); // Sin cantidad máxima
//                        updatedProduct.setTipo(product.getTipo());
//                        updatedProduct.setCategoria(product.getCategoria());
//                        updatedProduct.setIdGrupo(groupId);
//                        updatedProduct.setFecha(null); // Usamos null para el campo `fecha`
//
//                        if (inventoryDAO.updateInventoryProduct(updatedProduct)) {
//                            productosModificados.add(product.getNombreProducto() + " - Nueva cantidad mínima: " + newMinQuantity);
//                        } else {
//                            System.err.println("Error al ajustar la cantidad mínima de " + product.getNombreProducto());
//                        }
//                    }
//                } else {
//                    // Producto no existe, añadirlo
//                    System.out.println("Producto no existe en inventario, añadiéndolo: " + product.getNombreProducto());
//
//                    // Corrección: Asignar cantidad mínima basada en la receta
//                    System.out.println(">> DEPURANDO Producto nuevo: ");
//                    System.out.println("Cantidad Necesaria: " + product.getCantidad());
//
//                    // Validación: Si `getCantidad` devuelve 0, asignamos valor correcto explícitamente
//                    int cantidadNecesaria = product.getCantidad();
//                    if (cantidadNecesaria <= 0) {
//                        System.err.println("Error: Cantidad necesaria no válida para el producto " + product.getNombreProducto());
//                        cantidadNecesaria = 1; // Fallback de seguridad
//                    }
//
//                    product.setCantidad(0); // Cantidad inicial en inventario: 0
//                    product.setCantidadMinima(cantidadNecesaria); // Cantidad mínima basada en la receta
//                    product.setIdGrupo(groupId); // Asignar grupo actual
//                    product.setCantidadMaxima(0); // Sin cantidad máxima
//                    product.setFecha(null); // Usamos null para `fecha`
//
//                    System.out.println("Cantidad Final: " + product.getCantidad());
//                    System.out.println("Cantidad Mínima Final: " + product.getCantidadMinima());
//
//                    if (inventoryDAO.addInventoryProduct(product)) {
//                        System.out.println("Producto añadido exitosamente: " + product.getNombreProducto());
//                        nuevosProductos.add(product.getNombreProducto() + " - Cantidad mínima: " + product.getCantidadMinima());
//                    } else {
//                        System.err.println("Error al añadir el producto: " + product.getNombreProducto());
//                    }
//                }
//            }
//        }
//
//        // Generar mensajes de confirmación con productos añadidos y modificados
//        StringBuilder mensaje = new StringBuilder();
//        if (!nuevosProductos.isEmpty()) {
//            mensaje.append("Se han añadido nuevos productos al inventario:\n")
//                    .append(String.join("\n", nuevosProductos))
//                    .append("\n\n");
//        }
//        if (!productosModificados.isEmpty()) {
//            mensaje.append("Se han modificado los siguientes productos:\n")
//                    .append(String.join("\n", productosModificados));
//        }
//
//        if (mensaje.length() > 0) {
//            AlertUtils.showAlert(Alert.AlertType.INFORMATION, "Confirmación Exitosa", mensaje.toString());
//        } else {
//            AlertUtils.showAlert(Alert.AlertType.INFORMATION, "Confirmación Exitosa",
//                    "No hubo cambios en el inventario.");
//        }
//    }
    @FXML
    private void confirmMeal(ActionEvent event) {
        RecipeDAO recipeDAO = new RecipeDAO();
        InventoryDAO inventoryDAO = new InventoryDAO();
        int groupId = CurrentSession.getInstance().getUserGroupId(); // Obtener el ID del grupo actual

        // Mapa para acumular las cantidades necesarias por producto
        Map<String, Integer> productoCantidadNecesaria = new HashMap<>();

        // Listas para almacenar los productos nuevos y los productos actualizados
        List<String> nuevosProductos = new ArrayList<>();
        List<String> productosModificados = new ArrayList<>();

        // Recuperar todas las comidas del menú semanal
        List<Meal> weeklyMeals = mealDAO.getAllMeals();
        System.out.println("Menú semanal recuperado: " + weeklyMeals.size() + " comidas.");
        System.out.println("Grupo de usuario: " + groupId);

        for (Meal meal : weeklyMeals) {
            System.out.println("Procesando comida: " + meal.getId() + " - Receta: " + meal.getRecipeId());

            // Obtener los productos necesarios para la receta
            List<Product> products = recipeDAO.getProductsFromRecipe(meal.getRecipeId());
            System.out.println("Productos asociados a la receta " + meal.getRecipeId() + ": " + products.size());

            for (Product product : products) {
                String nombreProducto = product.getNombreProducto();
                int cantidadNecesaria = product.getCantidad();

                // Verificar si el producto ya existe en el mapa y sumar cantidades
                if (productoCantidadNecesaria.containsKey(nombreProducto)) {
                    int cantidadAcumulada = productoCantidadNecesaria.get(nombreProducto);
                    productoCantidadNecesaria.put(nombreProducto, cantidadAcumulada + cantidadNecesaria);
                } else {
                    productoCantidadNecesaria.put(nombreProducto, cantidadNecesaria);
                }
            }
        }

        // Procesar acumulaciones y actualizar el inventario
        for (Map.Entry<String, Integer> entry : productoCantidadNecesaria.entrySet()) {
            String nombreProducto = entry.getKey();
            int cantidadTotalNecesaria = entry.getValue();
            String categoria = "Alimentación"; // Asignar una categoría predeterminada si es necesario

            System.out.println("Procesando producto acumulado: " + nombreProducto + ", Cantidad total necesaria: " + cantidadTotalNecesaria);

            if (inventoryDAO.isProductInInventory(nombreProducto, groupId)) {
                // Si el producto ya existe, verificar cantidad
                int productId = inventoryDAO.getInventoryProductIdByName(nombreProducto, groupId);
                int currentQuantity = inventoryDAO.getCurrentQuantityById(productId);
                int minQuantity = inventoryDAO.getMinQuantity(productId);

                System.out.println("Cantidad actual: " + currentQuantity + ", Cantidad mínima: " + minQuantity);

                if (currentQuantity >= cantidadTotalNecesaria) {
                    // Hay suficiente cantidad, no se modifica
                    System.out.println("Suficientes " + nombreProducto + ". No se requiere modificación.");
                } else {
                    // Ajustar cantidad mínima
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
                // Añadir producto nuevo al inventario
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

        // Generar mensajes de confirmación con productos añadidos y modificados
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
