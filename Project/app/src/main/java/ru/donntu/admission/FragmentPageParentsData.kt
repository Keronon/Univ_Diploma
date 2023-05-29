package ru.donntu.admission

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText

class FragmentPageParentsData: Fragment()
{
    companion object {
        var father = Human("", "", "", "", "")
        var mother = Human("", "", "", "", "")
    }

    @Suppress("PropertyName")
    private lateinit var _this : View

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _this = inflater.inflate(R.layout.fragment_page_parents_data, container, false)
        return _this
    }

    override fun onPause()
    {
        super.onPause()

        father = Human(
            _this.findViewById<EditText>(R.id.TXT_father_surname).text.toString(),
            _this.findViewById<EditText>(R.id.TXT_father_name).text.toString(),
            _this.findViewById<EditText>(R.id.TXT_father_fathername).text.toString(),
            _this.findViewById<EditText>(R.id.TXT_father_phone).text.toString(),
            _this.findViewById<EditText>(R.id.TXT_father_reg).text.toString()
        )
            mother = Human(
            _this.findViewById<EditText>(R.id.TXT_mother_surname).text.toString(),
            _this.findViewById<EditText>(R.id.TXT_mother_name).text.toString(),
            _this.findViewById<EditText>(R.id.TXT_mother_fathername).text.toString(),
            _this.findViewById<EditText>(R.id.TXT_mother_phone).text.toString(),
            _this.findViewById<EditText>(R.id.TXT_mother_reg).text.toString()
        )
    }
}