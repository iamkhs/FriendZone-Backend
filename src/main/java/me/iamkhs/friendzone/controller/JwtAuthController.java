package me.iamkhs.friendzone.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.iamkhs.friendzone.dtos.UserDto;
import me.iamkhs.friendzone.entities.User;
import me.iamkhs.friendzone.jwtconfig.JwtUtil;
import me.iamkhs.friendzone.repository.UserRepository;
import me.iamkhs.friendzone.request.JwtResponse;
import me.iamkhs.friendzone.request.LoginRequest;
import me.iamkhs.friendzone.util.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/friend-zone/auth/")
@CrossOrigin("*")
public class JwtAuthController {

    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;

    @PostMapping("/login")
    public ResponseEntity<?> generateToken(@RequestBody LoginRequest loginRequest) {
        log.info("inside generateToken() method");
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.username(), loginRequest.password())
            );

            if (authentication.isAuthenticated()) {
                String username = authentication.getName();
                String token = jwtUtil.generateToken(username);

                Optional<User> optionalUser = userRepository.findByUsername(username);
                UserDto userDto = optionalUser.map(ModelMapper::userToUserDto).orElse(null);

                JwtResponse jwtResponse = new JwtResponse(token, userDto);
                log.info("jwtResponse {}", jwtResponse);
                return ResponseEntity.ok(jwtResponse);
            } else {
                log.error("invalid user request {}", loginRequest);
                throw new UsernameNotFoundException("Invalid user request !");
            }
        } catch (Exception e) {
            log.error("invalid username or password {}", loginRequest);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid Username or Password");
        }
    }

}
