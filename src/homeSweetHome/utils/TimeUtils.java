
package homeSweetHome.utils;


import java.sql.Time;

/**
 *
 * @author Usuario
 */
public class TimeUtils {
    
    /**
     * Convierte las selecciones de hora y minuto en un objeto Time.
     *
     * @param hour - Hora seleccionada en formato HH.
     * @param minute - Minutos seleccionados en formato mm.
     * @return Time - Objeto Time correspondiente.
     * @throws IllegalArgumentException - Si el formato es inválido.
     */
    public static Time parseTime(String hour, String minute) throws IllegalArgumentException {
        if (hour == null || minute == null || !hour.matches("\\d{2}") || !minute.matches("\\d{2}")) {
            throw new IllegalArgumentException("Formato de hora o minutos inválido.");
        }
        String timeText = hour + ":" + minute + ":00"; // Formato HH:mm:ss
        return Time.valueOf(timeText); // Convierte el texto a Time
    }
    
    
}
