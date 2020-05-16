package com.example.sharadasangeethashaale.ui.TodaysClasses;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sharadasangeethashaale.R;
import com.example.sharadasangeethashaale.Student_holder;
import com.example.sharadasangeethashaale.Student_pojo;
import com.example.sharadasangeethashaale.today_class_adapter;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.github.jhonnyx2012.horizontalpicker.DatePickerListener;
import com.github.jhonnyx2012.horizontalpicker.HorizontalPicker;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import org.joda.time.DateTime;

public class classesToday extends Fragment implements DatePickerListener {

    private HomeViewModel homeViewModel;
    String dayName;
    private FirestoreRecyclerAdapter<Student_pojo, today_class_adapter> adapter;
    RecyclerView recyclerView;
    FirebaseFirestore db;
    Button attendance;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        db=FirebaseFirestore.getInstance();
        View root = inflater.inflate(R.layout.fragment_classes_today, container, false);
        HorizontalPicker picker = (HorizontalPicker) root.findViewById(R.id.datePicker);
        recyclerView=root.findViewById(R.id.todayRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        attendance=root.findViewById(R.id.attendance);
        // initialize it and attach a listener

        picker.setListener(this).init();

        picker.setDate(new DateTime());
        return root;
    }

    @Override
    public void onDateSelected(DateTime dateSelected) {
        //Toast.makeText(getActivity(), "Date set", Toast.LENGTH_SHORT).show();
        //Toast.makeText(getActivity(), ""+dateSelected.getDayOfWeek(), Toast.LENGTH_SHORT).show();
        attendance.setVisibility(View.INVISIBLE);
        Toast.makeText(getActivity(), "Entered", Toast.LENGTH_SHORT).show();
        dayName=getDayName(dateSelected.getDayOfWeek());
        Query query=db.collection("students").whereArrayContains("daysOfWeek",dayName).whereEqualTo("status","active");
        FirestoreRecyclerOptions<Student_pojo> options=new FirestoreRecyclerOptions.Builder<Student_pojo>()
                .setQuery(query,Student_pojo.class)
                .build();
        adapter=new FirestoreRecyclerAdapter<Student_pojo, today_class_adapter>(options) {
            @Override
            protected void onBindViewHolder(@NonNull today_class_adapter holder, int position, @NonNull Student_pojo model) {
                if(model.getName()!=null)
                    attendance.setVisibility(View.VISIBLE);
                holder.nameTextView.setText(model.getName());
                Toast.makeText(getContext(), ""+model.getName(), Toast.LENGTH_SHORT).show();
                holder.fromTime.setText(model.getTimes().get(dayName).get(0));
                holder.toTime.setText(model.getTimes().get(dayName).get(1));
            }

            @NonNull
            @Override
            public today_class_adapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View v=LayoutInflater.from(parent.getContext()).inflate(R.layout.today_class_card,parent,false);
                return new today_class_adapter(v);
            }
        };
        adapter.startListening();
        recyclerView.setAdapter(adapter);

    }
    public String getDayName(int dayOfWeek){
        switch (dayOfWeek){
            case 1:return "Monday";
            case 2:return "Tuesday";
            case 3:return "Wednesday";
            case 4:return "Thursday";
            case 5:return "Friday";
            case 6:return "Saturday";

        }
        return "";
    }

}