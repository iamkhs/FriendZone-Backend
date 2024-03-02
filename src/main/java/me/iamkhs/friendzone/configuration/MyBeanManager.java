package me.iamkhs.friendzone.configuration;

import com.cloudinary.Cloudinary;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class MyBeanManager {

    private final String API_KEY = "446994387341493";
    private final String API_SECRET = "qDqjGSv-qi9eSNFrJoMLz4M8cqE";


    @Bean
    public Cloudinary getCloudinary(){
        Map<?, ?> config = new HashMap<>(
                Map.of("cloud_name", "dqkgbredv",
                        "api_key", API_KEY,
                        "api_secret", API_SECRET,
                        "secure", true)
        );
        return new Cloudinary(config);
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }
}
