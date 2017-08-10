package org.sai.entity;

import javax.persistence.*;
import java.util.Date;

@Entity(name = "Orders")
public class Order {

    public Order() {
    }

    public Order(Long id, Long customerId, Double amount, String channel) {
        this.id = id;
        this.customerId = customerId;
        this.amount = amount;
        this.channel = channel;
    }

    @Id
    private Long id;

    @Column
    private Long customerId;

    @Column
    private Double amount;

    @Column
    private String channel;

    @Column
    @Temporal(value = TemporalType.TIME)
    private Date dateTime = new Date();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }
}