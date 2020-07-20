package com.example.creditmanagement;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class TransactionActivity extends AppCompatActivity {

    TextView displayDetails;
    private String id, str, sender_name;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference userRef = db.collection("user");
    private CollectionReference transactionRef = db.collection("transactions");

    Spinner spinner;
    private int amount, senderCredit;//amount is the credits transferred

    public void transact(View view) {
        EditText amt = findViewById(R.id.editTextAmount);
        if (amt.getText().toString().isEmpty()) {
            Toast.makeText(this, "Please enter valid amount", Toast.LENGTH_SHORT).show();
        } else {
            amount = Integer.parseInt(amt.getText().toString());
            if (senderCredit < amount)
                Toast.makeText(this, "Insufficient Balance !", Toast.LENGTH_SHORT).show();
            else {
                senderCredit -= amount;
                transactionUpdate(str);
                transactionHistory();
                finish();
            }
        }

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction);
        displayDetails = findViewById(R.id.textview_display);
        spinner = findViewById(R.id.spinner);

        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        load();
        addInSpinner();
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                str = adapterView.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }


    private void transactionUpdate(String str) {

        userRef.document(id).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            documentSnapshot.getReference().update("credit", senderCredit);
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


        userRef.whereEqualTo("name", str).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    UserInformation userInformation = documentSnapshot.toObject(UserInformation.class);
                    int creditRecepient = userInformation.getCredit();

                    documentSnapshot.getReference().update("credit", creditRecepient + amount);
                    Toast.makeText(TransactionActivity.this, "Transaction Successful", Toast.LENGTH_SHORT).show();

                }


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(TransactionActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addInSpinner() {
        final List<String> names = new ArrayList<>();

        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, names);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        userRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String name_list = document.getString("name");
                        String id_list = document.getId();
                        if (!id_list.equals(id))
                            names.add(name_list);
                    }
                    adapter.notifyDataSetChanged();
                }
            }
        });
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

                            sender_name = name;
                            senderCredit = credit;
                            displayDetails.setText("Name:  " + name + "\n" + "Email:  " + email + "\n" + "Address: " + address + "\n" + "Credit: " + credit);
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

    @Override
    protected void onStart() {
        super.onStart();
        userRef.addSnapshotListener(this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot queryDocumentSnapshots, FirebaseFirestoreException e) {
                if (e != null) {
                    return;
                }

                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    UserInformation userInformation = documentSnapshot.toObject(UserInformation.class);

                    String name = userInformation.getName();

                }

            }
        });
    }

    private void transactionHistory() {
        Calendar calendar = Calendar.getInstance();
        String currDate = DateFormat.getDateInstance().format(calendar.getTime());
        TransactionHistory history = new TransactionHistory(sender_name, str, "" + amount, currDate);
        transactionRef.add(history)
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                       Log.i("error",e.toString());
                    }
                });
    }
}
