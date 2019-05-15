package adam.de.villiers;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import adam.de.pickersdk.activity.PickerSdkMapActivity;
import adam.de.pickersdk.model.PlaceModel;

import static adam.de.pickersdk.activity.PickerSdkMapActivity.getAdamPlacePickerIntent;

public class MainActivity extends AppCompatActivity {

    TextView latitude, longitude, address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button openMap = findViewById(R.id.open_map_button);

        latitude = findViewById(R.id.latitude);
        longitude = findViewById(R.id.longitude);
        address = findViewById(R.id.address);

        openMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(getAdamPlacePickerIntent(MainActivity.this), 1);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 1){
            if(resultCode == Activity.RESULT_OK){
                PlaceModel placeModel = PickerSdkMapActivity.getPlace(data);

                latitude.setText(String.format(getResources().getString(R.string.latitude_display), placeModel.getLat()));
                longitude.setText(String.format(getResources().getString(R.string.longitude_display), placeModel.getLng()));
                address.setText(String.format(getResources().getString(R.string.street_display), placeModel.getAddress()));
            }
        }
    }
}
