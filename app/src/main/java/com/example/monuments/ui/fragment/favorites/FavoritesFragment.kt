package com.example.monuments.ui.fragment.favorites

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Index
import com.example.monuments.databinding.FragmentFavoritesBinding
import com.example.monuments.domain.MonumentBO
import com.example.monuments.extensions.changeVisible
import com.example.monuments.repository.MainRepository
import com.example.monuments.ui.adapter.AuxListMonumentsAdapter
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class FavoritesFragment : Fragment() {

    private val viewModel by lazy { ViewModelProvider(this).get(FavoritesViewModel::class.java) }

    private var favoriteMonumentsViewBinding: FragmentFavoritesBinding? = null

    private val listener = object: AuxListMonumentsAdapter.AuxMonumentsListener {
        override fun onClick(id: String) {
            val action = FavoritesFragmentDirections.actionFavoritesToDetail(id)
            NavHostFragment.findNavController(this@FavoritesFragment).navigate(action)
        }
    }

    private val mAdapter by lazy { AuxListMonumentsAdapter(listener) }

    private val favoritesObserver = Observer<List<MonumentBO>> {
        favoriteMonumentsViewBinding?.favoritesProgressProgressBar?.changeVisible(true)
        if (it.isNotEmpty()) {
            mAdapter.submitList(it)

        } else {
            favoriteMonumentsViewBinding?.favoriteMonumentsImgMonumentImage?.changeVisible(true)
            favoriteMonumentsViewBinding?.favoriteMonumentsLabelEmptyListLabel?.changeVisible(true)
        }
        favoriteMonumentsViewBinding?.favoritesProgressProgressBar?.changeVisible(false)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        favoriteMonumentsViewBinding = FragmentFavoritesBinding.inflate(inflater, container, false)
        return favoriteMonumentsViewBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        favoriteMonumentsViewBinding?.favoritesListFavoritesMonuments?.layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
        favoriteMonumentsViewBinding?.favoritesListFavoritesMonuments?.adapter = mAdapter

        viewModel.getFavoritesMonuments().observe(viewLifecycleOwner, favoritesObserver)
    }
}
