package com.example.paul.allergytravelcardapp.userInterface;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.paul.alergytravelcardapp.R;
import com.example.paul.allergytravelcardapp.model.CardManager;

public class CardActivity extends AppCompatActivity {

    private static final int SWIPE_MIN_DISTANCE = 400;
    private static final int SWIPE_THRESHOLD_VELOCITY = 400;
    protected TextView cardTitleTextView, cardBodyTextView;
    protected ImageView allergyImageView, iconImageView, flagImageView;
    private String language = null;
    private String allergy = null;
    private int cardPos = 0;
    private LinearLayout cardLL;
    private Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card);
        getSupportActionBar().hide();
        context = this;

        //hold the card number so on swiping to the next card an intent can be passed using the next card in the arrayList.
        cardPos = getIntent().getIntExtra(CardManager.cn, -1);
        Log.d("cardPOs = -1", cardPos +"");
        //if a card is viewed from a notification, find the card to allow correct flinging between all cards
        if (cardPos == -1) {
            language = getIntent().getStringExtra(CardManager.ls);
            allergy = getIntent().getStringExtra(CardManager.as);
            // if viewing a card from a notification it will be placed as the first entry in the array, based on its recentlyView attribute
            cardPos = CardManager.getCardPositon(language, allergy);
            Log.d("cardPOs = -1", allergy + language + cardPos);
        } else {
            language = CardListFragment.getCardList().get(cardPos).getLanguage();
            allergy = CardListFragment.getCardList().get(cardPos).getAllergy();
        }

        allergyImageView = (ImageView) findViewById(R.id.allergyImageView);
        allergyImageView.setImageResource(CardManager.getResourceID(allergy));
        iconImageView = (ImageView) findViewById(R.id.iconImageView);
        iconImageView.setImageResource(R.drawable.appl_icon);
        flagImageView = (ImageView) findViewById(R.id.flagImageView);
        flagImageView.setImageResource(CardManager.getResourceID(language));

        cardTitleTextView = (TextView) findViewById(R.id.cardTitleTextView);
        cardTitleTextView.setText(allergy + " Allergy");
        Log.d("bodytext", allergy + language);
        cardBodyTextView = (TextView) findViewById(R.id.cardBodyTextView);
        String bodyText = CardManager.getCardBodyText(allergy, language, this);


        if (bodyText.length() > 120) {
            int textSize = 7500 / bodyText.length();
            Log.d("size", textSize + " " + bodyText.length());
            cardBodyTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
        }
        cardBodyTextView.setText(bodyText);

        final GestureDetector gestureDetectorC = new GestureDetector(this.getApplicationContext(), new GestureListener());

        cardLL = (LinearLayout) findViewById(R.id.cardLL);

        cardBodyTextView = (TextView) findViewById(R.id.cardBodyTextView);

        cardLL.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(final View view, final MotionEvent event) {
                return gestureDetectorC.onTouchEvent(event);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(CardActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
        finish();
        startActivity(intent);
    }

    private class GestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            return super.onSingleTapConfirmed(e);
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            //left to right swype
            if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                if (cardPos > 0) {
                    Intent newCardIntent = new Intent(CardActivity.this, CardActivity.class);
                    newCardIntent.putExtra(CardManager.cn, cardPos - 1);
                    startActivity(newCardIntent);
                    overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
                }
                if (cardPos == 0) {
                    Animation anim = AnimationUtils.loadAnimation(context, android.R.anim.fade_in);
                    anim.setDuration(500);
                    cardLL.startAnimation(anim);
                    new Handler().postDelayed(new Runnable() {
                        public void run() {

                        }

                    }, anim.getDuration());

                }
            }
            if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                int newCardPos = cardPos + 1;
                if (newCardPos < CardListFragment.cardList.size()) {
                    Intent newCardIntent = new Intent(CardActivity.this, CardActivity.class);
                    newCardIntent.putExtra(CardManager.cn, newCardPos);
                    startActivity(newCardIntent);
                    overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);

                }
                if (newCardPos == CardListFragment.cardList.size()) {
                    Animation anim = AnimationUtils.loadAnimation(context, android.R.anim.fade_in);
                    anim.setDuration(500);
                    cardLL.startAnimation(anim);
                    new Handler().postDelayed(new Runnable() {
                        public void run() {

                        }

                    }, anim.getDuration());

                }
            }
            return false;
        }

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

    }


}
