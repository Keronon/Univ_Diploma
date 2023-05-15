package ru.donntu.admission

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.DatePicker
import android.widget.Toast
import java.util.*

class RegisterActivity : AppCompatActivity()
{
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        supportActionBar?.hide()

        // hooks

        val datePicker: DatePicker = findViewById(R.id.PICK_date)

        val BTN_back: Button = findViewById(R.id.BTN_back)
        val BTN_reg : Button = findViewById(R.id.BTN_reg)

        // checks

        val today = Calendar.getInstance(TimeZone.getTimeZone("GMT+3"))
        datePicker.init(today.get(Calendar.YEAR), today.get(Calendar.MONTH), today.get(Calendar.DAY_OF_MONTH)
        ) { _, year, month, day ->
            val month = month + 1
            val msg = "You Selected: $day/$month/$year"
            Toast.makeText(applicationContext, msg, Toast.LENGTH_SHORT).show()
        }

        // buttons

        BTN_back.setOnClickListener {
            Toast.makeText(applicationContext, "Возвращаемся", Toast.LENGTH_SHORT).show()
            onBackPressed()
        }
        BTN_reg.setOnClickListener {
            Toast.makeText(applicationContext, "Подтверждение регистрации", Toast.LENGTH_SHORT).show()
        }
    }
}