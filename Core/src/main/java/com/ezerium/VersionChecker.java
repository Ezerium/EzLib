package com.ezerium;

import com.ezerium.annotations.Async;
import com.ezerium.http.HTTPRequest;
import com.ezerium.utils.LoggerUtil;

public class VersionChecker {

    protected static final int VERSION_CODE = 2;

    @Async
    public void versionCheck() {
        HTTPRequest request = new HTTPRequest("https://api.ezerium.com/api/v1/ezlib/version");
        request.fetch((obj) -> {
            String version = obj.get("version").getAsString();
            int versionCode = obj.get("versionCode").getAsInt();

            if (versionCode > VERSION_CODE) {
                LoggerUtil.warn("A new version of EzLib is available! Version: " + version);
            }
        });
    }

}
