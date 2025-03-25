package com.ishyiga.controller;

import com.ishyiga.dto.ErrorResponse;
import com.ishyiga.entities.Invoice;
import com.ishyiga.entities.ListItem;
import com.ishyiga.service.InvoiceService;
import com.ishyiga.service.ListItemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
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

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/list-items")
@Slf4j
@Validated
@Tag(name = "List Item Management", description = "APIs for managing list items")
@CrossOrigin(origins = "*")
public class ListItemController {

    private final ListItemService listItemService;
    private final InvoiceService invoiceService;

    @Autowired
    public ListItemController(ListItemService listItemService, InvoiceService invoiceService) {
        this.listItemService = listItemService;
        this.invoiceService = invoiceService;
    }

    @PostMapping("/create")
    @PreAuthorize("hasAnyRole('ADMIN','ISHYIGA')")
    @Operation(summary = "Create a list item", description = "Creates a new list item")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "List item created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input"),
        @ApiResponse(responseCode = "403", description = "Access denied"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<?> createListItem(@Valid @RequestBody ListItem listItem) {
        try {
            // If invoice is just an ID, create a new Invoice object with that ID
            Object invoiceField = listItem.getInvoice();
            if (invoiceField != null) {
                // Check if the invoice field is a Map (JSON object) or Integer (ID)
                if (invoiceField instanceof Map) {
                    Map<?, ?> invoiceMap = (Map<?, ?>) invoiceField;
                    if (invoiceMap.containsKey("id")) {
                        Integer invoiceId = Integer.valueOf(invoiceMap.get("id").toString());
                        Invoice invoice = new Invoice();
                        invoice.setId(invoiceId);
                        listItem.setInvoice(invoice);
                    }
                } else if (invoiceField instanceof Integer) {
                    Integer invoiceId = (Integer) invoiceField;
                    Invoice invoice = new Invoice();
                    invoice.setId(invoiceId);
                    listItem.setInvoice(invoice);
                }
            }
            validateListItem(listItem);
            
            // Check if the Invoice exists
            if (!invoiceService.getInvoiceById(listItem.getInvoice().getId()).isPresent()) {
                return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("Invalid Invoice", "Invoice with ID " + listItem.getInvoice().getId() + " does not exist"));
            }
            
            // Get the actual invoice from the database
            Optional<Invoice> existingInvoice = invoiceService.getInvoiceById(listItem.getInvoice().getId());
            if (existingInvoice.isPresent()) {
                listItem.setInvoice(existingInvoice.get());
            }
            
            ListItem createdItem = listItemService.saveListItem(listItem);
            log.info("ListItem created successfully with ID: {}", createdItem.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(createdItem);
        } catch (Exception e) {
            log.error("Failed to create list item: {}", e.getMessage(), e);
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("Failed to create list item", e.getMessage()));
        }
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','ISHYIGA','BANK')")
    @Operation(summary = "Get all list items", description = "Retrieves a paginated list of all list items")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved list items"),
        @ApiResponse(responseCode = "403", description = "Access denied"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<?> getAllListItems(
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
            Page<ListItem> listItems = listItemService.getAllListItems(pageRequest);
            return ResponseEntity.ok(listItems);
        } catch (Exception e) {
            log.error("Failed to retrieve list items: {}", e.getMessage(), e);
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("Failed to retrieve list items", e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','ISHYIGA')")
    @Operation(summary = "Get list item by ID", description = "Retrieves a list item by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "List item found"),
        @ApiResponse(responseCode = "403", description = "Access denied"),
        @ApiResponse(responseCode = "404", description = "List item not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<?> getListItemById(
            @Parameter(description = "ID of the list item to retrieve")
            @PathVariable @Min(1) Integer id) {
        try {
            Optional<ListItem> listItem = listItemService.getListItemById(id);
            if (listItem.isPresent()) {
                return ResponseEntity.ok(listItem.get());
            }
            return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse("ListItem not found", "No list item found with id: " + id));
        } catch (Exception e) {
            log.error("Failed to retrieve list item {}: {}", id, e.getMessage(), e);
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("Failed to retrieve list item", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','ISHYIGA')")
    @Operation(summary = "Update a list item", description = "Updates an existing list item by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "List item updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input"),
        @ApiResponse(responseCode = "403", description = "Access denied"),
        @ApiResponse(responseCode = "404", description = "List item not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<?> updateListItem(
            @Parameter(description = "ID of the list item to update")
            @PathVariable @Min(1) Integer id,
            @Valid @RequestBody ListItem listItem) {
        try {
            validateListItem(listItem);
            listItem.setId(id);
            ListItem updatedItem = listItemService.saveListItem(listItem);
            log.info("List item {} updated successfully", id);
            return ResponseEntity.ok(updatedItem);
        } catch (Exception e) {
            log.error("Failed to update list item {}: {}", id, e.getMessage(), e);
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("Failed to update list item", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete a list item", description = "Deletes a list item by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "List item deleted successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied"),
        @ApiResponse(responseCode = "404", description = "List item not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<?> deleteListItem(
            @Parameter(description = "ID of the list item to delete")
            @PathVariable @Min(1) Integer id) {
        try {
            listItemService.deleteListItem(id);
            log.info("List item {} deleted successfully", id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            log.error("Failed to delete list item {}: {}", id, e.getMessage(), e);
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("Failed to delete list item", e.getMessage()));
        }
    }

    private void validateListItem(ListItem listItem) {
        if (listItem.getListIdProduct() == null) {
            throw new IllegalArgumentException("Product ID is required");
        }
        if (listItem.getQuantite() == null || listItem.getQuantite() <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than 0");
        }
        if (listItem.getPrice() == null || listItem.getPrice() <= 0) {
            throw new IllegalArgumentException("Price must be greater than 0");
        }
        if (listItem.getInvoice() == null || listItem.getInvoice().getId() == null) {
            throw new IllegalArgumentException("Invoice ID is required");
        }
    }
}


