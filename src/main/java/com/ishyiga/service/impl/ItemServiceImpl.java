
package com.ishyiga.service.impl;

import com.ishyiga.entities.Item;
import com.ishyiga.repo.ItemRepository;
import com.ishyiga.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ItemServiceImpl implements ItemService {
    @Autowired
    private ItemRepository itemRepository;
    public List<Item> getAllItems() { return itemRepository.findAll(); }
    public Item saveItem(Item item) { return itemRepository.save(item); }
    public void deleteItem(Long id) { itemRepository.deleteById(id); }
}
