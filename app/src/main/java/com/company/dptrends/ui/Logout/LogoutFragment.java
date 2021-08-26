package com.company.dptrends.ui.Logout;

import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.company.dptrends.MainActivity;
import com.company.dptrends.R;

import io.paperdb.Paper;

public class LogoutFragment extends Fragment {

    //private LogoutViewModel mViewModel;

    private Button yesButton, noButton;

    public static LogoutFragment newInstance() {
        return new LogoutFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_logout, container, false);
        yesButton = view.findViewById(R.id.Logout_yesButton);
        noButton = view.findViewById(R.id.Logout_noButton);
        Paper.init(getActivity());

        yesButton.setOnClickListener(v->{
            Toast.makeText(getActivity(), "See you Again", Toast.LENGTH_SHORT).show();
            Paper.book().destroy();
            Intent i = new Intent(getActivity(), MainActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
        });

        noButton.setOnClickListener(v->{
            Navigation.findNavController(view).navigate(R.id.nav_home);
        });

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //mViewModel = new ViewModelProvider(this).get(LogoutViewModel.class);
        // TODO: Use the ViewModel
    }

}