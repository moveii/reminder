package at.spengergasse.nvs.server.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * This is a configuration class for mvc web security.
 */

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    /**
     * If a user enters a URL manually, no error will be error page will be shown due to this method.
     *
     * @param registry the {@code ViewControllerRegistry}
     */

    @Override
    public void addViewControllers(final ViewControllerRegistry registry) {
        registry.addViewController("/login").setViewName("forward:/");
        registry.addViewController("/reminder").setViewName("forward:/");
        registry.addViewController("/registration").setViewName("forward:/");
        registry.addViewController("/user/edit").setViewName("forward:/");
    }
}
