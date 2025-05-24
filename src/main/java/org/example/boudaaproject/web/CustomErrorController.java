package org.example.boudaaproject.web;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;

@Slf4j
@Controller
public class CustomErrorController implements ErrorController {

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        Object exception = request.getAttribute(RequestDispatcher.ERROR_EXCEPTION);
        String errorMessage = "Erreur inconnue";

        if (status != null) {
            Integer statusCode = Integer.valueOf(status.toString());
            errorMessage = "Erreur HTTP " + statusCode;
            if (statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value() && exception instanceof Throwable) {
                log.error("Internal server error", (Throwable) exception);
                errorMessage = "Erreur serveur: " + ((Throwable) exception).getMessage();
            }
        }

        model.addAttribute("errorMessage", errorMessage);
        return "error"; // Assumes error.html template
    }
}