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

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AdminUserService {

    private final UserRepository userRepository;

    public UserResponseDto login(UserLoginRequestDto request) {
        log.info("Admin logging In service layer");
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AppException("Invalid email or password", HttpStatus.UNAUTHORIZED));

        if (!user.getPassword().equals(request.getPassword())) {
            throw new AppException("Invalid email or password", HttpStatus.UNAUTHORIZED);
        }

        return mapToResponse(user);
    }

    public UserResponseDto registerUser(UserRequestDto request) {
        log.info("Registering new Admin service layer");
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new AppException("Email already registered", HttpStatus.CONFLICT);
        }

        User user = new User(
                request.getName(),
                request.getEmail(),
                request.getPassword(),
                Role.ADMIN
        );

        User savedUser = userRepository.save(user);
        return mapToResponse(savedUser);
    }

    public UserResponseDto getUserById(Long id) {
        log.info("view user by id in service layer");
        User user = userRepository.findById(id)
                .orElseThrow(() -> new AppException("User not found", HttpStatus.NOT_FOUND));
        return mapToResponse(user);
    }

    public List<UserResponseDto> getAllUsers() {
        log.info("get all users in service layer");

        List<UserResponseDto> users = userRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        if (users.isEmpty()) {
            throw new AppException("No users found in the system", HttpStatus.NOT_FOUND);
        }

        return users;
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
