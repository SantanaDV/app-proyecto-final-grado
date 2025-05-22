package com.proyecto.facilgimapp.network;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

/**
 * Singleton para exponer si la red/servidor está UP o DOWN.
 */
public class ConnectionState {
    private static final ConnectionState INST = new ConnectionState();
    private final MutableLiveData<Boolean> networkUp = new MutableLiveData<>(true);

    private ConnectionState() {}

    public static ConnectionState get() {
        return INST;
    }

    /**
     * Observa el estado de la conexión (true = hay conexión; false = fallo de red).
     */
    public LiveData<Boolean> isNetworkUp() {
        return networkUp;
    }

    /** Llamar cuando falle la red. */
    public void postDown() {
        networkUp.postValue(false);
    }

    /** Llamar cuando una llamada remota tenga éxito. */
    public void postUp() {
        networkUp.postValue(true);
    }
}
