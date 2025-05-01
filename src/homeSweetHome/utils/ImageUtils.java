/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package homeSweetHome.utils;

import java.awt.image.BufferedImage; // Para manejar imágenes en formato BufferedImage
import java.io.ByteArrayOutputStream; // Para convertir la imagen a un flujo de bytes
import javax.imageio.ImageIO; // Para escribir la imagen en formato de bytes (ej. PNG, JPG)
import javax.sql.rowset.serial.SerialBlob; // Para crear un objeto Blob a partir de los bytes
import java.sql.Blob; // Para representar datos binarios en la base de datos
import javafx.embed.swing.SwingFXUtils; // Para convertir una imagen JavaFX a BufferedImage
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * Metodo para convertir una imagen de JavaFX a un Blob
 *
 * @author Usuario
 */
public class ImageUtils {

    public static Blob convertImageToBlob(Image image) throws Exception {

        if (image == null) {

            throw new IllegalArgumentException("La imagen no puede ser nula.");
        }

        // Convertir la imagen en bytes
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        BufferedImage bufferedImage = SwingFXUtils.fromFXImage(image, null);
        ImageIO.write(bufferedImage, "png", outputStream);
        byte[] imageBytes = outputStream.toByteArray();
        outputStream.close();

        // Crear un Blob a partir de los bytes
        return new SerialBlob(imageBytes);
    }

    /**
     * Metodo para darle forma circular a las imagenes
     *
     * @param imageView - ImageView
     */
    public static void setClipToCircle(ImageView imageView) {
    // Ajusta el tamaño del ImageView a un tamaño uniforme
    imageView.setFitWidth(100);
    imageView.setFitHeight(100);
    imageView.setPreserveRatio(false); // Desactiva la relación de aspecto para llenar completamente el espacio

    // Espera a que se calcule el tamaño real del ImageView
    imageView.layoutBoundsProperty().addListener((obs, oldBounds, newBounds) -> {
        double centerX = newBounds.getWidth() / 2; // Calcular el centro X
        double centerY = newBounds.getHeight() / 2; // Calcular el centro Y

        // Crea y centra el círculo de recorte
        javafx.scene.shape.Circle clip = new javafx.scene.shape.Circle();
        clip.setRadius(Math.min(newBounds.getWidth(), newBounds.getHeight()) / 2);
        clip.setCenterX(centerX);
        clip.setCenterY(centerY);

        // Aplica el clip al ImageView
        imageView.setClip(clip);
    });
}

}
