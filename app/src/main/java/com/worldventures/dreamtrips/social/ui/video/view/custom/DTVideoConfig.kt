package com.worldventures.dreamtrips.social.ui.video.view.custom

import com.worldventures.dreamtrips.social.ui.feed.model.video.Quality

data class DTVideoConfig(val uid: String, var mute: Boolean = false,
                         val qualities: List<Quality>, var selectedQualityPosition: Int = 0)
