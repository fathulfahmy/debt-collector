package com.patui.debtcollector;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

public class CollectedDebtsAdapter extends RecyclerView.Adapter<CollectedDebtsAdapter.ViewHolder> {

    private ArrayList<Debt> debts;
    private DatabaseReference dbRef;

    public CollectedDebtsAdapter(ArrayList<Debt> debts, DatabaseReference dbRef) {
        this.debts = debts;
        this.dbRef = dbRef;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_collected_debt, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Debt debt = debts.get(position);

        holder.name.setText(debt.getName());
        holder.amount.setText(String.format("RM %.2f", debt.getAmount()));

        holder.deleteButton.setOnClickListener(v -> dbRef.child(debt.getId()).removeValue());
    }

    @Override
    public int getItemCount() {
        return debts.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView name, amount;
        public ImageButton deleteButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.debt_name_collected);
            amount = itemView.findViewById(R.id.debt_amount_collected);
            deleteButton = itemView.findViewById(R.id.delete_button_collected);
        }
    }
}
