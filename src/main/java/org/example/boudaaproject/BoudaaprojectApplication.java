package org.example.boudaaproject;

import org.example.boudaaproject.entities.Dataset;
import org.example.boudaaproject.entities.Role;
import org.example.boudaaproject.entities.User;
import org.example.boudaaproject.repositories.DatasetRepository;
import org.example.boudaaproject.repositories.IRoleRepository;
import org.example.boudaaproject.repositories.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.repository.support.Repositories;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;

@SpringBootApplication
public class BoudaaprojectApplication {

    public static void main(String[] args) {
        SpringApplication.run(BoudaaprojectApplication.class, args);
    }

//
//    @Bean
//    public CommandLineRunner run(DatasetRepository datasetRepository) {
//        return args -> {
//            if (datasetRepository.count() == 0) {
//                Dataset dataset = Dataset.builder()
//                        .name("Dataset Test Builder")
//                        .description("Ce dataset est inséré automatiquement au démarrage")
//                        .chemin("C:\\Users\\dell\\Downloads\\EX3\\EX3\\BOUDAAPROJECT\\src\\main\\resources\\static\\uploads\\nli.csv")
//                        .createdAt(LocalDateTime.now())
//                        .build();
//
//                datasetRepository.save(dataset);
//
//                System.out.println("✅ Dataset ajouté avec succès !");
//            }
//        };}


    }


