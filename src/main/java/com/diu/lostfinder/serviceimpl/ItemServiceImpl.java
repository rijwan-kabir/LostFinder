package com.diu.lostfinder.serviceimpl;

import com.diu.lostfinder.entity.Item;
import com.diu.lostfinder.entity.User;
import com.diu.lostfinder.repository.ItemRepository;
import com.diu.lostfinder.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ItemServiceImpl implements ItemService {

    @Autowired
    private ItemRepository itemRepository;

    @Override
    public Item saveItem(Item item) {
        return itemRepository.save(item);
    }

    @Override
    public List<Item> getAllItems() {
        return itemRepository.findAll();
    }

    @Override
    public Optional<Item> getItemById(Long id) {
        return itemRepository.findById(id);
    }

    @Override
    public List<Item> getItemsByUser(User user) {
        return itemRepository.findByPostedBy(user);
    }

    @Override
    public List<Item> getPendingItems() {
        return itemRepository.findByStatus(Item.ItemStatus.PENDING);
    }

    @Override
    public List<Item> getApprovedItems() {
        return itemRepository.findByStatus(Item.ItemStatus.APPROVED);
    }

    @Override
    public List<Item> getItemsByType(Item.ItemType type) {
        return itemRepository.findByType(type);
    }

    @Override
    public Item updateItem(Item item) {
        return itemRepository.save(item);
    }

    @Override
    public void deleteItem(Long id) {
        itemRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void approveItem(Long id) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Item not found"));
        item.setStatus(Item.ItemStatus.APPROVED);
        item.setApprovedAt(LocalDateTime.now());
        itemRepository.save(item);
    }

    @Override
    @Transactional
    public void rejectItem(Long id) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Item not found"));
        item.setStatus(Item.ItemStatus.REJECTED);
        itemRepository.save(item);
    }
}