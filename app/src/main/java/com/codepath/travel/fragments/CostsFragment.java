package com.codepath.travel.fragments;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.codepath.travel.adapters.RecyclerTouched;
import com.codepath.travel.models.Destination;
import com.codepath.travel.models.Expense;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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

    private ArrayList<Expense> expenses;
    private RecyclerView rvExpenses;
    private ExpensesAdapter adapter;
    private RecyclerTouched rvTouchListener;
    private Double totalCost = 0.0;
    private EditText etExpenseName;
    private EditText etExpenseCost;
    private TextView tvTotalCost;
    private FloatingActionButton fabAddExpense;

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
        fabAddExpense = view.findViewById(R.id.fabAddExpense);
        fabAddExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCreateExpenseAlert();
            }
        });

        rvExpenses = view.findViewById(R.id.rvExpenses);
        expenses = new ArrayList<>();
        adapter = new ExpensesAdapter(getContext(), expenses);
        rvExpenses.setLayoutManager(new LinearLayoutManager(getContext()));
        rvExpenses.setAdapter(adapter);
        loadExpenses();

        rvTouchListener = new RecyclerTouched(getActivity(), rvExpenses);
        rvTouchListener.setSwipeOptionViews(R.id.delete_task, R.id.edit_entry).setSwipeable(R.id.expenseForeground, R.id.swipeMenuLayout, new RecyclerTouched.OnSwipeOptionsClickListener() {
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

    private void showCreateExpenseAlert() {
        final AlertDialog alertDialog = buildBasicExpenseAlert();
        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Create",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (isEmptyFields(etExpenseName, etExpenseCost)) {
                            return;
                        }
                        saveCost(etExpenseName.getText().toString(), etExpenseCost.getText().toString());
                    }
                });
        alertDialog.show();
        setAlertButtonColors(alertDialog);
    }

    private void showEditAlertDialog(Expense expense, int position) {
        final AlertDialog alertDialog = buildBasicExpenseAlert();
        etExpenseName.setText(expense.getName());
        etExpenseCost.setText(expense.getCost());
        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Save",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (isEmptyFields(etExpenseName, etExpenseCost)) {
                            return;
                        }
                        editExpense(expense, position);
                    }
                });
        alertDialog.show();
        setAlertButtonColors(alertDialog);
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

    private void editExpense(Expense expense, int position) {
        totalCost -= Double.parseDouble(expense.getCost());
        expense.setName(etExpenseName.getText().toString());
        expense.setCost(etExpenseCost.getText().toString());
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

    private AlertDialog buildBasicExpenseAlert() {
        View messageView = LayoutInflater.from(getContext()).inflate(R.layout.cost_message_item, null);
        etExpenseName = messageView.findViewById(R.id.etExpenseName);
        etExpenseCost = messageView.findViewById(R.id.etExpenseCost);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
        alertDialogBuilder.setView(messageView);
        AlertDialog alertDialog = alertDialogBuilder.create();

        alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        return alertDialog;
    }

    private boolean isEmptyFields(EditText etExpense, EditText etCost) {
        if (etExpenseName.getText().toString().isEmpty()) {
            Toast.makeText(getContext(), "Expense must be named", Toast.LENGTH_SHORT).show();
            return true;
        }
        if (etExpenseCost.getText().toString().isEmpty()) {
            Toast.makeText(getContext(), "Must add a cost", Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }

    private void setAlertButtonColors(AlertDialog alertDialog) {
        alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.medium_pink));
        alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.medium_pink));
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