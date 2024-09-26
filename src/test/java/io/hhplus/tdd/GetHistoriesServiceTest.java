package io.hhplus.tdd;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.point.PointHistory;
import io.hhplus.tdd.point.PointService;
import io.hhplus.tdd.point.TransactionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;

class GetHistoriesServiceTest {

    private PointHistoryTable pointHistoryTable;

    @Mock
    private PointService pointService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        pointHistoryTable = new PointHistoryTable();
    }

    @Test
    void id가_0보다_작거나_같은_id값은_예외() {

        //given
        long id = -1L;
        given(pointService.getPointHistories(id))
                .willThrow(new IllegalArgumentException("유효하지 않은 id 입니다."));

        //when
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                pointService.getPointHistories(id));

        //then
        assertEquals("유효하지 않은 id 입니다.", exception.getMessage());
    }

    @Test
    void id가_db에_존재하지_않으면_400() {

        //given
        long id = 999L;
        given(pointService.getPointHistories(id))
                .willThrow(new IllegalArgumentException("사용자가 존재하지 않습니다."));

        //when
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                pointService.getPointHistories(id));

        //then
        assertEquals("사용자가 존재하지 않습니다.", exception.getMessage());
    }

    @Test
    void 포인트_조회에_성공하면_PointHistory_리스트_객체_반환() {

        //given
        long id = 1L;

        PointHistory chargeHistory = pointHistoryTable.insert(id, 50, TransactionType.CHARGE, System.currentTimeMillis());
        PointHistory useHistory = pointHistoryTable.insert(id, 30, TransactionType.USE, System.currentTimeMillis());

        List<PointHistory> givenHistories = List.of(chargeHistory, useHistory);
        given(pointService.getPointHistories(id))
                .willReturn(givenHistories);

        //when
        List<PointHistory> pointHistories = pointService.getPointHistories(id);

        //then
        assertThat(pointHistories.size()).isEqualTo(2);

        assertThat(pointHistories.get(0)).isEqualTo(chargeHistory);
        assertThat(pointHistories.get(1)).isEqualTo(useHistory);
    }

}
