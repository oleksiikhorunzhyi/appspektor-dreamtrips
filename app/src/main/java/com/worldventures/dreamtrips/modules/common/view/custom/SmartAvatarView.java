package com.worldventures.dreamtrips.modules.common.view.custom;

import android.content.Context;
import android.util.AttributeSet;

import com.messenger.ui.widget.AvatarView;
import com.techery.spares.module.Injector;
import com.techery.spares.utils.UserStatusAdapter;
import com.worldventures.dreamtrips.modules.common.model.User;

import javax.inject.Inject;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class SmartAvatarView extends AvatarView {

   private User user;
   private Subscription subscription;

   @Inject UserStatusAdapter userStatusAdapter;

   public SmartAvatarView(Context context) {
      this(context, null);
   }

   public SmartAvatarView(Context context, AttributeSet attrs) {
      super(context, attrs);
   }

   public void setup(User user, Injector injector) {
      this.user = user;
      injector.inject(this);
      unsubscribe();
      subscription = userStatusAdapter.getUserHolder(user.getUsername())
            .first()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(isOnline -> {
               setOnline(isOnline);
            });
   }

   @Override
   protected void onDetachedFromWindow() {
      super.onDetachedFromWindow();
      unsubscribe();
   }

   private void unsubscribe() {
      if (subscription != null && !subscription.isUnsubscribed()) {
         subscription.unsubscribe();
      }
   }
}
