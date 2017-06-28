package com.worldventures.dreamtrips.wallet.ui.common.picker.base;

import com.worldventures.dreamtrips.modules.media_picker.model.MediaPickerModel;


public abstract class BasePickerViewModel implements MediaPickerModel {

   public  abstract int type(WalletPickerHolderFactory typeFactory);

   public abstract void setPickedTime(long pickedTime);
}
