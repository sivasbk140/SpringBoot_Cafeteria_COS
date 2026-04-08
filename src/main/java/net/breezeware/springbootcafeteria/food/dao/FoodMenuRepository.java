package net.breezeware.springbootcafeteria.food.dao;

import net.breezeware.springbootcafeteria.food.entity.FoodMenu;
import net.breezeware.springbootcafeteria.food.enumeration.MenuDay;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Spring Data JPA repository for {@link FoodMenu} entities.
 * <p>
 * Provides CRUD operations from {@link JpaRepository} and custom queries
 * for retrieving menus by day and category.
 * </p>
 */
public interface FoodMenuRepository extends JpaRepository<FoodMenu, Long> {

    /**
     * Finds all food menus scheduled for the given day of the week.
     *
     * @param menuDay the day to filter menus by (e.g. {@link MenuDay#MONDAY})
     * @return a list of menus (BREAKFAST, LUNCH, DINNER) for the specified day
     */
    List<FoodMenu> findByMenuDay(MenuDay menuDay);

}
