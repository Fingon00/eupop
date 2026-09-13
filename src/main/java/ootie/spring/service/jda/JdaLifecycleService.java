package ootie.spring.service.jda;

import jakarta.annotation.PreDestroy;
import ootie.discord.JdaService;
import org.springframework.stereotype.Service;

@Service
public class JdaLifecycleService {

    @PreDestroy
    private void shutdown() {
        JdaService.shutdown();
    }
}
