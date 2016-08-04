package com.worldventures.dreamtrips.core.utils;

import android.text.Layout;
import android.text.Spannable;
import android.text.method.LinkMovementMethod;
import android.text.style.URLSpan;
import android.view.MotionEvent;
import android.widget.TextView;

import com.innahema.collections.query.functions.Action1;

public class TextViewLinkHandler extends LinkMovementMethod {

    private Action1<String> onLinkClick;

    public TextViewLinkHandler(Action1<String> onLinkClick) {
        this.onLinkClick = onLinkClick;
    }

    public boolean onTouchEvent(TextView widget, Spannable buffer, MotionEvent event) {
        if (event.getAction() != MotionEvent.ACTION_UP)
            return super.onTouchEvent(widget, buffer, event);

        int x = (int) event.getX();
        int y = (int) event.getY();

        x -= widget.getTotalPaddingLeft();
        y -= widget.getTotalPaddingTop();

        x += widget.getScrollX();
        y += widget.getScrollY();

        Layout layout = widget.getLayout();
        int line = layout.getLineForVertical(y);
        int off = layout.getOffsetForHorizontal(line, x);

        URLSpan[] link = buffer.getSpans(off, off, URLSpan.class);
        if (link.length != 0) {
            if (onLinkClick != null) onLinkClick.apply(link[0].getURL());
        }
        return true;
    }
}