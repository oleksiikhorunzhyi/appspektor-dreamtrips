package com.worldventures.dreamtrips.view.adapter.item;

/**
 * Created by 1 on 04.03.15.
 */
public interface Swipeable {

    public long getItemId();

    public int getSwipeReactionType();

    public void setPinnedToSwipeLeft(boolean pinned);

    public boolean isPinnedToSwipeLeft();


}
