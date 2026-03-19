package net.breezeware.Spring_Boot_Cafeteria.food.repo;

import net.breezeware.Spring_Boot_Cafeteria.food.entity.FoodItem;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.List;


public interface FoodItemRepository extends JpaRepository<FoodItem, Long> {

    // List<FoodItem> findByItemId (Long id);

    List<FoodItem> findByName(String name);

    List<FoodItem> findByCategory(String category);

}
