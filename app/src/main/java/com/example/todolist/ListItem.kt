package com.example.todolist

import java.util.*

class ListItem {
    var id : Int
    var title : String
    var isSelected : Boolean
    var priority : String? = "low"
    var image : String = "rect1"
    var date : Date = Date(System.currentTimeMillis())

    constructor(id: Int, title: String, isSelected: Boolean = false) {
        this.id = id
        this.title = title
        this.isSelected = isSelected
    }
}