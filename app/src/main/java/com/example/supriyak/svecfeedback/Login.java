package com.example.supriyak.svecfeedback;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class Login extends AppCompatActivity {

    EditText username ;
    EditText password;
    Button login;
    TextView signup,forgotPassword;
    LoginDetails loginDetails;
    ProgressDialog pd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        SharedPreferences sp1=this.getSharedPreferences("Login", MODE_PRIVATE);
//
//        String type=sp1.getString("type", null);
//        if(type.equals("student")){
//            Intent student = new Intent(Login.this, StudentFeedbackForm.class);
//            startActivity(student);
//            return;
//        }
//        else if(type.equals("alumni")){
//            Intent alumni = new Intent(Login.this, AlumniFeedbackForm.class);
//            startActivity(alumni);
//            return;
//        }
//        else if(type.equals("employer")){
//            Intent employer = new Intent(Login.this,  EmployerFeedbackForm.class);
//            startActivity(employer);
//            return;
//        }
//        else if(type.equals("faculty")){
//            Intent faculty = new Intent(Login.this, FacultyFeedbackForm.class);
//            startActivity(faculty);
//            return;
//        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        username = (EditText)findViewById(R.id.username);
        password = (EditText)findViewById(R.id.password);
        login = (Button)findViewById(R.id.login);
        signup = (TextView)findViewById(R.id.signup);
        forgotPassword = (TextView)findViewById(R.id.forgot_password);
        loginDetails = new LoginDetails();
        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent forgot = new Intent(Login.this, ForgotPassword.class);
                startActivity(forgot);
            }
        });
        login.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                store();
                if(validate()){
                    //search db
                    pd=ProgressDialog.show(Login.this,"Please Wait while we verify your details","Please wait..");
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    db.collection("User Details")
                            .whereEqualTo("email",Login.this.loginDetails.username)
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        int i=0;
                                        Map<String,Object> details= new HashMap<String,Object>();
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            i=i+1;
                                            details=document.getData();
                                        }
                                        if(i==0){
                                            //No records
                                            FirebaseFirestore db = FirebaseFirestore.getInstance();
                                            db.collection("User Details")
                                            .whereEqualTo("mobile",Login.this.loginDetails.username)
                                                    .get()
                                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                            if (task.isSuccessful()) {
                                                                int i = 0;
                                                                Map<String,Object> details= new HashMap<String,Object>();
                                                                for (QueryDocumentSnapshot document : task.getResult()) {
                                                                    i = i + 1;
                                                                    details=document.getData();
                                                                }
                                                                if (i == 0) {
                                                                    //No records again
                                                                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                                                                    db.collection("User Details")
                                                                    .whereEqualTo("id",Login.this.loginDetails.username.toUpperCase())
                                                                            .get()
                                                                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                                    if (task.isSuccessful()) {
                                                                                        int i = 0;
                                                                                        Map<String,Object> details= new HashMap<String,Object>();
                                                                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                                                                            i = i + 1;
                                                                                            details=document.getData();
                                                                                        }
                                                                                        if (i == 0) {
                                                                                            //No records again
                                                                                            Login.this.pd.dismiss();
                                                                                            Toast.makeText(Login.this,"Incorrect Username or Password",Toast.LENGTH_LONG).show();
                                                                                        } else {
                                                                                            makemove(details);
                                                                                        }
                                                                                    }
                                                                                    else{
                                                                                        Login.this.pd.dismiss();
                                                                                        Toast.makeText(Login.this,"An Unknown Error occured. Please try again. in id",Toast.LENGTH_LONG).show();
                                                                                    }
                                                                                }
                                                                            });
                                                                } else {
                                                                    makemove(details);
                                                                }
                                                            } else {
                                                                Login.this.pd.dismiss();
                                                                Toast.makeText(Login.this,"An Unknown Error occured. Please try again. in mobile",Toast.LENGTH_LONG).show();
                                                            }
                                                        }
                                                    });
                                        }
                                        else{
                                            makemove(details);
                                        }
                                    } else {
                                        Toast.makeText(Login.this,"An Unknown Error occured. Please try again later. email",Toast.LENGTH_LONG).show();
                                        Login.this.pd.dismiss();
                                    }
                                }
                            });
                }
            }
        });
        signup.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent register = new Intent(Login.this, Registration.class);
                startActivity(register);
            }
        });
    }
    boolean validate(){
        if(loginDetails.username.equals("")){
            username.requestFocus();
            Toast.makeText(Login.this,"Username is required!",Toast.LENGTH_LONG).show();
            return false;
        }
        else if(loginDetails.password.equals("")){
            password.requestFocus();
            Toast.makeText(Login.this,"Password is required!",Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }
    void store(){
        loginDetails.username = username.getText().toString().trim();
        loginDetails.password = password.getText().toString().trim();
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

    void makemove(Map<String,Object> details){
        //details
        if(loginDetails.password.equals((String)(details.get("password")))){
            //correct details
            pd.dismiss();
            String type = details.get("type").toString();
            SharedPreferences sp=getSharedPreferences("Login", MODE_PRIVATE);
            SharedPreferences.Editor editor=sp.edit();
            editor.putString("firstname",(String)details.get("firstname"));
            editor.putString("lastname",(String)details.get("lastname"));
            editor.putString("mobile",(String)details.get("mobile"));
            editor.putString("email",(String)details.get("email"));
            editor.putString("address",(String)details.get("address"));
            editor.putString("password",(String)details.get("password"));
            if(type.equals("Student")){
                editor.putString("type","student");
                editor.putString("id",(String)details.get("id"));
                editor.commit();
                Intent student = new Intent(Login.this, StudentFeedbackForm.class);
                startActivity(student);
            }
            else if(type.equals("Alumni")){
                editor.putString("type","alumni");
                editor.putString("id",(String)details.get("id"));
                editor.commit();
                Intent alumni = new Intent(Login.this, AlumniFeedbackForm.class);
                startActivity(alumni);
            }
            else if(type.equals("Employer")){
                editor.putString("type","employer");
                editor.commit();
                Intent employer = new Intent(Login.this, EmployerFeedbackForm.class);
                startActivity(employer);
            }
            else{
                editor.putString("type","faculty");
                editor.putString("id",(String)details.get("id"));
                editor.commit();
                Intent faculty = new Intent(Login.this,FacultyFeedbackForm.class);
                startActivity(faculty);
            }
        }
        else{
            pd.dismiss();
            Toast.makeText(Login.this, "Incorrect Username or Password", Toast.LENGTH_LONG).show();
        }
    }
}

class LoginDetails{
    String username;
    String password;
}
