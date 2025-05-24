package org.example.boudaaproject.services;

import lombok.RequiredArgsConstructor;
import org.example.boudaaproject.entities.CoupleTexte;
import org.example.boudaaproject.entities.Dataset;
import org.example.boudaaproject.repositories.CoupleTexteRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
@Service
@RequiredArgsConstructor
public class CsvService {

    private static final Logger logger = LoggerFactory.getLogger(CsvService.class);

    private final IDatasetService datasetService;
    private final CoupleTexteRepository coupleTexteRepository;
    private final FileStorageService fileStorageService;
    public void importDatasetFromCsv(MultipartFile file, String datasetName, String description) throws IOException {

        if (datasetService.existsByName(datasetName)) {
            throw new IllegalArgumentException("Un dataset avec ce nom existe déjà !");
        }

        // Lire toutes les lignes à partir du fichier AVANT toute lecture InputStream
        List<String> lignes = new BufferedReader(
                new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))
                .lines()
                .toList();

        // Ensuite enregistrer physiquement le fichier (on ne touche plus au flux après)
        String chemin = fileStorageService.storeFile(file);

        Dataset dataset = Dataset.builder()
                .name(datasetName)
                .description(description)
                .chemin(chemin)
                .build();

        Dataset savedDataset = datasetService.saveDataset(dataset, file);

        if (lignes.isEmpty()) {
            logger.warn("Fichier vide !");
            return;
        }

        // Parsing CSV à partir de la liste de lignes déjà lues
        String headerLine = lignes.get(0);
        String[] headers = headerLine.split(",", -1);
        int indexTexte1 = -1;
        int indexTexte2 = -1;

        for (int i = 0; i < headers.length; i++) {
            String header = headers[i].trim().toLowerCase().replaceAll("[^a-z0-9]", "");
            if (header.contains("text1") || header.contains("texte1")) {
                indexTexte1 = i;
            } else if (header.contains("text2") || header.contains("texte2")) {
                indexTexte2 = i;
            }
        }

        if (indexTexte1 == -1 || indexTexte2 == -1) {
            throw new IllegalArgumentException("Colonnes 'Text1' ou 'Text2' manquantes !");
        }

        List<CoupleTexte> couples = new ArrayList<>();
        for (int i = 1; i < lignes.size(); i++) {
            String line = lignes.get(i);
            String[] values = line.split(",", -1);
            if (values.length > Math.max(indexTexte1, indexTexte2)) {
                String texte1 = values[indexTexte1].trim();
                String texte2 = values[indexTexte2].trim();
                if (!texte1.isEmpty() && !texte2.isEmpty()) {
                    couples.add(CoupleTexte.builder()
                            .texte1(texte1)
                            .texte2(texte2)
                            .dataset(savedDataset)
                            .build());
                }
            }
        }

        coupleTexteRepository.saveAll(couples);
        logger.info("Import terminé : {} couples enregistrés", couples.size());
    }    }