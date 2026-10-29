package pe.rapillantas.api.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
  @Value("${app.uploads-dir:./uploads}")
  private String uploadsDir;

  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {
    // Fotos subidas desde el dispositivo se sirven en /uploads/**
    registry.addResourceHandler("/uploads/**")
        .addResourceLocations("file:" + uploadsDir + "/");
  }
}
