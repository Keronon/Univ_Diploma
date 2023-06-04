package ru.donntu.admission

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import android.net.Uri
import org.apache.commons.net.ftp.FTP
import org.apache.commons.net.ftp.FTPClient
import java.io.BufferedInputStream

class ActivityPersonalData : AppCompatActivity()
{
    private lateinit var adapter: AdapterPersonalData
    private lateinit var pager: ViewPager2

    @Suppress("LocalVariableName")
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_personal_data)

        // pager

        val TABs = arrayOf("Персональные данные", "Данные о родителях", "Сканы документов", "Базисные документы", "Анкета", "Подтверждение данных")
        pager = findViewById(R.id.view_pager)

        adapter = AdapterPersonalData(this)
        pager.adapter = adapter

        TabLayoutMediator(findViewById(R.id.tabs), pager) { tab, page -> tab.text = TABs[page] }.attach()

        // buttons

        findViewById<Button>(R.id.BTN_back).setOnClickListener {
            show(applicationContext, "Возвращаемся")
            @Suppress("DEPRECATION")
            onBackPressed()
        }

        findViewById<Button>(R.id.BTN_send).setOnClickListener {
            show(applicationContext, "Отправка")

            // on not user-checked data

            if (FragmentPageConfirm.personalData == null)
            {
                show(applicationContext, "Проверьте свои данные на последней странице")
                return@setOnClickListener
            }
            val data = FragmentPageConfirm.personalData!!

            // system-check data

            // TODO проверить существование всех обязательных полей
            // TODO проверить корректность всех полей
            // TODO обработать все поля, исключив из них '

            // send to DB

            // -> own

            val query_1 = "INSERT INTO own(o_id_account,\n" +
                    "o_lang, o_country, o_inn, o_dormitory, " +
                    "o_reg_region, o_reg_city, o_reg_district, o_reg_street, o_reg_house, o_reg_index, " +
                    "o_live_region, o_live_city, o_live_district, o_live_street, o_live_house, o_live_index)\n" +
                    "VALUES ('${1}', '${data.own.lang}', '${data.own.country}', '${data.own.inn}', '${data.own.dormitory}', " +
                    "'${data.own.reg.region}', '${data.own.reg.city}', '${data.own.reg.district}', '${data.own.reg.street}', '${data.own.reg.house}', '${data.own.reg.index}', " +
                    "'${data.own.live.region}', '${data.own.live.city}', '${data.own.live.district}', '${data.own.live.street}', '${data.own.live.house}', '${data.own.live.index}');"

            // -> parents

            val query_2 = "INSERT INTO parents(p_id_account,\n" +
                    "p_f_surname, p_f_name, p_f_fathername, p_f_phone, p_f_registration, " +
                    "p_m_surname, p_m_name, p_m_fathername, p_m_phone, p_m_registration)\n" +
                    "VALUES ('${1}', '${data.parents.father.surname}', '${data.parents.father.name}', '${data.parents.father.fathername}', '${data.parents.father.phone}', '${data.parents.father.reg}', " +
                    "'${data.parents.mother.surname}', '${data.parents.mother.name}', '${data.parents.mother.fathername}', '${data.parents.mother.phone}', '${data.parents.mother.reg}');"

            // -> docs

            val query_3 = "INSERT INTO docs(d_id_account, d_files_list, d_prev_educes_list, d_priority_contract)\n" +
                    "VALUES ('${1}', '${data.docs.map { v -> v.key }.joinToString("\n")}', '${data.baseDocsInfo.prevEducations}', '${data.priority_contract}');"

            // -> base docs

            var query_4 = "INSERT INTO base_docs(b_id_account, b_educ_prog, b_base_educ, b_educ_status) VALUES\n"
            data.baseDocsInfo.baseDocs.forEach { d -> query_4 += "('${1}', '${d.op}', '${d.educ}', '${d.educ_status}'),\n" }
            query_4 = query_4.dropLast(2) + ";"

            // -> cases

            var query_5 = "INSERT INTO cases(c_id_account, c_priority, c_educ_form, c_course, c_stream) VALUES\n"
            data.cases.forEach { c -> query_5 += "('${1}', '${c.priority}', '${c.fo}', '${c.course}', '${c.stream}'),\n" }
            query_5 = query_5.dropLast(2) + ";"

            lifecycleScope.launch { show(applicationContext, withContext(Dispatchers.IO) {
                DB_processor.queryUpdate(query_1)
                DB_processor.queryUpdate(query_2)
                DB_processor.queryUpdate(query_3)
                DB_processor.queryUpdate(query_4)
                DB_processor.queryUpdate(query_5)

                "Отправлено"
            }) }

            // send to FileZilla

            val files = mutableMapOf<String, Any>()
            data.docs.forEach { v -> files[v.key] = v.value }
            lifecycleScope.launch { show(applicationContext, withContext(Dispatchers.IO) {
                try
                {
                    val ftpClient = FTPClient()
                    ftpClient.connect("192.168.0.105", 21)
                    ftpClient.login("filezilla", "123")
                    ftpClient.enterLocalPassiveMode()
                    ftpClient.setFileType(FTP.BINARY_FILE_TYPE)

                    files.forEach { v ->
                        val inputStream = BufferedInputStream(contentResolver.openInputStream(v.value as Uri))
                        ftpClient.storeFile(v.key, inputStream)
                        inputStream.close()
                    }

                    ftpClient.logout()
                    ftpClient.disconnect()
                }
                catch (e: Exception) { throw e }

                "Выполнено"
            }) }

            // finalizing

            FragmentPageOwnData.own.clear()
            FragmentPageParentsData.parents.clear()
            FragmentPageDocs.docs.clear()
            FragmentPageBaseDocs.baseDocsInfo
            FragmentPageQuestionary.cases.clear()

            finish()

            val nextPage = Intent(this, ActivityChat::class.java)
            startActivity(nextPage)
        }
    }

    override fun onDestroy()
    {
        FragmentPageConfirm.personalData = null
        super.onDestroy()
    }
}
