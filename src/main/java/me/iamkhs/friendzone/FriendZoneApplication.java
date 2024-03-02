package me.iamkhs.friendzone;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class FriendZoneApplication {

    public static void main(String[] args) {
        SpringApplication.run(FriendZoneApplication.class, args);
    }
}
