package com.example.contactmanagerapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class ContactViewModel(
    private val repository: ContactRepository
) : ViewModel() {

    // Κείμενο αναζήτησης
    private val _searchQuery = MutableStateFlow("")

    val searchQuery: StateFlow<String> =
        _searchQuery.asStateFlow()

    // Λίστα επαφών
    val contacts = _searchQuery
        .flatMapLatest { query ->
            if (query.isBlank()) {
                repository.getAllContacts()
            } else {
                repository.searchContacts(query)
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Αλλαγή αναζήτησης
    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    // Προσθήκη επαφής
    fun insertContact(contact: Contact) {
        viewModelScope.launch {
            repository.insertContact(contact)
        }
    }

    // Επεξεργασία επαφής
    fun updateContact(contact: Contact) {
        viewModelScope.launch {
            repository.updateContact(contact)
        }
    }

    // Διαγραφή επαφής
    fun deleteContact(contact: Contact) {
        viewModelScope.launch {
            repository.deleteContact(contact)
        }
    }
}