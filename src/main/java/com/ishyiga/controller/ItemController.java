package com.ishyiga.controller;

import com.ishyiga.dto.ErrorResponse;
import com.ishyiga.entities.Item;
import com.ishyiga.service.ItemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
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

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/v1/items")
@Slf4j
@Validated
@Tag(name = "Item Management", description = "APIs for managing items")
@CrossOrigin(origins = "*")
public class ItemController {
    private final ItemService itemService;

    @Autowired
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','ISHYIGA','BANK')")
    @Operation(summary = "Get all items", description = "Retrieves a paginated list of all items")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved items"),
        @ApiResponse(responseCode = "403", description = "Access denied"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<?> getAllItems(
            @Parameter(description = "Page number (0-based)")
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @Parameter(description = "Page size")
            @RequestParam(defaultValue = "10") @Min(1) int size,
            @Parameter(description = "Field to sort by")
            @RequestParam(defaultValue = "id") String sortBy,
            @Parameter(description = "Sort direction (asc/desc)")
            @RequestParam(defaultValue = "asc") String direction) {
        try {
            Sort.Direction sortDirection = Sort.Direction.fromString(direction.toUpperCase());
            PageRequest pageRequest = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
            Page<Item> items = itemService.getAllItems(pageRequest);
            return ResponseEntity.ok(items);
        } catch (Exception e) {
            log.error("Failed to retrieve items: {}", e.getMessage(), e);
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("Failed to retrieve items", e.getMessage()));
        }
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','ISHYIGA')")
    @Operation(summary = "Create an item", description = "Creates a new item")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Item created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input"),
        @ApiResponse(responseCode = "403", description = "Access denied"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<?> createItem(@Valid @RequestBody Item item) {
        try {
            validateItem(item);
            Item createdItem = itemService.saveItem(item);
            log.info("Item created successfully with ID: {}", createdItem.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(createdItem);
        } catch (Exception e) {
            log.error("Failed to create item: {}", e.getMessage(), e);
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("Failed to create item", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','ISHYIGA')")
    @Operation(summary = "Update an item", description = "Updates an existing item by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Item updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input"),
        @ApiResponse(responseCode = "403", description = "Access denied"),
        @ApiResponse(responseCode = "404", description = "Item not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<?> updateItem(
            @Parameter(description = "ID of the item to update")
            @PathVariable @Min(1) Long id,
            @Valid @RequestBody Item item) {
        try {
            validateItem(item);
            item.setId(id);
            Item updatedItem = itemService.saveItem(item);
            log.info("Item {} updated successfully", id);
            return ResponseEntity.ok(updatedItem);
        } catch (Exception e) {
            log.error("Failed to update item {}: {}", id, e.getMessage(), e);
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("Failed to update item", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete an item", description = "Deletes an item by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Item deleted successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied"),
        @ApiResponse(responseCode = "404", description = "Item not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<?> deleteItem(
            @Parameter(description = "ID of the item to delete")
            @PathVariable @Min(1) Long id) {
        try {
            itemService.deleteItem(id);
            log.info("Item {} deleted successfully", id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            log.error("Failed to delete item {}: {}", id, e.getMessage(), e);
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("Failed to delete item", e.getMessage()));
        }
    }

    private void validateItem(Item item) {
        if (item.getIdProduct() == null || item.getIdProduct().trim().isEmpty()) {
            throw new IllegalArgumentException("Product ID is required");
        }
        if (item.getNameProduct() == null || item.getNameProduct().trim().isEmpty()) {
            throw new IllegalArgumentException("Product name is required");
        }
        if (item.getPrix() == null || item.getPrix().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Price must be greater than 0");
        }
    }
}
