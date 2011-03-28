/*
 * © Copyright 2011 Thibault Jouannic <thibault@jouannic.fr>. All Rights Reserved.
 *
 *  This file is part of UrbanJungle.
 *
 *  UrbanJungle is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  UrbanJungle is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with UrbanJungle. If not, see <http://www.gnu.org/licenses/>.
 */
package fr.miximum.urbanjungle;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import java.io.File;

public class CaptureActivity extends Activity
{
    /** Tag used for log */
     static final String TAG="UrbanJungle";

     /** The captured photo file name */
     private static final String CAPTURE_TITLE="UrbanJungle.jpg";

     /** Arbitrary code to use with getActivityForResult */
     private static final int TAKE_PHOTO_CODE = 2;

     /** Extra name for captured image path */
     public static final String EXTRA_IMAGE_PATH = "extraImagePath";

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.capture);

        Button capture = (Button) findViewById(R.id.capture_button);
        capture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // We use the stock camera app to take a photo
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                intent.putExtra(MediaStore.EXTRA_OUTPUT, getImageUri());
                startActivityForResult(intent, TAKE_PHOTO_CODE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == TAKE_PHOTO_CODE && resultCode == RESULT_OK) {
            Uri imagePath = getImageUri();

            Intent intent = new Intent(this, PreviewActivity.class);
            intent.putExtra(EXTRA_IMAGE_PATH, imagePath.getPath());
            startActivity(intent);
        }
    }

    /**
     * Get the uri of the captured file
     * @return A Uri which path is the path of an image file, stored on the dcim folder
     */
    private Uri getImageUri() {
        // Store image in dcim
        File file = new File(Environment.getExternalStorageDirectory() + "/DCIM", CAPTURE_TITLE);
        Uri imgUri = Uri.fromFile(file);

        return imgUri;
    }
}
