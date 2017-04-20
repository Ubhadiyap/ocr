package com.eneo.ocr;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.eneo.ocr.Model.MyShortcuts;

import org.litepal.tablemanager.Connector;

import java.util.ArrayList;
import java.util.List;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardGridArrayAdapter;
import it.gmariotti.cardslib.library.internal.CardHeader;
import it.gmariotti.cardslib.library.internal.CardThumbnail;
import it.gmariotti.cardslib.library.internal.base.BaseCard;
import it.gmariotti.cardslib.library.view.CardGridView;

/**
 * Created by stephineosoro on 09/09/16.
 */
public class CapturedList extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawer;
    static CardGridArrayAdapter mCardArrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capturedlist);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Eneo");



        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        getSqlite("datalocal");


        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        FloatingActionButton floatingActionButton = (FloatingActionButton) findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), MainActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {
        int id = menuItem.getItemId();

        if (id == R.id.nav_manage) {
            Intent intent = new Intent(this, CapturedList.class);
            startActivity(intent);

        }

//        if (id == R.id.nav_slideshow) {
           /* Intent intent = new Intent(this, AdminLogin.class);
            startActivity(intent);
//            finish();

        } else if (id == R.id.nav_manage) {
            Intent intent = new Intent(this, Favorites.class);
            startActivity(intent);

        } else if (id == R.id.wallet) {
            Intent intent = new Intent(this, Wallet.class);
            startActivity(intent);

        }
*/
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    public class GplayGridCard extends Card {

        protected TextView mTitle;
        protected TextView mSecondaryTitle;
        protected String headerTitle, zone;
        protected String secondaryTitle;

        public GplayGridCard(Context context) {
            super(context, R.layout.inner_content_detail);
        }


        public GplayGridCard(Context context, int innerLayout) {
            super(context, innerLayout);
        }

        private void init() {

            CardHeader header = new CardHeader(getContext());
            header.setButtonOverflowVisible(true);
            header.setTitle(headerTitle);
            header.setPopupMenu(R.menu.popupmain, new CardHeader.OnClickCardHeaderPopupMenuListener() {
                @Override
                public void onMenuItemClick(BaseCard card, MenuItem item) {
//                    Toast.makeText(getContext(), "Item " + item.getTitle(), Toast.LENGTH_SHORT).show();
                    String selected = card.getId();

                    /*
                    Intent intent = new Intent(getBaseContext(), CapturedList.class);
                    intent.putExtra("id", selected);
                    startActivity(intent);*/
//                    ID = card.getId();
                }
            });

            addCardHeader(header);
//            Log.e("URL", url);




          /*  OnCardClickListener clickListener = new OnCardClickListener() {
                @Override
                public void onClick(Card card, View view) {
                    //Do something
                }
            };

            addPartialOnClickListener(Card.CLICK_LISTENER_CONTENT_VIEW, clickListener);*/
         /*   setOnClickListener(new OnCardClickListener() {
                @Override
                public void onClick(Card card, View view) {
//                    Do something
                    String selected= card.getId();
                    Toast.makeText(getBaseContext(), "Item ID is" + selected, Toast.LENGTH_LONG).show();
                   *//* Intent intent =new Intent(getBaseContext(),ProductDetail.class);
                    intent.putExtra("id",selected);
                    intent.putExtra("product_name",card.getTitle());
                    startActivity(intent);*//*
                }
            });*/


        }

        @Override
        public void setupInnerViewElements(ViewGroup parent, View view) {

            TextView title = (TextView) view.findViewById(R.id.carddemo_gplay_main_inner_title);
            title.setText(zone);

            final TextView subtitle = (TextView) view.findViewById(R.id.carddemo_gplay_main_inner_subtitle);
            subtitle.setText(secondaryTitle);
//            subtitle.setTextIsSelectable(true);
            subtitle.setClickable(true);
           /* subtitle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ClipboardManager cm = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                    cm.setText(subtitle.getText());
//                    Toast.makeText(getContext(), "Copied to clipboard!", Toast.LENGTH_SHORT).show();
                    MyShortcuts.showToast("Copied to clipboard!", getBaseContext());
                *//*    Intent intent = new Intent(getContext(), EditPatient.class);
                    intent.putExtra("ID", getId());
                    startActivity(intent);*//*
//                    getParentCard().getId();
                    subtitle.getText();
                }
            });*/



           /* final TextView subtitle2 = (TextView) view.findViewById(R.id.carddemo_gplay_main_inner_subtitle2);
//            subtitle2.setText(secondaryTitle);
            subtitle2.setClickable(true);
            subtitle2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

//                    getParentCard().getId();
                   *//* Intent intent = new Intent(getContext(), AllPrescriptions.class);
                    intent.putExtra("ID", getId());
                    startActivity(intent);*//*
                }
            });*/
//            NetworkImageView thumbnail =(NetworkImageView)view.findViewById(R.id.card_thumbnail_image);
//            thumbnail.setImageUrl(url,imageLoader);


        }

        class GplayGridThumb extends CardThumbnail {

            public GplayGridThumb(Context context) {
                super(context);
            }

            @Override
            public void setupInnerViewElements(ViewGroup parent, View viewImage) {
                //viewImage.getLayoutParams().width = 196;
                //viewImage.getLayoutParams().height = 196;

            }
        }

    }


    private void getSqlite(String table_name) {

        List<String> stringList = new ArrayList<String>();
        Cursor cursor = null;
        ArrayList<Card> cards = new ArrayList<Card>();

        GplayGridCard card = new GplayGridCard(getBaseContext());

        card.headerTitle = "12 Watts - Serial 04211742590";
        card.secondaryTitle = "Yaounde";
        card.zone = "Douala";
        card.setId(1 + "");
        card.setTitle("12 Watts - Serial 04211742590");

        card.init();
        cards.add(card);


        GplayGridCard card2 = new GplayGridCard(getBaseContext());

        card2.headerTitle = "100 Watts - Serial 0421134540";
        card2.secondaryTitle = "Yaounde";
        card2.zone = "Douala";
        card2.setId(1 + "");
        card2.setTitle("12 Watts - Serial 04211742590");

        card2.init();
        cards.add(card2);

        try {
            cursor = Connector.getDatabase().rawQuery("select * from " + table_name + " order by id",
                    null);
            if (cursor.getCount() < 1) {
                MyShortcuts.showToast("No readings available. Capture data and it would appear here", getBaseContext());
            }
            if (cursor.moveToFirst()) {
                do {

                    String index = cursor.getString(cursor.getColumnIndex("meter_index"));
                    String serial = cursor.getString(cursor.getColumnIndex("meter_serial"));
                    String zone = cursor.getString(cursor.getColumnIndex("zone"));
                    String island = cursor.getString(cursor.getColumnIndex("island"));


                    GplayGridCard card1 = new GplayGridCard(getBaseContext());

                    card1.headerTitle = index + " Watts= Serial " + serial;
                    card1.secondaryTitle = island;
                    card1.zone=zone;
                    card1.setId(serial);
                    card1.setTitle(index + " Watts= Serial " + serial);

                    card1.init();
                    cards.add(card1);

                    Log.e("DATA", index + " and serial is " + serial);


                    stringList.add(index);

                } while (cursor.moveToNext());
                Log.e("StringList", stringList.toString() + stringList.size());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }

            mCardArrayAdapter = new CardGridArrayAdapter(getBaseContext(), cards);

            CardGridView listView = (CardGridView) findViewById(R.id.carddemo_grid_base1);
            if (listView != null) {
                listView.setAdapter(mCardArrayAdapter);
            }
        }
    }
}


