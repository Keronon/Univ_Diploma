package ru.donntu.admission

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.widget.SwitchCompat

class FragmentPageQuestionary(private val pageBaseDocs: FragmentPageBaseDocs) : Fragment()
{
    companion object { var cases = mutableListOf<Case>() }

    @Suppress("PropertyName")
    private lateinit var _this : View

    private lateinit var popInfo : Dialog
    private lateinit var popStreamSelect : Dialog

    private lateinit var fo      : MutableList<String>
    private lateinit var course  : MutableList<String>
    private lateinit var priority: MutableList<String>
    private lateinit var docs    : MutableList<String>

    @Suppress("PrivatePropertyName")
    private lateinit var adapter_docs: ArrayAdapter<String>

    @Suppress("LocalVariableName")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _this = inflater.inflate(R.layout.fragment_page_questionary, container, false)

        // hooks

        popInfo = Dialog(_this.context)
        popStreamSelect = Dialog(_this.context)

        val BTN_stream_1: Button = _this.findViewById(R.id.BTN_stream_1)
        val BTN_stream_2: Button = _this.findViewById(R.id.BTN_stream_2)
        val BTN_stream_3: Button = _this.findViewById(R.id.BTN_stream_3)

        // docs

        docs = mutableListOf("базовый документ не выбран", "базовый документ №1")
        adapter_docs = ArrayAdapter(_this.context, android.R.layout.simple_spinner_item, docs )

        val docs_1 = _this.findViewById<Spinner>(R.id.SPINNER_docs_1)
        val docs_2 = _this.findViewById<Spinner>(R.id.SPINNER_docs_2)
        val docs_3 = _this.findViewById<Spinner>(R.id.SPINNER_docs_3)

        docs_1.adapter = adapter_docs
        docs_2.adapter = adapter_docs
        docs_3.adapter = adapter_docs

        // fo - формы обучения

        fo = mutableListOf("очное", "заочное", "заочное (смежное)", "очно-заочное", "очно-заочное (смежное)", "экстернат")
        val adapter_fo = ArrayAdapter(_this.context, android.R.layout.simple_spinner_item, fo )

        val fo_1 = _this.findViewById<Spinner>(R.id.SPINNER_fo_1)
        val fo_2 = _this.findViewById<Spinner>(R.id.SPINNER_fo_2)
        val fo_3 = _this.findViewById<Spinner>(R.id.SPINNER_fo_3)

        fo_1.adapter = adapter_fo
        fo_2.adapter = adapter_fo
        fo_3.adapter = adapter_fo

        // course

        // TODO : добавить подкурсы

        // TODO : настраивать в зависимости от базового документа
        course = mutableListOf("на 1й курс", "на 2й курс")
        val adapter_course = ArrayAdapter(_this.context, android.R.layout.simple_spinner_item, course )

        val course_1 = _this.findViewById<Spinner>(R.id.SPINNER_course_1)
        val course_2 = _this.findViewById<Spinner>(R.id.SPINNER_course_2)
        val course_3 = _this.findViewById<Spinner>(R.id.SPINNER_course_3)

        course_1.adapter = adapter_course
        course_2.adapter = adapter_course
        course_3.adapter = adapter_course

        // stream

        @Suppress("SpellCheckingInspection")
        val onBTNstreamClick = fun (it: View) {
            show(_this.context, "Отображаем выбор НО")
            // TODO : настраивать список направлений в зависимости от :
            //        базового образования
            //        формы обучения
            //        курса
            showPopupStreamSelect(it as Button)
        }

        BTN_stream_1.setOnClickListener(onBTNstreamClick)
        BTN_stream_2.setOnClickListener(onBTNstreamClick)
        BTN_stream_3.setOnClickListener(onBTNstreamClick)

        // priority

        priority = mutableListOf("приоритет -", "приоритет 1")
        val adapter_priority = ArrayAdapter(_this.context, android.R.layout.simple_spinner_item, priority )

        val priority_1 = _this.findViewById<Spinner>(R.id.SPINNER_priority_1)
        val priority_2 = _this.findViewById<Spinner>(R.id.SPINNER_priority_2)
        val priority_3 = _this.findViewById<Spinner>(R.id.SPINNER_priority_3)

        priority_1.adapter = adapter_priority
        priority_2.adapter = adapter_priority
        priority_3.adapter = adapter_priority

        // cases

        val SWITCH_case_2: SwitchCompat = _this.findViewById(R.id.SWITCH_case_2)
        val layout_case_2: LinearLayout = _this.findViewById(R.id.LAYOUT_case_2)
        val SWITCH_case_3: SwitchCompat = _this.findViewById(R.id.SWITCH_case_3)
        val layout_case_3: LinearLayout = _this.findViewById(R.id.LAYOUT_case_3)

        SWITCH_case_2.setOnCheckedChangeListener { _: CompoundButton, b: Boolean ->
            if (b)
            {
                layout_case_2.layoutParams = LinearLayout.LayoutParams(layout_case_2.layoutParams.width, LinearLayout.LayoutParams.WRAP_CONTENT)
                priority.add("приоритет 2")
                adapter_priority.notifyDataSetChanged()
            }
            else
            {
                layout_case_2.layoutParams = LinearLayout.LayoutParams(layout_case_2.layoutParams.width, 0)
                priority.remove("приоритет 2")
                adapter_priority.notifyDataSetChanged()

                BTN_stream_2.text = getString(R.string.f_0)
                docs_2.setSelection(0)
                SWITCH_case_3.isChecked = false
            }
        }

        SWITCH_case_3.setOnCheckedChangeListener { _: CompoundButton, b: Boolean ->
            if (b)
            {
                layout_case_3.layoutParams = LinearLayout.LayoutParams(layout_case_3.layoutParams.width, LinearLayout.LayoutParams.WRAP_CONTENT)
                priority.add("приоритет 3")
                adapter_priority.notifyDataSetChanged()
            }
            else
            {
                layout_case_3.layoutParams = LinearLayout.LayoutParams(layout_case_3.layoutParams.width, 0)
                priority.remove("приоритет 3")
                adapter_priority.notifyDataSetChanged()

                BTN_stream_3.text = getString(R.string.f_0)
                docs_3.setSelection(0)
            }
        }

        // other

        _this.findViewById<Button>(R.id.BTN_case_info).setOnClickListener {
            show(_this.context, "Отображаем информацию о заполнении")
            showPopupFiles()
        }

        return _this
    }

    private fun showPopupFiles()
    {
        popInfo.setContentView(R.layout.popup_info_questionary)
        popInfo.findViewById<TextView>(R.id.BTN_close).setOnClickListener{ popInfo.dismiss() }
        popInfo.show()
    }

    @Suppress("LocalVariableName")
    private fun showPopupStreamSelect(it: Button)
    {
        popStreamSelect.setContentView(R.layout.popup_stream_select)
        popStreamSelect.findViewById<TextView>(R.id.BTN_close).setOnClickListener{ popStreamSelect.dismiss() }
        
        // hooks

        @Suppress("SpellCheckingInspection")
        val SCROLLs_f: Array<HorizontalScrollView> = arrayOf(
            popStreamSelect.findViewById(R.id.SCROLL_f_1),
            popStreamSelect.findViewById(R.id.SCROLL_f_2),
            popStreamSelect.findViewById(R.id.SCROLL_f_3),
            popStreamSelect.findViewById(R.id.SCROLL_f_4),
            popStreamSelect.findViewById(R.id.SCROLL_f_5),
            popStreamSelect.findViewById(R.id.SCROLL_f_6),
            popStreamSelect.findViewById(R.id.SCROLL_f_7),
            popStreamSelect.findViewById(R.id.SCROLL_f_8)
        )

        val BTNs_f : Array<ToggleButton> = arrayOf(
            popStreamSelect.findViewById(R.id.BTN_f_1),
            popStreamSelect.findViewById(R.id.BTN_f_2),
            popStreamSelect.findViewById(R.id.BTN_f_3),
            popStreamSelect.findViewById(R.id.BTN_f_4),
            popStreamSelect.findViewById(R.id.BTN_f_5),
            popStreamSelect.findViewById(R.id.BTN_f_6),
            popStreamSelect.findViewById(R.id.BTN_f_7),
            popStreamSelect.findViewById(R.id.BTN_f_8)
        )

        val RADIO_GRs_f: Array<RadioGroup> = arrayOf(
            popStreamSelect.findViewById(R.id.RADIO_f_1),
            popStreamSelect.findViewById(R.id.RADIO_f_2),
            popStreamSelect.findViewById(R.id.RADIO_f_3),
            popStreamSelect.findViewById(R.id.RADIO_f_4),
            popStreamSelect.findViewById(R.id.RADIO_f_5),
            popStreamSelect.findViewById(R.id.RADIO_f_6),
            popStreamSelect.findViewById(R.id.RADIO_f_7),
            popStreamSelect.findViewById(R.id.RADIO_f_8)
        )

        // scrolls hiding

        fun setListener(scroll: HorizontalScrollView): (ValueAnimator) -> Unit
        {
            return { animator: ValueAnimator ->
                val layoutParams = scroll.layoutParams
                layoutParams.height = animator.animatedValue as Int
                scroll.layoutParams = layoutParams
            }
        }

        SCROLLs_f[0].measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        SCROLLs_f[1].measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        SCROLLs_f[2].measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        SCROLLs_f[3].measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        SCROLLs_f[4].measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        SCROLLs_f[5].measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        SCROLLs_f[6].measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        SCROLLs_f[7].measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)

        val animatorsOpen = arrayOf(
            ValueAnimator.ofInt(SCROLLs_f[0].measuredHeight).setDuration(500),
            ValueAnimator.ofInt(SCROLLs_f[1].measuredHeight).setDuration(500),
            ValueAnimator.ofInt(SCROLLs_f[2].measuredHeight).setDuration(500),
            ValueAnimator.ofInt(SCROLLs_f[3].measuredHeight).setDuration(500),
            ValueAnimator.ofInt(SCROLLs_f[4].measuredHeight).setDuration(500),
            ValueAnimator.ofInt(SCROLLs_f[5].measuredHeight).setDuration(500),
            ValueAnimator.ofInt(SCROLLs_f[6].measuredHeight).setDuration(500),
            ValueAnimator.ofInt(SCROLLs_f[7].measuredHeight).setDuration(500)
        )
        animatorsOpen.forEachIndexed{ i, v -> v.addUpdateListener(setListener(SCROLLs_f[i])) }
        val animatorsClose = arrayOf(
            ValueAnimator.ofInt(SCROLLs_f[0].measuredHeight, 0).setDuration(500),
            ValueAnimator.ofInt(SCROLLs_f[1].measuredHeight, 0).setDuration(500),
            ValueAnimator.ofInt(SCROLLs_f[2].measuredHeight, 0).setDuration(500),
            ValueAnimator.ofInt(SCROLLs_f[3].measuredHeight, 0).setDuration(500),
            ValueAnimator.ofInt(SCROLLs_f[4].measuredHeight, 0).setDuration(500),
            ValueAnimator.ofInt(SCROLLs_f[5].measuredHeight, 0).setDuration(500),
            ValueAnimator.ofInt(SCROLLs_f[6].measuredHeight, 0).setDuration(500),
            ValueAnimator.ofInt(SCROLLs_f[7].measuredHeight, 0).setDuration(500)
        )
        animatorsClose.forEachIndexed{ i, v -> v.addUpdateListener(setListener(SCROLLs_f[i])) }

        BTNs_f.forEachIndexed { n, btn ->
            btn.setOnCheckedChangeListener { _: CompoundButton, b: Boolean ->
                if (b)
                {
                    BTNs_f.forEachIndexed { i, v -> if (i != n) v.isChecked = false }

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
        
        // on select radio

        RADIO_GRs_f.forEachIndexed { n, f ->
            f.setOnCheckedChangeListener { view, checkedId ->
                @SuppressLint("DiscouragedApi")
                val txt_f = getString(resources.getIdentifier("f_${n + 1}", "string", _this.context.packageName))

                var parts = view.findViewById<RadioButton>(checkedId).text.split(" : ")
                parts = parts.map { v -> v.trim() }
                @SuppressLint("SetTextI18n")
                it.text = "$txt_f - ${parts[0]}\n( ${parts[1]} ) ${parts[2]}"

                popStreamSelect.dismiss()
            }
        }

        popStreamSelect.show()
    }

    override fun onResume()
    {
        FragmentPageConfirm.personalData = null
        super.onResume()

        val switch = pageBaseDocs._this.findViewById<SwitchCompat>(R.id.SWITCH_doc_2)

        if (!switch.isChecked && docs.size < 3)
        {
            docs.add("базовый документ №2")
            adapter_docs.notifyDataSetChanged()
        }
        else
        if (switch.isChecked && docs.size > 2)
        {
            docs.remove("базовый документ №2")
            adapter_docs.notifyDataSetChanged()
        }
    }

    override fun onPause()
    {
        super.onPause()

        cases.clear()
        cases.add(
            Case(
                priority[_this.findViewById<Spinner>(R.id.SPINNER_priority_1).selectedItemPosition],
                fo      [_this.findViewById<Spinner>(R.id.SPINNER_fo_1      ).selectedItemPosition],
                course  [_this.findViewById<Spinner>(R.id.SPINNER_course_1  ).selectedItemPosition],
                _this.findViewById<Button>(R.id.BTN_stream_1).text.toString() // stream
            )
        )
        if (_this.findViewById<SwitchCompat>(R.id.SWITCH_case_2).isChecked)
        {
            cases.add(
                Case(
                    priority[_this.findViewById<Spinner>(R.id.SPINNER_priority_2).selectedItemPosition],
                    fo      [_this.findViewById<Spinner>(R.id.SPINNER_fo_2      ).selectedItemPosition],
                    course  [_this.findViewById<Spinner>(R.id.SPINNER_course_2  ).selectedItemPosition],
                    _this.findViewById<Button>(R.id.BTN_stream_2).text.toString() // stream
                )
            )
        }
        if (_this.findViewById<SwitchCompat>(R.id.SWITCH_case_3).isChecked)
        {
            cases.add(
                Case(
                    priority[_this.findViewById<Spinner>(R.id.SPINNER_priority_3).selectedItemPosition],
                    fo      [_this.findViewById<Spinner>(R.id.SPINNER_fo_3      ).selectedItemPosition],
                    course  [_this.findViewById<Spinner>(R.id.SPINNER_course_3  ).selectedItemPosition],
                    _this.findViewById<Button>(R.id.BTN_stream_3).text.toString() // stream
                )
            )
        }
    }
}