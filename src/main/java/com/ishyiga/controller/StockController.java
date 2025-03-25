package com.ishyiga.controller;

import com.ishyiga.dto.ErrorResponse;
import com.ishyiga.entities.Stock;
import com.ishyiga.service.StockService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1/stocks")
@Slf4j
@Validated
@Tag(name = "Stock Management", description = "APIs for managing stock records")
@CrossOrigin(origins = "*")
public class StockController {

    private final StockService stockService;

    @Autowired
    public StockController(StockService stockService) {
        this.stockService = stockService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN','ISHYIGA')")
    @Operation(summary = "Create or update a stock record", description = "Creates a new stock record or updates an existing one")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Stock created/updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input"),
        @ApiResponse(responseCode = "403", description = "Access denied"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<?> createOrUpdateStock(@Valid @RequestBody Stock stock) {
        try {
            if (stock.getClientId() == null || stock.getClientId().trim().isEmpty()) {
                return ResponseEntity
                    .badRequest()
                    .body(new ErrorResponse("Invalid input", "Client ID cannot be empty"));
            }
            if (stock.getTotalValue() == null || stock.getTotalValue() < 0) {
                return ResponseEntity
                    .badRequest()
                    .body(new ErrorResponse("Invalid input", "Total value must be non-negative"));
            }
            Stock savedStock = stockService.saveStock(stock);
            log.info("Stock created/updated successfully for clientId: {}", savedStock.getClientId());
            return ResponseEntity.status(HttpStatus.CREATED).body(savedStock);
        } catch (Exception e) {
            log.error("Failed to create/update stock: {}", e.getMessage(), e);
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("Failed to create/update stock", e.getMessage()));
        }
    }

    @GetMapping("/{clientId}")
    @PreAuthorize("hasAnyRole('ADMIN','ISHYIGA','BANK')")
    @Operation(summary = "Get stock by client ID", description = "Retrieves a stock record by its client ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Stock found"),
        @ApiResponse(responseCode = "403", description = "Access denied"),
        @ApiResponse(responseCode = "404", description = "Stock not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<?> getStockByClientId(
            @Parameter(description = "Client ID of the stock to retrieve")
            @PathVariable @NotBlank(message = "Client ID cannot be empty") String clientId) {
        try {
            Optional<Stock> stock = stockService.getStockByClientId(clientId);
            if (stock.isPresent()) {
                return ResponseEntity.ok(stock.get());
            }
            return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse("Stock not found", "No stock found for clientId: " + clientId));
        } catch (Exception e) {
            log.error("Failed to retrieve stock for clientId {}: {}", clientId, e.getMessage(), e);
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("Failed to retrieve stock", e.getMessage()));
        }
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','ISHYIGA','BANK')")
    @Operation(summary = "Get all stocks", description = "Retrieves a paginated list of all stock records")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved stocks"),
        @ApiResponse(responseCode = "403", description = "Access denied"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<?> getAllStocks(
            @Parameter(description = "Page number (0-based)")
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @Parameter(description = "Page size")
            @RequestParam(defaultValue = "10") @Min(1) int size,
            @Parameter(description = "Field to sort by")
            @RequestParam(defaultValue = "clientId") String sortBy,
            @Parameter(description = "Sort direction (asc/desc)")
            @RequestParam(defaultValue = "asc") String direction) {
        try {
            Sort.Direction sortDirection = Sort.Direction.fromString(direction.toUpperCase());
            PageRequest pageRequest = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
            Page<Stock> stocks = stockService.getAllStocks(pageRequest);
            return ResponseEntity.ok(stocks);
        } catch (Exception e) {
            log.error("Failed to retrieve stocks: {}", e.getMessage(), e);
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("Failed to retrieve stocks", e.getMessage()));
        }
    }

    @PutMapping("/{clientId}")
    @PreAuthorize("hasAnyRole('ADMIN','ISHYIGA')")
    @Operation(summary = "Update a stock record", description = "Updates an existing stock record by its client ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Stock updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input"),
        @ApiResponse(responseCode = "403", description = "Access denied"),
        @ApiResponse(responseCode = "404", description = "Stock not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<?> updateStock(
            @Parameter(description = "Client ID of the stock to update")
            @PathVariable @NotBlank(message = "Client ID cannot be empty") String clientId,
            @Valid @RequestBody Stock stock) {
        try {
            validateStock(stock);
            stock.setClientId(clientId);
            Stock updatedStock = stockService.saveStock(stock);
            log.info("Stock for client {} updated successfully", clientId);
            return ResponseEntity.ok(updatedStock);
        } catch (Exception e) {
            log.error("Failed to update stock for client {}: {}", clientId, e.getMessage(), e);
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("Failed to update stock", e.getMessage()));
        }
    }

    @DeleteMapping("/{clientId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete a stock record", description = "Deletes a stock record by its client ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Stock deleted successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied"),
        @ApiResponse(responseCode = "404", description = "Stock not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<?> deleteStock(
            @Parameter(description = "Client ID of the stock to delete")
            @PathVariable @NotBlank(message = "Client ID cannot be empty") String clientId) {
        try {
            stockService.deleteStockByClientId(clientId);
            log.info("Stock for client {} deleted successfully", clientId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            log.error("Failed to delete stock for client {}: {}", clientId, e.getMessage(), e);
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("Failed to delete stock", e.getMessage()));
        }
    }

    private void validateStock(Stock stock) {
        if (stock.getClientId() == null || stock.getClientId().trim().isEmpty()) {
            throw new IllegalArgumentException("Client ID is required");
        }
    }
}
