package ru.donntu.admission

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.LinearLayout.LayoutParams
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.github.barteksc.pdfviewer.PDFView
import com.google.android.material.navigation.NavigationView
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.security.MessageDigest
import java.security.SecureRandom
import java.util.*

class ActivityMain : AppCompatActivity() {

    companion object { val account = Account() } // статический объект класса

    // VARs

    private var created = false // используется для игнорирования вызова функций до создания окна

    // хранилище записей из БД, получаемых запросом
    // [ [ 0 = id, 1 = login, 2 = password, 3 = has_own ] ]
    private lateinit var accounts: MutableList<MutableList<Any>>

    // элементы экрана
    private lateinit var drawer: DrawerLayout
    private lateinit var nav   : NavigationView
    private lateinit var toggle: ActionBarDrawerToggle

    // контролируемые формы
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
        toggle = ActionBarDrawerToggle(this, drawer, findViewById(R.id.TOOL_bar),
            R.string.toolbar_opened, R.string.toolbar_closed
        )

        popPlan       = Dialog(this)
        popInfo       = Dialog(this)
        popReg        = Dialog(this)
        popRegConfirm = Dialog(this)
        popLogin      = Dialog(this)

        // settings

        drawer.addDrawerListener(toggle)
        nav.bringToFront()

        // управление контекстным меню
        nav.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menu_plan -> {
                    drawer.closeDrawer(GravityCompat.START)
                    showPopupPlan()
                }
                R.id.menu_info -> {
                    drawer.closeDrawer(GravityCompat.START)
                    showPopupInfo()
                }
                R.id.menu_register -> {
                    drawer.closeDrawer(GravityCompat.START)
                    showPopupReg()
                }
                R.id.menu_login -> {
                    drawer.closeDrawer(GravityCompat.START)
                    showPopupLogin()
                }
            }
            true
        }

        // загрузка имеющихся аккаунтов для использования при регистрации и авторизации
        lifecycleScope.launch { show(applicationContext, withContext(Dispatchers.IO)
        {
            DB_processor.connect()
            accounts = DB_processor.querySelect("SELECT a_id, a_login, a_password, a_status FROM accounts")

            created = true
            "Добро пожаловать"
        }) }
    }

    override fun onPostCreate(savedInstanceState: Bundle?)
    {
        toggle.syncState()
        super.onPostCreate(savedInstanceState)
    }

    override fun onResume()
    {
        if (created) lifecycleScope.launch { withContext(Dispatchers.IO) {
            accounts = DB_processor.querySelect("SELECT a_id, a_login, a_password, a_status FROM accounts")
        } }
        super.onResume()
    }

    override fun onPause()
    {
        accounts.clear()
        super.onPause()
    }

    override fun onDestroy()
    {
        lifecycleScope.launch { withContext(Dispatchers.IO) { DB_processor.disconnect() } }
        super.onDestroy()
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

    // PopUps

    // форма плана
    private fun showPopupPlan()
    {
        popPlan.setContentView(R.layout.popup_plan)
        popPlan.findViewById<TextView>(R.id.BTN_close).setOnClickListener{ popPlan.dismiss() }
        popPlan.show()
    }

    // форма информации
    private fun showPopupInfo()
    {
        popInfo.setContentView(R.layout.popup_info)
        popInfo.findViewById<TextView>(R.id.BTN_close).setOnClickListener{ popInfo.dismiss() }

        popInfo.findViewById<PDFView>(R.id.VIEW_pdf).fromAsset("PDFs/manual.pdf").load()

        popInfo.show()
    }

    // форма регистрации
    @Suppress("LocalVariableName")
    private fun showPopupReg()
    {
        popReg.setContentView(R.layout.popup_reg)

        initDatePicker()

        // focus changes

        val LBL_login = popReg.findViewById<TextView>(R.id.LBL_login)
        val TXT_login = popReg.findViewById<EditText>(R.id.TXT_login)
        TXT_login.setOnFocusChangeListener{ _, focused -> if (!focused) { checkLogin(TXT_login, LBL_login) } }

        val LBL_password = popReg.findViewById<TextView>(R.id.LBL_password)
        val TXT_password = popReg.findViewById<EditText>(R.id.TXT_password)
        TXT_password.setOnFocusChangeListener{ _, focused -> if (!focused) { checkPassword(TXT_password, LBL_password) } }

        val LBL_confirm = popReg.findViewById<TextView>(R.id.LBL_confirm)
        val TXT_confirm = popReg.findViewById<EditText>(R.id.TXT_confirm)
        TXT_confirm.setOnFocusChangeListener{ _, focused -> if (!focused) { checkConfirm(TXT_confirm, LBL_confirm) } }

        val LBL_name       = popReg.findViewById<TextView>(R.id.LBL_name      )
        val TXT_surname    = popReg.findViewById<EditText>(R.id.TXT_surname   )
        val TXT_name       = popReg.findViewById<EditText>(R.id.TXT_name      )
        val TXT_fathername = popReg.findViewById<EditText>(R.id.TXT_fathername)
        TXT_surname   .setOnFocusChangeListener{ _, focused -> if (!focused) { checkNames(TXT_surname, TXT_name, TXT_fathername, LBL_name) } }
        TXT_name      .setOnFocusChangeListener{ _, focused -> if (!focused) { checkNames(TXT_surname, TXT_name, TXT_fathername, LBL_name) } }
        TXT_fathername.setOnFocusChangeListener{ _, focused -> if (!focused) { checkNames(TXT_surname, TXT_name, TXT_fathername, LBL_name) } }

        val LBL_email = popReg.findViewById<TextView>(R.id.LBL_email)
        val TXT_email = popReg.findViewById<EditText>(R.id.TXT_email)
        TXT_email.setOnFocusChangeListener{ _, focused -> if (!focused) { checkEmail(TXT_email, LBL_email) } }

        val LBL_phone = popReg.findViewById<TextView>(R.id.LBL_phone)
        val TXT_phone = popReg.findViewById<EditText>(R.id.TXT_phone)
        TXT_phone.setOnFocusChangeListener{ _, focused -> if (!focused) { checkPhone(TXT_phone, LBL_phone) } }

        // buttons

        popReg.findViewById<Button>(R.id.BTN_back).setOnClickListener {
            popReg.dismiss()
        }

        val VIEW_scroll  = popReg.findViewById<ScrollView>(R.id.VIEW_scroll)
        val LBL_info     = popReg.findViewById<TextView  >(R.id.LBL_info)
        val layoutParams = LBL_info.layoutParams
        val BTN_reg      = popReg.findViewById<Button    >(R.id.BTN_reg)
        BTN_reg.setOnClickListener {
            popReg.currentFocus?.clearFocus()

            if (!checkLogin   (TXT_login   , LBL_login   ) or
                !checkPassword(TXT_password, LBL_password) or
                !checkConfirm (TXT_confirm , LBL_confirm ) or
                !checkNames   (TXT_surname , TXT_name    , TXT_fathername, LBL_name) or
                !checkEmail   (TXT_email   , LBL_email   ) or
                !checkPhone   (TXT_phone   , LBL_phone   ) )
            {
                layoutParams.height   = LayoutParams.WRAP_CONTENT
                LBL_info.layoutParams = layoutParams
                VIEW_scroll.scrollY   = 0
            }
            else showPopupRegConfirm()
        }

        // switch

        popReg.findViewById<SwitchCompat>(R.id.SWITCH_rights).setOnCheckedChangeListener { _, checked -> BTN_reg.isEnabled = checked }

        popReg.show()
    }

    // настройка выбора даты при регистрации
    private fun initDatePicker()
    {
        val datePicker: DatePicker = popReg.findViewById(R.id.PICK_date)
        val calendar = Calendar.getInstance()

        calendar.set(calendar[Calendar.YEAR] - 16, 0, 1)
        datePicker.updateDate( calendar[Calendar.YEAR], calendar[Calendar.MONTH], calendar[Calendar.DAY_OF_MONTH] )

        calendar.set(calendar[Calendar.YEAR] + 10, 11, 31)
        datePicker.maxDate = calendar.timeInMillis

        calendar.set(1950, 0, 1)
        datePicker.minDate = calendar.timeInMillis
    }

    // функции проверки данных в полях ввода при регистрации

    private fun checkLogin(txt: EditText, lbl: TextView): Boolean
    {
        val layoutParams = lbl.layoutParams
        account.login = txt.text.toString().trim()
        return if (
            account.login.contains("'") ||
            account.login.isEmpty() ||
            accounts.find { v -> (v[1] as String) == account.login } != null
        ) {
            layoutParams.height = LayoutParams.WRAP_CONTENT
            lbl.layoutParams = layoutParams

            false
        }
        else
        {
            layoutParams.height = 0
            lbl.layoutParams = layoutParams

            true
        }
    }

    private fun checkPassword(txt: EditText, lbl: TextView): Boolean
    {
        val layoutParams = lbl.layoutParams
        account.password = txt.text.toString().trim()
        return if (
            account.password.contains("'") ||
            account.password.length < 8 || account.password.length > 20
        ) {
            layoutParams.height = LayoutParams.WRAP_CONTENT
            lbl.layoutParams = layoutParams

            false
        }
        else
        {
            layoutParams.height = 0
            lbl.layoutParams = layoutParams

            true
        }
    }

    private fun checkNames(txt_s: EditText, txt_n: EditText, txt_f: EditText, lbl: TextView): Boolean
    {
        val layoutParams = lbl.layoutParams
        account.surname    = txt_s.text.toString().trim()
        account.name       = txt_n.text.toString().trim()
        account.fathername = txt_f.text.toString().trim()
        return if (
            account.surname.contains("'") || account.name.contains("'") || account.fathername.contains("'") ||
            account.surname.isEmpty()     || account.name.isEmpty()
        ) {
            layoutParams.height = LayoutParams.WRAP_CONTENT
            lbl.layoutParams = layoutParams

            false
        }
        else
        {
            layoutParams.height = 0
            lbl.layoutParams = layoutParams

            true
        }
    }

    private fun checkConfirm(txt: EditText, lbl: TextView): Boolean
    {
        val layoutParams = lbl.layoutParams
        return if ( account.password != txt.text.toString() )
        {
            layoutParams.height = LayoutParams.WRAP_CONTENT
            lbl.layoutParams = layoutParams

            false
        }
        else
        {
            layoutParams.height = 0
            lbl.layoutParams = layoutParams

            true
        }
    }

    private fun checkEmail(txt: EditText, lbl: TextView): Boolean
    {
        val layoutParams = lbl.layoutParams
        account.email = txt.text.toString().trim()
        return if (
            account.email.contains("'") ||
            !Regex(".+@((yandex\\.((ru)|(ua)|(com)))|(gmail\\.com)|(mail\\.ru)|(rambler\\.ru)|(vk\\.com))").matches(account.email)
        ) {
            layoutParams.height = LayoutParams.WRAP_CONTENT
            lbl.layoutParams = layoutParams

            false
        }
        else
        {
            layoutParams.height = 0
            lbl.layoutParams = layoutParams

            true
        }
    }

    private fun checkPhone(txt: EditText, lbl: TextView): Boolean
    {
        val layoutParams = lbl.layoutParams
        account.phone = txt.text.toString().trim()
        return if (
            !Regex("\\+(7|380) ?\\(?\\d{2,3}\\)? ?\\d{3}-? ?\\d{2}-? ?\\d{2}").matches(account.phone)
        ) {
            layoutParams.height = LayoutParams.WRAP_CONTENT
            lbl.layoutParams = layoutParams

            false
        }
        else
        {
            layoutParams.height = 0
            lbl.layoutParams = layoutParams

            true
        }
    }

    // форма подтверждения регистрации
    private fun showPopupRegConfirm()
    {
        popRegConfirm.setContentView(R.layout.popup_reg_confirm)
        prepareRegConfirm()

        // buttons

        popRegConfirm.findViewById<Button>(R.id.BTN_back).setOnClickListener {
            popRegConfirm.dismiss()
        }
        popRegConfirm.findViewById<Button>(R.id.BTN_reg).setOnClickListener {

            // шифрования регистрируемого пароля
            val salt = generateSalt()
            val saltStr = salt.joinToString("") { "%02x".format(it) }
            account.password = "${hashPassword(account.password, salt)}::${saltStr}"

            var query = "INSERT INTO accounts (" +
                        "a_login, a_password, a_surname, a_name, a_fathername, a_birthday, a_email, a_phone, a_other, a_status)\n" +
                        "VALUES ('%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', 'зарегистрировано') RETURNING a_id;"
            query = query.format(account.login, account.password, account.surname, account.name, account.fathername,
                                 account.birthday, account.email, account.phone, account.other)
            lifecycleScope.launch { show(applicationContext, withContext(Dispatchers.IO)
            {
                account.id = (DB_processor.querySelect(query)[0][0] as Int).toString()
                account.status = "зарегистрировано"
                "Зарегистрировано"
            }) }

            popReg.dismiss()
            popRegConfirm.dismiss()

            val nextPage = Intent(this, ActivityPersonalData::class.java)
            startActivity(nextPage)
        }

        popRegConfirm.show()
    }

    // подготовка данных на форме подтверждения регистрации
    private fun prepareRegConfirm()
    {
        fun DatePicker.getDateString(): String {
            return "$year-${if (month > 9) month + 1 else "0" + (month + 1)}-${if (dayOfMonth > 9) dayOfMonth else "0$dayOfMonth"}"
        }

        account.surname    = popReg.findViewById<EditText  >(R.id.TXT_surname   ).text.toString().replace("'", "`")
        account.name       = popReg.findViewById<EditText  >(R.id.TXT_name      ).text.toString().replace("'", "`")
        account.fathername = popReg.findViewById<EditText  >(R.id.TXT_fathername).text.toString().replace("'", "`")
        account.birthday   = popReg.findViewById<DatePicker>(R.id.PICK_date     ).getDateString().replace("'", "`")
        account.other      = popReg.findViewById<EditText  >(R.id.TXT_other     ).text.toString().replace("'", "`")

        popRegConfirm.findViewById<TextView>(R.id.TXT_login     ).text = account.login
        popRegConfirm.findViewById<TextView>(R.id.TXT_password  ).text = account.password
        popRegConfirm.findViewById<TextView>(R.id.TXT_surname   ).text = account.surname
        popRegConfirm.findViewById<TextView>(R.id.TXT_name      ).text = account.name
        popRegConfirm.findViewById<TextView>(R.id.TXT_fathername).text = account.fathername
        popRegConfirm.findViewById<TextView>(R.id.TXT_date      ).text = account.birthday
        popRegConfirm.findViewById<TextView>(R.id.TXT_email     ).text = account.email
        popRegConfirm.findViewById<TextView>(R.id.TXT_phone     ).text = account.phone
        popRegConfirm.findViewById<TextView>(R.id.TXT_other     ).text = account.other
    }

    // форма авторизации
    private fun showPopupLogin()
    {
        popLogin.setContentView(R.layout.popup_login)

        // buttons

        popLogin.findViewById<Button>(R.id.BTN_back).setOnClickListener {
            popLogin.dismiss()
        }

        popLogin.findViewById<Button>(R.id.BTN_reg).setOnClickListener {
            val login = popLogin.findViewById<EditText>(R.id.TXT_login).text.toString()

            // поиск пользователя
            val user = accounts.find { v -> v[1] as String == login }
            if (user != null)
            {
                // проверка пароля
                val passwordDB = user[2] as String
                val (dbStrPasswordHash, saltStr) = passwordDB.split("::")
                val salt = saltStr.chunked(2).map { it.toInt(16).toByte() }.toByteArray()
                val sysStrPasswordHash = hashPassword(popLogin.findViewById<EditText>(R.id.TXT_password).text.toString(), salt)

                if (dbStrPasswordHash == sysStrPasswordHash)
                {
                    account.id = (user[0] as Int).toString()
                    account.login  = user[1] as String
                    account.status = user[3] as String

                    // открытие разных окон при разном статусе заявления пользователя
                    val nextPage = if (account.status == "зарегистрировано")
                         Intent(this, ActivityPersonalData::class.java)
                    else Intent(this, ActivityChat::class.java)

                    startActivity(nextPage)
                }
                else show(applicationContext, "Не верный пароль")
            }
            else show(applicationContext, "Пользователь с логином '${login}' не зарегестрирован")

            popLogin.dismiss()
        }

        popLogin.show()
    }

    // функции шифрования

    private fun hashPassword(password: String, salt: ByteArray): String {
        val bytes = password.toByteArray(Charsets.UTF_8)
        val md = MessageDigest.getInstance("SHA-256")
        md.update(salt)
        val hashBytes = md.digest(bytes)
        return hashBytes.joinToString("") { "%02x".format(it) }
    }

    private fun generateSalt(): ByteArray {
        val salt = ByteArray(32)
        val random = SecureRandom()
        random.nextBytes(salt)
        return salt
    }

}