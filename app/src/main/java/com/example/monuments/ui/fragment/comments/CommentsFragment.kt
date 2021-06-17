package com.example.monuments.ui.fragment.comments

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.monuments.R
import com.example.monuments.databinding.FragmentCommentsBinding
import com.example.monuments.domain.CommentBO
import com.example.monuments.domain.MonumentBO
import com.example.monuments.extensions.changeVisible
import com.example.monuments.repository.MainRepository
import com.example.monuments.ui.adapter.CommentsAdapter
import com.example.monuments.ui.dialog.CommentDialogFragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class CommentsFragment: Fragment(), CommentDialogFragment.AddCommentListener {

    private val viewModel by lazy { ViewModelProvider(this).get(CommentsViewModel::class.java) }

    private var commentsViewBinding: FragmentCommentsBinding? = null

    private var id: String? = null

    private val commentsInterfaceImp = object : CommentsAdapter.CommentsInterface {
        override fun getEmail(): String = viewModel.getEmail()
    }

    private val mAdapter = CommentsAdapter(commentsInterfaceImp)

    private val progressBarObserver = Observer<Boolean> {
        commentsViewBinding?.commentsProgressProgressBar?.changeVisible(it)
    }

    private val commentsObserver = Observer<List<CommentBO>> {
        if (it.isNotEmpty()) {
            commentsViewBinding?.commentsImgNoCommentsImg?.changeVisible(false)
            commentsViewBinding?.commentsLabelNoCommentsLabel?.changeVisible(false)
            mAdapter.submitList(viewModel.sortComments(it))

        } else {
            commentsViewBinding?.commentsImgNoCommentsImg?.changeVisible(true)
            commentsViewBinding?.commentsLabelNoCommentsLabel?.changeVisible(true)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.comments_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.comments__btn__refresh_btn -> {
                viewModel.getData()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        commentsViewBinding = FragmentCommentsBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)
        return commentsViewBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        commentsViewBinding?.commentsListAllCommentsList?.layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
        commentsViewBinding?.commentsListAllCommentsList?.adapter = mAdapter

        viewModel.progressBarStatus.observe(viewLifecycleOwner, progressBarObserver)

        commentsViewBinding?.commentsBtnAddComment?.setOnClickListener {
            val dialog: CommentDialogFragment = CommentDialogFragment.newInstance()
            dialog.setTargetFragment(this, 10)
            dialog.show(this.parentFragmentManager, getString(R.string.detail_dialog_tag))
        }

        arguments?.let {
            id = CommentsFragmentArgs.fromBundle(it).id

            id?.let { id -> viewModel.getComments(id).observe(viewLifecycleOwner, commentsObserver) }


        }
    }


    override fun onAddBtnClicked(rate: Int, comment: String) {
        id?.let { id -> viewModel.addComment(rate, comment, id) }

    }
}