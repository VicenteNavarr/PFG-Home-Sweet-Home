/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package homeSweetHome.utils;

/**
 * Clase para validaciones
 * @author Usuario
 */
public class ValidationUtils {
    
    /**
     * Valida el formato del correo electrónico.
     *
     * @param email Correo electrónico a validar.
     * @return True si el correo es válido, false en caso contrario.
     */
    public static boolean isValidEmail(String email) {
        String emailRegex = "^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$"; // Regex básica para validar correos electrónicos
        return email.matches(emailRegex);
    }
    
    
    
}
