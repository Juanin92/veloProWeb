package com.veloProWeb.Controller;

import com.veloProWeb.Model.Entity.User;
import com.veloProWeb.Service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/usuario")
public class UserController {

    @Autowired private IUserService userService;

    @GetMapping
    public List<User> getListUser(){
        return userService.getAllUser();
    }
}
