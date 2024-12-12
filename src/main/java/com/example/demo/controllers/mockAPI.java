package com.example.demo.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import jakarta.mail.*;
import jakarta.mail.internet.*;
import java.util.*;
import java.io.UnsupportedEncodingException;
import com.example.demo.DTOs.RecomendacionDTO;

@RestController
@RequestMapping("/mockAPI")
public class mockAPI {

    private static final double RADIO_TIERRA = 6371000; // radio de la tierra en metros

    @GetMapping("/recomendarPuntos")
    public List<RecomendacionDTO> getRecomendaciones(
            @RequestParam double latitud,
            @RequestParam double longitud,
            @RequestParam double radio) {

        System.out.println(latitud);
        System.out.println(longitud);
        System.out.println(radio);
        List<RecomendacionDTO> listaRecomendaciones = new ArrayList<>();
        Random random = new Random();

        for (int i = 0; i < 10; i++) {
            double theta = 2 * Math.PI * random.nextDouble();
            double r = radio * Math.sqrt(random.nextDouble());
            double dx = r * Math.cos(theta);
            double dy = r * Math.sin(theta);
            double newLatitud = latitud + (dy / RADIO_TIERRA) * (180 / Math.PI);
            double newLongitud = longitud + (dx / (RADIO_TIERRA * Math.cos(Math.toRadians(latitud)))) * (180 / Math.PI);
            String nombre = "Punto " + (i + 1);
            RecomendacionDTO recomendacion = new RecomendacionDTO(nombre, newLatitud, newLongitud);
            listaRecomendaciones.add(recomendacion);
        }

        return listaRecomendaciones;
    }

    @PostMapping("/enviar-correo")
    public static void sendEmail(@RequestParam("mensaje") String message, @RequestParam("correo") String correo) {
        String host = "smtp.gmail.com"; // Servidor SMTP de Gmail
        String subject = "Heladeras Comunitarias";
        // Obtener las credenciales del correo desde variables de entorno
        final String user ="hheladerasdds@gmail.com";
        final String password = "mhFWtC0X7UJ!0!oQ";

        // Verificar que las credenciales no estén vacías
        if (user == null || user.isEmpty() || password == null || password.isEmpty()) {
            System.err.println("Las credenciales no están configuradas correctamente en las variables de entorno.");
            return;
        }

        // Configurar propiedades del servidor SMTP
        Properties props = new Properties();
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        // Crear sesión autenticada
        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(user, password);
            }
        });

        try {
            // Configurar el mensaje
            Message msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(user, "Admin Notificaciones")); // Remitente
            msg.addRecipient(Message.RecipientType.TO, new InternetAddress(correo)); // Destinatario
            msg.setSubject(subject);

            // Establecer el contenido del mensaje
            msg.setText(message);

            // Enviar el correo
            Transport.send(msg);
            System.out.println("Correo enviado con éxito a " + correo);

        } catch (MessagingException | UnsupportedEncodingException e) {
            System.err.println("Error al enviar correo: " + e.getMessage());
            e.printStackTrace();
        }
    }
}