package com.sergio.tallerandroid.app.ui.data_list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.sergio.tallerandroid.R
import com.sergio.tallerandroid.app.base.BaseFragment
import com.sergio.tallerandroid.app.extension.onChange
import com.sergio.tallerandroid.app.helpers.RecyclerViewHelper
import com.sergio.tallerandroid.databinding.FragmentDataListBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DataListFragment : BaseFragment() {
    override val viewModel: DataListViewModel by viewModels()
    private val adapter = AnimalAdapter( {viewModel.addAnimalSpecimen(it)}, {viewModel.subtractAnimalSpecimen(it)})
    private lateinit var binding: FragmentDataListBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.fragment_data_list, container, false)
        configFragment()

        return binding.root
    }

    override fun configFragment() {
        displayHomeAsUpEnabled(true)

        binding.animalListView.adapter = adapter
        binding.animalListView.layoutManager = LinearLayoutManager(context)
        binding.animalListView.addItemDecoration(RecyclerViewHelper(resources.getDimension(R.dimen.spacing_m).toInt()))
    }

    override fun observeViewModel() {
        viewModel.animalList?.observe(viewLifecycleOwner, {
            adapter.submitList(it)
            if(adapter.itemCount == 0) {
                binding.emptyDataText.visibility = View.VISIBLE
            } else {
                binding.emptyDataText.visibility = View.GONE
            }
        })

        viewModel.eventsFlow.onChange(lifecycle, lifecycleScope) {
            when(it) {
                is DataListViewModel.DataListEvents.ErrorDatabaseExtract -> setEmptyLayout()
            }
        }
    }

    private fun setEmptyLayout() {
        binding.emptyDataText.visibility = View.VISIBLE
    }

}