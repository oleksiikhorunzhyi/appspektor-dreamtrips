package com.worldventures.dreamtrips.modules.common.view.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.Layout;
import android.text.Selection;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.method.MovementMethod;
import android.text.style.ClickableSpan;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.worldventures.dreamtrips.R;

public class ShowMoreTextView extends TextView {

   private static final int DEF_SYMBOLS_LIMIT = 100;
   private static final String DEF_CAPTION_COLLAPSED = "Show more"; // TODO : move to res as string?
   private static final String DEF_CAPTION_EXPANDED = "Show less"; // TODO : move to res as string?
   private static final int DEF_CAPTION_COLOR = android.R.color.holo_blue_dark;
   private static final String DEF_SEPARATOR_EXPANDED = " "; // TODO : move to res as string?
   private static final String DEF_SEPARATOR_COLLAPSED = "... "; // TODO : move to res as string?
   //
   private int symbolsLimit;
   private String captionCollapsed;
   private String captionExpanded;
   private int captionColor;
   private String separatorExpanded;
   private String separatorCollapsed;
   private boolean extendClickableArea;
   //
   private String fullText;
   private boolean collapsed = true;
   //
   private Listener listener;
   private SimpleListener simpleListener;

   ///////////////////////////////////////////////////////////////////////////
   // Constructors
   ///////////////////////////////////////////////////////////////////////////

   public ShowMoreTextView(Context context) {
      this(context, null);
   }

   public ShowMoreTextView(Context context, AttributeSet attrs) {
      this(context, attrs, 0);
   }

   public ShowMoreTextView(Context context, AttributeSet attrs, int defStyleAttr) {
      super(context, attrs, defStyleAttr);
      init(context, attrs);
   }

   ///////////////////////////////////////////////////////////////////////////
   // Essential public methods
   ///////////////////////////////////////////////////////////////////////////

   // TODO : add them getter&setter methods for ability to setup stuff programmatically

   /**
    * Call this after setting text programmatically to re-draw state
    */
   public void redraw() {
      if (collapsed) {
         drawCollapsed();
      } else {
         drawExpanded();
      }
   }

   /**
    * Programmatically toggle the expanding/collapsing
    */
   public void toggleShowMore() {
      collapsed = !collapsed;
      redraw();
      pokeListeners();
   }

   public void setFullText(String fullText) {
      setText(fullText);
      this.fullText = fullText;
      redraw();
   }

   ///////////////////////////////////////////////////////////////////////////
   // Essential private methods
   ///////////////////////////////////////////////////////////////////////////

   private void init(Context context, AttributeSet attrs) {
      TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ShowMoreTextView);
      //
      symbolsLimit = a.getInt(R.styleable.ShowMoreTextView_smtv_symbolsLimit, DEF_SYMBOLS_LIMIT);
      captionCollapsed = a.getString(R.styleable.ShowMoreTextView_smtv_captionCollapsed);
      captionExpanded = a.getString(R.styleable.ShowMoreTextView_smtv_captionExpanded);
      captionColor = a.getColor(R.styleable.ShowMoreTextView_smtv_captionColor, android.R.color.holo_blue_dark);
      separatorExpanded = a.getString(R.styleable.ShowMoreTextView_smtv_separatorExpanded);
      separatorCollapsed = a.getString(R.styleable.ShowMoreTextView_smtv_separatorCollapsed);
      extendClickableArea = a.getBoolean(R.styleable.ShowMoreTextView_smtv_extendClickableArea, false);
      fullText = getText().toString();
      //
      if (symbolsLimit <= 0)
         throw new IllegalStateException("ShowMoreTextView: invalid symbolsLimit - \'" + symbolsLimit + "\'");
      if (captionCollapsed == null || captionCollapsed.isEmpty()) captionCollapsed = DEF_CAPTION_COLLAPSED;
      if (captionExpanded == null || captionExpanded.isEmpty()) captionExpanded = DEF_CAPTION_EXPANDED;
      if (separatorExpanded == null || separatorExpanded.isEmpty()) separatorExpanded = DEF_SEPARATOR_EXPANDED;
      if (separatorCollapsed == null || separatorCollapsed.isEmpty()) separatorCollapsed = DEF_SEPARATOR_COLLAPSED;
      //
      a.recycle();
      setMovementMethod(extendClickableArea ? HigherLinkMovementMethod.getInstance() : LinkMovementMethod.getInstance());
      drawCollapsed();
   }

   private void drawExpanded() {
      appendPressableCaption(fullText, DEF_SEPARATOR_EXPANDED, captionExpanded);
   }

   private void drawCollapsed() {
      if (fullText.length() <= symbolsLimit) return;
      appendPressableCaption(fullText.substring(0, symbolsLimit), DEF_SEPARATOR_COLLAPSED, captionCollapsed);
   }

   private void appendPressableCaption(String text, String separator, String caption) {
      final StringBuilder sb = new StringBuilder(text);
      sb.append(separator).append(caption);
      //
      final int spanStartIndex, spanEndIndex;
      spanStartIndex = sb.toString().length() - caption.length();
      spanEndIndex = sb.toString().length();
      //
      final SpannableString ss = new SpannableString(sb.toString());
      ss.setSpan(clickableSpan, spanStartIndex, spanEndIndex, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
      setText(ss);
   }

   private ClickableSpan clickableSpan = new ClickableSpan() {

      private Typeface typeface;

      @Override
      public void onClick(View widget) {
         toggleShowMore();
      }

      @Override
      public void updateDrawState(TextPaint ds) {
         if (typeface == null) typeface = Typeface.create(ds.getTypeface(), Typeface.BOLD);
         ds.setTypeface(typeface);
         ds.setColor(captionColor);
      }
   };

   ///////////////////////////////////////////////////////////////////////////
   // Saving and restoring state
   ///////////////////////////////////////////////////////////////////////////

   @Override
   public Parcelable onSaveInstanceState() {
      SavedState savedState = new SavedState(super.onSaveInstanceState());
      savedState.collapsed = collapsed;
      //
      return savedState;
   }

   @Override
   public void onRestoreInstanceState(Parcelable state) {
      if (!(state instanceof SavedState)) {
         super.onRestoreInstanceState(state);
         return;
      }
      //
      SavedState savedState = (SavedState) state;
      super.onRestoreInstanceState(savedState.getSuperState());
      //
      this.collapsed = savedState.collapsed;
      redraw();
   }

   static class SavedState extends BaseSavedState {

      boolean collapsed;

      SavedState(Parcelable superState) {
         super(superState);
      }

      private SavedState(Parcel in) {
         super(in);
         this.collapsed = in.readByte() == 1;
      }

      @Override
      public void writeToParcel(Parcel out, int flags) {
         super.writeToParcel(out, flags);
         out.writeByte((byte) (this.collapsed ? 1 : 0));
      }

      public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>() {
         public SavedState createFromParcel(Parcel in) {
            return new SavedState(in);
         }

         public SavedState[] newArray(int size) {
            return new SavedState[size];
         }
      };
   }

   ///////////////////////////////////////////////////////////////////////////
   // Listeners stuff
   ///////////////////////////////////////////////////////////////////////////

   private void pokeListeners() {
      if (listener != null) {
         if (collapsed) listener.onCollapsed(this);
         else listener.onExpanded(this);
      }
      //
      if (simpleListener != null) {
         simpleListener.onToggled(this, collapsed);
      }
   }

   public void setListener(Listener listener) {
      this.listener = listener;
   }

   public void setSimpleListener(SimpleListener simpleListener) {
      this.simpleListener = simpleListener;
   }

   public interface Listener {
      void onExpanded(ShowMoreTextView view);
      void onCollapsed(ShowMoreTextView view);
   }

   public interface SimpleListener {
      void onToggled(ShowMoreTextView view, boolean collapsed);
   }

   ///////////////////////////////////////////////////////////////////////////
   // Better LinkMovementMethod
   ///////////////////////////////////////////////////////////////////////////

   /**
    * Class below provided "as-is" with only code overridden where line of click detected:<br />
    * if no span here - we try line above and, if no luck - line below.
    * <br />
    *
    * @see LinkMovementMethod
    */
   private static class HigherLinkMovementMethod extends LinkMovementMethod {

      @Override
      public boolean onTouchEvent(TextView widget, Spannable buffer, MotionEvent event) {
         int action = event.getAction();

         if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_DOWN) {
            int x = (int) event.getX();
            int y = (int) event.getY();

            x -= widget.getTotalPaddingLeft();
            y -= widget.getTotalPaddingTop();

            x += widget.getScrollX();
            y += widget.getScrollY();

            Layout layout = widget.getLayout();
            int line = layout.getLineForVertical(y);
            int off = layout.getOffsetForHorizontal(line, x);

            ClickableSpan[] link = buffer.getSpans(off, off, ClickableSpan.class);

            ///////////////////////////////////////////////////////////////////////////
            // "magic" here: try lines above and below for better clickability
            ///////////////////////////////////////////////////////////////////////////
            if (link.length == 0 && line > 0) {
               line--;
               off = layout.getOffsetForHorizontal(line, x);
               link = buffer.getSpans(off, off, ClickableSpan.class);
               if (link.length == 0) {
                  line += 2;
                  if (line < layout.getLineCount()) {
                     off = layout.getOffsetForHorizontal(line, x);
                     link = buffer.getSpans(off, off, ClickableSpan.class);
                  }
               }
            }
            ///////////////////////////////////////////////////////////////////////////
            // "magic" ended
            ///////////////////////////////////////////////////////////////////////////

            if (link.length != 0) {
               if (action == MotionEvent.ACTION_UP) {
                  link[0].onClick(widget);
               } else if (action == MotionEvent.ACTION_DOWN) {
                  Selection.setSelection(buffer, buffer.getSpanStart(link[0]), buffer.getSpanEnd(link[0]));
               }

               return true;
            } else {
               Selection.removeSelection(buffer);
            }
         }

         return super.onTouchEvent(widget, buffer, event);
      }

      public static MovementMethod getInstance() {
         if (sInstance == null) sInstance = new HigherLinkMovementMethod();

         return sInstance;
      }

      private static HigherLinkMovementMethod sInstance;
   }
}
