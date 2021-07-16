package com.codepath.travel.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.RequestHeaders;
import com.codepath.asynchttpclient.RequestParams;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.codepath.travel.R;
import com.codepath.travel.adapters.ExpensesAdapter;
import com.codepath.travel.adapters.RecyclerTouchListener;
import com.codepath.travel.models.Expense;

import org.w3c.dom.Text;

import java.util.ArrayList;

import okhttp3.Headers;

/**
 * A simple {@link Fragment} subclass.
 */
public class CostsFragment extends Fragment {

    private RecyclerView rvExpenses;
    private ArrayList<Expense> expenses;
    private ExpensesAdapter adapter;
    private RecyclerTouchListener rvTouchListener;

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
        // TODO: set up view for summary of costs of user's trip
        // probably using Recycler View
        super.onViewCreated(view, savedInstanceState);

        rvExpenses = view.findViewById(R.id.rvExpenses);
        expenses = new ArrayList<>();
        expenses.add(new Expense("hotel", 100.00));
        expenses.add(new Expense("flight", 259.00));
        adapter = new ExpensesAdapter(getContext(), expenses);
        rvExpenses.setLayoutManager(new LinearLayoutManager(getContext()));
        rvExpenses.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        rvTouchListener = new RecyclerTouchListener(getActivity(), rvExpenses);
        rvTouchListener.setSwipeOptionViews(R.id.delete_task,R.id.edit_entry).setSwipeable(R.id.rowFG, R.id.swipeMenuLayout, new RecyclerTouchListener.OnSwipeOptionsClickListener() {
            @Override
            public void onSwipeOptionClicked(int viewID, int position) {
                switch (viewID) {
                    case R.id.delete_task:
                        expenses.remove(position);
                        adapter.notifyItemRemoved(position);
                        break;
                    case R.id.edit_entry:
                        Toast.makeText(getContext(), "Edit", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });
        rvExpenses.addOnItemTouchListener(rvTouchListener);
    }
}