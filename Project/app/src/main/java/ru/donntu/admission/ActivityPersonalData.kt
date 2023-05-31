package ru.donntu.admission

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator

class ActivityPersonalData : AppCompatActivity()
{
    private lateinit var adapter: AdapterPersonalData
    private lateinit var pager: ViewPager2

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_personal_data)

        // pager

        @Suppress("LocalVariableName")
        val TABs = arrayOf("Персональные данные", "Данные о родителях", "Сканы документов", "Базисные документы", "Анкета", "Подтверждение данных")
        pager = findViewById(R.id.view_pager)

        adapter = AdapterPersonalData(this)
        pager.adapter = adapter

        TabLayoutMediator(findViewById(R.id.tabs), pager) { tab, page -> tab.text = TABs[page] }.attach()

        // buttons

        findViewById<Button>(R.id.BTN_back).setOnClickListener {
            Toast.makeText(applicationContext, "Возвращаемся", Toast.LENGTH_SHORT).show()
            @Suppress("DEPRECATION")
            onBackPressed()
        }

        findViewById<Button>(R.id.BTN_send).setOnClickListener {
            Toast.makeText(applicationContext, "Отправка", Toast.LENGTH_SHORT).show()
            if (FragmentPageConfirm.personalData == null)
            {
                Toast.makeText(applicationContext, "Проверьте свои данные на последней странице", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            finish()
            FragmentPageOwnData.own.clear()
            FragmentPageParentsData.parents.clear()
            FragmentPageDocs.docs = ""
            FragmentPageBaseDocs.baseDocsInfo
            FragmentPageQuestionary.cases.clear()
            val nextPage = Intent(this, ActivityChat::class.java)
            startActivity(nextPage)
        }
    }
}
