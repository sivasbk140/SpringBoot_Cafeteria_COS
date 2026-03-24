package net.breezeware.Spring_Boot_Cafeteria.food.repo;

import net.breezeware.Spring_Boot_Cafeteria.food.entity.FoodItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FoodItemRepository extends JpaRepository<FoodItem, Long> {

    List<FoodItem> findByName(String name);

    Optional<FoodItem> findByNameIgnoreCase(String name);

    List<FoodItem> findByCategory(String category);

    @Query("SELECT f FROM FoodItem f WHERE f.quantity > 0")
    List<FoodItem> findAvailableItems();

    @Query("SELECT f FROM FoodItem f WHERE f.quantity <= :threshold")
    List<FoodItem> findLowStock(@Param("threshold") int threshold);

    List<FoodItem> findByNameContainingIgnoreCase(String keyword);
}
