package com.worldventures.dreamtrips.modules.common.presenter;

import android.net.Uri;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.utils.DiskCacheUtils;
import com.worldventures.dreamtrips.modules.common.view.activity.ShareActivity;

import java.io.File;

public class ShareActivityPM extends BasePresentation<ShareActivityPM.View> {
    public ShareActivityPM(View view) {
        super(view);
    }

    public void create(String imageUrl, String shareLink, String text, String type) {
        if (type.equals(ShareActivity.FB)) {
            view.shareFBDialog(imageUrl, shareLink, text);
        } else if (type.equals(ShareActivity.TW)) {
            File file = DiskCacheUtils.findInCache(imageUrl, ImageLoader.getInstance().getDiskCache());
            Uri parse = null;
            if (file != null) {
                parse = Uri.fromFile(file);
            }
            view.shareTwitterDialog(parse, shareLink, text);
        }
    }

    public void openShareActivity(String picture, String link, String text) {
        activityRouter.openShareFacebook(picture, link, text);
    }


    public static interface View extends BasePresentation.View {
        public void shareFBDialog(String url, String link, String text);

        public void shareTwitterDialog(Uri url, String link, String text);

    }
}
