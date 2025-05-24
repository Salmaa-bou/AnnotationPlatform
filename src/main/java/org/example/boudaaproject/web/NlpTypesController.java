package org.example.boudaaproject.web;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.example.boudaaproject.entities.Category;
import org.example.boudaaproject.entities.TachesDeNLP;
import org.example.boudaaproject.entities.Task;
import org.example.boudaaproject.services.NlpTaskTypeServiceI;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/gestionTypes")
public class NlpTypesController {
    private final NlpTaskTypeServiceI nlpTaskTypeService;

    @GetMapping("/tachesdenlp")
    public String gotoNlpTypeTasks(Model model) {
        List<TachesDeNLP> tasks = nlpTaskTypeService.getTachesDeNLP();
        model.addAttribute("tasks", tasks);
        return "admin/gestionTypes/nlpTypes";
    }

    @GetMapping("/createNewOne")
    public String createNewOne(Model model) {
        TachesDeNLP task = new TachesDeNLP();
        task.setCategories(List.of(new Category()));
        // start with one empty category
        model.addAttribute("tache", task);
        return "admin/gestionTypes/createNewOne";


    }

    @PostMapping("/submit")
    public String createTask(@ModelAttribute("tache") TachesDeNLP tache, Model model, RedirectAttributes redirectAttributes) {
        try {
            // Save task and categories logic
            nlpTaskTypeService.save(tache); // Assume this method exists
            redirectAttributes.addFlashAttribute("message", "Task created successfully!");
            return "redirect:/admin/gestionTypes/tachesdenlp";
        } catch (Exception e) {
            model.addAttribute("error", "Failed to create task: " + e.getMessage());
            return "/admin/gestionTypes/nlpTypes";
        }
    }
}
