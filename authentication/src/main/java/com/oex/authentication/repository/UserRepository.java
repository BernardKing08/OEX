package com.oex.authentication.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.oex.authentication.model.User;
import java.util.Optional;


public interface UserRepository extends JpaRepository<User, Long>{
    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

}
