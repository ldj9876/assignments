package com.example.personal_project;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.label.ImageLabel;
import com.google.mlkit.vision.label.ImageLabeler;
import com.google.mlkit.vision.label.ImageLabeling;
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class image_search extends AppCompatActivity {
    ImageView search_img;
    ImageAdapter adapter;
    GridView gridView;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();
    Uri currenturi;
    Map<String, Double> img_info = new HashMap<String, Double>();
    Boolean check = false;
    TextView txt;
    ArrayList<Double> distances = new ArrayList<Double>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_search);
        search_img = findViewById(R.id.imageView10);
        Intent get_intent = getIntent();
        currenturi = Uri.parse(get_intent.getStringExtra("uri"));
        txt = findViewById(R.id.textView123);
        get_info();





        txt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                Log.d("Tlqkf",txt.getText().toString());
                set_grid();
            }
        });

        adapter = new ImageAdapter(this);
        gridView = (GridView) findViewById(R.id.image_search_grid);

//        gridView.setAdapter(adapter);


    }

    public void get_info(){
        search_img.setImageURI(currenturi);
        Bitmap bitmap = ((BitmapDrawable)search_img.getDrawable()).getBitmap();
        final InputImage image = InputImage.fromBitmap(bitmap,0);
        ImageLabeler labeler = ImageLabeling.getClient(ImageLabelerOptions.DEFAULT_OPTIONS);
        labeler.process(image).addOnSuccessListener(new OnSuccessListener<List<ImageLabel>>() {
            @Override
            public void onSuccess(List<ImageLabel> imageLabels) {
                String txt1="";
                for (ImageLabel label : imageLabels){
                    if (txt1.length()==0){
                        txt1 = label.getText();
                    }
                    img_info.put(label.getText(), (double) label.getConfidence());
                }
                check=true;
                txt.setText(txt1);
            }
        });
    }
    public void set_grid() {
        db.collection("posts").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        QuerySnapshot result = task.getResult();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            double dist = 0;
                            Map<String,Object> info = document.getData();
                            List<String> labels = (List<String>) info.get("class");
                            List<Double> conf = (List<Double>) info.get("conf");
                            for (String key : img_info.keySet()) {
                                double metric = img_info.get(key);
                                dist += metric * metric;
                                for (String label : labels){
                                    if (label.equals(key))
                                        dist -= metric*metric - Math.pow(Math.abs(img_info.get(key) - conf.get(labels.indexOf(label))),2) ;
                                }
                            }
                            distances.add(dist);
                        }
                        Log.d("Tlqkf",Integer.toString(distances.size()));
                        get_data();
                    }
                });
    }
    public void get_data(){
        adapter.clear();
        adapter.notifyDataSetChanged();
        db.collection("posts").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for (QueryDocumentSnapshot document: task.getResult()) {
                    Map<String,Object> info = document.getData();
                    final String id = document.getId() ;
                    final String name = (String) info.get("username") ;
                    final String tags = (String) info.get("tags");

                    mStorageRef.child(id+ ".jpg").getBytes(1024 * 1024).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                        @Override
                        public void onSuccess(byte[] bytes) {
                            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                            adapter.addItem(bitmap,tags,name,id);
                            adapter.notifyDataSetChanged();
                            if (adapter.items.size() == distances.size())
                                start_sorting();
                        }
                    });
                }
            }
        });
    }

    public void start_sorting(){
        for (int i = 0; i<distances.size() ; i++){
            adapter.set_value(i,distances.get(i));
        }
        adapter.sorting();
        gridView.setAdapter(adapter);
    }



}
