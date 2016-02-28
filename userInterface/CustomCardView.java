package com.example.paul.allergytravelcardapp.userInterface;
/**
 * Created by Paul on 5/02/2016.
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.example.paul.alergytravelcardapp.R;
import com.example.paul.allergytravelcardapp.model.Card;
import com.example.paul.allergytravelcardapp.model.CardManager;

/**
 * Created by pjw527 on 04/02/2016.
 */
public class CustomCardView extends LinearLayout {

    protected TextView cardLanguage, cardAllergy;
    protected ImageView flagImage, allergyImage;

    public CustomCardView(Context context) {
        super(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.custom_list_view, this, true);
        cardLanguage = (TextView) findViewById(R.id.languageTextView);
        cardAllergy = (TextView) findViewById(R.id.allergyTextView);
        flagImage = (ImageView) findViewById(R.id.flagImageView);
        allergyImage = (ImageView) findViewById(R.id.allergyImageView);
    }

    public void setCard(Card card) {
        cardLanguage.setText(card.getLanguage());
        //check if long string and shorten to fit screen if in portrait mode, currently only affects sulfer diox.
        if (card.getAllergy().length() > 11 && !MainActivity.wideLayout) {
            String shortName = card.getAllergy().substring(0, Math.min(card.getAllergy().length(), 11)) + ".";
            cardAllergy.setText(shortName);
        } else {
            cardAllergy.setText(card.getAllergy());
        }
        flagImage.setImageResource(CardManager.getResourceID(card.getLanguage()));
        allergyImage.setImageResource(CardManager.getResourceID(card.getAllergy()));
    }
}

