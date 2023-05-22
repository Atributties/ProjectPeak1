package com.example.projectpeak1.controller;


import com.example.projectpeak1.dto.DoneProjectDTO;
import com.example.projectpeak1.dto.TaskAndSubtaskDTO;
import com.example.projectpeak1.entities.Project;
import com.example.projectpeak1.entities.Subtask;
import com.example.projectpeak1.entities.User;
import com.example.projectpeak1.services.ProjectService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import javax.security.auth.login.LoginException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping({""})
public class ProjectController {

    ProjectService projectService;

    public ProjectController(ProjectService projectService){
        this.projectService = projectService;
    }

    private int getUserId(HttpSession session) {
        return (int) session.getAttribute("userId");
    }


    @GetMapping(value = {"/userFrontend"})
    public String index(HttpSession session, Model model) {
        int userId = getUserId(session);
        if (userId == 0) {
            return "login";
        }
        User user = projectService.getUserFromId(userId);
        model.addAttribute("user", user);

        List<Project> list = projectService.getAllProjectById(userId);

        for (Project project : list ) {
            int daysToStart = projectService.getDaysToStartProject(project.getProjectId());
            project.setDaysToStart(daysToStart);
            int daysForProject = projectService.getDaysForProject(project.getProjectId());
            project.setDaysForProject(daysForProject);
            int daysLeft = projectService.getDaysLeftProject(project.getProjectId());
            project.setDaysLeft(daysLeft);
        }
        model.addAttribute("projects", list);
        return "userFrontend";
    }

    @GetMapping("/showProject/{id}")
    public String showProjectDetails(HttpSession session, @PathVariable("id") int projectId, Model model) {
        int userId = getUserId(session);
        if (userId == 0) {
            return "login";
        }


        User user = projectService.getUserFromId(userId);
        model.addAttribute("user", user);

        Project project = projectService.getProjectById(projectId);
        model.addAttribute("project", project);

        List<TaskAndSubtaskDTO> listOfTaskAndSub = projectService.getTaskAndSubTask(projectId);
        for (TaskAndSubtaskDTO taskAndSubtaskDTO : listOfTaskAndSub) {
            int daysToStartTask = projectService.getDaysToStartTask(taskAndSubtaskDTO.getId());
            taskAndSubtaskDTO.setDaysToStart(daysToStartTask);
            int daysForTask = projectService.getDaysForTask(taskAndSubtaskDTO.getId());
            taskAndSubtaskDTO.setDaysTask(daysForTask);
            int daysLeft = projectService.getDaysLeftTask(taskAndSubtaskDTO.getId());
            taskAndSubtaskDTO.setDaysLeft(daysLeft);
        }

        model.addAttribute("listOfTaskAndSub", listOfTaskAndSub);

        return "project";
    }






    @GetMapping("/createProject")
    public String createProject(HttpSession session, Model model) {
        int userId = getUserId(session);
        if (userId == 0) {
            return "login";
        }
        User user = projectService.getUserFromId(userId);
        model.addAttribute("user", user);

        model.addAttribute("project", new Project());
        return "createProject";
    }


    @PostMapping(value = {"/createProject"})
    public String processCreateProject(HttpSession session, @ModelAttribute Project project) {
        int userId = getUserId(session);

        projectService.createProject(project, userId);
        return "redirect:/userFrontend";
    }

    @GetMapping(value = {"/deleteProject/{id}"})
    public String deleteProject(HttpSession session, @PathVariable("id") int id) throws LoginException {
        int userId = getUserId(session);
        if (userId == 0) {
            return "login";
        }
        projectService.deleteProject(id);
        return "redirect:/userFrontend";

        //TODO add access denied like the one from wishlist
    }



    @GetMapping("/editProject/{id}")
    public String editProject(HttpSession session, @PathVariable("id") int projectId, Model model) {
        int userId = getUserId(session);
        if (userId == 0) {
            return "login";
        }
        User user = projectService.getUserFromId(userId);
        model.addAttribute("user", user);

        Project project = projectService.getProjectById(projectId);
        model.addAttribute("project", project);

        return "editProject";
    }
    @PostMapping("/editProject")
    public String updateProject(@ModelAttribute Project project) {
        Project originalProject = projectService.getProjectById(project.getProjectId());

        // Check if the start date or end date has changed
        if (!originalProject.getProjectStartDate().equals(project.getProjectStartDate())
                || !originalProject.getProjectEndDate().equals(project.getProjectEndDate())) {

            // Update the project
            projectService.updateProject(project);

            // Update task and subtask dates
            projectService.updateTaskAndSubtaskDates(project, originalProject);
        } else {
            // Only update the project without updating task and subtask dates
            projectService.updateProject(project);
        }

        return "redirect:/userFrontend";
    }


    @GetMapping(value = {"/doneProject/{id}"})
    public String doneProject(HttpSession session, @PathVariable("id") int id, Model model) throws LoginException {
        int userId = getUserId(session);
        if (userId == 0) {
            return "login";
        }
        DoneProjectDTO doneProjectDTO = new DoneProjectDTO();
        model.addAttribute("doneProject", doneProjectDTO);

        return "redirect:/userFrontend";

    }

    @PostMapping(value = {"/doneProject/{id}"})
    public String sendDoneProject(HttpSession session, @ModelAttribute DoneProjectDTO doneProjectDTO) throws LoginException {
        int userId = getUserId(session);
        if (userId == 0) {
            return "login";
        }
        projectService.doneProject(doneProjectDTO);


        return "redirect:/userFrontend";

    }






}
