package com.worldventures.dreamtrips.social.ui.membership.model

import com.worldventures.core.modules.video.model.VideoLanguage
import com.worldventures.core.modules.video.model.VideoLocale

// todo delete @JvmOverloads annotation when TrainingVideosPresenter, PresentationVideosPresenter, ThreeSixtyVideosPresenter will be rewritten into Kotlin
class MediaHeader @JvmOverloads constructor(val title: String,
                                            val showLanguage: Boolean = false,
                                            var videoLocale: VideoLocale? = null,
                                            var videoLanguage: VideoLanguage? = null)