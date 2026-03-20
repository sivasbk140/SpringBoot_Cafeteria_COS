package net.breezeware.Spring_Boot_Cafeteria.food.repository;

import net.breezeware.Spring_Boot_Cafeteria.food.entity.FoodMenu;
import net.breezeware.Spring_Boot_Cafeteria.food.entity.FoodMenuItemMap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FoodMenuItemMapRepository extends JpaRepository<FoodMenuItemMap, Long> {

    // Find all items in a menu
    List<FoodMenuItemMap> findByMenu(FoodMenu menu);

    List<FoodMenuItemMap> findByMenu_Id(Long menuId);

    // Find available items in menu
    List<FoodMenuItemMap> findByMenuAndIsAvailable(FoodMenu menu, Boolean isAvailable);

    // Find specific menu item mapping
    List<FoodMenuItemMap> findByMenu_IdAndFoodItem_Id(Long menuId, Long foodItemId);
}