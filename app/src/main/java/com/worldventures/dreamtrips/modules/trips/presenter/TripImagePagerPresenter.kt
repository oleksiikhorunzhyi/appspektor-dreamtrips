package com.worldventures.dreamtrips.modules.trips.presenter

import android.net.Uri

import com.worldventures.core.model.ImagePathHolder
import com.worldventures.core.utils.ImageUtils
import com.worldventures.dreamtrips.core.ui.fragment.BaseImagePresenter
import com.worldventures.dreamtrips.core.ui.fragment.ImageBundle

class TripImagePagerPresenter(bundle: ImageBundle<ImagePathHolder>) : BaseImagePresenter(bundle) {

   override fun createUri(image: ImagePathHolder, width: Int, height: Int): Uri {
      return Uri.parse(ImageUtils.getParametrizedUrl(image.imagePath, width, height))
   }
}

