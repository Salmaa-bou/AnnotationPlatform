package org.example.boudaaproject.services;

import org.example.boudaaproject.entities.SubTask;
import org.example.boudaaproject.entities.User;

import java.util.List;

public interface ISubTaskAssignmentService {
    void assignAnnotatorsToSubTasks(Long taskId);

}
