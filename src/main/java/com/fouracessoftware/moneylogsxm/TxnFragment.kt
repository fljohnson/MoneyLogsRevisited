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
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.appcompat.widget.Toolbar
import androidx.core.widget.addTextChangedListener
import androidx.navigation.fragment.findNavController
import com.fouracessoftware.moneylogsxm.datadeal.Category
import com.fouracessoftware.moneylogsxm.datadeal.Txn
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import java.util.*
import kotlin.math.min

class TxnFragment : Fragment() {


    private var processingUpdate: Boolean = false
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



        victor.findViewById<TextInputLayout>(R.id.amount).editText?.addTextChangedListener {
            val possible = it.toString().toFloatOrNull()
            if(possible != null)
                workingTxn.amount=possible
            else
                workingTxn.amount=0f
            updateContent()
        }

        victor.findViewById<TextInputLayout>(R.id.payee).editText?.addTextChangedListener {
            workingTxn.who=it.toString()//.trim()
            updateContent()
        }
        victor.findViewById<TextInputLayout>(R.id.notes).editText?.addTextChangedListener {
            val netNote = it.toString()//.trim()
            workingTxn.notes= if(netNote.isEmpty()) {
                null
                }
                else
            {
                netNote
            }
            updateContent()
        }


        (victor.findViewById<TextInputLayout>(R.id.menu_category).editText as AutoCompleteTextView)
            .onItemClickListener = AdapterView.OnItemClickListener { parent: AdapterView<*>?, _, position: Int, _: Long ->
            workingTxn.category_name = (parent?.adapter?.getItem(position) as Category).name.trim()
            updateContent()
        }

        return victor
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
        //neat trick: if it's a new one, do the setup based on a blank Txn, otherwise do the setup AFTER we've loaded the Txn
        if(txnId == -1L)
        {
            beginEditing(null)
        }
        else {
            viewModel.getTxn(txnId).observe(viewLifecycleOwner,{
                d -> beginEditing(d)
            })
        }


        super.onViewCreated(view, savedInstanceState)
    }

    private fun beginEditing(starting:Txn?) {
        originalTxn = starting
            ?: Txn(id=-1L,who="",date=dateFormat.format(Calendar.getInstance(TimeZone.GMT_ZONE)),amount=0f,category_name = "")
        val netNotes = if(originalTxn.notes == null) {
            null
        }
        else {
            originalTxn.notes!!.trim()
        }
        workingTxn = Txn(id=originalTxn.id,who=originalTxn.who.trim(),date=originalTxn.date,amount=originalTxn.amount,category_name = originalTxn.category_name,notes = netNotes)
        updateContent()

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
        if(originalTxn.notes != workingTxn.notes) {
            return true
        }
        return false
    }

    private fun beginSave() {
        if(!diffFromOriginal()) {
            findNavController().navigateUp()
        }

        viewModel.writeTxn(workingTxn)
        viewModel.getLastError().observe(viewLifecycleOwner,
            { t -> //update UI
                if(t!="WORKING") {
                    if(t!="OK") {
                        Snackbar.make(requireView(),t,Snackbar.LENGTH_LONG).show()
                    }
                    else {
                        //we're done here
                        Snackbar.make(requireView(),"Successfully saved transaction",Snackbar.LENGTH_SHORT)
                            .show()
                        findNavController().navigateUp()
                    }

                }
            })
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
        //prevent a cascade of calls to updateContent()
        if(processingUpdate) {
            return
        }
        processingUpdate = true
        view?.findViewById<TextView>(R.id.txtWhen)?.text=friendlyDate(workingTxn.date)
        setEditTextValue(view?.findViewById<TextInputLayout>(R.id.payee)?.editText!!,workingTxn.who)
        //?.setText()
     //   view?.findViewById<TextInputLayout>(R.id.amount)?.editText?.setText(workingTxn.)
        val workingTxt = view?.findViewById<TextInputLayout>(R.id.amount)?.editText!!.text.toString()
        if(workingTxt.toFloatOrNull() != workingTxn.amount) {
            setEditTextValue(
                view?.findViewById<TextInputLayout>(R.id.amount)?.editText!!,
                workingTxn.amount.toString()
            )
        }
        (view?.findViewById<TextInputLayout>(R.id.menu_category)?.editText as? AutoCompleteTextView)?.setText(workingTxn.category_name)
        val workingNotes = if(workingTxn.notes != null) { //neat "conditional assignment" construct
            workingTxn.notes!!
        }
        else
        {
            ""
        }
        setEditTextValue(view?.findViewById<TextInputLayout>(R.id.notes)?.editText!!,workingNotes)
        //view?.findViewById<TextInputLayout>(R.id.notes)?.editText?.setText(workingNotes)
        //view?.findViewById<TextInputLayout>(R.id.notes)?.editText?


        //now, attend to the Save button
        val barra = (requireActivity() as MainActivity).findViewById<Toolbar>(R.id.toolbar)
        diffFromOriginal().also { barra.menu.findItem(R.id.save).isVisible = it }
        processingUpdate = false
    }

    private fun setEditTextValue(editText: EditText, newValue: String) {
        val priorEnd = editText.selectionEnd
        val priorStart = editText.selectionStart
        var finalEnd:Int = min(priorEnd,newValue.length)
        var finalStart:Int = finalEnd - (priorEnd-priorStart)
        editText.setText(newValue)
        editText.setSelection(finalStart,finalEnd)
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