package com.worldventures.dreamtrips.social.ui.profile.view.widgets;

import android.content.Context;
import android.util.AttributeSet;

import com.messenger.ui.widget.AvatarView;
import com.worldventures.dreamtrips.social.util.UserStatusAdapter;
import com.worldventures.core.janet.Injector;
import com.worldventures.core.model.User;

import javax.inject.Inject;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class SmartAvatarView extends AvatarView {

   private Subscription subscription;

   @Inject UserStatusAdapter userStatusAdapter;

   public SmartAvatarView(Context context) {
      this(context, null);
   }

   public SmartAvatarView(Context context, AttributeSet attrs) {
      super(context, attrs);
   }

   public void setup(User user, Injector injector) {
      injector.inject(this);
      unsubscribe();
      subscription = userStatusAdapter.getUserHolder(user.getUsername())
            .first()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(this::setOnline);
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
