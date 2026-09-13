package ootie.spring.context;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Configuration
public class WebConfiguration implements WebMvcConfigurer {

    private final GameLockAndRequestContextInterceptor gameLockAndRequestContextInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(gameLockAndRequestContextInterceptor)
                .addPathPatterns("/api/game/{gameName}/**", "/api/public/game/{gameName}/**");
    }
}
