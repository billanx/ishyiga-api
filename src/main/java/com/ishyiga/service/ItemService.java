
package com.ishyiga.service;

import com.ishyiga.entities.Item;

import java.util.List;

public interface ItemService {
     List<Item> getAllItems();
     Item saveItem(Item item);
     void deleteItem(Long id);
}
