package com.example.todolist

import android.app.DatePickerDialog
import android.content.ContentValues
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.BaseColumns
import android.util.Log
import android.view.View
import android.widget.*
import kotlinx.android.synthetic.main.activity_list_item_details.*
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import java.time.LocalDate


class ListItemDetails : AppCompatActivity() {
    val dbHelper = FeedReaderDbHelper(this)
    lateinit var itemId : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_item_details)

        prepareSpinner(spinner, R.array.priorities)

        itemId = intent.getStringExtra("id")


        val db = dbHelper.readableDatabase
        val projection = arrayOf(FeedEntry.COLUMN_NAME_TITLE, FeedEntry.COLUMN_NAME_PRIORITY,
            FeedEntry.COLUMN_NAME_IMAGE, FeedEntry.COLUMN_NAME_TIME)
        val selection = "${BaseColumns._ID} = ?"
        val selectionArgs = arrayOf(itemId)
        val cursor = db.query(
            FeedEntry.TABLE_NAME,   // The table to query
            projection,             // The array of columns to return (pass null to get all)
            selection,              // The columns for the WHERE clause
            selectionArgs,          // The values for the WHERE clause
            null,           // don't group the rows
            null,            // don't filter by row groups
            null            // The sort order
        )

        with(cursor) {
            while(moveToNext()) {
                val itemTitle = getString(getColumnIndexOrThrow(FeedEntry.COLUMN_NAME_TITLE))
                val itemPriority = getString(getColumnIndexOrThrow(FeedEntry.COLUMN_NAME_PRIORITY))
                val itemImage = getString(getColumnIndexOrThrow(FeedEntry.COLUMN_NAME_IMAGE))
                val itemTime = getInt(getColumnIndexOrThrow(FeedEntry.COLUMN_NAME_TIME))
                val gregCal = GregorianCalendar()
                gregCal.setTime(DaysToDate(itemTime))
                val dateStr = "${gregCal.get(Calendar.DAY_OF_MONTH )+1}/${gregCal.get(Calendar.MONTH) + 1}/${gregCal.get(Calendar.YEAR)}"
                textView2.text = itemTitle
                picker.text = dateStr
                spinner.setSelection((spinner.adapter as ArrayAdapter<String>).getPosition(itemPriority))
                (radioGroup1.getChildAt(itemImage.takeLast(1).toInt() - 1) as RadioButton).isChecked = true
            }
        }
        cursor.close()
    }

    fun prepareSpinner(spinner: Spinner, stringArray: Int) {
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter.createFromResource(
            this,
            stringArray,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            spinner.adapter = adapter
        }
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parentView: AdapterView<*>, selectedItemView: View?, position: Int, id: Long) {
                val priority = parentView.getItemAtPosition(position).toString()
                val db = dbHelper.writableDatabase

                // New value for one column
                val values = ContentValues().apply {
                    put(FeedEntry.COLUMN_NAME_PRIORITY, priority)
                }
                // Which row to update, based on the title
                val selection = "${BaseColumns._ID} LIKE ?"
                val selectionArgs = arrayOf(itemId)
                val count = db.update(
                    FeedEntry.TABLE_NAME,
                    values,
                    selection,
                    selectionArgs)
            }

            override fun onNothingSelected(parentView: AdapterView<*>) {
                // your code here
            }

        }
    }

    fun changeImage(view: View) {
        val image = "rect${resources.getResourceEntryName(view.id).toString().takeLast(1)}"
        val db = dbHelper.writableDatabase

        // New value for one column
        val values = ContentValues().apply {
            put(FeedEntry.COLUMN_NAME_IMAGE, image)
        }
        // Which row to update, based on the title
        val selection = "${BaseColumns._ID} LIKE ?"
        val selectionArgs = arrayOf(itemId)
        val count = db.update(
            FeedEntry.TABLE_NAME,
            values,
            selection,
            selectionArgs)
    }

    fun changeDate(view: View) {
        val cal = Calendar.getInstance()
        val day = cal.get(Calendar.DAY_OF_MONTH)
        val month = cal.get(Calendar.MONTH)
        val year = cal.get(Calendar.YEAR)


        var time : Int
        val datePicker = DatePickerDialog(this, DatePickerDialog.OnDateSetListener {
                view: DatePicker?, pickerYear: Int, pickerMonth: Int, dayOfMonth: Int ->
                    picker.text = "${dayOfMonth}/${pickerMonth+1}/${pickerYear}"
                    time = DateToDays((GregorianCalendar(pickerYear, pickerMonth, dayOfMonth) as Calendar).time)


                    val db = dbHelper.writableDatabase

                    // New value for one column
                    val values = ContentValues().apply {
                        put(FeedEntry.COLUMN_NAME_TIME, time)
                    }
                    // Which row to update, based on the title
                    val selection = "${BaseColumns._ID} LIKE ?"
                    val selectionArgs = arrayOf(itemId)
                    val count = db.update(
                        FeedEntry.TABLE_NAME,
                        values,
                        selection,
                        selectionArgs)


        }, year, month, day)
        datePicker.show()
    }

    val MAGIC = 86400000L

    fun DateToDays(date: Date): Int {
        return (date.time / MAGIC).toInt()
    }

    fun DaysToDate(days: Int): Date {
        return Date(days.toLong() * MAGIC)
    }

}
