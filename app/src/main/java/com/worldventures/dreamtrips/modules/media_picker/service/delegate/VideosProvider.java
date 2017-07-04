package com.worldventures.dreamtrips.modules.media_picker.service.delegate;

import com.worldventures.dreamtrips.modules.media_picker.model.VideoPickerModel;

import java.util.List;

public interface VideosProvider {

   List<VideoPickerModel> provide();
}
