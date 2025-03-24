package com.ishyiga.service;

import com.ishyiga.entities.ListItem;

import java.util.List;
import java.util.Optional;

public interface ListItemService {
    ListItem saveListItem(ListItem listItem);
    List<ListItem> getAllListItems();
    Optional<ListItem> getListItemById(Integer id);
    ListItem updateListItem(Integer id, ListItem listItem);
    void deleteListItem(Integer id);
}

