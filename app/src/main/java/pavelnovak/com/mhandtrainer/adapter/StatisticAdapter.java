package pavelnovak.com.mhandtrainer.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import pavelnovak.com.mhandtrainer.R;
import pavelnovak.com.mhandtrainer.model.Training;

/**
 * Created by G710 on 21.03.2018.
 */

public class StatisticAdapter extends ArrayAdapter<Training> {

    public StatisticAdapter(@NonNull Context context, List<Training> statisticList) {
        super(context, 0, statisticList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Training statisticItem = getItem(position);

        LayoutInflater mInflater = (LayoutInflater) getContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        assert mInflater != null;
        convertView = mInflater.inflate(R.layout.statistic_item, null);

        TextView startDateTextView = (TextView) convertView.findViewById(R.id.start_date);
        TextView durationTextView = (TextView) convertView.findViewById(R.id.duration);
        TextView countTextView = (TextView) convertView.findViewById(R.id.count);
        TextView commentTextView = (TextView) convertView.findViewById(R.id.comment);
        TextView typeTextView = (TextView) convertView.findViewById(R.id.type);

        if (statisticItem != null) {
            startDateTextView.setText(formatDate(statisticItem.getStartDate()));
            durationTextView.setText(statisticItem.getDuration());
            countTextView.setText(String.valueOf(statisticItem.getCount()));
            commentTextView.setText(statisticItem.getComment());
            typeTextView.setText(statisticItem.getType());
        }

        return convertView;
    }

    private String formatDate(Date dateObject){
        Format formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
        if (dateObject == null){
            return "bad time";
        } else return formatter.format(dateObject);
    }
}
