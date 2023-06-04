package ru.donntu.admission

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.widget.SwitchCompat

class FragmentPageOwnData: Fragment()
{
    companion object { var own = Own() }

    @Suppress("PropertyName")
    private lateinit var _this : View

    @Suppress("LocalVariableName")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _this = inflater.inflate(R.layout.fragment_page_own_data, container, false)

        // check

        val layout_live: LinearLayout = _this.findViewById(R.id.LAYOUT_live_address)
        _this.findViewById<SwitchCompat>(R.id.SWITCH_equal).setOnCheckedChangeListener { _: CompoundButton, b: Boolean ->
            if (b)
                layout_live.layoutParams = LinearLayout.LayoutParams(layout_live.layoutParams.width, 0)
            else
                layout_live.layoutParams = LinearLayout.LayoutParams(layout_live.layoutParams.width, LinearLayout.LayoutParams.WRAP_CONTENT)
        }

        return _this
    }

    override fun onResume()
    {
        FragmentPageConfirm.personalData = null
        super.onResume()
    }

    override fun onPause()
    {
        super.onPause()

        val id        = _this.findViewById<RadioGroup>(R.id.RADIO_lang).checkedRadioButtonId
        own.lang      = if (id != -1) _this.findViewById<RadioButton>(id).text.toString() else ""
        own.country   = _this.findViewById<EditText>(R.id.TXT_country).text.toString()
        own.inn       = _this.findViewById<EditText>(R.id.TXT_inn).text.toString()
        own.dormitory = _this.findViewById<SwitchCompat>(R.id.SWITCH_dormitory).isChecked

        own.reg.region   = _this.findViewById<EditText>(R.id.TXT_reg_region  ).text.toString()
        own.reg.city     = _this.findViewById<EditText>(R.id.TXT_reg_city    ).text.toString()
        own.reg.district = _this.findViewById<EditText>(R.id.TXT_reg_district).text.toString()
        own.reg.street   = _this.findViewById<EditText>(R.id.TXT_reg_street  ).text.toString()
        own.reg.house    = _this.findViewById<EditText>(R.id.TXT_reg_house   ).text.toString()
        own.reg.index    = _this.findViewById<EditText>(R.id.TXT_reg_index   ).text.toString()

        if (_this.findViewById<SwitchCompat>(R.id.SWITCH_equal).isChecked)
        {
            own.live.region   = own.reg.region
            own.live.city     = own.reg.city
            own.live.district = own.reg.district
            own.live.street   = own.reg.street
            own.live.house    = own.reg.house
            own.live.index    = own.reg.index
        }
        else {
            own.live.region   = _this.findViewById<EditText>(R.id.TXT_live_region  ).text.toString()
            own.live.city     = _this.findViewById<EditText>(R.id.TXT_live_city    ).text.toString()
            own.live.district = _this.findViewById<EditText>(R.id.TXT_live_district).text.toString()
            own.live.street   = _this.findViewById<EditText>(R.id.TXT_live_street  ).text.toString()
            own.live.house    = _this.findViewById<EditText>(R.id.TXT_live_house   ).text.toString()
            own.live.index    = _this.findViewById<EditText>(R.id.TXT_live_index   ).text.toString()
        }
    }
}