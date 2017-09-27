package com.worldventures.dreamtrips.social.ui.feed.model;

import android.support.annotation.Nullable;

import com.worldventures.dreamtrips.modules.common.model.MediaAttachment;
import com.worldventures.dreamtrips.social.ui.feed.view.custom.tagview.viewgroup.newio.model.PhotoTag;
import com.worldventures.dreamtrips.modules.trips.model.Location;

import org.immutables.value.Value;

import java.util.ArrayList;

@Value.Immutable
@Value.Style(privateNoargConstructor = true, defaultAsDefault = true)
public interface SelectedPhoto {
   String path();
   int width();
   int height();
   MediaAttachment.Source source();
   @Nullable Location locationFromPost();
   @Nullable Location locationFromExif(); //analytics related
   @Nullable String title();
   @Nullable ArrayList<PhotoTag> tags();
   long size();
}
