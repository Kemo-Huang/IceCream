package com.example.icecream.utils;

import android.app.Application;
import android.os.AsyncTask;
import android.util.Log;

import com.example.icecream.database.entity.RssFeed;

import java.util.List;

/**
 * This class is to handle the resource from server and client.
 *
 * @author Kemo
 */
public class ResourceHandler {

  private static volatile ResourceHandler instance;

  private AppViewModel viewModel;
  private HttpHandler httpHandler;
  private static final String TAG = "Resource Handler: ";

  /**
   * Constructor.
   *
   * @param httpHandler http handler.
   * @param viewModel   view model.
   */
  private ResourceHandler(HttpHandler httpHandler,
                          AppViewModel viewModel) {
    this.viewModel = viewModel;
    this.httpHandler = httpHandler;
  }

  /**
   * Gets the singleton instance.
   *
   * @param httpHandler http handler.
   * @param viewModel   app view model.
   * @return instance.
   */
  public static ResourceHandler getInstance(final HttpHandler httpHandler,
                                            final AppViewModel viewModel) {
    if (instance == null) {
      synchronized (ResourceHandler.class) {
        if (instance == null) {
          instance = new ResourceHandler(httpHandler, viewModel);
        }
      }
    }
    return instance;
  }

  /**
   * Gets the speech url by article id.
   *
   * @param id article id.
   * @return url.
   */
  public static String getSpeechFileUrl(final Long id, Application application) {
    return application.getFilesDir() + "/speech/" + id + ".mp3";
  }

  /**
   * Updates all the available feeds.
   */
  public void updateAllRssFeeds() {
    new UpdateAllFeedsAsyncTask(this).execute();
  }

  /**
   * Updates the newest common articles.
   */
  public void updateCommonArticles() {
    new UpdateCommonArticlesAsyncTask(this).execute();
  }

  /**
   * Download speech for article id.
   *
   * @param id article id.
   */
  public void downloadSpeech(final Long id) {
    new UpdateSpeechAsyncTask(this).execute(id);
  }

  /**
   * Update user subscribed RSS feeds and articles.
   *
   * @param phone user phone.
   */
  public void updatePersonalResources(final String phone) {
    new UpdatePersonalResourcesAsyncTask(this).execute(phone);
  }

  /**
   * Subscribes the feed.
   *
   * @param phoneNumber user phone.
   * @param url         feed url.
   */
  public void subscribe(final String phoneNumber, String url) {
    new SubscribeAsyncTask(this).execute(phoneNumber, url);
  }

  /**
   * Unsubscribe the feed.
   *
   * @param phoneNumber user phone.
   * @param url         feed url.
   */
  public void unsubscribe(final String phoneNumber, String url) {
    new UnsubscribeAsyncTask(this).execute(phoneNumber, url);
  }

  /**
   * Load the RSS feeds.
   *
   * @param phone user phone.
   */
  public void loadRssFeeds(String phone) {
    viewModel.loadRssFeedsByPhone(phone);
  }

  /**
   * Load the articles.
   *
   * @param phone phone.
   */
  public void loadArticles(String phone) {
    viewModel.loadArticlesByPhone(phone);
  }


  private static class UpdateAllFeedsAsyncTask
      extends AsyncTask<Void, Void, HttpHandler.ResponseState> {

    private AppViewModel viewModel;

    private HttpHandler httpHandler;

    UpdateAllFeedsAsyncTask(ResourceHandler resourceHandler) {
      viewModel = resourceHandler.viewModel;
      httpHandler = resourceHandler.httpHandler;
    }

    @Override
    protected HttpHandler.ResponseState doInBackground(Void... params) {
      return httpHandler.getUpdateAllFeedsState();
    }

    @Override
    protected void onPostExecute(HttpHandler.ResponseState responseState) {
      if (responseState == HttpHandler.ResponseState.Valid) {
        // get those feeds successfully
        Log.i(TAG, "inserting all rss feeds");
        List<RssFeed> list = httpHandler.getAllRssFeeds();
        RssFeed[] arr = list.toArray(new RssFeed[0]);
        viewModel.insertAllRssFeeds(arr);
      }
    }
  }

  private static class UpdateCommonArticlesAsyncTask
      extends AsyncTask<Void, Void, HttpHandler.ResponseState> {

    private AppViewModel viewModel;

    private HttpHandler httpHandler;

    UpdateCommonArticlesAsyncTask(ResourceHandler resourceHandler) {
      viewModel = resourceHandler.viewModel;
      httpHandler = resourceHandler.httpHandler;
    }

    @Override
    protected HttpHandler.ResponseState doInBackground(Void... params) {
      return httpHandler.getUpdateCommonArticlesState();
    }

    @Override
    protected void onPostExecute(HttpHandler.ResponseState responseState) {
      if (responseState == HttpHandler.ResponseState.Valid) {
        // get those articles successfully
        viewModel.setCommonArticles(httpHandler.getCommonArticles());
      }
    }
  }


  private static class UpdatePersonalResourcesAsyncTask
      extends AsyncTask<String, Void, HttpHandler.ResponseState> {
    private AppViewModel viewModel;
    private HttpHandler httpHandler;
    private String phone;

    UpdatePersonalResourcesAsyncTask(ResourceHandler resourceHandler) {
      viewModel = resourceHandler.viewModel;
      httpHandler = resourceHandler.httpHandler;
    }

    @Override
    protected HttpHandler.ResponseState doInBackground(String... params) {
      phone = params[0];
      HttpHandler.ResponseState responseState = httpHandler.getUpdateRssFeedsState(phone);
      if (responseState == HttpHandler.ResponseState.Valid) {
        responseState = httpHandler.getUpdateArticlesState(phone);
      }
      return responseState;
    }

    @Override
    protected void onPostExecute(HttpHandler.ResponseState responseState) {
      switch (responseState) {
        case Valid:
          Log.i(TAG, "successfully gets personal rss feeds and articles");
          viewModel.setPersonalRssFeeds(httpHandler.getPersonalRssFeeds());
          viewModel.setPersonalArticles(httpHandler.getPersonalArticles());
          // store
          viewModel.insertPersonalRssFeedsArticles(
              phone,
              httpHandler.getPersonalRssFeeds(),
              httpHandler.getPersonalArticles()
          );
          break;
        case InvalidToken:
          // TODO back to login
          break;
        case NoSuchUser:
          // TODO back to login
          break;
        default:
          break;
      }
    }
  }

  private static class SubscribeAsyncTask
      extends AsyncTask<String, Void, HttpHandler.ResponseState> {
    private String phone;
    private String rssFeedUrl;
    private AppViewModel viewModel;
    private HttpHandler httpHandler;

    SubscribeAsyncTask(ResourceHandler resourceHandler) {
      viewModel = resourceHandler.viewModel;
      httpHandler = resourceHandler.httpHandler;
    }

    @Override
    protected HttpHandler.ResponseState doInBackground(String... params) {
      phone = params[0];
      rssFeedUrl = params[1];
      return httpHandler.getSubscribeFeedState(phone, rssFeedUrl);
    }

    @Override
    protected void onPostExecute(HttpHandler.ResponseState responseState) {
      switch (responseState) {
        case Valid:
          viewModel.subscribe(phone, rssFeedUrl);
          Log.i(TAG, "subscribe succeed");
          break;
        case InvalidToken:
          // TODO back to login
          break;
        case NoSuchUser:
          // TODO back to login
          break;
        default:
          break;
      }
    }
  }

  private static class UnsubscribeAsyncTask
      extends AsyncTask<String, Void, HttpHandler.ResponseState> {
    private String phone;
    private String rssFeedUrl;
    private AppViewModel viewModel;
    private HttpHandler httpHandler;

    UnsubscribeAsyncTask(ResourceHandler resourceHandler) {
      viewModel = resourceHandler.viewModel;
      httpHandler = resourceHandler.httpHandler;
    }

    @Override
    protected HttpHandler.ResponseState doInBackground(String... params) {
      phone = params[0];
      rssFeedUrl = params[1];
      return httpHandler.getUnsubscribeFeedState(phone, rssFeedUrl);
    }

    @Override
    protected void onPostExecute(HttpHandler.ResponseState responseState) {
      switch (responseState) {
        case Valid:
          viewModel.unsubscribe(phone, rssFeedUrl);
          Log.i(TAG, "unsubscribe succeed");
          break;
        case InvalidToken:
          // TODO back to login
          break;
        case NoSuchUser:
          // TODO back to login
          break;
        default:
          break;
      }
    }
  }

  private static class UpdateSpeechAsyncTask extends AsyncTask<Long, Void, String> {
    private HttpHandler httpHandler;
    private AppViewModel viewModel;
    private Long id;

    UpdateSpeechAsyncTask(ResourceHandler resourceHandler) {
      httpHandler = resourceHandler.httpHandler;
      viewModel = resourceHandler.viewModel;
    }

    @Override
    protected String doInBackground(Long... params) {
      id = params[0];
      httpHandler.getUpdateSpeech(id);
      return httpHandler.getUpdateSpeechInfo(id);
    }

    @Override
    protected void onPostExecute(String result) {
      viewModel.updateArticleParagraph(id, result);
    }
  }

}
