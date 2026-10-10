/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.services.updates;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.shatteredpixel.shatteredpixeldungeon.services.updates.AvailableUpdateData;
import com.shatteredpixel.shatteredpixeldungeon.services.updates.UpdateService;
import com.watabou.noosa.Game;
import com.watabou.utils.Bundle;
import com.watabou.utils.DeviceCompat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.net.ssl.SSLProtocolException;

public class GitHubUpdates
extends UpdateService {
    private static Pattern descPattern = Pattern.compile("(.*?)(\r\n|\n|\r)(\r\n|\n|\r)---", 40);
    private static Pattern versionCodePattern = Pattern.compile("internal version number: ([0-9]*)", 2);
    private static Pattern minAndroidPattern = Pattern.compile("Android .*\\(API ([0-9]*)\\)\\+ Devices", 2);
    private static Pattern minIOSPattern = Pattern.compile("iOS ([0-9]*)\\+ Devices", 2);

    @Override
    public boolean supportsUpdatePrompts() {
        return true;
    }

    @Override
    public boolean supportsBetaChannel() {
        return true;
    }

    @Override
    public void checkForUpdate(boolean useMetered, final boolean includeBetas, final UpdateService.UpdateResultCallback callback) {
        if (!useMetered && !Game.platform.connectedToUnmeteredNetwork()) {
            callback.onConnectionFailed();
            return;
        }
        Net.HttpRequest httpGet = new Net.HttpRequest("GET");
        httpGet.setUrl("https://api.github.com/repos/00-Evan/shattered-pixel-dungeon/releases");
        httpGet.setHeader("Accept", "application/vnd.github.v3+json");
        Gdx.net.sendHttpRequest(httpGet, new Net.HttpResponseListener(){

            @Override
            public void handleHttpResponse(Net.HttpResponse httpResponse) {
                try {
                    Bundle latestRelease = null;
                    int latestVersionCode = Game.versionCode;
                    for (Bundle b : Bundle.read(httpResponse.getResultAsStream()).getBundleArray()) {
                        Matcher minIOS;
                        Matcher minAndroid;
                        int releaseVersion;
                        Matcher m = versionCodePattern.matcher(b.getString("body"));
                        if (!m.find() || (releaseVersion = Integer.parseInt(m.group(1))) <= latestVersionCode || !includeBetas && b.getBoolean("prerelease") || (DeviceCompat.isAndroid() ? (minAndroid = minAndroidPattern.matcher(b.getString("body"))).find() && DeviceCompat.getPlatformVersion() < Integer.parseInt(minAndroid.group(1)) : DeviceCompat.isiOS() && (minIOS = minIOSPattern.matcher(b.getString("body"))).find() && DeviceCompat.getPlatformVersion() < Integer.parseInt(minIOS.group(1)))) continue;
                        latestRelease = b;
                        latestVersionCode = releaseVersion;
                    }
                    if (latestRelease == null) {
                        callback.onNoUpdateFound();
                    } else {
                        AvailableUpdateData update = new AvailableUpdateData();
                        update.versionName = latestRelease.getString("name");
                        update.versionCode = latestVersionCode;
                        Matcher m = descPattern.matcher(latestRelease.getString("body"));
                        m.find();
                        update.desc = m.group(1);
                        update.URL = latestRelease.getString("html_url");
                        callback.onUpdateAvailable(update);
                    }
                }
                catch (Exception e) {
                    Game.reportException(e);
                    callback.onConnectionFailed();
                }
            }

            @Override
            public void failed(Throwable t) {
                if (t instanceof SSLProtocolException) {
                    callback.onNoUpdateFound();
                } else {
                    Game.reportException(t);
                    callback.onConnectionFailed();
                }
            }

            @Override
            public void cancelled() {
                callback.onConnectionFailed();
            }
        });
    }

    @Override
    public void initializeUpdate(AvailableUpdateData update) {
        Game.platform.openURI(update.URL);
    }

    @Override
    public boolean supportsReviews() {
        return false;
    }

    @Override
    public void initializeReview(UpdateService.ReviewResultCallback callback) {
        callback.onComplete();
    }

    @Override
    public void openReviewURI() {
    }
}

