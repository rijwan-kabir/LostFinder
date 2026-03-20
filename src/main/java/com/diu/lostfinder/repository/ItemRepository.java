package com.diu.lostfinder.repository;

import com.diu.lostfinder.entity.Item;
import com.diu.lostfinder.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findByPostedBy(User user);
    List<Item> findByStatus(Item.ItemStatus status);
    List<Item> findByType(Item.ItemType type);
    List<Item> findByStatusAndType(Item.ItemStatus status, Item.ItemType type);
}