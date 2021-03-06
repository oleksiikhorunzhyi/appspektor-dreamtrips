package com.worldventures.dreamtrips.social.ui.feed.view.custom.tagview.viewgroup.newio;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.view.SimpleDraweeView;
import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.core.utils.Size;
import com.worldventures.dreamtrips.social.ui.feed.view.custom.tagview.CreationTagView;
import com.worldventures.dreamtrips.social.ui.feed.view.custom.tagview.ExistsTagView;
import com.worldventures.dreamtrips.social.ui.feed.view.custom.tagview.SuggestionTagView;
import com.worldventures.dreamtrips.social.ui.feed.view.custom.tagview.TagCreationActionsListener;
import com.worldventures.dreamtrips.social.ui.feed.view.custom.tagview.TagSuggestionActionListener;
import com.worldventures.dreamtrips.social.ui.feed.view.custom.tagview.TagView;
import com.worldventures.dreamtrips.social.ui.feed.view.custom.tagview.viewgroup.SuggestionHelpView;
import com.worldventures.dreamtrips.social.ui.feed.view.custom.tagview.viewgroup.newio.model.PhotoTag;
import com.worldventures.dreamtrips.social.ui.feed.view.custom.tagview.viewgroup.newio.model.TagPosition;

import java.util.ArrayList;
import java.util.List;

import icepick.Icepick;
import icepick.State;
import timber.log.Timber;

import static com.worldventures.dreamtrips.modules.common.view.custom.tagview.viewgroup.newio.CoordinatesTransformer.convertToAbsolute;
import static com.worldventures.dreamtrips.modules.common.view.custom.tagview.viewgroup.newio.CoordinatesTransformer.convertToProportional;


public class PhotoTagHolder extends RelativeLayout {

   private static final int MAX_WIDTH = 1;
   private static int index;

   @State boolean isShown;
   @State RectF imageBounds = new RectF();

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

   public RectF getImageBounds() {
      return imageBounds;
   }

   protected void hide() {
      setVisibility(View.INVISIBLE);
      isShown = false;
   }

   void addExistsTagView(PhotoTag photoTag, boolean deleteEnabled) {
      if (isExistingTagViewExists(photoTag)) {
         return;
      }
      ExistsTagView view = new ExistsTagView(getContext());
      view.setDeleteEnabled(deleteEnabled);
      view.setTagListener(tag -> manager.notifyTagDeleted(tag));
      addTagView(view, photoTag);
   }

   void addSuggestionTagView(PhotoTag photoTag, TagSuggestionActionListener tagSuggestionActionListener) {
      if (isSuggestionViewExists(photoTag)) {
         return;
      }
      SuggestionTagView view = new SuggestionTagView(getContext());
      view.setTagListener(tagSuggestionActionListener);
      addTagView(view, photoTag, 0);
      if (!isSuggestionHelpExists()) {
         addSuggestionHelp(photoTag);
      }
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
      removeSuggestionHelp();
      CreationTagView view = new CreationTagView(getContext());
      view.setTagListener(new TagCreationActionsListener() {
         @Override
         public void requestFriendList(String query, int page) {
            manager.requestFriends(query, page);
         }

         @Override
         public void onTagCreated(CreationTagView newTagView, PhotoTag suggestionTag, PhotoTag tag) {
            ArrayList<PhotoTag> photoTags = new ArrayList<>();
            photoTags.add(PhotoTag.cloneTag(tag));
            manager.addExistsTagViews(photoTags);
            if (suggestionTag != null) {
               TagPosition pos = suggestionTag.getProportionalPosition();
               removeTag(suggestionTag);
               PhotoTag nextSuggestion = findNextSuggestion(pos);
               if (nextSuggestion != null) {
                  addCreationTagViewBasedOnSuggestion(nextSuggestion);
               }
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
         List<View> viewsToRemove = new ArrayList<>();
         for (int i = 0; i < getChildCount(); i++) {
            TagView childAt = (TagView) getChildAt(i);
            if (childAt.getPhotoTag().equals(tag)) {
               viewsToRemove.add(childAt);
            }
         }
         Queryable.from(viewsToRemove).forEachR(this::removeView);
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

   @NonNull
   private LayoutParams calculatePosition(TagView view) {
      LayoutParams layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
      Size tagSize = view.getSize();
      float tagWidth = tagSize.getWidth();
      float tagHeight = tagSize.getHeight();
      int photoTagXPos = (int) (view.getAbsoluteTagPosition().getBottomRight().getX() - tagWidth / 2);
      int photoTagYPos = (int) (view.getAbsoluteTagPosition().getBottomRight().getY());
      //
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
      //
      layoutParams.leftMargin = photoTagXPos;
      layoutParams.topMargin = photoTagYPos;
      return layoutParams;
   }

   private void addSuggestionHelp(PhotoTag photoTag) {
      SuggestionHelpView helpView = new SuggestionHelpView(getContext());
      addTagView(helpView, photoTag, 0);
   }

   private void removeSuggestionHelp() {
      removeView(findTagView(null, SuggestionHelpView.class));
   }

   private boolean isSuggestionHelpExists() {
      return findTagView(null, SuggestionHelpView.class) != null;
   }

   private boolean isSuggestionViewExists() {
      return findSuggestionTagView() != null;
   }

   private boolean isSuggestionViewExists(PhotoTag photoTag) {
      return findSuggestionTagView(photoTag) != null;
   }

   private boolean isExistingTagViewExists(PhotoTag photoTag) {
      return findTagView(photoTag, ExistsTagView.class) != null;
   }

   private SuggestionTagView findSuggestionTagView() {
      return findSuggestionTagView(null);
   }

   private SuggestionTagView findSuggestionTagView(@Nullable PhotoTag photoTag) {
      return findTagView(photoTag, SuggestionTagView.class);
   }

   private <T extends TagView> T findTagView(@Nullable PhotoTag photoTag, Class<T> clazz) {
      for (int i = 0; i < getChildCount(); i++) {
         View view = getChildAt(i);
         if (view.getClass().equals(clazz)) {
            T tagView = (T) view;
            if (photoTag == null || tagView.getPhotoTag().equals(photoTag)) {
               return tagView;
            }
         }
      }
      return null;
   }

   protected void removeUncompletedViews() {
      View view = getChildAt(getChildCount() - 1);
      if (view instanceof CreationTagView) {
         removeView(view);
      }
   }

   protected CreationTagView getCreationTagView() {
      View view = getChildAt(getChildCount() - 1);
      if (view instanceof CreationTagView) {
         return (CreationTagView) view;
      }
      return null;
   }

   private PhotoTag findNextSuggestion(TagPosition pos) {
      if (!isSuggestionViewExists() || index++ > 100) {
         return null;
      }
      //
      float cX = getCenterX(pos);
      float cY = getCenterY(pos);
      PhotoTag result = null;
      double minDistance = Double.MAX_VALUE;
      for (int i = 0; i < getChildCount(); i++) {
         if (getChildAt(i) instanceof SuggestionTagView) {
            TagPosition childPos = ((SuggestionTagView) getChildAt(i)).getPhotoTag().getProportionalPosition();
            if (isOnWay(pos, childPos)) {
               double distance = Math.hypot(cX - getCenterX(childPos), cY - getCenterY(childPos));
               if (distance < minDistance) {
                  minDistance = distance;
                  result = ((SuggestionTagView) getChildAt(i)).getPhotoTag();
               }
            }
         }
      }
      if (result == null) {
         return findTagOnNextLine(pos);
      }
      //
      return result;
   }

   private boolean isOnWay(TagPosition way, TagPosition barricade) {
      int wbX = (int) (way.getBottomRight().getX() * 100);
      int wtY = (int) (way.getTopLeft().getY() * 100);
      int wbY = (int) (way.getBottomRight().getY() * 100);
      //
      Rect r1 = new Rect(wbX, wtY, MAX_WIDTH * 100, wbY);
      int tX = (int) (barricade.getTopLeft().getX() * 100);
      int tY = (int) (barricade.getTopLeft().getY() * 100);
      int bX = (int) (barricade.getBottomRight().getX() * 100);
      int bY = (int) (barricade.getBottomRight().getY() * 100);
      Rect r2 = new Rect(tX, tY, bX, bY);
      return r1.intersect(r2) || r1.contains(r2);
   }

   private static float getCenterX(TagPosition pos) {
      return pos.getTopLeft().getX() + Math.abs(pos.getBottomRight().getX() - pos.getTopLeft().getX()) / 2;
   }

   private static float getCenterY(TagPosition pos) {
      return pos.getTopLeft().getY() + Math.abs(pos.getBottomRight().getY() - pos.getTopLeft().getY()) / 2;
   }

   private PhotoTag findTagOnNextLine(TagPosition lastTagPosition) {
      TagPosition tagPosition = new TagPosition(0, lastTagPosition.getBottomRight()
            .getY(), 0, lastTagPosition.getBottomRight().getY() + (lastTagPosition.getBottomRight()
            .getY() - lastTagPosition.getTopLeft().getY()));
      if (tagPosition.getTopLeft().getY() >= MAX_WIDTH) {
         TagPosition restartTagPos = new TagPosition(0, 0, 0, lastTagPosition.getBottomRight()
               .getY() - lastTagPosition.getTopLeft().getY());
         return findNextSuggestion(restartTagPos);
      } else {
         return findNextSuggestion(tagPosition);
      }
   }
}
