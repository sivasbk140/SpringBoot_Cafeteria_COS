package net.breezeware.Spring_Boot_Cafeteria.user.service;

import  lombok .*;
import lombok.extern.slf4j.Slf4j;
import net.breezeware.Spring_Boot_Cafeteria.user.dto.UserRequestDto;
import net.breezeware.Spring_Boot_Cafeteria.user.dto.UserResponseDto;
import net.breezeware.Spring_Boot_Cafeteria.user.entity.User;
import net.breezeware.Spring_Boot_Cafeteria.user.repo.UserRepository;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
@Slf4j

public class DeliveryStaffService {

     private final UserRepository userRepository;


    public UserResponseDto login(UserRequestDto request) {
        log.info("Delivery Staff  logging In service layer");
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));

        if (!user.getPassword().equals(request.getPassword())) {
            throw new RuntimeException("Invalid email or password");
        }

        return mapToResponse(user);
    }

    public UserResponseDto registerUser(UserRequestDto request) {
        log.info("Registering new Delivery Staff service layer");
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already registered");
        }

        User user = new User(
                request.getName(),
                request.getEmail(),
                request.getPassword(),
                request.getRole()
        );

        User savedUser = userRepository.save(user);
        return mapToResponse(savedUser);
    }

    private UserResponseDto mapToResponse(User user) {
        return new UserResponseDto(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole()
        );
    }
}



