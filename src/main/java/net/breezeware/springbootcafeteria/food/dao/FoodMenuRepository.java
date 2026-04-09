package net.breezeware.springbootcafeteria.food.dao;

import net.breezeware.springbootcafeteria.food.entity.FoodMenu;
import net.breezeware.springbootcafeteria.food.enumeration.MenuDay;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FoodMenuRepository extends JpaRepository<FoodMenu, Long> {

    List<FoodMenu> findByMenuDay(MenuDay menuDay);

}
