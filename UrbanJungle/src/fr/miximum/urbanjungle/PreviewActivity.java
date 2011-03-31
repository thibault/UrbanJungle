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
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class PreviewActivity extends Activity {

    /** The captured image file. Get it's path from the starting intent */
    private File mImage;

    /** Log tag */
    private static final String TAG = "UrbanJungle";

    /** Progress dialog id */
    private static final int UPLOAD_PROGRESS_DIALOG = 0;
    private static final int UPLOAD_ERROR_DIALOG = 1;
    private static final int UPLOAD_SUCCESS_DIALOG = 2;
    private static final int WAITING_LOCATION_DIALOG = 3;

    /** Accuracy required (in meters), before we can upload file */
    private static final int LOCATION_ACCURACY_NEEDED = 100;

    /** Accuracy good enough so we can stop listening to gps */
    private static final int LOCATION_ACCURACY_ENOUGH = 30;

    /** Handler to confirm button */
    private Button mConfirm;

    /** Handler to cancel button */
    private Button mCancel;

    /** Uploading progress dialog */
    private ProgressDialog mDialog;

    private LocationListener mLocationListener;
    private LocationManager mLocationManager;
    private Location mLocation;

    /**
     * Called when the activity is created
     *
     * We load the captured image, and register button callbacks
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
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

        registerButtonCallbacks();
        registerLocationListener();
        showDialog(WAITING_LOCATION_DIALOG);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mLocationManager.removeUpdates(mLocationListener);
    }

    /**
     * Register callbacks for ui buttons
     */
    protected void registerButtonCallbacks() {
        // Cancel button callback
        mCancel = (Button) findViewById(R.id.preview_send_cancel);
        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PreviewActivity.this.finish();
            }
        });

        // Confirm button callback
        mConfirm = (Button) findViewById(R.id.preview_send_confirm);
        mConfirm.setEnabled(false);
        mConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new UploadImageTask().execute(mImage);
            }
        });
    }

    /**
     * Register required listeners to obtain gps location
     */
    protected void registerLocationListener() {

        mLocationListener = new LocationListener() {
            private boolean minAccuracyReached = false;

            @Override
            public void onLocationChanged(Location location) {
                if (mLocation == null) {
                    mLocation = location;
                } else if (location.getAccuracy() < mLocation.getAccuracy()) {
                    mLocation = location;
                }

                if (!minAccuracyReached && mLocation.getAccuracy() <= LOCATION_ACCURACY_NEEDED) {
                    minAccuracyReached = true;
                    dismissDialog(WAITING_LOCATION_DIALOG);
                    mConfirm.setEnabled(true);
                }

                if (mLocation.getAccuracy() <= LOCATION_ACCURACY_ENOUGH) {
                    mLocationManager.removeUpdates(mLocationListener);
                }
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {}

            @Override
            public void onProviderEnabled(String provider) {}

            @Override
            public void onProviderDisabled(String provider) {}
        };

        mLocationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, LOCATION_ACCURACY_NEEDED, mLocationListener);
    }

    /**
     * Initialize the dialogs
     */
    @Override
    protected Dialog onCreateDialog(int id) {
        switch(id) {
        case UPLOAD_PROGRESS_DIALOG:
            mDialog = new ProgressDialog(this);
            mDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            mDialog.setCancelable(false);
            mDialog.setTitle(getString(R.string.progress_dialog_title_connecting));
            return mDialog;

        case WAITING_LOCATION_DIALOG:
            ProgressDialog locationDialog = new ProgressDialog(this);
            locationDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            locationDialog.setCancelable(false);
            locationDialog.setTitle(getString(R.string.location_dialog_title));
            locationDialog.setMessage(getString(R.string.location_dialog_message));
            locationDialog.setButton(getString(R.string.cancel_send), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    PreviewActivity.this.finish();
                }
            });
            return locationDialog;

        case UPLOAD_ERROR_DIALOG:
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.upload_error_title)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setMessage(R.string.upload_error_message)
                    .setCancelable(false)
                    .setPositiveButton(getString(R.string.retry), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            PreviewActivity.this.finish();
                        }
                    });
            return builder.create();

        case UPLOAD_SUCCESS_DIALOG:
            AlertDialog.Builder success = new AlertDialog.Builder(this);
            success.setTitle(R.string.upload_success_title)
                    .setIcon(android.R.drawable.ic_dialog_info)
                    .setMessage(R.string.upload_success_message)
                    .setCancelable(false)
                    .setPositiveButton(getString(R.string.success), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            PreviewActivity.this.finish();
                        }
                    });
            return success.create();

        default:
            return null;
        }
    }

    /**
     * Prepare the progress dialog
     */
    @Override
    protected void onPrepareDialog(int id, Dialog dialog) {
        switch(id) {
        case UPLOAD_PROGRESS_DIALOG:
            mDialog.setProgress(0);
            mDialog.setTitle(getString(R.string.progress_dialog_title_connecting));
        }
    }

    /**
     * Load the image file into the imageView
     *
     * @param image
     */
    protected void loadImage(File image) {
        Bitmap bm = BitmapFactory.decodeFile(image.getPath());
        ImageView view = (ImageView) findViewById(R.id.preview_image);
        view.setImageBitmap(bm);
    }

    /**
     * Asynchronous task to upload file to server
     */
    class UploadImageTask extends AsyncTask<File, Integer, Boolean> {

        /** Upload file to this url */
        private static final String UPLOAD_URL = "http://thibault-laptop:8080/report";

        /** Send the file with this form name */
        private static final String FIELD_FILE = "file";
        private static final String FIELD_LATITUDE = "latitude";
        private static final String FIELD_LONGITUDE = "longitude";

        /**
         * Prepare activity before upload
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            setProgressBarIndeterminateVisibility(true);
            mConfirm.setEnabled(false);
            mCancel.setEnabled(false);
            showDialog(UPLOAD_PROGRESS_DIALOG);
        }

        /**
         * Clean app state after upload is completed
         */
        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            setProgressBarIndeterminateVisibility(false);
            mConfirm.setEnabled(true);
            mDialog.dismiss();

            if (result) {
                showDialog(UPLOAD_SUCCESS_DIALOG);
            } else {
                showDialog(UPLOAD_ERROR_DIALOG);
            }
        }

        @Override
        protected Boolean doInBackground(File... image) {
            return doFileUpload(image[0], UPLOAD_URL);
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);

            if (values[0] == 0) {
                mDialog.setTitle(getString(R.string.progress_dialog_title_uploading));
            }

            mDialog.setProgress(values[0]);
        }

        /**
         * Upload given file to given url, using raw socket
         * @see http://stackoverflow.com/questions/4966910/androidhow-to-upload-mp3-file-to-http-server
         *
         * @param file The file to upload
         * @param uploadUrl The uri the file is to be uploaded
         *
         * @return boolean true is the upload succeeded
         */
        private boolean doFileUpload(File file, String uploadUrl) {
            HttpURLConnection conn = null;
            DataOutputStream dos = null;
            String lineEnd = "\r\n";
            String twoHyphens = "--";
            String boundary = "*****";
            String separator = twoHyphens + boundary + lineEnd;
            int bytesRead, bytesAvailable, bufferSize;
            byte[] buffer;
            int maxBufferSize = 1 * 1024 * 1024;
            int sentBytes = 0;
            long fileSize = file.length();

            // The definitive url is of the kind:
            // http://host/report/latitude,longitude
            uploadUrl += "/" + mLocation.getLatitude() + "," + mLocation.getLongitude();

            // Send request
            try {
                // Configure connection
                URL url = new URL(uploadUrl);
                conn = (HttpURLConnection) url.openConnection();
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.setUseCaches(false);
                conn.setRequestMethod("PUT");
                conn.setRequestProperty("Connection", "Keep-Alive");
                conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                publishProgress(0);

                dos = new DataOutputStream(conn.getOutputStream());

                // Send location params
                writeFormField(dos, separator, FIELD_LATITUDE, "" + mLocation.getLatitude());
                writeFormField(dos, separator, FIELD_LONGITUDE, "" + mLocation.getLongitude());

                // Send multipart headers
                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"" + FIELD_FILE + "\";filename=\""
                        + file.getName() + "\"" + lineEnd);
                dos.writeBytes(lineEnd);

                // Read file and create buffer
                FileInputStream fileInputStream = new FileInputStream(file);
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                buffer = new byte[bufferSize];

                // Send file data
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                while (bytesRead > 0) {
                    // Write buffer to socket
                    dos.write(buffer, 0, bufferSize);

                    // Update progress dialog
                    sentBytes += bufferSize;
                    publishProgress((int)(sentBytes * 100 / fileSize));

                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                }

                // send multipart form data necesssary after file data
                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
                dos.flush();
                dos.close();
                fileInputStream.close();
            } catch (IOException ioe) {
                Log.e(TAG, "Cannot upload file: " + ioe.getMessage(), ioe);
                return false;
            }

            // Read response
            try {
                int responseCode = conn.getResponseCode();
                return responseCode == 200;
            } catch (IOException ioex) {
                Log.e(TAG, "Upload file failed: " + ioex.getMessage(), ioex);
                return false;
            } catch (Exception e) {
                Log.e(TAG, "Upload file failed: " + e.getMessage(), e);
                return false;
            }
        }

        private void writeFormField(DataOutputStream dos, String separator, String fieldName, String fieldValue) throws IOException
        {
            dos.writeBytes(separator);
            dos.writeBytes("Content-Disposition: form-data; name=\"" + fieldName + "\"\r\n");
            dos.writeBytes("\r\n");
            dos.writeBytes(fieldValue);
            dos.writeBytes("\r\n");
        }
    }
}
