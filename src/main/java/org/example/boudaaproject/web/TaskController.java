package org.example.boudaaproject.web;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.boudaaproject.dtos.CategoryDto;
import org.example.boudaaproject.dtos.DatasetDtoo;
import org.example.boudaaproject.dtos.TaskDto;
import org.example.boudaaproject.entities.Category;
import org.example.boudaaproject.entities.Task;
import org.example.boudaaproject.services.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/tasksgestion")
public class TaskController {
    private final ITaskService taskService;
    private final IDatasetService datasetService;
    private final NlpTaskTypeServiceI nlpTaskTypeService;
    private final ICategoryService categoryService;
    private final SubTaskAssignmentService subTaskAssignmentService;
    private final ITaskManagmentService taskManagmentService;
    private final TaskFactoryService taskFactoryService;
    @GetMapping("/gotoTasks")
    public String gotoTasks(Model model) {
        List<Task> tasks = taskService.getAllTasks();
        Map<Long, List<Category>> taskToCategoriesMap = new HashMap<>();
        for (Task task : tasks) {
            taskToCategoriesMap.put(task.getId(), categoryService.getCategoryByidNLPtache(task.getType()));
        }
        model.addAttribute("tasks", tasks);
        model.addAttribute("taskToCategoriesMap", taskToCategoriesMap);

        return "/admin/tasksgestion/task";
    }

    @GetMapping("/create-tasks")
    public String createTask(Model model) {
        model.addAttribute("datasets", datasetService.getAllDatasets());
        model.addAttribute("nlptaches", nlpTaskTypeService.getTachesDeNLP());
        model.addAttribute("task", new TaskDto());
        model.addAttribute("preloadedCategories", List.of());
        return "/admin/tasksgestion/create-task";
    }

    @GetMapping("/datasets/search")
    @ResponseBody
    public List<DatasetDtoo> searchDatasets(@RequestParam String keyword) {
        return datasetService.searchByName(keyword);
    }

    @GetMapping("/datasets/{id}/count")
    @ResponseBody
    public Long countCouples(@PathVariable Long id) {
        return datasetService.countDatasetsById(id);
    }

    @GetMapping("/nlptasks/{id}/categories")
    @ResponseBody
    public List<CategoryDto> getCategoriesByNlpTask(@PathVariable Long id) {
        return categoryService.getCategoryDtosByNlpTaskId(id);
    }

    @PostMapping("/create")
    public String createTaskPost(@ModelAttribute("task") TaskDto task, RedirectAttributes redirectAttributes) {
        try {
            taskManagmentService.createAndAssignTask(task);
            redirectAttributes.addFlashAttribute("message", "Task added successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error creating task: " + e.getMessage());
        }
        return "redirect:/admin/tasksgestion/gotoTasks";
    }

    @PostMapping("/delete")
    @Transactional
    public String deleteTask(@RequestParam("id") Long taskId, RedirectAttributes redirectAttributes) {
        try {
            taskService.deletetask(taskId);
            redirectAttributes.addFlashAttribute("message", "Task deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error deleting task: " + e.getMessage());
        }
        return "redirect:/admin/tasksgestion/gotoTasks";
    }

    @GetMapping("/search-task")
    public String searchTask(@RequestParam(name = "search", required = false) String keyword, Model model) {
        List<Task> tasks = (keyword != null && !keyword.isEmpty()) ?
                taskService.searchParMotcle(keyword) : taskService.getAllTasks();
        model.addAttribute("tasks", tasks);
        model.addAttribute("search", keyword);
        return "/admin/tasksgestion/task";
    }

    @GetMapping("/goTodetails")
    public String gotoTaskDetails(@RequestParam(name = "id") Long idtask, Model model) {
        Task task = taskService.getTaskWithDetails(idtask);
        model.addAttribute("task", task);
        return "/admin/tasksgestion/taskdetails";
    }

    @PostMapping("/assign-subtasks")
    public String assignSubtasks(@RequestParam("taskId") Long taskId, RedirectAttributes redirectAttributes) {
        try {
            subTaskAssignmentService.assignAnnotatorsToSubTasks(taskId);
            redirectAttributes.addFlashAttribute("message", "Annotateurs assignés avec succès !");
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("error", "Erreur : " + e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Une erreur inattendue est survenue: " + e.getMessage());
        }
        return "redirect:/admin/tasksgestion/gotoTasks";
    }

}