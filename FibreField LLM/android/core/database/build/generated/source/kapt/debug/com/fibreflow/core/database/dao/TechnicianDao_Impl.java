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
import com.fibreflow.core.database.entities.TechnicianEntity;
import com.fibreflow.core.database.entities.TechnicianRole;
import java.lang.Class;
import java.lang.Exception;
import java.lang.IllegalArgumentException;
import java.lang.Integer;
import java.lang.Long;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class TechnicianDao_Impl implements TechnicianDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<TechnicianEntity> __insertionAdapterOfTechnicianEntity;

  private final DateConverters __dateConverters = new DateConverters();

  private final EntityDeletionOrUpdateAdapter<TechnicianEntity> __deletionAdapterOfTechnicianEntity;

  private final EntityDeletionOrUpdateAdapter<TechnicianEntity> __updateAdapterOfTechnicianEntity;

  private final SharedSQLiteStatement __preparedStmtOfUpdateTechnicianActiveStatus;

  private final SharedSQLiteStatement __preparedStmtOfUpdateLastLoginTime;

  private final SharedSQLiteStatement __preparedStmtOfUpdateLastSyncTime;

  private final SharedSQLiteStatement __preparedStmtOfDeleteInactiveTechnicians;

  public TechnicianDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfTechnicianEntity = new EntityInsertionAdapter<TechnicianEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `technicians` (`technician_id`,`name`,`email`,`phone`,`role`,`certifications`,`active_projects`,`permissions`,`active`,`last_login`,`created_at`,`updated_at`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final TechnicianEntity entity) {
        if (entity.getTechnicianId() == null) {
          statement.bindNull(1);
        } else {
          statement.bindString(1, entity.getTechnicianId());
        }
        if (entity.getName() == null) {
          statement.bindNull(2);
        } else {
          statement.bindString(2, entity.getName());
        }
        if (entity.getEmail() == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, entity.getEmail());
        }
        if (entity.getPhone() == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.getPhone());
        }
        statement.bindString(5, __TechnicianRole_enumToString(entity.getRole()));
        if (entity.getCertifications() == null) {
          statement.bindNull(6);
        } else {
          statement.bindString(6, entity.getCertifications());
        }
        if (entity.getActiveProjects() == null) {
          statement.bindNull(7);
        } else {
          statement.bindString(7, entity.getActiveProjects());
        }
        if (entity.getPermissions() == null) {
          statement.bindNull(8);
        } else {
          statement.bindString(8, entity.getPermissions());
        }
        final int _tmp = entity.getActive() ? 1 : 0;
        statement.bindLong(9, _tmp);
        final Long _tmp_1 = __dateConverters.dateToTimestamp(entity.getLastLogin());
        if (_tmp_1 == null) {
          statement.bindNull(10);
        } else {
          statement.bindLong(10, _tmp_1);
        }
        final Long _tmp_2 = __dateConverters.dateToTimestamp(entity.getCreatedAt());
        if (_tmp_2 == null) {
          statement.bindNull(11);
        } else {
          statement.bindLong(11, _tmp_2);
        }
        final Long _tmp_3 = __dateConverters.dateToTimestamp(entity.getUpdatedAt());
        if (_tmp_3 == null) {
          statement.bindNull(12);
        } else {
          statement.bindLong(12, _tmp_3);
        }
      }
    };
    this.__deletionAdapterOfTechnicianEntity = new EntityDeletionOrUpdateAdapter<TechnicianEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `technicians` WHERE `technician_id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final TechnicianEntity entity) {
        if (entity.getTechnicianId() == null) {
          statement.bindNull(1);
        } else {
          statement.bindString(1, entity.getTechnicianId());
        }
      }
    };
    this.__updateAdapterOfTechnicianEntity = new EntityDeletionOrUpdateAdapter<TechnicianEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `technicians` SET `technician_id` = ?,`name` = ?,`email` = ?,`phone` = ?,`role` = ?,`certifications` = ?,`active_projects` = ?,`permissions` = ?,`active` = ?,`last_login` = ?,`created_at` = ?,`updated_at` = ? WHERE `technician_id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final TechnicianEntity entity) {
        if (entity.getTechnicianId() == null) {
          statement.bindNull(1);
        } else {
          statement.bindString(1, entity.getTechnicianId());
        }
        if (entity.getName() == null) {
          statement.bindNull(2);
        } else {
          statement.bindString(2, entity.getName());
        }
        if (entity.getEmail() == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, entity.getEmail());
        }
        if (entity.getPhone() == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.getPhone());
        }
        statement.bindString(5, __TechnicianRole_enumToString(entity.getRole()));
        if (entity.getCertifications() == null) {
          statement.bindNull(6);
        } else {
          statement.bindString(6, entity.getCertifications());
        }
        if (entity.getActiveProjects() == null) {
          statement.bindNull(7);
        } else {
          statement.bindString(7, entity.getActiveProjects());
        }
        if (entity.getPermissions() == null) {
          statement.bindNull(8);
        } else {
          statement.bindString(8, entity.getPermissions());
        }
        final int _tmp = entity.getActive() ? 1 : 0;
        statement.bindLong(9, _tmp);
        final Long _tmp_1 = __dateConverters.dateToTimestamp(entity.getLastLogin());
        if (_tmp_1 == null) {
          statement.bindNull(10);
        } else {
          statement.bindLong(10, _tmp_1);
        }
        final Long _tmp_2 = __dateConverters.dateToTimestamp(entity.getCreatedAt());
        if (_tmp_2 == null) {
          statement.bindNull(11);
        } else {
          statement.bindLong(11, _tmp_2);
        }
        final Long _tmp_3 = __dateConverters.dateToTimestamp(entity.getUpdatedAt());
        if (_tmp_3 == null) {
          statement.bindNull(12);
        } else {
          statement.bindLong(12, _tmp_3);
        }
        if (entity.getTechnicianId() == null) {
          statement.bindNull(13);
        } else {
          statement.bindString(13, entity.getTechnicianId());
        }
      }
    };
    this.__preparedStmtOfUpdateTechnicianActiveStatus = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE technicians SET isActive = ? WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfUpdateLastLoginTime = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE technicians SET lastLoginAt = ? WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfUpdateLastSyncTime = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE technicians SET lastSyncAt = ? WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteInactiveTechnicians = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM technicians WHERE isActive = 0 AND lastLoginAt < ?";
        return _query;
      }
    };
  }

  @Override
  public Object insertTechnician(final TechnicianEntity technician,
      final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfTechnicianEntity.insertAndReturnId(technician);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertTechnicians(final List<TechnicianEntity> technicians,
      final Continuation<? super List<Long>> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<List<Long>>() {
      @Override
      @NonNull
      public List<Long> call() throws Exception {
        __db.beginTransaction();
        try {
          final List<Long> _result = __insertionAdapterOfTechnicianEntity.insertAndReturnIdsList(technicians);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteTechnician(final TechnicianEntity technician,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfTechnicianEntity.handle(technician);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateTechnician(final TechnicianEntity technician,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfTechnicianEntity.handle(technician);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateTechnicianActiveStatus(final String technicianId, final boolean isActive,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfUpdateTechnicianActiveStatus.acquire();
        int _argIndex = 1;
        final int _tmp = isActive ? 1 : 0;
        _stmt.bindLong(_argIndex, _tmp);
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
          __preparedStmtOfUpdateTechnicianActiveStatus.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object updateLastLoginTime(final String technicianId, final long timestamp,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfUpdateLastLoginTime.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, timestamp);
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
          __preparedStmtOfUpdateLastLoginTime.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object updateLastSyncTime(final String technicianId, final long timestamp,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfUpdateLastSyncTime.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, timestamp);
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
          __preparedStmtOfUpdateLastSyncTime.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteInactiveTechnicians(final long cutoffDate,
      final Continuation<? super Integer> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Integer>() {
      @Override
      @NonNull
      public Integer call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteInactiveTechnicians.acquire();
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
          __preparedStmtOfDeleteInactiveTechnicians.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object getTechnicianById(final String technicianId,
      final Continuation<? super TechnicianEntity> $completion) {
    final String _sql = "SELECT * FROM technicians WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    if (technicianId == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, technicianId);
    }
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<TechnicianEntity>() {
      @Override
      @Nullable
      public TechnicianEntity call() throws Exception {
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
  public Flow<TechnicianEntity> getTechnicianByIdFlow(final String technicianId) {
    final String _sql = "SELECT * FROM technicians WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    if (technicianId == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, technicianId);
    }
    return CoroutinesRoom.createFlow(__db, false, new String[] {"technicians"}, new Callable<TechnicianEntity>() {
      @Override
      @Nullable
      public TechnicianEntity call() throws Exception {
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
  public Object getTechnicianByEmail(final String email,
      final Continuation<? super TechnicianEntity> $completion) {
    final String _sql = "SELECT * FROM technicians WHERE email = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    if (email == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, email);
    }
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<TechnicianEntity>() {
      @Override
      @Nullable
      public TechnicianEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfTechnicianId = CursorUtil.getColumnIndexOrThrow(_cursor, "technician_id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfEmail = CursorUtil.getColumnIndexOrThrow(_cursor, "email");
          final int _cursorIndexOfPhone = CursorUtil.getColumnIndexOrThrow(_cursor, "phone");
          final int _cursorIndexOfRole = CursorUtil.getColumnIndexOrThrow(_cursor, "role");
          final int _cursorIndexOfCertifications = CursorUtil.getColumnIndexOrThrow(_cursor, "certifications");
          final int _cursorIndexOfActiveProjects = CursorUtil.getColumnIndexOrThrow(_cursor, "active_projects");
          final int _cursorIndexOfPermissions = CursorUtil.getColumnIndexOrThrow(_cursor, "permissions");
          final int _cursorIndexOfActive = CursorUtil.getColumnIndexOrThrow(_cursor, "active");
          final int _cursorIndexOfLastLogin = CursorUtil.getColumnIndexOrThrow(_cursor, "last_login");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final TechnicianEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpTechnicianId;
            if (_cursor.isNull(_cursorIndexOfTechnicianId)) {
              _tmpTechnicianId = null;
            } else {
              _tmpTechnicianId = _cursor.getString(_cursorIndexOfTechnicianId);
            }
            final String _tmpName;
            if (_cursor.isNull(_cursorIndexOfName)) {
              _tmpName = null;
            } else {
              _tmpName = _cursor.getString(_cursorIndexOfName);
            }
            final String _tmpEmail;
            if (_cursor.isNull(_cursorIndexOfEmail)) {
              _tmpEmail = null;
            } else {
              _tmpEmail = _cursor.getString(_cursorIndexOfEmail);
            }
            final String _tmpPhone;
            if (_cursor.isNull(_cursorIndexOfPhone)) {
              _tmpPhone = null;
            } else {
              _tmpPhone = _cursor.getString(_cursorIndexOfPhone);
            }
            final TechnicianRole _tmpRole;
            _tmpRole = __TechnicianRole_stringToEnum(_cursor.getString(_cursorIndexOfRole));
            final String _tmpCertifications;
            if (_cursor.isNull(_cursorIndexOfCertifications)) {
              _tmpCertifications = null;
            } else {
              _tmpCertifications = _cursor.getString(_cursorIndexOfCertifications);
            }
            final String _tmpActiveProjects;
            if (_cursor.isNull(_cursorIndexOfActiveProjects)) {
              _tmpActiveProjects = null;
            } else {
              _tmpActiveProjects = _cursor.getString(_cursorIndexOfActiveProjects);
            }
            final String _tmpPermissions;
            if (_cursor.isNull(_cursorIndexOfPermissions)) {
              _tmpPermissions = null;
            } else {
              _tmpPermissions = _cursor.getString(_cursorIndexOfPermissions);
            }
            final boolean _tmpActive;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfActive);
            _tmpActive = _tmp != 0;
            final Date _tmpLastLogin;
            final Long _tmp_1;
            if (_cursor.isNull(_cursorIndexOfLastLogin)) {
              _tmp_1 = null;
            } else {
              _tmp_1 = _cursor.getLong(_cursorIndexOfLastLogin);
            }
            _tmpLastLogin = __dateConverters.fromTimestamp(_tmp_1);
            final Date _tmpCreatedAt;
            final Long _tmp_2;
            if (_cursor.isNull(_cursorIndexOfCreatedAt)) {
              _tmp_2 = null;
            } else {
              _tmp_2 = _cursor.getLong(_cursorIndexOfCreatedAt);
            }
            _tmpCreatedAt = __dateConverters.fromTimestamp(_tmp_2);
            final Date _tmpUpdatedAt;
            final Long _tmp_3;
            if (_cursor.isNull(_cursorIndexOfUpdatedAt)) {
              _tmp_3 = null;
            } else {
              _tmp_3 = _cursor.getLong(_cursorIndexOfUpdatedAt);
            }
            _tmpUpdatedAt = __dateConverters.fromTimestamp(_tmp_3);
            _result = new TechnicianEntity(_tmpTechnicianId,_tmpName,_tmpEmail,_tmpPhone,_tmpRole,_tmpCertifications,_tmpActiveProjects,_tmpPermissions,_tmpActive,_tmpLastLogin,_tmpCreatedAt,_tmpUpdatedAt);
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
  public Object getAllTechnicians(final Continuation<? super List<TechnicianEntity>> $completion) {
    final String _sql = "SELECT * FROM technicians ORDER BY name ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<TechnicianEntity>>() {
      @Override
      @NonNull
      public List<TechnicianEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfTechnicianId = CursorUtil.getColumnIndexOrThrow(_cursor, "technician_id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfEmail = CursorUtil.getColumnIndexOrThrow(_cursor, "email");
          final int _cursorIndexOfPhone = CursorUtil.getColumnIndexOrThrow(_cursor, "phone");
          final int _cursorIndexOfRole = CursorUtil.getColumnIndexOrThrow(_cursor, "role");
          final int _cursorIndexOfCertifications = CursorUtil.getColumnIndexOrThrow(_cursor, "certifications");
          final int _cursorIndexOfActiveProjects = CursorUtil.getColumnIndexOrThrow(_cursor, "active_projects");
          final int _cursorIndexOfPermissions = CursorUtil.getColumnIndexOrThrow(_cursor, "permissions");
          final int _cursorIndexOfActive = CursorUtil.getColumnIndexOrThrow(_cursor, "active");
          final int _cursorIndexOfLastLogin = CursorUtil.getColumnIndexOrThrow(_cursor, "last_login");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final List<TechnicianEntity> _result = new ArrayList<TechnicianEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final TechnicianEntity _item;
            final String _tmpTechnicianId;
            if (_cursor.isNull(_cursorIndexOfTechnicianId)) {
              _tmpTechnicianId = null;
            } else {
              _tmpTechnicianId = _cursor.getString(_cursorIndexOfTechnicianId);
            }
            final String _tmpName;
            if (_cursor.isNull(_cursorIndexOfName)) {
              _tmpName = null;
            } else {
              _tmpName = _cursor.getString(_cursorIndexOfName);
            }
            final String _tmpEmail;
            if (_cursor.isNull(_cursorIndexOfEmail)) {
              _tmpEmail = null;
            } else {
              _tmpEmail = _cursor.getString(_cursorIndexOfEmail);
            }
            final String _tmpPhone;
            if (_cursor.isNull(_cursorIndexOfPhone)) {
              _tmpPhone = null;
            } else {
              _tmpPhone = _cursor.getString(_cursorIndexOfPhone);
            }
            final TechnicianRole _tmpRole;
            _tmpRole = __TechnicianRole_stringToEnum(_cursor.getString(_cursorIndexOfRole));
            final String _tmpCertifications;
            if (_cursor.isNull(_cursorIndexOfCertifications)) {
              _tmpCertifications = null;
            } else {
              _tmpCertifications = _cursor.getString(_cursorIndexOfCertifications);
            }
            final String _tmpActiveProjects;
            if (_cursor.isNull(_cursorIndexOfActiveProjects)) {
              _tmpActiveProjects = null;
            } else {
              _tmpActiveProjects = _cursor.getString(_cursorIndexOfActiveProjects);
            }
            final String _tmpPermissions;
            if (_cursor.isNull(_cursorIndexOfPermissions)) {
              _tmpPermissions = null;
            } else {
              _tmpPermissions = _cursor.getString(_cursorIndexOfPermissions);
            }
            final boolean _tmpActive;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfActive);
            _tmpActive = _tmp != 0;
            final Date _tmpLastLogin;
            final Long _tmp_1;
            if (_cursor.isNull(_cursorIndexOfLastLogin)) {
              _tmp_1 = null;
            } else {
              _tmp_1 = _cursor.getLong(_cursorIndexOfLastLogin);
            }
            _tmpLastLogin = __dateConverters.fromTimestamp(_tmp_1);
            final Date _tmpCreatedAt;
            final Long _tmp_2;
            if (_cursor.isNull(_cursorIndexOfCreatedAt)) {
              _tmp_2 = null;
            } else {
              _tmp_2 = _cursor.getLong(_cursorIndexOfCreatedAt);
            }
            _tmpCreatedAt = __dateConverters.fromTimestamp(_tmp_2);
            final Date _tmpUpdatedAt;
            final Long _tmp_3;
            if (_cursor.isNull(_cursorIndexOfUpdatedAt)) {
              _tmp_3 = null;
            } else {
              _tmp_3 = _cursor.getLong(_cursorIndexOfUpdatedAt);
            }
            _tmpUpdatedAt = __dateConverters.fromTimestamp(_tmp_3);
            _item = new TechnicianEntity(_tmpTechnicianId,_tmpName,_tmpEmail,_tmpPhone,_tmpRole,_tmpCertifications,_tmpActiveProjects,_tmpPermissions,_tmpActive,_tmpLastLogin,_tmpCreatedAt,_tmpUpdatedAt);
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
  public Flow<List<TechnicianEntity>> getAllTechniciansFlow() {
    final String _sql = "SELECT * FROM technicians ORDER BY name ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"technicians"}, new Callable<List<TechnicianEntity>>() {
      @Override
      @NonNull
      public List<TechnicianEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfTechnicianId = CursorUtil.getColumnIndexOrThrow(_cursor, "technician_id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfEmail = CursorUtil.getColumnIndexOrThrow(_cursor, "email");
          final int _cursorIndexOfPhone = CursorUtil.getColumnIndexOrThrow(_cursor, "phone");
          final int _cursorIndexOfRole = CursorUtil.getColumnIndexOrThrow(_cursor, "role");
          final int _cursorIndexOfCertifications = CursorUtil.getColumnIndexOrThrow(_cursor, "certifications");
          final int _cursorIndexOfActiveProjects = CursorUtil.getColumnIndexOrThrow(_cursor, "active_projects");
          final int _cursorIndexOfPermissions = CursorUtil.getColumnIndexOrThrow(_cursor, "permissions");
          final int _cursorIndexOfActive = CursorUtil.getColumnIndexOrThrow(_cursor, "active");
          final int _cursorIndexOfLastLogin = CursorUtil.getColumnIndexOrThrow(_cursor, "last_login");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final List<TechnicianEntity> _result = new ArrayList<TechnicianEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final TechnicianEntity _item;
            final String _tmpTechnicianId;
            if (_cursor.isNull(_cursorIndexOfTechnicianId)) {
              _tmpTechnicianId = null;
            } else {
              _tmpTechnicianId = _cursor.getString(_cursorIndexOfTechnicianId);
            }
            final String _tmpName;
            if (_cursor.isNull(_cursorIndexOfName)) {
              _tmpName = null;
            } else {
              _tmpName = _cursor.getString(_cursorIndexOfName);
            }
            final String _tmpEmail;
            if (_cursor.isNull(_cursorIndexOfEmail)) {
              _tmpEmail = null;
            } else {
              _tmpEmail = _cursor.getString(_cursorIndexOfEmail);
            }
            final String _tmpPhone;
            if (_cursor.isNull(_cursorIndexOfPhone)) {
              _tmpPhone = null;
            } else {
              _tmpPhone = _cursor.getString(_cursorIndexOfPhone);
            }
            final TechnicianRole _tmpRole;
            _tmpRole = __TechnicianRole_stringToEnum(_cursor.getString(_cursorIndexOfRole));
            final String _tmpCertifications;
            if (_cursor.isNull(_cursorIndexOfCertifications)) {
              _tmpCertifications = null;
            } else {
              _tmpCertifications = _cursor.getString(_cursorIndexOfCertifications);
            }
            final String _tmpActiveProjects;
            if (_cursor.isNull(_cursorIndexOfActiveProjects)) {
              _tmpActiveProjects = null;
            } else {
              _tmpActiveProjects = _cursor.getString(_cursorIndexOfActiveProjects);
            }
            final String _tmpPermissions;
            if (_cursor.isNull(_cursorIndexOfPermissions)) {
              _tmpPermissions = null;
            } else {
              _tmpPermissions = _cursor.getString(_cursorIndexOfPermissions);
            }
            final boolean _tmpActive;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfActive);
            _tmpActive = _tmp != 0;
            final Date _tmpLastLogin;
            final Long _tmp_1;
            if (_cursor.isNull(_cursorIndexOfLastLogin)) {
              _tmp_1 = null;
            } else {
              _tmp_1 = _cursor.getLong(_cursorIndexOfLastLogin);
            }
            _tmpLastLogin = __dateConverters.fromTimestamp(_tmp_1);
            final Date _tmpCreatedAt;
            final Long _tmp_2;
            if (_cursor.isNull(_cursorIndexOfCreatedAt)) {
              _tmp_2 = null;
            } else {
              _tmp_2 = _cursor.getLong(_cursorIndexOfCreatedAt);
            }
            _tmpCreatedAt = __dateConverters.fromTimestamp(_tmp_2);
            final Date _tmpUpdatedAt;
            final Long _tmp_3;
            if (_cursor.isNull(_cursorIndexOfUpdatedAt)) {
              _tmp_3 = null;
            } else {
              _tmp_3 = _cursor.getLong(_cursorIndexOfUpdatedAt);
            }
            _tmpUpdatedAt = __dateConverters.fromTimestamp(_tmp_3);
            _item = new TechnicianEntity(_tmpTechnicianId,_tmpName,_tmpEmail,_tmpPhone,_tmpRole,_tmpCertifications,_tmpActiveProjects,_tmpPermissions,_tmpActive,_tmpLastLogin,_tmpCreatedAt,_tmpUpdatedAt);
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
  public Object getActiveTechnicians(
      final Continuation<? super List<TechnicianEntity>> $completion) {
    final String _sql = "SELECT * FROM technicians WHERE isActive = 1 ORDER BY name ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<TechnicianEntity>>() {
      @Override
      @NonNull
      public List<TechnicianEntity> call() throws Exception {
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
  public Flow<List<TechnicianEntity>> getActiveTechniciansFlow() {
    final String _sql = "SELECT * FROM technicians WHERE isActive = 1 ORDER BY name ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"technicians"}, new Callable<List<TechnicianEntity>>() {
      @Override
      @NonNull
      public List<TechnicianEntity> call() throws Exception {
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
  public Object getTechniciansByRole(final String role,
      final Continuation<? super List<TechnicianEntity>> $completion) {
    final String _sql = "SELECT * FROM technicians WHERE role = ? ORDER BY name ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    if (role == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, role);
    }
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<TechnicianEntity>>() {
      @Override
      @NonNull
      public List<TechnicianEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfTechnicianId = CursorUtil.getColumnIndexOrThrow(_cursor, "technician_id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfEmail = CursorUtil.getColumnIndexOrThrow(_cursor, "email");
          final int _cursorIndexOfPhone = CursorUtil.getColumnIndexOrThrow(_cursor, "phone");
          final int _cursorIndexOfRole = CursorUtil.getColumnIndexOrThrow(_cursor, "role");
          final int _cursorIndexOfCertifications = CursorUtil.getColumnIndexOrThrow(_cursor, "certifications");
          final int _cursorIndexOfActiveProjects = CursorUtil.getColumnIndexOrThrow(_cursor, "active_projects");
          final int _cursorIndexOfPermissions = CursorUtil.getColumnIndexOrThrow(_cursor, "permissions");
          final int _cursorIndexOfActive = CursorUtil.getColumnIndexOrThrow(_cursor, "active");
          final int _cursorIndexOfLastLogin = CursorUtil.getColumnIndexOrThrow(_cursor, "last_login");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final List<TechnicianEntity> _result = new ArrayList<TechnicianEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final TechnicianEntity _item;
            final String _tmpTechnicianId;
            if (_cursor.isNull(_cursorIndexOfTechnicianId)) {
              _tmpTechnicianId = null;
            } else {
              _tmpTechnicianId = _cursor.getString(_cursorIndexOfTechnicianId);
            }
            final String _tmpName;
            if (_cursor.isNull(_cursorIndexOfName)) {
              _tmpName = null;
            } else {
              _tmpName = _cursor.getString(_cursorIndexOfName);
            }
            final String _tmpEmail;
            if (_cursor.isNull(_cursorIndexOfEmail)) {
              _tmpEmail = null;
            } else {
              _tmpEmail = _cursor.getString(_cursorIndexOfEmail);
            }
            final String _tmpPhone;
            if (_cursor.isNull(_cursorIndexOfPhone)) {
              _tmpPhone = null;
            } else {
              _tmpPhone = _cursor.getString(_cursorIndexOfPhone);
            }
            final TechnicianRole _tmpRole;
            _tmpRole = __TechnicianRole_stringToEnum(_cursor.getString(_cursorIndexOfRole));
            final String _tmpCertifications;
            if (_cursor.isNull(_cursorIndexOfCertifications)) {
              _tmpCertifications = null;
            } else {
              _tmpCertifications = _cursor.getString(_cursorIndexOfCertifications);
            }
            final String _tmpActiveProjects;
            if (_cursor.isNull(_cursorIndexOfActiveProjects)) {
              _tmpActiveProjects = null;
            } else {
              _tmpActiveProjects = _cursor.getString(_cursorIndexOfActiveProjects);
            }
            final String _tmpPermissions;
            if (_cursor.isNull(_cursorIndexOfPermissions)) {
              _tmpPermissions = null;
            } else {
              _tmpPermissions = _cursor.getString(_cursorIndexOfPermissions);
            }
            final boolean _tmpActive;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfActive);
            _tmpActive = _tmp != 0;
            final Date _tmpLastLogin;
            final Long _tmp_1;
            if (_cursor.isNull(_cursorIndexOfLastLogin)) {
              _tmp_1 = null;
            } else {
              _tmp_1 = _cursor.getLong(_cursorIndexOfLastLogin);
            }
            _tmpLastLogin = __dateConverters.fromTimestamp(_tmp_1);
            final Date _tmpCreatedAt;
            final Long _tmp_2;
            if (_cursor.isNull(_cursorIndexOfCreatedAt)) {
              _tmp_2 = null;
            } else {
              _tmp_2 = _cursor.getLong(_cursorIndexOfCreatedAt);
            }
            _tmpCreatedAt = __dateConverters.fromTimestamp(_tmp_2);
            final Date _tmpUpdatedAt;
            final Long _tmp_3;
            if (_cursor.isNull(_cursorIndexOfUpdatedAt)) {
              _tmp_3 = null;
            } else {
              _tmp_3 = _cursor.getLong(_cursorIndexOfUpdatedAt);
            }
            _tmpUpdatedAt = __dateConverters.fromTimestamp(_tmp_3);
            _item = new TechnicianEntity(_tmpTechnicianId,_tmpName,_tmpEmail,_tmpPhone,_tmpRole,_tmpCertifications,_tmpActiveProjects,_tmpPermissions,_tmpActive,_tmpLastLogin,_tmpCreatedAt,_tmpUpdatedAt);
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
  public Flow<List<TechnicianEntity>> getTechniciansByRoleFlow(final String role) {
    final String _sql = "SELECT * FROM technicians WHERE role = ? ORDER BY name ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    if (role == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, role);
    }
    return CoroutinesRoom.createFlow(__db, false, new String[] {"technicians"}, new Callable<List<TechnicianEntity>>() {
      @Override
      @NonNull
      public List<TechnicianEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfTechnicianId = CursorUtil.getColumnIndexOrThrow(_cursor, "technician_id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfEmail = CursorUtil.getColumnIndexOrThrow(_cursor, "email");
          final int _cursorIndexOfPhone = CursorUtil.getColumnIndexOrThrow(_cursor, "phone");
          final int _cursorIndexOfRole = CursorUtil.getColumnIndexOrThrow(_cursor, "role");
          final int _cursorIndexOfCertifications = CursorUtil.getColumnIndexOrThrow(_cursor, "certifications");
          final int _cursorIndexOfActiveProjects = CursorUtil.getColumnIndexOrThrow(_cursor, "active_projects");
          final int _cursorIndexOfPermissions = CursorUtil.getColumnIndexOrThrow(_cursor, "permissions");
          final int _cursorIndexOfActive = CursorUtil.getColumnIndexOrThrow(_cursor, "active");
          final int _cursorIndexOfLastLogin = CursorUtil.getColumnIndexOrThrow(_cursor, "last_login");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final List<TechnicianEntity> _result = new ArrayList<TechnicianEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final TechnicianEntity _item;
            final String _tmpTechnicianId;
            if (_cursor.isNull(_cursorIndexOfTechnicianId)) {
              _tmpTechnicianId = null;
            } else {
              _tmpTechnicianId = _cursor.getString(_cursorIndexOfTechnicianId);
            }
            final String _tmpName;
            if (_cursor.isNull(_cursorIndexOfName)) {
              _tmpName = null;
            } else {
              _tmpName = _cursor.getString(_cursorIndexOfName);
            }
            final String _tmpEmail;
            if (_cursor.isNull(_cursorIndexOfEmail)) {
              _tmpEmail = null;
            } else {
              _tmpEmail = _cursor.getString(_cursorIndexOfEmail);
            }
            final String _tmpPhone;
            if (_cursor.isNull(_cursorIndexOfPhone)) {
              _tmpPhone = null;
            } else {
              _tmpPhone = _cursor.getString(_cursorIndexOfPhone);
            }
            final TechnicianRole _tmpRole;
            _tmpRole = __TechnicianRole_stringToEnum(_cursor.getString(_cursorIndexOfRole));
            final String _tmpCertifications;
            if (_cursor.isNull(_cursorIndexOfCertifications)) {
              _tmpCertifications = null;
            } else {
              _tmpCertifications = _cursor.getString(_cursorIndexOfCertifications);
            }
            final String _tmpActiveProjects;
            if (_cursor.isNull(_cursorIndexOfActiveProjects)) {
              _tmpActiveProjects = null;
            } else {
              _tmpActiveProjects = _cursor.getString(_cursorIndexOfActiveProjects);
            }
            final String _tmpPermissions;
            if (_cursor.isNull(_cursorIndexOfPermissions)) {
              _tmpPermissions = null;
            } else {
              _tmpPermissions = _cursor.getString(_cursorIndexOfPermissions);
            }
            final boolean _tmpActive;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfActive);
            _tmpActive = _tmp != 0;
            final Date _tmpLastLogin;
            final Long _tmp_1;
            if (_cursor.isNull(_cursorIndexOfLastLogin)) {
              _tmp_1 = null;
            } else {
              _tmp_1 = _cursor.getLong(_cursorIndexOfLastLogin);
            }
            _tmpLastLogin = __dateConverters.fromTimestamp(_tmp_1);
            final Date _tmpCreatedAt;
            final Long _tmp_2;
            if (_cursor.isNull(_cursorIndexOfCreatedAt)) {
              _tmp_2 = null;
            } else {
              _tmp_2 = _cursor.getLong(_cursorIndexOfCreatedAt);
            }
            _tmpCreatedAt = __dateConverters.fromTimestamp(_tmp_2);
            final Date _tmpUpdatedAt;
            final Long _tmp_3;
            if (_cursor.isNull(_cursorIndexOfUpdatedAt)) {
              _tmp_3 = null;
            } else {
              _tmp_3 = _cursor.getLong(_cursorIndexOfUpdatedAt);
            }
            _tmpUpdatedAt = __dateConverters.fromTimestamp(_tmp_3);
            _item = new TechnicianEntity(_tmpTechnicianId,_tmpName,_tmpEmail,_tmpPhone,_tmpRole,_tmpCertifications,_tmpActiveProjects,_tmpPermissions,_tmpActive,_tmpLastLogin,_tmpCreatedAt,_tmpUpdatedAt);
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
  public Object getActiveTechnicianCount(final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT COUNT(*) FROM technicians WHERE isActive = 1";
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
  public Object getTotalTechnicianCount(final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT COUNT(*) FROM technicians";
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
  public Object getTechnicianCountByRole(final String role,
      final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT COUNT(*) FROM technicians WHERE role = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    if (role == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, role);
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
  public Object getTechnicianCountByRole(
      final Continuation<? super Map<String, Integer>> $completion) {
    final String _sql = "SELECT role, COUNT(*) as count FROM technicians GROUP BY role ORDER BY count DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Map<String, Integer>>() {
      @Override
      @NonNull
      public Map<String, Integer> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Map<String, Integer> _result = new LinkedHashMap<String, Integer>();
          while (_cursor.moveToNext()) {
            final String _key;
            _key = new String();
            if () {
              _result.put(_key, null);
              continue;
            }
            final Integer _value;
            _value = new Integer();
            if (!_result.containsKey(_key)) {
              _result.put(_key, _value);
            }
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
  public Object searchTechnicians(final String query,
      final Continuation<? super List<TechnicianEntity>> $completion) {
    final String _sql = "SELECT * FROM technicians WHERE name LIKE '%' || ? || '%' OR email LIKE '%' || ? || '%' ORDER BY name ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    if (query == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, query);
    }
    _argIndex = 2;
    if (query == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, query);
    }
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<TechnicianEntity>>() {
      @Override
      @NonNull
      public List<TechnicianEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfTechnicianId = CursorUtil.getColumnIndexOrThrow(_cursor, "technician_id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfEmail = CursorUtil.getColumnIndexOrThrow(_cursor, "email");
          final int _cursorIndexOfPhone = CursorUtil.getColumnIndexOrThrow(_cursor, "phone");
          final int _cursorIndexOfRole = CursorUtil.getColumnIndexOrThrow(_cursor, "role");
          final int _cursorIndexOfCertifications = CursorUtil.getColumnIndexOrThrow(_cursor, "certifications");
          final int _cursorIndexOfActiveProjects = CursorUtil.getColumnIndexOrThrow(_cursor, "active_projects");
          final int _cursorIndexOfPermissions = CursorUtil.getColumnIndexOrThrow(_cursor, "permissions");
          final int _cursorIndexOfActive = CursorUtil.getColumnIndexOrThrow(_cursor, "active");
          final int _cursorIndexOfLastLogin = CursorUtil.getColumnIndexOrThrow(_cursor, "last_login");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final List<TechnicianEntity> _result = new ArrayList<TechnicianEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final TechnicianEntity _item;
            final String _tmpTechnicianId;
            if (_cursor.isNull(_cursorIndexOfTechnicianId)) {
              _tmpTechnicianId = null;
            } else {
              _tmpTechnicianId = _cursor.getString(_cursorIndexOfTechnicianId);
            }
            final String _tmpName;
            if (_cursor.isNull(_cursorIndexOfName)) {
              _tmpName = null;
            } else {
              _tmpName = _cursor.getString(_cursorIndexOfName);
            }
            final String _tmpEmail;
            if (_cursor.isNull(_cursorIndexOfEmail)) {
              _tmpEmail = null;
            } else {
              _tmpEmail = _cursor.getString(_cursorIndexOfEmail);
            }
            final String _tmpPhone;
            if (_cursor.isNull(_cursorIndexOfPhone)) {
              _tmpPhone = null;
            } else {
              _tmpPhone = _cursor.getString(_cursorIndexOfPhone);
            }
            final TechnicianRole _tmpRole;
            _tmpRole = __TechnicianRole_stringToEnum(_cursor.getString(_cursorIndexOfRole));
            final String _tmpCertifications;
            if (_cursor.isNull(_cursorIndexOfCertifications)) {
              _tmpCertifications = null;
            } else {
              _tmpCertifications = _cursor.getString(_cursorIndexOfCertifications);
            }
            final String _tmpActiveProjects;
            if (_cursor.isNull(_cursorIndexOfActiveProjects)) {
              _tmpActiveProjects = null;
            } else {
              _tmpActiveProjects = _cursor.getString(_cursorIndexOfActiveProjects);
            }
            final String _tmpPermissions;
            if (_cursor.isNull(_cursorIndexOfPermissions)) {
              _tmpPermissions = null;
            } else {
              _tmpPermissions = _cursor.getString(_cursorIndexOfPermissions);
            }
            final boolean _tmpActive;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfActive);
            _tmpActive = _tmp != 0;
            final Date _tmpLastLogin;
            final Long _tmp_1;
            if (_cursor.isNull(_cursorIndexOfLastLogin)) {
              _tmp_1 = null;
            } else {
              _tmp_1 = _cursor.getLong(_cursorIndexOfLastLogin);
            }
            _tmpLastLogin = __dateConverters.fromTimestamp(_tmp_1);
            final Date _tmpCreatedAt;
            final Long _tmp_2;
            if (_cursor.isNull(_cursorIndexOfCreatedAt)) {
              _tmp_2 = null;
            } else {
              _tmp_2 = _cursor.getLong(_cursorIndexOfCreatedAt);
            }
            _tmpCreatedAt = __dateConverters.fromTimestamp(_tmp_2);
            final Date _tmpUpdatedAt;
            final Long _tmp_3;
            if (_cursor.isNull(_cursorIndexOfUpdatedAt)) {
              _tmp_3 = null;
            } else {
              _tmp_3 = _cursor.getLong(_cursorIndexOfUpdatedAt);
            }
            _tmpUpdatedAt = __dateConverters.fromTimestamp(_tmp_3);
            _item = new TechnicianEntity(_tmpTechnicianId,_tmpName,_tmpEmail,_tmpPhone,_tmpRole,_tmpCertifications,_tmpActiveProjects,_tmpPermissions,_tmpActive,_tmpLastLogin,_tmpCreatedAt,_tmpUpdatedAt);
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
  public Object getTechniciansByLastLoginRange(final long startTime, final long endTime,
      final Continuation<? super List<TechnicianEntity>> $completion) {
    final String _sql = "SELECT * FROM technicians WHERE lastLoginAt BETWEEN ? AND ? ORDER BY lastLoginAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, startTime);
    _argIndex = 2;
    _statement.bindLong(_argIndex, endTime);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<TechnicianEntity>>() {
      @Override
      @NonNull
      public List<TechnicianEntity> call() throws Exception {
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
  public Object getAverageTimeSinceLastLogin(final Continuation<? super Long> $completion) {
    final String _sql = "SELECT AVG(System.currentTimeMillis() - lastLoginAt) FROM technicians WHERE isActive = 1 AND lastLoginAt IS NOT NULL";
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

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }

  private String __TechnicianRole_enumToString(@NonNull final TechnicianRole _value) {
    switch (_value) {
      case SUSPENDED: return "SUSPENDED";
      case TRAINEE: return "TRAINEE";
      case TECHNICIAN: return "TECHNICIAN";
      case SENIOR_TECHNICIAN: return "SENIOR_TECHNICIAN";
      case SUPERVISOR: return "SUPERVISOR";
      case MANAGER: return "MANAGER";
      case ADMIN: return "ADMIN";
      default: throw new IllegalArgumentException("Can't convert enum to string, unknown enum value: " + _value);
    }
  }

  private TechnicianRole __TechnicianRole_stringToEnum(@NonNull final String _value) {
    switch (_value) {
      case "SUSPENDED": return TechnicianRole.SUSPENDED;
      case "TRAINEE": return TechnicianRole.TRAINEE;
      case "TECHNICIAN": return TechnicianRole.TECHNICIAN;
      case "SENIOR_TECHNICIAN": return TechnicianRole.SENIOR_TECHNICIAN;
      case "SUPERVISOR": return TechnicianRole.SUPERVISOR;
      case "MANAGER": return TechnicianRole.MANAGER;
      case "ADMIN": return TechnicianRole.ADMIN;
      default: throw new IllegalArgumentException("Can't convert value to enum, unknown value: " + _value);
    }
  }
}
