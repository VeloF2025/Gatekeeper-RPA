package com.fibreflow.core.database.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomDatabaseKt;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.room.util.StringUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.fibreflow.core.database.converters.DateConverters;
import com.fibreflow.core.database.converters.StatusConverters;
import com.fibreflow.core.database.entities.PhotoEntity;
import com.fibreflow.core.database.entities.PhotoType;
import com.fibreflow.core.database.entities.UploadStatus;
import com.fibreflow.core.database.entities.ValidationStatus;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Float;
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

  private final SharedSQLiteStatement __preparedStmtOfUpdatePhotoChecksum;

  private final SharedSQLiteStatement __preparedStmtOfDeletePhotoByInstallationId;

  public PhotoDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfPhotoEntity = new EntityInsertionAdapter<PhotoEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `photos` (`photo_id`,`installation_id`,`photo_type`,`file_path`,`file_size_bytes`,`width`,`height`,`validation_status`,`validation_confidence`,`ai_metadata`,`manual_override`,`override_reason`,`override_by`,`override_at`,`upload_status`,`upload_attempts`,`last_upload_attempt`,`created_at`,`updated_at`,`checksum`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
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
        if (entity.getChecksum() == null) {
          statement.bindNull(20);
        } else {
          statement.bindString(20, entity.getChecksum());
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
        return "UPDATE OR ABORT `photos` SET `photo_id` = ?,`installation_id` = ?,`photo_type` = ?,`file_path` = ?,`file_size_bytes` = ?,`width` = ?,`height` = ?,`validation_status` = ?,`validation_confidence` = ?,`ai_metadata` = ?,`manual_override` = ?,`override_reason` = ?,`override_by` = ?,`override_at` = ?,`upload_status` = ?,`upload_attempts` = ?,`last_upload_attempt` = ?,`created_at` = ?,`updated_at` = ?,`checksum` = ? WHERE `photo_id` = ?";
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
        if (entity.getChecksum() == null) {
          statement.bindNull(20);
        } else {
          statement.bindString(20, entity.getChecksum());
        }
        statement.bindLong(21, entity.getPhotoId());
      }
    };
    this.__preparedStmtOfUpdatePhotoValidationStatus = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE photos SET validation_status = ?, updated_at = ?, override_reason = ? WHERE photo_id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfUpdatePhotoValidationResults = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE photos SET validation_status = ?, updated_at = ?, validation_confidence = ?, ai_metadata = ?, override_reason = ? WHERE photo_id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfMarkPhotoForSync = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE photos SET upload_status = 'PENDING' WHERE photo_id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfMarkPhotoSynced = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE photos SET upload_status = 'COMPLETED', updated_at = ? WHERE photo_id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfUpdatePhotoFilePath = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE photos SET file_path = ?, file_size_bytes = ? WHERE photo_id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfUpdatePhotoLocation = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE photos SET ai_metadata = ? WHERE photo_id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfUpdatePhotoQualityMetrics = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE photos SET validation_confidence = ? WHERE photo_id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteOldPhotos = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM photos WHERE created_at < ?";
        return _query;
      }
    };
    this.__preparedStmtOfUpdatePhotoChecksum = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE photos SET checksum = ? WHERE photo_id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfDeletePhotoByInstallationId = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM photos WHERE installation_id = ?";
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
  public Object insertOrUpdatePhoto(final PhotoEntity photo,
      final Continuation<? super Unit> $completion) {
    return RoomDatabaseKt.withTransaction(__db, (__cont) -> PhotoDao.DefaultImpls.insertOrUpdatePhoto(PhotoDao_Impl.this, photo, __cont), $completion);
  }

  @Override
  public Object updatePhotoValidationStatus(final long photoId, final String status,
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
        _stmt.bindLong(_argIndex, photoId);
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
  public Object updatePhotoValidationResults(final long photoId, final String status,
      final float confidence, final String objects, final String notes, final long timestamp,
      final Continuation<? super Unit> $completion) {
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
        if (notes == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindString(_argIndex, notes);
        }
        _argIndex = 6;
        _stmt.bindLong(_argIndex, photoId);
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
  public Object markPhotoForSync(final long photoId, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfMarkPhotoForSync.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, photoId);
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
  public Object markPhotoSynced(final long photoId, final long timestamp,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfMarkPhotoSynced.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, timestamp);
        _argIndex = 2;
        _stmt.bindLong(_argIndex, photoId);
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
  public Object updatePhotoFilePath(final long photoId, final String filePath, final long fileSize,
      final Continuation<? super Unit> $completion) {
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
        _stmt.bindLong(_argIndex, photoId);
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
  public Object updatePhotoLocation(final long photoId, final String locationData,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfUpdatePhotoLocation.acquire();
        int _argIndex = 1;
        if (locationData == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindString(_argIndex, locationData);
        }
        _argIndex = 2;
        _stmt.bindLong(_argIndex, photoId);
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
  public Object updatePhotoQualityMetrics(final long photoId, final Float qualityScore,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfUpdatePhotoQualityMetrics.acquire();
        int _argIndex = 1;
        if (qualityScore == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindDouble(_argIndex, qualityScore);
        }
        _argIndex = 2;
        _stmt.bindLong(_argIndex, photoId);
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
  public Object updatePhotoChecksum(final long photoId, final String checksum,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfUpdatePhotoChecksum.acquire();
        int _argIndex = 1;
        if (checksum == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindString(_argIndex, checksum);
        }
        _argIndex = 2;
        _stmt.bindLong(_argIndex, photoId);
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
          __preparedStmtOfUpdatePhotoChecksum.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object deletePhotoByInstallationId(final long installationId,
      final Continuation<? super Integer> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Integer>() {
      @Override
      @NonNull
      public Integer call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeletePhotoByInstallationId.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, installationId);
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
          __preparedStmtOfDeletePhotoByInstallationId.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object getPhotoById(final long photoId,
      final Continuation<? super PhotoEntity> $completion) {
    final String _sql = "SELECT * FROM photos WHERE photo_id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, photoId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<PhotoEntity>() {
      @Override
      @Nullable
      public PhotoEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfPhotoId = CursorUtil.getColumnIndexOrThrow(_cursor, "photo_id");
          final int _cursorIndexOfInstallationId = CursorUtil.getColumnIndexOrThrow(_cursor, "installation_id");
          final int _cursorIndexOfPhotoType = CursorUtil.getColumnIndexOrThrow(_cursor, "photo_type");
          final int _cursorIndexOfFilePath = CursorUtil.getColumnIndexOrThrow(_cursor, "file_path");
          final int _cursorIndexOfFileSizeBytes = CursorUtil.getColumnIndexOrThrow(_cursor, "file_size_bytes");
          final int _cursorIndexOfWidth = CursorUtil.getColumnIndexOrThrow(_cursor, "width");
          final int _cursorIndexOfHeight = CursorUtil.getColumnIndexOrThrow(_cursor, "height");
          final int _cursorIndexOfValidationStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "validation_status");
          final int _cursorIndexOfValidationConfidence = CursorUtil.getColumnIndexOrThrow(_cursor, "validation_confidence");
          final int _cursorIndexOfAiMetadata = CursorUtil.getColumnIndexOrThrow(_cursor, "ai_metadata");
          final int _cursorIndexOfManualOverride = CursorUtil.getColumnIndexOrThrow(_cursor, "manual_override");
          final int _cursorIndexOfOverrideReason = CursorUtil.getColumnIndexOrThrow(_cursor, "override_reason");
          final int _cursorIndexOfOverrideBy = CursorUtil.getColumnIndexOrThrow(_cursor, "override_by");
          final int _cursorIndexOfOverrideAt = CursorUtil.getColumnIndexOrThrow(_cursor, "override_at");
          final int _cursorIndexOfUploadStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "upload_status");
          final int _cursorIndexOfUploadAttempts = CursorUtil.getColumnIndexOrThrow(_cursor, "upload_attempts");
          final int _cursorIndexOfLastUploadAttempt = CursorUtil.getColumnIndexOrThrow(_cursor, "last_upload_attempt");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final int _cursorIndexOfChecksum = CursorUtil.getColumnIndexOrThrow(_cursor, "checksum");
          final PhotoEntity _result;
          if (_cursor.moveToFirst()) {
            final long _tmpPhotoId;
            _tmpPhotoId = _cursor.getLong(_cursorIndexOfPhotoId);
            final long _tmpInstallationId;
            _tmpInstallationId = _cursor.getLong(_cursorIndexOfInstallationId);
            final PhotoType _tmpPhotoType;
            final String _tmp;
            if (_cursor.isNull(_cursorIndexOfPhotoType)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getString(_cursorIndexOfPhotoType);
            }
            _tmpPhotoType = __statusConverters.toPhotoType(_tmp);
            final String _tmpFilePath;
            if (_cursor.isNull(_cursorIndexOfFilePath)) {
              _tmpFilePath = null;
            } else {
              _tmpFilePath = _cursor.getString(_cursorIndexOfFilePath);
            }
            final long _tmpFileSizeBytes;
            _tmpFileSizeBytes = _cursor.getLong(_cursorIndexOfFileSizeBytes);
            final int _tmpWidth;
            _tmpWidth = _cursor.getInt(_cursorIndexOfWidth);
            final int _tmpHeight;
            _tmpHeight = _cursor.getInt(_cursorIndexOfHeight);
            final ValidationStatus _tmpValidationStatus;
            final String _tmp_1;
            if (_cursor.isNull(_cursorIndexOfValidationStatus)) {
              _tmp_1 = null;
            } else {
              _tmp_1 = _cursor.getString(_cursorIndexOfValidationStatus);
            }
            _tmpValidationStatus = __statusConverters.toValidationStatus(_tmp_1);
            final Float _tmpValidationConfidence;
            if (_cursor.isNull(_cursorIndexOfValidationConfidence)) {
              _tmpValidationConfidence = null;
            } else {
              _tmpValidationConfidence = _cursor.getFloat(_cursorIndexOfValidationConfidence);
            }
            final String _tmpAiMetadata;
            if (_cursor.isNull(_cursorIndexOfAiMetadata)) {
              _tmpAiMetadata = null;
            } else {
              _tmpAiMetadata = _cursor.getString(_cursorIndexOfAiMetadata);
            }
            final boolean _tmpManualOverride;
            final int _tmp_2;
            _tmp_2 = _cursor.getInt(_cursorIndexOfManualOverride);
            _tmpManualOverride = _tmp_2 != 0;
            final String _tmpOverrideReason;
            if (_cursor.isNull(_cursorIndexOfOverrideReason)) {
              _tmpOverrideReason = null;
            } else {
              _tmpOverrideReason = _cursor.getString(_cursorIndexOfOverrideReason);
            }
            final String _tmpOverrideBy;
            if (_cursor.isNull(_cursorIndexOfOverrideBy)) {
              _tmpOverrideBy = null;
            } else {
              _tmpOverrideBy = _cursor.getString(_cursorIndexOfOverrideBy);
            }
            final Date _tmpOverrideAt;
            final Long _tmp_3;
            if (_cursor.isNull(_cursorIndexOfOverrideAt)) {
              _tmp_3 = null;
            } else {
              _tmp_3 = _cursor.getLong(_cursorIndexOfOverrideAt);
            }
            _tmpOverrideAt = __dateConverters.fromTimestamp(_tmp_3);
            final UploadStatus _tmpUploadStatus;
            final String _tmp_4;
            if (_cursor.isNull(_cursorIndexOfUploadStatus)) {
              _tmp_4 = null;
            } else {
              _tmp_4 = _cursor.getString(_cursorIndexOfUploadStatus);
            }
            _tmpUploadStatus = __statusConverters.toUploadStatus(_tmp_4);
            final int _tmpUploadAttempts;
            _tmpUploadAttempts = _cursor.getInt(_cursorIndexOfUploadAttempts);
            final Date _tmpLastUploadAttempt;
            final Long _tmp_5;
            if (_cursor.isNull(_cursorIndexOfLastUploadAttempt)) {
              _tmp_5 = null;
            } else {
              _tmp_5 = _cursor.getLong(_cursorIndexOfLastUploadAttempt);
            }
            _tmpLastUploadAttempt = __dateConverters.fromTimestamp(_tmp_5);
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
            final String _tmpChecksum;
            if (_cursor.isNull(_cursorIndexOfChecksum)) {
              _tmpChecksum = null;
            } else {
              _tmpChecksum = _cursor.getString(_cursorIndexOfChecksum);
            }
            _result = new PhotoEntity(_tmpPhotoId,_tmpInstallationId,_tmpPhotoType,_tmpFilePath,_tmpFileSizeBytes,_tmpWidth,_tmpHeight,_tmpValidationStatus,_tmpValidationConfidence,_tmpAiMetadata,_tmpManualOverride,_tmpOverrideReason,_tmpOverrideBy,_tmpOverrideAt,_tmpUploadStatus,_tmpUploadAttempts,_tmpLastUploadAttempt,_tmpCreatedAt,_tmpUpdatedAt,_tmpChecksum);
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
  public Flow<PhotoEntity> getPhotoByIdFlow(final long photoId) {
    final String _sql = "SELECT * FROM photos WHERE photo_id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, photoId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"photos"}, new Callable<PhotoEntity>() {
      @Override
      @Nullable
      public PhotoEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfPhotoId = CursorUtil.getColumnIndexOrThrow(_cursor, "photo_id");
          final int _cursorIndexOfInstallationId = CursorUtil.getColumnIndexOrThrow(_cursor, "installation_id");
          final int _cursorIndexOfPhotoType = CursorUtil.getColumnIndexOrThrow(_cursor, "photo_type");
          final int _cursorIndexOfFilePath = CursorUtil.getColumnIndexOrThrow(_cursor, "file_path");
          final int _cursorIndexOfFileSizeBytes = CursorUtil.getColumnIndexOrThrow(_cursor, "file_size_bytes");
          final int _cursorIndexOfWidth = CursorUtil.getColumnIndexOrThrow(_cursor, "width");
          final int _cursorIndexOfHeight = CursorUtil.getColumnIndexOrThrow(_cursor, "height");
          final int _cursorIndexOfValidationStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "validation_status");
          final int _cursorIndexOfValidationConfidence = CursorUtil.getColumnIndexOrThrow(_cursor, "validation_confidence");
          final int _cursorIndexOfAiMetadata = CursorUtil.getColumnIndexOrThrow(_cursor, "ai_metadata");
          final int _cursorIndexOfManualOverride = CursorUtil.getColumnIndexOrThrow(_cursor, "manual_override");
          final int _cursorIndexOfOverrideReason = CursorUtil.getColumnIndexOrThrow(_cursor, "override_reason");
          final int _cursorIndexOfOverrideBy = CursorUtil.getColumnIndexOrThrow(_cursor, "override_by");
          final int _cursorIndexOfOverrideAt = CursorUtil.getColumnIndexOrThrow(_cursor, "override_at");
          final int _cursorIndexOfUploadStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "upload_status");
          final int _cursorIndexOfUploadAttempts = CursorUtil.getColumnIndexOrThrow(_cursor, "upload_attempts");
          final int _cursorIndexOfLastUploadAttempt = CursorUtil.getColumnIndexOrThrow(_cursor, "last_upload_attempt");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final int _cursorIndexOfChecksum = CursorUtil.getColumnIndexOrThrow(_cursor, "checksum");
          final PhotoEntity _result;
          if (_cursor.moveToFirst()) {
            final long _tmpPhotoId;
            _tmpPhotoId = _cursor.getLong(_cursorIndexOfPhotoId);
            final long _tmpInstallationId;
            _tmpInstallationId = _cursor.getLong(_cursorIndexOfInstallationId);
            final PhotoType _tmpPhotoType;
            final String _tmp;
            if (_cursor.isNull(_cursorIndexOfPhotoType)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getString(_cursorIndexOfPhotoType);
            }
            _tmpPhotoType = __statusConverters.toPhotoType(_tmp);
            final String _tmpFilePath;
            if (_cursor.isNull(_cursorIndexOfFilePath)) {
              _tmpFilePath = null;
            } else {
              _tmpFilePath = _cursor.getString(_cursorIndexOfFilePath);
            }
            final long _tmpFileSizeBytes;
            _tmpFileSizeBytes = _cursor.getLong(_cursorIndexOfFileSizeBytes);
            final int _tmpWidth;
            _tmpWidth = _cursor.getInt(_cursorIndexOfWidth);
            final int _tmpHeight;
            _tmpHeight = _cursor.getInt(_cursorIndexOfHeight);
            final ValidationStatus _tmpValidationStatus;
            final String _tmp_1;
            if (_cursor.isNull(_cursorIndexOfValidationStatus)) {
              _tmp_1 = null;
            } else {
              _tmp_1 = _cursor.getString(_cursorIndexOfValidationStatus);
            }
            _tmpValidationStatus = __statusConverters.toValidationStatus(_tmp_1);
            final Float _tmpValidationConfidence;
            if (_cursor.isNull(_cursorIndexOfValidationConfidence)) {
              _tmpValidationConfidence = null;
            } else {
              _tmpValidationConfidence = _cursor.getFloat(_cursorIndexOfValidationConfidence);
            }
            final String _tmpAiMetadata;
            if (_cursor.isNull(_cursorIndexOfAiMetadata)) {
              _tmpAiMetadata = null;
            } else {
              _tmpAiMetadata = _cursor.getString(_cursorIndexOfAiMetadata);
            }
            final boolean _tmpManualOverride;
            final int _tmp_2;
            _tmp_2 = _cursor.getInt(_cursorIndexOfManualOverride);
            _tmpManualOverride = _tmp_2 != 0;
            final String _tmpOverrideReason;
            if (_cursor.isNull(_cursorIndexOfOverrideReason)) {
              _tmpOverrideReason = null;
            } else {
              _tmpOverrideReason = _cursor.getString(_cursorIndexOfOverrideReason);
            }
            final String _tmpOverrideBy;
            if (_cursor.isNull(_cursorIndexOfOverrideBy)) {
              _tmpOverrideBy = null;
            } else {
              _tmpOverrideBy = _cursor.getString(_cursorIndexOfOverrideBy);
            }
            final Date _tmpOverrideAt;
            final Long _tmp_3;
            if (_cursor.isNull(_cursorIndexOfOverrideAt)) {
              _tmp_3 = null;
            } else {
              _tmp_3 = _cursor.getLong(_cursorIndexOfOverrideAt);
            }
            _tmpOverrideAt = __dateConverters.fromTimestamp(_tmp_3);
            final UploadStatus _tmpUploadStatus;
            final String _tmp_4;
            if (_cursor.isNull(_cursorIndexOfUploadStatus)) {
              _tmp_4 = null;
            } else {
              _tmp_4 = _cursor.getString(_cursorIndexOfUploadStatus);
            }
            _tmpUploadStatus = __statusConverters.toUploadStatus(_tmp_4);
            final int _tmpUploadAttempts;
            _tmpUploadAttempts = _cursor.getInt(_cursorIndexOfUploadAttempts);
            final Date _tmpLastUploadAttempt;
            final Long _tmp_5;
            if (_cursor.isNull(_cursorIndexOfLastUploadAttempt)) {
              _tmp_5 = null;
            } else {
              _tmp_5 = _cursor.getLong(_cursorIndexOfLastUploadAttempt);
            }
            _tmpLastUploadAttempt = __dateConverters.fromTimestamp(_tmp_5);
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
            final String _tmpChecksum;
            if (_cursor.isNull(_cursorIndexOfChecksum)) {
              _tmpChecksum = null;
            } else {
              _tmpChecksum = _cursor.getString(_cursorIndexOfChecksum);
            }
            _result = new PhotoEntity(_tmpPhotoId,_tmpInstallationId,_tmpPhotoType,_tmpFilePath,_tmpFileSizeBytes,_tmpWidth,_tmpHeight,_tmpValidationStatus,_tmpValidationConfidence,_tmpAiMetadata,_tmpManualOverride,_tmpOverrideReason,_tmpOverrideBy,_tmpOverrideAt,_tmpUploadStatus,_tmpUploadAttempts,_tmpLastUploadAttempt,_tmpCreatedAt,_tmpUpdatedAt,_tmpChecksum);
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
  public Object getPhotosByInstallation(final long installationId,
      final Continuation<? super List<PhotoEntity>> $completion) {
    final String _sql = "SELECT * FROM photos WHERE installation_id = ? ORDER BY created_at ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, installationId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<PhotoEntity>>() {
      @Override
      @NonNull
      public List<PhotoEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfPhotoId = CursorUtil.getColumnIndexOrThrow(_cursor, "photo_id");
          final int _cursorIndexOfInstallationId = CursorUtil.getColumnIndexOrThrow(_cursor, "installation_id");
          final int _cursorIndexOfPhotoType = CursorUtil.getColumnIndexOrThrow(_cursor, "photo_type");
          final int _cursorIndexOfFilePath = CursorUtil.getColumnIndexOrThrow(_cursor, "file_path");
          final int _cursorIndexOfFileSizeBytes = CursorUtil.getColumnIndexOrThrow(_cursor, "file_size_bytes");
          final int _cursorIndexOfWidth = CursorUtil.getColumnIndexOrThrow(_cursor, "width");
          final int _cursorIndexOfHeight = CursorUtil.getColumnIndexOrThrow(_cursor, "height");
          final int _cursorIndexOfValidationStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "validation_status");
          final int _cursorIndexOfValidationConfidence = CursorUtil.getColumnIndexOrThrow(_cursor, "validation_confidence");
          final int _cursorIndexOfAiMetadata = CursorUtil.getColumnIndexOrThrow(_cursor, "ai_metadata");
          final int _cursorIndexOfManualOverride = CursorUtil.getColumnIndexOrThrow(_cursor, "manual_override");
          final int _cursorIndexOfOverrideReason = CursorUtil.getColumnIndexOrThrow(_cursor, "override_reason");
          final int _cursorIndexOfOverrideBy = CursorUtil.getColumnIndexOrThrow(_cursor, "override_by");
          final int _cursorIndexOfOverrideAt = CursorUtil.getColumnIndexOrThrow(_cursor, "override_at");
          final int _cursorIndexOfUploadStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "upload_status");
          final int _cursorIndexOfUploadAttempts = CursorUtil.getColumnIndexOrThrow(_cursor, "upload_attempts");
          final int _cursorIndexOfLastUploadAttempt = CursorUtil.getColumnIndexOrThrow(_cursor, "last_upload_attempt");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final int _cursorIndexOfChecksum = CursorUtil.getColumnIndexOrThrow(_cursor, "checksum");
          final List<PhotoEntity> _result = new ArrayList<PhotoEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final PhotoEntity _item;
            final long _tmpPhotoId;
            _tmpPhotoId = _cursor.getLong(_cursorIndexOfPhotoId);
            final long _tmpInstallationId;
            _tmpInstallationId = _cursor.getLong(_cursorIndexOfInstallationId);
            final PhotoType _tmpPhotoType;
            final String _tmp;
            if (_cursor.isNull(_cursorIndexOfPhotoType)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getString(_cursorIndexOfPhotoType);
            }
            _tmpPhotoType = __statusConverters.toPhotoType(_tmp);
            final String _tmpFilePath;
            if (_cursor.isNull(_cursorIndexOfFilePath)) {
              _tmpFilePath = null;
            } else {
              _tmpFilePath = _cursor.getString(_cursorIndexOfFilePath);
            }
            final long _tmpFileSizeBytes;
            _tmpFileSizeBytes = _cursor.getLong(_cursorIndexOfFileSizeBytes);
            final int _tmpWidth;
            _tmpWidth = _cursor.getInt(_cursorIndexOfWidth);
            final int _tmpHeight;
            _tmpHeight = _cursor.getInt(_cursorIndexOfHeight);
            final ValidationStatus _tmpValidationStatus;
            final String _tmp_1;
            if (_cursor.isNull(_cursorIndexOfValidationStatus)) {
              _tmp_1 = null;
            } else {
              _tmp_1 = _cursor.getString(_cursorIndexOfValidationStatus);
            }
            _tmpValidationStatus = __statusConverters.toValidationStatus(_tmp_1);
            final Float _tmpValidationConfidence;
            if (_cursor.isNull(_cursorIndexOfValidationConfidence)) {
              _tmpValidationConfidence = null;
            } else {
              _tmpValidationConfidence = _cursor.getFloat(_cursorIndexOfValidationConfidence);
            }
            final String _tmpAiMetadata;
            if (_cursor.isNull(_cursorIndexOfAiMetadata)) {
              _tmpAiMetadata = null;
            } else {
              _tmpAiMetadata = _cursor.getString(_cursorIndexOfAiMetadata);
            }
            final boolean _tmpManualOverride;
            final int _tmp_2;
            _tmp_2 = _cursor.getInt(_cursorIndexOfManualOverride);
            _tmpManualOverride = _tmp_2 != 0;
            final String _tmpOverrideReason;
            if (_cursor.isNull(_cursorIndexOfOverrideReason)) {
              _tmpOverrideReason = null;
            } else {
              _tmpOverrideReason = _cursor.getString(_cursorIndexOfOverrideReason);
            }
            final String _tmpOverrideBy;
            if (_cursor.isNull(_cursorIndexOfOverrideBy)) {
              _tmpOverrideBy = null;
            } else {
              _tmpOverrideBy = _cursor.getString(_cursorIndexOfOverrideBy);
            }
            final Date _tmpOverrideAt;
            final Long _tmp_3;
            if (_cursor.isNull(_cursorIndexOfOverrideAt)) {
              _tmp_3 = null;
            } else {
              _tmp_3 = _cursor.getLong(_cursorIndexOfOverrideAt);
            }
            _tmpOverrideAt = __dateConverters.fromTimestamp(_tmp_3);
            final UploadStatus _tmpUploadStatus;
            final String _tmp_4;
            if (_cursor.isNull(_cursorIndexOfUploadStatus)) {
              _tmp_4 = null;
            } else {
              _tmp_4 = _cursor.getString(_cursorIndexOfUploadStatus);
            }
            _tmpUploadStatus = __statusConverters.toUploadStatus(_tmp_4);
            final int _tmpUploadAttempts;
            _tmpUploadAttempts = _cursor.getInt(_cursorIndexOfUploadAttempts);
            final Date _tmpLastUploadAttempt;
            final Long _tmp_5;
            if (_cursor.isNull(_cursorIndexOfLastUploadAttempt)) {
              _tmp_5 = null;
            } else {
              _tmp_5 = _cursor.getLong(_cursorIndexOfLastUploadAttempt);
            }
            _tmpLastUploadAttempt = __dateConverters.fromTimestamp(_tmp_5);
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
            final String _tmpChecksum;
            if (_cursor.isNull(_cursorIndexOfChecksum)) {
              _tmpChecksum = null;
            } else {
              _tmpChecksum = _cursor.getString(_cursorIndexOfChecksum);
            }
            _item = new PhotoEntity(_tmpPhotoId,_tmpInstallationId,_tmpPhotoType,_tmpFilePath,_tmpFileSizeBytes,_tmpWidth,_tmpHeight,_tmpValidationStatus,_tmpValidationConfidence,_tmpAiMetadata,_tmpManualOverride,_tmpOverrideReason,_tmpOverrideBy,_tmpOverrideAt,_tmpUploadStatus,_tmpUploadAttempts,_tmpLastUploadAttempt,_tmpCreatedAt,_tmpUpdatedAt,_tmpChecksum);
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
  public Flow<List<PhotoEntity>> getPhotosByInstallationFlow(final long installationId) {
    final String _sql = "SELECT * FROM photos WHERE installation_id = ? ORDER BY created_at ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, installationId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"photos"}, new Callable<List<PhotoEntity>>() {
      @Override
      @NonNull
      public List<PhotoEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfPhotoId = CursorUtil.getColumnIndexOrThrow(_cursor, "photo_id");
          final int _cursorIndexOfInstallationId = CursorUtil.getColumnIndexOrThrow(_cursor, "installation_id");
          final int _cursorIndexOfPhotoType = CursorUtil.getColumnIndexOrThrow(_cursor, "photo_type");
          final int _cursorIndexOfFilePath = CursorUtil.getColumnIndexOrThrow(_cursor, "file_path");
          final int _cursorIndexOfFileSizeBytes = CursorUtil.getColumnIndexOrThrow(_cursor, "file_size_bytes");
          final int _cursorIndexOfWidth = CursorUtil.getColumnIndexOrThrow(_cursor, "width");
          final int _cursorIndexOfHeight = CursorUtil.getColumnIndexOrThrow(_cursor, "height");
          final int _cursorIndexOfValidationStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "validation_status");
          final int _cursorIndexOfValidationConfidence = CursorUtil.getColumnIndexOrThrow(_cursor, "validation_confidence");
          final int _cursorIndexOfAiMetadata = CursorUtil.getColumnIndexOrThrow(_cursor, "ai_metadata");
          final int _cursorIndexOfManualOverride = CursorUtil.getColumnIndexOrThrow(_cursor, "manual_override");
          final int _cursorIndexOfOverrideReason = CursorUtil.getColumnIndexOrThrow(_cursor, "override_reason");
          final int _cursorIndexOfOverrideBy = CursorUtil.getColumnIndexOrThrow(_cursor, "override_by");
          final int _cursorIndexOfOverrideAt = CursorUtil.getColumnIndexOrThrow(_cursor, "override_at");
          final int _cursorIndexOfUploadStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "upload_status");
          final int _cursorIndexOfUploadAttempts = CursorUtil.getColumnIndexOrThrow(_cursor, "upload_attempts");
          final int _cursorIndexOfLastUploadAttempt = CursorUtil.getColumnIndexOrThrow(_cursor, "last_upload_attempt");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final int _cursorIndexOfChecksum = CursorUtil.getColumnIndexOrThrow(_cursor, "checksum");
          final List<PhotoEntity> _result = new ArrayList<PhotoEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final PhotoEntity _item;
            final long _tmpPhotoId;
            _tmpPhotoId = _cursor.getLong(_cursorIndexOfPhotoId);
            final long _tmpInstallationId;
            _tmpInstallationId = _cursor.getLong(_cursorIndexOfInstallationId);
            final PhotoType _tmpPhotoType;
            final String _tmp;
            if (_cursor.isNull(_cursorIndexOfPhotoType)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getString(_cursorIndexOfPhotoType);
            }
            _tmpPhotoType = __statusConverters.toPhotoType(_tmp);
            final String _tmpFilePath;
            if (_cursor.isNull(_cursorIndexOfFilePath)) {
              _tmpFilePath = null;
            } else {
              _tmpFilePath = _cursor.getString(_cursorIndexOfFilePath);
            }
            final long _tmpFileSizeBytes;
            _tmpFileSizeBytes = _cursor.getLong(_cursorIndexOfFileSizeBytes);
            final int _tmpWidth;
            _tmpWidth = _cursor.getInt(_cursorIndexOfWidth);
            final int _tmpHeight;
            _tmpHeight = _cursor.getInt(_cursorIndexOfHeight);
            final ValidationStatus _tmpValidationStatus;
            final String _tmp_1;
            if (_cursor.isNull(_cursorIndexOfValidationStatus)) {
              _tmp_1 = null;
            } else {
              _tmp_1 = _cursor.getString(_cursorIndexOfValidationStatus);
            }
            _tmpValidationStatus = __statusConverters.toValidationStatus(_tmp_1);
            final Float _tmpValidationConfidence;
            if (_cursor.isNull(_cursorIndexOfValidationConfidence)) {
              _tmpValidationConfidence = null;
            } else {
              _tmpValidationConfidence = _cursor.getFloat(_cursorIndexOfValidationConfidence);
            }
            final String _tmpAiMetadata;
            if (_cursor.isNull(_cursorIndexOfAiMetadata)) {
              _tmpAiMetadata = null;
            } else {
              _tmpAiMetadata = _cursor.getString(_cursorIndexOfAiMetadata);
            }
            final boolean _tmpManualOverride;
            final int _tmp_2;
            _tmp_2 = _cursor.getInt(_cursorIndexOfManualOverride);
            _tmpManualOverride = _tmp_2 != 0;
            final String _tmpOverrideReason;
            if (_cursor.isNull(_cursorIndexOfOverrideReason)) {
              _tmpOverrideReason = null;
            } else {
              _tmpOverrideReason = _cursor.getString(_cursorIndexOfOverrideReason);
            }
            final String _tmpOverrideBy;
            if (_cursor.isNull(_cursorIndexOfOverrideBy)) {
              _tmpOverrideBy = null;
            } else {
              _tmpOverrideBy = _cursor.getString(_cursorIndexOfOverrideBy);
            }
            final Date _tmpOverrideAt;
            final Long _tmp_3;
            if (_cursor.isNull(_cursorIndexOfOverrideAt)) {
              _tmp_3 = null;
            } else {
              _tmp_3 = _cursor.getLong(_cursorIndexOfOverrideAt);
            }
            _tmpOverrideAt = __dateConverters.fromTimestamp(_tmp_3);
            final UploadStatus _tmpUploadStatus;
            final String _tmp_4;
            if (_cursor.isNull(_cursorIndexOfUploadStatus)) {
              _tmp_4 = null;
            } else {
              _tmp_4 = _cursor.getString(_cursorIndexOfUploadStatus);
            }
            _tmpUploadStatus = __statusConverters.toUploadStatus(_tmp_4);
            final int _tmpUploadAttempts;
            _tmpUploadAttempts = _cursor.getInt(_cursorIndexOfUploadAttempts);
            final Date _tmpLastUploadAttempt;
            final Long _tmp_5;
            if (_cursor.isNull(_cursorIndexOfLastUploadAttempt)) {
              _tmp_5 = null;
            } else {
              _tmp_5 = _cursor.getLong(_cursorIndexOfLastUploadAttempt);
            }
            _tmpLastUploadAttempt = __dateConverters.fromTimestamp(_tmp_5);
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
            final String _tmpChecksum;
            if (_cursor.isNull(_cursorIndexOfChecksum)) {
              _tmpChecksum = null;
            } else {
              _tmpChecksum = _cursor.getString(_cursorIndexOfChecksum);
            }
            _item = new PhotoEntity(_tmpPhotoId,_tmpInstallationId,_tmpPhotoType,_tmpFilePath,_tmpFileSizeBytes,_tmpWidth,_tmpHeight,_tmpValidationStatus,_tmpValidationConfidence,_tmpAiMetadata,_tmpManualOverride,_tmpOverrideReason,_tmpOverrideBy,_tmpOverrideAt,_tmpUploadStatus,_tmpUploadAttempts,_tmpLastUploadAttempt,_tmpCreatedAt,_tmpUpdatedAt,_tmpChecksum);
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
  public Object getPhotosByStep(final long installationId, final String photoType,
      final Continuation<? super List<PhotoEntity>> $completion) {
    final String _sql = "SELECT * FROM photos WHERE installation_id = ? AND photo_type = ? ORDER BY created_at ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, installationId);
    _argIndex = 2;
    if (photoType == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, photoType);
    }
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<PhotoEntity>>() {
      @Override
      @NonNull
      public List<PhotoEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfPhotoId = CursorUtil.getColumnIndexOrThrow(_cursor, "photo_id");
          final int _cursorIndexOfInstallationId = CursorUtil.getColumnIndexOrThrow(_cursor, "installation_id");
          final int _cursorIndexOfPhotoType = CursorUtil.getColumnIndexOrThrow(_cursor, "photo_type");
          final int _cursorIndexOfFilePath = CursorUtil.getColumnIndexOrThrow(_cursor, "file_path");
          final int _cursorIndexOfFileSizeBytes = CursorUtil.getColumnIndexOrThrow(_cursor, "file_size_bytes");
          final int _cursorIndexOfWidth = CursorUtil.getColumnIndexOrThrow(_cursor, "width");
          final int _cursorIndexOfHeight = CursorUtil.getColumnIndexOrThrow(_cursor, "height");
          final int _cursorIndexOfValidationStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "validation_status");
          final int _cursorIndexOfValidationConfidence = CursorUtil.getColumnIndexOrThrow(_cursor, "validation_confidence");
          final int _cursorIndexOfAiMetadata = CursorUtil.getColumnIndexOrThrow(_cursor, "ai_metadata");
          final int _cursorIndexOfManualOverride = CursorUtil.getColumnIndexOrThrow(_cursor, "manual_override");
          final int _cursorIndexOfOverrideReason = CursorUtil.getColumnIndexOrThrow(_cursor, "override_reason");
          final int _cursorIndexOfOverrideBy = CursorUtil.getColumnIndexOrThrow(_cursor, "override_by");
          final int _cursorIndexOfOverrideAt = CursorUtil.getColumnIndexOrThrow(_cursor, "override_at");
          final int _cursorIndexOfUploadStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "upload_status");
          final int _cursorIndexOfUploadAttempts = CursorUtil.getColumnIndexOrThrow(_cursor, "upload_attempts");
          final int _cursorIndexOfLastUploadAttempt = CursorUtil.getColumnIndexOrThrow(_cursor, "last_upload_attempt");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final int _cursorIndexOfChecksum = CursorUtil.getColumnIndexOrThrow(_cursor, "checksum");
          final List<PhotoEntity> _result = new ArrayList<PhotoEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final PhotoEntity _item;
            final long _tmpPhotoId;
            _tmpPhotoId = _cursor.getLong(_cursorIndexOfPhotoId);
            final long _tmpInstallationId;
            _tmpInstallationId = _cursor.getLong(_cursorIndexOfInstallationId);
            final PhotoType _tmpPhotoType;
            final String _tmp;
            if (_cursor.isNull(_cursorIndexOfPhotoType)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getString(_cursorIndexOfPhotoType);
            }
            _tmpPhotoType = __statusConverters.toPhotoType(_tmp);
            final String _tmpFilePath;
            if (_cursor.isNull(_cursorIndexOfFilePath)) {
              _tmpFilePath = null;
            } else {
              _tmpFilePath = _cursor.getString(_cursorIndexOfFilePath);
            }
            final long _tmpFileSizeBytes;
            _tmpFileSizeBytes = _cursor.getLong(_cursorIndexOfFileSizeBytes);
            final int _tmpWidth;
            _tmpWidth = _cursor.getInt(_cursorIndexOfWidth);
            final int _tmpHeight;
            _tmpHeight = _cursor.getInt(_cursorIndexOfHeight);
            final ValidationStatus _tmpValidationStatus;
            final String _tmp_1;
            if (_cursor.isNull(_cursorIndexOfValidationStatus)) {
              _tmp_1 = null;
            } else {
              _tmp_1 = _cursor.getString(_cursorIndexOfValidationStatus);
            }
            _tmpValidationStatus = __statusConverters.toValidationStatus(_tmp_1);
            final Float _tmpValidationConfidence;
            if (_cursor.isNull(_cursorIndexOfValidationConfidence)) {
              _tmpValidationConfidence = null;
            } else {
              _tmpValidationConfidence = _cursor.getFloat(_cursorIndexOfValidationConfidence);
            }
            final String _tmpAiMetadata;
            if (_cursor.isNull(_cursorIndexOfAiMetadata)) {
              _tmpAiMetadata = null;
            } else {
              _tmpAiMetadata = _cursor.getString(_cursorIndexOfAiMetadata);
            }
            final boolean _tmpManualOverride;
            final int _tmp_2;
            _tmp_2 = _cursor.getInt(_cursorIndexOfManualOverride);
            _tmpManualOverride = _tmp_2 != 0;
            final String _tmpOverrideReason;
            if (_cursor.isNull(_cursorIndexOfOverrideReason)) {
              _tmpOverrideReason = null;
            } else {
              _tmpOverrideReason = _cursor.getString(_cursorIndexOfOverrideReason);
            }
            final String _tmpOverrideBy;
            if (_cursor.isNull(_cursorIndexOfOverrideBy)) {
              _tmpOverrideBy = null;
            } else {
              _tmpOverrideBy = _cursor.getString(_cursorIndexOfOverrideBy);
            }
            final Date _tmpOverrideAt;
            final Long _tmp_3;
            if (_cursor.isNull(_cursorIndexOfOverrideAt)) {
              _tmp_3 = null;
            } else {
              _tmp_3 = _cursor.getLong(_cursorIndexOfOverrideAt);
            }
            _tmpOverrideAt = __dateConverters.fromTimestamp(_tmp_3);
            final UploadStatus _tmpUploadStatus;
            final String _tmp_4;
            if (_cursor.isNull(_cursorIndexOfUploadStatus)) {
              _tmp_4 = null;
            } else {
              _tmp_4 = _cursor.getString(_cursorIndexOfUploadStatus);
            }
            _tmpUploadStatus = __statusConverters.toUploadStatus(_tmp_4);
            final int _tmpUploadAttempts;
            _tmpUploadAttempts = _cursor.getInt(_cursorIndexOfUploadAttempts);
            final Date _tmpLastUploadAttempt;
            final Long _tmp_5;
            if (_cursor.isNull(_cursorIndexOfLastUploadAttempt)) {
              _tmp_5 = null;
            } else {
              _tmp_5 = _cursor.getLong(_cursorIndexOfLastUploadAttempt);
            }
            _tmpLastUploadAttempt = __dateConverters.fromTimestamp(_tmp_5);
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
            final String _tmpChecksum;
            if (_cursor.isNull(_cursorIndexOfChecksum)) {
              _tmpChecksum = null;
            } else {
              _tmpChecksum = _cursor.getString(_cursorIndexOfChecksum);
            }
            _item = new PhotoEntity(_tmpPhotoId,_tmpInstallationId,_tmpPhotoType,_tmpFilePath,_tmpFileSizeBytes,_tmpWidth,_tmpHeight,_tmpValidationStatus,_tmpValidationConfidence,_tmpAiMetadata,_tmpManualOverride,_tmpOverrideReason,_tmpOverrideBy,_tmpOverrideAt,_tmpUploadStatus,_tmpUploadAttempts,_tmpLastUploadAttempt,_tmpCreatedAt,_tmpUpdatedAt,_tmpChecksum);
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
  public Object getPhotosRequiringValidation(
      final Continuation<? super List<PhotoEntity>> $completion) {
    final String _sql = "SELECT * FROM photos WHERE validation_status = 'PENDING' ORDER BY created_at ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<PhotoEntity>>() {
      @Override
      @NonNull
      public List<PhotoEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfPhotoId = CursorUtil.getColumnIndexOrThrow(_cursor, "photo_id");
          final int _cursorIndexOfInstallationId = CursorUtil.getColumnIndexOrThrow(_cursor, "installation_id");
          final int _cursorIndexOfPhotoType = CursorUtil.getColumnIndexOrThrow(_cursor, "photo_type");
          final int _cursorIndexOfFilePath = CursorUtil.getColumnIndexOrThrow(_cursor, "file_path");
          final int _cursorIndexOfFileSizeBytes = CursorUtil.getColumnIndexOrThrow(_cursor, "file_size_bytes");
          final int _cursorIndexOfWidth = CursorUtil.getColumnIndexOrThrow(_cursor, "width");
          final int _cursorIndexOfHeight = CursorUtil.getColumnIndexOrThrow(_cursor, "height");
          final int _cursorIndexOfValidationStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "validation_status");
          final int _cursorIndexOfValidationConfidence = CursorUtil.getColumnIndexOrThrow(_cursor, "validation_confidence");
          final int _cursorIndexOfAiMetadata = CursorUtil.getColumnIndexOrThrow(_cursor, "ai_metadata");
          final int _cursorIndexOfManualOverride = CursorUtil.getColumnIndexOrThrow(_cursor, "manual_override");
          final int _cursorIndexOfOverrideReason = CursorUtil.getColumnIndexOrThrow(_cursor, "override_reason");
          final int _cursorIndexOfOverrideBy = CursorUtil.getColumnIndexOrThrow(_cursor, "override_by");
          final int _cursorIndexOfOverrideAt = CursorUtil.getColumnIndexOrThrow(_cursor, "override_at");
          final int _cursorIndexOfUploadStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "upload_status");
          final int _cursorIndexOfUploadAttempts = CursorUtil.getColumnIndexOrThrow(_cursor, "upload_attempts");
          final int _cursorIndexOfLastUploadAttempt = CursorUtil.getColumnIndexOrThrow(_cursor, "last_upload_attempt");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final int _cursorIndexOfChecksum = CursorUtil.getColumnIndexOrThrow(_cursor, "checksum");
          final List<PhotoEntity> _result = new ArrayList<PhotoEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final PhotoEntity _item;
            final long _tmpPhotoId;
            _tmpPhotoId = _cursor.getLong(_cursorIndexOfPhotoId);
            final long _tmpInstallationId;
            _tmpInstallationId = _cursor.getLong(_cursorIndexOfInstallationId);
            final PhotoType _tmpPhotoType;
            final String _tmp;
            if (_cursor.isNull(_cursorIndexOfPhotoType)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getString(_cursorIndexOfPhotoType);
            }
            _tmpPhotoType = __statusConverters.toPhotoType(_tmp);
            final String _tmpFilePath;
            if (_cursor.isNull(_cursorIndexOfFilePath)) {
              _tmpFilePath = null;
            } else {
              _tmpFilePath = _cursor.getString(_cursorIndexOfFilePath);
            }
            final long _tmpFileSizeBytes;
            _tmpFileSizeBytes = _cursor.getLong(_cursorIndexOfFileSizeBytes);
            final int _tmpWidth;
            _tmpWidth = _cursor.getInt(_cursorIndexOfWidth);
            final int _tmpHeight;
            _tmpHeight = _cursor.getInt(_cursorIndexOfHeight);
            final ValidationStatus _tmpValidationStatus;
            final String _tmp_1;
            if (_cursor.isNull(_cursorIndexOfValidationStatus)) {
              _tmp_1 = null;
            } else {
              _tmp_1 = _cursor.getString(_cursorIndexOfValidationStatus);
            }
            _tmpValidationStatus = __statusConverters.toValidationStatus(_tmp_1);
            final Float _tmpValidationConfidence;
            if (_cursor.isNull(_cursorIndexOfValidationConfidence)) {
              _tmpValidationConfidence = null;
            } else {
              _tmpValidationConfidence = _cursor.getFloat(_cursorIndexOfValidationConfidence);
            }
            final String _tmpAiMetadata;
            if (_cursor.isNull(_cursorIndexOfAiMetadata)) {
              _tmpAiMetadata = null;
            } else {
              _tmpAiMetadata = _cursor.getString(_cursorIndexOfAiMetadata);
            }
            final boolean _tmpManualOverride;
            final int _tmp_2;
            _tmp_2 = _cursor.getInt(_cursorIndexOfManualOverride);
            _tmpManualOverride = _tmp_2 != 0;
            final String _tmpOverrideReason;
            if (_cursor.isNull(_cursorIndexOfOverrideReason)) {
              _tmpOverrideReason = null;
            } else {
              _tmpOverrideReason = _cursor.getString(_cursorIndexOfOverrideReason);
            }
            final String _tmpOverrideBy;
            if (_cursor.isNull(_cursorIndexOfOverrideBy)) {
              _tmpOverrideBy = null;
            } else {
              _tmpOverrideBy = _cursor.getString(_cursorIndexOfOverrideBy);
            }
            final Date _tmpOverrideAt;
            final Long _tmp_3;
            if (_cursor.isNull(_cursorIndexOfOverrideAt)) {
              _tmp_3 = null;
            } else {
              _tmp_3 = _cursor.getLong(_cursorIndexOfOverrideAt);
            }
            _tmpOverrideAt = __dateConverters.fromTimestamp(_tmp_3);
            final UploadStatus _tmpUploadStatus;
            final String _tmp_4;
            if (_cursor.isNull(_cursorIndexOfUploadStatus)) {
              _tmp_4 = null;
            } else {
              _tmp_4 = _cursor.getString(_cursorIndexOfUploadStatus);
            }
            _tmpUploadStatus = __statusConverters.toUploadStatus(_tmp_4);
            final int _tmpUploadAttempts;
            _tmpUploadAttempts = _cursor.getInt(_cursorIndexOfUploadAttempts);
            final Date _tmpLastUploadAttempt;
            final Long _tmp_5;
            if (_cursor.isNull(_cursorIndexOfLastUploadAttempt)) {
              _tmp_5 = null;
            } else {
              _tmp_5 = _cursor.getLong(_cursorIndexOfLastUploadAttempt);
            }
            _tmpLastUploadAttempt = __dateConverters.fromTimestamp(_tmp_5);
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
            final String _tmpChecksum;
            if (_cursor.isNull(_cursorIndexOfChecksum)) {
              _tmpChecksum = null;
            } else {
              _tmpChecksum = _cursor.getString(_cursorIndexOfChecksum);
            }
            _item = new PhotoEntity(_tmpPhotoId,_tmpInstallationId,_tmpPhotoType,_tmpFilePath,_tmpFileSizeBytes,_tmpWidth,_tmpHeight,_tmpValidationStatus,_tmpValidationConfidence,_tmpAiMetadata,_tmpManualOverride,_tmpOverrideReason,_tmpOverrideBy,_tmpOverrideAt,_tmpUploadStatus,_tmpUploadAttempts,_tmpLastUploadAttempt,_tmpCreatedAt,_tmpUpdatedAt,_tmpChecksum);
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
  public Flow<List<PhotoEntity>> getPhotosRequiringValidationFlow() {
    final String _sql = "SELECT * FROM photos WHERE validation_status = 'PENDING' ORDER BY created_at ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"photos"}, new Callable<List<PhotoEntity>>() {
      @Override
      @NonNull
      public List<PhotoEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfPhotoId = CursorUtil.getColumnIndexOrThrow(_cursor, "photo_id");
          final int _cursorIndexOfInstallationId = CursorUtil.getColumnIndexOrThrow(_cursor, "installation_id");
          final int _cursorIndexOfPhotoType = CursorUtil.getColumnIndexOrThrow(_cursor, "photo_type");
          final int _cursorIndexOfFilePath = CursorUtil.getColumnIndexOrThrow(_cursor, "file_path");
          final int _cursorIndexOfFileSizeBytes = CursorUtil.getColumnIndexOrThrow(_cursor, "file_size_bytes");
          final int _cursorIndexOfWidth = CursorUtil.getColumnIndexOrThrow(_cursor, "width");
          final int _cursorIndexOfHeight = CursorUtil.getColumnIndexOrThrow(_cursor, "height");
          final int _cursorIndexOfValidationStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "validation_status");
          final int _cursorIndexOfValidationConfidence = CursorUtil.getColumnIndexOrThrow(_cursor, "validation_confidence");
          final int _cursorIndexOfAiMetadata = CursorUtil.getColumnIndexOrThrow(_cursor, "ai_metadata");
          final int _cursorIndexOfManualOverride = CursorUtil.getColumnIndexOrThrow(_cursor, "manual_override");
          final int _cursorIndexOfOverrideReason = CursorUtil.getColumnIndexOrThrow(_cursor, "override_reason");
          final int _cursorIndexOfOverrideBy = CursorUtil.getColumnIndexOrThrow(_cursor, "override_by");
          final int _cursorIndexOfOverrideAt = CursorUtil.getColumnIndexOrThrow(_cursor, "override_at");
          final int _cursorIndexOfUploadStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "upload_status");
          final int _cursorIndexOfUploadAttempts = CursorUtil.getColumnIndexOrThrow(_cursor, "upload_attempts");
          final int _cursorIndexOfLastUploadAttempt = CursorUtil.getColumnIndexOrThrow(_cursor, "last_upload_attempt");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final int _cursorIndexOfChecksum = CursorUtil.getColumnIndexOrThrow(_cursor, "checksum");
          final List<PhotoEntity> _result = new ArrayList<PhotoEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final PhotoEntity _item;
            final long _tmpPhotoId;
            _tmpPhotoId = _cursor.getLong(_cursorIndexOfPhotoId);
            final long _tmpInstallationId;
            _tmpInstallationId = _cursor.getLong(_cursorIndexOfInstallationId);
            final PhotoType _tmpPhotoType;
            final String _tmp;
            if (_cursor.isNull(_cursorIndexOfPhotoType)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getString(_cursorIndexOfPhotoType);
            }
            _tmpPhotoType = __statusConverters.toPhotoType(_tmp);
            final String _tmpFilePath;
            if (_cursor.isNull(_cursorIndexOfFilePath)) {
              _tmpFilePath = null;
            } else {
              _tmpFilePath = _cursor.getString(_cursorIndexOfFilePath);
            }
            final long _tmpFileSizeBytes;
            _tmpFileSizeBytes = _cursor.getLong(_cursorIndexOfFileSizeBytes);
            final int _tmpWidth;
            _tmpWidth = _cursor.getInt(_cursorIndexOfWidth);
            final int _tmpHeight;
            _tmpHeight = _cursor.getInt(_cursorIndexOfHeight);
            final ValidationStatus _tmpValidationStatus;
            final String _tmp_1;
            if (_cursor.isNull(_cursorIndexOfValidationStatus)) {
              _tmp_1 = null;
            } else {
              _tmp_1 = _cursor.getString(_cursorIndexOfValidationStatus);
            }
            _tmpValidationStatus = __statusConverters.toValidationStatus(_tmp_1);
            final Float _tmpValidationConfidence;
            if (_cursor.isNull(_cursorIndexOfValidationConfidence)) {
              _tmpValidationConfidence = null;
            } else {
              _tmpValidationConfidence = _cursor.getFloat(_cursorIndexOfValidationConfidence);
            }
            final String _tmpAiMetadata;
            if (_cursor.isNull(_cursorIndexOfAiMetadata)) {
              _tmpAiMetadata = null;
            } else {
              _tmpAiMetadata = _cursor.getString(_cursorIndexOfAiMetadata);
            }
            final boolean _tmpManualOverride;
            final int _tmp_2;
            _tmp_2 = _cursor.getInt(_cursorIndexOfManualOverride);
            _tmpManualOverride = _tmp_2 != 0;
            final String _tmpOverrideReason;
            if (_cursor.isNull(_cursorIndexOfOverrideReason)) {
              _tmpOverrideReason = null;
            } else {
              _tmpOverrideReason = _cursor.getString(_cursorIndexOfOverrideReason);
            }
            final String _tmpOverrideBy;
            if (_cursor.isNull(_cursorIndexOfOverrideBy)) {
              _tmpOverrideBy = null;
            } else {
              _tmpOverrideBy = _cursor.getString(_cursorIndexOfOverrideBy);
            }
            final Date _tmpOverrideAt;
            final Long _tmp_3;
            if (_cursor.isNull(_cursorIndexOfOverrideAt)) {
              _tmp_3 = null;
            } else {
              _tmp_3 = _cursor.getLong(_cursorIndexOfOverrideAt);
            }
            _tmpOverrideAt = __dateConverters.fromTimestamp(_tmp_3);
            final UploadStatus _tmpUploadStatus;
            final String _tmp_4;
            if (_cursor.isNull(_cursorIndexOfUploadStatus)) {
              _tmp_4 = null;
            } else {
              _tmp_4 = _cursor.getString(_cursorIndexOfUploadStatus);
            }
            _tmpUploadStatus = __statusConverters.toUploadStatus(_tmp_4);
            final int _tmpUploadAttempts;
            _tmpUploadAttempts = _cursor.getInt(_cursorIndexOfUploadAttempts);
            final Date _tmpLastUploadAttempt;
            final Long _tmp_5;
            if (_cursor.isNull(_cursorIndexOfLastUploadAttempt)) {
              _tmp_5 = null;
            } else {
              _tmp_5 = _cursor.getLong(_cursorIndexOfLastUploadAttempt);
            }
            _tmpLastUploadAttempt = __dateConverters.fromTimestamp(_tmp_5);
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
            final String _tmpChecksum;
            if (_cursor.isNull(_cursorIndexOfChecksum)) {
              _tmpChecksum = null;
            } else {
              _tmpChecksum = _cursor.getString(_cursorIndexOfChecksum);
            }
            _item = new PhotoEntity(_tmpPhotoId,_tmpInstallationId,_tmpPhotoType,_tmpFilePath,_tmpFileSizeBytes,_tmpWidth,_tmpHeight,_tmpValidationStatus,_tmpValidationConfidence,_tmpAiMetadata,_tmpManualOverride,_tmpOverrideReason,_tmpOverrideBy,_tmpOverrideAt,_tmpUploadStatus,_tmpUploadAttempts,_tmpLastUploadAttempt,_tmpCreatedAt,_tmpUpdatedAt,_tmpChecksum);
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
  public Object getValidatedPhotos(final Continuation<? super List<PhotoEntity>> $completion) {
    final String _sql = "SELECT * FROM photos WHERE validation_status = 'PASSED' ORDER BY updated_at DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<PhotoEntity>>() {
      @Override
      @NonNull
      public List<PhotoEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfPhotoId = CursorUtil.getColumnIndexOrThrow(_cursor, "photo_id");
          final int _cursorIndexOfInstallationId = CursorUtil.getColumnIndexOrThrow(_cursor, "installation_id");
          final int _cursorIndexOfPhotoType = CursorUtil.getColumnIndexOrThrow(_cursor, "photo_type");
          final int _cursorIndexOfFilePath = CursorUtil.getColumnIndexOrThrow(_cursor, "file_path");
          final int _cursorIndexOfFileSizeBytes = CursorUtil.getColumnIndexOrThrow(_cursor, "file_size_bytes");
          final int _cursorIndexOfWidth = CursorUtil.getColumnIndexOrThrow(_cursor, "width");
          final int _cursorIndexOfHeight = CursorUtil.getColumnIndexOrThrow(_cursor, "height");
          final int _cursorIndexOfValidationStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "validation_status");
          final int _cursorIndexOfValidationConfidence = CursorUtil.getColumnIndexOrThrow(_cursor, "validation_confidence");
          final int _cursorIndexOfAiMetadata = CursorUtil.getColumnIndexOrThrow(_cursor, "ai_metadata");
          final int _cursorIndexOfManualOverride = CursorUtil.getColumnIndexOrThrow(_cursor, "manual_override");
          final int _cursorIndexOfOverrideReason = CursorUtil.getColumnIndexOrThrow(_cursor, "override_reason");
          final int _cursorIndexOfOverrideBy = CursorUtil.getColumnIndexOrThrow(_cursor, "override_by");
          final int _cursorIndexOfOverrideAt = CursorUtil.getColumnIndexOrThrow(_cursor, "override_at");
          final int _cursorIndexOfUploadStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "upload_status");
          final int _cursorIndexOfUploadAttempts = CursorUtil.getColumnIndexOrThrow(_cursor, "upload_attempts");
          final int _cursorIndexOfLastUploadAttempt = CursorUtil.getColumnIndexOrThrow(_cursor, "last_upload_attempt");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final int _cursorIndexOfChecksum = CursorUtil.getColumnIndexOrThrow(_cursor, "checksum");
          final List<PhotoEntity> _result = new ArrayList<PhotoEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final PhotoEntity _item;
            final long _tmpPhotoId;
            _tmpPhotoId = _cursor.getLong(_cursorIndexOfPhotoId);
            final long _tmpInstallationId;
            _tmpInstallationId = _cursor.getLong(_cursorIndexOfInstallationId);
            final PhotoType _tmpPhotoType;
            final String _tmp;
            if (_cursor.isNull(_cursorIndexOfPhotoType)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getString(_cursorIndexOfPhotoType);
            }
            _tmpPhotoType = __statusConverters.toPhotoType(_tmp);
            final String _tmpFilePath;
            if (_cursor.isNull(_cursorIndexOfFilePath)) {
              _tmpFilePath = null;
            } else {
              _tmpFilePath = _cursor.getString(_cursorIndexOfFilePath);
            }
            final long _tmpFileSizeBytes;
            _tmpFileSizeBytes = _cursor.getLong(_cursorIndexOfFileSizeBytes);
            final int _tmpWidth;
            _tmpWidth = _cursor.getInt(_cursorIndexOfWidth);
            final int _tmpHeight;
            _tmpHeight = _cursor.getInt(_cursorIndexOfHeight);
            final ValidationStatus _tmpValidationStatus;
            final String _tmp_1;
            if (_cursor.isNull(_cursorIndexOfValidationStatus)) {
              _tmp_1 = null;
            } else {
              _tmp_1 = _cursor.getString(_cursorIndexOfValidationStatus);
            }
            _tmpValidationStatus = __statusConverters.toValidationStatus(_tmp_1);
            final Float _tmpValidationConfidence;
            if (_cursor.isNull(_cursorIndexOfValidationConfidence)) {
              _tmpValidationConfidence = null;
            } else {
              _tmpValidationConfidence = _cursor.getFloat(_cursorIndexOfValidationConfidence);
            }
            final String _tmpAiMetadata;
            if (_cursor.isNull(_cursorIndexOfAiMetadata)) {
              _tmpAiMetadata = null;
            } else {
              _tmpAiMetadata = _cursor.getString(_cursorIndexOfAiMetadata);
            }
            final boolean _tmpManualOverride;
            final int _tmp_2;
            _tmp_2 = _cursor.getInt(_cursorIndexOfManualOverride);
            _tmpManualOverride = _tmp_2 != 0;
            final String _tmpOverrideReason;
            if (_cursor.isNull(_cursorIndexOfOverrideReason)) {
              _tmpOverrideReason = null;
            } else {
              _tmpOverrideReason = _cursor.getString(_cursorIndexOfOverrideReason);
            }
            final String _tmpOverrideBy;
            if (_cursor.isNull(_cursorIndexOfOverrideBy)) {
              _tmpOverrideBy = null;
            } else {
              _tmpOverrideBy = _cursor.getString(_cursorIndexOfOverrideBy);
            }
            final Date _tmpOverrideAt;
            final Long _tmp_3;
            if (_cursor.isNull(_cursorIndexOfOverrideAt)) {
              _tmp_3 = null;
            } else {
              _tmp_3 = _cursor.getLong(_cursorIndexOfOverrideAt);
            }
            _tmpOverrideAt = __dateConverters.fromTimestamp(_tmp_3);
            final UploadStatus _tmpUploadStatus;
            final String _tmp_4;
            if (_cursor.isNull(_cursorIndexOfUploadStatus)) {
              _tmp_4 = null;
            } else {
              _tmp_4 = _cursor.getString(_cursorIndexOfUploadStatus);
            }
            _tmpUploadStatus = __statusConverters.toUploadStatus(_tmp_4);
            final int _tmpUploadAttempts;
            _tmpUploadAttempts = _cursor.getInt(_cursorIndexOfUploadAttempts);
            final Date _tmpLastUploadAttempt;
            final Long _tmp_5;
            if (_cursor.isNull(_cursorIndexOfLastUploadAttempt)) {
              _tmp_5 = null;
            } else {
              _tmp_5 = _cursor.getLong(_cursorIndexOfLastUploadAttempt);
            }
            _tmpLastUploadAttempt = __dateConverters.fromTimestamp(_tmp_5);
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
            final String _tmpChecksum;
            if (_cursor.isNull(_cursorIndexOfChecksum)) {
              _tmpChecksum = null;
            } else {
              _tmpChecksum = _cursor.getString(_cursorIndexOfChecksum);
            }
            _item = new PhotoEntity(_tmpPhotoId,_tmpInstallationId,_tmpPhotoType,_tmpFilePath,_tmpFileSizeBytes,_tmpWidth,_tmpHeight,_tmpValidationStatus,_tmpValidationConfidence,_tmpAiMetadata,_tmpManualOverride,_tmpOverrideReason,_tmpOverrideBy,_tmpOverrideAt,_tmpUploadStatus,_tmpUploadAttempts,_tmpLastUploadAttempt,_tmpCreatedAt,_tmpUpdatedAt,_tmpChecksum);
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
  public Object getFailedValidationPhotos(
      final Continuation<? super List<PhotoEntity>> $completion) {
    final String _sql = "SELECT * FROM photos WHERE validation_status = 'FAILED' ORDER BY updated_at DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<PhotoEntity>>() {
      @Override
      @NonNull
      public List<PhotoEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfPhotoId = CursorUtil.getColumnIndexOrThrow(_cursor, "photo_id");
          final int _cursorIndexOfInstallationId = CursorUtil.getColumnIndexOrThrow(_cursor, "installation_id");
          final int _cursorIndexOfPhotoType = CursorUtil.getColumnIndexOrThrow(_cursor, "photo_type");
          final int _cursorIndexOfFilePath = CursorUtil.getColumnIndexOrThrow(_cursor, "file_path");
          final int _cursorIndexOfFileSizeBytes = CursorUtil.getColumnIndexOrThrow(_cursor, "file_size_bytes");
          final int _cursorIndexOfWidth = CursorUtil.getColumnIndexOrThrow(_cursor, "width");
          final int _cursorIndexOfHeight = CursorUtil.getColumnIndexOrThrow(_cursor, "height");
          final int _cursorIndexOfValidationStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "validation_status");
          final int _cursorIndexOfValidationConfidence = CursorUtil.getColumnIndexOrThrow(_cursor, "validation_confidence");
          final int _cursorIndexOfAiMetadata = CursorUtil.getColumnIndexOrThrow(_cursor, "ai_metadata");
          final int _cursorIndexOfManualOverride = CursorUtil.getColumnIndexOrThrow(_cursor, "manual_override");
          final int _cursorIndexOfOverrideReason = CursorUtil.getColumnIndexOrThrow(_cursor, "override_reason");
          final int _cursorIndexOfOverrideBy = CursorUtil.getColumnIndexOrThrow(_cursor, "override_by");
          final int _cursorIndexOfOverrideAt = CursorUtil.getColumnIndexOrThrow(_cursor, "override_at");
          final int _cursorIndexOfUploadStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "upload_status");
          final int _cursorIndexOfUploadAttempts = CursorUtil.getColumnIndexOrThrow(_cursor, "upload_attempts");
          final int _cursorIndexOfLastUploadAttempt = CursorUtil.getColumnIndexOrThrow(_cursor, "last_upload_attempt");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final int _cursorIndexOfChecksum = CursorUtil.getColumnIndexOrThrow(_cursor, "checksum");
          final List<PhotoEntity> _result = new ArrayList<PhotoEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final PhotoEntity _item;
            final long _tmpPhotoId;
            _tmpPhotoId = _cursor.getLong(_cursorIndexOfPhotoId);
            final long _tmpInstallationId;
            _tmpInstallationId = _cursor.getLong(_cursorIndexOfInstallationId);
            final PhotoType _tmpPhotoType;
            final String _tmp;
            if (_cursor.isNull(_cursorIndexOfPhotoType)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getString(_cursorIndexOfPhotoType);
            }
            _tmpPhotoType = __statusConverters.toPhotoType(_tmp);
            final String _tmpFilePath;
            if (_cursor.isNull(_cursorIndexOfFilePath)) {
              _tmpFilePath = null;
            } else {
              _tmpFilePath = _cursor.getString(_cursorIndexOfFilePath);
            }
            final long _tmpFileSizeBytes;
            _tmpFileSizeBytes = _cursor.getLong(_cursorIndexOfFileSizeBytes);
            final int _tmpWidth;
            _tmpWidth = _cursor.getInt(_cursorIndexOfWidth);
            final int _tmpHeight;
            _tmpHeight = _cursor.getInt(_cursorIndexOfHeight);
            final ValidationStatus _tmpValidationStatus;
            final String _tmp_1;
            if (_cursor.isNull(_cursorIndexOfValidationStatus)) {
              _tmp_1 = null;
            } else {
              _tmp_1 = _cursor.getString(_cursorIndexOfValidationStatus);
            }
            _tmpValidationStatus = __statusConverters.toValidationStatus(_tmp_1);
            final Float _tmpValidationConfidence;
            if (_cursor.isNull(_cursorIndexOfValidationConfidence)) {
              _tmpValidationConfidence = null;
            } else {
              _tmpValidationConfidence = _cursor.getFloat(_cursorIndexOfValidationConfidence);
            }
            final String _tmpAiMetadata;
            if (_cursor.isNull(_cursorIndexOfAiMetadata)) {
              _tmpAiMetadata = null;
            } else {
              _tmpAiMetadata = _cursor.getString(_cursorIndexOfAiMetadata);
            }
            final boolean _tmpManualOverride;
            final int _tmp_2;
            _tmp_2 = _cursor.getInt(_cursorIndexOfManualOverride);
            _tmpManualOverride = _tmp_2 != 0;
            final String _tmpOverrideReason;
            if (_cursor.isNull(_cursorIndexOfOverrideReason)) {
              _tmpOverrideReason = null;
            } else {
              _tmpOverrideReason = _cursor.getString(_cursorIndexOfOverrideReason);
            }
            final String _tmpOverrideBy;
            if (_cursor.isNull(_cursorIndexOfOverrideBy)) {
              _tmpOverrideBy = null;
            } else {
              _tmpOverrideBy = _cursor.getString(_cursorIndexOfOverrideBy);
            }
            final Date _tmpOverrideAt;
            final Long _tmp_3;
            if (_cursor.isNull(_cursorIndexOfOverrideAt)) {
              _tmp_3 = null;
            } else {
              _tmp_3 = _cursor.getLong(_cursorIndexOfOverrideAt);
            }
            _tmpOverrideAt = __dateConverters.fromTimestamp(_tmp_3);
            final UploadStatus _tmpUploadStatus;
            final String _tmp_4;
            if (_cursor.isNull(_cursorIndexOfUploadStatus)) {
              _tmp_4 = null;
            } else {
              _tmp_4 = _cursor.getString(_cursorIndexOfUploadStatus);
            }
            _tmpUploadStatus = __statusConverters.toUploadStatus(_tmp_4);
            final int _tmpUploadAttempts;
            _tmpUploadAttempts = _cursor.getInt(_cursorIndexOfUploadAttempts);
            final Date _tmpLastUploadAttempt;
            final Long _tmp_5;
            if (_cursor.isNull(_cursorIndexOfLastUploadAttempt)) {
              _tmp_5 = null;
            } else {
              _tmp_5 = _cursor.getLong(_cursorIndexOfLastUploadAttempt);
            }
            _tmpLastUploadAttempt = __dateConverters.fromTimestamp(_tmp_5);
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
            final String _tmpChecksum;
            if (_cursor.isNull(_cursorIndexOfChecksum)) {
              _tmpChecksum = null;
            } else {
              _tmpChecksum = _cursor.getString(_cursorIndexOfChecksum);
            }
            _item = new PhotoEntity(_tmpPhotoId,_tmpInstallationId,_tmpPhotoType,_tmpFilePath,_tmpFileSizeBytes,_tmpWidth,_tmpHeight,_tmpValidationStatus,_tmpValidationConfidence,_tmpAiMetadata,_tmpManualOverride,_tmpOverrideReason,_tmpOverrideBy,_tmpOverrideAt,_tmpUploadStatus,_tmpUploadAttempts,_tmpLastUploadAttempt,_tmpCreatedAt,_tmpUpdatedAt,_tmpChecksum);
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
  public Object getPhotosNeedingSync(final Continuation<? super List<PhotoEntity>> $completion) {
    final String _sql = "SELECT * FROM photos WHERE upload_status = 'PENDING' ORDER BY created_at ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<PhotoEntity>>() {
      @Override
      @NonNull
      public List<PhotoEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfPhotoId = CursorUtil.getColumnIndexOrThrow(_cursor, "photo_id");
          final int _cursorIndexOfInstallationId = CursorUtil.getColumnIndexOrThrow(_cursor, "installation_id");
          final int _cursorIndexOfPhotoType = CursorUtil.getColumnIndexOrThrow(_cursor, "photo_type");
          final int _cursorIndexOfFilePath = CursorUtil.getColumnIndexOrThrow(_cursor, "file_path");
          final int _cursorIndexOfFileSizeBytes = CursorUtil.getColumnIndexOrThrow(_cursor, "file_size_bytes");
          final int _cursorIndexOfWidth = CursorUtil.getColumnIndexOrThrow(_cursor, "width");
          final int _cursorIndexOfHeight = CursorUtil.getColumnIndexOrThrow(_cursor, "height");
          final int _cursorIndexOfValidationStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "validation_status");
          final int _cursorIndexOfValidationConfidence = CursorUtil.getColumnIndexOrThrow(_cursor, "validation_confidence");
          final int _cursorIndexOfAiMetadata = CursorUtil.getColumnIndexOrThrow(_cursor, "ai_metadata");
          final int _cursorIndexOfManualOverride = CursorUtil.getColumnIndexOrThrow(_cursor, "manual_override");
          final int _cursorIndexOfOverrideReason = CursorUtil.getColumnIndexOrThrow(_cursor, "override_reason");
          final int _cursorIndexOfOverrideBy = CursorUtil.getColumnIndexOrThrow(_cursor, "override_by");
          final int _cursorIndexOfOverrideAt = CursorUtil.getColumnIndexOrThrow(_cursor, "override_at");
          final int _cursorIndexOfUploadStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "upload_status");
          final int _cursorIndexOfUploadAttempts = CursorUtil.getColumnIndexOrThrow(_cursor, "upload_attempts");
          final int _cursorIndexOfLastUploadAttempt = CursorUtil.getColumnIndexOrThrow(_cursor, "last_upload_attempt");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final int _cursorIndexOfChecksum = CursorUtil.getColumnIndexOrThrow(_cursor, "checksum");
          final List<PhotoEntity> _result = new ArrayList<PhotoEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final PhotoEntity _item;
            final long _tmpPhotoId;
            _tmpPhotoId = _cursor.getLong(_cursorIndexOfPhotoId);
            final long _tmpInstallationId;
            _tmpInstallationId = _cursor.getLong(_cursorIndexOfInstallationId);
            final PhotoType _tmpPhotoType;
            final String _tmp;
            if (_cursor.isNull(_cursorIndexOfPhotoType)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getString(_cursorIndexOfPhotoType);
            }
            _tmpPhotoType = __statusConverters.toPhotoType(_tmp);
            final String _tmpFilePath;
            if (_cursor.isNull(_cursorIndexOfFilePath)) {
              _tmpFilePath = null;
            } else {
              _tmpFilePath = _cursor.getString(_cursorIndexOfFilePath);
            }
            final long _tmpFileSizeBytes;
            _tmpFileSizeBytes = _cursor.getLong(_cursorIndexOfFileSizeBytes);
            final int _tmpWidth;
            _tmpWidth = _cursor.getInt(_cursorIndexOfWidth);
            final int _tmpHeight;
            _tmpHeight = _cursor.getInt(_cursorIndexOfHeight);
            final ValidationStatus _tmpValidationStatus;
            final String _tmp_1;
            if (_cursor.isNull(_cursorIndexOfValidationStatus)) {
              _tmp_1 = null;
            } else {
              _tmp_1 = _cursor.getString(_cursorIndexOfValidationStatus);
            }
            _tmpValidationStatus = __statusConverters.toValidationStatus(_tmp_1);
            final Float _tmpValidationConfidence;
            if (_cursor.isNull(_cursorIndexOfValidationConfidence)) {
              _tmpValidationConfidence = null;
            } else {
              _tmpValidationConfidence = _cursor.getFloat(_cursorIndexOfValidationConfidence);
            }
            final String _tmpAiMetadata;
            if (_cursor.isNull(_cursorIndexOfAiMetadata)) {
              _tmpAiMetadata = null;
            } else {
              _tmpAiMetadata = _cursor.getString(_cursorIndexOfAiMetadata);
            }
            final boolean _tmpManualOverride;
            final int _tmp_2;
            _tmp_2 = _cursor.getInt(_cursorIndexOfManualOverride);
            _tmpManualOverride = _tmp_2 != 0;
            final String _tmpOverrideReason;
            if (_cursor.isNull(_cursorIndexOfOverrideReason)) {
              _tmpOverrideReason = null;
            } else {
              _tmpOverrideReason = _cursor.getString(_cursorIndexOfOverrideReason);
            }
            final String _tmpOverrideBy;
            if (_cursor.isNull(_cursorIndexOfOverrideBy)) {
              _tmpOverrideBy = null;
            } else {
              _tmpOverrideBy = _cursor.getString(_cursorIndexOfOverrideBy);
            }
            final Date _tmpOverrideAt;
            final Long _tmp_3;
            if (_cursor.isNull(_cursorIndexOfOverrideAt)) {
              _tmp_3 = null;
            } else {
              _tmp_3 = _cursor.getLong(_cursorIndexOfOverrideAt);
            }
            _tmpOverrideAt = __dateConverters.fromTimestamp(_tmp_3);
            final UploadStatus _tmpUploadStatus;
            final String _tmp_4;
            if (_cursor.isNull(_cursorIndexOfUploadStatus)) {
              _tmp_4 = null;
            } else {
              _tmp_4 = _cursor.getString(_cursorIndexOfUploadStatus);
            }
            _tmpUploadStatus = __statusConverters.toUploadStatus(_tmp_4);
            final int _tmpUploadAttempts;
            _tmpUploadAttempts = _cursor.getInt(_cursorIndexOfUploadAttempts);
            final Date _tmpLastUploadAttempt;
            final Long _tmp_5;
            if (_cursor.isNull(_cursorIndexOfLastUploadAttempt)) {
              _tmp_5 = null;
            } else {
              _tmp_5 = _cursor.getLong(_cursorIndexOfLastUploadAttempt);
            }
            _tmpLastUploadAttempt = __dateConverters.fromTimestamp(_tmp_5);
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
            final String _tmpChecksum;
            if (_cursor.isNull(_cursorIndexOfChecksum)) {
              _tmpChecksum = null;
            } else {
              _tmpChecksum = _cursor.getString(_cursorIndexOfChecksum);
            }
            _item = new PhotoEntity(_tmpPhotoId,_tmpInstallationId,_tmpPhotoType,_tmpFilePath,_tmpFileSizeBytes,_tmpWidth,_tmpHeight,_tmpValidationStatus,_tmpValidationConfidence,_tmpAiMetadata,_tmpManualOverride,_tmpOverrideReason,_tmpOverrideBy,_tmpOverrideAt,_tmpUploadStatus,_tmpUploadAttempts,_tmpLastUploadAttempt,_tmpCreatedAt,_tmpUpdatedAt,_tmpChecksum);
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
  public Object getPhotosByQualityRange(final float minScore, final float maxScore,
      final Continuation<? super List<PhotoEntity>> $completion) {
    final String _sql = "SELECT * FROM photos WHERE validation_confidence BETWEEN ? AND ? ORDER BY validation_confidence DESC";
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
          final int _cursorIndexOfPhotoId = CursorUtil.getColumnIndexOrThrow(_cursor, "photo_id");
          final int _cursorIndexOfInstallationId = CursorUtil.getColumnIndexOrThrow(_cursor, "installation_id");
          final int _cursorIndexOfPhotoType = CursorUtil.getColumnIndexOrThrow(_cursor, "photo_type");
          final int _cursorIndexOfFilePath = CursorUtil.getColumnIndexOrThrow(_cursor, "file_path");
          final int _cursorIndexOfFileSizeBytes = CursorUtil.getColumnIndexOrThrow(_cursor, "file_size_bytes");
          final int _cursorIndexOfWidth = CursorUtil.getColumnIndexOrThrow(_cursor, "width");
          final int _cursorIndexOfHeight = CursorUtil.getColumnIndexOrThrow(_cursor, "height");
          final int _cursorIndexOfValidationStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "validation_status");
          final int _cursorIndexOfValidationConfidence = CursorUtil.getColumnIndexOrThrow(_cursor, "validation_confidence");
          final int _cursorIndexOfAiMetadata = CursorUtil.getColumnIndexOrThrow(_cursor, "ai_metadata");
          final int _cursorIndexOfManualOverride = CursorUtil.getColumnIndexOrThrow(_cursor, "manual_override");
          final int _cursorIndexOfOverrideReason = CursorUtil.getColumnIndexOrThrow(_cursor, "override_reason");
          final int _cursorIndexOfOverrideBy = CursorUtil.getColumnIndexOrThrow(_cursor, "override_by");
          final int _cursorIndexOfOverrideAt = CursorUtil.getColumnIndexOrThrow(_cursor, "override_at");
          final int _cursorIndexOfUploadStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "upload_status");
          final int _cursorIndexOfUploadAttempts = CursorUtil.getColumnIndexOrThrow(_cursor, "upload_attempts");
          final int _cursorIndexOfLastUploadAttempt = CursorUtil.getColumnIndexOrThrow(_cursor, "last_upload_attempt");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final int _cursorIndexOfChecksum = CursorUtil.getColumnIndexOrThrow(_cursor, "checksum");
          final List<PhotoEntity> _result = new ArrayList<PhotoEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final PhotoEntity _item;
            final long _tmpPhotoId;
            _tmpPhotoId = _cursor.getLong(_cursorIndexOfPhotoId);
            final long _tmpInstallationId;
            _tmpInstallationId = _cursor.getLong(_cursorIndexOfInstallationId);
            final PhotoType _tmpPhotoType;
            final String _tmp;
            if (_cursor.isNull(_cursorIndexOfPhotoType)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getString(_cursorIndexOfPhotoType);
            }
            _tmpPhotoType = __statusConverters.toPhotoType(_tmp);
            final String _tmpFilePath;
            if (_cursor.isNull(_cursorIndexOfFilePath)) {
              _tmpFilePath = null;
            } else {
              _tmpFilePath = _cursor.getString(_cursorIndexOfFilePath);
            }
            final long _tmpFileSizeBytes;
            _tmpFileSizeBytes = _cursor.getLong(_cursorIndexOfFileSizeBytes);
            final int _tmpWidth;
            _tmpWidth = _cursor.getInt(_cursorIndexOfWidth);
            final int _tmpHeight;
            _tmpHeight = _cursor.getInt(_cursorIndexOfHeight);
            final ValidationStatus _tmpValidationStatus;
            final String _tmp_1;
            if (_cursor.isNull(_cursorIndexOfValidationStatus)) {
              _tmp_1 = null;
            } else {
              _tmp_1 = _cursor.getString(_cursorIndexOfValidationStatus);
            }
            _tmpValidationStatus = __statusConverters.toValidationStatus(_tmp_1);
            final Float _tmpValidationConfidence;
            if (_cursor.isNull(_cursorIndexOfValidationConfidence)) {
              _tmpValidationConfidence = null;
            } else {
              _tmpValidationConfidence = _cursor.getFloat(_cursorIndexOfValidationConfidence);
            }
            final String _tmpAiMetadata;
            if (_cursor.isNull(_cursorIndexOfAiMetadata)) {
              _tmpAiMetadata = null;
            } else {
              _tmpAiMetadata = _cursor.getString(_cursorIndexOfAiMetadata);
            }
            final boolean _tmpManualOverride;
            final int _tmp_2;
            _tmp_2 = _cursor.getInt(_cursorIndexOfManualOverride);
            _tmpManualOverride = _tmp_2 != 0;
            final String _tmpOverrideReason;
            if (_cursor.isNull(_cursorIndexOfOverrideReason)) {
              _tmpOverrideReason = null;
            } else {
              _tmpOverrideReason = _cursor.getString(_cursorIndexOfOverrideReason);
            }
            final String _tmpOverrideBy;
            if (_cursor.isNull(_cursorIndexOfOverrideBy)) {
              _tmpOverrideBy = null;
            } else {
              _tmpOverrideBy = _cursor.getString(_cursorIndexOfOverrideBy);
            }
            final Date _tmpOverrideAt;
            final Long _tmp_3;
            if (_cursor.isNull(_cursorIndexOfOverrideAt)) {
              _tmp_3 = null;
            } else {
              _tmp_3 = _cursor.getLong(_cursorIndexOfOverrideAt);
            }
            _tmpOverrideAt = __dateConverters.fromTimestamp(_tmp_3);
            final UploadStatus _tmpUploadStatus;
            final String _tmp_4;
            if (_cursor.isNull(_cursorIndexOfUploadStatus)) {
              _tmp_4 = null;
            } else {
              _tmp_4 = _cursor.getString(_cursorIndexOfUploadStatus);
            }
            _tmpUploadStatus = __statusConverters.toUploadStatus(_tmp_4);
            final int _tmpUploadAttempts;
            _tmpUploadAttempts = _cursor.getInt(_cursorIndexOfUploadAttempts);
            final Date _tmpLastUploadAttempt;
            final Long _tmp_5;
            if (_cursor.isNull(_cursorIndexOfLastUploadAttempt)) {
              _tmp_5 = null;
            } else {
              _tmp_5 = _cursor.getLong(_cursorIndexOfLastUploadAttempt);
            }
            _tmpLastUploadAttempt = __dateConverters.fromTimestamp(_tmp_5);
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
            final String _tmpChecksum;
            if (_cursor.isNull(_cursorIndexOfChecksum)) {
              _tmpChecksum = null;
            } else {
              _tmpChecksum = _cursor.getString(_cursorIndexOfChecksum);
            }
            _item = new PhotoEntity(_tmpPhotoId,_tmpInstallationId,_tmpPhotoType,_tmpFilePath,_tmpFileSizeBytes,_tmpWidth,_tmpHeight,_tmpValidationStatus,_tmpValidationConfidence,_tmpAiMetadata,_tmpManualOverride,_tmpOverrideReason,_tmpOverrideBy,_tmpOverrideAt,_tmpUploadStatus,_tmpUploadAttempts,_tmpLastUploadAttempt,_tmpCreatedAt,_tmpUpdatedAt,_tmpChecksum);
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
  public Object getLowQualityPhotos(final Continuation<? super List<PhotoEntity>> $completion) {
    final String _sql = "SELECT * FROM photos WHERE validation_confidence < 0.6 ORDER BY validation_confidence ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<PhotoEntity>>() {
      @Override
      @NonNull
      public List<PhotoEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfPhotoId = CursorUtil.getColumnIndexOrThrow(_cursor, "photo_id");
          final int _cursorIndexOfInstallationId = CursorUtil.getColumnIndexOrThrow(_cursor, "installation_id");
          final int _cursorIndexOfPhotoType = CursorUtil.getColumnIndexOrThrow(_cursor, "photo_type");
          final int _cursorIndexOfFilePath = CursorUtil.getColumnIndexOrThrow(_cursor, "file_path");
          final int _cursorIndexOfFileSizeBytes = CursorUtil.getColumnIndexOrThrow(_cursor, "file_size_bytes");
          final int _cursorIndexOfWidth = CursorUtil.getColumnIndexOrThrow(_cursor, "width");
          final int _cursorIndexOfHeight = CursorUtil.getColumnIndexOrThrow(_cursor, "height");
          final int _cursorIndexOfValidationStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "validation_status");
          final int _cursorIndexOfValidationConfidence = CursorUtil.getColumnIndexOrThrow(_cursor, "validation_confidence");
          final int _cursorIndexOfAiMetadata = CursorUtil.getColumnIndexOrThrow(_cursor, "ai_metadata");
          final int _cursorIndexOfManualOverride = CursorUtil.getColumnIndexOrThrow(_cursor, "manual_override");
          final int _cursorIndexOfOverrideReason = CursorUtil.getColumnIndexOrThrow(_cursor, "override_reason");
          final int _cursorIndexOfOverrideBy = CursorUtil.getColumnIndexOrThrow(_cursor, "override_by");
          final int _cursorIndexOfOverrideAt = CursorUtil.getColumnIndexOrThrow(_cursor, "override_at");
          final int _cursorIndexOfUploadStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "upload_status");
          final int _cursorIndexOfUploadAttempts = CursorUtil.getColumnIndexOrThrow(_cursor, "upload_attempts");
          final int _cursorIndexOfLastUploadAttempt = CursorUtil.getColumnIndexOrThrow(_cursor, "last_upload_attempt");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final int _cursorIndexOfChecksum = CursorUtil.getColumnIndexOrThrow(_cursor, "checksum");
          final List<PhotoEntity> _result = new ArrayList<PhotoEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final PhotoEntity _item;
            final long _tmpPhotoId;
            _tmpPhotoId = _cursor.getLong(_cursorIndexOfPhotoId);
            final long _tmpInstallationId;
            _tmpInstallationId = _cursor.getLong(_cursorIndexOfInstallationId);
            final PhotoType _tmpPhotoType;
            final String _tmp;
            if (_cursor.isNull(_cursorIndexOfPhotoType)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getString(_cursorIndexOfPhotoType);
            }
            _tmpPhotoType = __statusConverters.toPhotoType(_tmp);
            final String _tmpFilePath;
            if (_cursor.isNull(_cursorIndexOfFilePath)) {
              _tmpFilePath = null;
            } else {
              _tmpFilePath = _cursor.getString(_cursorIndexOfFilePath);
            }
            final long _tmpFileSizeBytes;
            _tmpFileSizeBytes = _cursor.getLong(_cursorIndexOfFileSizeBytes);
            final int _tmpWidth;
            _tmpWidth = _cursor.getInt(_cursorIndexOfWidth);
            final int _tmpHeight;
            _tmpHeight = _cursor.getInt(_cursorIndexOfHeight);
            final ValidationStatus _tmpValidationStatus;
            final String _tmp_1;
            if (_cursor.isNull(_cursorIndexOfValidationStatus)) {
              _tmp_1 = null;
            } else {
              _tmp_1 = _cursor.getString(_cursorIndexOfValidationStatus);
            }
            _tmpValidationStatus = __statusConverters.toValidationStatus(_tmp_1);
            final Float _tmpValidationConfidence;
            if (_cursor.isNull(_cursorIndexOfValidationConfidence)) {
              _tmpValidationConfidence = null;
            } else {
              _tmpValidationConfidence = _cursor.getFloat(_cursorIndexOfValidationConfidence);
            }
            final String _tmpAiMetadata;
            if (_cursor.isNull(_cursorIndexOfAiMetadata)) {
              _tmpAiMetadata = null;
            } else {
              _tmpAiMetadata = _cursor.getString(_cursorIndexOfAiMetadata);
            }
            final boolean _tmpManualOverride;
            final int _tmp_2;
            _tmp_2 = _cursor.getInt(_cursorIndexOfManualOverride);
            _tmpManualOverride = _tmp_2 != 0;
            final String _tmpOverrideReason;
            if (_cursor.isNull(_cursorIndexOfOverrideReason)) {
              _tmpOverrideReason = null;
            } else {
              _tmpOverrideReason = _cursor.getString(_cursorIndexOfOverrideReason);
            }
            final String _tmpOverrideBy;
            if (_cursor.isNull(_cursorIndexOfOverrideBy)) {
              _tmpOverrideBy = null;
            } else {
              _tmpOverrideBy = _cursor.getString(_cursorIndexOfOverrideBy);
            }
            final Date _tmpOverrideAt;
            final Long _tmp_3;
            if (_cursor.isNull(_cursorIndexOfOverrideAt)) {
              _tmp_3 = null;
            } else {
              _tmp_3 = _cursor.getLong(_cursorIndexOfOverrideAt);
            }
            _tmpOverrideAt = __dateConverters.fromTimestamp(_tmp_3);
            final UploadStatus _tmpUploadStatus;
            final String _tmp_4;
            if (_cursor.isNull(_cursorIndexOfUploadStatus)) {
              _tmp_4 = null;
            } else {
              _tmp_4 = _cursor.getString(_cursorIndexOfUploadStatus);
            }
            _tmpUploadStatus = __statusConverters.toUploadStatus(_tmp_4);
            final int _tmpUploadAttempts;
            _tmpUploadAttempts = _cursor.getInt(_cursorIndexOfUploadAttempts);
            final Date _tmpLastUploadAttempt;
            final Long _tmp_5;
            if (_cursor.isNull(_cursorIndexOfLastUploadAttempt)) {
              _tmp_5 = null;
            } else {
              _tmp_5 = _cursor.getLong(_cursorIndexOfLastUploadAttempt);
            }
            _tmpLastUploadAttempt = __dateConverters.fromTimestamp(_tmp_5);
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
            final String _tmpChecksum;
            if (_cursor.isNull(_cursorIndexOfChecksum)) {
              _tmpChecksum = null;
            } else {
              _tmpChecksum = _cursor.getString(_cursorIndexOfChecksum);
            }
            _item = new PhotoEntity(_tmpPhotoId,_tmpInstallationId,_tmpPhotoType,_tmpFilePath,_tmpFileSizeBytes,_tmpWidth,_tmpHeight,_tmpValidationStatus,_tmpValidationConfidence,_tmpAiMetadata,_tmpManualOverride,_tmpOverrideReason,_tmpOverrideBy,_tmpOverrideAt,_tmpUploadStatus,_tmpUploadAttempts,_tmpLastUploadAttempt,_tmpCreatedAt,_tmpUpdatedAt,_tmpChecksum);
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
  public Object getPhotoCountByValidationStatus(final String status,
      final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT COUNT(*) FROM photos WHERE validation_status = ?";
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
    final String _sql = "SELECT * FROM photos WHERE created_at BETWEEN ? AND ? ORDER BY created_at DESC";
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
          final int _cursorIndexOfPhotoId = CursorUtil.getColumnIndexOrThrow(_cursor, "photo_id");
          final int _cursorIndexOfInstallationId = CursorUtil.getColumnIndexOrThrow(_cursor, "installation_id");
          final int _cursorIndexOfPhotoType = CursorUtil.getColumnIndexOrThrow(_cursor, "photo_type");
          final int _cursorIndexOfFilePath = CursorUtil.getColumnIndexOrThrow(_cursor, "file_path");
          final int _cursorIndexOfFileSizeBytes = CursorUtil.getColumnIndexOrThrow(_cursor, "file_size_bytes");
          final int _cursorIndexOfWidth = CursorUtil.getColumnIndexOrThrow(_cursor, "width");
          final int _cursorIndexOfHeight = CursorUtil.getColumnIndexOrThrow(_cursor, "height");
          final int _cursorIndexOfValidationStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "validation_status");
          final int _cursorIndexOfValidationConfidence = CursorUtil.getColumnIndexOrThrow(_cursor, "validation_confidence");
          final int _cursorIndexOfAiMetadata = CursorUtil.getColumnIndexOrThrow(_cursor, "ai_metadata");
          final int _cursorIndexOfManualOverride = CursorUtil.getColumnIndexOrThrow(_cursor, "manual_override");
          final int _cursorIndexOfOverrideReason = CursorUtil.getColumnIndexOrThrow(_cursor, "override_reason");
          final int _cursorIndexOfOverrideBy = CursorUtil.getColumnIndexOrThrow(_cursor, "override_by");
          final int _cursorIndexOfOverrideAt = CursorUtil.getColumnIndexOrThrow(_cursor, "override_at");
          final int _cursorIndexOfUploadStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "upload_status");
          final int _cursorIndexOfUploadAttempts = CursorUtil.getColumnIndexOrThrow(_cursor, "upload_attempts");
          final int _cursorIndexOfLastUploadAttempt = CursorUtil.getColumnIndexOrThrow(_cursor, "last_upload_attempt");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final int _cursorIndexOfChecksum = CursorUtil.getColumnIndexOrThrow(_cursor, "checksum");
          final List<PhotoEntity> _result = new ArrayList<PhotoEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final PhotoEntity _item;
            final long _tmpPhotoId;
            _tmpPhotoId = _cursor.getLong(_cursorIndexOfPhotoId);
            final long _tmpInstallationId;
            _tmpInstallationId = _cursor.getLong(_cursorIndexOfInstallationId);
            final PhotoType _tmpPhotoType;
            final String _tmp;
            if (_cursor.isNull(_cursorIndexOfPhotoType)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getString(_cursorIndexOfPhotoType);
            }
            _tmpPhotoType = __statusConverters.toPhotoType(_tmp);
            final String _tmpFilePath;
            if (_cursor.isNull(_cursorIndexOfFilePath)) {
              _tmpFilePath = null;
            } else {
              _tmpFilePath = _cursor.getString(_cursorIndexOfFilePath);
            }
            final long _tmpFileSizeBytes;
            _tmpFileSizeBytes = _cursor.getLong(_cursorIndexOfFileSizeBytes);
            final int _tmpWidth;
            _tmpWidth = _cursor.getInt(_cursorIndexOfWidth);
            final int _tmpHeight;
            _tmpHeight = _cursor.getInt(_cursorIndexOfHeight);
            final ValidationStatus _tmpValidationStatus;
            final String _tmp_1;
            if (_cursor.isNull(_cursorIndexOfValidationStatus)) {
              _tmp_1 = null;
            } else {
              _tmp_1 = _cursor.getString(_cursorIndexOfValidationStatus);
            }
            _tmpValidationStatus = __statusConverters.toValidationStatus(_tmp_1);
            final Float _tmpValidationConfidence;
            if (_cursor.isNull(_cursorIndexOfValidationConfidence)) {
              _tmpValidationConfidence = null;
            } else {
              _tmpValidationConfidence = _cursor.getFloat(_cursorIndexOfValidationConfidence);
            }
            final String _tmpAiMetadata;
            if (_cursor.isNull(_cursorIndexOfAiMetadata)) {
              _tmpAiMetadata = null;
            } else {
              _tmpAiMetadata = _cursor.getString(_cursorIndexOfAiMetadata);
            }
            final boolean _tmpManualOverride;
            final int _tmp_2;
            _tmp_2 = _cursor.getInt(_cursorIndexOfManualOverride);
            _tmpManualOverride = _tmp_2 != 0;
            final String _tmpOverrideReason;
            if (_cursor.isNull(_cursorIndexOfOverrideReason)) {
              _tmpOverrideReason = null;
            } else {
              _tmpOverrideReason = _cursor.getString(_cursorIndexOfOverrideReason);
            }
            final String _tmpOverrideBy;
            if (_cursor.isNull(_cursorIndexOfOverrideBy)) {
              _tmpOverrideBy = null;
            } else {
              _tmpOverrideBy = _cursor.getString(_cursorIndexOfOverrideBy);
            }
            final Date _tmpOverrideAt;
            final Long _tmp_3;
            if (_cursor.isNull(_cursorIndexOfOverrideAt)) {
              _tmp_3 = null;
            } else {
              _tmp_3 = _cursor.getLong(_cursorIndexOfOverrideAt);
            }
            _tmpOverrideAt = __dateConverters.fromTimestamp(_tmp_3);
            final UploadStatus _tmpUploadStatus;
            final String _tmp_4;
            if (_cursor.isNull(_cursorIndexOfUploadStatus)) {
              _tmp_4 = null;
            } else {
              _tmp_4 = _cursor.getString(_cursorIndexOfUploadStatus);
            }
            _tmpUploadStatus = __statusConverters.toUploadStatus(_tmp_4);
            final int _tmpUploadAttempts;
            _tmpUploadAttempts = _cursor.getInt(_cursorIndexOfUploadAttempts);
            final Date _tmpLastUploadAttempt;
            final Long _tmp_5;
            if (_cursor.isNull(_cursorIndexOfLastUploadAttempt)) {
              _tmp_5 = null;
            } else {
              _tmp_5 = _cursor.getLong(_cursorIndexOfLastUploadAttempt);
            }
            _tmpLastUploadAttempt = __dateConverters.fromTimestamp(_tmp_5);
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
            final String _tmpChecksum;
            if (_cursor.isNull(_cursorIndexOfChecksum)) {
              _tmpChecksum = null;
            } else {
              _tmpChecksum = _cursor.getString(_cursorIndexOfChecksum);
            }
            _item = new PhotoEntity(_tmpPhotoId,_tmpInstallationId,_tmpPhotoType,_tmpFilePath,_tmpFileSizeBytes,_tmpWidth,_tmpHeight,_tmpValidationStatus,_tmpValidationConfidence,_tmpAiMetadata,_tmpManualOverride,_tmpOverrideReason,_tmpOverrideBy,_tmpOverrideAt,_tmpUploadStatus,_tmpUploadAttempts,_tmpLastUploadAttempt,_tmpCreatedAt,_tmpUpdatedAt,_tmpChecksum);
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
  public Object getPhotosByTechnician(final String technicianId,
      final Continuation<? super List<PhotoEntity>> $completion) {
    final String _sql = "SELECT p.* FROM photos p INNER JOIN installations i ON p.installation_id = i.installation_id WHERE i.technician_id = ? ORDER BY p.created_at DESC";
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
          final int _cursorIndexOfPhotoId = CursorUtil.getColumnIndexOrThrow(_cursor, "photo_id");
          final int _cursorIndexOfInstallationId = CursorUtil.getColumnIndexOrThrow(_cursor, "installation_id");
          final int _cursorIndexOfPhotoType = CursorUtil.getColumnIndexOrThrow(_cursor, "photo_type");
          final int _cursorIndexOfFilePath = CursorUtil.getColumnIndexOrThrow(_cursor, "file_path");
          final int _cursorIndexOfFileSizeBytes = CursorUtil.getColumnIndexOrThrow(_cursor, "file_size_bytes");
          final int _cursorIndexOfWidth = CursorUtil.getColumnIndexOrThrow(_cursor, "width");
          final int _cursorIndexOfHeight = CursorUtil.getColumnIndexOrThrow(_cursor, "height");
          final int _cursorIndexOfValidationStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "validation_status");
          final int _cursorIndexOfValidationConfidence = CursorUtil.getColumnIndexOrThrow(_cursor, "validation_confidence");
          final int _cursorIndexOfAiMetadata = CursorUtil.getColumnIndexOrThrow(_cursor, "ai_metadata");
          final int _cursorIndexOfManualOverride = CursorUtil.getColumnIndexOrThrow(_cursor, "manual_override");
          final int _cursorIndexOfOverrideReason = CursorUtil.getColumnIndexOrThrow(_cursor, "override_reason");
          final int _cursorIndexOfOverrideBy = CursorUtil.getColumnIndexOrThrow(_cursor, "override_by");
          final int _cursorIndexOfOverrideAt = CursorUtil.getColumnIndexOrThrow(_cursor, "override_at");
          final int _cursorIndexOfUploadStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "upload_status");
          final int _cursorIndexOfUploadAttempts = CursorUtil.getColumnIndexOrThrow(_cursor, "upload_attempts");
          final int _cursorIndexOfLastUploadAttempt = CursorUtil.getColumnIndexOrThrow(_cursor, "last_upload_attempt");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final int _cursorIndexOfChecksum = CursorUtil.getColumnIndexOrThrow(_cursor, "checksum");
          final List<PhotoEntity> _result = new ArrayList<PhotoEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final PhotoEntity _item;
            final long _tmpPhotoId;
            _tmpPhotoId = _cursor.getLong(_cursorIndexOfPhotoId);
            final long _tmpInstallationId;
            _tmpInstallationId = _cursor.getLong(_cursorIndexOfInstallationId);
            final PhotoType _tmpPhotoType;
            final String _tmp;
            if (_cursor.isNull(_cursorIndexOfPhotoType)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getString(_cursorIndexOfPhotoType);
            }
            _tmpPhotoType = __statusConverters.toPhotoType(_tmp);
            final String _tmpFilePath;
            if (_cursor.isNull(_cursorIndexOfFilePath)) {
              _tmpFilePath = null;
            } else {
              _tmpFilePath = _cursor.getString(_cursorIndexOfFilePath);
            }
            final long _tmpFileSizeBytes;
            _tmpFileSizeBytes = _cursor.getLong(_cursorIndexOfFileSizeBytes);
            final int _tmpWidth;
            _tmpWidth = _cursor.getInt(_cursorIndexOfWidth);
            final int _tmpHeight;
            _tmpHeight = _cursor.getInt(_cursorIndexOfHeight);
            final ValidationStatus _tmpValidationStatus;
            final String _tmp_1;
            if (_cursor.isNull(_cursorIndexOfValidationStatus)) {
              _tmp_1 = null;
            } else {
              _tmp_1 = _cursor.getString(_cursorIndexOfValidationStatus);
            }
            _tmpValidationStatus = __statusConverters.toValidationStatus(_tmp_1);
            final Float _tmpValidationConfidence;
            if (_cursor.isNull(_cursorIndexOfValidationConfidence)) {
              _tmpValidationConfidence = null;
            } else {
              _tmpValidationConfidence = _cursor.getFloat(_cursorIndexOfValidationConfidence);
            }
            final String _tmpAiMetadata;
            if (_cursor.isNull(_cursorIndexOfAiMetadata)) {
              _tmpAiMetadata = null;
            } else {
              _tmpAiMetadata = _cursor.getString(_cursorIndexOfAiMetadata);
            }
            final boolean _tmpManualOverride;
            final int _tmp_2;
            _tmp_2 = _cursor.getInt(_cursorIndexOfManualOverride);
            _tmpManualOverride = _tmp_2 != 0;
            final String _tmpOverrideReason;
            if (_cursor.isNull(_cursorIndexOfOverrideReason)) {
              _tmpOverrideReason = null;
            } else {
              _tmpOverrideReason = _cursor.getString(_cursorIndexOfOverrideReason);
            }
            final String _tmpOverrideBy;
            if (_cursor.isNull(_cursorIndexOfOverrideBy)) {
              _tmpOverrideBy = null;
            } else {
              _tmpOverrideBy = _cursor.getString(_cursorIndexOfOverrideBy);
            }
            final Date _tmpOverrideAt;
            final Long _tmp_3;
            if (_cursor.isNull(_cursorIndexOfOverrideAt)) {
              _tmp_3 = null;
            } else {
              _tmp_3 = _cursor.getLong(_cursorIndexOfOverrideAt);
            }
            _tmpOverrideAt = __dateConverters.fromTimestamp(_tmp_3);
            final UploadStatus _tmpUploadStatus;
            final String _tmp_4;
            if (_cursor.isNull(_cursorIndexOfUploadStatus)) {
              _tmp_4 = null;
            } else {
              _tmp_4 = _cursor.getString(_cursorIndexOfUploadStatus);
            }
            _tmpUploadStatus = __statusConverters.toUploadStatus(_tmp_4);
            final int _tmpUploadAttempts;
            _tmpUploadAttempts = _cursor.getInt(_cursorIndexOfUploadAttempts);
            final Date _tmpLastUploadAttempt;
            final Long _tmp_5;
            if (_cursor.isNull(_cursorIndexOfLastUploadAttempt)) {
              _tmp_5 = null;
            } else {
              _tmp_5 = _cursor.getLong(_cursorIndexOfLastUploadAttempt);
            }
            _tmpLastUploadAttempt = __dateConverters.fromTimestamp(_tmp_5);
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
            final String _tmpChecksum;
            if (_cursor.isNull(_cursorIndexOfChecksum)) {
              _tmpChecksum = null;
            } else {
              _tmpChecksum = _cursor.getString(_cursorIndexOfChecksum);
            }
            _item = new PhotoEntity(_tmpPhotoId,_tmpInstallationId,_tmpPhotoType,_tmpFilePath,_tmpFileSizeBytes,_tmpWidth,_tmpHeight,_tmpValidationStatus,_tmpValidationConfidence,_tmpAiMetadata,_tmpManualOverride,_tmpOverrideReason,_tmpOverrideBy,_tmpOverrideAt,_tmpUploadStatus,_tmpUploadAttempts,_tmpLastUploadAttempt,_tmpCreatedAt,_tmpUpdatedAt,_tmpChecksum);
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
  public Object getAveragePhotoQuality(final Continuation<? super Float> $completion) {
    final String _sql = "SELECT AVG(validation_confidence) FROM photos WHERE validation_confidence IS NOT NULL";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Float>() {
      @Override
      @Nullable
      public Float call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Float _result;
          if (_cursor.moveToFirst()) {
            final Float _tmp;
            if (_cursor.isNull(0)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getFloat(0);
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
  public Object getPassedValidationCount(final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT COUNT(*) FROM photos WHERE validation_status = 'PASSED'";
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
  public Object searchPhotosByDetectedObject(final String objectName,
      final Continuation<? super List<PhotoEntity>> $completion) {
    final String _sql = "SELECT * FROM photos WHERE ai_metadata LIKE '%' || ? || '%' ORDER BY created_at DESC";
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
          final int _cursorIndexOfPhotoId = CursorUtil.getColumnIndexOrThrow(_cursor, "photo_id");
          final int _cursorIndexOfInstallationId = CursorUtil.getColumnIndexOrThrow(_cursor, "installation_id");
          final int _cursorIndexOfPhotoType = CursorUtil.getColumnIndexOrThrow(_cursor, "photo_type");
          final int _cursorIndexOfFilePath = CursorUtil.getColumnIndexOrThrow(_cursor, "file_path");
          final int _cursorIndexOfFileSizeBytes = CursorUtil.getColumnIndexOrThrow(_cursor, "file_size_bytes");
          final int _cursorIndexOfWidth = CursorUtil.getColumnIndexOrThrow(_cursor, "width");
          final int _cursorIndexOfHeight = CursorUtil.getColumnIndexOrThrow(_cursor, "height");
          final int _cursorIndexOfValidationStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "validation_status");
          final int _cursorIndexOfValidationConfidence = CursorUtil.getColumnIndexOrThrow(_cursor, "validation_confidence");
          final int _cursorIndexOfAiMetadata = CursorUtil.getColumnIndexOrThrow(_cursor, "ai_metadata");
          final int _cursorIndexOfManualOverride = CursorUtil.getColumnIndexOrThrow(_cursor, "manual_override");
          final int _cursorIndexOfOverrideReason = CursorUtil.getColumnIndexOrThrow(_cursor, "override_reason");
          final int _cursorIndexOfOverrideBy = CursorUtil.getColumnIndexOrThrow(_cursor, "override_by");
          final int _cursorIndexOfOverrideAt = CursorUtil.getColumnIndexOrThrow(_cursor, "override_at");
          final int _cursorIndexOfUploadStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "upload_status");
          final int _cursorIndexOfUploadAttempts = CursorUtil.getColumnIndexOrThrow(_cursor, "upload_attempts");
          final int _cursorIndexOfLastUploadAttempt = CursorUtil.getColumnIndexOrThrow(_cursor, "last_upload_attempt");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final int _cursorIndexOfChecksum = CursorUtil.getColumnIndexOrThrow(_cursor, "checksum");
          final List<PhotoEntity> _result = new ArrayList<PhotoEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final PhotoEntity _item;
            final long _tmpPhotoId;
            _tmpPhotoId = _cursor.getLong(_cursorIndexOfPhotoId);
            final long _tmpInstallationId;
            _tmpInstallationId = _cursor.getLong(_cursorIndexOfInstallationId);
            final PhotoType _tmpPhotoType;
            final String _tmp;
            if (_cursor.isNull(_cursorIndexOfPhotoType)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getString(_cursorIndexOfPhotoType);
            }
            _tmpPhotoType = __statusConverters.toPhotoType(_tmp);
            final String _tmpFilePath;
            if (_cursor.isNull(_cursorIndexOfFilePath)) {
              _tmpFilePath = null;
            } else {
              _tmpFilePath = _cursor.getString(_cursorIndexOfFilePath);
            }
            final long _tmpFileSizeBytes;
            _tmpFileSizeBytes = _cursor.getLong(_cursorIndexOfFileSizeBytes);
            final int _tmpWidth;
            _tmpWidth = _cursor.getInt(_cursorIndexOfWidth);
            final int _tmpHeight;
            _tmpHeight = _cursor.getInt(_cursorIndexOfHeight);
            final ValidationStatus _tmpValidationStatus;
            final String _tmp_1;
            if (_cursor.isNull(_cursorIndexOfValidationStatus)) {
              _tmp_1 = null;
            } else {
              _tmp_1 = _cursor.getString(_cursorIndexOfValidationStatus);
            }
            _tmpValidationStatus = __statusConverters.toValidationStatus(_tmp_1);
            final Float _tmpValidationConfidence;
            if (_cursor.isNull(_cursorIndexOfValidationConfidence)) {
              _tmpValidationConfidence = null;
            } else {
              _tmpValidationConfidence = _cursor.getFloat(_cursorIndexOfValidationConfidence);
            }
            final String _tmpAiMetadata;
            if (_cursor.isNull(_cursorIndexOfAiMetadata)) {
              _tmpAiMetadata = null;
            } else {
              _tmpAiMetadata = _cursor.getString(_cursorIndexOfAiMetadata);
            }
            final boolean _tmpManualOverride;
            final int _tmp_2;
            _tmp_2 = _cursor.getInt(_cursorIndexOfManualOverride);
            _tmpManualOverride = _tmp_2 != 0;
            final String _tmpOverrideReason;
            if (_cursor.isNull(_cursorIndexOfOverrideReason)) {
              _tmpOverrideReason = null;
            } else {
              _tmpOverrideReason = _cursor.getString(_cursorIndexOfOverrideReason);
            }
            final String _tmpOverrideBy;
            if (_cursor.isNull(_cursorIndexOfOverrideBy)) {
              _tmpOverrideBy = null;
            } else {
              _tmpOverrideBy = _cursor.getString(_cursorIndexOfOverrideBy);
            }
            final Date _tmpOverrideAt;
            final Long _tmp_3;
            if (_cursor.isNull(_cursorIndexOfOverrideAt)) {
              _tmp_3 = null;
            } else {
              _tmp_3 = _cursor.getLong(_cursorIndexOfOverrideAt);
            }
            _tmpOverrideAt = __dateConverters.fromTimestamp(_tmp_3);
            final UploadStatus _tmpUploadStatus;
            final String _tmp_4;
            if (_cursor.isNull(_cursorIndexOfUploadStatus)) {
              _tmp_4 = null;
            } else {
              _tmp_4 = _cursor.getString(_cursorIndexOfUploadStatus);
            }
            _tmpUploadStatus = __statusConverters.toUploadStatus(_tmp_4);
            final int _tmpUploadAttempts;
            _tmpUploadAttempts = _cursor.getInt(_cursorIndexOfUploadAttempts);
            final Date _tmpLastUploadAttempt;
            final Long _tmp_5;
            if (_cursor.isNull(_cursorIndexOfLastUploadAttempt)) {
              _tmp_5 = null;
            } else {
              _tmp_5 = _cursor.getLong(_cursorIndexOfLastUploadAttempt);
            }
            _tmpLastUploadAttempt = __dateConverters.fromTimestamp(_tmp_5);
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
            final String _tmpChecksum;
            if (_cursor.isNull(_cursorIndexOfChecksum)) {
              _tmpChecksum = null;
            } else {
              _tmpChecksum = _cursor.getString(_cursorIndexOfChecksum);
            }
            _item = new PhotoEntity(_tmpPhotoId,_tmpInstallationId,_tmpPhotoType,_tmpFilePath,_tmpFileSizeBytes,_tmpWidth,_tmpHeight,_tmpValidationStatus,_tmpValidationConfidence,_tmpAiMetadata,_tmpManualOverride,_tmpOverrideReason,_tmpOverrideBy,_tmpOverrideAt,_tmpUploadStatus,_tmpUploadAttempts,_tmpLastUploadAttempt,_tmpCreatedAt,_tmpUpdatedAt,_tmpChecksum);
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
  public Object searchPhotosByExtractedText(final String searchText,
      final Continuation<? super List<PhotoEntity>> $completion) {
    final String _sql = "SELECT * FROM photos WHERE ai_metadata LIKE '%' || ? || '%' ORDER BY created_at DESC";
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
          final int _cursorIndexOfPhotoId = CursorUtil.getColumnIndexOrThrow(_cursor, "photo_id");
          final int _cursorIndexOfInstallationId = CursorUtil.getColumnIndexOrThrow(_cursor, "installation_id");
          final int _cursorIndexOfPhotoType = CursorUtil.getColumnIndexOrThrow(_cursor, "photo_type");
          final int _cursorIndexOfFilePath = CursorUtil.getColumnIndexOrThrow(_cursor, "file_path");
          final int _cursorIndexOfFileSizeBytes = CursorUtil.getColumnIndexOrThrow(_cursor, "file_size_bytes");
          final int _cursorIndexOfWidth = CursorUtil.getColumnIndexOrThrow(_cursor, "width");
          final int _cursorIndexOfHeight = CursorUtil.getColumnIndexOrThrow(_cursor, "height");
          final int _cursorIndexOfValidationStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "validation_status");
          final int _cursorIndexOfValidationConfidence = CursorUtil.getColumnIndexOrThrow(_cursor, "validation_confidence");
          final int _cursorIndexOfAiMetadata = CursorUtil.getColumnIndexOrThrow(_cursor, "ai_metadata");
          final int _cursorIndexOfManualOverride = CursorUtil.getColumnIndexOrThrow(_cursor, "manual_override");
          final int _cursorIndexOfOverrideReason = CursorUtil.getColumnIndexOrThrow(_cursor, "override_reason");
          final int _cursorIndexOfOverrideBy = CursorUtil.getColumnIndexOrThrow(_cursor, "override_by");
          final int _cursorIndexOfOverrideAt = CursorUtil.getColumnIndexOrThrow(_cursor, "override_at");
          final int _cursorIndexOfUploadStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "upload_status");
          final int _cursorIndexOfUploadAttempts = CursorUtil.getColumnIndexOrThrow(_cursor, "upload_attempts");
          final int _cursorIndexOfLastUploadAttempt = CursorUtil.getColumnIndexOrThrow(_cursor, "last_upload_attempt");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final int _cursorIndexOfChecksum = CursorUtil.getColumnIndexOrThrow(_cursor, "checksum");
          final List<PhotoEntity> _result = new ArrayList<PhotoEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final PhotoEntity _item;
            final long _tmpPhotoId;
            _tmpPhotoId = _cursor.getLong(_cursorIndexOfPhotoId);
            final long _tmpInstallationId;
            _tmpInstallationId = _cursor.getLong(_cursorIndexOfInstallationId);
            final PhotoType _tmpPhotoType;
            final String _tmp;
            if (_cursor.isNull(_cursorIndexOfPhotoType)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getString(_cursorIndexOfPhotoType);
            }
            _tmpPhotoType = __statusConverters.toPhotoType(_tmp);
            final String _tmpFilePath;
            if (_cursor.isNull(_cursorIndexOfFilePath)) {
              _tmpFilePath = null;
            } else {
              _tmpFilePath = _cursor.getString(_cursorIndexOfFilePath);
            }
            final long _tmpFileSizeBytes;
            _tmpFileSizeBytes = _cursor.getLong(_cursorIndexOfFileSizeBytes);
            final int _tmpWidth;
            _tmpWidth = _cursor.getInt(_cursorIndexOfWidth);
            final int _tmpHeight;
            _tmpHeight = _cursor.getInt(_cursorIndexOfHeight);
            final ValidationStatus _tmpValidationStatus;
            final String _tmp_1;
            if (_cursor.isNull(_cursorIndexOfValidationStatus)) {
              _tmp_1 = null;
            } else {
              _tmp_1 = _cursor.getString(_cursorIndexOfValidationStatus);
            }
            _tmpValidationStatus = __statusConverters.toValidationStatus(_tmp_1);
            final Float _tmpValidationConfidence;
            if (_cursor.isNull(_cursorIndexOfValidationConfidence)) {
              _tmpValidationConfidence = null;
            } else {
              _tmpValidationConfidence = _cursor.getFloat(_cursorIndexOfValidationConfidence);
            }
            final String _tmpAiMetadata;
            if (_cursor.isNull(_cursorIndexOfAiMetadata)) {
              _tmpAiMetadata = null;
            } else {
              _tmpAiMetadata = _cursor.getString(_cursorIndexOfAiMetadata);
            }
            final boolean _tmpManualOverride;
            final int _tmp_2;
            _tmp_2 = _cursor.getInt(_cursorIndexOfManualOverride);
            _tmpManualOverride = _tmp_2 != 0;
            final String _tmpOverrideReason;
            if (_cursor.isNull(_cursorIndexOfOverrideReason)) {
              _tmpOverrideReason = null;
            } else {
              _tmpOverrideReason = _cursor.getString(_cursorIndexOfOverrideReason);
            }
            final String _tmpOverrideBy;
            if (_cursor.isNull(_cursorIndexOfOverrideBy)) {
              _tmpOverrideBy = null;
            } else {
              _tmpOverrideBy = _cursor.getString(_cursorIndexOfOverrideBy);
            }
            final Date _tmpOverrideAt;
            final Long _tmp_3;
            if (_cursor.isNull(_cursorIndexOfOverrideAt)) {
              _tmp_3 = null;
            } else {
              _tmp_3 = _cursor.getLong(_cursorIndexOfOverrideAt);
            }
            _tmpOverrideAt = __dateConverters.fromTimestamp(_tmp_3);
            final UploadStatus _tmpUploadStatus;
            final String _tmp_4;
            if (_cursor.isNull(_cursorIndexOfUploadStatus)) {
              _tmp_4 = null;
            } else {
              _tmp_4 = _cursor.getString(_cursorIndexOfUploadStatus);
            }
            _tmpUploadStatus = __statusConverters.toUploadStatus(_tmp_4);
            final int _tmpUploadAttempts;
            _tmpUploadAttempts = _cursor.getInt(_cursorIndexOfUploadAttempts);
            final Date _tmpLastUploadAttempt;
            final Long _tmp_5;
            if (_cursor.isNull(_cursorIndexOfLastUploadAttempt)) {
              _tmp_5 = null;
            } else {
              _tmp_5 = _cursor.getLong(_cursorIndexOfLastUploadAttempt);
            }
            _tmpLastUploadAttempt = __dateConverters.fromTimestamp(_tmp_5);
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
            final String _tmpChecksum;
            if (_cursor.isNull(_cursorIndexOfChecksum)) {
              _tmpChecksum = null;
            } else {
              _tmpChecksum = _cursor.getString(_cursorIndexOfChecksum);
            }
            _item = new PhotoEntity(_tmpPhotoId,_tmpInstallationId,_tmpPhotoType,_tmpFilePath,_tmpFileSizeBytes,_tmpWidth,_tmpHeight,_tmpValidationStatus,_tmpValidationConfidence,_tmpAiMetadata,_tmpManualOverride,_tmpOverrideReason,_tmpOverrideBy,_tmpOverrideAt,_tmpUploadStatus,_tmpUploadAttempts,_tmpLastUploadAttempt,_tmpCreatedAt,_tmpUpdatedAt,_tmpChecksum);
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
  public Object getOrphanedPhotos(final Continuation<? super List<PhotoEntity>> $completion) {
    final String _sql = "SELECT p.* FROM photos p LEFT JOIN installations i ON p.installation_id = i.installation_id WHERE i.installation_id IS NULL";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<PhotoEntity>>() {
      @Override
      @NonNull
      public List<PhotoEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfPhotoId = CursorUtil.getColumnIndexOrThrow(_cursor, "photo_id");
          final int _cursorIndexOfInstallationId = CursorUtil.getColumnIndexOrThrow(_cursor, "installation_id");
          final int _cursorIndexOfPhotoType = CursorUtil.getColumnIndexOrThrow(_cursor, "photo_type");
          final int _cursorIndexOfFilePath = CursorUtil.getColumnIndexOrThrow(_cursor, "file_path");
          final int _cursorIndexOfFileSizeBytes = CursorUtil.getColumnIndexOrThrow(_cursor, "file_size_bytes");
          final int _cursorIndexOfWidth = CursorUtil.getColumnIndexOrThrow(_cursor, "width");
          final int _cursorIndexOfHeight = CursorUtil.getColumnIndexOrThrow(_cursor, "height");
          final int _cursorIndexOfValidationStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "validation_status");
          final int _cursorIndexOfValidationConfidence = CursorUtil.getColumnIndexOrThrow(_cursor, "validation_confidence");
          final int _cursorIndexOfAiMetadata = CursorUtil.getColumnIndexOrThrow(_cursor, "ai_metadata");
          final int _cursorIndexOfManualOverride = CursorUtil.getColumnIndexOrThrow(_cursor, "manual_override");
          final int _cursorIndexOfOverrideReason = CursorUtil.getColumnIndexOrThrow(_cursor, "override_reason");
          final int _cursorIndexOfOverrideBy = CursorUtil.getColumnIndexOrThrow(_cursor, "override_by");
          final int _cursorIndexOfOverrideAt = CursorUtil.getColumnIndexOrThrow(_cursor, "override_at");
          final int _cursorIndexOfUploadStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "upload_status");
          final int _cursorIndexOfUploadAttempts = CursorUtil.getColumnIndexOrThrow(_cursor, "upload_attempts");
          final int _cursorIndexOfLastUploadAttempt = CursorUtil.getColumnIndexOrThrow(_cursor, "last_upload_attempt");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final int _cursorIndexOfChecksum = CursorUtil.getColumnIndexOrThrow(_cursor, "checksum");
          final List<PhotoEntity> _result = new ArrayList<PhotoEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final PhotoEntity _item;
            final long _tmpPhotoId;
            _tmpPhotoId = _cursor.getLong(_cursorIndexOfPhotoId);
            final long _tmpInstallationId;
            _tmpInstallationId = _cursor.getLong(_cursorIndexOfInstallationId);
            final PhotoType _tmpPhotoType;
            final String _tmp;
            if (_cursor.isNull(_cursorIndexOfPhotoType)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getString(_cursorIndexOfPhotoType);
            }
            _tmpPhotoType = __statusConverters.toPhotoType(_tmp);
            final String _tmpFilePath;
            if (_cursor.isNull(_cursorIndexOfFilePath)) {
              _tmpFilePath = null;
            } else {
              _tmpFilePath = _cursor.getString(_cursorIndexOfFilePath);
            }
            final long _tmpFileSizeBytes;
            _tmpFileSizeBytes = _cursor.getLong(_cursorIndexOfFileSizeBytes);
            final int _tmpWidth;
            _tmpWidth = _cursor.getInt(_cursorIndexOfWidth);
            final int _tmpHeight;
            _tmpHeight = _cursor.getInt(_cursorIndexOfHeight);
            final ValidationStatus _tmpValidationStatus;
            final String _tmp_1;
            if (_cursor.isNull(_cursorIndexOfValidationStatus)) {
              _tmp_1 = null;
            } else {
              _tmp_1 = _cursor.getString(_cursorIndexOfValidationStatus);
            }
            _tmpValidationStatus = __statusConverters.toValidationStatus(_tmp_1);
            final Float _tmpValidationConfidence;
            if (_cursor.isNull(_cursorIndexOfValidationConfidence)) {
              _tmpValidationConfidence = null;
            } else {
              _tmpValidationConfidence = _cursor.getFloat(_cursorIndexOfValidationConfidence);
            }
            final String _tmpAiMetadata;
            if (_cursor.isNull(_cursorIndexOfAiMetadata)) {
              _tmpAiMetadata = null;
            } else {
              _tmpAiMetadata = _cursor.getString(_cursorIndexOfAiMetadata);
            }
            final boolean _tmpManualOverride;
            final int _tmp_2;
            _tmp_2 = _cursor.getInt(_cursorIndexOfManualOverride);
            _tmpManualOverride = _tmp_2 != 0;
            final String _tmpOverrideReason;
            if (_cursor.isNull(_cursorIndexOfOverrideReason)) {
              _tmpOverrideReason = null;
            } else {
              _tmpOverrideReason = _cursor.getString(_cursorIndexOfOverrideReason);
            }
            final String _tmpOverrideBy;
            if (_cursor.isNull(_cursorIndexOfOverrideBy)) {
              _tmpOverrideBy = null;
            } else {
              _tmpOverrideBy = _cursor.getString(_cursorIndexOfOverrideBy);
            }
            final Date _tmpOverrideAt;
            final Long _tmp_3;
            if (_cursor.isNull(_cursorIndexOfOverrideAt)) {
              _tmp_3 = null;
            } else {
              _tmp_3 = _cursor.getLong(_cursorIndexOfOverrideAt);
            }
            _tmpOverrideAt = __dateConverters.fromTimestamp(_tmp_3);
            final UploadStatus _tmpUploadStatus;
            final String _tmp_4;
            if (_cursor.isNull(_cursorIndexOfUploadStatus)) {
              _tmp_4 = null;
            } else {
              _tmp_4 = _cursor.getString(_cursorIndexOfUploadStatus);
            }
            _tmpUploadStatus = __statusConverters.toUploadStatus(_tmp_4);
            final int _tmpUploadAttempts;
            _tmpUploadAttempts = _cursor.getInt(_cursorIndexOfUploadAttempts);
            final Date _tmpLastUploadAttempt;
            final Long _tmp_5;
            if (_cursor.isNull(_cursorIndexOfLastUploadAttempt)) {
              _tmp_5 = null;
            } else {
              _tmp_5 = _cursor.getLong(_cursorIndexOfLastUploadAttempt);
            }
            _tmpLastUploadAttempt = __dateConverters.fromTimestamp(_tmp_5);
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
            final String _tmpChecksum;
            if (_cursor.isNull(_cursorIndexOfChecksum)) {
              _tmpChecksum = null;
            } else {
              _tmpChecksum = _cursor.getString(_cursorIndexOfChecksum);
            }
            _item = new PhotoEntity(_tmpPhotoId,_tmpInstallationId,_tmpPhotoType,_tmpFilePath,_tmpFileSizeBytes,_tmpWidth,_tmpHeight,_tmpValidationStatus,_tmpValidationConfidence,_tmpAiMetadata,_tmpManualOverride,_tmpOverrideReason,_tmpOverrideBy,_tmpOverrideAt,_tmpUploadStatus,_tmpUploadAttempts,_tmpLastUploadAttempt,_tmpCreatedAt,_tmpUpdatedAt,_tmpChecksum);
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
  public Object getPhotosWithoutChecksum(
      final Continuation<? super List<PhotoEntity>> $completion) {
    final String _sql = "SELECT * FROM photos WHERE checksum IS NULL OR checksum = ''";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<PhotoEntity>>() {
      @Override
      @NonNull
      public List<PhotoEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfPhotoId = CursorUtil.getColumnIndexOrThrow(_cursor, "photo_id");
          final int _cursorIndexOfInstallationId = CursorUtil.getColumnIndexOrThrow(_cursor, "installation_id");
          final int _cursorIndexOfPhotoType = CursorUtil.getColumnIndexOrThrow(_cursor, "photo_type");
          final int _cursorIndexOfFilePath = CursorUtil.getColumnIndexOrThrow(_cursor, "file_path");
          final int _cursorIndexOfFileSizeBytes = CursorUtil.getColumnIndexOrThrow(_cursor, "file_size_bytes");
          final int _cursorIndexOfWidth = CursorUtil.getColumnIndexOrThrow(_cursor, "width");
          final int _cursorIndexOfHeight = CursorUtil.getColumnIndexOrThrow(_cursor, "height");
          final int _cursorIndexOfValidationStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "validation_status");
          final int _cursorIndexOfValidationConfidence = CursorUtil.getColumnIndexOrThrow(_cursor, "validation_confidence");
          final int _cursorIndexOfAiMetadata = CursorUtil.getColumnIndexOrThrow(_cursor, "ai_metadata");
          final int _cursorIndexOfManualOverride = CursorUtil.getColumnIndexOrThrow(_cursor, "manual_override");
          final int _cursorIndexOfOverrideReason = CursorUtil.getColumnIndexOrThrow(_cursor, "override_reason");
          final int _cursorIndexOfOverrideBy = CursorUtil.getColumnIndexOrThrow(_cursor, "override_by");
          final int _cursorIndexOfOverrideAt = CursorUtil.getColumnIndexOrThrow(_cursor, "override_at");
          final int _cursorIndexOfUploadStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "upload_status");
          final int _cursorIndexOfUploadAttempts = CursorUtil.getColumnIndexOrThrow(_cursor, "upload_attempts");
          final int _cursorIndexOfLastUploadAttempt = CursorUtil.getColumnIndexOrThrow(_cursor, "last_upload_attempt");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final int _cursorIndexOfChecksum = CursorUtil.getColumnIndexOrThrow(_cursor, "checksum");
          final List<PhotoEntity> _result = new ArrayList<PhotoEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final PhotoEntity _item;
            final long _tmpPhotoId;
            _tmpPhotoId = _cursor.getLong(_cursorIndexOfPhotoId);
            final long _tmpInstallationId;
            _tmpInstallationId = _cursor.getLong(_cursorIndexOfInstallationId);
            final PhotoType _tmpPhotoType;
            final String _tmp;
            if (_cursor.isNull(_cursorIndexOfPhotoType)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getString(_cursorIndexOfPhotoType);
            }
            _tmpPhotoType = __statusConverters.toPhotoType(_tmp);
            final String _tmpFilePath;
            if (_cursor.isNull(_cursorIndexOfFilePath)) {
              _tmpFilePath = null;
            } else {
              _tmpFilePath = _cursor.getString(_cursorIndexOfFilePath);
            }
            final long _tmpFileSizeBytes;
            _tmpFileSizeBytes = _cursor.getLong(_cursorIndexOfFileSizeBytes);
            final int _tmpWidth;
            _tmpWidth = _cursor.getInt(_cursorIndexOfWidth);
            final int _tmpHeight;
            _tmpHeight = _cursor.getInt(_cursorIndexOfHeight);
            final ValidationStatus _tmpValidationStatus;
            final String _tmp_1;
            if (_cursor.isNull(_cursorIndexOfValidationStatus)) {
              _tmp_1 = null;
            } else {
              _tmp_1 = _cursor.getString(_cursorIndexOfValidationStatus);
            }
            _tmpValidationStatus = __statusConverters.toValidationStatus(_tmp_1);
            final Float _tmpValidationConfidence;
            if (_cursor.isNull(_cursorIndexOfValidationConfidence)) {
              _tmpValidationConfidence = null;
            } else {
              _tmpValidationConfidence = _cursor.getFloat(_cursorIndexOfValidationConfidence);
            }
            final String _tmpAiMetadata;
            if (_cursor.isNull(_cursorIndexOfAiMetadata)) {
              _tmpAiMetadata = null;
            } else {
              _tmpAiMetadata = _cursor.getString(_cursorIndexOfAiMetadata);
            }
            final boolean _tmpManualOverride;
            final int _tmp_2;
            _tmp_2 = _cursor.getInt(_cursorIndexOfManualOverride);
            _tmpManualOverride = _tmp_2 != 0;
            final String _tmpOverrideReason;
            if (_cursor.isNull(_cursorIndexOfOverrideReason)) {
              _tmpOverrideReason = null;
            } else {
              _tmpOverrideReason = _cursor.getString(_cursorIndexOfOverrideReason);
            }
            final String _tmpOverrideBy;
            if (_cursor.isNull(_cursorIndexOfOverrideBy)) {
              _tmpOverrideBy = null;
            } else {
              _tmpOverrideBy = _cursor.getString(_cursorIndexOfOverrideBy);
            }
            final Date _tmpOverrideAt;
            final Long _tmp_3;
            if (_cursor.isNull(_cursorIndexOfOverrideAt)) {
              _tmp_3 = null;
            } else {
              _tmp_3 = _cursor.getLong(_cursorIndexOfOverrideAt);
            }
            _tmpOverrideAt = __dateConverters.fromTimestamp(_tmp_3);
            final UploadStatus _tmpUploadStatus;
            final String _tmp_4;
            if (_cursor.isNull(_cursorIndexOfUploadStatus)) {
              _tmp_4 = null;
            } else {
              _tmp_4 = _cursor.getString(_cursorIndexOfUploadStatus);
            }
            _tmpUploadStatus = __statusConverters.toUploadStatus(_tmp_4);
            final int _tmpUploadAttempts;
            _tmpUploadAttempts = _cursor.getInt(_cursorIndexOfUploadAttempts);
            final Date _tmpLastUploadAttempt;
            final Long _tmp_5;
            if (_cursor.isNull(_cursorIndexOfLastUploadAttempt)) {
              _tmp_5 = null;
            } else {
              _tmp_5 = _cursor.getLong(_cursorIndexOfLastUploadAttempt);
            }
            _tmpLastUploadAttempt = __dateConverters.fromTimestamp(_tmp_5);
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
            final String _tmpChecksum;
            if (_cursor.isNull(_cursorIndexOfChecksum)) {
              _tmpChecksum = null;
            } else {
              _tmpChecksum = _cursor.getString(_cursorIndexOfChecksum);
            }
            _item = new PhotoEntity(_tmpPhotoId,_tmpInstallationId,_tmpPhotoType,_tmpFilePath,_tmpFileSizeBytes,_tmpWidth,_tmpHeight,_tmpValidationStatus,_tmpValidationConfidence,_tmpAiMetadata,_tmpManualOverride,_tmpOverrideReason,_tmpOverrideBy,_tmpOverrideAt,_tmpUploadStatus,_tmpUploadAttempts,_tmpLastUploadAttempt,_tmpCreatedAt,_tmpUpdatedAt,_tmpChecksum);
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
  public Object getAllPhotos(final Continuation<? super List<PhotoEntity>> $completion) {
    final String _sql = "SELECT * FROM photos ORDER BY created_at DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<PhotoEntity>>() {
      @Override
      @NonNull
      public List<PhotoEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfPhotoId = CursorUtil.getColumnIndexOrThrow(_cursor, "photo_id");
          final int _cursorIndexOfInstallationId = CursorUtil.getColumnIndexOrThrow(_cursor, "installation_id");
          final int _cursorIndexOfPhotoType = CursorUtil.getColumnIndexOrThrow(_cursor, "photo_type");
          final int _cursorIndexOfFilePath = CursorUtil.getColumnIndexOrThrow(_cursor, "file_path");
          final int _cursorIndexOfFileSizeBytes = CursorUtil.getColumnIndexOrThrow(_cursor, "file_size_bytes");
          final int _cursorIndexOfWidth = CursorUtil.getColumnIndexOrThrow(_cursor, "width");
          final int _cursorIndexOfHeight = CursorUtil.getColumnIndexOrThrow(_cursor, "height");
          final int _cursorIndexOfValidationStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "validation_status");
          final int _cursorIndexOfValidationConfidence = CursorUtil.getColumnIndexOrThrow(_cursor, "validation_confidence");
          final int _cursorIndexOfAiMetadata = CursorUtil.getColumnIndexOrThrow(_cursor, "ai_metadata");
          final int _cursorIndexOfManualOverride = CursorUtil.getColumnIndexOrThrow(_cursor, "manual_override");
          final int _cursorIndexOfOverrideReason = CursorUtil.getColumnIndexOrThrow(_cursor, "override_reason");
          final int _cursorIndexOfOverrideBy = CursorUtil.getColumnIndexOrThrow(_cursor, "override_by");
          final int _cursorIndexOfOverrideAt = CursorUtil.getColumnIndexOrThrow(_cursor, "override_at");
          final int _cursorIndexOfUploadStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "upload_status");
          final int _cursorIndexOfUploadAttempts = CursorUtil.getColumnIndexOrThrow(_cursor, "upload_attempts");
          final int _cursorIndexOfLastUploadAttempt = CursorUtil.getColumnIndexOrThrow(_cursor, "last_upload_attempt");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final int _cursorIndexOfChecksum = CursorUtil.getColumnIndexOrThrow(_cursor, "checksum");
          final List<PhotoEntity> _result = new ArrayList<PhotoEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final PhotoEntity _item;
            final long _tmpPhotoId;
            _tmpPhotoId = _cursor.getLong(_cursorIndexOfPhotoId);
            final long _tmpInstallationId;
            _tmpInstallationId = _cursor.getLong(_cursorIndexOfInstallationId);
            final PhotoType _tmpPhotoType;
            final String _tmp;
            if (_cursor.isNull(_cursorIndexOfPhotoType)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getString(_cursorIndexOfPhotoType);
            }
            _tmpPhotoType = __statusConverters.toPhotoType(_tmp);
            final String _tmpFilePath;
            if (_cursor.isNull(_cursorIndexOfFilePath)) {
              _tmpFilePath = null;
            } else {
              _tmpFilePath = _cursor.getString(_cursorIndexOfFilePath);
            }
            final long _tmpFileSizeBytes;
            _tmpFileSizeBytes = _cursor.getLong(_cursorIndexOfFileSizeBytes);
            final int _tmpWidth;
            _tmpWidth = _cursor.getInt(_cursorIndexOfWidth);
            final int _tmpHeight;
            _tmpHeight = _cursor.getInt(_cursorIndexOfHeight);
            final ValidationStatus _tmpValidationStatus;
            final String _tmp_1;
            if (_cursor.isNull(_cursorIndexOfValidationStatus)) {
              _tmp_1 = null;
            } else {
              _tmp_1 = _cursor.getString(_cursorIndexOfValidationStatus);
            }
            _tmpValidationStatus = __statusConverters.toValidationStatus(_tmp_1);
            final Float _tmpValidationConfidence;
            if (_cursor.isNull(_cursorIndexOfValidationConfidence)) {
              _tmpValidationConfidence = null;
            } else {
              _tmpValidationConfidence = _cursor.getFloat(_cursorIndexOfValidationConfidence);
            }
            final String _tmpAiMetadata;
            if (_cursor.isNull(_cursorIndexOfAiMetadata)) {
              _tmpAiMetadata = null;
            } else {
              _tmpAiMetadata = _cursor.getString(_cursorIndexOfAiMetadata);
            }
            final boolean _tmpManualOverride;
            final int _tmp_2;
            _tmp_2 = _cursor.getInt(_cursorIndexOfManualOverride);
            _tmpManualOverride = _tmp_2 != 0;
            final String _tmpOverrideReason;
            if (_cursor.isNull(_cursorIndexOfOverrideReason)) {
              _tmpOverrideReason = null;
            } else {
              _tmpOverrideReason = _cursor.getString(_cursorIndexOfOverrideReason);
            }
            final String _tmpOverrideBy;
            if (_cursor.isNull(_cursorIndexOfOverrideBy)) {
              _tmpOverrideBy = null;
            } else {
              _tmpOverrideBy = _cursor.getString(_cursorIndexOfOverrideBy);
            }
            final Date _tmpOverrideAt;
            final Long _tmp_3;
            if (_cursor.isNull(_cursorIndexOfOverrideAt)) {
              _tmp_3 = null;
            } else {
              _tmp_3 = _cursor.getLong(_cursorIndexOfOverrideAt);
            }
            _tmpOverrideAt = __dateConverters.fromTimestamp(_tmp_3);
            final UploadStatus _tmpUploadStatus;
            final String _tmp_4;
            if (_cursor.isNull(_cursorIndexOfUploadStatus)) {
              _tmp_4 = null;
            } else {
              _tmp_4 = _cursor.getString(_cursorIndexOfUploadStatus);
            }
            _tmpUploadStatus = __statusConverters.toUploadStatus(_tmp_4);
            final int _tmpUploadAttempts;
            _tmpUploadAttempts = _cursor.getInt(_cursorIndexOfUploadAttempts);
            final Date _tmpLastUploadAttempt;
            final Long _tmp_5;
            if (_cursor.isNull(_cursorIndexOfLastUploadAttempt)) {
              _tmp_5 = null;
            } else {
              _tmp_5 = _cursor.getLong(_cursorIndexOfLastUploadAttempt);
            }
            _tmpLastUploadAttempt = __dateConverters.fromTimestamp(_tmp_5);
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
            final String _tmpChecksum;
            if (_cursor.isNull(_cursorIndexOfChecksum)) {
              _tmpChecksum = null;
            } else {
              _tmpChecksum = _cursor.getString(_cursorIndexOfChecksum);
            }
            _item = new PhotoEntity(_tmpPhotoId,_tmpInstallationId,_tmpPhotoType,_tmpFilePath,_tmpFileSizeBytes,_tmpWidth,_tmpHeight,_tmpValidationStatus,_tmpValidationConfidence,_tmpAiMetadata,_tmpManualOverride,_tmpOverrideReason,_tmpOverrideBy,_tmpOverrideAt,_tmpUploadStatus,_tmpUploadAttempts,_tmpLastUploadAttempt,_tmpCreatedAt,_tmpUpdatedAt,_tmpChecksum);
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
  public Object markPhotosSynced(final List<Long> photoIds, final long timestamp,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final StringBuilder _stringBuilder = StringUtil.newStringBuilder();
        _stringBuilder.append("UPDATE photos SET upload_status = 'COMPLETED', updated_at = ");
        _stringBuilder.append("?");
        _stringBuilder.append(" WHERE photo_id IN (");
        final int _inputSize = photoIds.size();
        StringUtil.appendPlaceholders(_stringBuilder, _inputSize);
        _stringBuilder.append(")");
        final String _sql = _stringBuilder.toString();
        final SupportSQLiteStatement _stmt = __db.compileStatement(_sql);
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, timestamp);
        _argIndex = 2;
        for (Long _item : photoIds) {
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
