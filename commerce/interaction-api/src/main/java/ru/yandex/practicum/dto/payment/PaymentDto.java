package ru.yandex.practicum.dto.payment;

import java.math.BigDecimal;
import java.util.UUID;

public class PaymentDto {
    public class Payment {
        private UUID paymentId;
        private BigDecimal totalPayment;
        private BigDecimal deliveryTotal;
        private BigDecimal feeTotal;
    }
}
