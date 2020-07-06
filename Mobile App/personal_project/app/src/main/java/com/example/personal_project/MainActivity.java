package com.example.personal_project;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.label.ImageLabel;
import com.google.mlkit.vision.label.ImageLabeler;
import com.google.mlkit.vision.label.ImageLabeling;
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();
    EditText Username_edit, Password_edit, Search_tag;
    String Username = "", Password;
    LinearLayout search_layout, layout_asdf, signup_layout;
    Button search_button, x1, x2;
    ImageView search_img;
    ImageView[] imgs  = new ImageView[8];
    Uri currentImageUri;
    ImageButton s_img_btn;
    Drawable default_img;
    boolean First = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView title = findViewById(R.id.Title);
        signup_layout = findViewById(R.id.login_layout);
        set_images();
        x1 = findViewById(R.id.x_button);
        x2 = findViewById(R.id.x_button2);
        search_layout = findViewById(R.id.search_layout);
        search_layout.setVisibility(View.GONE);
        layout_asdf = findViewById(R.id.layout_asdf);
        Username_edit = findViewById(R.id.userid);
        Password_edit = findViewById(R.id.password);
        final TextView signup = findViewById(R.id.signup);
        final Toolbar myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        x1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                x1.setVisibility(View.GONE);
                search_layout.setVisibility(View.GONE);
            }
        });
        x2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                x2.setVisibility(View.GONE);
                layout_asdf.setVisibility(View.GONE);
            }
        });

        if(savedInstanceState != null) {
            First = savedInstanceState.getBoolean("First");
            Username = savedInstanceState.getString("Username");
            getSupportActionBar().setTitle(Username);
        }
        x1.setVisibility(View.GONE);
        x2.setVisibility(View.GONE);
        if(First){
            myToolbar.setVisibility(View.GONE);
        } else{
            signup_layout.setVisibility(View.GONE);
            myToolbar.setTitle(Username);

        }


        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent second = new Intent(MainActivity.this, Main2Activity.class);
                startActivity(second);
            }
        });
        Button login = findViewById(R.id.loginButton);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Username = Username_edit.getText().toString();
                Password = Password_edit.getText().toString();
                if(Username.length()==0){
                    Toast.makeText(MainActivity.this, "Wrong Username", Toast.LENGTH_SHORT).show();
                }else {
                DocumentReference docRef = db.collection("users").document(Username);
                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.getResult().exists()){
                            String password = (String) task.getResult().getData().get("password");
                            if (password.equals(Password)){
                                signup_layout.setVisibility(View.GONE);
                                getSupportActionBar().setTitle(Username);
                                myToolbar.setVisibility(View.VISIBLE);
                                First=false;
                            } else {
                                Toast.makeText(MainActivity.this, "Wrong Password", Toast.LENGTH_SHORT).show();
                            }
                        }
                        else{
                            Toast.makeText(MainActivity.this, "Wrong Username", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                }
            }
        });
        Search_tag = findViewById(R.id.search_text);
        search_button = findViewById(R.id.search_button);
        search_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent to_Search = new Intent(MainActivity.this, Search.class);
                String tag = Search_tag.getText().toString();

                to_Search.putExtra("tag", tag);
                search_layout.setVisibility(View.GONE);
                x1.setVisibility(View.GONE);
                x2.setVisibility(View.GONE);
                startActivity(to_Search);

            }
        });

        search_img =findViewById(R.id.search_image);
        default_img = search_img.getDrawable();
        search_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                startActivityForResult(gallery, 333);

            }
        });
        s_img_btn = findViewById(R.id.search_img_btn);
        s_img_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                x1.setVisibility(View.GONE);
                x2.setVisibility(View.GONE);
                startActivityForResult(gallery, 777);
            }
        });
        layout_asdf.setVisibility(View.GONE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu,menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.My_Posts :
                search_layout.setVisibility(View.GONE);
                layout_asdf.setVisibility(View.GONE);
                x1.setVisibility(View.GONE);
                x2.setVisibility(View.GONE);
                Intent myPost = new Intent(MainActivity.this, MyPage.class);
                myPost.putExtra("username",Username);
                startActivity(myPost);
                return true;
            case R.id.search_menu :
                search_img.setImageDrawable(default_img);
                Search_tag.setText("");
                x1.setVisibility(View.VISIBLE);
                x2.setVisibility(View.GONE);
                search_layout.setVisibility(View.VISIBLE);
                layout_asdf.setVisibility(View.GONE);

                return true;
            case R.id.img_search_menu:
                search_layout.setVisibility(View.GONE);
                layout_asdf.setVisibility(View.VISIBLE);
                x1.setVisibility(View.GONE);
                x2.setVisibility(View.VISIBLE);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    public void set_images(){
        imgs[0] = findViewById(R.id.imageView);
        imgs[1] = findViewById(R.id.imageView2);
        imgs[2] = findViewById(R.id.imageView3);
        imgs[3] = findViewById(R.id.imageView4);
        imgs[4] = findViewById(R.id.imageView5);
        imgs[5] = findViewById(R.id.imageView6);
        imgs[6] = findViewById(R.id.imageView7);
        imgs[7] = findViewById(R.id.imageView8);
        db.collection("posts")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        int i = 0;
                        String[] post_id = new String[8];
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            if (i==8){
                                break;
                            }
                            post_id[i] = document.getId() + ".jpg";
                            i += 1;
                        }
                        for (int j = 0 ; j < 8 ; j ++){
                            final int k = j;
                            mStorageRef.child(post_id[j]).getBytes(1024 * 1024).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                                @Override
                                public void onSuccess(byte[] bytes) {
                                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                                    imgs[k].setImageBitmap(bitmap);
                                }
                            });
                        }

                    }
                });
    }
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(data!=null) {
            if (requestCode == 777) {
                currentImageUri = data.getData();
                Intent img_search = new Intent(MainActivity.this, image_search.class);
                img_search.putExtra("uri", currentImageUri.toString());
                startActivity(img_search);
                layout_asdf.setVisibility(View.GONE);


            } else if (requestCode == 333) {
                currentImageUri = data.getData();
                search_img.setImageURI(currentImageUri);
                Bitmap bitmap = ((BitmapDrawable) search_img.getDrawable()).getBitmap();
                InputImage image = InputImage.fromBitmap(bitmap, 0);
                ImageLabeler labeler = ImageLabeling.getClient(ImageLabelerOptions.DEFAULT_OPTIONS);
                labeler.process(image)
                        .addOnSuccessListener(new OnSuccessListener<List<ImageLabel>>() {
                            @Override
                            public void onSuccess(List<ImageLabel> imageLabels) {
                                int i = 0;
                                Search_tag.setText("");
                                for (ImageLabel label : imageLabels) {
                                    i++;
                                    String text = label.getText();
                                    float confidence = label.getConfidence();
                                    if (i <= 1) {
                                        Search_tag.setText(text);
                                    } else {
                                        break;
                                    }
                                }

                            }
                        });
            }
        }
    }
    @Override
    protected void onSaveInstanceState(Bundle outstate){
        super.onSaveInstanceState(outstate);
        outstate.putBoolean("First",First);
        outstate.putString("Username",Username);
    }

}
