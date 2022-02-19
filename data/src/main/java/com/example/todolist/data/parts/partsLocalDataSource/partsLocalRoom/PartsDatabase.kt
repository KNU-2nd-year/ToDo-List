package com.example.todolist.data.parts.partsLocalDataSource.partsLocalRoom

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.todolist.data.parts.partsLocalDataSource.entities.ImagePartDbModel
import com.example.todolist.data.parts.partsLocalDataSource.entities.TextPartDbModel
import com.example.todolist.data.parts.partsLocalDataSource.entities.TodoPartDbModel
import com.example.todolist.data.parts.partsLocalDataSource.partsLocalRoom.imagePartDataDao.ImageContentDao
import com.example.todolist.data.parts.partsLocalDataSource.partsLocalRoom.imagePartDataDao.ImageMetaInfoDao
import com.example.todolist.data.parts.partsLocalDataSource.partsLocalRoom.imagePartDataDao.ImagePartDataDao
import com.example.todolist.data.parts.partsLocalDataSource.partsLocalRoom.imagePartDataDao.entities.ImageContent
import com.example.todolist.data.parts.partsLocalDataSource.partsLocalRoom.imagePartDataDao.entities.ImageMetaInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Provider

@Database(
    entities = [TextPartDbModel::class, TodoPartDbModel::class, ImageMetaInfo::class],
    version = 1,
    exportSchema = false,
)
abstract class PartsDatabase : RoomDatabase() {
    abstract fun textPartDataDao(): TextPartDataDao
    abstract fun todoPartDataDao(): TodoPartDataDao
    abstract fun imageMetaInfoData(): ImageMetaInfoDao

    class CallBack @Inject constructor(
        private val database: Provider<PartsDatabase>,
        //private val applicationScope: CoroutineScope = CoroutineScope(SupervisorJob())
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)

            val textPartDao = database.get().textPartDataDao()
            val todoPartDao = database.get().todoPartDataDao()

            /*applicationScope*/
            CoroutineScope(SupervisorJob()).launch {
                textPartDao.addTextPart(TextPartDbModel("You need to to your homework!!", 1, 1))
                textPartDao.addTextPart(TextPartDbModel("You need to to your homework!! - 2", 2, 1))
                textPartDao.addTextPart(TextPartDbModel("You need to to your homework!! - 3", 4, 1))

                todoPartDao.addTodoPart(TodoPartDbModel("Do 1\nDo 2\nDo sth else", 3, 1))
                todoPartDao.addTodoPart(TodoPartDbModel("Do 1\nDo 2\nDo sth else", 5, 1))
            }
        }
    }
}