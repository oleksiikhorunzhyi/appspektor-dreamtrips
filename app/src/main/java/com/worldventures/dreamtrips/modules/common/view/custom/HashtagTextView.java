package com.worldventures.dreamtrips.modules.common.view.custom;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.ColorInt;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ReplacementSpan;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.worldventures.dreamtrips.modules.feed.model.feed.hashtag.Hashtag;

import java.util.ArrayList;
import java.util.List;

public class HashtagTextView extends TextView {

    private @ColorInt
    int hashtagTextColor;
    private
    @ColorInt
    int selectedHashtagTextColor;
    private
    @ColorInt
    int selectedHashtagBackgroundColor;

    private HashtagClickListener hashtagClickListener;

    public HashtagTextView(Context context) {
        this(context, null);
    }

    public HashtagTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HashtagTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        hashtagTextColor = context.getResources().getColor(android.R.color.holo_blue_light);
        selectedHashtagTextColor = context.getResources().getColor(android.R.color.white);
        selectedHashtagBackgroundColor = context.getResources().getColor(android.R.color.holo_blue_light);
    }

    public void setHashtagTextColor(int hashtagTextColor) {
        this.hashtagTextColor = hashtagTextColor;
    }

    public void setSelectedTextColor(int hashtagTextColor, int selectedHashtagTextColor, int selectedHashtagBackgroundColor) {
        this.hashtagTextColor = hashtagTextColor;
        this.selectedHashtagTextColor = selectedHashtagTextColor;
        this.selectedHashtagBackgroundColor = selectedHashtagBackgroundColor;
    }

    public void setHashtagClickListener(HashtagClickListener hashtagClickListener) {
        this.hashtagClickListener = hashtagClickListener;
    }

    public void highlightHashtags(List<Hashtag> clickableHashtags) {
        setMovementMethod(LinkMovementMethod.getInstance());
        setText(process(getText().toString(), clickableHashtags, new ArrayList<>()), BufferType.SPANNABLE);
    }

    public void highlightHashtags(List<Hashtag> clickableHashtags, List<Hashtag> selectedHashtags) {
        setMovementMethod(LinkMovementMethod.getInstance());
        setText(process(getText().toString(), clickableHashtags, selectedHashtags), BufferType.SPANNABLE);
    }

    private SpannableStringBuilder process(String text, List<Hashtag> clickableHashtags, List<Hashtag> selectedHashtags) {
        final SpannableStringBuilder spannableText = new SpannableStringBuilder(text);
        for (final Hashtag hashtagHolder : clickableHashtags) {
            final String hashtag = String.format("#%s", hashtagHolder.getHashtag());

            int index1 = text.indexOf(hashtag);
            int index2;

            while (index1 != -1) {
                index2 = index1 + hashtag.length();

                if (selectedHashtags.contains(hashtagHolder))
                    spannableText.setSpan(new RoundedBackgroundSpan(selectedHashtagTextColor, selectedHashtagBackgroundColor), index1, index2, 0);

                spannableText.setSpan(new HashtagClickableSpan(hashtagTextColor) {
                    @Override
                    public void onClick(View widget) {
                        if (hashtagClickListener != null)
                            hashtagClickListener.onHashtagClicked(hashtag);
                    }
                }, index1, index2, 0);
                index1 = text.indexOf(hashtag, index2);
            }
        }
        return spannableText;
    }

    private static class HashtagClickableSpan extends ClickableSpan {

        @ColorInt
        int textColor;

        public HashtagClickableSpan(int clickColor) {
            this.textColor = clickColor;
        }

        @Override
        public void onClick(View widget) {
        }

        @Override
        public void updateDrawState(TextPaint textPaint) {
            textPaint.setUnderlineText(false);
            textPaint.setColor(textColor == 0 ? textPaint.linkColor : textColor);
        }
    }

    private static class RoundedBackgroundSpan extends ReplacementSpan {

        final int CORNER_RADIUS = 8;
        @ColorInt
        int textColor;
        @ColorInt
        int backgroundColor;

        RoundedBackgroundSpan(@ColorInt int textColor, @ColorInt int backgroundColor) {
            super();
            this.textColor = textColor;
            this.backgroundColor = backgroundColor;
        }

        @Override
        public void draw(Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, Paint paint) {
            RectF rect = new RectF(x, top, x + measureText(paint, text, start, end), bottom);
            paint.setColor(backgroundColor);
            canvas.drawRoundRect(rect, CORNER_RADIUS, CORNER_RADIUS, paint);
            paint.setColor(textColor);
            canvas.drawText(text, start, end, x, y, paint);
        }

        @Override
        public int getSize(Paint paint, CharSequence text, int start, int end, Paint.FontMetricsInt fm) {
            return Math.round(paint.measureText(text, start, end));
        }

        private float measureText(Paint paint, CharSequence text, int start, int end) {
            return paint.measureText(text, start, end);
        }
    }


    public interface HashtagClickListener {
        void onHashtagClicked(String hashtag);
    }
}
