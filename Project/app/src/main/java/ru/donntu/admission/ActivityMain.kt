package ru.donntu.admission

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.LinearLayout.LayoutParams
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

    companion object { val account = Account() }

    // VARs

    private lateinit var accounts: MutableList<MutableList<Any>> // [ [ 0 = id, 1 = login, 2 = password, 3 = has_own ] ]

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
                    show(applicationContext, "Отображаем план")
                    drawer.closeDrawer(GravityCompat.START)
                    showPopupPlan()
                }
                R.id.menu_info -> {
                    show(applicationContext, "Отображаем информацию о комиссии")
                    drawer.closeDrawer(GravityCompat.START)
                    showPopupInfo()
                }
                R.id.menu_register -> {
                    show(applicationContext, "Начинаем регистрацию")
                    drawer.closeDrawer(GravityCompat.START)
                    showPopupReg()
                }
                R.id.menu_login -> {
                    show(applicationContext, "Начинаем вход")
                    drawer.closeDrawer(GravityCompat.START)
                    showPopupLogin()
                }
            }
            true
        }

        // DB
        lifecycleScope.launch { show(applicationContext, withContext(Dispatchers.IO) { DB_processor.connect(); "БД подключена" }) }
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        toggle.syncState()
        super.onPostCreate(savedInstanceState)
    }

    override fun onResume()
    {
        lifecycleScope.launch { show(applicationContext, withContext(Dispatchers.IO) {
            accounts = DB_processor.querySelect(
"SELECT a_id, a_login, a_password, (COUNT(o_id) > 0) FROM accounts\nINNER JOIN own ON o_id_account = a_id\nGROUP BY a_id")

            "пользователи загружены"
        }) }
        super.onResume()
    }

    override fun onPause()
    {
        account.clear()
        show(applicationContext, "аккаунты очищены")
        super.onPause()
    }

    override fun onDestroy()
    {
        // DB
        lifecycleScope.launch { show(applicationContext, withContext(Dispatchers.IO) { DB_processor.disconnect(); "БД отключена" }) }
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

    @Suppress("LocalVariableName")
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

        // onLooseFocus

        // -> login

        val LBL_login = popReg.findViewById<TextView>(R.id.LBL_login)
        var layoutParams = LBL_login.layoutParams
        popReg.findViewById<EditText>(R.id.TXT_login).setOnFocusChangeListener { txt, focused -> if (!focused) {
            account.login = (txt as EditText).text.toString()
            if (
                account.login.contains("'") ||
                accounts.find { v -> (v[1] as String) == account.login } != null
            ) {
                layoutParams.height = LayoutParams.WRAP_CONTENT
                LBL_login.layoutParams = layoutParams
            }
            else
            {
                layoutParams.height = 0
                LBL_login.layoutParams = layoutParams
            }
        } }

        // -> password

        val LBL_password = popReg.findViewById<TextView>(R.id.LBL_password)
        layoutParams = LBL_password.layoutParams
        popReg.findViewById<EditText>(R.id.TXT_password).setOnFocusChangeListener { txt, focused -> if (!focused) {
            account.password = (txt as EditText).text.toString()
            if (
                account.password.contains("'") ||
                account.password.length < 8 || account.password.length > 20
            ) {
                layoutParams.height = LayoutParams.WRAP_CONTENT
                LBL_password.layoutParams = layoutParams
            }
            else
            {
                layoutParams.height = 0
                LBL_password.layoutParams = layoutParams
            }
        } }

        // -> confirm

        val LBL_confirm = popReg.findViewById<TextView>(R.id.LBL_confirm)
        layoutParams = LBL_confirm.layoutParams
        popReg.findViewById<EditText>(R.id.TXT_confirm).setOnFocusChangeListener { txt, focused -> if (!focused) {
            if ( account.password != (txt as EditText).text.toString() )
            {
                layoutParams.height = LayoutParams.WRAP_CONTENT
                LBL_confirm.layoutParams = layoutParams
            }
            else
            {
                layoutParams.height = 0
                LBL_confirm.layoutParams = layoutParams
            }
        } }

        // -> email

        val LBL_email = popReg.findViewById<TextView>(R.id.LBL_email)
        layoutParams = LBL_email.layoutParams
        popReg.findViewById<EditText>(R.id.TXT_email).setOnFocusChangeListener { txt, focused -> if (!focused) {
            account.email = (txt as EditText).text.toString()
            if (
                account.email.contains("'") ||
                !account.phone.matches(Regex.fromLiteral("^\\w*@((yandex.((ru)|(ua)|(com)))|(gmail.com)|(mail.ru)|(rambler.ru)|(vk.com))$"))
            ) {
                layoutParams.height = LayoutParams.WRAP_CONTENT
                LBL_email.layoutParams = layoutParams
            }
            else
            {
                layoutParams.height = 0
                LBL_email.layoutParams = layoutParams
            }
        } }

        // -> phone

        val LBL_phone = popReg.findViewById<TextView>(R.id.LBL_phone)
        layoutParams = LBL_phone.layoutParams
        popReg.findViewById<EditText>(R.id.TXT_phone).setOnFocusChangeListener { txt, focused -> if (!focused) {
            account.phone = (txt as EditText).text.toString()
            if (
                account.phone.contains("'") ||
                account.phone.matches(Regex.fromLiteral("^\\+(7|380) ?\\(?[0-9]{2,3}\\)? ?[0-9]{3}-? ?[0-9]{2}-? ?[0-9]{2}$"))
            ) {
                layoutParams.height = LayoutParams.WRAP_CONTENT
                LBL_phone.layoutParams = layoutParams
            }
            else
            {
                layoutParams.height = 0
                LBL_phone.layoutParams = layoutParams
            }
        } }

        // buttons

        popReg.findViewById<Button>(R.id.BTN_back).setOnClickListener {
            show(applicationContext, "Возвращаемся")
            popReg.dismiss()
        }
        val BTN_reg = popReg.findViewById<Button>(R.id.BTN_reg)
        BTN_reg.setOnClickListener {
            show(applicationContext, "Подтверждение регистрации")
            popReg.currentFocus?.clearFocus()

            if (LBL_login   .layoutParams.height != 0 ||
                LBL_password.layoutParams.height != 0 ||
                LBL_confirm .layoutParams.height != 0 ||
                LBL_email   .layoutParams.height != 0 ||
                LBL_phone   .layoutParams.height != 0 )
            {
                val LBL_info = popReg.findViewById<TextView>(R.id.LBL_info)
                layoutParams = LBL_info.layoutParams
                layoutParams.height = LayoutParams.WRAP_CONTENT
                LBL_info.layoutParams = layoutParams
            }
            else showPopupRegConfirm()
        }

        // switch

        popReg.findViewById<SwitchCompat>(R.id.SWITCH_rights).setOnCheckedChangeListener { _, checked -> BTN_reg.isEnabled = checked }

        popReg.show()
    }

    private fun showPopupRegConfirm()
    {
        fun DatePicker.getDateString(): String {
            return "$year/${if (month > 9) month + 1 else "0" + (month + 1)}/${if (dayOfMonth > 9) dayOfMonth else "0$dayOfMonth"}"
        }

        popRegConfirm.setContentView(R.layout.popup_reg_confirm)
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

        // buttons

        popRegConfirm.findViewById<Button>(R.id.BTN_back).setOnClickListener {
            show(applicationContext, "Возвращаемся")
            popRegConfirm.dismiss()
        }
        popRegConfirm.findViewById<Button>(R.id.BTN_reg).setOnClickListener {
            show(applicationContext, "Регистрируем")

            val salt = generateSalt()
            val saltStr = salt.joinToString("") { "%02x".format(it) }
            account.password = "${hashPassword(account.password, salt)}::${saltStr}"

            val query = "INSERT INTO accounts (p_id_account,\n" +
                        "a_login, a_password, a_surname, a_name, a_fathername, a_birthday, a_email, a_phone, a_other)\n" +
                        "VALUES ('${account.login}', '${account.password}', '${account.surname}', '${account.name}', '${account.fathername}', " +
                        "'${account.birthday}', '${account.email}', '${account.phone}', '${account.other}') RETURNING a_id;"
            lifecycleScope.launch { show(applicationContext, withContext(Dispatchers.IO)
            {
                account.id = (DB_processor.querySelect(query)[0][0] as Int).toString()
                "Отправлено"
            }) }

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
            show(applicationContext, "Возвращаемся")
            popLogin.dismiss()
        }

        @Suppress("LocalVariableName")
        val TXT_login = popLogin.findViewById<EditText>(R.id.TXT_login)
        popLogin.findViewById<Button>(R.id.BTN_reg).setOnClickListener {
            show(applicationContext, "Входим")

            val login = TXT_login.text.toString()
            val user = accounts.find { v -> v[1] as String == login }
            if (user != null)
            {
                val passwordDB = user[2] as String
                val (hashPasswordStr, saltStr) = passwordDB.split("::")
                val salt = saltStr.chunked(2).map { it.toInt(16).toByte() }.toByteArray()
                val password = hashPassword(account.password, salt)

                if (hashPasswordStr == password)
                {
                    account.id = (user[0] as Int).toString()

                    val nextPage = if (user[3] as Boolean) Intent(this, ActivityChat::class.java)
                                   else                    Intent(this, ActivityPersonalData::class.java)
                    startActivity(nextPage)
                }
                else show(applicationContext, "Не верный пароль")
            }
            else show(applicationContext, "Пользователь с логином '${login}' не зарегестрирован")

            popLogin.dismiss()
        }

        popLogin.show()
    }

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