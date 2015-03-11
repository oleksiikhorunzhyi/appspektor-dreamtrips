package com.worldventures.dreamtrips.presentation;

import android.net.Uri;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.utils.DiskCacheUtils;
import com.worldventures.dreamtrips.view.activity.ShareActivity;

import java.io.File;

public class ShareActivityPM extends BasePresentation<ShareActivityPM.View> {
    public ShareActivityPM(View view) {
        super(view);
    }

    public void create(String url, String text, String type) {
        if (type.equals(ShareActivity.FB)) {
            view.shareFBDialog(url, text);
        } else if (type.equals(ShareActivity.TW)) {
            File file = DiskCacheUtils.findInCache(url, ImageLoader.getInstance().getDiskCache());
            Uri parse = null;
            if (file != null) {
                Uri.fromFile(file);
            }
            view.shareTwitterDialog(parse, text);
        }
    }


    public static interface View extends BasePresentation.View {
        public void shareFBDialog(String url, String text);

        public void shareTwitterDialog(Uri url, String text);

    }
}
