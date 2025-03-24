package com.ishyiga.repo;

import com.ishyiga.entities.ListItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ListItemRepository extends JpaRepository<ListItem, Integer> {
}

