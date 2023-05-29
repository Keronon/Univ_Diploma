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
    companion object {
        var own = Own("", "", "", false,
                      OwnAddress("", "", "", "", "", ""),
                      OwnAddress("", "", "", "", "", ""))
    }

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

        val id = _this.findViewById<RadioGroup>(R.id.RADIO_lang).checkedRadioButtonId

        val address = OwnAddress( // address reg
            _this.findViewById<EditText>(R.id.TXT_reg_region).text.toString(),
            _this.findViewById<EditText>(R.id.TXT_reg_city).text.toString(),
            _this.findViewById<EditText>(R.id.TXT_reg_district).text.toString(),
            _this.findViewById<EditText>(R.id.TXT_reg_street).text.toString(),
            _this.findViewById<EditText>(R.id.TXT_reg_house).text.toString(),
            _this.findViewById<EditText>(R.id.TXT_reg_index).text.toString()
        )

        own = Own(
            if (id != -1) _this.findViewById<RadioButton>(id).text.toString() else "", // lang
            _this.findViewById<EditText>(R.id.TXT_country).text.toString(),
            _this.findViewById<EditText>(R.id.TXT_inn).text.toString(),
            _this.findViewById<SwitchCompat>(R.id.SWITCH_dormitory).isChecked,

            address,
            if (_this.findViewById<SwitchCompat>(R.id.SWITCH_equal).isChecked) address.copy()
            else OwnAddress( // address live
                _this.findViewById<EditText>(R.id.TXT_live_region).text.toString(),
                _this.findViewById<EditText>(R.id.TXT_live_city).text.toString(),
                _this.findViewById<EditText>(R.id.TXT_live_district).text.toString(),
                _this.findViewById<EditText>(R.id.TXT_live_street).text.toString(),
                _this.findViewById<EditText>(R.id.TXT_live_house).text.toString(),
                _this.findViewById<EditText>(R.id.TXT_live_index).text.toString()
            )
        )
    }
}