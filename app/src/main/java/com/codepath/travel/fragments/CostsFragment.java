package com.codepath.travel.fragments;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.travel.R;
import com.codepath.travel.adapters.ExpensesAdapter;
import com.codepath.travel.adapters.RecyclerTouchListener;
import com.codepath.travel.models.Expense;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class CostsFragment extends Fragment {

    private RecyclerView rvExpenses;
    private ArrayList<Expense> expenses;
    private ExpensesAdapter adapter;
    private RecyclerTouchListener rvTouchListener;
    private EditText etEditExpense;
    private EditText etEditCost;
    private EditText etExpense;
    private EditText etMoney;
    private Button btnAddCost;
    private Double totalCost = 0.0;
    private TextView tvTotalCost;


    public CostsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_costs, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvTotalCost = view.findViewById(R.id.tvTotalCost);
        etExpense = view.findViewById(R.id.etExpense);
        etMoney = view.findViewById(R.id.etMoney);
        btnAddCost = view.findViewById(R.id.btnAddCost);
        btnAddCost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etExpense.getText().toString().isEmpty()) {
                    Toast.makeText(getContext(), "Expenses must be named", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (etMoney.getText().toString().isEmpty()) {
                    Toast.makeText(getContext(), "Must enter an amount", Toast.LENGTH_SHORT).show();
                    return;
                }
                saveCost(etExpense.getText().toString(), etMoney.getText().toString());
                etExpense.setText("");
                etMoney.setText("");
            }
        });

        rvExpenses = view.findViewById(R.id.rvExpenses);
        expenses = new ArrayList<>();
        adapter = new ExpensesAdapter(getContext(), expenses);
        rvExpenses.setLayoutManager(new LinearLayoutManager(getContext()));
        rvExpenses.setAdapter(adapter);
        expenses.add(new Expense("hotel", 100.00));
        expenses.add(new Expense("flight", 259.00));
        for (int i = 0; i < expenses.size(); i++) {
            totalCost += expenses.get(i).getCost();
        }
        tvTotalCost.setText(String.format("%.2f", totalCost));
        adapter.notifyDataSetChanged();

        rvTouchListener = new RecyclerTouchListener(getActivity(), rvExpenses);
        rvTouchListener.setSwipeOptionViews(R.id.delete_task,R.id.edit_entry).setSwipeable(R.id.rowFG, R.id.swipeMenuLayout, new RecyclerTouchListener.OnSwipeOptionsClickListener() {
            @Override
            public void onSwipeOptionClicked(int viewID, int position) {
                switch (viewID) {
                    case R.id.delete_task:
                        totalCost -= expenses.get(position).getCost();
                        tvTotalCost.setText(String.format("%.2f", totalCost));
                        expenses.remove(position);
                        adapter.notifyItemRemoved(position);
                        break;
                    case R.id.edit_entry:
                        showEditAlertDialog(expenses.get(position), position);
                        break;
                }
            }
        });
        rvExpenses.addOnItemTouchListener(rvTouchListener);
    }

    private void saveCost(String name, String cost) {
        Expense expense = new Expense(name, Double.parseDouble(cost));
        expenses.add(expense);
        totalCost += Double.parseDouble(cost);
        tvTotalCost.setText(String.format("%.2f", totalCost));
        adapter.notifyItemInserted(expenses.size()-1);
    }

    private void showEditAlertDialog(Expense expense, int position) {
        View messageView = LayoutInflater.from(getContext()).inflate(R.layout.cost_message_item, null);
        etEditExpense = messageView.findViewById(R.id.etEditExpense);
        etEditCost = messageView.findViewById(R.id.etEditCost);

        etEditExpense.setText(expense.getName());
        etEditCost.setText(String.valueOf(expense.getCost()));

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
        alertDialogBuilder.setView(messageView);
        final AlertDialog alertDialog = alertDialogBuilder.create();

        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Save Changes",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (etEditExpense.getText().toString().isEmpty()) {
                            Toast.makeText(getContext(), "Expense must be named", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (etEditCost.getText().toString().isEmpty()) {
                            Toast.makeText(getContext(), "Must add a cost", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        totalCost -= expense.getCost();
                        expense.setName(etEditExpense.getText().toString());
                        expense.setCost(Double.parseDouble(etEditCost.getText().toString()));
                        totalCost += expense.getCost();
                        tvTotalCost.setText(String.format("%.2f", totalCost));
                        adapter.notifyItemChanged(position);
                    }
                });
        alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        alertDialog.show();
    }
}