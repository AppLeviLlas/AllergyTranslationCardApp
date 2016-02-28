package com.example.paul.allergytravelcardapp.userInterface;

import android.animation.LayoutTransition;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.view.ContextMenu;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.example.paul.alergytravelcardapp.R;
import com.example.paul.allergytravelcardapp.model.Card;
import com.example.paul.allergytravelcardapp.model.CardDBOpenHelper;
import com.example.paul.allergytravelcardapp.model.CardManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Paul on 12/02/2016.
 */
public class CardListFragment extends Fragment {

    private static final int SWIPE_MIN_DISTANCE = 200;
    private static final int SWIPE_THRESHOLD_VELOCITY = 200;
    public static List<Card> cardList = new ArrayList<Card>();
    static Boolean firstTime = true;
    protected ListView cardListView;
    protected CustomListViewAdaptor cardListViewAdaptor;
    protected int itemPosition;
    SQLiteDatabase db;
    CardDBOpenHelper cardDBOpenHelper;
    Context context;
    private Handler handler;
    private CardListListener listener = null;
    private NotificationManager mNotificationManager;

    public static List<Card> getCardList() {
        return cardList;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_card_list, container, false);

        container = new LinearLayout(view.getContext());
        LayoutTransition layoutTransition = new LayoutTransition();
        container.setLayoutTransition(layoutTransition);
        context = this.getContext();

        cardDBOpenHelper = new CardDBOpenHelper(view.getContext());
        db = cardDBOpenHelper.getReadableDatabase();

        if (firstTime) {
            cardList.addAll(cardDBOpenHelper.getAllCards());
            Collections.sort(cardList);
            firstTime = false;
        }
        cardListView = (ListView) view.findViewById(R.id.listView);
        registerForContextMenu(cardListView);
        cardListViewAdaptor = new CustomListViewAdaptor(view.getContext(), cardList);
        cardListView.setAdapter(cardListViewAdaptor);

        registerForContextMenu(cardListView);

        //create an instance of the handler for non-UI bound service
        handler = new Handler();

        final GestureDetector gestureDetector = new GestureDetector(this.getContext(), new GestureListener());
        cardListView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(final View view, final MotionEvent event) {
                itemPosition = cardListView.getSelectedItemPosition();
                return gestureDetector.onTouchEvent(event);
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
        if (!(context instanceof CardListListener)) {
            throw new IllegalStateException("Container activity must implement the FragmentListener interface.");
        }
        listener = (CardListListener) context;
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        final int position = info.position;

        switch (item.getItemId()) {

            case R.id.view_card:
                Card cardView = cardList.get(position);
                Intent newCardIntent = new Intent(getActivity(), CardActivity.class);
                newCardIntent.putExtra(CardManager.ls, cardView.getLanguage());
                newCardIntent.putExtra(CardManager.as, cardView.getAllergy());
                newCardIntent.putExtra(CardManager.cn, cardList.indexOf(cardView));
                startActivity(newCardIntent);
                return true;

            case R.id.delete_card:
                final Card cardDelete = cardList.get(position);
                deleteCard(cardDelete);
                Toast.makeText(getActivity(), "Card deleted", Toast.LENGTH_SHORT).show();
                return true;

            case R.id.move_card_bottom:
                Card cardBottom = cardList.get(position);
                cardBottom.setLastViewed(cardList.get(cardList.size() - 1).getLastViewed() - 1);
                Collections.sort(cardList);
                cardListViewAdaptor.notifyDataSetChanged();
                return true;

            case R.id.move_card_top:
                Card cardTop = cardList.get(position);
                cardTop.setLastViewed(CardManager.getCurrentDateInt());
                Collections.sort(cardList);
                cardListViewAdaptor.notifyDataSetChanged();
                return true;

            case R.id.show_countries:
                final Card cardShowCountries = cardList.get(position);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String[] countryArray = CardManager.getCountries(cardShowCountries.getLanguage(), context);
                        final String message = "Your " + cardShowCountries.getLanguage() + " " + cardShowCountries.getAllergy() + " Allergy Card can be used in " + CardManager.buildCountryMessage(countryArray);
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                // ... as such, this is also run in the UI thread
                                AlertDialog alertDialog = new AlertDialog.Builder(context).create();
                                alertDialog.setTitle(cardShowCountries.getLanguage() + " " + cardShowCountries.getAllergy() + " Allergy Card.");
                                alertDialog.setMessage(message);
                                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "CLOSE",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        });
                                alertDialog.show();
                            }
                        });
                    }
                }).start();
                return true;

            case R.id.create_notification:
                Card notificationCard = cardList.get(position);

                NotificationCompat.Builder mBuilder =
                        new NotificationCompat.Builder(getActivity().getApplicationContext())
                                .setSmallIcon(R.drawable.appl_icon_2)
                                .setContentTitle(notificationCard.getLanguage() + " " + notificationCard.getAllergy() + " Allergy Card.")
                                .setContentText("Touch to open this allergy card.");
                // Creates an explicit intent for the Card Activity and passes the language and allergy to the
                Intent notificationIntent = new Intent(getActivity().getApplicationContext(), CardActivity.class);
                //The notification intent needs to pass the language and allergy fields to the card activity incase the card
                //is deleted prior to viewing.
                notificationIntent.putExtra(CardManager.ls, notificationCard.getLanguage());
                notificationIntent.putExtra(CardManager.as, notificationCard.getAllergy());
                //notificationIntent.putExtra(CardManager.cn, notificationCard.getDbID());
                // The stack builder object will contain an artificial back stack for the
                // started Activity.
                // This ensures that navigating backward from the Activity leads out of
                // your application to the Home screen.
                TaskStackBuilder stackBuilder = TaskStackBuilder.create(getActivity().getApplicationContext());
                // Adds the back stack for the Intent (but not the Intent itself)
                stackBuilder.addParentStack(CardActivity.class);
                // Adds the Intent that starts the Activity to the top of the stack
                stackBuilder.addNextIntent(notificationIntent);
                PendingIntent resultPendingIntent =
                        stackBuilder.getPendingIntent(notificationCard.getDbID(),
                                PendingIntent.FLAG_UPDATE_CURRENT
                        );
                mBuilder.setContentIntent(resultPendingIntent);
                NotificationManager mNotificationManager =
                        (NotificationManager) getActivity().getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
                // mId allows you to update the notification later on.
                mNotificationManager.notify(notificationCard.getDbID(), mBuilder.build());
            default:
                return super.onContextItemSelected(item);
        }
    }

    public void deleteCard(Card card) {
        cardDBOpenHelper.deleteCardID(card.getDbID());
        cardList.remove(card);
        cardListViewAdaptor.notifyDataSetChanged();
        mNotificationManager = (NotificationManager) getActivity().getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancel(card.getDbID());
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo menuInfo) {
        getActivity().getMenuInflater().inflate(R.menu.long_click_menu, menu);
    }

    public interface CardListListener {
        void onCardListTouched();
    }

    private class GestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            final int position = cardListView.pointToPosition((int) e.getX(), (int) e.getY());
            if (position != -1) {
                Card card = cardList.get(position);
                Intent newCardIntent = new Intent(getActivity(), CardActivity.class);
                newCardIntent.putExtra(CardManager.cn, position);
                startActivity(newCardIntent);
                getActivity().overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
                card.setLastViewed(CardManager.getCurrentDateInt());
                Collections.sort(CardListFragment.getCardList());
            }
            return super.onSingleTapConfirmed(e);
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            //get card position id
            final int position = cardListView.pointToPosition((int) e1.getX(), (int) e1.getY());
            if (position != -1 && position < cardList.size()) {
                //left to right swype
                if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                    Animation anim = AnimationUtils.loadAnimation(getActivity(), android.R.anim.slide_out_right);
                    anim.setDuration(300);
                    final Card card = cardList.get(position);
                    cardListView.getChildAt(position).startAnimation(anim);
                    new Handler().postDelayed(new Runnable() {
                        public void run() {
                            Toast.makeText(getActivity(), "Card deleted.", Toast.LENGTH_SHORT).show();
                            deleteCard(card);
                        }
                    }, anim.getDuration());
                }
            }
            return false;
        }
    }
}




