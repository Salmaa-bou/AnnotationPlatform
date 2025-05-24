package org.example.boudaaproject.services;

import org.example.boudaaproject.entities.User;
import org.example.boudaaproject.entities.UserState;

import java.util.Optional;

public interface IUserService {
    void registerUser(User user);
    User findById(Long id) ;


}
