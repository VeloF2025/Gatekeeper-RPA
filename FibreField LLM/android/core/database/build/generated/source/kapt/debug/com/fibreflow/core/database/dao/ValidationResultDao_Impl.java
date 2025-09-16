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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
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
        final String _query = "DELETE FROM validation_results WHERE created_at < ?";
        return _query;
      }
    };
  }

  @Override
  public Object insertValidationResult(final ValidationResultEntity result,
      final Continuation<? super Long> arg1) {
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
    }, arg1);
  }

  @Override
  public Object insertValidationResults(final List<ValidationResultEntity> results,
      final Continuation<? super List<Long>> arg1) {
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
    }, arg1);
  }

  @Override
  public Object deleteValidationResult(final ValidationResultEntity result,
      final Continuation<? super Unit> arg1) {
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
    }, arg1);
  }

  @Override
  public Object updateValidationResult(final ValidationResultEntity result,
      final Continuation<? super Unit> arg1) {
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
    }, arg1);
  }

  @Override
  public Object deleteOldValidationResults(final long cutoffDate,
      final Continuation<? super Integer> arg1) {
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
    }, arg1);
  }

  @Override
  public Object getValidationResultById(final long resultId,
      final Continuation<? super ValidationResultEntity> arg1) {
    final String _sql = "SELECT * FROM validation_results WHERE validation_id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, resultId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<ValidationResultEntity>() {
      @Override
      @Nullable
      public ValidationResultEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfValidationId = CursorUtil.getColumnIndexOrThrow(_cursor, "validation_id");
          final int _cursorIndexOfPhotoId = CursorUtil.getColumnIndexOrThrow(_cursor, "photo_id");
          final int _cursorIndexOfValidationType = CursorUtil.getColumnIndexOrThrow(_cursor, "validation_type");
          final int _cursorIndexOfIsValid = CursorUtil.getColumnIndexOrThrow(_cursor, "is_valid");
          final int _cursorIndexOfConfidenceScore = CursorUtil.getColumnIndexOrThrow(_cursor, "confidence_score");
          final int _cursorIndexOfValidationData = CursorUtil.getColumnIndexOrThrow(_cursor, "validation_data");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final ValidationResultEntity _result;
          if (_cursor.moveToFirst()) {
            final long _tmpValidationId;
            _tmpValidationId = _cursor.getLong(_cursorIndexOfValidationId);
            final long _tmpPhotoId;
            _tmpPhotoId = _cursor.getLong(_cursorIndexOfPhotoId);
            final String _tmpValidationType;
            if (_cursor.isNull(_cursorIndexOfValidationType)) {
              _tmpValidationType = null;
            } else {
              _tmpValidationType = _cursor.getString(_cursorIndexOfValidationType);
            }
            final boolean _tmpIsValid;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsValid);
            _tmpIsValid = _tmp != 0;
            final float _tmpConfidenceScore;
            _tmpConfidenceScore = _cursor.getFloat(_cursorIndexOfConfidenceScore);
            final String _tmpValidationData;
            if (_cursor.isNull(_cursorIndexOfValidationData)) {
              _tmpValidationData = null;
            } else {
              _tmpValidationData = _cursor.getString(_cursorIndexOfValidationData);
            }
            final Date _tmpCreatedAt;
            final Long _tmp_1;
            if (_cursor.isNull(_cursorIndexOfCreatedAt)) {
              _tmp_1 = null;
            } else {
              _tmp_1 = _cursor.getLong(_cursorIndexOfCreatedAt);
            }
            _tmpCreatedAt = __dateConverters.fromTimestamp(_tmp_1);
            _result = new ValidationResultEntity(_tmpValidationId,_tmpPhotoId,_tmpValidationType,_tmpIsValid,_tmpConfidenceScore,_tmpValidationData,_tmpCreatedAt);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, arg1);
  }

  @Override
  public Object getValidationResultsByPhoto(final long photoId,
      final Continuation<? super List<ValidationResultEntity>> arg1) {
    final String _sql = "SELECT * FROM validation_results WHERE photo_id = ? ORDER BY created_at DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, photoId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<ValidationResultEntity>>() {
      @Override
      @NonNull
      public List<ValidationResultEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfValidationId = CursorUtil.getColumnIndexOrThrow(_cursor, "validation_id");
          final int _cursorIndexOfPhotoId = CursorUtil.getColumnIndexOrThrow(_cursor, "photo_id");
          final int _cursorIndexOfValidationType = CursorUtil.getColumnIndexOrThrow(_cursor, "validation_type");
          final int _cursorIndexOfIsValid = CursorUtil.getColumnIndexOrThrow(_cursor, "is_valid");
          final int _cursorIndexOfConfidenceScore = CursorUtil.getColumnIndexOrThrow(_cursor, "confidence_score");
          final int _cursorIndexOfValidationData = CursorUtil.getColumnIndexOrThrow(_cursor, "validation_data");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final List<ValidationResultEntity> _result = new ArrayList<ValidationResultEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ValidationResultEntity _item;
            final long _tmpValidationId;
            _tmpValidationId = _cursor.getLong(_cursorIndexOfValidationId);
            final long _tmpPhotoId;
            _tmpPhotoId = _cursor.getLong(_cursorIndexOfPhotoId);
            final String _tmpValidationType;
            if (_cursor.isNull(_cursorIndexOfValidationType)) {
              _tmpValidationType = null;
            } else {
              _tmpValidationType = _cursor.getString(_cursorIndexOfValidationType);
            }
            final boolean _tmpIsValid;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsValid);
            _tmpIsValid = _tmp != 0;
            final float _tmpConfidenceScore;
            _tmpConfidenceScore = _cursor.getFloat(_cursorIndexOfConfidenceScore);
            final String _tmpValidationData;
            if (_cursor.isNull(_cursorIndexOfValidationData)) {
              _tmpValidationData = null;
            } else {
              _tmpValidationData = _cursor.getString(_cursorIndexOfValidationData);
            }
            final Date _tmpCreatedAt;
            final Long _tmp_1;
            if (_cursor.isNull(_cursorIndexOfCreatedAt)) {
              _tmp_1 = null;
            } else {
              _tmp_1 = _cursor.getLong(_cursorIndexOfCreatedAt);
            }
            _tmpCreatedAt = __dateConverters.fromTimestamp(_tmp_1);
            _item = new ValidationResultEntity(_tmpValidationId,_tmpPhotoId,_tmpValidationType,_tmpIsValid,_tmpConfidenceScore,_tmpValidationData,_tmpCreatedAt);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, arg1);
  }

  @Override
  public Object getValidationResultsByInstallation(final long installationId,
      final Continuation<? super List<ValidationResultEntity>> arg1) {
    final String _sql = "SELECT * FROM validation_results WHERE photo_id IN (SELECT photo_id FROM photos WHERE installation_id = ?) ORDER BY created_at DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, installationId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<ValidationResultEntity>>() {
      @Override
      @NonNull
      public List<ValidationResultEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfValidationId = CursorUtil.getColumnIndexOrThrow(_cursor, "validation_id");
          final int _cursorIndexOfPhotoId = CursorUtil.getColumnIndexOrThrow(_cursor, "photo_id");
          final int _cursorIndexOfValidationType = CursorUtil.getColumnIndexOrThrow(_cursor, "validation_type");
          final int _cursorIndexOfIsValid = CursorUtil.getColumnIndexOrThrow(_cursor, "is_valid");
          final int _cursorIndexOfConfidenceScore = CursorUtil.getColumnIndexOrThrow(_cursor, "confidence_score");
          final int _cursorIndexOfValidationData = CursorUtil.getColumnIndexOrThrow(_cursor, "validation_data");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final List<ValidationResultEntity> _result = new ArrayList<ValidationResultEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ValidationResultEntity _item;
            final long _tmpValidationId;
            _tmpValidationId = _cursor.getLong(_cursorIndexOfValidationId);
            final long _tmpPhotoId;
            _tmpPhotoId = _cursor.getLong(_cursorIndexOfPhotoId);
            final String _tmpValidationType;
            if (_cursor.isNull(_cursorIndexOfValidationType)) {
              _tmpValidationType = null;
            } else {
              _tmpValidationType = _cursor.getString(_cursorIndexOfValidationType);
            }
            final boolean _tmpIsValid;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsValid);
            _tmpIsValid = _tmp != 0;
            final float _tmpConfidenceScore;
            _tmpConfidenceScore = _cursor.getFloat(_cursorIndexOfConfidenceScore);
            final String _tmpValidationData;
            if (_cursor.isNull(_cursorIndexOfValidationData)) {
              _tmpValidationData = null;
            } else {
              _tmpValidationData = _cursor.getString(_cursorIndexOfValidationData);
            }
            final Date _tmpCreatedAt;
            final Long _tmp_1;
            if (_cursor.isNull(_cursorIndexOfCreatedAt)) {
              _tmp_1 = null;
            } else {
              _tmp_1 = _cursor.getLong(_cursorIndexOfCreatedAt);
            }
            _tmpCreatedAt = __dateConverters.fromTimestamp(_tmp_1);
            _item = new ValidationResultEntity(_tmpValidationId,_tmpPhotoId,_tmpValidationType,_tmpIsValid,_tmpConfidenceScore,_tmpValidationData,_tmpCreatedAt);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, arg1);
  }

  @Override
  public Object getValidationResultsByStatus(final boolean isValid,
      final Continuation<? super List<ValidationResultEntity>> arg1) {
    final String _sql = "SELECT * FROM validation_results WHERE is_valid = ? ORDER BY created_at DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    final int _tmp = isValid ? 1 : 0;
    _statement.bindLong(_argIndex, _tmp);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<ValidationResultEntity>>() {
      @Override
      @NonNull
      public List<ValidationResultEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfValidationId = CursorUtil.getColumnIndexOrThrow(_cursor, "validation_id");
          final int _cursorIndexOfPhotoId = CursorUtil.getColumnIndexOrThrow(_cursor, "photo_id");
          final int _cursorIndexOfValidationType = CursorUtil.getColumnIndexOrThrow(_cursor, "validation_type");
          final int _cursorIndexOfIsValid = CursorUtil.getColumnIndexOrThrow(_cursor, "is_valid");
          final int _cursorIndexOfConfidenceScore = CursorUtil.getColumnIndexOrThrow(_cursor, "confidence_score");
          final int _cursorIndexOfValidationData = CursorUtil.getColumnIndexOrThrow(_cursor, "validation_data");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final List<ValidationResultEntity> _result = new ArrayList<ValidationResultEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ValidationResultEntity _item;
            final long _tmpValidationId;
            _tmpValidationId = _cursor.getLong(_cursorIndexOfValidationId);
            final long _tmpPhotoId;
            _tmpPhotoId = _cursor.getLong(_cursorIndexOfPhotoId);
            final String _tmpValidationType;
            if (_cursor.isNull(_cursorIndexOfValidationType)) {
              _tmpValidationType = null;
            } else {
              _tmpValidationType = _cursor.getString(_cursorIndexOfValidationType);
            }
            final boolean _tmpIsValid;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfIsValid);
            _tmpIsValid = _tmp_1 != 0;
            final float _tmpConfidenceScore;
            _tmpConfidenceScore = _cursor.getFloat(_cursorIndexOfConfidenceScore);
            final String _tmpValidationData;
            if (_cursor.isNull(_cursorIndexOfValidationData)) {
              _tmpValidationData = null;
            } else {
              _tmpValidationData = _cursor.getString(_cursorIndexOfValidationData);
            }
            final Date _tmpCreatedAt;
            final Long _tmp_2;
            if (_cursor.isNull(_cursorIndexOfCreatedAt)) {
              _tmp_2 = null;
            } else {
              _tmp_2 = _cursor.getLong(_cursorIndexOfCreatedAt);
            }
            _tmpCreatedAt = __dateConverters.fromTimestamp(_tmp_2);
            _item = new ValidationResultEntity(_tmpValidationId,_tmpPhotoId,_tmpValidationType,_tmpIsValid,_tmpConfidenceScore,_tmpValidationData,_tmpCreatedAt);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, arg1);
  }

  @Override
  public Object getValidationResultCountByStatus(final boolean isValid,
      final Continuation<? super Integer> arg1) {
    final String _sql = "SELECT COUNT(*) FROM validation_results WHERE is_valid = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    final int _tmp = isValid ? 1 : 0;
    _statement.bindLong(_argIndex, _tmp);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Integer>() {
      @Override
      @NonNull
      public Integer call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Integer _result;
          if (_cursor.moveToFirst()) {
            final Integer _tmp_1;
            if (_cursor.isNull(0)) {
              _tmp_1 = null;
            } else {
              _tmp_1 = _cursor.getInt(0);
            }
            _result = _tmp_1;
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, arg1);
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
