package ru.donntu.admission

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.DatePicker
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.github.barteksc.pdfviewer.PDFView
import com.google.android.material.navigation.NavigationView
import java.util.*

class ActivityMain : AppCompatActivity() {

    // VARs

    private lateinit var drawer: DrawerLayout
    private lateinit var nav   : NavigationView
    private lateinit var toggle: ActionBarDrawerToggle

    private lateinit var popPlan      : Dialog
    private lateinit var popInfo      : Dialog
    private lateinit var popReg       : Dialog
    private lateinit var popRegConfirm: Dialog
    private lateinit var popLogin     : Dialog

    // FUNCs

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // hooks

        drawer = findViewById(R.id.LAYOUT_core)
        nav    = findViewById(R.id.VIEW_nav)
        toggle = ActionBarDrawerToggle(
            this, drawer,
            findViewById(R.id.TOOL_bar),
            R.string.toolbar_opened,
            R.string.toolbar_closed
        )

        popPlan       = Dialog(this)
        popInfo       = Dialog(this)
        popReg        = Dialog(this)
        popRegConfirm = Dialog(this)
        popLogin      = Dialog(this)

        // settings

        drawer.addDrawerListener(toggle)
        nav.bringToFront()

        // menu items

        nav.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menu_plan -> {
                    Toast.makeText(applicationContext, "Отображаем план", Toast.LENGTH_SHORT).show()
                    drawer.closeDrawer(GravityCompat.START)
                    showPopupPlan()
                }
                R.id.menu_info -> {
                    Toast.makeText(applicationContext, "Отображаем информацию о комиссии", Toast.LENGTH_SHORT).show()
                    drawer.closeDrawer(GravityCompat.START)
                    showPopupInfo()
                }
                R.id.menu_register -> {
                    Toast.makeText(applicationContext, "Начинаем регистрацию", Toast.LENGTH_SHORT).show()
                    drawer.closeDrawer(GravityCompat.START)
                    showPopupReg()
                }
                R.id.menu_login -> {
                    Toast.makeText(applicationContext, "Начинаем вход", Toast.LENGTH_SHORT).show()
                    drawer.closeDrawer(GravityCompat.START)
                    showPopupLogin()
                }
            }
            true
        }
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        toggle.syncState()
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed()
    {
        if (drawer.isDrawerOpen(GravityCompat.START))
            drawer.closeDrawer(GravityCompat.START)
        else
            @Suppress("DEPRECATION")
            super.onBackPressed()
    }

    private fun showPopupPlan()
    {
        popPlan.setContentView(R.layout.popup_plan)
        popPlan.findViewById<TextView>(R.id.BTN_close).setOnClickListener{ popPlan.dismiss() }
        popPlan.show()
    }

    private fun showPopupInfo()
    {
        popInfo.setContentView(R.layout.popup_info)
        popInfo.findViewById<TextView>(R.id.BTN_close).setOnClickListener{ popInfo.dismiss() }

        popInfo.findViewById<PDFView>(R.id.VIEW_pdf).fromAsset("PDFs/manual.pdf").load()

        popInfo.show()
    }

    private fun showPopupReg()
    {
        popReg.setContentView(R.layout.popup_reg)

        // date

        val datePicker: DatePicker = popReg.findViewById(R.id.PICK_date)
        val calendar = Calendar.getInstance()

        calendar.set(calendar[Calendar.YEAR] - 16, 0, 1)
        datePicker.updateDate( calendar[Calendar.YEAR], calendar[Calendar.MONTH], calendar[Calendar.DAY_OF_MONTH] )

        calendar.set(calendar[Calendar.YEAR] + 10, 11, 31)
        datePicker.maxDate = calendar.timeInMillis

        calendar.set(1950, 0, 1)
        datePicker.minDate = calendar.timeInMillis

        // TODO : onLooseFocus check data

        // buttons

        popReg.findViewById<Button>(R.id.BTN_back).setOnClickListener {
            Toast.makeText(applicationContext, "Возвращаемся", Toast.LENGTH_SHORT).show()
            popReg.dismiss()
        }
        popReg.findViewById<Button>(R.id.BTN_reg).setOnClickListener {
            Toast.makeText(applicationContext, "Подтверждение регистрации", Toast.LENGTH_SHORT).show()
            popReg.currentFocus?.clearFocus()
            // TODO : check data
            showPopupRegConfirm(popReg)
        }

        popReg.show()
    }

    private fun showPopupRegConfirm(popReg: Dialog)
    {
        fun DatePicker.getDateString(): String {
            return "$year/${if (month > 9) month + 1 else "0" + (month + 1)}/${if (dayOfMonth > 9) dayOfMonth else "0$dayOfMonth"}"
        }

        popRegConfirm.setContentView(R.layout.popup_reg_confirm)

        popRegConfirm.findViewById<TextView>(R.id.TXT_login     ).text = popReg.findViewById<TextView>(R.id.TXT_login     ).text
        @SuppressLint("SetTextI18n")
        popRegConfirm.findViewById<TextView>(R.id.TXT_password  ).text = "символов: ${popReg.findViewById<TextView>(R.id.TXT_password).text.length}"
        popRegConfirm.findViewById<TextView>(R.id.TXT_surname   ).text = popReg.findViewById<TextView>(R.id.TXT_surname   ).text
        popRegConfirm.findViewById<TextView>(R.id.TXT_name      ).text = popReg.findViewById<TextView>(R.id.TXT_name      ).text
        popRegConfirm.findViewById<TextView>(R.id.TXT_fathername).text = popReg.findViewById<TextView>(R.id.TXT_fathername).text
        popRegConfirm.findViewById<TextView>(R.id.TXT_date      ).text = popReg.findViewById<DatePicker>(R.id.PICK_date).getDateString()
        popRegConfirm.findViewById<TextView>(R.id.TXT_email     ).text = popReg.findViewById<TextView>(R.id.TXT_email     ).text
        popRegConfirm.findViewById<TextView>(R.id.TXT_phone     ).text = popReg.findViewById<TextView>(R.id.TXT_phone     ).text
        popRegConfirm.findViewById<TextView>(R.id.TXT_other     ).text = popReg.findViewById<TextView>(R.id.TXT_other     ).text

        // buttons

        popRegConfirm.findViewById<Button>(R.id.BTN_back).setOnClickListener {
            Toast.makeText(applicationContext, "Возвращаемся", Toast.LENGTH_SHORT).show()
            popRegConfirm.dismiss()
        }
        popRegConfirm.findViewById<Button>(R.id.BTN_reg).setOnClickListener {
            Toast.makeText(applicationContext, "Регистрируем", Toast.LENGTH_SHORT).show()
            popReg.dismiss()
            popRegConfirm.dismiss()

            val nextPage = Intent(this, ActivityPersonalData::class.java)
            startActivity(nextPage)
        }

        popRegConfirm.show()
    }

    private fun showPopupLogin()
    {
        popLogin.setContentView(R.layout.popup_login)

        // buttons

        popLogin.findViewById<Button>(R.id.BTN_back).setOnClickListener {
            Toast.makeText(applicationContext, "Возвращаемся", Toast.LENGTH_SHORT).show()
            popLogin.dismiss()
        }
        popLogin.findViewById<Button>(R.id.BTN_reg).setOnClickListener {
            Toast.makeText(applicationContext, "Входим", Toast.LENGTH_SHORT).show()
            // TODO : check data
            popLogin.dismiss()
        }

        popLogin.show()
    }

}