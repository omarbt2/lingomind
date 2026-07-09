package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        ExerciseEntity::class,
        UserProgressEntity::class,
        WrongAnswerEntity::class,
        WritingSubmissionEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun lingoMindDao(): LingoMindDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "lingomind_database"
                )
                    .addCallback(DatabaseCallback(context))
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class DatabaseCallback(
        private val context: Context
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                CoroutineScope(Dispatchers.IO).launch {
                    val dao = database.lingoMindDao()
                    
                    // Pre-populate initial User Progress
                    dao.insertUserProgress(UserProgressEntity())

                    // Pre-populate initial Exercises for Lesson 1 (Chapter 1, Unit 1)
                    val initialExercises = listOf(
                        ExerciseEntity(
                            id = 101,
                            lessonId = 1,
                            exerciseType = "mcq",
                            orderInLesson = 1,
                            promptAr = "اختر المعنى الصحيح لـ 'إعلان':",
                            promptEn = "Choose the correct meaning",
                            contentJson = """{"options":["yummy","app","advertisement"],"correct_index":2}""",
                            correctAnswer = "advertisement",
                            explanationAr = "الكلمة الصحيحة هي 'advertisement' وتعني إعلان. بينما 'yummy' تعني لذيذ و 'app' تعني تطبيق الهاتف.",
                            difficulty = "easy",
                            skillTags = "vocabulary"
                        ),
                        ExerciseEntity(
                            id = 102,
                            lessonId = 1,
                            exerciseType = "fill_blank",
                            orderInLesson = 2,
                            promptAr = "املأ الفراغ بالكلمة المناسبة:",
                            promptEn = "Fill in the blank",
                            contentJson = """{"sentence":"Yes, ___ east, and you'll see the bank on the right.","word_bank":["go","tell","win"]}""",
                            correctAnswer = "go",
                            explanationAr = "نقول 'go east' أي اتجه شرقًا، وهي الصياغة الصحيحة لإعطاء التعليمات والاتجاهات.",
                            difficulty = "medium",
                            skillTags = "directions,imperatives"
                        ),
                        ExerciseEntity(
                            id = 103,
                            lessonId = 1,
                            exerciseType = "reorder",
                            orderInLesson = 3,
                            promptAr = "رتّب الكلمات لتكوين الترجمة الصحيحة لـ 'I'm renting a house.':",
                            promptEn = "Reorder words",
                            contentJson = """{"shuffled_words":["متحف","أستأجر","غاليا","بيتا","أن","منزلك"]}""",
                            correctAnswer = "أستأجر بيتا",
                            explanationAr = "الترجمة الصحيحة والمنطقية للجملة هي 'أستأجر بيتا'. الكلمات الأخرى مشتتة.",
                            difficulty = "medium",
                            skillTags = "grammar,translation"
                        ),
                        ExerciseEntity(
                            id = 104,
                            lessonId = 1,
                            exerciseType = "transform",
                            orderInLesson = 4,
                            promptAr = "حوّل الجملة التالية إلى صيغة النفي:",
                            promptEn = "Make the sentence negative",
                            contentJson = """{"original_sentence":"I go to school.","instruction_ar":"حوّل الجملة إلى صيغة النفي"}""",
                            correctAnswer = "I do not go to school",
                            explanationAr = "لنفي الفعل المضارع البسيط البسيط للضمير I نضع 'do not' أو الاختصار 'don't' قبل المصدر: 'I do not go to school'.",
                            difficulty = "hard",
                            skillTags = "negation,present_simple"
                        ),
                        ExerciseEntity(
                            id = 105,
                            lessonId = 1,
                            exerciseType = "free_writing",
                            orderInLesson = 5,
                            promptAr = "اكتب عن يومك المفضل باللغة الإنجليزية (بحد أدنى 20 كلمة):",
                            promptEn = "Write about your favorite day",
                            contentJson = """{"min_words":20,"topic_ar":"اكتب عن يومك المفضل"}""",
                            correctAnswer = null,
                            explanationAr = "لقد أتممت تمرين الكتابة الحرة! سيتم إرساله للتقييم الآلي بالذكاء الاصطناعي وتزويدك بالنتيجة والملاحظات.",
                            difficulty = "hard",
                            skillTags = "writing,free_expression"
                        )
                    )
                    dao.insertExercises(initialExercises)
                }
            }
        }
    }
}
