package homeSweetHome.utils;

import javafx.scene.control.Alert;

/**
 * Clase utilitaria para mostrar alertas.
 */
public class AlertUtils {

    /**
     * Método estático para mostrar alertas.
     *
     * @param alertType - Tipo de alerta (por ejemplo, INFORMACIÓN, ERROR, etc.).
     * @param title - Título de la alerta.
     * @param message - Mensaje que se mostrará en la alerta.
     */
    public static void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

