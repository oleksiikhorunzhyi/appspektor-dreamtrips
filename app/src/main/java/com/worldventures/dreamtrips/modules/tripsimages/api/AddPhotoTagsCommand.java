package com.worldventures.dreamtrips.modules.tripsimages.api;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.api.request.Command;
import com.worldventures.dreamtrips.modules.common.view.custom.tagview.viewgroup.newio.model.PhotoTag;
import com.worldventures.dreamtrips.modules.tripsimages.model.AddPhotoTag;

import java.util.ArrayList;
import java.util.List;

public class AddPhotoTagsCommand extends Command<ArrayList<PhotoTag>> {

   private String photoId;
   private List<PhotoTag> tags;

   public AddPhotoTagsCommand(String photoId, List<PhotoTag> tags) {
      super((Class<ArrayList<PhotoTag>>) new ArrayList<PhotoTag>().getClass());
      this.photoId = photoId;
      this.tags = tags;
   }

   @Override
   public ArrayList<PhotoTag> loadDataFromNetwork() throws Exception {
      return getService().addPhotoTags(photoId, new AddPhotoTag(tags));
   }

   @Override
   public int getErrorMessage() {
      return R.string.error_fail_to_tag_people;
   }
}
