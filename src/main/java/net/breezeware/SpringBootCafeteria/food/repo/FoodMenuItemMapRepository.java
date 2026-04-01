package net.breezeware.SpringBootCafeteria.food.repo;

import net.breezeware.SpringBootCafeteria.food.entity.FoodMenu;
import net.breezeware.SpringBootCafeteria.food.entity.FoodMenuItemMap;
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