package com.worldventures.dreamtrips.modules.common.view.custom.tagview.viewgroup;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;

import com.worldventures.dreamtrips.modules.common.presenter.PreviewPhotoTaggableHolderPresenter;
import com.worldventures.dreamtrips.modules.tripsimages.model.Photo;

import rx.functions.Action0;

public class PreviewPhotoTaggableHolderViewGroup extends TaggableImageViewGroup<PreviewPhotoTaggableHolderPresenter> implements PreviewPhotoTaggableHolderPresenter.View {

    private Action0 onTagDeletedAction;

    public PreviewPhotoTaggableHolderViewGroup(Context context) {
        super(context);
    }

    public PreviewPhotoTaggableHolderViewGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PreviewPhotoTaggableHolderViewGroup(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @NonNull
    @Override
    protected PreviewPhotoTaggableHolderPresenter getPresenter(Photo photo) {
        return new PreviewPhotoTaggableHolderPresenter(photo);
    }

    public void setOnTagDeletedAction(Action0 onTagDeletedAction) {
        this.onTagDeletedAction = onTagDeletedAction;
    }

    @Override
    public void onTagDeleted() {
        onTagDeletedAction.call();
    }
}
