package com.huy3999.schedules.dragboardview.callback;

import com.huy3999.schedules.dragboardview.model.DragColumn;

public interface DragHorizontalAdapter {
    void onDrag(int position);

    /**
     * call if on dropItem item
     * @param page item position in Entry List (which in HorizontalRecyclerView)
     * @param position dropItem item to position in entry.getItemList()
     *                 0 <= position <= size()-1
     */
    void onDrop(int page, int position, DragColumn tag);

    /**
     * call if on dragItem out of current entry page
     */
    void onDragOut();

    /**
     * call if on dragItem in current entry page
     *
     * @param position item position in Item List (which in VerticalRecyclerView)
     *                 0 <= position <= size()-1
     */

    void onDragIn(int position, DragColumn dragColumnObject);

    /**
     * call if event.getAction() == MotionEvent.ACTION_MOVE
     *
     * @param position item position in Item List (VerticalRecyclerView)
     *                 0 <= position <= size()-1
     */
    void updateDragItemVisibility(int position);
}
