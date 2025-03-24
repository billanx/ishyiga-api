package com.ishyiga.controller;

import com.ishyiga.entities.ListItem;
import com.ishyiga.service.ListItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/list-items")
public class ListItemController {

    @Autowired
    private ListItemService listItemService;

    @PostMapping("/create")
    @PreAuthorize("hasAnyRole('ADMIN','ISHYIGA')")
    public ResponseEntity<ListItem> createListItem(@RequestBody ListItem listItem) {
        return ResponseEntity.ok(listItemService.saveListItem(listItem));
    }

    @GetMapping("/all")
    @PreAuthorize("hasAnyRole('ADMIN','ISHYIGA','BANK')")
    public ResponseEntity<List<ListItem>> getAllListItems() {
        return ResponseEntity.ok(listItemService.getAllListItems());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','ISHYIGA')")
    public ResponseEntity<ListItem> getListItemById(@PathVariable Integer id) {
        Optional<ListItem> listItem = listItemService.getListItemById(id);
        return listItem.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','ISHYIGA')")
    public ResponseEntity<ListItem> updateListItem(@PathVariable Integer id, @RequestBody ListItem listItem) {
        return ResponseEntity.ok(listItemService.updateListItem(id, listItem));
    }

    @DeleteMapping("/{id}")
    @PutMapping("/{id}")@PreAuthorize("hasAnyRole('ADMIN','ISHYIGA')")
    public ResponseEntity<Void> deleteListItem(@PathVariable Integer id) {
        listItemService.deleteListItem(id);
        return ResponseEntity.noContent().build();
    }
}

