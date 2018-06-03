package recognizetext.recognizetext;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextDetector;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {


    @BindView(R.id.img)
    ImageView img;
    @BindView(R.id.snap)
    TextView snap;
    @BindView(R.id.detect)
    TextView detect;
    @BindView(R.id.detectedtext)
    TextView detectedtext;
    Bitmap bmp;
    public static int Result=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        bmp = null;

    }

    @OnClick({R.id.snap, R.id.detect})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.snap:
                Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(i,Result);
                break;
            case R.id.detect:
                if(! bmp.equals(null))
                    detecttext();
                else
                    Toast.makeText(this, "please upload an image first", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void detecttext() {

            FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(bmp);
            FirebaseVisionTextDetector detector = FirebaseVision.getInstance().getVisionTextDetector();
            detector.detectInImage(image).addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
                @Override
                public void onSuccess(FirebaseVisionText firebaseVisionText) {
                    processText(firebaseVisionText);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(MainActivity.this, "text recognition Failed", Toast.LENGTH_SHORT).show();
                }
            });
    }

    private void processText(FirebaseVisionText firebaseVisionText) {
        List<FirebaseVisionText.Block> blocks = firebaseVisionText.getBlocks();
        if (blocks.size() == 0)
            Toast.makeText(this, "no Text in the Image", Toast.LENGTH_SHORT).show();
        else
        {
            String txt = "";
            for (FirebaseVisionText.Block newBlock:firebaseVisionText.getBlocks())
            {
                Rect boundingBox = newBlock.getBoundingBox();
                Point[] cornerPoints = newBlock.getCornerPoints();
                txt = txt+newBlock.getText()+"\n";
            }
            detectedtext.setText(txt);

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Result && resultCode == RESULT_OK)
        {
            Bundle bundle = data.getExtras();
            bmp = (Bitmap) bundle.get("data");
            img.setImageBitmap(bmp);
            detectedtext.setText("");
        }
    }
}
