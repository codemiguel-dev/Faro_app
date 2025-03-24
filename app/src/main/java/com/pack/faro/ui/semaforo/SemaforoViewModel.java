package com.pack.faro.ui.semaforo;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SemaforoViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public SemaforoViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("semaforo");
    }


    public LiveData<String> getText() {
        return mText;
    }
}