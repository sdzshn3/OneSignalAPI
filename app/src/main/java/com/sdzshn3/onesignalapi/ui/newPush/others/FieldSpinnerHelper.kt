package com.sdzshn3.onesignalapi.ui.newPush.others

import android.app.Activity
import android.content.Context
import android.view.View
import android.widget.*
import com.sdzshn3.onesignalapi.R
import com.sdzshn3.onesignalapi.oneSignalPOJO.Filter


fun verify(
    filter: Filter, context: Activity, fieldSpinner: Spinner, filterValueEditText: EditText,
    filterKeyEditText: EditText, relationSpinner: Spinner,
    filterHoursAgoEditText: EditText, filterRadiusEditText: EditText,
    filterLatitudeEditText: EditText, filterLongitudeEditText: EditText
): Filter? {
    when (fieldSpinner.selectedItemPosition) {
        0 -> {
            // Select
            Toast.makeText(
                context,
                "Please select a field",
                Toast.LENGTH_SHORT
            ).show()
            return null
        }
        1 -> {
            // Tag
            if (filterKeyEditText.text.toString()
                    .isBlank() || filterValueEditText.text.toString().isBlank()
            ) {
                Toast.makeText(
                    context,
                    "Please fill both key and value",
                    Toast.LENGTH_SHORT
                )
                    .show()
                return null
            }
            filter.key = filterKeyEditText.text.toString()
            filter.relation = relationSpinner.selectedItem as String
            filter.value = filterValueEditText.text.toString()
        }
        2 -> {
            // Last Session
            if (filterHoursAgoEditText.text.toString().isBlank()) {
                Toast.makeText(
                    context,
                    "Please fill Hours Ago",
                    Toast.LENGTH_SHORT
                ).show()
                return null
            }
            filter.relation = relationSpinner.selectedItem as String
            filter.hoursAgo = filterHoursAgoEditText.text.toString()
        }
        3 -> {
            // First Session
            if (filterHoursAgoEditText.text.toString().isBlank()) {
                Toast.makeText(
                    context,
                    "Please fill Hours Ago",
                    Toast.LENGTH_SHORT
                ).show()
                return null
            }
            filter.relation = relationSpinner.selectedItem as String
            filter.hoursAgo = filterHoursAgoEditText.text.toString()
        }
        4 -> {
            // Session Count
            if (filterValueEditText.text.toString().isBlank()) {
                Toast.makeText(
                    context,
                    "Please fill value",
                    Toast.LENGTH_SHORT
                ).show()
                return null
            }
            filter.relation = relationSpinner.selectedItem as String
            filter.value = filterValueEditText.text.toString()
        }
        5 -> {
            //Session Time
            if (filterValueEditText.text.toString().isBlank()) {
                Toast.makeText(
                    context,
                    "Please fill value",
                    Toast.LENGTH_SHORT
                ).show()
                return null
            }
            filter.relation = relationSpinner.selectedItem as String
            filter.value = filterValueEditText.text.toString()
        }
        6 -> {
            // Amount Spent
            if (filterValueEditText.text.toString().isBlank()) {
                Toast.makeText(
                    context,
                    "Please fill value",
                    Toast.LENGTH_SHORT
                ).show()
                return null
            }
            filter.relation = relationSpinner.selectedItem as String
            filter.value = filterValueEditText.text.toString()
        }
        7 -> {
            // Bought SKU
            if (filterKeyEditText.text.toString()
                    .isBlank() || filterValueEditText.text.toString().isBlank()
            ) {
                Toast.makeText(
                    context,
                    "Please fill both key and value",
                    Toast.LENGTH_SHORT
                )
                    .show()
                return null
            }
            filter.key = filterKeyEditText.text.toString()
            filter.relation = relationSpinner.selectedItem as String
            filter.value = filterValueEditText.text.toString()
        }
        8 -> {
            // Language
            if (filterValueEditText.text.toString().isBlank()) {
                Toast.makeText(
                    context,
                    "Please fill value",
                    Toast.LENGTH_SHORT
                ).show()
                return null
            }
            filter.relation = relationSpinner.selectedItem as String
            filter.value = filterValueEditText.text.toString()
        }
        9 -> {
            // App Version
            if (filterValueEditText.text.toString().isBlank()) {
                Toast.makeText(
                    context,
                    "Please fill value",
                    Toast.LENGTH_SHORT
                ).show()
                return null
            }
            filter.relation = relationSpinner.selectedItem as String
            filter.value = filterValueEditText.text.toString()
        }
        10 -> {
            // Location
            if (filterRadiusEditText.text.toString().isBlank() ||
                filterLatitudeEditText.text.toString().isBlank() ||
                filterLongitudeEditText.text.toString().isBlank()
            ) {
                Toast.makeText(
                    context,
                    "Please fill Radius, Latitude and Longitude",
                    Toast.LENGTH_SHORT
                ).show()
                return null
            }
            filter.radius = filterRadiusEditText.text.toString()
            filter.latitude = filterLatitudeEditText.text.toString()
            filter.longitude = filterLongitudeEditText.text.toString()
        }
        11 -> {
            // Email
            if (filterValueEditText.text.toString().isBlank()) {
                Toast.makeText(
                    context,
                    "Please fill value",
                    Toast.LENGTH_SHORT
                ).show()
                return null
            }
            filter.value = filterValueEditText.text.toString()
        }
        12 -> {
            // Country
            if (filterValueEditText.text.toString().isBlank()) {
                Toast.makeText(
                    context,
                    "Please fill value",
                    Toast.LENGTH_SHORT
                ).show()
                return null
            }
            filter.relation = relationSpinner.selectedItem as String
            filter.value = filterValueEditText.text.toString()
        }
    }
    return filter
}

fun manage(
    position: Int,
    context: Context,
    keyLinearLayout: LinearLayout,
    relationRelativeLayout: RelativeLayout,
    hoursAgoLinearLayout: LinearLayout,
    valueLinearLayout: LinearLayout,
    radiusLinearLayout: LinearLayout,
    latitudeLinearLayout: LinearLayout,
    longitudeLinearLayout: LinearLayout,
    relationSpinner: Spinner
) {
    when (position) {
        0 -> {
            //Select
            keyLinearLayout.visibility = View.GONE
            relationRelativeLayout.visibility = View.GONE
            hoursAgoLinearLayout.visibility = View.GONE
            valueLinearLayout.visibility = View.GONE
            radiusLinearLayout.visibility = View.GONE
            latitudeLinearLayout.visibility = View.GONE
            longitudeLinearLayout.visibility = View.GONE
        }
        1 -> {
            //Tag
            keyLinearLayout.visibility = View.VISIBLE
            relationRelativeLayout.visibility = View.VISIBLE
            hoursAgoLinearLayout.visibility = View.GONE
            valueLinearLayout.visibility = View.VISIBLE
            radiusLinearLayout.visibility = View.GONE
            latitudeLinearLayout.visibility = View.GONE
            longitudeLinearLayout.visibility = View.GONE

            val relationAdapter = ArrayAdapter.createFromResource(
                context,
                R.array.tag_relations, android.R.layout.simple_spinner_item
            )
            relationAdapter.setDropDownViewResource(R.layout.drop_down_item_layout)
            relationSpinner.adapter = relationAdapter
        }
        2 -> {
            //Last Session
            keyLinearLayout.visibility = View.GONE
            relationRelativeLayout.visibility = View.VISIBLE
            hoursAgoLinearLayout.visibility = View.VISIBLE
            valueLinearLayout.visibility = View.GONE
            radiusLinearLayout.visibility = View.GONE
            latitudeLinearLayout.visibility = View.GONE
            longitudeLinearLayout.visibility = View.GONE

            val lastSessionAdapter = ArrayAdapter.createFromResource(
                context,
                R.array.last_session_relations, android.R.layout.simple_spinner_item
            )
            lastSessionAdapter.setDropDownViewResource(R.layout.drop_down_item_layout)
            relationSpinner.adapter = lastSessionAdapter
        }
        3 -> {
            //First Session
            keyLinearLayout.visibility = View.GONE
            relationRelativeLayout.visibility = View.VISIBLE
            hoursAgoLinearLayout.visibility = View.VISIBLE
            valueLinearLayout.visibility = View.GONE
            radiusLinearLayout.visibility = View.GONE
            latitudeLinearLayout.visibility = View.GONE
            longitudeLinearLayout.visibility = View.GONE

            val firstSessionAdapter = ArrayAdapter.createFromResource(
                context,
                R.array.first_session_relations,
                android.R.layout.simple_spinner_item
            )
            firstSessionAdapter.setDropDownViewResource(R.layout.drop_down_item_layout)
            relationSpinner.adapter = firstSessionAdapter
        }
        4 -> {
            //Session Count
            keyLinearLayout.visibility = View.GONE
            relationRelativeLayout.visibility = View.VISIBLE
            hoursAgoLinearLayout.visibility = View.GONE
            valueLinearLayout.visibility = View.VISIBLE
            radiusLinearLayout.visibility = View.GONE
            latitudeLinearLayout.visibility = View.GONE
            longitudeLinearLayout.visibility = View.GONE

            val sessionCountAdapter = ArrayAdapter.createFromResource(
                context,
                R.array.session_count_relations,
                android.R.layout.simple_spinner_item
            )
            sessionCountAdapter.setDropDownViewResource(R.layout.drop_down_item_layout)
            relationSpinner.adapter = sessionCountAdapter
        }
        5 -> {
            //Session Time
            keyLinearLayout.visibility = View.GONE
            relationRelativeLayout.visibility = View.VISIBLE
            hoursAgoLinearLayout.visibility = View.GONE
            valueLinearLayout.visibility = View.VISIBLE
            radiusLinearLayout.visibility = View.GONE
            latitudeLinearLayout.visibility = View.GONE
            longitudeLinearLayout.visibility = View.GONE

            val sessionTimeAdapter = ArrayAdapter.createFromResource(
                context,
                R.array.session_time_relations, android.R.layout.simple_spinner_item
            )
            sessionTimeAdapter.setDropDownViewResource(R.layout.drop_down_item_layout)
            relationSpinner.adapter = sessionTimeAdapter
        }
        6 -> {
            //Amount Spent
            keyLinearLayout.visibility = View.GONE
            relationRelativeLayout.visibility = View.VISIBLE
            hoursAgoLinearLayout.visibility = View.GONE
            valueLinearLayout.visibility = View.VISIBLE
            radiusLinearLayout.visibility = View.GONE
            latitudeLinearLayout.visibility = View.GONE
            longitudeLinearLayout.visibility = View.GONE

            val amountSpentAdapter = ArrayAdapter.createFromResource(
                context,
                R.array.amount_spent_relations, android.R.layout.simple_spinner_item
            )
            amountSpentAdapter.setDropDownViewResource(R.layout.drop_down_item_layout)
            relationSpinner.adapter = amountSpentAdapter
        }
        7 -> {
            //Bought SKU
            keyLinearLayout.visibility = View.VISIBLE
            relationRelativeLayout.visibility = View.VISIBLE
            hoursAgoLinearLayout.visibility = View.GONE
            valueLinearLayout.visibility = View.VISIBLE
            radiusLinearLayout.visibility = View.GONE
            latitudeLinearLayout.visibility = View.GONE
            longitudeLinearLayout.visibility = View.GONE

            val boughtSKUAdapter = ArrayAdapter.createFromResource(
                context,
                R.array.bought_sku_relations, android.R.layout.simple_spinner_item
            )
            boughtSKUAdapter.setDropDownViewResource(R.layout.drop_down_item_layout)
            relationSpinner.adapter = boughtSKUAdapter
        }
        8 -> {
            //Language
            keyLinearLayout.visibility = View.GONE
            relationRelativeLayout.visibility = View.VISIBLE
            hoursAgoLinearLayout.visibility = View.GONE
            valueLinearLayout.visibility = View.VISIBLE
            radiusLinearLayout.visibility = View.GONE
            latitudeLinearLayout.visibility = View.GONE
            longitudeLinearLayout.visibility = View.GONE

            val languageAdapter = ArrayAdapter.createFromResource(
                context,
                R.array.language_relations, android.R.layout.simple_spinner_item
            )
            languageAdapter.setDropDownViewResource(R.layout.drop_down_item_layout)
            relationSpinner.adapter = languageAdapter
        }
        9 -> {
            //App Version
            keyLinearLayout.visibility = View.GONE
            relationRelativeLayout.visibility = View.VISIBLE
            hoursAgoLinearLayout.visibility = View.GONE
            valueLinearLayout.visibility = View.VISIBLE
            radiusLinearLayout.visibility = View.GONE
            latitudeLinearLayout.visibility = View.GONE
            longitudeLinearLayout.visibility = View.GONE

            val appVersionAdapter = ArrayAdapter.createFromResource(
                context,
                R.array.app_version_relations, android.R.layout.simple_spinner_item
            )
            appVersionAdapter.setDropDownViewResource(R.layout.drop_down_item_layout)
            relationSpinner.adapter = appVersionAdapter
        }
        10 -> {
            //Location
            keyLinearLayout.visibility = View.GONE
            relationRelativeLayout.visibility = View.GONE
            hoursAgoLinearLayout.visibility = View.GONE
            valueLinearLayout.visibility = View.GONE
            radiusLinearLayout.visibility = View.VISIBLE
            latitudeLinearLayout.visibility = View.VISIBLE
            longitudeLinearLayout.visibility = View.VISIBLE
        }
        11 -> {
            //Email
            keyLinearLayout.visibility = View.GONE
            relationRelativeLayout.visibility = View.GONE
            hoursAgoLinearLayout.visibility = View.GONE
            valueLinearLayout.visibility = View.VISIBLE
            radiusLinearLayout.visibility = View.GONE
            latitudeLinearLayout.visibility = View.GONE
            longitudeLinearLayout.visibility = View.GONE
        }
        12 -> {
            //Country
            keyLinearLayout.visibility = View.GONE
            relationRelativeLayout.visibility = View.VISIBLE
            hoursAgoLinearLayout.visibility = View.GONE
            valueLinearLayout.visibility = View.VISIBLE
            radiusLinearLayout.visibility = View.GONE
            latitudeLinearLayout.visibility = View.GONE
            longitudeLinearLayout.visibility = View.GONE

            val countryAdapter = ArrayAdapter.createFromResource(
                context,
                R.array.country_relations, android.R.layout.simple_spinner_item
            )
            countryAdapter.setDropDownViewResource(R.layout.drop_down_item_layout)
            relationSpinner.adapter = countryAdapter
        }
    }
}
