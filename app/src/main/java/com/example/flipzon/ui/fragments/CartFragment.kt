package com.example.flipzon.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.flipzon.databinding.FragmentCartBinding
import com.example.flipzon.ui.adapters.CartAdapter
import com.example.flipzon.viewmodel.CartViewModel
import com.example.flipzon.viewmodel.CheckoutUiState

class CartFragment : Fragment() {

    private var _binding: FragmentCartBinding? = null
    private val binding get() = _binding!!

    private val cartViewModel: CartViewModel by activityViewModels()
    private lateinit var cartAdapter: CartAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        observeCart()
        observeCheckout()

        binding.btnCheckout.setOnClickListener {
            cartViewModel.checkout()
        }
    }

    private fun setupRecyclerView() {
        cartAdapter = CartAdapter(
            onIncrease = { productId -> cartViewModel.increaseQuantity(productId) },
            onDecrease = { productId -> cartViewModel.decreaseQuantity(productId) }
        )
        binding.rvCart.layoutManager = LinearLayoutManager(requireContext())
        binding.rvCart.adapter = cartAdapter
    }

    private fun observeCart() {
        cartViewModel.cartItems.observe(viewLifecycleOwner) { items ->
            cartAdapter.submitList(items)

            if (items.isNullOrEmpty()) {
                binding.tvEmptyCart.visibility = View.VISIBLE
                binding.rvCart.visibility = View.GONE
                binding.btnCheckout.isEnabled = false
                binding.tvTotal.text = "$0.00"
            } else {
                binding.tvEmptyCart.visibility = View.GONE
                binding.rvCart.visibility = View.VISIBLE
                binding.btnCheckout.isEnabled = true
                val total = items.sumOf { it.price * it.quantity }
                binding.tvTotal.text = String.format("$%.2f", total)
            }
        }
    }

    private fun observeCheckout() {
        cartViewModel.checkoutState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is CheckoutUiState.Loading -> {
                    binding.progressCheckout.visibility = View.VISIBLE
                    binding.btnCheckout.isEnabled = false
                }
                is CheckoutUiState.Success -> {
                    binding.progressCheckout.visibility = View.GONE
                    binding.btnCheckout.isEnabled = true
                    Toast.makeText(requireContext(), "Order placed successfully!", Toast.LENGTH_LONG).show()
                }
                is CheckoutUiState.Error -> {
                    binding.progressCheckout.visibility = View.GONE
                    binding.btnCheckout.isEnabled = true
                    Toast.makeText(requireContext(), state.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
