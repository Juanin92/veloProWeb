package com.veloproweb.service.data;

import com.veloproweb.model.dto.data.LocalDataDTO;
import com.veloproweb.model.entity.data.LocalData;

public interface ILocalDataService {
    void updateData(LocalDataDTO localData);
    LocalDataDTO getData();
    LocalData getDataToEmail();
}
