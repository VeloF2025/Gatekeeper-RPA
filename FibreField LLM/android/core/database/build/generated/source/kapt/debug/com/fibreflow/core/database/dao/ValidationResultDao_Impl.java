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
import com.fibreflow.core.database.entities.ValidationResultEntity;
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

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class ValidationResultDao_Impl implements ValidationResultDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<ValidationResultEntity> __insertionAdapterOfValidationResultEntity;

  private final DateConverters __dateConverters = new DateConverters();

  private final EntityDeletionOrUpdateAdapter<ValidationResultEntity> __deletionAdapterOfValidationResultEntity;

  private final EntityDeletionOrUpdateAdapter<ValidationResultEntity> __updateAdapterOfValidationResultEntity;

  private final SharedSQLiteStatement __preparedStmtOfDeleteOldValidationResults;

  public ValidationResultDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfValidationResultEntity = new EntityInsertionAdapter<ValidationResultEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `validation_results` (`validation_id`,`photo_id`,`validation_type`,`is_valid`,`confidence_score`,`validation_data`,`created_at`) VALUES (?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final ValidationResultEntity entity) {
        statement.bindLong(1, entity.getValidationId());
        statement.bindLong(2, entity.getPhotoId());
        if (entity.getValidationType() == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, entity.getValidationType());
        }
        final int _tmp = entity.isValid() ? 1 : 0;
        statement.bindLong(4, _tmp);
        statement.bindDouble(5, entity.getConfidenceScore());
        if (entity.getValidationData() == null) {
          statement.bindNull(6);
        } else {
          statement.bindString(6, entity.getValidationData());
        }
        final Long _tmp_1 = __dateConverters.dateToTimestamp(entity.getCreatedAt());
        if (_tmp_1 == null) {
          statement.bindNull(7);
        } else {
          statement.bindLong(7, _tmp_1);
        }
      }
    };
    this.__deletionAdapterOfValidationResultEntity = new EntityDeletionOrUpdateAdapter<ValidationResultEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `validation_results` WHERE `validation_id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final ValidationResultEntity entity) {
        statement.bindLong(1, entity.getValidationId());
      }
    };
    this.__updateAdapterOfValidationResultEntity = new EntityDeletionOrUpdateAdapter<ValidationResultEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `validation_results` SET `validation_id` = ?,`photo_id` = ?,`validation_type` = ?,`is_valid` = ?,`confidence_score` = ?,`validation_data` = ?,`created_at` = ? WHERE `validation_id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final ValidationResultEntity entity) {
        statement.bindLong(1, entity.getValidationId());
        statement.bindLong(2, entity.getPhotoId());
        if (entity.getValidationType() == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, entity.getValidationType());
        }
        final int _tmp = entity.isValid() ? 1 : 0;
        statement.bindLong(4, _tmp);
        statement.bindDouble(5, entity.getConfidenceScore());
        if (entity.getValidationData() == null) {
          statement.bindNull(6);
        } else {
          statement.bindString(6, entity.getValidationData());
        }
        final Long _tmp_1 = __dateConverters.dateToTimestamp(entity.getCreatedAt());
        if (_tmp_1 == null) {
          statement.bindNull(7);
        } else {
          statement.bindLong(7, _tmp_1);
        }
        statement.bindLong(8, entity.getValidationId());
      }
    };
    this.__preparedStmtOfDeleteOldValidationResults = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM validation_results WHERE timestamp < ?";
        return _query;
      }
    };
  }

  @Override
  public Object insertValidationResult(final ValidationResultEntity result,
      final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfValidationResultEntity.insertAndReturnId(result);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertValidationResults(final List<ValidationResultEntity> results,
      final Continuation<? super List<Long>> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<List<Long>>() {
      @Override
      @NonNull
      public List<Long> call() throws Exception {
        __db.beginTransaction();
        try {
          final List<Long> _result = __insertionAdapterOfValidationResultEntity.insertAndReturnIdsList(results);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteValidationResult(final ValidationResultEntity result,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfValidationResultEntity.handle(result);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateValidationResult(final ValidationResultEntity result,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfValidationResultEntity.handle(result);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteOldValidationResults(final long cutoffDate,
      final Continuation<? super Integer> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Integer>() {
      @Override
      @NonNull
      public Integer call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteOldValidationResults.acquire();
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
          __preparedStmtOfDeleteOldValidationResults.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object getValidationResultById(final String resultId,
      final Continuation<? super ValidationResultEntity> $completion) {
    final String _sql = "SELECT * FROM validation_results WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    if (resultId == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, resultId);
    }
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<ValidationResultEntity>() {
      @Override
      @Nullable
      public ValidationResultEntity call() throws Exception {
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
  public Object getValidationResultsByPhoto(final String photoId,
      final Continuation<? super List<ValidationResultEntity>> $completion) {
    final String _sql = "SELECT * FROM validation_results WHERE photoId = ? ORDER BY timestamp DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    if (photoId == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, photoId);
    }
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<ValidationResultEntity>>() {
      @Override
      @NonNull
      public List<ValidationResultEntity> call() throws Exception {
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
  public Object getValidationResultsByInstallation(final String installationId,
      final Continuation<? super List<ValidationResultEntity>> $completion) {
    final String _sql = "SELECT * FROM validation_results WHERE installationId = ? ORDER BY timestamp DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    if (installationId == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, installationId);
    }
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<ValidationResultEntity>>() {
      @Override
      @NonNull
      public List<ValidationResultEntity> call() throws Exception {
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
  public Object getValidationResultsByStatus(final String status,
      final Continuation<? super List<ValidationResultEntity>> $completion) {
    final String _sql = "SELECT * FROM validation_results WHERE validationStatus = ? ORDER BY timestamp DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    if (status == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, status);
    }
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<ValidationResultEntity>>() {
      @Override
      @NonNull
      public List<ValidationResultEntity> call() throws Exception {
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
  public Object getValidationResultCountByStatus(final String status,
      final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT COUNT(*) FROM validation_results WHERE validationStatus = ?";
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
