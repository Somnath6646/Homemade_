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
import com.wenull.homemade.databinding.ItemUserOwnedPacksBinding


class UserPacksAdapter : RecyclerView.Adapter<UserPacksViewHolder>(){

    private val _packs = ArrayList<Pack>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserPacksViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = DataBindingUtil.inflate<ItemUserOwnedPacksBinding>(inflater, R.layout.item_user_owned_packs, parent, false)
        return UserPacksViewHolder(binding)
    }

    override fun getItemCount(): Int = _packs.size

    override fun onBindViewHolder(holder: UserPacksViewHolder, position: Int) {
        holder.bind(_packs[position])
    }

    fun setList(packs: List<Pack>){
        _packs.clear()
        _packs.addAll(packs)
        notifyDataSetChanged()
    }
}

class UserPacksViewHolder(private val binding: ItemUserOwnedPacksBinding): RecyclerView.ViewHolder(binding.root){
    fun bind(pack: Pack){
        Picasso.get().load(pack.imageUrl).into(binding.packThumbnail)
        binding.packName.text = pack.name
        binding.packShortDescription.text = pack.shortDescription
    }
}