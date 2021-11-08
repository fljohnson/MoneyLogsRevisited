package com.fouracessoftware.moneylogsxm

import android.content.Context
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.icu.util.TimeZone
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import android.widget.AdapterView.OnItemSelectedListener
import androidx.appcompat.widget.Toolbar
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.fouracessoftware.moneylogsxm.datadeal.Category
import com.fouracessoftware.moneylogsxm.datadeal.Txn
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputLayout
import java.util.*

class TxnFragment : Fragment(), OnItemSelectedListener {


    private lateinit var viewModel: MainListViewModel
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)

    private lateinit var workingTxn:Txn
    private lateinit var originalTxn:Txn

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val barra = (requireActivity() as MainActivity).findViewById<Toolbar>(R.id.toolbar)

        barra.setNavigationOnClickListener {
           if(!diffFromOriginal())
            {
                findNavController().navigateUp()
            }
            else
            {
                MaterialAlertDialogBuilder(requireContext())
                    .setMessage("Changes will not be saved ")
                    .setNegativeButton("Cancel", null)
                    .setPositiveButton("OK") { _, _ ->
                        findNavController().navigateUp()
                    }
                    .show()

           }
        }

        barra.setOnMenuItemClickListener { x ->

            when (x.itemId) {
                R.id.save -> {
                    beginSave()
                    true
                }
                else -> {
                    false
                }
            }
        }

        val victor = inflater.inflate(R.layout.txn_fragment, container, false)


        victor.findViewById<Button>(R.id.btnChangeDate)?.setOnClickListener {
            startDatePicker()
        }

        victor.findViewById<EditText>(R.id.amount).addTextChangedListener {
            workingTxn.amount=it.toString().toFloat()
        }
        victor.findViewById<EditText>(R.id.payee).addTextChangedListener {
            workingTxn.who=it.toString()
        }


        (victor.findViewById<TextInputLayout>(R.id.menu_category).editText as AutoCompleteTextView)
            .onItemSelectedListener = this
        return victor
    }

    //yeah, this is dirty. I'll look up anonymous classes later
    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        workingTxn.category_name = (parent?.adapter?.getItem(position) as Category).name
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel = ViewModelProvider(this).get(MainListViewModel::class.java)
        viewModel.getCategories().observe(viewLifecycleOwner,
            { t -> //update UI
                setupList(t)
            })
        var txnId = -1L
        if(arguments != null) {
            txnId = requireArguments().getLong("ID")
        }
        if(txnId == -1L)
        {
            originalTxn = Txn(id=-1L,who="",date=dateFormat.format(Calendar.getInstance(TimeZone.GMT_ZONE)),amount=0f,category_name = "")
            workingTxn = Txn(id=-1L,who=originalTxn.who,date=originalTxn.date,amount=originalTxn.amount,category_name = originalTxn.category_name)
        }

        updateContent()

        super.onViewCreated(view, savedInstanceState)
    }

    private fun diffFromOriginal():Boolean {
        if(originalTxn.date != workingTxn.date) {
            return true
        }
        if(originalTxn.who != workingTxn.who) {
            return true
        }
        if(originalTxn.amount != workingTxn.amount) {
            return true
        }
        if(originalTxn.category_name != workingTxn.category_name) {
            return true
        }
        return false
    }

    private fun beginSave() {
        if(!diffFromOriginal()) {
            findNavController().navigateUp()
        }
    }
    private fun setupList(t: List<Category>?) {
        val adapter = CategoryAdapter(requireContext(), R.layout.category_menu_item,t)

        (this.view?.findViewById<TextInputLayout>(R.id.menu_category)?.editText as? AutoCompleteTextView)?.setAdapter(adapter)
    }

    private fun startDatePicker() {


        val whatToSay = getString(R.string.lbl_set_pay_date)

        var defaultDate = MaterialDatePicker.todayInUtcMilliseconds()
        val currentDate = getCalendarForDate(workingTxn.date)
        if(currentDate != null) {
            defaultDate = currentDate.timeInMillis

        }
        val datePicker =
            MaterialDatePicker.Builder.datePicker()
                .setTitleText(whatToSay)
                .setInputMode(MaterialDatePicker.INPUT_MODE_TEXT)
                .setSelection(defaultDate)
                .build()
        //if the user commits to the date, get the value
        datePicker.addOnPositiveButtonClickListener {
            //the MaterialDatePicker apparently "thinks" in UTC (FLJ, 10/18/2021)
            val calendar = Calendar.getInstance(TimeZone.GMT_ZONE)
            calendar.timeInMillis =  it
            workingTxn.date = dateFormat.format(calendar)
            updateContent()
        }


        //show it!
        datePicker.show(this.childFragmentManager,"tat")
    }


    private fun getCalendarForDate(textdate:CharSequence): Calendar? {
        val outdate = Calendar.getInstance(TimeZone.GMT_ZONE)

        //now here's a neat Kotlin construct: a try-catch block can be an expression
        return try {
            outdate.time = dateFormat.parse(textdate.toString())
            outdate
        } catch (ecch:Exception) {
            null
        }
    }

    private fun friendlyDate(isodate:String):String {
        val outdate = Calendar.getInstance(TimeZone.GMT_ZONE)
        outdate.time = dateFormat.parse(isodate)
        val localFmt = SimpleDateFormat( "MMM d, yyyy", Locale.US)
        return localFmt.format(outdate)
    }
    private fun updateContent() {
        view?.findViewById<TextView>(R.id.txtWhen)?.text=friendlyDate(workingTxn.date)
    }


    class CategoryAdapter(
        context: Context,
        private val resource: Int,
        categoryList: List<Category>?
    ) :
        ArrayAdapter<Category>(context, resource, categoryList!!) {
        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            var theView = convertView
            if(theView == null) {
                theView = LayoutInflater.from(context).inflate(resource,parent,false)

            }
            //"if content == null, return theView" (FLJ, 10/3/2021)
            val content = getItem(position) ?: return theView!!


            var labelText = content.name
            if(!content.description.isNullOrEmpty())
            {
                labelText += " ("+content.description+")"
            }
            theView!!.findViewById<TextView>(R.id.txView).text = labelText


            return theView
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        val barra = (requireActivity() as MainActivity).findViewById<Toolbar>(R.id.toolbar)
        barra.menu.findItem(R.id.save).isVisible = false
    }



}