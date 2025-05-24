package org.example.boudaaproject.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.boudaaproject.entities.CoupleTexte;
import org.example.boudaaproject.entities.Dataset;
import org.example.boudaaproject.repositories.CoupleTexteRepository;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
@RequiredArgsConstructor
@Slf4j
public class CoupleTexteImpl implements ICoupleTexteService {
    private final CoupleTexteRepository coupleTexteRepository;

    @Override
    public void parseAndSaveCouples(Path filePath, Dataset dataset) {
        int lineNumber = 0;

        try (BufferedReader reader = Files.newBufferedReader(filePath)) {
            String line;
            boolean isFirstLine = true;

            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (isFirstLine) {
                    isFirstLine = false;
                    continue; // skip header
                }

                try {
                    String[] parts = line.split(",", -1);

                    if (parts.length < 3) {
                        log.warn("Ligne ignorée (format invalide): {}", line);
                        continue;
                    }

                    String texte1 = parts[1].trim().replaceAll("^\"|\"$", "");
                    String texte2 = parts[2].trim().replaceAll("^\"|\"$", "");

                    CoupleTexte couple = new CoupleTexte();
                    couple.setTexte1(texte1);
                    couple.setTexte2(texte2);
                    couple.setDataset(dataset);

                    coupleTexteRepository.save(couple);
                    log.info("Couple inséré: [{}] -> [{}]", texte1, texte2);

                } catch (Exception ex) {
                    log.error("Erreur lors du traitement de la ligne {}: {}", lineNumber, line, ex);
                }
            }

            log.info("Traitement terminé. Total lignes traitées: {}", lineNumber);

        } catch (IOException e) {
            log.error("Erreur pendant la lecture du fichier CSV", e);
            throw new RuntimeException("Erreur pendant la lecture du fichier CSV", e);
        }
    }
}
