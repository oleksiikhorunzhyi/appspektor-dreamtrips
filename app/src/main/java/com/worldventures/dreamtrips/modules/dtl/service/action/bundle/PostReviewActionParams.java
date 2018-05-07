package com.worldventures.dreamtrips.modules.dtl.service.action.bundle;

import android.support.annotation.Nullable;

import com.worldventures.core.modules.picker.model.PhotoPickerModel;

import org.immutables.value.Value;

import java.util.List;

@Value.Immutable
public interface PostReviewActionParams {

   @Nullable
   List<PhotoPickerModel> attachments();

   String productId();

   String comment();

   Integer rating();

   Boolean verified();
}
