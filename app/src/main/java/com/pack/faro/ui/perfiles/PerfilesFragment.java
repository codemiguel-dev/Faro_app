package com.pack.faro.ui.perfiles;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.pack.faro.UserListActivity;
import com.pack.faro.databinding.FragmentGalleryBinding;

public class PerfilesFragment extends Fragment{

    private FragmentGalleryBinding binding;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
  PerfilesViewModel perfilesViewModel =
          new ViewModelProvider(this).get(PerfilesViewModel.class);

  binding = FragmentGalleryBinding.inflate(inflater, container, false);

  binding.btnlistuser.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
          Intent intent = new Intent(getContext(), UserListActivity.class);
          startActivity(intent);
      }
  });
//al parecer este fragmento de código tiene que estar debajo para la función correcta
  View root = binding.getRoot();

  return root;



    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}