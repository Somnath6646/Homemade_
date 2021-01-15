package com.wenull.homemade.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.squareup.picasso.Picasso
import com.wenull.homemade.R
import com.wenull.homemade.databinding.ItemAvailablePacksBinding
import com.wenull.homemade.utils.helper.Constants
import com.wenull.homemade.utils.model.FoodPack

class AvailablePacksAdapter(private val onClick: (FoodPack) -> Unit, private val enrollOnClick: (ArrayList<Long>, String) -> Unit) : RecyclerView.Adapter<AvailablePacksViewHolder>() {

    private val _packs = ArrayList<FoodPack>()
    private val _userPacks = ArrayList<Long>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AvailablePacksViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = DataBindingUtil.inflate<ItemAvailablePacksBinding>(inflater, R.layout.item_available_packs, parent, false)
        return AvailablePacksViewHolder(binding, onClick, enrollOnClick)
    }

    override fun getItemCount(): Int = _packs.size

    override fun onBindViewHolder(holder: AvailablePacksViewHolder, position: Int) {
        holder.bind(_packs[position], _userPacks)
    }

    fun setList(packs: ArrayList<FoodPack>, packIds: ArrayList<Long>) {
        _packs.clear()
        _packs.addAll(packs)
        _userPacks.clear()
        _userPacks.addAll(packIds)
        notifyDataSetChanged()
    }
}

class AvailablePacksViewHolder(private val binding: ItemAvailablePacksBinding, private val onClick: (FoodPack) -> Unit, private val enrollOnClick: (ArrayList<Long>, String) -> Unit) : RecyclerView.ViewHolder(binding.root) {

    fun bind(pack: FoodPack, packIds: ArrayList<Long>) {

        val imageReference =
            Firebase.storage.reference.child("${Constants.COLLECTION_PACKS}/${pack.imageName}")

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

        if(!packIds.contains(pack.id)) {
            binding.enrollButton.text = Constants.ENROLL_NOW
            binding.enrollButton.setOnClickListener {
                val newPackIds = packIds
                newPackIds.add(pack.id)
                enrollOnClick(newPackIds, binding.enrollButton.text.toString())
            }
        } else {
            binding.enrollButton.text = Constants.ENROLLED
            binding.enrollButton.setCompoundDrawables(null, null, null, null)
            binding.enrollButton.setOnClickListener {
                val newPackIds = packIds
                newPackIds.remove(pack.id)
                enrollOnClick(newPackIds, binding.enrollButton.text.toString())
            }
        }

        binding.packName.text = pack.name
        binding.packShortDescription.text = pack.description

        binding.cardView.setOnClickListener { onClick(pack) }

    }

}
