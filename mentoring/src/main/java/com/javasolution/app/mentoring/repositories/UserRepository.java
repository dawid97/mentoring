package com.javasolution.app.mentoring.repositories;

import com.javasolution.app.mentoring.entities.User;
import com.javasolution.app.mentoring.entities.UserRole;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {
    User findByEmail(String email);

    User findByUserRole(UserRole userRole);
}