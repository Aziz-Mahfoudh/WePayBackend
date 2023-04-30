package com.example.userapp.appuser;

import com.example.userapp.exception.UserNotFoundException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@Transactional(readOnly = true)
public interface ParticularRepository extends JpaRepository<ParticularUser, Integer> {
    ParticularUser findAppUserById(Integer id) throws UserNotFoundException;
    ParticularUser findByEmail(String email);
}
