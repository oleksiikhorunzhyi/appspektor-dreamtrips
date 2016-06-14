package com.worldventures.dreamtrips.modules.common.view.custom;

import android.content.Context;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.ViewGroup;

import com.crashlytics.android.Crashlytics;
import com.facebook.drawee.view.SimpleDraweeView;
import com.jakewharton.rxbinding.view.RxView;
import com.trello.rxlifecycle.RxLifecycle;
import com.worldventures.dreamtrips.core.utils.GraphicUtils;

import rx.functions.Action1;
import timber.log.Timber;

/**
 * {@link SimpleDraweeView} inheritant that is aware of imagery resizing service.
 * <br /><br />
 *
 * <b>
 * Note: might work incorrectly if {@link android.widget.ImageView.ScaleType ScaleType}
 * is set to e.g. {@link android.widget.ImageView.ScaleType#CENTER_CROP CENTER_CROP}
 * </b>
 * <br /><br />
 *
 * Image might be of low quality then, because it get's resized to view's borders by server <br />
 * and then client upscales it to match given {@link android.widget.ImageView.ScaleType ScaleType}
 * <br /><br />
 */
public class ImageryDraweeView extends SimpleDraweeView {

    public ImageryDraweeView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * Same as {@link SimpleDraweeView#setImageURI(Uri)} but accepts URL as a String
     * @param url url to image source
     */
    public void setImageUrl(String url) {
        determineSize(url, formattedUrl -> {
            try {
                super.setImageURI(Uri.parse(formattedUrl));
            } catch (Exception e) {
                // react gracefully on inability to show picture: do not show it
                Crashlytics.logException(e);
                Timber.e(e, "Could not show picture: %s", url);
            }
        });
    }

    @Override
    public void setImageURI(Uri uri) {
        setImageUrl(uri.toString());
    }

    protected void determineSize(String url, Action1<String> onSizeDeterminedAction) {
        if (cannotSizeView()) {
            if (getWidth() == 0 || getHeight() == 0) {
                RxView.layoutChangeEvents(this)
                        .compose(RxLifecycle.bindView(this))
                        .subscribe(aVoid ->
                                onSizeDeterminedAction.call(formatImageUri(url)));
            } else {
                onSizeDeterminedAction.call(formatImageUri(url));
            }
        } else {
            onSizeDeterminedAction.call(GraphicUtils.formatUrlWithParams(url,
                    getLayoutParams().width, getLayoutParams().height));
        }
    }

    /**
     * Determines if size can be determined before view is measured.
     * @return true if size is set in <code>dp</code>,
     * false if view is {@link ViewGroup.LayoutParams#MATCH_PARENT MATCH_PARENT} or
     * {@link ViewGroup.LayoutParams#WRAP_CONTENT WRAP_CONTENT}
     */
    protected boolean cannotSizeView() {
        return getLayoutParams().height == ViewGroup.LayoutParams.MATCH_PARENT
                || getLayoutParams().height == ViewGroup.LayoutParams.WRAP_CONTENT
                || getLayoutParams().width == ViewGroup.LayoutParams.MATCH_PARENT
                || getLayoutParams().width == ViewGroup.LayoutParams.WRAP_CONTENT;
    }

    protected String formatImageUri(String url, int width, int height) {
        return GraphicUtils.formatUrlWithParams(url, width, height);
    }

    protected String formatImageUri(String url) {
        return formatImageUri(url, getWidth(), getHeight());
    }
}
