package com.patui.debtcollector;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AddDebtActivity extends AppCompatActivity {

    private EditText nameInput, amountInput, timestampInput;
    private Button cancelButton, confirmButton;

    private Calendar selectedDateTime;
    private DatabaseReference dbRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_debt);

        nameInput = findViewById(R.id.name_input);
        amountInput = findViewById(R.id.amount_input);
        timestampInput = findViewById(R.id.timestamp_input);
        cancelButton = findViewById(R.id.cancel_button);
        confirmButton = findViewById(R.id.confirm_button);

        selectedDateTime = Calendar.getInstance();
        timestampInput.setOnClickListener(v -> showDatePicker());

        dbRef = FirebaseDatabase.getInstance().getReference().child("ongoingDebts");

        cancelButton.setOnClickListener(v -> startActivity(new Intent(AddDebtActivity.this, MainActivity.class)));

        confirmButton.setOnClickListener(v -> {
            String name = nameInput.getText().toString();
            String amountStr = amountInput.getText().toString();
            String timestamp = timestampInput.getText().toString().trim();
            double amount = Double.parseDouble(amountStr);

            if (name.isEmpty() || amountStr.isEmpty() || timestamp.isEmpty()) {
                Toast.makeText(this, "Please fill out all fields.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (amount < 0.01) {
                Toast.makeText(this, "Minimum amount is RM 0.01", Toast.LENGTH_SHORT).show();
                return;
            }

            String id = dbRef.push().getKey();
            Debt newDebt = new Debt(id, name, amount, timestamp);
            if (id != null) dbRef.child(id).setValue(newDebt)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(AddDebtActivity.this, "Debt added successfully!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(AddDebtActivity.this, MainActivity.class));
                    })
                    .addOnFailureListener(e -> Toast.makeText(AddDebtActivity.this, "Failed to add debt.", Toast.LENGTH_SHORT).show());
        });
    }

    private void showDatePicker() {
        MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select Date")
                .setCalendarConstraints(new CalendarConstraints.Builder().build())
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .build();

        datePicker.addOnPositiveButtonClickListener(selection -> {
            selectedDateTime.setTimeInMillis(selection);
            showTimePicker();
        });

        datePicker.show(getSupportFragmentManager(), "DATE_PICKER");
    }

    private void showTimePicker() {
        int hour = selectedDateTime.get(Calendar.HOUR_OF_DAY);
        int minute = selectedDateTime.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this, (view, hourOfDay, minuteOfHour) -> {
            selectedDateTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
            selectedDateTime.set(Calendar.MINUTE, minuteOfHour);

            String formattedTimestamp = new SimpleDateFormat("dd MMM yyyy, hh:mma", Locale.ENGLISH)
                    .format(selectedDateTime.getTime());
            timestampInput.setText(formattedTimestamp);
        }, hour, minute, false);

        timePickerDialog.show();
    }
}
