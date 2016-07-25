package com.worldventures.dreamtrips.modules.common.view.custom;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.ColorInt;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ReplacementSpan;
import android.util.AttributeSet;
import android.util.Pair;
import android.view.View;
import android.widget.TextView;

import com.worldventures.dreamtrips.R;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HashtagTextView extends TextView {

    private static final int MAX_HASHTAG_LENGTH = 250;

    private @ColorInt int hashtagTextColor;
    private @ColorInt int selectedHashtagTextColor;
    private @ColorInt int selectedHashtagBackgroundColor;

    private HashtagClickListener hashtagClickListener;

    public HashtagTextView(Context context) {
        this(context, null);
    }

    public HashtagTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HashtagTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        hashtagTextColor = context.getResources().getColor(R.color.hashtag_text_color);
        selectedHashtagTextColor = context.getResources().getColor(R.color.hashtag_text_color);
        selectedHashtagBackgroundColor = context.getResources().getColor(R.color.highlight_hashtag_bg);
    }

    public void setHashtagTextColor(@ColorInt int hashtagTextColor) {
        this.hashtagTextColor = hashtagTextColor;
    }

    public void setSelectedTextColor(@ColorInt int hashtagTextColor, @ColorInt int selectedHashtagTextColor, @ColorInt int selectedHashtagBackgroundColor) {
        this.hashtagTextColor = hashtagTextColor;
        this.selectedHashtagTextColor = selectedHashtagTextColor;
        this.selectedHashtagBackgroundColor = selectedHashtagBackgroundColor;
    }

    public void setHashtagClickListener(HashtagClickListener hashtagClickListener) {
        this.hashtagClickListener = hashtagClickListener;
    }

    public void highlightHashtags(List<String> clickableHashtags) {
        setMovementMethod(LinkMovementMethod.getInstance());
        setText(process(getText().toString(), clickableHashtags, new ArrayList<>()), BufferType.SPANNABLE);
    }

    public void highlightHashtags(List<String> clickableHashtags, List<String> selectedHashtags) {
        setMovementMethod(LinkMovementMethod.getInstance());
        setText(process(getText().toString(), clickableHashtags, selectedHashtags), BufferType.SPANNABLE);
    }


    private SpannableStringBuilder process(final String text, final List<String> clickableHashtags, final List<String> selectedHashtags) {
        final SpannableStringBuilder spannable = new SpannableStringBuilder(text);

        for (final String hashtag : clickableHashtags) {
            if (hashtag != null) {
                final String formattedHashtag = String.format("#%s", hashtag.toLowerCase());
                List<Pair<Integer, Integer>> pairs = findIndexesForKeyword(text.toLowerCase(), formattedHashtag);

                for (Pair<Integer, Integer> pair : pairs) {
                    if (selectedHashtags.contains(hashtag)) {
                        highlight(spannable, pair.first, pair.second);
                    }
                    attachClick(spannable, pair.first, pair.second, formattedHashtag);
                }
            }
        }

        return spannable;
    }

    private void highlight(Spannable spannable, int start, int end) {
        spannable.setSpan(new RoundedBackgroundSpan(selectedHashtagTextColor, selectedHashtagBackgroundColor), start, end, 0);
    }

    private void attachClick(Spannable spannable, int start, int end, final String callbackString) {
        spannable.setSpan(new HashtagClickableSpan(hashtagTextColor) {
            @Override
            public void onClick(View widget) {
                if (hashtagClickListener != null)
                    hashtagClickListener.onHashtagClicked(callbackString);
            }
        }, start, end, 0);
    }

    /**
     * @param text
     * @param keyword
     * @return List <(keywordIndexStart, keywordIndexEnd)>
     */
    public List<Pair<Integer, Integer>> findIndexesForKeyword(String text, String keyword) {
        keyword = keyword.trim().toLowerCase();
        String regularExpression = keyword.length() >= MAX_HASHTAG_LENGTH ? "%s" : "%s\\b";
        String regex = String.format(regularExpression, keyword);
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(text.toLowerCase());

        List<Pair<Integer, Integer>> indexWrapper = new ArrayList<>();

        while (matcher.find()) {
            int end = matcher.end();
            int start = matcher.start();
            indexWrapper.add(new Pair<>(start, end));
        }
        return indexWrapper;
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
