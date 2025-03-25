package com.ishyiga.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import lombok.Data;

import java.util.Date;

@Entity
@Data
@Table(name="STOCK")
public class Stock {
    @Id
    @Column(name = "client_id")
    private String clientId;
    @Column(name = "total_value")
    private Long totalValue;
    @Column(name = "today")
    private Date today;
}
