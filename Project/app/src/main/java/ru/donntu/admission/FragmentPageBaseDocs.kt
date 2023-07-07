package ru.donntu.admission

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.widget.SwitchCompat

@Suppress("PropertyName")
class FragmentPageBaseDocs: Fragment()
{
    companion object { var baseDocsInfo = BaseDocsInfo() } // статический объект класса

    lateinit var _this: View

    private lateinit var op  : Array<String> // названия образовательных программ
    private lateinit var educ: Array<String> // уровней образования

    @Suppress("LocalVariableName")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _this = inflater.inflate(R.layout.fragment_page_base_docs, container, false)

        op   = arrayOf(getString(R.string.bachelor), getString(R.string.expert), getString(R.string.master))
        educ = arrayOf("Ср. общ.", "Ср. проф.")

        // выпадающие списки

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

        // переключатели

        val layout_doc_2: LinearLayout = _this.findViewById(R.id.LAYOUT_doc_2)
        _this.findViewById<SwitchCompat>(R.id.SWITCH_doc_2).setOnCheckedChangeListener { _: CompoundButton, b: Boolean ->
            if (b)
                layout_doc_2.layoutParams = LinearLayout.LayoutParams(layout_doc_2.layoutParams.width, 0)
            else
                layout_doc_2.layoutParams = LinearLayout.LayoutParams(layout_doc_2.layoutParams.width, LinearLayout.LayoutParams.WRAP_CONTENT)
        }

        return _this
    }

    override fun onResume()
    {
        FragmentPageConfirm.personalData = null
        super.onResume()
    }

    // сохранение данных при остановке страницы
    override fun onPause()
    {
        super.onPause()

        val list = mutableListOf<String>()
        var check: CheckBox = _this.findViewById(R.id.CHECK_bachelor)
        if (check.isChecked) list.add(check.text.toString())
        check = _this.findViewById(R.id.CHECK_expert)
        if (check.isChecked) list.add(check.text.toString())
        check = _this.findViewById(R.id.CHECK_master)
        if (check.isChecked) list.add(check.text.toString())
        baseDocsInfo.prevEducations = list.joinToString()

        baseDocsInfo.baseDocs.clear()
        var id = _this.findViewById<RadioGroup>(R.id.RADIO_educ_1).checkedRadioButtonId
        baseDocsInfo.baseDocs.add(
            BaseDoc(
                op  [_this.findViewById<Spinner>(R.id.SPINNER_op_1  ).selectedItemPosition],
                educ[_this.findViewById<Spinner>(R.id.SPINNER_educ_1).selectedItemPosition],
                if (id != -1) _this.findViewById<RadioButton>(id).text.toString() else "" // educ status
            )
        )

        if (!_this.findViewById<SwitchCompat>(R.id.SWITCH_doc_2).isChecked)
        {
            id = _this.findViewById<RadioGroup>(R.id.RADIO_educ_2).checkedRadioButtonId
            baseDocsInfo.baseDocs.add(
                BaseDoc(
                    op  [_this.findViewById<Spinner>(R.id.SPINNER_op_2  ).selectedItemPosition],
                    educ[_this.findViewById<Spinner>(R.id.SPINNER_educ_2).selectedItemPosition],
                    if (id != -1) _this.findViewById<RadioButton>(id).text.toString() else "" // educ status
                )
            )
        }
    }
}