package com.anp.services;

import java.util.List;

import com.anp.domain.models.serviceModels.LoggerServiceModel;

public interface LoggerService {

    List<LoggerServiceModel> getAllLogs();

    List<LoggerServiceModel> getLogsByUsername(String username);

    boolean deleteAll();

    boolean deleteByName(String username);

    boolean createLog(String method, String principal, String tableName, String action);
}
