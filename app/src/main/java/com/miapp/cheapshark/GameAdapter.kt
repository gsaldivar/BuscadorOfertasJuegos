package com.miapp.cheapshark

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.miapp.cheapshark.api.GameDeal
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

class GameAdapter(
    private val deals: List<GameDeal>,
    private val dolarValue: Double
) : RecyclerView.Adapter<GameAdapter.GameViewHolder>() {

    private val clSymbols = DecimalFormatSymbols(Locale("es", "CL"))
    private val clFormatter = DecimalFormat("#,##0", clSymbols)

    class GameViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivThumbnail: ImageView = itemView.findViewById(R.id.ivGameThumbnail)
        val tvTitle: TextView = itemView.findViewById(R.id.tvGameTitle)
        val tvPricesUSD: TextView = itemView.findViewById(R.id.tvGamePricesUSD)
        val tvPricesCLP: TextView = itemView.findViewById(R.id.tvGamePricesCLP)
        val tvStore: TextView = itemView.findViewById(R.id.tvGameStore)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GameViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_game, parent, false)
        return GameViewHolder(view)
    }

    override fun onBindViewHolder(holder: GameViewHolder, position: Int) {
        val deal = deals[position]

        holder.tvTitle.text = deal.title
        holder.tvPricesUSD.text = "Oferta: \$${deal.salePrice} | Normal: \$${deal.normalPrice}"

        // Conversión a CLP
        val salePriceCLP = deal.salePrice.toDoubleOrNull()?.let { it * dolarValue }
        val normalPriceCLP = deal.normalPrice.toDoubleOrNull()?.let { it * dolarValue }

        holder.tvPricesCLP.text = if (salePriceCLP != null && normalPriceCLP != null) {
            val oferta = "\$${clFormatter.format(salePriceCLP)} CLP"
            val normal = "\$${clFormatter.format(normalPriceCLP)} CLP"
            "Oferta: $oferta | Normal: $normal"
        } else {
            "Precio CLP no disponible"
        }

        holder.tvStore.text = "Tienda: ${getStoreName(deal.storeID)}"

        if (deal.thumbnailUrl.isNotBlank()) {
            Glide.with(holder.itemView.context)
                .load(deal.thumbnailUrl)
                .into(holder.ivThumbnail)
        }
    }

    override fun getItemCount(): Int = deals.size

    private fun getStoreName(storeId: String): String = when (storeId) {
        "1" -> "Steam"
        "2" -> "GamersGate"
        "3" -> "GreenManGaming"
        "7" -> "GOG"
        "11" -> "Humble Store"
        "13" -> "Fanatical"
        "25" -> "Epic Games Store"
        else -> "Tienda Desconocida"
    }
}
