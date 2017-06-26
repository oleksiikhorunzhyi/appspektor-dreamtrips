package com.worldventures.dreamtrips.wallet.ui.common.picker.base;

import com.worldventures.dreamtrips.modules.common.model.BasePhotoPickerModel;


public abstract class BasePickerViewModel implements BasePhotoPickerModel {

   public  abstract int type(WalletPickerHolderFactory typeFactory);

   public abstract void setPickedTime(long pickedTime);
}
