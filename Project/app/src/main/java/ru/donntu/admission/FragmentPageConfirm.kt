package ru.donntu.admission

import android.animation.ValueAnimator
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*

class FragmentPageConfirm: Fragment()
{
    @Suppress("PropertyName")
    lateinit var _this: View
    private lateinit var animatorsOpen: Array<ValueAnimator>
    private lateinit var animatorsClose: Array<ValueAnimator>
    @Suppress("PrivatePropertyName")
    private lateinit var BTNs_p: Array<ToggleButton>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _this = inflater.inflate(R.layout.fragment_page_confirm, container, false)

        // hooks

        @Suppress("SpellCheckingInspection", "LocalVariableName")
        val LAYOUTs_p: Array<LinearLayout> = arrayOf(
            _this.findViewById(R.id.LAYOUT_p_1),
            _this.findViewById(R.id.LAYOUT_p_2),
            _this.findViewById(R.id.LAYOUT_p_3),
            _this.findViewById(R.id.LAYOUT_p_4)
        )
        LAYOUTs_p[0].measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        LAYOUTs_p[1].measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        LAYOUTs_p[2].measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        LAYOUTs_p[3].measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)

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
            ValueAnimator.ofInt(LAYOUTs_p[3].measuredHeight).setDuration(500)
        )
        animatorsOpen.forEachIndexed{ i, v -> v.addUpdateListener(setListener(LAYOUTs_p[i])) }
        animatorsClose = arrayOf(
            ValueAnimator.ofInt(LAYOUTs_p[0].measuredHeight, 0).setDuration(500),
            ValueAnimator.ofInt(LAYOUTs_p[1].measuredHeight, 0).setDuration(500),
            ValueAnimator.ofInt(LAYOUTs_p[2].measuredHeight, 0).setDuration(500),
            ValueAnimator.ofInt(LAYOUTs_p[3].measuredHeight, 0).setDuration(500)
        )
        animatorsClose.forEachIndexed{ i, v -> v.addUpdateListener(setListener(LAYOUTs_p[i])) }

        @Suppress("LocalVariableName")
        BTNs_p = arrayOf(
            _this.findViewById(R.id.BTN_p_1),
            _this.findViewById(R.id.BTN_p_2),
            _this.findViewById(R.id.BTN_p_3),
            _this.findViewById(R.id.BTN_p_4)
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

        // TODO : настраивать в зависимости к-ва записей анкеты
        val priority = mutableListOf("-", "1", "2", "3")
        @Suppress("LocalVariableName")
        val adapter_priority = ArrayAdapter(_this.context, android.R.layout.simple_spinner_item, priority )
        _this.findViewById<Spinner>(R.id.SPINNER_priority).adapter = adapter_priority

        return _this
    }

    override fun onPause()
    {
        super.onPause()
        BTNs_p.forEach { v -> v.isChecked = false }
    }

    @Suppress("LocalVariableName")
    override fun onResume()
    {
        super.onResume()

        val own    = FragmentPageOwnData    .own
        val father = FragmentPageParentsData.father
        val mother = FragmentPageParentsData.mother
        val docs   = FragmentPageDocs       .docs
        val cases  = FragmentPageQuestionary.cases

        // hooks

        _this.findViewById<TextView>(R.id.TXT_lang     ).text = own.lang
        _this.findViewById<TextView>(R.id.TXT_country  ).text = own.country
        _this.findViewById<TextView>(R.id.TXT_inn      ).text = own.inn
        _this.findViewById<TextView>(R.id.TXT_dormitory).text = if (own.dormitory) "Да" else "Нет"
        // -> reg
        _this.findViewById<TextView>(R.id.TXT_reg_region  ).text = own.address_reg.region
        _this.findViewById<TextView>(R.id.TXT_reg_city    ).text = own.address_reg.city
        _this.findViewById<TextView>(R.id.TXT_reg_district).text = own.address_reg.district
        _this.findViewById<TextView>(R.id.TXT_reg_street  ).text = own.address_reg.street
        _this.findViewById<TextView>(R.id.TXT_reg_house   ).text = own.address_reg.house
        _this.findViewById<TextView>(R.id.TXT_reg_index   ).text = own.address_reg.index
        // -> live
        _this.findViewById<TextView>(R.id.TXT_live_region  ).text = own.address_live.region
        _this.findViewById<TextView>(R.id.TXT_live_city    ).text = own.address_live.city
        _this.findViewById<TextView>(R.id.TXT_live_district).text = own.address_live.district
        _this.findViewById<TextView>(R.id.TXT_live_street  ).text = own.address_live.street
        _this.findViewById<TextView>(R.id.TXT_live_house   ).text = own.address_live.house
        _this.findViewById<TextView>(R.id.TXT_live_index   ).text = own.address_live.index

        // -> father
        _this.findViewById<TextView>(R.id.TXT_father_surname   ).text = father.surname
        _this.findViewById<TextView>(R.id.TXT_father_name      ).text = father.name
        _this.findViewById<TextView>(R.id.TXT_father_fathername).text = father.fathername
        _this.findViewById<TextView>(R.id.TXT_father_phone     ).text = father.phone
        _this.findViewById<TextView>(R.id.TXT_father_reg       ).text = father.reg
        // -> mother
        _this.findViewById<TextView>(R.id.TXT_mother_surname   ).text = mother.surname
        _this.findViewById<TextView>(R.id.TXT_mother_name      ).text = mother.name
        _this.findViewById<TextView>(R.id.TXT_mother_fathername).text = mother.fathername
        _this.findViewById<TextView>(R.id.TXT_mother_phone     ).text = mother.phone
        _this.findViewById<TextView>(R.id.TXT_mother_reg       ).text = mother.reg

        val txt = _this.findViewById<TextView>(R.id.TXT_docs)
        txt.text = docs
        val param = txt.layoutParams
        param.height = txt.lineHeight * docs.lines().size + (txt.paddingTop + 5) * 2
        txt.layoutParams = param
        val lay: LinearLayout = _this.findViewById(R.id.LAYOUT_p_3)
        lay.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        animatorsOpen[2].setIntValues(lay.measuredHeight)
        animatorsClose[2].setIntValues(lay.measuredHeight, 0)

        // -> case 1
        _this.findViewById<TextView>(R.id.TXT_priority_1).text = cases[0].priority
        _this.findViewById<TextView>(R.id.TXT_fo_1      ).text = cases[0].fo
        _this.findViewById<TextView>(R.id.TXT_course_1  ).text = cases[0].course
        _this.findViewById<TextView>(R.id.TXT_stream_1  ).text = cases[0].stream
        // -> case 2
        val layout_2 = _this.findViewById<LinearLayout>(R.id.LAYOUT_case_2)
        if (cases.size > 1)
        {
            layout_2.layoutParams = LinearLayout.LayoutParams(
                layout_2.layoutParams.width,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            _this.findViewById<TextView>(R.id.TXT_priority_2).text = cases[1].priority
            _this.findViewById<TextView>(R.id.TXT_fo_2      ).text = cases[1].fo
            _this.findViewById<TextView>(R.id.TXT_course_2  ).text = cases[1].course
            _this.findViewById<TextView>(R.id.TXT_stream_2  ).text = cases[1].stream
        } else {
            layout_2.layoutParams = LinearLayout.LayoutParams(
                layout_2.layoutParams.width, 0
            )
        }
        // -> case 3
        val layout_3 = _this.findViewById<LinearLayout>(R.id.LAYOUT_case_3)
        if (cases.size > 2)
        {
            layout_3.layoutParams = LinearLayout.LayoutParams(
                layout_3.layoutParams.width,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            _this.findViewById<TextView>(R.id.TXT_priority_3).text = cases[2].priority
            _this.findViewById<TextView>(R.id.TXT_fo_3      ).text = cases[2].fo
            _this.findViewById<TextView>(R.id.TXT_course_3  ).text = cases[2].course
            _this.findViewById<TextView>(R.id.TXT_stream_3  ).text = cases[2].stream
        } else {
            layout_3.layoutParams = LinearLayout.LayoutParams(
                layout_3.layoutParams.width, 0
            )
        }

        val layout: LinearLayout = _this.findViewById(R.id.LAYOUT_p_4)
        layout.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        animatorsOpen[3].setIntValues(layout.measuredHeight)
        animatorsClose[3].setIntValues(lay.measuredHeight, 0)
    }
}