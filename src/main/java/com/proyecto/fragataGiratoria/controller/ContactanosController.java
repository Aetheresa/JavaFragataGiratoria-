package com.proyecto.fragataGiratoria.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * Controlador para manejar el formulario de contacto.
 * Envía correos al administrador usando JavaMailSender.
 */
@Controller
public class ContactanosController {

    private final JavaMailSender mailSender;

    public ContactanosController(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    /**
     * Clase interna que representa y valida el formulario de contacto.
     */
    public static class ContactForm {

        @NotBlank(message = "El nombre es obligatorio.")
        @Size(max = 255, message = "El nombre no debe superar los 255 caracteres.")
        private String nombre;

        @NotBlank(message = "El correo electrónico es obligatorio.")
        @Email(message = "El formato del correo no es válido.")
        private String email;

        @NotBlank(message = "El asunto es obligatorio.")
        @Size(max = 255, message = "El asunto no debe superar los 255 caracteres.")
        private String asunto;

        @NotBlank(message = "El mensaje no puede estar vacío.")
        private String mensaje;

        // Getters y Setters
        public String getNombre() { return nombre; }
        public void setNombre(String nombre) { this.nombre = nombre; }

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }

        public String getAsunto() { return asunto; }
        public void setAsunto(String asunto) { this.asunto = asunto; }

        public String getMensaje() { return mensaje; }
        public void setMensaje(String mensaje) { this.mensaje = mensaje; }
    }

    /**
     * Muestra el formulario de contacto.
     */
    @GetMapping("/contactanos")
    public String mostrarFormulario(Model model) {
        model.addAttribute("ContactForm", new ContactForm());
        return "contactanos"; // templates/contactanos.html
    }

    /**
     * Procesa el envío del formulario de contacto y envía el correo.
     */
    @PostMapping("/contactanos/enviar")
    public String enviarMensaje(
            @Valid @ModelAttribute("ContactForm") ContactForm form,
            BindingResult result,
            Model model) {

        if (result.hasErrors()) {
            model.addAttribute("error", "Por favor corrige los errores del formulario.");
            return "contactanos";
        }

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo("arlcornd@gmail.com");
            message.setCc("nm891678@gmail.com");
            message.setSubject("Nuevo mensaje de contacto: " + form.getAsunto());
            message.setText(
                    "Has recibido un nuevo mensaje de contacto:\n\n" +
                    "Nombre: " + form.getNombre() + "\n" +
                    "Correo: " + form.getEmail() + "\n\n" +
                    "Mensaje:\n" + form.getMensaje()
            );
            message.setFrom("no-reply@fragatagiratoria.com");

            mailSender.send(message);

            model.addAttribute("success", "¡Tu mensaje ha sido enviado con éxito!");
            model.addAttribute("contactForm", new ContactForm());

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Hubo un problema al enviar tu mensaje. Inténtalo nuevamente.");
        }

        return "contactanos";
    }
}
