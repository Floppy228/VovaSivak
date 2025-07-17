package ru.vladimir.tgBot.rest;

import org.springframework.web.bind.annotation.*;
import ru.vladimir.tgBot.entity.Client;
import ru.vladimir.tgBot.entity.ClientOrder;
import ru.vladimir.tgBot.entity.Product;
import ru.vladimir.tgBot.service.EntitiesService;

import java.util.List;

@RestController
@RequestMapping("/rest/clients")
public class ClientRestController {

    private final EntitiesService service;

    public ClientRestController(EntitiesService service) {
        this.service = service;
    }

    @GetMapping("/{id}/orders")
    public List<ClientOrder> getClientOrders(@PathVariable Long id) {
        return service.getClientOrders(id);
    }

    @GetMapping("/{id}/products")
    public List<Product> getClientProducts(@PathVariable Long id) {
        return service.getClientProducts(id);
    }

    @GetMapping("/search")
    public List<Client> searchClients(@RequestParam String name) {
        return service.searchClientsByName(name);
    }
}
