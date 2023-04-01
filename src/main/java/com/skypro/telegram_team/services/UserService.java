package com.skypro.telegram_team.services;

import com.skypro.telegram_team.models.User;
import com.skypro.telegram_team.repositories.UserRepository;
import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@Log4j
@Service
public class UserService {
    UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    @Transactional
    public User save(User user) {
        log.info("Saving user: " + user);
        return userRepository.save(user);
    }

//    @Transactional
//    public User update(User user) {
//        log.info("Updating user: " + user);
//        return userRepository.updateUserById(user.getId(), user);
//    }

    public User findById(Long id) {
        log.info("Finding user by id: " + id);
        return userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("User not found"));
    }
    @Transactional
    public User deleteById(Long id) {
        log.info("Deleting user by id: " + id);
        User user = userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("User not found"));
        userRepository.delete(user);
        return user;
    }

    public List<User> findAll() {
        log.info("Finding all users");
        return userRepository.findAll();
    }
}
