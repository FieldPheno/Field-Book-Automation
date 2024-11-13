package com.fieldbook.tracker.activities.brapi.io.filter

import android.content.Context
import android.content.Intent
import com.fieldbook.tracker.R
import com.fieldbook.tracker.activities.brapi.io.BrapiCacheModel
import com.fieldbook.tracker.activities.brapi.io.TrialStudyModel
import com.fieldbook.tracker.adapters.CheckboxListAdapter
import org.brapi.v2.model.core.BrAPISeason

open class BrapiSeasonsFilterActivity(override val titleResId: Int = R.string.brapi_filter_type_season) :
    BrapiSubFilterListActivity<BrAPISeason>() {

    companion object {

        const val FILTER_NAME = "seasonDbIds"

        fun getIntent(context: Context): Intent {
            return Intent(context, BrapiSeasonsFilterActivity::class.java)
        }
    }

    override val filterName: String
        get() = FILTER_NAME

    override fun BrapiCacheModel.filterByPreferences(): BrapiCacheModel {

        val programDbIds = getIds(BrapiProgramFilterActivity.FILTER_NAME)
        val trialDbIds = getIds(BrapiTrialsFilterActivity.FILTER_NAME)
        return this.also {
            it.studies =
                it.studies.filter { if (programDbIds.isNotEmpty()) it.programDbId in programDbIds else true }
                    .filter { if (trialDbIds.isNotEmpty()) it.trialDbId in trialDbIds else true }
        }
    }

    override fun BrapiCacheModel.mapToUiModel(): List<CheckboxListAdapter.Model> {

        val seasonSet = hashSetOf<String>()

        studies.forEach { model ->
            model.study.seasons?.filterNotNull()?.forEach { season ->
                seasonSet.add(season)
            }
        }

        return seasonSet.toList().map {
            CheckboxListAdapter.Model(
                checked = false,
                id = it,
                label = it,
                subLabel = ""
            )
        }
    }
}
