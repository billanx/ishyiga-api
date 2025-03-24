package com.ishyiga.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Entity
@Table(name = "items")
@Data
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "id_product")
    private String idProduct;

    @Column(name = "name_product")
    private String nameProduct;

    @Column(name = "code")
    private String code;

    @Column(name = "prix")
    private BigDecimal prix;

    @Column(name = "prix_societe")
    private BigDecimal prixSociete;

    @Column(name = "prix_rama")
    private BigDecimal prixRama;

    @Column(name = "prix_corar")
    private BigDecimal prixCorar;

    @Column(name = "prix_mmi")
    private BigDecimal prixMmi;

    @Column(name = "prix_soras")
    private BigDecimal prixSoras;

    @Column(name = "prix_van")
    private BigDecimal prixVan;

    @Column(name = "tva")
    private BigDecimal tva;

    @Column(name = "observation")
    private String observation;

    @Column(name = "code_bar")
    private String codeBar;

    @Column(name = "code_soc_rama")
    private String codeSocRama;

    @Column(name = "code_soc_mmi")
    private String codeSocMmi;

    @Column(name = "code_soc_soras")
    private String codeSocSoras;

    @Column(name = "code_soc_corar")
    private String codeSocCorar;

    @Column(name = "prix_revient")
    private BigDecimal prixRevient;

    @Column(name = "coef")
    private BigDecimal coef;

    @Column(name = "famille")
    private String famille;

    @Column(name = "maximum")
    private Integer maximum;

    @Column(name = "code_frss")
    private String codeFrss;

    @Column(name = "designation_frss")
    private String designationFrss;

    @Column(name = "conditionnement")
    private String conditionnement;

    @Column(name = "prix_aar")
    private BigDecimal prixAar;

    @Column(name = "code_soc_aar")
    private String codeSocAar;

    @Column(name = "prix_unr")
    private BigDecimal prixUnr;

    @Column(name = "id_new")
    private String idNew;

    @Column(name = "status")
    private String status;

    @Column(name = "fabricant")
    private String fabricant;

    @Column(name = "keycode")
    private String keycode;

    @Column(name = "last_stored_bup")
    private String lastStoredBup;

    @Column(name = "dosage")
    private String dosage;

    @Column(name = "interaction")
    private String interaction;

    @Column(name = "prix_ur")
    private BigDecimal prixUr;

    @Column(name = "prix_t1")
    private BigDecimal prixT1;

    @Column(name = "prix_t2")
    private BigDecimal prixT2;

    @Column(name = "prix_t3")
    private BigDecimal prixT3;

    @Column(name = "changed")
    private Boolean changed;

    @Column(name = "prix_t4")
    private BigDecimal prixT4;

    @Column(name = "prix_t5")
    private BigDecimal prixT5;

    @Column(name = "prix_barreau")
    private BigDecimal prixBarreau;

    @Column(name = "observation_soc")
    private String observationSoc;

    @Column(name = "prix_pactilis")
    private BigDecimal prixPactilis;

    @Column(name = "insurance_name")
    private String insuranceName;

    @Column(name = "atc_code")
    private String atcCode;

    @Column(name = "inn")
    private String inn;

    @Column(name = "hs_code")
    private String hsCode;

    @Column(name = "prix_gashora")
    private BigDecimal prixGashora;

    @Column(name = "code_soc_pih")
    private String codeSocPih;

    @Column(name = "code_classement")
    private String codeClassement;

    @Column(name = "items_class")
    private String itemsClass;

    @Column(name = "tax_code")
    private String taxCode;

    @Column(name = "prix_britam")
    private BigDecimal prixBritam;

    @Column(name = "prix_farg")
    private BigDecimal prixFarg;

    @Column(name = "prix_unr2")
    private BigDecimal prixUnr2;

    @Column(name = "prix_uap")
    private BigDecimal prixUap;

    @Column(name = "prix_allianz")
    private BigDecimal prixAllianz;

    @Column(name = "prix_bupa")
    private BigDecimal prixBupa;

    @Column(name = "prix_rba")
    private BigDecimal prixRba;

    @Column(name = "prix_vuba")
    private BigDecimal prixVuba;

    @Column(name = "prix_rbar")
    private BigDecimal prixRbar;

    @Column(name = "prix_saham")
    private BigDecimal prixSaham;

    @Column(name = "prix_sanlam")
    private BigDecimal prixSanlam;

    @Column(name = "item_inn_rwa_code")
    private String itemInnRwaCode;

    @Column(name = "scan")
    private String scan;

    @Column(name = "prix_radiant")
    private BigDecimal prixRadiant;

    @Column(name = "prix_magerwa")
    private BigDecimal prixMagerwa;

    @Column(name = "prix_bralirwa")
    private BigDecimal prixBralirwa;

    @Column(name = "prix_equity")
    private BigDecimal prixEquity;

    @Column(name = "prix_cogebanque")
    private BigDecimal prixCogebanque;

    @Column(name = "prix_urwego")
    private BigDecimal prixUrwego;

    @Column(name = "prix_rbc")
    private BigDecimal prixRbc;

    @Column(name = "prix_marriott")
    private BigDecimal prixMarriott;

    @Column(name = "prix_vanbreda")
    private BigDecimal prixVanbreda;

    @Column(name = "prix_mis")
    private BigDecimal prixMis;

    @Column(name = "prix_prime")
    private BigDecimal prixPrime;

    @Column(name = "niki_code")
    private String nikiCode;

    @Column(name = "niki_descr")
    private String nikiDescr;

    @Column(name = "niki_status")
    private String nikiStatus;

    @Column(name = "key_word")
    private String keyWord;

    @Column(name = "molecule")
    private String molecule;

    @Column(name = "poids")
    private BigDecimal poids;

    @Column(name = "hauteur")
    private BigDecimal hauteur;

    @Column(name = "longeur")
    private BigDecimal longeur;

    @Column(name = "largeur")
    private BigDecimal largeur;

    @Column(name = "package")
    private String packageType;

    @Column(name = "prix_oxfam")
    private BigDecimal prixOxfam;

    @Column(name = "prix_brussels")
    private BigDecimal prixBrussels;

    @Column(name = "id_product_niki")
    private String idProductNiki;

    @Column(name = "prix_psf")
    private BigDecimal prixPsf;

    @Column(name = "suivie_stock")
    private Boolean suivieStock;

    @Column(name = "prix_eden")
    private BigDecimal prixEden;

    @Column(name = "prix_ubuzima")
    private BigDecimal prixUbuzima;

    @Column(name = "prix_phi")
    private BigDecimal prixPhi;

    @Column(name = "prix_unhcr")
    private BigDecimal prixUnhcr;

    @Column(name = "kaos_item")
    private String kaosItem;

    @Column(name = "prix_save")
    private BigDecimal prixSave;

    @Column(name = "item_type")
    private String itemType;

    @Column(name = "qty_unit")
    private String qtyUnit;

    @Column(name = "pck_uniy")
    private String pckUniry;

    @Column(name = "manufacture")
    private String manufacture;

    @Column(name = "prix_msh")
    private BigDecimal prixMsh;

    // Getters and Setters
    // Add getters and setters for all fields
}

