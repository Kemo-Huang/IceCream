package com.example.icecream.searchscreen;

import android.os.Bundle;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewTreeObserver;
import com.example.icecream.R;
import com.example.icecream.search.BoilerplateActivity;
import com.example.icecream.transition.FadeInTransition;
import com.example.icecream.transition.FadeOutTransition;
import com.example.icecream.transition.SimpleTransitionListener;

/**
 * The search activity.
 * @author aaron
 */
public class SearchActivity extends BoilerplateActivity {

  /*
   * The search bar.
   */
  private Searchbar searchbar;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_search);

    searchbar = (Searchbar) findViewById(R.id.search_toolbar);
    setSupportActionBar(searchbar);

    if (isFirstTimeRunning(savedInstanceState)) {
      searchbar.hideContent();

      ViewTreeObserver viewTreeObserver = searchbar.getViewTreeObserver();
      viewTreeObserver.addOnGlobalLayoutListener(
          new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
              searchbar.getViewTreeObserver().removeOnGlobalLayoutListener(this);

              showSearch();
            }

            private void showSearch() {
              TransitionManager.beginDelayedTransition(
                  searchbar, FadeInTransition.createTransition());
              searchbar.showContent();
            }
          });
    }
  }

  private boolean isFirstTimeRunning(Bundle savedInstanceState) {
    return savedInstanceState == null;
  }

  @Override
  public void finish() {
    hideKeyboard();

    exitTransitionWithAction(
        new Runnable() {
          @Override
          public void run() {
            SearchActivity.super.finish();

            // override the system pending transition as we are handling ourselves
            overridePendingTransition(0, 0);
          }
        });
  }

  private void exitTransitionWithAction(final Runnable endingAction) {

    Transition transition =
        FadeOutTransition.withAction(
            new SimpleTransitionListener() {
              @Override
              public void onTransitionEnd(Transition transition) {
                endingAction.run();
              }
            });

    TransitionManager.beginDelayedTransition(searchbar, transition);
    searchbar.hideContent();
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.menu_search, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId() == android.R.id.home) {
      finish();
      return true;
    } else if (item.getItemId() == R.id.action_clear) {
      searchbar.clearText();
      return true;
    }
    return super.onOptionsItemSelected(item);
  }
}
