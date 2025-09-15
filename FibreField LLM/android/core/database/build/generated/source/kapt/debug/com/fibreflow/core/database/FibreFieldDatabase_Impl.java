package com.fibreflow.core.database;

import androidx.annotation.NonNull;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.RoomDatabase;
import androidx.room.RoomOpenHelper;
import androidx.room.migration.AutoMigrationSpec;
import androidx.room.migration.Migration;
import androidx.room.util.DBUtil;
import androidx.room.util.TableInfo;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;
import com.fibreflow.core.database.dao.AIConversationDao;
import com.fibreflow.core.database.dao.AIConversationDao_Impl;
import com.fibreflow.core.database.dao.ConfigurationDao;
import com.fibreflow.core.database.dao.ConfigurationDao_Impl;
import com.fibreflow.core.database.dao.DropDao;
import com.fibreflow.core.database.dao.DropDao_Impl;
import com.fibreflow.core.database.dao.InstallationDao;
import com.fibreflow.core.database.dao.InstallationDao_Impl;
import com.fibreflow.core.database.dao.PhotoDao;
import com.fibreflow.core.database.dao.PhotoDao_Impl;
import com.fibreflow.core.database.dao.ProjectDao;
import com.fibreflow.core.database.dao.ProjectDao_Impl;
import com.fibreflow.core.database.dao.RemediationDao;
import com.fibreflow.core.database.dao.RemediationDao_Impl;
import com.fibreflow.core.database.dao.SessionDao;
import com.fibreflow.core.database.dao.SessionDao_Impl;
import com.fibreflow.core.database.dao.SyncQueueDao;
import com.fibreflow.core.database.dao.SyncQueueDao_Impl;
import com.fibreflow.core.database.dao.TechnicianDao;
import com.fibreflow.core.database.dao.TechnicianDao_Impl;
import com.fibreflow.core.database.dao.ValidationResultDao;
import com.fibreflow.core.database.dao.ValidationResultDao_Impl;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class FibreFieldDatabase_Impl extends FibreFieldDatabase {
  private volatile ProjectDao _projectDao;

  private volatile DropDao _dropDao;

  private volatile TechnicianDao _technicianDao;

  private volatile InstallationDao _installationDao;

  private volatile PhotoDao _photoDao;

  private volatile RemediationDao _remediationDao;

  private volatile SyncQueueDao _syncQueueDao;

  private volatile AIConversationDao _aIConversationDao;

  private volatile ValidationResultDao _validationResultDao;

  private volatile SessionDao _sessionDao;

  private volatile ConfigurationDao _configurationDao;

  @Override
  @NonNull
  protected SupportSQLiteOpenHelper createOpenHelper(@NonNull final DatabaseConfiguration config) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(config, new RoomOpenHelper.Delegate(1) {
      @Override
      public void createAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `projects` (`project_id` INTEGER NOT NULL, `project_name` TEXT NOT NULL, `boundary_polygon` TEXT NOT NULL, `total_drops` INTEGER NOT NULL, `completed_drops` INTEGER NOT NULL, `active_drops` INTEGER NOT NULL, `failed_drops` INTEGER NOT NULL, `active` INTEGER NOT NULL, `created_at` INTEGER NOT NULL, `updated_at` INTEGER NOT NULL, `metadata` TEXT, PRIMARY KEY(`project_id`))");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_projects_active` ON `projects` (`active`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_projects_project_name` ON `projects` (`project_name`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_projects_created_at` ON `projects` (`created_at`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `drops` (`drop_number` TEXT NOT NULL, `project_id` INTEGER NOT NULL, `latitude` REAL NOT NULL, `longitude` REAL NOT NULL, `altitude` REAL, `accuracy` REAL, `address` TEXT NOT NULL, `status` TEXT NOT NULL, `assigned_technician_id` TEXT, `customer_name` TEXT, `customer_phone` TEXT, `customer_email` TEXT, `installation_date` INTEGER, `activation_status` TEXT NOT NULL, `activation_date` INTEGER, `notes` TEXT, `priority` INTEGER NOT NULL, `created_at` INTEGER NOT NULL, `updated_at` INTEGER NOT NULL, `sync_status` TEXT NOT NULL, `last_sync_attempt` INTEGER, `sync_error` TEXT, `assigned_at` INTEGER, `completed_at` INTEGER, `needs_sync` INTEGER NOT NULL, PRIMARY KEY(`drop_number`), FOREIGN KEY(`project_id`) REFERENCES `projects`(`project_id`) ON UPDATE NO ACTION ON DELETE CASCADE , FOREIGN KEY(`assigned_technician_id`) REFERENCES `technicians`(`technician_id`) ON UPDATE NO ACTION ON DELETE SET NULL )");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_drops_project_id` ON `drops` (`project_id`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_drops_status` ON `drops` (`status`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_drops_assigned_technician_id` ON `drops` (`assigned_technician_id`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_drops_latitude_longitude` ON `drops` (`latitude`, `longitude`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_drops_sync_status` ON `drops` (`sync_status`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_drops_project_id_status` ON `drops` (`project_id`, `status`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_drops_installation_date` ON `drops` (`installation_date`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_drops_priority` ON `drops` (`priority`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_drops_activation_status` ON `drops` (`activation_status`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `technicians` (`technician_id` TEXT NOT NULL, `name` TEXT NOT NULL, `email` TEXT, `phone` TEXT, `role` TEXT NOT NULL, `certifications` TEXT, `active_projects` TEXT, `permissions` TEXT, `active` INTEGER NOT NULL, `last_login` INTEGER, `created_at` INTEGER NOT NULL, `updated_at` INTEGER NOT NULL, `last_sync_at` INTEGER, PRIMARY KEY(`technician_id`))");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_technicians_active` ON `technicians` (`active`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_technicians_role` ON `technicians` (`role`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_technicians_email` ON `technicians` (`email`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_technicians_last_login` ON `technicians` (`last_login`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `installations` (`installation_id` INTEGER NOT NULL, `drop_number` TEXT NOT NULL, `technician_id` TEXT, `status` TEXT NOT NULL, `start_time` INTEGER NOT NULL, `end_time` INTEGER, `ont_serial` TEXT, `speed_test_results` TEXT, `photos` TEXT NOT NULL, `completed_steps` TEXT, `current_step` INTEGER NOT NULL, `total_steps` INTEGER NOT NULL, `validation_errors` TEXT, `ai_guidance_used` INTEGER NOT NULL, `manual_override_used` INTEGER NOT NULL, `created_at` INTEGER NOT NULL, `updated_at` INTEGER NOT NULL, PRIMARY KEY(`installation_id`), FOREIGN KEY(`drop_number`) REFERENCES `drops`(`drop_number`) ON UPDATE NO ACTION ON DELETE CASCADE , FOREIGN KEY(`technician_id`) REFERENCES `technicians`(`technician_id`) ON UPDATE NO ACTION ON DELETE SET NULL )");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_installations_drop_number` ON `installations` (`drop_number`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_installations_technician_id` ON `installations` (`technician_id`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_installations_status` ON `installations` (`status`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_installations_created_at` ON `installations` (`created_at`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `photos` (`photo_id` INTEGER NOT NULL, `installation_id` INTEGER NOT NULL, `photo_type` TEXT NOT NULL, `file_path` TEXT NOT NULL, `file_size_bytes` INTEGER NOT NULL, `width` INTEGER NOT NULL, `height` INTEGER NOT NULL, `validation_status` TEXT NOT NULL, `validation_confidence` REAL, `ai_metadata` TEXT, `manual_override` INTEGER NOT NULL, `override_reason` TEXT, `override_by` TEXT, `override_at` INTEGER, `upload_status` TEXT NOT NULL, `upload_attempts` INTEGER NOT NULL, `last_upload_attempt` INTEGER, `created_at` INTEGER NOT NULL, `updated_at` INTEGER NOT NULL, PRIMARY KEY(`photo_id`), FOREIGN KEY(`installation_id`) REFERENCES `installations`(`installation_id`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_photos_installation_id` ON `photos` (`installation_id`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_photos_photo_type` ON `photos` (`photo_type`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_photos_validation_status` ON `photos` (`validation_status`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_photos_created_at` ON `photos` (`created_at`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `remediations` (`remediation_id` INTEGER NOT NULL, `installation_id` INTEGER NOT NULL, `issue_type` TEXT NOT NULL, `description` TEXT NOT NULL, `severity` TEXT NOT NULL, `status` TEXT NOT NULL, `assigned_to` TEXT, `resolution_notes` TEXT, `resolved_at` INTEGER, `created_at` INTEGER NOT NULL, `updated_at` INTEGER NOT NULL, PRIMARY KEY(`remediation_id`), FOREIGN KEY(`installation_id`) REFERENCES `installations`(`installation_id`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_remediations_installation_id` ON `remediations` (`installation_id`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_remediations_status` ON `remediations` (`status`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_remediations_created_at` ON `remediations` (`created_at`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `sync_queue` (`queue_id` TEXT NOT NULL, `entity_type` TEXT NOT NULL, `entity_id` TEXT NOT NULL, `operation` TEXT NOT NULL, `data` TEXT NOT NULL, `priority` INTEGER NOT NULL, `status` TEXT NOT NULL, `retry_count` INTEGER NOT NULL, `max_retries` INTEGER NOT NULL, `last_attempt` INTEGER, `next_attempt` INTEGER, `error_message` TEXT, `created_at` INTEGER NOT NULL, `updated_at` INTEGER NOT NULL, PRIMARY KEY(`queue_id`))");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_sync_queue_entity_type` ON `sync_queue` (`entity_type`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_sync_queue_priority` ON `sync_queue` (`priority`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_sync_queue_status` ON `sync_queue` (`status`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_sync_queue_next_attempt` ON `sync_queue` (`next_attempt`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `ai_conversations` (`conversation_id` INTEGER NOT NULL, `installation_id` INTEGER, `user_message` TEXT NOT NULL, `ai_response` TEXT NOT NULL, `confidence_score` REAL, `created_at` INTEGER NOT NULL, PRIMARY KEY(`conversation_id`))");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_ai_conversations_installation_id` ON `ai_conversations` (`installation_id`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_ai_conversations_created_at` ON `ai_conversations` (`created_at`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `validation_results` (`validation_id` INTEGER NOT NULL, `photo_id` INTEGER NOT NULL, `validation_type` TEXT NOT NULL, `is_valid` INTEGER NOT NULL, `confidence_score` REAL NOT NULL, `validation_data` TEXT NOT NULL, `created_at` INTEGER NOT NULL, PRIMARY KEY(`validation_id`))");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_validation_results_photo_id` ON `validation_results` (`photo_id`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_validation_results_validation_type` ON `validation_results` (`validation_type`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_validation_results_created_at` ON `validation_results` (`created_at`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `sessions` (`session_id` TEXT NOT NULL, `technician_id` TEXT NOT NULL, `device_id` TEXT NOT NULL, `is_active` INTEGER NOT NULL, `last_activity` INTEGER NOT NULL, `created_at` INTEGER NOT NULL, `expires_at` INTEGER NOT NULL, PRIMARY KEY(`session_id`))");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_sessions_technician_id` ON `sessions` (`technician_id`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_sessions_is_active` ON `sessions` (`is_active`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_sessions_created_at` ON `sessions` (`created_at`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `offline_map_tiles` (`tile_id` TEXT NOT NULL, `zoom_level` INTEGER NOT NULL, `x` INTEGER NOT NULL, `y` INTEGER NOT NULL, `tile_data` BLOB NOT NULL, `file_size` INTEGER NOT NULL, `last_accessed` INTEGER NOT NULL, `created_at` INTEGER NOT NULL, PRIMARY KEY(`tile_id`))");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_offline_map_tiles_zoom_level_x_y` ON `offline_map_tiles` (`zoom_level`, `x`, `y`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_offline_map_tiles_last_accessed` ON `offline_map_tiles` (`last_accessed`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `configuration` (`config_key` TEXT NOT NULL, `config_value` TEXT NOT NULL, `config_type` TEXT NOT NULL, `is_encrypted` INTEGER NOT NULL, `description` TEXT, `updated_at` INTEGER NOT NULL, `updated_by` TEXT, PRIMARY KEY(`config_key`))");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_configuration_config_key` ON `configuration` (`config_key`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_configuration_updated_at` ON `configuration` (`updated_at`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, 'c68b678ca13836bca80948d28a42af4e')");
      }

      @Override
      public void dropAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS `projects`");
        db.execSQL("DROP TABLE IF EXISTS `drops`");
        db.execSQL("DROP TABLE IF EXISTS `technicians`");
        db.execSQL("DROP TABLE IF EXISTS `installations`");
        db.execSQL("DROP TABLE IF EXISTS `photos`");
        db.execSQL("DROP TABLE IF EXISTS `remediations`");
        db.execSQL("DROP TABLE IF EXISTS `sync_queue`");
        db.execSQL("DROP TABLE IF EXISTS `ai_conversations`");
        db.execSQL("DROP TABLE IF EXISTS `validation_results`");
        db.execSQL("DROP TABLE IF EXISTS `sessions`");
        db.execSQL("DROP TABLE IF EXISTS `offline_map_tiles`");
        db.execSQL("DROP TABLE IF EXISTS `configuration`");
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onDestructiveMigration(db);
          }
        }
      }

      @Override
      public void onCreate(@NonNull final SupportSQLiteDatabase db) {
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onCreate(db);
          }
        }
      }

      @Override
      public void onOpen(@NonNull final SupportSQLiteDatabase db) {
        mDatabase = db;
        db.execSQL("PRAGMA foreign_keys = ON");
        internalInitInvalidationTracker(db);
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onOpen(db);
          }
        }
      }

      @Override
      public void onPreMigrate(@NonNull final SupportSQLiteDatabase db) {
        DBUtil.dropFtsSyncTriggers(db);
      }

      @Override
      public void onPostMigrate(@NonNull final SupportSQLiteDatabase db) {
      }

      @Override
      @NonNull
      public RoomOpenHelper.ValidationResult onValidateSchema(
          @NonNull final SupportSQLiteDatabase db) {
        final HashMap<String, TableInfo.Column> _columnsProjects = new HashMap<String, TableInfo.Column>(11);
        _columnsProjects.put("project_id", new TableInfo.Column("project_id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsProjects.put("project_name", new TableInfo.Column("project_name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsProjects.put("boundary_polygon", new TableInfo.Column("boundary_polygon", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsProjects.put("total_drops", new TableInfo.Column("total_drops", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsProjects.put("completed_drops", new TableInfo.Column("completed_drops", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsProjects.put("active_drops", new TableInfo.Column("active_drops", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsProjects.put("failed_drops", new TableInfo.Column("failed_drops", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsProjects.put("active", new TableInfo.Column("active", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsProjects.put("created_at", new TableInfo.Column("created_at", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsProjects.put("updated_at", new TableInfo.Column("updated_at", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsProjects.put("metadata", new TableInfo.Column("metadata", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysProjects = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesProjects = new HashSet<TableInfo.Index>(3);
        _indicesProjects.add(new TableInfo.Index("index_projects_active", false, Arrays.asList("active"), Arrays.asList("ASC")));
        _indicesProjects.add(new TableInfo.Index("index_projects_project_name", false, Arrays.asList("project_name"), Arrays.asList("ASC")));
        _indicesProjects.add(new TableInfo.Index("index_projects_created_at", false, Arrays.asList("created_at"), Arrays.asList("ASC")));
        final TableInfo _infoProjects = new TableInfo("projects", _columnsProjects, _foreignKeysProjects, _indicesProjects);
        final TableInfo _existingProjects = TableInfo.read(db, "projects");
        if (!_infoProjects.equals(_existingProjects)) {
          return new RoomOpenHelper.ValidationResult(false, "projects(com.fibreflow.core.database.entities.ProjectEntity).\n"
                  + " Expected:\n" + _infoProjects + "\n"
                  + " Found:\n" + _existingProjects);
        }
        final HashMap<String, TableInfo.Column> _columnsDrops = new HashMap<String, TableInfo.Column>(25);
        _columnsDrops.put("drop_number", new TableInfo.Column("drop_number", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDrops.put("project_id", new TableInfo.Column("project_id", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDrops.put("latitude", new TableInfo.Column("latitude", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDrops.put("longitude", new TableInfo.Column("longitude", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDrops.put("altitude", new TableInfo.Column("altitude", "REAL", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDrops.put("accuracy", new TableInfo.Column("accuracy", "REAL", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDrops.put("address", new TableInfo.Column("address", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDrops.put("status", new TableInfo.Column("status", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDrops.put("assigned_technician_id", new TableInfo.Column("assigned_technician_id", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDrops.put("customer_name", new TableInfo.Column("customer_name", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDrops.put("customer_phone", new TableInfo.Column("customer_phone", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDrops.put("customer_email", new TableInfo.Column("customer_email", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDrops.put("installation_date", new TableInfo.Column("installation_date", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDrops.put("activation_status", new TableInfo.Column("activation_status", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDrops.put("activation_date", new TableInfo.Column("activation_date", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDrops.put("notes", new TableInfo.Column("notes", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDrops.put("priority", new TableInfo.Column("priority", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDrops.put("created_at", new TableInfo.Column("created_at", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDrops.put("updated_at", new TableInfo.Column("updated_at", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDrops.put("sync_status", new TableInfo.Column("sync_status", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDrops.put("last_sync_attempt", new TableInfo.Column("last_sync_attempt", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDrops.put("sync_error", new TableInfo.Column("sync_error", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDrops.put("assigned_at", new TableInfo.Column("assigned_at", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDrops.put("completed_at", new TableInfo.Column("completed_at", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDrops.put("needs_sync", new TableInfo.Column("needs_sync", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysDrops = new HashSet<TableInfo.ForeignKey>(2);
        _foreignKeysDrops.add(new TableInfo.ForeignKey("projects", "CASCADE", "NO ACTION", Arrays.asList("project_id"), Arrays.asList("project_id")));
        _foreignKeysDrops.add(new TableInfo.ForeignKey("technicians", "SET NULL", "NO ACTION", Arrays.asList("assigned_technician_id"), Arrays.asList("technician_id")));
        final HashSet<TableInfo.Index> _indicesDrops = new HashSet<TableInfo.Index>(9);
        _indicesDrops.add(new TableInfo.Index("index_drops_project_id", false, Arrays.asList("project_id"), Arrays.asList("ASC")));
        _indicesDrops.add(new TableInfo.Index("index_drops_status", false, Arrays.asList("status"), Arrays.asList("ASC")));
        _indicesDrops.add(new TableInfo.Index("index_drops_assigned_technician_id", false, Arrays.asList("assigned_technician_id"), Arrays.asList("ASC")));
        _indicesDrops.add(new TableInfo.Index("index_drops_latitude_longitude", false, Arrays.asList("latitude", "longitude"), Arrays.asList("ASC", "ASC")));
        _indicesDrops.add(new TableInfo.Index("index_drops_sync_status", false, Arrays.asList("sync_status"), Arrays.asList("ASC")));
        _indicesDrops.add(new TableInfo.Index("index_drops_project_id_status", false, Arrays.asList("project_id", "status"), Arrays.asList("ASC", "ASC")));
        _indicesDrops.add(new TableInfo.Index("index_drops_installation_date", false, Arrays.asList("installation_date"), Arrays.asList("ASC")));
        _indicesDrops.add(new TableInfo.Index("index_drops_priority", false, Arrays.asList("priority"), Arrays.asList("ASC")));
        _indicesDrops.add(new TableInfo.Index("index_drops_activation_status", false, Arrays.asList("activation_status"), Arrays.asList("ASC")));
        final TableInfo _infoDrops = new TableInfo("drops", _columnsDrops, _foreignKeysDrops, _indicesDrops);
        final TableInfo _existingDrops = TableInfo.read(db, "drops");
        if (!_infoDrops.equals(_existingDrops)) {
          return new RoomOpenHelper.ValidationResult(false, "drops(com.fibreflow.core.database.entities.DropEntity).\n"
                  + " Expected:\n" + _infoDrops + "\n"
                  + " Found:\n" + _existingDrops);
        }
        final HashMap<String, TableInfo.Column> _columnsTechnicians = new HashMap<String, TableInfo.Column>(13);
        _columnsTechnicians.put("technician_id", new TableInfo.Column("technician_id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTechnicians.put("name", new TableInfo.Column("name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTechnicians.put("email", new TableInfo.Column("email", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTechnicians.put("phone", new TableInfo.Column("phone", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTechnicians.put("role", new TableInfo.Column("role", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTechnicians.put("certifications", new TableInfo.Column("certifications", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTechnicians.put("active_projects", new TableInfo.Column("active_projects", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTechnicians.put("permissions", new TableInfo.Column("permissions", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTechnicians.put("active", new TableInfo.Column("active", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTechnicians.put("last_login", new TableInfo.Column("last_login", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTechnicians.put("created_at", new TableInfo.Column("created_at", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTechnicians.put("updated_at", new TableInfo.Column("updated_at", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTechnicians.put("last_sync_at", new TableInfo.Column("last_sync_at", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysTechnicians = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesTechnicians = new HashSet<TableInfo.Index>(4);
        _indicesTechnicians.add(new TableInfo.Index("index_technicians_active", false, Arrays.asList("active"), Arrays.asList("ASC")));
        _indicesTechnicians.add(new TableInfo.Index("index_technicians_role", false, Arrays.asList("role"), Arrays.asList("ASC")));
        _indicesTechnicians.add(new TableInfo.Index("index_technicians_email", false, Arrays.asList("email"), Arrays.asList("ASC")));
        _indicesTechnicians.add(new TableInfo.Index("index_technicians_last_login", false, Arrays.asList("last_login"), Arrays.asList("ASC")));
        final TableInfo _infoTechnicians = new TableInfo("technicians", _columnsTechnicians, _foreignKeysTechnicians, _indicesTechnicians);
        final TableInfo _existingTechnicians = TableInfo.read(db, "technicians");
        if (!_infoTechnicians.equals(_existingTechnicians)) {
          return new RoomOpenHelper.ValidationResult(false, "technicians(com.fibreflow.core.database.entities.TechnicianEntity).\n"
                  + " Expected:\n" + _infoTechnicians + "\n"
                  + " Found:\n" + _existingTechnicians);
        }
        final HashMap<String, TableInfo.Column> _columnsInstallations = new HashMap<String, TableInfo.Column>(17);
        _columnsInstallations.put("installation_id", new TableInfo.Column("installation_id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsInstallations.put("drop_number", new TableInfo.Column("drop_number", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsInstallations.put("technician_id", new TableInfo.Column("technician_id", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsInstallations.put("status", new TableInfo.Column("status", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsInstallations.put("start_time", new TableInfo.Column("start_time", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsInstallations.put("end_time", new TableInfo.Column("end_time", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsInstallations.put("ont_serial", new TableInfo.Column("ont_serial", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsInstallations.put("speed_test_results", new TableInfo.Column("speed_test_results", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsInstallations.put("photos", new TableInfo.Column("photos", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsInstallations.put("completed_steps", new TableInfo.Column("completed_steps", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsInstallations.put("current_step", new TableInfo.Column("current_step", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsInstallations.put("total_steps", new TableInfo.Column("total_steps", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsInstallations.put("validation_errors", new TableInfo.Column("validation_errors", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsInstallations.put("ai_guidance_used", new TableInfo.Column("ai_guidance_used", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsInstallations.put("manual_override_used", new TableInfo.Column("manual_override_used", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsInstallations.put("created_at", new TableInfo.Column("created_at", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsInstallations.put("updated_at", new TableInfo.Column("updated_at", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysInstallations = new HashSet<TableInfo.ForeignKey>(2);
        _foreignKeysInstallations.add(new TableInfo.ForeignKey("drops", "CASCADE", "NO ACTION", Arrays.asList("drop_number"), Arrays.asList("drop_number")));
        _foreignKeysInstallations.add(new TableInfo.ForeignKey("technicians", "SET NULL", "NO ACTION", Arrays.asList("technician_id"), Arrays.asList("technician_id")));
        final HashSet<TableInfo.Index> _indicesInstallations = new HashSet<TableInfo.Index>(4);
        _indicesInstallations.add(new TableInfo.Index("index_installations_drop_number", false, Arrays.asList("drop_number"), Arrays.asList("ASC")));
        _indicesInstallations.add(new TableInfo.Index("index_installations_technician_id", false, Arrays.asList("technician_id"), Arrays.asList("ASC")));
        _indicesInstallations.add(new TableInfo.Index("index_installations_status", false, Arrays.asList("status"), Arrays.asList("ASC")));
        _indicesInstallations.add(new TableInfo.Index("index_installations_created_at", false, Arrays.asList("created_at"), Arrays.asList("ASC")));
        final TableInfo _infoInstallations = new TableInfo("installations", _columnsInstallations, _foreignKeysInstallations, _indicesInstallations);
        final TableInfo _existingInstallations = TableInfo.read(db, "installations");
        if (!_infoInstallations.equals(_existingInstallations)) {
          return new RoomOpenHelper.ValidationResult(false, "installations(com.fibreflow.core.database.entities.InstallationEntity).\n"
                  + " Expected:\n" + _infoInstallations + "\n"
                  + " Found:\n" + _existingInstallations);
        }
        final HashMap<String, TableInfo.Column> _columnsPhotos = new HashMap<String, TableInfo.Column>(19);
        _columnsPhotos.put("photo_id", new TableInfo.Column("photo_id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPhotos.put("installation_id", new TableInfo.Column("installation_id", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPhotos.put("photo_type", new TableInfo.Column("photo_type", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPhotos.put("file_path", new TableInfo.Column("file_path", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPhotos.put("file_size_bytes", new TableInfo.Column("file_size_bytes", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPhotos.put("width", new TableInfo.Column("width", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPhotos.put("height", new TableInfo.Column("height", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPhotos.put("validation_status", new TableInfo.Column("validation_status", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPhotos.put("validation_confidence", new TableInfo.Column("validation_confidence", "REAL", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPhotos.put("ai_metadata", new TableInfo.Column("ai_metadata", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPhotos.put("manual_override", new TableInfo.Column("manual_override", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPhotos.put("override_reason", new TableInfo.Column("override_reason", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPhotos.put("override_by", new TableInfo.Column("override_by", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPhotos.put("override_at", new TableInfo.Column("override_at", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPhotos.put("upload_status", new TableInfo.Column("upload_status", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPhotos.put("upload_attempts", new TableInfo.Column("upload_attempts", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPhotos.put("last_upload_attempt", new TableInfo.Column("last_upload_attempt", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPhotos.put("created_at", new TableInfo.Column("created_at", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPhotos.put("updated_at", new TableInfo.Column("updated_at", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysPhotos = new HashSet<TableInfo.ForeignKey>(1);
        _foreignKeysPhotos.add(new TableInfo.ForeignKey("installations", "CASCADE", "NO ACTION", Arrays.asList("installation_id"), Arrays.asList("installation_id")));
        final HashSet<TableInfo.Index> _indicesPhotos = new HashSet<TableInfo.Index>(4);
        _indicesPhotos.add(new TableInfo.Index("index_photos_installation_id", false, Arrays.asList("installation_id"), Arrays.asList("ASC")));
        _indicesPhotos.add(new TableInfo.Index("index_photos_photo_type", false, Arrays.asList("photo_type"), Arrays.asList("ASC")));
        _indicesPhotos.add(new TableInfo.Index("index_photos_validation_status", false, Arrays.asList("validation_status"), Arrays.asList("ASC")));
        _indicesPhotos.add(new TableInfo.Index("index_photos_created_at", false, Arrays.asList("created_at"), Arrays.asList("ASC")));
        final TableInfo _infoPhotos = new TableInfo("photos", _columnsPhotos, _foreignKeysPhotos, _indicesPhotos);
        final TableInfo _existingPhotos = TableInfo.read(db, "photos");
        if (!_infoPhotos.equals(_existingPhotos)) {
          return new RoomOpenHelper.ValidationResult(false, "photos(com.fibreflow.core.database.entities.PhotoEntity).\n"
                  + " Expected:\n" + _infoPhotos + "\n"
                  + " Found:\n" + _existingPhotos);
        }
        final HashMap<String, TableInfo.Column> _columnsRemediations = new HashMap<String, TableInfo.Column>(11);
        _columnsRemediations.put("remediation_id", new TableInfo.Column("remediation_id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRemediations.put("installation_id", new TableInfo.Column("installation_id", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRemediations.put("issue_type", new TableInfo.Column("issue_type", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRemediations.put("description", new TableInfo.Column("description", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRemediations.put("severity", new TableInfo.Column("severity", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRemediations.put("status", new TableInfo.Column("status", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRemediations.put("assigned_to", new TableInfo.Column("assigned_to", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRemediations.put("resolution_notes", new TableInfo.Column("resolution_notes", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRemediations.put("resolved_at", new TableInfo.Column("resolved_at", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRemediations.put("created_at", new TableInfo.Column("created_at", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRemediations.put("updated_at", new TableInfo.Column("updated_at", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysRemediations = new HashSet<TableInfo.ForeignKey>(1);
        _foreignKeysRemediations.add(new TableInfo.ForeignKey("installations", "CASCADE", "NO ACTION", Arrays.asList("installation_id"), Arrays.asList("installation_id")));
        final HashSet<TableInfo.Index> _indicesRemediations = new HashSet<TableInfo.Index>(3);
        _indicesRemediations.add(new TableInfo.Index("index_remediations_installation_id", false, Arrays.asList("installation_id"), Arrays.asList("ASC")));
        _indicesRemediations.add(new TableInfo.Index("index_remediations_status", false, Arrays.asList("status"), Arrays.asList("ASC")));
        _indicesRemediations.add(new TableInfo.Index("index_remediations_created_at", false, Arrays.asList("created_at"), Arrays.asList("ASC")));
        final TableInfo _infoRemediations = new TableInfo("remediations", _columnsRemediations, _foreignKeysRemediations, _indicesRemediations);
        final TableInfo _existingRemediations = TableInfo.read(db, "remediations");
        if (!_infoRemediations.equals(_existingRemediations)) {
          return new RoomOpenHelper.ValidationResult(false, "remediations(com.fibreflow.core.database.entities.RemediationEntity).\n"
                  + " Expected:\n" + _infoRemediations + "\n"
                  + " Found:\n" + _existingRemediations);
        }
        final HashMap<String, TableInfo.Column> _columnsSyncQueue = new HashMap<String, TableInfo.Column>(14);
        _columnsSyncQueue.put("queue_id", new TableInfo.Column("queue_id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSyncQueue.put("entity_type", new TableInfo.Column("entity_type", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSyncQueue.put("entity_id", new TableInfo.Column("entity_id", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSyncQueue.put("operation", new TableInfo.Column("operation", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSyncQueue.put("data", new TableInfo.Column("data", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSyncQueue.put("priority", new TableInfo.Column("priority", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSyncQueue.put("status", new TableInfo.Column("status", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSyncQueue.put("retry_count", new TableInfo.Column("retry_count", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSyncQueue.put("max_retries", new TableInfo.Column("max_retries", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSyncQueue.put("last_attempt", new TableInfo.Column("last_attempt", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSyncQueue.put("next_attempt", new TableInfo.Column("next_attempt", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSyncQueue.put("error_message", new TableInfo.Column("error_message", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSyncQueue.put("created_at", new TableInfo.Column("created_at", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSyncQueue.put("updated_at", new TableInfo.Column("updated_at", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysSyncQueue = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesSyncQueue = new HashSet<TableInfo.Index>(4);
        _indicesSyncQueue.add(new TableInfo.Index("index_sync_queue_entity_type", false, Arrays.asList("entity_type"), Arrays.asList("ASC")));
        _indicesSyncQueue.add(new TableInfo.Index("index_sync_queue_priority", false, Arrays.asList("priority"), Arrays.asList("ASC")));
        _indicesSyncQueue.add(new TableInfo.Index("index_sync_queue_status", false, Arrays.asList("status"), Arrays.asList("ASC")));
        _indicesSyncQueue.add(new TableInfo.Index("index_sync_queue_next_attempt", false, Arrays.asList("next_attempt"), Arrays.asList("ASC")));
        final TableInfo _infoSyncQueue = new TableInfo("sync_queue", _columnsSyncQueue, _foreignKeysSyncQueue, _indicesSyncQueue);
        final TableInfo _existingSyncQueue = TableInfo.read(db, "sync_queue");
        if (!_infoSyncQueue.equals(_existingSyncQueue)) {
          return new RoomOpenHelper.ValidationResult(false, "sync_queue(com.fibreflow.core.database.entities.SyncQueueEntity).\n"
                  + " Expected:\n" + _infoSyncQueue + "\n"
                  + " Found:\n" + _existingSyncQueue);
        }
        final HashMap<String, TableInfo.Column> _columnsAiConversations = new HashMap<String, TableInfo.Column>(6);
        _columnsAiConversations.put("conversation_id", new TableInfo.Column("conversation_id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAiConversations.put("installation_id", new TableInfo.Column("installation_id", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAiConversations.put("user_message", new TableInfo.Column("user_message", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAiConversations.put("ai_response", new TableInfo.Column("ai_response", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAiConversations.put("confidence_score", new TableInfo.Column("confidence_score", "REAL", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAiConversations.put("created_at", new TableInfo.Column("created_at", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysAiConversations = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesAiConversations = new HashSet<TableInfo.Index>(2);
        _indicesAiConversations.add(new TableInfo.Index("index_ai_conversations_installation_id", false, Arrays.asList("installation_id"), Arrays.asList("ASC")));
        _indicesAiConversations.add(new TableInfo.Index("index_ai_conversations_created_at", false, Arrays.asList("created_at"), Arrays.asList("ASC")));
        final TableInfo _infoAiConversations = new TableInfo("ai_conversations", _columnsAiConversations, _foreignKeysAiConversations, _indicesAiConversations);
        final TableInfo _existingAiConversations = TableInfo.read(db, "ai_conversations");
        if (!_infoAiConversations.equals(_existingAiConversations)) {
          return new RoomOpenHelper.ValidationResult(false, "ai_conversations(com.fibreflow.core.database.entities.AIConversationEntity).\n"
                  + " Expected:\n" + _infoAiConversations + "\n"
                  + " Found:\n" + _existingAiConversations);
        }
        final HashMap<String, TableInfo.Column> _columnsValidationResults = new HashMap<String, TableInfo.Column>(7);
        _columnsValidationResults.put("validation_id", new TableInfo.Column("validation_id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsValidationResults.put("photo_id", new TableInfo.Column("photo_id", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsValidationResults.put("validation_type", new TableInfo.Column("validation_type", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsValidationResults.put("is_valid", new TableInfo.Column("is_valid", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsValidationResults.put("confidence_score", new TableInfo.Column("confidence_score", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsValidationResults.put("validation_data", new TableInfo.Column("validation_data", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsValidationResults.put("created_at", new TableInfo.Column("created_at", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysValidationResults = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesValidationResults = new HashSet<TableInfo.Index>(3);
        _indicesValidationResults.add(new TableInfo.Index("index_validation_results_photo_id", false, Arrays.asList("photo_id"), Arrays.asList("ASC")));
        _indicesValidationResults.add(new TableInfo.Index("index_validation_results_validation_type", false, Arrays.asList("validation_type"), Arrays.asList("ASC")));
        _indicesValidationResults.add(new TableInfo.Index("index_validation_results_created_at", false, Arrays.asList("created_at"), Arrays.asList("ASC")));
        final TableInfo _infoValidationResults = new TableInfo("validation_results", _columnsValidationResults, _foreignKeysValidationResults, _indicesValidationResults);
        final TableInfo _existingValidationResults = TableInfo.read(db, "validation_results");
        if (!_infoValidationResults.equals(_existingValidationResults)) {
          return new RoomOpenHelper.ValidationResult(false, "validation_results(com.fibreflow.core.database.entities.ValidationResultEntity).\n"
                  + " Expected:\n" + _infoValidationResults + "\n"
                  + " Found:\n" + _existingValidationResults);
        }
        final HashMap<String, TableInfo.Column> _columnsSessions = new HashMap<String, TableInfo.Column>(7);
        _columnsSessions.put("session_id", new TableInfo.Column("session_id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSessions.put("technician_id", new TableInfo.Column("technician_id", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSessions.put("device_id", new TableInfo.Column("device_id", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSessions.put("is_active", new TableInfo.Column("is_active", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSessions.put("last_activity", new TableInfo.Column("last_activity", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSessions.put("created_at", new TableInfo.Column("created_at", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSessions.put("expires_at", new TableInfo.Column("expires_at", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysSessions = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesSessions = new HashSet<TableInfo.Index>(3);
        _indicesSessions.add(new TableInfo.Index("index_sessions_technician_id", false, Arrays.asList("technician_id"), Arrays.asList("ASC")));
        _indicesSessions.add(new TableInfo.Index("index_sessions_is_active", false, Arrays.asList("is_active"), Arrays.asList("ASC")));
        _indicesSessions.add(new TableInfo.Index("index_sessions_created_at", false, Arrays.asList("created_at"), Arrays.asList("ASC")));
        final TableInfo _infoSessions = new TableInfo("sessions", _columnsSessions, _foreignKeysSessions, _indicesSessions);
        final TableInfo _existingSessions = TableInfo.read(db, "sessions");
        if (!_infoSessions.equals(_existingSessions)) {
          return new RoomOpenHelper.ValidationResult(false, "sessions(com.fibreflow.core.database.entities.SessionEntity).\n"
                  + " Expected:\n" + _infoSessions + "\n"
                  + " Found:\n" + _existingSessions);
        }
        final HashMap<String, TableInfo.Column> _columnsOfflineMapTiles = new HashMap<String, TableInfo.Column>(8);
        _columnsOfflineMapTiles.put("tile_id", new TableInfo.Column("tile_id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsOfflineMapTiles.put("zoom_level", new TableInfo.Column("zoom_level", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsOfflineMapTiles.put("x", new TableInfo.Column("x", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsOfflineMapTiles.put("y", new TableInfo.Column("y", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsOfflineMapTiles.put("tile_data", new TableInfo.Column("tile_data", "BLOB", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsOfflineMapTiles.put("file_size", new TableInfo.Column("file_size", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsOfflineMapTiles.put("last_accessed", new TableInfo.Column("last_accessed", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsOfflineMapTiles.put("created_at", new TableInfo.Column("created_at", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysOfflineMapTiles = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesOfflineMapTiles = new HashSet<TableInfo.Index>(2);
        _indicesOfflineMapTiles.add(new TableInfo.Index("index_offline_map_tiles_zoom_level_x_y", false, Arrays.asList("zoom_level", "x", "y"), Arrays.asList("ASC", "ASC", "ASC")));
        _indicesOfflineMapTiles.add(new TableInfo.Index("index_offline_map_tiles_last_accessed", false, Arrays.asList("last_accessed"), Arrays.asList("ASC")));
        final TableInfo _infoOfflineMapTiles = new TableInfo("offline_map_tiles", _columnsOfflineMapTiles, _foreignKeysOfflineMapTiles, _indicesOfflineMapTiles);
        final TableInfo _existingOfflineMapTiles = TableInfo.read(db, "offline_map_tiles");
        if (!_infoOfflineMapTiles.equals(_existingOfflineMapTiles)) {
          return new RoomOpenHelper.ValidationResult(false, "offline_map_tiles(com.fibreflow.core.database.entities.OfflineMapTileEntity).\n"
                  + " Expected:\n" + _infoOfflineMapTiles + "\n"
                  + " Found:\n" + _existingOfflineMapTiles);
        }
        final HashMap<String, TableInfo.Column> _columnsConfiguration = new HashMap<String, TableInfo.Column>(7);
        _columnsConfiguration.put("config_key", new TableInfo.Column("config_key", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsConfiguration.put("config_value", new TableInfo.Column("config_value", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsConfiguration.put("config_type", new TableInfo.Column("config_type", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsConfiguration.put("is_encrypted", new TableInfo.Column("is_encrypted", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsConfiguration.put("description", new TableInfo.Column("description", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsConfiguration.put("updated_at", new TableInfo.Column("updated_at", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsConfiguration.put("updated_by", new TableInfo.Column("updated_by", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysConfiguration = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesConfiguration = new HashSet<TableInfo.Index>(2);
        _indicesConfiguration.add(new TableInfo.Index("index_configuration_config_key", false, Arrays.asList("config_key"), Arrays.asList("ASC")));
        _indicesConfiguration.add(new TableInfo.Index("index_configuration_updated_at", false, Arrays.asList("updated_at"), Arrays.asList("ASC")));
        final TableInfo _infoConfiguration = new TableInfo("configuration", _columnsConfiguration, _foreignKeysConfiguration, _indicesConfiguration);
        final TableInfo _existingConfiguration = TableInfo.read(db, "configuration");
        if (!_infoConfiguration.equals(_existingConfiguration)) {
          return new RoomOpenHelper.ValidationResult(false, "configuration(com.fibreflow.core.database.entities.ConfigurationEntity).\n"
                  + " Expected:\n" + _infoConfiguration + "\n"
                  + " Found:\n" + _existingConfiguration);
        }
        return new RoomOpenHelper.ValidationResult(true, null);
      }
    }, "c68b678ca13836bca80948d28a42af4e", "bda69c3c48a2dc7314afa288956794c4");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(config.context).name(config.name).callback(_openCallback).build();
    final SupportSQLiteOpenHelper _helper = config.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  @NonNull
  protected InvalidationTracker createInvalidationTracker() {
    final HashMap<String, String> _shadowTablesMap = new HashMap<String, String>(0);
    final HashMap<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(0);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "projects","drops","technicians","installations","photos","remediations","sync_queue","ai_conversations","validation_results","sessions","offline_map_tiles","configuration");
  }

  @Override
  public void clearAllTables() {
    super.assertNotMainThread();
    final SupportSQLiteDatabase _db = super.getOpenHelper().getWritableDatabase();
    final boolean _supportsDeferForeignKeys = android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP;
    try {
      if (!_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA foreign_keys = FALSE");
      }
      super.beginTransaction();
      if (_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA defer_foreign_keys = TRUE");
      }
      _db.execSQL("DELETE FROM `projects`");
      _db.execSQL("DELETE FROM `drops`");
      _db.execSQL("DELETE FROM `technicians`");
      _db.execSQL("DELETE FROM `installations`");
      _db.execSQL("DELETE FROM `photos`");
      _db.execSQL("DELETE FROM `remediations`");
      _db.execSQL("DELETE FROM `sync_queue`");
      _db.execSQL("DELETE FROM `ai_conversations`");
      _db.execSQL("DELETE FROM `validation_results`");
      _db.execSQL("DELETE FROM `sessions`");
      _db.execSQL("DELETE FROM `offline_map_tiles`");
      _db.execSQL("DELETE FROM `configuration`");
      super.setTransactionSuccessful();
    } finally {
      super.endTransaction();
      if (!_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA foreign_keys = TRUE");
      }
      _db.query("PRAGMA wal_checkpoint(FULL)").close();
      if (!_db.inTransaction()) {
        _db.execSQL("VACUUM");
      }
    }
  }

  @Override
  @NonNull
  protected Map<Class<?>, List<Class<?>>> getRequiredTypeConverters() {
    final HashMap<Class<?>, List<Class<?>>> _typeConvertersMap = new HashMap<Class<?>, List<Class<?>>>();
    _typeConvertersMap.put(ProjectDao.class, ProjectDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(DropDao.class, DropDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(TechnicianDao.class, TechnicianDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(InstallationDao.class, InstallationDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(PhotoDao.class, PhotoDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(RemediationDao.class, RemediationDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(SyncQueueDao.class, SyncQueueDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(AIConversationDao.class, AIConversationDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(ValidationResultDao.class, ValidationResultDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(SessionDao.class, SessionDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(ConfigurationDao.class, ConfigurationDao_Impl.getRequiredConverters());
    return _typeConvertersMap;
  }

  @Override
  @NonNull
  public Set<Class<? extends AutoMigrationSpec>> getRequiredAutoMigrationSpecs() {
    final HashSet<Class<? extends AutoMigrationSpec>> _autoMigrationSpecsSet = new HashSet<Class<? extends AutoMigrationSpec>>();
    return _autoMigrationSpecsSet;
  }

  @Override
  @NonNull
  public List<Migration> getAutoMigrations(
      @NonNull final Map<Class<? extends AutoMigrationSpec>, AutoMigrationSpec> autoMigrationSpecs) {
    final List<Migration> _autoMigrations = new ArrayList<Migration>();
    return _autoMigrations;
  }

  @Override
  public ProjectDao projectDao() {
    if (_projectDao != null) {
      return _projectDao;
    } else {
      synchronized(this) {
        if(_projectDao == null) {
          _projectDao = new ProjectDao_Impl(this);
        }
        return _projectDao;
      }
    }
  }

  @Override
  public DropDao dropDao() {
    if (_dropDao != null) {
      return _dropDao;
    } else {
      synchronized(this) {
        if(_dropDao == null) {
          _dropDao = new DropDao_Impl(this);
        }
        return _dropDao;
      }
    }
  }

  @Override
  public TechnicianDao technicianDao() {
    if (_technicianDao != null) {
      return _technicianDao;
    } else {
      synchronized(this) {
        if(_technicianDao == null) {
          _technicianDao = new TechnicianDao_Impl(this);
        }
        return _technicianDao;
      }
    }
  }

  @Override
  public InstallationDao installationDao() {
    if (_installationDao != null) {
      return _installationDao;
    } else {
      synchronized(this) {
        if(_installationDao == null) {
          _installationDao = new InstallationDao_Impl(this);
        }
        return _installationDao;
      }
    }
  }

  @Override
  public PhotoDao photoDao() {
    if (_photoDao != null) {
      return _photoDao;
    } else {
      synchronized(this) {
        if(_photoDao == null) {
          _photoDao = new PhotoDao_Impl(this);
        }
        return _photoDao;
      }
    }
  }

  @Override
  public RemediationDao remediationDao() {
    if (_remediationDao != null) {
      return _remediationDao;
    } else {
      synchronized(this) {
        if(_remediationDao == null) {
          _remediationDao = new RemediationDao_Impl(this);
        }
        return _remediationDao;
      }
    }
  }

  @Override
  public SyncQueueDao syncQueueDao() {
    if (_syncQueueDao != null) {
      return _syncQueueDao;
    } else {
      synchronized(this) {
        if(_syncQueueDao == null) {
          _syncQueueDao = new SyncQueueDao_Impl(this);
        }
        return _syncQueueDao;
      }
    }
  }

  @Override
  public AIConversationDao aiConversationDao() {
    if (_aIConversationDao != null) {
      return _aIConversationDao;
    } else {
      synchronized(this) {
        if(_aIConversationDao == null) {
          _aIConversationDao = new AIConversationDao_Impl(this);
        }
        return _aIConversationDao;
      }
    }
  }

  @Override
  public ValidationResultDao validationResultDao() {
    if (_validationResultDao != null) {
      return _validationResultDao;
    } else {
      synchronized(this) {
        if(_validationResultDao == null) {
          _validationResultDao = new ValidationResultDao_Impl(this);
        }
        return _validationResultDao;
      }
    }
  }

  @Override
  public SessionDao sessionDao() {
    if (_sessionDao != null) {
      return _sessionDao;
    } else {
      synchronized(this) {
        if(_sessionDao == null) {
          _sessionDao = new SessionDao_Impl(this);
        }
        return _sessionDao;
      }
    }
  }

  @Override
  public ConfigurationDao configurationDao() {
    if (_configurationDao != null) {
      return _configurationDao;
    } else {
      synchronized(this) {
        if(_configurationDao == null) {
          _configurationDao = new ConfigurationDao_Impl(this);
        }
        return _configurationDao;
      }
    }
  }
}
