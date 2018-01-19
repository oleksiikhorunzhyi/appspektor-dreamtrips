package com.worldventures.wallet.ui.common.binding

import android.databinding.BindingAdapter
import android.support.design.widget.TextInputLayout
import com.facebook.drawee.view.SimpleDraweeView

@BindingAdapter("imageUrl")
fun setImageUrl(view: SimpleDraweeView, imageUrl: String?) {
   view.setImageURI(imageUrl)
}

@BindingAdapter("errorText")
fun setErrorMessage(view: TextInputLayout, errorMessage: CharSequence) {
   view.error = errorMessage
}

