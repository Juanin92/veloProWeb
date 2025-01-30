package com.veloProWeb.Service.User.Interface;

import com.veloProWeb.Model.Entity.LocalData;

import java.util.List;

public interface ILocalDataService {
    void saveData(String name, String phone, String email, String access, String address);
    void updateData(LocalData localData);
    List<LocalData> getData();
}
