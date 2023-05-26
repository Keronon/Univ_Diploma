package ru.donntu.admission

import android.app.ActionBar.LayoutParams
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.widget.SwitchCompat

class FragmentPageBaseDocs: Fragment()
{
    @Suppress("PropertyName")
    lateinit var SWITCH_doc_2: SwitchCompat

    @Suppress("LocalVariableName")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val _this = inflater.inflate(R.layout.fragment_page_base_docs, container, false)

        val op   = arrayOf(getString(R.string.bachelor), getString(R.string.expert), getString(R.string.master))
        val educ = arrayOf("Ср. общ.", "Ср. проф.")

        // spinners

        val op_1 = _this.findViewById<Spinner>(R.id.SPINNER_op_1  )
        val op_2 = _this.findViewById<Spinner>(R.id.SPINNER_op_2  )
        val adapter_op = ArrayAdapter(_this.context, android.R.layout.simple_spinner_item, op  )
        op_1.adapter   = adapter_op
        op_2.adapter   = adapter_op

        val educ_1 = _this.findViewById<Spinner>(R.id.SPINNER_educ_1)
        val educ_2 = _this.findViewById<Spinner>(R.id.SPINNER_educ_2)
        val adapter_educ = ArrayAdapter(_this.context, android.R.layout.simple_spinner_item, educ)
        educ_1.adapter   = adapter_educ
        educ_2.adapter   = adapter_educ

        // switch

        val layout_doc_2: LinearLayout = _this.findViewById(R.id.LAYOUT_doc_2)
        SWITCH_doc_2 = _this.findViewById(R.id.SWITCH_doc_2)
        SWITCH_doc_2.setOnCheckedChangeListener { _: CompoundButton, b: Boolean ->
            if (b)
                layout_doc_2.layoutParams = LinearLayout.LayoutParams(layout_doc_2.layoutParams.width, 0)
            else
                layout_doc_2.layoutParams = LinearLayout.LayoutParams(layout_doc_2.layoutParams.width, LayoutParams.WRAP_CONTENT)
        }

        return _this
    }
}