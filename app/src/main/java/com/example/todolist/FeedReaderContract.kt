package com.example.todolist

import android.provider.BaseColumns

object FeedEntry : BaseColumns {
    const val TABLE_NAME = "list_items"
    const val COLUMN_NAME_TITLE = "title"
    const val COLUMN_NAME_TIME = "time"
    const val COLUMN_NAME_IMAGE = "img"
    const val COLUMN_NAME_PRIORITY = "priority"
    const val COLUMN_NAME_DONE = "done"
}
