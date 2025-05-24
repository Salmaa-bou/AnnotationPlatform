package org.example.boudaaproject.web;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.boudaaproject.dtos.DatasetWithTasksDTO;
import org.example.boudaaproject.entities.Dataset;
import org.example.boudaaproject.services.CsvService;
import org.example.boudaaproject.services.ICoupleTexteService;
import org.example.boudaaproject.services.IDatasetService;
import org.example.boudaaproject.services.TaskService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.nio.file.Path;
import java.nio.file.Files;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin/datasetsgestion")

@RequiredArgsConstructor
public class DatasetController {
    private final CsvService csvService;
    private final IDatasetService datasetService;
    private final ICoupleTexteService coupleTexteService;
    private final TaskService taskService;

    @GetMapping("/datasets")
    public String datasetsgestion(Model model) {
        model.addAttribute("datasets", datasetService.getAllDatasets());
        return "/admin/datasetsgestion/Dataset";
    }
    @GetMapping("/updateDataset")
    public String UpdateDataset(@RequestParam Long id ,Model model) {
        Optional<Dataset> dataset = datasetService.getDatasetById(id);
        if (dataset.isPresent()) {
            model.addAttribute("dataset", dataset.get());
            return "/admin/datasetsgestion/update-form";
        } else {
            model.addAttribute("error", "user not found");
            return "/admin/datasetsgestion/Dataset";

        }
    }
    @PostMapping("/updateDataset")
    public String updateDataset(@ModelAttribute Dataset dataset,
                                @RequestParam("file") MultipartFile file,
                                RedirectAttributes redirectAttributes) {
        try {
            datasetService.updateDataset(dataset, file);
            redirectAttributes.addFlashAttribute("success", "Dataset updated successfully.");
            return "redirect:/admin/datasetsgestion/datasets";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Update failed.");
            return "redirect:/admin/datasetsgestion/updateDataset?id=" + dataset.getId();
        }
    }

    @GetMapping("/newDataset")
    public String newDataset(Model model) {
        model.addAttribute("dataset", new Dataset());
        return "/admin/datasetsgestion/datasetForm";
    }
    @PostMapping("/save")
    public String saveDataset(@Valid @ModelAttribute("dataset") Dataset dataset,
                              @RequestParam("name") String name,
                              @RequestParam("description") String description,
                              @RequestParam("file") MultipartFile file,
                              RedirectAttributes redirectAttributes,
                              BindingResult bindingResult,
                              Model model) {


        if (bindingResult.hasErrors()) {
            // Si le formulaire n'est pas valide, retournez à la page avec des erreurs
            model.addAttribute("error", "Des erreurs ont été trouvées dans le formulaire.");
            return "/admin/datasetsgestion/datasetForm";
        }
        try {
            // Vérification d'un dataset existant
            if (datasetService.existsByName(name)) {
                model.addAttribute("error", "Un dataset avec ce nom existe déjà !");
                return "/admin/datasetsgestion/datasetForm";
            }

            // Importation et sauvegarde du dataset + couples
            csvService.importDatasetFromCsv(file, name, description);

            redirectAttributes.addFlashAttribute("success", "Dataset importé avec succès : " + name);
            return "redirect:/admin/datasetsgestion/datasets";

        } catch (Exception e) {
            model.addAttribute("error", "Erreur lors de l'import du dataset : " + e.getMessage());
            return "/admin/datasetsgestion/datasetForm";
        }
    }
    @GetMapping("/datasets/search")
    @ResponseBody
    public List<Dataset> searchDatasets(@RequestParam String keyword) {
        return datasetService.getDatasetsByByMotcle(keyword);
    }

    @GetMapping("/search")
    public String search( @RequestParam(name="keyword") String motcle, Model model) {
       List<Dataset> datasets;
        if (motcle != null && !motcle.isEmpty()) {
            datasets = datasetService.getDatasetsByByMotcle(motcle);
        } else {
          datasets = datasetService.getAllDatasets(); // Récupérer tous les annotateurs si aucune recherche
        }
        model.addAttribute("datasets", datasets);
   return "/admin/datasetsgestion/Dataset";
    }
    @PostMapping("/delete")
    public String deleteDataset(@RequestParam("id") Long datasetId, Model model) {
        datasetService.deleteDataset(datasetId);
        model.addAttribute("message", "Annotateur supprimé avec succès !");

        model.addAttribute("datasets", datasetService.getAllDatasets());
        return "/admin/datasetsgestion/Dataset";
    }
    @GetMapping("/DetailsDataset")
    public String showDetails(@RequestParam("id") Long id, Model model) {
    DatasetWithTasksDTO dto = datasetService.getDatasetWithTasks(id);;
        System.out.println("Tâches du dataset: " + dto.getTasks());
        model.addAttribute("dataset", dto.getDataset());
        model.addAttribute("tasks", dto.getTasks());
        return "admin/datasetsgestion/datasetDetails"; // ton fichier HTML
    }



}
