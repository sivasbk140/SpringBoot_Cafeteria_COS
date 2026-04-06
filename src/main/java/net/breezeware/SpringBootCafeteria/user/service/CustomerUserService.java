package net.breezeware.SpringBootCafeteria.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.breezeware.SpringBootCafeteria.exception.AppException;
import net.breezeware.SpringBootCafeteria.user.dto.UserLoginRequestDto;
import net.breezeware.SpringBootCafeteria.user.dto.UserRequestDto;
import net.breezeware.SpringBootCafeteria.user.dto.UserResponseDto;
import net.breezeware.SpringBootCafeteria.user.entity.User;
import net.breezeware.SpringBootCafeteria.user.enumeration.Role;
import net.breezeware.SpringBootCafeteria.user.repo.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CustomerUserService {

    private final UserRepository userRepository;

    public UserResponseDto login(UserLoginRequestDto request) {
        log.info("customer logging In service layer");
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AppException("Invalid email or password", HttpStatus.UNAUTHORIZED));

        if (!user.getPassword().equals(request.getPassword())) {
            throw new AppException("Invalid email or password", HttpStatus.UNAUTHORIZED);
        }

        return mapToResponse(user);
    }

    public UserResponseDto registerUser(UserRequestDto request) {
        log.info("Registering customer service layer");
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new AppException("Email already registered", HttpStatus.CONFLICT);
        }

        User user = new User(
                request.getName(),
                request.getEmail(),
                request.getPassword(),
                Role.CUSTOMER
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
