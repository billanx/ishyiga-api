
package com.ishyiga.entities;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name= "refund_cancelled")
public class RefundCancelled {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private int month;
    private int invoiceCount;
    private Double salesValue;
    private Double totalVat;
    private Double cash;
    private Double credit;
}
