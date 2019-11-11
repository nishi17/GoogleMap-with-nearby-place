package cloudzon.com.googlecurrentlocation.Globle;

import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import android.view.Gravity;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

/**
 * Created by lg on 8/29/2016.
 */
public class Common {

    public static final String INTERNET_NOT_FOUND_MSG = "No Internet found!!";

    public static boolean checkInternetConnection(Context context){
        ConnectivityManager cm =
                (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        return isConnected;
    }

    public static void alertDialog(Context contex, String msg) {

        AlertDialog.Builder builder = new AlertDialog.Builder(contex);
        builder.setTitle("Alert!");
        builder.setMessage(msg);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub

                // Intent i=new
                // Intent(contex,BankDebitCardConfigurationMain.class);
                // startActivity(i);
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.show();
        TextView messageText = (TextView) dialog.findViewById(android.R.id.message);
        messageText.setGravity(Gravity.CENTER);
    }

}
