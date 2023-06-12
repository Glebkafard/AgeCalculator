package com.example.agecalculator;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private static final long startDate = 946670400000L; // 01.01.2000
    private TextView infoTextView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        infoTextView = findViewById(R.id.infoTextView);

        EditText editTextDate = findViewById(R.id.editTextDate);
        CalendarView calendarView = findViewById(R.id.calendarView);

        Calendar currentCalendar = Calendar.getInstance();
        currentCalendar.setTimeInMillis(startDate);
        String dateText = getResources().getString(
                R.string.date_format,
                currentCalendar.get(Calendar.DATE),
                currentCalendar.get(Calendar.MONTH) + 1,
                currentCalendar.get(Calendar.YEAR)
        );
        editTextDate.setText(dateText);

        editTextDate.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String[] split = s.toString().split("\\.");
                if (split.length != 3) return;

                int[] date;
                try {
                    date = Arrays.stream(split).mapToInt(Integer::parseInt).toArray();
                } catch (Exception e) {
                    return;
                }

                Calendar calendar = Calendar.getInstance();
                calendar.set(date[2], date[1] - 1, date[0]);

                calendarView.setDate(calendar.getTimeInMillis(), false, true);
                calculate(calendar.getTimeInMillis());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        calendarView.setDate(startDate);
        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            Calendar calendar = Calendar.getInstance();
            calendar.set(year, month, dayOfMonth);
            String text = getResources().getString(
                    R.string.date_format, dayOfMonth, month + 1, year
            );
            editTextDate.setText(text);
            calculate(calendar.getTimeInMillis());
        });

        calculate(startDate);
    }

    private void calculate(Long date) {
        long between = new Date().getTime() - date;
        long seconds = between / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;
        long age = age(date);
        long dayUntilBirthday = dayUntilBirthday(date);

        infoTextView.setText(getResources().getString(
                R.string.result_text, seconds, minutes, hours, days, age, dayUntilBirthday
        ));
    }

    private int dayUntilBirthday(Long date) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        long now = calendar.getTimeInMillis();

        calendar.setTimeInMillis(date);
        calendar.set(Calendar.YEAR, year);
        if (calendar.getTimeInMillis() < now) {
            calendar.add(Calendar.YEAR, 1);
        }
        return (int) ((calendar.getTimeInMillis() - now) / (1000 * 60 * 60 * 24));
    }

    private int age(Long date) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int dayOfYear = calendar.get(Calendar.DAY_OF_YEAR);

        calendar.setTimeInMillis(date);

        int age = year - calendar.get(Calendar.YEAR);
        return dayOfYear < calendar.get(Calendar.DAY_OF_YEAR) ? age - 1 : age;
    }
}
