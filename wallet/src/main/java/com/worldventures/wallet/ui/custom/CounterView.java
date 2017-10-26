package com.worldventures.wallet.ui.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.databinding.BindingAdapter;
import android.databinding.DataBindingUtil;
import android.databinding.InverseBindingAdapter;
import android.databinding.InverseBindingListener;
import android.databinding.InverseBindingMethod;
import android.databinding.InverseBindingMethods;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextSwitcher;
import android.widget.TextView;

import com.worldventures.wallet.R;
import com.worldventures.wallet.databinding.WalletCustomViewCounterBinding;

import static android.view.LayoutInflater.from;

@InverseBindingMethods(value = {
      @InverseBindingMethod(
            type = CounterView.class,
            attribute = "counter_value"
      )
})
public class CounterView extends LinearLayout implements TextSwitcher.ViewFactory {

   private static final int DEFAULT_MIN_VALUE = 0;
   private static final int DEFAULT_MAX_VALUE = 5;

   private int count = 0;
   private int minValue = DEFAULT_MIN_VALUE;
   private int maxValue = DEFAULT_MAX_VALUE;

   private WalletCustomViewCounterBinding binding;

   private OnCountChangeListener countChangeListener;

   public CounterView(Context context) {
      this(context, null);
   }

   public CounterView(Context context, @Nullable AttributeSet attrs) {
      this(context, attrs, 0);
   }

   public CounterView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
      super(context, attrs, defStyleAttr);
      init(context, attrs);
   }

   private void init(Context context, AttributeSet attrs) {
      binding = DataBindingUtil.inflate(from(context), R.layout.wallet_custom_view_counter, this, true);
      initSwitcher();
      initListeners();
      initAttrs(context, attrs);
      updateDisplay();
   }

   private void initAttrs(Context context, AttributeSet attrs) {
      if (attrs != null) {
         TypedArray attr = context.obtainStyledAttributes(
               attrs,
               R.styleable.CounterView
         );
         try {
            final int minValue = attr.getInt(R.styleable.CounterView_counter_min_value, DEFAULT_MIN_VALUE);
            final int maxValue = attr.getInt(R.styleable.CounterView_counter_max_value, DEFAULT_MAX_VALUE);

            setMinValue(minValue);
            setMaxValue(maxValue);
         } finally {
            attr.recycle();
         }
      }
   }

   private void initSwitcher() {
      final Animation inAnimation = AnimationUtils.loadAnimation(getContext(), android.R.anim.fade_in);
      final Animation outAnimation = AnimationUtils.loadAnimation(getContext(), android.R.anim.fade_out);

      binding.tsCounterValue.setFactory(this);
      binding.tsCounterValue.setInAnimation(inAnimation);
      binding.tsCounterValue.setOutAnimation(outAnimation);
   }

   private void initListeners() {
      binding.setOnPlusCountClick(v -> actionPlusCount());
      binding.setOnMinusCountClick(v -> actionMinusCount());
   }

   @BindingAdapter("counter_valueAttrChanged")
   public static void setCounterListener(CounterView view,
         final InverseBindingListener counterChanged) {
      if (counterChanged == null) {
         view.setOnCountChangeListener(null);
      } else {
         view.setOnCountChangeListener((minValue1, maxValue1, count1) -> counterChanged.onChange());
      }
   }

   @BindingAdapter(value = {"onCounterChange", "counter_valueAttrChanged"},
                   requireAll = false)
   public static void setCounterListener(CounterView view,
         final OnCountChangeListener listener,
         final InverseBindingListener colorChange) {
      if (colorChange == null) {
         view.setOnCountChangeListener(listener);
      } else {
         view.setOnCountChangeListener((minValue1, maxValue1, count1) -> {
            if (listener != null) {
               listener.onCountChanged(minValue1, maxValue1, count1);
            }
            colorChange.onChange();
         });
      }
   }

   @BindingAdapter("counter_value")
   public static void setCounterValue(CounterView counterView, int newValue) {
      counterView.count = newValue;
      counterView.updateDisplay();
   }

   @InverseBindingAdapter(attribute = "counter_value")
   public static int getCounterValue(CounterView counterView) {
      return counterView.count;
   }

   public void setOnCountChangeListener(OnCountChangeListener countChangeListener) {
      this.countChangeListener = countChangeListener;
   }

   public void setMaxValue(int maxValue) {
      this.maxValue = maxValue;
   }

   public void setMinValue(int minValue) {
      this.minValue = minValue;
   }

   private void updateDisplay() {
      binding.tsCounterValue.setText(String.valueOf(count));
   }

   private void actionPlusCount() {
      if (count + 1 <= maxValue) {
         count++;
         if (countChangeListener != null) countChangeListener.onCountChanged(minValue, maxValue, count);
         updateDisplay();
      }
   }

   private void actionMinusCount() {
      if (count - 1 >= minValue) {
         count--;
         if (countChangeListener != null) countChangeListener.onCountChanged(minValue, maxValue, count);
         updateDisplay();
      }
   }

   public interface OnCountChangeListener {

      void onCountChanged(int minValue, int maxValue, int count);
   }

   @Override
   public View makeView() {
      TextView textView = new TextView(getContext());
      textView.setLayoutParams(new FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.MATCH_PARENT)
      );
      textView.setGravity(Gravity.CENTER);
      textView.setPadding(
            getResources().getDimensionPixelSize(R.dimen.spacing_small),
            0,
            getResources().getDimensionPixelSize(R.dimen.spacing_small),
            0
      );
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
         textView.setForegroundGravity(Gravity.CENTER);
      }
      textView.setTextSize(getResources().getDimension(R.dimen.font_tiny) / 2);
      textView.setTextColor(Color.BLACK);
      return textView;
   }
}
