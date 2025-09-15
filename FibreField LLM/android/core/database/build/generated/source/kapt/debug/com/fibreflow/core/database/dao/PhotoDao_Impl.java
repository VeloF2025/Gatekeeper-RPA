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
import com.fibreflow.core.database.converters.StatusConverters;
import com.fibreflow.core.database.entities.PhotoEntity;
import java.lang.Class;
import java.lang.Double;
import java.lang.Exception;
import java.lang.Float;
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
import kotlin.Pair;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class PhotoDao_Impl implements PhotoDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<PhotoEntity> __insertionAdapterOfPhotoEntity;

  private final StatusConverters __statusConverters = new StatusConverters();

  private final DateConverters __dateConverters = new DateConverters();

  private final EntityDeletionOrUpdateAdapter<PhotoEntity> __deletionAdapterOfPhotoEntity;

  private final EntityDeletionOrUpdateAdapter<PhotoEntity> __updateAdapterOfPhotoEntity;

  private final SharedSQLiteStatement __preparedStmtOfUpdatePhotoValidationStatus;

  private final SharedSQLiteStatement __preparedStmtOfUpdatePhotoValidationResults;

  private final SharedSQLiteStatement __preparedStmtOfMarkPhotoForSync;

  private final SharedSQLiteStatement __preparedStmtOfMarkPhotoSynced;

  private final SharedSQLiteStatement __preparedStmtOfUpdatePhotoFilePath;

  private final SharedSQLiteStatement __preparedStmtOfUpdatePhotoLocation;

  private final SharedSQLiteStatement __preparedStmtOfUpdatePhotoQualityMetrics;

  private final SharedSQLiteStatement __preparedStmtOfDeleteOldPhotos;

  public PhotoDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfPhotoEntity = new EntityInsertionAdapter<PhotoEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `photos` (`photo_id`,`installation_id`,`photo_type`,`file_path`,`file_size_bytes`,`width`,`height`,`validation_status`,`validation_confidence`,`ai_metadata`,`manual_override`,`override_reason`,`override_by`,`override_at`,`upload_status`,`upload_attempts`,`last_upload_attempt`,`created_at`,`updated_at`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final PhotoEntity entity) {
        statement.bindLong(1, entity.getPhotoId());
        statement.bindLong(2, entity.getInstallationId());
        final String _tmp = __statusConverters.fromPhotoType(entity.getPhotoType());
        if (_tmp == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, _tmp);
        }
        if (entity.getFilePath() == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.getFilePath());
        }
        statement.bindLong(5, entity.getFileSizeBytes());
        statement.bindLong(6, entity.getWidth());
        statement.bindLong(7, entity.getHeight());
        final String _tmp_1 = __statusConverters.fromValidationStatus(entity.getValidationStatus());
        if (_tmp_1 == null) {
          statement.bindNull(8);
        } else {
          statement.bindString(8, _tmp_1);
        }
        if (entity.getValidationConfidence() == null) {
          statement.bindNull(9);
        } else {
          statement.bindDouble(9, entity.getValidationConfidence());
        }
        if (entity.getAiMetadata() == null) {
          statement.bindNull(10);
        } else {
          statement.bindString(10, entity.getAiMetadata());
        }
        final int _tmp_2 = entity.getManualOverride() ? 1 : 0;
        statement.bindLong(11, _tmp_2);
        if (entity.getOverrideReason() == null) {
          statement.bindNull(12);
        } else {
          statement.bindString(12, entity.getOverrideReason());
        }
        if (entity.getOverrideBy() == null) {
          statement.bindNull(13);
        } else {
          statement.bindString(13, entity.getOverrideBy());
        }
        final Long _tmp_3 = __dateConverters.dateToTimestamp(entity.getOverrideAt());
        if (_tmp_3 == null) {
          statement.bindNull(14);
        } else {
          statement.bindLong(14, _tmp_3);
        }
        final String _tmp_4 = __statusConverters.fromUploadStatus(entity.getUploadStatus());
        if (_tmp_4 == null) {
          statement.bindNull(15);
        } else {
          statement.bindString(15, _tmp_4);
        }
        statement.bindLong(16, entity.getUploadAttempts());
        final Long _tmp_5 = __dateConverters.dateToTimestamp(entity.getLastUploadAttempt());
        if (_tmp_5 == null) {
          statement.bindNull(17);
        } else {
          statement.bindLong(17, _tmp_5);
        }
        final Long _tmp_6 = __dateConverters.dateToTimestamp(entity.getCreatedAt());
        if (_tmp_6 == null) {
          statement.bindNull(18);
        } else {
          statement.bindLong(18, _tmp_6);
        }
        final Long _tmp_7 = __dateConverters.dateToTimestamp(entity.getUpdatedAt());
        if (_tmp_7 == null) {
          statement.bindNull(19);
        } else {
          statement.bindLong(19, _tmp_7);
        }
      }
    };
    this.__deletionAdapterOfPhotoEntity = new EntityDeletionOrUpdateAdapter<PhotoEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `photos` WHERE `photo_id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final PhotoEntity entity) {
        statement.bindLong(1, entity.getPhotoId());
      }
    };
    this.__updateAdapterOfPhotoEntity = new EntityDeletionOrUpdateAdapter<PhotoEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `photos` SET `photo_id` = ?,`installation_id` = ?,`photo_type` = ?,`file_path` = ?,`file_size_bytes` = ?,`width` = ?,`height` = ?,`validation_status` = ?,`validation_confidence` = ?,`ai_metadata` = ?,`manual_override` = ?,`override_reason` = ?,`override_by` = ?,`override_at` = ?,`upload_status` = ?,`upload_attempts` = ?,`last_upload_attempt` = ?,`created_at` = ?,`updated_at` = ? WHERE `photo_id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final PhotoEntity entity) {
        statement.bindLong(1, entity.getPhotoId());
        statement.bindLong(2, entity.getInstallationId());
        final String _tmp = __statusConverters.fromPhotoType(entity.getPhotoType());
        if (_tmp == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, _tmp);
        }
        if (entity.getFilePath() == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.getFilePath());
        }
        statement.bindLong(5, entity.getFileSizeBytes());
        statement.bindLong(6, entity.getWidth());
        statement.bindLong(7, entity.getHeight());
        final String _tmp_1 = __statusConverters.fromValidationStatus(entity.getValidationStatus());
        if (_tmp_1 == null) {
          statement.bindNull(8);
        } else {
          statement.bindString(8, _tmp_1);
        }
        if (entity.getValidationConfidence() == null) {
          statement.bindNull(9);
        } else {
          statement.bindDouble(9, entity.getValidationConfidence());
        }
        if (entity.getAiMetadata() == null) {
          statement.bindNull(10);
        } else {
          statement.bindString(10, entity.getAiMetadata());
        }
        final int _tmp_2 = entity.getManualOverride() ? 1 : 0;
        statement.bindLong(11, _tmp_2);
        if (entity.getOverrideReason() == null) {
          statement.bindNull(12);
        } else {
          statement.bindString(12, entity.getOverrideReason());
        }
        if (entity.getOverrideBy() == null) {
          statement.bindNull(13);
        } else {
          statement.bindString(13, entity.getOverrideBy());
        }
        final Long _tmp_3 = __dateConverters.dateToTimestamp(entity.getOverrideAt());
        if (_tmp_3 == null) {
          statement.bindNull(14);
        } else {
          statement.bindLong(14, _tmp_3);
        }
        final String _tmp_4 = __statusConverters.fromUploadStatus(entity.getUploadStatus());
        if (_tmp_4 == null) {
          statement.bindNull(15);
        } else {
          statement.bindString(15, _tmp_4);
        }
        statement.bindLong(16, entity.getUploadAttempts());
        final Long _tmp_5 = __dateConverters.dateToTimestamp(entity.getLastUploadAttempt());
        if (_tmp_5 == null) {
          statement.bindNull(17);
        } else {
          statement.bindLong(17, _tmp_5);
        }
        final Long _tmp_6 = __dateConverters.dateToTimestamp(entity.getCreatedAt());
        if (_tmp_6 == null) {
          statement.bindNull(18);
        } else {
          statement.bindLong(18, _tmp_6);
        }
        final Long _tmp_7 = __dateConverters.dateToTimestamp(entity.getUpdatedAt());
        if (_tmp_7 == null) {
          statement.bindNull(19);
        } else {
          statement.bindLong(19, _tmp_7);
        }
        statement.bindLong(20, entity.getPhotoId());
      }
    };
    this.__preparedStmtOfUpdatePhotoValidationStatus = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE photos SET validationStatus = ?, validatedAt = ?, validationNotes = ? WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfUpdatePhotoValidationResults = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE photos SET validationStatus = ?, validatedAt = ?, validationConfidence = ?, detectedObjects = ?, extractedText = ?, validationNotes = ? WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfMarkPhotoForSync = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE photos SET needsSync = 1 WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfMarkPhotoSynced = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE photos SET needsSync = 0, lastSyncedAt = ? WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfUpdatePhotoFilePath = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE photos SET filePath = ?, fileSizeBytes = ? WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfUpdatePhotoLocation = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE photos SET latitude = ?, longitude = ?, altitude = ?, bearing = ? WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfUpdatePhotoQualityMetrics = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE photos SET brightness = ?, sharpness = ?, contrast = ?, qualityScore = ? WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteOldPhotos = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM photos WHERE capturedAt < ?";
        return _query;
      }
    };
  }

  @Override
  public Object insertPhoto(final PhotoEntity photo, final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfPhotoEntity.insertAndReturnId(photo);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertPhotos(final List<PhotoEntity> photos,
      final Continuation<? super List<Long>> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<List<Long>>() {
      @Override
      @NonNull
      public List<Long> call() throws Exception {
        __db.beginTransaction();
        try {
          final List<Long> _result = __insertionAdapterOfPhotoEntity.insertAndReturnIdsList(photos);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deletePhoto(final PhotoEntity photo, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfPhotoEntity.handle(photo);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deletePhotos(final List<PhotoEntity> photos,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfPhotoEntity.handleMultiple(photos);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updatePhoto(final PhotoEntity photo, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfPhotoEntity.handle(photo);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updatePhotos(final List<PhotoEntity> photos,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfPhotoEntity.handleMultiple(photos);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updatePhotoValidationStatus(final String photoId, final String status,
      final String notes, final long timestamp, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfUpdatePhotoValidationStatus.acquire();
        int _argIndex = 1;
        if (status == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindString(_argIndex, status);
        }
        _argIndex = 2;
        _stmt.bindLong(_argIndex, timestamp);
        _argIndex = 3;
        if (notes == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindString(_argIndex, notes);
        }
        _argIndex = 4;
        if (photoId == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindString(_argIndex, photoId);
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
          __preparedStmtOfUpdatePhotoValidationStatus.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object updatePhotoValidationResults(final String photoId, final String status,
      final float confidence, final String objects, final String text, final String notes,
      final long timestamp, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfUpdatePhotoValidationResults.acquire();
        int _argIndex = 1;
        if (status == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindString(_argIndex, status);
        }
        _argIndex = 2;
        _stmt.bindLong(_argIndex, timestamp);
        _argIndex = 3;
        _stmt.bindDouble(_argIndex, confidence);
        _argIndex = 4;
        if (objects == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindString(_argIndex, objects);
        }
        _argIndex = 5;
        if (text == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindString(_argIndex, text);
        }
        _argIndex = 6;
        if (notes == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindString(_argIndex, notes);
        }
        _argIndex = 7;
        if (photoId == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindString(_argIndex, photoId);
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
          __preparedStmtOfUpdatePhotoValidationResults.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object markPhotoForSync(final String photoId,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfMarkPhotoForSync.acquire();
        int _argIndex = 1;
        if (photoId == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindString(_argIndex, photoId);
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
          __preparedStmtOfMarkPhotoForSync.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object markPhotoSynced(final String photoId, final long timestamp,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfMarkPhotoSynced.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, timestamp);
        _argIndex = 2;
        if (photoId == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindString(_argIndex, photoId);
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
          __preparedStmtOfMarkPhotoSynced.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object updatePhotoFilePath(final String photoId, final String filePath,
      final long fileSize, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfUpdatePhotoFilePath.acquire();
        int _argIndex = 1;
        if (filePath == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindString(_argIndex, filePath);
        }
        _argIndex = 2;
        _stmt.bindLong(_argIndex, fileSize);
        _argIndex = 3;
        if (photoId == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindString(_argIndex, photoId);
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
          __preparedStmtOfUpdatePhotoFilePath.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object updatePhotoLocation(final String photoId, final Double latitude,
      final Double longitude, final Double altitude, final Float bearing,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfUpdatePhotoLocation.acquire();
        int _argIndex = 1;
        if (latitude == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindDouble(_argIndex, latitude);
        }
        _argIndex = 2;
        if (longitude == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindDouble(_argIndex, longitude);
        }
        _argIndex = 3;
        if (altitude == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindDouble(_argIndex, altitude);
        }
        _argIndex = 4;
        if (bearing == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindDouble(_argIndex, bearing);
        }
        _argIndex = 5;
        if (photoId == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindString(_argIndex, photoId);
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
          __preparedStmtOfUpdatePhotoLocation.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object updatePhotoQualityMetrics(final String photoId, final Float brightness,
      final Float sharpness, final Float contrast, final Float qualityScore,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfUpdatePhotoQualityMetrics.acquire();
        int _argIndex = 1;
        if (brightness == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindDouble(_argIndex, brightness);
        }
        _argIndex = 2;
        if (sharpness == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindDouble(_argIndex, sharpness);
        }
        _argIndex = 3;
        if (contrast == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindDouble(_argIndex, contrast);
        }
        _argIndex = 4;
        if (qualityScore == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindDouble(_argIndex, qualityScore);
        }
        _argIndex = 5;
        if (photoId == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindString(_argIndex, photoId);
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
          __preparedStmtOfUpdatePhotoQualityMetrics.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteOldPhotos(final long cutoffDate,
      final Continuation<? super Integer> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Integer>() {
      @Override
      @NonNull
      public Integer call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteOldPhotos.acquire();
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
          __preparedStmtOfDeleteOldPhotos.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object getPhotoById(final String photoId,
      final Continuation<? super PhotoEntity> $completion) {
    final String _sql = "SELECT * FROM photos WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    if (photoId == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, photoId);
    }
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<PhotoEntity>() {
      @Override
      @Nullable
      public PhotoEntity call() throws Exception {
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
  public Flow<PhotoEntity> getPhotoByIdFlow(final String photoId) {
    final String _sql = "SELECT * FROM photos WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    if (photoId == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, photoId);
    }
    return CoroutinesRoom.createFlow(__db, false, new String[] {"photos"}, new Callable<PhotoEntity>() {
      @Override
      @Nullable
      public PhotoEntity call() throws Exception {
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
  public Object getPhotosByInstallation(final String installationId,
      final Continuation<? super List<PhotoEntity>> $completion) {
    final String _sql = "SELECT * FROM photos WHERE installationId = ? ORDER BY sequenceNumber ASC, capturedAt ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    if (installationId == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, installationId);
    }
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<PhotoEntity>>() {
      @Override
      @NonNull
      public List<PhotoEntity> call() throws Exception {
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
  public Flow<List<PhotoEntity>> getPhotosByInstallationFlow(final String installationId) {
    final String _sql = "SELECT * FROM photos WHERE installationId = ? ORDER BY sequenceNumber ASC, capturedAt ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    if (installationId == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, installationId);
    }
    return CoroutinesRoom.createFlow(__db, false, new String[] {"photos"}, new Callable<List<PhotoEntity>>() {
      @Override
      @NonNull
      public List<PhotoEntity> call() throws Exception {
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
  public Object getPhotosByStep(final String installationId, final String stepName,
      final Continuation<? super List<PhotoEntity>> $completion) {
    final String _sql = "SELECT * FROM photos WHERE installationId = ? AND stepName = ? ORDER BY sequenceNumber ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    if (installationId == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, installationId);
    }
    _argIndex = 2;
    if (stepName == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, stepName);
    }
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<PhotoEntity>>() {
      @Override
      @NonNull
      public List<PhotoEntity> call() throws Exception {
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
  public Object getPhotosRequiringValidation(
      final Continuation<? super List<PhotoEntity>> $completion) {
    final String _sql = "SELECT * FROM photos WHERE validationStatus = 'PENDING' ORDER BY capturedAt ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<PhotoEntity>>() {
      @Override
      @NonNull
      public List<PhotoEntity> call() throws Exception {
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
  public Flow<List<PhotoEntity>> getPhotosRequiringValidationFlow() {
    final String _sql = "SELECT * FROM photos WHERE validationStatus = 'PENDING' ORDER BY capturedAt ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"photos"}, new Callable<List<PhotoEntity>>() {
      @Override
      @NonNull
      public List<PhotoEntity> call() throws Exception {
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
  public Object getValidatedPhotos(final Continuation<? super List<PhotoEntity>> $completion) {
    final String _sql = "SELECT * FROM photos WHERE validationStatus = 'PASSED' ORDER BY validatedAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<PhotoEntity>>() {
      @Override
      @NonNull
      public List<PhotoEntity> call() throws Exception {
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
  public Object getFailedValidationPhotos(
      final Continuation<? super List<PhotoEntity>> $completion) {
    final String _sql = "SELECT * FROM photos WHERE validationStatus = 'FAILED' ORDER BY validatedAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<PhotoEntity>>() {
      @Override
      @NonNull
      public List<PhotoEntity> call() throws Exception {
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
  public Object getPhotosNeedingSync(final Continuation<? super List<PhotoEntity>> $completion) {
    final String _sql = "SELECT * FROM photos WHERE needsSync = 1 ORDER BY capturedAt ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<PhotoEntity>>() {
      @Override
      @NonNull
      public List<PhotoEntity> call() throws Exception {
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
  public Object getPhotosByQualityRange(final float minScore, final float maxScore,
      final Continuation<? super List<PhotoEntity>> $completion) {
    final String _sql = "SELECT * FROM photos WHERE qualityScore BETWEEN ? AND ? ORDER BY qualityScore DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindDouble(_argIndex, minScore);
    _argIndex = 2;
    _statement.bindDouble(_argIndex, maxScore);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<PhotoEntity>>() {
      @Override
      @NonNull
      public List<PhotoEntity> call() throws Exception {
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
  public Object getLowQualityPhotos(final Continuation<? super List<PhotoEntity>> $completion) {
    final String _sql = "SELECT * FROM photos WHERE qualityScore < 0.6 ORDER BY qualityScore ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<PhotoEntity>>() {
      @Override
      @NonNull
      public List<PhotoEntity> call() throws Exception {
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
  public Object getPhotoCountByValidationStatus(final String status,
      final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT COUNT(*) FROM photos WHERE validationStatus = ?";
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

  @Override
  public Object getTotalPhotoCount(final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT COUNT(*) FROM photos";
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
  public Object getPhotosInDateRange(final long startDate, final long endDate,
      final Continuation<? super List<PhotoEntity>> $completion) {
    final String _sql = "SELECT * FROM photos WHERE capturedAt BETWEEN ? AND ? ORDER BY capturedAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, startDate);
    _argIndex = 2;
    _statement.bindLong(_argIndex, endDate);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<PhotoEntity>>() {
      @Override
      @NonNull
      public List<PhotoEntity> call() throws Exception {
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
  public Object getPhotosByTechnician(final String technicianId,
      final Continuation<? super List<PhotoEntity>> $completion) {
    final String _sql = "SELECT p.* FROM photos p INNER JOIN installations i ON p.installationId = i.id WHERE i.technicianId = ? ORDER BY p.capturedAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    if (technicianId == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, technicianId);
    }
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<PhotoEntity>>() {
      @Override
      @NonNull
      public List<PhotoEntity> call() throws Exception {
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
  public Object getAverageQualityByStep(
      final Continuation<? super Map<String, Pair<Float, Integer>>> $completion) {
    final String _sql = "\n"
            + "        SELECT stepName, AVG(qualityScore) as avgQuality, COUNT(*) as photoCount\n"
            + "        FROM photos\n"
            + "        WHERE qualityScore IS NOT NULL\n"
            + "        GROUP BY stepName\n"
            + "        ORDER BY avgQuality DESC\n"
            + "    ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Map<String, Pair<Float, Integer>>>() {
      @Override
      @NonNull
      public Map<String, Pair<Float, Integer>> call() throws Exception {
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
  public Object getValidationStatistics(
      final Continuation<? super Map<String, Pair<Integer, Float>>> $completion) {
    final String _sql = "\n"
            + "        SELECT validationStatus, COUNT(*) as count,\n"
            + "               AVG(validationConfidence) as avgConfidence\n"
            + "        FROM photos\n"
            + "        WHERE validationStatus IS NOT NULL\n"
            + "        GROUP BY validationStatus\n"
            + "    ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Map<String, Pair<Integer, Float>>>() {
      @Override
      @NonNull
      public Map<String, Pair<Integer, Float>> call() throws Exception {
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
  public Object searchPhotosByDetectedObject(final String objectName,
      final Continuation<? super List<PhotoEntity>> $completion) {
    final String _sql = "SELECT * FROM photos WHERE detectedObjects LIKE '%' || ? || '%' ORDER BY capturedAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    if (objectName == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, objectName);
    }
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<PhotoEntity>>() {
      @Override
      @NonNull
      public List<PhotoEntity> call() throws Exception {
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
  public Object searchPhotosByExtractedText(final String searchText,
      final Continuation<? super List<PhotoEntity>> $completion) {
    final String _sql = "SELECT * FROM photos WHERE extractedText LIKE '%' || ? || '%' ORDER BY capturedAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    if (searchText == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, searchText);
    }
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<PhotoEntity>>() {
      @Override
      @NonNull
      public List<PhotoEntity> call() throws Exception {
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
  public Object markPhotosSynced(final List<String> photoIds, final long timestamp,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final StringBuilder _stringBuilder = StringUtil.newStringBuilder();
        _stringBuilder.append("UPDATE photos SET needsSync = 0, lastSyncedAt = ");
        _stringBuilder.append("?");
        _stringBuilder.append(" WHERE id IN (");
        final int _inputSize = photoIds.size();
        StringUtil.appendPlaceholders(_stringBuilder, _inputSize);
        _stringBuilder.append(")");
        final String _sql = _stringBuilder.toString();
        final SupportSQLiteStatement _stmt = __db.compileStatement(_sql);
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, timestamp);
        _argIndex = 2;
        for (String _item : photoIds) {
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
