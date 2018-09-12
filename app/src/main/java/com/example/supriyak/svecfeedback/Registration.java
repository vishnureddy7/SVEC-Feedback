package com.example.supriyak.svecfeedback;

import android.app.ProgressDialog;
import android.content.Intent;
import android.nfc.Tag;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class Registration extends AppCompatActivity {

    Spinner spinner;
    EditText id;
    UserDetails details;
    Button submit;
    EditText firstname,lastname,mobile,email,address,password,cpassword;
    String toast;
    View v;
    ProgressDialog pd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        spinner = (Spinner) findViewById(R.id.userType);
        id = (EditText) findViewById(R.id.id);
        details= new UserDetails();
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                    EditText id1 = (EditText) findViewById(R.id.id);
                    switch (position) {
                        case 1:
                            details.type = "Faculty";
                            id1.setVisibility(View.VISIBLE);
                            id1.setHint("Faculty ID");
                            break;
                        case 3:
                            details.type = "Employer";
                            Registration.this.id.setText("");
                            id1.setVisibility(View.GONE);
                            break;
                        default:
                            id1.setVisibility(View.VISIBLE);
                            id1.setHint("Student ID");
                            details.type = (position == 2) ? "Alumni" : "Student";
                            break;
                    }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                //pass
            }
        });
        submit = (Button)findViewById(R.id.register);
        firstname = (EditText) findViewById(R.id.firstname);
        lastname = (EditText) findViewById(R.id.lastname);
        password = (EditText) findViewById(R.id.password);
        cpassword = (EditText) findViewById(R.id.cpassword);
        address = (EditText) findViewById(R.id.address);
        email = (EditText) findViewById(R.id.email);
        mobile = (EditText) findViewById(R.id.mobile);
        submit.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                fillDetails();
                //if(true){
                if(validate()){
                    //insert into firebase
                    pd = ProgressDialog.show(Registration.this,"Please wait while we register your account","Please wait...");
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    if(!details.type.equals("Employer")){
                        db.collection("User Details")
                                .whereEqualTo("id", details.id)
                                .whereEqualTo("type",details.type)
                                .get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {
                                            int i=0;
                                            for (QueryDocumentSnapshot document : task.getResult()) {
                                                i=i+1;
                                                Log.d("SUCCESS", document.getId() + " => " + document.getData());
                                            }
                                            if(i!=0){
                                                Registration.this.pd.dismiss();
                                                sendToast("User Already Exists!");
                                                focus(id);
                                            }
                                            else{
                                                FirebaseFirestore db = FirebaseFirestore.getInstance();
                                                db.collection("User Details")
                                                        .whereEqualTo("mobile",details.mobile)
                                                        .get()
                                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                if (task.isSuccessful()) {
                                                                    int i = 0;
                                                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                                                        i = i + 1;
                                                                    }
                                                                    if (i != 0) {
                                                                        Registration.this.pd.dismiss();
                                                                        Registration.this.sendToast("Mobile Number is already in use!");
                                                                        Registration.this.focus(Registration.this.email);
                                                                    } else {
                                                                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                                                                        db.collection("User Details")
                                                                                .whereEqualTo("email",details.email)
                                                                                .get()
                                                                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                                    @Override
                                                                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                                        if(task.isSuccessful()){
                                                                                            int i=0;
                                                                                            for(QueryDocumentSnapshot document: task.getResult()){
                                                                                                i=i+1;
                                                                                            }
                                                                                            if(i!=0){
                                                                                                Registration.this.pd.dismiss();
                                                                                                Registration.this.sendToast("Email ID is already in use!");
                                                                                                Registration.this.focus(Registration.this.mobile);
                                                                                            }
                                                                                            else{
                                                                                                Registration.this.registerAccount();
                                                                                            }
                                                                                        }
                                                                                        else{
                                                                                            sendToast("Error While Registering. Please try again later.");
                                                                                            Registration.this.pd.dismiss();
                                                                                        }
                                                                                    }
                                                                                });
                                                                    }
                                                                }
                                                                else{
                                                                    sendToast("Error While Registering. Please try again later.");
                                                                    Registration.this.pd.dismiss();
                                                                }
                                                            }
                                                        });
                                            }
                                        } else {
                                            sendToast("Error While Registering. Please try again later.");
                                            Registration.this.pd.dismiss();
                                        }
                                    }
                                });
                    }
                    else {
                        db.collection("User Details")
                                .whereEqualTo("mobile", details.mobile)
                                .get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {
                                            int i = 0;
                                            for (QueryDocumentSnapshot document : task.getResult()) {
                                                i = i + 1;
                                            }
                                            if (i != 0) {
                                                Registration.this.pd.dismiss();
                                                Registration.this.sendToast("Mobile Number is already in use!");
                                                Registration.this.focus(Registration.this.email);
                                            } else {
                                                FirebaseFirestore db = FirebaseFirestore.getInstance();
                                                db.collection("User Details")
                                                        .whereEqualTo("email", details.email)
                                                        .get()
                                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                if (task.isSuccessful()) {
                                                                    int i = 0;
                                                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                                                        i = i + 1;
                                                                    }
                                                                    if (i != 0) {
                                                                        Registration.this.pd.dismiss();
                                                                        Registration.this.sendToast("Email ID is already in use!");
                                                                        Registration.this.focus(Registration.this.mobile);
                                                                    } else {
                                                                        Registration.this.registerAccount();
                                                                    }
                                                                } else {
                                                                    sendToast("Error While Registering. Please try again later.");
                                                                    Registration.this.pd.dismiss();
                                                                }
                                                            }
                                                        });
                                            }
                                        } else {
                                            sendToast("Error While Registering. Please try again later.");
                                            Registration.this.pd.dismiss();
                                        }
                                    }
                                });
                    }
                }
            }
        });
    }

    public void fillDetails(){
        details.firstname = firstname.getText().toString().trim();
        details.lastname = lastname.getText().toString().trim();
        details.password = password.getText().toString().trim();
        details.cpassword = cpassword.getText().toString().trim();
        details.mobile = mobile.getText().toString().trim();
        details.email = email.getText().toString().trim();
        details.address = address.getText().toString().trim();
        details.id = id.getText().toString().trim().toUpperCase();
        switch (spinner.getSelectedItemPosition()){
            case 1:
                details.type="Faculty";
                break;
            case 3:
                details.type="Employer";
                Registration.this.id.setText("");
                details.id="";
                break;
            case 2:
                details.type="Alumni";
                break;
            case 0:
            default:
                details.type = "Student";
                break;
        }
    }

    public boolean validate(){
        toast = "";
        v=null;
        Pattern VALID_EMAIL_ADDRESS_REGEX =
                Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
        if(details.firstname.length()==0){
            toast+="First name is required!";
            v=firstname;
        }
        else if(details.lastname.length()==0){
            toast+="Last name is required!";
            v=lastname;
        }
        else if(!details.type.equals("Employer") && details.id.length()==0){
            String type = details.type;
            if(details.type.equals("Alumni")){
                type="Student";
            }
            toast+=type+" ID is required!";
            v=id;
        }
        else if(!details.type.equals("Employer") && details.id.length()<8){
            String type = details.type;
            if(details.type.equals("Alumni")){
                type="Student";
            }
            toast+=type+" ID is not valid!";
            v=id;
        }
        else if(details.password.length()==0){
            toast+="Password is required!";
            v=password;
        }
        else if(details.password.length()<8){
            toast+="Password length must be greater than 8!";
            v=password;
        }
        else if(!details.password.equals(details.cpassword)){
            toast+="Passwords did not match!";
            v=cpassword;
        }
        else if(details.mobile.length()==0){
            toast +="Mobile number is required";
            v=mobile;
        }
        else if(details.mobile.length()<10){
            toast+="Invalid mobile number!";
            v=mobile;
        }
        else if(details.email.length()==0){
            toast+="Email ID is required!";
            v=email;
        }
        else if(!VALID_EMAIL_ADDRESS_REGEX .matcher(details.email).find()){
            toast+="Emaild ID is invalid!";
            v=email;
        }
        else if(details.address.length()==0){
            toast+="Address is required!";
            v=address;
        }
        if(!toast.equals("")){
            sendToast(toast);
            focus(v);
            return false;
        }
        return true;
    }

    public Map<String, Object> getUser(){
        Map<String, Object> user = new HashMap<String,Object>();
        user.put("firstname",details.firstname);
        user.put("lastname",details.lastname);
        user.put("email",details.email);
        user.put("id",details.id);
        user.put("mobile",details.mobile);
        user.put("password",details.password);
        user.put("address",details.address);
        user.put("type",details.type);
        user.put("filled",false);
        return user;
    }

    public void sendToast(String text){
        Toast.makeText(Registration.this,text,Toast.LENGTH_LONG).show();
    }

    public void focus(View v){
        v.requestFocus();
    }

    void registerAccount(){
        FirebaseFirestore db=FirebaseFirestore.getInstance();
        db.collection("User Details").document()
                .set(getUser())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        sendToast("Successfully registered.");
                        Registration.this.pd.dismiss();
                        Intent thankyou = new Intent(Registration.this,RegistrationThankyou.class);
                        startActivity(thankyou);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        sendToast("Error occured while Registering. Please try again..");
                        Registration.this.pd.dismiss();
                    }
                });
    }
}

class UserDetails{
    String firstname;
    String lastname;
    String password;
    String cpassword;
    String type;
    String mobile;
    String email;
    String address;
    String id;
}