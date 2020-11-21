package utils;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import models.L2Update;
import models.Snapshot;

public class JSONUtils {
    public static L2Update toL2Update(String json) {
        Gson gson = new Gson();
        L2Update l2Update = gson.fromJson(json, L2Update.class);
        return l2Update;
    }

    public static Snapshot toSnapshot(String json) {
        Gson gson = new Gson();
        Snapshot snapshot = gson.fromJson(json, Snapshot.class);
        return snapshot;
    }

    public static String getType(String json) {
        JsonElement jsonElement = JsonParser.parseString(json);
        return jsonElement.getAsJsonObject().get("type").getAsString();
    }
}
