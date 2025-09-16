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
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.room.util.StringUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.fibreflow.core.database.converters.DateConverters;
import com.fibreflow.core.database.converters.LocationConverters;
import com.fibreflow.core.database.converters.StatusConverters;
import com.fibreflow.core.database.entities.InstallationEntity;
import com.fibreflow.core.database.entities.InstallationStatus;
import java.lang.Boolean;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Integer;
import java.lang.Long;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.StringBuilder;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
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

  private final SharedSQLiteStatement __preparedStmtOfDeleteInstallationByDropNumber;

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
        final String _query = "UPDATE installations SET ai_guidance_used = 1, updated_at = ? WHERE installation_id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfMarkInstallationSynced = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE installations SET ai_guidance_used = 0, updated_at = ? WHERE installation_id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfUpdateInstallationStatus = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE installations SET status = ?, updated_at = ? WHERE installation_id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfUpdateInstallationProgress = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE installations SET current_step = ?, updated_at = ? WHERE installation_id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfMarkInstallationCompleted = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE installations SET status = 'COMPLETED', end_time = ?, updated_at = ? WHERE installation_id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteOldCompletedInstallations = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM installations WHERE status = 'COMPLETED' AND end_time < ?";
        return _query;
      }
    };
    this.__preparedStmtOfUpdateValidationIssuesFlag = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE installations SET validation_errors = ?, updated_at = ? WHERE installation_id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteInstallationByDropNumber = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM installations WHERE drop_number = ?";
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
  public Object markInstallationForSync(final long installationId, final long timestamp,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfMarkInstallationForSync.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, timestamp);
        _argIndex = 2;
        _stmt.bindLong(_argIndex, installationId);
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
  public Object markInstallationSynced(final long installationId, final long timestamp,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfMarkInstallationSynced.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, timestamp);
        _argIndex = 2;
        _stmt.bindLong(_argIndex, installationId);
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
  public Object updateInstallationStatus(final long installationId, final String status,
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
        _stmt.bindLong(_argIndex, installationId);
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
  public Object updateInstallationProgress(final long installationId, final int currentStep,
      final long timestamp, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfUpdateInstallationProgress.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, currentStep);
        _argIndex = 2;
        _stmt.bindLong(_argIndex, timestamp);
        _argIndex = 3;
        _stmt.bindLong(_argIndex, installationId);
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
  public Object markInstallationCompleted(final long installationId, final long timestamp,
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
        _stmt.bindLong(_argIndex, installationId);
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
  public Object updateValidationIssuesFlag(final long installationId, final String validationErrors,
      final long timestamp, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfUpdateValidationIssuesFlag.acquire();
        int _argIndex = 1;
        if (validationErrors == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindString(_argIndex, validationErrors);
        }
        _argIndex = 2;
        _stmt.bindLong(_argIndex, timestamp);
        _argIndex = 3;
        _stmt.bindLong(_argIndex, installationId);
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
  public Object deleteInstallationByDropNumber(final String dropNumber,
      final Continuation<? super Integer> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Integer>() {
      @Override
      @NonNull
      public Integer call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteInstallationByDropNumber.acquire();
        int _argIndex = 1;
        if (dropNumber == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindString(_argIndex, dropNumber);
        }
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
          __preparedStmtOfDeleteInstallationByDropNumber.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object getInstallationById(final long installationId,
      final Continuation<? super InstallationEntity> $completion) {
    final String _sql = "SELECT * FROM installations WHERE installation_id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, installationId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<InstallationEntity>() {
      @Override
      @Nullable
      public InstallationEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfInstallationId = CursorUtil.getColumnIndexOrThrow(_cursor, "installation_id");
          final int _cursorIndexOfDropNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "drop_number");
          final int _cursorIndexOfTechnicianId = CursorUtil.getColumnIndexOrThrow(_cursor, "technician_id");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfStartTime = CursorUtil.getColumnIndexOrThrow(_cursor, "start_time");
          final int _cursorIndexOfEndTime = CursorUtil.getColumnIndexOrThrow(_cursor, "end_time");
          final int _cursorIndexOfOntSerial = CursorUtil.getColumnIndexOrThrow(_cursor, "ont_serial");
          final int _cursorIndexOfSpeedTestResults = CursorUtil.getColumnIndexOrThrow(_cursor, "speed_test_results");
          final int _cursorIndexOfPhotos = CursorUtil.getColumnIndexOrThrow(_cursor, "photos");
          final int _cursorIndexOfCompletedSteps = CursorUtil.getColumnIndexOrThrow(_cursor, "completed_steps");
          final int _cursorIndexOfCurrentStep = CursorUtil.getColumnIndexOrThrow(_cursor, "current_step");
          final int _cursorIndexOfTotalSteps = CursorUtil.getColumnIndexOrThrow(_cursor, "total_steps");
          final int _cursorIndexOfValidationErrors = CursorUtil.getColumnIndexOrThrow(_cursor, "validation_errors");
          final int _cursorIndexOfAiGuidanceUsed = CursorUtil.getColumnIndexOrThrow(_cursor, "ai_guidance_used");
          final int _cursorIndexOfManualOverrideUsed = CursorUtil.getColumnIndexOrThrow(_cursor, "manual_override_used");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final InstallationEntity _result;
          if (_cursor.moveToFirst()) {
            final long _tmpInstallationId;
            _tmpInstallationId = _cursor.getLong(_cursorIndexOfInstallationId);
            final String _tmpDropNumber;
            if (_cursor.isNull(_cursorIndexOfDropNumber)) {
              _tmpDropNumber = null;
            } else {
              _tmpDropNumber = _cursor.getString(_cursorIndexOfDropNumber);
            }
            final String _tmpTechnicianId;
            if (_cursor.isNull(_cursorIndexOfTechnicianId)) {
              _tmpTechnicianId = null;
            } else {
              _tmpTechnicianId = _cursor.getString(_cursorIndexOfTechnicianId);
            }
            final InstallationStatus _tmpStatus;
            final String _tmp;
            if (_cursor.isNull(_cursorIndexOfStatus)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getString(_cursorIndexOfStatus);
            }
            _tmpStatus = __statusConverters.toInstallationStatus(_tmp);
            final Date _tmpStartTime;
            final Long _tmp_1;
            if (_cursor.isNull(_cursorIndexOfStartTime)) {
              _tmp_1 = null;
            } else {
              _tmp_1 = _cursor.getLong(_cursorIndexOfStartTime);
            }
            _tmpStartTime = __dateConverters.fromTimestamp(_tmp_1);
            final Date _tmpEndTime;
            final Long _tmp_2;
            if (_cursor.isNull(_cursorIndexOfEndTime)) {
              _tmp_2 = null;
            } else {
              _tmp_2 = _cursor.getLong(_cursorIndexOfEndTime);
            }
            _tmpEndTime = __dateConverters.fromTimestamp(_tmp_2);
            final String _tmpOntSerial;
            if (_cursor.isNull(_cursorIndexOfOntSerial)) {
              _tmpOntSerial = null;
            } else {
              _tmpOntSerial = _cursor.getString(_cursorIndexOfOntSerial);
            }
            final String _tmpSpeedTestResults;
            if (_cursor.isNull(_cursorIndexOfSpeedTestResults)) {
              _tmpSpeedTestResults = null;
            } else {
              _tmpSpeedTestResults = _cursor.getString(_cursorIndexOfSpeedTestResults);
            }
            final List<Long> _tmpPhotos;
            final String _tmp_3;
            if (_cursor.isNull(_cursorIndexOfPhotos)) {
              _tmp_3 = null;
            } else {
              _tmp_3 = _cursor.getString(_cursorIndexOfPhotos);
            }
            _tmpPhotos = __locationConverters.fromLongList(_tmp_3);
            final String _tmpCompletedSteps;
            if (_cursor.isNull(_cursorIndexOfCompletedSteps)) {
              _tmpCompletedSteps = null;
            } else {
              _tmpCompletedSteps = _cursor.getString(_cursorIndexOfCompletedSteps);
            }
            final int _tmpCurrentStep;
            _tmpCurrentStep = _cursor.getInt(_cursorIndexOfCurrentStep);
            final int _tmpTotalSteps;
            _tmpTotalSteps = _cursor.getInt(_cursorIndexOfTotalSteps);
            final String _tmpValidationErrors;
            if (_cursor.isNull(_cursorIndexOfValidationErrors)) {
              _tmpValidationErrors = null;
            } else {
              _tmpValidationErrors = _cursor.getString(_cursorIndexOfValidationErrors);
            }
            final boolean _tmpAiGuidanceUsed;
            final int _tmp_4;
            _tmp_4 = _cursor.getInt(_cursorIndexOfAiGuidanceUsed);
            _tmpAiGuidanceUsed = _tmp_4 != 0;
            final boolean _tmpManualOverrideUsed;
            final int _tmp_5;
            _tmp_5 = _cursor.getInt(_cursorIndexOfManualOverrideUsed);
            _tmpManualOverrideUsed = _tmp_5 != 0;
            final Date _tmpCreatedAt;
            final Long _tmp_6;
            if (_cursor.isNull(_cursorIndexOfCreatedAt)) {
              _tmp_6 = null;
            } else {
              _tmp_6 = _cursor.getLong(_cursorIndexOfCreatedAt);
            }
            _tmpCreatedAt = __dateConverters.fromTimestamp(_tmp_6);
            final Date _tmpUpdatedAt;
            final Long _tmp_7;
            if (_cursor.isNull(_cursorIndexOfUpdatedAt)) {
              _tmp_7 = null;
            } else {
              _tmp_7 = _cursor.getLong(_cursorIndexOfUpdatedAt);
            }
            _tmpUpdatedAt = __dateConverters.fromTimestamp(_tmp_7);
            _result = new InstallationEntity(_tmpInstallationId,_tmpDropNumber,_tmpTechnicianId,_tmpStatus,_tmpStartTime,_tmpEndTime,_tmpOntSerial,_tmpSpeedTestResults,_tmpPhotos,_tmpCompletedSteps,_tmpCurrentStep,_tmpTotalSteps,_tmpValidationErrors,_tmpAiGuidanceUsed,_tmpManualOverrideUsed,_tmpCreatedAt,_tmpUpdatedAt);
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
  public Flow<InstallationEntity> getInstallationByIdFlow(final long installationId) {
    final String _sql = "SELECT * FROM installations WHERE installation_id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, installationId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"installations"}, new Callable<InstallationEntity>() {
      @Override
      @Nullable
      public InstallationEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfInstallationId = CursorUtil.getColumnIndexOrThrow(_cursor, "installation_id");
          final int _cursorIndexOfDropNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "drop_number");
          final int _cursorIndexOfTechnicianId = CursorUtil.getColumnIndexOrThrow(_cursor, "technician_id");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfStartTime = CursorUtil.getColumnIndexOrThrow(_cursor, "start_time");
          final int _cursorIndexOfEndTime = CursorUtil.getColumnIndexOrThrow(_cursor, "end_time");
          final int _cursorIndexOfOntSerial = CursorUtil.getColumnIndexOrThrow(_cursor, "ont_serial");
          final int _cursorIndexOfSpeedTestResults = CursorUtil.getColumnIndexOrThrow(_cursor, "speed_test_results");
          final int _cursorIndexOfPhotos = CursorUtil.getColumnIndexOrThrow(_cursor, "photos");
          final int _cursorIndexOfCompletedSteps = CursorUtil.getColumnIndexOrThrow(_cursor, "completed_steps");
          final int _cursorIndexOfCurrentStep = CursorUtil.getColumnIndexOrThrow(_cursor, "current_step");
          final int _cursorIndexOfTotalSteps = CursorUtil.getColumnIndexOrThrow(_cursor, "total_steps");
          final int _cursorIndexOfValidationErrors = CursorUtil.getColumnIndexOrThrow(_cursor, "validation_errors");
          final int _cursorIndexOfAiGuidanceUsed = CursorUtil.getColumnIndexOrThrow(_cursor, "ai_guidance_used");
          final int _cursorIndexOfManualOverrideUsed = CursorUtil.getColumnIndexOrThrow(_cursor, "manual_override_used");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final InstallationEntity _result;
          if (_cursor.moveToFirst()) {
            final long _tmpInstallationId;
            _tmpInstallationId = _cursor.getLong(_cursorIndexOfInstallationId);
            final String _tmpDropNumber;
            if (_cursor.isNull(_cursorIndexOfDropNumber)) {
              _tmpDropNumber = null;
            } else {
              _tmpDropNumber = _cursor.getString(_cursorIndexOfDropNumber);
            }
            final String _tmpTechnicianId;
            if (_cursor.isNull(_cursorIndexOfTechnicianId)) {
              _tmpTechnicianId = null;
            } else {
              _tmpTechnicianId = _cursor.getString(_cursorIndexOfTechnicianId);
            }
            final InstallationStatus _tmpStatus;
            final String _tmp;
            if (_cursor.isNull(_cursorIndexOfStatus)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getString(_cursorIndexOfStatus);
            }
            _tmpStatus = __statusConverters.toInstallationStatus(_tmp);
            final Date _tmpStartTime;
            final Long _tmp_1;
            if (_cursor.isNull(_cursorIndexOfStartTime)) {
              _tmp_1 = null;
            } else {
              _tmp_1 = _cursor.getLong(_cursorIndexOfStartTime);
            }
            _tmpStartTime = __dateConverters.fromTimestamp(_tmp_1);
            final Date _tmpEndTime;
            final Long _tmp_2;
            if (_cursor.isNull(_cursorIndexOfEndTime)) {
              _tmp_2 = null;
            } else {
              _tmp_2 = _cursor.getLong(_cursorIndexOfEndTime);
            }
            _tmpEndTime = __dateConverters.fromTimestamp(_tmp_2);
            final String _tmpOntSerial;
            if (_cursor.isNull(_cursorIndexOfOntSerial)) {
              _tmpOntSerial = null;
            } else {
              _tmpOntSerial = _cursor.getString(_cursorIndexOfOntSerial);
            }
            final String _tmpSpeedTestResults;
            if (_cursor.isNull(_cursorIndexOfSpeedTestResults)) {
              _tmpSpeedTestResults = null;
            } else {
              _tmpSpeedTestResults = _cursor.getString(_cursorIndexOfSpeedTestResults);
            }
            final List<Long> _tmpPhotos;
            final String _tmp_3;
            if (_cursor.isNull(_cursorIndexOfPhotos)) {
              _tmp_3 = null;
            } else {
              _tmp_3 = _cursor.getString(_cursorIndexOfPhotos);
            }
            _tmpPhotos = __locationConverters.fromLongList(_tmp_3);
            final String _tmpCompletedSteps;
            if (_cursor.isNull(_cursorIndexOfCompletedSteps)) {
              _tmpCompletedSteps = null;
            } else {
              _tmpCompletedSteps = _cursor.getString(_cursorIndexOfCompletedSteps);
            }
            final int _tmpCurrentStep;
            _tmpCurrentStep = _cursor.getInt(_cursorIndexOfCurrentStep);
            final int _tmpTotalSteps;
            _tmpTotalSteps = _cursor.getInt(_cursorIndexOfTotalSteps);
            final String _tmpValidationErrors;
            if (_cursor.isNull(_cursorIndexOfValidationErrors)) {
              _tmpValidationErrors = null;
            } else {
              _tmpValidationErrors = _cursor.getString(_cursorIndexOfValidationErrors);
            }
            final boolean _tmpAiGuidanceUsed;
            final int _tmp_4;
            _tmp_4 = _cursor.getInt(_cursorIndexOfAiGuidanceUsed);
            _tmpAiGuidanceUsed = _tmp_4 != 0;
            final boolean _tmpManualOverrideUsed;
            final int _tmp_5;
            _tmp_5 = _cursor.getInt(_cursorIndexOfManualOverrideUsed);
            _tmpManualOverrideUsed = _tmp_5 != 0;
            final Date _tmpCreatedAt;
            final Long _tmp_6;
            if (_cursor.isNull(_cursorIndexOfCreatedAt)) {
              _tmp_6 = null;
            } else {
              _tmp_6 = _cursor.getLong(_cursorIndexOfCreatedAt);
            }
            _tmpCreatedAt = __dateConverters.fromTimestamp(_tmp_6);
            final Date _tmpUpdatedAt;
            final Long _tmp_7;
            if (_cursor.isNull(_cursorIndexOfUpdatedAt)) {
              _tmp_7 = null;
            } else {
              _tmp_7 = _cursor.getLong(_cursorIndexOfUpdatedAt);
            }
            _tmpUpdatedAt = __dateConverters.fromTimestamp(_tmp_7);
            _result = new InstallationEntity(_tmpInstallationId,_tmpDropNumber,_tmpTechnicianId,_tmpStatus,_tmpStartTime,_tmpEndTime,_tmpOntSerial,_tmpSpeedTestResults,_tmpPhotos,_tmpCompletedSteps,_tmpCurrentStep,_tmpTotalSteps,_tmpValidationErrors,_tmpAiGuidanceUsed,_tmpManualOverrideUsed,_tmpCreatedAt,_tmpUpdatedAt);
          } else {
            _result = null;
          }
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
    final String _sql = "SELECT * FROM installations WHERE technician_id = ? ORDER BY created_at DESC";
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
          final int _cursorIndexOfInstallationId = CursorUtil.getColumnIndexOrThrow(_cursor, "installation_id");
          final int _cursorIndexOfDropNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "drop_number");
          final int _cursorIndexOfTechnicianId = CursorUtil.getColumnIndexOrThrow(_cursor, "technician_id");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfStartTime = CursorUtil.getColumnIndexOrThrow(_cursor, "start_time");
          final int _cursorIndexOfEndTime = CursorUtil.getColumnIndexOrThrow(_cursor, "end_time");
          final int _cursorIndexOfOntSerial = CursorUtil.getColumnIndexOrThrow(_cursor, "ont_serial");
          final int _cursorIndexOfSpeedTestResults = CursorUtil.getColumnIndexOrThrow(_cursor, "speed_test_results");
          final int _cursorIndexOfPhotos = CursorUtil.getColumnIndexOrThrow(_cursor, "photos");
          final int _cursorIndexOfCompletedSteps = CursorUtil.getColumnIndexOrThrow(_cursor, "completed_steps");
          final int _cursorIndexOfCurrentStep = CursorUtil.getColumnIndexOrThrow(_cursor, "current_step");
          final int _cursorIndexOfTotalSteps = CursorUtil.getColumnIndexOrThrow(_cursor, "total_steps");
          final int _cursorIndexOfValidationErrors = CursorUtil.getColumnIndexOrThrow(_cursor, "validation_errors");
          final int _cursorIndexOfAiGuidanceUsed = CursorUtil.getColumnIndexOrThrow(_cursor, "ai_guidance_used");
          final int _cursorIndexOfManualOverrideUsed = CursorUtil.getColumnIndexOrThrow(_cursor, "manual_override_used");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final List<InstallationEntity> _result = new ArrayList<InstallationEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final InstallationEntity _item;
            final long _tmpInstallationId;
            _tmpInstallationId = _cursor.getLong(_cursorIndexOfInstallationId);
            final String _tmpDropNumber;
            if (_cursor.isNull(_cursorIndexOfDropNumber)) {
              _tmpDropNumber = null;
            } else {
              _tmpDropNumber = _cursor.getString(_cursorIndexOfDropNumber);
            }
            final String _tmpTechnicianId;
            if (_cursor.isNull(_cursorIndexOfTechnicianId)) {
              _tmpTechnicianId = null;
            } else {
              _tmpTechnicianId = _cursor.getString(_cursorIndexOfTechnicianId);
            }
            final InstallationStatus _tmpStatus;
            final String _tmp;
            if (_cursor.isNull(_cursorIndexOfStatus)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getString(_cursorIndexOfStatus);
            }
            _tmpStatus = __statusConverters.toInstallationStatus(_tmp);
            final Date _tmpStartTime;
            final Long _tmp_1;
            if (_cursor.isNull(_cursorIndexOfStartTime)) {
              _tmp_1 = null;
            } else {
              _tmp_1 = _cursor.getLong(_cursorIndexOfStartTime);
            }
            _tmpStartTime = __dateConverters.fromTimestamp(_tmp_1);
            final Date _tmpEndTime;
            final Long _tmp_2;
            if (_cursor.isNull(_cursorIndexOfEndTime)) {
              _tmp_2 = null;
            } else {
              _tmp_2 = _cursor.getLong(_cursorIndexOfEndTime);
            }
            _tmpEndTime = __dateConverters.fromTimestamp(_tmp_2);
            final String _tmpOntSerial;
            if (_cursor.isNull(_cursorIndexOfOntSerial)) {
              _tmpOntSerial = null;
            } else {
              _tmpOntSerial = _cursor.getString(_cursorIndexOfOntSerial);
            }
            final String _tmpSpeedTestResults;
            if (_cursor.isNull(_cursorIndexOfSpeedTestResults)) {
              _tmpSpeedTestResults = null;
            } else {
              _tmpSpeedTestResults = _cursor.getString(_cursorIndexOfSpeedTestResults);
            }
            final List<Long> _tmpPhotos;
            final String _tmp_3;
            if (_cursor.isNull(_cursorIndexOfPhotos)) {
              _tmp_3 = null;
            } else {
              _tmp_3 = _cursor.getString(_cursorIndexOfPhotos);
            }
            _tmpPhotos = __locationConverters.fromLongList(_tmp_3);
            final String _tmpCompletedSteps;
            if (_cursor.isNull(_cursorIndexOfCompletedSteps)) {
              _tmpCompletedSteps = null;
            } else {
              _tmpCompletedSteps = _cursor.getString(_cursorIndexOfCompletedSteps);
            }
            final int _tmpCurrentStep;
            _tmpCurrentStep = _cursor.getInt(_cursorIndexOfCurrentStep);
            final int _tmpTotalSteps;
            _tmpTotalSteps = _cursor.getInt(_cursorIndexOfTotalSteps);
            final String _tmpValidationErrors;
            if (_cursor.isNull(_cursorIndexOfValidationErrors)) {
              _tmpValidationErrors = null;
            } else {
              _tmpValidationErrors = _cursor.getString(_cursorIndexOfValidationErrors);
            }
            final boolean _tmpAiGuidanceUsed;
            final int _tmp_4;
            _tmp_4 = _cursor.getInt(_cursorIndexOfAiGuidanceUsed);
            _tmpAiGuidanceUsed = _tmp_4 != 0;
            final boolean _tmpManualOverrideUsed;
            final int _tmp_5;
            _tmp_5 = _cursor.getInt(_cursorIndexOfManualOverrideUsed);
            _tmpManualOverrideUsed = _tmp_5 != 0;
            final Date _tmpCreatedAt;
            final Long _tmp_6;
            if (_cursor.isNull(_cursorIndexOfCreatedAt)) {
              _tmp_6 = null;
            } else {
              _tmp_6 = _cursor.getLong(_cursorIndexOfCreatedAt);
            }
            _tmpCreatedAt = __dateConverters.fromTimestamp(_tmp_6);
            final Date _tmpUpdatedAt;
            final Long _tmp_7;
            if (_cursor.isNull(_cursorIndexOfUpdatedAt)) {
              _tmp_7 = null;
            } else {
              _tmp_7 = _cursor.getLong(_cursorIndexOfUpdatedAt);
            }
            _tmpUpdatedAt = __dateConverters.fromTimestamp(_tmp_7);
            _item = new InstallationEntity(_tmpInstallationId,_tmpDropNumber,_tmpTechnicianId,_tmpStatus,_tmpStartTime,_tmpEndTime,_tmpOntSerial,_tmpSpeedTestResults,_tmpPhotos,_tmpCompletedSteps,_tmpCurrentStep,_tmpTotalSteps,_tmpValidationErrors,_tmpAiGuidanceUsed,_tmpManualOverrideUsed,_tmpCreatedAt,_tmpUpdatedAt);
            _result.add(_item);
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
  public Flow<List<InstallationEntity>> getInstallationsByTechnicianFlow(
      final String technicianId) {
    final String _sql = "SELECT * FROM installations WHERE technician_id = ? ORDER BY created_at DESC";
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
          final int _cursorIndexOfInstallationId = CursorUtil.getColumnIndexOrThrow(_cursor, "installation_id");
          final int _cursorIndexOfDropNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "drop_number");
          final int _cursorIndexOfTechnicianId = CursorUtil.getColumnIndexOrThrow(_cursor, "technician_id");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfStartTime = CursorUtil.getColumnIndexOrThrow(_cursor, "start_time");
          final int _cursorIndexOfEndTime = CursorUtil.getColumnIndexOrThrow(_cursor, "end_time");
          final int _cursorIndexOfOntSerial = CursorUtil.getColumnIndexOrThrow(_cursor, "ont_serial");
          final int _cursorIndexOfSpeedTestResults = CursorUtil.getColumnIndexOrThrow(_cursor, "speed_test_results");
          final int _cursorIndexOfPhotos = CursorUtil.getColumnIndexOrThrow(_cursor, "photos");
          final int _cursorIndexOfCompletedSteps = CursorUtil.getColumnIndexOrThrow(_cursor, "completed_steps");
          final int _cursorIndexOfCurrentStep = CursorUtil.getColumnIndexOrThrow(_cursor, "current_step");
          final int _cursorIndexOfTotalSteps = CursorUtil.getColumnIndexOrThrow(_cursor, "total_steps");
          final int _cursorIndexOfValidationErrors = CursorUtil.getColumnIndexOrThrow(_cursor, "validation_errors");
          final int _cursorIndexOfAiGuidanceUsed = CursorUtil.getColumnIndexOrThrow(_cursor, "ai_guidance_used");
          final int _cursorIndexOfManualOverrideUsed = CursorUtil.getColumnIndexOrThrow(_cursor, "manual_override_used");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final List<InstallationEntity> _result = new ArrayList<InstallationEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final InstallationEntity _item;
            final long _tmpInstallationId;
            _tmpInstallationId = _cursor.getLong(_cursorIndexOfInstallationId);
            final String _tmpDropNumber;
            if (_cursor.isNull(_cursorIndexOfDropNumber)) {
              _tmpDropNumber = null;
            } else {
              _tmpDropNumber = _cursor.getString(_cursorIndexOfDropNumber);
            }
            final String _tmpTechnicianId;
            if (_cursor.isNull(_cursorIndexOfTechnicianId)) {
              _tmpTechnicianId = null;
            } else {
              _tmpTechnicianId = _cursor.getString(_cursorIndexOfTechnicianId);
            }
            final InstallationStatus _tmpStatus;
            final String _tmp;
            if (_cursor.isNull(_cursorIndexOfStatus)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getString(_cursorIndexOfStatus);
            }
            _tmpStatus = __statusConverters.toInstallationStatus(_tmp);
            final Date _tmpStartTime;
            final Long _tmp_1;
            if (_cursor.isNull(_cursorIndexOfStartTime)) {
              _tmp_1 = null;
            } else {
              _tmp_1 = _cursor.getLong(_cursorIndexOfStartTime);
            }
            _tmpStartTime = __dateConverters.fromTimestamp(_tmp_1);
            final Date _tmpEndTime;
            final Long _tmp_2;
            if (_cursor.isNull(_cursorIndexOfEndTime)) {
              _tmp_2 = null;
            } else {
              _tmp_2 = _cursor.getLong(_cursorIndexOfEndTime);
            }
            _tmpEndTime = __dateConverters.fromTimestamp(_tmp_2);
            final String _tmpOntSerial;
            if (_cursor.isNull(_cursorIndexOfOntSerial)) {
              _tmpOntSerial = null;
            } else {
              _tmpOntSerial = _cursor.getString(_cursorIndexOfOntSerial);
            }
            final String _tmpSpeedTestResults;
            if (_cursor.isNull(_cursorIndexOfSpeedTestResults)) {
              _tmpSpeedTestResults = null;
            } else {
              _tmpSpeedTestResults = _cursor.getString(_cursorIndexOfSpeedTestResults);
            }
            final List<Long> _tmpPhotos;
            final String _tmp_3;
            if (_cursor.isNull(_cursorIndexOfPhotos)) {
              _tmp_3 = null;
            } else {
              _tmp_3 = _cursor.getString(_cursorIndexOfPhotos);
            }
            _tmpPhotos = __locationConverters.fromLongList(_tmp_3);
            final String _tmpCompletedSteps;
            if (_cursor.isNull(_cursorIndexOfCompletedSteps)) {
              _tmpCompletedSteps = null;
            } else {
              _tmpCompletedSteps = _cursor.getString(_cursorIndexOfCompletedSteps);
            }
            final int _tmpCurrentStep;
            _tmpCurrentStep = _cursor.getInt(_cursorIndexOfCurrentStep);
            final int _tmpTotalSteps;
            _tmpTotalSteps = _cursor.getInt(_cursorIndexOfTotalSteps);
            final String _tmpValidationErrors;
            if (_cursor.isNull(_cursorIndexOfValidationErrors)) {
              _tmpValidationErrors = null;
            } else {
              _tmpValidationErrors = _cursor.getString(_cursorIndexOfValidationErrors);
            }
            final boolean _tmpAiGuidanceUsed;
            final int _tmp_4;
            _tmp_4 = _cursor.getInt(_cursorIndexOfAiGuidanceUsed);
            _tmpAiGuidanceUsed = _tmp_4 != 0;
            final boolean _tmpManualOverrideUsed;
            final int _tmp_5;
            _tmp_5 = _cursor.getInt(_cursorIndexOfManualOverrideUsed);
            _tmpManualOverrideUsed = _tmp_5 != 0;
            final Date _tmpCreatedAt;
            final Long _tmp_6;
            if (_cursor.isNull(_cursorIndexOfCreatedAt)) {
              _tmp_6 = null;
            } else {
              _tmp_6 = _cursor.getLong(_cursorIndexOfCreatedAt);
            }
            _tmpCreatedAt = __dateConverters.fromTimestamp(_tmp_6);
            final Date _tmpUpdatedAt;
            final Long _tmp_7;
            if (_cursor.isNull(_cursorIndexOfUpdatedAt)) {
              _tmp_7 = null;
            } else {
              _tmp_7 = _cursor.getLong(_cursorIndexOfUpdatedAt);
            }
            _tmpUpdatedAt = __dateConverters.fromTimestamp(_tmp_7);
            _item = new InstallationEntity(_tmpInstallationId,_tmpDropNumber,_tmpTechnicianId,_tmpStatus,_tmpStartTime,_tmpEndTime,_tmpOntSerial,_tmpSpeedTestResults,_tmpPhotos,_tmpCompletedSteps,_tmpCurrentStep,_tmpTotalSteps,_tmpValidationErrors,_tmpAiGuidanceUsed,_tmpManualOverrideUsed,_tmpCreatedAt,_tmpUpdatedAt);
            _result.add(_item);
          }
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
  public Object getInstallationsByDrop(final String dropNumber,
      final Continuation<? super List<InstallationEntity>> $completion) {
    final String _sql = "SELECT * FROM installations WHERE drop_number = ? ORDER BY created_at DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    if (dropNumber == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, dropNumber);
    }
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<InstallationEntity>>() {
      @Override
      @NonNull
      public List<InstallationEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfInstallationId = CursorUtil.getColumnIndexOrThrow(_cursor, "installation_id");
          final int _cursorIndexOfDropNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "drop_number");
          final int _cursorIndexOfTechnicianId = CursorUtil.getColumnIndexOrThrow(_cursor, "technician_id");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfStartTime = CursorUtil.getColumnIndexOrThrow(_cursor, "start_time");
          final int _cursorIndexOfEndTime = CursorUtil.getColumnIndexOrThrow(_cursor, "end_time");
          final int _cursorIndexOfOntSerial = CursorUtil.getColumnIndexOrThrow(_cursor, "ont_serial");
          final int _cursorIndexOfSpeedTestResults = CursorUtil.getColumnIndexOrThrow(_cursor, "speed_test_results");
          final int _cursorIndexOfPhotos = CursorUtil.getColumnIndexOrThrow(_cursor, "photos");
          final int _cursorIndexOfCompletedSteps = CursorUtil.getColumnIndexOrThrow(_cursor, "completed_steps");
          final int _cursorIndexOfCurrentStep = CursorUtil.getColumnIndexOrThrow(_cursor, "current_step");
          final int _cursorIndexOfTotalSteps = CursorUtil.getColumnIndexOrThrow(_cursor, "total_steps");
          final int _cursorIndexOfValidationErrors = CursorUtil.getColumnIndexOrThrow(_cursor, "validation_errors");
          final int _cursorIndexOfAiGuidanceUsed = CursorUtil.getColumnIndexOrThrow(_cursor, "ai_guidance_used");
          final int _cursorIndexOfManualOverrideUsed = CursorUtil.getColumnIndexOrThrow(_cursor, "manual_override_used");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final List<InstallationEntity> _result = new ArrayList<InstallationEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final InstallationEntity _item;
            final long _tmpInstallationId;
            _tmpInstallationId = _cursor.getLong(_cursorIndexOfInstallationId);
            final String _tmpDropNumber;
            if (_cursor.isNull(_cursorIndexOfDropNumber)) {
              _tmpDropNumber = null;
            } else {
              _tmpDropNumber = _cursor.getString(_cursorIndexOfDropNumber);
            }
            final String _tmpTechnicianId;
            if (_cursor.isNull(_cursorIndexOfTechnicianId)) {
              _tmpTechnicianId = null;
            } else {
              _tmpTechnicianId = _cursor.getString(_cursorIndexOfTechnicianId);
            }
            final InstallationStatus _tmpStatus;
            final String _tmp;
            if (_cursor.isNull(_cursorIndexOfStatus)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getString(_cursorIndexOfStatus);
            }
            _tmpStatus = __statusConverters.toInstallationStatus(_tmp);
            final Date _tmpStartTime;
            final Long _tmp_1;
            if (_cursor.isNull(_cursorIndexOfStartTime)) {
              _tmp_1 = null;
            } else {
              _tmp_1 = _cursor.getLong(_cursorIndexOfStartTime);
            }
            _tmpStartTime = __dateConverters.fromTimestamp(_tmp_1);
            final Date _tmpEndTime;
            final Long _tmp_2;
            if (_cursor.isNull(_cursorIndexOfEndTime)) {
              _tmp_2 = null;
            } else {
              _tmp_2 = _cursor.getLong(_cursorIndexOfEndTime);
            }
            _tmpEndTime = __dateConverters.fromTimestamp(_tmp_2);
            final String _tmpOntSerial;
            if (_cursor.isNull(_cursorIndexOfOntSerial)) {
              _tmpOntSerial = null;
            } else {
              _tmpOntSerial = _cursor.getString(_cursorIndexOfOntSerial);
            }
            final String _tmpSpeedTestResults;
            if (_cursor.isNull(_cursorIndexOfSpeedTestResults)) {
              _tmpSpeedTestResults = null;
            } else {
              _tmpSpeedTestResults = _cursor.getString(_cursorIndexOfSpeedTestResults);
            }
            final List<Long> _tmpPhotos;
            final String _tmp_3;
            if (_cursor.isNull(_cursorIndexOfPhotos)) {
              _tmp_3 = null;
            } else {
              _tmp_3 = _cursor.getString(_cursorIndexOfPhotos);
            }
            _tmpPhotos = __locationConverters.fromLongList(_tmp_3);
            final String _tmpCompletedSteps;
            if (_cursor.isNull(_cursorIndexOfCompletedSteps)) {
              _tmpCompletedSteps = null;
            } else {
              _tmpCompletedSteps = _cursor.getString(_cursorIndexOfCompletedSteps);
            }
            final int _tmpCurrentStep;
            _tmpCurrentStep = _cursor.getInt(_cursorIndexOfCurrentStep);
            final int _tmpTotalSteps;
            _tmpTotalSteps = _cursor.getInt(_cursorIndexOfTotalSteps);
            final String _tmpValidationErrors;
            if (_cursor.isNull(_cursorIndexOfValidationErrors)) {
              _tmpValidationErrors = null;
            } else {
              _tmpValidationErrors = _cursor.getString(_cursorIndexOfValidationErrors);
            }
            final boolean _tmpAiGuidanceUsed;
            final int _tmp_4;
            _tmp_4 = _cursor.getInt(_cursorIndexOfAiGuidanceUsed);
            _tmpAiGuidanceUsed = _tmp_4 != 0;
            final boolean _tmpManualOverrideUsed;
            final int _tmp_5;
            _tmp_5 = _cursor.getInt(_cursorIndexOfManualOverrideUsed);
            _tmpManualOverrideUsed = _tmp_5 != 0;
            final Date _tmpCreatedAt;
            final Long _tmp_6;
            if (_cursor.isNull(_cursorIndexOfCreatedAt)) {
              _tmp_6 = null;
            } else {
              _tmp_6 = _cursor.getLong(_cursorIndexOfCreatedAt);
            }
            _tmpCreatedAt = __dateConverters.fromTimestamp(_tmp_6);
            final Date _tmpUpdatedAt;
            final Long _tmp_7;
            if (_cursor.isNull(_cursorIndexOfUpdatedAt)) {
              _tmp_7 = null;
            } else {
              _tmp_7 = _cursor.getLong(_cursorIndexOfUpdatedAt);
            }
            _tmpUpdatedAt = __dateConverters.fromTimestamp(_tmp_7);
            _item = new InstallationEntity(_tmpInstallationId,_tmpDropNumber,_tmpTechnicianId,_tmpStatus,_tmpStartTime,_tmpEndTime,_tmpOntSerial,_tmpSpeedTestResults,_tmpPhotos,_tmpCompletedSteps,_tmpCurrentStep,_tmpTotalSteps,_tmpValidationErrors,_tmpAiGuidanceUsed,_tmpManualOverrideUsed,_tmpCreatedAt,_tmpUpdatedAt);
            _result.add(_item);
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
  public Object getInstallationsByStatus(final String status,
      final Continuation<? super List<InstallationEntity>> $completion) {
    final String _sql = "SELECT * FROM installations WHERE status = ? ORDER BY updated_at DESC";
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
          final int _cursorIndexOfInstallationId = CursorUtil.getColumnIndexOrThrow(_cursor, "installation_id");
          final int _cursorIndexOfDropNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "drop_number");
          final int _cursorIndexOfTechnicianId = CursorUtil.getColumnIndexOrThrow(_cursor, "technician_id");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfStartTime = CursorUtil.getColumnIndexOrThrow(_cursor, "start_time");
          final int _cursorIndexOfEndTime = CursorUtil.getColumnIndexOrThrow(_cursor, "end_time");
          final int _cursorIndexOfOntSerial = CursorUtil.getColumnIndexOrThrow(_cursor, "ont_serial");
          final int _cursorIndexOfSpeedTestResults = CursorUtil.getColumnIndexOrThrow(_cursor, "speed_test_results");
          final int _cursorIndexOfPhotos = CursorUtil.getColumnIndexOrThrow(_cursor, "photos");
          final int _cursorIndexOfCompletedSteps = CursorUtil.getColumnIndexOrThrow(_cursor, "completed_steps");
          final int _cursorIndexOfCurrentStep = CursorUtil.getColumnIndexOrThrow(_cursor, "current_step");
          final int _cursorIndexOfTotalSteps = CursorUtil.getColumnIndexOrThrow(_cursor, "total_steps");
          final int _cursorIndexOfValidationErrors = CursorUtil.getColumnIndexOrThrow(_cursor, "validation_errors");
          final int _cursorIndexOfAiGuidanceUsed = CursorUtil.getColumnIndexOrThrow(_cursor, "ai_guidance_used");
          final int _cursorIndexOfManualOverrideUsed = CursorUtil.getColumnIndexOrThrow(_cursor, "manual_override_used");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final List<InstallationEntity> _result = new ArrayList<InstallationEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final InstallationEntity _item;
            final long _tmpInstallationId;
            _tmpInstallationId = _cursor.getLong(_cursorIndexOfInstallationId);
            final String _tmpDropNumber;
            if (_cursor.isNull(_cursorIndexOfDropNumber)) {
              _tmpDropNumber = null;
            } else {
              _tmpDropNumber = _cursor.getString(_cursorIndexOfDropNumber);
            }
            final String _tmpTechnicianId;
            if (_cursor.isNull(_cursorIndexOfTechnicianId)) {
              _tmpTechnicianId = null;
            } else {
              _tmpTechnicianId = _cursor.getString(_cursorIndexOfTechnicianId);
            }
            final InstallationStatus _tmpStatus;
            final String _tmp;
            if (_cursor.isNull(_cursorIndexOfStatus)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getString(_cursorIndexOfStatus);
            }
            _tmpStatus = __statusConverters.toInstallationStatus(_tmp);
            final Date _tmpStartTime;
            final Long _tmp_1;
            if (_cursor.isNull(_cursorIndexOfStartTime)) {
              _tmp_1 = null;
            } else {
              _tmp_1 = _cursor.getLong(_cursorIndexOfStartTime);
            }
            _tmpStartTime = __dateConverters.fromTimestamp(_tmp_1);
            final Date _tmpEndTime;
            final Long _tmp_2;
            if (_cursor.isNull(_cursorIndexOfEndTime)) {
              _tmp_2 = null;
            } else {
              _tmp_2 = _cursor.getLong(_cursorIndexOfEndTime);
            }
            _tmpEndTime = __dateConverters.fromTimestamp(_tmp_2);
            final String _tmpOntSerial;
            if (_cursor.isNull(_cursorIndexOfOntSerial)) {
              _tmpOntSerial = null;
            } else {
              _tmpOntSerial = _cursor.getString(_cursorIndexOfOntSerial);
            }
            final String _tmpSpeedTestResults;
            if (_cursor.isNull(_cursorIndexOfSpeedTestResults)) {
              _tmpSpeedTestResults = null;
            } else {
              _tmpSpeedTestResults = _cursor.getString(_cursorIndexOfSpeedTestResults);
            }
            final List<Long> _tmpPhotos;
            final String _tmp_3;
            if (_cursor.isNull(_cursorIndexOfPhotos)) {
              _tmp_3 = null;
            } else {
              _tmp_3 = _cursor.getString(_cursorIndexOfPhotos);
            }
            _tmpPhotos = __locationConverters.fromLongList(_tmp_3);
            final String _tmpCompletedSteps;
            if (_cursor.isNull(_cursorIndexOfCompletedSteps)) {
              _tmpCompletedSteps = null;
            } else {
              _tmpCompletedSteps = _cursor.getString(_cursorIndexOfCompletedSteps);
            }
            final int _tmpCurrentStep;
            _tmpCurrentStep = _cursor.getInt(_cursorIndexOfCurrentStep);
            final int _tmpTotalSteps;
            _tmpTotalSteps = _cursor.getInt(_cursorIndexOfTotalSteps);
            final String _tmpValidationErrors;
            if (_cursor.isNull(_cursorIndexOfValidationErrors)) {
              _tmpValidationErrors = null;
            } else {
              _tmpValidationErrors = _cursor.getString(_cursorIndexOfValidationErrors);
            }
            final boolean _tmpAiGuidanceUsed;
            final int _tmp_4;
            _tmp_4 = _cursor.getInt(_cursorIndexOfAiGuidanceUsed);
            _tmpAiGuidanceUsed = _tmp_4 != 0;
            final boolean _tmpManualOverrideUsed;
            final int _tmp_5;
            _tmp_5 = _cursor.getInt(_cursorIndexOfManualOverrideUsed);
            _tmpManualOverrideUsed = _tmp_5 != 0;
            final Date _tmpCreatedAt;
            final Long _tmp_6;
            if (_cursor.isNull(_cursorIndexOfCreatedAt)) {
              _tmp_6 = null;
            } else {
              _tmp_6 = _cursor.getLong(_cursorIndexOfCreatedAt);
            }
            _tmpCreatedAt = __dateConverters.fromTimestamp(_tmp_6);
            final Date _tmpUpdatedAt;
            final Long _tmp_7;
            if (_cursor.isNull(_cursorIndexOfUpdatedAt)) {
              _tmp_7 = null;
            } else {
              _tmp_7 = _cursor.getLong(_cursorIndexOfUpdatedAt);
            }
            _tmpUpdatedAt = __dateConverters.fromTimestamp(_tmp_7);
            _item = new InstallationEntity(_tmpInstallationId,_tmpDropNumber,_tmpTechnicianId,_tmpStatus,_tmpStartTime,_tmpEndTime,_tmpOntSerial,_tmpSpeedTestResults,_tmpPhotos,_tmpCompletedSteps,_tmpCurrentStep,_tmpTotalSteps,_tmpValidationErrors,_tmpAiGuidanceUsed,_tmpManualOverrideUsed,_tmpCreatedAt,_tmpUpdatedAt);
            _result.add(_item);
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
  public Flow<List<InstallationEntity>> getInstallationsByStatusFlow(final String status) {
    final String _sql = "SELECT * FROM installations WHERE status = ? ORDER BY updated_at DESC";
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
          final int _cursorIndexOfInstallationId = CursorUtil.getColumnIndexOrThrow(_cursor, "installation_id");
          final int _cursorIndexOfDropNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "drop_number");
          final int _cursorIndexOfTechnicianId = CursorUtil.getColumnIndexOrThrow(_cursor, "technician_id");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfStartTime = CursorUtil.getColumnIndexOrThrow(_cursor, "start_time");
          final int _cursorIndexOfEndTime = CursorUtil.getColumnIndexOrThrow(_cursor, "end_time");
          final int _cursorIndexOfOntSerial = CursorUtil.getColumnIndexOrThrow(_cursor, "ont_serial");
          final int _cursorIndexOfSpeedTestResults = CursorUtil.getColumnIndexOrThrow(_cursor, "speed_test_results");
          final int _cursorIndexOfPhotos = CursorUtil.getColumnIndexOrThrow(_cursor, "photos");
          final int _cursorIndexOfCompletedSteps = CursorUtil.getColumnIndexOrThrow(_cursor, "completed_steps");
          final int _cursorIndexOfCurrentStep = CursorUtil.getColumnIndexOrThrow(_cursor, "current_step");
          final int _cursorIndexOfTotalSteps = CursorUtil.getColumnIndexOrThrow(_cursor, "total_steps");
          final int _cursorIndexOfValidationErrors = CursorUtil.getColumnIndexOrThrow(_cursor, "validation_errors");
          final int _cursorIndexOfAiGuidanceUsed = CursorUtil.getColumnIndexOrThrow(_cursor, "ai_guidance_used");
          final int _cursorIndexOfManualOverrideUsed = CursorUtil.getColumnIndexOrThrow(_cursor, "manual_override_used");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final List<InstallationEntity> _result = new ArrayList<InstallationEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final InstallationEntity _item;
            final long _tmpInstallationId;
            _tmpInstallationId = _cursor.getLong(_cursorIndexOfInstallationId);
            final String _tmpDropNumber;
            if (_cursor.isNull(_cursorIndexOfDropNumber)) {
              _tmpDropNumber = null;
            } else {
              _tmpDropNumber = _cursor.getString(_cursorIndexOfDropNumber);
            }
            final String _tmpTechnicianId;
            if (_cursor.isNull(_cursorIndexOfTechnicianId)) {
              _tmpTechnicianId = null;
            } else {
              _tmpTechnicianId = _cursor.getString(_cursorIndexOfTechnicianId);
            }
            final InstallationStatus _tmpStatus;
            final String _tmp;
            if (_cursor.isNull(_cursorIndexOfStatus)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getString(_cursorIndexOfStatus);
            }
            _tmpStatus = __statusConverters.toInstallationStatus(_tmp);
            final Date _tmpStartTime;
            final Long _tmp_1;
            if (_cursor.isNull(_cursorIndexOfStartTime)) {
              _tmp_1 = null;
            } else {
              _tmp_1 = _cursor.getLong(_cursorIndexOfStartTime);
            }
            _tmpStartTime = __dateConverters.fromTimestamp(_tmp_1);
            final Date _tmpEndTime;
            final Long _tmp_2;
            if (_cursor.isNull(_cursorIndexOfEndTime)) {
              _tmp_2 = null;
            } else {
              _tmp_2 = _cursor.getLong(_cursorIndexOfEndTime);
            }
            _tmpEndTime = __dateConverters.fromTimestamp(_tmp_2);
            final String _tmpOntSerial;
            if (_cursor.isNull(_cursorIndexOfOntSerial)) {
              _tmpOntSerial = null;
            } else {
              _tmpOntSerial = _cursor.getString(_cursorIndexOfOntSerial);
            }
            final String _tmpSpeedTestResults;
            if (_cursor.isNull(_cursorIndexOfSpeedTestResults)) {
              _tmpSpeedTestResults = null;
            } else {
              _tmpSpeedTestResults = _cursor.getString(_cursorIndexOfSpeedTestResults);
            }
            final List<Long> _tmpPhotos;
            final String _tmp_3;
            if (_cursor.isNull(_cursorIndexOfPhotos)) {
              _tmp_3 = null;
            } else {
              _tmp_3 = _cursor.getString(_cursorIndexOfPhotos);
            }
            _tmpPhotos = __locationConverters.fromLongList(_tmp_3);
            final String _tmpCompletedSteps;
            if (_cursor.isNull(_cursorIndexOfCompletedSteps)) {
              _tmpCompletedSteps = null;
            } else {
              _tmpCompletedSteps = _cursor.getString(_cursorIndexOfCompletedSteps);
            }
            final int _tmpCurrentStep;
            _tmpCurrentStep = _cursor.getInt(_cursorIndexOfCurrentStep);
            final int _tmpTotalSteps;
            _tmpTotalSteps = _cursor.getInt(_cursorIndexOfTotalSteps);
            final String _tmpValidationErrors;
            if (_cursor.isNull(_cursorIndexOfValidationErrors)) {
              _tmpValidationErrors = null;
            } else {
              _tmpValidationErrors = _cursor.getString(_cursorIndexOfValidationErrors);
            }
            final boolean _tmpAiGuidanceUsed;
            final int _tmp_4;
            _tmp_4 = _cursor.getInt(_cursorIndexOfAiGuidanceUsed);
            _tmpAiGuidanceUsed = _tmp_4 != 0;
            final boolean _tmpManualOverrideUsed;
            final int _tmp_5;
            _tmp_5 = _cursor.getInt(_cursorIndexOfManualOverrideUsed);
            _tmpManualOverrideUsed = _tmp_5 != 0;
            final Date _tmpCreatedAt;
            final Long _tmp_6;
            if (_cursor.isNull(_cursorIndexOfCreatedAt)) {
              _tmp_6 = null;
            } else {
              _tmp_6 = _cursor.getLong(_cursorIndexOfCreatedAt);
            }
            _tmpCreatedAt = __dateConverters.fromTimestamp(_tmp_6);
            final Date _tmpUpdatedAt;
            final Long _tmp_7;
            if (_cursor.isNull(_cursorIndexOfUpdatedAt)) {
              _tmp_7 = null;
            } else {
              _tmp_7 = _cursor.getLong(_cursorIndexOfUpdatedAt);
            }
            _tmpUpdatedAt = __dateConverters.fromTimestamp(_tmp_7);
            _item = new InstallationEntity(_tmpInstallationId,_tmpDropNumber,_tmpTechnicianId,_tmpStatus,_tmpStartTime,_tmpEndTime,_tmpOntSerial,_tmpSpeedTestResults,_tmpPhotos,_tmpCompletedSteps,_tmpCurrentStep,_tmpTotalSteps,_tmpValidationErrors,_tmpAiGuidanceUsed,_tmpManualOverrideUsed,_tmpCreatedAt,_tmpUpdatedAt);
            _result.add(_item);
          }
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
    final String _sql = "SELECT * FROM installations WHERE status NOT IN ('COMPLETED', 'CANCELLED') ORDER BY created_at ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<InstallationEntity>>() {
      @Override
      @NonNull
      public List<InstallationEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfInstallationId = CursorUtil.getColumnIndexOrThrow(_cursor, "installation_id");
          final int _cursorIndexOfDropNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "drop_number");
          final int _cursorIndexOfTechnicianId = CursorUtil.getColumnIndexOrThrow(_cursor, "technician_id");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfStartTime = CursorUtil.getColumnIndexOrThrow(_cursor, "start_time");
          final int _cursorIndexOfEndTime = CursorUtil.getColumnIndexOrThrow(_cursor, "end_time");
          final int _cursorIndexOfOntSerial = CursorUtil.getColumnIndexOrThrow(_cursor, "ont_serial");
          final int _cursorIndexOfSpeedTestResults = CursorUtil.getColumnIndexOrThrow(_cursor, "speed_test_results");
          final int _cursorIndexOfPhotos = CursorUtil.getColumnIndexOrThrow(_cursor, "photos");
          final int _cursorIndexOfCompletedSteps = CursorUtil.getColumnIndexOrThrow(_cursor, "completed_steps");
          final int _cursorIndexOfCurrentStep = CursorUtil.getColumnIndexOrThrow(_cursor, "current_step");
          final int _cursorIndexOfTotalSteps = CursorUtil.getColumnIndexOrThrow(_cursor, "total_steps");
          final int _cursorIndexOfValidationErrors = CursorUtil.getColumnIndexOrThrow(_cursor, "validation_errors");
          final int _cursorIndexOfAiGuidanceUsed = CursorUtil.getColumnIndexOrThrow(_cursor, "ai_guidance_used");
          final int _cursorIndexOfManualOverrideUsed = CursorUtil.getColumnIndexOrThrow(_cursor, "manual_override_used");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final List<InstallationEntity> _result = new ArrayList<InstallationEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final InstallationEntity _item;
            final long _tmpInstallationId;
            _tmpInstallationId = _cursor.getLong(_cursorIndexOfInstallationId);
            final String _tmpDropNumber;
            if (_cursor.isNull(_cursorIndexOfDropNumber)) {
              _tmpDropNumber = null;
            } else {
              _tmpDropNumber = _cursor.getString(_cursorIndexOfDropNumber);
            }
            final String _tmpTechnicianId;
            if (_cursor.isNull(_cursorIndexOfTechnicianId)) {
              _tmpTechnicianId = null;
            } else {
              _tmpTechnicianId = _cursor.getString(_cursorIndexOfTechnicianId);
            }
            final InstallationStatus _tmpStatus;
            final String _tmp;
            if (_cursor.isNull(_cursorIndexOfStatus)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getString(_cursorIndexOfStatus);
            }
            _tmpStatus = __statusConverters.toInstallationStatus(_tmp);
            final Date _tmpStartTime;
            final Long _tmp_1;
            if (_cursor.isNull(_cursorIndexOfStartTime)) {
              _tmp_1 = null;
            } else {
              _tmp_1 = _cursor.getLong(_cursorIndexOfStartTime);
            }
            _tmpStartTime = __dateConverters.fromTimestamp(_tmp_1);
            final Date _tmpEndTime;
            final Long _tmp_2;
            if (_cursor.isNull(_cursorIndexOfEndTime)) {
              _tmp_2 = null;
            } else {
              _tmp_2 = _cursor.getLong(_cursorIndexOfEndTime);
            }
            _tmpEndTime = __dateConverters.fromTimestamp(_tmp_2);
            final String _tmpOntSerial;
            if (_cursor.isNull(_cursorIndexOfOntSerial)) {
              _tmpOntSerial = null;
            } else {
              _tmpOntSerial = _cursor.getString(_cursorIndexOfOntSerial);
            }
            final String _tmpSpeedTestResults;
            if (_cursor.isNull(_cursorIndexOfSpeedTestResults)) {
              _tmpSpeedTestResults = null;
            } else {
              _tmpSpeedTestResults = _cursor.getString(_cursorIndexOfSpeedTestResults);
            }
            final List<Long> _tmpPhotos;
            final String _tmp_3;
            if (_cursor.isNull(_cursorIndexOfPhotos)) {
              _tmp_3 = null;
            } else {
              _tmp_3 = _cursor.getString(_cursorIndexOfPhotos);
            }
            _tmpPhotos = __locationConverters.fromLongList(_tmp_3);
            final String _tmpCompletedSteps;
            if (_cursor.isNull(_cursorIndexOfCompletedSteps)) {
              _tmpCompletedSteps = null;
            } else {
              _tmpCompletedSteps = _cursor.getString(_cursorIndexOfCompletedSteps);
            }
            final int _tmpCurrentStep;
            _tmpCurrentStep = _cursor.getInt(_cursorIndexOfCurrentStep);
            final int _tmpTotalSteps;
            _tmpTotalSteps = _cursor.getInt(_cursorIndexOfTotalSteps);
            final String _tmpValidationErrors;
            if (_cursor.isNull(_cursorIndexOfValidationErrors)) {
              _tmpValidationErrors = null;
            } else {
              _tmpValidationErrors = _cursor.getString(_cursorIndexOfValidationErrors);
            }
            final boolean _tmpAiGuidanceUsed;
            final int _tmp_4;
            _tmp_4 = _cursor.getInt(_cursorIndexOfAiGuidanceUsed);
            _tmpAiGuidanceUsed = _tmp_4 != 0;
            final boolean _tmpManualOverrideUsed;
            final int _tmp_5;
            _tmp_5 = _cursor.getInt(_cursorIndexOfManualOverrideUsed);
            _tmpManualOverrideUsed = _tmp_5 != 0;
            final Date _tmpCreatedAt;
            final Long _tmp_6;
            if (_cursor.isNull(_cursorIndexOfCreatedAt)) {
              _tmp_6 = null;
            } else {
              _tmp_6 = _cursor.getLong(_cursorIndexOfCreatedAt);
            }
            _tmpCreatedAt = __dateConverters.fromTimestamp(_tmp_6);
            final Date _tmpUpdatedAt;
            final Long _tmp_7;
            if (_cursor.isNull(_cursorIndexOfUpdatedAt)) {
              _tmp_7 = null;
            } else {
              _tmp_7 = _cursor.getLong(_cursorIndexOfUpdatedAt);
            }
            _tmpUpdatedAt = __dateConverters.fromTimestamp(_tmp_7);
            _item = new InstallationEntity(_tmpInstallationId,_tmpDropNumber,_tmpTechnicianId,_tmpStatus,_tmpStartTime,_tmpEndTime,_tmpOntSerial,_tmpSpeedTestResults,_tmpPhotos,_tmpCompletedSteps,_tmpCurrentStep,_tmpTotalSteps,_tmpValidationErrors,_tmpAiGuidanceUsed,_tmpManualOverrideUsed,_tmpCreatedAt,_tmpUpdatedAt);
            _result.add(_item);
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
  public Flow<List<InstallationEntity>> getPendingInstallationsFlow() {
    final String _sql = "SELECT * FROM installations WHERE status NOT IN ('COMPLETED', 'CANCELLED') ORDER BY created_at ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"installations"}, new Callable<List<InstallationEntity>>() {
      @Override
      @NonNull
      public List<InstallationEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfInstallationId = CursorUtil.getColumnIndexOrThrow(_cursor, "installation_id");
          final int _cursorIndexOfDropNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "drop_number");
          final int _cursorIndexOfTechnicianId = CursorUtil.getColumnIndexOrThrow(_cursor, "technician_id");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfStartTime = CursorUtil.getColumnIndexOrThrow(_cursor, "start_time");
          final int _cursorIndexOfEndTime = CursorUtil.getColumnIndexOrThrow(_cursor, "end_time");
          final int _cursorIndexOfOntSerial = CursorUtil.getColumnIndexOrThrow(_cursor, "ont_serial");
          final int _cursorIndexOfSpeedTestResults = CursorUtil.getColumnIndexOrThrow(_cursor, "speed_test_results");
          final int _cursorIndexOfPhotos = CursorUtil.getColumnIndexOrThrow(_cursor, "photos");
          final int _cursorIndexOfCompletedSteps = CursorUtil.getColumnIndexOrThrow(_cursor, "completed_steps");
          final int _cursorIndexOfCurrentStep = CursorUtil.getColumnIndexOrThrow(_cursor, "current_step");
          final int _cursorIndexOfTotalSteps = CursorUtil.getColumnIndexOrThrow(_cursor, "total_steps");
          final int _cursorIndexOfValidationErrors = CursorUtil.getColumnIndexOrThrow(_cursor, "validation_errors");
          final int _cursorIndexOfAiGuidanceUsed = CursorUtil.getColumnIndexOrThrow(_cursor, "ai_guidance_used");
          final int _cursorIndexOfManualOverrideUsed = CursorUtil.getColumnIndexOrThrow(_cursor, "manual_override_used");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final List<InstallationEntity> _result = new ArrayList<InstallationEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final InstallationEntity _item;
            final long _tmpInstallationId;
            _tmpInstallationId = _cursor.getLong(_cursorIndexOfInstallationId);
            final String _tmpDropNumber;
            if (_cursor.isNull(_cursorIndexOfDropNumber)) {
              _tmpDropNumber = null;
            } else {
              _tmpDropNumber = _cursor.getString(_cursorIndexOfDropNumber);
            }
            final String _tmpTechnicianId;
            if (_cursor.isNull(_cursorIndexOfTechnicianId)) {
              _tmpTechnicianId = null;
            } else {
              _tmpTechnicianId = _cursor.getString(_cursorIndexOfTechnicianId);
            }
            final InstallationStatus _tmpStatus;
            final String _tmp;
            if (_cursor.isNull(_cursorIndexOfStatus)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getString(_cursorIndexOfStatus);
            }
            _tmpStatus = __statusConverters.toInstallationStatus(_tmp);
            final Date _tmpStartTime;
            final Long _tmp_1;
            if (_cursor.isNull(_cursorIndexOfStartTime)) {
              _tmp_1 = null;
            } else {
              _tmp_1 = _cursor.getLong(_cursorIndexOfStartTime);
            }
            _tmpStartTime = __dateConverters.fromTimestamp(_tmp_1);
            final Date _tmpEndTime;
            final Long _tmp_2;
            if (_cursor.isNull(_cursorIndexOfEndTime)) {
              _tmp_2 = null;
            } else {
              _tmp_2 = _cursor.getLong(_cursorIndexOfEndTime);
            }
            _tmpEndTime = __dateConverters.fromTimestamp(_tmp_2);
            final String _tmpOntSerial;
            if (_cursor.isNull(_cursorIndexOfOntSerial)) {
              _tmpOntSerial = null;
            } else {
              _tmpOntSerial = _cursor.getString(_cursorIndexOfOntSerial);
            }
            final String _tmpSpeedTestResults;
            if (_cursor.isNull(_cursorIndexOfSpeedTestResults)) {
              _tmpSpeedTestResults = null;
            } else {
              _tmpSpeedTestResults = _cursor.getString(_cursorIndexOfSpeedTestResults);
            }
            final List<Long> _tmpPhotos;
            final String _tmp_3;
            if (_cursor.isNull(_cursorIndexOfPhotos)) {
              _tmp_3 = null;
            } else {
              _tmp_3 = _cursor.getString(_cursorIndexOfPhotos);
            }
            _tmpPhotos = __locationConverters.fromLongList(_tmp_3);
            final String _tmpCompletedSteps;
            if (_cursor.isNull(_cursorIndexOfCompletedSteps)) {
              _tmpCompletedSteps = null;
            } else {
              _tmpCompletedSteps = _cursor.getString(_cursorIndexOfCompletedSteps);
            }
            final int _tmpCurrentStep;
            _tmpCurrentStep = _cursor.getInt(_cursorIndexOfCurrentStep);
            final int _tmpTotalSteps;
            _tmpTotalSteps = _cursor.getInt(_cursorIndexOfTotalSteps);
            final String _tmpValidationErrors;
            if (_cursor.isNull(_cursorIndexOfValidationErrors)) {
              _tmpValidationErrors = null;
            } else {
              _tmpValidationErrors = _cursor.getString(_cursorIndexOfValidationErrors);
            }
            final boolean _tmpAiGuidanceUsed;
            final int _tmp_4;
            _tmp_4 = _cursor.getInt(_cursorIndexOfAiGuidanceUsed);
            _tmpAiGuidanceUsed = _tmp_4 != 0;
            final boolean _tmpManualOverrideUsed;
            final int _tmp_5;
            _tmp_5 = _cursor.getInt(_cursorIndexOfManualOverrideUsed);
            _tmpManualOverrideUsed = _tmp_5 != 0;
            final Date _tmpCreatedAt;
            final Long _tmp_6;
            if (_cursor.isNull(_cursorIndexOfCreatedAt)) {
              _tmp_6 = null;
            } else {
              _tmp_6 = _cursor.getLong(_cursorIndexOfCreatedAt);
            }
            _tmpCreatedAt = __dateConverters.fromTimestamp(_tmp_6);
            final Date _tmpUpdatedAt;
            final Long _tmp_7;
            if (_cursor.isNull(_cursorIndexOfUpdatedAt)) {
              _tmp_7 = null;
            } else {
              _tmp_7 = _cursor.getLong(_cursorIndexOfUpdatedAt);
            }
            _tmpUpdatedAt = __dateConverters.fromTimestamp(_tmp_7);
            _item = new InstallationEntity(_tmpInstallationId,_tmpDropNumber,_tmpTechnicianId,_tmpStatus,_tmpStartTime,_tmpEndTime,_tmpOntSerial,_tmpSpeedTestResults,_tmpPhotos,_tmpCompletedSteps,_tmpCurrentStep,_tmpTotalSteps,_tmpValidationErrors,_tmpAiGuidanceUsed,_tmpManualOverrideUsed,_tmpCreatedAt,_tmpUpdatedAt);
            _result.add(_item);
          }
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
    final String _sql = "SELECT * FROM installations WHERE status = 'COMPLETED' AND end_time BETWEEN ? AND ? ORDER BY end_time DESC";
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
          final int _cursorIndexOfInstallationId = CursorUtil.getColumnIndexOrThrow(_cursor, "installation_id");
          final int _cursorIndexOfDropNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "drop_number");
          final int _cursorIndexOfTechnicianId = CursorUtil.getColumnIndexOrThrow(_cursor, "technician_id");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfStartTime = CursorUtil.getColumnIndexOrThrow(_cursor, "start_time");
          final int _cursorIndexOfEndTime = CursorUtil.getColumnIndexOrThrow(_cursor, "end_time");
          final int _cursorIndexOfOntSerial = CursorUtil.getColumnIndexOrThrow(_cursor, "ont_serial");
          final int _cursorIndexOfSpeedTestResults = CursorUtil.getColumnIndexOrThrow(_cursor, "speed_test_results");
          final int _cursorIndexOfPhotos = CursorUtil.getColumnIndexOrThrow(_cursor, "photos");
          final int _cursorIndexOfCompletedSteps = CursorUtil.getColumnIndexOrThrow(_cursor, "completed_steps");
          final int _cursorIndexOfCurrentStep = CursorUtil.getColumnIndexOrThrow(_cursor, "current_step");
          final int _cursorIndexOfTotalSteps = CursorUtil.getColumnIndexOrThrow(_cursor, "total_steps");
          final int _cursorIndexOfValidationErrors = CursorUtil.getColumnIndexOrThrow(_cursor, "validation_errors");
          final int _cursorIndexOfAiGuidanceUsed = CursorUtil.getColumnIndexOrThrow(_cursor, "ai_guidance_used");
          final int _cursorIndexOfManualOverrideUsed = CursorUtil.getColumnIndexOrThrow(_cursor, "manual_override_used");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final List<InstallationEntity> _result = new ArrayList<InstallationEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final InstallationEntity _item;
            final long _tmpInstallationId;
            _tmpInstallationId = _cursor.getLong(_cursorIndexOfInstallationId);
            final String _tmpDropNumber;
            if (_cursor.isNull(_cursorIndexOfDropNumber)) {
              _tmpDropNumber = null;
            } else {
              _tmpDropNumber = _cursor.getString(_cursorIndexOfDropNumber);
            }
            final String _tmpTechnicianId;
            if (_cursor.isNull(_cursorIndexOfTechnicianId)) {
              _tmpTechnicianId = null;
            } else {
              _tmpTechnicianId = _cursor.getString(_cursorIndexOfTechnicianId);
            }
            final InstallationStatus _tmpStatus;
            final String _tmp;
            if (_cursor.isNull(_cursorIndexOfStatus)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getString(_cursorIndexOfStatus);
            }
            _tmpStatus = __statusConverters.toInstallationStatus(_tmp);
            final Date _tmpStartTime;
            final Long _tmp_1;
            if (_cursor.isNull(_cursorIndexOfStartTime)) {
              _tmp_1 = null;
            } else {
              _tmp_1 = _cursor.getLong(_cursorIndexOfStartTime);
            }
            _tmpStartTime = __dateConverters.fromTimestamp(_tmp_1);
            final Date _tmpEndTime;
            final Long _tmp_2;
            if (_cursor.isNull(_cursorIndexOfEndTime)) {
              _tmp_2 = null;
            } else {
              _tmp_2 = _cursor.getLong(_cursorIndexOfEndTime);
            }
            _tmpEndTime = __dateConverters.fromTimestamp(_tmp_2);
            final String _tmpOntSerial;
            if (_cursor.isNull(_cursorIndexOfOntSerial)) {
              _tmpOntSerial = null;
            } else {
              _tmpOntSerial = _cursor.getString(_cursorIndexOfOntSerial);
            }
            final String _tmpSpeedTestResults;
            if (_cursor.isNull(_cursorIndexOfSpeedTestResults)) {
              _tmpSpeedTestResults = null;
            } else {
              _tmpSpeedTestResults = _cursor.getString(_cursorIndexOfSpeedTestResults);
            }
            final List<Long> _tmpPhotos;
            final String _tmp_3;
            if (_cursor.isNull(_cursorIndexOfPhotos)) {
              _tmp_3 = null;
            } else {
              _tmp_3 = _cursor.getString(_cursorIndexOfPhotos);
            }
            _tmpPhotos = __locationConverters.fromLongList(_tmp_3);
            final String _tmpCompletedSteps;
            if (_cursor.isNull(_cursorIndexOfCompletedSteps)) {
              _tmpCompletedSteps = null;
            } else {
              _tmpCompletedSteps = _cursor.getString(_cursorIndexOfCompletedSteps);
            }
            final int _tmpCurrentStep;
            _tmpCurrentStep = _cursor.getInt(_cursorIndexOfCurrentStep);
            final int _tmpTotalSteps;
            _tmpTotalSteps = _cursor.getInt(_cursorIndexOfTotalSteps);
            final String _tmpValidationErrors;
            if (_cursor.isNull(_cursorIndexOfValidationErrors)) {
              _tmpValidationErrors = null;
            } else {
              _tmpValidationErrors = _cursor.getString(_cursorIndexOfValidationErrors);
            }
            final boolean _tmpAiGuidanceUsed;
            final int _tmp_4;
            _tmp_4 = _cursor.getInt(_cursorIndexOfAiGuidanceUsed);
            _tmpAiGuidanceUsed = _tmp_4 != 0;
            final boolean _tmpManualOverrideUsed;
            final int _tmp_5;
            _tmp_5 = _cursor.getInt(_cursorIndexOfManualOverrideUsed);
            _tmpManualOverrideUsed = _tmp_5 != 0;
            final Date _tmpCreatedAt;
            final Long _tmp_6;
            if (_cursor.isNull(_cursorIndexOfCreatedAt)) {
              _tmp_6 = null;
            } else {
              _tmp_6 = _cursor.getLong(_cursorIndexOfCreatedAt);
            }
            _tmpCreatedAt = __dateConverters.fromTimestamp(_tmp_6);
            final Date _tmpUpdatedAt;
            final Long _tmp_7;
            if (_cursor.isNull(_cursorIndexOfUpdatedAt)) {
              _tmp_7 = null;
            } else {
              _tmp_7 = _cursor.getLong(_cursorIndexOfUpdatedAt);
            }
            _tmpUpdatedAt = __dateConverters.fromTimestamp(_tmp_7);
            _item = new InstallationEntity(_tmpInstallationId,_tmpDropNumber,_tmpTechnicianId,_tmpStatus,_tmpStartTime,_tmpEndTime,_tmpOntSerial,_tmpSpeedTestResults,_tmpPhotos,_tmpCompletedSteps,_tmpCurrentStep,_tmpTotalSteps,_tmpValidationErrors,_tmpAiGuidanceUsed,_tmpManualOverrideUsed,_tmpCreatedAt,_tmpUpdatedAt);
            _result.add(_item);
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
  public Object getInstallationsNeedingSync(
      final Continuation<? super List<InstallationEntity>> $completion) {
    final String _sql = "SELECT * FROM installations WHERE ai_guidance_used = 1 ORDER BY updated_at ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<InstallationEntity>>() {
      @Override
      @NonNull
      public List<InstallationEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfInstallationId = CursorUtil.getColumnIndexOrThrow(_cursor, "installation_id");
          final int _cursorIndexOfDropNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "drop_number");
          final int _cursorIndexOfTechnicianId = CursorUtil.getColumnIndexOrThrow(_cursor, "technician_id");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfStartTime = CursorUtil.getColumnIndexOrThrow(_cursor, "start_time");
          final int _cursorIndexOfEndTime = CursorUtil.getColumnIndexOrThrow(_cursor, "end_time");
          final int _cursorIndexOfOntSerial = CursorUtil.getColumnIndexOrThrow(_cursor, "ont_serial");
          final int _cursorIndexOfSpeedTestResults = CursorUtil.getColumnIndexOrThrow(_cursor, "speed_test_results");
          final int _cursorIndexOfPhotos = CursorUtil.getColumnIndexOrThrow(_cursor, "photos");
          final int _cursorIndexOfCompletedSteps = CursorUtil.getColumnIndexOrThrow(_cursor, "completed_steps");
          final int _cursorIndexOfCurrentStep = CursorUtil.getColumnIndexOrThrow(_cursor, "current_step");
          final int _cursorIndexOfTotalSteps = CursorUtil.getColumnIndexOrThrow(_cursor, "total_steps");
          final int _cursorIndexOfValidationErrors = CursorUtil.getColumnIndexOrThrow(_cursor, "validation_errors");
          final int _cursorIndexOfAiGuidanceUsed = CursorUtil.getColumnIndexOrThrow(_cursor, "ai_guidance_used");
          final int _cursorIndexOfManualOverrideUsed = CursorUtil.getColumnIndexOrThrow(_cursor, "manual_override_used");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final List<InstallationEntity> _result = new ArrayList<InstallationEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final InstallationEntity _item;
            final long _tmpInstallationId;
            _tmpInstallationId = _cursor.getLong(_cursorIndexOfInstallationId);
            final String _tmpDropNumber;
            if (_cursor.isNull(_cursorIndexOfDropNumber)) {
              _tmpDropNumber = null;
            } else {
              _tmpDropNumber = _cursor.getString(_cursorIndexOfDropNumber);
            }
            final String _tmpTechnicianId;
            if (_cursor.isNull(_cursorIndexOfTechnicianId)) {
              _tmpTechnicianId = null;
            } else {
              _tmpTechnicianId = _cursor.getString(_cursorIndexOfTechnicianId);
            }
            final InstallationStatus _tmpStatus;
            final String _tmp;
            if (_cursor.isNull(_cursorIndexOfStatus)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getString(_cursorIndexOfStatus);
            }
            _tmpStatus = __statusConverters.toInstallationStatus(_tmp);
            final Date _tmpStartTime;
            final Long _tmp_1;
            if (_cursor.isNull(_cursorIndexOfStartTime)) {
              _tmp_1 = null;
            } else {
              _tmp_1 = _cursor.getLong(_cursorIndexOfStartTime);
            }
            _tmpStartTime = __dateConverters.fromTimestamp(_tmp_1);
            final Date _tmpEndTime;
            final Long _tmp_2;
            if (_cursor.isNull(_cursorIndexOfEndTime)) {
              _tmp_2 = null;
            } else {
              _tmp_2 = _cursor.getLong(_cursorIndexOfEndTime);
            }
            _tmpEndTime = __dateConverters.fromTimestamp(_tmp_2);
            final String _tmpOntSerial;
            if (_cursor.isNull(_cursorIndexOfOntSerial)) {
              _tmpOntSerial = null;
            } else {
              _tmpOntSerial = _cursor.getString(_cursorIndexOfOntSerial);
            }
            final String _tmpSpeedTestResults;
            if (_cursor.isNull(_cursorIndexOfSpeedTestResults)) {
              _tmpSpeedTestResults = null;
            } else {
              _tmpSpeedTestResults = _cursor.getString(_cursorIndexOfSpeedTestResults);
            }
            final List<Long> _tmpPhotos;
            final String _tmp_3;
            if (_cursor.isNull(_cursorIndexOfPhotos)) {
              _tmp_3 = null;
            } else {
              _tmp_3 = _cursor.getString(_cursorIndexOfPhotos);
            }
            _tmpPhotos = __locationConverters.fromLongList(_tmp_3);
            final String _tmpCompletedSteps;
            if (_cursor.isNull(_cursorIndexOfCompletedSteps)) {
              _tmpCompletedSteps = null;
            } else {
              _tmpCompletedSteps = _cursor.getString(_cursorIndexOfCompletedSteps);
            }
            final int _tmpCurrentStep;
            _tmpCurrentStep = _cursor.getInt(_cursorIndexOfCurrentStep);
            final int _tmpTotalSteps;
            _tmpTotalSteps = _cursor.getInt(_cursorIndexOfTotalSteps);
            final String _tmpValidationErrors;
            if (_cursor.isNull(_cursorIndexOfValidationErrors)) {
              _tmpValidationErrors = null;
            } else {
              _tmpValidationErrors = _cursor.getString(_cursorIndexOfValidationErrors);
            }
            final boolean _tmpAiGuidanceUsed;
            final int _tmp_4;
            _tmp_4 = _cursor.getInt(_cursorIndexOfAiGuidanceUsed);
            _tmpAiGuidanceUsed = _tmp_4 != 0;
            final boolean _tmpManualOverrideUsed;
            final int _tmp_5;
            _tmp_5 = _cursor.getInt(_cursorIndexOfManualOverrideUsed);
            _tmpManualOverrideUsed = _tmp_5 != 0;
            final Date _tmpCreatedAt;
            final Long _tmp_6;
            if (_cursor.isNull(_cursorIndexOfCreatedAt)) {
              _tmp_6 = null;
            } else {
              _tmp_6 = _cursor.getLong(_cursorIndexOfCreatedAt);
            }
            _tmpCreatedAt = __dateConverters.fromTimestamp(_tmp_6);
            final Date _tmpUpdatedAt;
            final Long _tmp_7;
            if (_cursor.isNull(_cursorIndexOfUpdatedAt)) {
              _tmp_7 = null;
            } else {
              _tmp_7 = _cursor.getLong(_cursorIndexOfUpdatedAt);
            }
            _tmpUpdatedAt = __dateConverters.fromTimestamp(_tmp_7);
            _item = new InstallationEntity(_tmpInstallationId,_tmpDropNumber,_tmpTechnicianId,_tmpStatus,_tmpStartTime,_tmpEndTime,_tmpOntSerial,_tmpSpeedTestResults,_tmpPhotos,_tmpCompletedSteps,_tmpCurrentStep,_tmpTotalSteps,_tmpValidationErrors,_tmpAiGuidanceUsed,_tmpManualOverrideUsed,_tmpCreatedAt,_tmpUpdatedAt);
            _result.add(_item);
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
    final String _sql = "SELECT * FROM installations WHERE created_at BETWEEN ? AND ? ORDER BY created_at DESC";
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
          final int _cursorIndexOfInstallationId = CursorUtil.getColumnIndexOrThrow(_cursor, "installation_id");
          final int _cursorIndexOfDropNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "drop_number");
          final int _cursorIndexOfTechnicianId = CursorUtil.getColumnIndexOrThrow(_cursor, "technician_id");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfStartTime = CursorUtil.getColumnIndexOrThrow(_cursor, "start_time");
          final int _cursorIndexOfEndTime = CursorUtil.getColumnIndexOrThrow(_cursor, "end_time");
          final int _cursorIndexOfOntSerial = CursorUtil.getColumnIndexOrThrow(_cursor, "ont_serial");
          final int _cursorIndexOfSpeedTestResults = CursorUtil.getColumnIndexOrThrow(_cursor, "speed_test_results");
          final int _cursorIndexOfPhotos = CursorUtil.getColumnIndexOrThrow(_cursor, "photos");
          final int _cursorIndexOfCompletedSteps = CursorUtil.getColumnIndexOrThrow(_cursor, "completed_steps");
          final int _cursorIndexOfCurrentStep = CursorUtil.getColumnIndexOrThrow(_cursor, "current_step");
          final int _cursorIndexOfTotalSteps = CursorUtil.getColumnIndexOrThrow(_cursor, "total_steps");
          final int _cursorIndexOfValidationErrors = CursorUtil.getColumnIndexOrThrow(_cursor, "validation_errors");
          final int _cursorIndexOfAiGuidanceUsed = CursorUtil.getColumnIndexOrThrow(_cursor, "ai_guidance_used");
          final int _cursorIndexOfManualOverrideUsed = CursorUtil.getColumnIndexOrThrow(_cursor, "manual_override_used");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final List<InstallationEntity> _result = new ArrayList<InstallationEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final InstallationEntity _item;
            final long _tmpInstallationId;
            _tmpInstallationId = _cursor.getLong(_cursorIndexOfInstallationId);
            final String _tmpDropNumber;
            if (_cursor.isNull(_cursorIndexOfDropNumber)) {
              _tmpDropNumber = null;
            } else {
              _tmpDropNumber = _cursor.getString(_cursorIndexOfDropNumber);
            }
            final String _tmpTechnicianId;
            if (_cursor.isNull(_cursorIndexOfTechnicianId)) {
              _tmpTechnicianId = null;
            } else {
              _tmpTechnicianId = _cursor.getString(_cursorIndexOfTechnicianId);
            }
            final InstallationStatus _tmpStatus;
            final String _tmp;
            if (_cursor.isNull(_cursorIndexOfStatus)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getString(_cursorIndexOfStatus);
            }
            _tmpStatus = __statusConverters.toInstallationStatus(_tmp);
            final Date _tmpStartTime;
            final Long _tmp_1;
            if (_cursor.isNull(_cursorIndexOfStartTime)) {
              _tmp_1 = null;
            } else {
              _tmp_1 = _cursor.getLong(_cursorIndexOfStartTime);
            }
            _tmpStartTime = __dateConverters.fromTimestamp(_tmp_1);
            final Date _tmpEndTime;
            final Long _tmp_2;
            if (_cursor.isNull(_cursorIndexOfEndTime)) {
              _tmp_2 = null;
            } else {
              _tmp_2 = _cursor.getLong(_cursorIndexOfEndTime);
            }
            _tmpEndTime = __dateConverters.fromTimestamp(_tmp_2);
            final String _tmpOntSerial;
            if (_cursor.isNull(_cursorIndexOfOntSerial)) {
              _tmpOntSerial = null;
            } else {
              _tmpOntSerial = _cursor.getString(_cursorIndexOfOntSerial);
            }
            final String _tmpSpeedTestResults;
            if (_cursor.isNull(_cursorIndexOfSpeedTestResults)) {
              _tmpSpeedTestResults = null;
            } else {
              _tmpSpeedTestResults = _cursor.getString(_cursorIndexOfSpeedTestResults);
            }
            final List<Long> _tmpPhotos;
            final String _tmp_3;
            if (_cursor.isNull(_cursorIndexOfPhotos)) {
              _tmp_3 = null;
            } else {
              _tmp_3 = _cursor.getString(_cursorIndexOfPhotos);
            }
            _tmpPhotos = __locationConverters.fromLongList(_tmp_3);
            final String _tmpCompletedSteps;
            if (_cursor.isNull(_cursorIndexOfCompletedSteps)) {
              _tmpCompletedSteps = null;
            } else {
              _tmpCompletedSteps = _cursor.getString(_cursorIndexOfCompletedSteps);
            }
            final int _tmpCurrentStep;
            _tmpCurrentStep = _cursor.getInt(_cursorIndexOfCurrentStep);
            final int _tmpTotalSteps;
            _tmpTotalSteps = _cursor.getInt(_cursorIndexOfTotalSteps);
            final String _tmpValidationErrors;
            if (_cursor.isNull(_cursorIndexOfValidationErrors)) {
              _tmpValidationErrors = null;
            } else {
              _tmpValidationErrors = _cursor.getString(_cursorIndexOfValidationErrors);
            }
            final boolean _tmpAiGuidanceUsed;
            final int _tmp_4;
            _tmp_4 = _cursor.getInt(_cursorIndexOfAiGuidanceUsed);
            _tmpAiGuidanceUsed = _tmp_4 != 0;
            final boolean _tmpManualOverrideUsed;
            final int _tmp_5;
            _tmp_5 = _cursor.getInt(_cursorIndexOfManualOverrideUsed);
            _tmpManualOverrideUsed = _tmp_5 != 0;
            final Date _tmpCreatedAt;
            final Long _tmp_6;
            if (_cursor.isNull(_cursorIndexOfCreatedAt)) {
              _tmp_6 = null;
            } else {
              _tmp_6 = _cursor.getLong(_cursorIndexOfCreatedAt);
            }
            _tmpCreatedAt = __dateConverters.fromTimestamp(_tmp_6);
            final Date _tmpUpdatedAt;
            final Long _tmp_7;
            if (_cursor.isNull(_cursorIndexOfUpdatedAt)) {
              _tmp_7 = null;
            } else {
              _tmp_7 = _cursor.getLong(_cursorIndexOfUpdatedAt);
            }
            _tmpUpdatedAt = __dateConverters.fromTimestamp(_tmp_7);
            _item = new InstallationEntity(_tmpInstallationId,_tmpDropNumber,_tmpTechnicianId,_tmpStatus,_tmpStartTime,_tmpEndTime,_tmpOntSerial,_tmpSpeedTestResults,_tmpPhotos,_tmpCompletedSteps,_tmpCurrentStep,_tmpTotalSteps,_tmpValidationErrors,_tmpAiGuidanceUsed,_tmpManualOverrideUsed,_tmpCreatedAt,_tmpUpdatedAt);
            _result.add(_item);
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
  public Object searchInstallationsByEquipment(final String ontSerial,
      final Continuation<? super List<InstallationEntity>> $completion) {
    final String _sql = "SELECT * FROM installations WHERE ont_serial LIKE '%' || ? || '%' ORDER BY created_at DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    if (ontSerial == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, ontSerial);
    }
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<InstallationEntity>>() {
      @Override
      @NonNull
      public List<InstallationEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfInstallationId = CursorUtil.getColumnIndexOrThrow(_cursor, "installation_id");
          final int _cursorIndexOfDropNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "drop_number");
          final int _cursorIndexOfTechnicianId = CursorUtil.getColumnIndexOrThrow(_cursor, "technician_id");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfStartTime = CursorUtil.getColumnIndexOrThrow(_cursor, "start_time");
          final int _cursorIndexOfEndTime = CursorUtil.getColumnIndexOrThrow(_cursor, "end_time");
          final int _cursorIndexOfOntSerial = CursorUtil.getColumnIndexOrThrow(_cursor, "ont_serial");
          final int _cursorIndexOfSpeedTestResults = CursorUtil.getColumnIndexOrThrow(_cursor, "speed_test_results");
          final int _cursorIndexOfPhotos = CursorUtil.getColumnIndexOrThrow(_cursor, "photos");
          final int _cursorIndexOfCompletedSteps = CursorUtil.getColumnIndexOrThrow(_cursor, "completed_steps");
          final int _cursorIndexOfCurrentStep = CursorUtil.getColumnIndexOrThrow(_cursor, "current_step");
          final int _cursorIndexOfTotalSteps = CursorUtil.getColumnIndexOrThrow(_cursor, "total_steps");
          final int _cursorIndexOfValidationErrors = CursorUtil.getColumnIndexOrThrow(_cursor, "validation_errors");
          final int _cursorIndexOfAiGuidanceUsed = CursorUtil.getColumnIndexOrThrow(_cursor, "ai_guidance_used");
          final int _cursorIndexOfManualOverrideUsed = CursorUtil.getColumnIndexOrThrow(_cursor, "manual_override_used");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final List<InstallationEntity> _result = new ArrayList<InstallationEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final InstallationEntity _item;
            final long _tmpInstallationId;
            _tmpInstallationId = _cursor.getLong(_cursorIndexOfInstallationId);
            final String _tmpDropNumber;
            if (_cursor.isNull(_cursorIndexOfDropNumber)) {
              _tmpDropNumber = null;
            } else {
              _tmpDropNumber = _cursor.getString(_cursorIndexOfDropNumber);
            }
            final String _tmpTechnicianId;
            if (_cursor.isNull(_cursorIndexOfTechnicianId)) {
              _tmpTechnicianId = null;
            } else {
              _tmpTechnicianId = _cursor.getString(_cursorIndexOfTechnicianId);
            }
            final InstallationStatus _tmpStatus;
            final String _tmp;
            if (_cursor.isNull(_cursorIndexOfStatus)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getString(_cursorIndexOfStatus);
            }
            _tmpStatus = __statusConverters.toInstallationStatus(_tmp);
            final Date _tmpStartTime;
            final Long _tmp_1;
            if (_cursor.isNull(_cursorIndexOfStartTime)) {
              _tmp_1 = null;
            } else {
              _tmp_1 = _cursor.getLong(_cursorIndexOfStartTime);
            }
            _tmpStartTime = __dateConverters.fromTimestamp(_tmp_1);
            final Date _tmpEndTime;
            final Long _tmp_2;
            if (_cursor.isNull(_cursorIndexOfEndTime)) {
              _tmp_2 = null;
            } else {
              _tmp_2 = _cursor.getLong(_cursorIndexOfEndTime);
            }
            _tmpEndTime = __dateConverters.fromTimestamp(_tmp_2);
            final String _tmpOntSerial;
            if (_cursor.isNull(_cursorIndexOfOntSerial)) {
              _tmpOntSerial = null;
            } else {
              _tmpOntSerial = _cursor.getString(_cursorIndexOfOntSerial);
            }
            final String _tmpSpeedTestResults;
            if (_cursor.isNull(_cursorIndexOfSpeedTestResults)) {
              _tmpSpeedTestResults = null;
            } else {
              _tmpSpeedTestResults = _cursor.getString(_cursorIndexOfSpeedTestResults);
            }
            final List<Long> _tmpPhotos;
            final String _tmp_3;
            if (_cursor.isNull(_cursorIndexOfPhotos)) {
              _tmp_3 = null;
            } else {
              _tmp_3 = _cursor.getString(_cursorIndexOfPhotos);
            }
            _tmpPhotos = __locationConverters.fromLongList(_tmp_3);
            final String _tmpCompletedSteps;
            if (_cursor.isNull(_cursorIndexOfCompletedSteps)) {
              _tmpCompletedSteps = null;
            } else {
              _tmpCompletedSteps = _cursor.getString(_cursorIndexOfCompletedSteps);
            }
            final int _tmpCurrentStep;
            _tmpCurrentStep = _cursor.getInt(_cursorIndexOfCurrentStep);
            final int _tmpTotalSteps;
            _tmpTotalSteps = _cursor.getInt(_cursorIndexOfTotalSteps);
            final String _tmpValidationErrors;
            if (_cursor.isNull(_cursorIndexOfValidationErrors)) {
              _tmpValidationErrors = null;
            } else {
              _tmpValidationErrors = _cursor.getString(_cursorIndexOfValidationErrors);
            }
            final boolean _tmpAiGuidanceUsed;
            final int _tmp_4;
            _tmp_4 = _cursor.getInt(_cursorIndexOfAiGuidanceUsed);
            _tmpAiGuidanceUsed = _tmp_4 != 0;
            final boolean _tmpManualOverrideUsed;
            final int _tmp_5;
            _tmp_5 = _cursor.getInt(_cursorIndexOfManualOverrideUsed);
            _tmpManualOverrideUsed = _tmp_5 != 0;
            final Date _tmpCreatedAt;
            final Long _tmp_6;
            if (_cursor.isNull(_cursorIndexOfCreatedAt)) {
              _tmp_6 = null;
            } else {
              _tmp_6 = _cursor.getLong(_cursorIndexOfCreatedAt);
            }
            _tmpCreatedAt = __dateConverters.fromTimestamp(_tmp_6);
            final Date _tmpUpdatedAt;
            final Long _tmp_7;
            if (_cursor.isNull(_cursorIndexOfUpdatedAt)) {
              _tmp_7 = null;
            } else {
              _tmp_7 = _cursor.getLong(_cursorIndexOfUpdatedAt);
            }
            _tmpUpdatedAt = __dateConverters.fromTimestamp(_tmp_7);
            _item = new InstallationEntity(_tmpInstallationId,_tmpDropNumber,_tmpTechnicianId,_tmpStatus,_tmpStartTime,_tmpEndTime,_tmpOntSerial,_tmpSpeedTestResults,_tmpPhotos,_tmpCompletedSteps,_tmpCurrentStep,_tmpTotalSteps,_tmpValidationErrors,_tmpAiGuidanceUsed,_tmpManualOverrideUsed,_tmpCreatedAt,_tmpUpdatedAt);
            _result.add(_item);
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
  public Object getInstallationsWithValidationIssues(
      final Continuation<? super List<InstallationEntity>> $completion) {
    final String _sql = "SELECT * FROM installations WHERE validation_errors IS NOT NULL ORDER BY updated_at DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<InstallationEntity>>() {
      @Override
      @NonNull
      public List<InstallationEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfInstallationId = CursorUtil.getColumnIndexOrThrow(_cursor, "installation_id");
          final int _cursorIndexOfDropNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "drop_number");
          final int _cursorIndexOfTechnicianId = CursorUtil.getColumnIndexOrThrow(_cursor, "technician_id");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfStartTime = CursorUtil.getColumnIndexOrThrow(_cursor, "start_time");
          final int _cursorIndexOfEndTime = CursorUtil.getColumnIndexOrThrow(_cursor, "end_time");
          final int _cursorIndexOfOntSerial = CursorUtil.getColumnIndexOrThrow(_cursor, "ont_serial");
          final int _cursorIndexOfSpeedTestResults = CursorUtil.getColumnIndexOrThrow(_cursor, "speed_test_results");
          final int _cursorIndexOfPhotos = CursorUtil.getColumnIndexOrThrow(_cursor, "photos");
          final int _cursorIndexOfCompletedSteps = CursorUtil.getColumnIndexOrThrow(_cursor, "completed_steps");
          final int _cursorIndexOfCurrentStep = CursorUtil.getColumnIndexOrThrow(_cursor, "current_step");
          final int _cursorIndexOfTotalSteps = CursorUtil.getColumnIndexOrThrow(_cursor, "total_steps");
          final int _cursorIndexOfValidationErrors = CursorUtil.getColumnIndexOrThrow(_cursor, "validation_errors");
          final int _cursorIndexOfAiGuidanceUsed = CursorUtil.getColumnIndexOrThrow(_cursor, "ai_guidance_used");
          final int _cursorIndexOfManualOverrideUsed = CursorUtil.getColumnIndexOrThrow(_cursor, "manual_override_used");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final List<InstallationEntity> _result = new ArrayList<InstallationEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final InstallationEntity _item;
            final long _tmpInstallationId;
            _tmpInstallationId = _cursor.getLong(_cursorIndexOfInstallationId);
            final String _tmpDropNumber;
            if (_cursor.isNull(_cursorIndexOfDropNumber)) {
              _tmpDropNumber = null;
            } else {
              _tmpDropNumber = _cursor.getString(_cursorIndexOfDropNumber);
            }
            final String _tmpTechnicianId;
            if (_cursor.isNull(_cursorIndexOfTechnicianId)) {
              _tmpTechnicianId = null;
            } else {
              _tmpTechnicianId = _cursor.getString(_cursorIndexOfTechnicianId);
            }
            final InstallationStatus _tmpStatus;
            final String _tmp;
            if (_cursor.isNull(_cursorIndexOfStatus)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getString(_cursorIndexOfStatus);
            }
            _tmpStatus = __statusConverters.toInstallationStatus(_tmp);
            final Date _tmpStartTime;
            final Long _tmp_1;
            if (_cursor.isNull(_cursorIndexOfStartTime)) {
              _tmp_1 = null;
            } else {
              _tmp_1 = _cursor.getLong(_cursorIndexOfStartTime);
            }
            _tmpStartTime = __dateConverters.fromTimestamp(_tmp_1);
            final Date _tmpEndTime;
            final Long _tmp_2;
            if (_cursor.isNull(_cursorIndexOfEndTime)) {
              _tmp_2 = null;
            } else {
              _tmp_2 = _cursor.getLong(_cursorIndexOfEndTime);
            }
            _tmpEndTime = __dateConverters.fromTimestamp(_tmp_2);
            final String _tmpOntSerial;
            if (_cursor.isNull(_cursorIndexOfOntSerial)) {
              _tmpOntSerial = null;
            } else {
              _tmpOntSerial = _cursor.getString(_cursorIndexOfOntSerial);
            }
            final String _tmpSpeedTestResults;
            if (_cursor.isNull(_cursorIndexOfSpeedTestResults)) {
              _tmpSpeedTestResults = null;
            } else {
              _tmpSpeedTestResults = _cursor.getString(_cursorIndexOfSpeedTestResults);
            }
            final List<Long> _tmpPhotos;
            final String _tmp_3;
            if (_cursor.isNull(_cursorIndexOfPhotos)) {
              _tmp_3 = null;
            } else {
              _tmp_3 = _cursor.getString(_cursorIndexOfPhotos);
            }
            _tmpPhotos = __locationConverters.fromLongList(_tmp_3);
            final String _tmpCompletedSteps;
            if (_cursor.isNull(_cursorIndexOfCompletedSteps)) {
              _tmpCompletedSteps = null;
            } else {
              _tmpCompletedSteps = _cursor.getString(_cursorIndexOfCompletedSteps);
            }
            final int _tmpCurrentStep;
            _tmpCurrentStep = _cursor.getInt(_cursorIndexOfCurrentStep);
            final int _tmpTotalSteps;
            _tmpTotalSteps = _cursor.getInt(_cursorIndexOfTotalSteps);
            final String _tmpValidationErrors;
            if (_cursor.isNull(_cursorIndexOfValidationErrors)) {
              _tmpValidationErrors = null;
            } else {
              _tmpValidationErrors = _cursor.getString(_cursorIndexOfValidationErrors);
            }
            final boolean _tmpAiGuidanceUsed;
            final int _tmp_4;
            _tmp_4 = _cursor.getInt(_cursorIndexOfAiGuidanceUsed);
            _tmpAiGuidanceUsed = _tmp_4 != 0;
            final boolean _tmpManualOverrideUsed;
            final int _tmp_5;
            _tmp_5 = _cursor.getInt(_cursorIndexOfManualOverrideUsed);
            _tmpManualOverrideUsed = _tmp_5 != 0;
            final Date _tmpCreatedAt;
            final Long _tmp_6;
            if (_cursor.isNull(_cursorIndexOfCreatedAt)) {
              _tmp_6 = null;
            } else {
              _tmp_6 = _cursor.getLong(_cursorIndexOfCreatedAt);
            }
            _tmpCreatedAt = __dateConverters.fromTimestamp(_tmp_6);
            final Date _tmpUpdatedAt;
            final Long _tmp_7;
            if (_cursor.isNull(_cursorIndexOfUpdatedAt)) {
              _tmp_7 = null;
            } else {
              _tmp_7 = _cursor.getLong(_cursorIndexOfUpdatedAt);
            }
            _tmpUpdatedAt = __dateConverters.fromTimestamp(_tmp_7);
            _item = new InstallationEntity(_tmpInstallationId,_tmpDropNumber,_tmpTechnicianId,_tmpStatus,_tmpStartTime,_tmpEndTime,_tmpOntSerial,_tmpSpeedTestResults,_tmpPhotos,_tmpCompletedSteps,_tmpCurrentStep,_tmpTotalSteps,_tmpValidationErrors,_tmpAiGuidanceUsed,_tmpManualOverrideUsed,_tmpCreatedAt,_tmpUpdatedAt);
            _result.add(_item);
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
  public Object getAverageInstallationTime(final Continuation<? super Long> $completion) {
    final String _sql = "SELECT AVG(end_time - start_time) FROM installations WHERE status = 'COMPLETED' AND end_time IS NOT NULL";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Long>() {
      @Override
      @Nullable
      public Long call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Long _result;
          if (_cursor.moveToFirst()) {
            final Long _tmp;
            if (_cursor.isNull(0)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getLong(0);
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
  public Object getOrphanedInstallations(
      final Continuation<? super List<InstallationEntity>> $completion) {
    final String _sql = "SELECT i.* FROM installations i LEFT JOIN drops d ON i.drop_number = d.drop_number WHERE d.drop_number IS NULL";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<InstallationEntity>>() {
      @Override
      @NonNull
      public List<InstallationEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfInstallationId = CursorUtil.getColumnIndexOrThrow(_cursor, "installation_id");
          final int _cursorIndexOfDropNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "drop_number");
          final int _cursorIndexOfTechnicianId = CursorUtil.getColumnIndexOrThrow(_cursor, "technician_id");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfStartTime = CursorUtil.getColumnIndexOrThrow(_cursor, "start_time");
          final int _cursorIndexOfEndTime = CursorUtil.getColumnIndexOrThrow(_cursor, "end_time");
          final int _cursorIndexOfOntSerial = CursorUtil.getColumnIndexOrThrow(_cursor, "ont_serial");
          final int _cursorIndexOfSpeedTestResults = CursorUtil.getColumnIndexOrThrow(_cursor, "speed_test_results");
          final int _cursorIndexOfPhotos = CursorUtil.getColumnIndexOrThrow(_cursor, "photos");
          final int _cursorIndexOfCompletedSteps = CursorUtil.getColumnIndexOrThrow(_cursor, "completed_steps");
          final int _cursorIndexOfCurrentStep = CursorUtil.getColumnIndexOrThrow(_cursor, "current_step");
          final int _cursorIndexOfTotalSteps = CursorUtil.getColumnIndexOrThrow(_cursor, "total_steps");
          final int _cursorIndexOfValidationErrors = CursorUtil.getColumnIndexOrThrow(_cursor, "validation_errors");
          final int _cursorIndexOfAiGuidanceUsed = CursorUtil.getColumnIndexOrThrow(_cursor, "ai_guidance_used");
          final int _cursorIndexOfManualOverrideUsed = CursorUtil.getColumnIndexOrThrow(_cursor, "manual_override_used");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final List<InstallationEntity> _result = new ArrayList<InstallationEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final InstallationEntity _item;
            final long _tmpInstallationId;
            _tmpInstallationId = _cursor.getLong(_cursorIndexOfInstallationId);
            final String _tmpDropNumber;
            if (_cursor.isNull(_cursorIndexOfDropNumber)) {
              _tmpDropNumber = null;
            } else {
              _tmpDropNumber = _cursor.getString(_cursorIndexOfDropNumber);
            }
            final String _tmpTechnicianId;
            if (_cursor.isNull(_cursorIndexOfTechnicianId)) {
              _tmpTechnicianId = null;
            } else {
              _tmpTechnicianId = _cursor.getString(_cursorIndexOfTechnicianId);
            }
            final InstallationStatus _tmpStatus;
            final String _tmp;
            if (_cursor.isNull(_cursorIndexOfStatus)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getString(_cursorIndexOfStatus);
            }
            _tmpStatus = __statusConverters.toInstallationStatus(_tmp);
            final Date _tmpStartTime;
            final Long _tmp_1;
            if (_cursor.isNull(_cursorIndexOfStartTime)) {
              _tmp_1 = null;
            } else {
              _tmp_1 = _cursor.getLong(_cursorIndexOfStartTime);
            }
            _tmpStartTime = __dateConverters.fromTimestamp(_tmp_1);
            final Date _tmpEndTime;
            final Long _tmp_2;
            if (_cursor.isNull(_cursorIndexOfEndTime)) {
              _tmp_2 = null;
            } else {
              _tmp_2 = _cursor.getLong(_cursorIndexOfEndTime);
            }
            _tmpEndTime = __dateConverters.fromTimestamp(_tmp_2);
            final String _tmpOntSerial;
            if (_cursor.isNull(_cursorIndexOfOntSerial)) {
              _tmpOntSerial = null;
            } else {
              _tmpOntSerial = _cursor.getString(_cursorIndexOfOntSerial);
            }
            final String _tmpSpeedTestResults;
            if (_cursor.isNull(_cursorIndexOfSpeedTestResults)) {
              _tmpSpeedTestResults = null;
            } else {
              _tmpSpeedTestResults = _cursor.getString(_cursorIndexOfSpeedTestResults);
            }
            final List<Long> _tmpPhotos;
            final String _tmp_3;
            if (_cursor.isNull(_cursorIndexOfPhotos)) {
              _tmp_3 = null;
            } else {
              _tmp_3 = _cursor.getString(_cursorIndexOfPhotos);
            }
            _tmpPhotos = __locationConverters.fromLongList(_tmp_3);
            final String _tmpCompletedSteps;
            if (_cursor.isNull(_cursorIndexOfCompletedSteps)) {
              _tmpCompletedSteps = null;
            } else {
              _tmpCompletedSteps = _cursor.getString(_cursorIndexOfCompletedSteps);
            }
            final int _tmpCurrentStep;
            _tmpCurrentStep = _cursor.getInt(_cursorIndexOfCurrentStep);
            final int _tmpTotalSteps;
            _tmpTotalSteps = _cursor.getInt(_cursorIndexOfTotalSteps);
            final String _tmpValidationErrors;
            if (_cursor.isNull(_cursorIndexOfValidationErrors)) {
              _tmpValidationErrors = null;
            } else {
              _tmpValidationErrors = _cursor.getString(_cursorIndexOfValidationErrors);
            }
            final boolean _tmpAiGuidanceUsed;
            final int _tmp_4;
            _tmp_4 = _cursor.getInt(_cursorIndexOfAiGuidanceUsed);
            _tmpAiGuidanceUsed = _tmp_4 != 0;
            final boolean _tmpManualOverrideUsed;
            final int _tmp_5;
            _tmp_5 = _cursor.getInt(_cursorIndexOfManualOverrideUsed);
            _tmpManualOverrideUsed = _tmp_5 != 0;
            final Date _tmpCreatedAt;
            final Long _tmp_6;
            if (_cursor.isNull(_cursorIndexOfCreatedAt)) {
              _tmp_6 = null;
            } else {
              _tmp_6 = _cursor.getLong(_cursorIndexOfCreatedAt);
            }
            _tmpCreatedAt = __dateConverters.fromTimestamp(_tmp_6);
            final Date _tmpUpdatedAt;
            final Long _tmp_7;
            if (_cursor.isNull(_cursorIndexOfUpdatedAt)) {
              _tmp_7 = null;
            } else {
              _tmp_7 = _cursor.getLong(_cursorIndexOfUpdatedAt);
            }
            _tmpUpdatedAt = __dateConverters.fromTimestamp(_tmp_7);
            _item = new InstallationEntity(_tmpInstallationId,_tmpDropNumber,_tmpTechnicianId,_tmpStatus,_tmpStartTime,_tmpEndTime,_tmpOntSerial,_tmpSpeedTestResults,_tmpPhotos,_tmpCompletedSteps,_tmpCurrentStep,_tmpTotalSteps,_tmpValidationErrors,_tmpAiGuidanceUsed,_tmpManualOverrideUsed,_tmpCreatedAt,_tmpUpdatedAt);
            _result.add(_item);
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
  public Object getRecordsWithFutureTimestamps(final long currentTime,
      final Continuation<? super List<InstallationEntity>> $completion) {
    final String _sql = "SELECT * FROM installations WHERE start_time > ? OR end_time > ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, currentTime);
    _argIndex = 2;
    _statement.bindLong(_argIndex, currentTime);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<InstallationEntity>>() {
      @Override
      @NonNull
      public List<InstallationEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfInstallationId = CursorUtil.getColumnIndexOrThrow(_cursor, "installation_id");
          final int _cursorIndexOfDropNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "drop_number");
          final int _cursorIndexOfTechnicianId = CursorUtil.getColumnIndexOrThrow(_cursor, "technician_id");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfStartTime = CursorUtil.getColumnIndexOrThrow(_cursor, "start_time");
          final int _cursorIndexOfEndTime = CursorUtil.getColumnIndexOrThrow(_cursor, "end_time");
          final int _cursorIndexOfOntSerial = CursorUtil.getColumnIndexOrThrow(_cursor, "ont_serial");
          final int _cursorIndexOfSpeedTestResults = CursorUtil.getColumnIndexOrThrow(_cursor, "speed_test_results");
          final int _cursorIndexOfPhotos = CursorUtil.getColumnIndexOrThrow(_cursor, "photos");
          final int _cursorIndexOfCompletedSteps = CursorUtil.getColumnIndexOrThrow(_cursor, "completed_steps");
          final int _cursorIndexOfCurrentStep = CursorUtil.getColumnIndexOrThrow(_cursor, "current_step");
          final int _cursorIndexOfTotalSteps = CursorUtil.getColumnIndexOrThrow(_cursor, "total_steps");
          final int _cursorIndexOfValidationErrors = CursorUtil.getColumnIndexOrThrow(_cursor, "validation_errors");
          final int _cursorIndexOfAiGuidanceUsed = CursorUtil.getColumnIndexOrThrow(_cursor, "ai_guidance_used");
          final int _cursorIndexOfManualOverrideUsed = CursorUtil.getColumnIndexOrThrow(_cursor, "manual_override_used");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final List<InstallationEntity> _result = new ArrayList<InstallationEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final InstallationEntity _item;
            final long _tmpInstallationId;
            _tmpInstallationId = _cursor.getLong(_cursorIndexOfInstallationId);
            final String _tmpDropNumber;
            if (_cursor.isNull(_cursorIndexOfDropNumber)) {
              _tmpDropNumber = null;
            } else {
              _tmpDropNumber = _cursor.getString(_cursorIndexOfDropNumber);
            }
            final String _tmpTechnicianId;
            if (_cursor.isNull(_cursorIndexOfTechnicianId)) {
              _tmpTechnicianId = null;
            } else {
              _tmpTechnicianId = _cursor.getString(_cursorIndexOfTechnicianId);
            }
            final InstallationStatus _tmpStatus;
            final String _tmp;
            if (_cursor.isNull(_cursorIndexOfStatus)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getString(_cursorIndexOfStatus);
            }
            _tmpStatus = __statusConverters.toInstallationStatus(_tmp);
            final Date _tmpStartTime;
            final Long _tmp_1;
            if (_cursor.isNull(_cursorIndexOfStartTime)) {
              _tmp_1 = null;
            } else {
              _tmp_1 = _cursor.getLong(_cursorIndexOfStartTime);
            }
            _tmpStartTime = __dateConverters.fromTimestamp(_tmp_1);
            final Date _tmpEndTime;
            final Long _tmp_2;
            if (_cursor.isNull(_cursorIndexOfEndTime)) {
              _tmp_2 = null;
            } else {
              _tmp_2 = _cursor.getLong(_cursorIndexOfEndTime);
            }
            _tmpEndTime = __dateConverters.fromTimestamp(_tmp_2);
            final String _tmpOntSerial;
            if (_cursor.isNull(_cursorIndexOfOntSerial)) {
              _tmpOntSerial = null;
            } else {
              _tmpOntSerial = _cursor.getString(_cursorIndexOfOntSerial);
            }
            final String _tmpSpeedTestResults;
            if (_cursor.isNull(_cursorIndexOfSpeedTestResults)) {
              _tmpSpeedTestResults = null;
            } else {
              _tmpSpeedTestResults = _cursor.getString(_cursorIndexOfSpeedTestResults);
            }
            final List<Long> _tmpPhotos;
            final String _tmp_3;
            if (_cursor.isNull(_cursorIndexOfPhotos)) {
              _tmp_3 = null;
            } else {
              _tmp_3 = _cursor.getString(_cursorIndexOfPhotos);
            }
            _tmpPhotos = __locationConverters.fromLongList(_tmp_3);
            final String _tmpCompletedSteps;
            if (_cursor.isNull(_cursorIndexOfCompletedSteps)) {
              _tmpCompletedSteps = null;
            } else {
              _tmpCompletedSteps = _cursor.getString(_cursorIndexOfCompletedSteps);
            }
            final int _tmpCurrentStep;
            _tmpCurrentStep = _cursor.getInt(_cursorIndexOfCurrentStep);
            final int _tmpTotalSteps;
            _tmpTotalSteps = _cursor.getInt(_cursorIndexOfTotalSteps);
            final String _tmpValidationErrors;
            if (_cursor.isNull(_cursorIndexOfValidationErrors)) {
              _tmpValidationErrors = null;
            } else {
              _tmpValidationErrors = _cursor.getString(_cursorIndexOfValidationErrors);
            }
            final boolean _tmpAiGuidanceUsed;
            final int _tmp_4;
            _tmp_4 = _cursor.getInt(_cursorIndexOfAiGuidanceUsed);
            _tmpAiGuidanceUsed = _tmp_4 != 0;
            final boolean _tmpManualOverrideUsed;
            final int _tmp_5;
            _tmp_5 = _cursor.getInt(_cursorIndexOfManualOverrideUsed);
            _tmpManualOverrideUsed = _tmp_5 != 0;
            final Date _tmpCreatedAt;
            final Long _tmp_6;
            if (_cursor.isNull(_cursorIndexOfCreatedAt)) {
              _tmp_6 = null;
            } else {
              _tmp_6 = _cursor.getLong(_cursorIndexOfCreatedAt);
            }
            _tmpCreatedAt = __dateConverters.fromTimestamp(_tmp_6);
            final Date _tmpUpdatedAt;
            final Long _tmp_7;
            if (_cursor.isNull(_cursorIndexOfUpdatedAt)) {
              _tmp_7 = null;
            } else {
              _tmp_7 = _cursor.getLong(_cursorIndexOfUpdatedAt);
            }
            _tmpUpdatedAt = __dateConverters.fromTimestamp(_tmp_7);
            _item = new InstallationEntity(_tmpInstallationId,_tmpDropNumber,_tmpTechnicianId,_tmpStatus,_tmpStartTime,_tmpEndTime,_tmpOntSerial,_tmpSpeedTestResults,_tmpPhotos,_tmpCompletedSteps,_tmpCurrentStep,_tmpTotalSteps,_tmpValidationErrors,_tmpAiGuidanceUsed,_tmpManualOverrideUsed,_tmpCreatedAt,_tmpUpdatedAt);
            _result.add(_item);
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
  public Object installationExists(final long installationId,
      final Continuation<? super Boolean> $completion) {
    final String _sql = "SELECT COUNT(*) FROM installations WHERE installation_id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, installationId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Boolean>() {
      @Override
      @NonNull
      public Boolean call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Boolean _result;
          if (_cursor.moveToFirst()) {
            final Integer _tmp;
            if (_cursor.isNull(0)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getInt(0);
            }
            _result = _tmp == null ? null : _tmp != 0;
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
  public Object markInstallationsSynced(final List<Long> installationIds, final long timestamp,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final StringBuilder _stringBuilder = StringUtil.newStringBuilder();
        _stringBuilder.append("UPDATE installations SET ai_guidance_used = 0, updated_at = ");
        _stringBuilder.append("?");
        _stringBuilder.append(" WHERE installation_id IN (");
        final int _inputSize = installationIds.size();
        StringUtil.appendPlaceholders(_stringBuilder, _inputSize);
        _stringBuilder.append(")");
        final String _sql = _stringBuilder.toString();
        final SupportSQLiteStatement _stmt = __db.compileStatement(_sql);
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, timestamp);
        _argIndex = 2;
        for (Long _item : installationIds) {
          if (_item == null) {
            _stmt.bindNull(_argIndex);
          } else {
            _stmt.bindLong(_argIndex, _item);
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
