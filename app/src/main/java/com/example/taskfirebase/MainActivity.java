package com.example.taskfirebase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private FirebaseFirestore objectFirebaseFirestore;
    private CollectionReference ObjectCollectioReference;
    private DocumentReference objectDocumentReference;
    private Dialog objectDialog;
    private EditText empId,empname,empsalary;
    private TextView tv_two;
    private static final String Collection_Name="Faculty";
    private static final String Emp_Name="Employee_name";
    private static final String Emp_salary="Employee_Salary";
    private String allData ="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        objectDialog=new Dialog(this);
        objectDialog.setContentView(R.layout.please_wait);

        empId=findViewById(R.id.EMPIDET);
        empname=findViewById(R.id.EMPNAMEET);
        empsalary=findViewById(R.id.EMPSALARYET);

        tv_two = findViewById(R.id.tv_two);
        tv_two.setMovementMethod(new ScrollingMovementMethod());
        try {
            objectFirebaseFirestore=FirebaseFirestore.getInstance();
            ObjectCollectioReference=objectFirebaseFirestore.collection(Collection_Name);
        }
        catch (Exception e) {
            Toast.makeText(this,e.getMessage(),Toast.LENGTH_LONG).show();
        }
    }

    public void add_values(View v) {

        try {
            tv_two.setText("");
            allData="";
            objectFirebaseFirestore = FirebaseFirestore.getInstance();
            objectFirebaseFirestore.collection(Collection_Name).document(empId.getText().toString()).get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                            if (task.getResult().exists()) {
                                Toast.makeText(MainActivity.this, "You Have Already Created", Toast.LENGTH_SHORT).show();
                            }

                            else
                            {
                                if(!empId.getText().toString().isEmpty() && !empname.getText().toString().isEmpty() && !empsalary.getText().toString().isEmpty()) {
                                    objectDialog.show();
                                    Map<String,Object> objMap=new HashMap<>();
                                    objMap.put(Emp_Name, empname.getText().toString());
                                    objMap.put(Emp_salary, empsalary.getText().toString());
                                    objectFirebaseFirestore.collection(Collection_Name)
                                            .document(empId.getText().toString()).set(objMap)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    objectDialog.dismiss();
                                                    empId.setText("");
                                                    empname.setText("");
                                                    empsalary.setText("");
                                                    empId.requestFocus();
                                                    Toast.makeText(MainActivity.this, "Data Added Successfully", Toast.LENGTH_SHORT).show();
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    objectDialog.dismiss();
                                                    Toast.makeText(MainActivity.this, "Fails To Add Data", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                }
                                else
                                {
                                    Toast.makeText(MainActivity.this, "Please Enter Valid Details", Toast.LENGTH_SHORT).show();
                                }
                            }

                        }
                    });
        }
        catch (Exception e)
        {
            Toast.makeText(this, "Add Values"+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }


    public void  show_all_collection_data(View v)
    {

        try
        {
            allData ="";
            objectDialog.show();
            tv_two.setText("");
            ObjectCollectioReference.get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            objectDialog.dismiss();

                            for (DocumentSnapshot objectDocumentReference : queryDocumentSnapshots) {
                                String EMPID = objectDocumentReference.getId();
                                String EMPName = objectDocumentReference.getString(Emp_Name);
                                String EMPSALARY = objectDocumentReference.getString(Emp_salary);
                                allData += "Employee ID : " + EMPID + '\n' + "Employee Name : " + EMPName + '\n' + "EMPLOYEE Salary : " + EMPSALARY +'\n'+"___________________________________"+'\n' ;
                            }
                            tv_two.setText(allData);
                            Toast.makeText(MainActivity.this,"Retrieve Data Succcessfully",Toast.LENGTH_LONG).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    objectDialog.dismiss();
                    Toast.makeText(MainActivity.this, "Fails to retrieve data:" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        }
        catch(Exception e)
        {
            Toast.makeText(this,e.getMessage(),Toast.LENGTH_LONG).show();
        }
    }

    public void  delete_document(View v)
    {
        try
        {
            if(empId.getText().toString().isEmpty())
            {
                Toast.makeText(this, "Please enter the document id", Toast.LENGTH_SHORT).show();
            }
            else {
                if (!empId.getText().toString().isEmpty()) {
                    objectDocumentReference = objectFirebaseFirestore.collection(Collection_Name)
                            .document(empId.getText().toString());
                    objectDocumentReference.delete()
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    objectDialog.dismiss();
                                    empId.setText("");
                                    Toast.makeText(MainActivity.this, "Do id Deleted", Toast.LENGTH_LONG).show();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    objectDialog.dismiss();
                                    Toast.makeText(MainActivity.this, "Fails To Delete", Toast.LENGTH_LONG).show();
                                }
                            });
                } else {
                    Toast.makeText(this, "Fails to Delete The Document", Toast.LENGTH_LONG);
                }
            }
        }
        catch (Exception e)
        {
            Toast.makeText(this,e.getMessage(),Toast.LENGTH_LONG).show();
        }
    }


    public void  deletecollection(String ID)
    {
        try
        {
            objectDocumentReference = objectFirebaseFirestore.collection(Collection_Name)
                    .document(ID);
            objectDocumentReference.delete()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            objectDialog.dismiss();
                            Toast.makeText(MainActivity.this,"Collection Delete Successfullly",Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            objectDialog.dismiss();
                            Toast.makeText(MainActivity.this,"Fails to Delete",Toast.LENGTH_LONG).show();
                        }
                    });
        }
        catch (Exception e)
        {
            Toast.makeText(this,e.getMessage(),Toast.LENGTH_LONG).show();
        }
    }
    public void delete_all_collection(View v)
    {

        objectDialog.show();
        ObjectCollectioReference.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        objectDialog.dismiss();
                        for (DocumentSnapshot objectDocumentReference : queryDocumentSnapshots) {
                            String std_ID = objectDocumentReference.getId();
                            deletecollection(std_ID);
                        }
                        Toast.makeText(MainActivity.this,"Collection Delete Successfullly",Toast.LENGTH_LONG).show();
                    }

                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                objectDialog.dismiss();
                Toast.makeText(MainActivity.this, "Fails to Delete:" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


    }
}
