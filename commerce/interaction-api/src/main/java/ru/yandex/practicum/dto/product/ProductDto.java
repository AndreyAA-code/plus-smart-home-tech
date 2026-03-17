package ru.yandex.practicum.dto.product;


import jakarta.validation.constraints.Min;
import lombok.*;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductDto {

private UUID productId;
@NotNull
private String productName;
@NotNull
private String description;
private String imageSrc;
@NotNull
private QuantityState quantityState;
@NotNull
private ProductState productState;
@NotNull
private ProductCategory productCategory;
@Min(1)
private BigDecimal price;
}
