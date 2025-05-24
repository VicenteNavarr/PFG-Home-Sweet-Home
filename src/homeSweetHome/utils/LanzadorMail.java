package homeSweetHome.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;


public class LanzadorMail {

    // Método  para enviar listas de compras
    public void lanzarEnvioCorreo(String filePath, String recipientEmail) {
        ProcessBuilder processBuilder = new ProcessBuilder(
                "java", "-cp", System.getProperty("java.class.path"), "homeSweetHome.utils.MailSender", filePath, recipientEmail
        );

        ejecutarProceso(processBuilder);
    }

    // Método  para enviar pass
    public void lanzarEnvioCorreoPass(String recipientEmail, String userPassword) {
        ProcessBuilder processBuilder = new ProcessBuilder(
                "java", "-cp", System.getProperty("java.class.path"), "homeSweetHome.utils.PasswordRecoveryMailSender", recipientEmail, userPassword
        );

        ejecutarProceso(processBuilder);
    }

    // Método auxiliar para ejecutar el proceso
    private void ejecutarProceso(ProcessBuilder processBuilder) {
        processBuilder.directory(new File("."));

        try {
            Process process = processBuilder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }

            BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            String errorLine;
            while ((errorLine = errorReader.readLine()) != null) {
                System.err.println("ERROR: " + errorLine);
            }

            int exitCode = process.waitFor();
            System.out.println("El proceso terminó con código: " + exitCode);

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
