import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ksh.daquotes.databinding.MainpageItemBinding
import com.ksh.daquotes.utility.Quote

class MainPageAdapter(private var quoteList: MutableList<Quote>) : RecyclerView.Adapter<MainPageAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: MainpageItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = MainpageItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val quote = quoteList[position]
        holder.binding.quoteText.text = quote.message
        holder.binding.authorText.text = "- ${quote.author} -"
    }

    override fun getItemCount(): Int = quoteList.size

    // 명언을 하나씩 추가하는 함수
    fun addQuote(quote: Quote) {
        quoteList.add(quote)
        notifyItemInserted(quoteList.size - 1) // 새 명언이 추가되면 업데이트
    }

    fun getQuote(position: Int): Quote? {
        return Quote(message = quoteList[position].message, author = quoteList[position].author)
    }
}
