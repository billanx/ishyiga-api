
package com.ishyiga.controller;

import com.ishyiga.entities.Purchase;
import com.ishyiga.entities.Sale;
import com.ishyiga.service.PurchaseService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/purchases")
class PurchaseController {
    @Autowired
    private PurchaseService purchaseService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','ISHYIGA','BANK')")
    public List<Purchase> getAllPurchases() {
        return purchaseService.getAllPurchases();
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','ISHYIGA')")
    public Purchase createPurchase(@RequestBody Purchase purchase) {
        return purchaseService.savePurchase(purchase);
    }

//    @PostMapping(value = "/list", consumes = "application/json")
//    public Map<String, List<?>> updatePurchase(@Valid @RequestBody List<Purchase> purchases){
//        return purchaseService.updatePurchases(purchases);
//    }
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','ISHYIGA')")
    public void deletePurchase(@PathVariable Long id) {
        purchaseService.deletePurchase(id);
    }
}
