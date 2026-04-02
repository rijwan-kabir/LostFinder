package com.diu.lostfinder.service;

import com.diu.lostfinder.entity.Item;
import com.diu.lostfinder.entity.User;
import java.util.List;
import java.util.Optional;

public interface ItemService {

    Item saveItem(Item item);

    List<Item> getAllItems();

    Optional<Item> getItemById(Long id);

    List<Item> getItemsByUser(User user);

    List<Item> getPendingItems();

    List<Item> getApprovedItems();

    List<Item> getItemsByType(String type);

    void approveItem(Long id);

    void rejectItem(Long id);
}