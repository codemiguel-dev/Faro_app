package com.pack.faro.ui.semaforo;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.pack.faro.AgregarSemaforo;
import com.pack.faro.databinding.FragmentSlideshowBinding;

public class SemaforoFragment extends Fragment {


    private FragmentSlideshowBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        SemaforoViewModel semaforoViewModel =
                new ViewModelProvider(this).get(SemaforoViewModel.class);

        binding = FragmentSlideshowBinding.inflate(inflater, container, false);
        binding.ingresarformulario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), AgregarSemaforo.class);
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