package org.example.boudaaproject.services;

import org.example.boudaaproject.dtos.TaskDto;
import org.example.boudaaproject.entities.Task;

public interface TaskFactoryService {
    Task createTask(TaskDto dto);

//    Task createFullTask(TaskDto taskDto);
    TaskDto convertToDto(Task task);
}
