package net.breezeware.SpringBootCafeteria.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.breezeware.SpringBootCafeteria.exception.DuplicateResourceException;
import net.breezeware.SpringBootCafeteria.exception.InvalidCredentialException;
import net.breezeware.SpringBootCafeteria.user.dto.UserLoginRequestDto;
import net.breezeware.SpringBootCafeteria.user.dto.UserRequestDto;
import net.breezeware.SpringBootCafeteria.user.dto.UserResponseDto;
import net.breezeware.SpringBootCafeteria.user.entity.User;
import net.breezeware.SpringBootCafeteria.user.enumeration.Role;
import net.breezeware.SpringBootCafeteria.user.repo.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class DeliveryStaffService {

    private final UserRepository userRepository;

    public UserResponseDto login(UserLoginRequestDto request) {
        log.info("Delivery Staff logging In service layer");
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new InvalidCredentialException("Invalid email or password"));

        if (!user.getPassword().equals(request.getPassword())) {
            throw new InvalidCredentialException("Invalid email or password");
        }

        return mapToResponse(user);
    }

    public UserResponseDto registerUser(UserRequestDto request) {
        log.info("Registering new Delivery Staff service layer");
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email already registered");
        }

        User user = new User(
                request.getName(),
                request.getEmail(),
                request.getPassword(),
                Role.DELIVERY_STAFF
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