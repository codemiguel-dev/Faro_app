package com.pack.faro.ui.maps;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.pack.faro.MapsBufonActivity;
import com.pack.faro.MapsSemaforoActivity;

import com.pack.faro.databinding.FragmentMapsBinding;

import com.pack.faro.ui.perfiles.PerfilesViewModel;

public class MapsFragment extends Fragment {

    private FragmentMapsBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        PerfilesViewModel perfilesViewModel =
                new ViewModelProvider(this).get(PerfilesViewModel.class);

        binding = FragmentMapsBinding.inflate(inflater, container, false);

        binding.btnmapssemaforo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), MapsSemaforoActivity.class);
                startActivity(intent);
            }
        });

        binding.btnmapsbufon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), MapsBufonActivity.class);
                startActivity(intent);
            }
        });
        View root = binding.getRoot();


        return root;

    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
