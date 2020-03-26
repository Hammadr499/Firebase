package com.example.firebase;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

import java.util.HashMap;
import java.util.Map;


public class MainActivity extends AppCompatActivity {

    private FirebaseFirestore objectFirebase;
    private static String tableName = "studentDetail";

    private EditText ET1, ET2, ET3, ET4;
    private Dialog objdialog;

    private CollectionReference collectionReference;
    private DocumentReference documentReference;

    private TextView textView;
    private Button ADD, Display, Delete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        connect();
    }

    public void connect() {
        try {
            objectFirebase = FirebaseFirestore.getInstance();
            ET1 = findViewById(R.id.documentID);

            ET2 = findViewById(R.id.name);
            ET3 = findViewById(R.id.Rollno);

            ET4 = findViewById(R.id.section);
            objdialog = new Dialog(this);

            objdialog.setContentView(R.layout.dialouge_file);
            objdialog.setCancelable(false);

            textView = findViewById(R.id.TV1);
            ADD = findViewById(R.id.ADD);

            Display = findViewById(R.id.Display);
            Delete = findViewById(R.id.Delete);

            ADD.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addValues();
                }
            });

            Display.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getValues();
                }
            });

            Delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteDocument();
                }
            });
        } catch (Exception e) {
            Toast.makeText(this, "Error connecting" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void addValues() {
        try {
            if (!ET1.getText().toString().isEmpty()
                    && !ET2.getText().toString().isEmpty()
                    && !ET3.getText().toString().isEmpty()
                    && !ET4.getText().toString().isEmpty()) {
                objdialog.show();
                documentReference = objectFirebase.collection(tableName).document(ET1.getText().toString());
                documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (!task.getResult().exists()) {
                            objdialog.show();
                            Map<String, Object> objectMap = new HashMap<>();
                            objectMap.put("Name", ET2.getText().toString());

                            objectMap.put("RollNo", ET3.getText().toString());
                            objectMap.put("Section", ET4.getText().toString());


                            objectFirebase.collection(tableName).document(ET1.getText().toString())
                                    .set(objectMap)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {

                                            objdialog.dismiss();
                                            Toast.makeText(MainActivity.this, "Data Added Successfully", Toast.LENGTH_SHORT).show();
                                            ET1.setText("");
                                            ET2.setText("");

                                            ET3.setText("");
                                            ET4.setText("");

                                            ET1.requestFocus();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            objdialog.dismiss();
                                            Toast.makeText(MainActivity.this, "Data not Added", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        } else {
                            objdialog.dismiss();
                            Toast.makeText(MainActivity.this, "Data is Already Available", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            } else {
                Toast.makeText(MainActivity.this, "Please Fill All Fields", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            objdialog.dismiss();
            Toast.makeText(MainActivity.this, "Exception" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void getValues() {
        objdialog.show();
        try {
            collectionReference = objectFirebase.collection(tableName);
            collectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                    if (e != null) {
                        return;
                    } else {
                        String collection = "";
                        for (QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots) {
                            String documentid = queryDocumentSnapshot.getId();
                            String name = queryDocumentSnapshot.getString("Name");
                            String Rollno = queryDocumentSnapshot.getString("RollNo");
                            String Scetion = queryDocumentSnapshot.getString("Section");
                            collection += "Document ID : " + documentid + "\nName is : " + name + "\nRoll no : " + Rollno + "\nSection : " + Scetion + "\n\n";
                            objdialog.dismiss();
                            textView.setText(collection);
                        }
                    }
                }
            });

        } catch (Exception e) {

        }
    }

    public void deleteDocument() {
        try {

            if (!ET1.getText().toString().isEmpty()) {
                objdialog.show();
                documentReference = objectFirebase.collection(tableName).document(ET1.getText().toString());
                documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.getResult().exists()) {
                            objectFirebase.collection(tableName).document(ET1.getText().toString()).delete()
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            objdialog.dismiss();
                                            Toast.makeText(MainActivity.this, "Document Deleted Successfully", Toast.LENGTH_SHORT).show();
                                            ET1.setText("");
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    objdialog.dismiss();
                                    Toast.makeText(MainActivity.this, "Document Not Deleted" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            objdialog.dismiss();
                            Toast.makeText(MainActivity.this, "Please Enter The Correct ID", Toast.LENGTH_SHORT).show();
                            ET1.setText("");
                        }
                    }

                });

            } else {

                objdialog.dismiss();
                Toast.makeText(MainActivity.this, "Please Enter The Id", Toast.LENGTH_SHORT).show();
                ET1.requestFocus();
            }
        } catch (Exception e) {
            objdialog.dismiss();
            Toast.makeText(MainActivity.this, "Exception" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}

