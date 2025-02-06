//package com.example.drawrun.ui.search.adaptor
//
//import android.view.LayoutInflater
//import android.view.ViewGroup
//import androidx.recyclerview.widget.RecyclerView
//
//class SearchResultAdapter : ListAdapter<SearchResult, SearchResultViewHolder>(SearchDiffCallback()) {
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchResultViewHolder {
//        val binding = ItemSearchResultBinding.inflate(
//            LayoutInflater.from(parent.context), parent, false
//        )
//        return SearchResultViewHolder(binding)
//    }
//
//    override fun onBindViewHolder(holder: SearchResultViewHolder, position: Int) {
//        holder.bind(getItem(position))
//    }
//}
//
//class SearchResultViewHolder(
//    private val binding: ItemSearchResultBinding
//) : RecyclerView.ViewHolder(binding.root) {
//
//    fun bind(item: SearchResult) {
//        binding.apply {
//            titleTextView.text = item.title
//            distanceTextView.text = item.distance
//            locationTextView.text = item.location
//        }
//    }
//}
//
//data class SearchResult(
//    val title: String,
//    val distance: String,
//    val location: String
//)
