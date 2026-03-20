package net.breezeware.Spring_Boot_Cafeteria.food.repository;

import net.breezeware.Spring_Boot_Cafeteria.food.entity.AvailabilityMap;
import net.breezeware.Spring_Boot_Cafeteria.food.entity.FoodMenu;
import net.breezeware.Spring_Boot_Cafeteria.food.entity.MenuDay;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AvailabilityMapRepository extends JpaRepository<AvailabilityMap, Long> {

    // Find all availability days for a menu
    List<AvailabilityMap> findByMenu(FoodMenu menu);

    List<AvailabilityMap> findByMenu_Id(Long menuId);

    // Find all menus available on a specific day
    List<AvailabilityMap> findByMenuDay(MenuDay menuDay);

    // Check if menu is available on a specific day
    boolean existsByMenu_IdAndMenuDay(Long menuId, MenuDay menuDay);
}