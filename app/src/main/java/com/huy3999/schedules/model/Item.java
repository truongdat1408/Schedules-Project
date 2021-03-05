package com.huy3999.schedules.model;

import com.google.gson.annotations.SerializedName;
import com.huy3999.schedules.dragboardview.model.DragItem;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
@Entity(tableName = "item")
public class Item implements DragItem {
    @NonNull
    @PrimaryKey()
    @SerializedName("id")
    public final String id;
    @ColumnInfo(name = "name")
    @SerializedName("name")
    public String name;
    @ColumnInfo(name = "description")
    @SerializedName("description")
    public String description;
    @ColumnInfo(name = "state")
    @SerializedName("state")
    public final String state;
    @ColumnInfo(name = "project_id")
    @SerializedName("project_id")
    public final String project_id;
    @ColumnInfo(name = "member")
    @SerializedName("member")
    public final ArrayList<String> member;


    public Item(String id, String name, String description, String state, String project_id, ArrayList<String> member) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.state = state;
        this.project_id = project_id;
        this.member = member;
    }

    @Override
    public int getColumnIndex() {
        return 0;
    }

    @Override
    public int getItemIndex() {
        return 0;
    }

    @Override
    public void setColumnIndex(int columnIndexInHorizontalRecycleView) {

    }

    @Override
    public void setItemIndex(int itemIndexInVerticalRecycleView) {

    }
}


