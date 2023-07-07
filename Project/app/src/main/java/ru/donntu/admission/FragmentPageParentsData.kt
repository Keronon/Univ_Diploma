package ru.donntu.admission

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText

class FragmentPageParentsData: Fragment()
{
    companion object { val parents = Parents() } // статический объект класса

    @Suppress("PropertyName")
    private lateinit var _this : View

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _this = inflater.inflate(R.layout.fragment_page_parents_data, container, false)
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

        parents.father.surname    = _this.findViewById<EditText>(R.id.TXT_father_surname   ).text.toString()
        parents.father.name       = _this.findViewById<EditText>(R.id.TXT_father_name      ).text.toString()
        parents.father.fathername = _this.findViewById<EditText>(R.id.TXT_father_fathername).text.toString()
        parents.father.phone      = _this.findViewById<EditText>(R.id.TXT_father_phone     ).text.toString()
        parents.father.reg        = _this.findViewById<EditText>(R.id.TXT_father_reg       ).text.toString()

        parents.mother.surname    = _this.findViewById<EditText>(R.id.TXT_mother_surname   ).text.toString()
        parents.mother.name       = _this.findViewById<EditText>(R.id.TXT_mother_name      ).text.toString()
        parents.mother.fathername = _this.findViewById<EditText>(R.id.TXT_mother_fathername).text.toString()
        parents.mother.phone      = _this.findViewById<EditText>(R.id.TXT_mother_phone     ).text.toString()
        parents.mother.reg        = _this.findViewById<EditText>(R.id.TXT_mother_reg       ).text.toString()
    }
}