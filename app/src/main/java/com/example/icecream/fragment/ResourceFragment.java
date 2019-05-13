package com.example.icecream.fragment;



import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import com.example.icecream.R;


public class ResourceFragment extends Fragment {

  public static ResourceFragment newInstance() {
    return new ResourceFragment();
  }

  /** use to connect to play fragment through main activity*/
  MusicConnector musicConnector;

  /**
   * this interface is use to connect the play fragment, the main activity should
   * implement it.
   * @author Penna
   */
  public interface MusicConnector {
    public void sendNewMusic();

    public void startPlayer();

    public void stopPlayer();

    public void pausePlayer();
  }

  @Override
  public View onCreateView(
      LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

    View view = inflater.inflate(R.layout.fragment_resource, container, false);
    Button btgoToPersonalPage = view.findViewById(R.id.bt_goToPersonalDetail);
    btgoToPersonalPage.setOnClickListener(v -> System.out.println("Fuck You from resource"));
    return view;
  }
}
