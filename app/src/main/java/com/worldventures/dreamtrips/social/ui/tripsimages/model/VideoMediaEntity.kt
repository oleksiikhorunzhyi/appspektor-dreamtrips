package com.worldventures.dreamtrips.social.ui.tripsimages.model

import com.worldventures.dreamtrips.social.ui.feed.model.video.Video

class VideoMediaEntity(val video: Video) : BaseMediaEntity<Video>(video, MediaEntityType.VIDEO)
