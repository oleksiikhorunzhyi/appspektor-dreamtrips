package com.worldventures.dreamtrips.social.ui.feed.view.custom.tagview;

import android.content.Context;
import android.database.DataSetObserver;
import android.support.v7.widget.AppCompatAutoCompleteTextView;
import android.util.AttributeSet;
import android.widget.AbsListView;
import android.widget.AutoCompleteTextView;
import android.widget.ListPopupWindow;
import android.widget.ListView;

import java.lang.reflect.Field;

import timber.log.Timber;

public class FriendsAutoCompleteTextView extends AppCompatAutoCompleteTextView {

   private AbsListView.OnScrollListener listener;

   public FriendsAutoCompleteTextView(Context context) {
      super(context);
   }

   public FriendsAutoCompleteTextView(Context context, AttributeSet attrs) {
      super(context, attrs);
   }

   public FriendsAutoCompleteTextView(Context context, AttributeSet attrs, int defStyleAttr) {
      super(context, attrs, defStyleAttr);
   }

   public void setOnScrollListener(AbsListView.OnScrollListener listener) {
      this.listener = listener;
   }

   @Override
   public void showDropDown() {
      super.showDropDown();

      try {
         getListView().setOnScrollListener(listener);
         getAdapter().unregisterDataSetObserver(getDataSetObserver());
      } catch (Exception e) {
         Timber.e("");
      }
   }

   protected ListView getListView() {
      try {
         Field privateStringField = AutoCompleteTextView.class.getDeclaredField("mPopup");
         privateStringField.setAccessible(true);
         ListPopupWindow fieldValue = (ListPopupWindow) privateStringField.get(this);
         return fieldValue.getListView();
      } catch (Exception e) {
         return null;
      }
   }


   protected DataSetObserver getDataSetObserver() {
      try {
         Field privateStringField = AutoCompleteTextView.class.getDeclaredField("mObserver");
         privateStringField.setAccessible(true);
         return (DataSetObserver) privateStringField.get(this);
      } catch (Exception e) {
         return null;
      }
   }
}
