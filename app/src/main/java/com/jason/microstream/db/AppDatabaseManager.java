package com.jason.microstream.db;

import android.text.TextUtils;
import com.jason.microstream.MsApplication;
import com.jason.microstream.account.AccountManager;
import com.jason.microstream.db.entity.generator.DaoMaster;
import com.jason.microstream.db.entity.generator.DaoSession;

import org.greenrobot.greendao.database.Database;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class AppDatabaseManager {
    private static AppDatabaseManager appDatabaseManager;
    private static final String DATABASE_NAME = "APP_USER_DB";
    private AppDatabaseManager() {
    }
    public static AppDatabaseManager getInstance() {
        if (appDatabaseManager == null) {
            synchronized (AppDatabaseManager.class) {
                if (appDatabaseManager == null) {
                    appDatabaseManager = new AppDatabaseManager();
                    return appDatabaseManager;
                }
            }
        }

        return appDatabaseManager;
    }

    DaoMaster mDaoMaster;
    DaoSession mDaoSession;
    private void init() {
        String dbName = DATABASE_NAME + mUserId;
        DaoMaster.OpenHelper helper = new AppDbOpenHelper(MsApplication.getInstance(), dbName, null);
//         helper.getEncryptedWritableDb(key);
        Database db = helper.getWritableDb();
        mDaoMaster = new DaoMaster(db);
        mDaoSession = mDaoMaster.newSession();
    }


    private String mUserId;
    private Lock lock = new ReentrantLock();
    public DaoSession getDaoSession() {
        String userId = AccountManager.get().getUid();
        if (TextUtils.isEmpty(userId)) {
            return null;
        }
        if (TextUtils.isEmpty(mUserId) || !mUserId.equals(userId) || mDaoSession == null) {
            mUserId = userId;
            lock.lock();
            if (mDaoSession == null) {
                init();
            }
            lock.unlock();
        }
        return mDaoSession;
    }
}
