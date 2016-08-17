package com.worldventures.dreamtrips.modules.tripsimages.presenter;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.core.rx.RxView;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.common.view.custom.tagview.viewgroup.newio.PhotoTagHolderManager;
import com.worldventures.dreamtrips.modules.common.view.custom.tagview.viewgroup.newio.model.PhotoTag;
import com.worldventures.dreamtrips.modules.friends.api.GetFriendsQuery;

import java.util.ArrayList;
import java.util.List;

import icepick.State;
import rx.functions.Action1;

public class EditPhotoTagsPresenter extends Presenter<EditPhotoTagsPresenter.View> implements PhotoTagHolderManager.FriendRequestProxy {

   private static final int PAGE_SIZE = 100;

   @State ArrayList<PhotoTag> locallyAddedTags = new ArrayList<>();
   @State ArrayList<PhotoTag> locallyDeletedTags = new ArrayList<>();

   private long requestId;
   private List<PhotoTag> photoTags;

   public EditPhotoTagsPresenter(long requestId, List<PhotoTag> photoTags) {
      this.requestId = requestId;
      this.photoTags = photoTags;
   }

   @Override
   public void requestFriends(String query, int page, Action1<List<User>> act) {
      doRequest(new GetFriendsQuery(null, query, page, PAGE_SIZE), friends -> act.call(Queryable.from(friends)
            .filter(user -> !isUserExists(user))
            .toList()));
   }

   private boolean isUserExists(User user) {
      boolean containsOnServer = isContainUser(photoTags, user);
      boolean containsUserInLocallyAdded = isContainUser(locallyAddedTags, user);
      boolean containUserInDeleted = isContainUser(locallyDeletedTags, user);
      return containsUserInLocallyAdded || (containsOnServer && !containUserInDeleted);
   }

   private boolean isContainUser(List<PhotoTag> tagList, User user) {
      return Queryable.from(tagList).map(tag -> tag.getUser() == null ? new User() : tag.getUser()).contains(user);
   }

   public void onTagAdded(PhotoTag tag) {
      locallyAddedTags.add(tag);
      locallyDeletedTags.remove(tag);
   }

   public void onTagDeleted(PhotoTag tag) {
      locallyDeletedTags.add(tag);
      locallyAddedTags.remove(tag);
   }

   public void onDone() {
      locallyAddedTags.removeAll(photoTags);
      locallyAddedTags.addAll(photoTags);
      locallyAddedTags.removeAll(locallyDeletedTags);
      //
      view.notifyAboutTags(requestId, locallyAddedTags, locallyDeletedTags);
   }

   public interface View extends RxView {

      void notifyAboutTags(long requestId, ArrayList<PhotoTag> addedTags, ArrayList<PhotoTag> deletedTags);
   }
}
