package com.example.supriyak.svecfeedback;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

public class EmployerFeedbackForm extends AppCompatActivity {

    Button submit;
    EditText suggestions;
    int[] ids={R.id.ka,R.id.kb,R.id.kc,R.id.sa,R.id.sb,R.id.sc,R.id.sd,R.id.apa,R.id.apb,R.id.ata,R.id.atb,R.id.atc,R.id.atd,R.id.ate};
    String[] question ={"ka","kb","kc","sa","sb","sc","sd","apa","apb","ata","atb","atc","atd","ate"};//3 4 2 5

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employer_feedback_form);
        suggestions = (EditText)findViewById(R.id.suggestions);
        submit = (Button)findViewById(R.id.submit);
        // ka kb kc sa sb sc sd apa apb ata atb atc atd ate
        submit.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                int selected[]=new int[14];
                String x="";
                for(int i=0;i<14;i++){
                    selected[i]=selectedItem(ids[i]);
                    x=x+" "+selected[i];
                }
                Toast.makeText(EmployerFeedbackForm.this,x,Toast.LENGTH_LONG).show();
                Map<String, Object> feedback = new HashMap<String,Object>();
                for(int i=0;i<14;i++){
                    feedback.put(question[i],selected[i]);
                }
                feedback.put("suggestions",suggestions.getText().toString());
                FirebaseFirestore db=FirebaseFirestore.getInstance();
                db.collection("Employer Feedback")
                        .document()
                        .set(feedback)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(EmployerFeedbackForm.this,"Successfully registered",Toast.LENGTH_LONG).show();
//                                Intent thankyou = new Intent(Registration.this,RegistrationThankyou.class);
//                                startActivity(thankyou);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(EmployerFeedbackForm.this,"Error while Registering. Please try again later.",Toast.LENGTH_LONG).show();
                            }
                        });
            }
        });
    }

    int selectedItem(int id){
        return Integer.parseInt(((RadioButton)findViewById(((RadioGroup)findViewById(id)).getCheckedRadioButtonId())).getText().toString());
    }
}
