package com.example.contactmanagerapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

class ContactAdapter(
    private val onEditClick: (Contact) -> Unit,
    private val onDeleteClick: (Contact) -> Unit
) : ListAdapter<Contact, ContactAdapter.ContactViewHolder>(ContactDiffCallback()) {

    class ContactViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        val nameTextView: TextView =
            itemView.findViewById(R.id.textViewName)

        val phoneTextView: TextView =
            itemView.findViewById(R.id.textViewPhone)

        val emailTextView: TextView =
            itemView.findViewById(R.id.textViewEmail)

        val editButton: Button =
            itemView.findViewById(R.id.buttonEdit)

        val deleteButton: Button =
            itemView.findViewById(R.id.buttonDelete)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ContactViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_contact, parent, false)

        return ContactViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: ContactViewHolder,
        position: Int
    ) {

        val contact = getItem(position)

        holder.nameTextView.text = holder.itemView.context.getString(
            R.string.contact_full_name,
            contact.name,
            contact.lastName
        )

        holder.phoneTextView.text = contact.phone
        holder.emailTextView.text = contact.email

        holder.editButton.setOnClickListener {
            onEditClick(contact)
        }

        holder.deleteButton.setOnClickListener {
            onDeleteClick(contact)
        }
    }

    class ContactDiffCallback : DiffUtil.ItemCallback<Contact>() {

        override fun areItemsTheSame(
            oldItem: Contact,
            newItem: Contact
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: Contact,
            newItem: Contact
        ): Boolean {
            return oldItem == newItem
        }
    }
}