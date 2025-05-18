package homeSweetHome.controller.purchase;

import homeSweetHome.dataPersistence.BudgetDAO;
import homeSweetHome.dataPersistence.CurrentSession;
import homeSweetHome.dataPersistence.InventoryDAO;
import homeSweetHome.dataPersistence.PurchaseDAO;
import homeSweetHome.dataPersistence.UserDAO;
import homeSweetHome.model.Budget;
import homeSweetHome.model.Product;
import homeSweetHome.utils.AlertUtils;
import homeSweetHome.utils.LanguageManager;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TableCell;
import javafx.scene.control.Label;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.converter.IntegerStringConverter;
import java.sql.SQLException;
import java.util.Arrays;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.TextInputDialog;
import java.nio.file.*;
import java.io.IOException;
import javax.mail.PasswordAuthentication;
import java.util.Properties;
import javafx.scene.image.Image;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * Controlador para gestionar la vista de compras. Permite visualizar y
 * manipular el inventario, interactuar con el usuario y realizar
 * actualizaciones en la base de datos.
 */
public class PurchaseViewController {

    // Tabla del inventario
    @FXML
    private TableView<Product> tableViewInventory; // Ahora trabaja con Product
    @FXML
    private TableColumn<Product, String> colProductName; // Columna para el nombre del producto
    @FXML
    private TableColumn<Product, Void> colQuantity; // Columna para "Cantidad Actual" con botones y valor
    @FXML
    private TableColumn<Product, Void> colMinQuantity; // Columna para "Cantidad Mínima" con botones y valor
    @FXML
    private TableColumn<Product, String> colCategory; // Columna para la categoría
    @FXML
    private TableColumn<Product, String> colType; // Columna para mostrar el tipo en la tabla de inventario
    @FXML
    private TableColumn<Product, Void> colQuantityControls; // Columna para botones dinámicos
    // Tabla de la lista de compras
    @FXML
    private TableView<Product> tableViewShoppingList; // Ahora trabaja con Product
    @FXML
    private TableColumn<Product, String> colShoppingProduct; // Columna para el nombre del producto en la lista de compras
    @FXML
    private TableColumn<Product, Integer> colShoppingQuantity; // Columna para la cantidad necesaria
    @FXML
    private TableColumn<Product, String> colShoppingType; // Columna para mostrar el tipo en la tabla de lista de compras
    @FXML
    private Button btnOpenAddNewProduct; // Botón para añadir productos
    @FXML
    private Button btnCompletePurchase; // Botón para completar la compra
    @FXML
    private Label purchasesTitle;
    @FXML
    private Button btnSendPurchase;
    @FXML
    private Label instructionProduct;

    // Lista interna que contiene los datos del inventario
    private ObservableList<Product> inventoryList = FXCollections.observableArrayList();

    // Lista interna que contiene los datos de la lista de compras
    private ObservableList<Product> shoppingList = FXCollections.observableArrayList();

    int role = CurrentSession.getInstance().getUserRole(); // Tomamos rol para control de permisos


    /**
     * Carga los productos del inventario pertenecientes al grupo especificado.
     *
     * @param groupId El ID del grupo cuyos productos se cargarán.
     */
    public void loadInventory(int groupId) {
        InventoryDAO inventoryDAO = new InventoryDAO();

        // Limpia la lista actual antes de cargar nuevos datos
        System.out.println("=== Iniciando carga de inventario ===");
        System.out.println("ID del grupo: " + groupId);
        inventoryList.clear();

        // Recupera los datos del inventario desde el DAO
        System.out.println("Recuperando productos del DAO...");
        List<Product> productosCargados = inventoryDAO.getAllInventoryProducts(groupId);

        // Depuración: verifica los datos recuperados desde el DAO
        System.out.println("=== Productos recuperados del DAO ===");
        for (Product product : productosCargados) {
            System.out.println("Producto recuperado: ");
            System.out.println("Nombre: " + product.getNombreProducto());
            System.out.println("Cantidad: " + product.getCantidad());
            System.out.println("Cantidad mínima: " + product.getCantidadMinima());
            System.out.println("Medida (Tipo): " + product.getTipo());
            System.out.println("Categoría: " + product.getCategoria());
        }

        // Agrega los productos cargados a la lista de inventario
        inventoryList.addAll(productosCargados);
        System.out.println("Productos agregados a inventoryList.");

        // Depuración: verifica el contenido final de inventoryList
        System.out.println("=== Contenido final de inventoryList ===");
        for (Product product : inventoryList) {
            System.out.println("Producto: " + product.getNombreProducto() + ", Cantidad: " + product.getCantidad()
                    + ", Medida: " + product.getTipo() + ", Categoría: " + product.getCategoria());
        }

        System.out.println("=== Carga de inventario completada ===");
    }

    /**
     * Inicializa los componentes de la vista al cargar. Configura las columnas
     * de la tabla y carga los datos del inventario y la lista de compras según
     * el grupo del usuario conectado.
     */
    public void initialize() {

        //Si el usuario tiene rol consultor, desactivamos botones
        if (role == 2) {

            btnOpenAddNewProduct.setDisable(true);
            btnCompletePurchase.setDisable(true);
            btnSendPurchase.setDisable(true);

        }

/////////////////////////////////IDIOMAS/////////////////////////////////////////////
        // Registra este controlador como listener del LanguageManager
        LanguageManager.getInstance().addListener(() -> Platform.runLater(this::updateTexts));
        updateTexts(); // Actualiza los textos inicialmente

/////////////////////////////////FIN IDIOMAS///////////////////////////////////////////// 
        // Configura las columnas principales de inventario
        colProductName.setCellValueFactory(new PropertyValueFactory<>("nombreProducto"));
        colCategory.setCellValueFactory(new PropertyValueFactory<>("categoria"));
        colType.setCellValueFactory(new PropertyValueFactory<>("tipo")); // Nueva columna para "Cantidad" o "Gramos"

        // Configura los controles dinámicos para las columnas de cantidad y cantidad mínima
        configureColumnWithControls(colQuantity, "cantidad");
        configureColumnWithControls(colMinQuantity, "cantidadMinima");

        // Configura las columnas principales de lista de compras
        colShoppingProduct.setCellValueFactory(new PropertyValueFactory<>("nombreProducto"));
        colShoppingQuantity.setCellValueFactory(new PropertyValueFactory<>("cantidad")); // Cantidad necesaria
        colShoppingType.setCellValueFactory(new PropertyValueFactory<>("tipo"));

        // Configura la columna de cantidades como editable
        colShoppingQuantity.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        colShoppingQuantity.setOnEditCommit(event -> {
            Product item = event.getRowValue();
            item.setCantidad(event.getNewValue()); // Actualizar el modelo con la nueva cantidad
        });

        // Hace la tabla editable
        tableViewShoppingList.setEditable(true);

        // Inicializa las listas observables para inventario y lista de compras
        inventoryList = FXCollections.observableArrayList();
        shoppingList = FXCollections.observableArrayList();

        // Enlaza las listas observables a las tablas
        tableViewInventory.setItems(inventoryList);
        tableViewShoppingList.setItems(shoppingList);

        // Obtiene el grupo del usuario conectado desde CurrentSession
        int userGroupId = CurrentSession.getInstance().getUserGroupId();

        // Carga los datos de inventario desde la base de datos
        loadInventory(userGroupId);

        // Carga la lista de compras inicial basada en el inventario
        for (Product product : inventoryList) {
            updateShoppingList(product); // Evalúa cada producto y lo añade a la lista de la compra si es necesario
        }

        // Listener para manejar clics en las filas de la tabla de inventario
        tableViewInventory.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2 && tableViewInventory.getSelectionModel().getSelectedItem() != null) {
                Product selectedProduct = tableViewInventory.getSelectionModel().getSelectedItem();
                openUpdateProductView(selectedProduct); // Abre la vista de edición
            }
        });
    }

/////////////////////////////////IDIOMAS/////////////////////////////////////////////
    /**
     * Actualiza los textos de la interfaz en función del idioma.
     */
    private void updateTexts() {

        // Acceder al Singleton del LanguageManager
        LanguageManager languageManager = LanguageManager.getInstance();

        if (languageManager == null) {

            System.err.println("Error: LanguageManager no está disponible.");
            return;
        }

        // Traducción de los textos de los botones
        btnOpenAddNewProduct.setText(languageManager.getTranslation("addNewProduct"));
        btnCompletePurchase.setText(languageManager.getTranslation("completePurchase"));
        btnSendPurchase.setText(languageManager.getTranslation("sendPurchase"));
        instructionProduct.setText(languageManager.getTranslation("instructionProduct"));

        purchasesTitle.setText(languageManager.getTranslation("purchasesTitle"));

        // Traducción de encabezados de las columnas en la tabla de inventario
        colProductName.setText(languageManager.getTranslation("productName"));
        colQuantity.setText(languageManager.getTranslation("quantity"));
        colMinQuantity.setText(languageManager.getTranslation("minQuantity"));
        colCategory.setText(languageManager.getTranslation("category"));
        colType.setText(languageManager.getTranslation("type"));

        // Traducción de encabezados de las columnas en la tabla de la lista de compras
        colShoppingProduct.setText(languageManager.getTranslation("shoppingProduct"));
        colShoppingQuantity.setText(languageManager.getTranslation("shoppingQuantity"));
        colShoppingType.setText(languageManager.getTranslation("shoppingType"));

        // Depuración: Confirmar idioma actual
        System.out.println("Traducciones aplicadas exitosamente en idioma: " + languageManager.getTranslation("currentLanguage"));
    }

/////////////////////////////////FIN IDIOMAS/////////////////////////////////////////////    
    /**
     * Abre la vista para actualizar o eliminar un producto seleccionado.
     *
     * @param product El producto seleccionado en la tabla.
     */
    private void openUpdateProductView(Product product) {

        //No permite el doble click para acceder a update si consultor
        if (role == 2) {

            //System.out.println("Acceso denegado: los usuarios con rol 2 no pueden actualizar productos.");
            return; // Sale del método sin abrir la vista
        }

        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/homeSweetHome/view/purchase/UpdateProductView.fxml"));
            Parent root = loader.load();

            // Obtiene el controlador de la vista
            UpdateProductViewController updateController = loader.getController();
            updateController.setProduct(product); // Pasa el producto seleccionado
            updateController.setPurchaseViewController(this); // Referencia al controlador principal para actualizaciones

            // Crea la escena y muestra
            Stage stage = new Stage();
            stage.setTitle("Actualizar Producto");
            stage.setResizable(false);
            stage.getIcons().add(new Image(getClass().getResourceAsStream("/images/shoppingIconBlue.png")));
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL); // Ventana modal
            stage.showAndWait(); // Espera hasta que se cierre la ventana

        } catch (IOException e) {

            e.printStackTrace();
            System.err.println("Error al abrir la vista UpdateProductView.");
        }
    }

    /**
     * Configura una columna para que incluya controles (+/-) y permita al
     * usuario ajustar cantidades directamente desde la tabla.
     *
     * @param column La columna a configurar.
     * @param propertyName El nombre de la propiedad del modelo que se ajustará
     * (por ejemplo, "cantidad" o "cantidadMinima").
     */
    private void configureColumnWithControls(TableColumn<Product, Void> column, String propertyName) {

        Callback<TableColumn<Product, Void>, TableCell<Product, Void>> cellFactory = new Callback<>() {

            @Override
            public TableCell<Product, Void> call(final TableColumn<Product, Void> param) {

                return new TableCell<>() {

                    private final Button btnIncrease = new Button("+");
                    private final Button btnDecrease = new Button("-");
                    private final Label lblValue = new Label(); // Muestra el valor actual

                    {

                        //Si el usuario tiene rol consultor, desactivamos botones
                        if (role == 2) {

                            btnIncrease.setDisable(true);
                            btnDecrease.setDisable(true);

                        }

                        // Configura el estilo visual de los botones
                        btnIncrease.getStyleClass().add("btn-increase");
                        btnDecrease.getStyleClass().add("btn-decrease");

                        // Acción para aumentar la cantidad
                        btnIncrease.setOnAction(event -> {

                            Product product = getTableView().getItems().get(getIndex());
                            if ("cantidad".equals(propertyName)) {

                                product.setCantidad(product.getCantidad() + getIncrement(product.getTipo()));
                                lblValue.setText(formatValue(product.getCantidad())); // Actualiza la vista

                            } else if ("cantidadMinima".equals(propertyName)) {

                                product.setCantidadMinima(product.getCantidadMinima() + getIncrement(product.getTipo()));
                                lblValue.setText(formatValue(product.getCantidadMinima())); // Actualiza la vista
                            }

                            updateInventoryProduct(product); // Actualiza la base de datos
                            updateShoppingList(product); // Sincroniza con la lista de compras
                            tableViewInventory.refresh(); // Refresca la tabla
                            tableViewShoppingList.refresh(); // Refresca la lista de compras

                        });

                        // Acción para disminuir la cantidad
                        btnDecrease.setOnAction(event -> {

                            Product product = getTableView().getItems().get(getIndex());

                            if ("cantidad".equals(propertyName)) {

                                product.setCantidad(Math.max(0, product.getCantidad() - getIncrement(product.getTipo())));
                                lblValue.setText(formatValue(product.getCantidad())); // Actualiza la vista
                            } else if ("cantidadMinima".equals(propertyName)) {

                                product.setCantidadMinima(Math.max(0, product.getCantidadMinima() - getIncrement(product.getTipo())));
                                lblValue.setText(formatValue(product.getCantidadMinima())); // Actualiza la vista
                            }

                            updateInventoryProduct(product); // Actualiza la base de datos
                            updateShoppingList(product); // Sincroniza con la lista de compras
                            tableViewInventory.refresh(); // Refresca la tabla
                            tableViewShoppingList.refresh(); // Refresca la lista de compras

                        });
                    }

                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);

                        if (empty) {
                            setGraphic(null);
                        } else {
                            Product product = getTableView().getItems().get(getIndex());

                            if ("cantidad".equals(propertyName)) {
                                lblValue.setText(formatValue(product.getCantidad()));
                            } else if ("cantidadMinima".equals(propertyName)) {
                                lblValue.setText(formatValue(product.getCantidadMinima()));
                            }

                            lblValue.setPrefWidth(50); // Define un ancho fijo para evitar que se mueva

                            HBox buttonsBox = new HBox(5, btnDecrease, btnIncrease);
                            buttonsBox.setAlignment(Pos.CENTER_RIGHT);

                            HBox container = new HBox(15, lblValue, buttonsBox);
                            container.setAlignment(Pos.CENTER); // Asegura mejor distribución

                            setGraphic(container);
                        }
                    }

                    // Método auxiliar para calcular el incremento basado en el tipo
                    private int getIncrement(String tipo) {

                        return "Gramos".equals(tipo) ? 50 : 1; // Incremento de 50 para "Gramos", 1 para "Cantidad".
                    }

                    // Método auxiliar para formatear valores sin ningún sufijo
                    private String formatValue(int cantidad) {

                        return String.valueOf(cantidad); // Devuelve el número como texto sin formato adicional.
                    }
                };
            }
        };

        column.setCellFactory(cellFactory);
    }

    /**
     * Formatea el valor mostrado según el tipo (Cantidad o Gramos).
     *
     * @param value El valor a formatear.
     * @param tipo El tipo de medida ("Cantidad" o "Gramos").
     * @return El valor formateado como cadena.
     */
    private String formatValue(int value, String tipo) {
        return "Cantidad".equals(tipo) ? String.valueOf(value) : value + " g";
    }

    /**
     * Actualiza un producto en la base de datos.
     *
     * @param product El objeto del producto a actualizar.
     */
    public void updateInventoryProduct(Product product) {

        InventoryDAO inventoryDAO = new InventoryDAO();
        boolean updated = inventoryDAO.updateInventoryProduct(product);

        if (!updated) {

            System.err.println("Error al actualizar el producto en la base de datos. Producto: "
                    + product.getNombreProducto() + ", Tipo: " + product.getTipo());

        } else {

            System.out.println("Producto actualizado correctamente: "
                    + product.getNombreProducto() + ", Tipo: " + product.getTipo());
        }
    }

    /**
     * Actualiza la lista de la compra si la cantidad en inventario es menor que
     * la cantidad mínima. Elimina el producto si ya no cumple la condición.
     *
     * @param product El objeto del inventario que se evaluará.
     */
    public void updateShoppingList(Product product) {

        // Verifica si la cantidad actual es menor que la cantidad mínima
        if (product.getCantidad() < product.getCantidadMinima()) {

            // Calcula la cantidad necesaria para alcanzar la cantidad mínima
            int cantidadNecesaria = product.getCantidadMinima() - product.getCantidad();

            // Comprueba si el producto ya existe en la lista de compras
            boolean exists = shoppingList.stream().anyMatch(p -> p.getId() == product.getId());

            if (!exists) {

                // Si no existe, crea un nuevo producto para la lista de compras
                Product shoppingProduct = new Product(
                        product.getId(), // Usa el ID del producto existente
                        product.getNombreProducto(),
                        cantidadNecesaria, // Cantidad necesaria calculada
                        0, // Cantidad mínima no relevante en lista de compras
                        0, // Cantidad máxima no relevante en lista de compras
                        product.getTipo(),
                        product.getCategoria(),
                        product.getIdGrupo(),
                        LocalDate.now().toString() // Fecha actual
                );

                shoppingList.add(shoppingProduct);
                System.out.println("Producto añadido a la lista de compras: " + shoppingProduct.getNombreProducto()
                        + ", cantidad necesaria: " + cantidadNecesaria + ", tipo: " + shoppingProduct.getTipo());

            } else {

                // Si ya existe, actualiza la cantidad necesaria
                shoppingList.stream()
                        .filter(p -> p.getId() == product.getId())
                        .forEach(p -> {
                            p.setCantidad(cantidadNecesaria);
                            p.setTipo(product.getTipo()); // Actualiza el tipo si cambia
                            System.out.println("Cantidad actualizada en la lista de compras: " + p.getNombreProducto()
                                    + ", cantidad necesaria: " + cantidadNecesaria + ", tipo: " + p.getTipo());
                        });
            }

        } else {

            // Si la cantidad actual es mayor o igual que la mínima, elimina el producto de la lista de compras
            boolean removed = shoppingList.removeIf(p -> p.getId() == product.getId());

            if (removed) {

                System.out.println("Producto eliminado de la lista de compras: " + product.getNombreProducto());
            }
        }

        // Refresca la tabla de la lista de compras
        tableViewShoppingList.refresh();
    }

    /**
     * Abre la ventana para crear un nuevo producto
     *
     * @param event - Evento activado al presionar el botón "Nuevo producto".
     */
    @FXML
    private void openAddNewProduct(ActionEvent event) {

        try {

            // Carga la vista CreateEventView desde el archivo FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/homeSweetHome/view/purchase/CreateProductView.fxml"));
            Parent root = loader.load();

            // Obtiene el controlador de la nueva vista
            CreateProductViewController createProductController = loader.getController();

            // Pasa la referencia del controlador principal
            createProductController.setPurchaseViewController(this);

            // Configura una nueva ventana para la vista de creación
            Stage stage = new Stage();
            stage.setTitle("Añadir Nuevo Producto"); // Título de la ventana
            stage.setResizable(false);
            stage.getIcons().add(new Image(getClass().getResourceAsStream("/images/shoppingIconBlue.png")));
            stage.setScene(new Scene(root)); // Configura la escena
            stage.initModality(Modality.WINDOW_MODAL); // Establece la ventana como modal
            stage.initOwner(btnOpenAddNewProduct.getScene().getWindow()); // Asocia la ventana actual como propietaria
            stage.showAndWait(); // Muestra la ventana y espera a que se cierre

        } catch (IOException e) {

            // Registra un error en caso de problemas al cargar la vista
            System.err.println("Error al cargar la vista CreateProductView: " + e.getMessage());
        }
    }

    /**
     * Carga los productos de la lista de compras pertenecientes al grupo
     * especificado sin eliminar productos existentes que no estén en la
     * consulta.
     *
     * @param groupId El ID del grupo cuyos productos se cargarán.
     */
    public void loadShoppingList(int groupId) {

        try {

            PurchaseDAO purchaseDAO = new PurchaseDAO();

            // Recupera los datos actualizados desde el DAO
            List<Product> updatedProducts = purchaseDAO.getPurchasesByGroup(groupId);

            // Mantiene los productos existentes y añade solo los nuevos
            for (Product updatedProduct : updatedProducts) {

                // Verifica si el producto ya existe en la lista observable
                boolean exists = shoppingList.stream()
                        .anyMatch(product -> product.getId() == updatedProduct.getId());

                if (!exists) {

                    shoppingList.add(updatedProduct);
                }
            }

            // Verifica que los datos se hayan cargado correctamente
            for (Product product : shoppingList) {

                System.out.println("Lista actualizada: " + product.getNombreProducto()
                        + ", cantidad necesaria: " + product.getCantidad()
                        + ", tipo: " + product.getTipo());
            }

            // Refresca la tabla de la lista de compras para reflejar los cambios
            tableViewShoppingList.refresh();

        } catch (Exception e) {

            e.printStackTrace();
            AlertUtils.showAlert(Alert.AlertType.ERROR, "Error",
                    "Hubo un problema al cargar la lista de compras.");
        }
    }

    /**
     * Método principal para completar una compra. Incluye validación de
     * cantidades, actualización de inventario, manejo de productos fuera de la
     * lista, registro de gastos y limpieza de la lista de compras.
     *
     * @param event Evento activado al presionar el botón "Completar Compra".
     */
    @FXML
    private void completePurchase(ActionEvent event) {

        System.out.println("Inicio de completePurchase");

        // Obtiene el idioma actual
        String idiomaActivo = LanguageManager.getInstance().getLanguageCode();

        // Declaración de variables antes del `try`
        String tituloModificacion, mensajeModificacion;
        String tituloProductosFuera, mensajeProductosFuera;
        String tituloGasto, mensajeGasto;
        String tituloExito, headerExito, contenidoExito;
        String tituloCancelado, mensajeCancelado;
        String tituloError = "Error";
        String mensajeError;

        boolean compraCompletada = false; // Nueva variable para controlar si la compra fue realmente completada 

        if (idiomaActivo.equals("en")) {

            tituloModificacion = "Modify Quantities";
            mensajeModificacion = "Have you modified the quantities of the products in the list?";

            tituloProductosFuera = "Products Outside the List";
            mensajeProductosFuera = "Did you buy products that were not on the list?";

            tituloGasto = "Register Expense";
            mensajeGasto = "Do you want to continue with expense registration?";

            tituloExito = "Purchase Processed";
            headerExito = "Purchase completed successfully!";
            contenidoExito = "The products have been processed correctly, and the expense has been successfully registered in the system.";

            tituloCancelado = "Operation Cancelled";
            mensajeCancelado = "The purchase process was cancelled by the user. No changes have been made.";

            mensajeError = "There was a problem processing the purchase.";

        } else {

            tituloModificacion = "Modificar Cantidades";
            mensajeModificacion = "¿Has modificado las cantidades de los productos en la lista?";

            tituloProductosFuera = "Productos Fuera de la Lista";
            mensajeProductosFuera = "¿Has comprado productos que no estaban en la lista?";

            tituloGasto = "Registrar Gasto";
            mensajeGasto = "¿Desea continuar con el registro del gasto?";

            tituloExito = "Compra Procesada";
            headerExito = "¡Compra completada con éxito!";
            contenidoExito = "Los productos han sido procesados correctamente y el gasto ha sido registrado con éxito en el sistema.";

            tituloCancelado = "Operación Cancelada";
            mensajeCancelado = "El proceso de compra fue cancelado por el usuario. No se han realizado cambios.";

            mensajeError = "Hubo un problema al procesar la compra.";
        }

        try {

            // Valida modificación de cantidades
            Optional<ButtonType> resultCantidad = showConfirmationAlert(tituloModificacion, mensajeModificacion);

            if (resultCantidad.isPresent() && resultCantidad.get() == ButtonType.CANCEL) {

                System.out.println("El usuario canceló la compra en la etapa de modificación de cantidades.");
                AlertUtils.showAlert(Alert.AlertType.INFORMATION, tituloCancelado, mensajeCancelado);
                return; //  Sale completamente sin hacer cambios 
            }

            if (resultCantidad.isPresent() && resultCantidad.get() == ButtonType.OK) {

                System.out.println("El usuario indicó que ha modificado las cantidades. Salimos del proceso.");
                return; //  Sale del proceso y no sigue 
            }

            // Manejo de productos fuera de la lista
            boolean productosFuera = true;
            while (productosFuera) {
                Optional<ButtonType> resultProductos = showConfirmationAlert(tituloProductosFuera, mensajeProductosFuera);

                if (resultProductos.isPresent() && resultProductos.get() == ButtonType.CANCEL) {

                    System.out.println("El usuario canceló la compra en la etapa de productos fuera de la lista.");
                    AlertUtils.showAlert(Alert.AlertType.INFORMATION, tituloCancelado, mensajeCancelado);
                    return; //  Salie sin hacer cambios 
                }

                if (resultProductos.isPresent() && resultProductos.get() == ButtonType.OK) {

                    System.out.println("El usuario indicó que compró productos fuera de la lista.");
                    InventoryDAO inventoryDAO = new InventoryDAO();
                    manageProductsOutsideList(inventoryDAO);

                } else {

                    productosFuera = false; // ? Si el usuario dice que no, pasa a la siguiente 
                }
            }

            // Registra el gasto
            Optional<ButtonType> resultGasto = showConfirmationAlert(tituloGasto, mensajeGasto);

            if (resultGasto.isPresent() && resultGasto.get() == ButtonType.CANCEL) {

                System.out.println("El usuario canceló la compra en la etapa de registro del gasto.");
                AlertUtils.showAlert(Alert.AlertType.INFORMATION, tituloCancelado, mensajeCancelado);
                return; //  Sale sin hacer cambios 
            }

            //  Si el usuario cancela el diálogo de importe, detenemos el flujo
            boolean gastoRegistrado = registerExpense();

            if (!gastoRegistrado) {

                System.out.println("El usuario canceló la introducción del importe.");
                AlertUtils.showAlert(Alert.AlertType.INFORMATION, tituloCancelado, mensajeCancelado);
                return; // sale sin hacer cambios 
            }

            compraCompletada = true; //  Marcamos que la compra se completó correctamente 

            // Vacía la lista de compras solo si la compra se completó
            clearShoppingList();

            // Muestra mensaje final solo si la compra fue exitosa
            if (compraCompletada) {

                Alert alertSuccess = new Alert(Alert.AlertType.INFORMATION);
                alertSuccess.getDialogPane().getStylesheets().add(getClass().getResource("/homeSweetHome/utils/alertsCss.css").toExternalForm());
                alertSuccess.setTitle(tituloExito);
                alertSuccess.setHeaderText(headerExito);
                alertSuccess.setContentText(contenidoExito);
                alertSuccess.showAndWait();
                System.out.println("Proceso completo: Compra procesada y gasto registrado correctamente.");
            }

        } catch (Exception e) {

            e.printStackTrace();
            AlertUtils.showAlert(Alert.AlertType.ERROR, tituloError, mensajeError);
        }
    }

    /**
     * Muestra una alerta de confirmación genérica.
     */
    private Optional<ButtonType> showConfirmationAlert(String titulo, String mensaje) {

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.getDialogPane().getStylesheets().add(getClass().getResource("/homeSweetHome/utils/alertsCss.css").toExternalForm());
        alert.setTitle(titulo);
        alert.setHeaderText(mensaje);
        alert.getButtonTypes().setAll(ButtonType.OK, ButtonType.NO, ButtonType.CANCEL);
        return alert.showAndWait();
    }

    /**
     * Muestra una alerta de confirmación al usuario para decidir si desea
     * continuar o cancelar.
     *
     * @param title Título de la alerta.
     * @param message Mensaje explicativo de la alerta.
     * @return True si el usuario desea continuar, False si decide cancelar.
     */
    private boolean confirmAction(String title, String message) {

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.getDialogPane().getStylesheets().add(getClass().getResource("/homeSweetHome/utils/alertsCss.css").toExternalForm());
        alert.setTitle(title);
        alert.setHeaderText(message);

        ButtonType continueButton = new ButtonType("Continuar", ButtonBar.ButtonData.YES);
        ButtonType cancelButton = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(continueButton, cancelButton);

        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == continueButton;
    }

    /**
     * Valida si las cantidades de los productos en la lista han sido
     * modificadas. Si deben ser modificadas, muestra un mensaje informativo y
     * detiene el flujo.
     *
     * @return True si las cantidades no requieren modificación, False si deben
     * ser modificadas.
     */
    private boolean validateModifiedQuantities() {

        // Obtener el idioma actual
        String idiomaActivo = LanguageManager.getInstance().getLanguageCode();

        String tituloAlerta;
        String encabezadoAlerta;
        String contenidoAlerta;
        String textoSi;
        String textoNo;
        String tituloModificar;
        String mensajeModificar;

        // Definelos textos según el idioma seleccionado
        if (idiomaActivo.equals("en")) {

            tituloAlerta = "Complete Purchase";
            encabezadoAlerta = "Have you modified the product quantities in the list?";
            contenidoAlerta = "If not, the necessary amounts will be added automatically to the inventory.";
            textoSi = "Yes";
            textoNo = "No";
            tituloModificar = "Modify Quantities";
            mensajeModificar = "Please modify the quantities directly in the table, then press 'Complete Purchase' again.";

        } else {

            tituloAlerta = "Completar Compra";
            encabezadoAlerta = "¿Has modificado las cantidades de los productos en la lista?";
            contenidoAlerta = "Si no has modificado las cantidades, se sumarán las necesarias directamente al inventario.";
            textoSi = "Sí";
            textoNo = "No";
            tituloModificar = "Modificar Cantidades";
            mensajeModificar = "Por favor, modifica las cantidades directamente en la tabla y luego pulsa nuevamente 'Completar Compra'.";
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.getDialogPane().getStylesheets().add(getClass().getResource("/homeSweetHome/utils/alertsCss.css").toExternalForm());
        alert.setTitle(tituloAlerta);
        alert.setHeaderText(encabezadoAlerta);
        alert.setContentText(contenidoAlerta);

        ButtonType yesButton = new ButtonType(textoSi);
        ButtonType noButton = new ButtonType(textoNo);
        alert.getButtonTypes().setAll(yesButton, noButton);

        Optional<ButtonType> result = alert.showAndWait();
        System.out.println("Resultado de la alerta (modificar cantidades): " + result);

        if (result.isPresent() && result.get() == yesButton) {

            AlertUtils.showAlert(Alert.AlertType.INFORMATION, tituloModificar, mensajeModificar);
            return false; // Indica que el flujo debe detenerse

        } else if (!result.isPresent()) {

            System.err.println("No se seleccionó ninguna opción en la alerta de modificación de cantidades.");
            return false;
        }

        System.out.println("El usuario indicó que no modificó las cantidades.");
        return true;
    }

    /**
     * Actualiza las cantidades de los productos en el inventario basado en la
     * lista de compras.
     *
     * @param inventoryDAO DAO para interactuar con la base de datos del
     * inventario.
     * @throws SQLException Si ocurre un error al acceder o actualizar los datos
     * del inventario.
     */
    private void updateInventoryQuantities(InventoryDAO inventoryDAO) throws SQLException {

        for (Product product : shoppingList) {

            System.out.println("Procesando producto: " + product.getNombreProducto() + " (ID: " + product.getId() + ")");

            int currentQuantity = inventoryDAO.getCurrentQuantityById(product.getId());

            if (currentQuantity == -1) {

                throw new SQLException("No se pudo obtener la cantidad actual del producto: " + product.getNombreProducto());
            }

            int newQuantity = currentQuantity + product.getCantidad();
            boolean updated = inventoryDAO.updateInventoryQuantity(product.getId(), newQuantity);
            System.out.println("Nueva cantidad calculada: " + newQuantity);

            if (!updated) {

                AlertUtils.showAlert(Alert.AlertType.ERROR, "Error",
                        "No se pudo actualizar la cantidad del producto: " + product.getNombreProducto());
                return;
            }
        }
    }

    /**
     * Maneja los productos que no estaban en la lista de compras, mostrando un
     * popup para añadirlos al inventario.
     *
     * @param inventoryDAO DAO para interactuar con la base de datos del
     * inventario.
     * @throws IOException Si ocurre un problema al cargar la ventana del popup.
     * @throws SQLException Si ocurre un error al acceder o actualizar los datos
     * del inventario.
     */
    private void manageProductsOutsideList(InventoryDAO inventoryDAO) throws IOException, SQLException {

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.getDialogPane().getStylesheets().add(getClass().getResource("/homeSweetHome/utils/alertsCss.css").toExternalForm());
        alert.setTitle("Productos Fuera de la Lista");
        alert.setHeaderText("¿Has comprado productos que no estaban en la lista?");
        alert.setContentText("Si es así, podrás añadirlos directamente al inventario y a la lista de compras.");

        ButtonType yesButton = new ButtonType("Sí", ButtonBar.ButtonData.YES);
        ButtonType noButton = new ButtonType("No", ButtonBar.ButtonData.NO);
        alert.getButtonTypes().setAll(yesButton, noButton);

        Optional<ButtonType> result = alert.showAndWait();
        System.out.println("Resultado de la alerta (productos fuera de la lista): " + result);

        if (result.isPresent() && result.get() == yesButton) {

            System.out.println("El usuario indicó que compró productos fuera de la lista.");

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/homeSweetHome/view/purchase/CreateProductInShoppingListView.fxml"));
            Parent root = loader.load();

            CreateProductInShoppingListViewController createController = loader.getController();
            createController.setPurchaseViewController(this);

            Stage stage = new Stage();
            stage.setTitle("Añadir Producto al Inventario");
            stage.setResizable(false);
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            Product newProduct = createController.getCreatedProduct();

            if (newProduct != null) {

                boolean productExistsInInventory = inventoryDAO.isProductInInventory(newProduct.getNombreProducto(), newProduct.getIdGrupo());

                if (productExistsInInventory) {

                    int idInventario = inventoryDAO.getInventoryProductIdByName(newProduct.getNombreProducto(), newProduct.getIdGrupo());
                    int currentQuantity = inventoryDAO.getCurrentQuantityById(idInventario);

                    int updatedQuantity = currentQuantity + newProduct.getCantidad();
                    inventoryDAO.updateInventoryQuantity(idInventario, updatedQuantity);

                } else {

                    inventoryDAO.addInventoryProduct(newProduct);
                }

                loadInventory(CurrentSession.getInstance().getUserGroupId());
            }
        }
    }

    /**
     * Solicita el importe y método de pago para registrar el gasto en la base
     * de datos.
     *
     * @throws SQLException Si ocurre un error al registrar el gasto en la base
     * de datos.
     */
    private boolean registerExpense() throws SQLException {

        TextInputDialog inputDialog = new TextInputDialog();
        inputDialog.setTitle("Registrar Coste");
        inputDialog.setHeaderText("Introduce el importe de la compra:");
        inputDialog.setContentText("Importe:");

        Optional<String> inputResult = inputDialog.showAndWait();

        if (!inputResult.isPresent()) {

            return false; //  Si el usuario cancela, devolvemos `false` 
        }

        double importe = Double.parseDouble(inputResult.get());

        List<String> paymentMethods = Arrays.asList("Efectivo", "Tarjeta");
        ChoiceDialog<String> paymentDialog = new ChoiceDialog<>("Efectivo", paymentMethods);
        paymentDialog.setTitle("Registrar Método de Pago");
        paymentDialog.setHeaderText("Selecciona el método de pago:");
        paymentDialog.setContentText("Método de pago:");

        Optional<String> paymentResult = paymentDialog.showAndWait();

        if (!paymentResult.isPresent()) {

            return false; //  Si el usuario cancela, devolvemos `false` 
        }

        String metodoPago = paymentResult.get();
        BudgetDAO budgetDAO = new BudgetDAO();
        Budget newBudget = new Budget(0, "Compra", "Alimentación", importe, metodoPago, LocalDate.now(), "", CurrentSession.getInstance().getUserGroupId());
        budgetDAO.addBudget(newBudget);

        return true; //  Gasto registrado correctamente 
    }

    /**
     * Vacía la lista de compras tanto en la interfaz como en la base de datos.
     *
     * @throws SQLException Si ocurre un error al vaciar la lista de compras en
     * la base de datos.
     */
    private void clearShoppingList() throws SQLException {

        shoppingList.clear();
        tableViewShoppingList.refresh();
        System.out.println("Lista de compras vaciada correctamente en la interfaz.");

        PurchaseDAO purchaseDAO = new PurchaseDAO();
        int groupId = CurrentSession.getInstance().getUserGroupId();
        purchaseDAO.clearShoppingList(groupId);
    }

    public void repaintShoppingListTable() {

        // Asigna directamente los datos actuales a la tabla
        ObservableList<Product> updatedShoppingList = FXCollections.observableArrayList(shoppingList);
        tableViewShoppingList.setItems(updatedShoppingList);
        tableViewShoppingList.refresh(); // Refresca gráficamente la tabla
        System.out.println("Tabla de lista de compras actualizada.");
    }

    public ObservableList<Product> getShoppingList() {

        return shoppingList; // Devuelve la lista observable directamente
    }

    public ObservableList<Product> getInventoryList() {

        return FXCollections.unmodifiableObservableList(inventoryList); // Proporciona acceso seguro a la lista
    }

//    /**
//     * Metodo que envia por mail la lista de la compra
//     *
//     * @param event
//     */
//    @FXML
//    private void sendPurchase(ActionEvent event) {
//        
//        String filePath = saveShoppingListToFile(); //  Guarda la lista y obtiene la ruta
//        System.out.println("Ruta del archivo: " + filePath); //  Verifica ruta correcta
//
//        String recipientEmail = "algamento2@gmail.com";
//        sendEmailWithShoppingList(recipientEmail, filePath); //  Envía la ruta, NO el contenido
//    }
    
     /**
     * Metodo que envia por mail la lista de la compra
     *
     * @param event
     */
    @FXML
    private void sendPurchase(ActionEvent event) {
        
        String filePath = saveShoppingListToFile(); // Guarda la lista y obtiene la ruta
        System.out.println("Ruta del archivo: " + filePath); // Verifica ruta correcta

        // Obtiene el correo electrónico del usuario actual
        int userId = CurrentSession.getInstance().getUserId();
        UserDAO userDAO = new UserDAO();
        String recipientEmail = userDAO.getUserEmailById(userId);

        if (recipientEmail != null && !recipientEmail.isEmpty()) {
            
            sendEmailWithShoppingList(recipientEmail, filePath); // Envía la lista
            System.out.println("Email enviado a: " + recipientEmail);
            
        } else {
            
            System.err.println("No se pudo obtener el correo electrónico del usuario.");
        }
    }

    /**
     * Guarda la lista de la compra en un txt
     *
     * @return
     */
    private String saveShoppingListToFile() {
        String filePath = "src/shoppingLists/shopping_list.txt"; //  Ruta 

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {

            for (Product product : shoppingList) {

                writer.write(product.getNombreProducto() + "," + product.getCantidad() + "," + product.getTipo());
                writer.newLine();
            }

            System.out.println("Lista guardada en: " + filePath);
            return filePath; //  Devuelve solo la ruta

        } catch (IOException e) {

            e.printStackTrace();
            return ""; //  En caso de error, devuelve una cadena vacía
        }
    }

    /**
     * Configura el envio del mail
     *
     * @param recipientEmail
     * @param filePath
     */
    private void sendEmailWithShoppingList(String recipientEmail, String filePath) {

        final String senderEmail = "apphomesweethome@gmail.com"; // correo de la app -> en esta caso un gmail con autenticación y contraseña de aplicación
        final String senderPassword = "mskh lydf tzib vguo"; // Usa autenticación segura ->contraseña app terceros de google

        // Lee el contenido del archivo correctamente
        String fileContent = "";

        try {

            System.out.println("Ruta del archivo recibida: '" + filePath + "'");

            Path path = Paths.get(filePath); //  Usa la ruta sin modificar
            fileContent = Files.readString(path); //  Usa `Files.readString()` en lugar de `readAllBytes()`

            System.out.println("Contenido del archivo:\n" + fileContent); //  Verifica contenido 

        } catch (IOException e) {

            e.printStackTrace();
        }

        // Configuración del servidor SMTP
        Properties properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587");

        // Sesión con autenticación segura
        Session session = Session.getInstance(properties, new javax.mail.Authenticator() {

            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(senderEmail, senderPassword);
            }
        });

        try {

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(senderEmail));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
            message.setSubject("Lista de Compras");

            // Cuerpo del correo con el contenido del archivo
            message.setText("Aquí está tu lista de compras:\n\n" + fileContent);

            Transport.send(message);
            
            if (LanguageManager.getInstance().getLanguageCode().equals("es")) {

                     AlertUtils.showAlert(Alert.AlertType.INFORMATION, "Éxito", "Correo enviado correctamente.");

                } else if (LanguageManager.getInstance().getLanguageCode().equals("en")) {

                     AlertUtils.showAlert(Alert.AlertType.INFORMATION, "Success", "Email sent successfully.");
                }
            
            System.out.println("Correo enviado correctamente a: " + recipientEmail);
            

        } catch (Exception e) {

            e.printStackTrace();
            
            if (LanguageManager.getInstance().getLanguageCode().equals("es")) {

                    AlertUtils.showAlert(Alert.AlertType.ERROR, "Error", "No se pudo enviar el correo. Formato inválido");

                } else if (LanguageManager.getInstance().getLanguageCode().equals("en")) {

                    AlertUtils.showAlert(Alert.AlertType.ERROR, "Error", "The email could not be sent. Invalid format.");
                }
            
        }
    }

}
