package com.veloProWeb.Service;

import com.veloProWeb.Model.Entity.User;
import com.veloProWeb.Repository.UserRepo;
import com.veloProWeb.Validation.UserValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class UserService implements IUserService{

    @Autowired private UserRepo userRepository;
    @Autowired private UserValidator validator;

    @Override
    public void addUser(User user) {
        User userDB = getUserCreated(user.getRut());
        if (userDB != null){
            throw new IllegalArgumentException("Usuario Existente: Ya hay existe el usuario");
        } else {
            validator.validate(user);
            user.setStatus(true);
            user.setDate(LocalDate.now());
            user.setName(user.getName().substring(0, 1).toUpperCase() + user.getName().substring(1));
            user.setSurname(user.getSurname().substring(0, 1).toUpperCase() + user.getSurname().substring(1));
            User user2 = getUser(user.getUsername());
            if (user2 != null && user2.getUsername().equalsIgnoreCase(user.getUsername())) {
                throw new IllegalArgumentException("Este nombre de usuario ya existe");
            }
            userRepository.save(user);
        }
    }

    @Override
    public void updateUser(User user) {

    }

    @Override
    public void deleteUser(User user) {

    }

    @Override
    public void activateUser(User user) {

    }

    @Override
    public List<User> getAllUser() {
        return userRepository.findAll();
    }

    @Override
    public User getAuthUser(String username, String pass) {
        return null;
    }

    @Override
    public User getAuthUserToken(String username, String token) {
        return null;
    }

    @Override
    public User getUser(String username) {
        return null;
    }

    @Override
    public void sendEmailCode(User user) {

    }

    @Override
    public void sendEmailUpdatePassword(User user) {

    }

    private User getUserCreated(String rut){
        Optional<User> user = userRepository.findByRut(rut);
        return user.orElse(null);
    }
}
