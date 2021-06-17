package com.example.monuments.ui.fragment.allMonuments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.monuments.R
import com.example.monuments.databinding.FragmentAllMonumentsBinding
import com.example.monuments.domain.MonumentBO
import com.example.monuments.extensions.changeVisible
import com.example.monuments.repository.MainRepository
import com.example.monuments.ui.adapter.AllMonumentsAdapter
import com.example.monuments.ui.dialog.RemoveMonumentDialogFragment
import com.example.monuments.utils.Constants
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AllMonumentsFragment : Fragment(), RemoveMonumentDialogFragment.RemoveMonumentListener {

    private val viewModel by lazy { ViewModelProvider(this).get(AllMonumentsViewModel::class.java) }

    private var allMonumentsViewBinding: FragmentAllMonumentsBinding? = null

    private val listener = object: AllMonumentsAdapter.AllMonumentsListener {
        override fun onFavClick(position: Int) {
            viewModel.changeFavorite(position)
        }

        override fun onLongClick(position: Int) {
            val dialog: RemoveMonumentDialogFragment = RemoveMonumentDialogFragment.newInstance(position)
            dialog.setTargetFragment(this@AllMonumentsFragment, 10)
            dialog.show(this@AllMonumentsFragment.parentFragmentManager, getString(R.string.detail_dialog_tag))
        }

        override fun onClick(id: String) {
            val action = AllMonumentsFragmentDirections.actionAllMonumentsToDetail(id)
            NavHostFragment.findNavController(this@AllMonumentsFragment).navigate(action)
        }
    }

    private val mAdapter = AllMonumentsAdapter(listener)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        allMonumentsViewBinding = FragmentAllMonumentsBinding.inflate(inflater, container, false)
        return allMonumentsViewBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.refreshData()

        allMonumentsViewBinding?.allListAllMonuments?.layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
        allMonumentsViewBinding?.allListAllMonuments?.adapter = mAdapter

        viewModel.getMonuments().observe(viewLifecycleOwner, monumentsObserver)

        viewModel.progressBar.observe(viewLifecycleOwner, progressBarObserver)
        viewModel.result.observe(viewLifecycleOwner, resultObserver)

        allMonumentsViewBinding?.allBtnMap?.setOnClickListener {
            val action = AllMonumentsFragmentDirections.actionAllMonumentsToMonumentsMaps()
            NavHostFragment.findNavController(this@AllMonumentsFragment).navigate(action)
        }
    }

    private val monumentsObserver = Observer<List<MonumentBO>>  {
        if (it.isNotEmpty()) {
            mAdapter.submitList(it)
        }
    }

    private val progressBarObserver = Observer<Boolean> {
        allMonumentsViewBinding?.allProgressProgressBar?.changeVisible(it)
    }

    private val resultObserver = Observer<Int> { result ->
        when (result) {
            Constants.NOT_SAME_USER_CODE -> {
                view?.let { view ->
                    Snackbar.make(view, getString(R.string.error_same_user), Snackbar.LENGTH_SHORT).show()
                }
                viewModel.notificationDone()
            }
            Constants.SUCCESS_CODE -> {
                view?.let { view ->
                    Snackbar.make(view, getString(R.string.detail_monument_deleted), Snackbar.LENGTH_SHORT).show()
                }
                viewModel.notificationDone()
            }
        }

    }

    override fun remove(monumentPosition: Int) {
        viewModel.removeMonument(monumentPosition)
    }

}