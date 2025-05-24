package org.example.boudaaproject.web;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.boudaaproject.entities.Role;
import org.example.boudaaproject.entities.User;
import org.example.boudaaproject.repositories.IRoleRepository;
import org.example.boudaaproject.repositories.UserRepository;
import org.example.boudaaproject.services.IUserService;
import org.example.boudaaproject.services.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller

public class AuthController {
    private IUserService userService;
    public AuthController(IUserService userService) {
        this.userService = userService;
    }
 @GetMapping("/login")
 public String showlogin() {
        return "login";
 }
 @GetMapping("/register")
 public String showregisterPage(Model model) {
        model.addAttribute("user", new User());
        return "registration";
 }
//
@PostMapping("/register")
public String processRegister(@ModelAttribute("user") @Valid User user,
                              BindingResult result,
                              Model model) {
    if (result.hasErrors()) {
        model.addAttribute("error", "Veuillez corriger les erreurs.");
        return "registration";
    }

    try {
        userService.registerUser(user);
        model.addAttribute("message", "Inscription réussie !");
        return "redirect:/login";
    } catch (Exception e) {
        model.addAttribute("error", "Erreur : " + e.getMessage());
        return "registration";
    }
}
    @GetMapping("/admin/home")
    public String adminHome() {
        return "admin/home";
    }

    @GetMapping("/annotator/home")
    public String annotatorHome() {
        return "annotator/home";
    }

    @GetMapping("/access-denied")
    public String accessDenied() {
        return "accessdenied";
    }

    @GetMapping("/")
    public String index() {
        return "index";
    }
    @GetMapping("/**")
    public String show() {
        return "help";

    }


}

