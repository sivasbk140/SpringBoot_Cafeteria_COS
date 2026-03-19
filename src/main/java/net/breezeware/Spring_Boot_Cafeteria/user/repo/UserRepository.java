package net.breezeware.Spring_Boot_Cafeteria.user.repo;

import net.breezeware.Spring_Boot_Cafeteria.user.enumeration.Role;
import net.breezeware.Spring_Boot_Cafeteria.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {


    Optional<User> findByEmail(String email);


    boolean existsByEmail(String email);





}