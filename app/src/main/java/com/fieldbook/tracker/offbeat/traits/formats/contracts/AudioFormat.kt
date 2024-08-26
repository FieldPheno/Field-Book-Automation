package com.fieldbook.tracker.offbeat.traits.formats.contracts

import com.fieldbook.tracker.R
import com.fieldbook.tracker.offbeat.traits.formats.Formats
import com.fieldbook.tracker.offbeat.traits.formats.TraitFormat
import com.fieldbook.tracker.offbeat.traits.formats.parameters.DetailsParameter
import com.fieldbook.tracker.offbeat.traits.formats.parameters.NameParameter
import com.fieldbook.tracker.offbeat.traits.formats.presenters.UriPresenter
import com.fieldbook.tracker.offbeat.traits.formats.presenters.ValuePresenter

class AudioFormat : TraitFormat(
    format = Formats.AUDIO,
    defaultLayoutId = R.layout.trait_audio,
    layoutView = null,
    databaseName = "audio",
    nameStringResourceId = R.string.traits_format_audio,
    iconDrawableResourceId = R.drawable.trait_audio,
    stringNameAux = null,
    NameParameter(),
    DetailsParameter()
), ValuePresenter by UriPresenter()