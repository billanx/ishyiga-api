
package com.ishyiga.entities;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "orders",
        uniqueConstraints = @UniqueConstraint(columnNames = {"client_id", "month", "year"}))
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private int month;
    private int day;
    private int itemCount;
    private Double poValue;
    private int year;
    private String client_id;
}
