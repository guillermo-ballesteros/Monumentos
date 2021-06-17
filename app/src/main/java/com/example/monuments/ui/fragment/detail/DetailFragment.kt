package com.example.monuments.ui.fragment.detail

import android.os.Bundle
import android.text.Html
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import com.example.monuments.R
import com.example.monuments.databinding.FragmentDetailBinding
import com.example.monuments.domain.CommentBO
import com.example.monuments.domain.MonumentBO
import com.example.monuments.extensions.changeVisible
import com.example.monuments.ui.adapter.DetailPageAdapter
import com.example.monuments.ui.dialog.MonumentLocationDialogFragment
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class DetailFragment : Fragment() {

    private val viewModel by lazy { ViewModelProvider(this).get(DetailViewModel::class.java) }

    private var detailViewBinding: FragmentDetailBinding? = null

    private val commentsObserver = Observer<List<CommentBO>> {

        detailViewBinding?.detailLabelNotification?.text = it.size.toString()
    }

    private val monumentObserver = Observer<MonumentBO>{
        val viewPagerPosition =  detailViewBinding?.detailPagerViewPager?.currentItem?: 0
        val mViewPagerAdapter = DetailPageAdapter(it.images)

        detailViewBinding?.detailPagerViewPager?.adapter = mViewPagerAdapter
        detailViewBinding?.detailPagerViewPager?.currentItem = viewPagerPosition
        detailViewBinding?.detailLabelTitle?.text = it.name
        detailViewBinding?.detailLabelCity?.text = it.city

        if (it.favorite.isFavorite) {
            detailViewBinding?.detailBtnFavorite?.setBackgroundResource(R.drawable.ic_fav_on)

        } else {
            detailViewBinding?.detailBtnFavorite?.setBackgroundResource(R.drawable.ic_fav_off)
        }

        detailViewBinding?.detailLabelDescription?.text = Html.fromHtml(it.description)

        if(!it.urlExtraInformation.isNullOrBlank()) {
            detailViewBinding?.detailBtnWiki?.visibility = View.VISIBLE
        }
    }

    private val progressBarObserver = Observer<Boolean> {
        if (it) {
            detailViewBinding?.detailProgressProgressBar?.changeVisible(it)

        } else {
            detailViewBinding?.detailProgressProgressBar?.changeVisible(it)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        detailViewBinding = FragmentDetailBinding.inflate(inflater, container, false)
        return detailViewBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.monument.observe(viewLifecycleOwner, monumentObserver)
        viewModel.progressBarStatus.observe(viewLifecycleOwner, progressBarObserver)


        arguments?.let {
            val id = DetailFragmentArgs.fromBundle(it).monumentId
            viewModel.getMonument(id)
            viewModel.getComments(id).observe(viewLifecycleOwner, commentsObserver)
        }

        detailViewBinding?.detailBtnPrevious?.setOnClickListener{
            detailViewBinding?.detailPagerViewPager?.let {
                it.currentItem = it.currentItem.minus(1)
            }
        }

        detailViewBinding?.detailBtnNext?.setOnClickListener {
            detailViewBinding?.detailPagerViewPager?.let {
                it.currentItem = it.currentItem.plus(1)
            }
        }

        detailViewBinding?.detailBtnFavorite?.setOnClickListener {
            viewModel.changeFavorite()
        }

        detailViewBinding?.detailBtnRate?.setOnClickListener {
            val id: String? = viewModel.monument.value?.id
            val name: String? = viewModel.monument.value?.name
            if (id != null && name != null) {
                val action = DetailFragmentDirections.actionDetailToCommentsFragment(id, name, true)
                NavHostFragment.findNavController(this).navigate(action)
            }
        }

        detailViewBinding?.detailBtnLocation?.setOnClickListener {
            val location = LatLng(viewModel.monument.value?.location?.latitude?: 0.0, viewModel.monument.value?.location?.longitude?: 0.0)
            val name = viewModel.monument.value?.name?: ""
            val dialog: MonumentLocationDialogFragment = MonumentLocationDialogFragment.newInstance(location, name)
            dialog.setTargetFragment(this, 10)
            dialog.show(this.parentFragmentManager, getString(R.string.detail_dialog_tag))
        }

        detailViewBinding?.detailBtnWiki?.setOnClickListener{
            val action = DetailFragmentDirections.actionDetailToUrlExtra(viewModel.monument.value?.name, viewModel.monument.value?.urlExtraInformation, true)
            NavHostFragment.findNavController(this).navigate(action)
        }
    }

}