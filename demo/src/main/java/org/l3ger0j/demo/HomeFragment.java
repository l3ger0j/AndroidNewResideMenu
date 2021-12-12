package org.l3ger0j.demo;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import org.l3ger0j.library.ResideNewMenu;

import java.util.Objects;

public class HomeFragment extends Fragment {

    private View parentView;
    private ResideNewMenu resideMenu;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        parentView = inflater.inflate(R.layout.home, container, false);
        setUpViews();
        return parentView;
    }

    private void setUpViews() {
        MenuActivity parentActivity = (MenuActivity) getActivity();
        resideMenu = Objects.requireNonNull(parentActivity).getResideMenu();

        parentView.findViewById(R.id.btn_open_menu).setOnClickListener(view ->
                resideMenu.openMenu(ResideNewMenu.DIRECTION_LEFT));

        // add gesture operation's ignored views
        FrameLayout ignored_view = parentView.findViewById(R.id.ignored_view);
        resideMenu.addIgnoredView(ignored_view);
    }

}
