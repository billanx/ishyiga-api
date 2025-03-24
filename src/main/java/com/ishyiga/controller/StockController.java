package com.ishyiga.controller;

import com.ishyiga.entities.Stock;
import com.ishyiga.service.StockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1/stocks")
public class StockController {

    @Autowired
    private StockService stockService;

    // Create or Update a Stock
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','ISHYIGA')")
    public ResponseEntity<Stock> createOrUpdateStock(@RequestBody Stock stock) {
        Stock savedStock = stockService.saveStock(stock);
        return new ResponseEntity<>(savedStock, HttpStatus.CREATED);
    }

    // Get a Stock by client_id
    @GetMapping("/{client_id}")
    @PreAuthorize("hasAnyRole('ADMIN','ISHYIGA','BANK')")
    public ResponseEntity<Stock> getStockByClientId(@PathVariable String client_id) {
        Optional<Stock> stock = stockService.getStockByClientId(client_id);
        return stock.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Get all Stocks
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','ISHYIGA','BANK')")
    public ResponseEntity<Iterable<Stock>> getAllStocks() {
        Iterable<Stock> stocks = stockService.getAllStocks();
        return ResponseEntity.ok(stocks);
    }

    // Delete a Stock by client_id
    @DeleteMapping("/{client_id}")
    @PreAuthorize("hasAnyRole('ADMIN','ISHYIGA')")
    public ResponseEntity<Void> deleteStockByClientId(@PathVariable String client_id) {
        stockService.deleteStockByClientId(client_id);
        return ResponseEntity.noContent().build();
    }
}
