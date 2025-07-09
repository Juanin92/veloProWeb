package com.veloproweb.scheduler;

import com.veloproweb.model.entity.user.User;
import com.veloproweb.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
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
    @Async("taskExecutor")
    public void cleanTokenToUsers(){
        List<User> usersWithTokens = userRepo.findByTokenIsNotNull();
        if (!usersWithTokens.isEmpty()) {
            usersWithTokens.forEach(user -> user.setToken(null));
            userRepo.saveAll(usersWithTokens);
        }
    }
}
