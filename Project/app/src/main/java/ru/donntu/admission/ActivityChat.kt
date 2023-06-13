package ru.donntu.admission

import android.animation.ValueAnimator
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class ActivityChat : AppCompatActivity()
{
    private val items = ArrayList<Map<String, Any>>()
    private val ids   = ArrayList<Long>()
    private val keys  = arrayOf("message", "footer")

    @Suppress("PrivatePropertyName")
    private lateinit var TXT_docs: TextView
    private lateinit var animatorOpen : ValueAnimator
    private lateinit var animatorClose: ValueAnimator

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        findViewById<TextView>(R.id.TXT_id    ).text = ActivityMain.account.id
        findViewById<TextView>(R.id.TXT_status).text = ActivityMain.account.status

        // buttons

        // TODO надо где-то брать анкету, или притягивать сюда данные

        // -> BTN data
        findViewById<Button>(R.id.BTN_data).setOnClickListener {
            Toast.makeText(applicationContext, "Отображаем анкету", Toast.LENGTH_SHORT).show()
        }

        // -> BTN docs
        @Suppress("LocalVariableName")
        TXT_docs = findViewById(R.id.TXT_docs)

        fun setListener(layout: TextView): (ValueAnimator) -> Unit
        {
            return { animator: ValueAnimator ->
                val layoutParams = layout.layoutParams
                layoutParams.height = animator.animatedValue as Int
                layout.layoutParams = layoutParams
            }
        }

        lifecycleScope.launch { show(applicationContext, withContext(Dispatchers.IO)
        {
            TXT_docs.text = DB_processor.querySelect("SELECT d_files_list FROM docs WHERE d_id_account = ${ActivityMain.account.id}")[0][0] as String

            TXT_docs.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
            animatorOpen = ValueAnimator.ofInt(TXT_docs.measuredHeight).setDuration(500)
            animatorOpen.addUpdateListener(setListener(TXT_docs))
            animatorClose = ValueAnimator.ofInt(TXT_docs.measuredHeight, 0).setDuration(500)
            animatorClose.addUpdateListener(setListener(TXT_docs))

            "Документы загружены"
        }) }

        @Suppress("LocalVariableName")
        val BTN_docs = findViewById<ToggleButton>(R.id.BTN_docs)
        BTN_docs.setOnCheckedChangeListener { _: CompoundButton, b: Boolean ->
            if (b)
            {
                animatorOpen.start()
                BTN_docs.text = BTN_docs.textOn
            }
            else
            {
                animatorClose.start()
                BTN_docs.text = BTN_docs.textOff
            }
        }

        // -> BTN send
        findViewById<Button>(R.id.BTN_send).setOnClickListener {
            @Suppress("LocalVariableName")
            val TXT_message = findViewById<EditText>(R.id.TXT_message)
            if (TXT_message.text.toString().isEmpty()) return@setOnClickListener

            Toast.makeText(applicationContext, "Отправляем сообщение", Toast.LENGTH_SHORT).show()

            fun Calendar.toTimeStr(): String {
                return "${this[Calendar.YEAR]}-${
                    if (this[Calendar.MONTH       ] > 9) this[Calendar.MONTH   ] + 1 else "0${this[Calendar.MONTH   ] + 1}" }-${
                    if (this[Calendar.DAY_OF_MONTH] > 9) this[Calendar.DAY_OF_MONTH] else "0${this[Calendar.DAY_OF_MONTH]}" } ${
                    if (this[Calendar.HOUR_OF_DAY ] > 9) this[Calendar.HOUR_OF_DAY ] else "0${this[Calendar.HOUR_OF_DAY ]}" }:${
                    if (this[Calendar.MINUTE      ] > 9) this[Calendar.MINUTE      ] else "0${this[Calendar.MINUTE      ]}" }:${
                    if (this[Calendar.SECOND      ] > 9) this[Calendar.SECOND      ] else "0${this[Calendar.SECOND      ]}" }"
            }

            val time = Calendar.getInstance().toTimeStr()

            var query = "INSERT INTO messages (m_id_account, m_direction, m_date, m_text, m_status)\n" +
                          "VALUES ('%s', '%s', '%s', '%s', '%s');"
            query = query.format(ActivityMain.account.id, "to admin", time,
                                 TXT_message.text.toString().replace("'", "`"), "не просмотрено")
            lifecycleScope.launch { show(applicationContext, withContext(Dispatchers.IO) {
                DB_processor.queryUpdate(query)
                "Отправлено"
            }) }

            TXT_message.text.clear()
        }

        // -> BTN back
        findViewById<Button>(R.id.BTN_back).setOnClickListener {
            Toast.makeText(applicationContext, "Возвращаемся", Toast.LENGTH_SHORT).show()
            @Suppress("DEPRECATION")
            onBackPressed()
        }

        // list

        val list: ListView = findViewById(R.id.VIEW_messages)
        val listAdapter = SimpleAdapter(
            this, items, android.R.layout.simple_list_item_activated_2,
            keys, intArrayOf(android.R.id.text1, android.R.id.text2)
        )
        list.adapter = listAdapter

        // messages timer

        // FIXme : контролировать ресурсы приложения при создании-удалении activity
        Timer().scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                lifecycleScope.launch { withContext(Dispatchers.IO) {
                    var query = "SELECT m_id, a_surname, a_name, a_fathername, m_direction, m_date, m_text, m_status FROM messages\n" +
                                "INNER JOIN accounts ON a_id = m_id_account WHERE m_id_account = %s ORDER BY m_date OFFSET ${ids.size}"
                    query = query.format(ActivityMain.account.id)
                    val res = DB_processor.querySelect(query)
                    val count = ids.size
                    res.forEach { row ->
                        val item: MutableMap<String, Any> = HashMap()
                        val sender = if (row[4] as String == "to admin")
                        {
                            var str = "${row[1]} ${row[2]}"
                            val fathername = row[3] as String
                            if (fathername.isNotEmpty()) str += " $fathername"
                            str
                        } else "оператор"
                        item[keys[0]] = row[6] as String
                        item[keys[1]] = "--\n${row[5]} [ ${row[7]} ]\n--\n$sender"
                        items.add(item)
                        ids.add(row[0] as Long)
                    }
                    runOnUiThread { if (count != ids.size) listAdapter.notifyDataSetChanged() }
                } }
            }
        }, 0, 5000)
    }
}