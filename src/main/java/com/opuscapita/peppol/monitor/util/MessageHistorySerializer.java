package com.opuscapita.peppol.monitor.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.opuscapita.peppol.commons.container.state.log.DocumentLog;
import org.springframework.stereotype.Component;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

@Component
public class MessageHistorySerializer {

    private final Gson gson = new GsonBuilder().disableHtmlEscaping().setVersion(1.0).create();

    public List<DocumentLog> fromJson(String json) {
        Type listType = new TypeToken<ArrayList<DocumentLog>>() {}.getType();
        return gson.fromJson(json, listType);
    }

    public String toJson(List<DocumentLog> history) {
        return gson.toJson(history);
    }

}
