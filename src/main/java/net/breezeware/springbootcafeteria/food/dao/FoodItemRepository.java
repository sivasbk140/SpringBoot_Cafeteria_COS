package net.breezeware.springbootcafeteria.food.dao;

import jakarta.persistence.LockModeType;
import net.breezeware.springbootcafeteria.food.entity.FoodItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FoodItemRepository extends JpaRepository<FoodItem, Long> {

    Optional<FoodItem> findByNameIgnoreCase(String name);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT f FROM FoodItem f WHERE f.id = :id")
    Optional<FoodItem> findByIdWithLock(@Param("id") Long id);

    List<FoodItem> findByCategory(String category);

    @Query("SELECT f FROM FoodItem f WHERE f.quantity > 0")
    List<FoodItem> findAvailableItems();

    @Query("SELECT f FROM FoodItem f WHERE f.quantity <= :threshold")
    List<FoodItem> findLowStock(@Param("threshold") int threshold);

    List<FoodItem> findByNameContainingIgnoreCase(String keyword);
}
