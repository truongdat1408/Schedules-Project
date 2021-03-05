package com.huy3999.schedules.dragboardview.callback;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

public interface DragHorizontalViewHolder {
    /**
     * each Horizontal ViewHolder is supposed to have a RecycleView
     * @return RecycleView in ViewHolder which contains vertical items
     */
    RecyclerView getRecyclerView();

    void findViewForContent(View itemView);
    void findViewForFooter(View itemView);
}
