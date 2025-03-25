package com.ishyiga.service.impl;

import com.ishyiga.entities.Purchase;
import com.ishyiga.entities.Sale;
import com.ishyiga.exception.BadRequestException;
import com.ishyiga.exception.DatabaseException;
import com.ishyiga.repo.PurchaseRepository;
import com.ishyiga.service.PurchaseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class PurchaseServiceImpl implements PurchaseService {
    @Autowired
    private PurchaseRepository purchaseRepository;

    @Override
    public Page<Purchase> getAllPurchases(Pageable pageable) {
        return purchaseRepository.findAll(pageable);
    }

    @Override
    public Purchase savePurchase(Purchase purchase) {
        try {
            return purchaseRepository.save(purchase);
        } catch (Exception e) {
            throw new DatabaseException("Error while saving the purchase: " + e.getCause());
        }
    }

    @Override
    public Map<String, List<?>> updatePurchases(List<Purchase> purchases) {
        List<Purchase> successList = new ArrayList<>();
        List<String> failedList = new ArrayList<>();

        for (Purchase dto : purchases) {
            try {
                successList.add(savePurchase(dto));
            } catch (BadRequestException e) {
                log.error("Skipping purchase updates for purchases {} due to error: {}", purchases, e.getMessage());
                failedList.add("Failed to update purchase for " + purchases + ": " + e.getMessage());
            }
        }

        return Map.of("success", successList, "failed", failedList);
    }

    @Override
    public void deletePurchase(Long id) {
        purchaseRepository.deleteById(id);
    }
}
