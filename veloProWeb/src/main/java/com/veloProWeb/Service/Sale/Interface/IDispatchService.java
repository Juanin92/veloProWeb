package com.veloProWeb.Service.Sale.Interface;

import com.veloProWeb.Model.Entity.Sale.Dispatch;

import java.util.List;

public interface IDispatchService {
    List<Dispatch> getDispatches();
    void createDispatch(Dispatch dispatch);
    void handleStatus(Long dispatchID, int action);
}
