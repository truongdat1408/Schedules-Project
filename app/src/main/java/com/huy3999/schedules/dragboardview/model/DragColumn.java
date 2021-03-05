package com.huy3999.schedules.dragboardview.model;

import java.util.List;

import androidx.annotation.IntRange;

public interface DragColumn {
    List<? extends DragItem> getItemList();

    @IntRange(from = 0)
    int getColumnIndex();

    void setColumnIndex(@IntRange(from = 0) int columnIndexInHorizontalRecycleView);
}
