package com.veloProWeb.service.data;

import com.veloProWeb.model.dto.data.LocalDataDTO;
import com.veloProWeb.model.entity.data.LocalData;

import java.util.List;

public interface ILocalDataService {
    void updateData(LocalDataDTO localData);
    LocalDataDTO getData();
    LocalData getDataToEmail();
}
