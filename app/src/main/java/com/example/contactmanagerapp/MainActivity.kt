package com.example.contactmanagerapp

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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

        // Δημιουργία βάσης δεδομένων
        val database = ContactDatabase.getDatabase(applicationContext)

        // Δημιουργία Repository
        val repository = ContactRepository(database.contactDao())

        // Δημιουργία ViewModel
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

        // Αναζήτηση
        val editTextSearch = findViewById<EditText>(
            R.id.editTextSearch
        )

        editTextSearch.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {}

            override fun onTextChanged(
                s: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {
                viewModel.updateSearchQuery(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        // Κουμπί προσθήκης
        val buttonAddContact = findViewById<Button>(
            R.id.buttonAddContact
        )

        buttonAddContact.setOnClickListener {
            showContactDialog()
        }

        // Παρακολούθηση λίστας επαφών
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.contacts.collect { contacts ->
                    adapter.submitList(contacts)
                }
            }
        }
    }

    // Προσθήκη ή επεξεργασία επαφής
    private fun showContactDialog(contact: Contact? = null) {

        val dialogView = layoutInflater.inflate(
            R.layout.dialog_contact,
            null
        )

        val editTextName = dialogView.findViewById<EditText>(
            R.id.editTextName
        )

        val editTextPhone = dialogView.findViewById<EditText>(
            R.id.editTextPhone
        )

        if (contact != null) {
            editTextName.setText(contact.name)
            editTextPhone.setText(contact.phone)
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
                    val phone = editTextPhone.text.toString().trim()

                    if (name.isEmpty() || phone.isEmpty()) {

                        Toast.makeText(
                            this,
                            "Συμπλήρωσε όνομα και τηλέφωνο",
                            Toast.LENGTH_SHORT
                        ).show()

                    } else {

                        if (contact == null) {

                            viewModel.insertContact(
                                Contact(
                                    name = name,
                                    phone = phone
                                )
                            )

                        } else {

                            viewModel.updateContact(
                                contact.copy(
                                    name = name,
                                    phone = phone
                                )
                            )
                        }

                        dialog.dismiss()
                    }
                }
        }

        dialog.show()
    }

    // Διαγραφή επαφής
    private fun deleteContact(contact: Contact) {

        AlertDialog.Builder(this)
            .setTitle("Διαγραφή επαφής")
            .setMessage(
                "Θέλεις σίγουρα να διαγράψεις την επαφή ${contact.name};"
            )
            .setPositiveButton("Ναι") { _, _ ->
                viewModel.deleteContact(contact)
            }
            .setNegativeButton("Όχι", null)
            .show()
    }
}