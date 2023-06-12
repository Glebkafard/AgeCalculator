package com.example.agecalculator

import android.os.Bundle
import android.widget.CalendarView
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import java.util.Calendar
import java.util.Date

class MainActivity : AppCompatActivity() {

    private lateinit var infoTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        infoTextView = findViewById(R.id.infoTextView)

        val editTextDate = findViewById<EditText>(R.id.editTextDate)
        val calendarView = findViewById<CalendarView>(R.id.calendarView)

        val currentCalendar = Calendar.getInstance()
        currentCalendar.timeInMillis = startDate
        val dateText = resources.getString(
            R.string.date_format,
            currentCalendar.get(Calendar.DATE),
            currentCalendar.get(Calendar.MONTH) + 1,
            currentCalendar.get(Calendar.YEAR)
        )
        editTextDate.setText(dateText)

        editTextDate.addTextChangedListener(
            onTextChanged = { text: CharSequence?, _: Int, _: Int, _: Int ->
                val split = text.toString().split('.')
                if (split.size != 3) return@addTextChangedListener
                val date = split.map { it.toIntOrNull() ?: return@addTextChangedListener }
                val calendar = Calendar.getInstance()
                calendar.set(date[2], date[1] - 1, date[0])
                calendarView.setDate(calendar.timeInMillis, false, true)
                calculate(calendar.timeInMillis)
            }
        )

        calendarView.date = startDate
        calendarView.setOnDateChangeListener { view, year, month, dayOfMonth ->
            val calendar = Calendar.getInstance()
            calendar.set(year, month, dayOfMonth)
            val text = resources.getString(R.string.date_format, dayOfMonth, month + 1, year)
            editTextDate.setText(text)
            calculate(calendar.timeInMillis)
        }

        calculate(startDate)
    }

    private fun calculate(date: Long) {
        val between = Date().time - date
        val seconds = between / 1000
        val minutes = seconds / 60
        val hours = minutes / 60
        val days = hours / 24
        val age = age(date)
        val dayUntilBirthday = dayUntilBirthday(date)

        infoTextView.text = resources.getString(
            R.string.result_text, seconds, minutes, hours, days, age, dayUntilBirthday
        )
    }

    private fun dayUntilBirthday(date: Long): Int {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val now = calendar.timeInMillis

        calendar.timeInMillis = date
        calendar.set(Calendar.YEAR, year)
        if (calendar.timeInMillis < now) {
            calendar.add(Calendar.YEAR, 1)
        }
        return ((calendar.timeInMillis - now) / (1000 * 60 * 60 * 24)).toInt()
    }

    private fun age(date: Long): Int {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val dayOfYear = calendar.get(Calendar.DAY_OF_YEAR)

        calendar.timeInMillis = date

        val age = year - calendar.get(Calendar.YEAR)
        return if (dayOfYear < calendar.get(Calendar.DAY_OF_YEAR)) age - 1 else age
    }

    companion object {
        const val startDate = 946670400000 // 01.01.2000
    }
}