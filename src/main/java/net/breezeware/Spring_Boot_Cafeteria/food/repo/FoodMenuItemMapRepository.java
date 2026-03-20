package net.breezeware.Spring_Boot_Cafeteria.food.repo;

import net.breezeware.Spring_Boot_Cafeteria.food.entity.FoodMenu;
import net.breezeware.Spring_Boot_Cafeteria.food.entity.FoodMenuItemMap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FoodMenuItemMapRepository extends JpaRepository<FoodMenuItemMap, Long> {

    List<FoodMenuItemMap> findByMenu(FoodMenu menu);

    List<FoodMenuItemMap> findByMenu_Id(Long menuId);

    List<FoodMenuItemMap> findByMenuAndIsAvailable(FoodMenu menu, Boolean isAvailable);

    List<FoodMenuItemMap> findByMenu_IdAndFoodItem_Id(Long menuId, Long foodItemId);
}