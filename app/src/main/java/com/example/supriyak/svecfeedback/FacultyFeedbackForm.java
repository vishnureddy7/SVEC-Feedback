package com.example.supriyak.svecfeedback;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class FacultyFeedbackForm extends AppCompatActivity {

    Button submit;
    EditText suggestions;
    ProgressDialog pd;
    int[] ids={R.id.ka,R.id.kb,R.id.sa,R.id.sb,R.id.sc,R.id.sd,R.id.apa,R.id.apb,R.id.ata,R.id.atb,R.id.atc,R.id.atd,R.id.ate};
    String[] question ={"ka","kb","sa","sb","sc","sd","apa","apb","ata","atb","atc","atd","ate"};//3 4 2 5

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faculty_feedback_form);
        suggestions = (EditText)findViewById(R.id.suggestions);
        submit = (Button)findViewById(R.id.submit);
        // ka kb kc sa sb sc sd apa apb ata atb atc atd ate
        submit.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                int selected[]=new int[13];
                String x="";
                pd= ProgressDialog.show(FacultyFeedbackForm.this,"Please Wait while we submit your feedback","Please wait..");
                for(int i=0;i<13;i++){
                    selected[i]=selectedItem(ids[i]);
                    x=x+" "+selected[i];
                }
                Toast.makeText(FacultyFeedbackForm.this,x,Toast.LENGTH_LONG).show();
                Map<String, Object> feedback = new HashMap<String,Object>();
                for(int i=0;i<13;i++){
                    feedback.put(question[i],selected[i]);
                }
                feedback.put("suggestions",suggestions.getText().toString());
                FirebaseFirestore db=FirebaseFirestore.getInstance();
                db.collection("Student Feedback")
                        .document()
                        .set(feedback)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                FacultyFeedbackForm.this.pd.dismiss();
                                Toast.makeText(FacultyFeedbackForm.this,"Feedback submitted successfully.",Toast.LENGTH_LONG).show();
                                Intent thankyou = new Intent(FacultyFeedbackForm.this,feedback_thankyou.class);
                                startActivity(thankyou);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                FacultyFeedbackForm.this.pd.dismiss();
                                Toast.makeText(FacultyFeedbackForm.this,"Error occured while submitting the feedback. Please try again later.",Toast.LENGTH_LONG).show();
                            }
                        });
            }
        });
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirm");
        builder.setMessage("Are you sure want to exit?");
        builder.setCancelable(false);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
                System.exit(0);
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //pass
            }
        });
        builder.show();
    }

    int selectedItem(int id){
        return Integer.parseInt(((RadioButton)findViewById(((RadioGroup)findViewById(id)).getCheckedRadioButtonId())).getText().toString());
    }
}
