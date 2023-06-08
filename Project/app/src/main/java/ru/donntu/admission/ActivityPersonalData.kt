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
import android.view.View
import org.apache.commons.net.ftp.FTP
import org.apache.commons.net.ftp.FTPClient
import java.io.BufferedInputStream

class ActivityPersonalData : AppCompatActivity()
{
    private lateinit var adapter: AdapterPersonalData
    private lateinit var pager: ViewPager2

    @Suppress("PrivatePropertyName")
    private lateinit var TABs: Array<String>
    private lateinit var data: PersonalData

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_personal_data)

        initPager()
        initButtons()
    }

    override fun onDestroy()
    {
        FragmentPageConfirm.personalData = null
        super.onDestroy()
    }

    private fun initPager()
    {
        TABs = arrayOf("Персональные данные", "Данные о родителях", "Сканы документов", "Базисные документы", "Анкета", "Подтверждение данных")
        pager = findViewById(R.id.view_pager)

        adapter = AdapterPersonalData(this)
        pager.adapter = adapter

        TabLayoutMediator(findViewById(R.id.tabs), pager) { tab, page -> tab.text = TABs[page] }.attach()
    }

    private fun initButtons()
    {
        findViewById<Button>(R.id.BTN_back).setOnClickListener {
            show(applicationContext, "Возвращаемся")
            @Suppress("DEPRECATION")
            onBackPressed()
        }

        findViewById<Button>(R.id.BTN_send).setOnClickListener(::btnSendOnClick)
    }

    // BTN_send onClick

    private fun btnSendOnClick(@Suppress("UNUSED_PARAMETER") it: View)
    {
        show(applicationContext, "Отправка")

        // on not user-checked data
        if (FragmentPageConfirm.personalData == null)
        {
            show(applicationContext, "Проверьте свои данные на вкладке '${TABs[5]}'")
            return
        }
        data = FragmentPageConfirm.personalData!!

        if (checkData()) return
        sendToDB(data)
        sendToFileZilla(data)

        // finalizing

        FragmentPageOwnData    .own         .clear()
        FragmentPageParentsData.parents     .clear()
        FragmentPageDocs       .docs        .clear()
        FragmentPageBaseDocs   .baseDocsInfo.clear()
        FragmentPageQuestionary.cases       .clear()

        finish()

        val nextPage = Intent(this, ActivityChat::class.java)
        startActivity(nextPage)
    }

    // -> checks

    private fun checkData(): Boolean
    {
        return checkOwn() && checkParents() && checkDocs() && checkBaseDocs() && checkCases()
    }

    private fun checkOwn(): Boolean
    {
        var stop = false

        if (data.own.lang == "") {
            show(applicationContext, "Пожалуйста, укажите изучаемый вами иностранный язык на вкладке '${TABs[0]}'")
            stop = true
        }

        if (data.own.country == "") {
            show(applicationContext, "Пожалуйста, укажите ваше гражданство на вкладке '${TABs[0]}'")
            stop = true
        }
        else data.own.country = data.own.country.replace("'", "`")

        if (data.own.inn != "") {
            if (data.own.inn.matches(Regex.fromLiteral("^\\d{8,12}$")))
                show(applicationContext, "Пожалуйста, укажите верный ИНН на вкладке '${TABs[0]}'")
            else
                data.own.inn = data.own.inn.replace("'", "`")
        }

        return stop && checkReg() && checkLive()
    }

    private fun checkReg(): Boolean
    {
        var stop = false

        if (data.own.reg.region == "") {
            show(applicationContext, "Пожалуйста, укажите область адреса регистрации на вкладке '${TABs[0]}'")
            stop = true
        }
        else data.own.reg.region = data.own.reg.region.replace("'", "`")

        if (data.own.reg.city == "") {
            show(applicationContext, "Пожалуйста, укажите населённый пункт адреса регистрации на вкладке '${TABs[0]}'")
            stop = true
        }
        else data.own.reg.city = data.own.reg.city.replace("'", "`")

        if (data.own.reg.district != "") { data.own.reg.district = data.own.reg.district.replace("'", "`") }

        if (data.own.reg.street == "") {
            show(applicationContext, "Пожалуйста, укажите улицу адреса регистрации на вкладке '${TABs[0]}'")
            stop = true
        }
        else data.own.reg.street = data.own.reg.street.replace("'", "`")

        if (data.own.reg.house == "") {
            show(applicationContext, "Пожалуйста, укажите дом адреса регистрации на вкладке '${TABs[0]}'")
            stop = true
        }
        else data.own.reg.house = data.own.reg.house.replace("'", "`")

        if (data.own.reg.index == "" || !data.own.reg.index.matches(Regex.fromLiteral("^\\d{4,10}$"))) {
            show(applicationContext, "Пожалуйста, укажите верный почтовый индекс адреса регистрации на вкладке '${TABs[0]}'")
            stop = true
        }
        else data.own.reg.index = data.own.reg.index.replace("'", "`")

        return stop
    }

    private fun checkLive(): Boolean
    {
        var stop = false

        if (data.own.live.region == "") {
            show(applicationContext, "Пожалуйста, укажите область адреса проживания на вкладке '${TABs[0]}'")
            stop = true
        }
        else data.own.live.region = data.own.live.region.replace("'", "`")

        if (data.own.live.city == "") {
            show(applicationContext, "Пожалуйста, укажите населённый пункт адреса проживания на вкладке '${TABs[0]}'")
            stop = true
        }
        else data.own.live.city = data.own.live.city.replace("'", "`")

        if (data.own.live.district != "") { data.own.live.district = data.own.live.district.replace("'", "`") }

        if (data.own.live.street == "") {
            show(applicationContext, "Пожалуйста, укажите улицу адреса проживания на вкладке '${TABs[0]}'")
            stop = true
        }
        else data.own.live.street = data.own.live.street.replace("'", "`")

        if (data.own.live.house == "") {
            show(applicationContext, "Пожалуйста, укажите дом адреса проживания на вкладке '${TABs[0]}'")
            stop = true
        }
        else data.own.live.house = data.own.live.house.replace("'", "`")

        if (data.own.live.index == "" || !data.own.reg.index.matches(Regex.fromLiteral("^\\d{4,10}$"))) {
            show(applicationContext, "Пожалуйста, укажите верный почтовый индекс адреса проживания на вкладке '${TABs[0]}'")
            stop = true
        }
        else data.own.live.index = data.own.live.index.replace("'", "`")

        return stop
    }

    private fun checkParents(): Boolean
    {
        var stop = false

        // -> father

        if (data.parents.father.surname == "") {
            show(applicationContext, "Пожалуйста, укажите фамилию отца на вкладке '${TABs[1]}'")
            stop = true
        }
        else data.parents.father.surname = data.parents.father.surname.replace("'", "`")

        if (data.parents.father.name == "") {
            show(applicationContext, "Пожалуйста, укажите имя отца на вкладке '${TABs[1]}'")
            stop = true
        }
        else data.parents.father.name = data.parents.father.name.replace("'", "`")

        if (data.parents.father.surname != "") { data.parents.father.surname = data.parents.father.surname.replace("'", "`") }

        if (data.parents.father.phone == "") {
            show(applicationContext, "Пожалуйста, укажите контактный телефон отца на вкладке '${TABs[1]}'")
            stop = true
        }
        else data.parents.father.phone = data.parents.father.phone.replace("'", "`")

        if (data.parents.father.reg != "") { data.parents.father.reg = data.parents.father.reg.replace("'", "`") }

        // -> mother

        if (data.parents.mother.surname == "") {
            show(applicationContext, "Пожалуйста, укажите фамилию отца на вкладке '${TABs[1]}'")
            stop = true
        }
        else data.parents.mother.surname = data.parents.mother.surname.replace("'", "`")

        if (data.parents.mother.name == "") {
            show(applicationContext, "Пожалуйста, укажите имя отца на вкладке '${TABs[1]}'")
            stop = true
        }
        else data.parents.mother.name = data.parents.mother.name.replace("'", "`")

        if (data.parents.mother.surname != "") { data.parents.mother.surname = data.parents.mother.surname.replace("'", "`") }

        if (data.parents.mother.phone == "") {
            show(applicationContext, "Пожалуйста, укажите контактный телефон отца на вкладке '${TABs[1]}'")
            stop = true
        }
        else data.parents.mother.phone = data.parents.mother.phone.replace("'", "`")

        if (data.parents.mother.reg != "") { data.parents.mother.reg = data.parents.mother.reg.replace("'", "`") }

        return stop
    }

    private fun checkDocs(): Boolean
    {
        var stop = false

        if (data.docs.size < 5) {
            show(applicationContext,
                 "Для передачи подготовлено недостаточно документов. Пожалуйста, ознакомьтесь со списком и загрузите все необходимые документы на вкладке '${TABs[2]}'")
            stop = true
        }
        else data.docs.forEach{ (name, _) ->
            @Suppress("LocalVariableName")
            val name_new = name.replace("'", "`")
            if (name != name_new) data.docs[name_new] = data.docs.remove(name)!!
        }

        return stop
    }

    private fun checkBaseDocs(): Boolean
    {
        var stop = false

        if (data.baseDocsInfo.baseDocs.size > 1 &&
            data.baseDocsInfo.baseDocs[0].op == data.baseDocsInfo.baseDocs[1].op)
        {
            show(applicationContext, "Пожалуйста, укажите разные ОП для документов на вкладке '${TABs[3]}'")
            stop = true
        }

        if (data.baseDocsInfo.baseDocs.size > 1 &&
            data.baseDocsInfo.baseDocs[0].educ == data.baseDocsInfo.baseDocs[1].educ)
        {
            show(applicationContext, "Пожалуйста, укажите разные базисные образования для документов на вкладке '${TABs[3]}'")
            stop = true
        }

        data.baseDocsInfo.baseDocs.forEachIndexed { i, doc ->
            if (doc.educ_status == "") {
                show(applicationContext, "Пожалуйста, укажите, имеется ли у вас уже образование по выбранной ОП в базисном документе №${i} на вкладке '${TABs[3]}'")
                stop = true
            }
        }

        return stop
    }

    private fun checkCases(): Boolean
    {
        var stop = false

        if (data.cases.size == 2)
        {
            if (data.cases[0].stream == data.cases[1].stream)
            {
                show(applicationContext, "Пожалуйста, укажите разные направления подготовки на вкладке '${TABs[4]}'")
                stop = true
            }
            if (data.cases[0].priority == data.cases[1].priority)
            {
                show(applicationContext, "Пожалуйста, укажите разные приоритеты на вкладке '${TABs[4]}'")
                stop = true
            }
        }
        else if (data.cases.size == 3)
        {
            if (data.cases[0].stream == data.cases[1].stream ||
                data.cases[0].stream == data.cases[2].stream ||
                data.cases[1].stream == data.cases[2].stream)
            {
                show(applicationContext, "Пожалуйста, укажите разные направления подготовки на вкладке '${TABs[4]}'")
                stop = true
            }
            if (data.cases[0].priority == data.cases[1].priority ||
                data.cases[0].priority == data.cases[2].priority ||
                data.cases[1].priority == data.cases[2].priority)
            {
                show(applicationContext, "Пожалуйста, укажите разные приоритеты на вкладке '${TABs[4]}'")
                stop = true
            }
        }

        return stop
    }

    // sends

    @Suppress("LocalVariableName")
    private fun sendToDB(data: PersonalData)
    {
        // -> own
        var query_1 = "INSERT INTO own (o_id_account,\n" +
                      "o_lang, o_country, o_inn, o_dormitory, " +
                      "o_reg_region, o_reg_city, o_reg_district, o_reg_street, o_reg_house, o_reg_index, " +
                      "o_live_region, o_live_city, o_live_district, o_live_street, o_live_house, o_live_index)\n" +
                      "VALUES ('%', '%', '%', '%', '%', '%', '%', '%', '%', '%', '%', '%', '%', '%', '%', '%', '%');"
        query_1 = query_1.format(1, data.own.lang, data.own.country, data.own.inn, data.own.dormitory,
                                 data.own.reg.region, data.own.reg.city, data.own.reg.district, data.own.reg.street, data.own.reg.house, data.own.reg.index,
                                 data.own.live.region, data.own.live.city, data.own.live.district, data.own.live.street, data.own.live.house, data.own.live.index)

        // -> parents
        var query_2 = "INSERT INTO parents (p_id_account,\n" +
                      "p_f_surname, p_f_name, p_f_fathername, p_f_phone, p_f_registration, " +
                      "p_m_surname, p_m_name, p_m_fathername, p_m_phone, p_m_registration)\n" +
                      "VALUES ('%', '%', '%', '%', '%', '%', '%', '%', '%', '%', '%');"
        query_2 = query_2.format(1,
                                 data.parents.father.surname, data.parents.father.name, data.parents.father.fathername, data.parents.father.phone, data.parents.father.reg,
                                 data.parents.mother.surname, data.parents.mother.name, data.parents.mother.fathername, data.parents.mother.phone, data.parents.mother.reg)

        // -> docs
        var query_3 = "INSERT INTO docs(d_id_account, d_files_list, d_prev_educes_list, d_priority_contract)\n" +
                      "VALUES ('%', '%', '%', '%');"
        query_3 = query_3.format(1, data.docs.map { v -> v.key }.joinToString("\n"), data.baseDocsInfo.prevEducations, data.priority_contract)

        // -> base docs
        var query_4 = "INSERT INTO base_docs (b_id_account, b_educ_prog, b_base_educ, b_educ_status) VALUES\n"
        data.baseDocsInfo.baseDocs.forEach { d -> query_4 += "('${1}', '${d.op}', '${d.educ}', '${d.educ_status}'),\n" }
        query_4 = query_4.dropLast(2) + ";"

        // -> cases
        var query_5 = "INSERT INTO cases (c_id_account, c_priority, c_educ_form, c_course, c_stream) VALUES\n"
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
    }

    private fun sendToFileZilla(data: PersonalData)
    {
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
    }
}
