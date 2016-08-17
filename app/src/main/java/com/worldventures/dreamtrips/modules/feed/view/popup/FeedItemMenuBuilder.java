package com.worldventures.dreamtrips.modules.feed.view.popup;

import android.content.Context;
import android.support.annotation.MenuRes;
import android.support.v7.widget.PopupMenu;
import android.view.View;

import com.worldventures.dreamtrips.R;

public class FeedItemMenuBuilder {

   private Context context;
   private View anchor;
   @MenuRes private int menuRes;
   private Action onDeleteAction;
   private Action onEditAction;
   private PopupMenu.OnDismissListener dismissListener;

   private FeedItemMenuBuilder() {
   }

   public static FeedItemMenuBuilder create(Context context, View anchor, @MenuRes int menuRes) {
      FeedItemMenuBuilder feedItemMenuBuilder = new FeedItemMenuBuilder();
      feedItemMenuBuilder.context = context;
      feedItemMenuBuilder.anchor = anchor;
      feedItemMenuBuilder.menuRes = menuRes;
      return feedItemMenuBuilder;
   }

   public FeedItemMenuBuilder onEdit(Action onEditAction) {
      this.onEditAction = onEditAction;
      return this;
   }

   public FeedItemMenuBuilder onDelete(Action onDeleteAction) {
      this.onDeleteAction = onDeleteAction;
      return this;
   }

   public FeedItemMenuBuilder dismissListener(PopupMenu.OnDismissListener listener) {
      this.dismissListener = listener;
      return this;
   }

   public void show() {
      PopupMenu popup = new PopupMenu(context, anchor);
      popup.inflate(menuRes);
      popup.setOnMenuItemClickListener(item -> {
         switch (item.getItemId()) {
            case R.id.action_delete:
               if (onDeleteAction != null) onDeleteAction.action();
               break;
            case R.id.action_edit:
               if (onEditAction != null) onEditAction.action();
               break;
         }

         return true;
      });
      popup.setOnDismissListener(dismissListener);
      popup.show();
   }

   public interface Action {
      void action();
   }

}
