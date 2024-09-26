package io.hhplus.tdd;

import io.hhplus.tdd.point.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class UsePointServiceTest {

    @Mock
    private UserPointRepository userPointRepository;
    @Mock
    private PointHistoryRepository pointHistoryRepository;

    @InjectMocks
    private PointService pointService;

    private UserPoint userPoint;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        // 초기 포인트를 100으로 설정된 유저로 세팅
        userPoint = new UserPoint(1L, 100L, System.currentTimeMillis());
    }

    @Test
    void id가_0보다_작거나_같으면_예외() {

        //given
        long id = -1L;
        long amount = 50L;

        //when
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                pointService.useChargePoint(id, amount));

        //then
        assertEquals("유효하지 않은 id 입니다.", exception.getMessage());
    }

    @Test
    void id가_내부_레파지토리에_존재하지_않으면_예외() {

        //given
        long id = 999L;
        long amount = 50L;

        //when
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                pointService.useChargePoint(id, amount));

        //then
        assertEquals("사용자가 존재하지 않습니다.", exception.getMessage());

//        verify(userPointRepository, never()).save(any(UserPoint.class));
//        verify(pointHistoryRepository, never()).insert(anyLong(), anyLong(), any(), anyLong());

    }

    @Test
    void 충전금액이_0_이하일_때_예외() {

        //given
        long id = 1L;
        long[] useAmount = new long[] {0L, -200L};

        for(long amount : useAmount) {

            //when
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                    pointService.useChargePoint(id, amount));

            //then
            assertEquals("사용할 금액은 0보다 커야 합니다.", exception.getMessage());

        }

    }


    @Test
    void 유저포인트_객체에_포인트사용_책임을_전가한다() {

        //given
        long id = 1L;
        long requestAmount = 50L;

        assertThat(userPoint.consumePoints(requestAmount).point()).isEqualTo(50L);

    }

    @Test
    void 사용자가_갖고있는_포인트보다_더_많은_포인트를_사용할_수_없다() {

        //given
        long id = 1L;
        long requestAmount = 150L;

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                userPoint.consumePoints(requestAmount));

        assertEquals("보유한 포인트보다 많은 금액을 사용할 수 없습니다.",exception.getMessage());
    }



    @Test
    void 포인트_사용에_성공한다() {

        //given
        long id = 1L;
        long requestAmount = 50L;
        UserPoint modifiedUserPoint = userPoint.consumePoints(requestAmount);

        //when
        when(userPointRepository.save(modifiedUserPoint))
                .thenReturn(modifiedUserPoint);

        UserPoint result = userPointRepository.save(modifiedUserPoint);
        assertThat(result.id()).isEqualTo(modifiedUserPoint.id());
        assertThat(result.point()).isEqualTo(modifiedUserPoint.point());

    }
    @Test
    void PointHistory_객체_생성() {

        //given
        long id = 1L;
        long requestAmount = 50L;

        assertThat(PointHistory.create(id, requestAmount, TransactionType.USE)).isInstanceOf(PointHistory.class);

    }

    @Test
    void 포인트_충전_이력_저장() {

        //given
        long id = 1L;
        long requestAmount = 50L;
        PointHistory pointHistory = PointHistory.create(id, requestAmount, TransactionType.USE);

        when(pointHistoryRepository.insert(pointHistory.userId(), pointHistory.amount(), pointHistory.type(), pointHistory.updateMillis()))
                .thenReturn(pointHistory);

        PointHistory result = pointHistoryRepository.insert(pointHistory.userId(), pointHistory.amount(), pointHistory.type(), pointHistory.updateMillis());
        assertNotNull(result);

    }



}
