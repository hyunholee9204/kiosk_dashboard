# KIOSK 실시간 매출 분석 대시보드 (End-to-End)

> **대용량 트랜잭션 데이터 적재부터 복잡한 통계 쿼리 연산, Spring Boot JPA REST API 서빙, 그리고 Chart.js 시각화까지 관통하는 풀스택 데이터 파이프라인 구축 프로젝트입니다.**

<br>

##  1. 프로젝트 개요
- **목적**: 키오스크에서 발생하는 실시간 주문 데이터를 기반으로 점주가 매장 현황을 직관적으로 파악할 수 있는 고성능 대시보드 시스템 구축
- **핵심 도전 과제**: 
  - 50만 건 이상의 대용량 가상 주문 데이터를 적재하여 실 상용 서비스 환경 시뮬레이션
  - 대수의 법칙(평균의 함정)을 피하기 위해 지점별/월별 가중치(확률 패턴)를 부여한 리얼리스틱 데이터 가공
  - 데이터 대량 적재 상황에서 차트 스케일링(Y축 최적화)을 통한 시각적 가독성 극대화

<br>

##  2. Tech Stack
- **Backend**: Java 17, Spring Boot, Spring Data JPA
- **Database**: PostgreSQL
- **Frontend**: HTML5, Bootstrap 5, Chart.js

<br>

##  3. 핵심 아키텍처 & 파이프라인
데이터가 흐르는 전체 레이어를 직접 설계하고 구현했습니다.

[PostgreSQL (50만 건)] ➡️ [JPA Native Query / Interface DTO] ➡️ [Spring REST Controller] ➡️ [Chart.js (Front)]

<br>

## 💡 4. 주요 기능 및 핵심 소스코드

### 1) 가중치 기반 대용량 데이터 적재 (PL/pgSQL)
단순한 무작위 데이터가 아닌 **"강남점 매출 집중", "5~6월 우상향 트렌드"** 등 실제 비즈니스 시나리오 가중치를 적용하여 50만 건의 트랜잭션을 디비 엔진 단에서 고속 벌크 적재했습니다.

```sql
-- 지점별/월별 빈부격차 가중치를 부여한 50만 건 트랜잭션 생성 스크립트 일부
FOR i IN 1..500000 LOOP
    v_rand := random();
    
    -- 강남점(1번) 50%, 홍대점(2번) 35%, 판교점(3번) 15% 가중치 부여
    IF v_rand < 0.50 THEN v_store_id := 1; 
    ELSIF v_rand < 0.85 THEN v_store_id := 2;
    ELSE v_store_id := 3;
    END IF;
    
    -- 지수 함수를 이용해 뒤로 갈수록(5, 6월) 매출이 폭발하는 트렌드 반영
    v_order_time := '2026-03-01 00:00:00'::timestamp + (random() ^ 0.7) * (interval '108 days');
    
    -- Orders & Order_details 연쇄 트랜잭션 Insert...
END LOOP;

```

### 2) 고성능 복잡 통계 연산 (Spring Data JPA Native Query)
윈도우 함수(DENSE_RANK() OVER) 및 EXTRACT/TO_CHAR를 활용하여 데이터베이스 최적화 연산을 수행하고, JPA Interface Mapping으로 유연하게 데이터를 바인딩했습니다.

```java
// SalesRepository.java - 카테고리별 1등 상품 추출 (윈도우 함수 적용)
@Query(value = "WITH menu_sales AS ( " +
               "    SELECT m.category, m.menu_name, SUM(od.quantity) AS total_qty " +
               "    FROM order_details od " +
               "    JOIN menus m ON od.menu_id = m.menu_id " +
               "    GROUP BY m.category, m.menu_name " +
               ") " +
               "SELECT category, menu_name AS menuName, total_qty AS totalQty, " +
               "DENSE_RANK() OVER (PARTITION BY category ORDER BY total_qty DESC) AS categoryRank " +
               "FROM menu_sales", nativeQuery = true)
List<CategoryTopMenuDto> findCategoryTopMenus();

```

### 3) 가변적 Y축 스케일 최적화 (JavaScript / Chart.js)
대용량 데이터의 특성상 차트가 밋밋한 일자로 단편화되는 문제를 해결하기 위해, JavaScript단에서 데이터의 Math.min / Math.max를 실시간 추적하여 Y축 범위를 다이내믹하게 쥐어짜는 알고리즘을 도입했습니다.

```javascript
options: { 
    scales: { 
        y: { 
            beginAtZero: false,
            // 최솟값의 95% 지점부터 Y축을 시작하게 만들어 미세한 격차의 굴곡을 시각적으로 뻥튀기함
            min: Math.floor(minStoreSales * 0.95),
            max: Math.ceil(maxStoreSales * 1.05)
        } 
    } 
}

```
