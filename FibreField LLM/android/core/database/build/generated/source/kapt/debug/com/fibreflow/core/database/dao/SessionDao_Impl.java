package com.fibreflow.core.database.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.fibreflow.core.database.converters.DateConverters;
import com.fibreflow.core.database.entities.SessionEntity;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Integer;
import java.lang.Long;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class SessionDao_Impl implements SessionDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<SessionEntity> __insertionAdapterOfSessionEntity;

  private final DateConverters __dateConverters = new DateConverters();

  private final EntityDeletionOrUpdateAdapter<SessionEntity> __deletionAdapterOfSessionEntity;

  private final EntityDeletionOrUpdateAdapter<SessionEntity> __updateAdapterOfSessionEntity;

  private final SharedSQLiteStatement __preparedStmtOfDeactivateSession;

  private final SharedSQLiteStatement __preparedStmtOfDeactivateAllSessions;

  private final SharedSQLiteStatement __preparedStmtOfDeleteOldSessions;

  public SessionDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfSessionEntity = new EntityInsertionAdapter<SessionEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `sessions` (`session_id`,`technician_id`,`device_id`,`is_active`,`last_activity`,`created_at`,`expires_at`) VALUES (?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final SessionEntity entity) {
        if (entity.getSessionId() == null) {
          statement.bindNull(1);
        } else {
          statement.bindString(1, entity.getSessionId());
        }
        if (entity.getTechnicianId() == null) {
          statement.bindNull(2);
        } else {
          statement.bindString(2, entity.getTechnicianId());
        }
        if (entity.getDeviceId() == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, entity.getDeviceId());
        }
        final int _tmp = entity.isActive() ? 1 : 0;
        statement.bindLong(4, _tmp);
        final Long _tmp_1 = __dateConverters.dateToTimestamp(entity.getLastActivity());
        if (_tmp_1 == null) {
          statement.bindNull(5);
        } else {
          statement.bindLong(5, _tmp_1);
        }
        final Long _tmp_2 = __dateConverters.dateToTimestamp(entity.getCreatedAt());
        if (_tmp_2 == null) {
          statement.bindNull(6);
        } else {
          statement.bindLong(6, _tmp_2);
        }
        final Long _tmp_3 = __dateConverters.dateToTimestamp(entity.getExpiresAt());
        if (_tmp_3 == null) {
          statement.bindNull(7);
        } else {
          statement.bindLong(7, _tmp_3);
        }
      }
    };
    this.__deletionAdapterOfSessionEntity = new EntityDeletionOrUpdateAdapter<SessionEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `sessions` WHERE `session_id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final SessionEntity entity) {
        if (entity.getSessionId() == null) {
          statement.bindNull(1);
        } else {
          statement.bindString(1, entity.getSessionId());
        }
      }
    };
    this.__updateAdapterOfSessionEntity = new EntityDeletionOrUpdateAdapter<SessionEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `sessions` SET `session_id` = ?,`technician_id` = ?,`device_id` = ?,`is_active` = ?,`last_activity` = ?,`created_at` = ?,`expires_at` = ? WHERE `session_id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final SessionEntity entity) {
        if (entity.getSessionId() == null) {
          statement.bindNull(1);
        } else {
          statement.bindString(1, entity.getSessionId());
        }
        if (entity.getTechnicianId() == null) {
          statement.bindNull(2);
        } else {
          statement.bindString(2, entity.getTechnicianId());
        }
        if (entity.getDeviceId() == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, entity.getDeviceId());
        }
        final int _tmp = entity.isActive() ? 1 : 0;
        statement.bindLong(4, _tmp);
        final Long _tmp_1 = __dateConverters.dateToTimestamp(entity.getLastActivity());
        if (_tmp_1 == null) {
          statement.bindNull(5);
        } else {
          statement.bindLong(5, _tmp_1);
        }
        final Long _tmp_2 = __dateConverters.dateToTimestamp(entity.getCreatedAt());
        if (_tmp_2 == null) {
          statement.bindNull(6);
        } else {
          statement.bindLong(6, _tmp_2);
        }
        final Long _tmp_3 = __dateConverters.dateToTimestamp(entity.getExpiresAt());
        if (_tmp_3 == null) {
          statement.bindNull(7);
        } else {
          statement.bindLong(7, _tmp_3);
        }
        if (entity.getSessionId() == null) {
          statement.bindNull(8);
        } else {
          statement.bindString(8, entity.getSessionId());
        }
      }
    };
    this.__preparedStmtOfDeactivateSession = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE sessions SET isActive = 0, logoutTime = ? WHERE technicianId = ? AND isActive = 1";
        return _query;
      }
    };
    this.__preparedStmtOfDeactivateAllSessions = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE sessions SET isActive = 0, logoutTime = ?";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteOldSessions = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM sessions WHERE logoutTime IS NOT NULL AND logoutTime < ?";
        return _query;
      }
    };
  }

  @Override
  public Object insertSession(final SessionEntity session,
      final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfSessionEntity.insertAndReturnId(session);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertSessions(final List<SessionEntity> sessions,
      final Continuation<? super List<Long>> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<List<Long>>() {
      @Override
      @NonNull
      public List<Long> call() throws Exception {
        __db.beginTransaction();
        try {
          final List<Long> _result = __insertionAdapterOfSessionEntity.insertAndReturnIdsList(sessions);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteSession(final SessionEntity session,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfSessionEntity.handle(session);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateSession(final SessionEntity session,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfSessionEntity.handle(session);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deactivateSession(final String technicianId, final long logoutTime,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeactivateSession.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, logoutTime);
        _argIndex = 2;
        if (technicianId == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindString(_argIndex, technicianId);
        }
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfDeactivateSession.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object deactivateAllSessions(final long logoutTime,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeactivateAllSessions.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, logoutTime);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfDeactivateAllSessions.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteOldSessions(final long cutoffDate,
      final Continuation<? super Integer> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Integer>() {
      @Override
      @NonNull
      public Integer call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteOldSessions.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, cutoffDate);
        try {
          __db.beginTransaction();
          try {
            final Integer _result = _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return _result;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfDeleteOldSessions.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object getSessionById(final String sessionId,
      final Continuation<? super SessionEntity> $completion) {
    final String _sql = "SELECT * FROM sessions WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    if (sessionId == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, sessionId);
    }
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<SessionEntity>() {
      @Override
      @Nullable
      public SessionEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getActiveSessionForTechnician(final String technicianId,
      final Continuation<? super SessionEntity> $completion) {
    final String _sql = "SELECT * FROM sessions WHERE technicianId = ? AND isActive = 1 ORDER BY loginTime DESC LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    if (technicianId == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, technicianId);
    }
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<SessionEntity>() {
      @Override
      @Nullable
      public SessionEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getSessionsForTechnician(final String technicianId,
      final Continuation<? super List<SessionEntity>> $completion) {
    final String _sql = "SELECT * FROM sessions WHERE technicianId = ? ORDER BY loginTime DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    if (technicianId == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, technicianId);
    }
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<SessionEntity>>() {
      @Override
      @NonNull
      public List<SessionEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getActiveSessions(final Continuation<? super List<SessionEntity>> $completion) {
    final String _sql = "SELECT * FROM sessions WHERE isActive = 1 ORDER BY loginTime DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<SessionEntity>>() {
      @Override
      @NonNull
      public List<SessionEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<SessionEntity>> getActiveSessionsFlow() {
    final String _sql = "SELECT * FROM sessions WHERE isActive = 1 ORDER BY loginTime DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"sessions"}, new Callable<List<SessionEntity>>() {
      @Override
      @NonNull
      public List<SessionEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object getActiveSessionCount(final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT COUNT(*) FROM sessions WHERE isActive = 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Integer>() {
      @Override
      @NonNull
      public Integer call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object isTechnicianActive(final String technicianId,
      final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT COUNT(*) FROM sessions WHERE technicianId = ? AND isActive = 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    if (technicianId == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, technicianId);
    }
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Integer>() {
      @Override
      @NonNull
      public Integer call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getSessionsInTimeRange(final long startTime, final long endTime,
      final Continuation<? super List<SessionEntity>> $completion) {
    final String _sql = "SELECT * FROM sessions WHERE loginTime BETWEEN ? AND ? ORDER BY loginTime DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, startTime);
    _argIndex = 2;
    _statement.bindLong(_argIndex, endTime);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<SessionEntity>>() {
      @Override
      @NonNull
      public List<SessionEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getAverageSessionDuration(final Continuation<? super Long> $completion) {
    final String _sql = "SELECT AVG(logoutTime - loginTime) FROM sessions WHERE logoutTime IS NOT NULL AND isActive = 0";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Long>() {
      @Override
      @Nullable
      public Long call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getSessionCountByTechnician(
      final Continuation<? super Map<String, Integer>> $completion) {
    final String _sql = "SELECT technicianId, COUNT(*) as sessionCount FROM sessions GROUP BY technicianId ORDER BY sessionCount DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Map<String, Integer>>() {
      @Override
      @NonNull
      public Map<String, Integer> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
