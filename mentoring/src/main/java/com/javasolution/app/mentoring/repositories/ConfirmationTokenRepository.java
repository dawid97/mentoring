package com.javasolution.app.mentoring.repositories;

import com.javasolution.app.mentoring.entities.ConfirmationToken;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ConfirmationTokenRepository extends CrudRepository<ConfirmationToken, Long> {
}
