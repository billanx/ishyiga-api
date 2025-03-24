package com.ishyiga.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "INVOICE_LIST",
        uniqueConstraints = @UniqueConstraint(columnNames = {"id_invoice", "num_client"}))
@Data
public class ListItem {

    @Id
    @Column(name="ID")
    private Integer id;

    @Column(name = "LIST_ID_PRODUCT", nullable = false)
    private Integer listIdProduct;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "ID_INVOICE", nullable = false)
    private Invoice invoice;

    @Column(name = "CODE_UNI", length = 30)
    private String codeUni;

    @Column(name = "NUM_LOT", length = 50)
    private String numLot;

    @Column(name = "QUANTITE", nullable = false)
    private Integer quantite;

    @Column(name = "PRICE", nullable = false)
    private Double price;

    @Column(name = "PRIX_REVIENT", columnDefinition = "DOUBLE DEFAULT 0")
    private Double prixRevient;

    @Column(name = "SCANNED", length = 1, columnDefinition = "CHAR(1) DEFAULT '0'")
    private String scanned;

    @Column(name = "DATE_EXP", length = 16, columnDefinition = "VARCHAR(16) DEFAULT '101010'")
    private String dateExp;

    @Column(name = "BON_LIVRAISON", length = 50, nullable = false, columnDefinition = "VARCHAR(50) DEFAULT 'BLA01'")
    private String bonLivraison;

    @Column(name = "QTE_RETOURNE")
    private Integer qteRetourne;

    @Column(name = "TVA")
    private Double tva;

    @Column(name = "ORIGINAL_PRICE")
    private Double originalPrice;

    @Column(name = "KEY_INVOICE", length = 200)
    private String keyInvoice;

    @Column(name = "WAREHOUSE", length = 100, columnDefinition = "VARCHAR(100) DEFAULT 'PRINCIPALE'")
    private String warehouse;

    @Column(name = "IGICUMA_STATUS", length = 50, columnDefinition = "VARCHAR(50) DEFAULT 'NO'")
    private String igicumaStatus;

    @Column(name = "TAX_CODE", length = 10)
    private String taxCode;

    @Column(name = "STATE", length = 10)
    private String state;

    // Default Constructor
    public ListItem() {}

    // Parameterized Constructor
    public ListItem(Invoice invoice, String codeUni, String numLot, Integer quantite, Double price, Double prixRevient,
                    String scanned, String dateExp, String bonLivraison, Integer qteRetourne, Double tva,
                    Double originalPrice, String keyInvoice, String warehouse, String igicumaStatus, String taxCode,
                    String state) {
        this.invoice = invoice;
        this.codeUni = codeUni;
        this.numLot = numLot;
        this.quantite = quantite;
        this.price = price;
        this.prixRevient = prixRevient;
        this.scanned = scanned;
        this.dateExp = dateExp;
        this.bonLivraison = bonLivraison;
        this.qteRetourne = qteRetourne;
        this.tva = tva;
        this.originalPrice = originalPrice;
        this.keyInvoice = keyInvoice;
        this.warehouse = warehouse;
        this.igicumaStatus = igicumaStatus;
        this.taxCode = taxCode;
        this.state = state;
    }
}

