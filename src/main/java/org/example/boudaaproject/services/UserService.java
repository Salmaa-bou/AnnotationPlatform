package org.example.boudaaproject.services;

import lombok.RequiredArgsConstructor;
import org.example.boudaaproject.entities.Role;
import org.example.boudaaproject.entities.User;
import org.example.boudaaproject.entities.UserState;
import org.example.boudaaproject.repositories.IRoleRepository;
import org.example.boudaaproject.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service

public class UserService implements IUserService {

    private final UserRepository userRepository;
    private final IRoleRepository roleRepository;
    private final BCryptPasswordEncoder passwordEncoder;


    public UserService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder,IRoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;

    }

    @Override
    public void registerUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Assigner le rôle ANNOTATOR par défaut
        Role annotatorRole = roleRepository.findByRole("annotator")
                .orElseThrow(() -> new RuntimeException("Role ANNOTATOR not found"));

        user.setRole(annotatorRole);

        // Sauvegarder l'utilisateur
        userRepository.save(user);
    }
    @Override
  public User findById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Tâche introuvable avec l'id: " + id));
  }
  public String getUserNameById(Long id){
       return userRepository.findUserNameById( id);
  }


}
