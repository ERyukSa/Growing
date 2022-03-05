package com.eryuksa.growing.motivation

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.eryuksa.growing.databinding.ItemYoutubeBinding
import com.eryuksa.growing.motivation.data.YoutubeItem

class YoutubeListAdapter(
    private val viewModel: YoutubeListViewModel,
    private val onClick: (YoutubeItem) -> Unit
) :
    RecyclerView.Adapter<YoutubeListAdapter.YoutubeHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): YoutubeHolder {
        val binding = ItemYoutubeBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return YoutubeHolder(binding)
    }

    override fun onBindViewHolder(holder: YoutubeHolder, position: Int) {
        holder.bind(viewModel.youtubeList.value!![position])
    }

    override fun getItemCount(): Int {
        return viewModel.youtubeList.value?.size ?: 0
    }

    inner class YoutubeHolder(private val binding: ItemYoutubeBinding) :
        RecyclerView.ViewHolder(binding.root) {

        lateinit var mYoutube: YoutubeItem

        init {
            itemView.setOnClickListener{
                onClick(mYoutube)
            }
        }

        fun bind(youtube: YoutubeItem) {
            mYoutube = youtube
            binding.youtube = youtube
        }
    }
}