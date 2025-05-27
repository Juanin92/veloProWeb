package com.veloProWeb.service.data;

import com.veloProWeb.model.entity.data.LocalData;

import java.util.List;

public interface ILocalDataService {
    void updateData(LocalData localData);
    List<LocalData> getData();
    List<LocalData> getDataToEmail();
}
