package pavelnovak.com.mhandtrainer.fragments;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.activeandroid.query.Select;

import java.util.List;

import pavelnovak.com.mhandtrainer.R;
import pavelnovak.com.mhandtrainer.adapter.StatisticAdapter;
import pavelnovak.com.mhandtrainer.model.Training;

/**
 * A simple {@link Fragment} subclass.
 */
public class StatisticFragment extends Fragment {

    private String login;

    public StatisticFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_statistic, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        View view = getView();
        if (view != null){
            ListView statisticListView = view.findViewById(R.id.statistic_list);
            StatisticAdapter statisticItemAdapter = new StatisticAdapter(view.getContext(), getTrainings(login));
            statisticListView.setAdapter(statisticItemAdapter);
        }
    }

    public List<Training> getTrainings(String login){
        return new Select()
                .from(Training.class)
                .where("loginUser = ?", login)
                .orderBy("Id DESC")
                .execute();
    }

    public void setUserLogin(String login){
        this.login = login;
    }
}
