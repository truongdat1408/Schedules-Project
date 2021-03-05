package com.huy3999.schedules.roomcache;

import com.huy3999.schedules.dragboardview.model.DragItem;
import com.huy3999.schedules.model.Item;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface ItemDao {
    @Query("SELECT * FROM item")
    List<Item> loadAllItems();

    @Query("SELECT * FROM item WHERE project_id = :project_id AND state = :state")
    List<Item> loadAllItemByState(String project_id, String state);
    @Query("DELETE FROM item")
    public void clear();
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertItem(Item item);

    @Update
    void updateItem(Item item);

    @Delete
    void delete(Item item);

}