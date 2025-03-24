package com.ishyiga.entities;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "sales",
        uniqueConstraints = @UniqueConstraint(columnNames = {"client_id", "month", "year"}))
public class Sale {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private int month;
    private int invoiceCount;
    private Double salesValue;
    private Double totalVat;
    private Double cash;
    private Double credit;
    private int year;
    private String client_id;
}
