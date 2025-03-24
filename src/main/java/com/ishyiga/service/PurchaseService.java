
package com.ishyiga.service;

import com.ishyiga.entities.Purchase;
import com.ishyiga.entities.Sale;

import java.util.List;
import java.util.Map;

public interface PurchaseService {
    List<Purchase> getAllPurchases();
    Purchase savePurchase(Purchase purchase);

    Map<String, List<?>> updatePurchases(List<Purchase> Purchase);
    void deletePurchase(Long id);
}
