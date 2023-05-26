package ru.donntu.admission

import android.app.ActionBar.LayoutParams
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.LinearLayout
import androidx.appcompat.widget.SwitchCompat

class FragmentPageOwnData: Fragment()
{
    @Suppress("LocalVariableName")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val _this = inflater.inflate(R.layout.fragment_page_own_data, container, false)

        // check

        val layout_live: LinearLayout = _this.findViewById(R.id.LAYOUT_live_address)
        _this.findViewById<SwitchCompat>(R.id.SWITCH_equal).setOnCheckedChangeListener { _: CompoundButton, b: Boolean ->
            if (b)
                layout_live.layoutParams = LinearLayout.LayoutParams(layout_live.layoutParams.width, 0)
            else
                layout_live.layoutParams = LinearLayout.LayoutParams(layout_live.layoutParams.width, LayoutParams.WRAP_CONTENT)
        }

        return _this
    }
}