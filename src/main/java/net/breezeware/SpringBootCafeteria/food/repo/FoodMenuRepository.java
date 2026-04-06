package net.breezeware.SpringBootCafeteria.food.repo;

import net.breezeware.SpringBootCafeteria.food.entity.FoodMenu;
import net.breezeware.SpringBootCafeteria.food.enumeration.MenuDay;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

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

    /**
     * Finds a specific menu by its category and day, ignoring case for the category.
     *
     * @param category the meal category (e.g. "BREAKFAST", "LUNCH", "DINNER")
     * @param menuDay  the day of the week
     * @return an {@link Optional} with the matching menu, or empty if not found
     */
    Optional<FoodMenu> findByCategoryIgnoreCaseAndMenuDay(String category, MenuDay menuDay);

}
