package com.ishyiga.service.impl;

import com.ishyiga.entities.Stock;
import com.ishyiga.repo.StockRepository;
import com.ishyiga.service.StockService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class StockServiceImpl implements StockService {
    @Autowired
    private StockRepository stockRepository;

    // Create or Update a Stock record
    public Stock saveStock(Stock stock) {
        return stockRepository.save(stock);
    }

    // Get a Stock by client_id
    public Optional<Stock> getStockByClientId(String clientId) {
        return stockRepository.findById(clientId);
    }

    // Get all Stock records with pagination
    public Page<Stock> getAllStocks(Pageable pageable) {
        return stockRepository.findAll(pageable);
    }

    // Delete a Stock by client_id
    public void deleteStockByClientId(String clientId) {
        stockRepository.deleteById(clientId);
    }
}
