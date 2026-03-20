package net.breezeware.Spring_Boot_Cafeteria.food.repo;

import net.breezeware.Spring_Boot_Cafeteria.food.entity.AvailabilityMap;
import net.breezeware.Spring_Boot_Cafeteria.food.entity.FoodMenu;
import net.breezeware.Spring_Boot_Cafeteria.food.enumeration.MenuDay;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AvailabilityMapRepository extends JpaRepository<AvailabilityMap, Long> {

    List<AvailabilityMap> findByMenu(FoodMenu menu);

    List<AvailabilityMap> findByMenu_Id(Long menuId);

    List<AvailabilityMap> findByMenuDay(MenuDay menuDay);

    boolean existsByMenu_IdAndMenuDay(Long menuId, MenuDay menuDay);
}