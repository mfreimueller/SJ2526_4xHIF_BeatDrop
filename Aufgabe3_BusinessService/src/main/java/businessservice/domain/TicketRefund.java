package businessservice.domain;

import java.math.BigDecimal;

public record TicketRefund(
        Performance performance,
        ConflictType conflictType,
        BigDecimal totalRefundAmount
) {}
