package com.fieldbook.tracker.activities.brapi.update

import android.content.Context
import android.content.Intent
import android.view.View
import com.fieldbook.tracker.R
import com.fieldbook.tracker.adapters.CheckboxListAdapter
import com.fieldbook.tracker.brapi.service.BrAPIServiceV2
import com.google.gson.reflect.TypeToken
import io.swagger.client.ApiException
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import org.brapi.client.v2.model.queryParams.core.ProgramQueryParams
import org.brapi.v2.model.core.BrAPIProgram
import org.brapi.v2.model.core.response.BrAPIProgramListResponse
import java.lang.reflect.Type

open class BrapiProgramFilterActivity(override val titleResId: Int = R.string.brapi_filter_type_program) :
    BrapiFilterActivity<BrAPIProgram, ProgramQueryParams, BrAPIProgramListResponse>() {

    companion object {

        const val FILTER_NAME = "$PREFIX.programDbIds"

        fun getIntent(context: Context): Intent {
            return Intent(context, BrapiProgramFilterActivity::class.java)
        }
    }

    override val filterName: String
        get() = FILTER_NAME

    override suspend fun queryByPage(params: ProgramQueryParams) = callbackFlow {

        try {

            (brapiService as BrAPIServiceV2).fetchPrograms(params, { response ->

                response.validateResponse { pagination, result ->

                    trySend(pagination to result.data.filterIsInstance<BrAPIProgram>())

                }

            }) { failCode ->

                trySend(null)

                toggleProgressBar(View.INVISIBLE)

                showErrorCode(failCode)
            }

            awaitClose()

        } catch (e: ApiException) {
            e.printStackTrace()
        }
    }

    override fun getQueryParams() = ProgramQueryParams()

    override fun List<BrAPIProgram?>.mapToUiModel() = filterNotNull().map { program ->
        CheckboxListAdapter.Model(
            checked = false,
            id = program.programDbId,
            label = program.programName,
            subLabel = program.programType ?: String()
        )
    }

    //TODO make these static?
    override fun getTypeToken(): Type =
        TypeToken.getParameterized(List::class.java, BrAPIProgram::class.java).type

    override fun onCreateOptionsMenu(menu: android.view.Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        menu?.findItem(R.id.action_check_all)?.isVisible = true
        menu?.findItem(R.id.action_reset_cache)?.isVisible = true
        menu?.findItem(R.id.action_brapi_filter)?.isVisible = false
        return true
    }
}
