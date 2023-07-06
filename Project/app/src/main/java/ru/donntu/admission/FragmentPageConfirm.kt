package ru.donntu.admission

import android.animation.ValueAnimator
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.widget.SwitchCompat

@Suppress("PrivatePropertyName")
class FragmentPageConfirm: Fragment()
{
    companion object { var personalData: PersonalData? = null }

    private lateinit var _this: View

    private lateinit var priority_contract: MutableList<String>
    private lateinit var adapter_priority_contract: ArrayAdapter<String>

    private lateinit var animatorsOpen: Array<ValueAnimator>
    private lateinit var animatorsClose: Array<ValueAnimator>
    private lateinit var BTNs_p: Array<ToggleButton>

    @Suppress("LocalVariableName")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _this = inflater.inflate(R.layout.fragment_page_confirm, container, false)

        // hooks

        @Suppress("SpellCheckingInspection")
        val LAYOUTs_p: Array<LinearLayout> = arrayOf(
            _this.findViewById(R.id.LAYOUT_p_1),
            _this.findViewById(R.id.LAYOUT_p_2),
            _this.findViewById(R.id.LAYOUT_p_3),
            _this.findViewById(R.id.LAYOUT_p_4),
            _this.findViewById(R.id.LAYOUT_p_5)
        )
        LAYOUTs_p[0].measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        LAYOUTs_p[1].measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        LAYOUTs_p[2].measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        LAYOUTs_p[3].measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        LAYOUTs_p[4].measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)

        // Устанавливаем обновление высоты объекта в зависимости от текущего значения анимации
        fun setListener(layout: LinearLayout): (ValueAnimator) -> Unit
        {
            return { animator: ValueAnimator ->
                val layoutParams = layout.layoutParams
                layoutParams.height = animator.animatedValue as Int
                layout.layoutParams = layoutParams
            }
        }
        animatorsOpen = arrayOf(
            ValueAnimator.ofInt(LAYOUTs_p[0].measuredHeight).setDuration(500),
            ValueAnimator.ofInt(LAYOUTs_p[1].measuredHeight).setDuration(500),
            ValueAnimator.ofInt(LAYOUTs_p[2].measuredHeight).setDuration(500),
            ValueAnimator.ofInt(LAYOUTs_p[3].measuredHeight).setDuration(500),
            ValueAnimator.ofInt(LAYOUTs_p[4].measuredHeight).setDuration(500)
        )
        animatorsOpen.forEachIndexed{ i, v -> v.addUpdateListener(setListener(LAYOUTs_p[i])) }
        animatorsClose = arrayOf(
            ValueAnimator.ofInt(LAYOUTs_p[0].measuredHeight, 0).setDuration(500),
            ValueAnimator.ofInt(LAYOUTs_p[1].measuredHeight, 0).setDuration(500),
            ValueAnimator.ofInt(LAYOUTs_p[2].measuredHeight, 0).setDuration(500),
            ValueAnimator.ofInt(LAYOUTs_p[3].measuredHeight, 0).setDuration(500),
            ValueAnimator.ofInt(LAYOUTs_p[4].measuredHeight, 0).setDuration(500)
        )
        animatorsClose.forEachIndexed{ i, v -> v.addUpdateListener(setListener(LAYOUTs_p[i])) }

        BTNs_p = arrayOf(
            _this.findViewById(R.id.BTN_p_1),
            _this.findViewById(R.id.BTN_p_2),
            _this.findViewById(R.id.BTN_p_3),
            _this.findViewById(R.id.BTN_p_4),
            _this.findViewById(R.id.BTN_p_5)
        )

        BTNs_p.forEachIndexed { n, btn ->
            btn.setOnCheckedChangeListener { _: CompoundButton, b: Boolean ->
                if (b)
                {
                    BTNs_p.forEachIndexed { i, v -> if (i != n) v.isChecked = false }

                    animatorsOpen[n].start()
                    btn.text = btn.textOn
                }
                else
                {
                    animatorsClose[n].start()
                    btn.text = btn.textOff
                }
            }
        }

        // contract

        priority_contract = mutableListOf("-", "1")
        adapter_priority_contract = ArrayAdapter(_this.context, android.R.layout.simple_spinner_item, priority_contract )
        _this.findViewById<Spinner>(R.id.SPINNER_priority_contract).adapter = adapter_priority_contract

        // switches

        val SWITCH_notice_1 = _this.findViewById<SwitchCompat>(R.id.SWITCH_notice_1)
        val SWITCH_notice_2 = _this.findViewById<SwitchCompat>(R.id.SWITCH_notice_2)
        val SWITCH_notice_3 = _this.findViewById<SwitchCompat>(R.id.SWITCH_notice_3)
        val BTN_send        = requireActivity().findViewById<Button>(R.id.BTN_send)
        @Suppress("UNUSED_PARAMETER")
        fun onChecked (b: CompoundButton, ch: Boolean)
        { BTN_send.isEnabled = SWITCH_notice_1.isChecked && SWITCH_notice_2.isChecked && SWITCH_notice_3.isChecked }
        SWITCH_notice_1.setOnCheckedChangeListener(::onChecked)
        SWITCH_notice_2.setOnCheckedChangeListener(::onChecked)
        SWITCH_notice_3.setOnCheckedChangeListener(::onChecked)

        return _this
    }

    override fun onPause()
    {
        BTNs_p.forEach { v -> v.isChecked = false }
        personalData!!.priority_contract = priority_contract[_this.findViewById<Spinner>(R.id.SPINNER_priority_contract).selectedItemPosition]
        super.onPause()
    }

    @Suppress("LocalVariableName")
    override fun onResume()
    {
        personalData = PersonalData(
            FragmentPageOwnData    .own,
            FragmentPageParentsData.parents,
            FragmentPageDocs       .docs,
            FragmentPageBaseDocs   .baseDocsInfo,
            FragmentPageQuestionary.cases
        )

        // hooks

        // => Own
        _this.findViewById<TextView>(R.id.TXT_lang     ).text = personalData!!.own.lang
        _this.findViewById<TextView>(R.id.TXT_country  ).text = personalData!!.own.country
        _this.findViewById<TextView>(R.id.TXT_inn      ).text = personalData!!.own.inn
        _this.findViewById<TextView>(R.id.TXT_dormitory).text = if (personalData!!.own.dormitory) "Да" else "Нет"
        // -> reg
        _this.findViewById<TextView>(R.id.TXT_reg_region  ).text = personalData!!.own.reg.region
        _this.findViewById<TextView>(R.id.TXT_reg_city    ).text = personalData!!.own.reg.city
        _this.findViewById<TextView>(R.id.TXT_reg_district).text = personalData!!.own.reg.district
        _this.findViewById<TextView>(R.id.TXT_reg_street  ).text = personalData!!.own.reg.street
        _this.findViewById<TextView>(R.id.TXT_reg_house   ).text = personalData!!.own.reg.house
        _this.findViewById<TextView>(R.id.TXT_reg_index   ).text = personalData!!.own.reg.index
        // -> live
        _this.findViewById<TextView>(R.id.TXT_live_region  ).text = personalData!!.own.live.region
        _this.findViewById<TextView>(R.id.TXT_live_city    ).text = personalData!!.own.live.city
        _this.findViewById<TextView>(R.id.TXT_live_district).text = personalData!!.own.live.district
        _this.findViewById<TextView>(R.id.TXT_live_street  ).text = personalData!!.own.live.street
        _this.findViewById<TextView>(R.id.TXT_live_house   ).text = personalData!!.own.live.house
        _this.findViewById<TextView>(R.id.TXT_live_index   ).text = personalData!!.own.live.index

        // => Parents
        // -> father
        _this.findViewById<TextView>(R.id.TXT_father_surname   ).text = personalData!!.parents.father.surname
        _this.findViewById<TextView>(R.id.TXT_father_name      ).text = personalData!!.parents.father.name
        _this.findViewById<TextView>(R.id.TXT_father_fathername).text = personalData!!.parents.father.fathername
        _this.findViewById<TextView>(R.id.TXT_father_phone     ).text = personalData!!.parents.father.phone
        _this.findViewById<TextView>(R.id.TXT_father_reg       ).text = personalData!!.parents.father.reg
        // -> mother
        _this.findViewById<TextView>(R.id.TXT_mother_surname   ).text = personalData!!.parents.mother.surname
        _this.findViewById<TextView>(R.id.TXT_mother_name      ).text = personalData!!.parents.mother.name
        _this.findViewById<TextView>(R.id.TXT_mother_fathername).text = personalData!!.parents.mother.fathername
        _this.findViewById<TextView>(R.id.TXT_mother_phone     ).text = personalData!!.parents.mother.phone
        _this.findViewById<TextView>(R.id.TXT_mother_reg       ).text = personalData!!.parents.mother.reg

        // => Docs
        val txt = _this.findViewById<TextView>(R.id.TXT_docs)
        txt.text = personalData!!.docs.map { v -> v.key }.joinToString("\n")
        val param = txt.layoutParams
        param.height = txt.lineHeight * txt.text.lines().size + (txt.paddingTop + 5) * 2
        txt.layoutParams = param

        var layout: LinearLayout = _this.findViewById(R.id.LAYOUT_p_3)
        layout.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        animatorsOpen[2].setIntValues(layout.measuredHeight)
        animatorsClose[2].setIntValues(layout.measuredHeight, 0)

        // => Base docs
        _this.findViewById<TextView>(R.id.TXT_confirm_prev_educ).text = personalData!!.baseDocsInfo.prevEducations
        // -> doc 1
        if (personalData!!.baseDocsInfo.baseDocs.size > 0)
        {
            _this.findViewById<TextView>(R.id.TXT_op_1         ).text = personalData!!.baseDocsInfo.baseDocs[0].op
            _this.findViewById<TextView>(R.id.TXT_educ_1       ).text = personalData!!.baseDocsInfo.baseDocs[0].educ
            _this.findViewById<TextView>(R.id.TXT_educ_status_1).text = personalData!!.baseDocsInfo.baseDocs[0].educ_status
        }
        // -> doc 2
        val layout_doc_2 = _this.findViewById<LinearLayout>(R.id.LAYOUT_doc_2)
        if (personalData!!.baseDocsInfo.baseDocs.size > 1)
        {
            layout_doc_2.layoutParams = LinearLayout.LayoutParams(
                layout_doc_2.layoutParams.width,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            _this.findViewById<TextView>(R.id.TXT_op_2         ).text = personalData!!.baseDocsInfo.baseDocs[1].op
            _this.findViewById<TextView>(R.id.TXT_educ_2       ).text = personalData!!.baseDocsInfo.baseDocs[1].educ
            _this.findViewById<TextView>(R.id.TXT_educ_status_2).text = personalData!!.baseDocsInfo.baseDocs[1].educ_status
        } else {
            layout_doc_2.layoutParams = LinearLayout.LayoutParams(
                layout_doc_2.layoutParams.width, 0
            )
        }

        layout = _this.findViewById(R.id.LAYOUT_p_4)
        layout.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        animatorsOpen[3].setIntValues(layout.measuredHeight)
        animatorsClose[3].setIntValues(layout.measuredHeight, 0)

        // => Questionary
        priority_contract = mutableListOf("-", "1")
        // -> case 1
        if (personalData!!.cases.size > 0)
        {
            _this.findViewById<TextView>(R.id.TXT_priority_1).text = personalData!!.cases[0].priority
            _this.findViewById<TextView>(R.id.TXT_fo_1      ).text = personalData!!.cases[0].fo
            _this.findViewById<TextView>(R.id.TXT_course_1  ).text = personalData!!.cases[0].course
            _this.findViewById<TextView>(R.id.TXT_stream_1  ).text = personalData!!.cases[0].stream
        }
        // -> case 2
        val layout_case_2 = _this.findViewById<LinearLayout>(R.id.LAYOUT_case_2)
        if (personalData!!.cases.size > 1)
        {
            priority_contract.add("2")

            layout_case_2.layoutParams = LinearLayout.LayoutParams(
                layout_case_2.layoutParams.width,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            _this.findViewById<TextView>(R.id.TXT_priority_2).text = personalData!!.cases[1].priority
            _this.findViewById<TextView>(R.id.TXT_fo_2      ).text = personalData!!.cases[1].fo
            _this.findViewById<TextView>(R.id.TXT_course_2  ).text = personalData!!.cases[1].course
            _this.findViewById<TextView>(R.id.TXT_stream_2  ).text = personalData!!.cases[1].stream
        } else {
            layout_case_2.layoutParams = LinearLayout.LayoutParams(
                layout_case_2.layoutParams.width, 0
            )
        }
        // -> case 3
        val layout_case_3 = _this.findViewById<LinearLayout>(R.id.LAYOUT_case_3)
        if (personalData!!.cases.size > 2)
        {
            priority_contract.add("3")

            layout_case_3.layoutParams = LinearLayout.LayoutParams(
                layout_case_3.layoutParams.width,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            _this.findViewById<TextView>(R.id.TXT_priority_3).text = personalData!!.cases[2].priority
            _this.findViewById<TextView>(R.id.TXT_fo_3      ).text = personalData!!.cases[2].fo
            _this.findViewById<TextView>(R.id.TXT_course_3  ).text = personalData!!.cases[2].course
            _this.findViewById<TextView>(R.id.TXT_stream_3  ).text = personalData!!.cases[2].stream
        } else {
            layout_case_3.layoutParams = LinearLayout.LayoutParams(
                layout_case_3.layoutParams.width, 0
            )
        }

        adapter_priority_contract.notifyDataSetChanged()

        layout = _this.findViewById(R.id.LAYOUT_p_5)
        layout.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        animatorsOpen[4].setIntValues(layout.measuredHeight)
        animatorsClose[4].setIntValues(layout.measuredHeight, 0)

        super.onResume()
    }
}