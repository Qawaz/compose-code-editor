package com.wakaztahir.android

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.wakaztahir.common.draggableItemsIndexed
import com.wakaztahir.common.draggableStateListOf

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                var list = draggableStateListOf("sdf", "sdf", "sdf", "sdfsdfasdf", "dslkfjsalkdfj", "slkdjflkds")

                var animationsEnabled by remember { mutableStateOf(true) }

                val scope = rememberCoroutineScope()

                LazyColumn {
                    draggableItemsIndexed(
                        list,
                        animationsEnabled = animationsEnabled,
                        updateAnimationsEnabled = {
                            animationsEnabled = it
                        },
                        scope = scope
                    ) { index, item ->
                        Text(
                            modifier = Modifier.dragger(),
                            text = "$index $item"
                        )
                    }
                }
            }
        }
    }
}