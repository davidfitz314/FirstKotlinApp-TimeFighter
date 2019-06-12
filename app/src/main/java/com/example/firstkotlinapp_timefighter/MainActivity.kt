package com.example.firstkotlinapp_timefighter

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import org.w3c.dom.Text

class MainActivity : AppCompatActivity() {
    internal val TAG = MainActivity::class.java.simpleName

    internal lateinit var tapMeButton: Button
    internal lateinit var gameScoreTextView: TextView
    internal lateinit var gameTimerTextView: TextView
    internal var score: Int = 0
    internal val initialCountDown: Long = 60000
    internal val countDownInterval: Long = 1000
    internal var gameStarted = false
    internal lateinit var countDownTimer: CountDownTimer
    internal var timeLeftOnTimer: Long = 60000

    companion object {
        private val SCORE_KEY = "SCORE_KEY"
        private val TIME_LEFT_KEY = "TIME_LEFT_KEY"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tapMeButton = findViewById<Button>(R.id.tapMeButton)
        gameScoreTextView = findViewById<TextView>(R.id.scoreView)
        gameTimerTextView = findViewById<TextView>(R.id.timeTextView)

        Log.d(TAG, "On Create, score is: $score")

        if (savedInstanceState != null){
            score = savedInstanceState.getInt(SCORE_KEY)
            timeLeftOnTimer = savedInstanceState.getLong(TIME_LEFT_KEY)
            restoreGame()
        } else {
            resetGame()
        }

        tapMeButton.setOnClickListener { view ->
            val bounceAnimation = AnimationUtils.loadAnimation(this, R.anim.bounce)
            view.startAnimation(bounceAnimation)
            incrementScore()
        }
    }

    private fun restoreGame() {
        gameScoreTextView.text = getString(R.string.score_textview_text, score.toString())
        val restoredTime = timeLeftOnTimer / 1000
        timeTextView.text = getString(R.string.time_textview_text, restoredTime.toString())

        countDownTimer = object : CountDownTimer(timeLeftOnTimer, countDownInterval){
            override fun onTick(millisUntilFinished: Long) {
                timeLeftOnTimer = millisUntilFinished
                var timeLeft = millisUntilFinished / 1000
                timeTextView.text = getString(R.string.time_textview_text, timeLeft.toString())
            }

            override fun onFinish() {
                gameEnded()
            }
        }

        countDownTimer.start()
        gameStarted = true

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(SCORE_KEY, score)
        outState.putLong(TIME_LEFT_KEY, timeLeftOnTimer)
        countDownTimer.cancel()
        Log.d(TAG, "onSaveInstanceState: score: $score, timeleft: $timeLeftOnTimer")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy called.")
    }

    private fun resetGame() {
        score = 0;
        gameScoreTextView.text = getString(R.string.score_textview_text, score.toString())
        val initialTimeLeft = initialCountDown / 1000
        gameTimerTextView.text = getString(R.string.time_textview_text, initialTimeLeft.toString())

        countDownTimer = object: CountDownTimer(initialCountDown, countDownInterval) {
            override fun onTick(millisUntilFinished: Long) {
                //save left over time
                timeLeftOnTimer = millisUntilFinished
                //otherwise just show the current time
                val timeLeft = millisUntilFinished / 1000
                gameTimerTextView.text = getString(R.string.time_textview_text, timeLeft.toString())
            }

            override fun onFinish() {
                gameEnded()
            }
        }

        gameStarted = false
    }

    private fun startGame() {
        countDownTimer.start()
        gameStarted = true;
    }

    private fun gameEnded() {
        Toast.makeText(this, getString(R.string.game_over_message, score.toString()), Toast.LENGTH_LONG).show()
        resetGame()
    }

    private fun incrementScore() {
        if (!gameStarted){
            startGame()
        }
        score++
        val newScore = getString(R.string.score_textview_text, score.toString())
        gameScoreTextView.text = newScore
    }


}
