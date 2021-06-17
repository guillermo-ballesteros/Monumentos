package com.example.monuments.ui.fragment.urlExtra

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.*
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import com.example.monuments.databinding.FragmentUrlExtraBinding


class UrlExtraFragment : Fragment() {

    private val webViewClient = object : WebViewClient() {
        override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
            return false
        }
    }

    private var urlExtraViewBinding: FragmentUrlExtraBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        urlExtraViewBinding = FragmentUrlExtraBinding.inflate(inflater, container, false)
        return urlExtraViewBinding?.root
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let {
            val url = UrlExtraFragmentArgs.fromBundle(it).url
            urlExtraViewBinding?.detailWebExtraInfo?.settings?.javaScriptEnabled = true
            urlExtraViewBinding?.detailWebExtraInfo?.settings?.builtInZoomControls = true

            if (url != null) {
                urlExtraViewBinding?.detailWebExtraInfo?.loadUrl(url)
            }

            urlExtraViewBinding?.detailWebExtraInfo?.webViewClient = webViewClient
        }
    }

}