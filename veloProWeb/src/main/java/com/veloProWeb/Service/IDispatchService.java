package com.veloProWeb.Service;

import com.veloProWeb.Model.Entity.Dispatch;

import java.util.List;

public interface IDispatchService {
    List<Dispatch> getDispatches();
    void createDispatch(Dispatch dispatch);
    void handleStatus(Long dispatchID, int action);
}
