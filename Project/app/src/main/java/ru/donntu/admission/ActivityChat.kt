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

class ActivityChat : AppCompatActivity()
{
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
            Toast.makeText(applicationContext, "Отправляем сообщение", Toast.LENGTH_SHORT).show()
        }

        // -> BTN back
        findViewById<Button>(R.id.BTN_back).setOnClickListener {
            Toast.makeText(applicationContext, "Возвращаемся", Toast.LENGTH_SHORT).show()
            @Suppress("DEPRECATION")
            onBackPressed()
        }
    }
}