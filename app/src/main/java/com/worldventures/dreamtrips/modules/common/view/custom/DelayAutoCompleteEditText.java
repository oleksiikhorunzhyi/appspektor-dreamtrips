package com.worldventures.dreamtrips.modules.common.view.custom;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.SearchView;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import com.worldventures.dreamtrips.modules.common.view.util.TextWatcherAdapter;

/**
 * Please use SearchView.OnQueryTextListener for listening this AutoCompleteTextView
 *
 * @see SearchView.OnQueryTextListener
 * @see DelayAutoCompleteEditText#setOnQueryTextListener(android.support.v7.widget.SearchView.OnQueryTextListener)
 * <p>
 * Note, that callbacks will come with delay (it is customizable)
 * @see DelayAutoCompleteEditText#delayInMillis
 * @see DelayAutoCompleteEditText#setDelayInMillis(long)
 */
public class DelayAutoCompleteEditText extends AutoCompleteTextView {

   private static final long SEARCH_TRIGGER_DELAY_IN_MS = 1000;
   private long delayInMillis = SEARCH_TRIGGER_DELAY_IN_MS;

   private static final int TRIGGER_ON_QUERY_TEXT_CHANGE = 1;
   private static final int TRIGGER_ON_QUERY_TEXT_SUBMIT = 2;

   InnerHandler handler = new InnerHandler();
   private SearchView.OnQueryTextListener listener;

   private static class InnerHandler extends Handler {
      SearchView.OnQueryTextListener listener;

      @Override
      public void handleMessage(Message msg) {
         if (msg.what == TRIGGER_ON_QUERY_TEXT_CHANGE) {
            if (listener != null) {
               listener.onQueryTextChange((String) msg.obj);
            }
         }
         if (msg.what == TRIGGER_ON_QUERY_TEXT_SUBMIT) {
            if (listener != null) {
               listener.onQueryTextSubmit((String) msg.obj);
            }
         }
      }
   }

   //region EditText listening
   private TextWatcher textWatcher = new TextWatcherAdapter() {
      @Override
      public void onTextChanged(CharSequence s, int start, int before, int count) {
         handler.removeMessages(TRIGGER_ON_QUERY_TEXT_CHANGE);
         Message msg = new Message();
         msg.what = TRIGGER_ON_QUERY_TEXT_CHANGE;
         msg.obj = s;
         handler.sendMessageDelayed(msg, delayInMillis);

         if (listener != null) {
            listener.onQueryTextChange(s.toString());
         }
      }
   };

   private OnEditorActionListener onEditorActionListener = new OnEditorActionListener() {
      @Override
      public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
         if (actionId == EditorInfo.IME_ACTION_DONE) {
            String text = v.getText().toString();

            handler.removeMessages(TRIGGER_ON_QUERY_TEXT_SUBMIT);
            Message msg = new Message();
            msg.what = TRIGGER_ON_QUERY_TEXT_SUBMIT;
            msg.obj = text;
            handler.sendMessageDelayed(msg, delayInMillis);

            if (listener != null) {
               listener.onQueryTextSubmit(text);
            }

            InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            return true;
         }
         return false;
      }
   };
   //endregion

   //region Setters
   public void setDelayInMillis(long delayInMillis) {
      this.delayInMillis = delayInMillis;
   }

   public void setOnQueryTextListener(SearchView.OnQueryTextListener listener) {
      this.listener = listener;
   }
   //endregion

   //region Constructors
   public DelayAutoCompleteEditText(Context context) {
      super(context);
   }

   public DelayAutoCompleteEditText(Context context, AttributeSet attrs) {
      super(context, attrs);
   }

   public DelayAutoCompleteEditText(Context context, AttributeSet attrs, int defStyleAttr) {
      super(context, attrs, defStyleAttr);
   }

   @TargetApi(Build.VERSION_CODES.LOLLIPOP)
   public DelayAutoCompleteEditText(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
      super(context, attrs, defStyleAttr, defStyleRes);
   }
   //endregion

   @Override
   protected void onFinishInflate() {
      super.onFinishInflate();
      addTextChangedListener(textWatcher);
      setOnEditorActionListener(onEditorActionListener);
   }

   @Override
   protected void onDetachedFromWindow() {
      super.onDetachedFromWindow();
      removeTextChangedListener(textWatcher);
   }
}