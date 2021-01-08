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
import com.wenull.homemade.databinding.ItemSkippedFoodsBinding
import com.wenull.homemade.utils.helper.Constants
import com.wenull.homemade.utils.model.FoodPack
import com.wenull.homemade.utils.model.OrderServer

class SkippedFoodsAdapter : RecyclerView.Adapter<SkippedViewHolder>() {

    private val foods = ArrayList<OrderServer>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SkippedViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = DataBindingUtil.inflate<ItemSkippedFoodsBinding>(inflater, R.layout.item_skipped_foods, parent, false)
        return SkippedViewHolder(binding)
    }

    override fun getItemCount(): Int = foods.size

    override fun onBindViewHolder(holder: SkippedViewHolder, position: Int) {
        holder.bind(foods[position])
    }

    fun setList(foodList: List<OrderServer>){
        foods.clear()
        foods.addAll(foodList)
        notifyDataSetChanged()
    }

}

class SkippedViewHolder(private val binding: ItemSkippedFoodsBinding) : RecyclerView.ViewHolder(binding.root) {

    fun bind(food: OrderServer) {

        val downloadReference = "${Constants.COLLECTION_FOODS}/${Constants.PACK_}${food.packId}/${food.imageName}"

        val imageReference =
            Firebase.storage.reference.child(downloadReference)
        Log.i("DownloadReference", downloadReference)

        imageReference.downloadUrl
            .addOnSuccessListener { uri ->
                Log.i("DownloadURL", uri.toString())
                Picasso.get()
                    .load(uri)
                    .centerCrop()
                    .resize(binding.imageThumbnailContainer.width, binding.imageThumbnailContainer.height)
                    .into(binding.foodItemThumbnail)
            }
            .addOnFailureListener { exception ->
                Log.i("Exception", "DownloadURL")
                exception.printStackTrace()
            }

        binding.foodName.text = food.name
        binding.foodDescription.text = food.description
        binding.foodPrice.text = food.price

    }

}
