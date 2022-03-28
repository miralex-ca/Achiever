package com.example.tasker.presentation.components.imagepicker

import android.content.Context
import android.view.LayoutInflater
import android.webkit.URLUtil
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.muralex.achiever.R
import com.muralex.achiever.data.models.usemodels.GroupData
import com.muralex.achiever.databinding.DialogImagePickerBinding
import com.muralex.achiever.presentation.utils.Constants
import com.muralex.achiever.presentation.utils.Constants.ITEM_ICONS
import com.muralex.achiever.presentation.utils.gone
import com.muralex.achiever.presentation.utils.setTabButtonActive
import com.muralex.achiever.presentation.utils.visible
import dagger.hilt.android.qualifiers.ActivityContext
import javax.inject.Inject


class ImagePickerDialog @Inject constructor (
    @ActivityContext private val context: Context,
    ) {



    private var dialog: AlertDialog? = null
    private var pics = ITEM_ICONS

    private var activeTab = 0

    fun setImages(list: List<String>) {
        pics = list
    }

    fun open() {

        val layoutInflater =
            context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val binding = DialogImagePickerBinding.inflate(layoutInflater)

        val imgPickerAdapter = ImgPickerAdapter(pics, -1
        ) { selectedImage: String ->
            onItemClicker?.let { it(selectedImage) }
            dismiss()
        }

        val layoutManager = GridLayoutManager(context, 4)
        binding.recyclerView.adapter = imgPickerAdapter
        binding.recyclerView.layoutManager = layoutManager

        binding.tabIcons.setOnClickListener {
            openTab(binding, ICONS_TAB)
        }

        binding.button.setOnClickListener {
            onItemClicker?.let { it1 -> it1("url") }
            dismiss()
        }


        binding.btnSaveUrl.setOnClickListener {

            val text = binding.tidImageUrl.text

            if ( URLUtil.isValidUrl(text.toString())) {
                onItemClicker?.let { listener -> listener(text.toString()) }
                dismiss()
            } else {
                binding.tilImageUrl.error = "Check the url"
            }

        }

        binding.tabImages.setOnClickListener {
            openTab(binding, IMAGES_TAB)
        }



        dialog = MaterialAlertDialogBuilder(context)
            //.setTitle(context.getString(R.string.select_icon_txt))
            .setView(binding.root)
            .setPositiveButton(context.getString(R.string.close_txt)) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun openTab(binding: DialogImagePickerBinding, tab: Int) {
        if (tab == ICONS_TAB) {
            binding.images.gone()
            binding.icons.visible()
            binding.tabIcons.setTabButtonActive(true)
            binding.tabImages.setTabButtonActive(false)
        } else {
            binding.images.visible()
            binding.icons.gone()
            binding.tabIcons.setTabButtonActive(false)
            binding.tabImages.setTabButtonActive(true)
        }
    }

    private var onItemClicker: ((String) -> Unit) ? = null

    fun setOnItemClicker(listener:  (String) -> Unit) {
        onItemClicker = listener
    }

    private fun dismiss() {
        dialog?.dismiss()
    }

    companion object {
        const val ICONS_TAB = 2
        const val  IMAGES_TAB = 3
    }

}