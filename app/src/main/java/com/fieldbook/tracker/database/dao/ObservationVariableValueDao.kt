package com.fieldbook.tracker.database.dao

import android.content.ContentValues
import androidx.core.content.contentValuesOf
import com.fieldbook.tracker.database.*
import com.fieldbook.tracker.database.Migrator.ObservationVariable
import com.fieldbook.tracker.database.Migrator.ObservationVariableValue

class ObservationVariableValueDao {

    companion object {

        fun getVariableValues(id: Int) = withDatabase { db ->

            return@withDatabase db.query(ObservationVariableValue.tableName,
                    where = "${ObservationVariable.FK} = ?",
                    whereArgs = arrayOf("$id")).toTable()

        }

        fun update(varId: String, attrId: String, value: String) = withDatabase { db ->

            db.update(ObservationVariableValue.tableName, ContentValues().apply {
                put("observation_variable_attribute_value", value)

            }, "${ObservationVariable.FK} = ? AND ${Migrator.ObservationVariableAttribute.FK} = ?", arrayOf(varId, attrId))

        }

        fun insertCloseKeyboard(closeKeyboardOnOpen: String, id: String) = withDatabase { db ->

            val attrId = ObservationVariableAttributeDao.getAttributeIdByName("closeKeyboardOnOpen")

            db.insert(ObservationVariableValue.tableName, null, contentValuesOf(

                ObservationVariable.FK to id,
                Migrator.ObservationVariableAttribute.FK to attrId,
                "observation_variable_attribute_value" to closeKeyboardOnOpen

            ))
        }

        fun insert(min: String, max: String, categories: String, closeKeyboardOnOpen: String, id: String) = withDatabase { db ->

            //iterate through mapping of the old columns that are now attr/vals
            mapOf(
                    "validValuesMin" to min,
                    "validValuesMax" to max,
                    "category" to categories,
                    "closeKeyboardOnOpen" to closeKeyboardOnOpen
            ).asSequence().forEach { attrValue ->

                val attrId = ObservationVariableAttributeDao.getAttributeIdByName(attrValue.key)

                db.insert(ObservationVariableValue.tableName, null, contentValuesOf(

                        ObservationVariable.FK to id,
                        Migrator.ObservationVariableAttribute.FK to attrId,
                        "observation_variable_attribute_value" to attrValue.value

                ))
            }
        }
    }
}