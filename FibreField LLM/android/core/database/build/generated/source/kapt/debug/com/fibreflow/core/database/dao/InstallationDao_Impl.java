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
import androidx.room.util.StringUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.fibreflow.core.database.converters.DateConverters;
import com.fibreflow.core.database.converters.LocationConverters;
import com.fibreflow.core.database.converters.StatusConverters;
import com.fibreflow.core.database.entities.InstallationEntity;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Integer;
import java.lang.Long;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.StringBuilder;
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
public final class InstallationDao_Impl implements InstallationDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<InstallationEntity> __insertionAdapterOfInstallationEntity;

  private final StatusConverters __statusConverters = new StatusConverters();

  private final DateConverters __dateConverters = new DateConverters();

  private final LocationConverters __locationConverters = new LocationConverters();

  private final EntityDeletionOrUpdateAdapter<InstallationEntity> __deletionAdapterOfInstallationEntity;

  private final EntityDeletionOrUpdateAdapter<InstallationEntity> __updateAdapterOfInstallationEntity;

  private final SharedSQLiteStatement __preparedStmtOfMarkInstallationForSync;

  private final SharedSQLiteStatement __preparedStmtOfMarkInstallationSynced;

  private final SharedSQLiteStatement __preparedStmtOfUpdateInstallationStatus;

  private final SharedSQLiteStatement __preparedStmtOfUpdateInstallationProgress;

  private final SharedSQLiteStatement __preparedStmtOfMarkInstallationCompleted;

  private final SharedSQLiteStatement __preparedStmtOfDeleteOldCompletedInstallations;

  private final SharedSQLiteStatement __preparedStmtOfUpdateValidationIssuesFlag;

  public InstallationDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfInstallationEntity = new EntityInsertionAdapter<InstallationEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `installations` (`installation_id`,`drop_number`,`technician_id`,`status`,`start_time`,`end_time`,`ont_serial`,`speed_test_results`,`photos`,`completed_steps`,`current_step`,`total_steps`,`validation_errors`,`ai_guidance_used`,`manual_override_used`,`created_at`,`updated_at`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final InstallationEntity entity) {
        statement.bindLong(1, entity.getInstallationId());
        if (entity.getDropNumber() == null) {
          statement.bindNull(2);
        } else {
          statement.bindString(2, entity.getDropNumber());
        }
        if (entity.getTechnicianId() == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, entity.getTechnicianId());
        }
        final String _tmp = __statusConverters.fromInstallationStatus(entity.getStatus());
        if (_tmp == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, _tmp);
        }
        final Long _tmp_1 = __dateConverters.dateToTimestamp(entity.getStartTime());
        if (_tmp_1 == null) {
          statement.bindNull(5);
        } else {
          statement.bindLong(5, _tmp_1);
        }
        final Long _tmp_2 = __dateConverters.dateToTimestamp(entity.getEndTime());
        if (_tmp_2 == null) {
          statement.bindNull(6);
        } else {
          statement.bindLong(6, _tmp_2);
        }
        if (entity.getOntSerial() == null) {
          statement.bindNull(7);
        } else {
          statement.bindString(7, entity.getOntSerial());
        }
        if (entity.getSpeedTestResults() == null) {
          statement.bindNull(8);
        } else {
          statement.bindString(8, entity.getSpeedTestResults());
        }
        final String _tmp_3 = __locationConverters.longListToString(entity.getPhotos());
        if (_tmp_3 == null) {
          statement.bindNull(9);
        } else {
          statement.bindString(9, _tmp_3);
        }
        if (entity.getCompletedSteps() == null) {
          statement.bindNull(10);
        } else {
          statement.bindString(10, entity.getCompletedSteps());
        }
        statement.bindLong(11, entity.getCurrentStep());
        statement.bindLong(12, entity.getTotalSteps());
        if (entity.getValidationErrors() == null) {
          statement.bindNull(13);
        } else {
          statement.bindString(13, entity.getValidationErrors());
        }
        final int _tmp_4 = entity.getAiGuidanceUsed() ? 1 : 0;
        statement.bindLong(14, _tmp_4);
        final int _tmp_5 = entity.getManualOverrideUsed() ? 1 : 0;
        statement.bindLong(15, _tmp_5);
        final Long _tmp_6 = __dateConverters.dateToTimestamp(entity.getCreatedAt());
        if (_tmp_6 == null) {
          statement.bindNull(16);
        } else {
          statement.bindLong(16, _tmp_6);
        }
        final Long _tmp_7 = __dateConverters.dateToTimestamp(entity.getUpdatedAt());
        if (_tmp_7 == null) {
          statement.bindNull(17);
        } else {
          statement.bindLong(17, _tmp_7);
        }
      }
    };
    this.__deletionAdapterOfInstallationEntity = new EntityDeletionOrUpdateAdapter<InstallationEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `installations` WHERE `installation_id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final InstallationEntity entity) {
        statement.bindLong(1, entity.getInstallationId());
      }
    };
    this.__updateAdapterOfInstallationEntity = new EntityDeletionOrUpdateAdapter<InstallationEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `installations` SET `installation_id` = ?,`drop_number` = ?,`technician_id` = ?,`status` = ?,`start_time` = ?,`end_time` = ?,`ont_serial` = ?,`speed_test_results` = ?,`photos` = ?,`completed_steps` = ?,`current_step` = ?,`total_steps` = ?,`validation_errors` = ?,`ai_guidance_used` = ?,`manual_override_used` = ?,`created_at` = ?,`updated_at` = ? WHERE `installation_id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final InstallationEntity entity) {
        statement.bindLong(1, entity.getInstallationId());
        if (entity.getDropNumber() == null) {
          statement.bindNull(2);
        } else {
          statement.bindString(2, entity.getDropNumber());
        }
        if (entity.getTechnicianId() == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, entity.getTechnicianId());
        }
        final String _tmp = __statusConverters.fromInstallationStatus(entity.getStatus());
        if (_tmp == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, _tmp);
        }
        final Long _tmp_1 = __dateConverters.dateToTimestamp(entity.getStartTime());
        if (_tmp_1 == null) {
          statement.bindNull(5);
        } else {
          statement.bindLong(5, _tmp_1);
        }
        final Long _tmp_2 = __dateConverters.dateToTimestamp(entity.getEndTime());
        if (_tmp_2 == null) {
          statement.bindNull(6);
        } else {
          statement.bindLong(6, _tmp_2);
        }
        if (entity.getOntSerial() == null) {
          statement.bindNull(7);
        } else {
          statement.bindString(7, entity.getOntSerial());
        }
        if (entity.getSpeedTestResults() == null) {
          statement.bindNull(8);
        } else {
          statement.bindString(8, entity.getSpeedTestResults());
        }
        final String _tmp_3 = __locationConverters.longListToString(entity.getPhotos());
        if (_tmp_3 == null) {
          statement.bindNull(9);
        } else {
          statement.bindString(9, _tmp_3);
        }
        if (entity.getCompletedSteps() == null) {
          statement.bindNull(10);
        } else {
          statement.bindString(10, entity.getCompletedSteps());
        }
        statement.bindLong(11, entity.getCurrentStep());
        statement.bindLong(12, entity.getTotalSteps());
        if (entity.getValidationErrors() == null) {
          statement.bindNull(13);
        } else {
          statement.bindString(13, entity.getValidationErrors());
        }
        final int _tmp_4 = entity.getAiGuidanceUsed() ? 1 : 0;
        statement.bindLong(14, _tmp_4);
        final int _tmp_5 = entity.getManualOverrideUsed() ? 1 : 0;
        statement.bindLong(15, _tmp_5);
        final Long _tmp_6 = __dateConverters.dateToTimestamp(entity.getCreatedAt());
        if (_tmp_6 == null) {
          statement.bindNull(16);
        } else {
          statement.bindLong(16, _tmp_6);
        }
        final Long _tmp_7 = __dateConverters.dateToTimestamp(entity.getUpdatedAt());
        if (_tmp_7 == null) {
          statement.bindNull(17);
        } else {
          statement.bindLong(17, _tmp_7);
        }
        statement.bindLong(18, entity.getInstallationId());
      }
    };
    this.__preparedStmtOfMarkInstallationForSync = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE installations SET needsSync = 1, updatedAt = ? WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfMarkInstallationSynced = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE installations SET needsSync = 0, lastSyncedAt = ? WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfUpdateInstallationStatus = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE installations SET status = ?, updatedAt = ? WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfUpdateInstallationProgress = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE installations SET currentStep = ?, progress = ?, updatedAt = ? WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfMarkInstallationCompleted = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE installations SET status = 'COMPLETED', completedAt = ?, updatedAt = ? WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteOldCompletedInstallations = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM installations WHERE status = 'COMPLETED' AND completedAt < ?";
        return _query;
      }
    };
    this.__preparedStmtOfUpdateValidationIssuesFlag = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE installations SET hasValidationIssues = ?, updatedAt = ? WHERE id = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insertInstallation(final InstallationEntity installation,
      final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfInstallationEntity.insertAndReturnId(installation);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertInstallations(final List<InstallationEntity> installations,
      final Continuation<? super List<Long>> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<List<Long>>() {
      @Override
      @NonNull
      public List<Long> call() throws Exception {
        __db.beginTransaction();
        try {
          final List<Long> _result = __insertionAdapterOfInstallationEntity.insertAndReturnIdsList(installations);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteInstallation(final InstallationEntity installation,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfInstallationEntity.handle(installation);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteInstallations(final List<InstallationEntity> installations,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfInstallationEntity.handleMultiple(installations);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateInstallation(final InstallationEntity installation,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfInstallationEntity.handle(installation);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateInstallations(final List<InstallationEntity> installations,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfInstallationEntity.handleMultiple(installations);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object markInstallationForSync(final String installationId, final long timestamp,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfMarkInstallationForSync.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, timestamp);
        _argIndex = 2;
        if (installationId == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindString(_argIndex, installationId);
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
          __preparedStmtOfMarkInstallationForSync.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object markInstallationSynced(final String installationId, final long timestamp,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfMarkInstallationSynced.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, timestamp);
        _argIndex = 2;
        if (installationId == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindString(_argIndex, installationId);
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
          __preparedStmtOfMarkInstallationSynced.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object updateInstallationStatus(final String installationId, final String status,
      final long timestamp, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfUpdateInstallationStatus.acquire();
        int _argIndex = 1;
        if (status == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindString(_argIndex, status);
        }
        _argIndex = 2;
        _stmt.bindLong(_argIndex, timestamp);
        _argIndex = 3;
        if (installationId == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindString(_argIndex, installationId);
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
          __preparedStmtOfUpdateInstallationStatus.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object updateInstallationProgress(final String installationId, final String currentStep,
      final float progress, final long timestamp, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfUpdateInstallationProgress.acquire();
        int _argIndex = 1;
        if (currentStep == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindString(_argIndex, currentStep);
        }
        _argIndex = 2;
        _stmt.bindDouble(_argIndex, progress);
        _argIndex = 3;
        _stmt.bindLong(_argIndex, timestamp);
        _argIndex = 4;
        if (installationId == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindString(_argIndex, installationId);
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
          __preparedStmtOfUpdateInstallationProgress.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object markInstallationCompleted(final String installationId, final long timestamp,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfMarkInstallationCompleted.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, timestamp);
        _argIndex = 2;
        _stmt.bindLong(_argIndex, timestamp);
        _argIndex = 3;
        if (installationId == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindString(_argIndex, installationId);
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
          __preparedStmtOfMarkInstallationCompleted.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteOldCompletedInstallations(final long cutoffDate,
      final Continuation<? super Integer> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Integer>() {
      @Override
      @NonNull
      public Integer call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteOldCompletedInstallations.acquire();
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
          __preparedStmtOfDeleteOldCompletedInstallations.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object updateValidationIssuesFlag(final String installationId, final boolean hasIssues,
      final long timestamp, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfUpdateValidationIssuesFlag.acquire();
        int _argIndex = 1;
        final int _tmp = hasIssues ? 1 : 0;
        _stmt.bindLong(_argIndex, _tmp);
        _argIndex = 2;
        _stmt.bindLong(_argIndex, timestamp);
        _argIndex = 3;
        if (installationId == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindString(_argIndex, installationId);
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
          __preparedStmtOfUpdateValidationIssuesFlag.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object getInstallationById(final String installationId,
      final Continuation<? super InstallationEntity> $completion) {
    final String _sql = "SELECT * FROM installations WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    if (installationId == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, installationId);
    }
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<InstallationEntity>() {
      @Override
      @Nullable
      public InstallationEntity call() throws Exception {
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
  public Flow<InstallationEntity> getInstallationByIdFlow(final String installationId) {
    final String _sql = "SELECT * FROM installations WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    if (installationId == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, installationId);
    }
    return CoroutinesRoom.createFlow(__db, false, new String[] {"installations"}, new Callable<InstallationEntity>() {
      @Override
      @Nullable
      public InstallationEntity call() throws Exception {
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
  public Object getInstallationsByTechnician(final String technicianId,
      final Continuation<? super List<InstallationEntity>> $completion) {
    final String _sql = "SELECT * FROM installations WHERE technicianId = ? ORDER BY createdAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    if (technicianId == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, technicianId);
    }
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<InstallationEntity>>() {
      @Override
      @NonNull
      public List<InstallationEntity> call() throws Exception {
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
  public Flow<List<InstallationEntity>> getInstallationsByTechnicianFlow(
      final String technicianId) {
    final String _sql = "SELECT * FROM installations WHERE technicianId = ? ORDER BY createdAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    if (technicianId == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, technicianId);
    }
    return CoroutinesRoom.createFlow(__db, false, new String[] {"installations"}, new Callable<List<InstallationEntity>>() {
      @Override
      @NonNull
      public List<InstallationEntity> call() throws Exception {
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
  public Object getInstallationsByDrop(final String dropId,
      final Continuation<? super List<InstallationEntity>> $completion) {
    final String _sql = "SELECT * FROM installations WHERE dropId = ? ORDER BY createdAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    if (dropId == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, dropId);
    }
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<InstallationEntity>>() {
      @Override
      @NonNull
      public List<InstallationEntity> call() throws Exception {
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
  public Object getInstallationsByStatus(final String status,
      final Continuation<? super List<InstallationEntity>> $completion) {
    final String _sql = "SELECT * FROM installations WHERE status = ? ORDER BY updatedAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    if (status == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, status);
    }
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<InstallationEntity>>() {
      @Override
      @NonNull
      public List<InstallationEntity> call() throws Exception {
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
  public Flow<List<InstallationEntity>> getInstallationsByStatusFlow(final String status) {
    final String _sql = "SELECT * FROM installations WHERE status = ? ORDER BY updatedAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    if (status == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, status);
    }
    return CoroutinesRoom.createFlow(__db, false, new String[] {"installations"}, new Callable<List<InstallationEntity>>() {
      @Override
      @NonNull
      public List<InstallationEntity> call() throws Exception {
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
  public Object getPendingInstallations(
      final Continuation<? super List<InstallationEntity>> $completion) {
    final String _sql = "SELECT * FROM installations WHERE status NOT IN ('COMPLETED', 'CANCELLED') ORDER BY createdAt ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<InstallationEntity>>() {
      @Override
      @NonNull
      public List<InstallationEntity> call() throws Exception {
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
  public Flow<List<InstallationEntity>> getPendingInstallationsFlow() {
    final String _sql = "SELECT * FROM installations WHERE status NOT IN ('COMPLETED', 'CANCELLED') ORDER BY createdAt ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"installations"}, new Callable<List<InstallationEntity>>() {
      @Override
      @NonNull
      public List<InstallationEntity> call() throws Exception {
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
  public Object getCompletedInstallationsInRange(final long startDate, final long endDate,
      final Continuation<? super List<InstallationEntity>> $completion) {
    final String _sql = "SELECT * FROM installations WHERE status = 'COMPLETED' AND completedAt BETWEEN ? AND ? ORDER BY completedAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, startDate);
    _argIndex = 2;
    _statement.bindLong(_argIndex, endDate);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<InstallationEntity>>() {
      @Override
      @NonNull
      public List<InstallationEntity> call() throws Exception {
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
  public Object getInstallationsNeedingSync(
      final Continuation<? super List<InstallationEntity>> $completion) {
    final String _sql = "SELECT * FROM installations WHERE needsSync = 1 ORDER BY updatedAt ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<InstallationEntity>>() {
      @Override
      @NonNull
      public List<InstallationEntity> call() throws Exception {
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
  public Object getInstallationCountByStatus(final String status,
      final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT COUNT(*) FROM installations WHERE status = ?";
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

  @Override
  public Object getTotalInstallationCount(final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT COUNT(*) FROM installations";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
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

  @Override
  public Object getInstallationsInDateRange(final long startDate, final long endDate,
      final Continuation<? super List<InstallationEntity>> $completion) {
    final String _sql = "SELECT * FROM installations WHERE createdAt BETWEEN ? AND ? ORDER BY createdAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, startDate);
    _argIndex = 2;
    _statement.bindLong(_argIndex, endDate);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<InstallationEntity>>() {
      @Override
      @NonNull
      public List<InstallationEntity> call() throws Exception {
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
  public Object searchInstallationsByEquipment(final String equipmentType,
      final Continuation<? super List<InstallationEntity>> $completion) {
    final String _sql = "SELECT * FROM installations WHERE equipmentType LIKE '%' || ? || '%' ORDER BY createdAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    if (equipmentType == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, equipmentType);
    }
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<InstallationEntity>>() {
      @Override
      @NonNull
      public List<InstallationEntity> call() throws Exception {
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
  public Object getInstallationsWithValidationIssues(
      final Continuation<? super List<InstallationEntity>> $completion) {
    final String _sql = "SELECT * FROM installations WHERE hasValidationIssues = 1 ORDER BY updatedAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<InstallationEntity>>() {
      @Override
      @NonNull
      public List<InstallationEntity> call() throws Exception {
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
  public Object getAverageInstallationTimeByEquipment(
      final Continuation<? super Map<String, Long>> $completion) {
    final String _sql = "\n"
            + "        SELECT equipmentType, AVG(completedAt - createdAt) as avgTime\n"
            + "        FROM installations\n"
            + "        WHERE status = 'COMPLETED' AND equipmentType IS NOT NULL\n"
            + "        GROUP BY equipmentType\n"
            + "        ORDER BY avgTime ASC\n"
            + "    ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Map<String, Long>>() {
      @Override
      @NonNull
      public Map<String, Long> call() throws Exception {
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
  public Object markInstallationsSynced(final List<String> installationIds, final long timestamp,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final StringBuilder _stringBuilder = StringUtil.newStringBuilder();
        _stringBuilder.append("UPDATE installations SET needsSync = 0, lastSyncedAt = ");
        _stringBuilder.append("?");
        _stringBuilder.append(" WHERE id IN (");
        final int _inputSize = installationIds.size();
        StringUtil.appendPlaceholders(_stringBuilder, _inputSize);
        _stringBuilder.append(")");
        final String _sql = _stringBuilder.toString();
        final SupportSQLiteStatement _stmt = __db.compileStatement(_sql);
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, timestamp);
        _argIndex = 2;
        for (String _item : installationIds) {
          if (_item == null) {
            _stmt.bindNull(_argIndex);
          } else {
            _stmt.bindString(_argIndex, _item);
          }
          _argIndex++;
        }
        __db.beginTransaction();
        try {
          _stmt.executeUpdateDelete();
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
