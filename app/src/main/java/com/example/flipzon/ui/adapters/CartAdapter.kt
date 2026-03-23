package com.example.flipzon.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.flipzon.R
import com.example.flipzon.data.local.CartEntity
import com.example.flipzon.databinding.ItemCartBinding

class CartAdapter(
    private val onIncrease: (Int) -> Unit,
    private val onDecrease: (Int) -> Unit
) : ListAdapter<CartEntity, CartAdapter.CartViewHolder>(CartDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val binding = ItemCartBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CartViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class CartViewHolder(private val binding: ItemCartBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: CartEntity) {
            binding.tvCartTitle.text = item.title
            binding.tvCartPrice.text = String.format("$%.2f", item.price)
            binding.tvQuantity.text = item.quantity.toString()

            Glide.with(binding.ivCartProduct.context)
                .load(item.thumbnail)
                .placeholder(R.drawable.ic_launcher_background)
                .into(binding.ivCartProduct)

            binding.btnPlus.setOnClickListener {
                onIncrease(item.productId)
            }

            binding.btnMinus.setOnClickListener {
                onDecrease(item.productId)
            }
        }
    }

    class CartDiffCallback : DiffUtil.ItemCallback<CartEntity>() {
        override fun areItemsTheSame(oldItem: CartEntity, newItem: CartEntity): Boolean {
            return oldItem.productId == newItem.productId
        }

        override fun areContentsTheSame(oldItem: CartEntity, newItem: CartEntity): Boolean {
            return oldItem == newItem
        }
    }
}
