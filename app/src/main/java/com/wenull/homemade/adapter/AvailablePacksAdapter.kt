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
import com.wenull.homemade.databinding.ItemAvailablePacksBinding
import com.wenull.homemade.utils.helper.Constants
import com.wenull.homemade.utils.model.FoodPack

class AvailablePacksAdapter : RecyclerView.Adapter<AvailablePacksViewHolder>() {

    private val _packs = ArrayList<FoodPack>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AvailablePacksViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = DataBindingUtil.inflate<ItemAvailablePacksBinding>(inflater, R.layout.item_available_packs, parent, false)
        return AvailablePacksViewHolder(binding)
    }

    override fun getItemCount(): Int = _packs.size

    override fun onBindViewHolder(holder: AvailablePacksViewHolder, position: Int) {
        holder.bind(_packs[position])
    }

    fun setList(packs: ArrayList<FoodPack>) {
        _packs.clear()
        _packs.addAll(packs)
        notifyDataSetChanged()
    }
}

class AvailablePacksViewHolder(private val binding: ItemAvailablePacksBinding) : RecyclerView.ViewHolder(binding.root) {

    fun bind(pack: FoodPack) {

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

    }

}
