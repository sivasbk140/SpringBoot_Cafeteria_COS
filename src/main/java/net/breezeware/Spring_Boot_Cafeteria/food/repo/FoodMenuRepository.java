package net.breezeware.Spring_Boot_Cafeteria.food.repo;

import net.breezeware.Spring_Boot_Cafeteria.food.entity.FoodMenu;
import net.breezeware.Spring_Boot_Cafeteria.food.enumeration.MenuDay;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FoodMenuRepository extends JpaRepository<FoodMenu, Long> {

    List<FoodMenu> findByMenuDay(MenuDay menuDay);

    Optional<FoodMenu> findByCategoryIgnoreCaseAndMenuDay(String category, MenuDay menuDay);

}
