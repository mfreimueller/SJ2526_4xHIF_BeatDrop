package businessservice.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import businessservice.domain.Artist;
import businessservice.domain.ConflictType;
import businessservice.domain.Performance;
import businessservice.domain.Stage;
import businessservice.domain.TicketOrder;
import businessservice.domain.TicketType;

class TicketRefundServiceTest {

    private final TicketRefundService service = new TicketRefundService();

    private final Stage mainStage = new Stage("Main Stage", 5000);
    private final Stage secondStage = new Stage("Second Stage", 3000);

    private final Artist artistA = new Artist("Artist A", "Rock");
    private final Artist artistB = new Artist("Artist B", "Electronic");

    private final LocalDate day = LocalDate.of(2026, 7, 15);

    @Test
    @DisplayName("STAGE_OVERLAP: overlapping performances exceeding 25% threshold should produce refund")
    void stageOverlap_exceedsThreshold_returnsRefund() {
        // A: 14:00-14:45 (startMinute=840, duration=45)
        var early = createPerformance(artistA, mainStage, day, 840, 45, 100);
        // B: 14:30-15:15 (startMinute=870, duration=45)
        // Overlap: 14:30-14:45 = 15min => 15/45 = 33.3% > 25%
        var late = createPerformance(artistA, mainStage, day, 870, 45, 100);
        addTicketOrder(late, TicketType.REGULAR, new BigDecimal("100.00"));

        var result = service.conflictsForStage(List.of(early, late), mainStage);

        assertEquals(1, result.size());
        var refund = result.getFirst();
        assertEquals(late.getId(), refund.performance().getId());
        assertEquals(ConflictType.STAGE_OVERLAP, refund.conflictType());
        assertEquals(new BigDecimal("75.00"), refund.totalRefundAmount());
    }

    @Test
    @DisplayName("STAGE_OVERLAP: overlapping performances below 25% threshold should NOT refund")
    void stageOverlap_belowThreshold_noRefund() {
        // A: 14:00-15:00 (startMinute=840, duration=60)
        var early = createPerformance(artistA, mainStage, day, 840, 60, 100);
        // B: 14:50-15:50 (startMinute=890, duration=60)
        // Overlap: 14:50-15:00 = 10min => 10/60 = 16.7% < 25%
        var late = createPerformance(artistA, mainStage, day, 890, 60, 100);
        addTicketOrder(late, TicketType.REGULAR, new BigDecimal("100.00"));

        var result = service.conflictsForStage(List.of(early, late), mainStage);

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("STAGE_OVERLAP: exactly 25% overlap is not refunded (strictly greater required)")
    void stageOverlap_exact25percent_noRefund() {
        // A: 14:00-14:40 (startMinute=840, duration=40)
        var early = createPerformance(artistA, mainStage, day, 840, 40, 100);
        // B: 14:30-15:30 (startMinute=870, duration=60)
        // Overlap: 14:30-14:40 = 10min => 10/60 = 16.7% < 25%
        // Hmm, that's below. Let me calculate differently:
        // A: 14:00-15:00 (duration=60)
        // B: 14:45-15:45 (duration=60)
        // Overlap: 14:45-15:00 = 15min => 15/60 = 25% exactly — NOT refunded
        var early2 = createPerformance(artistA, mainStage, day, 840, 60, 100);
        var late2 = createPerformance(artistA, mainStage, day, 885, 60, 100);
        addTicketOrder(late2, TicketType.REGULAR, new BigDecimal("100.00"));

        var result = service.conflictsForStage(List.of(early2, late2), mainStage);

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("STAGE_OVERLAP: non-overlapping performances should produce no conflicts")
    void stageOverlap_nonOverlapping_noConflict() {
        // A: 14:00-15:00 (startMinute=840, duration=60)
        var a = createPerformance(artistA, mainStage, day, 840, 60, 100);
        // B: 15:00-16:00 (startMinute=900, duration=60) — starts exactly when A ends
        var b = createPerformance(artistB, mainStage, day, 900, 60, 100);

        var result = service.conflictsForStage(List.of(a, b), mainStage);

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("ARTIST_OVERLAP: same artist, different stages, overlapping times → refund")
    void artistOverlap_differentStages_returnsRefund() {
        var onMain = createPerformance(artistA, mainStage, day, 840, 60, 100);
        var onSecond = createPerformance(artistA, secondStage, day, 870, 60, 100);
        addTicketOrder(onSecond, TicketType.REGULAR, new BigDecimal("100.00"));

        var result = service.conflictsForArtist(List.of(onMain, onSecond), artistA);

        assertEquals(1, result.size());
        var refund = result.getFirst();
        assertEquals(onSecond.getId(), refund.performance().getId());
        assertEquals(ConflictType.ARTIST_OVERLAP, refund.conflictType());
    }

    @Test
    @DisplayName("ARTIST_OVERLAP: same artist on same stage is STAGE_OVERLAP, not ARTIST_OVERLAP")
    void artistOverlap_sameStage_isStageOverlap() {
        var early = createPerformance(artistA, mainStage, day, 840, 60, 100);
        var late = createPerformance(artistA, mainStage, day, 870, 60, 100);
        addTicketOrder(late, TicketType.REGULAR, new BigDecimal("100.00"));

        var stageResult = service.conflictsForStage(List.of(early, late), mainStage);
        var artistResult = service.conflictsForArtist(List.of(early, late), artistA);

        // Should be detected as STAGE_OVERLAP, not ARTIST_OVERLAP (same stage)
        assertEquals(1, stageResult.size());
        assertEquals(ConflictType.STAGE_OVERLAP, stageResult.getFirst().conflictType());
        // Artist overlap should be empty since both on same stage
        assertTrue(artistResult.isEmpty());
    }

    @Test
    @DisplayName("ARTIST_OVERLAP: non-overlapping performances → no conflict")
    void artistOverlap_nonOverlapping_noConflict() {
        var onMain = createPerformance(artistA, mainStage, day, 840, 60, 100);
        var onSecond = createPerformance(artistA, secondStage, day, 920, 60, 100);

        var result = service.conflictsForArtist(List.of(onMain, onSecond), artistA);

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Invalid durations (≤0 or >480) should be ignored")
    void invalidDuration_ignored() {
        var validEarly = createPerformance(artistA, mainStage, day, 840, 60, 100);
        var validLate = createPerformance(artistB, mainStage, day, 870, 60, 100);
        var zeroDuration = createPerformance(artistB, mainStage, day, 900, 0, 100);
        var negativeDuration = createPerformance(artistB, mainStage, day, 960, -10, 100);
        var tooLong = createPerformance(artistB, mainStage, day, 1020, 481, 100);
        addTicketOrder(validLate, TicketType.REGULAR, new BigDecimal("100.00"));

        // Only validEarly and validLate should be considered (they overlap)
        var result = service.conflictsForStage(
                List.of(validEarly, validLate, zeroDuration, negativeDuration, tooLong), mainStage);

        assertEquals(1, result.size());
        assertEquals(new BigDecimal("75.00"), result.getFirst().totalRefundAmount());
    }

    @Test
    @DisplayName("Regular tickets are refunded at 75% of price")
    void refundCalculation_regular_75percent() {
        var earlier = createPerformance(artistA, mainStage, day, 840, 60, 100);
        var later = createPerformance(artistB, mainStage, day, 870, 60, 100);
        addTicketOrder(later, TicketType.REGULAR, new BigDecimal("200.00"));

        var result = service.conflictsForStage(List.of(earlier, later), mainStage);

        assertEquals(new BigDecimal("150.00"), result.getFirst().totalRefundAmount());
    }

    @Test
    @DisplayName("VIP tickets are refunded at 100% of price")
    void refundCalculation_vip_100percent() {
        var earlier = createPerformance(artistA, mainStage, day, 840, 60, 100);
        var later = createPerformance(artistB, mainStage, day, 870, 60, 100);
        addTicketOrder(later, TicketType.VIP, new BigDecimal("300.00"));

        var result = service.conflictsForStage(List.of(earlier, later), mainStage);

        assertEquals(new BigDecimal("300.00"), result.getFirst().totalRefundAmount());
    }

    @Test
    @DisplayName("Promo tickets get zero refund")
    void refundCalculation_promo_zero() {
        var earlier = createPerformance(artistA, mainStage, day, 840, 60, 100);
        var later = createPerformance(artistB, mainStage, day, 870, 60, 100);
        addTicketOrder(later, TicketType.PROMO, new BigDecimal("50.00"));

        var result = service.conflictsForStage(List.of(earlier, later), mainStage);

        assertEquals(BigDecimal.ZERO, result.getFirst().totalRefundAmount());
    }

    @Test
    @DisplayName("Festival tickets are excluded from refund")
    void refundCalculation_festival_excluded() {
        var earlier = createPerformance(artistA, mainStage, day, 840, 60, 100);
        var later = createPerformance(artistB, mainStage, day, 870, 60, 100);
        addTicketOrder(later, TicketType.FESTIVAL, new BigDecimal("150.00"));

        var result = service.conflictsForStage(List.of(earlier, later), mainStage);

        assertEquals(BigDecimal.ZERO, result.getFirst().totalRefundAmount());
    }

    @Test
    @DisplayName("Mixed ticket types on one performance: Regular 75% + VIP 100% + Promo 0%")
    void refundCalculation_mixedTypes() {
        var earlier = createPerformance(artistA, mainStage, day, 840, 60, 100);
        var later = createPerformance(artistB, mainStage, day, 870, 60, 100);
        addTicketOrder(later, TicketType.REGULAR, new BigDecimal("100.00"));
        addTicketOrder(later, TicketType.VIP, new BigDecimal("200.00"));
        addTicketOrder(later, TicketType.PROMO, new BigDecimal("50.00"));
        addTicketOrder(later, TicketType.FESTIVAL, new BigDecimal("150.00"));

        var result = service.conflictsForStage(List.of(earlier, later), mainStage);

        // 100*0.75 + 200*1.0 + 50*0 + 150*0 (excluded) = 75 + 200 = 275
        assertEquals(new BigDecimal("275.00"), result.getFirst().totalRefundAmount());
    }

    @Test
    @DisplayName("Null performance list should return empty result")
    void conflictsForStage_nullPerformances() {
        var result = service.conflictsForStage(null, mainStage);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Duplicate conflict (A,B) and (B,A) should not appear")
    void duplicateConflict_notReported() {
        // Three performances: A(14:00-15:00), B(14:30-15:30), C(15:00-16:00)
        // A vs B: overlap 14:30-15:00 = 30min, 30/60 = 50% > 25% → B refunded
        // A vs C: no overlap
        // B vs C: no overlap (B ends at 15:30, C starts at 15:00 — actually they overlap!)
        // Let me adjust: A(14:00-14:45), B(14:30-15:15), C(14:45-15:30)
        // Actually, let me keep it simple — just verify no pair appears twice
        var a = createPerformance(artistA, mainStage, day, 840, 45, 100);
        var later = createPerformance(artistB, mainStage, day, 870, 45, 100);
        addTicketOrder(later, TicketType.REGULAR, new BigDecimal("100.00"));

        var result = service.conflictsForStage(List.of(a, later), mainStage);

        // Only one result for the pair (A is earlier, B is later)
        assertEquals(1, result.size());
        assertEquals(later.getId(), result.getFirst().performance().getId());
    }

    // --- Helper methods ---

    private long nextId = 1;

    private Performance createPerformance(Artist artist, Stage stage, LocalDate day,
                                          int startMinute, int durationMinutes, int price) {
        var perf = new Performance();
        perf.setId(nextId++);
        perf.setArtist(artist);
        perf.setStage(stage);
        perf.setDay(day);
        perf.setStartMinute(startMinute);
        perf.setDurationMinutes(durationMinutes);
        perf.setTicketOrders(new ArrayList<>());
        return perf;
    }

    private void addTicketOrder(Performance perf, TicketType type, BigDecimal price) {
        var order = new TicketOrder();
        order.setId(nextId++);
        order.setPerformance(perf);
        order.setTicketType(type);
        order.setPrice(price);
        perf.getTicketOrders().add(order);
    }
}
