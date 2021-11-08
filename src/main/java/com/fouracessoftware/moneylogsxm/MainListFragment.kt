package com.fouracessoftware.moneylogsxm

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.Nullable
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import com.fouracessoftware.moneylogsxm.datadeal.Category
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainListFragment : Fragment() {


    private lateinit var viewModel: MainListViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.main_list_fragment, container, false)
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(MainListViewModel::class.java)
        // TODO: Use the ViewModel
        viewModel.getCategories().observe(viewLifecycleOwner,object : Observer<List<Category>?> {
            override fun onChanged(t: List<Category>?) {
                //update UI
                setupList(t)
            }
        })

        view.findViewById<FloatingActionButton>(R.id.floatingActionButton).setOnClickListener {
            val barra = (requireActivity() as MainActivity).findViewById<Toolbar>(R.id.toolbar)
            barra.menu.findItem(R.id.save).isVisible = true;
            var args = Bundle()
            args.putLong("ID",-1L)
            (requireActivity() as MainActivity).navController.navigate(R.id.txnFragment,args)
        }
    }

    private fun setupList(t: List<Category>?) {
        if(t == null) {
            return
        }
        val groupData:ArrayList<HashMap<String, String>> = arrayListOf()
        val groupFrom = arrayOf("NAME")
        val groupTo= intArrayOf(android.R.id.text1) //arrayOf(R.id.category_name).toIntArray()
        val groupLayout = android.R.layout.simple_expandable_list_item_1//R.layout.list_category

        val childData:ArrayList<ArrayList<HashMap<String,Int>>> = arrayListOf()
        val childFrom = arrayOf("TXN")
        val childTo = intArrayOf(android.R.id.text2)
        val childLayout = android.R.layout.simple_expandable_list_item_2
        for(i in t){
            val aha = HashMap<String, String>()
            aha.set("NAME", i.name)
            groupData.add(aha)

            childData.add(arrayListOf())
        }
        val adapteur = SimpleExpandableListAdapter(context,groupData,groupLayout,groupFrom,groupTo,
            childData,childLayout,childFrom,childTo)
        view?.findViewById<ExpandableListView>(R.id.bigList)?.setAdapter(adapteur)
    }

}

