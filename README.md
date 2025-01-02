# Musinsa
# 무신사 상품 API

이 프로젝트는 Spring Boot, JPA, H2 데이터베이스를 사용하여 무신사 상품 데이터를 관리하는 RESTful API를 제공합니다. 이 API는 상품 정보의 생성, 수정 및 삭제를 지원합니다.
* h2 database musinsa table

* <img width="785" alt="h2db_table_musinsa" src="https://github.com/user-attachments/assets/d31e8ceb-9a41-4752-ac96-ec8384c16354" />

## 구현된 기능
### 구현 1) - 카테고리 별 최저가격 브랜드와 상품 가격, 총액을 조회하는 API
* 요청값 : 없음
* 응답값 : Frontend에서 값 변경없이 아래와 같은 화면을 출력하기 위한 JSON
* 
* 테스트 : Postman GET http://localhost:8080/api/musinsa/minprices

* <img width="1037" alt="구현1_postman" src="https://github.com/user-attachments/assets/cbae6b12-d797-48b3-9409-2b4cd5f2b928" />

### 구현 2) - 단일 브랜드로 모든 카테고리 상품을 구매할 때 최저가격에 판매하는 브랜드와 카테고리의 상품가격, 총액을 조회하는 API
* 테스트 : Postman GET http://localhost:8080/api/musinsa/minpricebrand

* <img width="1013" alt="구현2_postman" src="https://github.com/user-attachments/assets/c5025770-b01b-46e4-a76c-ae459d095353" />

### 구현 3) - 카테고리 이름으로 최저, 최고 가격 브랜드와 상품 가격을 조회하는 API
* 요청값 : 카테고리명 (String)
* 응답값 
* 테스트 : http://localhost:8080/api/musinsa/minMaxPrice/상의

* <img width="1016" alt="구현3_postman" src="https://github.com/user-attachments/assets/bcb1b589-1adc-49e5-a711-963eb2772b0b" />

### 구현 4) 브랜드 및 상품을 추가 / 업데이트 / 삭제하는 API
* 요청값- Request Body JSON
* 응답값
* 성공 혹은 실패 여부 JSON
* API 실패 시, 실패값과 실패 사유를 전달해야 합니다.

* 이 API는 다음 기능을 포함합니다.

* **무신사 상품에 대한 CRUD 작업:** 무신사 상품 레코드의 생성, 수정 및 삭제를 허용합니다. API 엔드포인트는 `/api/musinsa`입니다. 요청과 응답은 JSON 형식이며, 성공/실패 여부와 자세한 에러 메시지를 포함하는 상세한 에러 응답이 제공됩니다.
* **카테고리별 최소 및 최대 가격 정보 조회:** 지정된 카테고리의 최소 및 최대 가격과 해당 브랜드를 조회합니다. API 엔드포인트는 `/api/minMaxPrice/{category}`입니다.  category 매개변수는 "상의", "아우터", "바지", "스니커즈", "가방", "모자", "양말", "액세서리" 중 하나여야 합니다.


#### 상품생성
* 테스트 : Postman POST http://localhost:8080/api/musinsa

* <img width="990" alt="구현4_post" src="https://github.com/user-attachments/assets/dcb9729e-5b59-4c42-b833-ec9b0067b0f9" />

* 입력데이터 :
{
    "brand":"J", 
    "tops": 20123,
    "outer":30123 , 
    "pants": 40123, 
    "sneakers": 50123, 
    "bag":60123, 
    "cap":70123, 
    "socks":80123, 
    "accessory":90123
}

* 상품생성결과:


* <img width="468" alt="구현4_post_db" src="https://github.com/user-attachments/assets/4f3f23c6-66fa-4167-9364-5baffcb7b847" />



#### 상품업데이트
* 테스트 : Postman PUT http://localhost:8080/api/musinsa/10

* <img width="990" alt="구현4_post" src="https://github.com/user-attachments/assets/510484ae-0782-4e06-b910-325b99cbae42" />

* 상품업데이트 결과

* <img width="472" alt="구현4_put_update_db" src="https://github.com/user-attachments/assets/f3d9de8c-2a3a-4de6-a282-2674ea3dd9f0" />

#### 상품삭제
* 테스트 : Postman DELETE http://localhost:8080/api/musinsa/10

* <img width="1020" alt="구현4_del_post" src="https://github.com/user-attachments/assets/eabff303-752a-4ce7-a5a7-f18fb6578a71" />


* 상품삭제결과 : 

* <img width="467" alt="구현4_del_db" src="https://github.com/user-attachments/assets/af666c71-9dff-4635-bc86-6fc2939f82f2" />



## 포함되지 않은 기능 및 예외 처리

다음 기능은 이 API에 구현되지 않았습니다.

* **인증 및 권한 부여:** 인증 또는 권한 부여 메커니즘은 구현되지 않았습니다.
* **페이징 및 필터링:** 상품 데이터 조회에 대한 페이징 또는 필터링 옵션이 제공되지 않습니다.
* **입력 유효성 검사:** 강력한 입력 유효성 검사가 구현되지 않았습니다.
* **에러 핸들링:** 기본적인 에러 핸들링이 있지만, 다양한 시나리오에 대한 포괄적인 에러 핸들링은 프로덕션 환경 사용을 위해 필요합니다. 여기에는 자세한 로깅과 잠재적인 사용자 정의 예외 처리가 포함됩니다.
* 주어진 상품 카테고리 이외의 상품도 생성되도록 되어 있습니다.
* 상품 생성시 기본 8개 상품이 아닌 경우, 없는 상품은 null값이 들어갑니다. 이 상품이 입력되면 최소가격 등 다른 API에 영향을 줍니다(데이터가 null이며 0으로 처리하여 계산).
* 로그 로깅
* 모니터링
* CI/CD
* 인증 및 권한

## 코드 빌드, 테스트 및 실행

**사전 준비:**

* Java 17 이상
* Maven
* Postman (테스트용)
* Spring Tool Suite 4

**단계:**

1. 저장소 복제: `git clone <저장소_URL>`
2. 프로젝트 디렉토리로 이동: `cd musinsa-exam`
3. 프로젝트 클리: `mvn clean`
4. 프로젝트 빌드: `mvn clean install`
5. 애플리케이션 실행: `mvn spring-boot:run`
6. Postman을 사용하여 API 테스트 (아래 Postman 테스트 샘플 참조).

**Postman 테스트 샘플:**

(위 테스트 이미지 참고)

## 데이터베이스 설정

이 애플리케이션은 메모리 내 H2 데이터베이스를 사용합니다. 데이터베이스 스키마는 애플리케이션 시작 시 자동으로 생성됩니다. `application.properties` 파일의 `spring.jpa.hibernate.ddl-auto=update` 에서 스키마 설정을 확인할 수 있습니다. 프로덕션 환경에서는 이 설정을 `none` 또는 `validate`로 변경해야 합니다.

## 추가 정보

* 이 프로젝트는 Lombok을 사용하여 반복적인 코드를 줄였습니다. IDE에 Lombok 플러그인이 설치되어 있는지 확인하십시오.
* 이 프로젝트는 데이터베이스 상호 작용을 위해 Spring Data JPA를 사용합니다.
* 이 프로젝트는 JSON 처리를 위해 Jackson을 사용합니다.


## 향후 개발 계획

향후 개선 사항은 다음과 같습니다.

* 인증 및 권한 부여 구현
* 페이징 및 필터링 옵션 추가
* 더욱 강력한 입력 유효성 검사 구현
* 사용자 정의 예외 및 자세한 로깅을 통한 에러 핸들링 개선
* CSV 파일에서 데이터 로딩 추가

## **추가 front page 작성**
* vue.js로 1번 구현 문제에 대한 테이블 페이지를 생성하였습니다.
