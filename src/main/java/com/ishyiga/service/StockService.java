package com.ishyiga.service;

import com.ishyiga.entities.Stock;
import com.ishyiga.repo.StockRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

public interface StockService {

    // Create or Update a Stock record
    Stock saveStock(Stock stock);

    // Get a Stock by client_id
    Optional<Stock> getStockByClientId(String clientId);

    // Get all Stock records with pagination
    Page<Stock> getAllStocks(Pageable pageable);

    // Delete a Stock by client_id
    void deleteStockByClientId(String clientId);
}
