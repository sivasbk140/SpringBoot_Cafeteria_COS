package net.breezeware.SpringBootCafeteria.food.repo;

import net.breezeware.SpringBootCafeteria.food.entity.FoodItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for {@link FoodItem} entities.
 * <p>
 * Provides CRUD operations inherited from {@link JpaRepository} along with
 * custom queries for searching, filtering, and stock management.
 * </p>
 */
public interface FoodItemRepository extends JpaRepository<FoodItem, Long> {

    /**
     * Finds all food items with the exact given name (case-sensitive).
     *
     * @param name the exact name to search for
     * @return a list of matching food items
     */
    List<FoodItem> findByName(String name);

    /**
     * Finds a food item by name, ignoring case.
     *
     * @param name the name to search for (case-insensitive)
     * @return an {@link Optional} with the matching food item, or empty if not found
     */
    Optional<FoodItem> findByNameIgnoreCase(String name);

    /**
     * Finds all food items belonging to the given category.
     *
     * @param category the category string (e.g. "BREAKFAST", "MAIN_COURSE")
     * @return a list of food items in the specified category
     */
    List<FoodItem> findByCategory(String category);

    /**
     * Finds all food items that are currently in stock (quantity &gt; 0).
     *
     * @return a list of food items with available stock
     */
    @Query("SELECT f FROM FoodItem f WHERE f.quantity > 0")
    List<FoodItem> findAvailableItems();

    /**
     * Finds all food items whose stock is at or below the given threshold.
     * <p>Used for low-stock alerts by the admin.</p>
     *
     * @param threshold the maximum quantity to consider as low stock
     * @return a list of food items with quantity &lt;= threshold
     */
    @Query("SELECT f FROM FoodItem f WHERE f.quantity <= :threshold")
    List<FoodItem> findLowStock(@Param("threshold") int threshold);

    /**
     * Searches for food items whose name contains the given keyword, ignoring case.
     *
     * @param keyword the partial name keyword to search for
     * @return a list of matching food items
     */
    List<FoodItem> findByNameContainingIgnoreCase(String keyword);
}
