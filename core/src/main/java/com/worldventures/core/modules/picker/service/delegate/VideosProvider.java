package com.worldventures.core.modules.picker.service.delegate;

import com.worldventures.core.modules.picker.model.VideoPickerModel;

import java.util.List;

public interface VideosProvider {

   List<VideoPickerModel> provide(int count);
}
