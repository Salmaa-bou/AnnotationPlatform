package org.example.boudaaproject.services;

import lombok.RequiredArgsConstructor;
import org.example.boudaaproject.entities.User;
import org.example.boudaaproject.entities.UserState;
import org.example.boudaaproject.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
    @Service
    public class UserStateService {
    private final UserRepository userRepository;

        public List<UserState> findAllStates() {
            return Arrays.asList(UserState.values());
        }

    }

