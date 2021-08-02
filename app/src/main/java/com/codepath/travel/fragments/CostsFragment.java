package com.codepath.travel.fragments;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.codepath.travel.R;
import com.codepath.travel.adapters.ExpensesAdapter;
import com.codepath.travel.adapters.RecyclerTouchListener;
import com.codepath.travel.models.Destination;
import com.codepath.travel.models.Expense;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * This fragment displays the list of expenses for the trip with any prior selected expenses from flight, hotel, etc.
 * already loaded in. The user can add, edit, and delete expenses (except those that were automatically loaded in).
 * The user can reach this fragment through bottom navigation.
 */
public class CostsFragment extends Fragment {

    private static ArrayList<Expense> expenses;
    private RecyclerView rvExpenses;
    private ExpensesAdapter adapter;
    private RecyclerTouchListener rvTouchListener;
    private EditText etEditExpense;
    private EditText etEditCost;
    private Double totalCost = 0.0;
    private EditText etExpenseName;
    private Button btnAddCost;
    private EditText etExpenseAmount;
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

        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Cost Breakdown");

        tvTotalCost = view.findViewById(R.id.tvTotalCost);
        etExpenseName = view.findViewById(R.id.etExpense);
        etExpenseAmount = view.findViewById(R.id.etMoney);
        btnAddCost = view.findViewById(R.id.btnAddCost);
        btnAddCost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etExpenseName.getText().toString().isEmpty()) {
                    Toast.makeText(getContext(), "Expenses must be named", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (etExpenseAmount.getText().toString().isEmpty()) {
                    Toast.makeText(getContext(), "Must enter an amount", Toast.LENGTH_SHORT).show();
                    return;
                }
                saveCost(etExpenseName.getText().toString(), etExpenseAmount.getText().toString());
                etExpenseName.setText("");
                etExpenseAmount.setText("");
            }
        });

        rvExpenses = view.findViewById(R.id.rvExpenses);
        expenses = new ArrayList<>();
        adapter = new ExpensesAdapter(getContext(), expenses);
        rvExpenses.setLayoutManager(new LinearLayoutManager(getContext()));
        rvExpenses.setAdapter(adapter);
        loadExpenses();

        rvTouchListener = new RecyclerTouchListener(getActivity(), rvExpenses);
        rvTouchListener.setSwipeOptionViews(R.id.delete_task, R.id.edit_entry).setSwipeable(R.id.expenseForeground, R.id.swipeMenuLayout, new RecyclerTouchListener.OnSwipeOptionsClickListener() {
            @Override
            public void onSwipeOptionClicked(int viewID, int position) {
                if (expenses.get(position).isProtected()) {
                    showProtectedAlertDialog();
                    return;
                }
                switch (viewID) {
                    case R.id.delete_task:
                        deleteExpense(position);
                        break;
                    case R.id.edit_entry:
                        showEditAlertDialog(expenses.get(position), position);
                        break;
                }
            }
        });
        rvExpenses.addOnItemTouchListener(rvTouchListener);
    }

    private void showProtectedAlertDialog() {
        View messageView = LayoutInflater.from(getContext()).inflate(R.layout.no_edit_message_item, null);
        TextView alertMessage = messageView.findViewById(R.id.tvMessage);
        alertMessage.setText(getResources().getString(R.string.no_editing_prices_message));

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
        alertDialogBuilder.setView(messageView);
        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        alertDialog.show();
    }

    private void deleteExpense(int position) {
        totalCost -= Double.parseDouble(expenses.get(position).getCost());
        tvTotalCost.setText(String.format("%.2f", totalCost));
        Expense deleteExpense = expenses.get(position);
        expenses.remove(position);
        adapter.notifyItemRemoved(position);
        deleteExpense.deleteInBackground();
    }

    private void saveCost(String name, String cost) {
        Expense expense = createExpense(name, cost);
        expense.setIsEditable();
        expense.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Toast.makeText(getContext(), "Unable to save expense", Toast.LENGTH_SHORT).show();
                    return;
                }
                Toast.makeText(getContext(), "Expense added!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showEditAlertDialog(Expense expense, int position) {
        View messageView = LayoutInflater.from(getContext()).inflate(R.layout.cost_message_item, null);
        etEditExpense = messageView.findViewById(R.id.tvMapMessage);
        etEditCost = messageView.findViewById(R.id.tvLocationName);

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
                        editExpense(expense, position);
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

    private void editExpense(Expense expense, int position) {
        totalCost -= Double.parseDouble(expense.getCost());
        expense.setName(etEditExpense.getText().toString());
        expense.setCost(etEditCost.getText().toString());
        totalCost += Double.parseDouble(expense.getCost());
        tvTotalCost.setText(String.format("%.2f", totalCost));
        expense.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Toast.makeText(getContext(), "Unable to save edits", Toast.LENGTH_SHORT).show();
                    return;
                }
                Toast.makeText(getContext(), "Edits saved", Toast.LENGTH_SHORT).show();
                adapter.notifyItemChanged(position);
            }
        });
    }

    private void loadExpenses() {
        loadDestinationExpenses();
        ParseQuery<Expense> query = ParseQuery.getQuery(Expense.class);
        query.whereEqualTo(Expense.KEY_USER, ParseUser.getCurrentUser());
        query.findInBackground(new FindCallback<Expense>() {
            @Override
            public void done(List<Expense> queryExpenses, ParseException e) {
                expenses.addAll(queryExpenses);
                adapter.notifyDataSetChanged();
                for (int i = 0; i < queryExpenses.size(); i++) {
                    totalCost += Double.parseDouble(queryExpenses.get(i).getCost());
                }
                tvTotalCost.setText(String.format("%.2f", totalCost));
            }
        });
    }

    private void loadDestinationExpenses() {
        ParseQuery<Destination> query = ParseQuery.getQuery(Destination.class);
        query.whereEqualTo(Destination.KEY_USER, ParseUser.getCurrentUser());
        query.findInBackground(new FindCallback<Destination>() {
            @Override
            public void done(List<Destination> destinations, ParseException e) {
                for (Destination destination : destinations) {
                    if (destination.getHotelCost() != null) {
                        createExpense(destination.getHotelName() + " (" + destination.getCountry() + ")", destination.getHotelCost());
                    }
                    if (destination.isRoundtrip() != null && destination.isRoundtrip()) {
                        createExpense("Roundtrip flight to " + destination.getArriveAirportName(), destination.getCost());
                    } else {
                        if (destination.getCost() != null) {
                            createExpense("Flight to " + destination.getArriveAirportName(), destination.getCost());
                        }
                        if (destination.getInboundCost() != null) {
                            createExpense("Return flight from" + destination.getFormattedLocationName(), destination.getInboundCost());
                        }
                    }
                }
            }
        });
    }

    private Expense createExpense(String name, String cost) {
        Expense expense = new Expense();
        expense.setUser(ParseUser.getCurrentUser());
        expense.setName(name);
        expense.setCost(cost);
        expense.setIsProtected();
        expenses.add(expense);
        totalCost += Double.parseDouble(cost);
        tvTotalCost.setText(String.format("%.2f", totalCost));
        adapter.notifyItemInserted(expenses.size() - 1);
        return expense;
    }
}