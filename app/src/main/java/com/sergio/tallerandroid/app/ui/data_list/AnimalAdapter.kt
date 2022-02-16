package com.sergio.tallerandroid.app.ui.data_list

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.sergio.tallerandroid.databinding.ViewAnimalItemBinding
import com.sergio.tallerandroid.model.AnimalData

class AnimalAdapter(private val addClickListener: (animal: AnimalData) -> Unit, private val subtractClickListener: (animal: AnimalData) -> Unit) : ListAdapter<AnimalData, AnimalAdapter.ViewHolder>(AnimalDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item, addClickListener, subtractClickListener)
    }

    class ViewHolder private constructor(private val binding: ViewAnimalItemBinding) : RecyclerView.ViewHolder(binding.root) {

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ViewAnimalItemBinding.inflate(layoutInflater, parent, false)

                return ViewHolder(binding)
            }
        }

        fun bind(item: AnimalData, addClickListener: (animal: AnimalData) -> Unit, subtractClickListener: (animal: AnimalData) -> Unit) {
            binding.animalData = item
            binding.specimensText.text = item.specimens.toString()
            binding.addButton.setOnClickListener { addClickListener(item) }
            binding.subtractButton.setOnClickListener { subtractClickListener(item) }
            binding.executePendingBindings()
        }
    }
}

class AnimalDiffCallback : DiffUtil.ItemCallback<AnimalData>() {
    override fun areItemsTheSame(oldItem: AnimalData, newItem: AnimalData): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: AnimalData, newItem: AnimalData): Boolean {
        return oldItem == newItem
    }
}


