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
import com.fibreflow.core.database.converters.StatusConverters;
import com.fibreflow.core.database.entities.ActivationStatus;
import com.fibreflow.core.database.entities.DropEntity;
import com.fibreflow.core.database.entities.DropStatus;
import com.fibreflow.core.database.entities.SyncStatus;
import java.lang.Class;
import java.lang.Double;
import java.lang.Exception;
import java.lang.Float;
import java.lang.IllegalArgumentException;
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
public final class DropDao_Impl implements DropDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<DropEntity> __insertionAdapterOfDropEntity;

  private final DateConverters __dateConverters = new DateConverters();

  private final StatusConverters __statusConverters = new StatusConverters();

  private final EntityDeletionOrUpdateAdapter<DropEntity> __deletionAdapterOfDropEntity;

  private final EntityDeletionOrUpdateAdapter<DropEntity> __updateAdapterOfDropEntity;

  private final SharedSQLiteStatement __preparedStmtOfUpdateDropStatus;

  private final SharedSQLiteStatement __preparedStmtOfAssignDropToTechnician;

  private final SharedSQLiteStatement __preparedStmtOfUnassignDrop;

  private final SharedSQLiteStatement __preparedStmtOfMarkDropCompleted;

  private final SharedSQLiteStatement __preparedStmtOfUpdateDropLocation;

  private final SharedSQLiteStatement __preparedStmtOfUpdateDropPriority;

  private final SharedSQLiteStatement __preparedStmtOfMarkDropForSync;

  private final SharedSQLiteStatement __preparedStmtOfMarkDropSynced;

  private final SharedSQLiteStatement __preparedStmtOfUpdateDropNotes;

  public DropDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfDropEntity = new EntityInsertionAdapter<DropEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `drops` (`drop_number`,`project_id`,`latitude`,`longitude`,`altitude`,`accuracy`,`address`,`status`,`assigned_technician_id`,`customer_name`,`customer_phone`,`customer_email`,`installation_date`,`activation_status`,`activation_date`,`notes`,`priority`,`created_at`,`updated_at`,`sync_status`,`last_sync_attempt`,`sync_error`,`assigned_at`,`completed_at`,`needs_sync`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final DropEntity entity) {
        if (entity.getDropNumber() == null) {
          statement.bindNull(1);
        } else {
          statement.bindString(1, entity.getDropNumber());
        }
        statement.bindLong(2, entity.getProjectId());
        statement.bindDouble(3, entity.getLatitude());
        statement.bindDouble(4, entity.getLongitude());
        if (entity.getAltitude() == null) {
          statement.bindNull(5);
        } else {
          statement.bindDouble(5, entity.getAltitude());
        }
        if (entity.getAccuracy() == null) {
          statement.bindNull(6);
        } else {
          statement.bindDouble(6, entity.getAccuracy());
        }
        if (entity.getAddress() == null) {
          statement.bindNull(7);
        } else {
          statement.bindString(7, entity.getAddress());
        }
        statement.bindString(8, __DropStatus_enumToString(entity.getStatus()));
        if (entity.getAssignedTechnicianId() == null) {
          statement.bindNull(9);
        } else {
          statement.bindString(9, entity.getAssignedTechnicianId());
        }
        if (entity.getCustomerName() == null) {
          statement.bindNull(10);
        } else {
          statement.bindString(10, entity.getCustomerName());
        }
        if (entity.getCustomerPhone() == null) {
          statement.bindNull(11);
        } else {
          statement.bindString(11, entity.getCustomerPhone());
        }
        if (entity.getCustomerEmail() == null) {
          statement.bindNull(12);
        } else {
          statement.bindString(12, entity.getCustomerEmail());
        }
        final Long _tmp = __dateConverters.dateToTimestamp(entity.getInstallationDate());
        if (_tmp == null) {
          statement.bindNull(13);
        } else {
          statement.bindLong(13, _tmp);
        }
        statement.bindString(14, __ActivationStatus_enumToString(entity.getActivationStatus()));
        final Long _tmp_1 = __dateConverters.dateToTimestamp(entity.getActivationDate());
        if (_tmp_1 == null) {
          statement.bindNull(15);
        } else {
          statement.bindLong(15, _tmp_1);
        }
        if (entity.getNotes() == null) {
          statement.bindNull(16);
        } else {
          statement.bindString(16, entity.getNotes());
        }
        statement.bindLong(17, entity.getPriority());
        final Long _tmp_2 = __dateConverters.dateToTimestamp(entity.getCreatedAt());
        if (_tmp_2 == null) {
          statement.bindNull(18);
        } else {
          statement.bindLong(18, _tmp_2);
        }
        final Long _tmp_3 = __dateConverters.dateToTimestamp(entity.getUpdatedAt());
        if (_tmp_3 == null) {
          statement.bindNull(19);
        } else {
          statement.bindLong(19, _tmp_3);
        }
        final String _tmp_4 = __statusConverters.fromSyncStatus(entity.getSyncStatus());
        if (_tmp_4 == null) {
          statement.bindNull(20);
        } else {
          statement.bindString(20, _tmp_4);
        }
        final Long _tmp_5 = __dateConverters.dateToTimestamp(entity.getLastSyncAttempt());
        if (_tmp_5 == null) {
          statement.bindNull(21);
        } else {
          statement.bindLong(21, _tmp_5);
        }
        if (entity.getSyncError() == null) {
          statement.bindNull(22);
        } else {
          statement.bindString(22, entity.getSyncError());
        }
        final Long _tmp_6 = __dateConverters.dateToTimestamp(entity.getAssignedAt());
        if (_tmp_6 == null) {
          statement.bindNull(23);
        } else {
          statement.bindLong(23, _tmp_6);
        }
        final Long _tmp_7 = __dateConverters.dateToTimestamp(entity.getCompletedAt());
        if (_tmp_7 == null) {
          statement.bindNull(24);
        } else {
          statement.bindLong(24, _tmp_7);
        }
        final int _tmp_8 = entity.getNeedsSync() ? 1 : 0;
        statement.bindLong(25, _tmp_8);
      }
    };
    this.__deletionAdapterOfDropEntity = new EntityDeletionOrUpdateAdapter<DropEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `drops` WHERE `drop_number` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final DropEntity entity) {
        if (entity.getDropNumber() == null) {
          statement.bindNull(1);
        } else {
          statement.bindString(1, entity.getDropNumber());
        }
      }
    };
    this.__updateAdapterOfDropEntity = new EntityDeletionOrUpdateAdapter<DropEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `drops` SET `drop_number` = ?,`project_id` = ?,`latitude` = ?,`longitude` = ?,`altitude` = ?,`accuracy` = ?,`address` = ?,`status` = ?,`assigned_technician_id` = ?,`customer_name` = ?,`customer_phone` = ?,`customer_email` = ?,`installation_date` = ?,`activation_status` = ?,`activation_date` = ?,`notes` = ?,`priority` = ?,`created_at` = ?,`updated_at` = ?,`sync_status` = ?,`last_sync_attempt` = ?,`sync_error` = ?,`assigned_at` = ?,`completed_at` = ?,`needs_sync` = ? WHERE `drop_number` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final DropEntity entity) {
        if (entity.getDropNumber() == null) {
          statement.bindNull(1);
        } else {
          statement.bindString(1, entity.getDropNumber());
        }
        statement.bindLong(2, entity.getProjectId());
        statement.bindDouble(3, entity.getLatitude());
        statement.bindDouble(4, entity.getLongitude());
        if (entity.getAltitude() == null) {
          statement.bindNull(5);
        } else {
          statement.bindDouble(5, entity.getAltitude());
        }
        if (entity.getAccuracy() == null) {
          statement.bindNull(6);
        } else {
          statement.bindDouble(6, entity.getAccuracy());
        }
        if (entity.getAddress() == null) {
          statement.bindNull(7);
        } else {
          statement.bindString(7, entity.getAddress());
        }
        statement.bindString(8, __DropStatus_enumToString(entity.getStatus()));
        if (entity.getAssignedTechnicianId() == null) {
          statement.bindNull(9);
        } else {
          statement.bindString(9, entity.getAssignedTechnicianId());
        }
        if (entity.getCustomerName() == null) {
          statement.bindNull(10);
        } else {
          statement.bindString(10, entity.getCustomerName());
        }
        if (entity.getCustomerPhone() == null) {
          statement.bindNull(11);
        } else {
          statement.bindString(11, entity.getCustomerPhone());
        }
        if (entity.getCustomerEmail() == null) {
          statement.bindNull(12);
        } else {
          statement.bindString(12, entity.getCustomerEmail());
        }
        final Long _tmp = __dateConverters.dateToTimestamp(entity.getInstallationDate());
        if (_tmp == null) {
          statement.bindNull(13);
        } else {
          statement.bindLong(13, _tmp);
        }
        statement.bindString(14, __ActivationStatus_enumToString(entity.getActivationStatus()));
        final Long _tmp_1 = __dateConverters.dateToTimestamp(entity.getActivationDate());
        if (_tmp_1 == null) {
          statement.bindNull(15);
        } else {
          statement.bindLong(15, _tmp_1);
        }
        if (entity.getNotes() == null) {
          statement.bindNull(16);
        } else {
          statement.bindString(16, entity.getNotes());
        }
        statement.bindLong(17, entity.getPriority());
        final Long _tmp_2 = __dateConverters.dateToTimestamp(entity.getCreatedAt());
        if (_tmp_2 == null) {
          statement.bindNull(18);
        } else {
          statement.bindLong(18, _tmp_2);
        }
        final Long _tmp_3 = __dateConverters.dateToTimestamp(entity.getUpdatedAt());
        if (_tmp_3 == null) {
          statement.bindNull(19);
        } else {
          statement.bindLong(19, _tmp_3);
        }
        final String _tmp_4 = __statusConverters.fromSyncStatus(entity.getSyncStatus());
        if (_tmp_4 == null) {
          statement.bindNull(20);
        } else {
          statement.bindString(20, _tmp_4);
        }
        final Long _tmp_5 = __dateConverters.dateToTimestamp(entity.getLastSyncAttempt());
        if (_tmp_5 == null) {
          statement.bindNull(21);
        } else {
          statement.bindLong(21, _tmp_5);
        }
        if (entity.getSyncError() == null) {
          statement.bindNull(22);
        } else {
          statement.bindString(22, entity.getSyncError());
        }
        final Long _tmp_6 = __dateConverters.dateToTimestamp(entity.getAssignedAt());
        if (_tmp_6 == null) {
          statement.bindNull(23);
        } else {
          statement.bindLong(23, _tmp_6);
        }
        final Long _tmp_7 = __dateConverters.dateToTimestamp(entity.getCompletedAt());
        if (_tmp_7 == null) {
          statement.bindNull(24);
        } else {
          statement.bindLong(24, _tmp_7);
        }
        final int _tmp_8 = entity.getNeedsSync() ? 1 : 0;
        statement.bindLong(25, _tmp_8);
        if (entity.getDropNumber() == null) {
          statement.bindNull(26);
        } else {
          statement.bindString(26, entity.getDropNumber());
        }
      }
    };
    this.__preparedStmtOfUpdateDropStatus = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE drops SET status = ?, updated_at = ? WHERE drop_number = ?";
        return _query;
      }
    };
    this.__preparedStmtOfAssignDropToTechnician = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE drops SET assigned_technician_id = ?, status = 'IN_PROGRESS', assigned_at = ?, updated_at = ? WHERE drop_number = ?";
        return _query;
      }
    };
    this.__preparedStmtOfUnassignDrop = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE drops SET assigned_technician_id = NULL, status = 'AVAILABLE', assigned_at = NULL, updated_at = ? WHERE drop_number = ?";
        return _query;
      }
    };
    this.__preparedStmtOfMarkDropCompleted = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE drops SET status = 'COMPLETED', completed_at = ?, updated_at = ? WHERE drop_number = ?";
        return _query;
      }
    };
    this.__preparedStmtOfUpdateDropLocation = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE drops SET latitude = ?, longitude = ?, updated_at = ? WHERE drop_number = ?";
        return _query;
      }
    };
    this.__preparedStmtOfUpdateDropPriority = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE drops SET priority = ?, updated_at = ? WHERE drop_number = ?";
        return _query;
      }
    };
    this.__preparedStmtOfMarkDropForSync = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE drops SET needs_sync = 1, updated_at = ? WHERE drop_number = ?";
        return _query;
      }
    };
    this.__preparedStmtOfMarkDropSynced = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE drops SET needs_sync = 0, last_sync_attempt = ? WHERE drop_number = ?";
        return _query;
      }
    };
    this.__preparedStmtOfUpdateDropNotes = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE drops SET notes = ?, updated_at = ? WHERE drop_number = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insertDrop(final DropEntity drop, final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfDropEntity.insertAndReturnId(drop);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertDrops(final List<DropEntity> drops,
      final Continuation<? super List<Long>> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<List<Long>>() {
      @Override
      @NonNull
      public List<Long> call() throws Exception {
        __db.beginTransaction();
        try {
          final List<Long> _result = __insertionAdapterOfDropEntity.insertAndReturnIdsList(drops);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteDrop(final DropEntity drop, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfDropEntity.handle(drop);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteDrops(final List<DropEntity> drops,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfDropEntity.handleMultiple(drops);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateDrop(final DropEntity drop, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfDropEntity.handle(drop);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateDrops(final List<DropEntity> drops,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfDropEntity.handleMultiple(drops);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateDropStatus(final String dropId, final String status, final long timestamp,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfUpdateDropStatus.acquire();
        int _argIndex = 1;
        if (status == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindString(_argIndex, status);
        }
        _argIndex = 2;
        _stmt.bindLong(_argIndex, timestamp);
        _argIndex = 3;
        if (dropId == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindString(_argIndex, dropId);
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
          __preparedStmtOfUpdateDropStatus.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object assignDropToTechnician(final String dropId, final String technicianId,
      final long timestamp, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfAssignDropToTechnician.acquire();
        int _argIndex = 1;
        if (technicianId == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindString(_argIndex, technicianId);
        }
        _argIndex = 2;
        _stmt.bindLong(_argIndex, timestamp);
        _argIndex = 3;
        _stmt.bindLong(_argIndex, timestamp);
        _argIndex = 4;
        if (dropId == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindString(_argIndex, dropId);
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
          __preparedStmtOfAssignDropToTechnician.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object unassignDrop(final String dropId, final long timestamp,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfUnassignDrop.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, timestamp);
        _argIndex = 2;
        if (dropId == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindString(_argIndex, dropId);
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
          __preparedStmtOfUnassignDrop.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object markDropCompleted(final String dropId, final long timestamp,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfMarkDropCompleted.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, timestamp);
        _argIndex = 2;
        _stmt.bindLong(_argIndex, timestamp);
        _argIndex = 3;
        if (dropId == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindString(_argIndex, dropId);
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
          __preparedStmtOfMarkDropCompleted.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object updateDropLocation(final String dropId, final double latitude,
      final double longitude, final long timestamp, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfUpdateDropLocation.acquire();
        int _argIndex = 1;
        _stmt.bindDouble(_argIndex, latitude);
        _argIndex = 2;
        _stmt.bindDouble(_argIndex, longitude);
        _argIndex = 3;
        _stmt.bindLong(_argIndex, timestamp);
        _argIndex = 4;
        if (dropId == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindString(_argIndex, dropId);
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
          __preparedStmtOfUpdateDropLocation.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object updateDropPriority(final String dropId, final int priority, final long timestamp,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfUpdateDropPriority.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, priority);
        _argIndex = 2;
        _stmt.bindLong(_argIndex, timestamp);
        _argIndex = 3;
        if (dropId == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindString(_argIndex, dropId);
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
          __preparedStmtOfUpdateDropPriority.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object markDropForSync(final String dropId, final long timestamp,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfMarkDropForSync.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, timestamp);
        _argIndex = 2;
        if (dropId == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindString(_argIndex, dropId);
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
          __preparedStmtOfMarkDropForSync.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object markDropSynced(final String dropId, final long timestamp,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfMarkDropSynced.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, timestamp);
        _argIndex = 2;
        if (dropId == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindString(_argIndex, dropId);
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
          __preparedStmtOfMarkDropSynced.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object updateDropNotes(final String dropId, final String notes, final long timestamp,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfUpdateDropNotes.acquire();
        int _argIndex = 1;
        if (notes == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindString(_argIndex, notes);
        }
        _argIndex = 2;
        _stmt.bindLong(_argIndex, timestamp);
        _argIndex = 3;
        if (dropId == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindString(_argIndex, dropId);
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
          __preparedStmtOfUpdateDropNotes.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object getDropById(final String dropId,
      final Continuation<? super DropEntity> $completion) {
    final String _sql = "SELECT * FROM drops WHERE drop_number = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    if (dropId == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, dropId);
    }
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<DropEntity>() {
      @Override
      @Nullable
      public DropEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfDropNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "drop_number");
          final int _cursorIndexOfProjectId = CursorUtil.getColumnIndexOrThrow(_cursor, "project_id");
          final int _cursorIndexOfLatitude = CursorUtil.getColumnIndexOrThrow(_cursor, "latitude");
          final int _cursorIndexOfLongitude = CursorUtil.getColumnIndexOrThrow(_cursor, "longitude");
          final int _cursorIndexOfAltitude = CursorUtil.getColumnIndexOrThrow(_cursor, "altitude");
          final int _cursorIndexOfAccuracy = CursorUtil.getColumnIndexOrThrow(_cursor, "accuracy");
          final int _cursorIndexOfAddress = CursorUtil.getColumnIndexOrThrow(_cursor, "address");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfAssignedTechnicianId = CursorUtil.getColumnIndexOrThrow(_cursor, "assigned_technician_id");
          final int _cursorIndexOfCustomerName = CursorUtil.getColumnIndexOrThrow(_cursor, "customer_name");
          final int _cursorIndexOfCustomerPhone = CursorUtil.getColumnIndexOrThrow(_cursor, "customer_phone");
          final int _cursorIndexOfCustomerEmail = CursorUtil.getColumnIndexOrThrow(_cursor, "customer_email");
          final int _cursorIndexOfInstallationDate = CursorUtil.getColumnIndexOrThrow(_cursor, "installation_date");
          final int _cursorIndexOfActivationStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "activation_status");
          final int _cursorIndexOfActivationDate = CursorUtil.getColumnIndexOrThrow(_cursor, "activation_date");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfPriority = CursorUtil.getColumnIndexOrThrow(_cursor, "priority");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final int _cursorIndexOfSyncStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "sync_status");
          final int _cursorIndexOfLastSyncAttempt = CursorUtil.getColumnIndexOrThrow(_cursor, "last_sync_attempt");
          final int _cursorIndexOfSyncError = CursorUtil.getColumnIndexOrThrow(_cursor, "sync_error");
          final int _cursorIndexOfAssignedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "assigned_at");
          final int _cursorIndexOfCompletedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "completed_at");
          final int _cursorIndexOfNeedsSync = CursorUtil.getColumnIndexOrThrow(_cursor, "needs_sync");
          final DropEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpDropNumber;
            if (_cursor.isNull(_cursorIndexOfDropNumber)) {
              _tmpDropNumber = null;
            } else {
              _tmpDropNumber = _cursor.getString(_cursorIndexOfDropNumber);
            }
            final int _tmpProjectId;
            _tmpProjectId = _cursor.getInt(_cursorIndexOfProjectId);
            final double _tmpLatitude;
            _tmpLatitude = _cursor.getDouble(_cursorIndexOfLatitude);
            final double _tmpLongitude;
            _tmpLongitude = _cursor.getDouble(_cursorIndexOfLongitude);
            final Double _tmpAltitude;
            if (_cursor.isNull(_cursorIndexOfAltitude)) {
              _tmpAltitude = null;
            } else {
              _tmpAltitude = _cursor.getDouble(_cursorIndexOfAltitude);
            }
            final Float _tmpAccuracy;
            if (_cursor.isNull(_cursorIndexOfAccuracy)) {
              _tmpAccuracy = null;
            } else {
              _tmpAccuracy = _cursor.getFloat(_cursorIndexOfAccuracy);
            }
            final String _tmpAddress;
            if (_cursor.isNull(_cursorIndexOfAddress)) {
              _tmpAddress = null;
            } else {
              _tmpAddress = _cursor.getString(_cursorIndexOfAddress);
            }
            final DropStatus _tmpStatus;
            _tmpStatus = __DropStatus_stringToEnum(_cursor.getString(_cursorIndexOfStatus));
            final String _tmpAssignedTechnicianId;
            if (_cursor.isNull(_cursorIndexOfAssignedTechnicianId)) {
              _tmpAssignedTechnicianId = null;
            } else {
              _tmpAssignedTechnicianId = _cursor.getString(_cursorIndexOfAssignedTechnicianId);
            }
            final String _tmpCustomerName;
            if (_cursor.isNull(_cursorIndexOfCustomerName)) {
              _tmpCustomerName = null;
            } else {
              _tmpCustomerName = _cursor.getString(_cursorIndexOfCustomerName);
            }
            final String _tmpCustomerPhone;
            if (_cursor.isNull(_cursorIndexOfCustomerPhone)) {
              _tmpCustomerPhone = null;
            } else {
              _tmpCustomerPhone = _cursor.getString(_cursorIndexOfCustomerPhone);
            }
            final String _tmpCustomerEmail;
            if (_cursor.isNull(_cursorIndexOfCustomerEmail)) {
              _tmpCustomerEmail = null;
            } else {
              _tmpCustomerEmail = _cursor.getString(_cursorIndexOfCustomerEmail);
            }
            final Date _tmpInstallationDate;
            final Long _tmp;
            if (_cursor.isNull(_cursorIndexOfInstallationDate)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getLong(_cursorIndexOfInstallationDate);
            }
            _tmpInstallationDate = __dateConverters.fromTimestamp(_tmp);
            final ActivationStatus _tmpActivationStatus;
            _tmpActivationStatus = __ActivationStatus_stringToEnum(_cursor.getString(_cursorIndexOfActivationStatus));
            final Date _tmpActivationDate;
            final Long _tmp_1;
            if (_cursor.isNull(_cursorIndexOfActivationDate)) {
              _tmp_1 = null;
            } else {
              _tmp_1 = _cursor.getLong(_cursorIndexOfActivationDate);
            }
            _tmpActivationDate = __dateConverters.fromTimestamp(_tmp_1);
            final String _tmpNotes;
            if (_cursor.isNull(_cursorIndexOfNotes)) {
              _tmpNotes = null;
            } else {
              _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
            }
            final int _tmpPriority;
            _tmpPriority = _cursor.getInt(_cursorIndexOfPriority);
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
            final SyncStatus _tmpSyncStatus;
            final String _tmp_4;
            if (_cursor.isNull(_cursorIndexOfSyncStatus)) {
              _tmp_4 = null;
            } else {
              _tmp_4 = _cursor.getString(_cursorIndexOfSyncStatus);
            }
            _tmpSyncStatus = __statusConverters.toSyncStatus(_tmp_4);
            final Date _tmpLastSyncAttempt;
            final Long _tmp_5;
            if (_cursor.isNull(_cursorIndexOfLastSyncAttempt)) {
              _tmp_5 = null;
            } else {
              _tmp_5 = _cursor.getLong(_cursorIndexOfLastSyncAttempt);
            }
            _tmpLastSyncAttempt = __dateConverters.fromTimestamp(_tmp_5);
            final String _tmpSyncError;
            if (_cursor.isNull(_cursorIndexOfSyncError)) {
              _tmpSyncError = null;
            } else {
              _tmpSyncError = _cursor.getString(_cursorIndexOfSyncError);
            }
            final Date _tmpAssignedAt;
            final Long _tmp_6;
            if (_cursor.isNull(_cursorIndexOfAssignedAt)) {
              _tmp_6 = null;
            } else {
              _tmp_6 = _cursor.getLong(_cursorIndexOfAssignedAt);
            }
            _tmpAssignedAt = __dateConverters.fromTimestamp(_tmp_6);
            final Date _tmpCompletedAt;
            final Long _tmp_7;
            if (_cursor.isNull(_cursorIndexOfCompletedAt)) {
              _tmp_7 = null;
            } else {
              _tmp_7 = _cursor.getLong(_cursorIndexOfCompletedAt);
            }
            _tmpCompletedAt = __dateConverters.fromTimestamp(_tmp_7);
            final boolean _tmpNeedsSync;
            final int _tmp_8;
            _tmp_8 = _cursor.getInt(_cursorIndexOfNeedsSync);
            _tmpNeedsSync = _tmp_8 != 0;
            _result = new DropEntity(_tmpDropNumber,_tmpProjectId,_tmpLatitude,_tmpLongitude,_tmpAltitude,_tmpAccuracy,_tmpAddress,_tmpStatus,_tmpAssignedTechnicianId,_tmpCustomerName,_tmpCustomerPhone,_tmpCustomerEmail,_tmpInstallationDate,_tmpActivationStatus,_tmpActivationDate,_tmpNotes,_tmpPriority,_tmpCreatedAt,_tmpUpdatedAt,_tmpSyncStatus,_tmpLastSyncAttempt,_tmpSyncError,_tmpAssignedAt,_tmpCompletedAt,_tmpNeedsSync);
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
  public Flow<DropEntity> getDropByIdFlow(final String dropId) {
    final String _sql = "SELECT * FROM drops WHERE drop_number = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    if (dropId == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, dropId);
    }
    return CoroutinesRoom.createFlow(__db, false, new String[] {"drops"}, new Callable<DropEntity>() {
      @Override
      @Nullable
      public DropEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfDropNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "drop_number");
          final int _cursorIndexOfProjectId = CursorUtil.getColumnIndexOrThrow(_cursor, "project_id");
          final int _cursorIndexOfLatitude = CursorUtil.getColumnIndexOrThrow(_cursor, "latitude");
          final int _cursorIndexOfLongitude = CursorUtil.getColumnIndexOrThrow(_cursor, "longitude");
          final int _cursorIndexOfAltitude = CursorUtil.getColumnIndexOrThrow(_cursor, "altitude");
          final int _cursorIndexOfAccuracy = CursorUtil.getColumnIndexOrThrow(_cursor, "accuracy");
          final int _cursorIndexOfAddress = CursorUtil.getColumnIndexOrThrow(_cursor, "address");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfAssignedTechnicianId = CursorUtil.getColumnIndexOrThrow(_cursor, "assigned_technician_id");
          final int _cursorIndexOfCustomerName = CursorUtil.getColumnIndexOrThrow(_cursor, "customer_name");
          final int _cursorIndexOfCustomerPhone = CursorUtil.getColumnIndexOrThrow(_cursor, "customer_phone");
          final int _cursorIndexOfCustomerEmail = CursorUtil.getColumnIndexOrThrow(_cursor, "customer_email");
          final int _cursorIndexOfInstallationDate = CursorUtil.getColumnIndexOrThrow(_cursor, "installation_date");
          final int _cursorIndexOfActivationStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "activation_status");
          final int _cursorIndexOfActivationDate = CursorUtil.getColumnIndexOrThrow(_cursor, "activation_date");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfPriority = CursorUtil.getColumnIndexOrThrow(_cursor, "priority");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final int _cursorIndexOfSyncStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "sync_status");
          final int _cursorIndexOfLastSyncAttempt = CursorUtil.getColumnIndexOrThrow(_cursor, "last_sync_attempt");
          final int _cursorIndexOfSyncError = CursorUtil.getColumnIndexOrThrow(_cursor, "sync_error");
          final int _cursorIndexOfAssignedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "assigned_at");
          final int _cursorIndexOfCompletedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "completed_at");
          final int _cursorIndexOfNeedsSync = CursorUtil.getColumnIndexOrThrow(_cursor, "needs_sync");
          final DropEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpDropNumber;
            if (_cursor.isNull(_cursorIndexOfDropNumber)) {
              _tmpDropNumber = null;
            } else {
              _tmpDropNumber = _cursor.getString(_cursorIndexOfDropNumber);
            }
            final int _tmpProjectId;
            _tmpProjectId = _cursor.getInt(_cursorIndexOfProjectId);
            final double _tmpLatitude;
            _tmpLatitude = _cursor.getDouble(_cursorIndexOfLatitude);
            final double _tmpLongitude;
            _tmpLongitude = _cursor.getDouble(_cursorIndexOfLongitude);
            final Double _tmpAltitude;
            if (_cursor.isNull(_cursorIndexOfAltitude)) {
              _tmpAltitude = null;
            } else {
              _tmpAltitude = _cursor.getDouble(_cursorIndexOfAltitude);
            }
            final Float _tmpAccuracy;
            if (_cursor.isNull(_cursorIndexOfAccuracy)) {
              _tmpAccuracy = null;
            } else {
              _tmpAccuracy = _cursor.getFloat(_cursorIndexOfAccuracy);
            }
            final String _tmpAddress;
            if (_cursor.isNull(_cursorIndexOfAddress)) {
              _tmpAddress = null;
            } else {
              _tmpAddress = _cursor.getString(_cursorIndexOfAddress);
            }
            final DropStatus _tmpStatus;
            _tmpStatus = __DropStatus_stringToEnum(_cursor.getString(_cursorIndexOfStatus));
            final String _tmpAssignedTechnicianId;
            if (_cursor.isNull(_cursorIndexOfAssignedTechnicianId)) {
              _tmpAssignedTechnicianId = null;
            } else {
              _tmpAssignedTechnicianId = _cursor.getString(_cursorIndexOfAssignedTechnicianId);
            }
            final String _tmpCustomerName;
            if (_cursor.isNull(_cursorIndexOfCustomerName)) {
              _tmpCustomerName = null;
            } else {
              _tmpCustomerName = _cursor.getString(_cursorIndexOfCustomerName);
            }
            final String _tmpCustomerPhone;
            if (_cursor.isNull(_cursorIndexOfCustomerPhone)) {
              _tmpCustomerPhone = null;
            } else {
              _tmpCustomerPhone = _cursor.getString(_cursorIndexOfCustomerPhone);
            }
            final String _tmpCustomerEmail;
            if (_cursor.isNull(_cursorIndexOfCustomerEmail)) {
              _tmpCustomerEmail = null;
            } else {
              _tmpCustomerEmail = _cursor.getString(_cursorIndexOfCustomerEmail);
            }
            final Date _tmpInstallationDate;
            final Long _tmp;
            if (_cursor.isNull(_cursorIndexOfInstallationDate)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getLong(_cursorIndexOfInstallationDate);
            }
            _tmpInstallationDate = __dateConverters.fromTimestamp(_tmp);
            final ActivationStatus _tmpActivationStatus;
            _tmpActivationStatus = __ActivationStatus_stringToEnum(_cursor.getString(_cursorIndexOfActivationStatus));
            final Date _tmpActivationDate;
            final Long _tmp_1;
            if (_cursor.isNull(_cursorIndexOfActivationDate)) {
              _tmp_1 = null;
            } else {
              _tmp_1 = _cursor.getLong(_cursorIndexOfActivationDate);
            }
            _tmpActivationDate = __dateConverters.fromTimestamp(_tmp_1);
            final String _tmpNotes;
            if (_cursor.isNull(_cursorIndexOfNotes)) {
              _tmpNotes = null;
            } else {
              _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
            }
            final int _tmpPriority;
            _tmpPriority = _cursor.getInt(_cursorIndexOfPriority);
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
            final SyncStatus _tmpSyncStatus;
            final String _tmp_4;
            if (_cursor.isNull(_cursorIndexOfSyncStatus)) {
              _tmp_4 = null;
            } else {
              _tmp_4 = _cursor.getString(_cursorIndexOfSyncStatus);
            }
            _tmpSyncStatus = __statusConverters.toSyncStatus(_tmp_4);
            final Date _tmpLastSyncAttempt;
            final Long _tmp_5;
            if (_cursor.isNull(_cursorIndexOfLastSyncAttempt)) {
              _tmp_5 = null;
            } else {
              _tmp_5 = _cursor.getLong(_cursorIndexOfLastSyncAttempt);
            }
            _tmpLastSyncAttempt = __dateConverters.fromTimestamp(_tmp_5);
            final String _tmpSyncError;
            if (_cursor.isNull(_cursorIndexOfSyncError)) {
              _tmpSyncError = null;
            } else {
              _tmpSyncError = _cursor.getString(_cursorIndexOfSyncError);
            }
            final Date _tmpAssignedAt;
            final Long _tmp_6;
            if (_cursor.isNull(_cursorIndexOfAssignedAt)) {
              _tmp_6 = null;
            } else {
              _tmp_6 = _cursor.getLong(_cursorIndexOfAssignedAt);
            }
            _tmpAssignedAt = __dateConverters.fromTimestamp(_tmp_6);
            final Date _tmpCompletedAt;
            final Long _tmp_7;
            if (_cursor.isNull(_cursorIndexOfCompletedAt)) {
              _tmp_7 = null;
            } else {
              _tmp_7 = _cursor.getLong(_cursorIndexOfCompletedAt);
            }
            _tmpCompletedAt = __dateConverters.fromTimestamp(_tmp_7);
            final boolean _tmpNeedsSync;
            final int _tmp_8;
            _tmp_8 = _cursor.getInt(_cursorIndexOfNeedsSync);
            _tmpNeedsSync = _tmp_8 != 0;
            _result = new DropEntity(_tmpDropNumber,_tmpProjectId,_tmpLatitude,_tmpLongitude,_tmpAltitude,_tmpAccuracy,_tmpAddress,_tmpStatus,_tmpAssignedTechnicianId,_tmpCustomerName,_tmpCustomerPhone,_tmpCustomerEmail,_tmpInstallationDate,_tmpActivationStatus,_tmpActivationDate,_tmpNotes,_tmpPriority,_tmpCreatedAt,_tmpUpdatedAt,_tmpSyncStatus,_tmpLastSyncAttempt,_tmpSyncError,_tmpAssignedAt,_tmpCompletedAt,_tmpNeedsSync);
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
  public Object getAllDrops(final Continuation<? super List<DropEntity>> $completion) {
    final String _sql = "SELECT * FROM drops ORDER BY created_at DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<DropEntity>>() {
      @Override
      @NonNull
      public List<DropEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfDropNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "drop_number");
          final int _cursorIndexOfProjectId = CursorUtil.getColumnIndexOrThrow(_cursor, "project_id");
          final int _cursorIndexOfLatitude = CursorUtil.getColumnIndexOrThrow(_cursor, "latitude");
          final int _cursorIndexOfLongitude = CursorUtil.getColumnIndexOrThrow(_cursor, "longitude");
          final int _cursorIndexOfAltitude = CursorUtil.getColumnIndexOrThrow(_cursor, "altitude");
          final int _cursorIndexOfAccuracy = CursorUtil.getColumnIndexOrThrow(_cursor, "accuracy");
          final int _cursorIndexOfAddress = CursorUtil.getColumnIndexOrThrow(_cursor, "address");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfAssignedTechnicianId = CursorUtil.getColumnIndexOrThrow(_cursor, "assigned_technician_id");
          final int _cursorIndexOfCustomerName = CursorUtil.getColumnIndexOrThrow(_cursor, "customer_name");
          final int _cursorIndexOfCustomerPhone = CursorUtil.getColumnIndexOrThrow(_cursor, "customer_phone");
          final int _cursorIndexOfCustomerEmail = CursorUtil.getColumnIndexOrThrow(_cursor, "customer_email");
          final int _cursorIndexOfInstallationDate = CursorUtil.getColumnIndexOrThrow(_cursor, "installation_date");
          final int _cursorIndexOfActivationStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "activation_status");
          final int _cursorIndexOfActivationDate = CursorUtil.getColumnIndexOrThrow(_cursor, "activation_date");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfPriority = CursorUtil.getColumnIndexOrThrow(_cursor, "priority");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final int _cursorIndexOfSyncStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "sync_status");
          final int _cursorIndexOfLastSyncAttempt = CursorUtil.getColumnIndexOrThrow(_cursor, "last_sync_attempt");
          final int _cursorIndexOfSyncError = CursorUtil.getColumnIndexOrThrow(_cursor, "sync_error");
          final int _cursorIndexOfAssignedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "assigned_at");
          final int _cursorIndexOfCompletedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "completed_at");
          final int _cursorIndexOfNeedsSync = CursorUtil.getColumnIndexOrThrow(_cursor, "needs_sync");
          final List<DropEntity> _result = new ArrayList<DropEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final DropEntity _item;
            final String _tmpDropNumber;
            if (_cursor.isNull(_cursorIndexOfDropNumber)) {
              _tmpDropNumber = null;
            } else {
              _tmpDropNumber = _cursor.getString(_cursorIndexOfDropNumber);
            }
            final int _tmpProjectId;
            _tmpProjectId = _cursor.getInt(_cursorIndexOfProjectId);
            final double _tmpLatitude;
            _tmpLatitude = _cursor.getDouble(_cursorIndexOfLatitude);
            final double _tmpLongitude;
            _tmpLongitude = _cursor.getDouble(_cursorIndexOfLongitude);
            final Double _tmpAltitude;
            if (_cursor.isNull(_cursorIndexOfAltitude)) {
              _tmpAltitude = null;
            } else {
              _tmpAltitude = _cursor.getDouble(_cursorIndexOfAltitude);
            }
            final Float _tmpAccuracy;
            if (_cursor.isNull(_cursorIndexOfAccuracy)) {
              _tmpAccuracy = null;
            } else {
              _tmpAccuracy = _cursor.getFloat(_cursorIndexOfAccuracy);
            }
            final String _tmpAddress;
            if (_cursor.isNull(_cursorIndexOfAddress)) {
              _tmpAddress = null;
            } else {
              _tmpAddress = _cursor.getString(_cursorIndexOfAddress);
            }
            final DropStatus _tmpStatus;
            _tmpStatus = __DropStatus_stringToEnum(_cursor.getString(_cursorIndexOfStatus));
            final String _tmpAssignedTechnicianId;
            if (_cursor.isNull(_cursorIndexOfAssignedTechnicianId)) {
              _tmpAssignedTechnicianId = null;
            } else {
              _tmpAssignedTechnicianId = _cursor.getString(_cursorIndexOfAssignedTechnicianId);
            }
            final String _tmpCustomerName;
            if (_cursor.isNull(_cursorIndexOfCustomerName)) {
              _tmpCustomerName = null;
            } else {
              _tmpCustomerName = _cursor.getString(_cursorIndexOfCustomerName);
            }
            final String _tmpCustomerPhone;
            if (_cursor.isNull(_cursorIndexOfCustomerPhone)) {
              _tmpCustomerPhone = null;
            } else {
              _tmpCustomerPhone = _cursor.getString(_cursorIndexOfCustomerPhone);
            }
            final String _tmpCustomerEmail;
            if (_cursor.isNull(_cursorIndexOfCustomerEmail)) {
              _tmpCustomerEmail = null;
            } else {
              _tmpCustomerEmail = _cursor.getString(_cursorIndexOfCustomerEmail);
            }
            final Date _tmpInstallationDate;
            final Long _tmp;
            if (_cursor.isNull(_cursorIndexOfInstallationDate)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getLong(_cursorIndexOfInstallationDate);
            }
            _tmpInstallationDate = __dateConverters.fromTimestamp(_tmp);
            final ActivationStatus _tmpActivationStatus;
            _tmpActivationStatus = __ActivationStatus_stringToEnum(_cursor.getString(_cursorIndexOfActivationStatus));
            final Date _tmpActivationDate;
            final Long _tmp_1;
            if (_cursor.isNull(_cursorIndexOfActivationDate)) {
              _tmp_1 = null;
            } else {
              _tmp_1 = _cursor.getLong(_cursorIndexOfActivationDate);
            }
            _tmpActivationDate = __dateConverters.fromTimestamp(_tmp_1);
            final String _tmpNotes;
            if (_cursor.isNull(_cursorIndexOfNotes)) {
              _tmpNotes = null;
            } else {
              _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
            }
            final int _tmpPriority;
            _tmpPriority = _cursor.getInt(_cursorIndexOfPriority);
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
            final SyncStatus _tmpSyncStatus;
            final String _tmp_4;
            if (_cursor.isNull(_cursorIndexOfSyncStatus)) {
              _tmp_4 = null;
            } else {
              _tmp_4 = _cursor.getString(_cursorIndexOfSyncStatus);
            }
            _tmpSyncStatus = __statusConverters.toSyncStatus(_tmp_4);
            final Date _tmpLastSyncAttempt;
            final Long _tmp_5;
            if (_cursor.isNull(_cursorIndexOfLastSyncAttempt)) {
              _tmp_5 = null;
            } else {
              _tmp_5 = _cursor.getLong(_cursorIndexOfLastSyncAttempt);
            }
            _tmpLastSyncAttempt = __dateConverters.fromTimestamp(_tmp_5);
            final String _tmpSyncError;
            if (_cursor.isNull(_cursorIndexOfSyncError)) {
              _tmpSyncError = null;
            } else {
              _tmpSyncError = _cursor.getString(_cursorIndexOfSyncError);
            }
            final Date _tmpAssignedAt;
            final Long _tmp_6;
            if (_cursor.isNull(_cursorIndexOfAssignedAt)) {
              _tmp_6 = null;
            } else {
              _tmp_6 = _cursor.getLong(_cursorIndexOfAssignedAt);
            }
            _tmpAssignedAt = __dateConverters.fromTimestamp(_tmp_6);
            final Date _tmpCompletedAt;
            final Long _tmp_7;
            if (_cursor.isNull(_cursorIndexOfCompletedAt)) {
              _tmp_7 = null;
            } else {
              _tmp_7 = _cursor.getLong(_cursorIndexOfCompletedAt);
            }
            _tmpCompletedAt = __dateConverters.fromTimestamp(_tmp_7);
            final boolean _tmpNeedsSync;
            final int _tmp_8;
            _tmp_8 = _cursor.getInt(_cursorIndexOfNeedsSync);
            _tmpNeedsSync = _tmp_8 != 0;
            _item = new DropEntity(_tmpDropNumber,_tmpProjectId,_tmpLatitude,_tmpLongitude,_tmpAltitude,_tmpAccuracy,_tmpAddress,_tmpStatus,_tmpAssignedTechnicianId,_tmpCustomerName,_tmpCustomerPhone,_tmpCustomerEmail,_tmpInstallationDate,_tmpActivationStatus,_tmpActivationDate,_tmpNotes,_tmpPriority,_tmpCreatedAt,_tmpUpdatedAt,_tmpSyncStatus,_tmpLastSyncAttempt,_tmpSyncError,_tmpAssignedAt,_tmpCompletedAt,_tmpNeedsSync);
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
  public Flow<List<DropEntity>> getAllDropsFlow() {
    final String _sql = "SELECT * FROM drops ORDER BY created_at DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"drops"}, new Callable<List<DropEntity>>() {
      @Override
      @NonNull
      public List<DropEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfDropNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "drop_number");
          final int _cursorIndexOfProjectId = CursorUtil.getColumnIndexOrThrow(_cursor, "project_id");
          final int _cursorIndexOfLatitude = CursorUtil.getColumnIndexOrThrow(_cursor, "latitude");
          final int _cursorIndexOfLongitude = CursorUtil.getColumnIndexOrThrow(_cursor, "longitude");
          final int _cursorIndexOfAltitude = CursorUtil.getColumnIndexOrThrow(_cursor, "altitude");
          final int _cursorIndexOfAccuracy = CursorUtil.getColumnIndexOrThrow(_cursor, "accuracy");
          final int _cursorIndexOfAddress = CursorUtil.getColumnIndexOrThrow(_cursor, "address");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfAssignedTechnicianId = CursorUtil.getColumnIndexOrThrow(_cursor, "assigned_technician_id");
          final int _cursorIndexOfCustomerName = CursorUtil.getColumnIndexOrThrow(_cursor, "customer_name");
          final int _cursorIndexOfCustomerPhone = CursorUtil.getColumnIndexOrThrow(_cursor, "customer_phone");
          final int _cursorIndexOfCustomerEmail = CursorUtil.getColumnIndexOrThrow(_cursor, "customer_email");
          final int _cursorIndexOfInstallationDate = CursorUtil.getColumnIndexOrThrow(_cursor, "installation_date");
          final int _cursorIndexOfActivationStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "activation_status");
          final int _cursorIndexOfActivationDate = CursorUtil.getColumnIndexOrThrow(_cursor, "activation_date");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfPriority = CursorUtil.getColumnIndexOrThrow(_cursor, "priority");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final int _cursorIndexOfSyncStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "sync_status");
          final int _cursorIndexOfLastSyncAttempt = CursorUtil.getColumnIndexOrThrow(_cursor, "last_sync_attempt");
          final int _cursorIndexOfSyncError = CursorUtil.getColumnIndexOrThrow(_cursor, "sync_error");
          final int _cursorIndexOfAssignedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "assigned_at");
          final int _cursorIndexOfCompletedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "completed_at");
          final int _cursorIndexOfNeedsSync = CursorUtil.getColumnIndexOrThrow(_cursor, "needs_sync");
          final List<DropEntity> _result = new ArrayList<DropEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final DropEntity _item;
            final String _tmpDropNumber;
            if (_cursor.isNull(_cursorIndexOfDropNumber)) {
              _tmpDropNumber = null;
            } else {
              _tmpDropNumber = _cursor.getString(_cursorIndexOfDropNumber);
            }
            final int _tmpProjectId;
            _tmpProjectId = _cursor.getInt(_cursorIndexOfProjectId);
            final double _tmpLatitude;
            _tmpLatitude = _cursor.getDouble(_cursorIndexOfLatitude);
            final double _tmpLongitude;
            _tmpLongitude = _cursor.getDouble(_cursorIndexOfLongitude);
            final Double _tmpAltitude;
            if (_cursor.isNull(_cursorIndexOfAltitude)) {
              _tmpAltitude = null;
            } else {
              _tmpAltitude = _cursor.getDouble(_cursorIndexOfAltitude);
            }
            final Float _tmpAccuracy;
            if (_cursor.isNull(_cursorIndexOfAccuracy)) {
              _tmpAccuracy = null;
            } else {
              _tmpAccuracy = _cursor.getFloat(_cursorIndexOfAccuracy);
            }
            final String _tmpAddress;
            if (_cursor.isNull(_cursorIndexOfAddress)) {
              _tmpAddress = null;
            } else {
              _tmpAddress = _cursor.getString(_cursorIndexOfAddress);
            }
            final DropStatus _tmpStatus;
            _tmpStatus = __DropStatus_stringToEnum(_cursor.getString(_cursorIndexOfStatus));
            final String _tmpAssignedTechnicianId;
            if (_cursor.isNull(_cursorIndexOfAssignedTechnicianId)) {
              _tmpAssignedTechnicianId = null;
            } else {
              _tmpAssignedTechnicianId = _cursor.getString(_cursorIndexOfAssignedTechnicianId);
            }
            final String _tmpCustomerName;
            if (_cursor.isNull(_cursorIndexOfCustomerName)) {
              _tmpCustomerName = null;
            } else {
              _tmpCustomerName = _cursor.getString(_cursorIndexOfCustomerName);
            }
            final String _tmpCustomerPhone;
            if (_cursor.isNull(_cursorIndexOfCustomerPhone)) {
              _tmpCustomerPhone = null;
            } else {
              _tmpCustomerPhone = _cursor.getString(_cursorIndexOfCustomerPhone);
            }
            final String _tmpCustomerEmail;
            if (_cursor.isNull(_cursorIndexOfCustomerEmail)) {
              _tmpCustomerEmail = null;
            } else {
              _tmpCustomerEmail = _cursor.getString(_cursorIndexOfCustomerEmail);
            }
            final Date _tmpInstallationDate;
            final Long _tmp;
            if (_cursor.isNull(_cursorIndexOfInstallationDate)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getLong(_cursorIndexOfInstallationDate);
            }
            _tmpInstallationDate = __dateConverters.fromTimestamp(_tmp);
            final ActivationStatus _tmpActivationStatus;
            _tmpActivationStatus = __ActivationStatus_stringToEnum(_cursor.getString(_cursorIndexOfActivationStatus));
            final Date _tmpActivationDate;
            final Long _tmp_1;
            if (_cursor.isNull(_cursorIndexOfActivationDate)) {
              _tmp_1 = null;
            } else {
              _tmp_1 = _cursor.getLong(_cursorIndexOfActivationDate);
            }
            _tmpActivationDate = __dateConverters.fromTimestamp(_tmp_1);
            final String _tmpNotes;
            if (_cursor.isNull(_cursorIndexOfNotes)) {
              _tmpNotes = null;
            } else {
              _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
            }
            final int _tmpPriority;
            _tmpPriority = _cursor.getInt(_cursorIndexOfPriority);
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
            final SyncStatus _tmpSyncStatus;
            final String _tmp_4;
            if (_cursor.isNull(_cursorIndexOfSyncStatus)) {
              _tmp_4 = null;
            } else {
              _tmp_4 = _cursor.getString(_cursorIndexOfSyncStatus);
            }
            _tmpSyncStatus = __statusConverters.toSyncStatus(_tmp_4);
            final Date _tmpLastSyncAttempt;
            final Long _tmp_5;
            if (_cursor.isNull(_cursorIndexOfLastSyncAttempt)) {
              _tmp_5 = null;
            } else {
              _tmp_5 = _cursor.getLong(_cursorIndexOfLastSyncAttempt);
            }
            _tmpLastSyncAttempt = __dateConverters.fromTimestamp(_tmp_5);
            final String _tmpSyncError;
            if (_cursor.isNull(_cursorIndexOfSyncError)) {
              _tmpSyncError = null;
            } else {
              _tmpSyncError = _cursor.getString(_cursorIndexOfSyncError);
            }
            final Date _tmpAssignedAt;
            final Long _tmp_6;
            if (_cursor.isNull(_cursorIndexOfAssignedAt)) {
              _tmp_6 = null;
            } else {
              _tmp_6 = _cursor.getLong(_cursorIndexOfAssignedAt);
            }
            _tmpAssignedAt = __dateConverters.fromTimestamp(_tmp_6);
            final Date _tmpCompletedAt;
            final Long _tmp_7;
            if (_cursor.isNull(_cursorIndexOfCompletedAt)) {
              _tmp_7 = null;
            } else {
              _tmp_7 = _cursor.getLong(_cursorIndexOfCompletedAt);
            }
            _tmpCompletedAt = __dateConverters.fromTimestamp(_tmp_7);
            final boolean _tmpNeedsSync;
            final int _tmp_8;
            _tmp_8 = _cursor.getInt(_cursorIndexOfNeedsSync);
            _tmpNeedsSync = _tmp_8 != 0;
            _item = new DropEntity(_tmpDropNumber,_tmpProjectId,_tmpLatitude,_tmpLongitude,_tmpAltitude,_tmpAccuracy,_tmpAddress,_tmpStatus,_tmpAssignedTechnicianId,_tmpCustomerName,_tmpCustomerPhone,_tmpCustomerEmail,_tmpInstallationDate,_tmpActivationStatus,_tmpActivationDate,_tmpNotes,_tmpPriority,_tmpCreatedAt,_tmpUpdatedAt,_tmpSyncStatus,_tmpLastSyncAttempt,_tmpSyncError,_tmpAssignedAt,_tmpCompletedAt,_tmpNeedsSync);
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
  public Object getDropsByStatus(final String status,
      final Continuation<? super List<DropEntity>> $completion) {
    final String _sql = "SELECT * FROM drops WHERE status = ? ORDER BY updated_at DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    if (status == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, status);
    }
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<DropEntity>>() {
      @Override
      @NonNull
      public List<DropEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfDropNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "drop_number");
          final int _cursorIndexOfProjectId = CursorUtil.getColumnIndexOrThrow(_cursor, "project_id");
          final int _cursorIndexOfLatitude = CursorUtil.getColumnIndexOrThrow(_cursor, "latitude");
          final int _cursorIndexOfLongitude = CursorUtil.getColumnIndexOrThrow(_cursor, "longitude");
          final int _cursorIndexOfAltitude = CursorUtil.getColumnIndexOrThrow(_cursor, "altitude");
          final int _cursorIndexOfAccuracy = CursorUtil.getColumnIndexOrThrow(_cursor, "accuracy");
          final int _cursorIndexOfAddress = CursorUtil.getColumnIndexOrThrow(_cursor, "address");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfAssignedTechnicianId = CursorUtil.getColumnIndexOrThrow(_cursor, "assigned_technician_id");
          final int _cursorIndexOfCustomerName = CursorUtil.getColumnIndexOrThrow(_cursor, "customer_name");
          final int _cursorIndexOfCustomerPhone = CursorUtil.getColumnIndexOrThrow(_cursor, "customer_phone");
          final int _cursorIndexOfCustomerEmail = CursorUtil.getColumnIndexOrThrow(_cursor, "customer_email");
          final int _cursorIndexOfInstallationDate = CursorUtil.getColumnIndexOrThrow(_cursor, "installation_date");
          final int _cursorIndexOfActivationStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "activation_status");
          final int _cursorIndexOfActivationDate = CursorUtil.getColumnIndexOrThrow(_cursor, "activation_date");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfPriority = CursorUtil.getColumnIndexOrThrow(_cursor, "priority");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final int _cursorIndexOfSyncStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "sync_status");
          final int _cursorIndexOfLastSyncAttempt = CursorUtil.getColumnIndexOrThrow(_cursor, "last_sync_attempt");
          final int _cursorIndexOfSyncError = CursorUtil.getColumnIndexOrThrow(_cursor, "sync_error");
          final int _cursorIndexOfAssignedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "assigned_at");
          final int _cursorIndexOfCompletedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "completed_at");
          final int _cursorIndexOfNeedsSync = CursorUtil.getColumnIndexOrThrow(_cursor, "needs_sync");
          final List<DropEntity> _result = new ArrayList<DropEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final DropEntity _item;
            final String _tmpDropNumber;
            if (_cursor.isNull(_cursorIndexOfDropNumber)) {
              _tmpDropNumber = null;
            } else {
              _tmpDropNumber = _cursor.getString(_cursorIndexOfDropNumber);
            }
            final int _tmpProjectId;
            _tmpProjectId = _cursor.getInt(_cursorIndexOfProjectId);
            final double _tmpLatitude;
            _tmpLatitude = _cursor.getDouble(_cursorIndexOfLatitude);
            final double _tmpLongitude;
            _tmpLongitude = _cursor.getDouble(_cursorIndexOfLongitude);
            final Double _tmpAltitude;
            if (_cursor.isNull(_cursorIndexOfAltitude)) {
              _tmpAltitude = null;
            } else {
              _tmpAltitude = _cursor.getDouble(_cursorIndexOfAltitude);
            }
            final Float _tmpAccuracy;
            if (_cursor.isNull(_cursorIndexOfAccuracy)) {
              _tmpAccuracy = null;
            } else {
              _tmpAccuracy = _cursor.getFloat(_cursorIndexOfAccuracy);
            }
            final String _tmpAddress;
            if (_cursor.isNull(_cursorIndexOfAddress)) {
              _tmpAddress = null;
            } else {
              _tmpAddress = _cursor.getString(_cursorIndexOfAddress);
            }
            final DropStatus _tmpStatus;
            _tmpStatus = __DropStatus_stringToEnum(_cursor.getString(_cursorIndexOfStatus));
            final String _tmpAssignedTechnicianId;
            if (_cursor.isNull(_cursorIndexOfAssignedTechnicianId)) {
              _tmpAssignedTechnicianId = null;
            } else {
              _tmpAssignedTechnicianId = _cursor.getString(_cursorIndexOfAssignedTechnicianId);
            }
            final String _tmpCustomerName;
            if (_cursor.isNull(_cursorIndexOfCustomerName)) {
              _tmpCustomerName = null;
            } else {
              _tmpCustomerName = _cursor.getString(_cursorIndexOfCustomerName);
            }
            final String _tmpCustomerPhone;
            if (_cursor.isNull(_cursorIndexOfCustomerPhone)) {
              _tmpCustomerPhone = null;
            } else {
              _tmpCustomerPhone = _cursor.getString(_cursorIndexOfCustomerPhone);
            }
            final String _tmpCustomerEmail;
            if (_cursor.isNull(_cursorIndexOfCustomerEmail)) {
              _tmpCustomerEmail = null;
            } else {
              _tmpCustomerEmail = _cursor.getString(_cursorIndexOfCustomerEmail);
            }
            final Date _tmpInstallationDate;
            final Long _tmp;
            if (_cursor.isNull(_cursorIndexOfInstallationDate)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getLong(_cursorIndexOfInstallationDate);
            }
            _tmpInstallationDate = __dateConverters.fromTimestamp(_tmp);
            final ActivationStatus _tmpActivationStatus;
            _tmpActivationStatus = __ActivationStatus_stringToEnum(_cursor.getString(_cursorIndexOfActivationStatus));
            final Date _tmpActivationDate;
            final Long _tmp_1;
            if (_cursor.isNull(_cursorIndexOfActivationDate)) {
              _tmp_1 = null;
            } else {
              _tmp_1 = _cursor.getLong(_cursorIndexOfActivationDate);
            }
            _tmpActivationDate = __dateConverters.fromTimestamp(_tmp_1);
            final String _tmpNotes;
            if (_cursor.isNull(_cursorIndexOfNotes)) {
              _tmpNotes = null;
            } else {
              _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
            }
            final int _tmpPriority;
            _tmpPriority = _cursor.getInt(_cursorIndexOfPriority);
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
            final SyncStatus _tmpSyncStatus;
            final String _tmp_4;
            if (_cursor.isNull(_cursorIndexOfSyncStatus)) {
              _tmp_4 = null;
            } else {
              _tmp_4 = _cursor.getString(_cursorIndexOfSyncStatus);
            }
            _tmpSyncStatus = __statusConverters.toSyncStatus(_tmp_4);
            final Date _tmpLastSyncAttempt;
            final Long _tmp_5;
            if (_cursor.isNull(_cursorIndexOfLastSyncAttempt)) {
              _tmp_5 = null;
            } else {
              _tmp_5 = _cursor.getLong(_cursorIndexOfLastSyncAttempt);
            }
            _tmpLastSyncAttempt = __dateConverters.fromTimestamp(_tmp_5);
            final String _tmpSyncError;
            if (_cursor.isNull(_cursorIndexOfSyncError)) {
              _tmpSyncError = null;
            } else {
              _tmpSyncError = _cursor.getString(_cursorIndexOfSyncError);
            }
            final Date _tmpAssignedAt;
            final Long _tmp_6;
            if (_cursor.isNull(_cursorIndexOfAssignedAt)) {
              _tmp_6 = null;
            } else {
              _tmp_6 = _cursor.getLong(_cursorIndexOfAssignedAt);
            }
            _tmpAssignedAt = __dateConverters.fromTimestamp(_tmp_6);
            final Date _tmpCompletedAt;
            final Long _tmp_7;
            if (_cursor.isNull(_cursorIndexOfCompletedAt)) {
              _tmp_7 = null;
            } else {
              _tmp_7 = _cursor.getLong(_cursorIndexOfCompletedAt);
            }
            _tmpCompletedAt = __dateConverters.fromTimestamp(_tmp_7);
            final boolean _tmpNeedsSync;
            final int _tmp_8;
            _tmp_8 = _cursor.getInt(_cursorIndexOfNeedsSync);
            _tmpNeedsSync = _tmp_8 != 0;
            _item = new DropEntity(_tmpDropNumber,_tmpProjectId,_tmpLatitude,_tmpLongitude,_tmpAltitude,_tmpAccuracy,_tmpAddress,_tmpStatus,_tmpAssignedTechnicianId,_tmpCustomerName,_tmpCustomerPhone,_tmpCustomerEmail,_tmpInstallationDate,_tmpActivationStatus,_tmpActivationDate,_tmpNotes,_tmpPriority,_tmpCreatedAt,_tmpUpdatedAt,_tmpSyncStatus,_tmpLastSyncAttempt,_tmpSyncError,_tmpAssignedAt,_tmpCompletedAt,_tmpNeedsSync);
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
  public Flow<List<DropEntity>> getDropsByStatusFlow(final String status) {
    final String _sql = "SELECT * FROM drops WHERE status = ? ORDER BY updated_at DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    if (status == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, status);
    }
    return CoroutinesRoom.createFlow(__db, false, new String[] {"drops"}, new Callable<List<DropEntity>>() {
      @Override
      @NonNull
      public List<DropEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfDropNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "drop_number");
          final int _cursorIndexOfProjectId = CursorUtil.getColumnIndexOrThrow(_cursor, "project_id");
          final int _cursorIndexOfLatitude = CursorUtil.getColumnIndexOrThrow(_cursor, "latitude");
          final int _cursorIndexOfLongitude = CursorUtil.getColumnIndexOrThrow(_cursor, "longitude");
          final int _cursorIndexOfAltitude = CursorUtil.getColumnIndexOrThrow(_cursor, "altitude");
          final int _cursorIndexOfAccuracy = CursorUtil.getColumnIndexOrThrow(_cursor, "accuracy");
          final int _cursorIndexOfAddress = CursorUtil.getColumnIndexOrThrow(_cursor, "address");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfAssignedTechnicianId = CursorUtil.getColumnIndexOrThrow(_cursor, "assigned_technician_id");
          final int _cursorIndexOfCustomerName = CursorUtil.getColumnIndexOrThrow(_cursor, "customer_name");
          final int _cursorIndexOfCustomerPhone = CursorUtil.getColumnIndexOrThrow(_cursor, "customer_phone");
          final int _cursorIndexOfCustomerEmail = CursorUtil.getColumnIndexOrThrow(_cursor, "customer_email");
          final int _cursorIndexOfInstallationDate = CursorUtil.getColumnIndexOrThrow(_cursor, "installation_date");
          final int _cursorIndexOfActivationStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "activation_status");
          final int _cursorIndexOfActivationDate = CursorUtil.getColumnIndexOrThrow(_cursor, "activation_date");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfPriority = CursorUtil.getColumnIndexOrThrow(_cursor, "priority");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final int _cursorIndexOfSyncStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "sync_status");
          final int _cursorIndexOfLastSyncAttempt = CursorUtil.getColumnIndexOrThrow(_cursor, "last_sync_attempt");
          final int _cursorIndexOfSyncError = CursorUtil.getColumnIndexOrThrow(_cursor, "sync_error");
          final int _cursorIndexOfAssignedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "assigned_at");
          final int _cursorIndexOfCompletedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "completed_at");
          final int _cursorIndexOfNeedsSync = CursorUtil.getColumnIndexOrThrow(_cursor, "needs_sync");
          final List<DropEntity> _result = new ArrayList<DropEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final DropEntity _item;
            final String _tmpDropNumber;
            if (_cursor.isNull(_cursorIndexOfDropNumber)) {
              _tmpDropNumber = null;
            } else {
              _tmpDropNumber = _cursor.getString(_cursorIndexOfDropNumber);
            }
            final int _tmpProjectId;
            _tmpProjectId = _cursor.getInt(_cursorIndexOfProjectId);
            final double _tmpLatitude;
            _tmpLatitude = _cursor.getDouble(_cursorIndexOfLatitude);
            final double _tmpLongitude;
            _tmpLongitude = _cursor.getDouble(_cursorIndexOfLongitude);
            final Double _tmpAltitude;
            if (_cursor.isNull(_cursorIndexOfAltitude)) {
              _tmpAltitude = null;
            } else {
              _tmpAltitude = _cursor.getDouble(_cursorIndexOfAltitude);
            }
            final Float _tmpAccuracy;
            if (_cursor.isNull(_cursorIndexOfAccuracy)) {
              _tmpAccuracy = null;
            } else {
              _tmpAccuracy = _cursor.getFloat(_cursorIndexOfAccuracy);
            }
            final String _tmpAddress;
            if (_cursor.isNull(_cursorIndexOfAddress)) {
              _tmpAddress = null;
            } else {
              _tmpAddress = _cursor.getString(_cursorIndexOfAddress);
            }
            final DropStatus _tmpStatus;
            _tmpStatus = __DropStatus_stringToEnum(_cursor.getString(_cursorIndexOfStatus));
            final String _tmpAssignedTechnicianId;
            if (_cursor.isNull(_cursorIndexOfAssignedTechnicianId)) {
              _tmpAssignedTechnicianId = null;
            } else {
              _tmpAssignedTechnicianId = _cursor.getString(_cursorIndexOfAssignedTechnicianId);
            }
            final String _tmpCustomerName;
            if (_cursor.isNull(_cursorIndexOfCustomerName)) {
              _tmpCustomerName = null;
            } else {
              _tmpCustomerName = _cursor.getString(_cursorIndexOfCustomerName);
            }
            final String _tmpCustomerPhone;
            if (_cursor.isNull(_cursorIndexOfCustomerPhone)) {
              _tmpCustomerPhone = null;
            } else {
              _tmpCustomerPhone = _cursor.getString(_cursorIndexOfCustomerPhone);
            }
            final String _tmpCustomerEmail;
            if (_cursor.isNull(_cursorIndexOfCustomerEmail)) {
              _tmpCustomerEmail = null;
            } else {
              _tmpCustomerEmail = _cursor.getString(_cursorIndexOfCustomerEmail);
            }
            final Date _tmpInstallationDate;
            final Long _tmp;
            if (_cursor.isNull(_cursorIndexOfInstallationDate)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getLong(_cursorIndexOfInstallationDate);
            }
            _tmpInstallationDate = __dateConverters.fromTimestamp(_tmp);
            final ActivationStatus _tmpActivationStatus;
            _tmpActivationStatus = __ActivationStatus_stringToEnum(_cursor.getString(_cursorIndexOfActivationStatus));
            final Date _tmpActivationDate;
            final Long _tmp_1;
            if (_cursor.isNull(_cursorIndexOfActivationDate)) {
              _tmp_1 = null;
            } else {
              _tmp_1 = _cursor.getLong(_cursorIndexOfActivationDate);
            }
            _tmpActivationDate = __dateConverters.fromTimestamp(_tmp_1);
            final String _tmpNotes;
            if (_cursor.isNull(_cursorIndexOfNotes)) {
              _tmpNotes = null;
            } else {
              _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
            }
            final int _tmpPriority;
            _tmpPriority = _cursor.getInt(_cursorIndexOfPriority);
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
            final SyncStatus _tmpSyncStatus;
            final String _tmp_4;
            if (_cursor.isNull(_cursorIndexOfSyncStatus)) {
              _tmp_4 = null;
            } else {
              _tmp_4 = _cursor.getString(_cursorIndexOfSyncStatus);
            }
            _tmpSyncStatus = __statusConverters.toSyncStatus(_tmp_4);
            final Date _tmpLastSyncAttempt;
            final Long _tmp_5;
            if (_cursor.isNull(_cursorIndexOfLastSyncAttempt)) {
              _tmp_5 = null;
            } else {
              _tmp_5 = _cursor.getLong(_cursorIndexOfLastSyncAttempt);
            }
            _tmpLastSyncAttempt = __dateConverters.fromTimestamp(_tmp_5);
            final String _tmpSyncError;
            if (_cursor.isNull(_cursorIndexOfSyncError)) {
              _tmpSyncError = null;
            } else {
              _tmpSyncError = _cursor.getString(_cursorIndexOfSyncError);
            }
            final Date _tmpAssignedAt;
            final Long _tmp_6;
            if (_cursor.isNull(_cursorIndexOfAssignedAt)) {
              _tmp_6 = null;
            } else {
              _tmp_6 = _cursor.getLong(_cursorIndexOfAssignedAt);
            }
            _tmpAssignedAt = __dateConverters.fromTimestamp(_tmp_6);
            final Date _tmpCompletedAt;
            final Long _tmp_7;
            if (_cursor.isNull(_cursorIndexOfCompletedAt)) {
              _tmp_7 = null;
            } else {
              _tmp_7 = _cursor.getLong(_cursorIndexOfCompletedAt);
            }
            _tmpCompletedAt = __dateConverters.fromTimestamp(_tmp_7);
            final boolean _tmpNeedsSync;
            final int _tmp_8;
            _tmp_8 = _cursor.getInt(_cursorIndexOfNeedsSync);
            _tmpNeedsSync = _tmp_8 != 0;
            _item = new DropEntity(_tmpDropNumber,_tmpProjectId,_tmpLatitude,_tmpLongitude,_tmpAltitude,_tmpAccuracy,_tmpAddress,_tmpStatus,_tmpAssignedTechnicianId,_tmpCustomerName,_tmpCustomerPhone,_tmpCustomerEmail,_tmpInstallationDate,_tmpActivationStatus,_tmpActivationDate,_tmpNotes,_tmpPriority,_tmpCreatedAt,_tmpUpdatedAt,_tmpSyncStatus,_tmpLastSyncAttempt,_tmpSyncError,_tmpAssignedAt,_tmpCompletedAt,_tmpNeedsSync);
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
  public Object getAvailableDrops(final Continuation<? super List<DropEntity>> $completion) {
    final String _sql = "SELECT * FROM drops WHERE status IN ('AVAILABLE', 'PENDING') ORDER BY priority DESC, created_at ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<DropEntity>>() {
      @Override
      @NonNull
      public List<DropEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfDropNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "drop_number");
          final int _cursorIndexOfProjectId = CursorUtil.getColumnIndexOrThrow(_cursor, "project_id");
          final int _cursorIndexOfLatitude = CursorUtil.getColumnIndexOrThrow(_cursor, "latitude");
          final int _cursorIndexOfLongitude = CursorUtil.getColumnIndexOrThrow(_cursor, "longitude");
          final int _cursorIndexOfAltitude = CursorUtil.getColumnIndexOrThrow(_cursor, "altitude");
          final int _cursorIndexOfAccuracy = CursorUtil.getColumnIndexOrThrow(_cursor, "accuracy");
          final int _cursorIndexOfAddress = CursorUtil.getColumnIndexOrThrow(_cursor, "address");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfAssignedTechnicianId = CursorUtil.getColumnIndexOrThrow(_cursor, "assigned_technician_id");
          final int _cursorIndexOfCustomerName = CursorUtil.getColumnIndexOrThrow(_cursor, "customer_name");
          final int _cursorIndexOfCustomerPhone = CursorUtil.getColumnIndexOrThrow(_cursor, "customer_phone");
          final int _cursorIndexOfCustomerEmail = CursorUtil.getColumnIndexOrThrow(_cursor, "customer_email");
          final int _cursorIndexOfInstallationDate = CursorUtil.getColumnIndexOrThrow(_cursor, "installation_date");
          final int _cursorIndexOfActivationStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "activation_status");
          final int _cursorIndexOfActivationDate = CursorUtil.getColumnIndexOrThrow(_cursor, "activation_date");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfPriority = CursorUtil.getColumnIndexOrThrow(_cursor, "priority");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final int _cursorIndexOfSyncStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "sync_status");
          final int _cursorIndexOfLastSyncAttempt = CursorUtil.getColumnIndexOrThrow(_cursor, "last_sync_attempt");
          final int _cursorIndexOfSyncError = CursorUtil.getColumnIndexOrThrow(_cursor, "sync_error");
          final int _cursorIndexOfAssignedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "assigned_at");
          final int _cursorIndexOfCompletedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "completed_at");
          final int _cursorIndexOfNeedsSync = CursorUtil.getColumnIndexOrThrow(_cursor, "needs_sync");
          final List<DropEntity> _result = new ArrayList<DropEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final DropEntity _item;
            final String _tmpDropNumber;
            if (_cursor.isNull(_cursorIndexOfDropNumber)) {
              _tmpDropNumber = null;
            } else {
              _tmpDropNumber = _cursor.getString(_cursorIndexOfDropNumber);
            }
            final int _tmpProjectId;
            _tmpProjectId = _cursor.getInt(_cursorIndexOfProjectId);
            final double _tmpLatitude;
            _tmpLatitude = _cursor.getDouble(_cursorIndexOfLatitude);
            final double _tmpLongitude;
            _tmpLongitude = _cursor.getDouble(_cursorIndexOfLongitude);
            final Double _tmpAltitude;
            if (_cursor.isNull(_cursorIndexOfAltitude)) {
              _tmpAltitude = null;
            } else {
              _tmpAltitude = _cursor.getDouble(_cursorIndexOfAltitude);
            }
            final Float _tmpAccuracy;
            if (_cursor.isNull(_cursorIndexOfAccuracy)) {
              _tmpAccuracy = null;
            } else {
              _tmpAccuracy = _cursor.getFloat(_cursorIndexOfAccuracy);
            }
            final String _tmpAddress;
            if (_cursor.isNull(_cursorIndexOfAddress)) {
              _tmpAddress = null;
            } else {
              _tmpAddress = _cursor.getString(_cursorIndexOfAddress);
            }
            final DropStatus _tmpStatus;
            _tmpStatus = __DropStatus_stringToEnum(_cursor.getString(_cursorIndexOfStatus));
            final String _tmpAssignedTechnicianId;
            if (_cursor.isNull(_cursorIndexOfAssignedTechnicianId)) {
              _tmpAssignedTechnicianId = null;
            } else {
              _tmpAssignedTechnicianId = _cursor.getString(_cursorIndexOfAssignedTechnicianId);
            }
            final String _tmpCustomerName;
            if (_cursor.isNull(_cursorIndexOfCustomerName)) {
              _tmpCustomerName = null;
            } else {
              _tmpCustomerName = _cursor.getString(_cursorIndexOfCustomerName);
            }
            final String _tmpCustomerPhone;
            if (_cursor.isNull(_cursorIndexOfCustomerPhone)) {
              _tmpCustomerPhone = null;
            } else {
              _tmpCustomerPhone = _cursor.getString(_cursorIndexOfCustomerPhone);
            }
            final String _tmpCustomerEmail;
            if (_cursor.isNull(_cursorIndexOfCustomerEmail)) {
              _tmpCustomerEmail = null;
            } else {
              _tmpCustomerEmail = _cursor.getString(_cursorIndexOfCustomerEmail);
            }
            final Date _tmpInstallationDate;
            final Long _tmp;
            if (_cursor.isNull(_cursorIndexOfInstallationDate)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getLong(_cursorIndexOfInstallationDate);
            }
            _tmpInstallationDate = __dateConverters.fromTimestamp(_tmp);
            final ActivationStatus _tmpActivationStatus;
            _tmpActivationStatus = __ActivationStatus_stringToEnum(_cursor.getString(_cursorIndexOfActivationStatus));
            final Date _tmpActivationDate;
            final Long _tmp_1;
            if (_cursor.isNull(_cursorIndexOfActivationDate)) {
              _tmp_1 = null;
            } else {
              _tmp_1 = _cursor.getLong(_cursorIndexOfActivationDate);
            }
            _tmpActivationDate = __dateConverters.fromTimestamp(_tmp_1);
            final String _tmpNotes;
            if (_cursor.isNull(_cursorIndexOfNotes)) {
              _tmpNotes = null;
            } else {
              _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
            }
            final int _tmpPriority;
            _tmpPriority = _cursor.getInt(_cursorIndexOfPriority);
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
            final SyncStatus _tmpSyncStatus;
            final String _tmp_4;
            if (_cursor.isNull(_cursorIndexOfSyncStatus)) {
              _tmp_4 = null;
            } else {
              _tmp_4 = _cursor.getString(_cursorIndexOfSyncStatus);
            }
            _tmpSyncStatus = __statusConverters.toSyncStatus(_tmp_4);
            final Date _tmpLastSyncAttempt;
            final Long _tmp_5;
            if (_cursor.isNull(_cursorIndexOfLastSyncAttempt)) {
              _tmp_5 = null;
            } else {
              _tmp_5 = _cursor.getLong(_cursorIndexOfLastSyncAttempt);
            }
            _tmpLastSyncAttempt = __dateConverters.fromTimestamp(_tmp_5);
            final String _tmpSyncError;
            if (_cursor.isNull(_cursorIndexOfSyncError)) {
              _tmpSyncError = null;
            } else {
              _tmpSyncError = _cursor.getString(_cursorIndexOfSyncError);
            }
            final Date _tmpAssignedAt;
            final Long _tmp_6;
            if (_cursor.isNull(_cursorIndexOfAssignedAt)) {
              _tmp_6 = null;
            } else {
              _tmp_6 = _cursor.getLong(_cursorIndexOfAssignedAt);
            }
            _tmpAssignedAt = __dateConverters.fromTimestamp(_tmp_6);
            final Date _tmpCompletedAt;
            final Long _tmp_7;
            if (_cursor.isNull(_cursorIndexOfCompletedAt)) {
              _tmp_7 = null;
            } else {
              _tmp_7 = _cursor.getLong(_cursorIndexOfCompletedAt);
            }
            _tmpCompletedAt = __dateConverters.fromTimestamp(_tmp_7);
            final boolean _tmpNeedsSync;
            final int _tmp_8;
            _tmp_8 = _cursor.getInt(_cursorIndexOfNeedsSync);
            _tmpNeedsSync = _tmp_8 != 0;
            _item = new DropEntity(_tmpDropNumber,_tmpProjectId,_tmpLatitude,_tmpLongitude,_tmpAltitude,_tmpAccuracy,_tmpAddress,_tmpStatus,_tmpAssignedTechnicianId,_tmpCustomerName,_tmpCustomerPhone,_tmpCustomerEmail,_tmpInstallationDate,_tmpActivationStatus,_tmpActivationDate,_tmpNotes,_tmpPriority,_tmpCreatedAt,_tmpUpdatedAt,_tmpSyncStatus,_tmpLastSyncAttempt,_tmpSyncError,_tmpAssignedAt,_tmpCompletedAt,_tmpNeedsSync);
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
  public Flow<List<DropEntity>> getAvailableDropsFlow() {
    final String _sql = "SELECT * FROM drops WHERE status IN ('AVAILABLE', 'PENDING') ORDER BY priority DESC, created_at ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"drops"}, new Callable<List<DropEntity>>() {
      @Override
      @NonNull
      public List<DropEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfDropNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "drop_number");
          final int _cursorIndexOfProjectId = CursorUtil.getColumnIndexOrThrow(_cursor, "project_id");
          final int _cursorIndexOfLatitude = CursorUtil.getColumnIndexOrThrow(_cursor, "latitude");
          final int _cursorIndexOfLongitude = CursorUtil.getColumnIndexOrThrow(_cursor, "longitude");
          final int _cursorIndexOfAltitude = CursorUtil.getColumnIndexOrThrow(_cursor, "altitude");
          final int _cursorIndexOfAccuracy = CursorUtil.getColumnIndexOrThrow(_cursor, "accuracy");
          final int _cursorIndexOfAddress = CursorUtil.getColumnIndexOrThrow(_cursor, "address");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfAssignedTechnicianId = CursorUtil.getColumnIndexOrThrow(_cursor, "assigned_technician_id");
          final int _cursorIndexOfCustomerName = CursorUtil.getColumnIndexOrThrow(_cursor, "customer_name");
          final int _cursorIndexOfCustomerPhone = CursorUtil.getColumnIndexOrThrow(_cursor, "customer_phone");
          final int _cursorIndexOfCustomerEmail = CursorUtil.getColumnIndexOrThrow(_cursor, "customer_email");
          final int _cursorIndexOfInstallationDate = CursorUtil.getColumnIndexOrThrow(_cursor, "installation_date");
          final int _cursorIndexOfActivationStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "activation_status");
          final int _cursorIndexOfActivationDate = CursorUtil.getColumnIndexOrThrow(_cursor, "activation_date");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfPriority = CursorUtil.getColumnIndexOrThrow(_cursor, "priority");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final int _cursorIndexOfSyncStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "sync_status");
          final int _cursorIndexOfLastSyncAttempt = CursorUtil.getColumnIndexOrThrow(_cursor, "last_sync_attempt");
          final int _cursorIndexOfSyncError = CursorUtil.getColumnIndexOrThrow(_cursor, "sync_error");
          final int _cursorIndexOfAssignedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "assigned_at");
          final int _cursorIndexOfCompletedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "completed_at");
          final int _cursorIndexOfNeedsSync = CursorUtil.getColumnIndexOrThrow(_cursor, "needs_sync");
          final List<DropEntity> _result = new ArrayList<DropEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final DropEntity _item;
            final String _tmpDropNumber;
            if (_cursor.isNull(_cursorIndexOfDropNumber)) {
              _tmpDropNumber = null;
            } else {
              _tmpDropNumber = _cursor.getString(_cursorIndexOfDropNumber);
            }
            final int _tmpProjectId;
            _tmpProjectId = _cursor.getInt(_cursorIndexOfProjectId);
            final double _tmpLatitude;
            _tmpLatitude = _cursor.getDouble(_cursorIndexOfLatitude);
            final double _tmpLongitude;
            _tmpLongitude = _cursor.getDouble(_cursorIndexOfLongitude);
            final Double _tmpAltitude;
            if (_cursor.isNull(_cursorIndexOfAltitude)) {
              _tmpAltitude = null;
            } else {
              _tmpAltitude = _cursor.getDouble(_cursorIndexOfAltitude);
            }
            final Float _tmpAccuracy;
            if (_cursor.isNull(_cursorIndexOfAccuracy)) {
              _tmpAccuracy = null;
            } else {
              _tmpAccuracy = _cursor.getFloat(_cursorIndexOfAccuracy);
            }
            final String _tmpAddress;
            if (_cursor.isNull(_cursorIndexOfAddress)) {
              _tmpAddress = null;
            } else {
              _tmpAddress = _cursor.getString(_cursorIndexOfAddress);
            }
            final DropStatus _tmpStatus;
            _tmpStatus = __DropStatus_stringToEnum(_cursor.getString(_cursorIndexOfStatus));
            final String _tmpAssignedTechnicianId;
            if (_cursor.isNull(_cursorIndexOfAssignedTechnicianId)) {
              _tmpAssignedTechnicianId = null;
            } else {
              _tmpAssignedTechnicianId = _cursor.getString(_cursorIndexOfAssignedTechnicianId);
            }
            final String _tmpCustomerName;
            if (_cursor.isNull(_cursorIndexOfCustomerName)) {
              _tmpCustomerName = null;
            } else {
              _tmpCustomerName = _cursor.getString(_cursorIndexOfCustomerName);
            }
            final String _tmpCustomerPhone;
            if (_cursor.isNull(_cursorIndexOfCustomerPhone)) {
              _tmpCustomerPhone = null;
            } else {
              _tmpCustomerPhone = _cursor.getString(_cursorIndexOfCustomerPhone);
            }
            final String _tmpCustomerEmail;
            if (_cursor.isNull(_cursorIndexOfCustomerEmail)) {
              _tmpCustomerEmail = null;
            } else {
              _tmpCustomerEmail = _cursor.getString(_cursorIndexOfCustomerEmail);
            }
            final Date _tmpInstallationDate;
            final Long _tmp;
            if (_cursor.isNull(_cursorIndexOfInstallationDate)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getLong(_cursorIndexOfInstallationDate);
            }
            _tmpInstallationDate = __dateConverters.fromTimestamp(_tmp);
            final ActivationStatus _tmpActivationStatus;
            _tmpActivationStatus = __ActivationStatus_stringToEnum(_cursor.getString(_cursorIndexOfActivationStatus));
            final Date _tmpActivationDate;
            final Long _tmp_1;
            if (_cursor.isNull(_cursorIndexOfActivationDate)) {
              _tmp_1 = null;
            } else {
              _tmp_1 = _cursor.getLong(_cursorIndexOfActivationDate);
            }
            _tmpActivationDate = __dateConverters.fromTimestamp(_tmp_1);
            final String _tmpNotes;
            if (_cursor.isNull(_cursorIndexOfNotes)) {
              _tmpNotes = null;
            } else {
              _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
            }
            final int _tmpPriority;
            _tmpPriority = _cursor.getInt(_cursorIndexOfPriority);
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
            final SyncStatus _tmpSyncStatus;
            final String _tmp_4;
            if (_cursor.isNull(_cursorIndexOfSyncStatus)) {
              _tmp_4 = null;
            } else {
              _tmp_4 = _cursor.getString(_cursorIndexOfSyncStatus);
            }
            _tmpSyncStatus = __statusConverters.toSyncStatus(_tmp_4);
            final Date _tmpLastSyncAttempt;
            final Long _tmp_5;
            if (_cursor.isNull(_cursorIndexOfLastSyncAttempt)) {
              _tmp_5 = null;
            } else {
              _tmp_5 = _cursor.getLong(_cursorIndexOfLastSyncAttempt);
            }
            _tmpLastSyncAttempt = __dateConverters.fromTimestamp(_tmp_5);
            final String _tmpSyncError;
            if (_cursor.isNull(_cursorIndexOfSyncError)) {
              _tmpSyncError = null;
            } else {
              _tmpSyncError = _cursor.getString(_cursorIndexOfSyncError);
            }
            final Date _tmpAssignedAt;
            final Long _tmp_6;
            if (_cursor.isNull(_cursorIndexOfAssignedAt)) {
              _tmp_6 = null;
            } else {
              _tmp_6 = _cursor.getLong(_cursorIndexOfAssignedAt);
            }
            _tmpAssignedAt = __dateConverters.fromTimestamp(_tmp_6);
            final Date _tmpCompletedAt;
            final Long _tmp_7;
            if (_cursor.isNull(_cursorIndexOfCompletedAt)) {
              _tmp_7 = null;
            } else {
              _tmp_7 = _cursor.getLong(_cursorIndexOfCompletedAt);
            }
            _tmpCompletedAt = __dateConverters.fromTimestamp(_tmp_7);
            final boolean _tmpNeedsSync;
            final int _tmp_8;
            _tmp_8 = _cursor.getInt(_cursorIndexOfNeedsSync);
            _tmpNeedsSync = _tmp_8 != 0;
            _item = new DropEntity(_tmpDropNumber,_tmpProjectId,_tmpLatitude,_tmpLongitude,_tmpAltitude,_tmpAccuracy,_tmpAddress,_tmpStatus,_tmpAssignedTechnicianId,_tmpCustomerName,_tmpCustomerPhone,_tmpCustomerEmail,_tmpInstallationDate,_tmpActivationStatus,_tmpActivationDate,_tmpNotes,_tmpPriority,_tmpCreatedAt,_tmpUpdatedAt,_tmpSyncStatus,_tmpLastSyncAttempt,_tmpSyncError,_tmpAssignedAt,_tmpCompletedAt,_tmpNeedsSync);
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
  public Object getDropsInProximity(final double minLat, final double maxLat, final double minLng,
      final double maxLng, final Continuation<? super List<DropEntity>> $completion) {
    final String _sql = "SELECT * FROM drops WHERE status IN ('AVAILABLE', 'PENDING') AND latitude BETWEEN ? AND ? AND longitude BETWEEN ? AND ? ORDER BY priority DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 4);
    int _argIndex = 1;
    _statement.bindDouble(_argIndex, minLat);
    _argIndex = 2;
    _statement.bindDouble(_argIndex, maxLat);
    _argIndex = 3;
    _statement.bindDouble(_argIndex, minLng);
    _argIndex = 4;
    _statement.bindDouble(_argIndex, maxLng);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<DropEntity>>() {
      @Override
      @NonNull
      public List<DropEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfDropNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "drop_number");
          final int _cursorIndexOfProjectId = CursorUtil.getColumnIndexOrThrow(_cursor, "project_id");
          final int _cursorIndexOfLatitude = CursorUtil.getColumnIndexOrThrow(_cursor, "latitude");
          final int _cursorIndexOfLongitude = CursorUtil.getColumnIndexOrThrow(_cursor, "longitude");
          final int _cursorIndexOfAltitude = CursorUtil.getColumnIndexOrThrow(_cursor, "altitude");
          final int _cursorIndexOfAccuracy = CursorUtil.getColumnIndexOrThrow(_cursor, "accuracy");
          final int _cursorIndexOfAddress = CursorUtil.getColumnIndexOrThrow(_cursor, "address");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfAssignedTechnicianId = CursorUtil.getColumnIndexOrThrow(_cursor, "assigned_technician_id");
          final int _cursorIndexOfCustomerName = CursorUtil.getColumnIndexOrThrow(_cursor, "customer_name");
          final int _cursorIndexOfCustomerPhone = CursorUtil.getColumnIndexOrThrow(_cursor, "customer_phone");
          final int _cursorIndexOfCustomerEmail = CursorUtil.getColumnIndexOrThrow(_cursor, "customer_email");
          final int _cursorIndexOfInstallationDate = CursorUtil.getColumnIndexOrThrow(_cursor, "installation_date");
          final int _cursorIndexOfActivationStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "activation_status");
          final int _cursorIndexOfActivationDate = CursorUtil.getColumnIndexOrThrow(_cursor, "activation_date");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfPriority = CursorUtil.getColumnIndexOrThrow(_cursor, "priority");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final int _cursorIndexOfSyncStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "sync_status");
          final int _cursorIndexOfLastSyncAttempt = CursorUtil.getColumnIndexOrThrow(_cursor, "last_sync_attempt");
          final int _cursorIndexOfSyncError = CursorUtil.getColumnIndexOrThrow(_cursor, "sync_error");
          final int _cursorIndexOfAssignedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "assigned_at");
          final int _cursorIndexOfCompletedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "completed_at");
          final int _cursorIndexOfNeedsSync = CursorUtil.getColumnIndexOrThrow(_cursor, "needs_sync");
          final List<DropEntity> _result = new ArrayList<DropEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final DropEntity _item;
            final String _tmpDropNumber;
            if (_cursor.isNull(_cursorIndexOfDropNumber)) {
              _tmpDropNumber = null;
            } else {
              _tmpDropNumber = _cursor.getString(_cursorIndexOfDropNumber);
            }
            final int _tmpProjectId;
            _tmpProjectId = _cursor.getInt(_cursorIndexOfProjectId);
            final double _tmpLatitude;
            _tmpLatitude = _cursor.getDouble(_cursorIndexOfLatitude);
            final double _tmpLongitude;
            _tmpLongitude = _cursor.getDouble(_cursorIndexOfLongitude);
            final Double _tmpAltitude;
            if (_cursor.isNull(_cursorIndexOfAltitude)) {
              _tmpAltitude = null;
            } else {
              _tmpAltitude = _cursor.getDouble(_cursorIndexOfAltitude);
            }
            final Float _tmpAccuracy;
            if (_cursor.isNull(_cursorIndexOfAccuracy)) {
              _tmpAccuracy = null;
            } else {
              _tmpAccuracy = _cursor.getFloat(_cursorIndexOfAccuracy);
            }
            final String _tmpAddress;
            if (_cursor.isNull(_cursorIndexOfAddress)) {
              _tmpAddress = null;
            } else {
              _tmpAddress = _cursor.getString(_cursorIndexOfAddress);
            }
            final DropStatus _tmpStatus;
            _tmpStatus = __DropStatus_stringToEnum(_cursor.getString(_cursorIndexOfStatus));
            final String _tmpAssignedTechnicianId;
            if (_cursor.isNull(_cursorIndexOfAssignedTechnicianId)) {
              _tmpAssignedTechnicianId = null;
            } else {
              _tmpAssignedTechnicianId = _cursor.getString(_cursorIndexOfAssignedTechnicianId);
            }
            final String _tmpCustomerName;
            if (_cursor.isNull(_cursorIndexOfCustomerName)) {
              _tmpCustomerName = null;
            } else {
              _tmpCustomerName = _cursor.getString(_cursorIndexOfCustomerName);
            }
            final String _tmpCustomerPhone;
            if (_cursor.isNull(_cursorIndexOfCustomerPhone)) {
              _tmpCustomerPhone = null;
            } else {
              _tmpCustomerPhone = _cursor.getString(_cursorIndexOfCustomerPhone);
            }
            final String _tmpCustomerEmail;
            if (_cursor.isNull(_cursorIndexOfCustomerEmail)) {
              _tmpCustomerEmail = null;
            } else {
              _tmpCustomerEmail = _cursor.getString(_cursorIndexOfCustomerEmail);
            }
            final Date _tmpInstallationDate;
            final Long _tmp;
            if (_cursor.isNull(_cursorIndexOfInstallationDate)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getLong(_cursorIndexOfInstallationDate);
            }
            _tmpInstallationDate = __dateConverters.fromTimestamp(_tmp);
            final ActivationStatus _tmpActivationStatus;
            _tmpActivationStatus = __ActivationStatus_stringToEnum(_cursor.getString(_cursorIndexOfActivationStatus));
            final Date _tmpActivationDate;
            final Long _tmp_1;
            if (_cursor.isNull(_cursorIndexOfActivationDate)) {
              _tmp_1 = null;
            } else {
              _tmp_1 = _cursor.getLong(_cursorIndexOfActivationDate);
            }
            _tmpActivationDate = __dateConverters.fromTimestamp(_tmp_1);
            final String _tmpNotes;
            if (_cursor.isNull(_cursorIndexOfNotes)) {
              _tmpNotes = null;
            } else {
              _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
            }
            final int _tmpPriority;
            _tmpPriority = _cursor.getInt(_cursorIndexOfPriority);
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
            final SyncStatus _tmpSyncStatus;
            final String _tmp_4;
            if (_cursor.isNull(_cursorIndexOfSyncStatus)) {
              _tmp_4 = null;
            } else {
              _tmp_4 = _cursor.getString(_cursorIndexOfSyncStatus);
            }
            _tmpSyncStatus = __statusConverters.toSyncStatus(_tmp_4);
            final Date _tmpLastSyncAttempt;
            final Long _tmp_5;
            if (_cursor.isNull(_cursorIndexOfLastSyncAttempt)) {
              _tmp_5 = null;
            } else {
              _tmp_5 = _cursor.getLong(_cursorIndexOfLastSyncAttempt);
            }
            _tmpLastSyncAttempt = __dateConverters.fromTimestamp(_tmp_5);
            final String _tmpSyncError;
            if (_cursor.isNull(_cursorIndexOfSyncError)) {
              _tmpSyncError = null;
            } else {
              _tmpSyncError = _cursor.getString(_cursorIndexOfSyncError);
            }
            final Date _tmpAssignedAt;
            final Long _tmp_6;
            if (_cursor.isNull(_cursorIndexOfAssignedAt)) {
              _tmp_6 = null;
            } else {
              _tmp_6 = _cursor.getLong(_cursorIndexOfAssignedAt);
            }
            _tmpAssignedAt = __dateConverters.fromTimestamp(_tmp_6);
            final Date _tmpCompletedAt;
            final Long _tmp_7;
            if (_cursor.isNull(_cursorIndexOfCompletedAt)) {
              _tmp_7 = null;
            } else {
              _tmp_7 = _cursor.getLong(_cursorIndexOfCompletedAt);
            }
            _tmpCompletedAt = __dateConverters.fromTimestamp(_tmp_7);
            final boolean _tmpNeedsSync;
            final int _tmp_8;
            _tmp_8 = _cursor.getInt(_cursorIndexOfNeedsSync);
            _tmpNeedsSync = _tmp_8 != 0;
            _item = new DropEntity(_tmpDropNumber,_tmpProjectId,_tmpLatitude,_tmpLongitude,_tmpAltitude,_tmpAccuracy,_tmpAddress,_tmpStatus,_tmpAssignedTechnicianId,_tmpCustomerName,_tmpCustomerPhone,_tmpCustomerEmail,_tmpInstallationDate,_tmpActivationStatus,_tmpActivationDate,_tmpNotes,_tmpPriority,_tmpCreatedAt,_tmpUpdatedAt,_tmpSyncStatus,_tmpLastSyncAttempt,_tmpSyncError,_tmpAssignedAt,_tmpCompletedAt,_tmpNeedsSync);
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
  public Object getDropsAssignedToTechnician(final String technicianId,
      final Continuation<? super List<DropEntity>> $completion) {
    final String _sql = "SELECT * FROM drops WHERE assigned_technician_id = ? ORDER BY priority DESC, assigned_at ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    if (technicianId == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, technicianId);
    }
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<DropEntity>>() {
      @Override
      @NonNull
      public List<DropEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfDropNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "drop_number");
          final int _cursorIndexOfProjectId = CursorUtil.getColumnIndexOrThrow(_cursor, "project_id");
          final int _cursorIndexOfLatitude = CursorUtil.getColumnIndexOrThrow(_cursor, "latitude");
          final int _cursorIndexOfLongitude = CursorUtil.getColumnIndexOrThrow(_cursor, "longitude");
          final int _cursorIndexOfAltitude = CursorUtil.getColumnIndexOrThrow(_cursor, "altitude");
          final int _cursorIndexOfAccuracy = CursorUtil.getColumnIndexOrThrow(_cursor, "accuracy");
          final int _cursorIndexOfAddress = CursorUtil.getColumnIndexOrThrow(_cursor, "address");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfAssignedTechnicianId = CursorUtil.getColumnIndexOrThrow(_cursor, "assigned_technician_id");
          final int _cursorIndexOfCustomerName = CursorUtil.getColumnIndexOrThrow(_cursor, "customer_name");
          final int _cursorIndexOfCustomerPhone = CursorUtil.getColumnIndexOrThrow(_cursor, "customer_phone");
          final int _cursorIndexOfCustomerEmail = CursorUtil.getColumnIndexOrThrow(_cursor, "customer_email");
          final int _cursorIndexOfInstallationDate = CursorUtil.getColumnIndexOrThrow(_cursor, "installation_date");
          final int _cursorIndexOfActivationStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "activation_status");
          final int _cursorIndexOfActivationDate = CursorUtil.getColumnIndexOrThrow(_cursor, "activation_date");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfPriority = CursorUtil.getColumnIndexOrThrow(_cursor, "priority");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final int _cursorIndexOfSyncStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "sync_status");
          final int _cursorIndexOfLastSyncAttempt = CursorUtil.getColumnIndexOrThrow(_cursor, "last_sync_attempt");
          final int _cursorIndexOfSyncError = CursorUtil.getColumnIndexOrThrow(_cursor, "sync_error");
          final int _cursorIndexOfAssignedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "assigned_at");
          final int _cursorIndexOfCompletedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "completed_at");
          final int _cursorIndexOfNeedsSync = CursorUtil.getColumnIndexOrThrow(_cursor, "needs_sync");
          final List<DropEntity> _result = new ArrayList<DropEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final DropEntity _item;
            final String _tmpDropNumber;
            if (_cursor.isNull(_cursorIndexOfDropNumber)) {
              _tmpDropNumber = null;
            } else {
              _tmpDropNumber = _cursor.getString(_cursorIndexOfDropNumber);
            }
            final int _tmpProjectId;
            _tmpProjectId = _cursor.getInt(_cursorIndexOfProjectId);
            final double _tmpLatitude;
            _tmpLatitude = _cursor.getDouble(_cursorIndexOfLatitude);
            final double _tmpLongitude;
            _tmpLongitude = _cursor.getDouble(_cursorIndexOfLongitude);
            final Double _tmpAltitude;
            if (_cursor.isNull(_cursorIndexOfAltitude)) {
              _tmpAltitude = null;
            } else {
              _tmpAltitude = _cursor.getDouble(_cursorIndexOfAltitude);
            }
            final Float _tmpAccuracy;
            if (_cursor.isNull(_cursorIndexOfAccuracy)) {
              _tmpAccuracy = null;
            } else {
              _tmpAccuracy = _cursor.getFloat(_cursorIndexOfAccuracy);
            }
            final String _tmpAddress;
            if (_cursor.isNull(_cursorIndexOfAddress)) {
              _tmpAddress = null;
            } else {
              _tmpAddress = _cursor.getString(_cursorIndexOfAddress);
            }
            final DropStatus _tmpStatus;
            _tmpStatus = __DropStatus_stringToEnum(_cursor.getString(_cursorIndexOfStatus));
            final String _tmpAssignedTechnicianId;
            if (_cursor.isNull(_cursorIndexOfAssignedTechnicianId)) {
              _tmpAssignedTechnicianId = null;
            } else {
              _tmpAssignedTechnicianId = _cursor.getString(_cursorIndexOfAssignedTechnicianId);
            }
            final String _tmpCustomerName;
            if (_cursor.isNull(_cursorIndexOfCustomerName)) {
              _tmpCustomerName = null;
            } else {
              _tmpCustomerName = _cursor.getString(_cursorIndexOfCustomerName);
            }
            final String _tmpCustomerPhone;
            if (_cursor.isNull(_cursorIndexOfCustomerPhone)) {
              _tmpCustomerPhone = null;
            } else {
              _tmpCustomerPhone = _cursor.getString(_cursorIndexOfCustomerPhone);
            }
            final String _tmpCustomerEmail;
            if (_cursor.isNull(_cursorIndexOfCustomerEmail)) {
              _tmpCustomerEmail = null;
            } else {
              _tmpCustomerEmail = _cursor.getString(_cursorIndexOfCustomerEmail);
            }
            final Date _tmpInstallationDate;
            final Long _tmp;
            if (_cursor.isNull(_cursorIndexOfInstallationDate)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getLong(_cursorIndexOfInstallationDate);
            }
            _tmpInstallationDate = __dateConverters.fromTimestamp(_tmp);
            final ActivationStatus _tmpActivationStatus;
            _tmpActivationStatus = __ActivationStatus_stringToEnum(_cursor.getString(_cursorIndexOfActivationStatus));
            final Date _tmpActivationDate;
            final Long _tmp_1;
            if (_cursor.isNull(_cursorIndexOfActivationDate)) {
              _tmp_1 = null;
            } else {
              _tmp_1 = _cursor.getLong(_cursorIndexOfActivationDate);
            }
            _tmpActivationDate = __dateConverters.fromTimestamp(_tmp_1);
            final String _tmpNotes;
            if (_cursor.isNull(_cursorIndexOfNotes)) {
              _tmpNotes = null;
            } else {
              _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
            }
            final int _tmpPriority;
            _tmpPriority = _cursor.getInt(_cursorIndexOfPriority);
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
            final SyncStatus _tmpSyncStatus;
            final String _tmp_4;
            if (_cursor.isNull(_cursorIndexOfSyncStatus)) {
              _tmp_4 = null;
            } else {
              _tmp_4 = _cursor.getString(_cursorIndexOfSyncStatus);
            }
            _tmpSyncStatus = __statusConverters.toSyncStatus(_tmp_4);
            final Date _tmpLastSyncAttempt;
            final Long _tmp_5;
            if (_cursor.isNull(_cursorIndexOfLastSyncAttempt)) {
              _tmp_5 = null;
            } else {
              _tmp_5 = _cursor.getLong(_cursorIndexOfLastSyncAttempt);
            }
            _tmpLastSyncAttempt = __dateConverters.fromTimestamp(_tmp_5);
            final String _tmpSyncError;
            if (_cursor.isNull(_cursorIndexOfSyncError)) {
              _tmpSyncError = null;
            } else {
              _tmpSyncError = _cursor.getString(_cursorIndexOfSyncError);
            }
            final Date _tmpAssignedAt;
            final Long _tmp_6;
            if (_cursor.isNull(_cursorIndexOfAssignedAt)) {
              _tmp_6 = null;
            } else {
              _tmp_6 = _cursor.getLong(_cursorIndexOfAssignedAt);
            }
            _tmpAssignedAt = __dateConverters.fromTimestamp(_tmp_6);
            final Date _tmpCompletedAt;
            final Long _tmp_7;
            if (_cursor.isNull(_cursorIndexOfCompletedAt)) {
              _tmp_7 = null;
            } else {
              _tmp_7 = _cursor.getLong(_cursorIndexOfCompletedAt);
            }
            _tmpCompletedAt = __dateConverters.fromTimestamp(_tmp_7);
            final boolean _tmpNeedsSync;
            final int _tmp_8;
            _tmp_8 = _cursor.getInt(_cursorIndexOfNeedsSync);
            _tmpNeedsSync = _tmp_8 != 0;
            _item = new DropEntity(_tmpDropNumber,_tmpProjectId,_tmpLatitude,_tmpLongitude,_tmpAltitude,_tmpAccuracy,_tmpAddress,_tmpStatus,_tmpAssignedTechnicianId,_tmpCustomerName,_tmpCustomerPhone,_tmpCustomerEmail,_tmpInstallationDate,_tmpActivationStatus,_tmpActivationDate,_tmpNotes,_tmpPriority,_tmpCreatedAt,_tmpUpdatedAt,_tmpSyncStatus,_tmpLastSyncAttempt,_tmpSyncError,_tmpAssignedAt,_tmpCompletedAt,_tmpNeedsSync);
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
  public Flow<List<DropEntity>> getDropsAssignedToTechnicianFlow(final String technicianId) {
    final String _sql = "SELECT * FROM drops WHERE assigned_technician_id = ? ORDER BY priority DESC, assigned_at ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    if (technicianId == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, technicianId);
    }
    return CoroutinesRoom.createFlow(__db, false, new String[] {"drops"}, new Callable<List<DropEntity>>() {
      @Override
      @NonNull
      public List<DropEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfDropNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "drop_number");
          final int _cursorIndexOfProjectId = CursorUtil.getColumnIndexOrThrow(_cursor, "project_id");
          final int _cursorIndexOfLatitude = CursorUtil.getColumnIndexOrThrow(_cursor, "latitude");
          final int _cursorIndexOfLongitude = CursorUtil.getColumnIndexOrThrow(_cursor, "longitude");
          final int _cursorIndexOfAltitude = CursorUtil.getColumnIndexOrThrow(_cursor, "altitude");
          final int _cursorIndexOfAccuracy = CursorUtil.getColumnIndexOrThrow(_cursor, "accuracy");
          final int _cursorIndexOfAddress = CursorUtil.getColumnIndexOrThrow(_cursor, "address");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfAssignedTechnicianId = CursorUtil.getColumnIndexOrThrow(_cursor, "assigned_technician_id");
          final int _cursorIndexOfCustomerName = CursorUtil.getColumnIndexOrThrow(_cursor, "customer_name");
          final int _cursorIndexOfCustomerPhone = CursorUtil.getColumnIndexOrThrow(_cursor, "customer_phone");
          final int _cursorIndexOfCustomerEmail = CursorUtil.getColumnIndexOrThrow(_cursor, "customer_email");
          final int _cursorIndexOfInstallationDate = CursorUtil.getColumnIndexOrThrow(_cursor, "installation_date");
          final int _cursorIndexOfActivationStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "activation_status");
          final int _cursorIndexOfActivationDate = CursorUtil.getColumnIndexOrThrow(_cursor, "activation_date");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfPriority = CursorUtil.getColumnIndexOrThrow(_cursor, "priority");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final int _cursorIndexOfSyncStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "sync_status");
          final int _cursorIndexOfLastSyncAttempt = CursorUtil.getColumnIndexOrThrow(_cursor, "last_sync_attempt");
          final int _cursorIndexOfSyncError = CursorUtil.getColumnIndexOrThrow(_cursor, "sync_error");
          final int _cursorIndexOfAssignedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "assigned_at");
          final int _cursorIndexOfCompletedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "completed_at");
          final int _cursorIndexOfNeedsSync = CursorUtil.getColumnIndexOrThrow(_cursor, "needs_sync");
          final List<DropEntity> _result = new ArrayList<DropEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final DropEntity _item;
            final String _tmpDropNumber;
            if (_cursor.isNull(_cursorIndexOfDropNumber)) {
              _tmpDropNumber = null;
            } else {
              _tmpDropNumber = _cursor.getString(_cursorIndexOfDropNumber);
            }
            final int _tmpProjectId;
            _tmpProjectId = _cursor.getInt(_cursorIndexOfProjectId);
            final double _tmpLatitude;
            _tmpLatitude = _cursor.getDouble(_cursorIndexOfLatitude);
            final double _tmpLongitude;
            _tmpLongitude = _cursor.getDouble(_cursorIndexOfLongitude);
            final Double _tmpAltitude;
            if (_cursor.isNull(_cursorIndexOfAltitude)) {
              _tmpAltitude = null;
            } else {
              _tmpAltitude = _cursor.getDouble(_cursorIndexOfAltitude);
            }
            final Float _tmpAccuracy;
            if (_cursor.isNull(_cursorIndexOfAccuracy)) {
              _tmpAccuracy = null;
            } else {
              _tmpAccuracy = _cursor.getFloat(_cursorIndexOfAccuracy);
            }
            final String _tmpAddress;
            if (_cursor.isNull(_cursorIndexOfAddress)) {
              _tmpAddress = null;
            } else {
              _tmpAddress = _cursor.getString(_cursorIndexOfAddress);
            }
            final DropStatus _tmpStatus;
            _tmpStatus = __DropStatus_stringToEnum(_cursor.getString(_cursorIndexOfStatus));
            final String _tmpAssignedTechnicianId;
            if (_cursor.isNull(_cursorIndexOfAssignedTechnicianId)) {
              _tmpAssignedTechnicianId = null;
            } else {
              _tmpAssignedTechnicianId = _cursor.getString(_cursorIndexOfAssignedTechnicianId);
            }
            final String _tmpCustomerName;
            if (_cursor.isNull(_cursorIndexOfCustomerName)) {
              _tmpCustomerName = null;
            } else {
              _tmpCustomerName = _cursor.getString(_cursorIndexOfCustomerName);
            }
            final String _tmpCustomerPhone;
            if (_cursor.isNull(_cursorIndexOfCustomerPhone)) {
              _tmpCustomerPhone = null;
            } else {
              _tmpCustomerPhone = _cursor.getString(_cursorIndexOfCustomerPhone);
            }
            final String _tmpCustomerEmail;
            if (_cursor.isNull(_cursorIndexOfCustomerEmail)) {
              _tmpCustomerEmail = null;
            } else {
              _tmpCustomerEmail = _cursor.getString(_cursorIndexOfCustomerEmail);
            }
            final Date _tmpInstallationDate;
            final Long _tmp;
            if (_cursor.isNull(_cursorIndexOfInstallationDate)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getLong(_cursorIndexOfInstallationDate);
            }
            _tmpInstallationDate = __dateConverters.fromTimestamp(_tmp);
            final ActivationStatus _tmpActivationStatus;
            _tmpActivationStatus = __ActivationStatus_stringToEnum(_cursor.getString(_cursorIndexOfActivationStatus));
            final Date _tmpActivationDate;
            final Long _tmp_1;
            if (_cursor.isNull(_cursorIndexOfActivationDate)) {
              _tmp_1 = null;
            } else {
              _tmp_1 = _cursor.getLong(_cursorIndexOfActivationDate);
            }
            _tmpActivationDate = __dateConverters.fromTimestamp(_tmp_1);
            final String _tmpNotes;
            if (_cursor.isNull(_cursorIndexOfNotes)) {
              _tmpNotes = null;
            } else {
              _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
            }
            final int _tmpPriority;
            _tmpPriority = _cursor.getInt(_cursorIndexOfPriority);
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
            final SyncStatus _tmpSyncStatus;
            final String _tmp_4;
            if (_cursor.isNull(_cursorIndexOfSyncStatus)) {
              _tmp_4 = null;
            } else {
              _tmp_4 = _cursor.getString(_cursorIndexOfSyncStatus);
            }
            _tmpSyncStatus = __statusConverters.toSyncStatus(_tmp_4);
            final Date _tmpLastSyncAttempt;
            final Long _tmp_5;
            if (_cursor.isNull(_cursorIndexOfLastSyncAttempt)) {
              _tmp_5 = null;
            } else {
              _tmp_5 = _cursor.getLong(_cursorIndexOfLastSyncAttempt);
            }
            _tmpLastSyncAttempt = __dateConverters.fromTimestamp(_tmp_5);
            final String _tmpSyncError;
            if (_cursor.isNull(_cursorIndexOfSyncError)) {
              _tmpSyncError = null;
            } else {
              _tmpSyncError = _cursor.getString(_cursorIndexOfSyncError);
            }
            final Date _tmpAssignedAt;
            final Long _tmp_6;
            if (_cursor.isNull(_cursorIndexOfAssignedAt)) {
              _tmp_6 = null;
            } else {
              _tmp_6 = _cursor.getLong(_cursorIndexOfAssignedAt);
            }
            _tmpAssignedAt = __dateConverters.fromTimestamp(_tmp_6);
            final Date _tmpCompletedAt;
            final Long _tmp_7;
            if (_cursor.isNull(_cursorIndexOfCompletedAt)) {
              _tmp_7 = null;
            } else {
              _tmp_7 = _cursor.getLong(_cursorIndexOfCompletedAt);
            }
            _tmpCompletedAt = __dateConverters.fromTimestamp(_tmp_7);
            final boolean _tmpNeedsSync;
            final int _tmp_8;
            _tmp_8 = _cursor.getInt(_cursorIndexOfNeedsSync);
            _tmpNeedsSync = _tmp_8 != 0;
            _item = new DropEntity(_tmpDropNumber,_tmpProjectId,_tmpLatitude,_tmpLongitude,_tmpAltitude,_tmpAccuracy,_tmpAddress,_tmpStatus,_tmpAssignedTechnicianId,_tmpCustomerName,_tmpCustomerPhone,_tmpCustomerEmail,_tmpInstallationDate,_tmpActivationStatus,_tmpActivationDate,_tmpNotes,_tmpPriority,_tmpCreatedAt,_tmpUpdatedAt,_tmpSyncStatus,_tmpLastSyncAttempt,_tmpSyncError,_tmpAssignedAt,_tmpCompletedAt,_tmpNeedsSync);
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
  public Object getDropCountByStatus(final String status,
      final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT COUNT(*) FROM drops WHERE status = ?";
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
  public Object getTotalDropCount(final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT COUNT(*) FROM drops";
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
  public Object getDropsNeedingSync(final Continuation<? super List<DropEntity>> $completion) {
    final String _sql = "SELECT * FROM drops WHERE needs_sync = 1 ORDER BY updated_at ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<DropEntity>>() {
      @Override
      @NonNull
      public List<DropEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfDropNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "drop_number");
          final int _cursorIndexOfProjectId = CursorUtil.getColumnIndexOrThrow(_cursor, "project_id");
          final int _cursorIndexOfLatitude = CursorUtil.getColumnIndexOrThrow(_cursor, "latitude");
          final int _cursorIndexOfLongitude = CursorUtil.getColumnIndexOrThrow(_cursor, "longitude");
          final int _cursorIndexOfAltitude = CursorUtil.getColumnIndexOrThrow(_cursor, "altitude");
          final int _cursorIndexOfAccuracy = CursorUtil.getColumnIndexOrThrow(_cursor, "accuracy");
          final int _cursorIndexOfAddress = CursorUtil.getColumnIndexOrThrow(_cursor, "address");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfAssignedTechnicianId = CursorUtil.getColumnIndexOrThrow(_cursor, "assigned_technician_id");
          final int _cursorIndexOfCustomerName = CursorUtil.getColumnIndexOrThrow(_cursor, "customer_name");
          final int _cursorIndexOfCustomerPhone = CursorUtil.getColumnIndexOrThrow(_cursor, "customer_phone");
          final int _cursorIndexOfCustomerEmail = CursorUtil.getColumnIndexOrThrow(_cursor, "customer_email");
          final int _cursorIndexOfInstallationDate = CursorUtil.getColumnIndexOrThrow(_cursor, "installation_date");
          final int _cursorIndexOfActivationStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "activation_status");
          final int _cursorIndexOfActivationDate = CursorUtil.getColumnIndexOrThrow(_cursor, "activation_date");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfPriority = CursorUtil.getColumnIndexOrThrow(_cursor, "priority");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final int _cursorIndexOfSyncStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "sync_status");
          final int _cursorIndexOfLastSyncAttempt = CursorUtil.getColumnIndexOrThrow(_cursor, "last_sync_attempt");
          final int _cursorIndexOfSyncError = CursorUtil.getColumnIndexOrThrow(_cursor, "sync_error");
          final int _cursorIndexOfAssignedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "assigned_at");
          final int _cursorIndexOfCompletedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "completed_at");
          final int _cursorIndexOfNeedsSync = CursorUtil.getColumnIndexOrThrow(_cursor, "needs_sync");
          final List<DropEntity> _result = new ArrayList<DropEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final DropEntity _item;
            final String _tmpDropNumber;
            if (_cursor.isNull(_cursorIndexOfDropNumber)) {
              _tmpDropNumber = null;
            } else {
              _tmpDropNumber = _cursor.getString(_cursorIndexOfDropNumber);
            }
            final int _tmpProjectId;
            _tmpProjectId = _cursor.getInt(_cursorIndexOfProjectId);
            final double _tmpLatitude;
            _tmpLatitude = _cursor.getDouble(_cursorIndexOfLatitude);
            final double _tmpLongitude;
            _tmpLongitude = _cursor.getDouble(_cursorIndexOfLongitude);
            final Double _tmpAltitude;
            if (_cursor.isNull(_cursorIndexOfAltitude)) {
              _tmpAltitude = null;
            } else {
              _tmpAltitude = _cursor.getDouble(_cursorIndexOfAltitude);
            }
            final Float _tmpAccuracy;
            if (_cursor.isNull(_cursorIndexOfAccuracy)) {
              _tmpAccuracy = null;
            } else {
              _tmpAccuracy = _cursor.getFloat(_cursorIndexOfAccuracy);
            }
            final String _tmpAddress;
            if (_cursor.isNull(_cursorIndexOfAddress)) {
              _tmpAddress = null;
            } else {
              _tmpAddress = _cursor.getString(_cursorIndexOfAddress);
            }
            final DropStatus _tmpStatus;
            _tmpStatus = __DropStatus_stringToEnum(_cursor.getString(_cursorIndexOfStatus));
            final String _tmpAssignedTechnicianId;
            if (_cursor.isNull(_cursorIndexOfAssignedTechnicianId)) {
              _tmpAssignedTechnicianId = null;
            } else {
              _tmpAssignedTechnicianId = _cursor.getString(_cursorIndexOfAssignedTechnicianId);
            }
            final String _tmpCustomerName;
            if (_cursor.isNull(_cursorIndexOfCustomerName)) {
              _tmpCustomerName = null;
            } else {
              _tmpCustomerName = _cursor.getString(_cursorIndexOfCustomerName);
            }
            final String _tmpCustomerPhone;
            if (_cursor.isNull(_cursorIndexOfCustomerPhone)) {
              _tmpCustomerPhone = null;
            } else {
              _tmpCustomerPhone = _cursor.getString(_cursorIndexOfCustomerPhone);
            }
            final String _tmpCustomerEmail;
            if (_cursor.isNull(_cursorIndexOfCustomerEmail)) {
              _tmpCustomerEmail = null;
            } else {
              _tmpCustomerEmail = _cursor.getString(_cursorIndexOfCustomerEmail);
            }
            final Date _tmpInstallationDate;
            final Long _tmp;
            if (_cursor.isNull(_cursorIndexOfInstallationDate)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getLong(_cursorIndexOfInstallationDate);
            }
            _tmpInstallationDate = __dateConverters.fromTimestamp(_tmp);
            final ActivationStatus _tmpActivationStatus;
            _tmpActivationStatus = __ActivationStatus_stringToEnum(_cursor.getString(_cursorIndexOfActivationStatus));
            final Date _tmpActivationDate;
            final Long _tmp_1;
            if (_cursor.isNull(_cursorIndexOfActivationDate)) {
              _tmp_1 = null;
            } else {
              _tmp_1 = _cursor.getLong(_cursorIndexOfActivationDate);
            }
            _tmpActivationDate = __dateConverters.fromTimestamp(_tmp_1);
            final String _tmpNotes;
            if (_cursor.isNull(_cursorIndexOfNotes)) {
              _tmpNotes = null;
            } else {
              _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
            }
            final int _tmpPriority;
            _tmpPriority = _cursor.getInt(_cursorIndexOfPriority);
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
            final SyncStatus _tmpSyncStatus;
            final String _tmp_4;
            if (_cursor.isNull(_cursorIndexOfSyncStatus)) {
              _tmp_4 = null;
            } else {
              _tmp_4 = _cursor.getString(_cursorIndexOfSyncStatus);
            }
            _tmpSyncStatus = __statusConverters.toSyncStatus(_tmp_4);
            final Date _tmpLastSyncAttempt;
            final Long _tmp_5;
            if (_cursor.isNull(_cursorIndexOfLastSyncAttempt)) {
              _tmp_5 = null;
            } else {
              _tmp_5 = _cursor.getLong(_cursorIndexOfLastSyncAttempt);
            }
            _tmpLastSyncAttempt = __dateConverters.fromTimestamp(_tmp_5);
            final String _tmpSyncError;
            if (_cursor.isNull(_cursorIndexOfSyncError)) {
              _tmpSyncError = null;
            } else {
              _tmpSyncError = _cursor.getString(_cursorIndexOfSyncError);
            }
            final Date _tmpAssignedAt;
            final Long _tmp_6;
            if (_cursor.isNull(_cursorIndexOfAssignedAt)) {
              _tmp_6 = null;
            } else {
              _tmp_6 = _cursor.getLong(_cursorIndexOfAssignedAt);
            }
            _tmpAssignedAt = __dateConverters.fromTimestamp(_tmp_6);
            final Date _tmpCompletedAt;
            final Long _tmp_7;
            if (_cursor.isNull(_cursorIndexOfCompletedAt)) {
              _tmp_7 = null;
            } else {
              _tmp_7 = _cursor.getLong(_cursorIndexOfCompletedAt);
            }
            _tmpCompletedAt = __dateConverters.fromTimestamp(_tmp_7);
            final boolean _tmpNeedsSync;
            final int _tmp_8;
            _tmp_8 = _cursor.getInt(_cursorIndexOfNeedsSync);
            _tmpNeedsSync = _tmp_8 != 0;
            _item = new DropEntity(_tmpDropNumber,_tmpProjectId,_tmpLatitude,_tmpLongitude,_tmpAltitude,_tmpAccuracy,_tmpAddress,_tmpStatus,_tmpAssignedTechnicianId,_tmpCustomerName,_tmpCustomerPhone,_tmpCustomerEmail,_tmpInstallationDate,_tmpActivationStatus,_tmpActivationDate,_tmpNotes,_tmpPriority,_tmpCreatedAt,_tmpUpdatedAt,_tmpSyncStatus,_tmpLastSyncAttempt,_tmpSyncError,_tmpAssignedAt,_tmpCompletedAt,_tmpNeedsSync);
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
  public Object searchDropsByAddress(final String query,
      final Continuation<? super List<DropEntity>> $completion) {
    final String _sql = "SELECT * FROM drops WHERE address LIKE '%' || ? || '%' ORDER BY created_at DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    if (query == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, query);
    }
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<DropEntity>>() {
      @Override
      @NonNull
      public List<DropEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfDropNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "drop_number");
          final int _cursorIndexOfProjectId = CursorUtil.getColumnIndexOrThrow(_cursor, "project_id");
          final int _cursorIndexOfLatitude = CursorUtil.getColumnIndexOrThrow(_cursor, "latitude");
          final int _cursorIndexOfLongitude = CursorUtil.getColumnIndexOrThrow(_cursor, "longitude");
          final int _cursorIndexOfAltitude = CursorUtil.getColumnIndexOrThrow(_cursor, "altitude");
          final int _cursorIndexOfAccuracy = CursorUtil.getColumnIndexOrThrow(_cursor, "accuracy");
          final int _cursorIndexOfAddress = CursorUtil.getColumnIndexOrThrow(_cursor, "address");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfAssignedTechnicianId = CursorUtil.getColumnIndexOrThrow(_cursor, "assigned_technician_id");
          final int _cursorIndexOfCustomerName = CursorUtil.getColumnIndexOrThrow(_cursor, "customer_name");
          final int _cursorIndexOfCustomerPhone = CursorUtil.getColumnIndexOrThrow(_cursor, "customer_phone");
          final int _cursorIndexOfCustomerEmail = CursorUtil.getColumnIndexOrThrow(_cursor, "customer_email");
          final int _cursorIndexOfInstallationDate = CursorUtil.getColumnIndexOrThrow(_cursor, "installation_date");
          final int _cursorIndexOfActivationStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "activation_status");
          final int _cursorIndexOfActivationDate = CursorUtil.getColumnIndexOrThrow(_cursor, "activation_date");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfPriority = CursorUtil.getColumnIndexOrThrow(_cursor, "priority");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final int _cursorIndexOfSyncStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "sync_status");
          final int _cursorIndexOfLastSyncAttempt = CursorUtil.getColumnIndexOrThrow(_cursor, "last_sync_attempt");
          final int _cursorIndexOfSyncError = CursorUtil.getColumnIndexOrThrow(_cursor, "sync_error");
          final int _cursorIndexOfAssignedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "assigned_at");
          final int _cursorIndexOfCompletedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "completed_at");
          final int _cursorIndexOfNeedsSync = CursorUtil.getColumnIndexOrThrow(_cursor, "needs_sync");
          final List<DropEntity> _result = new ArrayList<DropEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final DropEntity _item;
            final String _tmpDropNumber;
            if (_cursor.isNull(_cursorIndexOfDropNumber)) {
              _tmpDropNumber = null;
            } else {
              _tmpDropNumber = _cursor.getString(_cursorIndexOfDropNumber);
            }
            final int _tmpProjectId;
            _tmpProjectId = _cursor.getInt(_cursorIndexOfProjectId);
            final double _tmpLatitude;
            _tmpLatitude = _cursor.getDouble(_cursorIndexOfLatitude);
            final double _tmpLongitude;
            _tmpLongitude = _cursor.getDouble(_cursorIndexOfLongitude);
            final Double _tmpAltitude;
            if (_cursor.isNull(_cursorIndexOfAltitude)) {
              _tmpAltitude = null;
            } else {
              _tmpAltitude = _cursor.getDouble(_cursorIndexOfAltitude);
            }
            final Float _tmpAccuracy;
            if (_cursor.isNull(_cursorIndexOfAccuracy)) {
              _tmpAccuracy = null;
            } else {
              _tmpAccuracy = _cursor.getFloat(_cursorIndexOfAccuracy);
            }
            final String _tmpAddress;
            if (_cursor.isNull(_cursorIndexOfAddress)) {
              _tmpAddress = null;
            } else {
              _tmpAddress = _cursor.getString(_cursorIndexOfAddress);
            }
            final DropStatus _tmpStatus;
            _tmpStatus = __DropStatus_stringToEnum(_cursor.getString(_cursorIndexOfStatus));
            final String _tmpAssignedTechnicianId;
            if (_cursor.isNull(_cursorIndexOfAssignedTechnicianId)) {
              _tmpAssignedTechnicianId = null;
            } else {
              _tmpAssignedTechnicianId = _cursor.getString(_cursorIndexOfAssignedTechnicianId);
            }
            final String _tmpCustomerName;
            if (_cursor.isNull(_cursorIndexOfCustomerName)) {
              _tmpCustomerName = null;
            } else {
              _tmpCustomerName = _cursor.getString(_cursorIndexOfCustomerName);
            }
            final String _tmpCustomerPhone;
            if (_cursor.isNull(_cursorIndexOfCustomerPhone)) {
              _tmpCustomerPhone = null;
            } else {
              _tmpCustomerPhone = _cursor.getString(_cursorIndexOfCustomerPhone);
            }
            final String _tmpCustomerEmail;
            if (_cursor.isNull(_cursorIndexOfCustomerEmail)) {
              _tmpCustomerEmail = null;
            } else {
              _tmpCustomerEmail = _cursor.getString(_cursorIndexOfCustomerEmail);
            }
            final Date _tmpInstallationDate;
            final Long _tmp;
            if (_cursor.isNull(_cursorIndexOfInstallationDate)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getLong(_cursorIndexOfInstallationDate);
            }
            _tmpInstallationDate = __dateConverters.fromTimestamp(_tmp);
            final ActivationStatus _tmpActivationStatus;
            _tmpActivationStatus = __ActivationStatus_stringToEnum(_cursor.getString(_cursorIndexOfActivationStatus));
            final Date _tmpActivationDate;
            final Long _tmp_1;
            if (_cursor.isNull(_cursorIndexOfActivationDate)) {
              _tmp_1 = null;
            } else {
              _tmp_1 = _cursor.getLong(_cursorIndexOfActivationDate);
            }
            _tmpActivationDate = __dateConverters.fromTimestamp(_tmp_1);
            final String _tmpNotes;
            if (_cursor.isNull(_cursorIndexOfNotes)) {
              _tmpNotes = null;
            } else {
              _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
            }
            final int _tmpPriority;
            _tmpPriority = _cursor.getInt(_cursorIndexOfPriority);
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
            final SyncStatus _tmpSyncStatus;
            final String _tmp_4;
            if (_cursor.isNull(_cursorIndexOfSyncStatus)) {
              _tmp_4 = null;
            } else {
              _tmp_4 = _cursor.getString(_cursorIndexOfSyncStatus);
            }
            _tmpSyncStatus = __statusConverters.toSyncStatus(_tmp_4);
            final Date _tmpLastSyncAttempt;
            final Long _tmp_5;
            if (_cursor.isNull(_cursorIndexOfLastSyncAttempt)) {
              _tmp_5 = null;
            } else {
              _tmp_5 = _cursor.getLong(_cursorIndexOfLastSyncAttempt);
            }
            _tmpLastSyncAttempt = __dateConverters.fromTimestamp(_tmp_5);
            final String _tmpSyncError;
            if (_cursor.isNull(_cursorIndexOfSyncError)) {
              _tmpSyncError = null;
            } else {
              _tmpSyncError = _cursor.getString(_cursorIndexOfSyncError);
            }
            final Date _tmpAssignedAt;
            final Long _tmp_6;
            if (_cursor.isNull(_cursorIndexOfAssignedAt)) {
              _tmp_6 = null;
            } else {
              _tmp_6 = _cursor.getLong(_cursorIndexOfAssignedAt);
            }
            _tmpAssignedAt = __dateConverters.fromTimestamp(_tmp_6);
            final Date _tmpCompletedAt;
            final Long _tmp_7;
            if (_cursor.isNull(_cursorIndexOfCompletedAt)) {
              _tmp_7 = null;
            } else {
              _tmp_7 = _cursor.getLong(_cursorIndexOfCompletedAt);
            }
            _tmpCompletedAt = __dateConverters.fromTimestamp(_tmp_7);
            final boolean _tmpNeedsSync;
            final int _tmp_8;
            _tmp_8 = _cursor.getInt(_cursorIndexOfNeedsSync);
            _tmpNeedsSync = _tmp_8 != 0;
            _item = new DropEntity(_tmpDropNumber,_tmpProjectId,_tmpLatitude,_tmpLongitude,_tmpAltitude,_tmpAccuracy,_tmpAddress,_tmpStatus,_tmpAssignedTechnicianId,_tmpCustomerName,_tmpCustomerPhone,_tmpCustomerEmail,_tmpInstallationDate,_tmpActivationStatus,_tmpActivationDate,_tmpNotes,_tmpPriority,_tmpCreatedAt,_tmpUpdatedAt,_tmpSyncStatus,_tmpLastSyncAttempt,_tmpSyncError,_tmpAssignedAt,_tmpCompletedAt,_tmpNeedsSync);
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
  public Object getDropsByMinPriority(final int minPriority,
      final Continuation<? super List<DropEntity>> $completion) {
    final String _sql = "SELECT * FROM drops WHERE priority >= ? ORDER BY priority DESC, created_at ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, minPriority);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<DropEntity>>() {
      @Override
      @NonNull
      public List<DropEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfDropNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "drop_number");
          final int _cursorIndexOfProjectId = CursorUtil.getColumnIndexOrThrow(_cursor, "project_id");
          final int _cursorIndexOfLatitude = CursorUtil.getColumnIndexOrThrow(_cursor, "latitude");
          final int _cursorIndexOfLongitude = CursorUtil.getColumnIndexOrThrow(_cursor, "longitude");
          final int _cursorIndexOfAltitude = CursorUtil.getColumnIndexOrThrow(_cursor, "altitude");
          final int _cursorIndexOfAccuracy = CursorUtil.getColumnIndexOrThrow(_cursor, "accuracy");
          final int _cursorIndexOfAddress = CursorUtil.getColumnIndexOrThrow(_cursor, "address");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfAssignedTechnicianId = CursorUtil.getColumnIndexOrThrow(_cursor, "assigned_technician_id");
          final int _cursorIndexOfCustomerName = CursorUtil.getColumnIndexOrThrow(_cursor, "customer_name");
          final int _cursorIndexOfCustomerPhone = CursorUtil.getColumnIndexOrThrow(_cursor, "customer_phone");
          final int _cursorIndexOfCustomerEmail = CursorUtil.getColumnIndexOrThrow(_cursor, "customer_email");
          final int _cursorIndexOfInstallationDate = CursorUtil.getColumnIndexOrThrow(_cursor, "installation_date");
          final int _cursorIndexOfActivationStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "activation_status");
          final int _cursorIndexOfActivationDate = CursorUtil.getColumnIndexOrThrow(_cursor, "activation_date");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfPriority = CursorUtil.getColumnIndexOrThrow(_cursor, "priority");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final int _cursorIndexOfSyncStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "sync_status");
          final int _cursorIndexOfLastSyncAttempt = CursorUtil.getColumnIndexOrThrow(_cursor, "last_sync_attempt");
          final int _cursorIndexOfSyncError = CursorUtil.getColumnIndexOrThrow(_cursor, "sync_error");
          final int _cursorIndexOfAssignedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "assigned_at");
          final int _cursorIndexOfCompletedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "completed_at");
          final int _cursorIndexOfNeedsSync = CursorUtil.getColumnIndexOrThrow(_cursor, "needs_sync");
          final List<DropEntity> _result = new ArrayList<DropEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final DropEntity _item;
            final String _tmpDropNumber;
            if (_cursor.isNull(_cursorIndexOfDropNumber)) {
              _tmpDropNumber = null;
            } else {
              _tmpDropNumber = _cursor.getString(_cursorIndexOfDropNumber);
            }
            final int _tmpProjectId;
            _tmpProjectId = _cursor.getInt(_cursorIndexOfProjectId);
            final double _tmpLatitude;
            _tmpLatitude = _cursor.getDouble(_cursorIndexOfLatitude);
            final double _tmpLongitude;
            _tmpLongitude = _cursor.getDouble(_cursorIndexOfLongitude);
            final Double _tmpAltitude;
            if (_cursor.isNull(_cursorIndexOfAltitude)) {
              _tmpAltitude = null;
            } else {
              _tmpAltitude = _cursor.getDouble(_cursorIndexOfAltitude);
            }
            final Float _tmpAccuracy;
            if (_cursor.isNull(_cursorIndexOfAccuracy)) {
              _tmpAccuracy = null;
            } else {
              _tmpAccuracy = _cursor.getFloat(_cursorIndexOfAccuracy);
            }
            final String _tmpAddress;
            if (_cursor.isNull(_cursorIndexOfAddress)) {
              _tmpAddress = null;
            } else {
              _tmpAddress = _cursor.getString(_cursorIndexOfAddress);
            }
            final DropStatus _tmpStatus;
            _tmpStatus = __DropStatus_stringToEnum(_cursor.getString(_cursorIndexOfStatus));
            final String _tmpAssignedTechnicianId;
            if (_cursor.isNull(_cursorIndexOfAssignedTechnicianId)) {
              _tmpAssignedTechnicianId = null;
            } else {
              _tmpAssignedTechnicianId = _cursor.getString(_cursorIndexOfAssignedTechnicianId);
            }
            final String _tmpCustomerName;
            if (_cursor.isNull(_cursorIndexOfCustomerName)) {
              _tmpCustomerName = null;
            } else {
              _tmpCustomerName = _cursor.getString(_cursorIndexOfCustomerName);
            }
            final String _tmpCustomerPhone;
            if (_cursor.isNull(_cursorIndexOfCustomerPhone)) {
              _tmpCustomerPhone = null;
            } else {
              _tmpCustomerPhone = _cursor.getString(_cursorIndexOfCustomerPhone);
            }
            final String _tmpCustomerEmail;
            if (_cursor.isNull(_cursorIndexOfCustomerEmail)) {
              _tmpCustomerEmail = null;
            } else {
              _tmpCustomerEmail = _cursor.getString(_cursorIndexOfCustomerEmail);
            }
            final Date _tmpInstallationDate;
            final Long _tmp;
            if (_cursor.isNull(_cursorIndexOfInstallationDate)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getLong(_cursorIndexOfInstallationDate);
            }
            _tmpInstallationDate = __dateConverters.fromTimestamp(_tmp);
            final ActivationStatus _tmpActivationStatus;
            _tmpActivationStatus = __ActivationStatus_stringToEnum(_cursor.getString(_cursorIndexOfActivationStatus));
            final Date _tmpActivationDate;
            final Long _tmp_1;
            if (_cursor.isNull(_cursorIndexOfActivationDate)) {
              _tmp_1 = null;
            } else {
              _tmp_1 = _cursor.getLong(_cursorIndexOfActivationDate);
            }
            _tmpActivationDate = __dateConverters.fromTimestamp(_tmp_1);
            final String _tmpNotes;
            if (_cursor.isNull(_cursorIndexOfNotes)) {
              _tmpNotes = null;
            } else {
              _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
            }
            final int _tmpPriority;
            _tmpPriority = _cursor.getInt(_cursorIndexOfPriority);
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
            final SyncStatus _tmpSyncStatus;
            final String _tmp_4;
            if (_cursor.isNull(_cursorIndexOfSyncStatus)) {
              _tmp_4 = null;
            } else {
              _tmp_4 = _cursor.getString(_cursorIndexOfSyncStatus);
            }
            _tmpSyncStatus = __statusConverters.toSyncStatus(_tmp_4);
            final Date _tmpLastSyncAttempt;
            final Long _tmp_5;
            if (_cursor.isNull(_cursorIndexOfLastSyncAttempt)) {
              _tmp_5 = null;
            } else {
              _tmp_5 = _cursor.getLong(_cursorIndexOfLastSyncAttempt);
            }
            _tmpLastSyncAttempt = __dateConverters.fromTimestamp(_tmp_5);
            final String _tmpSyncError;
            if (_cursor.isNull(_cursorIndexOfSyncError)) {
              _tmpSyncError = null;
            } else {
              _tmpSyncError = _cursor.getString(_cursorIndexOfSyncError);
            }
            final Date _tmpAssignedAt;
            final Long _tmp_6;
            if (_cursor.isNull(_cursorIndexOfAssignedAt)) {
              _tmp_6 = null;
            } else {
              _tmp_6 = _cursor.getLong(_cursorIndexOfAssignedAt);
            }
            _tmpAssignedAt = __dateConverters.fromTimestamp(_tmp_6);
            final Date _tmpCompletedAt;
            final Long _tmp_7;
            if (_cursor.isNull(_cursorIndexOfCompletedAt)) {
              _tmp_7 = null;
            } else {
              _tmp_7 = _cursor.getLong(_cursorIndexOfCompletedAt);
            }
            _tmpCompletedAt = __dateConverters.fromTimestamp(_tmp_7);
            final boolean _tmpNeedsSync;
            final int _tmp_8;
            _tmp_8 = _cursor.getInt(_cursorIndexOfNeedsSync);
            _tmpNeedsSync = _tmp_8 != 0;
            _item = new DropEntity(_tmpDropNumber,_tmpProjectId,_tmpLatitude,_tmpLongitude,_tmpAltitude,_tmpAccuracy,_tmpAddress,_tmpStatus,_tmpAssignedTechnicianId,_tmpCustomerName,_tmpCustomerPhone,_tmpCustomerEmail,_tmpInstallationDate,_tmpActivationStatus,_tmpActivationDate,_tmpNotes,_tmpPriority,_tmpCreatedAt,_tmpUpdatedAt,_tmpSyncStatus,_tmpLastSyncAttempt,_tmpSyncError,_tmpAssignedAt,_tmpCompletedAt,_tmpNeedsSync);
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
  public Object getOverdueDrops(final long cutoffTime,
      final Continuation<? super List<DropEntity>> $completion) {
    final String _sql = "SELECT * FROM drops WHERE status = 'IN_PROGRESS' AND assigned_at < ? ORDER BY assigned_at ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, cutoffTime);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<DropEntity>>() {
      @Override
      @NonNull
      public List<DropEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfDropNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "drop_number");
          final int _cursorIndexOfProjectId = CursorUtil.getColumnIndexOrThrow(_cursor, "project_id");
          final int _cursorIndexOfLatitude = CursorUtil.getColumnIndexOrThrow(_cursor, "latitude");
          final int _cursorIndexOfLongitude = CursorUtil.getColumnIndexOrThrow(_cursor, "longitude");
          final int _cursorIndexOfAltitude = CursorUtil.getColumnIndexOrThrow(_cursor, "altitude");
          final int _cursorIndexOfAccuracy = CursorUtil.getColumnIndexOrThrow(_cursor, "accuracy");
          final int _cursorIndexOfAddress = CursorUtil.getColumnIndexOrThrow(_cursor, "address");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfAssignedTechnicianId = CursorUtil.getColumnIndexOrThrow(_cursor, "assigned_technician_id");
          final int _cursorIndexOfCustomerName = CursorUtil.getColumnIndexOrThrow(_cursor, "customer_name");
          final int _cursorIndexOfCustomerPhone = CursorUtil.getColumnIndexOrThrow(_cursor, "customer_phone");
          final int _cursorIndexOfCustomerEmail = CursorUtil.getColumnIndexOrThrow(_cursor, "customer_email");
          final int _cursorIndexOfInstallationDate = CursorUtil.getColumnIndexOrThrow(_cursor, "installation_date");
          final int _cursorIndexOfActivationStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "activation_status");
          final int _cursorIndexOfActivationDate = CursorUtil.getColumnIndexOrThrow(_cursor, "activation_date");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfPriority = CursorUtil.getColumnIndexOrThrow(_cursor, "priority");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final int _cursorIndexOfSyncStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "sync_status");
          final int _cursorIndexOfLastSyncAttempt = CursorUtil.getColumnIndexOrThrow(_cursor, "last_sync_attempt");
          final int _cursorIndexOfSyncError = CursorUtil.getColumnIndexOrThrow(_cursor, "sync_error");
          final int _cursorIndexOfAssignedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "assigned_at");
          final int _cursorIndexOfCompletedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "completed_at");
          final int _cursorIndexOfNeedsSync = CursorUtil.getColumnIndexOrThrow(_cursor, "needs_sync");
          final List<DropEntity> _result = new ArrayList<DropEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final DropEntity _item;
            final String _tmpDropNumber;
            if (_cursor.isNull(_cursorIndexOfDropNumber)) {
              _tmpDropNumber = null;
            } else {
              _tmpDropNumber = _cursor.getString(_cursorIndexOfDropNumber);
            }
            final int _tmpProjectId;
            _tmpProjectId = _cursor.getInt(_cursorIndexOfProjectId);
            final double _tmpLatitude;
            _tmpLatitude = _cursor.getDouble(_cursorIndexOfLatitude);
            final double _tmpLongitude;
            _tmpLongitude = _cursor.getDouble(_cursorIndexOfLongitude);
            final Double _tmpAltitude;
            if (_cursor.isNull(_cursorIndexOfAltitude)) {
              _tmpAltitude = null;
            } else {
              _tmpAltitude = _cursor.getDouble(_cursorIndexOfAltitude);
            }
            final Float _tmpAccuracy;
            if (_cursor.isNull(_cursorIndexOfAccuracy)) {
              _tmpAccuracy = null;
            } else {
              _tmpAccuracy = _cursor.getFloat(_cursorIndexOfAccuracy);
            }
            final String _tmpAddress;
            if (_cursor.isNull(_cursorIndexOfAddress)) {
              _tmpAddress = null;
            } else {
              _tmpAddress = _cursor.getString(_cursorIndexOfAddress);
            }
            final DropStatus _tmpStatus;
            _tmpStatus = __DropStatus_stringToEnum(_cursor.getString(_cursorIndexOfStatus));
            final String _tmpAssignedTechnicianId;
            if (_cursor.isNull(_cursorIndexOfAssignedTechnicianId)) {
              _tmpAssignedTechnicianId = null;
            } else {
              _tmpAssignedTechnicianId = _cursor.getString(_cursorIndexOfAssignedTechnicianId);
            }
            final String _tmpCustomerName;
            if (_cursor.isNull(_cursorIndexOfCustomerName)) {
              _tmpCustomerName = null;
            } else {
              _tmpCustomerName = _cursor.getString(_cursorIndexOfCustomerName);
            }
            final String _tmpCustomerPhone;
            if (_cursor.isNull(_cursorIndexOfCustomerPhone)) {
              _tmpCustomerPhone = null;
            } else {
              _tmpCustomerPhone = _cursor.getString(_cursorIndexOfCustomerPhone);
            }
            final String _tmpCustomerEmail;
            if (_cursor.isNull(_cursorIndexOfCustomerEmail)) {
              _tmpCustomerEmail = null;
            } else {
              _tmpCustomerEmail = _cursor.getString(_cursorIndexOfCustomerEmail);
            }
            final Date _tmpInstallationDate;
            final Long _tmp;
            if (_cursor.isNull(_cursorIndexOfInstallationDate)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getLong(_cursorIndexOfInstallationDate);
            }
            _tmpInstallationDate = __dateConverters.fromTimestamp(_tmp);
            final ActivationStatus _tmpActivationStatus;
            _tmpActivationStatus = __ActivationStatus_stringToEnum(_cursor.getString(_cursorIndexOfActivationStatus));
            final Date _tmpActivationDate;
            final Long _tmp_1;
            if (_cursor.isNull(_cursorIndexOfActivationDate)) {
              _tmp_1 = null;
            } else {
              _tmp_1 = _cursor.getLong(_cursorIndexOfActivationDate);
            }
            _tmpActivationDate = __dateConverters.fromTimestamp(_tmp_1);
            final String _tmpNotes;
            if (_cursor.isNull(_cursorIndexOfNotes)) {
              _tmpNotes = null;
            } else {
              _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
            }
            final int _tmpPriority;
            _tmpPriority = _cursor.getInt(_cursorIndexOfPriority);
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
            final SyncStatus _tmpSyncStatus;
            final String _tmp_4;
            if (_cursor.isNull(_cursorIndexOfSyncStatus)) {
              _tmp_4 = null;
            } else {
              _tmp_4 = _cursor.getString(_cursorIndexOfSyncStatus);
            }
            _tmpSyncStatus = __statusConverters.toSyncStatus(_tmp_4);
            final Date _tmpLastSyncAttempt;
            final Long _tmp_5;
            if (_cursor.isNull(_cursorIndexOfLastSyncAttempt)) {
              _tmp_5 = null;
            } else {
              _tmp_5 = _cursor.getLong(_cursorIndexOfLastSyncAttempt);
            }
            _tmpLastSyncAttempt = __dateConverters.fromTimestamp(_tmp_5);
            final String _tmpSyncError;
            if (_cursor.isNull(_cursorIndexOfSyncError)) {
              _tmpSyncError = null;
            } else {
              _tmpSyncError = _cursor.getString(_cursorIndexOfSyncError);
            }
            final Date _tmpAssignedAt;
            final Long _tmp_6;
            if (_cursor.isNull(_cursorIndexOfAssignedAt)) {
              _tmp_6 = null;
            } else {
              _tmp_6 = _cursor.getLong(_cursorIndexOfAssignedAt);
            }
            _tmpAssignedAt = __dateConverters.fromTimestamp(_tmp_6);
            final Date _tmpCompletedAt;
            final Long _tmp_7;
            if (_cursor.isNull(_cursorIndexOfCompletedAt)) {
              _tmp_7 = null;
            } else {
              _tmp_7 = _cursor.getLong(_cursorIndexOfCompletedAt);
            }
            _tmpCompletedAt = __dateConverters.fromTimestamp(_tmp_7);
            final boolean _tmpNeedsSync;
            final int _tmp_8;
            _tmp_8 = _cursor.getInt(_cursorIndexOfNeedsSync);
            _tmpNeedsSync = _tmp_8 != 0;
            _item = new DropEntity(_tmpDropNumber,_tmpProjectId,_tmpLatitude,_tmpLongitude,_tmpAltitude,_tmpAccuracy,_tmpAddress,_tmpStatus,_tmpAssignedTechnicianId,_tmpCustomerName,_tmpCustomerPhone,_tmpCustomerEmail,_tmpInstallationDate,_tmpActivationStatus,_tmpActivationDate,_tmpNotes,_tmpPriority,_tmpCreatedAt,_tmpUpdatedAt,_tmpSyncStatus,_tmpLastSyncAttempt,_tmpSyncError,_tmpAssignedAt,_tmpCompletedAt,_tmpNeedsSync);
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
  public Object getDropsInDateRange(final long startDate, final long endDate,
      final Continuation<? super List<DropEntity>> $completion) {
    final String _sql = "SELECT * FROM drops WHERE created_at BETWEEN ? AND ? ORDER BY created_at DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, startDate);
    _argIndex = 2;
    _statement.bindLong(_argIndex, endDate);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<DropEntity>>() {
      @Override
      @NonNull
      public List<DropEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfDropNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "drop_number");
          final int _cursorIndexOfProjectId = CursorUtil.getColumnIndexOrThrow(_cursor, "project_id");
          final int _cursorIndexOfLatitude = CursorUtil.getColumnIndexOrThrow(_cursor, "latitude");
          final int _cursorIndexOfLongitude = CursorUtil.getColumnIndexOrThrow(_cursor, "longitude");
          final int _cursorIndexOfAltitude = CursorUtil.getColumnIndexOrThrow(_cursor, "altitude");
          final int _cursorIndexOfAccuracy = CursorUtil.getColumnIndexOrThrow(_cursor, "accuracy");
          final int _cursorIndexOfAddress = CursorUtil.getColumnIndexOrThrow(_cursor, "address");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfAssignedTechnicianId = CursorUtil.getColumnIndexOrThrow(_cursor, "assigned_technician_id");
          final int _cursorIndexOfCustomerName = CursorUtil.getColumnIndexOrThrow(_cursor, "customer_name");
          final int _cursorIndexOfCustomerPhone = CursorUtil.getColumnIndexOrThrow(_cursor, "customer_phone");
          final int _cursorIndexOfCustomerEmail = CursorUtil.getColumnIndexOrThrow(_cursor, "customer_email");
          final int _cursorIndexOfInstallationDate = CursorUtil.getColumnIndexOrThrow(_cursor, "installation_date");
          final int _cursorIndexOfActivationStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "activation_status");
          final int _cursorIndexOfActivationDate = CursorUtil.getColumnIndexOrThrow(_cursor, "activation_date");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfPriority = CursorUtil.getColumnIndexOrThrow(_cursor, "priority");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final int _cursorIndexOfSyncStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "sync_status");
          final int _cursorIndexOfLastSyncAttempt = CursorUtil.getColumnIndexOrThrow(_cursor, "last_sync_attempt");
          final int _cursorIndexOfSyncError = CursorUtil.getColumnIndexOrThrow(_cursor, "sync_error");
          final int _cursorIndexOfAssignedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "assigned_at");
          final int _cursorIndexOfCompletedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "completed_at");
          final int _cursorIndexOfNeedsSync = CursorUtil.getColumnIndexOrThrow(_cursor, "needs_sync");
          final List<DropEntity> _result = new ArrayList<DropEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final DropEntity _item;
            final String _tmpDropNumber;
            if (_cursor.isNull(_cursorIndexOfDropNumber)) {
              _tmpDropNumber = null;
            } else {
              _tmpDropNumber = _cursor.getString(_cursorIndexOfDropNumber);
            }
            final int _tmpProjectId;
            _tmpProjectId = _cursor.getInt(_cursorIndexOfProjectId);
            final double _tmpLatitude;
            _tmpLatitude = _cursor.getDouble(_cursorIndexOfLatitude);
            final double _tmpLongitude;
            _tmpLongitude = _cursor.getDouble(_cursorIndexOfLongitude);
            final Double _tmpAltitude;
            if (_cursor.isNull(_cursorIndexOfAltitude)) {
              _tmpAltitude = null;
            } else {
              _tmpAltitude = _cursor.getDouble(_cursorIndexOfAltitude);
            }
            final Float _tmpAccuracy;
            if (_cursor.isNull(_cursorIndexOfAccuracy)) {
              _tmpAccuracy = null;
            } else {
              _tmpAccuracy = _cursor.getFloat(_cursorIndexOfAccuracy);
            }
            final String _tmpAddress;
            if (_cursor.isNull(_cursorIndexOfAddress)) {
              _tmpAddress = null;
            } else {
              _tmpAddress = _cursor.getString(_cursorIndexOfAddress);
            }
            final DropStatus _tmpStatus;
            _tmpStatus = __DropStatus_stringToEnum(_cursor.getString(_cursorIndexOfStatus));
            final String _tmpAssignedTechnicianId;
            if (_cursor.isNull(_cursorIndexOfAssignedTechnicianId)) {
              _tmpAssignedTechnicianId = null;
            } else {
              _tmpAssignedTechnicianId = _cursor.getString(_cursorIndexOfAssignedTechnicianId);
            }
            final String _tmpCustomerName;
            if (_cursor.isNull(_cursorIndexOfCustomerName)) {
              _tmpCustomerName = null;
            } else {
              _tmpCustomerName = _cursor.getString(_cursorIndexOfCustomerName);
            }
            final String _tmpCustomerPhone;
            if (_cursor.isNull(_cursorIndexOfCustomerPhone)) {
              _tmpCustomerPhone = null;
            } else {
              _tmpCustomerPhone = _cursor.getString(_cursorIndexOfCustomerPhone);
            }
            final String _tmpCustomerEmail;
            if (_cursor.isNull(_cursorIndexOfCustomerEmail)) {
              _tmpCustomerEmail = null;
            } else {
              _tmpCustomerEmail = _cursor.getString(_cursorIndexOfCustomerEmail);
            }
            final Date _tmpInstallationDate;
            final Long _tmp;
            if (_cursor.isNull(_cursorIndexOfInstallationDate)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getLong(_cursorIndexOfInstallationDate);
            }
            _tmpInstallationDate = __dateConverters.fromTimestamp(_tmp);
            final ActivationStatus _tmpActivationStatus;
            _tmpActivationStatus = __ActivationStatus_stringToEnum(_cursor.getString(_cursorIndexOfActivationStatus));
            final Date _tmpActivationDate;
            final Long _tmp_1;
            if (_cursor.isNull(_cursorIndexOfActivationDate)) {
              _tmp_1 = null;
            } else {
              _tmp_1 = _cursor.getLong(_cursorIndexOfActivationDate);
            }
            _tmpActivationDate = __dateConverters.fromTimestamp(_tmp_1);
            final String _tmpNotes;
            if (_cursor.isNull(_cursorIndexOfNotes)) {
              _tmpNotes = null;
            } else {
              _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
            }
            final int _tmpPriority;
            _tmpPriority = _cursor.getInt(_cursorIndexOfPriority);
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
            final SyncStatus _tmpSyncStatus;
            final String _tmp_4;
            if (_cursor.isNull(_cursorIndexOfSyncStatus)) {
              _tmp_4 = null;
            } else {
              _tmp_4 = _cursor.getString(_cursorIndexOfSyncStatus);
            }
            _tmpSyncStatus = __statusConverters.toSyncStatus(_tmp_4);
            final Date _tmpLastSyncAttempt;
            final Long _tmp_5;
            if (_cursor.isNull(_cursorIndexOfLastSyncAttempt)) {
              _tmp_5 = null;
            } else {
              _tmp_5 = _cursor.getLong(_cursorIndexOfLastSyncAttempt);
            }
            _tmpLastSyncAttempt = __dateConverters.fromTimestamp(_tmp_5);
            final String _tmpSyncError;
            if (_cursor.isNull(_cursorIndexOfSyncError)) {
              _tmpSyncError = null;
            } else {
              _tmpSyncError = _cursor.getString(_cursorIndexOfSyncError);
            }
            final Date _tmpAssignedAt;
            final Long _tmp_6;
            if (_cursor.isNull(_cursorIndexOfAssignedAt)) {
              _tmp_6 = null;
            } else {
              _tmp_6 = _cursor.getLong(_cursorIndexOfAssignedAt);
            }
            _tmpAssignedAt = __dateConverters.fromTimestamp(_tmp_6);
            final Date _tmpCompletedAt;
            final Long _tmp_7;
            if (_cursor.isNull(_cursorIndexOfCompletedAt)) {
              _tmp_7 = null;
            } else {
              _tmp_7 = _cursor.getLong(_cursorIndexOfCompletedAt);
            }
            _tmpCompletedAt = __dateConverters.fromTimestamp(_tmp_7);
            final boolean _tmpNeedsSync;
            final int _tmp_8;
            _tmp_8 = _cursor.getInt(_cursorIndexOfNeedsSync);
            _tmpNeedsSync = _tmp_8 != 0;
            _item = new DropEntity(_tmpDropNumber,_tmpProjectId,_tmpLatitude,_tmpLongitude,_tmpAltitude,_tmpAccuracy,_tmpAddress,_tmpStatus,_tmpAssignedTechnicianId,_tmpCustomerName,_tmpCustomerPhone,_tmpCustomerEmail,_tmpInstallationDate,_tmpActivationStatus,_tmpActivationDate,_tmpNotes,_tmpPriority,_tmpCreatedAt,_tmpUpdatedAt,_tmpSyncStatus,_tmpLastSyncAttempt,_tmpSyncError,_tmpAssignedAt,_tmpCompletedAt,_tmpNeedsSync);
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
  public Object getDropStatusStatistics(
      final Continuation<? super Map<String, Integer>> $completion) {
    final String _sql = "\n"
            + "        SELECT status, COUNT(*) as count\n"
            + "        FROM drops\n"
            + "        GROUP BY status\n"
            + "        ORDER BY count DESC\n"
            + "    ";
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
  public Object getAverageCompletionTimeByPriority(
      final Continuation<? super Map<Integer, Long>> $completion) {
    final String _sql = "\n"
            + "        SELECT priority, AVG(completed_at - assigned_at) as avgCompletionTime\n"
            + "        FROM drops\n"
            + "        WHERE status = 'COMPLETED' AND assigned_at IS NOT NULL AND completed_at IS NOT NULL\n"
            + "        GROUP BY priority\n"
            + "        ORDER BY priority DESC\n"
            + "    ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Map<Integer, Long>>() {
      @Override
      @NonNull
      public Map<Integer, Long> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Map<Integer, Long> _result = new LinkedHashMap<Integer, Long>();
          while (_cursor.moveToNext()) {
            final Integer _key;
            _key = new Integer();
            if () {
              _result.put(_key, null);
              continue;
            }
            final Long _value;
            _value = new Long();
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
  public Object markDropsSynced(final List<String> dropIds, final long timestamp,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final StringBuilder _stringBuilder = StringUtil.newStringBuilder();
        _stringBuilder.append("UPDATE drops SET needs_sync = 0, last_sync_attempt = ");
        _stringBuilder.append("?");
        _stringBuilder.append(" WHERE drop_number IN (");
        final int _inputSize = dropIds.size();
        StringUtil.appendPlaceholders(_stringBuilder, _inputSize);
        _stringBuilder.append(")");
        final String _sql = _stringBuilder.toString();
        final SupportSQLiteStatement _stmt = __db.compileStatement(_sql);
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, timestamp);
        _argIndex = 2;
        for (String _item : dropIds) {
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

  private String __DropStatus_enumToString(@NonNull final DropStatus _value) {
    switch (_value) {
      case AVAILABLE: return "AVAILABLE";
      case ASSIGNED: return "ASSIGNED";
      case IN_PROGRESS: return "IN_PROGRESS";
      case PENDING_ACTIVATION: return "PENDING_ACTIVATION";
      case COMPLETED: return "COMPLETED";
      case FAILED: return "FAILED";
      case REMEDIATION_REQUIRED: return "REMEDIATION_REQUIRED";
      default: throw new IllegalArgumentException("Can't convert enum to string, unknown enum value: " + _value);
    }
  }

  private String __ActivationStatus_enumToString(@NonNull final ActivationStatus _value) {
    switch (_value) {
      case PENDING: return "PENDING";
      case SCHEDULED: return "SCHEDULED";
      case ACTIVE: return "ACTIVE";
      case FAILED: return "FAILED";
      default: throw new IllegalArgumentException("Can't convert enum to string, unknown enum value: " + _value);
    }
  }

  private DropStatus __DropStatus_stringToEnum(@NonNull final String _value) {
    switch (_value) {
      case "AVAILABLE": return DropStatus.AVAILABLE;
      case "ASSIGNED": return DropStatus.ASSIGNED;
      case "IN_PROGRESS": return DropStatus.IN_PROGRESS;
      case "PENDING_ACTIVATION": return DropStatus.PENDING_ACTIVATION;
      case "COMPLETED": return DropStatus.COMPLETED;
      case "FAILED": return DropStatus.FAILED;
      case "REMEDIATION_REQUIRED": return DropStatus.REMEDIATION_REQUIRED;
      default: throw new IllegalArgumentException("Can't convert value to enum, unknown value: " + _value);
    }
  }

  private ActivationStatus __ActivationStatus_stringToEnum(@NonNull final String _value) {
    switch (_value) {
      case "PENDING": return ActivationStatus.PENDING;
      case "SCHEDULED": return ActivationStatus.SCHEDULED;
      case "ACTIVE": return ActivationStatus.ACTIVE;
      case "FAILED": return ActivationStatus.FAILED;
      default: throw new IllegalArgumentException("Can't convert value to enum, unknown value: " + _value);
    }
  }
}
