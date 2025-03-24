package com.ishyiga.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.util.Date;

@Entity
@Data
@Table(name="STOCK")
public class Stock {
    @Id
    private String client_id;
    private Long total_value;
    private Date today;
}
