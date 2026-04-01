package net.breezeware.SpringBootCafeteria.food.repo;

import net.breezeware.SpringBootCafeteria.food.entity.FoodMenu;
import net.breezeware.SpringBootCafeteria.food.enumeration.MenuDay;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FoodMenuRepository extends JpaRepository<FoodMenu, Long> {

    List<FoodMenu> findByMenuDay(MenuDay menuDay);

    Optional<FoodMenu> findByCategoryIgnoreCaseAndMenuDay(String category, MenuDay menuDay);

}
