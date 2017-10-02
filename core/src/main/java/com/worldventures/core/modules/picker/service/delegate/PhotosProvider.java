package com.worldventures.core.modules.picker.service.delegate;

import com.worldventures.core.modules.picker.model.PhotoPickerModel;

import java.util.Date;
import java.util.List;

public interface PhotosProvider {

   List<PhotoPickerModel> provide(Date maxDateTaken, int count);
}
