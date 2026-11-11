package com.example.contactmanagerapp

import kotlinx.coroutines.flow.Flow

class ContactRepository(
    private val contactDao: ContactDao
) {

    // Εμφάνιση όλων των επαφών
    fun getAllContacts(): Flow<List<Contact>> {
        return contactDao.getAllContacts()
    }

    // Αναζήτηση επαφών
    fun searchContacts(query: String): Flow<List<Contact>> {
        return contactDao.searchContacts(query)
    }

    // Προσθήκη επαφής
    suspend fun insertContact(contact: Contact) {
        contactDao.insertContact(contact)
    }

    // Επεξεργασία επαφής
    suspend fun updateContact(contact: Contact) {
        contactDao.updateContact(contact)
    }

    // Διαγραφή επαφής
    suspend fun deleteContact(contact: Contact) {
        contactDao.deleteContact(contact)
    }
}