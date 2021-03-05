package com.huy3999.schedules.dragboardview.callback;

import com.huy3999.schedules.dragboardview.model.DragItem;

public interface DragVerticalAdapter {
    /**
     * call if on dragItem item
     * @param position item position in Item List (which in VerticalRecyclerView)
     *                 0 <= position <= size()-1
     */
    void onDrag(int position);

    /**
     * call if on dropItem item
     * @param page item position in Entry List (which in HorizontalRecyclerView)
     * @param position dropItem item to position in entry.getItemList()
     *                 0 <= position <= size()-1
     * @param tag convert it to your Item object
     */
    void onDrop(int page, int position, DragItem tag);

    /**
     * call if on dragItem out of current entry page
     */
    void onDragOut();

    /**
     * call if on dragItem in of current entry page
     *
     * @param position item position in Item List (which in VerticalRecyclerView)
     *                 0 <= position <= size()-1
     *                 拖动的 View 在纵向 recyclerView 上的 position
     * @param tag convert it to your Item object
     */
//    void onDragIn(int position, DragItem tag);
//
//    void onDragIn(int position, DragItem item);

    void onDragIn(int position, DragItem item);

    /**
     * call if event.getAction() == MotionEvent.ACTION_MOVE
     *
     * @param position item position in Item List (VerticalRecyclerView)
     *                 0 <= position <= size()-1
     *                 拖动的 View 在纵向 recyclerView 上的 position
     */
    void updateDragItemVisibility(int position);
}
