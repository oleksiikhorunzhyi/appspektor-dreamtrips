package com.worldventures.dreamtrips.modules.common.view.custom.tagview.viewgroup.newio;

import android.content.Context;
import android.graphics.RectF;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.view.SimpleDraweeView;
import com.worldventures.dreamtrips.modules.common.view.custom.tagview.CreationTagView;
import com.worldventures.dreamtrips.modules.common.view.custom.tagview.ExistsTagView;
import com.worldventures.dreamtrips.modules.common.view.custom.tagview.SuggestionTagView;
import com.worldventures.dreamtrips.modules.common.view.custom.tagview.TagCreationActionsListener;
import com.worldventures.dreamtrips.modules.common.view.custom.tagview.TagSuggestionActionListener;
import com.worldventures.dreamtrips.modules.common.view.custom.tagview.TagView;
import com.worldventures.dreamtrips.modules.common.view.custom.tagview.viewgroup.SuggestionHelpView;
import com.worldventures.dreamtrips.modules.common.view.custom.tagview.viewgroup.newio.model.PhotoTag;
import com.worldventures.dreamtrips.modules.common.view.custom.tagview.viewgroup.newio.model.Position;
import com.worldventures.dreamtrips.modules.common.view.custom.tagview.viewgroup.newio.model.TagPosition;
import com.worldventures.dreamtrips.modules.common.view.util.Size;

import icepick.Icepick;
import icepick.State;
import timber.log.Timber;

import static com.worldventures.dreamtrips.modules.common.view.util.CoordinatesTransformer.convertToAbsolute;
import static com.worldventures.dreamtrips.modules.common.view.util.CoordinatesTransformer.convertToProportional;

public class PhotoTagHolder extends RelativeLayout {

    @State
    boolean isShown;

    @State
    RectF imageBounds = new RectF();

    PhotoTagHolderManager manager;

    public PhotoTagHolder(Context context) {
        super(context);
    }

    public PhotoTagHolder(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PhotoTagHolder(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable parcelable = super.onSaveInstanceState();
        return Icepick.saveInstanceState(this, parcelable);
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        super.onRestoreInstanceState(Icepick.restoreInstanceState(this, state));
    }

    protected void show(PhotoTagHolderManager manager, SimpleDraweeView imageView) {
        this.manager = manager;

        imageView.getHierarchy().setActualImageScaleType(ScalingUtils.ScaleType.FIT_CENTER);
        imageView.getHierarchy().getActualImageBounds(imageBounds);
        setVisibility(View.VISIBLE);
        isShown = true;
    }

    protected void hide() {
        setVisibility(View.INVISIBLE);
        isShown = false;
    }


    protected void addExistsTagView(PhotoTag photoTag) {
        ExistsTagView view = new ExistsTagView(getContext());
        view.setTagListener(photoTag1 -> manager.notifyTagDeleted(photoTag1));
        addTagView(view, photoTag);
    }

    protected void addSuggestionTagView(PhotoTag photoTag, TagSuggestionActionListener tagSuggestionActionListener) {
        SuggestionTagView view = new SuggestionTagView(getContext());
        view.setTagListener(tagSuggestionActionListener);
        addTagView(view, photoTag, 0);
        //
        if (!isSuggestionHelpExists()) addSuggestionHelp(photoTag, view);
    }


    protected void addCreationTagView(float x, float y) {
        addCreationTag(x, y);
    }

    protected void addCreationTagViewBasedOnSuggestion(PhotoTag suggestion) {
        TagPosition absolute = convertToAbsolute(suggestion.getProportionalPosition(), imageBounds);
        float x = absolute.getTopLeft().getX() + (absolute.getBottomRight().getX() - absolute.getTopLeft().getX()) / 2;
        float y = absolute.getBottomRight().getY();
        CreationTagView creationTagView = addCreationTag(x, y);
        creationTagView.setSuggestionTag(suggestion);
    }


    private CreationTagView addCreationTag(float x, float y) {
        removeUncompletedViews();
        CreationTagView view = new CreationTagView(getContext());
        view.setTagListener(new TagCreationActionsListener() {
            @Override
            public void requestFriendList(String query, int page) {
                manager.requestFriends(query, page);
            }

            @Override
            public void onTagCreated(CreationTagView newTagView, PhotoTag suggestionTag, PhotoTag tag) {
                tag = PhotoTag.cloneTag(tag);
                addExistsTagView(tag);
                if (suggestionTag != null) {
                    TagPosition pos = suggestionTag.getProportionalPosition();
                    PhotoTag nextSuggestion = findNextSuggestion(pos);
                    if (nextSuggestion != null) {
                        addCreationTagViewBasedOnSuggestion(nextSuggestion);
                    }
                    removeTag(suggestionTag);
                }
                removeView(newTagView);
                manager.notifyTagCreated(tag);

            }

        });
        PhotoTag photoTag = new PhotoTag(convertToProportional(new TagPosition(x, y, x, y), imageBounds), 0);
        addTagView(view, photoTag);
        return view;
    }

    private void removeTag(PhotoTag tag) {
        try {
            for (int i = 0; i < getChildCount(); i++) {
                TagView childAt = (TagView) getChildAt(i);
                if (childAt.getPhotoTag().equals(tag)) {
                    removeView(childAt);
                }
            }
        } catch (Exception e) {
            Timber.i(e, "");
        }
    }

    protected <T extends TagView> void addTagView(T view, PhotoTag photoTag) {
        addTagView(view, photoTag, -1);
    }

    protected <T extends TagView> void addTagView(T view, PhotoTag photoTag, int viewPos) {
        TagPosition tagPosition = convertToAbsolute(photoTag.getProportionalPosition(), imageBounds);
        view.setAbsoluteTagPosition(tagPosition);

        view.setPhotoTag(photoTag);
        LayoutParams layoutParams = calculatePosition(view);
        addView(view, viewPos, layoutParams);
    }

    protected void addSuggestionHelp(PhotoTag photoTag, SuggestionTagView suggestionTagView) {
        SuggestionHelpView helpView = new SuggestionHelpView(getContext());
        addTagView(helpView, photoTag, 0);
        suggestionTagView.setSuggestionHelpView(helpView);
    }

    @NonNull
    private LayoutParams calculatePosition(TagView view) {
        LayoutParams layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        Size tagSize = view.getSize();
        float tagWidth = tagSize.getWidth();
        float tagHeight = tagSize.getHeight();
        int photoTagXPos = (int) (view.getAbsoluteTagPosition().getBottomRight().getX() - tagWidth / 2);
        int photoTagYPos = (int) (view.getAbsoluteTagPosition().getBottomRight().getY());

        if (photoTagXPos < 0) {
            photoTagXPos = 0;
        }
        if (view.getAbsoluteTagPosition().getTopLeft().getX() > getWidth() - tagWidth) {
            photoTagXPos = (int) (getWidth() - tagWidth);
        }
        if (photoTagYPos < 0) {
            photoTagYPos = 0;
        }
        if (view.getAbsoluteTagPosition().getTopLeft().getY() > getHeight() - tagHeight) {
            photoTagYPos = (int) (getHeight() - tagHeight - (getHeight() - photoTagYPos));
        }

        layoutParams.leftMargin = photoTagXPos;
        layoutParams.topMargin = photoTagYPos;
        return layoutParams;
    }

    private boolean isSuggestionHelpExists() {
        for (int i = 0; i < getChildCount(); i++) {
            if (getChildAt(i) instanceof SuggestionHelpView) {
                return true;
            }
        }
        return false;
    }

    protected void removeUncompletedViews() {
        View view = getChildAt(getChildCount() - 1);
        if (view instanceof CreationTagView) removeView(view);
    }

    protected CreationTagView getCreationTagView() {
        View view = getChildAt(getChildCount() - 1);
        if (view instanceof CreationTagView) return (CreationTagView) view;
        return null;
    }

    private PhotoTag findNextSuggestion(TagPosition pos) {
        float originX = pos.getTopLeft().getX();
        float originY = pos.getTopLeft().getY();

        PhotoTag result = null;
        float minX = Float.MAX_VALUE, minY = Float.MAX_VALUE;
        for (int i = 0; i < getChildCount(); i++) {
            if (getChildAt(i) instanceof SuggestionTagView) {
                Position childPos = ((SuggestionTagView) getChildAt(i)).getPhotoTag().getProportionalPosition().getTopLeft();
                if (childPos.getY() >= originY && childPos.getX() > originX || childPos.getY() > originY && childPos.getX() >= originX) {
                    if (childPos.getY() - originY < minY) {
                        result = ((SuggestionTagView) getChildAt(i)).getPhotoTag();
                        minX = childPos.getX() - originX;
                        minY = childPos.getY() - originY;
                    } else if (childPos.getY() - originY == minY) {
                        if (originX - childPos.getX() < minX) {
                            result = ((SuggestionTagView) getChildAt(i)).getPhotoTag();
                            minX = childPos.getX() - originX;
                            minY = childPos.getY() - originY;
                        }
                    }
                }
            }
        }
        return result;

    }

}
