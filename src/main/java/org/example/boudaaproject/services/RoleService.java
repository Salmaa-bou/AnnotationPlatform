package org.example.boudaaproject.services;

import lombok.RequiredArgsConstructor;
import org.example.boudaaproject.entities.Role;
import org.example.boudaaproject.entities.UserState;
import org.example.boudaaproject.repositories.IRoleRepository;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleService implements IRoleService {
    private final IRoleRepository roleRepository;
    @Override
    public List<Role> getRoles() {
        return roleRepository.findAll();
    }

}

