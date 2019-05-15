# Place Picker Library

Place picker library is a very basic, very simple library created to grab a picked location on google maps.

# Libraries

play-services-maps is added to view google maps.

# Installation

Android Studio is required.
The developer can clone the repository, and run the application through Android Studio. Or download the application as a zip file, and open with Android studio.

`<uses-permission android:name="android.permission.INTERNET" />`

is required to be added in the manifest of the application

# How to use

Once the library is cloned or downloaded, simply call this fuction;

`startActivityForResult(getPlacePickerIntent(MainActivity.this), 1);`

when you want to open the map. 

Once a user has pin pointed a location, either by search or by press, it will store the Latitude, Longatude and street address.
To access these variables, the developer needs to do this;

```
@Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 1){
            if(resultCode == Activity.RESULT_OK){
                PlaceModel placeModel = PickerSdkMapActivity.getPlace(data);
            }
        }
    }
```

The developer has full control over what they decide to do with the result data once the model is set;

```
placeModel.getLat();
placeModel.getLng();
placeModel.getAddress();
```

