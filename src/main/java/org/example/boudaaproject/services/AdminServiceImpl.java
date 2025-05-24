package org.example.boudaaproject.services;

import lombok.RequiredArgsConstructor;
import org.example.boudaaproject.entities.*;
import org.example.boudaaproject.entities.User;
import org.example.boudaaproject.repositories.*;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.swing.text.html.Option;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {
    private final IRoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final IDatasetService datasetService;

//    private final CategoryRepository categoryRepository;
//    private final AnnotationRepository annotationRepository;
//    private final DatasetRepository datasetRepository;
//    private final CoupleTexteRepository coupleTexteRepository;
//    private final NlpTaskTypeRepository nlpTaskTypeRepository;
//    private final TaskRepository taskRepository;

    @Override
    public void createAnnotator(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        Role annotatorRole = roleRepository.findByRole("annotator")
                .orElseThrow(() -> new RuntimeException("Role 'annotator' non trouvé"));
        user.setRole(annotatorRole);
        userRepository.save(user);
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public void updateAnnotator(User updatedUser) {
        Optional<User> existingUserOpt = userRepository.findById(updatedUser.getId());
        if (existingUserOpt.isPresent()) {
            User user = existingUserOpt.get();

            // Set only the fields that are provided in the updatedUser object
            user.setUsername(updatedUser.getUsername());
            user.setEmail(updatedUser.getEmail());
            user.setPhone(updatedUser.getPhone());
            user.setAddress(updatedUser.getAddress());
            user.setRib(updatedUser.getRib());

            // Update password only if it's provided
            if (updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty()) {
                user.setPassword(passwordEncoder.encode(updatedUser.getPassword()));  // Encoding the new password
            }

            // Save the updated user in the database
            userRepository.save(user);
        } else {
            throw new RuntimeException("Utilisateur introuvable");
        }
    }

    @Override
    public List<User> listAnnotators() {
        Role annotatorRole = roleRepository.findByRole("annotator")
                .orElseThrow(() -> new RuntimeException("Role 'annotator' non trouvé"));
        return userRepository.findAllByRole(annotatorRole);
    }

    @Override
    public long countAnnotators() {
        Role annotatorRole = roleRepository.findByRole("annotator")
                .orElseThrow(() -> new RuntimeException("Role 'annotator' non trouvé"));
        return userRepository.countByRole(annotatorRole);
    }


    @Override
    public List<User> getAllAnnotators() {

        Role annotatorRole = roleRepository.findByRole("annotator")
                .orElseThrow(() -> new RuntimeException("Role 'annotator' non trouvé"));
        return userRepository.findAllByRole(annotatorRole);
    }


    @Override
    public void promoteToAdmin(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        Role adminRole = roleRepository.findByRole("admin")
                .orElseThrow(() -> new RuntimeException("Role 'admin' non trouvé"));
        user.setRole(adminRole);
        userRepository.save(user);
    }
    @Override
    public List<User> chercherAnnotator(String username){
        return userRepository.searchByKeyword(username);

    }

    @Override
    public Optional<User> getAnnotator(Long id) {
        return userRepository.findById(id);
    }
    @Override
    public void toggleUserStatus(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        if (user.getUserState() == UserState.ENABLED) {
            user.setUserState(UserState.DISABLED);
        } else {
            user.setUserState(UserState.ENABLED);
        }

        userRepository.save(user);
    }

    @Override
    public void updateUserState(Long id, UserState newState) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        user.setUserState(newState);
        userRepository.save(user);
    }





}
