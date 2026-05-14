package businessservice.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import businessservice.domain.Artist;
import businessservice.domain.ConflictType;
import businessservice.domain.Performance;
import businessservice.domain.Stage;
import businessservice.domain.TicketOrder;
import businessservice.domain.TicketRefund;
import businessservice.domain.TicketType;

@Service
public class TicketRefundService {

    public List<TicketRefund> conflictsForStage(List<Performance> performances, Stage stage) {
        if (performances == null) return List.of();

        var filtered = performances.stream()
                .filter(p -> p.getStage().equals(stage))
                .filter(this::isValidDuration)
                .toList();

        return findStageOverlaps(filtered);
    }

    public List<TicketRefund> conflictsForArtist(List<Performance> performances, Artist artist) {
        if (performances == null) return List.of();

        var filtered = performances.stream()
                .filter(p -> p.getArtist().equals(artist))
                .filter(this::isValidDuration)
                .toList();

        return findArtistOverlaps(filtered);
    }

    private boolean isValidDuration(Performance p) {
        return p.getDurationMinutes() > 0 && p.getDurationMinutes() <= 480;
    }

    private List<TicketRefund> findStageOverlaps(List<Performance> performances) {
        var result = new ArrayList<TicketRefund>();

        var byDay = performances.stream()
                .collect(Collectors.groupingBy(Performance::getDay));

        for (var dayPerformances : byDay.values()) {
            var sorted = dayPerformances.stream()
                    .sorted(Comparator.comparingInt(Performance::getStartMinute))
                    .toList();

            for (int i = 0; i < sorted.size(); i++) {
                for (int j = i + 1; j < sorted.size(); j++) {
                    var earlier = sorted.get(i);
                    var later = sorted.get(j);
                    if (overlaps(earlier, later)) {
                        var overlapEnd = Math.min(endMinute(earlier), endMinute(later));
                        var overlapStart = later.getStartMinute();
                        var overlapDuration = overlapEnd - overlapStart;
                        var threshold = later.getDurationMinutes() * 0.25;
                        if (overlapDuration > threshold) {
                            result.add(createRefund(later, earlier, ConflictType.STAGE_OVERLAP));
                        }
                    }
                }
            }
        }
        return result;
    }

    private List<TicketRefund> findArtistOverlaps(List<Performance> performances) {
        var result = new ArrayList<TicketRefund>();

        var byDay = performances.stream()
                .collect(Collectors.groupingBy(Performance::getDay));

        for (var dayPerformances : byDay.values()) {
            var sameDay = new ArrayList<>(dayPerformances);

            for (int i = 0; i < sameDay.size(); i++) {
                for (int j = i + 1; j < sameDay.size(); j++) {
                    var a = sameDay.get(i);
                    var b = sameDay.get(j);
                    // Artist overlap only counts on different stages
                    if (a.getStage().equals(b.getStage())) continue;
                    if (!overlaps(a, b)) continue;

                    var later = a.getStartMinute() >= b.getStartMinute() ? a : b;
                    var earlier = a.getStartMinute() >= b.getStartMinute() ? b : a;
                    result.add(createRefund(later, earlier, ConflictType.ARTIST_OVERLAP));
                }
            }
        }
        return result;
    }

    private boolean overlaps(Performance a, Performance b) {
        return a.getStartMinute() < endMinute(b) && b.getStartMinute() < endMinute(a);
    }

    private int endMinute(Performance p) {
        return p.getStartMinute() + p.getDurationMinutes();
    }

    private TicketRefund createRefund(Performance later, Performance earlier, ConflictType type) {
        var totalRefund = later.getTicketOrders().stream()
                .filter(o -> o.getTicketType() != TicketType.FESTIVAL)
                .map(this::refundForOrder)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new TicketRefund(later, type, totalRefund);
    }

    private BigDecimal refundForOrder(TicketOrder order) {
        return switch (order.getTicketType()) {
            case REGULAR -> order.getPrice().multiply(BigDecimal.valueOf(0.75)).setScale(2, RoundingMode.HALF_UP);
            case VIP -> order.getPrice();
            case PROMO -> BigDecimal.ZERO;
            case FESTIVAL -> BigDecimal.ZERO;
        };
    }
}
