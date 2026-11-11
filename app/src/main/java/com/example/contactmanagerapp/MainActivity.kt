package com.example.contactmanagerapp

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var adapter: ContactAdapter
    private lateinit var viewModel: ContactViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        Toast.makeText(
            this,
            "Η MainActivity ξεκίνησε!",
            Toast.LENGTH_LONG
        ).show()

        // Database
        val database = ContactDatabase.getDatabase(applicationContext)

        // Repository
        val repository = ContactRepository(database.contactDao())

        // ViewModel
        val factory = ContactViewModelFactory(repository)

        viewModel = ViewModelProvider(
            this,
            factory
        )[ContactViewModel::class.java]

        // RecyclerView
        val recyclerView = findViewById<RecyclerView>(
            R.id.recyclerViewContacts
        )

        adapter = ContactAdapter(
            onEditClick = { contact ->
                showContactDialog(contact)
            },
            onDeleteClick = { contact ->
                deleteContact(contact)
            }
        )

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        // Search
        val editTextSearch = findViewById<EditText>(
            R.id.editTextSearch
        )

        editTextSearch.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {
                viewModel.updateSearchQuery(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })

        // Add Contact Button
        val buttonAddContact = findViewById<Button>(
            R.id.buttonAddContact
        )

        buttonAddContact.setOnClickListener {
            showContactDialog()
        }

        // Observe Contacts
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.contacts.collect { contacts ->
                    adapter.submitList(contacts)
                }
            }
        }
    }

    // Add / Edit Contact
    private fun showContactDialog(contact: Contact? = null) {

        val dialogView = layoutInflater.inflate(
            R.layout.dialog_contact,
            null
        )

        val editTextName = dialogView.findViewById<EditText>(
            R.id.editTextName
        )

        val editTextLastName = dialogView.findViewById<EditText>(
            R.id.editTextLastName
        )

        val editTextPhone = dialogView.findViewById<EditText>(
            R.id.editTextPhone
        )

        val editTextEmail = dialogView.findViewById<EditText>(
            R.id.editTextEmail
        )

        // Fill fields when editing
        if (contact != null) {
            editTextName.setText(contact.name)
            editTextLastName.setText(contact.lastName)
            editTextPhone.setText(contact.phone)
            editTextEmail.setText(contact.email)
        }

        val dialog = AlertDialog.Builder(this)
            .setTitle(
                if (contact == null) {
                    "Νέα επαφή"
                } else {
                    "Επεξεργασία επαφής"
                }
            )
            .setView(dialogView)
            .setPositiveButton("Αποθήκευση", null)
            .setNegativeButton("Ακύρωση", null)
            .create()

        dialog.setOnShowListener {

            dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                .setOnClickListener {

                    val name = editTextName.text.toString().trim()
                    val lastName = editTextLastName.text.toString().trim()
                    val phone = editTextPhone.text.toString().trim()
                    val email = editTextEmail.text.toString().trim()

                    // Validation
                    if (
                        name.isEmpty() ||
                        lastName.isEmpty() ||
                        phone.isEmpty()
                    ) {

                        Toast.makeText(
                            this,
                            "Συμπλήρωσε όνομα, επώνυμο και τηλέφωνο",
                            Toast.LENGTH_SHORT
                        ).show()

                    } else if (
                        email.isNotEmpty() &&
                        !Patterns.EMAIL_ADDRESS.matcher(email).matches()
                    ) {

                        Toast.makeText(
                            this,
                            "Συμπλήρωσε έγκυρο email",
                            Toast.LENGTH_SHORT
                        ).show()

                    } else {

                        if (contact == null) {

                            // Insert Contact
                            viewModel.insertContact(
                                Contact(
                                    name = name,
                                    lastName = lastName,
                                    phone = phone,
                                    email = email
                                )
                            )

                        } else {

                            // Update Contact
                            viewModel.updateContact(
                                contact.copy(
                                    name = name,
                                    lastName = lastName,
                                    phone = phone,
                                    email = email
                                )
                            )
                        }

                        dialog.dismiss()
                    }
                }
        }

        dialog.show()
    }

    // Delete Contact
    private fun deleteContact(contact: Contact) {

        AlertDialog.Builder(this)
            .setTitle("Διαγραφή επαφής")
            .setMessage(
                "Θέλεις σίγουρα να διαγράψεις την επαφή " +
                        "${contact.name} ${contact.lastName};"
            )
            .setPositiveButton("Ναι") { _, _ ->
                viewModel.deleteContact(contact)
            }
            .setNegativeButton("Όχι", null)
            .show()
    }
}