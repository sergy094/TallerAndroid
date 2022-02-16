package com.sergio.tallerandroid.app.ui.home

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.sergio.tallerandroid.R
import com.sergio.tallerandroid.app.base.BaseFragment
import com.sergio.tallerandroid.app.extension.onChange
import com.sergio.tallerandroid.databinding.FragmentHomeBinding
import com.sergio.tallerandroid.model.FamilyData
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class HomeFragment : BaseFragment() {
    override val viewModel: HomeViewModel by viewModels()
    private lateinit var binding: FragmentHomeBinding
    private var animalDialog: AlertDialog? = null
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.fragment_home, container, false)
        configFragment()

        return binding.root
    }

    override fun configFragment() {
        displayHomeAsUpEnabled(false)

        viewModel.setStatisticsData()
        binding.viewDataButton.setOnClickListener { viewModel.onViewDataClick() }
        binding.deleteDataButton.setOnClickListener { viewModel.onDeleteDataClick() }
        binding.addAnimalFab.setOnClickListener { viewModel.onAddAnimalClick() }

    }

    override fun observeViewModel() {
        viewModel.apply {
            eventsFlow.onChange(lifecycle, lifecycleScope) {
                when (it) {
                    is HomeViewModel.HomeEvents.AddAnimalDialogEvent -> showAddAnimalDialog()
                    is HomeViewModel.HomeEvents.OnViewDataClickEvent -> navigateToViewData()
                    is HomeViewModel.HomeEvents.DeleteDataEvent -> showDeleteMessage()
                    is HomeViewModel.HomeEvents.AnimalInserted -> showAddSuccessMessage(true)
                    is HomeViewModel.HomeEvents.ExistingAnimalInserted -> showAddSuccessMessage(false)
                    is HomeViewModel.HomeEvents.OnDeleteDataClickEvent -> showDeleteConfirmDialog()
                }
            }

            errorEventFlow.onChange(lifecycle, lifecycleScope) { errors ->
                if (errors.isNotEmpty()) {
                    resetErrors()
                    validateForm(errors)
                } else {
                    animalDialog?.dismiss()
                }
            }

            speciesNumber.observe(viewLifecycleOwner, {
                binding.speciesNumberCount.text = speciesNumber.value.toString()
            })

            specimensNumber.observe(viewLifecycleOwner, {
                binding.specimensNumberCount.text = specimensNumber.value.toString()
            })
        }
    }

    private fun showAddSuccessMessage(isNewAnimal: Boolean) {
        viewModel.setStatisticsData()
        val message = if(isNewAnimal) getString(R.string.home_fragment_snackbar_new_animal_text) else getString(R.string.home_fragment_snackbar_existing_animal_text)
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
    }

    private fun showDeleteMessage() {
        viewModel.setStatisticsData()
        Snackbar.make(binding.root, getString(R.string.home_fragment_snackbar_delete_database_text), Snackbar.LENGTH_SHORT).show()
    }

    private fun resetErrors() {
        animalDialog?.findViewById<TextInputEditText>(R.id.species_text_field)?.error = null
        animalDialog?.findViewById<AutoCompleteTextView>(R.id.family_text_field)?.error = null
    }

    private fun validateForm(errorList: List<HomeViewModel.FormErrorEvents>) {
        if(errorList.filterIsInstance<HomeViewModel.FormErrorEvents.EmptySpeciesError>().isNotEmpty()) {
            animalDialog?.findViewById<TextInputEditText>(R.id.species_text_field)?.error = getString(R.string.common_required_error_text)
        }
        if(errorList.filterIsInstance<HomeViewModel.FormErrorEvents.EmptyFamilyError>().isNotEmpty()) {
            animalDialog?.findViewById<AutoCompleteTextView>(R.id.family_text_field)?.error = getString(R.string.common_required_error_text)
        }
    }

    private fun navigateToViewData() {
        findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToDataListFragment())
    }

    private fun showDeleteConfirmDialog() {
        AlertDialog.Builder(requireContext())
            .setCancelable(false)
            .setTitle("Borrar datos")
            .setMessage("¿Quieres borrar todos los datos")
            .setPositiveButton(getString(R.string.common_accept_text)) { _, _ ->
                viewModel.deleteData()
            }
            .setNegativeButton(getString(R.string.common_cancel_text), null)
            .show()
    }

    private fun showAddAnimalDialog() {
        val dialogView =
            requireActivity().layoutInflater.inflate(R.layout.custom_data_input_dialog, null)
        val speciesInput = dialogView.findViewById<TextInputEditText>(R.id.species_text_field)
        val familyInput = dialogView.findViewById<AutoCompleteTextView>(R.id.family_text_field)
        var selectedFamily: FamilyData? = null
        familyInput?.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            selectedFamily = FamilyData.fromId(position)
        }

        val items = FamilyData.getList().map { getString(it) }
        val adapter = ArrayAdapter(requireContext(), R.layout.family_item_layout, items)
        familyInput.setAdapter(adapter)

        val builder = AlertDialog.Builder(requireContext())
        builder.setPositiveButton(getString(R.string.common_accept_text), null)
            .setNeutralButton(getString(R.string.common_cancel_text), null)
            .setView(dialogView)
            .setCancelable(false)

        animalDialog = builder.create()

        animalDialog?.show()

        animalDialog?.getButton(AlertDialog.BUTTON_POSITIVE)?.setOnClickListener {
            viewModel.validateForm(
                speciesInput.text.toString(),
                selectedFamily
            )
        }
    }
}