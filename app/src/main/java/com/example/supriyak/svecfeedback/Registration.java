package com.example.supriyak.svecfeedback;

import android.nfc.Tag;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Pattern;

public class Registration extends AppCompatActivity {

    Spinner spinner;
    EditText id;
    UserDetails details;
    Button submit;
    EditText firstname,lastname,mobile,email,address,password,cpassword;
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
                if(validate()){
                    //insert into firebase
                    sendToast("Success");

                    // Write a message to the database
                    // Write a message to the database
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference myRef = database.getReference("message");

                    myRef.setValue("Hello, World!");
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
        details.id = id.getText().toString().trim();
        switch (spinner.getSelectedItemPosition()){
            case 1:
                details.type="Faculty";
                break;
            case 3:
                details.type="Employer";
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
        String toast = "";
        View v=null;
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

    public void sendToast(String text){
        Toast.makeText(Registration.this,text,Toast.LENGTH_LONG).show();
    }

    public void focus(View v){
        v.requestFocus();
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