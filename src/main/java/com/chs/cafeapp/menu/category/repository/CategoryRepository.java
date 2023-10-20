package com.chs.cafeapp.menu.category.repository;

import com.chs.cafeapp.menu.category.entity.Category;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
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

  @Transactional
  void deleteById(Long categoryId);

}
