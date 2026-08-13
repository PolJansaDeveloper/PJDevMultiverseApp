package com.pjdev.data.source.local.database

import androidx.room.testing.MigrationTestHelper
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MultiverseDatabaseMigrationTest {

    @get:Rule
    val migrationTestHelper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        MultiverseDatabase::class.java,
    )

    @Test
    fun migration1To2PreservesRemoteKeyAndAddsFreshnessTimestamp() {
        migrationTestHelper
            .createDatabase(
                DATABASE_1_TO_2,
                1,
            )
            .apply {
                execSQL(
                    """
                    INSERT INTO remote_keys (
                        searchQuery,
                        nextPage
                    )
                    VALUES (
                        'rick',
                        2
                    )
                    """.trimIndent(),
                )

                close()
            }

        val migratedDatabase =
            migrationTestHelper.runMigrationsAndValidate(
                DATABASE_1_TO_2,
                2,
                true,
                MIGRATION_1_2,
            )

        migratedDatabase.query(
            """
            SELECT
                searchQuery,
                nextPage,
                lastUpdatedAtMillis
            FROM remote_keys
            WHERE searchQuery = 'rick'
            """.trimIndent(),
        ).use { cursor ->
            assertTrue(
                cursor.moveToFirst(),
            )

            assertEquals(
                "rick",
                cursor.getString(
                    cursor.getColumnIndexOrThrow(
                        "searchQuery",
                    ),
                ),
            )

            assertEquals(
                2,
                cursor.getInt(
                    cursor.getColumnIndexOrThrow(
                        "nextPage",
                    ),
                ),
            )

            assertTrue(
                cursor.getLong(
                    cursor.getColumnIndexOrThrow(
                        "lastUpdatedAtMillis",
                    ),
                ) > 0L,
            )
        }

        migratedDatabase.close()
    }

    @Test
    fun migration2To3PreservesEpisodeRelationAndAddsPosition() {
        migrationTestHelper
            .createDatabase(
                DATABASE_2_TO_3,
                2,
            )
            .apply {
                execSQL(
                    """
                    INSERT INTO characters (
                        id,
                        name,
                        status,
                        species,
                        origin,
                        location,
                        imageUrl,
                        episodeCount
                    )
                    VALUES (
                        1,
                        'Rick Sanchez',
                        'Alive',
                        'Human',
                        'Earth',
                        'Citadel of Ricks',
                        'https://example.com/rick.jpg',
                        1
                    )
                    """.trimIndent(),
                )

                execSQL(
                    """
                    INSERT INTO episodes (
                        id,
                        name,
                        code,
                        airDate
                    )
                    VALUES (
                        7,
                        'Rick Potion #9',
                        'S01E06',
                        'January 27, 2014'
                    )
                    """.trimIndent(),
                )

                execSQL(
                    """
                    INSERT INTO character_episode_cross_ref (
                        characterId,
                        episodeId
                    )
                    VALUES (
                        1,
                        7
                    )
                    """.trimIndent(),
                )

                close()
            }

        val migratedDatabase =
            migrationTestHelper.runMigrationsAndValidate(
                DATABASE_2_TO_3,
                3,
                true,
                MIGRATION_2_3,
            )

        migratedDatabase.query(
            """
            SELECT
                characterId,
                episodeId,
                position
            FROM character_episode_cross_ref
            WHERE characterId = 1
            """.trimIndent(),
        ).use { cursor ->
            assertTrue(
                cursor.moveToFirst(),
            )

            assertEquals(
                1,
                cursor.getInt(
                    cursor.getColumnIndexOrThrow(
                        "characterId",
                    ),
                ),
            )

            assertEquals(
                7,
                cursor.getInt(
                    cursor.getColumnIndexOrThrow(
                        "episodeId",
                    ),
                ),
            )

            /*
             * Existing rows preserve their previous ID-based ordering until
             * the next network detail refresh stores the real API position.
             */
            assertEquals(
                7,
                cursor.getInt(
                    cursor.getColumnIndexOrThrow(
                        "position",
                    ),
                ),
            )
        }

        migratedDatabase.close()
    }

    @Test
    fun migrationFromVersion1To3CompletesSuccessfully() {
        migrationTestHelper
            .createDatabase(
                DATABASE_1_TO_3,
                1,
            )
            .apply {
                execSQL(
                    """
                    INSERT INTO remote_keys (
                        searchQuery,
                        nextPage
                    )
                    VALUES (
                        'rick',
                        2
                    )
                    """.trimIndent(),
                )

                close()
            }

        val migratedDatabase =
            migrationTestHelper.runMigrationsAndValidate(
                DATABASE_1_TO_3,
                3,
                true,
                MIGRATION_1_2,
                MIGRATION_2_3,
            )

        migratedDatabase.query(
            """
            SELECT
                nextPage,
                lastUpdatedAtMillis
            FROM remote_keys
            WHERE searchQuery = 'rick'
            """.trimIndent(),
        ).use { cursor ->
            assertTrue(
                cursor.moveToFirst(),
            )

            assertEquals(
                2,
                cursor.getInt(
                    cursor.getColumnIndexOrThrow(
                        "nextPage",
                    ),
                ),
            )

            assertTrue(
                cursor.getLong(
                    cursor.getColumnIndexOrThrow(
                        "lastUpdatedAtMillis",
                    ),
                ) > 0L,
            )
        }

        migratedDatabase.close()
    }

    private companion object {
        const val DATABASE_1_TO_2 =
            "migration-1-to-2"

        const val DATABASE_2_TO_3 =
            "migration-2-to-3"

        const val DATABASE_1_TO_3 =
            "migration-1-to-3"
    }
}