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
