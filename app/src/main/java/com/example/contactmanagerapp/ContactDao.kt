package com.example.contactmanagerapp

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ContactDao {

    // Εμφάνιση όλων των επαφών με αλφαβητική σειρά
    @Query("SELECT * FROM contacts ORDER BY name COLLATE NOCASE ASC")
    fun getAllContacts(): Flow<List<Contact>>

    // Αναζήτηση επαφών με βάση το όνομα ή το τηλέφωνο
    @Query("""
        SELECT * FROM contacts
        WHERE name LIKE '%' || :searchQuery || '%'
        OR phone LIKE '%' || :searchQuery || '%'
        ORDER BY name COLLATE NOCASE ASC
    """)
    fun searchContacts(searchQuery: String): Flow<List<Contact>>

    // Προσθήκη νέας επαφής
    @Insert
    suspend fun insertContact(contact: Contact)

    // Ενημέρωση υπάρχουσας επαφής
    @Update
    suspend fun updateContact(contact: Contact)

    // Διαγραφή επαφής
    @Delete
    suspend fun deleteContact(contact: Contact)
}