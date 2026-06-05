package com.example.studyapp2.service;

import com.example.studyapp2.entity.User;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    public User findByUsername(String username) {
        return new User(username, "학습자");
    }
}
