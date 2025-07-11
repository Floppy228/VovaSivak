//package ru.vladimir.tgBot.entity;
//import jakarta.persistence.*;
//
//@Entity
//public class OrderProduct {
//    @Id
//    @GeneratedValue
//    private Long id;
//
//    @ManyToOne
//    private ClientOrder order;
//
//    @ManyToOne
//    private Product product;
//
//    @Column(nullable = false)
//    private Integer countProduct;
package ru.vladimir.tgBot.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;

@Entity
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class OrderProduct {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "order_id", nullable = false)
    private ClientOrder order;

    @ManyToOne(optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private Integer countProduct;

    public OrderProduct() {}

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public ClientOrder getOrder() { return order; }
    public void setOrder(ClientOrder order) { this.order = order; }

    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }

    public Integer getCountProduct() { return countProduct; }
    public void setCountProduct(Integer countProduct) { this.countProduct = countProduct; }
}
