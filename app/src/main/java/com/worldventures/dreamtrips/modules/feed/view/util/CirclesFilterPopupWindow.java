package com.worldventures.dreamtrips.modules.feed.view.util;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.v7.widget.ListPopupWindow;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.ListView;
import android.widget.TextView;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.friends.model.Circle;

import java.util.List;

public class CirclesFilterPopupWindow {
   private static final int DISMISS_DELAY = 200;

   Context context;
   ListPopupWindow listPopupWindow;

   ArrayAdapter<Circle> adapter;
   Circle checkedCircle;

   private int width = 0;

   private long timeWhenDismissed;

   public CirclesFilterPopupWindow(Context context) {
      this.context = context;
      listPopupWindow = new ListPopupWindow(context);
      listPopupWindow.setOnDismissListener(() -> timeWhenDismissed = System.currentTimeMillis());
   }

   public void setAnchorView(View view) {
      listPopupWindow.setAnchorView(view);
   }

   public void setCircles(List<Circle> circles) {
      adapter = new ArrayAdapter<>(context, R.layout.list_item_circle_filter, circles);
      listPopupWindow.setAdapter(adapter);
      calculateWidth(circles);
   }

   private void calculateWidth(List<Circle> circles) {
      TextView textView = (CheckedTextView) View.inflate(context, R.layout.list_item_circle_filter, null);
      Paint paint = textView.getPaint();
      Rect bounds = new Rect();
      int maxWidth = 0;

      for (Circle circle : circles) {
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

   public void show() {
      setBounds();
      listPopupWindow.show();
      listPopupWindow.getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
   }

   private void setBounds() {
      listPopupWindow.setHeight(ListPopupWindow.WRAP_CONTENT);
      listPopupWindow.setWidth(width);
   }

   private void checkCircle() {
      ListView listView = listPopupWindow.getListView();

      if (listView == null) return;

      for (int position = 0; position < adapter.getCount(); position++) {
         if (TextUtils.equals(adapter.getItem(position).getId(), checkedCircle.getId())) {
            listView.setItemChecked(position, true);
            break;
         }
      }
   }

   public void dismiss() {
      listPopupWindow.dismiss();
   }

   public void setOnItemClickListener(AdapterView.OnItemClickListener clickListener) {
      listPopupWindow.setOnItemClickListener(clickListener);
   }

   public boolean dismissPassed() {
      return timeWhenDismissed != 0L && (System.currentTimeMillis() - timeWhenDismissed) > DISMISS_DELAY;
   }
}
