import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Toast;

import com.moithepro.instatoolsandroid.R;

import java.util.ArrayList;
import java.util.LinkedList;

import or.nevet.goraphics.ExceptiOrn;
import or.nevet.goraphics.GOraphicViewBuilder;
import or.nevet.goraphics.GOraphics;
import or.nevet.goraphics.ListActivityGraphics;


public class InstagramActivity extends AppCompatActivity {

    String userName;
    String password;
    private static String newUserName;
    private ListActivityGraphics graphics;
    private ArrayList<GOraphicViewBuilder.OrView> orViews;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instagram);
        orViews = new ArrayList<>();
        userName = getIntent().getStringExtra("userName");
        password = getIntent().getStringExtra("password");

        graphics = GOraphics.createDynamicVerticalListActivity(this, R.drawable.gray, GOraphics.BackgroundImageScaleType.MatchScreen, 60);
        GOraphicViewBuilder.OrView orView = new GOraphicViewBuilder.OrView(this, Color.BLUE);
        GOraphicViewBuilder.OrView backButton = GOraphicViewBuilder.BuildSingleOrViewButton(this, 0, "Back", Color.WHITE, Color.BLACK, 25, 0, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        GOraphicViewBuilder.OrView newProfileButton = GOraphicViewBuilder.BuildSingleOrViewImageButtonByDrawable(this, 0, R.drawable.plus,  25,  new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InstagramActivity.this, AddProfileActivity.class);
                startActivity(intent);
            }
        });
        View v = backButton.getView().getChildAt(0);
        ((ConstraintLayout)v.getParent()).removeView(v);
        orView.placeView(v, 0);

        View v2 = newProfileButton.getView().getChildAt(0);
        ((ConstraintLayout)v2.getParent()).removeView(v2);
        orView.placeView(v2, 100);
        GOraphics.addOrViewFromTopOfDynamicActivity(graphics, orView);

        GOraphicViewBuilder.OrView orView2 = new GOraphicViewBuilder.OrView(this, Color.TRANSPARENT);
        ScrollView scrollView = new ScrollView(this);
        orView2.placeView(scrollView, 50);

        addProfile(userName, password);
    }

    public void addProfile(String userName, String password) {
        //TODO: get the profile picture url, and the text to be shown.

//        String url = ;
//        String text = ;

        String url = "https://upload.wikimedia.org/wikipedia/commons/thumb/b/b6/Image_created_with_a_mobile_phone.png/1200px-Image_created_with_a_mobile_phone.png";
        String text = "hello";

        GOraphicViewBuilder.OrView orView = GOraphicViewBuilder.AddImageButtonToOrViewPackageByUrl(GOraphicViewBuilder.AddBodyTextToOrViewPackage(GOraphicViewBuilder.BuildEmptyOrViewPackage(this, Color.DKGRAY), text, Color.WHITE, 20), url, 300, 300, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GOraphicViewBuilder.OrView orView1 = GOraphicViewBuilder.BuildSingleOrViewImageViewByUrl(InstagramActivity.this, 0, url, 600, 600, Color.TRANSPARENT);
                View v3 = orView1.getView().getChildAt(0);
                ((ConstraintLayout)v3.getParent()).removeView(v3);
                new AlertDialog.Builder(InstagramActivity.this).setView(v3).create().show();
            }
        });
        try {
            GOraphics.addOrViewToListOfDynamicListActivity(graphics, orView);
        } catch (ExceptiOrn exceptiOrn) {
            exceptiOrn.printStackTrace();
        }
        orViews.add(orView);

    }

    public static void setNewUserName(String userName) {
        newUserName = userName;
    }
}