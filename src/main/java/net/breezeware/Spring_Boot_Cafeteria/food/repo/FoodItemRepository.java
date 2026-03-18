package net.breezeware.Spring_Boot_Cafeteria.food.repo;

import jakarta.persistence.Id;
import net.breezeware.Spring_Boot_Cafeteria.food.entity.FoodItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public interface FoodItemRepository extends JpaRepository<FoodItem, Long> {

     ArrayList<FoodItem> findByItemId (Long id);

    List<FoodItem> findByName(String name);

    List<FoodItem> findByCategory(String category);

}
