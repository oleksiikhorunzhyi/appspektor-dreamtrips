package com.worldventures.core.modules.picker;

import com.worldventures.core.modules.picker.helper.PickerPermissionChecker;
import com.worldventures.core.modules.picker.helper.PickerPermissionUiHandler;
import com.worldventures.core.ui.util.permission.PermissionDispatcher;

import dagger.Module;
import dagger.Provides;

@Module(complete = false, library = true)
public class MediaPickerHelperActivityModule {

   @Provides
   PickerPermissionChecker providePickerPermissionChecker(PermissionDispatcher permissionDispatcher) {
      return new PickerPermissionChecker(permissionDispatcher);
   }

   @Provides
   PickerPermissionUiHandler providePickerPermissionUiHandler() {
      return new PickerPermissionUiHandler();
   }

}
