package com.veloProWeb.Service.User.Interface;

import com.veloProWeb.Model.Entity.User.LocalData;

import java.util.List;

public interface ILocalDataService {
    void updateData(LocalData localData);
    List<LocalData> getData();
}
