package com.netology.mymapapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.netology.mymapapp.dto.UserMap
import com.netology.mymaps.databinding.ItemUserMapBinding

interface OnInteractionListener {
    fun onItemClick(position: Int)
    fun onEdit(location: UserMap) {}
    fun onRemove(location: UserMap) {}
}

class LocationAdapter(
    private val onInteractionListener: OnInteractionListener
) : ListAdapter<UserMap, PostViewHolder>(PostDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding = ItemUserMapBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostViewHolder(binding, onInteractionListener)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val location = getItem(position)
        holder.itemView.setOnClickListener{
            onInteractionListener.onItemClick(position)
        }
        holder.bind(location)
    }
}

class PostViewHolder(
    private val binding: ItemUserMapBinding,
    private val onInteractionListener: OnInteractionListener,
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(location: UserMap) {
        binding.apply {
            tvMapTitle.text = location.title

            ivEdit.setOnClickListener{
                onInteractionListener.onEdit(location)
            }

            ivRemove.setOnClickListener{
                onInteractionListener.onRemove(location)
            }

        }
    }
}

class PostDiffCallback : DiffUtil.ItemCallback<UserMap>() {
    override fun areItemsTheSame(oldItem: UserMap, newItem: UserMap): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: UserMap, newItem: UserMap): Boolean {
        return oldItem == newItem
    }
}