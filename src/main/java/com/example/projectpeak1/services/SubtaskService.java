package com.example.projectpeak1.services;


import com.example.projectpeak1.dto.DoneSubtaskDTO;
import com.example.projectpeak1.dto.DoneTaskDTO;
import com.example.projectpeak1.dto.TaskAndSubtaskDTO;
import com.example.projectpeak1.entities.Subtask;
import com.example.projectpeak1.entities.User;
import com.example.projectpeak1.repositories.IRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import javax.security.auth.login.LoginException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
public class SubtaskService {


    IRepository repository;

    public SubtaskService(ApplicationContext context, @Value("${projectPeak.repository.impl}") String impl) {
        repository = (IRepository) context.getBean(impl);
    }

    public void createTask(Subtask subtask, int taskId) {
        repository.createSubtask(subtask, taskId);
    }

    public Subtask getSubtaskById(int subtaskId) {
        return repository.getSubtaskById(subtaskId);
    }

    public void editSubtask(Subtask subtask) {
        repository.editSubtask(subtask);
    }

    public int getProjectIdBtSubtaskId(int subtaskId) {
        return repository.getProjectIdBySubtaskId(subtaskId);
    }

    public void deleteSubtask(int subtaskId) throws LoginException {
        repository.deleteSubtask(subtaskId);
    }


    public TaskAndSubtaskDTO getTaskAndSubTaskById(int taskId) {
        return repository.getTaskAndSubTask(taskId);
    }
    public User getUserFromId(int id){
        return repository.getUserFromId(id);
    }

    public int getTaskIdBySubtaskId(int subtaskId) throws LoginException {
        return repository.getTaskIdBySubtaskId(subtaskId);
    }
    public int getDaysToStartSubtask(int subTaskId) {
        LocalDate startDate = repository.getStartDateSubtask(subTaskId);
        LocalDate currentDate = LocalDate.now();
        return (int) ChronoUnit.DAYS.between(currentDate, startDate);
    }


    public int getDaysForSubtask(int subTaskId) {
        LocalDate startDate = repository.getStartDateSubtask(subTaskId);
        LocalDate endDate = repository.getEndDateSubtask(subTaskId);
        return (int) ChronoUnit.DAYS.between(startDate, endDate);
    }

    public int getDaysLeftSubtask(int subTaskId) {

        LocalDate endDate = repository.getEndDateSubtask(subTaskId);
        LocalDate startDate = repository.getStartDateSubtask(subTaskId);
        LocalDate currentDate = LocalDate.now();

        if(currentDate.isBefore(startDate)){
            return 0;
        }else {
            return (int) ChronoUnit.DAYS.between(currentDate, endDate);
        }

    }

    public void doneSubtask(int id){
        repository.doneSubtask(id);
    }

    public List<DoneSubtaskDTO> getAllDoneSubtask(int taskId) {
        List<DoneSubtaskDTO> doneSubtaskDTOS = new ArrayList<>();

        List<DoneSubtaskDTO> listFromDatabase = repository.getAllDoneSubtask(taskId);
        // Iterate over each done project and calculate the expected and used days
        for (DoneSubtaskDTO doneSubtaskDTO : listFromDatabase) {

            LocalDate subtaskStartDate = doneSubtaskDTO.getSubTaskStartDate();
            LocalDate subtaskEndDate = doneSubtaskDTO.getSubTaskEndDate();
            LocalDate subtaskCompletedDate = doneSubtaskDTO.getSubtaskCompletedDate();

            long calculateExpectedDays = ChronoUnit.DAYS.between(subtaskStartDate, subtaskEndDate);
            long calculateUsedDays = ChronoUnit.DAYS.between(subtaskStartDate, subtaskCompletedDate);

            doneSubtaskDTO.setSubtaskExpectedDays((int) calculateExpectedDays);
            doneSubtaskDTO.setSubtaskUsedDays((int) calculateUsedDays);

            doneSubtaskDTOS.add(doneSubtaskDTO);
        }

        return doneSubtaskDTOS;
    }
}
