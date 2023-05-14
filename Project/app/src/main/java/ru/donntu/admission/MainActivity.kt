package ru.donntu.admission

import android.app.Dialog
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.github.barteksc.pdfviewer.PDFView
import com.google.android.material.navigation.NavigationView


class MainActivity : AppCompatActivity() {

    // VARs

    private lateinit var drawer: DrawerLayout
    private lateinit var nav   : NavigationView
    private lateinit var toggle: ActionBarDrawerToggle

    private lateinit var popPlan: Dialog
    private lateinit var popInfo: Dialog

    // FUNCs

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()

        // hooks
        drawer = findViewById(R.id.LAYOUT_core)
        nav    = findViewById(R.id.VIEW_nav)
        toggle = ActionBarDrawerToggle(
            this, drawer,
            findViewById(R.id.TOOL_bar),
            R.string.toolbar_opened,
            R.string.toolbar_closed
        )

        popPlan = Dialog(this)
        popInfo = Dialog(this)

        // settings
        drawer.addDrawerListener(toggle)
        nav.bringToFront()

        // on menu clicks
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
                }
                R.id.menu_login -> {
                    Toast.makeText(applicationContext, "Начинаем вход", Toast.LENGTH_SHORT).show()
                    drawer.closeDrawer(GravityCompat.START)
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
            super.onBackPressed()
    }

    private fun showPopupPlan()
    {
        popPlan.setContentView(R.layout.plan)
        val BTN_close: TextView = popPlan.findViewById(R.id.BTN_close)
        BTN_close.setOnClickListener{ popPlan.dismiss() }
        popPlan.show()
    }

    private fun showPopupInfo()
    {
        popInfo.setContentView(R.layout.info_commis)
        val BTN_close: TextView = popInfo.findViewById(R.id.BTN_close)
        BTN_close.setOnClickListener{ popInfo.dismiss() }

        val pdfView: PDFView = popInfo.findViewById(R.id.VIEW_pdf)
        pdfView.fromAsset("PDFs/manual.pdf").load()

        popInfo.show()
    }
}