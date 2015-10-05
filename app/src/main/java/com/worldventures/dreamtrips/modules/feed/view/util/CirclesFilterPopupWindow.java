package com.worldventures.dreamtrips.modules.feed.view.util;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.v7.widget.ListPopupWindow;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.ListView;
import android.widget.TextView;


import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.friends.model.Circle;

import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class CirclesFilterPopupWindow {

    Context context;
    ListPopupWindow listPopupWindow;

    ArrayAdapter<Circle> adapter;
    Circle checkedCircle;

    private int width = 0;

    public CirclesFilterPopupWindow(Context context){
        this.context = context;
        listPopupWindow = new ListPopupWindow(context);
    }

    public void setAnchorView(View view){
        listPopupWindow.setAnchorView(view);
    }

    public void setCircles(List<Circle> circles){
        adapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_single_choice, circles);
        listPopupWindow.setAdapter(adapter);
        calculateWidth(circles);
    }

    private void calculateWidth(List<Circle> circles){
        TextView textView = (CheckedTextView) View.inflate(context, android.R.layout.simple_list_item_single_choice, null);
        Paint paint = textView.getPaint();
        Rect bounds = new Rect();
        int maxWidth = 0;

        for (Circle circle: circles){
            String circleName = circle.getName();
            paint.getTextBounds(circleName, 0, circleName.length(), bounds);
            int width = bounds.width();
            if (width > maxWidth) maxWidth = width;
        }

        width = maxWidth + context.getResources().getDimensionPixelOffset(R.dimen.popup_filter_item_extra_space);
    }

    public void setCheckedCircle(@NonNull Circle checkedCircle) {
        this.checkedCircle = checkedCircle;
        checkCircle();
    }

    public void show(){
        setBounds();
        listPopupWindow.show();
        listPopupWindow.getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
    }

    private void setBounds(){
        listPopupWindow.setHeight(ListPopupWindow.WRAP_CONTENT);
        listPopupWindow.setWidth(width);
    }

    private void checkCircle(){
        ListView listView = listPopupWindow.getListView();

        if (listView == null) return;

        for (int position = 0; position < adapter.getCount(); position++){
            if (StringUtils.equals(adapter.getItem(position).getId(), checkedCircle.getId())){
                listView.setItemChecked(position, true);
                break;
            }
        }
    }

    public void dismiss(){
        listPopupWindow.dismiss();
    }

    public void setOnItemClickListener(AdapterView.OnItemClickListener clickListener) {
        listPopupWindow.setOnItemClickListener(clickListener);
    }
}
