package com.huy3999.schedules.model;

import com.huy3999.schedules.dragboardview.model.DragColumn;
import com.huy3999.schedules.dragboardview.model.DragItem;

import java.util.List;

public class Entry implements DragColumn {
    private  String id;
    private  String name;
    private  List<DragItem> itemList;

    public Entry(String id, String name, List<DragItem> items) {
        this.id = id;
        this.name = name;
        this.itemList = items;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setItemList(List<DragItem> itemList) {
        this.itemList = itemList;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<DragItem> getItemList() {
        return itemList;
    }

    @Override
    public int getColumnIndex() {
        return 0;
    }

    @Override
    public void setColumnIndex(int columnIndexInHorizontalRecycleView) {

    }
}
