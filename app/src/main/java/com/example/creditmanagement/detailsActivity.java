package com.example.creditmanagement;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class detailsActivity extends AppCompatActivity {

    EditText nameE,emailE,addressE,creditE;
    String  name;
    String email;
    String address;
    int credit;
    long maxid=0;
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
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.done) {
            nameE =findViewById(R.id.name);
            addressE=findViewById(R.id.address);
            emailE=findViewById(R.id.email);
            creditE=findViewById(R.id.credit);


            name=nameE.getText().toString().trim();
            email=emailE.getText().toString().trim();
            address=addressE.getText().toString().trim();
            credit=Integer.parseInt(creditE.getText().toString().trim());

            if (TextUtils.isEmpty(name) && TextUtils.isEmpty(email)){
                Toast.makeText(this, "Enter name and email", Toast.LENGTH_LONG).show();
            }
            else {


                UserInformation userinformation=new UserInformation(name,address,email,credit);

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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
