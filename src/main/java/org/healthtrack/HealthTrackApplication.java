package org.healthtrack;

import org.healthtrack.service.*;
import org.healthtrack.ui.LoginFrame;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import javax.swing.SwingUtilities;

@SpringBootApplication
public class HealthTrackApplication {
    private static ConfigurableApplicationContext context;
    
    public static void main(String[] args) {
        // Disable headless mode to allow GUI creation
        System.setProperty("java.awt.headless", "false");
        
        // Start Spring Boot application context
        context = SpringApplication.run(HealthTrackApplication.class, args);

        // Get UserService bean from Spring context
        UserService userService = context.getBean(UserService.class);

        // Launch login UI on Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            LoginFrame loginFrame = new LoginFrame(userService);
            loginFrame.setVisible(true);
        });
    }
    
    /**
     * 获取Spring应用上下文，用于在其他类中获取服务Bean
     */
    public static ConfigurableApplicationContext getContext() {
        if (context == null) {
            throw new IllegalStateException("Spring应用上下文尚未初始化，请确保在Spring Boot启动后再调用此方法");
        }
        return context;
    }
}