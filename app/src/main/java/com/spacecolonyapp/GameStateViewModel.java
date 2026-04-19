package com.spacecolonyapp;

import androidx.lifecycle.ViewModel;
import com.spacecolony.backend.ColonyManager;

public class GameStateViewModel extends ViewModel {

    private final ColonyManager colonyManager = new ColonyManager();

    public ColonyManager getColonyManager() {
        return colonyManager;
    }
}