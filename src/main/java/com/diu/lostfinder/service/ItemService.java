package com.diu.lostfinder.service;

import com.diu.lostfinder.entity.Item;
import com.diu.lostfinder.entity.User;  // ← Add this import
import com.diu.lostfinder.repository.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ItemService {

    @Autowired
    private ItemRepository itemRepository;

    public Item saveItem(Item item) {
        return itemRepository.save(item);
    }

    public List<Item> getAllItems() {
        return itemRepository.findAll();
    }

    public Optional<Item> getItemById(Long id) {
        return itemRepository.findById(id);
    }

    public List<Item> getItemsByUser(User user) {
        return itemRepository.findByPostedBy(user);
    }

    public List<Item> getPendingItems() {
        return itemRepository.findByStatus(Item.ItemStatus.PENDING);
    }

    public List<Item> getApprovedItems() {
        return itemRepository.findByStatus(Item.ItemStatus.APPROVED);
    }

    public List<Item> getItemsByType(Item.ItemType type) {
        return itemRepository.findByType(type);
    }

    public Item updateItem(Item item) {
        return itemRepository.save(item);
    }

    public void deleteItem(Long id) {
        itemRepository.deleteById(id);
    }

    @Transactional
    public void approveItem(Long id) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Item not found"));
        item.setStatus(Item.ItemStatus.APPROVED);
        item.setApprovedAt(LocalDateTime.now());
        itemRepository.save(item);
    }

    @Transactional
    public void rejectItem(Long id) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Item not found"));
        item.setStatus(Item.ItemStatus.REJECTED);
        itemRepository.save(item);
    }
}