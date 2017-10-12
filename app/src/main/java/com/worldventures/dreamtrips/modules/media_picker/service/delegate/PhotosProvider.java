package com.worldventures.dreamtrips.modules.media_picker.service.delegate;

import com.worldventures.dreamtrips.modules.media_picker.model.PhotoPickerModel;

import java.util.Date;
import java.util.List;

public interface PhotosProvider {

   List<PhotoPickerModel> provide(Date maxDateTaken, int count);
}
