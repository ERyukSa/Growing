package com.eryuksa.growing.motivation

import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.eryuksa.growing.R
import com.eryuksa.growing.databinding.FragmentMotivationBinding
import com.eryuksa.growing.motivation.data.YoutubeRepository
import com.eryuksa.growing.motivation.youtube_detail.YoutubeDetailActivity

class MotivationFragment : Fragment(R.layout.fragment_motivation) {

    private lateinit var binding: FragmentMotivationBinding

    private val listViewModel: YoutubeListViewModel by lazy {
        ViewModelProvider(this)[YoutubeListViewModel::class.java]
    }
    private val listAdapter: YoutubeListAdapter by lazy {
        YoutubeListAdapter(listViewModel) {
            val intent = YoutubeDetailActivity.newIntent(requireContext(), it.videoId, it.title)
            startActivity(intent)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        setUpBinding(inflater, container)
        setUpRecyclerView()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        listViewModel.youtubeList.observe(viewLifecycleOwner) {
            if (listViewModel.categoryChanged) {
                listAdapter.notifyDataSetChanged()
            } else {
                listAdapter.notifyItemRangeInserted(
                    listViewModel.prevListSize - 1,
                    it.size - listViewModel.prevListSize
                )
            }
        }
    }

    private fun setUpBinding(inflater: LayoutInflater, container: ViewGroup?) {
        binding = FragmentMotivationBinding.inflate(inflater, container, false)
        binding.listViewModel = listViewModel
        binding.lifecycleOwner = viewLifecycleOwner
    }

    private fun setUpRecyclerView() {
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            adapter = listAdapter
            addItemDecoration(ListItemDecoration())
        }
        
        binding.recyclerView.addOnScrollListener(object: RecyclerView.OnScrollListener() {

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                // ??????????????? ????????? ????????? ????????? LIMIT_SIZE ????????? ??? ?????? ????????? ???????????? ??????
                if (listViewModel.currentListSize % YoutubeRepository.LIMIT_SIZE != 0) {
                    return
                }

                super.onScrolled(recyclerView, dx, dy)

                val lastPos =
                    (recyclerView.layoutManager!! as LinearLayoutManager).findLastVisibleItemPosition()

                if (recyclerView.adapter!!.itemCount - lastPos <= LOAD_ITEMS_OFFSET) {
                    listViewModel.loadMoreItems()
                }
            }
        })
    }

    companion object {
        private const val LOAD_ITEMS_OFFSET = 5 // ????????? ???????????? ??????) ???????????? ??? 5??? ????????? ???????????? ????????? ??????

        fun newInstance(): MotivationFragment {
            return MotivationFragment()
        }
    }
}

class ListItemDecoration: RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        if (parent.getChildAdapterPosition(view) != 0) {
            outRect.top = 12
        }
    }
}