package com.veloProWeb.Service;

import com.veloProWeb.Model.Entity.User;
import com.veloProWeb.Repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService implements IUserService{

    @Autowired private UserRepo userRepository;

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}
