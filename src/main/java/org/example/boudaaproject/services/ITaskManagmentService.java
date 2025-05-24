package org.example.boudaaproject.services;

import org.example.boudaaproject.dtos.TaskDto;
import org.example.boudaaproject.entities.Task;

public interface ITaskManagmentService {
    Task createAndAssignTask(TaskDto taskDto);
}
