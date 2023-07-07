package ru.donntu.admission

import android.animation.ValueAnimator
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import android.widget.*
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class ActivityChat : AppCompatActivity()
{
    private val ids = ArrayList<Long>() // контроль подгруженных в приложение id сообщений

    @Suppress("PrivatePropertyName")
    private lateinit var TXT_docs: TextView // текстовое-поле для вывода перечня отправленных на сервер файлов
    private lateinit var animatorOpen : ValueAnimator // обработчик анимации раскрытия поля перечня файлов
    private lateinit var animatorClose: ValueAnimator // обработчик анимации закрытия поля перечня файлов

    // основная функция для класса
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        // текущее состояние заявления
        findViewById<TextView>(R.id.TXT_id    ).text = ActivityMain.account.id
        findViewById<TextView>(R.id.TXT_status).text = ActivityMain.account.status

        // buttons

        // TODO надо где-то брать анкету, как это сделано на сайте, или притягивать сюда данные, и открывать в информационной форме
        // -> BTN data
        // findViewById<Button>(R.id.BTN_data).setOnClickListener {
        //     Toast.makeText(applicationContext, "Отображаем анкету", Toast.LENGTH_SHORT).show()
        // }

        // -> toggle-BTN docs
        @Suppress("LocalVariableName")
        val BTN_docs = findViewById<ToggleButton>(R.id.BTN_docs)
        BTN_docs.setOnCheckedChangeListener { _: CompoundButton, b: Boolean ->
            if (b) // если кнопка активна
            {
                // отобразить перечень документов
                animatorOpen.start()
                BTN_docs.text = BTN_docs.textOn
            }
            else
            {
                // скрыть перечень документов
                animatorClose.start()
                BTN_docs.text = BTN_docs.textOff
            }
        }

        // нажатие на кнопку отправки сообщения
        // -> BTN send
        findViewById<Button>(R.id.BTN_send).setOnClickListener {

            // проверяем наличие текста в сообщении
            @Suppress("LocalVariableName")
            val TXT_message = findViewById<EditText>(R.id.TXT_message)
            if (TXT_message.text.toString().isEmpty()) return@setOnClickListener

            // функция для форматирования текущего времени
            fun Calendar.toTimeStr(): String {
                return "${this[Calendar.YEAR]}-${
                    if (this[Calendar.MONTH       ] > 9) this[Calendar.MONTH   ] + 1 else "0${this[Calendar.MONTH   ] + 1}" }-${
                    if (this[Calendar.DAY_OF_MONTH] > 9) this[Calendar.DAY_OF_MONTH] else "0${this[Calendar.DAY_OF_MONTH]}" } ${
                    if (this[Calendar.HOUR_OF_DAY ] > 9) this[Calendar.HOUR_OF_DAY ] else "0${this[Calendar.HOUR_OF_DAY ]}" }:${
                    if (this[Calendar.MINUTE      ] > 9) this[Calendar.MINUTE      ] else "0${this[Calendar.MINUTE      ]}" }:${
                    if (this[Calendar.SECOND      ] > 9) this[Calendar.SECOND      ] else "0${this[Calendar.SECOND      ]}" }"
            }

            val time = Calendar.getInstance().toTimeStr()

            // подготовка и отправка сообщения в виде записи в БД
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
            @Suppress("DEPRECATION")
            onBackPressed()
        }

        // список сообщений. его структурирование и отображение

        // класс-структура элемента списка
        @Suppress("PropertyName")
        class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
        {
            val TXT_title: TextView = itemView.findViewById(R.id.title)
            val TXT_body : TextView = itemView.findViewById(R.id.body)
        }

        // класс для определения данных элемента списка
        data class Item(val title: String, val body: String)
        // класс управляющий поведением списка
        class ItemAdapter(private val items: MutableList<Item>) : RecyclerView.Adapter<ItemViewHolder>() {

            override fun onCreateViewHolder(parent: android.view.ViewGroup, viewType: Int): ItemViewHolder {
                val view = android.view.LayoutInflater.from(parent.context).inflate(R.layout.recycler_item, parent, false)
                return ItemViewHolder(view)
            }

            // запись значений в видимые элементы списка
            override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
                val item = items[position]
                holder.TXT_title.text = item.title
                holder.TXT_body.text = item.body
            }

            override fun getItemCount(): Int {
                return items.size
            }
        }

        // назначение объектов списка в переменные
        val list = findViewById<RecyclerView>(R.id.VIEW_messages)
        list.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this)
        val items = mutableListOf<Item>()
        list.adapter = ItemAdapter(items)

        // таймер, обновляющий список сообщений

        @Suppress("LocalVariableName")
        TXT_docs = findViewById(R.id.TXT_docs)

        // делигируемая функция при настройке анимации текстового поля с перечнем документов
        fun setListener(layout: TextView): (ValueAnimator) -> Unit
        {
            return { animator: ValueAnimator ->
                val layoutParams = layout.layoutParams
                layoutParams.height = animator.animatedValue as Int
                layout.layoutParams = layoutParams
            }
        }

        // FIXme : контролировать ресурсы приложения при создании-удалении activity

        // обработка тика таймера
        Timer().scheduleAtFixedRate(object : TimerTask() {
            override fun run() {

                // всё, что связано с сетевыми запросами требуется выполнять в параллельном потоке
                lifecycleScope.launch { withContext(Dispatchers.IO) {

                    // обновление перечня документов, если он пуст
                    if (TXT_docs.text.isEmpty())
                    {
                        val rows =
                            DB_processor.querySelect("SELECT d_files_list FROM docs WHERE d_id_account = ${ActivityMain.account.id}")
                        var txt = ""
                        rows.forEach { r -> txt += r[0] as String }
                        TXT_docs.text = txt

                        TXT_docs.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
                        animatorOpen = ValueAnimator.ofInt(TXT_docs.measuredHeight).setDuration(500)
                        animatorOpen.addUpdateListener(setListener(TXT_docs))
                        animatorClose =
                            ValueAnimator.ofInt(TXT_docs.measuredHeight, 0).setDuration(500)
                        animatorClose.addUpdateListener(setListener(TXT_docs))
                    }

                    // обновление списка сообщений
                    var query = "SELECT m_id, a_surname, a_name, a_fathername, m_direction, m_date, m_text, m_status FROM messages\n" +
                                "INNER JOIN accounts ON a_id = m_id_account WHERE m_id_account = %s ORDER BY m_date OFFSET ${ids.size}"
                    query = query.format(ActivityMain.account.id)
                    val res = DB_processor.querySelect(query)
                    val count = ids.size
                    res.forEach { row ->
                        val sender = if (row[4] as String == "to admin")
                        {
                            var str = "${row[1]} ${row[2]}"
                            val fathername = row[3] as String
                            if (fathername.isNotEmpty()) str += " $fathername"
                            str
                        } else "оператор"
                        items.add(Item(
                            "${row[5]} [ ${row[7]} ]\n$sender\n--",
                            row[6] as String
                        ))
                        ids.add(row[0] as Long)
                    }
                    runOnUiThread { if (count != ids.size) (list.adapter as ItemAdapter).notifyItemRangeChanged(count - 1, ids.size - count) }

                } }

            }
        }, 0, 5000)
    }
}