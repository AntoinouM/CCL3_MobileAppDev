package com.example.myapplication

import androidx.room.*
import androidx.room.ForeignKey.Companion.CASCADE
import androidx.room.migration.AutoMigrationSpec
import java.io.Serializable

@Entity
data class Category(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val name: String
) : Serializable

@Entity(foreignKeys = [ForeignKey(entity = Category::class,
    parentColumns = arrayOf("id"),
    childColumns = arrayOf("categoryId"),
    onDelete = CASCADE
)]
)
data class Topic(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val name: String,
    val categoryId: Int
) : Serializable

@Entity(foreignKeys = [ForeignKey(entity = Topic::class,
    parentColumns = arrayOf("id"),
    childColumns = arrayOf("topicId"),
    onDelete = CASCADE)]
)
data class Card(
    @PrimaryKey(autoGenerate = true) var id: Int,
    var title: String,
    var content: String,
    val topicId: Int
) : Serializable

@Entity(foreignKeys = [ForeignKey(
    entity = Card::class,
    parentColumns = arrayOf("id"),
    childColumns = arrayOf("cardId"),
    onDelete = CASCADE),
    ForeignKey(
    entity = Category::class,
    parentColumns = arrayOf("id"),
    childColumns = arrayOf("categoryId"),
    onDelete = CASCADE)]
)
data class CardResult(
    @PrimaryKey(autoGenerate = true,) var id: Int,
    val cardId: Int,
    val categoryId: Int,
    var success: Boolean
) : Serializable

data class TopicWithCards(
    @Embedded val topic: Topic,
    @Relation(
        parentColumn = "id",
        entityColumn = "topicId"
    )
    var cards: List<Card>
)

data class CategoryWithTopicsWithCards(
    @Embedded val category: Category,
    @Relation(
        entity = Topic::class,
        parentColumn = "id",
        entityColumn = "categoryId"
    )
    var topics: List<TopicWithCards>
)

data class CategoryWithTopics(
    @Embedded val category: Category,
    @Relation(
        parentColumn = "id",
        entityColumn = "categoryId"
    )
    var topics: List<Topic>
)

@Dao
interface CategoryDao {

    @Query("SELECT * FROM category")
    fun getAll(): List<Category>

    @Transaction
    @Query("SELECT * FROM category")
    fun getCategoriesWithTopics(): List<CategoryWithTopics>

    @Transaction
    @Query("SELECT * FROM category")
    fun getCategoriesWithTopicsWithCards(): List<CategoryWithTopicsWithCards>

    @Query("SELECT * FROM category WHERE id = :id")
    fun getById(id: Int): Category

    @Query("SELECT * FROM category WHERE name = :name")
    fun getCategoryByName(name: String): Category

    @Transaction
    @Query("SELECT * FROM category WHERE name = :name")
    fun getCategoryWithTopicsWithCards(name: String): CategoryWithTopicsWithCards

    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insert(category: Category)

    @Update
    fun update(category: Category)

    @Delete
    fun delete(category: Category)

    @Query("DELETE FROM category")
    fun deleteAll()
}

@Dao
interface TopicDao {
    @Query("SELECT * FROM topic")
    fun getAll(): List<Topic>

    @Query("SELECT * FROM topic WHERE name = :name")
    fun getTopicByName(name: String): Topic

    @Transaction
    @Query("SELECT * FROM topic WHERE categoryId = :id")
    fun getTopicWithCards(id: Int): List<TopicWithCards>

    @Query("SELECT * FROM topic WHERE id = :id")
    fun getById(id: Int): Topic

    @Query("SELECT * FROM topic WHERE categoryId = :id")
    fun getTopicsOfCategory(id: Int): List<Topic>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(topic: Topic)

    @Update
    fun update(topic: Topic)

    @Delete
    fun delete(topic: Topic)

    @Query("DELETE FROM topic")
    fun deleteAll()
}

@Dao
interface CardDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(card: Card)

    @Update
    fun update(card: Card)

    @Delete
    fun delete(card: Card)

    @Query("DELETE FROM card")
    fun deleteAll()

    @Query("SELECT * FROM card")
    fun getAll(): List<Card>

    @Query("SELECT * FROM card JOIN topic ON card.topicId = topic.id JOIN category ON topic.categoryId = category.id WHERE category.name = :name")
    fun getAllCardsOfCategory(name: String): List<Card>

    @Query("SELECT * FROM card WHERE topicId = :id")
    fun getCardsofTopic(id: Int): List<Card>
}

@Dao
interface CardResultDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(cardResult: CardResult)

    @Update
    fun update(cardResult: CardResult)

    @Delete
    fun delete(cardResult: CardResult)

    @Query("SELECT * FROM cardResult")
    fun getAll(): List<CardResult>

    @Query("DELETE FROM cardResult")
    fun deleteAll()

}

@Database(entities = [Category::class, Topic::class, Card::class, CardResult::class], version = 6, autoMigrations = [
        AutoMigration (from = 5, to = 6, spec = AppDatabase.MyAutoMigration::class)
])
abstract class AppDatabase : RoomDatabase() {

    //@DeleteColumn("Topic","title")
    //@RenameColumn("topic", "content", "name")
    class MyAutoMigration : AutoMigrationSpec
    abstract fun categoryDao(): CategoryDao
    abstract fun topicDao(): TopicDao
    abstract fun cardDao(): CardDao
    abstract fun CardResultDao(): CardResultDao
}