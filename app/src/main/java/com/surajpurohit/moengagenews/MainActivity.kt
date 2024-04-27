package com.surajpurohit.moengagenews

import android.os.Build
import android.os.Bundle
import android.view.ContextMenu
import android.view.MenuItem
import android.view.View
import android.widget.SearchView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.surajpurohit.moengagenews.adapter.NewsAdapter
import com.surajpurohit.moengagenews.databinding.ActivityMainBinding
import com.surajpurohit.moengagenews.model.News
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class MainActivity : AppCompatActivity() {
    private lateinit var newsArrayList: ArrayList<News>
    private lateinit var adapter: NewsAdapter
    private val api =
        "https://candidate-test-data-moengage.s3.amazonaws.com/Android/news-api-feed/staticResponse.json"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        //View Binding
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        //Disable dark mode
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        newsArrayList = ArrayList()
        binding.newsList.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        adapter = NewsAdapter(newsArrayList, this)
        binding.newsList.adapter = adapter

        fetchDataFromApi(api)

        registerForContextMenu(binding.filterNewsIcon)

        binding.filterNewsIcon.setOnClickListener { v ->
            Toast.makeText(
                this,
                "Long press for filter menu!",
                Toast.LENGTH_SHORT
            ).show()
        }

        // searching in list
        binding.searchNews.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    adapter.filter(query)
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText != null) {
                    adapter.filter(newText)
                }
                return false
            }
        })

        adapter.updateData(newsArrayList)
    }

    override fun onCreateContextMenu(menu: ContextMenu?, v: View?, menuInfo: ContextMenu.ContextMenuInfo?) {
        super.onCreateContextMenu(menu, v, menuInfo)
        val inflater = menuInflater
        inflater.inflate(R.menu.filter_menu, menu)
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        val itemId = item.itemId
        when(itemId) {
            R.id.new_to_old -> {
                sortNewsArticles("new_to_old")
                return true
            }

            R.id.old_to_new -> {
                sortNewsArticles("old_to_new")
                return true
            }
        }
        return super.onContextItemSelected(item)
    }

    private fun fetchDataFromApi(api: String) {
        val apiUrl = api

        Thread {
            val jsonString = try {
                val url = URL(apiUrl)
                val conn = url.openConnection() as HttpURLConnection
                conn.requestMethod = "GET"


                conn.connect()

                if (conn.responseCode != HttpURLConnection.HTTP_OK) {
                    throw RuntimeException("Error fetching data: ${conn.responseCode}")
                }

                val inputStream = conn.inputStream
                val responseString = inputStream.bufferedReader().use { it.readText() }
                inputStream.close()
                conn.disconnect()
                responseString
            } catch (e: Exception) {
                e.printStackTrace()
                return@Thread
            }

            runOnUiThread {
                parseApiResponse(jsonString)
            }
        }.start()
    }

    private fun parseApiResponse(jsonString: String) {
        val jsonObject =
            JSONObject(jsonString)

        if (jsonObject.has("status") && jsonObject.getString("status") == "ok") {
            val articlesArray = jsonObject.getJSONArray("articles")

            newsArrayList.clear()
            for (i in 0 until articlesArray.length()) {
                val articleObject = articlesArray.getJSONObject(i)

                // Extract News source name
                val sourceObject = articleObject.getJSONObject("source")
                val newsSource = sourceObject.getString("name")

                // Get news info
                val newsHeadline = articleObject.getString("title")
                val newsDescription = articleObject.getString("description")
                val newsAuthor = articleObject.getString("author")
                val newsImage = articleObject.getString("urlToImage")
                val newsDate = articleObject.getString("publishedAt")
                val newsUrl = articleObject.getString("url")

                // Add data to the list
                if (::newsArrayList.isInitialized) {
                    newsArrayList.add(
                        (News(
                            newsSource,
                            newsDate,
                            newsHeadline,
                            newsImage,
                            newsAuthor,
                            newsDescription,
                            newsUrl
                        ))
                    )
                }
            }
        } else {
            // Handle cases where "status" is not "ok" or the structure is different
            //println("Unexpected JSON response format")
        }

        // Update the RecyclerView adapter
        adapter.notifyDataSetChanged()
    }

    //Sorting news articles based on old-to-new and new-to-old
    private fun sortNewsArticles(sortOrder: String) {
        when (sortOrder) {
            "new_to_old" -> newsArrayList.sortWith(compareByDescending { it.newsDate })
            "old_to_new" -> newsArrayList.sortWith(compareBy { it.newsDate })
            else -> throw IllegalArgumentException("Invalid sort order: $sortOrder")
        }
        adapter.notifyDataSetChanged()
    }
}