package com.diu.lostfinder.repository;

import com.diu.lostfinder.entity.Item;
import com.diu.lostfinder.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findByPostedBy(User user);
    List<Item> findByStatus(Item.ItemStatus status);
    List<Item> findByType(Item.ItemType type);

    // Add these methods
    @Query("SELECT i FROM Item i WHERE i.postedBy.id = :userId")
    List<Item> findByPostedByUserId(@Param("userId") Long userId);

    @Modifying
    @Transactional
    @Query("DELETE FROM Item i WHERE i.postedBy.id = :userId")
    void deleteByPostedByUserId(@Param("userId") Long userId);
}