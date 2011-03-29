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

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.IBinder;
import android.util.Log;

import java.io.File;
import java.io.IOException;

public class HttpUploader extends Service {

    private static final String UPLOAD_URL = "http://thibault-laptop:8080/upload";

    private static final String FORM_FILE_TITLE = "file";

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);

        // Start file upload
        Uri data = intent.getData();
        if (data != null) {
            File image = new File(data.getPath());
            doFileUpload(image, UPLOAD_URL);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * Upload given file to given url
     * @param file
     * @param uploadUrl
     */
    private boolean doFileUpload(File file, String uploadUrl) {
        HttpClient client = new DefaultHttpClient();
        HttpPost post = new HttpPost(uploadUrl);
        MultipartEntity entity = new MultipartEntity();
        entity.addPart(FORM_FILE_TITLE, new FileBody(file));
        post.setEntity(entity);
        HttpResponse response = null;

        try {
            response = client.execute(post);
        } catch (ClientProtocolException e) {
            Log.e(CaptureActivity.TAG, "Protocol exception " + e.getMessage());
        } catch (IOException e) {
            Log.e(CaptureActivity.TAG, "IO exception " + e.getMessage());
        }

        return response.getStatusLine().getStatusCode() == 200;
    }
}
