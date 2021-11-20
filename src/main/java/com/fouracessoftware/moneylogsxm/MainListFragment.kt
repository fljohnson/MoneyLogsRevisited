package com.fouracessoftware.moneylogsxm

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.Nullable
import androidx.appcompat.widget.Toolbar
import androidx.core.view.iterator
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import com.fouracessoftware.moneylogsxm.datadeal.Category
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainListFragment : Fragment() {


    private lateinit var viewModel: MainListViewModel
    private val dbIDs = ArrayList<ArrayList<Long>>()
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
        viewModel.getCategoriesAndTxns().observe(viewLifecycleOwner,object : Observer<List<Displayable>?> {
            override fun onChanged(t: List<Displayable>?) {
                //update UI
                setupList(t)
            }
        })
        viewModel.loadTxnsPerCategory()
        view?.findViewById<ExpandableListView>(R.id.bigList)?.setOnChildClickListener(object:ExpandableListView.OnChildClickListener{
            override fun onChildClick(
                parent: ExpandableListView?,
                v: View?,
                groupPosition: Int,
                childPosition: Int,
                id: Long
            ): Boolean {
                //bundle that ID as an arg, then navigate
                openItem(dbIDs[groupPosition][childPosition])
                return true
            }

        }

        )

        val barra = (requireActivity() as MainActivity).findViewById<Toolbar>(R.id.toolbar)
        for(i in barra.menu){
            i.isVisible = false;
        }
        barra.menu.findItem(R.id.chronoview).isVisible = true

        barra.setOnMenuItemClickListener { x ->

            when (x.itemId) {
                R.id.chronoview -> {
                 //   (requireActivity() as MainActivity).navController.navigate(R.id.monthListFragment)
                    true
                }
                else -> {
                    false
                }
            }
        }

        view.findViewById<FloatingActionButton>(R.id.floatingActionButton).setOnClickListener {
            //val barra = (requireActivity() as MainActivity).findViewById<Toolbar>(R.id.toolbar)
            //barra.menu.findItem(R.id.save).isVisible = true;
            openItem(-1L)

        }
    }

    private fun openItem(i:Long) {
        var args = Bundle()
        args.putLong("ID",i)
        (requireActivity() as MainActivity).navController.navigate(R.id.txnFragment,args)
    }

    private fun setupList(t: List<Displayable>?) {
        if(t == null) {
            return
        }
        val groupData:ArrayList<HashMap<String, String>> = arrayListOf()
        val groupFrom = arrayOf("NAME","SUBTOTAL")
        val groupTo= intArrayOf(R.id.text1,R.id.total1) //arrayOf(R.id.category_name).toIntArray()
        val groupLayout = R.layout.list_category

        val childData: ArrayList<ArrayList<HashMap<String,String>>> = arrayListOf()
        val childFrom = arrayOf("TXN")
        val childTo = intArrayOf(android.R.id.text2)
        val childLayout = android.R.layout.simple_expandable_list_item_2
        dbIDs.clear()
        var x=-1
        for(i in t){
            x++
            dbIDs.add(arrayListOf())
            val aha = HashMap<String, String>()

            val children: ArrayList<HashMap<String, String>> = ArrayList()
            var y=-1
            for(j in i.txnSet){
                y++
                var curChildMap = HashMap<String,String>()
                curChildMap.set("TXN","${j.amount} to ${j.who} on ${j.date}")
                children.add(curChildMap)
                println("Adding "+j.id+"at ($x,$y)")
                dbIDs[x].add(j.id)
            }
            aha.set("NAME", i.category_name) //arrayListOf(," \$${i.sigma()}"))
            aha.set("SUBTOTAL", " \$${i.sigma()}")
            groupData.add(aha)
            childData.add(children)
        }
        val adapteur = SimpleExpandableListAdapter(context,groupData,groupLayout,groupFrom,groupTo,
            childData,childLayout,childFrom,childTo)
        view?.findViewById<ExpandableListView>(R.id.bigList)?.setAdapter(adapteur)

    }
/*
    class TxnExpandableListAdapter(
        context: Context?,
        groupData: ArrayList<HashMap<String, String>>,
        groupLayout: Int,
        groupFrom: Array<String>,
        groupTo: IntArray,
        childData: ArrayList<ArrayList<HashMap<String, String>>>,
        childLayout: Int,
        childFrom: Array<String>,
        childTo: IntArray
    ) : SimpleExpandableListAdapter(context,groupData,groupLayout,groupFrom,groupTo,
            childData,childLayout,childFrom,childTo) {

    }
*/
}



