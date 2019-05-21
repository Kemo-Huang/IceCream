package com.example.icecream.ui.activity;

import android.app.Fragment;
import android.content.Context;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;

import android.os.AsyncTask;

import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.RemoteViews;

import com.example.icecream.R;
import com.example.icecream.ui.fragment.PlayFragment;
import com.example.icecream.ui.fragment.ResourceFragment;
import com.example.icecream.ui.component.menu.DrawerAdapter;
import com.example.icecream.ui.component.menu.DrawerItem;
import com.example.icecream.ui.component.menu.SimpleItem;
import com.example.icecream.ui.component.menu.SpaceItem;
//import com.example.icecream.search.BoilerplateActivity;
import com.example.icecream.utils.AppViewModel;
import com.example.icecream.utils.HttpHandler;
import com.yarolegovich.slidingrootnav.SlidingRootNav;
import com.yarolegovich.slidingrootnav.SlidingRootNavBuilder;

import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;

import okhttp3.OkHttpClient;


/**
 * The main activity.
 * <p>
 * Reference: https://blog.csdn.net/u013926110/article/details/46945199
 * @author Penna
 * @version V1.0
 */
public class MainActivity extends AppCompatActivity
    implements View.OnClickListener,
    DrawerAdapter.OnItemSelectedListener,
    ResourceFragment.MusicConnector {
  // TODO: bind the speaker service here but not playfragment.

  private static final int POS_DASHBOARD = 0;
  private static final int POS_ACCOUNT = 1;
  private static final int POS_MESSAGES = 2;
  private static final int POS_CART = 3;
  private static final int POS_LOGOUT = 5;
  private static final String TAG = "MainActivity";

  private Toolbar toolbar;
  private int toolbarMargin;

  private String[] screenTitles;
  private Drawable[] screenIcons;

  private SlidingRootNav slidingRootNav;
  private DrawerAdapter adapter;
  private ViewPager viewPager;


  private NotificationManager musicBarManage;
  private Notification notify;
  private RemoteViews remoteViews;

  private String phone;
  private HttpHandler httpHandler;
  private AppViewModel viewModel;

  /**
   * connection between UI and Repository.
   */
//    private AppViewModel mViewMode;
  @Override
  protected void onCreate(final Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    // get phone from login
    phone = getIntent().getStringExtra(Intent.EXTRA_TEXT);

    // 定义数据
    final Map<Integer, android.support.v4.app.Fragment> data = new TreeMap<>();
    data.put(0, ResourceFragment.newInstance());
    data.put(1, PlayFragment.newInstance());

    // 找到ViewPager
    viewPager = (ViewPager) findViewById(R.id.view_pager);

    // 为ViewPager配置Adapter
    viewPager.setAdapter(
        new FragmentStatePagerAdapter(getSupportFragmentManager()) {
          @Override
          public android.support.v4.app.Fragment getItem(int position) {
            return data.get(position);
          }

          @Override
          public int getCount() {
            return data.size();
          }
        });

//    Toolbar toolbar = findViewById(R.id.toolbar);
//    setSupportActionBar(toolbar);

    slidingRootNav =
        new SlidingRootNavBuilder(this)
            .withToolbarMenuToggle(toolbar)
            .withMenuOpened(false)
            .withContentClickableWhenMenuOpened(false)
            .withSavedState(savedInstanceState)
            .withMenuLayout(R.layout.menu_left_drawer)
            .inject();

    screenIcons = loadScreenIcons();
    screenTitles = loadScreenTitles();

    adapter =
        new DrawerAdapter(
            Arrays.asList(
                createItemFor(POS_DASHBOARD).setChecked(true),
                createItemFor(POS_ACCOUNT),
                createItemFor(POS_MESSAGES),
                createItemFor(POS_CART),
                new SpaceItem(48),
                createItemFor(POS_LOGOUT)));
    adapter.setListener(this);

    RecyclerView draw_list = findViewById(R.id.draw_list);

    draw_list.setNestedScrollingEnabled(false);
    draw_list.setLayoutManager(new LinearLayoutManager(this));
    draw_list.setAdapter(adapter);

    adapter.setSelected(POS_DASHBOARD);

    // http
    httpHandler = new HttpHandler(new OkHttpClient(), this);

    // view model
    viewModel = new AppViewModel(getApplication());
  }


  @Override
  protected void onDestroy() {
    super.onDestroy();
  }


  /**
   * This method is invoked from resource fragment to set up the customized Toolbar.
   *
   * @param tb : The instance of SimpleToolbar
   */

  public void setUpToolbar(Toolbar tb) {
//    toolbar = tb;
//    setSupportActionBar(toolbar);
//
//    toolbarMargin = getResources().getDimensionPixelSize(R.dimen.toolbarMargin);
//    toolbar.setOnClickListener(v -> showKeyboard());
//    super.setUpToolbar(toolbar);
    toolbar = tb;
//    toolbar.setLogo(R.mipmap.logo);
//    getSupportActionBar().setDisplayHomeAsUpEnabled(false);
////    getSupportActionBar().setTitle("");
  }

  @Override
  public void onItemSelected(int position) {
    if (position == POS_LOGOUT) {
      login();
      adapter.setSelected(POS_DASHBOARD);
//      finish();
    }
    slidingRootNav.closeMenu();
//    Fragment selectedScreen = CenteredTextFragment.createFor(screenTitles[position]);
//    showFragment(selectedScreen);

    Log.i("Draw", "" + position);
  }

  private void login() {
    final Context context = this;
    final Class destActivity = LoginActivity.class;
    final Intent registerIntent = new Intent(context, destActivity);
    startActivity(registerIntent);
  }

  /**
   * This method is to switch fragment according to the selected draw item.
   *
   * @param fragment : The destined fragment.
   */
  private void showFragment(Fragment fragment) {
    getFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();
  }

  /**
   * This method creates the draw items.
   *
   * @param position : create item by index to initialize.
   * @return com.example.icecream.ui.component.menu.DrawerItem
   */
  private DrawerItem createItemFor(int position) {
    return new SimpleItem(screenIcons[position], screenTitles[position])
        .withIconTint(color(R.color.textColorSecondary))
        .withTextTint(color(R.color.textColorPrimary))
        .withSelectedIconTint(color(R.color.colorAccent))
        .withSelectedTextTint(color(R.color.colorAccent));
  }

  private String[] loadScreenTitles() {
    return getResources().getStringArray(R.array.ld_activityScreenTitles);
  }

  private Drawable[] loadScreenIcons() {
    TypedArray ta = getResources().obtainTypedArray(R.array.ld_activityScreenIcons);
    Drawable[] icons = new Drawable[ta.length()];
    for (int i = 0; i < ta.length(); i++) {
      int id = ta.getResourceId(i, 0);
      if (id != 0) {
        icons[i] = ContextCompat.getDrawable(this, id);
      }
    }
    ta.recycle();
    return icons;
  }

  @ColorInt
  private int color(@ColorRes int res) {
    return ContextCompat.getColor(this, res);
  }

  @Override
  public void onClick(View v) {
    switch (v.getId()) {
      // do nothing
    }
  }


  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.menu_main, menu);
    return true;
  }

  @Override
  public void onArticleSelect() {
    Log.i(TAG, "onArticleSelect: Go fuck your self");
    viewPager.setCurrentItem(1, true);
  }

  @Override
  public void onBackPressed(){
    super.onBackPressed();
  }

  private static class UpdateRssFeedsAsyncTask extends AsyncTask<String, Void, HttpHandler.ResponseState> {

    private WeakReference<MainActivity> activityReference;

    // only retain a weak reference to the activity
    UpdateRssFeedsAsyncTask(MainActivity context) {
      activityReference = new WeakReference<>(context);
    }

    @Override
    protected HttpHandler.ResponseState doInBackground(String... params) {
      MainActivity activity = activityReference.get();
      if (activity == null || activity.isFinishing()) {
        return null;
      }
      return activity.httpHandler.getUpdateRSSFeedsState(params[0]);
    }

    @Override
    protected void onPostExecute(HttpHandler.ResponseState responseState) {
      MainActivity activity = activityReference.get();
      if (activity == null || activity.isFinishing()) {
        return;
      }
      switch (responseState) {
        case Valid:
          // get those feeds successfully
          Log.i(TAG, "get rss feeds");
          activity.viewModel.setPersonalRssFeeds(activity.httpHandler.getRssFeeds());
          break;
        case InvalidToken:
          // TODO back to login
//          activity.login();
          break;
        case NoSuchUser:
          // TODO back to login
//          activity.login();
          break;
        default:
          break;
      }
    }
  }

  private static class UpdateArticlesAsyncTask extends AsyncTask<String, Void, HttpHandler.ResponseState> {

    private WeakReference<MainActivity> activityReference;

    // only retain a weak reference to the activity
    UpdateArticlesAsyncTask(MainActivity context) {
      activityReference = new WeakReference<>(context);
    }

    @Override
    protected HttpHandler.ResponseState doInBackground(String... params) {
      MainActivity activity = activityReference.get();
      if (activity == null || activity.isFinishing()) {
        return null;
      }
      return activity.httpHandler.getUpdateArticlesState(params[0]);
    }

    @Override
    protected void onPostExecute(HttpHandler.ResponseState responseState) {
      MainActivity activity = activityReference.get();
      if (activity == null || activity.isFinishing()) {
        return;
      }
      switch (responseState) {
        case Valid:
          // get those articles successfully
          Log.i(TAG, "get articles");
          activity.viewModel.setPersonalArticles(activity.httpHandler.getArticles());
          break;
        case InvalidToken:
          // TODO back to login
//          activity.login();
          break;
        case NoSuchUser:
          // TODO back to login
//          activity.login();
          break;
        default:
          break;
      }
    }
  }

  private static class SubscribeAsyncTask extends AsyncTask<String, Void, HttpHandler.ResponseState> {

    private WeakReference<MainActivity> activityReference;

    // only retain a weak reference to the activity
    SubscribeAsyncTask(MainActivity context) {
      activityReference = new WeakReference<>(context);
    }

    @Override
    protected HttpHandler.ResponseState doInBackground(String... params) {
      MainActivity activity = activityReference.get();
      if (activity == null || activity.isFinishing()) {
        return null;
      }
      return activity.httpHandler.getSubscribeFeedState(params[0], params[1]);
    }

    @Override
    protected void onPostExecute(HttpHandler.ResponseState responseState) {
      MainActivity activity = activityReference.get();
      if (activity == null || activity.isFinishing()) {
        return;
      }
      switch (responseState) {
        case Valid:
          Log.i(TAG, "subscribe succeed");
          // TODO
          break;
        case InvalidToken:
          // TODO back to login
//          activity.login();
          break;
        case NoSuchUser:
          // TODO back to login
//          activity.login();
          break;
        default:
          break;
      }
    }
  }

  private static class UnsubscribeAsyncTask extends AsyncTask<String, Void, HttpHandler.ResponseState> {

    private WeakReference<MainActivity> activityReference;

    // only retain a weak reference to the activity
    UnsubscribeAsyncTask(MainActivity context) {
      activityReference = new WeakReference<>(context);
    }

    @Override
    protected HttpHandler.ResponseState doInBackground(String... params) {
      MainActivity activity = activityReference.get();
      if (activity == null || activity.isFinishing()) {
        return null;
      }
      return activity.httpHandler.getUnsubscribeFeedState(params[0], params[1]);
    }

    @Override
    protected void onPostExecute(HttpHandler.ResponseState responseState) {
      MainActivity activity = activityReference.get();
      if (activity == null || activity.isFinishing()) {
        return;
      }
      switch (responseState) {
        case Valid:
          Log.i(TAG, "unsubscribe succeed");
          // TODO
          break;
        case InvalidToken:
          // TODO back to login
//          activity.login();
          break;
        case NoSuchUser:
          // TODO back to login
//          activity.login();
          break;
        default:
          break;
      }
    }
  }

}
