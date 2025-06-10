package com.example.mycoffeeapp.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mycoffeeapp.Domain.ItemsModel
import com.example.mycoffeeapp.Helper.ManagmentFavorite
import com.example.mycoffeeapp.databinding.ViewholderFavoriteBinding // Đảm bảo đúng tên class binding

class FavoriteAdapter(
    private val items: ArrayList<ItemsModel>,
    private val context: Context,
    private val managmentFavorite: ManagmentFavorite,
    private val userId: String
) : RecyclerView.Adapter<FavoriteAdapter.FavoriteViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteViewHolder {
        val binding = ViewholderFavoriteBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FavoriteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FavoriteViewHolder, position: Int) {
        val item = items[position]

        holder.binding.titleTxt.text = item.title
        // RẤT QUAN TRỌNG: Đảm bảo ID này khớp chính xác với ID trong viewholder_favorite.xml
        // Nếu trong XML là priceTxt, thì dùng holder.binding.priceTxt.text
        // Nếu trong XML là feeEachItem, thì dùng holder.binding.feeEachItem.text
        holder.binding.feeEachItem.text = "${item.price} VND" // Dựa trên XML bạn cung cấp, đây là feeEachItem

        // RẤT QUAN TRỌNG: Đảm bảo ID này khớp chính xác với ID trong viewholder_favorite.xml
        // Dựa trên XML bạn cung cấp, đây là picCart
        Glide.with(holder.itemView.context)
            .load(item.picUrl.firstOrNull()) // Sử dụng .firstOrNull() để tránh lỗi nếu picUrl rỗng
            .into(holder.binding.picCart)

        // Xử lý sự kiện hủy yêu thích
        // RẤT QUAN TRỌNG: Đảm bảo ID này khớp chính xác với ID trong viewholder_favorite.xml
        // Dựa trên XML bạn cung cấp, đây là removeItemBtn
        holder.binding.removeItemBtn.setOnClickListener {
            val itemRemoved = items[position]
            managmentFavorite.toggleFavorite(itemRemoved, userId) // Gọi toggleFavorite để xóa

            // Cần thông báo cho adapter biết item đã bị xóa để RecyclerView cập nhật
            items.removeAt(position)
            notifyItemRemoved(position)
            // notifyItemRangeChanged có thể không cần thiết nếu chỉ xóa 1 item,
            // nhưng có thể hữu ích để các item khác dịch chuyển đúng vị trí.
            notifyItemRangeChanged(position, items.size)
            Toast.makeText(context, "${itemRemoved.title} removed from favorites", Toast.LENGTH_SHORT).show()
        }
    }

    override fun getItemCount(): Int = items.size

    class FavoriteViewHolder(val binding: ViewholderFavoriteBinding) : RecyclerView.ViewHolder(binding.root)
}