package ru.learning.uirush

import android.os.Bundle
import android.view.animation.Animation
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import ru.learning.lastrushcoding.uib.StatsView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        val view = findViewById<StatsView>(R.id.statsView)
        view.data = listOf(
            500F, 500F, 500F, 500F
        )

        val textView = findViewById<TextView>(R.id.label)

        view.startAnimation(
            android.view.animation.AnimationUtils
                .loadAnimation(this, R.anim.animation).apply {
                    setAnimationListener(object: Animation.AnimationListener{
                        override fun onAnimationEnd(p0: Animation?) {
                            textView.text = "onAnimationStart"
                        }

                        override fun onAnimationRepeat(p0: Animation?) {
                            textView.text = "onAnimationEnd"
                        }

                        override fun onAnimationStart(p0: Animation?) {
                            textView.text = "onAnimationRepeat"
                        }

                    })
                }
        )
    }
}