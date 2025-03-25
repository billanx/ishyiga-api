package com.ishyiga.service;

import com.ishyiga.entities.ListItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface ListItemService {
    ListItem saveListItem(ListItem listItem);
    Page<ListItem> getAllListItems(Pageable pageable);
    Optional<ListItem> getListItemById(Integer id);
    ListItem updateListItem(Integer id, ListItem listItem);
    void deleteListItem(Integer id);
}

