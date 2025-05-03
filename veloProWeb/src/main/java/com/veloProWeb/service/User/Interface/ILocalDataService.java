package com.veloProWeb.service.User.Interface;

import com.veloProWeb.model.entity.User.LocalData;

import java.util.List;

public interface ILocalDataService {
    void updateData(LocalData localData);
    List<LocalData> getData();
    List<LocalData> getDataToEmail();
}
