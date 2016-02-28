package com.example.paul.allergytravelcardapp.model;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.Log;

import com.example.paul.alergytravelcardapp.R;
import com.example.paul.allergytravelcardapp.userInterface.CardListFragment;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Class with static methods to provide business logic for Card objects.
 */
public class CardManager {

    //static Strings for naming data to b passed over intents
    public static String ls = "languageSelected";
    public static String as = "allergySelected";
    public static String cn = "cardNumber";

    /**
     * Method to turn a String into required resource ID. utilised to turn a card parameter,
     * e.g. language or allergy, into a resource ID and get the associated image from the res
     * directory.
     *
     * @param string
     * @return
     */
    public static int getResourceID(String string) {
        // int to hold resource id to be returned
        int id = 0;
        if (string != null) {
            //to lowercase
            string = string.toLowerCase();
            //remove whitespace
            string = string.replaceAll("\\s+", "");
            try {
                Class res = R.drawable.class;
                Field field = res.getField(string);
                id = field.getInt(null);
            } catch (Exception e) {
                //if resource is null the apps symbol is displayed as the default (fail gracefully)
                id = R.drawable.appl_icon;
                Log.e("MyTag", "Failure to get drawable id. For " + string, e);
            }
            return id;
        } else {
            //if resource is null the apps symbol is displayed as the default (fail gracefully)
            return R.drawable.allergy_symbol;
        }
    }

    /**
     * Method for getting the current data and converting to int. Utilised as the lastViewed
     * parameter in a Card object to sort objects.
     *
     * @return
     */
    public static int getCurrentDateInt() {
        int date = (int) new Date().getTime() / 1000;
        return date;
    }

    /**
     * Builds a string to output to the user the countries their card can be utilised in.
     *
     * @param countryArray
     * @return
     */
    public static String buildCountryMessage(String countryArray[]) {

        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < countryArray.length; i++) {
            if (i == (countryArray.length - 1)) {
                builder.append("and ");
            }
            builder.append(countryArray[i]);
            if (i < (countryArray.length - 2)) {
                builder.append(", ");
            }
            if (i < (countryArray.length - 1)) {
                builder.append(" ");
            }
        }
        builder.append(".");
        return builder.toString();
    }

    public static List<TypedArray> getMultiTypedArray(Context context, String key) {
        List<TypedArray> array = new ArrayList<>();

        try {
            Class<R.array> res = R.array.class;
            Field field;
            int counter = 0;

            do {
                field = res.getField(key + "_" + counter);
                array.add(context.getResources().obtainTypedArray(field.getInt(null)));
                counter++;
            } while (field != null);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return array;
        }
    }

    public static String getCardBodyText(String allergy, String language, Context context) {
        String cardBody = null;

        int allergyId = context.getResources().getIdentifier("allergy_index", "array", context.getPackageName());
        int languageId = context.getResources().getIdentifier(language, "array", context.getPackageName());
        String[] allergyIndex = context.getResources().getStringArray(allergyId);
        String[] languageArray = context.getResources().getStringArray(languageId);

        int index = -1;
        for (int i = 0; i < allergyIndex.length; i++) {
            if (allergyIndex[i].equals(allergy)) {
                index = i;
                break;
            }
        }
        if (index > -1 && index < languageArray.length) {
            cardBody = languageArray[index];
        }

        return cardBody;
    }

    /**
     * This method provides the requesting context with a string[] of countries that speak Language
     * parameter. Provides the user with a list of counties that a given card can be used based on language.
     *
     * @param language
     * @param context
     * @return
     */
    public static String[] getCountries(String language, Context context) {
        int id = 0;
        String lang = language.toLowerCase();
        id = context.getResources().getIdentifier(lang, "array", context.getPackageName());
        return context.getResources().getStringArray(id);
    }

    /**
     * Method to return the language based on the country parameter.  This is utlised to inform a
     * user when they arrive in a supported country they will be provided a notification that
     * they can make an allergy card in the local language.
     *
     * @param country
     * @param context
     * @return Language String
     */
    public static String getLanguage(String country, Context context) {
        String language = null;

        String[] languagesArray = context.getResources().getStringArray(R.array.language_array);
        for (String l : languagesArray) {

            String[] countries = getCountries(l, context);
            for (String c : countries) {
                if (c.equals(country)) {
                    language = l;
                    break;
                }
            }
        }
        return language;
    }

    public static int getCardPositon(String language, String allergy) {
        int positon = -1;
        for(int i = 0; i < CardListFragment.getCardList().size(); i++ ) {
        Card card = CardListFragment.getCardList().get(i);
            if (card.getLanguage().equals(language) && card.getAllergy().equals(allergy)) {
                positon = i;
                break;
            }
        }
        return positon;
    }
}
