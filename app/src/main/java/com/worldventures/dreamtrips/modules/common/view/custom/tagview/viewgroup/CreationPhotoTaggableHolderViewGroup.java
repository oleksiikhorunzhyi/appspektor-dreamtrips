package com.worldventures.dreamtrips.modules.common.view.custom.tagview.viewgroup;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.presenter.CreationPhotoTaggableHolderPresenter;
import com.worldventures.dreamtrips.modules.common.view.custom.tagview.CreationTagView;
import com.worldventures.dreamtrips.modules.common.view.custom.tagview.SuggestionTagView;
import com.worldventures.dreamtrips.modules.common.view.custom.tagview.TagActionListener;
import com.worldventures.dreamtrips.modules.common.view.custom.tagview.TagCreationActionsListener;
import com.worldventures.dreamtrips.modules.common.view.custom.tagview.TagSuggestionActionListener;
import com.worldventures.dreamtrips.modules.common.view.custom.tagview.TagView;
import com.worldventures.dreamtrips.modules.common.view.util.CoordinatesTransformer;
import com.worldventures.dreamtrips.modules.tripsimages.model.Photo;
import com.worldventures.dreamtrips.modules.tripsimages.model.PhotoTag;
import com.worldventures.dreamtrips.modules.tripsimages.vision.ImageUtils;

import org.apache.commons.lang3.SerializationUtils;

import java.util.ArrayList;
import java.util.List;

import icepick.State;
import rx.Observable;

public class CreationPhotoTaggableHolderViewGroup extends TaggableImageViewGroup<CreationPhotoTaggableHolderPresenter>
        implements CreationPhotoTaggableHolderPresenter.View {

    @State
    ArrayList<PhotoTag> locallyAddedTags = new ArrayList<>();
    @State
    ArrayList<PhotoTag> locallyDeletedTags = new ArrayList<>();

    private GestureDetector gestureDetector;
    private TaggableCompleteListener completeListener;
    private CreationPhotoTaggableHolderViewDelegate delegate;

    public CreationPhotoTaggableHolderViewGroup(Context context) {
        this(context, null);
    }

    public CreationPhotoTaggableHolderViewGroup(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CreationPhotoTaggableHolderViewGroup(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(getContext()).inflate(R.layout.photo_tag_title, this, true);
        gestureDetector = new GestureDetector(getContext(), new CreationPhotoTaggableHolderViewDelegate.SingleTapConfirm(this));
        delegate = new CreationPhotoTaggableHolderViewDelegate(this);
    }

    @NonNull
    @Override
    protected CreationPhotoTaggableHolderPresenter createPresenter(Photo photo) {
        return new CreationPhotoTaggableHolderPresenter(photo);
    }

    @Override
    public void addTags(List<PhotoTag> tags) {
        super.addTags(Queryable.from(tags)
                .filter(element -> !locallyDeletedTags.contains(element))
                .filter((photoTag) -> !delegate.isExistingViewExist(photoTag)).toList());
    }

    protected void addSuggestionTagView(PhotoTag photoTag) {
        TagView view = new SuggestionTagView(getContext());
        view.setTagListener(createTagListener(view));
        addTagView(view, photoTag, 0);
    }

    protected void addCreationTagView(PhotoTag photoTag) {
        addCreationTagViewBasedOnSuggestion(photoTag, null);
    }

    public void addCreationTagView(float x, float y) {
        addCreationTagView(new PhotoTag(CoordinatesTransformer.convertToProportional(new PhotoTag.TagPosition(x, y, x, y), getImageBounds()), null));
    }

    protected void addCreationTagViewBasedOnSuggestion(PhotoTag photoTag, SuggestionTagView tagView) {
        removeUncompletedViews();
        CreationTagView view = new CreationTagView(getContext());
        view.setTagListener((TagCreationActionsListener) createTagListener(view));
        view.setSuggestionTagView(tagView);
        addTagView(view, photoTag);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        gestureDetector.onTouchEvent(event);
        return true;

    }

    @Override
    public void onRequestsComplete() {
        if (completeListener != null)
            completeListener.onTagRequestsComplete();
    }

    @Override
    public void addTag(PhotoTag tag) {
        locallyAddedTags.add(tag);
    }

    @Override
    public void deleteTag(PhotoTag tag) {
        if (locallyAddedTags.contains(tag)) {
            locallyAddedTags.remove(tag);
            return;
        }
        locallyDeletedTags.add(tag);
    }

    @NonNull
    @Override
    protected TagActionListener createTagListener(TagView view) {
        TagActionListener tagCreationActionsListener;
        if (view instanceof SuggestionTagView) {
            tagCreationActionsListener = getTagSuggestionActionListener();
        } else if (view instanceof CreationTagView) {
            tagCreationActionsListener = getTagCreationActionsListener((CreationTagView) view);
        } else {
            tagCreationActionsListener = super.createTagListener(view);

        }
        return tagCreationActionsListener;
    }

    @NonNull
    private TagSuggestionActionListener getTagSuggestionActionListener() {
        return new TagSuggestionActionListener() {
            @Override
            public void onFrameClicked(SuggestionTagView suggestionTagView, PhotoTag tag) {
                addCreationTagViewBasedOnSuggestion(tag, suggestionTagView);
            }

            @Override
            public void onTagDeleted(PhotoTag tag) {
                presenter.deletePhotoTag(tag);
            }
        };
    }

    @NonNull
    private TagCreationActionsListener getTagCreationActionsListener(final CreationTagView view) {
        return new TagCreationActionsListener() {
            @Override
            public void requestFriendList(String query) {
                presenter.loadFriends(query, view);
            }

            @Override
            public void onTagCreated(CreationTagView newTagView, SuggestionTagView suggestionTagView, PhotoTag tag) {
                PhotoTag cloneTag = SerializationUtils.clone(tag);
                addTag(cloneTag);
                addExistsTagView(cloneTag);
                removeView(newTagView);
                removeView(suggestionTagView);
            }

            @Override
            public void onTagDeleted(PhotoTag tag) {
                presenter.deletePhotoTag(tag);
            }
        };
    }

    @Override
    public ArrayList<PhotoTag> getLocallyAddedTags() {
        return locallyAddedTags;
    }

    @Override
    public ArrayList<PhotoTag> getLocallyDeletedTags() {
        return locallyDeletedTags;
    }

    @Override
    public void showSuggestions() {
        ImageUtils.getBitmap(getContext(), Uri.parse(presenter.getPhoto().getFSImage().getUrl()), 0, 0)
                .compose(bitmapObservable -> ImageUtils.getRecognizedFaces(getContext(), bitmapObservable))
                .map(photoTags -> Queryable.from(photoTags).filter(photoTag -> !delegate.isSuggestionViewExist(photoTag)).toList())
                .flatMap(Observable::from)
                .filter(this::isIntersectedWithPhotoTags)
                .subscribe(this::addSuggestionTagView);
    }

    protected boolean isIntersectedWithPhotoTags(PhotoTag suggestion) {
        return !Queryable.from(locallyAddedTags)
                .concat(presenter.getPhoto().getPhotoTags())
                .any(element -> element.getProportionalPosition().intersected(suggestion.getProportionalPosition()));
    }


    public void setCompleteListener(TaggableCompleteListener completeListener) {
        this.completeListener = completeListener;
    }

    public void pushRequests() {
        presenter.pushRequests();
    }

    public interface TaggableCompleteListener {
        void onTagRequestsComplete();
    }

}
