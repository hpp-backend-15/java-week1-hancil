package io.hhplus.tdd;

import io.hhplus.tdd.point.PointHistory;
import io.hhplus.tdd.point.PointService;
import io.hhplus.tdd.point.UserPoint;
import io.hhplus.tdd.point.UserPointRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class PointServiceIntegrationTest {

    @Autowired
    private PointService pointService;

    @Autowired
    private UserPointRepository userPointRepository;

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

        // 초기값이 100이다. <- setup 참고
        // 각 스레드가 100씩 충전했으므로 총 2000이 되어야 함
        assertEquals(2000, updatedUserPoint.point());

        //history의 list size를 통해 요청에 맞게 호출되어 저장 되었는지 검증
        List<PointHistory> pointHistories = pointService.getPointHistories(userId);
        assertThat(pointHistories).hasSize(threadCount);
    }

    @Test
    public void 여러_사용자_동시충전_정상_작동_테스트() throws InterruptedException {
        // Given
        long[] userIds = {1, 2, 3, 2, 2, 1, 3, 2, 3, 1};
        long[] amounts = {100, 200, 300,100, 200, 300,100, 200, 300,200};
        int threadCount = 10;

        // 스레드 풀과 CountDownLatch를 사용하여 동시성 테스트

        // 멀티 스레드 환경은 순서를 보장하진 않는다.
        // 데이터 유실이 발생하지 않는지 어떻게 보장할 수 있을까?
        // ExecutorService executorService = Executors.newFixedThreadPool(threadCount);

        //싱글 스레드 환경
        ExecutorService executorService = Executors.newSingleThreadExecutor();

        CountDownLatch latch = new CountDownLatch(threadCount);

        // When
        for (int i = 0; i < userIds.length; i++) {

            final long id = userIds[i];
            final long amount = amounts[i];

            executorService.submit(() -> {
                try {
                    pointService.chargeUserPoint(id, amount);
                } finally {
                    latch.countDown();
                }
            });
        }

        // 모든 스레드가 완료될 때까지 대기
        latch.await();

        // 충전 이력 조회
        List<PointHistory> pointHistories = new ArrayList<>();
        for (long id : new int[]{1,2,3}) {
            pointHistories.addAll(pointService.getPointHistories(id));
        }

        // 충전 이력의 순서 검증
        // 이력의 순서가 사용자 아이디 순서와 충전 금액 순서에 따라 맞는지 검증
        for (int i = 1; i <= userIds.length; i++) {
            PointHistory history = pointHistories.get(i-1); // 순서대로 검증
            assertThat(history.userId()).isEqualTo(userIds[history.id().intValue() - 1]);
        }

    }

}
