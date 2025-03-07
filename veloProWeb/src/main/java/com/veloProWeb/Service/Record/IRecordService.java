package com.veloProWeb.Service.Record;

import com.veloProWeb.Model.Entity.Record;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

public interface IRecordService {
    void registerEntry(UserDetails userDetails);
    void registerEnd(UserDetails userDetails);
    void registerAction(UserDetails userDetails, String action, String comment);
    List<Record> getAllRecord();
}
