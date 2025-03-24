package com.ishyiga.service.impl;

import com.ishyiga.entities.ListItem;
import com.ishyiga.exception.DatabaseException;
import com.ishyiga.repo.ListItemRepository;
import com.ishyiga.service.ListItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ListItemServiceImpl implements ListItemService {

    @Autowired
    private ListItemRepository listItemRepository;

    @Override
    public ListItem saveListItem(ListItem listItem) {
        try {
            return listItemRepository.save(listItem);
        } catch (Exception e) {
            throw new DatabaseException("Error while saving the invoice: " + e.getCause());
        }
    }

    @Override
    public List<ListItem> getAllListItems() {
        return listItemRepository.findAll();
    }

    @Override
    public Optional<ListItem> getListItemById(Integer id) {
        return listItemRepository.findById(id);
    }

    @Override
    public ListItem updateListItem(Integer id, ListItem updatedListItem) {
        return listItemRepository.findById(id).map(listItem -> {
            listItem.setCodeUni(updatedListItem.getCodeUni());
            listItem.setNumLot(updatedListItem.getNumLot());
            listItem.setQuantite(updatedListItem.getQuantite());
            listItem.setPrice(updatedListItem.getPrice());
            return listItemRepository.save(listItem);
        }).orElseThrow(() -> new RuntimeException("ListItem not found"));
    }

    @Override
    public void deleteListItem(Integer id) {
        listItemRepository.deleteById(id);
    }
}

