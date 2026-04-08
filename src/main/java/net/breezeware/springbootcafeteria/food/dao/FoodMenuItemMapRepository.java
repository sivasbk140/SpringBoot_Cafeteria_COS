package net.breezeware.springbootcafeteria.food.dao;

import net.breezeware.springbootcafeteria.food.entity.FoodMenuItemMap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FoodMenuItemMapRepository extends JpaRepository<FoodMenuItemMap, Long> {

}