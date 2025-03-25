package com.ishyiga.service;

import com.ishyiga.entities.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ItemService {
    Page<Item> getAllItems(Pageable pageable);
    Item saveItem(Item item);
    void deleteItem(Long id);
}
