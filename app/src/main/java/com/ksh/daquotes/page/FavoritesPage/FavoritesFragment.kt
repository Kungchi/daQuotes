package com.ksh.daquotes.page.FavoritesPage

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.ksh.daquotes.databinding.FragmentFavoritesBinding
import com.ksh.daquotes.db
import com.ksh.daquotes.utility.Quote
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FavoritesFragment : Fragment() {
    private lateinit var binding: FragmentFavoritesBinding
    private lateinit var adapter: FavoritesAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.favoritesRecyclerView.setHasFixedSize(true)
        binding.favoritesRecyclerView.itemAnimator = null  // 애니메이션 제거

        CoroutineScope(Dispatchers.IO).launch {
            var quote_list : List<Quote> = db.quoteDao().getAll()
            Log.e("테스트", "db 계속 조회중")
            withContext(Dispatchers.Main) {
                adapter = FavoritesAdapter(quote_list)
                binding.favoritesRecyclerView.layoutManager = GridLayoutManager(requireContext(), 2) // 3개의 열
                binding.favoritesRecyclerView.adapter = adapter
            }
        }
    }
}
