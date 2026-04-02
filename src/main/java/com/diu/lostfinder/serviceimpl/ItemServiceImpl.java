package com.diu.lostfinder.serviceimpl;

import com.diu.lostfinder.entity.Item;
import com.diu.lostfinder.entity.User;
import com.diu.lostfinder.repository.ItemRepository;
import com.diu.lostfinder.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ItemServiceImpl implements ItemService {

    @Autowired
    private ItemRepository itemRepository;

    @Override
    public Item saveItem(Item item) {
        itemRepository.save(item);
        return item;
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
        return itemRepository.findByUserId(user.getId());
    }

    @Override
    public List<Item> getPendingItems() {
        return itemRepository.findByStatus("PENDING");
    }

    @Override
    public List<Item> getApprovedItems() {
        return itemRepository.findByStatus("APPROVED");
    }

    @Override
    public List<Item> getItemsByType(String type) {
        return itemRepository.findByType(type);
    }

    @Override
    public void approveItem(Long id) {
        itemRepository.approveItem(id, null);
    }

    @Override
    public void rejectItem(Long id) {
        itemRepository.rejectItem(id);
    }
}