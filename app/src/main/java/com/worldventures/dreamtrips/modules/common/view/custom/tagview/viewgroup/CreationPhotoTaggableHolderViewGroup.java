package com.worldventures.dreamtrips.modules.common.view.custom.tagview.viewgroup;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.modules.common.presenter.CreationPhotoTaggableHolderPresenter;
import com.worldventures.dreamtrips.modules.common.view.custom.tagview.CreationTagView;
import com.worldventures.dreamtrips.modules.common.view.custom.tagview.SuggestionTagView;
import com.worldventures.dreamtrips.modules.common.view.custom.tagview.TagCreationActionsListener;
import com.worldventures.dreamtrips.modules.common.view.custom.tagview.TagSuggestionActionListener;
import com.worldventures.dreamtrips.modules.common.view.custom.tagview.TagView;
import com.worldventures.dreamtrips.modules.common.view.custom.tagview.viewgroup.newio.model.TagPosition;
import com.worldventures.dreamtrips.modules.common.view.util.CoordinatesTransformer;
import com.worldventures.dreamtrips.modules.tripsimages.model.Photo;
import com.worldventures.dreamtrips.modules.common.view.custom.tagview.viewgroup.newio.model.PhotoTag;
import com.worldventures.dreamtrips.modules.tripsimages.vision.ImageUtils;

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

    public CreationPhotoTaggableHolderViewGroup(Context context) {
        this(context, null);
    }

    public CreationPhotoTaggableHolderViewGroup(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CreationPhotoTaggableHolderViewGroup(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        gestureDetector = new GestureDetector(getContext(), new PhotoTaggableHolderViewDelegate.SingleTapConfirm(this));
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
                .toList());
    }

    public void addSuggestionTagView(PhotoTag photoTag) {
        TagView view = new SuggestionTagView(getContext());
     //todo dep   view.setTagListener(createTagListener(view));
        addTagView(view, photoTag, 0);
        //
        if (!isSuggestionHelpExists()) addSuggestionHelp(photoTag, (SuggestionTagView) view);
    }

    private boolean isSuggestionHelpExists() {
        for (int i = 0; i < getChildCount(); i++) {
            if (getChildAt(i) instanceof SuggestionHelpView) {
                return true;
            }
        }
        return false;
    }

    protected void addSuggestionHelp(PhotoTag photoTag, SuggestionTagView suggestionTagView) {
        SuggestionHelpView helpView = new SuggestionHelpView(getContext());
        addTagView(helpView, photoTag, 0);
        suggestionTagView.setSuggestionHelpView(helpView);
    }

    protected void addCreationTagView(PhotoTag photoTag) {
        addCreationTagViewBasedOnSuggestion(photoTag, null);
    }

    public void addCreationTagView(float x, float y) {
        addCreationTagView(new PhotoTag(CoordinatesTransformer.convertToProportional(new TagPosition(x, y, x, y), getImageBounds()), 0));
    }

    protected void addCreationTagViewBasedOnSuggestion(PhotoTag photoTag, SuggestionTagView tagView) {
        removeUncompletedViews();
        CreationTagView view = new CreationTagView(getContext());
     //todo  dep  view.setTagListener((TagCreationActionsListener) createTagListener(view));
       // view.setSuggestionTag(tagView);
        addTagView(view, photoTag);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        gestureDetector.onTouchEvent(event);
        return true;
    }

    @Override
    public void redrawTags() {
        super.redrawTags();
    }

    @Override
    public void onRequestsComplete() {
        if (completeListener != null)
            completeListener.onTagRequestsComplete();
    }

    @Override
    public void addTag(PhotoTag tag) {
        locallyAddedTags.add(tag);
        locallyDeletedTags.remove(tag);
    }

    @Override
    public void deleteTag(PhotoTag tag) {
        if (locallyAddedTags.contains(tag)) {
            locallyAddedTags.remove(tag);
            return;
        }
        locallyDeletedTags.add(tag);
    }

  /* todo dep
   @NonNull
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
    }*/

    @NonNull
    private TagSuggestionActionListener getTagSuggestionActionListener() {
        return new TagSuggestionActionListener() {
            @Override
            public void onFrameClicked( PhotoTag tag) {
               //todo addCreationTagViewBasedOnSuggestion(tag);
            }
        };
    }

    @NonNull
    private TagCreationActionsListener getTagCreationActionsListener(final CreationTagView view) {
        return new TagCreationActionsListener() {
            @Override
            public void requestFriendList(String query, int page) {
                presenter.loadFriends(query, page, view);
            }

            @Override
            public void onTagCreated(CreationTagView newTagView, PhotoTag suggestion, PhotoTag tag) {

            }

            //@Override
            public void onTagCreated(CreationTagView newTagView, SuggestionTagView suggestionTagView, PhotoTag tag) {
                PhotoTag cloneTag = PhotoTag.cloneTag(tag);
                addTag(cloneTag);
                addExistsTagView(cloneTag);
                removeView(newTagView);
                removeView(suggestionTagView);
            //    if (suggestionTagView != null) suggestionTagView.removeHelpView();
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
        ArrayList<PhotoTag> existed = new ArrayList<>(presenter.getPhoto().getPhotoTags());
        existed.removeAll(locallyDeletedTags);
        return !Queryable.from(locallyAddedTags)
                .concat(existed)
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
