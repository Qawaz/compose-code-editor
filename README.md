# Compose Code Editor (Multiplatform)

## Description

**Compose Code Editor** is a code highlighting / editing library for compose , it does not make use of web view so it renders fast

It also supports kotlin multiplatform and supports Android,JVM & Web at the moment , It will be better if you could get it from
github packages since I use that and post latest versions there and Jitpack might not support multiplatform
The version 2.0.3 is only for Android , Its 3.0.0 and afterwards for multiplatform

## Demo

![Screen Recording (9-11-2021 4-29-35 PM)](https://user-images.githubusercontent.com/42442700/132946529-c0c76bf4-b055-4be9-b89a-48c1b3295f89.gif)

## Setup

You can either get this from github packages or jitpack

#### Step 1. Make sure you have jitpack repository in your build file

```groovy
allprojects {
    repositories {
        maven { url 'https://jitpack.io' }
    }
}
```

#### Step 2. Add the dependency

```groovy
dependencies {
    implementation 'com.github.qawaz:compose-code-editor:2.0.3'
}
```

### Multiplatform Dependency

#### Step 1 : Add the Github Packages Repo

```kotlin

val githubProperties = Properties()
githubProperties.load(FileInputStream(rootProject.file("github.properties")))

allprojects {
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/username/repo")
            credentials {
                username = (githubProperties["gpr.usr"] ?: System.getenv("GPR_USER")).toString()
                password = (githubProperties["gpr.key"] ?: System.getenv("GPR_API_KEY")).toString()
            }
        }
    }
}
```

#### Step 2 : Create Github Properties File

Create `github.properties` file in your project at root level and add two properties (make github personal access token)

```properties
gpr.usr=yourusername
gpr.key=yourgithubpersonalaccesstoken
```

#### Step 3 : Add The Dependency

```kotlin
implementation("com.wakaztahir:codeeditor:3.0.5")
```

## Usage

```kotlin
// Step 1. Declare Language & Code
val language = CodeLang.Kotlin
val code = """             
    package com.wakaztahir.codeeditor
    
    fun main(){
        println("Hello World");
    }
""".trimIndent()

// Step 2. Create Parser & Theme
val parser = remember { PrettifyParser() } // try getting from LocalPrettifyParser.current
var themeState by remember { mutableStateOf(CodeThemeType.Monokai) }
val theme = remember(themeState) { themeState.theme() }
```

### Using Text Composable

```kotlin
// Step 3. Parse Code For Highlighting
val parsedCode = remember {
    parseCodeAsAnnotatedString(
        parser = parser,
        theme = theme,
        lang = language,
        code = code
    )
}

// Step 4. Display In A Text Composable
Text(parsedCode)
```

### Using TextField Composable

```kotlin
var textFieldValue by remember {
  mutableStateOf(
    TextFieldValue(
      annotatedString = parseCodeAsAnnotatedString(
        parser = parser,
        theme = theme,
        lang = language,
        code = code
      )
    )
  )
}

OutlinedTextField(
  modifier = Modifier.fillMaxSize(),
  value = textFieldValue,
  onValueChange = {
    textFieldValue = it.copy(
      annotatedString = parseCodeAsAnnotatedString(
        parser = parser,
        theme = theme,
        lang = language,
        code = it.text
      )
    )
  }
)
```

### Displaying Line Numbers

To display line numbers in the text field we must use a `BasicTextField` since it has a parameter for `onTextLayout`

A basic example can be setup like this , On every text layout a new array is created
which contains top offsets of each line in the `BasicTextField`

```kotlin

val language = CodeLang.Kotlin
val code = """             
    package com.wakaztahir.codeeditor
    
    fun main(){
        println("Hello World");
    }
    """.trimIndent()

val parser = remember { PrettifyParser() }
val themeState by remember { mutableStateOf(CodeThemeType.Default) }
val theme = remember(themeState) { themeState.theme }

fun parse(code: String): AnnotatedString {
    return parseCodeAsAnnotatedString(
        parser = parser,
        theme = theme,
        lang = language,
        code = code
    )
}

var textFieldValue by remember { mutableStateOf(TextFieldValue(parse(code))) }
var lineTops by remember { mutableStateOf(emptyArray<Float>()) }
val density = LocalDensity.current

Row {
    if (lineTops.isNotEmpty()) {
        Box(modifier = Modifier.padding(horizontal = 4.dp)) {
            lineTops.forEachIndexed { index, top ->
                Text(
                    modifier = Modifier.offset(y = with(density) { top.toDp() }),
                    text = index.toString(),
                    color = MaterialTheme.colors.onBackground.copy(.3f)
                )
            }
        }
    }
    BasicTextField(
        modifier = Modifier.fillMaxSize(),
        value = textFieldValue,
        onValueChange = {
            textFieldValue = it.copy(annotatedString = parse(it.text))
        },
        onTextLayout = { result ->
            lineTops = Array(result.lineCount) { result.getLineTop(it) }
        }
    )
}
```

## List of available languages & their extensions

Default (```"default-code"```), HTML (```"default-markup"```) , C/C++/Objective-C (```"c"```, ```"cc"```, ```"cpp"```, ```"cxx"```, ```"cyc"```, ```"m"```),
C# (```"cs"```), Java (```"java"```),Kotlin (```"kt"```) ,Bash (```"bash"```, ```"bsh"```, ```"csh"```, ```"sh"```),
Python (```"cv"```, ```"py"```, ```"python"```), Perl (```"perl"```, ```"pl"```, ```"pm"```),
Ruby (```"rb"```, ```"ruby"```), JavaScript (```"javascript"```, ```"js"```),
CoffeeScript (```"coffee"```), Rust (```"rc"```, ```"rs"```, ```"rust"```), Appollo (```"apollo"```
, ```"agc"```, ```"aea"```), Basic (```"basic"```, ```"cbm"```), Clojure (```"clj"```),
Css (```"css"```), Dart (```"dart"```), Erlang (```"erlang"```, ```"erl"```), Go (```"go"```),
Haskell (```"hs"```), Lisp (```"cl"```, ```"el"```, ```"lisp"```, ```"lsp"```, ```"scm"```
, ```"ss"```, ```"rkt"```), Llvm (```"llvm"```, ```"ll"```), Lua (```"lua"```),
Matlab (```"matlab"```), ML (OCaml, SML, F#, etc) (```"fs"```, ```"ml"```), Mumps (```"mumps"```),
N (```"n"```, ```"nemerle"```), Pascal (```"pascal"```), R (```"r"```, ```"s"```, ```"R"```
, ```"S"```, ```"Splus"```), Rd (```"Rd"```, ```"rd"```), Scala (```"scala"```), SQL (```"sql"```),
Tex (```"latex"```, ```"tex"```), VB (```"vb"```, ```"vbs"```), VHDL (```"vhdl"```, ```"vhd"```),
Tcl (```"tcl"```), Wiki (```"wiki.meta"```), XQuery (```"xq"```, ```"xquery"```), YAML (```"yaml"```
, ```"yml"```), Markdown (```"md"```, ```"markdown"```), formats (```"json"```, ```"xml"```
, ```"proto"```), ```"regex"```

Didn't found yours? Please, open issue to show your interest & I'll try to add this language in next
releases.

## List of available themes

* Default
* [Monokai](http://www.eclipsecolorthemes.org/?view=theme&id=386)

## Issues

* Lack of themes
