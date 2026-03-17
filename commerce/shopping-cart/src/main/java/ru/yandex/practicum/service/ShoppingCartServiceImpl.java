package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.api.WarehouseFeignClient;
import ru.yandex.practicum.dto.cart.ShoppingCartDto;
import ru.yandex.practicum.dto.product.ChangeProductQuantityRequest;
import ru.yandex.practicum.dto.warehouse.BookedProductsDto;
import ru.yandex.practicum.exception.DeactivateCartException;
import ru.yandex.practicum.exception.NoProductsInShoppingCartException;
import ru.yandex.practicum.exception.NotAuthorizedUserException;
import ru.yandex.practicum.mapper.ShoppingCartMapper;
import ru.yandex.practicum.model.ShoppingCart;
import ru.yandex.practicum.repository.ShoppingCartRepository;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class ShoppingCartServiceImpl implements ShoppingCartService {

    private final ShoppingCartRepository shoppingCartRepository;
    private final ShoppingCartMapper cartMapper;
    private final WarehouseFeignClient warehouseClient;

    @Transactional(readOnly = true)
    @Override
    public ShoppingCartDto getShoppingCart(String username) {
        validateUsername(username);
        log.info("Get shopping cart for username {}", username);
        ShoppingCart cart = getOrCreateShoppingCart(username);
        log.info("Get shopping cart", cart);
        return cartMapper.toCartDto(cart);
    }

    @Transactional
    @Override
    public ShoppingCartDto addProductToShoppingCart(String username, Map<UUID, Integer> products) {
        log.info("Add product to cart for username {}", username);
        validateUsername(username);
        ShoppingCart cart = getOrCreateShoppingCart(username);
        checkCartIsActive(cart);
        Map<UUID, Integer> oldProducts = cart.getProducts();
        oldProducts.putAll(products);
        cart.setProducts(oldProducts);
        log.info("Get shopping cart", cart);

        BookedProductsDto bookedProductsDto = warehouseClient.checkProductQuantityEnoughForShoppingCart(cartMapper.toCartDto(cart));
        log.info("Checked warehouse for shopping cart", bookedProductsDto);

        shoppingCartRepository.save(cart);
        log.info("Get shopping cart", cart);
        return cartMapper.toCartDto(cart);
    }

    @Transactional
    @Override
    public void deactivateCurrentShoppingCart(String username) {
        validateUsername(username);
        ShoppingCart cart = getOrCreateShoppingCart(username);
        checkCartIsActive(cart);
        cart.setActive(false);
        shoppingCartRepository.save(cart);
        log.info("Get shopping cart", cart);
    }

    @Transactional
    @Override
    public ShoppingCartDto removeFromShoppingCart(String username, List<UUID> products) {
        validateUsername(username);
        log.info("Remove product from cart for username {}", username);
        ShoppingCart cart = getOrCreateShoppingCart(username);
        checkCartIsActive(cart);
        Map<UUID, Integer> oldProducts = cart.getProducts();
        for (UUID idToRemove : products) {
            if (oldProducts.containsKey(idToRemove)) {
                oldProducts.remove(idToRemove);
            } else {
                throw new NoProductsInShoppingCartException("Такого продукта нет в корзине");
            }
        }
        cart.setProducts(oldProducts);
        log.info("Get shopping cart", cart);
        shoppingCartRepository.save(cart);
        log.info("Get shopping cart", cart);
        return cartMapper.toCartDto(cart);
    }

    @Transactional
    @Override
    public ShoppingCartDto changeProductQuantity(String username, ChangeProductQuantityRequest request) {
        validateUsername(username);
        log.info("Change product quantity for cart for username {}", username);
        ShoppingCart cart = getOrCreateShoppingCart(username);
        checkCartIsActive(cart);
        Map<UUID, Integer> oldProducts = cart.getProducts();
        if (oldProducts.containsKey(request.getProductId())) {
            oldProducts.put(request.getProductId(), request.getNewQuantity());
        } else {
            throw new NoProductsInShoppingCartException("Такого продукта нет в корзине");
        }
        cart.setProducts(oldProducts);
        log.info("Get shopping cart", cart);

        BookedProductsDto bookedProductsDto = warehouseClient.checkProductQuantityEnoughForShoppingCart(cartMapper.toCartDto(cart));
        log.info("Checked warehouse for shopping cart", bookedProductsDto);

        shoppingCartRepository.save(cart);
        log.info("Get shopping cart", cart);
        return cartMapper.toCartDto(cart);
    }

    private void validateUsername(String username) {
        if (username.isBlank()) {
            throw new NotAuthorizedUserException(username);
        }
    }

    private ShoppingCart getOrCreateShoppingCart(String username) {
        return shoppingCartRepository.findByUsername(username)
                .orElseGet(() -> {
                    log.info("Create new shopping cart for username {}", username);
                    ShoppingCart newCart = new ShoppingCart();
                    newCart.setUsername(username);
                    return shoppingCartRepository.save(newCart);
                });
    }

    private void checkCartIsActive(ShoppingCart cart) {
        if(!cart.getActive()) {
            throw new DeactivateCartException("Shopping car" + cart.getUsername() + " not active");
        }
    }
}
