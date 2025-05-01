package homeSweetHome.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Utilización de patrón singleton para evitar tener que pasar 
 * el language manager a cada vista mediante los controladores, que era un lío
 * Toma el lenguaje del combo del MainView
 * @author Usuario
 */
public class LanguageManager {

    private static LanguageManager instance; // Instancia única

    private ResourceBundle bundle; // Bundle de recursos para las traducciones
    private List<Runnable> listeners = new ArrayList<>(); // Lista de listeners
    private String languageCode; // Código de idioma actual

    // Constructor privado para evitar instanciación directa
    private LanguageManager() {
    }

    // Método estático para obtener la instancia única
    public static LanguageManager getInstance() {
        if (instance == null) {
            instance = new LanguageManager();
        }
        return instance;
    }

    // Configura el idioma y actualiza el ResourceBundle
    @SuppressWarnings("deprecation")
    public void setLanguage(String languageCode) {
        this.languageCode = languageCode;
        Locale locale = new Locale(languageCode);
        bundle = ResourceBundle.getBundle("homeSweetHome.utils.messages", locale);

        // Notifica a todos los listeners sobre el cambio de idioma
        notifyListeners();
    }
    
    

    // Obtiene una traducción a partir de una clave
    public String getTranslation(String key) {
        if (bundle != null && bundle.containsKey(key)) {
            return bundle.getString(key); // Devuelve la traducción
        }
        return key; // Devuelve la clave si no se encuentra
    }

    // Añade un listener que será notificado al cambiar el idioma
    public void addListener(Runnable listener) {
        listeners.add(listener);
    }

    // Notifica a todos los listeners registrados
    private void notifyListeners() {
        for (Runnable listener : listeners) {
            listener.run();
        }
    }

    // Obtiene el idioma actual configurado
    public String getLanguageCode() {
        return languageCode;
    }
}
