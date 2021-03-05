package com.huy3999.schedules.dragboardview.model;

import androidx.annotation.IntRange;

public interface DragItem {
    @IntRange(from = 0)
    int getColumnIndex();

    @IntRange(from = 0)
    int getItemIndex();

    void setColumnIndex(@IntRange(from = 0) int columnIndexInHorizontalRecycleView);

    void setItemIndex(@IntRange(from = 0) int itemIndexInVerticalRecycleView);
}
