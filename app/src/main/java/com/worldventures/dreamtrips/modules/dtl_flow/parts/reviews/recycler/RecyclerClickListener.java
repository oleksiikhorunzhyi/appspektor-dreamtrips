package com.worldventures.dreamtrips.modules.dtl_flow.parts.reviews.recycler;

import android.view.View;

/**
 * Created by Andres Rubiano Del Chiaro on 14/10/15.
 */
public interface RecyclerClickListener {
    void onClick(View view, int position);

    void onLongClick(View view, int position);
}