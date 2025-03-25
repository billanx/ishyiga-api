package com.ishyiga.service.impl;

import com.ishyiga.entities.Item;
import com.ishyiga.exception.DatabaseException;
import com.ishyiga.repo.ItemRepository;
import com.ishyiga.service.ItemService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ItemServiceImpl implements ItemService {
    @Autowired
    private ItemRepository itemRepository;

    @Override
    public Page<Item> getAllItems(Pageable pageable) {
        try {
            return itemRepository.findAll(pageable);
        } catch (Exception e) {
            log.error("Error retrieving items: {}", e.getMessage(), e);
            throw new DatabaseException("Error retrieving items: " + e.getMessage());
        }
    }

    @Override
    public Item saveItem(Item item) {
        try {
            return itemRepository.save(item);
        } catch (Exception e) {
            log.error("Error saving item: {}", e.getMessage(), e);
            throw new DatabaseException("Error saving item: " + e.getMessage());
        }
    }

    @Override
    public void deleteItem(Long id) {
        try {
            itemRepository.deleteById(id);
        } catch (Exception e) {
            log.error("Error deleting item with ID {}: {}", id, e.getMessage(), e);
            throw new DatabaseException("Error deleting item: " + e.getMessage());
        }
    }
}
