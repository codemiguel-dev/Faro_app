package com.pack.faro.ui.perfil;

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
import com.pack.faro.databinding.FragmentPerfilBinding;

public class PerfilFragment extends Fragment{

    private FragmentPerfilBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
  PerfilViewModel perfilesViewModel =
          new ViewModelProvider(this).get(PerfilViewModel.class);

  binding = FragmentPerfilBinding.inflate(inflater, container, false);

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