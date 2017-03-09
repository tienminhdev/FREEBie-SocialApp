package dev.tienminh.freebie.TabsFragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import dev.tienminh.freebie.R;

/**
 * Created by thang on 23/02/2017.
 */

public class TabTimKiem extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tab_timkiem,container,false);
        return view;
    }
}
