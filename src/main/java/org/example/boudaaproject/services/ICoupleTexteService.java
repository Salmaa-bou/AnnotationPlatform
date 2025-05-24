package org.example.boudaaproject.services;

import org.example.boudaaproject.entities.Dataset;

import java.nio.file.Path;

public interface ICoupleTexteService {
    void  parseAndSaveCouples(Path filePath, Dataset dataset);
}
