package com.skypro.telegram_team.services;

import com.skypro.telegram_team.models.User;
import com.skypro.telegram_team.repositories.UserRepository;
import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Log4j
@Service
public class UserService {
    UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void save(User user) {
        log.info("Saving user: " + user);
        userRepository.save(user);
    }

    public User findById(Long id) {
        log.info("Finding user by id: " + id);
        return userRepository.findById(id).orElse(null);
    }

    public void deleteById(Long id) {
        log.info("Deleting user by id: " + id);
        userRepository.deleteById(id);
    }

    public List<User> findAll() {
        log.info("Finding all users");
        return userRepository.findAll();
    }
}
