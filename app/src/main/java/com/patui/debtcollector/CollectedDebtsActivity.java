package com.patui.debtcollector;

import android.os.Bundle;
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

public class CollectedDebtsActivity extends AppCompatActivity {

    private RecyclerView collectedDebtsRecyclerView;
    private DatabaseReference dbRef;
    private ArrayList<Debt> collectedDebts;
    private CollectedDebtsAdapter adapter;
    private SimpleDateFormat timestampFormat = new SimpleDateFormat("dd MMM yyyy, hh:mma", Locale.ENGLISH);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collected_debts);

        collectedDebtsRecyclerView = findViewById(R.id.collected_debts_recyclerview);

        dbRef = FirebaseDatabase.getInstance().getReference().child("collectedDebts");

        collectedDebts = new ArrayList<>();
        adapter = new CollectedDebtsAdapter(collectedDebts, dbRef);
        collectedDebtsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        collectedDebtsRecyclerView.setAdapter(adapter);

        dbRef.orderByChild("timestamp").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                collectedDebts.clear();
                for (DataSnapshot debtSnapshot : snapshot.getChildren()) {
                    Debt debt = debtSnapshot.getValue(Debt.class);
                    if (debt != null) collectedDebts.add(debt);
                }

                Collections.sort(collectedDebts, (debt1, debt2) -> {
                    try {
                        Date date1 = timestampFormat.parse(debt1.getTimestamp());
                        Date date2 = timestampFormat.parse(debt2.getTimestamp());

                        if (date1 != null && date2 != null) {
                            return Long.compare(date2.getTime(), date1.getTime());
                        } else {
                            return 0;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        return 0; // If parsing fails, keep the original order
                    }
                });

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(CollectedDebtsActivity.this, "Failed to load debts.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
