package ru.yandex.practicum.service;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.api.ShoppingStoreFeignClient;
import ru.yandex.practicum.dto.order.OrdersDto;
import ru.yandex.practicum.dto.payment.PaymentDto;
import ru.yandex.practicum.exception.NoProductsInOrderException;
import ru.yandex.practicum.exception.NotEnoughInfoInOrderToCalculateException;
import ru.yandex.practicum.exception.ProductNotFoundException;
import ru.yandex.practicum.mapper.PaymentMapper;
import ru.yandex.practicum.model.Payment;
import ru.yandex.practicum.repository.PaymentRepository;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class PaymentServiceImpl implements PaymentService {
    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;
    private final ShoppingStoreFeignClient shoppingStoreFeignClient;
    private static final BigDecimal VAT = BigDecimal.valueOf(0.1);


    @Override
    public PaymentDto createPayment(OrdersDto orderDto) {
        log.info("Create payment {}", orderDto);
        BigDecimal productCost = orderDto.getProductPrice();
        BigDecimal deliveryTotal = orderDto.getDeliveryPrice();
        BigDecimal totalCost = orderDto.getTotalPrice();
        if (productCost == null || deliveryTotal == null || totalCost == null) {
            throw new NotEnoughInfoInOrderToCalculateException("Not enough info in order to calculate payment");
        }
        BigDecimal feeTotal = productCost.multiply(VAT);
        Payment payment = paymentMapper.toPayment(orderDto, feeTotal);
        payment = paymentRepository.save(payment);
        log.info("Payment {}", payment);
        return paymentMapper.toPaymentDto(payment);
    }

    @Override
    public BigDecimal getTotalCost(OrdersDto orderDto) {
        log.info("Get total cost {}", orderDto);
        BigDecimal productCost = orderDto.getProductPrice();
        BigDecimal deliveryTotal = orderDto.getDeliveryPrice();
        if (productCost == null || deliveryTotal == null) {
            throw new NotEnoughInfoInOrderToCalculateException("Not enough info in order to calculate payment");
        }
        BigDecimal feeTotal = productCost.multiply(VAT);
        BigDecimal totalCost = productCost.add(feeTotal).add(deliveryTotal);
        log.info("Total cost {}", totalCost);
        return totalCost;
    }

    @Override
    public void paymentRefunded(UUID paymentId) {
        log.info("Payment refunded {}", paymentId);

    }

    @Override
    public BigDecimal productCost(OrdersDto orderDto) {
        log.info("Product cost {}", orderDto);
        Map<UUID, Integer> products = orderDto.getProducts();
        if (products.isEmpty()) {
            throw new NoProductsInOrderException("No product in shopping cart");
        }
        BigDecimal productCost = BigDecimal.valueOf(0.0);
        for (Map.Entry<UUID, Integer> entry : products.entrySet()) {
            UUID productId = entry.getKey();
            Integer quantity = entry.getValue();

            BigDecimal price;
            try {
                price = shoppingStoreFeignClient.getProduct(productId).getPrice();
            } catch (FeignException.NotFound e) {
                throw new ProductNotFoundException("Product not found: " + productId);
            } catch (FeignException e) {
                throw new RuntimeException("Failed to fetch product: " + productId, e);
            }

            productCost = productCost.add(price.multiply(BigDecimal.valueOf(quantity)));
        }

        log.info("Calculated total product cost: {}", productCost);
        return productCost;
    }

    @Override
    public void paymentFailed(UUID paymentId) {
        log.info("Payment failed {}", paymentId);

    }
}
