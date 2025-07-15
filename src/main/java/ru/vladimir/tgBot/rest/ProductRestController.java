package ru.vladimir.tgBot.rest;

import org.springframework.web.bind.annotation.*;
import ru.vladimir.tgBot.entity.Product;
import ru.vladimir.tgBot.service.EntitiesService;

import java.util.List;

@RestController
@RequestMapping("/rest/products")
public class ProductRestController {

    private final EntitiesService service;

    public ProductRestController(EntitiesService service) {
        this.service = service;
    }

    @GetMapping("/search")
    public List<Product> searchProducts(@RequestParam(required = false) String name,
                                        @RequestParam(required = false) Long categoryId) {
        List<Product> products = service.searchProductsByName(name != null ? name : "");
        if (categoryId != null) {
            products = products.stream()
                    .filter(p -> p.getCategory() != null && p.getCategory().getId().equals(categoryId))
                    .toList();
        }
        return products;
    }

    @GetMapping("/popular")
    public List<Product> getPopularProducts(@RequestParam Integer limit) {
        return service.getTopPopularProducts(limit);
    }
}
