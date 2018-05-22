package Dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import ofeksprojects.ofek.com.nightout.MainNavActivity;
import ofeksprojects.ofek.com.nightout.R;

import static Fragments.SearchFragment.PREDEFINED_SEARCH_TAG;

public class ActivateGpsDialog extends AlertDialog {
    public ActivateGpsDialog(final Activity activity, final int requestedTypeSearch) {
        super(activity);
        setMessage(activity.getString(R.string.activate_gps_dialog_message));
        setButton(BUTTON_POSITIVE, activity.getString(R.string.activate_gps_dialog_btn_positive), new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Bundle bundle = new Bundle();
                bundle.putInt(PREDEFINED_SEARCH_TAG,requestedTypeSearch);
                activity.startActivityForResult(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS),MainNavActivity.OPEN_LOCATION_SETTINGS_REQUEST_CODE);
            }
        });
        setButton(BUTTON_NEGATIVE, activity.getString(R.string.activate_gps_dialog_btn_negative), new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                Toast.makeText(activity, R.string.activate_gps_dialog_negative_message, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
