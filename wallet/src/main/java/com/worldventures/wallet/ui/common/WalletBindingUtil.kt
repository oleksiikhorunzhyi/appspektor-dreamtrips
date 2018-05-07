package com.worldventures.wallet.ui.common

import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.view.View

internal fun <T: ViewDataBinding>bindView(rootView: View): T =
      DataBindingUtil.bind(rootView) ?: throw NullPointerException("Binding for current view is not exist")
