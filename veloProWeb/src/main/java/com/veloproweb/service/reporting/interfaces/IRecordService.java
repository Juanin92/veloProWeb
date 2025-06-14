package com.veloproweb.service.reporting.interfaces;

import com.veloproweb.model.dto.reporting.RecordResponseDTO;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

public interface IRecordService {
    void registerEntry(UserDetails userDetails);
    void registerEnd(UserDetails userDetails);
    void registerAction(UserDetails userDetails, String action, String comment);
    void registerActionManual(String username, String action, String comment);
    List<RecordResponseDTO> getAllRecord();
}
