package com.chs.cafeapp.menu.entity;

import static com.chs.cafeapp.exception.type.ErrorCode.CAN_NOT_ORDER_THAN_STOCK;

import com.chs.cafeapp.base.BaseEntity;
import com.chs.cafeapp.exception.CustomException;
import com.chs.cafeapp.menu.category.entity.Category;
import com.chs.cafeapp.menu.dto.MenuDto;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Menus extends BaseEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  private String name;
  private int kcal;
  private String description;
  private int stock;
  private int price;
  private boolean isSoldOut;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name="category_id")
  private Category category;

  public void setCategory(Category category) {
    this.category = category;
  }
  public static Menus toEntity(MenuDto menuDto) {
    return Menus.builder()
                  .id(menuDto.getId())
                  .name(menuDto.getName())
                  .kcal(menuDto.getKcal())
                  .description(menuDto.getDescription())
                  .stock(menuDto.getStock())
                  .price(menuDto.getPrice())
                  .isSoldOut(menuDto.isSoldOut())
                  .build();
  }

  public void addStock(int stockAmount) {
    this.stock += stockAmount;
  }

  public void minusStock(int stockAmount) {
    this.stock -= stockAmount;
    if (this.stock < 0) {
      this.stock += stockAmount;
      throw new CustomException(CAN_NOT_ORDER_THAN_STOCK);
    }
  }

  public void plusStockByCancel(int stockAmount) {
    this.stock += stockAmount;
  }
  public void setSoldOut(boolean isSoldOut) {
    this.isSoldOut = isSoldOut;
  }
}
