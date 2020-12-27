package com.wenull.homemade.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.wenull.homemade.R
import com.wenull.homemade.databinding.ItemUserOwnedPacksBinding
import com.wenull.homemade.utils.helper.Constants
import com.wenull.homemade.utils.model.FoodPack


class UserPacksAdapter : RecyclerView.Adapter<UserPacksViewHolder>(){

    private val _packs = ArrayList<FoodPack>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserPacksViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = DataBindingUtil.inflate<ItemUserOwnedPacksBinding>(inflater, R.layout.item_user_owned_packs, parent, false)
        return UserPacksViewHolder(binding)
    }

    override fun getItemCount(): Int = _packs.size

    override fun onBindViewHolder(holder: UserPacksViewHolder, position: Int) {
        holder.bind(_packs[position])
    }

    fun setList(packs: List<FoodPack>){
        _packs.clear()
        _packs.addAll(packs)
        notifyDataSetChanged()
    }
}

class UserPacksViewHolder(private val binding: ItemUserOwnedPacksBinding): RecyclerView.ViewHolder(binding.root){
    fun bind(pack: FoodPack){

        val storageReference =
            Firebase.storage.reference.child("${Constants.FOOD_PACK}/${pack.imageName}")

        Glide.with(binding.root)
            .load(storageReference)
            .into(binding.packThumbnail)

        binding.packName.text = pack.name
        binding.packShortDescription.text = pack.description
    }
}