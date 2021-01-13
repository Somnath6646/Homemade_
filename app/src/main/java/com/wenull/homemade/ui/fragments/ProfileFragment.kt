package com.wenull.homemade.ui.fragments

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.MenuInflater
import android.view.View
import android.widget.PopupMenu
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.squareup.picasso.Picasso
import com.wenull.homemade.R
import com.wenull.homemade.adapter.UserPacksAdapter
import com.wenull.homemade.databinding.FragmentProfileBinding
import com.wenull.homemade.ui.viewmodel.HomemadeViewModel
import com.wenull.homemade.ui.fragments.base.BaseFragment
import com.wenull.homemade.utils.helper.Constants
import com.wenull.homemade.utils.model.FoodPack
import com.wenull.homemade.utils.model.User
import com.wenull.homemade.utils.model.UserAddress
import com.wenull.homemade.utils.model.UserSkippedData
import kotlinx.android.synthetic.main.alert_dialog.*


class ProfileFragment : BaseFragment<FragmentProfileBinding, HomemadeViewModel>() {

    private lateinit var adapter: UserPacksAdapter

    private val auth = FirebaseAuth.getInstance()

    var userSkippedData: UserSkippedData? = null

    override fun getLayout(): Int = R.layout.fragment_profile

    override fun getViewModelClass(): Class<HomemadeViewModel> = HomemadeViewModel::class.java

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        binding.lifecycleOwner = viewLifecycleOwner
        viewModel.setFirebaseSourceCallback()

        setUpRecyclerView()
        getUserSkippedData()
        getUserData()

        binding.profileBackBtn.setOnClickListener {
            findNavController().popBackStack()
        }

        viewModel.userCredentialsUpdateSuccessful.observe(viewLifecycleOwner, Observer { isSuccessful ->
            getUserData()
        })

    }

    private fun getUserSkippedData() {
        viewModel.getUserSkippedData(auth.currentUser!!.uid)
        viewModel.userSkippedData.observe(viewLifecycleOwner, Observer { data ->
            userSkippedData = data
        })
    }

    private fun getUserData() {

        viewModel.fetchUserData(auth.currentUser!!.uid)
        viewModel.userData.observe(viewLifecycleOwner, Observer { user ->
            setAddressAndName(user.address, user.firstName, user.lastName)
            setUserImage(user.imageName)
            goToEditProfileFragment(user)
        })

    }

    private fun setAddressAndName(address: UserAddress, firstName: String, lastName: String) {

        binding.usernameProfilefrag.text = "$firstName $lastName"

        val addressText =
            "${address.buildingNameOrNumber},\n${address.streetName},\n${address.locality},\n${address.city} - ${address.pincode}"

        binding.addressProfilefrag.text = addressText

    }

    private fun setUserImage(imageName: String) {

        val downloadReference = "${Constants.COLLECTION_USERS}/$imageName"

        val imageReference =
            Firebase.storage.reference.child(downloadReference)
        Log.i("DownloadReferenceProf", downloadReference)

        imageReference.downloadUrl
            .addOnSuccessListener { uri ->
                Log.i("DownloadURL", uri.toString())
                Picasso.get()
                    .load(uri)
                    .centerCrop()
                    .resize(binding.profilePicImageView.width, binding.profilePicImageView.height)
                    .into(binding.profilePicImageView)
            }
            .addOnFailureListener { exception ->
                Log.i("Exception", "DownloadURLProfile")
                exception.printStackTrace()
            }

    }

    private fun setUpRecyclerView() {

        adapter = UserPacksAdapter {pack, view -> onOptOutMenuClick(pack, view)}
        binding.recylerViewYourPacks.adapter = adapter
        binding.recylerViewYourPacks.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        displayPacks()

    }

    private fun displayPacks() {
        viewModel.fetchPackDetails()
        viewModel.packs.observe(viewLifecycleOwner, Observer { packs ->
            adapter.setList(packs)
        })
    }

    private fun onOptOutMenuClick(pack: FoodPack, view: View) {

        val popup = PopupMenu(requireContext(), view)
        val menuInflater = MenuInflater(requireContext())

        menuInflater.inflate(R.menu.optout_menu, popup.menu)
        popup.show()

        popup.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.forever_optout -> {
                    //foreveroptout
                    foreverOptout(pack)
                    return@setOnMenuItemClickListener true
                }

                R.id.selective_optout -> {
                    if(userSkippedData == null) {
                        userSkippedData = UserSkippedData(
                            uid = auth.currentUser!!.uid,
                            skippedMeals = ArrayList()
                        )
                    }
                    val action = ProfileFragmentDirections.actionProfileFragmentToOptoutBottomsheetFragment(pack, userSkippedData!!)
                    findNavController().navigate(action)
                    return@setOnMenuItemClickListener true
                }

                else -> false //nothing
            }
        }

    }

    fun goToEditProfileFragment(user: User) {

        binding.profileEditBtn.setOnClickListener {

            val dialog = Dialog(requireContext())

            dialog.setContentView(R.layout.alert_dialog)

            dialog.dialog_main_message.text = Constants.GO_TO_EDIT_PROFILE_DIALOG_MAIN_MESSAGE
            dialog.dialog_sub_text.text = Constants.GO_TO_EDIT_PROFILE_DIALOG_SUB_MESSAGE

            dialog.button_no.setOnClickListener { dialog.dismiss() }

            dialog.button_yes.setOnClickListener {
                val action = ProfileFragmentDirections.actionProfileFragmentToEditProfileFragment(user)
                findNavController().navigate(action)
                dialog.dismiss()
            }

            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            dialog.show()

        }

    }

    private fun foreverOptout(foodPack: FoodPack) {}

}
