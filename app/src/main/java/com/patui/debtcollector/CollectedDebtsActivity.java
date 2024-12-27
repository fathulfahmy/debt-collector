package com.patui.debtcollector;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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

public class CollectedDebtsActivity extends AppCompatActivity {

    private RecyclerView collectedDebtsRecyclerView;
    private Button backButton;
    private DatabaseReference dbRef;
    private ArrayList<Debt> collectedDebts;
    private CollectedDebtsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collected_debts);

        collectedDebtsRecyclerView = findViewById(R.id.collected_debts_recyclerview);
        backButton = findViewById(R.id.back_button);

        dbRef = FirebaseDatabase.getInstance().getReference().child("collectedDebts");

        // Load Collected Debts
        collectedDebts = new ArrayList<>();
        adapter = new CollectedDebtsAdapter(collectedDebts, dbRef);
        collectedDebtsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        collectedDebtsRecyclerView.setAdapter(adapter);

        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                collectedDebts.clear();
                for (DataSnapshot debtSnapshot : snapshot.getChildren()) {
                    Debt debt = debtSnapshot.getValue(Debt.class);
                    if (debt != null) collectedDebts.add(debt);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(CollectedDebtsActivity.this, "Failed to load debts.", Toast.LENGTH_SHORT).show();
            }
        });

        backButton.setOnClickListener(v -> startActivity(new Intent(CollectedDebtsActivity.this, MainActivity.class)));
    }
}
