package com.xborg.vendx.database

import android.content.Context
import androidx.room.*
import com.google.gson.annotations.SerializedName

@Database(entities = [AccessToken::class], version = 1)
abstract class AccessTokenDatabase : RoomDatabase() {
    abstract fun accessTokenDao(): AccessTokenDao

    companion object {

        @Volatile
        private var instence: AccessTokenDatabase? = null

        fun getInstance(context: Context): AccessTokenDatabase {
            return instence ?: synchronized(this) {
                instence ?: buildDatabase(context).also { instence = it }
            }
        }

        private fun buildDatabase(context: Context): AccessTokenDatabase {
            return Room.databaseBuilder(
                context, AccessTokenDatabase::class.java,
                "access_token"
            )
                .fallbackToDestructiveMigration()
                .build()
        }
    }
}

@Dao
abstract class  AccessTokenDao {

    @Query("SELECT * FROM access_token LIMIT 1")
    abstract fun get(): AccessToken?

    fun getToken(): String {
        val accessToken = get()!!.accessToken
        val token = get()!!.token
        return accessToken ?: token!!
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insert(accessToken: AccessToken)

    @Query("DELETE FROM access_token")
    abstract fun clear()

}

@Entity(tableName = "access_token")
data class AccessToken(
    @PrimaryKey
    val id: Long,

//    this token will be populated when using email login
    @ColumnInfo(name = "email")
    @SerializedName("email") val email: String?,

    @ColumnInfo(name = "token")
    @SerializedName("token") val token: String?,

//    this fields will be populated when using oauth login
    @ColumnInfo(name = "access_token")
    @SerializedName("access_token") val accessToken: String?,

    @ColumnInfo(name = "expires_in")
    @SerializedName("expires_in") val expiresIn: Int?,

    @ColumnInfo(name = "token_type")
    @SerializedName("token_type") val tokenType: String?,

    @ColumnInfo(name = "scope")
    @SerializedName("scope") val scope: String?,

    @ColumnInfo(name = "refresh_token")
    @SerializedName("refresh_token") val refreshToken: String?
)