package com.example.bankboost;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;

import androidx.annotation.Nullable;

public class TypingAnimation extends androidx.appcompat.widget.AppCompatTextView {
    private CharSequence myText; // Stores the text to be animated
    private int myIndex; // Keeps track of the current index while animating
    private long myDelay = 100; // Delay between each character animation

    public TypingAnimation(Context context) {
        super(context);
    }

    public TypingAnimation(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    private final Handler myHandler = new Handler(); // Handler to post delayed runnables
    private final Runnable characterAdder = new Runnable() {
        @Override
        public void run() {
            setText(myText.subSequence(0, myIndex++)); // Set the text up to the current index
            if (myIndex <= myText.length()) {
                myHandler.postDelayed(characterAdder, myDelay); // If not reached the end, schedule the next character animation
            }
        }
    };

    public void animateText(CharSequence myTxt) {
        myText = myTxt; // Set the text to be animated
        myIndex = 0; // Reset the index

        setText(""); // Clear the text before starting animation

        myHandler.removeCallbacks(characterAdder); // Remove any existing callbacks
        myHandler.postDelayed(characterAdder, myDelay); // Start the animation with the specified delay
    }

    public void setCharacterAdder(long n) {
        myDelay = n; // Set the delay between character animations
    }
}