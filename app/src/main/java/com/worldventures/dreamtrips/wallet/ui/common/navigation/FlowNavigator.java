package com.worldventures.dreamtrips.wallet.ui.common.navigation;

import android.app.Activity;

import flow.Flow;
import flow.History;
import flow.path.Path;

public class FlowNavigator implements Navigator {

   private final Activity activity;
   private final Flow flow;

   public FlowNavigator(Activity activity) {
      this.activity = activity;
      this.flow = Flow.get(activity);
   }

   @Override
   public void go(Path path) {
      flow.set(path);
   }

   @Override
   public void single(Path path) {
      single(path, Flow.Direction.FORWARD);
   }

   @Override
   public void single(Path path, Flow.Direction direction) {
      flow.setHistory(History.single(path), direction);
   }

   @Override
   public void withoutLast(Path path) {
      History.Builder historyBuilder = flow.getHistory().buildUpon();
      historyBuilder.pop();
      historyBuilder.push(path);
      flow.setHistory(historyBuilder.build(), Flow.Direction.FORWARD);
   }

   @Override
   public void goBack() {
      if (!flow.goBack()) {
         // close keyboard or close activity
         activity.onBackPressed();
      }
   }

}
