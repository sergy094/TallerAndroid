package com.sergio.tallerandroid.app.base

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel

abstract class BaseFragment : Fragment() {

    abstract val viewModel: ViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        observeViewModel()
        super.onViewCreated(view, savedInstanceState)
    }

    abstract fun configFragment()

    abstract fun observeViewModel()

    open fun displayHomeAsUpEnabled(showHomeAsUp: Boolean) =
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(showHomeAsUp)
}