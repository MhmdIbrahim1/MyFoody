package com.example.myfoody

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp // This annotation is used to tell the compiler that this is the application class
class MyApplication: Application(){
}