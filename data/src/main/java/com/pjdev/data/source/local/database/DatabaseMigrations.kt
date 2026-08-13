package com.pjdev.data.source.local.database

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_1_2 = object : Migration(1, 2) {

    override fun migrate(
        db: SupportSQLiteDatabase,
    ) {
        db.execSQL(
            """
            ALTER TABLE remote_keys
            ADD COLUMN lastUpdatedAtMillis INTEGER NOT NULL DEFAULT 0
            """.trimIndent(),
        )

        db.execSQL(
            """
            UPDATE remote_keys
            SET lastUpdatedAtMillis =
                CAST(strftime('%s', 'now') AS INTEGER) * 1000
            """.trimIndent(),
        )
    }
}

val MIGRATION_2_3 = object : Migration(2, 3) {

    override fun migrate(
        db: SupportSQLiteDatabase,
    ) {
        db.execSQL(
            """
            ALTER TABLE character_episode_cross_ref
            ADD COLUMN position INTEGER NOT NULL DEFAULT 0
            """.trimIndent(),
        )

        /*
         * Existing relationships keep the ordering behaviour they had before
         * this migration. The exact API order will be persisted the next time
         * the character detail is refreshed.
         */
        db.execSQL(
            """
            UPDATE character_episode_cross_ref
            SET position = episodeId
            """.trimIndent(),
        )

        db.execSQL(
            """
            DROP INDEX IF EXISTS
            index_character_episode_cross_ref_characterId
            """.trimIndent(),
        )

        db.execSQL(
            """
            CREATE INDEX IF NOT EXISTS
            index_character_episode_cross_ref_characterId_position
            ON character_episode_cross_ref(characterId, position)
            """.trimIndent(),
        )
    }
}