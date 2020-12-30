package com.wenull.homemade.adapter

import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.squareup.picasso.Picasso
import com.wenull.homemade.R
import com.wenull.homemade.databinding.ItemUserOwnedPacksBinding
import com.wenull.homemade.utils.helper.Constants
import com.wenull.homemade.utils.model.FoodPack


class UserPacksAdapter(private val optOutMenuClickListener: (FoodPack) -> Unit) : RecyclerView.Adapter<UserPacksViewHolder>(){

    private val _packs = ArrayList<FoodPack>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserPacksViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = DataBindingUtil.inflate<ItemUserOwnedPacksBinding>(inflater, R.layout.item_user_owned_packs, parent, false)
        return UserPacksViewHolder(binding, optOutMenuClickListener)
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

class UserPacksViewHolder(private val binding: ItemUserOwnedPacksBinding, private val optOutMenuClickListener: (FoodPack) -> Unit): RecyclerView.ViewHolder(binding.root){
    fun bind(pack: FoodPack){

        val imageReference =
            Firebase.storage.reference.child("${Constants.FOOD_PACK}/${pack.imageName}")

        var imageUrl: Uri? = null

        imageReference.downloadUrl
            .addOnSuccessListener { uri ->
                Log.i("DownloadURL", uri.toString())
                Picasso.get()
                    .load(uri)
                    .centerCrop()
                    .resize(binding.packThumbnailContainer.width, binding.packThumbnailContainer.height)
                    .into(binding.packThumbnail)
            }
            .addOnFailureListener { exception ->
                Log.i("Exception", "DownloadURL")
                exception.printStackTrace()
            }

        binding.packName.text = pack.name
        binding.packShortDescription.text = pack.description

        binding.optoutMenuBtn.setOnClickListener {
            optOutMenuClickListener(pack)
        }
    }
}