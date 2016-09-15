/*
 * Copyright 2012 Evgeny Shishkin
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.messenger.ui.inappnotifications.appmsg;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;

/**
 * In-layout notifications. Based on {@link android.widget.Toast} notifications
 * and article by Cyril Mottier (http://android.cyrilmottier.com/?p=773).
 *
 * @author e.shishkin
 */
public class AppMsg {

   /**
    * Show the view or text notification for a short period of time. This time
    * could be user-definable. This is the default.
    *
    * @see #setDuration
    */
   public static final int LENGTH_SHORT = 3000;

   /**
    * Show the view or text notification for a long period of time. This time
    * could be user-definable.
    *
    * @see #setDuration
    */
   public static final int LENGTH_LONG = 5000;

   /**
    * <p>Show the view or text notification for an undefined amount of time
    * -Usually until an invocation of {@link #cancel()}, {@link #cancelAll(Activity)},
    * {@link #cancelAll()} or {@link Activity#onDestroy()}-,
    * stacking on top of any other {@link AppMsg} with this duration.</p>
    * <p>
    * <p><b>Note</b>: You are responsible
    * for calling {@link #cancel()} on such {@link AppMsg}.</p>
    *
    * @see #setDuration
    */
   public static final int LENGTH_STICKY = -1;

   /**
    * Lowest priority, messages with this priority will be showed after all messages with priority
    * {@link #PRIORITY_HIGH} and {@link #PRIORITY_NORMAL} have been shown.
    *
    * @see #setPriority(int)
    */
   public static final int PRIORITY_LOW = Integer.MIN_VALUE;
   /**
    * Normal priority, messages with this priority will be showed after all messages with priority
    * {@link #PRIORITY_HIGH} but before {@link #PRIORITY_LOW} have been shown.
    *
    * @see #setPriority(int)
    */
   public static final int PRIORITY_NORMAL = 0;
   /**
    * Highest priority, messages with this priority will be showed before any other message.
    *
    * @see #setPriority(int)
    */
   public static final int PRIORITY_HIGH = Integer.MAX_VALUE;

   private final Activity activity;
   private int duration = LENGTH_SHORT;
   private View view;
   private ViewGroup parent;
   private LayoutParams layoutParams;
   private boolean isFloating;
   Animation inAnimation, outAnimation;
   int priority = PRIORITY_NORMAL;

   /**
    * Construct an empty AppMsg object. You must call {@link #setView} before
    * you can call {@link #show}.
    *
    * @param activity {@link android.app.Activity} to use.
    */
   public AppMsg(Activity activity) {
      this.activity = activity;
   }

   public static AppMsg showCustomView(Activity context, View view, int duration, int priority) {
      AppMsg result = new AppMsg(context);
      result.setPriority(priority);
      result.view = view;
      result.duration = duration;
      result.isFloating = true;
      return result;
   }

   /**
    * Show the view for the specified duration.
    */
   public void show() {
      MsgManager manager = MsgManager.obtain(activity);
      manager.add(this);
   }

   /**
    * @return <code>true</code> if the {@link AppMsg} is being displayed, else <code>false</code>.
    */
   public boolean isShowing() {
      if (isFloating) {
         return view != null && view.getParent() != null;
      } else {
         return view.getVisibility() == View.VISIBLE;
      }
   }

   /**
    * Close the view if it's showing, or don't show it if it isn't showing yet.
    * You do not normally have to call this.  Normally view will disappear on its own
    * after the appropriate duration.
    */
   public void cancel() {
      MsgManager.obtain(activity).clearMsg(this);

   }

   /**
    * Cancels all queued {@link AppMsg}s, in all Activities. If there is a {@link AppMsg}
    * displayed currently, it will be the last one displayed.
    */
   public static void cancelAll() {
      MsgManager.clearAll();
   }

   /**
    * Cancels all queued {@link AppMsg}s, in given {@link Activity}.
    * If there is a {@link AppMsg} displayed currently, it will be the last one displayed.
    *
    * @param activity
    */
   public static void cancelAll(Activity activity) {
      MsgManager.release(activity);
   }

   /**
    * Return the activity.
    */
   public Activity getActivity() {
      return activity;
   }

   /**
    * Set the view to show.
    *
    * @see #getView
    */
   public void setView(View view) {
      this.view = view;
   }

   /**
    * Return the view.
    *
    * @see #setView
    */
   public View getView() {
      return view;
   }

   /**
    * Set how long to show the view for.
    *
    * @see #LENGTH_SHORT
    * @see #LENGTH_LONG
    */
   public void setDuration(int duration) {
      this.duration = duration;
   }

   /**
    * Return the duration.
    *
    * @see #setDuration
    */
   public int getDuration() {
      return duration;
   }

   /**
    * Gets the crouton's layout parameters, constructing a default if necessary.
    *
    * @return the layout parameters
    */
   public LayoutParams getLayoutParams() {
      if (layoutParams == null) {
         layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
      }
      return layoutParams;
   }

   /**
    * Sets the layout parameters which will be used to display the crouton.
    *
    * @param layoutParams The layout parameters to use.
    * @return <code>this</code>, for chaining.
    */
   public AppMsg setLayoutParams(LayoutParams layoutParams) {
      this.layoutParams = layoutParams;
      return this;
   }

   /**
    * Constructs and sets the layout parameters to have some gravity.
    *
    * @param gravity the gravity of the Crouton
    * @return <code>this</code>, for chaining.
    * @see android.view.Gravity
    */
   public AppMsg setLayoutGravity(int gravity) {
      layoutParams = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, gravity);
      return this;
   }

   /**
    * Return the value of isFloating.
    *
    * @see #setFloating(boolean)
    */
   public boolean isFloating() {
      return isFloating;
   }

   /**
    * Sets the value of isFloating.
    *
    * @param mFloating
    */
   public void setFloating(boolean mFloating) {
      this.isFloating = mFloating;
   }

   /**
    * Sets the Animations to be used when displaying/removing the Crouton.
    *
    * @param inAnimation  the Animation resource ID to be used when displaying.
    * @param outAnimation the Animation resource ID to be used when removing.
    */
   public AppMsg setAnimation(int inAnimation, int outAnimation) {
      return setAnimation(AnimationUtils.loadAnimation(activity, inAnimation), AnimationUtils.loadAnimation(activity, outAnimation));
   }

   /**
    * Sets the Animations to be used when displaying/removing the Crouton.
    *
    * @param inAnimation  the Animation to be used when displaying.
    * @param outAnimation the Animation to be used when removing.
    */
   public AppMsg setAnimation(Animation inAnimation, Animation outAnimation) {
      this.inAnimation = inAnimation;
      this.outAnimation = outAnimation;
      return this;
   }

   /**
    * @return Current priority
    * @see #PRIORITY_HIGH
    * @see #PRIORITY_NORMAL
    * @see #PRIORITY_LOW
    */
   public int getPriority() {
      return priority;
   }

   /**
    * <p>Set priority for this message</p>
    * <p><b>Note</b>: This only affects the order in which the messages get shown,
    * not the stacking order of the views.</p>
    * <p>
    * <p>Example: In the queue there are 3 messages [A, B, C],
    * all of them with priority {@link #PRIORITY_NORMAL}, currently message A is being shown
    * so we add a new message D with priority {@link #PRIORITY_HIGH}, after A goes away, given that
    * D has a higher priority than B an the reset, D will be shown, then once that D is gone,
    * B will be shown, and then finally C.</p>
    *
    * @param priority A value indicating priority, although you can use any integer value, usage of already
    *                 defined is highly encouraged.
    * @see #PRIORITY_HIGH
    * @see #PRIORITY_NORMAL
    * @see #PRIORITY_LOW
    */
   public void setPriority(int priority) {
      this.priority = priority;
   }

   /**
    * @return Provided parent to add {@link #getView()} to using {@link #getLayoutParams()}.
    */
   public ViewGroup getParent() {
      return parent;
   }

   /**
    * Provide a different parent than Activity decor view
    *
    * @param parent Provided parent to add {@link #getView()} to using {@link #getLayoutParams()}.
    */
   public void setParent(ViewGroup parent) {
      this.parent = parent;
   }

   /**
    * Provide a different parent than Activity decor view
    *
    * @param parentId Provided parent id to add {@link #getView()} to using {@link #getLayoutParams()}.
    */
   public void setParent(int parentId) {
      setParent((ViewGroup) activity.findViewById(parentId));
   }
}
