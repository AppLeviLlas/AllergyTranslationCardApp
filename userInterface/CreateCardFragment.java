package com.example.paul.allergytravelcardapp.userInterface;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Spinner;
import com.example.paul.alergytravelcardapp.R;
import com.example.paul.allergytravelcardapp.model.Card;
import com.example.paul.allergytravelcardapp.model.CardDBOpenHelper;
import com.example.paul.allergytravelcardapp.model.CardManager;

import java.util.Collections;

/**
 * Created by Paul on 12/02/2016.
 */
public class CreateCardFragment extends Fragment {

    String[] allergyArray;
    String[] languageArray;
    Button createCardButton;
    Spinner allergySpinner;
    Spinner languageSpinner;
    SQLiteDatabase db;
    CardDBOpenHelper cardDBOpenHelper;
    private CreateCardListener listener = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_create_card_view, container, false);
        createCardButton = (Button) view.findViewById(R.id.createCardButton);
        languageSpinner = (Spinner) view.findViewById(R.id.languageSpinner);
        allergySpinner = (Spinner) view.findViewById(R.id.allergySpinner);
        cardDBOpenHelper = new CardDBOpenHelper(view.getContext());
        languageArray = getResources().getStringArray(R.array.language_array);
        languageSpinner.setAdapter(new CustomSpinnerViewAdapter(getActivity(), R.layout.spinner_view, languageArray));
        allergyArray = getResources().getStringArray(R.array.allergy_array);
        allergySpinner.setAdapter(new CustomSpinnerViewAdapter(getActivity(), R.layout.spinner_view, allergyArray));

        createCardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createNewCard(languageSpinner.getSelectedItem().toString(), allergySpinner.getSelectedItem().toString());
            }
        });
        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (!(context instanceof CreateCardListener)) {
            throw new IllegalStateException(
                    "Container activity must implement " +
                            "the FragmentListener interface.");
        }
        listener = (CreateCardListener) context;
    }

    public void createNewCard(String language, String allergy) {
        Card newCard = new Card(language, allergy, CardManager.getCurrentDateInt());
        db = cardDBOpenHelper.getReadableDatabase();

        //check card doesn't already exist, if it does, delete from DB so the new card's date sorts it to the top.
        int sameCard = cardDBOpenHelper.deleteCardLA(newCard);
//        if (sameCard > 0) {
//            cardDBOpenHelper.deleteCardID(sameCard);
//        }
        cardDBOpenHelper.addCard(db, newCard);

        CardListFragment.getCardList().clear();
        CardListFragment.getCardList().addAll(cardDBOpenHelper.getAllCards());
        Collections.sort(CardListFragment.getCardList());

        Intent newCardIntent = new Intent(getActivity(), CardActivity.class);
        newCardIntent.putExtra(CardManager.ls, languageSpinner.getSelectedItem().toString());
        newCardIntent.putExtra(CardManager.as, allergySpinner.getSelectedItem().toString());

        startActivity(newCardIntent);
        getActivity().overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
    }

    public interface CreateCardListener {
        void onCreateCardTouched();
    }
}






