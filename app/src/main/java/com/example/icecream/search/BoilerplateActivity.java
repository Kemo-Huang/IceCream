package com.example.icecream.search;

import android.app.Activity;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.view.inputmethod.InputMethodManager;

/** Activity containing all boilerplate code not related to the purposes of this demo */
public class BoilerplateActivity extends AppCompatActivity {

  public void hideKeyboard() {
    InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
    imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
  }

  public void showKeyboard() {
    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
    imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);
  }

  public void shareDemo() {
    // shameless self promotion :dance:
    ShareDemo shareDemo = new ShareDemo(this);
    shareDemo.shareDemo();
  }
}
