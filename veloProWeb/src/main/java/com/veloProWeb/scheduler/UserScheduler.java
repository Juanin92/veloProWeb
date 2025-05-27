package com.veloProWeb.scheduler;

import com.veloProWeb.model.entity.User.User;
import com.veloProWeb.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class UserScheduler {

    private final UserRepo userRepo;

    /**
     * Limpia los campos de token del usuario cada 2 hr automáticamente
     */
    @Scheduled(fixedRate = 7200000)
    public void cleanTokenToUsers(){
        List<User> usersWithTokens = userRepo.findByTokenIsNotNull();
        if (!usersWithTokens.isEmpty()) {
            usersWithTokens.forEach(user -> user.setToken(null));
            userRepo.saveAll(usersWithTokens);
        }
    }
}
