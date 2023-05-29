package ru.donntu.admission

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class ActivityPersonalData : AppCompatActivity()
{
    private lateinit var adapter  : AdapterPersonalData
    private lateinit var pager: ViewPager2
    private lateinit var tabs: TabLayout

    @Suppress("PrivatePropertyName")
    private lateinit var BTN_back : Button
    @Suppress("PrivatePropertyName")
    private lateinit var BTN_send : Button

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_personal_data)

        // hooks

        @Suppress("LocalVariableName")
        val TABs = arrayOf("Персональные данные", "Данные о родителях", "Сканы документов", "Базовые документы", "Анкета", "Подтверждение данных")
        tabs  = findViewById(R.id.tabs)
        pager = findViewById(R.id.view_pager)
        BTN_back = findViewById(R.id.BTN_back)
        BTN_send = findViewById(R.id.BTN_send)

        // pager

        adapter = AdapterPersonalData(this)
        pager.adapter = adapter

        TabLayoutMediator(tabs, pager) { tab, page -> tab.text = TABs[page] }.attach()

        // buttons

        BTN_back.setOnClickListener {
            Toast.makeText(applicationContext, "Возвращаемся", Toast.LENGTH_SHORT).show()
            @Suppress("DEPRECATION")
            onBackPressed()
        }

        BTN_send.setOnClickListener {
            Toast.makeText(applicationContext, "Отправка", Toast.LENGTH_SHORT).show()
            if (adapter.fragments[0] == null ||
                adapter.fragments[1] == null ||
                adapter.fragments[2] == null ||
                adapter.fragments[3] == null ||
                adapter.fragments[4] == null ||
                adapter.fragments[5] == null )
            {
                Toast.makeText(applicationContext, "Данные внесены не на всех страницах", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            // adapter.fragments[5].requireView().findViewById<TextView>(R.id.TXT_inn).text = "FRAG 1 : ${++click}"
        }
    }
}
