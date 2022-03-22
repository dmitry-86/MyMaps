package com.netology.mymaps.app

import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.map
import androidx.navigation.fragment.findNavController
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.netology.mymapapp.adapter.LocationAdapter
import com.netology.mymapapp.adapter.OnInteractionListener
import com.netology.mymapapp.dto.UserMap
import com.netology.mymapapp.viewmodel.MapViewModel
import com.netology.mymaps.R
import com.netology.mymaps.databinding.FragmentLocationsBinding

class FragmentLocations : Fragment() {

    private val viewModel: MapViewModel by viewModels(
        ownerProducer = ::requireParentFragment
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.go_to_map, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.map) {
            findNavController().navigate(R.id.action_fragmentLocations_to_displayMapsFragment)
            return true
        } else {
            super.onOptionsItemSelected(item)
            return true
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentLocationsBinding.inflate(
            inflater,
            container,
            false
        )

        val bundle = Bundle()

        val adapter = LocationAdapter(object : OnInteractionListener {
            override fun onItemClick(position: Int) {
                //передаем позицию точки
                bundle.putLong("id", position.toLong())
                findNavController().navigate(
                    R.id.action_fragmentLocations_to_displayMapsFragment,
                    bundle
                )
            }

            override fun onRemove(location: UserMap) {
                viewModel.removeById(location.id)
            }

            override fun onEdit(location: UserMap) {
                showAlertDialog()
                viewModel.edit(location)
            }

        })

        binding.list.adapter = adapter

        viewModel.data.observe(viewLifecycleOwner) { locations ->
            adapter.submitList(locations)
        }

        binding.fabCreateMap.setOnClickListener {
            findNavController().navigate(R.id.action_fragmentLocations_to_createMapFragment)
        }

        return binding.root
    }

    private fun showAlertDialog() {
        val placeFormView =
            LayoutInflater.from(activity).inflate(R.layout.dialog_create_place, null)
        val dialog = AlertDialog.Builder(requireActivity())
            .setTitle(getString(R.string.edit)).setMessage(getString(R.string.enter_new_title))
            .setView(placeFormView)
            .setNegativeButton(getString(R.string.cancel), null)
            .setPositiveButton(getString(R.string.ok), null)
            .show()

        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener {
            val title = placeFormView.findViewById<EditText>(R.id.etTitle).text.toString()
            if (title.trim().isEmpty()) {
                Toast.makeText(activity, getString(R.string.title_is_empty), Toast.LENGTH_LONG)
                    .show()
                return@setOnClickListener
            }
            viewModel.changeContent(title)
            viewModel.save()
            dialog.dismiss()
        }

    }
}