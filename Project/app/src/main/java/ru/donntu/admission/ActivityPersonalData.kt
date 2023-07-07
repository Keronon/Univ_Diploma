package ru.donntu.admission

import android.app.Dialog
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
import android.widget.TextView
import java.io.FileInputStream

class ActivityPersonalData : AppCompatActivity()
{
    private lateinit var err: String // собираемая строка, содержащая список допущенных пользователем ошибок заполнения данных
    private lateinit var popErr: Dialog

    private lateinit var adapter: AdapterPersonalData // контроллер области с перелистываемыми страницами
    private lateinit var pager: ViewPager2 // сама область с перелистываемыми страницами

    @Suppress("PrivatePropertyName")
    private lateinit var TABs: Array<String> // список имён страниц
    private lateinit var data: PersonalData // переменная для хранения данных

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_personal_data)

        popErr = Dialog(this)

        initPager()
        initButtons()
    }

    override fun onDestroy()
    {
        FragmentPageConfirm.personalData = null
        super.onDestroy()
    }

    // инициализация области с перелистываемыми страницами
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
            @Suppress("DEPRECATION")
            onBackPressed()
        }

        findViewById<Button>(R.id.BTN_send).setOnClickListener(::btnSendOnClick)
    }

    private fun showPopupErr()
    {
        popErr.setContentView(R.layout.popup_err)
        popErr.findViewById<TextView>(R.id.TXT_err).text = err
        popErr.findViewById<TextView>(R.id.BTN_close).setOnClickListener{ popErr.dismiss() }
        popErr.show()
    }

    // BTN_send onClick

    private fun btnSendOnClick(@Suppress("UNUSED_PARAMETER") it: View)
    {
        err = ""

        // если пользователь не на странице с проверкой данных
        if (FragmentPageConfirm.personalData == null)
        {
            show(applicationContext, "Проверьте свои данные на вкладке '${TABs[5]}'")
            return
        }
        data = FragmentPageConfirm.personalData!!

        // проверка корректности данных
        if (!checkData())
        {
            showPopupErr()
            return
        }

        // отправка данных на файловый сервер и в БД
        val zipName = "archive.zip"
        val zipPath = "${filesDir.absolutePath}/$zipName"
        archiveFiles(data.docs, zipPath)
        val archiveName = "/${ActivityMain.account.id}.${java.util.UUID.randomUUID().toString().substring(0, 8)}.zip"
        sendToFileZilla(zipPath, archiveName)
        sendToDB(data, archiveName)

        show(applicationContext, "Отправлено")

        // finalizing

        FragmentPageOwnData    .own         .clear()
        FragmentPageParentsData.parents     .clear()
        FragmentPageDocs       .docs        .clear()
        FragmentPageBaseDocs   .baseDocsInfo.clear()
        FragmentPageQuestionary.cases       .clear()

        ActivityMain.account.status = "проверяется"
        finish()

        val nextPage = Intent(this, ActivityChat::class.java)
        startActivity(nextPage)
    }

    // -> checks

    private fun checkData(): Boolean
    {
        return checkOwn() and checkParents() and checkDocs() and checkBaseDocs() and checkCases()
    }

    private fun checkOwn(): Boolean
    {
        var normal = true

        if (data.own.lang == "") {
            err += "Пожалуйста, укажите изучаемый вами иностранный язык на вкладке '${TABs[0]}'\n\n"
            normal = false
        }

        if (data.own.country == "") {
            err += "Пожалуйста, укажите ваше гражданство на вкладке '${TABs[0]}'\n\n"
            normal = false
        }
        else data.own.country = data.own.country.replace("'", "`")

        if (data.own.inn != "") {
            if (data.own.inn.matches(Regex.fromLiteral("^\\d{8,12}$")))
                err += "Пожалуйста, укажите верный ИНН на вкладке '${TABs[0]}'\n\n"
            else
                data.own.inn = data.own.inn.replace("'", "`")
        }

        return normal and checkReg() and checkLive()
    }

    private fun checkReg(): Boolean
    {
        var normal = true

        if (data.own.reg.region == "") {
            err += "Пожалуйста, укажите область адреса регистрации на вкладке '${TABs[0]}'\n\n"
            normal = false
        }
        else data.own.reg.region = data.own.reg.region.replace("'", "`")

        if (data.own.reg.city == "") {
            err += "Пожалуйста, укажите населённый пункт адреса регистрации на вкладке '${TABs[0]}'\n\n"
            normal = false
        }
        else data.own.reg.city = data.own.reg.city.replace("'", "`")

        if (data.own.reg.district != "") { data.own.reg.district = data.own.reg.district.replace("'", "`") }

        if (data.own.reg.street == "") {
            err += "Пожалуйста, укажите улицу адреса регистрации на вкладке '${TABs[0]}'\n\n"
            normal = false
        }
        else data.own.reg.street = data.own.reg.street.replace("'", "`")

        if (data.own.reg.house == "") {
            err += "Пожалуйста, укажите дом адреса регистрации на вкладке '${TABs[0]}'\n\n"
            normal = false
        }
        else data.own.reg.house = data.own.reg.house.replace("'", "`")

        if (data.own.reg.index == "" || !Regex("^\\d{4,10}$").matches(data.own.reg.index)) {
            err += "Пожалуйста, укажите верный почтовый индекс адреса регистрации на вкладке '${TABs[0]}'\n\n"
            normal = false
        }
        else data.own.reg.index = data.own.reg.index.replace("'", "`")

        return normal
    }

    private fun checkLive(): Boolean
    {
        var normal = true

        if (data.own.live.region == "") {
            err += "Пожалуйста, укажите область адреса проживания на вкладке '${TABs[0]}'\n\n"
            normal = false
        }
        else data.own.live.region = data.own.live.region.replace("'", "`")

        if (data.own.live.city == "") {
            err += "Пожалуйста, укажите населённый пункт адреса проживания на вкладке '${TABs[0]}'\n\n"
            normal = false
        }
        else data.own.live.city = data.own.live.city.replace("'", "`")

        if (data.own.live.district != "") { data.own.live.district = data.own.live.district.replace("'", "`") }

        if (data.own.live.street == "") {
            err += "Пожалуйста, укажите улицу адреса проживания на вкладке '${TABs[0]}'\n\n"
            normal = false
        }
        else data.own.live.street = data.own.live.street.replace("'", "`")

        if (data.own.live.house == "") {
            err += "Пожалуйста, укажите дом адреса проживания на вкладке '${TABs[0]}'\n\n"
            normal = false
        }
        else data.own.live.house = data.own.live.house.replace("'", "`")

        if (data.own.live.index == "" || !Regex("^\\d{4,10}$").matches(data.own.live.index)) {
            err += "Пожалуйста, укажите верный почтовый индекс адреса проживания на вкладке '${TABs[0]}'\n\n"
            normal = false
        }
        else data.own.live.index = data.own.live.index.replace("'", "`")

        return normal
    }

    private fun checkParents(): Boolean
    {
        var normal = true

        // -> father

        if (data.parents.father.surname == "") {
            err += "Пожалуйста, укажите фамилию отца на вкладке '${TABs[1]}'\n\n"
            normal = false
        }
        else data.parents.father.surname = data.parents.father.surname.replace("'", "`")

        if (data.parents.father.name == "") {
            err += "Пожалуйста, укажите имя отца на вкладке '${TABs[1]}'\n\n"
            normal = false
        }
        else data.parents.father.name = data.parents.father.name.replace("'", "`")

        if (data.parents.father.surname != "") { data.parents.father.surname = data.parents.father.surname.replace("'", "`") }

        if (data.parents.father.phone == "") {
            err += "Пожалуйста, укажите контактный телефон отца на вкладке '${TABs[1]}'\n\n"
            normal = false
        }
        else data.parents.father.phone = data.parents.father.phone.replace("'", "`")

        if (data.parents.father.reg != "") { data.parents.father.reg = data.parents.father.reg.replace("'", "`") }

        // -> mother

        if (data.parents.mother.surname == "") {
            err += "Пожалуйста, укажите фамилию матери на вкладке '${TABs[1]}'\n\n"
            normal = false
        }
        else data.parents.mother.surname = data.parents.mother.surname.replace("'", "`")

        if (data.parents.mother.name == "") {
            err += "Пожалуйста, укажите имя матери на вкладке '${TABs[1]}'\n\n"
            normal = false
        }
        else data.parents.mother.name = data.parents.mother.name.replace("'", "`")

        if (data.parents.mother.surname != "") { data.parents.mother.surname = data.parents.mother.surname.replace("'", "`") }

        if (data.parents.mother.phone == "") {
            err += "Пожалуйста, укажите контактный телефон матери на вкладке '${TABs[1]}'\n\n"
            normal = false
        }
        else data.parents.mother.phone = data.parents.mother.phone.replace("'", "`")

        if (data.parents.mother.reg != "") { data.parents.mother.reg = data.parents.mother.reg.replace("'", "`") }

        return normal
    }

    private fun checkDocs(): Boolean
    {
        var normal = true

        if (data.docs.size < 5) {
            err += "Для передачи подготовлено недостаточно документов. Пожалуйста, ознакомьтесь со списком и загрузите все необходимые документы на вкладке '${TABs[2]}'\n\n"
            normal = false
        }
        else data.docs.forEach{ (name, _) ->
            @Suppress("LocalVariableName")
            val name_new = name.replace("'", "`")
            if (name != name_new) data.docs[name_new] = data.docs.remove(name)!!
        }

        return normal
    }

    private fun checkBaseDocs(): Boolean
    {
        var normal = true

        if (data.baseDocsInfo.baseDocs.size > 1 &&
            data.baseDocsInfo.baseDocs[0].op == data.baseDocsInfo.baseDocs[1].op)
        {
            err += "Пожалуйста, укажите разные ОП для документов на вкладке '${TABs[3]}'\n\n"
            normal = false
        }

        if (data.baseDocsInfo.baseDocs.size > 1 &&
            data.baseDocsInfo.baseDocs[0].educ == data.baseDocsInfo.baseDocs[1].educ)
        {
            err += "Пожалуйста, укажите разные базисные образования для документов на вкладке '${TABs[3]}'\n\n"
            normal = false
        }

        data.baseDocsInfo.baseDocs.forEachIndexed { i, doc ->
            if (doc.educ_status == "") {
                err += "Пожалуйста, укажите, имеется ли у вас уже образование по выбранной ОП в базисном документе №${i + 1} на вкладке '${TABs[3]}'\n\n"
                normal = false
            }
        }

        return normal
    }

    private fun checkCases(): Boolean
    {
        var normal = true

        data.cases.forEachIndexed { i, v ->
            if (v.stream.isEmpty() || v.stream == "направление подготовки не выбрано")
            {
                err += "Пожалуйста, укажите направление подготовки №${i + 1} на вкладке '${TABs[4]}'\n\n"
                normal = false
            }

            if (v.priority.isEmpty() || v.priority == "приоритет -")
            {
                err += "Пожалуйста, укажите приоритет для направления подготовки №${i + 1} на вкладке '${TABs[4]}'\n\n"
                normal = false
            }
        }

        if (data.cases.size == 2)
        {
            if (data.cases[0].stream == data.cases[1].stream)
            {
                err += "Пожалуйста, укажите разные направления подготовки на вкладке '${TABs[4]}'\n\n"
                normal = false
            }
            if (data.cases[0].priority == data.cases[1].priority)
            {
                err += "Пожалуйста, укажите разные приоритеты на вкладке '${TABs[4]}'\n\n"
                normal = false
            }
        }
        else if (data.cases.size == 3)
        {
            if (data.cases[0].stream == data.cases[1].stream ||
                data.cases[0].stream == data.cases[2].stream ||
                data.cases[1].stream == data.cases[2].stream)
            {
                err += "Пожалуйста, укажите разные направления подготовки на вкладке '${TABs[4]}'\n\n"
                normal = false
            }
            if (data.cases[0].priority == data.cases[1].priority ||
                data.cases[0].priority == data.cases[2].priority ||
                data.cases[1].priority == data.cases[2].priority)
            {
                err += "Пожалуйста, укажите разные приоритеты на вкладке '${TABs[4]}'\n\n"
                normal = false
            }
        }

        return normal
    }

    // функции отправки данных

    @Suppress("LocalVariableName")
    private fun sendToDB(data: PersonalData, archiveName: String)
    {
        // -> own
        var query_1 = "INSERT INTO own (o_id_account,\n" +
                      "o_lang, o_country, o_inn, o_dormitory, " +
                      "o_reg_region, o_reg_city, o_reg_district, o_reg_street, o_reg_house, o_reg_index, " +
                      "o_live_region, o_live_city, o_live_district, o_live_street, o_live_house, o_live_index)\n" +
                      "VALUES ('%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s');"
        query_1 = query_1.format(ActivityMain.account.id, data.own.lang, data.own.country, data.own.inn, data.own.dormitory,
                                 data.own.reg.region, data.own.reg.city, data.own.reg.district, data.own.reg.street, data.own.reg.house, data.own.reg.index,
                                 data.own.live.region, data.own.live.city, data.own.live.district, data.own.live.street, data.own.live.house, data.own.live.index)

        // -> parents
        var query_2 = "INSERT INTO parents (p_id_account,\n" +
                      "p_f_surname, p_f_name, p_f_fathername, p_f_phone, p_f_registration, " +
                      "p_m_surname, p_m_name, p_m_fathername, p_m_phone, p_m_registration)\n" +
                      "VALUES ('%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s');"
        query_2 = query_2.format(ActivityMain.account.id,
                                 data.parents.father.surname, data.parents.father.name, data.parents.father.fathername, data.parents.father.phone, data.parents.father.reg,
                                 data.parents.mother.surname, data.parents.mother.name, data.parents.mother.fathername, data.parents.mother.phone, data.parents.mother.reg)

        // -> docs
        var query_3 = "INSERT INTO docs(d_id_account, d_files_list, d_prev_educes_list, d_priority_contract)\n" +
                      "VALUES ('%s', '%s', '%s', '%s');"
        query_3 = query_3.format(ActivityMain.account.id, data.docs.map { v -> v.key }.joinToString("\n") + "\n-- в : $archiveName",
                                 data.baseDocsInfo.prevEducations, data.priority_contract)

        // -> base docs
        var query_4 = "INSERT INTO base_docs (b_id_account, b_educ_prog, b_base_educ, b_educ_status) VALUES\n"
        data.baseDocsInfo.baseDocs.forEach { d -> query_4 += "('${ActivityMain.account.id}', '${d.op}', '${d.educ}', '${d.educ_status}'),\n" }
        query_4 = query_4.dropLast(2) + ";"

        // -> cases
        var query_5 = "INSERT INTO cases (c_id_account, c_priority, c_educ_form, c_course, c_stream) VALUES\n"
        data.cases.forEach { c -> query_5 += "('${ActivityMain.account.id}', '${c.priority}', '${c.fo}', '${c.course}', '${c.stream}'),\n" }
        query_5 = query_5.dropLast(2) + ";"

        lifecycleScope.launch { withContext(Dispatchers.IO) {
            DB_processor.queryUpdate(query_1)
            DB_processor.queryUpdate(query_2)
            DB_processor.queryUpdate(query_3)
            DB_processor.queryUpdate(query_4)
            DB_processor.queryUpdate(query_5)
            DB_processor.queryUpdate("UPDATE accounts SET a_status = 'проверяется' WHERE a_id = ${ActivityMain.account.id};")
        } }
    }

    private fun sendToFileZilla(zipPath: String, archiveName: String)
    {
        val files = mutableMapOf<String, Any>()
        data.docs.forEach { v -> files[v.key] = v.value }
        lifecycleScope.launch { withContext(Dispatchers.IO) {
            try
            {
                val ftpClient = org.apache.commons.net.ftp.FTPClient()
                ftpClient.connect("10.0.2.2", 21)
                ftpClient.login("filezilla", "123")
                ftpClient.enterLocalPassiveMode()
                ftpClient.setFileType(org.apache.commons.net.ftp.FTP.BINARY_FILE_TYPE)

                FileInputStream(zipPath).use { inputStream -> ftpClient.storeFile(archiveName, inputStream) }

                ftpClient.logout()
                ftpClient.disconnect()
            }
            catch (e: Exception) { throw e }
        } }
    }

    private fun archiveFiles(files: MutableMap<String, Any>, zipPath: String) {
        val buffer = ByteArray(1024)
        java.util.zip.ZipOutputStream(
            java.io.BufferedOutputStream(java.io.FileOutputStream(zipPath))
        ).use { zipOutputStream ->
            files.forEach { (name, _uri) ->
                val uri = _uri as Uri
                val inputStream = contentResolver.openInputStream(uri)
                if (inputStream != null) {
                    java.io.BufferedInputStream(inputStream).use { bufferedInput ->
                        val entry = java.util.zip.ZipEntry(name)
                        zipOutputStream.putNextEntry(entry)
                        var len: Int
                        while (bufferedInput.read(buffer).also { len = it } > 0) {
                            zipOutputStream.write(buffer, 0, len)
                        }
                        zipOutputStream.closeEntry()
                    }
                }
            }
        }
    }
}
