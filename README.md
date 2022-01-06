# Compose Code Editor (Android)

## Description

**Compose Code Editor** is a code highlighting / editing library for compose , it does not make use of web view so it renders fast

Its also supports kotlin multiplatform and supports Android & JVM at the moment , It will be better if you could get it from
github packages since I use that and post latest versions there and Jitpack might not support multiplatform
The version 2.0.2 is only for Android , Its 3.0.0 and afterwards for multiplatform

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
    implementation 'com.github.timeline-notes:compose-code-editor:2.0.2'
}
```

## Usage

```kotlin
// Step 1. Declare Language & Code
val language = CodeLang.Java
val code = """             
    package com.wakaztahir.codeeditor
    
    public static void main(String[] args){
        System.out.println("Hello World")
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
    // Same Steps From Above
    val language = CodeLang.Java
    val code = """             
        package com.wakaztahir.codeeditor
        
        public static void main(String[] args){
            System.out.println("Hello World")
        }
        """.trimIndent()
    
    val parser = remember { PrettifyParser() }
    val themeState by remember { mutableStateOf(CodeThemeType.Default) }
    val theme = remember(themeState) { themeState.theme() }

    // Creating Text Field Value , Prasing Initial Code As Annotated String
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
    
    // Displaying In A Text Field
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

## List of available languages & their extensions

Default (```"default-code"```), HTML (```"default-markup"```) , C/C++/Objective-C (```"c"```, ```"cc"```, ```"cpp"```, ```"cxx"```, ```"cyc"```, ```"m"```),
C# (```"cs"```), Java (```"java"```), Bash (```"bash"```, ```"bsh"```, ```"csh"```, ```"sh"```),
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

* Does not support kotlin yet , but basic syntax highlighting can be achieved by using another
  language
* Lack of themes
* Everytime user types code in a text field , all the code is parsed again rather than only the
  changed lines which makes it a little inefficient , This is due to compose not supporting
  multiline text editing yet , so it will be fixed in future
