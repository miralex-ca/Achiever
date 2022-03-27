package com.muralex.achiever.presentation.fragments.contacts

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.muralex.achiever.R
import com.muralex.achiever.databinding.FragmentContactsBinding
import com.muralex.achiever.presentation.utils.dataBindings
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ContactsFragment : Fragment(R.layout.fragment_contacts) {

    @Inject
    lateinit var contactActions: ContactActions

    private val binding by dataBindings(FragmentContactsBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnSendFeedback.setOnClickListener{contactActions.sendFeedback()}
        binding.btnSendReport.setOnClickListener{contactActions.sendReport()}
        binding.btnShare.setOnClickListener{contactActions.shareApp()}
        binding.btnRate.setOnClickListener{contactActions.rateApp()}
    }

}