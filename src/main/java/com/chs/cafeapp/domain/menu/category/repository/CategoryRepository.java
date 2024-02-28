package com.chs.cafeapp.domain.menu.category.repository;

import com.chs.cafeapp.domain.menu.category.entity.Category;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

  boolean existsBySuperCategory(String superCategory);
  boolean existsByBaseCategory(String baseCategory);
  boolean existsBySuperCategoryAndBaseCategory(String superCategory, String baseCategory);
  Optional<Category> findCategoryBySuperCategoryAndBaseCategory(String superCategory, String baseCategory);
  Optional<Category> findBySuperCategoryAndBaseCategory(String superCategory, String baseCategory);
  List<Category> findAllBySuperCategory(String superCategory);
  List<Category> findAllByBaseCategory(String baseCategory);

  @Query(value = "SELECT id FROM category WHERE super_category = :superCategory", nativeQuery = true)
  List<Long> findIdsBySuperCategory(String superCategory);
  @Query(value = "SELECT id FROM category WHERE base_category = :baseCategory", nativeQuery = true)
  List<Long> findIdsByBaseCategory(String baseCategory);
  @Transactional
  void deleteById(Long categoryId);

}
