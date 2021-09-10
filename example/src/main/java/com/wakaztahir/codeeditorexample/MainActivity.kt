package com.wakaztahir.codeeditorexample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.TextField
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import com.wakaztahir.codeeditor.highlight.prettify.PrettifyParser
import com.wakaztahir.codeeditorexample.theme.CodeEditorTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {

            CodeEditorTheme {

                val language = "javascript"
                val code = """package io.github.kbiakov.codeviewexample;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import org.jetbrains.annotations.NotNull;

import com.wakaztahir.codeeditor.CodeView;
import com.wakaztahir.codeeditor.OnCodeLineClickListener;
import io.github.kbiakov.codeview.adapters.CodeWithDiffsAdapter;
import io.github.kbiakov.codeview.adapters.Options;
import io.github.kbiakov.codeview.highlight.ColorTheme;
import io.github.kbiakov.codeview.highlight.ColorThemeData;
import io.github.kbiakov.codeview.highlight.Font;
import io.github.kbiakov.codeview.highlight.FontCache;
import com.wakaztahir.codeeditor.views.DiffModel;

public class ListingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listings);

        final CodeView codeView = (CodeView) findViewById(R.id.code_view);

        /*
         * 1: set code content
         */

        // auto language recognition
        codeView.setCode(getString(R.string.listing_js));

        // specify language for code listing
        codeView.setCode(getString(R.string.listing_py), "py");    }
}""".trimIndent()

                var result by remember {
                    mutableStateOf(TextFieldValue(code))
                }

                val parser = remember { PrettifyParser() }

                val theme = remember { MonokaiTheme() }

                TextField(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(color = MaterialTheme.colors.background),
                    value = result,
                    onValueChange = {
                        result = it.copy(
                            annotatedString = parser.parse(language, it.text)
                                .toAnnotatedString(theme, it.text)
                        )
                    }
                )
            }
        }
    }
}