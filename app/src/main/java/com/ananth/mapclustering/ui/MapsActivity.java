package com.ananth.mapclustering.ui;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Base64;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;
import com.ananth.mapclustering.R;
import com.ananth.mapclustering.model.MarkerItem;
import com.ananth.mapclustering.model.Person;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.ui.IconGenerator;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, ClusterManager.OnClusterClickListener<Person>,ClusterManager.OnClusterInfoWindowClickListener<Person>, ClusterManager.OnClusterItemClickListener<Person>, ClusterManager.OnClusterItemInfoWindowClickListener<Person> {

    private GoogleMap mMap;
    private FloatingActionButton mDefault;
    private FloatingActionButton mCustom;
    FloatingActionsMenu menuMultipleActions;
    private FrameLayout mFrameLay;
    private ClusterManager<MarkerItem> mClusterManager;
    private ClusterManager<Person> mCustomClusterManager;
    private Random mRandom = new Random(1984);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mFrameLay = (FrameLayout) findViewById(R.id.flContent);
        menuMultipleActions = (FloatingActionsMenu) findViewById(R.id.fab_menu);
        mDefault = (FloatingActionButton) findViewById(R.id.default_cluster);
        mCustom = (FloatingActionButton) findViewById(R.id.custom_cluster);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        mDefault.setIcon(R.drawable.locate);
        mDefault.setSize(FloatingActionButton.SIZE_NORMAL);
        mDefault.setColorNormalResId(R.color.normal);
        mDefault.setColorPressedResId(R.color.pressed);
//        mCreateCollection.setBackgroundResource(R.drawable.fab_create_collection_bg);
        mDefault.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menuMultipleActions.collapse();
                mMap.clear();
                showDefaultCluster();

            }
        });

        mCustom.setIcon(R.drawable.user);
        mCustom.setSize(FloatingActionButton.SIZE_NORMAL);
        mCustom.setColorNormalResId(R.color.custom_normal);
        mCustom.setColorPressedResId(R.color.custom_pressed);
//        mCreateCollection.setBackgroundResource(R.drawable.fab_create_collection_bg);
        mCustom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menuMultipleActions.collapse();
                mMap.clear();
                showCustomCluster();

            }
        });

        menuMultipleActions.setOnFloatingActionsMenuUpdateListener(new FloatingActionsMenu.OnFloatingActionsMenuUpdateListener() {
            @Override
            public void onMenuExpanded() {
                // TODO Auto-generated method stub
                mFrameLay.setBackgroundColor(Color.parseColor("#a4000000"));

            }

            @Override
            public void onMenuCollapsed() {
                // TODO Auto-generated method stub
                mFrameLay.setBackgroundColor(Color.TRANSPARENT);
            }
        });
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        showDefaultCluster();

    }

    private void showDefaultCluster()
    {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(51.503186, -0.126446), 10));
        mClusterManager = new ClusterManager<MarkerItem>(this, mMap);
        mMap.setOnCameraChangeListener(mClusterManager);

        addDefaultMarkers();
    }

    private void addDefaultMarkers() {
        String mEcodedString = getString(R.string.latlng);
        String json = null;
        List<MarkerItem> items = new ArrayList<MarkerItem>();
        try {
            json = readEncodedJsonString(mEcodedString);
            JSONArray array = new JSONArray(json);
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                double lat = object.getDouble("lat");
                double lng = object.getDouble("lng");
                items.add(new MarkerItem(lat, lng));
                mClusterManager.addItems(items);
            }

        } catch (JSONException e) {

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String readEncodedJsonString(String encoded) throws java.io.IOException {
        byte[] data = Base64.decode(encoded, Base64.DEFAULT);
        return new String(data, "UTF-8");
    }

    @Override
    public boolean onClusterClick(Cluster<Person> cluster) {
        // Show a toast with some info when the cluster is clicked.
        String firstName = cluster.getItems().iterator().next().name;
        Toast.makeText(this, cluster.getSize() + " (including " + firstName + ")", Toast.LENGTH_SHORT).show();

        // Zoom in the cluster. Need to create LatLngBounds and including all the cluster items
        // inside of bounds, then animate to center of the bounds.

        // Create the builder to collect all essential cluster items for the bounds.
        LatLngBounds.Builder builder = LatLngBounds.builder();
        for (ClusterItem item : cluster.getItems()) {
            builder.include(item.getPosition());
        }
        // Get the LatLngBounds
        final LatLngBounds bounds = builder.build();

        // Animate camera to the bounds
        try {
            mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void onClusterInfoWindowClick(Cluster<Person> cluster) {

    }

    @Override
    public boolean onClusterItemClick(Person person) {
        return false;
    }

    @Override
    public void onClusterItemInfoWindowClick(Person person) {

    }


    private class PersonRenderer extends DefaultClusterRenderer<Person> {
        private final IconGenerator mIconGenerator = new IconGenerator(getApplicationContext());
        private final IconGenerator mClusterIconGenerator = new IconGenerator(getApplicationContext());
        private final ImageView mImageView;
        private final ImageView mClusterImageView;
        private final int mDimension;

        public PersonRenderer() {
            super(getApplicationContext(),mMap, mCustomClusterManager);

            View multiProfile = getLayoutInflater().inflate(R.layout.multi_profile, null);
            mClusterIconGenerator.setContentView(multiProfile);
            mClusterImageView = (ImageView) multiProfile.findViewById(R.id.image);

            mImageView = new ImageView(getApplicationContext());
            mDimension = (int) getResources().getDimension(R.dimen.custom_profile_image);
            mImageView.setLayoutParams(new ViewGroup.LayoutParams(mDimension, mDimension));
            int padding = (int) getResources().getDimension(R.dimen.custom_profile_padding);
            mImageView.setPadding(padding, padding, padding, padding);
            mIconGenerator.setContentView(mImageView);
        }

        @Override
        protected void onBeforeClusterItemRendered(Person person, MarkerOptions markerOptions) {
            // Draw a single person.
            // Set the info window to show their name.
            mImageView.setImageResource(person.profilePhoto);
            Bitmap icon = mIconGenerator.makeIcon();
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon)).title(person.name);
        }

        @Override
        protected void onBeforeClusterRendered(Cluster<Person> cluster, MarkerOptions markerOptions) {
            // Draw multiple people.
            // Note: this method runs on the UI thread. Don't spend too much time in here (like in this example).
            List<Drawable> profilePhotos = new ArrayList<Drawable>(Math.min(4, cluster.getSize()));
            int width = mDimension;
            int height = mDimension;

            for (Person p : cluster.getItems()) {
                // Draw 4 at most.
                if (profilePhotos.size() == 4) break;
                Drawable drawable = getResources().getDrawable(p.profilePhoto);
                drawable.setBounds(0, 0, width, height);
                profilePhotos.add(drawable);
            }
            MultiDrawable multiDrawable = new MultiDrawable(profilePhotos);
            multiDrawable.setBounds(0, 0, width, height);

            mClusterImageView.setImageDrawable(multiDrawable);
            Bitmap icon = mClusterIconGenerator.makeIcon(String.valueOf(cluster.getSize()));
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon));
        }

        @Override
        protected boolean shouldRenderAsCluster(Cluster cluster) {
            // Always render clusters.
            return cluster.getSize() > 1;
        }
    }

    private void showCustomCluster()
    {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(51.503186, -0.126446), 9.5f));

        mCustomClusterManager = new ClusterManager<Person>(this,mMap);
        mCustomClusterManager.setRenderer(new PersonRenderer());
        mMap.setOnCameraChangeListener(mCustomClusterManager);
        mMap.setOnMarkerClickListener(mCustomClusterManager);
        mMap.setOnInfoWindowClickListener(mCustomClusterManager);
        mCustomClusterManager.setOnClusterClickListener(this);
        mCustomClusterManager.setOnClusterInfoWindowClickListener(this);
        mCustomClusterManager.setOnClusterItemClickListener(this);
        mCustomClusterManager.setOnClusterItemInfoWindowClickListener(this);

        addItems();
        mCustomClusterManager.cluster();
    }

    private void addItems() {
        mCustomClusterManager.addItem(new Person(position(), "Walter", R.drawable.walter));
        mCustomClusterManager.addItem(new Person(position(), "Gran", R.drawable.gran));
        mCustomClusterManager.addItem(new Person(position(), "Ruth", R.drawable.ruth));
        mCustomClusterManager.addItem(new Person(position(), "Stefan", R.drawable.stefan));
        mCustomClusterManager.addItem(new Person(position(), "Mechanic", R.drawable.mechanic));
        mCustomClusterManager.addItem(new Person(position(), "Yeats", R.drawable.yeats));
        mCustomClusterManager.addItem(new Person(position(), "John", R.drawable.john));
        mCustomClusterManager.addItem(new Person(position(), "Trevor the Turtle", R.drawable.turtle));
        mCustomClusterManager.addItem(new Person(position(), "Teach", R.drawable.teacher));
    }

    private LatLng position() {
        return new LatLng(random(51.6723432, 51.38494009999999), random(0.148271, -0.3514683));
    }

    private double random(double min, double max) {
        return mRandom.nextDouble() * (max - min) + min;
    }


}
