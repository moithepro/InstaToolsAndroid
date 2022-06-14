import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.moithepro.instatoolsandroid.R;

import androidx.appcompat.app.AppCompatActivity;

import or.nevet.goraphics.ExceptiOrn;
import or.nevet.goraphics.GOraphicViewBuilder;
import or.nevet.goraphics.GOraphics;
import or.nevet.goraphics.ListActivityGraphics;

public class AddProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ListActivityGraphics graphics = GOraphics.createDynamicVerticalListActivity(this, R.drawable.background1, GOraphics.BackgroundImageScaleType.KeepAspectRatio, 20);
        GOraphics.addOrViewFromTopOfDynamicActivity(graphics, GOraphicViewBuilder.BuildSingleOrViewBodyText(this, 50, "Please enter your instagram details:", Color.BLACK, 25, Color.GRAY));
        try {
            GOraphics.addOrViewToListOfDynamicListActivity(graphics, GOraphicViewBuilder.BuildSingleOrViewEditText(this, 50, "userName:", Color.BLACK, Color.TRANSPARENT, 20, Color.TRANSPARENT));
            GOraphics.addOrViewToListOfDynamicListActivity(graphics, GOraphicViewBuilder.BuildSingleOrViewEditText(this, 50, "password:", Color.BLACK, Color.TRANSPARENT, 20, Color.TRANSPARENT));
            GOraphics.addOrViewFromBottomOfDynamicActivity(graphics, GOraphicViewBuilder.BuildSingleOrViewButton(this,  50, "Next", Color.BLACK, Color.CYAN, 20, Color.TRANSPARENT, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String userName = ((EditText)graphics.getOrViewsList().get(1).getView().getChildAt(0)).getText().toString().trim();
//                    if (isUserExistsOnInstaLoader(userName, password)) {
                        InstagramActivity.setNewUserName(userName);
                        finish();
//                    } else {
//                        graphics.showToastMessage("The details that you entered are wrong");
//                    }
                }
            }));
        } catch (ExceptiOrn exceptiOrn) {
            exceptiOrn.printStackTrace();
        }
    }


//    //TODO: Maytav
//    private boolean isUserExistsOnInstaLoader(String userName, String password) {
//        //TODO: Check the details. If the user exists, return true, else, return false.
//
//    }

}