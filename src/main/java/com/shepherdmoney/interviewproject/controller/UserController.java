package com.shepherdmoney.interviewproject.controller;

import com.shepherdmoney.interviewproject.model.User;
import com.shepherdmoney.interviewproject.repository.UserRepository;
import com.shepherdmoney.interviewproject.vo.request.CreateUserPayload;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {

    private UserRepository userRepository;

    @PutMapping("/user")
    public ResponseEntity<Integer> createUser(@RequestBody CreateUserPayload payload) {
        User user = new User();
        user.setName(payload.getName());
        user.setEmail(payload.getEmail());

        // Save the user to the database
        User savedUser = userRepository.save(user);

        // Return the user ID in the response
        return ResponseEntity.status(HttpStatus.OK).body(savedUser.getId());
    }

    @DeleteMapping("/user")
    public ResponseEntity<String> deleteUser(@RequestParam int userId) {
        if (!userRepository.existsById(userId)) {
            // Return 400 Bad Request if the user doesn't exist
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User with ID " + userId + " does not exist.");
        }

        // Delete the user
        userRepository.deleteById(userId);

        // Return 200 OK with a success message
        return ResponseEntity.status(HttpStatus.OK).body("User with ID " + userId + " has been successfully deleted.");
    }
}
