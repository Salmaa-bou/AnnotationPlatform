package org.example.boudaaproject.services;
import org.example.boudaaproject.entities.*;
import lombok.RequiredArgsConstructor;
import org.example.boudaaproject.repositories.SubTaskAssignmentRepository;
import org.example.boudaaproject.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnnotatorServiceImpl implements IAnnotatorService {
    private final Logger log = LoggerFactory.getLogger(AnnotatorServiceImpl.class);
    private final UserRepository userRepository;
    private final SubTaskAssignmentRepository subTaskAssignmentRepository;

//    @Transactional(readOnly = true)
//    public List<Task> getAssignedTasks(Long annotatorId) {
//        log.info("Fetching tasks for annotator ID: {}", annotatorId);
//        List<SubTaskAssignment> assignments = subTaskAssignmentRepository.findByAnnotatorId(annotatorId);
//        return assignments.stream()
//                .map(SubTaskAssignment::getTask)
//                .distinct()
//                .collect(Collectors.toList());
//    }


}
