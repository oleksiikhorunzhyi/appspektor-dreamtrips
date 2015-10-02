package com.worldventures.dreamtrips.modules.feed.view.util;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v7.widget.ListPopupWindow;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;


import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.friends.model.Circle;

import java.util.List;

public class CirclesFilterPopupWindow {

    private static final int MAX_ITEM_COUNT = 5;

    Context context;
    ListPopupWindow listPopupWindow;

    private int width = 0;
    private int height = 0;

    public CirclesFilterPopupWindow(Context context){
        this.context = context;
        listPopupWindow = new ListPopupWindow(context);
    }

    public void setAnchorView(View view){
        listPopupWindow.setAnchorView(view);
    }

    public void setCircles(List<Circle> circles){
        ArrayAdapter<Circle> filterAdapter = new ArrayAdapter(context, R.layout.filter_item_adapter, R.id.tv_filter_name);
        filterAdapter.addAll(circles);
        listPopupWindow.setAdapter(filterAdapter);
        calculateBounds(circles);
    }

    private void calculateBounds(List<Circle> circles){
        width  = calculateWidth(circles);
        height = calculateHeight(circles);
    }

    private int calculateWidth(List<Circle> circles){
        View view = View.inflate(context, R.layout.filter_item_adapter, null);
        TextView textView = (TextView) view.findViewById(R.id.tv_filter_name);
        Paint paint = textView.getPaint();
        Rect bounds = new Rect();
        int maxWidth = 0;

        for (Circle circle: circles){
            String circleName = circle.getName();
            paint.getTextBounds(circleName, 0, circleName.length(), bounds);
            int width = bounds.width();
            if (width > maxWidth) maxWidth = width;
        }

        return context.getResources().getDimensionPixelOffset(R.dimen.popup_filter_item_padding_left)
                + maxWidth
                + context.getResources().getDimensionPixelOffset(R.dimen.popup_filter_item_padding_right);
    }

    private int calculateHeight(List<Circle> circles){
        int itemHeight = context.getResources().getDimensionPixelOffset(R.dimen.popup_filter_item_height);
        return (circles.size() < MAX_ITEM_COUNT)? itemHeight*circles.size() : itemHeight*MAX_ITEM_COUNT;
    }

    public void show(){
        setBounds();
        listPopupWindow.show();
    }

    private void setBounds(){
        listPopupWindow.setHeight(height);
        listPopupWindow.setWidth(width);
    }

    public void dismiss(){
        listPopupWindow.dismiss();
    }

    public void setOnItemClickListener(AdapterView.OnItemClickListener clickListener) {
        listPopupWindow.setOnItemClickListener(clickListener);
    }
}
