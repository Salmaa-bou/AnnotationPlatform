package org.example.boudaaproject.services;

import lombok.Getter;
import lombok.Setter;
import org.example.boudaaproject.entities.User;
import org.example.boudaaproject.entities.UserState;
import org.example.boudaaproject.repositories.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Getter
@Setter
public class CustomerUserDetailsService  implements UserDetailsService {
    private final UserRepository userRepository;

    public CustomerUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé"));

        // Check if the account is active
        if (user.getUserState() != UserState.ENABLED) {
            throw new UsernameNotFoundException("Le compte de l'utilisateur est " + user.getUserState());
        }

        return new org.springframework.security.core.userdetails.User(
                String.valueOf(user.getId()), // Use ID instead of username
                user.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().getRole()))
        );

}
}
