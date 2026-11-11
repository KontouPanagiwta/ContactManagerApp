package com.example.contactmanagerapp

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "contacts")
data class Contact(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val name: String,

    val lastName: String,

    val phone: String,

    val email: String
)