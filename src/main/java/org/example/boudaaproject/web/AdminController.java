package org.example.boudaaproject.web;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.example.boudaaproject.entities.User;
import lombok.RequiredArgsConstructor;
import org.example.boudaaproject.entities.UserState;
import org.example.boudaaproject.services.*;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {
    private final AdminService adminService;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final RoleService roleService;
    private final UserStateService userStateService;


    /// //alllalla fash tssali tasks zidi methode see details bash naffishew tasks dyal wahd dataset


    @GetMapping("/**")
    public String noaccess() {
        return "/admin/accessdenied";
    }
    //dakshi dyal CRUD ANNOTATOR
    @GetMapping("/gestion-annotators")
    public String gestionAnnotators(Model model) {

        List<User> annotators = adminService.getAllAnnotators();

        model.addAttribute("annotators", annotators);
        model.addAttribute("allStates", userStateService.findAllStates()); // Pass all possible user states
        model.addAttribute("roles", roleService.getRoles());
        return "/admin/gestion-annotators";
    }

    @PostMapping("/delete")
    public String deleteUser(@RequestParam Long id, Model model) {
        adminService.deleteUser(id);
        model.addAttribute("message", "Annotateur supprimé avec succès !");
        List<User> annotators = adminService.getAllAnnotators();
        model.addAttribute("annotators", annotators);
        model.addAttribute("userStates", userStateService.findAllStates()); // Pass all possible user states
        model.addAttribute("roles", roleService.getRoles());
        return "/admin/gestion-annotators";
    }
    @GetMapping("/search-annotator")
    public String searchAnnotator(Model model, @RequestParam(value = "search", required = false) String search) {
        List<User> annotators;

        if (search != null && !search.isEmpty()) {
            annotators = adminService.chercherAnnotator(search);
        } else {
            annotators = adminService.getAllAnnotators(); // Récupérer tous les annotateurs si aucune recherche
        }

        model.addAttribute("annotators", annotators);
        return "/admin/gestion-annotators";
    }
    @GetMapping("/addForm")
   public String addForm(Model model) {
        //On crée un nouvel objet User vide (pour la création d’un nouvel annotateur).
        //
        //Il est envoyé à la vue sous le nom user, ce qui permet de lier les
        // champs du formulaire avec th:field="*{...}".
        model.addAttribute("user", new User());
        return "/admin/add-form";
    }
    @PostMapping("/addAnnotator")
    //@valid va verfier si les champs de form respecte ce qu om met lorsqu on a definie entity
    //bindingresult quand on finie la verifiction et la validation les erreurs ou bien les messages d errures vont etre stockes ici
   //Utilise RedirectAttributes pour gérer les messages après une
    // redirection, et Model pour lier des données à la vue sans redirection.

    public String addAnnotator(@ModelAttribute("user") @Valid User user,
                               BindingResult result,
                               RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            //si il ya des errors de saisie roujou3
            return "/admin/add-form";
        }
        //sinon on enregistre l objet
        adminService.createAnnotator(user);
        redirectAttributes.addFlashAttribute("message", "Annotateur ajouté avec succès !");
        return "redirect:/admin/gestion-annotators";
    }
@GetMapping("/updateForm")
    public String updateForm(@RequestParam Long id, Model model) {
      Optional<User> user = adminService.getAnnotator(id);
     if (user.isPresent()) {
         model.addAttribute("user", user.get());
         return "/admin/update-form";
     }else{
         model.addAttribute("error", "user not found");
         return "redirect:/admin/home";

     }


}
    @PostMapping("/updateUser")
    public String updateUser(@ModelAttribute("user") @Valid User user,
                             BindingResult result,
                             RedirectAttributes redirectAttributes
                             ) {



            // Delegate the user update to the service layer
            adminService.updateAnnotator(user);

            // After successful update, add a success message and redirect
            redirectAttributes.addFlashAttribute("message", "Annotateur mis à jour avec succès!");


        return "redirect:/admin/gestion-annotators"; // Redirect to annotators list after success
    }


    @PostMapping("/promotedToAdmin")
    public String promoteToAdmin(@RequestParam("id") Long userId, Model model) {
        try {
            adminService.promoteToAdmin(userId);
            List<User> annotators = adminService.getAllAnnotators();
            model.addAttribute("annotators", annotators);
            model.addAttribute("allStates", userStateService.findAllStates()); // Pass all possible user states
            model.addAttribute("roles", roleService.getRoles());
            model.addAttribute("successMessage", "L'utilisateur a été promu au rôle d'administrateur avec succès.");
        } catch (RuntimeException e) {
           model.addAttribute("errorMessage", "Erreur : " + e.getMessage());
        }

        return "/admin/gestion-annotators"; // Redirection vers la page des annotateurs
    }
    //c est comme un soft delete car apres assignments subtasks les annotations c est un probleme
    // de supprimer un annotator donc on fait ce soft delete c ad desable the state user et aussi on assurant qu un user n apas comme
    //etat enabled va rien recoie comme tasks etc et aussi authentification..

    @PostMapping("/update-user-state")
    public String updateUserState(@RequestParam("id") Long id,
                                  @RequestParam("state") String state,
                                  RedirectAttributes redirectAttributes) {
        if (state == null || state.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "État invalide.");
            return "redirect:/admin/gestion-annotators";
        }

        try {
            UserState newState = UserState.valueOf(state.toUpperCase());
            adminService.updateUserState(id, newState);
            redirectAttributes.addFlashAttribute("successMessage", "Statut mis à jour avec succès.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "État invalide.");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erreur : " + e.getMessage());
        }

        return "redirect:/admin/gestion-annotators";
    }




}
