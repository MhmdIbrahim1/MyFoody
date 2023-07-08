package com.example.myfoody.ui.fragments.instructions

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebViewClient
import com.example.myfoody.databinding.FragmentInstructionsBinding
import com.example.myfoody.models.Result
import com.example.myfoody.util.Constants
import com.example.myfoody.util.retrieveParcelable


class Instructions : Fragment() {
    private var _binding: FragmentInstructionsBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentInstructionsBinding.inflate(layoutInflater, container, false)

        // Retrieve recipe details from arguments bundle
        val args = arguments
        val myBundle: Result? = args?.retrieveParcelable(Constants.RECIPE_RESULT_KEY)

        if (myBundle != null) {
            // Load the recipe instructions in the WebView
            binding.instructionsWebView.webViewClient = object : WebViewClient() {}
            val websiteUrl: String = myBundle.sourceUrl
            binding.instructionsWebView.loadUrl(websiteUrl)
        }
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}