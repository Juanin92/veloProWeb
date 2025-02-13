package com.veloProWeb.Service.Record;

import com.veloProWeb.Model.Entity.Record;
import com.veloProWeb.Model.Entity.User.User;

import java.util.List;

public interface IRecordService {
    void registerEntry(User user);
    void registerEnd(User user);
    void registerAction(User user, String action, String comment);
    List<Record> getAllRecord();
}
