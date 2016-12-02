package com.worldventures.dreamtrips.modules.background_uploading.model;

import com.worldventures.dreamtrips.modules.trips.model.Location;

import org.immutables.value.Value;

import java.util.List;

@Value.Immutable
public interface PostWithAttachmentBody {
   String text();
   Location location();
   List<Attachment> attachments();
}
