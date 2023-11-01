# 카페 메뉴 스마트 주문 서비스 : CafeApp
해당 카페의 메뉴(음식, 음료, MD 굿즈)를 미리 결제하고 주문하는 서비스 입니다.

## 프로젝트 기능 설계
* 회원가입 기능
* 로그인 기능
* 카페 메뉴 장바구니 기능
* 카페 메뉴 결제(주문) 기능
  * 사용자의 위치가 가게의 위치와 반경 10KM이내만 결제(주문) 가능
* 스탬프 적립 기능
  * 결제할 경우 음료 잔수에 맞게 스탬프 적립 기능
  * 스탬프가 10개 모두 적립되었을 경우 쿠폰 1장으로 전환 기능

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

### Trouble Shooting
[troble shooting 보러가기](https://github.com/HanSeulChung/CafeApp/blob/main/doc/TROUBLE_SHOOTING.md)
