package com.fouracessoftware.moneylogsxm

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.fouracessoftware.moneylogsxm.datadeal.Txn
import com.google.android.material.floatingactionbutton.FloatingActionButton

/**
 * A fragment representing a list of Items.
 */
class TxnListFragment : Fragment() {

    private lateinit var viewModel: MainListViewModel
    private var columnCount = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            columnCount = it.getInt(ARG_COLUMN_COUNT)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_txn_list, container, false)

        viewModel = ViewModelProvider(this).get(MainListViewModel::class.java)

        val barra = (requireActivity() as MainActivity).findViewById<Toolbar>(R.id.toolbar)

        barra.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
        // Set the adapter
        val rv = view.findViewById<RecyclerView>(R.id.list)
        if (rv is RecyclerView) {
            with(rv) {
                layoutManager = when {
                    columnCount <= 1 -> LinearLayoutManager(context)
                    else -> GridLayoutManager(context, columnCount)
                }

                viewModel.getTxns().observe(viewLifecycleOwner,object : Observer<List<Txn>?> {
                    override fun onChanged(t: List<Txn>?) {
                        //update UI
                        adapter = TxnRecyclerViewAdapter(t)
                    }
                })
                viewModel.loadTxns()

            }
        }

        view.findViewById<FloatingActionButton>(R.id.floatingActionButtonChronoList).setOnClickListener {
            //val barra = (requireActivity() as MainActivity).findViewById<Toolbar>(R.id.toolbar)
            //barra.menu.findItem(R.id.save).isVisible = true;
            openItem(-1L)

        }
        return view
    }


    fun openItem(i:Long) {
        var args = Bundle()
        args.putLong("ID",i)
        (requireActivity() as MainActivity).navController.navigate(R.id.txnFragment,args)
    }

    fun onItemClick(index: Int) {
        view?.findViewById<RecyclerView>(R.id.list)?.adapter?.let {
            val i = it.getItemId(index)
            if(i != -1L) {
                openItem(i)
            }
        }

    }

    companion object {

        // TODO: Customize parameter argument names
        const val ARG_COLUMN_COUNT = "column-count"

        // TODO: Customize parameter initialization
        @JvmStatic
        fun newInstance(columnCount: Int) =
            TxnListFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_COLUMN_COUNT, columnCount)
                }
            }
    }
}