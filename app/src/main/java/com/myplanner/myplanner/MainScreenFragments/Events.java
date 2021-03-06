package com.myplanner.myplanner.MainScreenFragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.myplanner.myplanner.R;

import java.util.ArrayList;
import java.util.List;

public class Events extends Fragment {
    private final List<String> titles = new ArrayList<>();
    private final List<String> times = new ArrayList<>();
    private final List<String> bodies = new ArrayList<>();
    private final List<Integer> ids = new ArrayList<>();

    EventRecycleViewAdapter adapter;

    // ---------------------------------------------------------------------------------------------
    // ----------------------------------------- Interface -----------------------------------------
    // ---------------------------------------------------------------------------------------------

    EventInterface mCallback;
    public interface EventInterface {
        void eventClickedAction(int eventID);
    }

    // ---------------------------------------------------------------------------------------------
    // -------------------------------- Fragment Override Functions --------------------------------
    // ---------------------------------------------------------------------------------------------

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mCallback = (EventInterface) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final RecyclerView rv = (RecyclerView) inflater.inflate(R.layout.basic_recyclerview, container, false);
        adapter = new EventRecycleViewAdapter();
        rv.setLayoutManager(new LinearLayoutManager(rv.getContext()));
        rv.setAdapter(adapter);
        return rv;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallback = null;
    }

    // ---------------------------------------------------------------------------------------------
    // --------------------------------- Functions Called by Main ----------------------------------
    // ---------------------------------------------------------------------------------------------

    public void reloadData() {
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    public void clearEventLists() {
        titles.clear();
        times.clear();
        bodies.clear();
        ids.clear();
    }

    public void addEventInfo(String title, String time, String body, int id) {
        titles.add(title);
        times.add(time);
        bodies.add(body);
        ids.add(id);
    }

    // ---------------------------------------------------------------------------------------------
    // ----------------------------------- RecyclerView Adapter ------------------------------------
    // ---------------------------------------------------------------------------------------------

    class EventRecycleViewAdapter extends RecyclerView.Adapter<EventRecycleViewAdapter.ViewHolder> {

        class ViewHolder extends RecyclerView.ViewHolder {
            private final View view;
            private final TextView title;
            private final TextView time;
            private final TextView body;
            private int id;

            private ViewHolder(View nview) {
                super(nview);
                view = nview;
                title = (TextView) view.findViewById(R.id.title_text);
                time = (TextView) view.findViewById(R.id.time_text);
                body = (TextView) view.findViewById(R.id.body_text);
                id = -1;

                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mCallback.eventClickedAction(id);
                    }
                });
            }
        }

        private EventRecycleViewAdapter() {
            this.notifyDataSetChanged();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_event_list_item, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.title.setText(titles.get(position));
            holder.time.setText(times.get(position));
            holder.body.setText(bodies.get(position));
            holder.id = ids.get(position);
        }

        @Override
        public int getItemCount() {
            if (titles == null || titles.isEmpty()) {
                return 0;
            } else {
                return titles.size();
            }
        }
    }
}
