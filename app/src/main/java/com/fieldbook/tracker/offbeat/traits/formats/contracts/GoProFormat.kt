package com.fieldbook.tracker.offbeat.traits.formats.contracts

import com.fieldbook.tracker.R
import com.fieldbook.tracker.offbeat.traits.formats.Formats

class GoProFormat : BasePhotoFormat(
    format = Formats.GO_PRO,
    nameStringResourceId = R.string.traits_format_go_pro_camera,
    iconDrawableResourceId = R.drawable.ic_trait_gopro,
)