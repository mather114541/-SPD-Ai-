/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.services.updates;

import com.shatteredpixel.shatteredpixeldungeon.SPDSettings;
import com.shatteredpixel.shatteredpixeldungeon.services.updates.AvailableUpdateData;
import com.shatteredpixel.shatteredpixeldungeon.services.updates.UpdateService;
import com.watabou.utils.Callback;
import java.util.Date;

public class Updates {
    public static UpdateService service;
    private static Date lastCheck;
    private static final long CHECK_DELAY = 3600000L;
    private static AvailableUpdateData updateData;

    public static boolean supportsUpdates() {
        return service != null;
    }

    public static boolean supportsUpdatePrompts() {
        return Updates.supportsUpdates() && service.supportsUpdatePrompts();
    }

    public static boolean supportsBetaChannel() {
        return Updates.supportsUpdates() && service.supportsBetaChannel();
    }

    public static void checkForUpdate() {
        if (!Updates.supportsUpdatePrompts()) {
            return;
        }
        if (lastCheck != null && new Date().getTime() - lastCheck.getTime() < 3600000L) {
            return;
        }
        if (SPDSettings.betas()) {
            SPDSettings.betas(true);
        }
        service.checkForUpdate(!SPDSettings.WiFi(), SPDSettings.betas(), new UpdateService.UpdateResultCallback(){

            @Override
            public void onUpdateAvailable(AvailableUpdateData update) {
                lastCheck = new Date();
                updateData = update;
            }

            @Override
            public void onNoUpdateFound() {
                lastCheck = new Date();
            }

            @Override
            public void onConnectionFailed() {
                lastCheck = null;
            }
        });
    }

    public static void launchUpdate(AvailableUpdateData data) {
        service.initializeUpdate(data);
    }

    public static boolean updateAvailable() {
        return updateData != null;
    }

    public static AvailableUpdateData updateData() {
        return updateData;
    }

    public static void clearUpdate() {
        updateData = null;
        lastCheck = null;
    }

    public static boolean supportsReviews() {
        return Updates.supportsUpdates() && service.supportsReviews();
    }

    public static void launchReview(final Callback callback) {
        if (Updates.supportsUpdates()) {
            service.initializeReview(new UpdateService.ReviewResultCallback(){

                @Override
                public void onComplete() {
                    callback.call();
                }
            });
        } else {
            callback.call();
        }
    }

    public static void openReviewURI() {
        if (Updates.supportsUpdates()) {
            service.openReviewURI();
        }
    }

    static {
        lastCheck = null;
        updateData = null;
    }
}

