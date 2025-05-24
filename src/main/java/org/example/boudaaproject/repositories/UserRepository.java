package org.example.boudaaproject.repositories;

import org.example.boudaaproject.entities.Role;
import org.example.boudaaproject.entities.User;
import org.example.boudaaproject.entities.UserState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    //Si tu retournes directement User, et qu'il n’existe pas : ⚠️ tu obtiens une NullPointerException.
//
//Si tu retournes Optional<User>, tu forces à gérer le cas où l'utilisateur n’est pas trouvé.
    Optional<User> findByUsername(String username);
    User findUserByUsername(String username);
    Optional<User> findByEmail(String email);
    Optional<User> findByPhone(String phone);


    Optional<User> findByUsernameAndPassword(String username, String password);

//    List<User> findByUserName(String username);
List<User> findByUsernameContainingIgnoreCase(String username);
    boolean existsByEmail(String email);
    List<User> findAllByRole(Role  role);
     Optional<User> findUserById( Long id);
    long countByRole(Role role);
    List<User> findByRole_Role(String roleName);
    Optional<User> findById(Long id);

    List<User> findByAddressContainingIgnoreCase(String address);
    List<User> findByEmailContainingIgnoreCase(String email);
    List<User> findByPhoneContainingIgnoreCase(String phoneNumber);
    @Query("SELECT u FROM User u WHERE u.role.role = :roleName")
    List<User> findByRoleName(@Param("roleName") String roleName);


    @Query("SELECT u FROM User u WHERE " +
            "LOWER(u.username) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(u.address) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(u.phone) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<User> searchByKeyword(@Param("keyword") String keyword);
    String findUserNameById(Long id);






}
