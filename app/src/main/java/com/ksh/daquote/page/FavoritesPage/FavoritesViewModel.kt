package com.ksh.daquote.page.FavoritesPage

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ksh.daquote.db
import com.ksh.daquote.utility.Quote
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FavoritesViewModel : ViewModel() {
    private val _liveData = MutableLiveData<List<Quote>>()
    val liveDate : LiveData<List<Quote>>
        get() = _liveData

    init {
        load()
    }

     fun load() {
        viewModelScope.launch(Dispatchers.IO) {
            _liveData.postValue(db.quoteDao().getAll())
        }
    }

    fun add_remove(quote: Quote) {
        viewModelScope.launch(Dispatchers.IO) {
            val like_quote = db.quoteDao().getSearch(quote.message)
            if(like_quote != null) {
                db.quoteDao().delete(quote.message)
            } else {
                db.quoteDao().insert(quote)
            }
            load()
        }
    }
}