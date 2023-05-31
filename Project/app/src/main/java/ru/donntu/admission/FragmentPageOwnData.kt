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

    override fun onPause()
    {
        super.onPause()
        FragmentPageConfirm.personalData = null

        val id        = _this.findViewById<RadioGroup>(R.id.RADIO_lang).checkedRadioButtonId
        own.lang      = if (id != -1) _this.findViewById<RadioButton>(id).text.toString() else ""
        own.country   = _this.findViewById<EditText>(R.id.TXT_country).text.toString()
        own.inn       = _this.findViewById<EditText>(R.id.TXT_inn).text.toString()
        own.dormitory = _this.findViewById<SwitchCompat>(R.id.SWITCH_dormitory).isChecked

        own.address_reg.region   = _this.findViewById<EditText>(R.id.TXT_reg_region  ).text.toString()
        own.address_reg.city     = _this.findViewById<EditText>(R.id.TXT_reg_city    ).text.toString()
        own.address_reg.district = _this.findViewById<EditText>(R.id.TXT_reg_district).text.toString()
        own.address_reg.street   = _this.findViewById<EditText>(R.id.TXT_reg_street  ).text.toString()
        own.address_reg.house    = _this.findViewById<EditText>(R.id.TXT_reg_house   ).text.toString()
        own.address_reg.index    = _this.findViewById<EditText>(R.id.TXT_reg_index   ).text.toString()

        if (_this.findViewById<SwitchCompat>(R.id.SWITCH_equal).isChecked)
        {
            own.address_live.region   = own.address_reg.region
            own.address_live.city     = own.address_reg.city
            own.address_live.district = own.address_reg.district
            own.address_live.street   = own.address_reg.street
            own.address_live.house    = own.address_reg.house
            own.address_live.index    = own.address_reg.index
        }
        else {
            own.address_live.region   = _this.findViewById<EditText>(R.id.TXT_live_region  ).text.toString()
            own.address_live.city     = _this.findViewById<EditText>(R.id.TXT_live_city    ).text.toString()
            own.address_live.district = _this.findViewById<EditText>(R.id.TXT_live_district).text.toString()
            own.address_live.street   = _this.findViewById<EditText>(R.id.TXT_live_street  ).text.toString()
            own.address_live.house    = _this.findViewById<EditText>(R.id.TXT_live_house   ).text.toString()
            own.address_live.index    = _this.findViewById<EditText>(R.id.TXT_live_index   ).text.toString()
        }
    }
}