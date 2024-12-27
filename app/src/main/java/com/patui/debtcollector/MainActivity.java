package com.patui.debtcollector;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private TextView totalAmountCollected;
    private RecyclerView ongoingDebtsRecyclerView;
    private Button viewRecordsButton, addDebtButton;
    private DatabaseReference dbRef;
    private ArrayList<Debt> ongoingDebts;
    private OngoingDebtsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        totalAmountCollected = findViewById(R.id.total_amount_collected);
        ongoingDebtsRecyclerView = findViewById(R.id.ongoing_debts_recyclerview);
        viewRecordsButton = findViewById(R.id.view_records_button);
        addDebtButton = findViewById(R.id.add_debt_button);

        dbRef = FirebaseDatabase.getInstance().getReference();

        // Load Total Amount Collected
        dbRef.child("collectedDebts").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                double total = 0.0;
                for (DataSnapshot snapshot : task.getResult().getChildren()) {
                    Debt debt = snapshot.getValue(Debt.class);
                    if (debt != null) total += debt.getAmount();
                }
                totalAmountCollected.setText("Total Collected: RM " + total);
            } else {
                Toast.makeText(MainActivity.this, "Failed to load total collected.", Toast.LENGTH_SHORT).show();
            }
        });

        // Load Ongoing Debts
        ongoingDebts = new ArrayList<>();
        adapter = new OngoingDebtsAdapter(ongoingDebts, dbRef);
        ongoingDebtsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        ongoingDebtsRecyclerView.setAdapter(adapter);

        dbRef.child("ongoingDebts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ongoingDebts.clear();
                for (DataSnapshot debtSnapshot : snapshot.getChildren()) {
                    Debt debt = debtSnapshot.getValue(Debt.class);
                    if (debt != null) ongoingDebts.add(debt);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this, "Failed to load debts.", Toast.LENGTH_SHORT).show();
            }
        });

        // Button Listeners
        viewRecordsButton.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, CollectedDebtsActivity.class)));
        addDebtButton.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, AddDebtActivity.class)));
    }
}
