package com.fieldbook.tracker.fragments

import android.app.ProgressDialog
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Html
import android.util.Log
import androidx.documentfile.provider.DocumentFile
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import com.fieldbook.tracker.R
import com.fieldbook.tracker.database.DataHelper
import com.fieldbook.tracker.preferences.GeneralKeys
import com.fieldbook.tracker.utilities.Utils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.phenoapps.utils.BaseDocumentTreeUtil
import javax.inject.Inject

@AndroidEntryPoint
class ImportDBFragment : Fragment(){

    @Inject
    lateinit var database: DataHelper
    val handler: Handler = Handler(Looper.getMainLooper())

    lateinit var dialog: ProgressDialog
    private var fail: Boolean = false

    //coroutine scope for launching background process
    private val scope by lazy {
        CoroutineScope(Dispatchers.IO)
    }


    private var mContext: Context? = null
    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onDetach() {
        super.onDetach()
        mContext = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        scope.launch {

            withContext(Dispatchers.Main){
                showImportDialog()
            }

            // Load database with sample data
            try {
                val sampleDatabase = BaseDocumentTreeUtil.getFile(
                    mContext,
                    R.string.dir_database, "sample.db"
                )
                if (sampleDatabase != null && sampleDatabase.exists()) {
                    // database import might take some time
                    withContext(Dispatchers.IO){
                        invokeDBImport(sampleDatabase)
                    }
                }
            } catch (e: Exception){
                Log.d("Database", e.toString())
                e.printStackTrace()
                fail = true
            }

            withContext(Dispatchers.Main){

                dialog.dismiss()
                if (fail) {
                    Utils.makeToast(mContext, context?.getString(R.string.import_error_general))
                }

                // reset the preference value
                val prefs = mContext?.let { PreferenceManager.getDefaultSharedPreferences(it) }
                prefs?.edit()?.putBoolean(GeneralKeys.LOAD_SAMPLE_DATA, false)?.apply()
                val temp = prefs?.getBoolean(GeneralKeys.LOAD_SAMPLE_DATA,true)

            }

        }
    }

    private fun invokeDBImport(dbFile: DocumentFile) {
        database.open()
        database.importDatabase(dbFile)
    }

    private fun showImportDialog() {
        // show only if the fragment is added to the activity
        if (isAdded) {
            dialog = ProgressDialog(mContext, R.style.AppAlertDialog)
            dialog.isIndeterminate = true
            dialog.setCancelable(false)
            dialog.setMessage(
                Html
                    .fromHtml(context?.getString(R.string.import_dialog_importing))
            )
            dialog.show()
        }
    }

}