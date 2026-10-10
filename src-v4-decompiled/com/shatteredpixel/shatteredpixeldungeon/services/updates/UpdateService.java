/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.services.updates;

import com.shatteredpixel.shatteredpixeldungeon.services.updates.AvailableUpdateData;

public abstract class UpdateService {
    public abstract boolean supportsUpdatePrompts();

    public abstract boolean supportsBetaChannel();

    public abstract void checkForUpdate(boolean var1, boolean var2, UpdateResultCallback var3);

    public abstract void initializeUpdate(AvailableUpdateData var1);

    public abstract boolean supportsReviews();

    public abstract void initializeReview(ReviewResultCallback var1);

    public abstract void openReviewURI();

    public static abstract class ReviewResultCallback {
        public abstract void onComplete();
    }

    public static abstract class UpdateResultCallback {
        public abstract void onUpdateAvailable(AvailableUpdateData var1);

        public abstract void onNoUpdateFound();

        public abstract void onConnectionFailed();
    }
}

