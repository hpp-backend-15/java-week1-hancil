# java-week1-hancil
항해+ 1주차 과제 - TDD로 개발하기 (정한슬)


# 분석 및 보고서

## 동시성 제어의 필요성과 목적

- **동시성 제어의 필요성**
    - 동시성 제어는 여러 요청이 동시에 처리될 때 데이터의 **일관성**과 **무결성**을 보장하기 위해 필요합니다.
    - 만약 여러 스레드나 프로세스가 동시에 같은 데이터에 접근하거나 수정한다면, 데이터 충돌, 값 덮어 쓰기, 데이터 손실 등 예기치 않은 문제가 발생할 수 있습니다.
    - 예를 들어, 포인트 충전 시 시스템에서 두 개이상의 충전요청이 동시에 처리되면, 서로의 결과를 덮어 쓰거나 잘못된 값으로 저장될 가능성이 있습니다.
- **동시성 제어의 목적**
    - 여러 작업이 동시에 수행되더라도 각 작업이 순차적으로 처리되어 시스템의 **일관성**과 **신뢰성**을 유지하는 것이 목적입니다.
    - `synchronized` , `Lock` 과 같은 메커니즘을 활용하여 동시 접근을 안전하게 처리하는 것이 일반적입니다.
    

## 동시성 제어 방법론

### Synchronized

- **장점**: 특정 메서드나 블록에 대해서만 동기화를 적용할 수 있습니다.
- **단점**: 잠금(lock) 해제가 자동으로 이루어지지 않으며, 잠금이 오래 유지되면 시스템 성능이 저하될 수 있습니다.

### Lock & ReentrantLock

- **장점**: `tryLock()`  메서드를 통해 데드락 회피 및 타임아웃을 설정하는 등, 보다 세밀한 제어가 가능합니다.
- **단점**: 명시적으로 `unlock()` 을 호출해야 하므로, 잘못 사용 시 문제가 발생할 수 있습니다.

### ConcurrentHashMap을 이용한 사용자별 동시성 제어
![image](https://github.com/user-attachments/assets/a5f6e95f-acc6-4f01-a4b1-a8e8877942eb)


- 장점: 동일 사용자에 대해 발생하는 동시 요청을 순차적으로 처리할 수 있으며, 다른 사용자에 대한 요청은 병렬로 처리할 수 있어 성능 효율이 높습니다.
- 단점: 사용후 Lock을 제대로 정리하지 않으면 메모리 누수 문제가 발생할 수 있습니다.

## 테스트 및 결과 분석

- **포인트 충전하는 Service Method**
- ![image](https://github.com/user-attachments/assets/7e6b9d52-e3e1-4ca0-82a2-eab4f57e40f2)
    
- **Lock을 사용하지 않은 경우**
    - PointService에서 lock은 주석 처리 후 테스트를 실행해 보았습니다.
    
    ```java
      @BeforeEach
      public void setUp() {
          long[] userIds = {1, 2, 3};
          for(long id : userIds){
              userPointRepository.save(new UserPoint(id, 1000L, System.currentTimeMillis()));
          }
      }
    
     @Test
        public void 같은_사용자_동시_충전_정상_작동_테스트() throws InterruptedException {
    
            // Given
            long userId = 1L;
            long amount = 100;
            int threadCount = 10;
    
            // 스레드 풀과 CountDownLatch를 사용하여 동시성 테스트
            ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
            CountDownLatch latch = new CountDownLatch(threadCount);
    
            // When
            for (int i = 0; i < threadCount; i++) {
                executorService.submit(() -> {
                    try {
                        pointService.chargeUserPoint(userId, amount);
                    } finally {
                        latch.countDown();
                    }
                });
            }
    
            // 모든 스레드가 완료될 때까지 대기
            latch.await();
    
            // Then
            UserPoint updatedUserPoint = pointService.getUserPoint(userId);
    
            // 초기값이 1000이다. <- setup 참고
            // 각 스레드가 100씩 충전했으므로 총 2000이 되어야 함
            assertEquals(2000, updatedUserPoint.point());
    
            //history의 list size를 통해 요청에 맞게 호출되어 저장 되었는지 검증
            List<PointHistory> pointHistories = pointService.getPointHistories(userId);
            assertThat(pointHistories).hasSize(threadCount);
        }
    ```
    ![image](https://github.com/user-attachments/assets/4230f7fb-0c74-4358-9125-e359354faaec)

    - 여러 스레드가 동시에 접근하여 서로의 업데이트를 덮어쓰는 문제가 발생합니다.
- **Lock을 사용한 경우**

    ![image](https://github.com/user-attachments/assets/3ce4a073-c13e-446d-93cf-5d18ec889b70)


## 성능 분석

- 미완성
