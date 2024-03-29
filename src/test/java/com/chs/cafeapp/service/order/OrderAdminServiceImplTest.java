package com.chs.cafeapp.service.order;

import static com.chs.cafeapp.domain.order.type.OrderStatus.CancelByCafe;
import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.chs.cafeapp.auth.type.UserSex;
import com.chs.cafeapp.domain.cart.entity.Cart;
import com.chs.cafeapp.domain.cart.entity.CartMenu;
import com.chs.cafeapp.domain.cart.repository.CartMenusRepository;
import com.chs.cafeapp.domain.cart.repository.CartRepository;
import com.chs.cafeapp.domain.cart.service.impl.CartMenuServiceImpl;
import com.chs.cafeapp.domain.menu.category.entity.Category;
import com.chs.cafeapp.domain.menu.entity.Menus;
import com.chs.cafeapp.domain.menu.repository.MenuRepository;
import com.chs.cafeapp.domain.order.dto.OrderAllFromCartInput;
import com.chs.cafeapp.domain.order.dto.OrderDto;
import com.chs.cafeapp.domain.order.dto.OrderFromCartInput;
import com.chs.cafeapp.domain.order.dto.OrderInput;
import com.chs.cafeapp.domain.order.entity.Order;
import com.chs.cafeapp.domain.order.entity.OrderedMenu;
import com.chs.cafeapp.domain.order.repository.OrderRepository;
import com.chs.cafeapp.domain.order.repository.OrderedMenuRepository;
import com.chs.cafeapp.domain.order.service.impl.OrderAdminServiceImpl;
import com.chs.cafeapp.domain.order.service.impl.OrderMemberServiceImpl;
import com.chs.cafeapp.domain.order.service.validation.ValidationCheck;
import com.chs.cafeapp.domain.order.type.OrderStatus;
import com.chs.cafeapp.domain.stamp.service.impl.StampServiceImpl;
import com.chs.cafeapp.auth.member.entity.Member;
import com.chs.cafeapp.auth.member.repository.MemberRepository;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

@ExtendWith(MockitoExtension.class)
class OrderAdminServiceImplTest {
  @Mock
  private MemberRepository userRepository;
  @Mock
  private MenuRepository menuRepository;
  @Mock
  private CartRepository cartRepository;
  @Mock
  private OrderRepository orderRepository;
  @Mock
  private OrderedMenuRepository orderedMenuRepository;
  @Mock
  private CartMenusRepository cartMenusRepository;

  @InjectMocks
  private OrderAdminServiceImpl orderServiceForAdmin;

  @InjectMocks
  private OrderMemberServiceImpl orderServiceForUser;
  @Mock
  private StampServiceImpl stampService;
  @Mock
  private CartMenuServiceImpl cartMenuService;

  @Mock
  private ValidationCheck validationCheck;

  static Member user;
  static Menus menu1;
  static Menus menu2;
  static Menus menu3;
  static Category category;
  static OrderedMenu orderedMenu1;
  static OrderedMenu orderedMenu10;
  @BeforeEach
  public void setUp() {
    user = Member.builder()
        .id(4L)
        .loginId("user2@naver.com")
        .password("user2비밀번호")
        .name("user2 이름")
        .nickName("user2 닉네임")
        .sex(UserSex.MALE)
        .age(32)
        .build();

    menu1 = Menus.builder()
        .id(1L)
        .name("맛있는 닭가슴살 샌드위치")
        .kcal(500)
        .description("특제 비법 소스를 넣어 굉장히 맛있고 건강한 샌드위치 입니다.")
        .stock(10)
        .price(10000)
        .isSoldOut(false)
        .build();

    category = Category.builder()
        .superCategory("음료")
        .baseCategory("에스프레소")
        .build();

    menu2 = Menus.builder()
        .id(2L)
        .name("시원한 아이스 아메리카노")
        .kcal(8)
        .description("고소한 원두가 특징인 아이스 카페 아메리카노 입니다.")
        .stock(10)
        .price(4100)
        .isSoldOut(false)
        .category(category)
        .build();

    menu3 = Menus.builder()
        .id(3L)
        .name("따뜻한 핫 아메리카노")
        .kcal(8)
        .description("고소한 원두가 특징인 핫 카페 아메리카노 입니다.")
        .stock(10)
        .price(4100)
        .isSoldOut(false)
        .build();

    orderedMenu1 = OrderedMenu.builder()
        .userId(user.getLoginId())
        .quantity(3)
        .totalPrice(menu1.getPrice() * 3)
        .menus(menu1)
        .build();

    orderedMenu10 = OrderedMenu.builder()
        .userId(user.getLoginId())
        .quantity(3)
        .totalPrice(menu2.getPrice() * 3)
        .menus(menu2)
        .build();
  }

  @Test
  @Transactional
  @DisplayName("장바구니 전체 메뉴 주문 성공")
  void orderAllFromCart_Success() {
    // given
    CartMenu cartMenu1 = CartMenu.builder()
        .id(1L)
        .menus(menu1)
        .quantity(3)
        .build();

    CartMenu cartMenu2 = CartMenu.builder()
        .id(2L)
        .menus(menu2)
        .quantity(3)
        .build();

    Cart cart = Cart.builder()
        .id(1L)
        .cartMenu(Arrays.asList(cartMenu1, cartMenu2)) // Set cartMenu list
        .member(user)
        .build();

    Order order = Order.builder()
        .id(1L)
        .member(user)
        .couponUse(false)
        .build();


    cartMenu1.setCart(cart);
    cartMenu2.setCart(cart);
    user.setCart(cart);

    OrderedMenu orderedMenu1 = OrderedMenu.builder()
        .userId(cartMenu1.getCart().getMember().getLoginId())
        .quantity(cartMenu1.getQuantity())
        .totalPrice(cartMenu1.getQuantity() * cartMenu1.getMenus().getPrice())
        .menus(cartMenu1.getMenus())
        .order(order)
        .build();
    OrderedMenu orderedMenu2 = OrderedMenu.builder()
        .userId(cartMenu2.getCart().getMember().getLoginId())
        .quantity(cartMenu2.getQuantity())
        .totalPrice(cartMenu2.getQuantity() * cartMenu2.getMenus().getPrice())
        .menus(cartMenu2.getMenus())
        .order(order)
        .build();

    System.out.println(order.getOrderStatus());


    // orderedMenuRepository를 Mock 객체로 생성합니다.
    when(orderedMenuRepository.save(any(OrderedMenu.class)))
        .thenReturn(orderedMenu1) // 첫 번째 호출에 대한 반환 값
        .thenReturn(orderedMenu2); // 두 번째 호출에 대한 반환 값
    when(validationCheck.validationUser(anyString())).thenReturn(user);
    when(validationCheck.validationCart(anyLong())).thenReturn(cart);
    when(validationCheck.validationMenus(anyLong())).thenReturn(menu1).thenReturn(menu2);
    when(orderRepository.save(any())).thenReturn(order);

    // When
    OrderAllFromCartInput orderAllFromCartInput = new OrderAllFromCartInput(1L, false);
    OrderDto result = orderServiceForUser.orderAllFromCart(orderAllFromCartInput, "user2@naver.com");

    // Then
    verify(cartMenuService, times(1)).deleteAllCartMenu(1L, "user2@naver.com");
    assertNotNull(result);
    System.out.println(result);
    assertEquals(result.getOrderedMenus().size(), 2);
    assertEquals(menu1.getStock(), 10 - 3);
    assertEquals(menu2.getStock(), 10 - 3);
    assertEquals(order.getOrderStatus(), OrderStatus.PaySuccess);
    assertEquals(result.getTotalQuantity(), 6);
    assertEquals(result.getTotalPrice(), cartMenu1.getMenus().getPrice() * cartMenu1.getQuantity() + cartMenu2.getMenus().getPrice() * cartMenu2.getQuantity());

    // 해당 메서드 진행시 cart, cartMenu도 삭제된 것을 확인하고 싶으나 mock이 되어있어서 그대로 2가 반환됨.
//    assertEquals(cartRepository.findById(1L).get().getCartMenu().size(), 0);
//    assertEquals(cartMenusRepository.findAll().size() , 0);
  }

  @Test
  @Transactional
  @DisplayName("장바구니 선택 메뉴 주문 성공")
  void orderFromCart_Success() {
      //given

    CartMenu cartMenu1 = CartMenu.builder()
        .id(1L)
        .menus(menu1)
        .quantity(3)
        .build();

    CartMenu cartMenu2 = CartMenu.builder()
        .id(2L)
        .menus(menu2)
        .quantity(3)
        .build();

    CartMenu cartMenu3 = CartMenu.builder()
        .id(3L)
        .menus(menu3)
        .quantity(5)
        .build();

    Cart cart = Cart.builder()
        .id(1L)
        .cartMenu(Arrays.asList(cartMenu1, cartMenu2, cartMenu3))
        .member(user)
        .build();

    Order order = Order.builder()
        .id(1L)
        .member(user)
        .build();

    cartMenu1.setCart(cart);
    cartMenu2.setCart(cart);
    cartMenu3.setCart(cart);
    user.setCart(cart);


    when(orderRepository.save(any(Order.class))).thenReturn(order);


    OrderedMenu orderedMenu1 = OrderedMenu.builder()
        .userId(cartMenu1.getCart().getMember().getLoginId())
        .quantity(cartMenu1.getQuantity())
        .totalPrice(cartMenu1.getQuantity() * cartMenu1.getMenus().getPrice())
        .menus(cartMenu1.getMenus())
        .order(order)
        .build();

    OrderedMenu orderedMenu2 = OrderedMenu.builder()
        .userId(cartMenu3.getCart().getMember().getLoginId())
        .quantity(cartMenu3.getQuantity())
        .totalPrice(cartMenu3.getQuantity() * cartMenu3.getMenus().getPrice())
        .menus(cartMenu3.getMenus())
        .order(order)
        .build();

    System.out.println(order.getOrderStatus());

    when(orderedMenuRepository.save(any(OrderedMenu.class)))
        .thenReturn(orderedMenu1)
        .thenReturn(orderedMenu2);

    when(validationCheck.validationUser(anyString())).thenReturn(user);
    when(validationCheck.validationCart(anyLong())).thenReturn(cart);
    when(validationCheck.validationCartMenu(anyLong())).thenReturn(cartMenu1).thenReturn(cartMenu3);
    when(validationCheck.validationMenus(anyLong())).thenReturn(menu1).thenReturn(menu3);
    // when
    OrderFromCartInput orderFromCartInput = new OrderFromCartInput(1L, Arrays.asList(1L, 3L), false);
    OrderDto result = orderServiceForUser.orderFromCart(orderFromCartInput, "user2@naver.com");

    // then
    verify(cartMenuService, times(1)).deleteSpecificCartMenu(1L, 1L, "user2@naver.com");
    verify(cartMenuService, times(1)).deleteSpecificCartMenu(1L, 3L, "user2@naver.com");
    assertNotNull(result);
    System.out.println(result);
    assertEquals(result.getOrderedMenus().size(), 2);
    assertEquals(menu1.getStock(), 10 - 3);
    assertEquals(menu2.getStock(), 10);
    assertEquals(menu3.getStock(), 10 - 5);
    assertEquals(order.getOrderStatus(), OrderStatus.PaySuccess);
    assertEquals(result.getTotalQuantity(), 8);
    assertEquals(result.getTotalPrice(), cartMenu1.getMenus().getPrice() * cartMenu1.getQuantity()
                                                +cartMenu3.getMenus().getPrice() * cartMenu3.getQuantity());
  }

  @Test
  @Transactional
  @DisplayName("개별 메뉴 주문 성공")
  void orderIndividualMenu_Success() {
    //given
    Order order = Order.builder()
        .id(1L)
        .member(user)
        .orderStatus(OrderStatus.PaySuccess)
        .build();

    orderedMenu10.setOrder(order);
    order.setOrderedMenus(orderedMenu10);
    order.setTotalQuantity(Arrays.asList(orderedMenu10));
    order.setTotalPrice(Arrays.asList(orderedMenu10));

    orderRepository.save(order);
    orderedMenuRepository.save(orderedMenu10);
    when(orderRepository.save(any(Order.class))).thenReturn(order);
    when(orderedMenuRepository.save(any(OrderedMenu.class))).thenReturn(orderedMenu10);
    when(validationCheck.validationUser(any())).thenReturn(user);
    when(validationCheck.validationMenus(anyLong())).thenReturn(menu2);

    // when
    OrderInput orderInput = new OrderInput(1L, "맛있는 닭가슴살 샌드위치", 10000, 3, false);
    OrderDto result = orderServiceForUser.orderIndividualMenu(orderInput, "user2@naver.com");

    // then
    assertNotNull(result);
    System.out.println(result);
    assertEquals(result.getOrderedMenus().size(), 1);
    assertEquals(result.getId(), 1L);
    assertEquals(menu2.getStock(), 10 - 3);
    assertEquals(menu1.getStock(), 10);
    assertEquals(order.getOrderStatus(), OrderStatus.PaySuccess);
    assertEquals(result.getTotalQuantity(), 3);
    assertEquals(result.getTotalPrice(), menu2.getPrice() * orderInput.getQuantity());

  }
  
  @Test
  @DisplayName("주문 거절 성공")
  void rejectOrderTest_Success() {
    //given
    Order order = Order.builder()
        .id(1L)
        .member(user)
        .orderStatus(OrderStatus.PaySuccess)
        .build();

    orderRepository.save(order);
    when(orderRepository.save(any(Order.class))).thenReturn(order);
    when(validationCheck.validationOrder(anyLong())).thenReturn(order);
    //when
    OrderDto orderDto = orderServiceForAdmin.rejectOrder(1L);
    //then
    assertNotNull(orderDto);
    assertEquals(orderDto.getOrderStatus(), CancelByCafe);
  }

  @Test
  @DisplayName("주문 상태 변경 성공: PaySuccess -> PreParingMenus")
  void changeOrderStatusTest_Success1() {
    //given
    Member user = Member.builder()
        .id(1L)
        .loginId("usertest1@naver.com")
        .build();

    Order order = Order.builder()
        .id(1L)
        .member(user)
        .orderStatus(OrderStatus.PaySuccess)
        .build();

    orderRepository.save(order);
    when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
    when(orderRepository.save(any(Order.class))).thenReturn(order);
    //when
    OrderDto orderDto = orderServiceForAdmin.changeOrderStatus(1L);
    //then
    assertNotNull(orderDto);
    assertEquals(orderDto.getOrderStatus(), OrderStatus.PreParingMenus);
  }

  @Test
  @DisplayName("주문 상태 변경 성공: PreParingMenus -> WaitingPickUp")
  void changeOrderStatusTest_Success2() {
    //given
    Member user = Member.builder()
        .id(1L)
        .loginId("usertest1@naver.com")
        .build();

    Order order = Order.builder()
        .id(1L)
        .member(user)
        .orderStatus(OrderStatus.PreParingMenus)
        .build();

    orderRepository.save(order);
    when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
    when(orderRepository.save(any(Order.class))).thenReturn(order);
    //when
    OrderDto orderDto = orderServiceForAdmin.changeOrderStatus(1L);
    //then
    assertNotNull(orderDto);
    assertEquals(orderDto.getOrderStatus(), OrderStatus.WaitingPickUp);
  }

  @Test
  @DisplayName("주문 상태 변경 성공: WaitingPickUp -> PickUpSuccess")
  void changeOrderStatusTest_Success3() {
    //given
    Order order = Order.builder()
        .id(1L)
        .member(user)
        .orderStatus(OrderStatus.WaitingPickUp)
        .orderedMenus(Arrays.asList(orderedMenu10))
        .build();

    orderedMenu10.setOrder(order);
    orderRepository.save(order);

    when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
    when(orderRepository.save(any(Order.class))).thenReturn(order);
    //when
    OrderDto orderDto = orderServiceForAdmin.changeOrderStatus(1L);
    //then
    verify(stampService, times(1)).addStampNumbers(orderedMenu10.getQuantity(), "user2@naver.com");
    assertNotNull(orderDto);
    assertEquals(orderDto.getOrderStatus(), OrderStatus.PickUpSuccess);
  }
  @Test
  @DisplayName("주문 상태 변경 실패")
  void changeOrderStatusTest_Fail() {
    //given
    Order order = Order.builder()
        .id(1L)
        .member(user)
        .orderStatus(OrderStatus.PayFail)
        .build();

    orderRepository.save(order);
    when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
    //when

    //then
    assertThrows(IllegalStateException.class, () -> orderServiceForAdmin.changeOrderStatus(1L));

  }

  @DisplayName("PayFail만 조회")
  void viewOrdersByOrderStatus_PayFail() {
    //given
    Menus menus = Menus.builder()
        .id(1L)
        .build();

    Order order1 = Order.builder()
        .id(1L)
        .member(user)
        .orderStatus(OrderStatus.PayFail)
        .build();

    OrderedMenu orderedMenu1 = OrderedMenu.builder()
        .quantity(3)
        .totalPrice(1000 * 3)
        .order(order1)  // Order와의 관계 설정
        .menus(menus)
        .build();

    Order order2 = Order.builder()
        .id(2L)
        .member(user)
        .orderStatus(OrderStatus.PaySuccess)
        .build();

    OrderedMenu orderedMenu2 = OrderedMenu.builder()
        .quantity(4)
        .totalPrice(2000 * 4)
        .order(order2)  // Order와의 관계 설정
        .menus(menus)
        .build();

    order1.getOrderedMenus().add(orderedMenu1);
    order2.getOrderedMenus().add(orderedMenu2);
    orderRepository.save(order1);
    orderRepository.save(order2);
    orderedMenuRepository.save(orderedMenu1);
    orderedMenuRepository.save(orderedMenu2);

    orderRepository.save(order1);
    orderRepository.save(order2);
    orderedMenuRepository.save(orderedMenu1);
    orderedMenuRepository.save(orderedMenu2);

    Pageable pageable = PageRequest.of(0, 4, Sort.unsorted());
    when(orderRepository.findAllByOrderStatus(any(OrderStatus.class), any(Pageable.class))).thenReturn(new SliceImpl<>(Arrays.asList(order1), pageable, false));

    //when
    Slice<OrderDto> orderDtos = orderServiceForAdmin.viewOrdersByOrderStatus(1, pageable);
    List<OrderDto> orderDtoList = orderDtos.getContent();
    //then
    assertEquals(1, orderDtoList.size());
    assertEquals(3, orderDtoList.get(0).getOrderedMenus().get(0).getQuantity());
  }

  @Test
  @DisplayName("PaySuccess만 조회")
  void viewOrdersByOrderStatus_PaySuccess() {
    //given
    Member user = Member.builder()
        .loginId("user2@naver.com")
        .build();

    Menus menus = Menus.builder()
        .id(1L)
        .build();

    Order order1 = Order.builder()
        .id(1L)
        .member(user)
        .orderStatus(OrderStatus.PayFail)
        .build();

    OrderedMenu orderedMenu1 = OrderedMenu.builder()
        .quantity(3)
        .totalPrice(1000 * 3)
        .order(order1)  // Order와의 관계 설정
        .menus(menus)
        .build();

    Order order2 = Order.builder()
        .id(2L)
        .member(user)
        .orderStatus(OrderStatus.PaySuccess)
        .build();

    OrderedMenu orderedMenu2 = OrderedMenu.builder()
        .quantity(4)
        .totalPrice(2000 * 4)
        .order(order2)  // Order와의 관계 설정
        .menus(menus)
        .build();

    order1.getOrderedMenus().add(orderedMenu1);
    order2.getOrderedMenus().add(orderedMenu2);
    orderRepository.save(order1);
    orderRepository.save(order2);
    orderedMenuRepository.save(orderedMenu1);
    orderedMenuRepository.save(orderedMenu2);

    Pageable pageable = PageRequest.of(0, 4, Sort.unsorted());
    when(orderRepository.findAllByOrderStatus(any(OrderStatus.class), any(Pageable.class))).thenReturn(new SliceImpl<>(Arrays.asList(order2), pageable, false));

    //when
    Slice<OrderDto> orderDtos = orderServiceForAdmin.viewOrdersByOrderStatus(2, pageable);
    List<OrderDto> orderDtoList = orderDtos.getContent();
    //then
    System.out.println(orderDtos);
    assertEquals(orderDtoList.size(), 1);
    assertEquals(orderDtoList.get(0).getOrderedMenus().get(0).getQuantity(), 4);
  }

  @Test
  @DisplayName("CancelByCafe만 조회")
  void viewOrdersByOrderStatus_CancelByCafe() {
    //given
    Menus menus = Menus.builder()
        .id(1L)
        .build();

    Order order1 = Order.builder()
        .id(1L)
        .member(user)
        .orderStatus(CancelByCafe)
        .build();

    OrderedMenu orderedMenu1 = OrderedMenu.builder()
        .quantity(3)
        .totalPrice(1000 * 3)
        .order(order1)  // Order와의 관계 설정
        .menus(menus)
        .build();

    Order order2 = Order.builder()
        .id(2L)
        .member(user)
        .orderStatus(CancelByCafe)
        .build();

    OrderedMenu orderedMenu2 = OrderedMenu.builder()
        .quantity(4)
        .totalPrice(2000 * 4)
        .order(order2)  // Order와의 관계 설정
        .menus(menus)
        .build();

    order1.getOrderedMenus().add(orderedMenu1);
    order2.getOrderedMenus().add(orderedMenu2);
    orderRepository.save(order1);
    orderRepository.save(order2);
    orderedMenuRepository.save(orderedMenu1);
    orderedMenuRepository.save(orderedMenu2);

    Pageable pageable = PageRequest.of(0, 4, Sort.unsorted());
    when(orderRepository.findAllByOrderStatus(any(OrderStatus.class), any(Pageable.class))).thenReturn(new SliceImpl<>(Arrays.asList(order1, order2), pageable, false));

    //when
    Slice<OrderDto> orderDtos = orderServiceForAdmin.viewOrdersByOrderStatus(3, pageable);
    List<OrderDto> orderDtoList = orderDtos.getContent();
    //then
    assertEquals(orderDtoList.size(), 2);
    assertEquals(orderDtoList.get(0).getOrderedMenus().size(), 1);
  }

  @Test
  @DisplayName("CancelByUser만 조회")
  void viewOrdersByOrderStatus_CancelByUser() {
    //given
    Member user = Member.builder()
        .loginId("user2@naver.com")
        .build();

    Menus menus = Menus.builder()
        .id(1L)
        .build();

    Menus menus2 = Menus.builder()
        .id(2L)
        .build();

    Order order1 = Order.builder()
        .id(1L)
        .member(user)
        .orderStatus(OrderStatus.CancelByUser)
        .build();

    OrderedMenu orderedMenu1 = OrderedMenu.builder()
        .quantity(3)
        .totalPrice(1000 * 3)
        .order(order1)  // Order와의 관계 설정
        .menus(menus)
        .build();

    Order order2 = Order.builder()
        .id(2L)
        .member(user)
        .orderStatus(OrderStatus.PaySuccess)
        .build();

    OrderedMenu orderedMenu2 = OrderedMenu.builder()
        .quantity(4)
        .totalPrice(2000 * 4)
        .order(order1)  // Order와의 관계 설정
        .menus(menus2)
        .build();

    OrderedMenu orderedMenu3 = OrderedMenu.builder()
        .quantity(1)
        .totalPrice(2000 * 1)
        .order(order2)  // Order와의 관계 설정
        .menus(menus)
        .build();

    order1.getOrderedMenus().add(orderedMenu1);
    order1.getOrderedMenus().add(orderedMenu2);
    order2.getOrderedMenus().add(orderedMenu3);
    orderRepository.save(order1);
    orderRepository.save(order2);
    orderedMenuRepository.save(orderedMenu1);
    orderedMenuRepository.save(orderedMenu2);

    Pageable pageable = PageRequest.of(0, 4, Sort.unsorted());
    when(orderRepository.findAllByOrderStatus(any(OrderStatus.class), any(Pageable.class))).thenReturn(new SliceImpl<>(Arrays.asList(order1), pageable, false));

    //when
    Slice<OrderDto> orderDtos = orderServiceForAdmin.viewOrdersByOrderStatus(4, pageable);
    List<OrderDto> orderDtoList = orderDtos.getContent();
    //then
    assertEquals(orderDtoList.size(), 1);
    assertEquals(orderDtoList.get(0).getOrderedMenus().size(), 2);
  }

  // TODO: 앞선 테스트 코드와 동일한 형식, 지울지 그래도 둬야하는지 일단 keep
//  @Test
//  @DisplayName("PreParingMenus만 조회")
//  void viewOrdersByOrderStatus_PreParingMenus() {
//    //given
//
//    //when
//    //then
//  }
//
//  @Test
//  @DisplayName("WaitingPickUp만 조회")
//  void viewOrdersByOrderStatus_WaitingPickUp() {
//    //given
//
//    //when
//    //then
//  }
//
//  @Test
//  @DisplayName("PickUpSuccess만 조회")
//  void viewOrdersByOrderStatus_PickUpSuccess() {
//    //given
//
//    //when
//    //then
//  }
}