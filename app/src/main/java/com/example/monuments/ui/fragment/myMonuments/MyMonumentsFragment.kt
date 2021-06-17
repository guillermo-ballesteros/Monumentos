package com.example.monuments.ui.fragment.myMonuments

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
import com.example.monuments.R
import com.example.monuments.databinding.FragmentMyMonumentsBinding
import com.example.monuments.domain.MonumentBO
import com.example.monuments.extensions.changeVisible
import com.example.monuments.ui.adapter.AuxListMonumentsAdapter
import com.example.monuments.ui.dialog.RemoveMonumentDialogFragment
import com.example.monuments.utils.Constants
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MyMonumentsFragment : Fragment(), RemoveMonumentDialogFragment.RemoveMonumentListener {

    private val viewModel by lazy { ViewModelProvider(this).get(MyMonumentsViewModel::class.java) }

    private var myMonumentsViewBinding: FragmentMyMonumentsBinding? = null

    private val resultObserver = Observer<Int> { result ->
        when (result) {
            Constants.SUCCESS_CODE -> {
                view?.let { view ->
                    Snackbar.make(view, getString(R.string.detail_monument_deleted), Snackbar.LENGTH_SHORT).show()
                }
                viewModel.notificationDone()
            }
        }

    }

    private val progressBarObserver = Observer<Boolean> { value ->
        myMonumentsViewBinding?.myMonumentsProgressProgressBar?.changeVisible(value)
    }

    private val listObserver = Observer<List<MonumentBO>> {
        myMonumentsViewBinding?.myMonumentsProgressProgressBar?.changeVisible(true)
        if (it.isEmpty()) {
            myMonumentsViewBinding?.myMonumentsLabelEmptyListLabel?.changeVisible(true)
            myMonumentsViewBinding?.myMonumentsImgMonumentImage?.changeVisible(true)
        } else {
            mAdapter.submitList(it)
        }
        myMonumentsViewBinding?.myMonumentsProgressProgressBar?.changeVisible(false)
    }

    private val listener = object: AuxListMonumentsAdapter.AuxMonumentsListener {
        override fun onClick(id: String) {
            val action = MyMonumentsFragmentDirections.actionMyMonumentsToDetail(id)
            NavHostFragment.findNavController(this@MyMonumentsFragment).navigate(action)
        }

        override fun onLongClick(id: String) {
            val dialog: RemoveMonumentDialogFragment = RemoveMonumentDialogFragment.newInstance(id)
            dialog.setTargetFragment(this@MyMonumentsFragment, 10)
            dialog.show(this@MyMonumentsFragment.parentFragmentManager, getString(R.string.detail_dialog_tag))
        }
    }

    private val mAdapter by lazy { AuxListMonumentsAdapter(listener) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        myMonumentsViewBinding = FragmentMyMonumentsBinding.inflate(inflater, container, false)
        return myMonumentsViewBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        myMonumentsViewBinding?.monumentsListMyMonuments?.layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
        myMonumentsViewBinding?.monumentsListMyMonuments?.adapter = mAdapter

        viewModel.getMyMonuments()?.observe(viewLifecycleOwner, listObserver)
        viewModel.result.observe(viewLifecycleOwner, resultObserver)
        viewModel.progressBar.observe(viewLifecycleOwner, progressBarObserver)

        myMonumentsViewBinding?.myMonumentsBtnAdd?.setOnClickListener {
            val action = MyMonumentsFragmentDirections.actionMyMonumentsToAddMonumentsFragment()
            NavHostFragment.findNavController(this@MyMonumentsFragment).navigate(action)
        }

    }

    override fun remove(id: String) {
        viewModel.removeMonument(id)
    }

}