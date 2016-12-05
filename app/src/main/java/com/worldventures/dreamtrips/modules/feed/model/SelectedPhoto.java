package com.worldventures.dreamtrips.modules.feed.model;

import android.support.annotation.Nullable;

import com.worldventures.dreamtrips.modules.common.view.custom.tagview.viewgroup.newio.model.PhotoTag;
import com.worldventures.dreamtrips.modules.trips.model.Location;

import org.immutables.value.Value;

import java.util.List;

@Value.Immutable
public interface SelectedPhoto {
   String path();
   int width();
   int height();
   @Nullable Location locationFromPost();
   @Nullable Location locationFromExif(); //analytics related
   @Nullable String title();
   @Nullable List<PhotoTag> tags();
}
