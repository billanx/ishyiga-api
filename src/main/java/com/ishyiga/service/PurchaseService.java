package com.ishyiga.service;

import com.ishyiga.entities.Purchase;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface PurchaseService {
    Page<Purchase> getAllPurchases(Pageable pageable);
    Purchase savePurchase(Purchase purchase);

    Map<String, List<?>> updatePurchases(List<Purchase> Purchase);
    void deletePurchase(Long id);
}
