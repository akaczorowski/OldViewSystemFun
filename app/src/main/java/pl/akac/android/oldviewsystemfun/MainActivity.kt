package pl.akac.android.oldviewsystemfun

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModels()
    private lateinit var itemAdapter: ItemAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        itemAdapter = ItemAdapter(itemClickListener = {
            viewModel.onAction(Action.ItemClick(it))
        })

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = itemAdapter
        recyclerView.setHasFixedSize(true)

        // #1
        val dividerItemDecoration = DividerItemDecoration(this, LinearLayoutManager.VERTICAL)
        dividerItemDecoration.setDrawable(ContextCompat.getDrawable(this, R.drawable.divider_drawable))
        recyclerView.addItemDecoration(dividerItemDecoration)

        // #2
        recyclerView.addItemDecoration(OffsetDividerItemDecoration())

//        recyclerView.itemAnimator = FadeItemAnimator()




        // ### coroutines start
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.state.collect {
                    submitList(it)
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.sideEffect.collect {
                    handleSideEffect(it)
                }
            }
        }
        // ### coroutines end

        findViewById<Button>(R.id.button).setOnClickListener {
            viewModel.onAction(Action.AddMoreItems)
        }
    }

    private fun submitList(state: UiState) {
        itemAdapter.submitList(state.list)
    }

    private fun handleSideEffect(
        effect: SideEffect,
    ) {
        when (effect) {
            SideEffect.NotifyUserNewItemAdded -> {
                Toast.makeText(
                    this@MainActivity, "Item Added!", Toast.LENGTH_SHORT
                ).show()

                findViewById<RecyclerView>(R.id.recyclerView).smoothScrollToPosition(0)
            }

            is SideEffect.ItemClicked -> Toast.makeText(
                this@MainActivity, "id: ${effect.data.id}, data: ${effect.data.title}", Toast.LENGTH_SHORT
            ).show()
        }
    }
}