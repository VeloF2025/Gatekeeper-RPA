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
import com.fibreflow.core.database.entities.AIConversationEntity;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Float;
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
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class AIConversationDao_Impl implements AIConversationDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<AIConversationEntity> __insertionAdapterOfAIConversationEntity;

  private final DateConverters __dateConverters = new DateConverters();

  private final EntityDeletionOrUpdateAdapter<AIConversationEntity> __deletionAdapterOfAIConversationEntity;

  private final EntityDeletionOrUpdateAdapter<AIConversationEntity> __updateAdapterOfAIConversationEntity;

  private final SharedSQLiteStatement __preparedStmtOfDeleteOldConversations;

  public AIConversationDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfAIConversationEntity = new EntityInsertionAdapter<AIConversationEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `ai_conversations` (`conversation_id`,`installation_id`,`user_message`,`ai_response`,`confidence_score`,`created_at`) VALUES (?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final AIConversationEntity entity) {
        statement.bindLong(1, entity.getConversationId());
        if (entity.getInstallationId() == null) {
          statement.bindNull(2);
        } else {
          statement.bindLong(2, entity.getInstallationId());
        }
        if (entity.getUserMessage() == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, entity.getUserMessage());
        }
        if (entity.getAiResponse() == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.getAiResponse());
        }
        if (entity.getConfidenceScore() == null) {
          statement.bindNull(5);
        } else {
          statement.bindDouble(5, entity.getConfidenceScore());
        }
        final Long _tmp = __dateConverters.dateToTimestamp(entity.getCreatedAt());
        if (_tmp == null) {
          statement.bindNull(6);
        } else {
          statement.bindLong(6, _tmp);
        }
      }
    };
    this.__deletionAdapterOfAIConversationEntity = new EntityDeletionOrUpdateAdapter<AIConversationEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `ai_conversations` WHERE `conversation_id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final AIConversationEntity entity) {
        statement.bindLong(1, entity.getConversationId());
      }
    };
    this.__updateAdapterOfAIConversationEntity = new EntityDeletionOrUpdateAdapter<AIConversationEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `ai_conversations` SET `conversation_id` = ?,`installation_id` = ?,`user_message` = ?,`ai_response` = ?,`confidence_score` = ?,`created_at` = ? WHERE `conversation_id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final AIConversationEntity entity) {
        statement.bindLong(1, entity.getConversationId());
        if (entity.getInstallationId() == null) {
          statement.bindNull(2);
        } else {
          statement.bindLong(2, entity.getInstallationId());
        }
        if (entity.getUserMessage() == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, entity.getUserMessage());
        }
        if (entity.getAiResponse() == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.getAiResponse());
        }
        if (entity.getConfidenceScore() == null) {
          statement.bindNull(5);
        } else {
          statement.bindDouble(5, entity.getConfidenceScore());
        }
        final Long _tmp = __dateConverters.dateToTimestamp(entity.getCreatedAt());
        if (_tmp == null) {
          statement.bindNull(6);
        } else {
          statement.bindLong(6, _tmp);
        }
        statement.bindLong(7, entity.getConversationId());
      }
    };
    this.__preparedStmtOfDeleteOldConversations = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM ai_conversations WHERE created_at < ?";
        return _query;
      }
    };
  }

  @Override
  public Object insertConversation(final AIConversationEntity conversation,
      final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfAIConversationEntity.insertAndReturnId(conversation);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertConversations(final List<AIConversationEntity> conversations,
      final Continuation<? super List<Long>> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<List<Long>>() {
      @Override
      @NonNull
      public List<Long> call() throws Exception {
        __db.beginTransaction();
        try {
          final List<Long> _result = __insertionAdapterOfAIConversationEntity.insertAndReturnIdsList(conversations);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteConversation(final AIConversationEntity conversation,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfAIConversationEntity.handle(conversation);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateConversation(final AIConversationEntity conversation,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfAIConversationEntity.handle(conversation);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteOldConversations(final long cutoffDate,
      final Continuation<? super Integer> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Integer>() {
      @Override
      @NonNull
      public Integer call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteOldConversations.acquire();
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
          __preparedStmtOfDeleteOldConversations.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object getConversationById(final long conversationId,
      final Continuation<? super AIConversationEntity> $completion) {
    final String _sql = "SELECT * FROM ai_conversations WHERE conversation_id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, conversationId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<AIConversationEntity>() {
      @Override
      @Nullable
      public AIConversationEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfConversationId = CursorUtil.getColumnIndexOrThrow(_cursor, "conversation_id");
          final int _cursorIndexOfInstallationId = CursorUtil.getColumnIndexOrThrow(_cursor, "installation_id");
          final int _cursorIndexOfUserMessage = CursorUtil.getColumnIndexOrThrow(_cursor, "user_message");
          final int _cursorIndexOfAiResponse = CursorUtil.getColumnIndexOrThrow(_cursor, "ai_response");
          final int _cursorIndexOfConfidenceScore = CursorUtil.getColumnIndexOrThrow(_cursor, "confidence_score");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final AIConversationEntity _result;
          if (_cursor.moveToFirst()) {
            final long _tmpConversationId;
            _tmpConversationId = _cursor.getLong(_cursorIndexOfConversationId);
            final Long _tmpInstallationId;
            if (_cursor.isNull(_cursorIndexOfInstallationId)) {
              _tmpInstallationId = null;
            } else {
              _tmpInstallationId = _cursor.getLong(_cursorIndexOfInstallationId);
            }
            final String _tmpUserMessage;
            if (_cursor.isNull(_cursorIndexOfUserMessage)) {
              _tmpUserMessage = null;
            } else {
              _tmpUserMessage = _cursor.getString(_cursorIndexOfUserMessage);
            }
            final String _tmpAiResponse;
            if (_cursor.isNull(_cursorIndexOfAiResponse)) {
              _tmpAiResponse = null;
            } else {
              _tmpAiResponse = _cursor.getString(_cursorIndexOfAiResponse);
            }
            final Float _tmpConfidenceScore;
            if (_cursor.isNull(_cursorIndexOfConfidenceScore)) {
              _tmpConfidenceScore = null;
            } else {
              _tmpConfidenceScore = _cursor.getFloat(_cursorIndexOfConfidenceScore);
            }
            final Date _tmpCreatedAt;
            final Long _tmp;
            if (_cursor.isNull(_cursorIndexOfCreatedAt)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getLong(_cursorIndexOfCreatedAt);
            }
            _tmpCreatedAt = __dateConverters.fromTimestamp(_tmp);
            _result = new AIConversationEntity(_tmpConversationId,_tmpInstallationId,_tmpUserMessage,_tmpAiResponse,_tmpConfidenceScore,_tmpCreatedAt);
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
  public Object getConversationsByInstallation(final long installationId,
      final Continuation<? super List<AIConversationEntity>> $completion) {
    final String _sql = "SELECT * FROM ai_conversations WHERE installation_id = ? ORDER BY created_at ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, installationId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<AIConversationEntity>>() {
      @Override
      @NonNull
      public List<AIConversationEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfConversationId = CursorUtil.getColumnIndexOrThrow(_cursor, "conversation_id");
          final int _cursorIndexOfInstallationId = CursorUtil.getColumnIndexOrThrow(_cursor, "installation_id");
          final int _cursorIndexOfUserMessage = CursorUtil.getColumnIndexOrThrow(_cursor, "user_message");
          final int _cursorIndexOfAiResponse = CursorUtil.getColumnIndexOrThrow(_cursor, "ai_response");
          final int _cursorIndexOfConfidenceScore = CursorUtil.getColumnIndexOrThrow(_cursor, "confidence_score");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final List<AIConversationEntity> _result = new ArrayList<AIConversationEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final AIConversationEntity _item;
            final long _tmpConversationId;
            _tmpConversationId = _cursor.getLong(_cursorIndexOfConversationId);
            final Long _tmpInstallationId;
            if (_cursor.isNull(_cursorIndexOfInstallationId)) {
              _tmpInstallationId = null;
            } else {
              _tmpInstallationId = _cursor.getLong(_cursorIndexOfInstallationId);
            }
            final String _tmpUserMessage;
            if (_cursor.isNull(_cursorIndexOfUserMessage)) {
              _tmpUserMessage = null;
            } else {
              _tmpUserMessage = _cursor.getString(_cursorIndexOfUserMessage);
            }
            final String _tmpAiResponse;
            if (_cursor.isNull(_cursorIndexOfAiResponse)) {
              _tmpAiResponse = null;
            } else {
              _tmpAiResponse = _cursor.getString(_cursorIndexOfAiResponse);
            }
            final Float _tmpConfidenceScore;
            if (_cursor.isNull(_cursorIndexOfConfidenceScore)) {
              _tmpConfidenceScore = null;
            } else {
              _tmpConfidenceScore = _cursor.getFloat(_cursorIndexOfConfidenceScore);
            }
            final Date _tmpCreatedAt;
            final Long _tmp;
            if (_cursor.isNull(_cursorIndexOfCreatedAt)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getLong(_cursorIndexOfCreatedAt);
            }
            _tmpCreatedAt = __dateConverters.fromTimestamp(_tmp);
            _item = new AIConversationEntity(_tmpConversationId,_tmpInstallationId,_tmpUserMessage,_tmpAiResponse,_tmpConfidenceScore,_tmpCreatedAt);
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
  public Flow<List<AIConversationEntity>> getConversationsByInstallationFlow(
      final long installationId) {
    final String _sql = "SELECT * FROM ai_conversations WHERE installation_id = ? ORDER BY created_at ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, installationId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"ai_conversations"}, new Callable<List<AIConversationEntity>>() {
      @Override
      @NonNull
      public List<AIConversationEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfConversationId = CursorUtil.getColumnIndexOrThrow(_cursor, "conversation_id");
          final int _cursorIndexOfInstallationId = CursorUtil.getColumnIndexOrThrow(_cursor, "installation_id");
          final int _cursorIndexOfUserMessage = CursorUtil.getColumnIndexOrThrow(_cursor, "user_message");
          final int _cursorIndexOfAiResponse = CursorUtil.getColumnIndexOrThrow(_cursor, "ai_response");
          final int _cursorIndexOfConfidenceScore = CursorUtil.getColumnIndexOrThrow(_cursor, "confidence_score");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final List<AIConversationEntity> _result = new ArrayList<AIConversationEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final AIConversationEntity _item;
            final long _tmpConversationId;
            _tmpConversationId = _cursor.getLong(_cursorIndexOfConversationId);
            final Long _tmpInstallationId;
            if (_cursor.isNull(_cursorIndexOfInstallationId)) {
              _tmpInstallationId = null;
            } else {
              _tmpInstallationId = _cursor.getLong(_cursorIndexOfInstallationId);
            }
            final String _tmpUserMessage;
            if (_cursor.isNull(_cursorIndexOfUserMessage)) {
              _tmpUserMessage = null;
            } else {
              _tmpUserMessage = _cursor.getString(_cursorIndexOfUserMessage);
            }
            final String _tmpAiResponse;
            if (_cursor.isNull(_cursorIndexOfAiResponse)) {
              _tmpAiResponse = null;
            } else {
              _tmpAiResponse = _cursor.getString(_cursorIndexOfAiResponse);
            }
            final Float _tmpConfidenceScore;
            if (_cursor.isNull(_cursorIndexOfConfidenceScore)) {
              _tmpConfidenceScore = null;
            } else {
              _tmpConfidenceScore = _cursor.getFloat(_cursorIndexOfConfidenceScore);
            }
            final Date _tmpCreatedAt;
            final Long _tmp;
            if (_cursor.isNull(_cursorIndexOfCreatedAt)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getLong(_cursorIndexOfCreatedAt);
            }
            _tmpCreatedAt = __dateConverters.fromTimestamp(_tmp);
            _item = new AIConversationEntity(_tmpConversationId,_tmpInstallationId,_tmpUserMessage,_tmpAiResponse,_tmpConfidenceScore,_tmpCreatedAt);
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
  public Object getRecentConversations(final int limit,
      final Continuation<? super List<AIConversationEntity>> $completion) {
    final String _sql = "SELECT * FROM ai_conversations ORDER BY created_at DESC LIMIT ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, limit);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<AIConversationEntity>>() {
      @Override
      @NonNull
      public List<AIConversationEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfConversationId = CursorUtil.getColumnIndexOrThrow(_cursor, "conversation_id");
          final int _cursorIndexOfInstallationId = CursorUtil.getColumnIndexOrThrow(_cursor, "installation_id");
          final int _cursorIndexOfUserMessage = CursorUtil.getColumnIndexOrThrow(_cursor, "user_message");
          final int _cursorIndexOfAiResponse = CursorUtil.getColumnIndexOrThrow(_cursor, "ai_response");
          final int _cursorIndexOfConfidenceScore = CursorUtil.getColumnIndexOrThrow(_cursor, "confidence_score");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final List<AIConversationEntity> _result = new ArrayList<AIConversationEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final AIConversationEntity _item;
            final long _tmpConversationId;
            _tmpConversationId = _cursor.getLong(_cursorIndexOfConversationId);
            final Long _tmpInstallationId;
            if (_cursor.isNull(_cursorIndexOfInstallationId)) {
              _tmpInstallationId = null;
            } else {
              _tmpInstallationId = _cursor.getLong(_cursorIndexOfInstallationId);
            }
            final String _tmpUserMessage;
            if (_cursor.isNull(_cursorIndexOfUserMessage)) {
              _tmpUserMessage = null;
            } else {
              _tmpUserMessage = _cursor.getString(_cursorIndexOfUserMessage);
            }
            final String _tmpAiResponse;
            if (_cursor.isNull(_cursorIndexOfAiResponse)) {
              _tmpAiResponse = null;
            } else {
              _tmpAiResponse = _cursor.getString(_cursorIndexOfAiResponse);
            }
            final Float _tmpConfidenceScore;
            if (_cursor.isNull(_cursorIndexOfConfidenceScore)) {
              _tmpConfidenceScore = null;
            } else {
              _tmpConfidenceScore = _cursor.getFloat(_cursorIndexOfConfidenceScore);
            }
            final Date _tmpCreatedAt;
            final Long _tmp;
            if (_cursor.isNull(_cursorIndexOfCreatedAt)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getLong(_cursorIndexOfCreatedAt);
            }
            _tmpCreatedAt = __dateConverters.fromTimestamp(_tmp);
            _item = new AIConversationEntity(_tmpConversationId,_tmpInstallationId,_tmpUserMessage,_tmpAiResponse,_tmpConfidenceScore,_tmpCreatedAt);
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
  public Object getTotalConversationCount(final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT COUNT(*) FROM ai_conversations";
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

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
