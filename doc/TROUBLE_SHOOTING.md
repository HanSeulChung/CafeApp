# Trouble Shooting
프로젝트를 진행하면서 발생한 문제점들과 해결법입니다.

### 1. BaseEntity 상속받은 Entity가 수정이 될때 CreateDt null이 되는 오류
<h3>해결 방법 </h3>
<h4>update=false로 해결되었습니다.</h4>

![image](https://github.com/HanSeulChung/CafeApp/assets/94779505/1be00d94-af62-44ba-818f-95111128029b)
<hr>

### 2. PR 2만 Merge full request했을 때 PR 3도 merge 되어 닫힌 경우 발생
<img src="https://github.com/HanSeulChung/CafeApp/assets/94779505/d133fa2b-6152-4595-9fb5-fb4d0a14991a" width="600" height="220">
<br>
<img src="https://github.com/HanSeulChung/CafeApp/assets/94779505/3cf52552-37d1-4d2d-b6cc-1c1a7a91ed78" width="700" height="400">
<h3>문제 원인</h3>
<h4>PR3의 브랜치가 PR2 브랜치에서 분기된 브랜치였고, PR2의 브랜치가 PR3의 히스토리를 포함했기 때문이었다.</h4>
<h3>문제 방지 해결책</h3>
<h4>PR할때 기능은 최대한 세부화할 것. ex> 메뉴주문 x -> 단일 메뉴 주문, 여러 상품 메뉴 주문</h4>
<h4>브랜치 간섭을 최소화 하기 위해서 git flow를 참고해 브랜치 전략을 세웠다.
main, dev, feature/기능, test/feature-test와 같이 두고 dev를 각 기능 브랜치 분기점 포인트로 잡고 기능 개발 후 테스트까지 완료 한 뒤 dev 브랜치에 병합하고 마지막으로 dev -> main으로 병합할것이다.</h4>
<hr>

### 3. gitignore 잘 생각하기. log.xml을 업로드 한 뒤 생성되는 log들은 gitignore에 넣어 merge시 쓸데없는 충돌이 되지 않도록 미연에 방지하기
![image](https://github.com/HanSeulChung/CafeApp/assets/94779505/9bbd55d1-be11-431e-80af-29038698c9f6)
<hr>

### 4. DataJpaTest로 JPA Repository 테스트 진행시 null값이 나오면 안되는 것에서 null값 반환으로 테스트 진행불가
<h3>문제 상황</h3>
<h4>entity.CartAndCartMenuTest.java</h4>

```java
  // given
    .. 적당한 given

  // when
  List<CartMenu> cartMenus = cartMenusRepository.findAllByCartId(1L);
    for (CartMenu cartMenu : cartMenus) {
      cartMenu.setCart(null); // Cart 참조 제거
      cartMenusRepository.save(cartMenu); // 변경 사항 저장
    }

    cartMenusRepository.deleteAllByCartId(1L); // CartMenu 삭제
    // then
    assertEquals(cartRepository.findById(1L).get().getCartMenu().size(), 0);
```

로 cartMenu를 참조 제거한뒤 삭제하면 해당 연결되었던 cart의 cartMenuList가 null이 아닌 0이여야한다.

(Cart에서는 고아 객체 자동삭제로 셋팅해놨기 때문에 )

기존에 연결되었다가 삭제된 것이기 때문에 null이 되면 안된다.

그러나
```assertEquals(cartRepository.findById(1L).get().getCartMenu().size(), 0);``` 에서 0이 아닌 null값이 나와 통과할 수 가 없었다.


<h3>해결 방법</h3>
<h4>영속성 컨텍스트와 캐시, EntityManager em.clear()로 해결</h4>

```java
  @Autowired
  private EntityManager em;
  
...
    //when
    List<CartMenu> cartMenus = cartMenusRepository.findAllByCartId(1L);
    for (CartMenu cartMenu : cartMenus) {
      cartMenu.setCart(null); // Cart 참조 제거
      cartMenusRepository.save(cartMenu); // 변경 사항 저장
    }

    cartMenusRepository.deleteAllByCartId(1L); // CartMenu 삭제
    em.clear();

    //then
    assertEquals(cartRepository.findById(1L).get().getCartMenu().size(), 0);
 ```

### 5. 원격 저장소에 올라가있는 커밋일 경우 rebase merge 주의

![image](https://github.com/HanSeulChung/CafeApp/assets/94779505/64324f22-dafc-4abe-b183-1a5885d223ec)
<h3>문제 원인</h3>
<h4>dev 브랜치에서 main 브랜치로 PR요청 후 rebase merge를 했을 때 해시값이 바뀐채로 rebase가 되기 때문에 같은 커밋 이력이라고 해도 해시값이 달라져 다른 커밋이라고 인식해서 중복 커밋이 발생하게 되었다. rebase로 깃 브랜치 이력이 깔끔해지는건 맞으나 보통 원격 저장소에 push 되어있는것은 함부로 rebase하지 말것. 협업시에는 병합도 조심히 하자. 더러워 지더라도 안전하고 만만한 일반 merge를 할 것..</h4>

### 6. 사용자에 대한 엔티티를 User 보다는 Member로 둘 것
![image](https://github.com/HanSeulChung/CafeApp/assets/94779505/f8357d9d-c54d-42c3-afde-cc0b8a0a4a26)

Security를 진행할때 UserDetails의 User와 변수명이 같아 import하기도 불편하고 가독성이 떨어진다. 추후 다른 프로젝트를 진행할땐 member 등으로 엔티티명 교체할 것

### 7. 주문시 동시성 제어와 캐싱으로 성능을 높일 때 데이터의 일관성 문제
<h3>주문 시 동시성 제어 문제</h3>
단일 서버로 생각해야할지, <br>
분산 서버로 생각해두고 해야할지 감이 안온다.<br>
단일 서버로 하면 멀티 쓰레드에 안전한 클래스를 통해 구현하는 것이 오버헤드가 적을것이며, <br>
실무에서는 단일 서버보다 분산 서버로 더 많은 사용량에 대비하는 것이 더 많이 쓰일 것 같아 분산 서버에 관련해서 생각하려고 하는데 뭐가 맞는지 모르겠다.

<h3>캐싱으로 성능을 높일 때 주문 시 메뉴의 데이터 일관성 문제(해당 메뉴의 재고가 바로 바로 바뀔때 바로 적용이 되어야한다.)</h3>
캐싱시 global cache로 redis를 선택하는 이유가 데이터의 일관성 때문.
