# 카페 메뉴 스마트 주문 서비스 : CafeApp
해당 카페의 메뉴(음식, 음료, MD 굿즈)를 미리 결제하고 주문하는 서비스 입니다.

## Tech Stack
<div align=center>
  <img src="https://img.shields.io/badge/IntelliJ_IDEA-000000?style=for-the-badge&logo=intellij-idea&logoColor=white">
  <img src="https://img.shields.io/badge/java-007396?style=for-the-badge&logo=java&logoColor=white">
  <img src="https://img.shields.io/badge/JDK-Oracle_Open_JDK-007396?style=for-the-badge&logo=mariaDB&logoColor=white"> 
  <img src="https://img.shields.io/badge/gradle-02303A?style=for-the-badge&logo=gradle&logoColor=white">
  </br>
  <img src="https://img.shields.io/badge/spring-6DB33F?style=for-the-badge&logo=spring&logoColor=white"> 
  <img src="https://img.shields.io/badge/Spring_Boot-6DB33F?style=for-the-badge&logo=spring&logoColor=white">
  <img src="https://img.shields.io/badge/Spring_Security-6DB33F?style=for-the-badge&logo=spring-security&logoColor=white">
  <img src="https://img.shields.io/badge/Spring_Data_JPA-6DB33?style=for-the-badge&logo=mariaDB&logoColor=white"> 
  </br>
   <img src="https://img.shields.io/badge/mariaDB-003545?style=for-the-badge&logo=mariaDB&logoColor=white">
  <img src="https://img.shields.io/badge/Json_Web_Tokens-000000?style=for-the-badge&logo=json-web-tokens&logoColor=white"> 
  <img src="https://img.shields.io/badge/Lombok-BC4520?style=for-the-badge&logo=lombok&logoColor=white"> 
  </br>
  <img src="https://img.shields.io/badge/github-181717?style=for-the-badge&logo=github&logoColor=white">
  <img src="https://img.shields.io/badge/git-F05032?style=for-the-badge&logo=git&logoColor=white">
</div>

## 프로젝트 기능 설계
* ### **공통**
  * [ ] 회원가입 기능
  * [ ] 소셜 회원가입 기능
  * [ ] 로그인 기능
  * [ ] 소셜 로그인 기능
* ### **카페 관계자(admin)**
  * 카테고리 
    * 상품 카테고리 등록
    * 상품 카테고리 수정
    * 상품 카테고리 삭제
    * 카테고리 전체 조회
    * 카테고리 대분류(superCategory)로 조회
  * 메뉴
    * 메뉴 등록
    * 메뉴 수정
    * 메뉴 삭제
    * 메뉴 수량 변경
    * 메뉴 상태 품절으로 변경
    * 메뉴 상태 판매중으로 변경
    * 메뉴 전체 조회
    * 메뉴 대분류 카테고리(superCategory)로 조회
    * 메뉴 중분류 카테고리(baseCategory)로 조회
  * 주문
    * 주문 조회<br>
    ➡️ 전체 주문 조회 가능 (단, 최대 1년 이내만 조회 가능)<br>
    ➡️ 기간별로 주문 조회 가능(하루, 1주일, 1달, 3개월, 6개월, 1년)<br>
    ➡️ 주문 상태별로 조회<br>
    ➡️ 기간 + 주문 상태별로 조회(하루, 1주일, 1달, 3개월, 6개월, 1년)<br>
    * 주문 거절
    * 주문 상태 변경
    * 주문 상태 변경시 마지막 PickUpSuccess(픽업 완료)시 주문자의 주문한 메뉴 중 음료의 잔수 대로 스탬프 기능
*  ### **카페 사용자(user)**
   * 메뉴
     * 메뉴 전체 조회
     * 메뉴 대분류 카테고리(superCategory)로 조회
     * 메뉴 중분류 카테고리(baseCategory)로 조회
     * 메뉴 조회에서 메뉴 자세히 보기<br>
       ➡️ MenuDetail에서 menu id, name, .. 을 전달하여 개별 메뉴 주문으로 주문 가능
   * 주문
   * [ ] 주문은 가게에서 2km 이내에서만 주문 가능
     * 메뉴 상세페이지에서 바로 주문(개별 메뉴 주문)
     * 장바구니에 담긴 장바구니 메뉴들 전체 주문
     * 장바구니에서 선택한 장바구니 메뉴들 주문
     * 결제 취소<br>
      ➡️ 주문 상태가 결제 성공 -> 메뉴 준비 중 일 경우 결제 취소 안됨
     * 주문 조회<br>
      ➡️ 전체 주문 조회 가능 (단, 최대 1년 이내만 조회 가능)<br>
      ➡️ 기간별로 주문 조회 가능(하루, 1주일, 1달, 3개월, 6개월, 1년)<br>
      사용자는 주문 상태별로 조회 불가능(기간별로 조회시 마지막 상태가 해당 사용자의 주문 상태)
     * [ ] 카페에서 주문 상태 변경시 알람
   * 장바구니
     * 장바구니에 담기<br>
       ➡️ 메뉴를 보다 `장바구니에 담기` 진행 시 이미 장바구니에 담겨있는 메뉴이면 수량이 추가됨
     * 장바구니에 담긴 장바구니 메뉴 수량 변경<br>
       ➡️ 수량 변경은 장바구니에 담겨있는 수량을 0으로 바꿀수는 없음. 
     * 장바구니 조회
     * 장바구니 전체 삭제
     * 장바구니 특정 메뉴만 삭제
   * 스탬프
     * 스탬프 조회
     * 스탬프 갯수가 10개가 될 경우 쿠폰 자동 변환되며 다시 스탬프는 비워짐
   * 쿠폰
     * 결제 시 쿠폰 사용 가능
     * 쿠폰 전체 조회
     * 사용 가능한 쿠폰 조회
     * 사용 불가능한 쿠폰 조회(기간 만료, 사용 완료 동시 조회)

*TODO*
* [ ] 주문시 실제 주문의 순서 고려
* [ ] 주문 트랜잭션 lock, rollback 고려
* [ ] JPA의 N+1 문제
* [ ] 쿼리의 실행 갯수 고려
* [ ] 주문 상태 변경시 pub-sub 구조로 알림 기능을 보낼지, 상태 조회 서버를 별도 구축할 지 고려
* [ ] 사용자의 위치를 어떻게 받을 지
* [ ] 조회시 List로 받고있는것 Paging, Paging 시에도 쿼리 limit 구문 고려



## ERD
11/01 ERD 수정 (이후 수정 있을 수도 있음)
![image](https://github.com/HanSeulChung/CafeApp/assets/94779505/c0cd93f2-345f-43e4-9e03-70ccd2aeefa9)

<details>
<summary><h4>이전 ERD 셋팅</h4></summary>
  <div markdown=1>
   
   10/26 ERD 수정 (이후 수정 있을 수도 있음)
![image](https://github.com/HanSeulChung/CafeApp/assets/94779505/f17c72f3-18ca-409e-ac65-83b506fe70b0)
   10/19 초기 **변경됨**
   
![image](https://github.com/HanSeulChung/CafeApp/assets/94779505/a2ba85b6-3a56-472d-8805-eb29615fced6)
  </div>
</details>

## 

### Trouble Shooting
[troble shooting 보러가기](https://github.com/HanSeulChung/CafeApp/blob/main/doc/TROUBLE_SHOOTING.md)
