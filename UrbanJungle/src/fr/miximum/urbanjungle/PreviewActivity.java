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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;

public class PreviewActivity extends Activity {

    private File mImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.preview);

        setResult(RESULT_CANCELED);

        // Import image
        Bundle extras = getIntent().getExtras();
        String imagePath = extras.getString(CaptureActivity.EXTRA_IMAGE_PATH);
        mImage = new File(imagePath);
        if (mImage.exists()) {
            setResult(RESULT_OK);
            loadImage(mImage);
        }

        // Set button callbacks
        Button confirm = (Button) findViewById(R.id.preview_send_confirm);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent startUpload = new Intent(getApplicationContext(), HttpUploader.class);
                startUpload.setData(Uri.fromFile(mImage));
                startService(startUpload);
            }
        });
    }

    /**
     * Load the image file into the imageView
     * @param image
     */
    protected void loadImage(File image) {
        Bitmap bm = BitmapFactory.decodeFile(image.getPath());
        ImageView view = (ImageView) findViewById(R.id.preview_image);
        view.setImageBitmap(bm);
    }
}