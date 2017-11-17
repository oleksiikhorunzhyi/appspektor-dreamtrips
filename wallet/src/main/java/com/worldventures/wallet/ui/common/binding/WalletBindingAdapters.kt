package com.worldventures.wallet.ui.common.binding

import android.databinding.BindingAdapter
import com.facebook.drawee.view.SimpleDraweeView

@BindingAdapter("imageUrl")
fun setImageUrl(view: SimpleDraweeView, imageUrl: String?) {
   view.setImageURI(imageUrl)
}