package com.gubatron.helloAR;

import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MotionEvent;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.ar.core.Anchor;
import com.google.ar.core.HitResult;
import com.google.ar.core.Plane;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.rendering.Renderable;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.TransformableNode;

import java.util.logging.Logger;

public class MainActivity extends AppCompatActivity {

    private ArFragment arFragment;
    private LocationUpdatesComponent locationUpdatesComponent;

    private final static Logger LOG = Logger.getLogger(MainActivity.class.getName());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        PermissionChecker.checkCameraPermissions(this);
        PermissionChecker.checkLocationPermissions(this);

        // gps location listener
        locationUpdatesComponent = new LocationUpdatesComponent(this, getLocationUpdatesListener());

        arFragment = (ArFragment) getSupportFragmentManager().findFragmentById(R.id.ux_fragment);

        // tap listener
        arFragment.setOnTapArPlaneListener(
                (HitResult hitResult, Plane plane, MotionEvent motionEvent) -> {
                    if (plane.getType() != Plane.Type.HORIZONTAL_UPWARD_FACING) {
                        return;
                    }

                    // Create anchor at tap location
                    Anchor anchor = hitResult.createAnchor();
                    placeObject(arFragment, anchor, Uri.parse("YourModel.glb")); //replace "YourModel.glb" with your 3D model name
                });
    }

    private LocationUpdatesComponent.LocationUpdateListener getLocationUpdatesListener() {
        return MainActivity.this::onLocationUpdate;
    }

    private void onLocationUpdate(Location location) {
        // update location coordinates on the screen
        // log GPS coordinates from Location object
        location.getLatitude();
        location.getLongitude();
        location.getAltitude();
        location.getSpeed();
        location.getBearing();
        LOG.info("MainActivity.onLocationUpdate: lat:" + location.getLatitude() + ", lon:" + location.getLongitude() + ", alt:" + location.getAltitude() + ", speed:" + location.getSpeed() + ", bearing:" + location.getBearing());
    }

    private void placeObject(ArFragment arFragment, Anchor anchor, Uri modelUri) {
        ModelRenderable.builder()
                .setSource(arFragment.getContext(), modelUri)
                .build()
                .thenAccept(renderable -> addNodeToScene(arFragment, anchor, renderable))
                .exceptionally((throwable -> {
                    Toast toast = Toast.makeText(this, "Error loading 3D model", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    return null;
                }));
    }

    private void addNodeToScene(ArFragment arFragment, Anchor anchor, Renderable renderable) {
        AnchorNode anchorNode = new AnchorNode(anchor);
        TransformableNode node = new TransformableNode(arFragment.getTransformationSystem());
        node.setRenderable(renderable);
        node.setParent(anchorNode);
        arFragment.getArSceneView().getScene().addChild(anchorNode);
        node.select();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PermissionChecker.MY_PERMISSIONS_REQUEST_CAMERA: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission was granted, do the camera-related task you need to do.
                } else {
                    // Permission denied, disable the functionality that depends on this permission.
                }
                return;
            }
            case PermissionChecker.MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    locationUpdatesComponent.startLocationUpdates();
                } else {
                    // Permission denied, disable the functionality that depends on this permission.
                    locationUpdatesComponent.stopLocationUpdates();
                }
                return;
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        locationUpdatesComponent.startLocationUpdates();
    }

    @Override
    protected void onPause() {
        super.onPause();
        locationUpdatesComponent.stopLocationUpdates();
    }
}