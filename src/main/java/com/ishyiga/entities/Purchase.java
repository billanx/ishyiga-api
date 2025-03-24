
package com.ishyiga.entities;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "purchases",
        uniqueConstraints = @UniqueConstraint(columnNames = {"client_id", "month", "year"}))
public class Purchase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private int month;
    private int invoiceCount;
    private Double poValue;
    private String client_id;
    private int year;
}
