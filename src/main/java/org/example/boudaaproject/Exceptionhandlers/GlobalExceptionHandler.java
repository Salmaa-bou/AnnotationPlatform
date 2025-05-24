package org.example.boudaaproject.Exceptionhandlers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public String handleAllExceptions(Exception ex, Model model) {
        log.error("Unhandled exception occurred", ex);
        model.addAttribute("errorMessage", "Une erreur inattendue s'est produite: " + ex.getMessage());
        return "error"; // Assumes error.html template
    }
}