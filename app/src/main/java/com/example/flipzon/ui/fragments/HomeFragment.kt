package com.example.flipzon.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.flipzon.databinding.FragmentHomeBinding
import com.example.flipzon.ui.adapters.ProductAdapter
import com.example.flipzon.viewmodel.CartViewModel
import com.example.flipzon.viewmodel.HomeViewModel
import com.example.flipzon.viewmodel.ProductsUiState

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val homeViewModel: HomeViewModel by viewModels()
    private val cartViewModel: CartViewModel by activityViewModels()

    private lateinit var productAdapter: ProductAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupSearch()
        observeProducts()

        binding.btnRetry.setOnClickListener {
            homeViewModel.retry()
        }
    }

    private fun setupRecyclerView() {
        productAdapter = ProductAdapter { product ->
            cartViewModel.addToCart(product.id, product.title, product.price, product.thumbnail)
            Toast.makeText(requireContext(), "${product.title} added to cart", Toast.LENGTH_SHORT).show()
        }

        val layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rvProducts.layoutManager = layoutManager
        binding.rvProducts.adapter = productAdapter

        binding.rvProducts.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val totalItemCount = layoutManager.itemCount
                val lastVisible = layoutManager.findLastVisibleItemPosition()

                if (lastVisible >= totalItemCount - 4) {
                    homeViewModel.loadMore()
                }
            }
        })
    }

    private fun setupSearch() {
        binding.etSearch.addTextChangedListener { text ->
            homeViewModel.searchProducts(text.toString())
        }
    }

    private fun observeProducts() {
        homeViewModel.productsState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is ProductsUiState.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.rvProducts.visibility = View.GONE
                    binding.errorLayout.visibility = View.GONE
                    binding.tvEmpty.visibility = View.GONE
                }
                is ProductsUiState.Success -> {
                    binding.progressBar.visibility = View.GONE
                    binding.rvProducts.visibility = View.VISIBLE
                    binding.errorLayout.visibility = View.GONE
                    binding.tvEmpty.visibility = View.GONE
                    productAdapter.submitList(state.products)
                }
                is ProductsUiState.Error -> {
                    binding.progressBar.visibility = View.GONE
                    binding.rvProducts.visibility = View.GONE
                    binding.errorLayout.visibility = View.VISIBLE
                    binding.tvEmpty.visibility = View.GONE
                    binding.tvError.text = state.message
                }
                is ProductsUiState.Empty -> {
                    binding.progressBar.visibility = View.GONE
                    binding.rvProducts.visibility = View.GONE
                    binding.errorLayout.visibility = View.GONE
                    binding.tvEmpty.visibility = View.VISIBLE
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
