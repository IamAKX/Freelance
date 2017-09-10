package Utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Base64;

import com.akash.applications.freelance.ProfileSettingsOptions.Experience;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Created by akash on 8/2/17.
 */

public class ImageEncoder {


    public static String encode(Bitmap bitmap)
    {
        if(bitmap == null)
            return "null";
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 20, baos);
        byte[] imageBytes = baos.toByteArray();
        return Base64.encodeToString(imageBytes, Base64.DEFAULT);
    }

    public static String encodeUri(Uri uri, Context context)
    {
        String encodedImg = null;
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
            encodedImg = encode(bitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return encodedImg;
    }

}
