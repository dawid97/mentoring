package com.javasolution.app.mentoring.services;

import com.javasolution.app.mentoring.entities.ConfirmationToken;
import com.javasolution.app.mentoring.entities.User;
import com.javasolution.app.mentoring.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService implements UserDetailsService {

    @Autowired //must be autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    private final UserRepository userRepository;
    private final ConfirmationTokenService confirmationTokenService;

    public UserService(UserRepository userRepository,
                       ConfirmationTokenService confirmationTokenService) {
        this.confirmationTokenService = confirmationTokenService;
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        final Optional<User> optionalUser = userRepository.findByEmail(email);
        return optionalUser.orElseThrow(() -> new UsernameNotFoundException("User with email " + email + " cannot be found."));
    }

    public User signUpUser(User user) {
        final String encryptedPassword = bCryptPasswordEncoder.encode(user.getPassword());
        user.setPassword(encryptedPassword);
        user.setConfirmPassword("");

        final ConfirmationToken confirmationToken = new ConfirmationToken(user);
        confirmationTokenService.saveConfirmationToken(confirmationToken);

        final User savedUser = userRepository.save(user);

        savedUser.setPassword("");
        return savedUser;
    }
}
