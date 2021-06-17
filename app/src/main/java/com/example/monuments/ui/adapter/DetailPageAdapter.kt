package com.example.monuments.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.viewpager.widget.PagerAdapter
import com.bumptech.glide.Glide
import com.example.monuments.R
import com.example.monuments.domain.ImageBO
import java.util.*

class DetailPageAdapter(private val images: List<ImageBO>): PagerAdapter() {
    override fun getCount(): Int = images.size
    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object` as ConstraintLayout
    }
    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val view: View = LayoutInflater.from(container.context).inflate(R.layout.image_slider, container, false)
        val imageView: ImageView = view.findViewById(R.id.slider__img__monument_img)
        Glide.with(view.context).load(images[position].url).into(imageView)
        Objects.requireNonNull(container).addView(view)
        return view
    }
    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as ConstraintLayout)
    }
}