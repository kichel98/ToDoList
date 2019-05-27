package com.example.todolist

import android.content.ContentValues
import android.content.Intent
import android.content.SharedPreferences
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import android.os.Bundle
import android.provider.BaseColumns
import android.util.Log
import android.view.View
import android.widget.CheckBox
import java.util.*
import android.R.id.edit
import android.app.Activity


class MainActivity : AppCompatActivity() {
    val dbHelper = FeedReaderDbHelper(this)
    var sort : String? = null
    lateinit var list: ArrayList<ListItem>
    lateinit var myAdapter: MyArrayAdapter
    lateinit var preferences: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        preferences = getSharedPreferences("myPreferences", Activity.MODE_PRIVATE)

        sort = preferences.getString("sorting", "")

        list = ArrayList<ListItem>()
        fillArrayFromDatabase()
        myAdapter = MyArrayAdapter(this, list)

        todo_list_view.adapter = myAdapter

        todo_list_view.setOnItemClickListener { _, _, index, _ ->
            val intent = Intent(this, ListItemDetails::class.java).apply {
                putExtra("id", list[index].id.toString())

            }
            startActivity(intent)
        }

        todo_list_view.setOnItemLongClickListener { _, _, index, _ ->
            deleteFromDatabase(list[index].id)
            fillArrayFromDatabase()
            myAdapter.notifyDataSetChanged()
            true
        }
    }

    fun addListItem(view: View) {
        // Gets the data repository in write mode
        val db = dbHelper.writableDatabase

        val newText = editText.text.toString()
        if (newText != "") {
            // Create a new map of values, where column names are the keys
            val values = ContentValues().apply {
                put(FeedEntry.COLUMN_NAME_TITLE, newText)
                put(FeedEntry.COLUMN_NAME_DONE, 0)
                put(FeedEntry.COLUMN_NAME_PRIORITY, "")
                put(FeedEntry.COLUMN_NAME_IMAGE, "rect1")
                put(FeedEntry.COLUMN_NAME_TIME, DateToDays(Date(System.currentTimeMillis())))
            }
            // Insert the new row, returning the primary key value of the new row
            val newRowId = db?.insert(FeedEntry.TABLE_NAME, null, values)
        }
        fillArrayFromDatabase()
        // list.add(ListItem(editText.text.toString()))
        myAdapter.notifyDataSetChanged()
        editText.setText("")
    }

    fun fillArrayFromDatabase() {
        val db = dbHelper.readableDatabase

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        val projection = arrayOf(BaseColumns._ID, FeedEntry.COLUMN_NAME_TITLE, FeedEntry.COLUMN_NAME_DONE,
            FeedEntry.COLUMN_NAME_PRIORITY, FeedEntry.COLUMN_NAME_IMAGE, FeedEntry.COLUMN_NAME_TIME)

        // Filter results WHERE "title" = 'My Title'
//        val selection = "${FeedEntry.COLUMN_NAME_TITLE} = ?"
//        val selectionArgs = arrayOf("My Title")

        // How you want the results sorted in the resulting Cursor
        // val sortOrder = "${FeedEntry.COLUMN_NAME_SUBTITLE} DESC"

        val cursor = db.query(
            FeedEntry.TABLE_NAME,   // The table to query
            projection,             // The array of columns to return (pass null to get all)
            null,
            null,
            null,
//            selection,              // The columns for the WHERE clause
//            selectionArgs,          // The values for the WHERE clause
            null,                   // don't group the rows
            sort                   // don't filter by row groups
//            sortOrder               // The sort order
        )
        list.clear()
        with(cursor) {
            while (moveToNext()) {
                val itemId = getInt(getColumnIndexOrThrow(BaseColumns._ID))
                val itemTitle = getString(getColumnIndexOrThrow(FeedEntry.COLUMN_NAME_TITLE))
                val itemDone = getInt(getColumnIndexOrThrow(FeedEntry.COLUMN_NAME_DONE)) != 0
                val itemPriority = getString(getColumnIndexOrThrow(FeedEntry.COLUMN_NAME_PRIORITY))
                val itemImage = getString(getColumnIndexOrThrow(FeedEntry.COLUMN_NAME_IMAGE))
                val itemTime = DaysToDate(getInt(getColumnIndexOrThrow(FeedEntry.COLUMN_NAME_TIME)))
                val listItem = ListItem(itemId, itemTitle, itemDone)
                if(itemPriority != "") {
                    listItem.priority = itemPriority
                }

                listItem.image = itemImage
                listItem.date = itemTime
                list.add(listItem)
            }
        }
        cursor.close()
    }

    fun deleteFromDatabase(id: Int) {
        val db = dbHelper.writableDatabase
        // Define 'where' part of query.
        val selection = "${BaseColumns._ID} LIKE ?"
        // Specify arguments in placeholder order.
        val selectionArgs = arrayOf(id.toString())
        // Issue SQL statement.
        val deletedRows = db.delete(FeedEntry.TABLE_NAME, selection, selectionArgs)
    }


    fun onCheckboxClicked(view: View) {
        if (view is CheckBox) {
            val checked: Int = if (view.isChecked) 1 else 0

            val db = dbHelper.writableDatabase

            // New value for one column
            val values = ContentValues().apply {
                put(FeedEntry.COLUMN_NAME_DONE, checked)
            }
            val id = list[todo_list_view.indexOfChild(view.parent as View)].id

            // Which row to update, based on the title
            val selection = "${BaseColumns._ID} LIKE ?"
            val selectionArgs = arrayOf(id.toString())
            val count = db.update(
                FeedEntry.TABLE_NAME,
                values,
                selection,
                selectionArgs)
        }
        fillArrayFromDatabase()
        myAdapter.notifyDataSetChanged()
    }

    fun sortTimeAsc(view: View) {
        sort = "${FeedEntry.COLUMN_NAME_TIME} ASC"
        val preferencesEditor = preferences.edit()
        preferencesEditor.putString("sorting", "${FeedEntry.COLUMN_NAME_TIME} ASC")
        preferencesEditor.commit()
        fillArrayFromDatabase()
        myAdapter.notifyDataSetChanged()
    }

    fun sortTimeDesc(view: View) {
        sort = "${FeedEntry.COLUMN_NAME_TIME} DESC"
        val preferencesEditor = preferences.edit()
        preferencesEditor.putString("sorting", "${FeedEntry.COLUMN_NAME_TIME} DESC")
        preferencesEditor.commit()
        fillArrayFromDatabase()
        myAdapter.notifyDataSetChanged()
    }

    fun sortImgAsc(view: View) {
        sort = "${FeedEntry.COLUMN_NAME_IMAGE} ASC"
        val preferencesEditor = preferences.edit()
        preferencesEditor.putString("sorting", "${FeedEntry.COLUMN_NAME_IMAGE} ASC")
        preferencesEditor.commit()
        fillArrayFromDatabase()
        myAdapter.notifyDataSetChanged()
    }

    fun sortImgDesc(view: View) {
        sort = "${FeedEntry.COLUMN_NAME_IMAGE} DESC"
        val preferencesEditor = preferences.edit()
        preferencesEditor.putString("sorting", "${FeedEntry.COLUMN_NAME_IMAGE} DESC")
        preferencesEditor.commit()
        fillArrayFromDatabase()
        myAdapter.notifyDataSetChanged()
    }

    fun sortDone(view: View) {
        sort = "${FeedEntry.COLUMN_NAME_DONE} ASC"
        val preferencesEditor = preferences.edit()
        preferencesEditor.putString("sorting", "${FeedEntry.COLUMN_NAME_DONE} ASC")
        preferencesEditor.commit()
        fillArrayFromDatabase()
        myAdapter.notifyDataSetChanged()
    }

    val MAGIC = 86400000L

    fun DateToDays(date: Date): Int {
        return (date.time / MAGIC).toInt()
    }

    fun DaysToDate(days: Int): Date {
        return Date(days.toLong() * MAGIC)
    }
}

