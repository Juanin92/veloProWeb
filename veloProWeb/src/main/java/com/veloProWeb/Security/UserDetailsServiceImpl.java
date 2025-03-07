package com.veloProWeb.Security;

import com.veloProWeb.Repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

//Se encarga de cargar los detalles de un usuario desde la base de datos
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired private UserRepo userRepo;

    /**
     * Carga los detalles de un usuario por su nombre de usuario.
     * @param username El nombre de usuario del usuario a cargar.
     * @return Un objeto UserDetails que representa al usuario.
     * @throws UsernameNotFoundException Si el usuario no se encuentra.
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<com.veloProWeb.Model.Entity.User.User> user = userRepo.findByUsername(username);
        if (user.isPresent()){
            // crea una lista para almacenar sus roles (autoridades).
            List<GrantedAuthority> authorities = new ArrayList<>();
            authorities.add(new SimpleGrantedAuthority(user.get().getRole().name()));
            return new User(user.get().getUsername(), user.get().getPassword(), authorities);
        }else {
            throw new UsernameNotFoundException("Usuario no encontrado: " + username);
        }
    }
}
