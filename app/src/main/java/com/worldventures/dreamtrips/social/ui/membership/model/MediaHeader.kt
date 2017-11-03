package com.worldventures.dreamtrips.social.ui.membership.model

import com.worldventures.core.modules.video.model.VideoLanguage
import com.worldventures.core.modules.video.model.VideoLocale

class MediaHeader @JvmOverloads constructor(val title: String, val showLanguage: Boolean = false,
                   var videoLocale: VideoLocale? = null, var videoLanguage: VideoLanguage? = null)