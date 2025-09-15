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
import com.fibreflow.core.database.entities.SyncQueueEntity;
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
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class SyncQueueDao_Impl implements SyncQueueDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<SyncQueueEntity> __insertionAdapterOfSyncQueueEntity;

  private final DateConverters __dateConverters = new DateConverters();

  private final EntityDeletionOrUpdateAdapter<SyncQueueEntity> __deletionAdapterOfSyncQueueEntity;

  private final EntityDeletionOrUpdateAdapter<SyncQueueEntity> __updateAdapterOfSyncQueueEntity;

  private final SharedSQLiteStatement __preparedStmtOfUpdateSyncStatus;

  private final SharedSQLiteStatement __preparedStmtOfIncrementRetryCount;

  private final SharedSQLiteStatement __preparedStmtOfDeleteCompletedItems;

  public SyncQueueDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfSyncQueueEntity = new EntityInsertionAdapter<SyncQueueEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `sync_queue` (`queue_id`,`entity_type`,`entity_id`,`operation`,`data`,`priority`,`status`,`retry_count`,`max_retries`,`last_attempt`,`next_attempt`,`error_message`,`created_at`,`updated_at`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final SyncQueueEntity entity) {
        if (entity.getQueueId() == null) {
          statement.bindNull(1);
        } else {
          statement.bindString(1, entity.getQueueId());
        }
        if (entity.getEntityType() == null) {
          statement.bindNull(2);
        } else {
          statement.bindString(2, entity.getEntityType());
        }
        if (entity.getEntityId() == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, entity.getEntityId());
        }
        if (entity.getOperation() == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.getOperation());
        }
        if (entity.getData() == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, entity.getData());
        }
        statement.bindLong(6, entity.getPriority());
        if (entity.getStatus() == null) {
          statement.bindNull(7);
        } else {
          statement.bindString(7, entity.getStatus());
        }
        statement.bindLong(8, entity.getRetryCount());
        statement.bindLong(9, entity.getMaxRetries());
        final Long _tmp = __dateConverters.dateToTimestamp(entity.getLastAttempt());
        if (_tmp == null) {
          statement.bindNull(10);
        } else {
          statement.bindLong(10, _tmp);
        }
        final Long _tmp_1 = __dateConverters.dateToTimestamp(entity.getNextAttempt());
        if (_tmp_1 == null) {
          statement.bindNull(11);
        } else {
          statement.bindLong(11, _tmp_1);
        }
        if (entity.getErrorMessage() == null) {
          statement.bindNull(12);
        } else {
          statement.bindString(12, entity.getErrorMessage());
        }
        final Long _tmp_2 = __dateConverters.dateToTimestamp(entity.getCreatedAt());
        if (_tmp_2 == null) {
          statement.bindNull(13);
        } else {
          statement.bindLong(13, _tmp_2);
        }
        final Long _tmp_3 = __dateConverters.dateToTimestamp(entity.getUpdatedAt());
        if (_tmp_3 == null) {
          statement.bindNull(14);
        } else {
          statement.bindLong(14, _tmp_3);
        }
      }
    };
    this.__deletionAdapterOfSyncQueueEntity = new EntityDeletionOrUpdateAdapter<SyncQueueEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `sync_queue` WHERE `queue_id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final SyncQueueEntity entity) {
        if (entity.getQueueId() == null) {
          statement.bindNull(1);
        } else {
          statement.bindString(1, entity.getQueueId());
        }
      }
    };
    this.__updateAdapterOfSyncQueueEntity = new EntityDeletionOrUpdateAdapter<SyncQueueEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `sync_queue` SET `queue_id` = ?,`entity_type` = ?,`entity_id` = ?,`operation` = ?,`data` = ?,`priority` = ?,`status` = ?,`retry_count` = ?,`max_retries` = ?,`last_attempt` = ?,`next_attempt` = ?,`error_message` = ?,`created_at` = ?,`updated_at` = ? WHERE `queue_id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final SyncQueueEntity entity) {
        if (entity.getQueueId() == null) {
          statement.bindNull(1);
        } else {
          statement.bindString(1, entity.getQueueId());
        }
        if (entity.getEntityType() == null) {
          statement.bindNull(2);
        } else {
          statement.bindString(2, entity.getEntityType());
        }
        if (entity.getEntityId() == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, entity.getEntityId());
        }
        if (entity.getOperation() == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.getOperation());
        }
        if (entity.getData() == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, entity.getData());
        }
        statement.bindLong(6, entity.getPriority());
        if (entity.getStatus() == null) {
          statement.bindNull(7);
        } else {
          statement.bindString(7, entity.getStatus());
        }
        statement.bindLong(8, entity.getRetryCount());
        statement.bindLong(9, entity.getMaxRetries());
        final Long _tmp = __dateConverters.dateToTimestamp(entity.getLastAttempt());
        if (_tmp == null) {
          statement.bindNull(10);
        } else {
          statement.bindLong(10, _tmp);
        }
        final Long _tmp_1 = __dateConverters.dateToTimestamp(entity.getNextAttempt());
        if (_tmp_1 == null) {
          statement.bindNull(11);
        } else {
          statement.bindLong(11, _tmp_1);
        }
        if (entity.getErrorMessage() == null) {
          statement.bindNull(12);
        } else {
          statement.bindString(12, entity.getErrorMessage());
        }
        final Long _tmp_2 = __dateConverters.dateToTimestamp(entity.getCreatedAt());
        if (_tmp_2 == null) {
          statement.bindNull(13);
        } else {
          statement.bindLong(13, _tmp_2);
        }
        final Long _tmp_3 = __dateConverters.dateToTimestamp(entity.getUpdatedAt());
        if (_tmp_3 == null) {
          statement.bindNull(14);
        } else {
          statement.bindLong(14, _tmp_3);
        }
        if (entity.getQueueId() == null) {
          statement.bindNull(15);
        } else {
          statement.bindString(15, entity.getQueueId());
        }
      }
    };
    this.__preparedStmtOfUpdateSyncStatus = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE sync_queue SET status = ?, updatedAt = ? WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfIncrementRetryCount = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE sync_queue SET retryCount = retryCount + 1, lastAttemptAt = ?, updatedAt = ? WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteCompletedItems = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM sync_queue WHERE status = 'COMPLETED' AND updatedAt < ?";
        return _query;
      }
    };
  }

  @Override
  public Object insertSyncItem(final SyncQueueEntity item,
      final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfSyncQueueEntity.insertAndReturnId(item);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertSyncItems(final List<SyncQueueEntity> items,
      final Continuation<? super List<Long>> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<List<Long>>() {
      @Override
      @NonNull
      public List<Long> call() throws Exception {
        __db.beginTransaction();
        try {
          final List<Long> _result = __insertionAdapterOfSyncQueueEntity.insertAndReturnIdsList(items);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteSyncItem(final SyncQueueEntity item,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfSyncQueueEntity.handle(item);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateSyncItem(final SyncQueueEntity item,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfSyncQueueEntity.handle(item);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateSyncStatus(final String itemId, final String status, final long timestamp,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfUpdateSyncStatus.acquire();
        int _argIndex = 1;
        if (status == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindString(_argIndex, status);
        }
        _argIndex = 2;
        _stmt.bindLong(_argIndex, timestamp);
        _argIndex = 3;
        if (itemId == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindString(_argIndex, itemId);
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
          __preparedStmtOfUpdateSyncStatus.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object incrementRetryCount(final String itemId, final long timestamp,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfIncrementRetryCount.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, timestamp);
        _argIndex = 2;
        _stmt.bindLong(_argIndex, timestamp);
        _argIndex = 3;
        if (itemId == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindString(_argIndex, itemId);
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
          __preparedStmtOfIncrementRetryCount.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteCompletedItems(final long cutoffDate,
      final Continuation<? super Integer> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Integer>() {
      @Override
      @NonNull
      public Integer call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteCompletedItems.acquire();
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
          __preparedStmtOfDeleteCompletedItems.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object getSyncItemById(final String itemId,
      final Continuation<? super SyncQueueEntity> $completion) {
    final String _sql = "SELECT * FROM sync_queue WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    if (itemId == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, itemId);
    }
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<SyncQueueEntity>() {
      @Override
      @Nullable
      public SyncQueueEntity call() throws Exception {
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
  public Object getPendingSyncItems(final Continuation<? super List<SyncQueueEntity>> $completion) {
    final String _sql = "SELECT * FROM sync_queue WHERE status = 'PENDING' ORDER BY priority DESC, createdAt ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<SyncQueueEntity>>() {
      @Override
      @NonNull
      public List<SyncQueueEntity> call() throws Exception {
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
  public Flow<List<SyncQueueEntity>> getPendingSyncItemsFlow() {
    final String _sql = "SELECT * FROM sync_queue WHERE status = 'PENDING' ORDER BY priority DESC, createdAt ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"sync_queue"}, new Callable<List<SyncQueueEntity>>() {
      @Override
      @NonNull
      public List<SyncQueueEntity> call() throws Exception {
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
  public Object getFailedSyncItems(final Continuation<? super List<SyncQueueEntity>> $completion) {
    final String _sql = "SELECT * FROM sync_queue WHERE status = 'FAILED' ORDER BY retryCount ASC, createdAt ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<SyncQueueEntity>>() {
      @Override
      @NonNull
      public List<SyncQueueEntity> call() throws Exception {
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
  public Object getSyncItemCountByStatus(final String status,
      final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT COUNT(*) FROM sync_queue WHERE status = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    if (status == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, status);
    }
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Integer>() {
      @Override
      @NonNull
      public Integer call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Integer _result;
          if (_cursor.moveToFirst()) {
            final Integer _tmp;
            if (_cursor.isNull(0)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getInt(0);
            }
            _result = _tmp;
          } else {
            _result = null;
          }
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
