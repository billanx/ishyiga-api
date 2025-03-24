package com.ishyiga.entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;
import java.util.List;

@Entity
@Table(name = "INVOICE",
        uniqueConstraints = @UniqueConstraint(columnNames = {"id_invoice", "num_client"}))
@Data
public class Invoice {

    @Id
    @Column(name="ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "ID_INVOICE")
    private Integer idInvoice;

    @Column(name = "NUM_CLIENT", length = 100)
    private String numClient;

    @Column(name = "DATE", length = 20)
    private String date;

    @Column(name = "TOTAL")
    private Double total;

    @Column(name = "EMPLOYE", length = 500)
    private String employe;

    @Column(name = "HEURE", nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Timestamp heure;

    @Column(name = "TVA")
    private Double tva;

    @Column(name = "DOCUMENT", columnDefinition = "INTEGER DEFAULT 0")
    private Integer document;

    @Column(name = "SCANNED", length = 3, columnDefinition = "CHAR(3) DEFAULT '1'")
    private String scanned;

    @Column(name = "MODE", length = 100, columnDefinition = "VARCHAR(100) DEFAULT ''")
    private String mode;

    @Column(name = "STATUS", length = 100, columnDefinition = "VARCHAR(100) DEFAULT ''")
    private String status;

    @Column(name = "SERVED")
    private Timestamp served;

    @Column(name = "RETOUR", length = 10, nullable = false, columnDefinition = "VARCHAR(10) DEFAULT 'OUI'")
    private String retour;

    @Column(name = "NUM_FACT", length = 15, nullable = false, columnDefinition = "VARCHAR(15) DEFAULT 'FG'")
    private String numFact;

    @Column(name = "REFERENCE", length = 200, columnDefinition = "VARCHAR(200) DEFAULT ' '")
    private String reference;

//    @JsonManagedReference
    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ListItem> listItems;

    // Default Constructor
    public Invoice() {}

    // Parameterized Constructor
    public Invoice(String numClient, String date, Double total, String employe, Timestamp heure, Double tva,
                   Integer document, String scanned, String mode, String status, Timestamp served, String retour,
                   String numFact, String reference) {
        this.numClient = numClient;
        this.date = date;
        this.total = total;
        this.employe = employe;
        this.heure = heure;
        this.tva = tva;
        this.document = document;
        this.scanned = scanned;
        this.mode = mode;
        this.status = status;
        this.served = served;
        this.retour = retour;
        this.numFact = numFact;
        this.reference = reference;
    }

    public List<ListItem> getListItems() { return listItems; }
    public void setListItems(List<ListItem> listItems) {
        this.listItems = listItems;
        if (listItems != null) {
            for (ListItem item : listItems) {
                item.setInvoice(this); // Ensure relationship consistency
            }
        }
    }
}

