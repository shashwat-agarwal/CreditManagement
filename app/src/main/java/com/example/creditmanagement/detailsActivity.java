package com.example.creditmanagement;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import static java.lang.Integer.parseInt;
import static java.lang.Integer.valueOf;

public class detailsActivity extends AppCompatActivity {

    private EditText nameE, emailE, addressE, creditE;


    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference userRef = db.collection("user");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.done:
                done();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void done() {
        nameE = findViewById(R.id.name);
        addressE = findViewById(R.id.address);
        emailE = findViewById(R.id.email);
        creditE = findViewById(R.id.credit);


        String name = nameE.getText().toString().trim();
        String email = emailE.getText().toString().trim();
        String address = addressE.getText().toString().trim();
        int credit = parseInt(creditE.getText().toString());

        if (name.isEmpty() || email.isEmpty()) {
            Log.i("ERROR", "CHECK ONCE");
            Toast.makeText(this, "Enter name and email", Toast.LENGTH_LONG).show();
            return;
        }


        if (creditE.getText().toString().isEmpty()) credit = 0;
        if (address.isEmpty()) address = "Not specified by User";

        UserInformation userinformation = new UserInformation(name, address, email, credit);

        userRef.add(userinformation).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Toast.makeText(getApplicationContext(), "Information Saved...", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(detailsActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
            }
        });

        finish();

    }
}
