package com.wenull.homemade.adapter

import android.app.ActionBar
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.wenull.homemade.Pack
import com.wenull.homemade.R
import com.wenull.homemade.databinding.ItemAvailablePacksBinding


class AvailablePacksAdapter : RecyclerView.Adapter<AvailablePacksViewHolder>(){

    private val _packs = ArrayList<Pack>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AvailablePacksViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = DataBindingUtil.inflate<ItemAvailablePacksBinding>(inflater, R.layout.item_available_packs, parent, false)
        return AvailablePacksViewHolder(binding)
    }

    override fun getItemCount(): Int = _packs.size

    override fun onBindViewHolder(holder: AvailablePacksViewHolder, position: Int) {
        holder.bind(_packs[position])
    }

    fun setList(packs: List<Pack>){
        _packs.clear()
        _packs.addAll(packs)
        notifyDataSetChanged()
    }
}

class AvailablePacksViewHolder(private val binding: ItemAvailablePacksBinding): RecyclerView.ViewHolder(binding.root){
    fun bind(pack: Pack){
        Picasso.get().load(pack.imageUrl).into(binding.packThumbnail)
        binding.packName.text = pack.name
        binding.packShortDescription.text = pack.shortDescription
    }
}