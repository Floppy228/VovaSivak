package ru.vladimir.tgBot.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;

@Entity
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
@Table(name = "order_product") // можно указать явно
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

    @Column(name = "count_product", nullable = false)
    private Integer countProduct;

    public OrderProduct() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public ClientOrder getOrder() { return order; }
    public void setOrder(ClientOrder order) { this.order = order; }

    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }

    public Integer getCountProduct() { return countProduct; }
    public void setCountProduct(Integer countProduct) { this.countProduct = countProduct; }
}
