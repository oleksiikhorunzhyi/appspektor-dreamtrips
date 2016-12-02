package com.worldventures.dreamtrips.modules.background_uploading.model;

import com.worldventures.dreamtrips.modules.common.view.custom.tagview.viewgroup.newio.model.PhotoTag;

import org.immutables.value.Value;

import java.util.List;

@Value.Immutable
public interface Attachment {

   int id();
   State state();
   String filePath();
   String originUrl();

   String title();
   List<PhotoTag> photoTagList();
   int progress();
   int width();
   int height();

   public enum State {
      SCHEDULED, STARTED, UPLOADED, FAILED
   }
}
