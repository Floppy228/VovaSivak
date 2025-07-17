package ru.vladimir.tgBot.rest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
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
        return service.searchProducts(name, categoryId);
    }

    @GetMapping("/popular")
    public List<Product> getPopularProducts(@RequestParam Integer limit) {
        return service.getTopPopularProducts(limit);
    }
}
