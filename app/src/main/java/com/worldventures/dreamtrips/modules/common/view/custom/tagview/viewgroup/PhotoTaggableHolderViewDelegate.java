package com.worldventures.dreamtrips.modules.common.view.custom.tagview.viewgroup;

import android.view.GestureDetector;
import android.view.MotionEvent;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.modules.common.view.custom.tagview.ExistsTagView;
import com.worldventures.dreamtrips.modules.common.view.custom.tagview.SuggestionTagView;
import com.worldventures.dreamtrips.modules.common.view.custom.tagview.viewgroup.newio.model.TagPosition;
import com.worldventures.dreamtrips.modules.common.view.util.CoordinatesTransformer;
import com.worldventures.dreamtrips.modules.common.view.custom.tagview.viewgroup.newio.model.PhotoTag;

import java.util.ArrayList;
import java.util.List;

public class PhotoTaggableHolderViewDelegate {

    TaggableImageViewGroup viewGroup;

    public PhotoTaggableHolderViewDelegate(TaggableImageViewGroup viewGroup) {
        this.viewGroup = viewGroup;
    }

    public List<SuggestionTagView> getSuggestionTagViews() {
        List<SuggestionTagView> views = new ArrayList<>();
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            if ((viewGroup.getChildAt(i) instanceof SuggestionTagView)) {
                views.add((SuggestionTagView) viewGroup.getChildAt(i));
            }
        }
        return views;
    }

    public boolean isSuggestionViewExist(PhotoTag photoTag) {
        TagPosition pos = CoordinatesTransformer.convertToAbsolute(photoTag.getProportionalPosition(), viewGroup.getImageBounds());
        return Queryable.from(getSuggestionTagViews()).any(view -> {
            return pos.intersected(view.getAbsoluteTagPosition());
        });
    }

    public List<ExistsTagView> getExistingTagViews() {
        List<ExistsTagView> views = new ArrayList<>();
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            if ((viewGroup.getChildAt(i) instanceof ExistsTagView)) {
                views.add((ExistsTagView) viewGroup.getChildAt(i));
            }
        }
        return views;
    }

    public boolean isExistingViewExist(PhotoTag photoTag) {
        TagPosition pos = CoordinatesTransformer.convertToAbsolute(photoTag.getProportionalPosition(), viewGroup.getImageBounds());
        return Queryable.from(getExistingTagViews()).any(view -> {
            return pos.intersected(view.getAbsoluteTagPosition());
        });
    }


    public static class SingleTapConfirm extends GestureDetector.SimpleOnGestureListener {

        private CreationPhotoTaggableHolderViewGroup viewGroup;

        public SingleTapConfirm(CreationPhotoTaggableHolderViewGroup viewGroup) {
            this.viewGroup = viewGroup;
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent event) {
            boolean confirmed = viewGroup.getImageBounds().contains(event.getX(), event.getY());
            if (confirmed) {
                viewGroup.addCreationTagView(event.getX(), event.getY());
            }
            return confirmed;
        }

    }


}
