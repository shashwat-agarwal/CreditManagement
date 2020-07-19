package com.example.creditmanagement;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class TransactionActivity extends AppCompatActivity {

    TextView displayDetails;
    private String id;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference userRef = db.collection("user");

    public void transact(View view) {
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction);
        displayDetails = findViewById(R.id.textview_display);
        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        load();
    }

    public void load() {
        userRef.document(id).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            UserInformation userInformation = documentSnapshot.toObject(UserInformation.class);
                            String name = userInformation.getName();
                            String address = userInformation.getAddress();
                            String email = userInformation.getEmail();
                            int credit = userInformation.getCredit();
                            displayDetails.setText("Name:  " + name + "\n" + "Email:  " + email+"\n"+"Address: "+address +"\n"+"Credit: "+  credit);
                        } else {
                            Toast.makeText(TransactionActivity.this, "Document does not exist", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(TransactionActivity.this, "Error!", Toast.LENGTH_SHORT).show();
                        Log.d("TAG", e.toString());
                    }
                });
    }
}
