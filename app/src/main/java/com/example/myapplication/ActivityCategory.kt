package com.example.myapplication

import android.content.Context
import android.content.Intent
import android.graphics.Rect
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.annotation.DimenRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import java.lang.Math.abs

class ActivityCategory : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category)
        val db = Room.databaseBuilder(this, AppDatabase::class.java, "smartBinderDB").allowMainThreadQueries().build()
        val viewPager = findViewById<ViewPager2>(R.id.viewPager)
        val activeTopic = intent.getSerializableExtra("topic") as Topic
        val topics = db.topicDao().getTopicsOfCategory(activeTopic.categoryId)
        val topicsWithCards = db.topicDao().getTopicWithCards(activeTopic.categoryId)
        val activeTopicId = topics.indexOf(activeTopic)

        setupViewPager(viewPager, topicsWithCards, activeTopicId)

        val btnAdd = findViewById<Button>(R.id.btnAddCard)
        btnAdd.setOnClickListener {
            val currTopicPos = viewPager.currentItem
            val currTopic = topics[currTopicPos]
            val intent = Intent(this, ActivityAddCard::class.java)
            intent.putExtra("topic", currTopic)
            startActivity(intent)
        }
    }

    private fun setupViewPager(viewPager: ViewPager2, topicsWithCards: List<TopicsWithCards>, activeTopicId: Int) {
        viewPager.adapter = TopicPagerAdapter(supportFragmentManager, lifecycle, topicsWithCards)
        viewPager.currentItem = activeTopicId
        viewPager.offscreenPageLimit = 1

        val nextItemVisiblePx = resources.getDimension(R.dimen.viewpager_next_item_visible)
        val currentItemHorizontalMarginPx = resources.getDimension(R.dimen.viewpager_current_item_horizontal_margin)
        val pageTranslationX = nextItemVisiblePx + currentItemHorizontalMarginPx
        val pageTransformer = ViewPager2.PageTransformer { page: View, position: Float ->
            page.translationX = -pageTranslationX * position
            page.alpha = 0.25f + (1 - kotlin.math.abs(position))
        }
        viewPager.setPageTransformer(pageTransformer)
        val itemDecoration = HorizontalMarginItemDecoration(this, R.dimen.viewpager_current_item_horizontal_margin
        )
        viewPager.addItemDecoration(itemDecoration)
    }
}

