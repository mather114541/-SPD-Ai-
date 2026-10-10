/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.services.updates;

import com.shatteredpixel.shatteredpixeldungeon.services.updates.GitHubUpdates;
import com.shatteredpixel.shatteredpixeldungeon.services.updates.UpdateService;

public class UpdateImpl {
    private static UpdateService updateChecker = new GitHubUpdates();

    public static UpdateService getUpdateService() {
        return updateChecker;
    }

    public static boolean supportsUpdates() {
        return true;
    }
}

