package com.patui.debtcollector;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddDebtActivity extends AppCompatActivity {

    private EditText nameInput, amountInput;
    private Button cancelButton, confirmButton;
    private DatabaseReference dbRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_debt);

        nameInput = findViewById(R.id.name_input);
        amountInput = findViewById(R.id.amount_input);
        cancelButton = findViewById(R.id.cancel_button);
        confirmButton = findViewById(R.id.confirm_button);

        dbRef = FirebaseDatabase.getInstance().getReference().child("ongoingDebts");

        cancelButton.setOnClickListener(v -> startActivity(new Intent(AddDebtActivity.this, MainActivity.class)));

        confirmButton.setOnClickListener(v -> {
            String name = nameInput.getText().toString();
            String amountStr = amountInput.getText().toString();

            if (name.isEmpty() || amountStr.isEmpty()) {
                Toast.makeText(this, "Please fill out all fields.", Toast.LENGTH_SHORT).show();
                return;
            }

            double amount = Double.parseDouble(amountStr);
            String id = dbRef.push().getKey();
            Debt newDebt = new Debt(id, name, amount, false);
            if (id != null) dbRef.child(id).setValue(newDebt)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(AddDebtActivity.this, "Debt added successfully!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(AddDebtActivity.this, MainActivity.class));
                    })
                    .addOnFailureListener(e -> Toast.makeText(AddDebtActivity.this, "Failed to add debt.", Toast.LENGTH_SHORT).show());
        });
    }
}
