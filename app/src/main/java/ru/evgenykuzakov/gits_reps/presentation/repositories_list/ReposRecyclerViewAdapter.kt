package ru.evgenykuzakov.gits_reps.presentation.repositories_list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.evgenykuzakov.gits_reps.data.storage.ProgrammingLanguages
import ru.evgenykuzakov.gits_reps.databinding.RepoItemBinding
import ru.evgenykuzakov.gits_reps.domain.model.Repo
import javax.inject.Inject

class ReposRecyclerViewAdapter @Inject constructor(
    private val programmingLanguages: ProgrammingLanguages
) : RecyclerView.Adapter<ReposRecyclerViewAdapter.ReposViewHolder>() {

    private var items: List<Repo> = emptyList()
    private var onItemClickListener: OnItemClickListener? = null

    interface OnItemClickListener {
        fun onClick(repo: Repo)
    }

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        this.onItemClickListener = onItemClickListener
    }

    fun setItems(items: List<Repo>) {
        this.items = items
        notifyDataSetChanged()
    }

    inner class ReposViewHolder(private val binding: RepoItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(repo: Repo) {
            binding.tvName.text = repo.name
            binding.tvLanguage.text = repo.language
            binding.tvAboutRepo.text = repo.description
            if (repo.description.isEmpty()) {
                binding.tvAboutRepo.visibility = View.GONE
            } else binding.tvAboutRepo.visibility = View.VISIBLE
            repo.language?.let {
                binding.tvLanguage.setTextColor(
                    programmingLanguages.getLanguageColor(
                        it
                    )
                )
            }
            binding.root.setOnClickListener {
                onItemClickListener?.onClick(repo)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReposViewHolder {
        val binding = RepoItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ReposViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ReposViewHolder, position: Int) {
        holder.bind(items[position])
    }

}