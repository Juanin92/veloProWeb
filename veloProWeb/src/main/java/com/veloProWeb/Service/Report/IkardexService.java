package com.veloProWeb.Service.Report;

import com.veloProWeb.Model.DTO.KardexRequestDTO;
import com.veloProWeb.Model.Entity.Kardex;

import java.util.List;

public interface IkardexService {
    void addKardex(KardexRequestDTO dto);
    List<Kardex> getAll();
}
