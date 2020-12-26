package com.wenull.homemade.ui.fragments

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.FileProvider
import androidx.navigation.fragment.findNavController
import com.squareup.picasso.Picasso
import com.wenull.homemade.R
import com.wenull.homemade.databinding.FragmentCredentialsBinding
import com.wenull.homemade.ui.activities.HomemadeActivityViewModel
import com.wenull.homemade.ui.fragments.base.BaseFragment
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class CredentialsFragment : BaseFragment<FragmentCredentialsBinding, HomemadeActivityViewModel>(){

    private val REQUEST_TAKE_PHOTO = 1
    private val PICK_IMAGE = 100
    private var currentPhotoPath: String? = null

    override fun getLayout(): Int  = R.layout.fragment_credentials

    override fun getViewModelClass(): Class<HomemadeActivityViewModel>  = HomemadeActivityViewModel::class.java

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.proceedBtnCredfrag.setOnClickListener {
            val action = CredentialsFragmentDirections.actionCredentialsFragmentToHomeFragment()
            findNavController().navigate(action)
        }

        binding.profilePicCardView.setOnClickListener {
            openGallery()
        }
    }

    fun openGallery() {
        val options = arrayOf<CharSequence>("Take Photo", "Select from gallery", "Cancel")
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Add Photo")
        builder.setItems(options) { dialog, which ->
            when (which) {
                0 -> {
                    val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    if (takePictureIntent.resolveActivity(requireActivity().packageManager) != null) {
                        // Create the File where the photo should go
                        var photoFile: File? = null
                        try {
                            photoFile = createImageFile()
                        } catch (ex: IOException) {
                            // Error occurred while creating the File
                            //
                        }
                        // Continue only if the File was successfully created
                        if (photoFile != null) {
                            val photoURI = FileProvider.getUriForFile(requireContext(),
                                    "com.wenull.homemade.fileprovider",
                                    photoFile)
                            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                            startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO)
                        }
                    }
                }
                1 -> {
                    val intent2 = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
                    startActivityForResult(intent2,PICK_IMAGE)
                }
                else -> dialog.dismiss()
            }
        }
        builder.show()
    }

    var i = 0

    private fun setPic() {
        Picasso.get().load(File(currentPhotoPath)).resize(75,75).centerCrop().into(binding.profilePicImageView)
    }

    @Throws(IOException::class)
    private fun createImageFile(): File? {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageName = "JPEG_" + timeStamp + "_"
        val storageDir: File? = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val image = File.createTempFile(
                imageName,
                ".jpg",
                storageDir
        )
        currentPhotoPath = image.absolutePath
        return image
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_TAKE_PHOTO -> if (resultCode == Activity.RESULT_OK) {
                setPic()
            }
            PICK_IMAGE -> if (resultCode == Activity.RESULT_OK) {
                val imageUri = data!!.data

                Picasso.get().load(imageUri).resize(75,75).centerCrop().into(binding.profilePicImageView)
            }
        }
    }
}