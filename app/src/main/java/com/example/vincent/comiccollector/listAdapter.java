package com.example.vincent.comiccollector;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;


public class listAdapter extends ArrayAdapter {
    // prepare some variables that are used in the whole class.
    private final ArrayList<Double> scores;
    private Context context;


    public listAdapter(@NonNull Context context, ArrayList<Double> scores, int resource) {
        super(context, resource, scores);
        this.context = context;
        this.scores = scores;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(
                Activity.LAYOUT_INFLATER_SERVICE);
        final View view = inflater.inflate(R.layout.row_layout,null);

        // set the variables and listeners.
        setEditText(scores.get(position),R.id.qualityScore,view);
        ImageButton save = view.findViewById(R.id.save);
        ImageButton delete = view.findViewById(R.id.delete);
        delete.setOnClickListener(new explainLongClick());
        delete.setOnLongClickListener(new deleteScore(position,view));
        save.setOnClickListener(new saveScore(position,view));
        return view;
    }

    // set a score in the edittext.
    public void setEditText(Double input,int id,View view){
        EditText editText = view.findViewById(id);
        editText.setText(input.toString());
    }

    // the OnLongClickListener to delete the score.
    private class deleteScore implements View.OnLongClickListener {
        int position;
        View view;

        public deleteScore(int i,View view) {
            this.position=i;
            this.view = view;
        }

        @Override
        public boolean onLongClick(View view) {
            view=this.view;
            // visualize that the score is deleted.
            changeScore(position,false,view);
            ConstraintLayout constraintLayout = view.findViewById(R.id.content);
            constraintLayout.setVisibility(View.GONE);
            TextView textView = view.findViewById(R.id.deletedInfo);
            textView.setVisibility(View.VISIBLE);
            return true;
        }
    }

    // the OnLongClickListener to save the score.
    private class saveScore implements View.OnClickListener {
        int position;
        View view;
        public saveScore(int i,View view) {
            this.view=view;
            this.position=i;
        }
        @Override
        public void onClick(View view) {
            view=this.view;
            changeScore(position,true,view);
        }
    }

    // the OnClickListener to notify how to delete the score.
    private class explainLongClick implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            notifyUser(getContext().getString(R.string.warningDelete),getContext());
        }
    }

    // update or delete the score from the shared preference.
    public void changeScore(int i,Boolean changing,View view){
        Boolean failed=true;

        // retrieve the old scores.
        SharedPreferences sharedPref1 = getContext().getSharedPreferences("scores", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref1.edit();

        // checks what's needs to be done.
        if(changing) {
            String editText = addComicDialog.getEditText(R.id.qualityScore, view);
            if (editText.length() != 0) {
                Double score = Double.parseDouble(editText);

                // checks requirements
                if (score <= 10.0 && score > 0.0) {
                    failed = false;
                    editor.putString("score_" + i, editText);
                    scores.set(i,score);
                }
            }
            if (failed) {
                notifyUser("Please fill in a value between 1-10", getContext());
            }
        }
        else {
            editor.remove("score_" + i);
        }
        editor.apply();

    }

    // show a toast to notify the user.
    public static void notifyUser(String text,Context context){
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }
}
