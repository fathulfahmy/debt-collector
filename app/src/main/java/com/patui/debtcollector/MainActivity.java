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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private TextView totalAmountCollected;
    private RecyclerView ongoingDebtsRecyclerView;
    private Button viewRecordsButton, addDebtButton;
    private DatabaseReference dbRef;
    private ArrayList<Debt> ongoingDebts;
    private OngoingDebtsAdapter adapter;
    private SimpleDateFormat timestampFormat = new SimpleDateFormat("d MMM yyyy, h:mma", Locale.ENGLISH);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        totalAmountCollected = findViewById(R.id.total_amount_collected);
        ongoingDebtsRecyclerView = findViewById(R.id.ongoing_debts_recyclerview);
        viewRecordsButton = findViewById(R.id.view_records_button);
        addDebtButton = findViewById(R.id.add_debt_button);

        dbRef = FirebaseDatabase.getInstance().getReference();

        dbRef.child("collectedDebts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                double total = 0.0;
                for (DataSnapshot debtSnapshot : snapshot.getChildren()) {
                    Debt debt = debtSnapshot.getValue(Debt.class);
                    if (debt != null) total += debt.getAmount();
                }
                totalAmountCollected.setText("RM " + total);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this, "Failed to update total collected.", Toast.LENGTH_SHORT).show();
            }
        });

        // Load Ongoing Debts
        ongoingDebts = new ArrayList<>();
        adapter = new OngoingDebtsAdapter(ongoingDebts, dbRef);
        ongoingDebtsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        ongoingDebtsRecyclerView.setAdapter(adapter);

        dbRef.child("ongoingDebts").orderByChild("timestamp").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ongoingDebts.clear();
                for (DataSnapshot debtSnapshot : snapshot.getChildren()) {
                    Debt debt = debtSnapshot.getValue(Debt.class);
                    if (debt != null) ongoingDebts.add(debt);
                }

                // Sorting ongoing debts by timestamp
                Collections.sort(ongoingDebts, (debt1, debt2) -> {
                    try {
                        Date date1 = timestampFormat.parse(debt1.getTimestamp());
                        Date date2 = timestampFormat.parse(debt2.getTimestamp());

                        if (date1 != null && date2 != null) {
                            return Long.compare(date2.getTime(), date1.getTime()); // Sort descending
                        } else {
                            return 0;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        return 0; // If parsing fails, keep the original order
                    }
                });

                // Notify the adapter to refresh the list
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this, "Failed to load ongoing debts.", Toast.LENGTH_SHORT).show();
            }
        });


        // Button Listeners
        viewRecordsButton.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, CollectedDebtsActivity.class)));
        addDebtButton.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, AddDebtActivity.class)));
    }
}
