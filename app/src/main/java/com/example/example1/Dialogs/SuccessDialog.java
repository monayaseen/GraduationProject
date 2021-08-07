package com.example.example1.Dialogs;

        import android.app.Activity;
        import android.app.Dialog;
        import android.content.Intent;
        import android.view.View;
        import android.view.Window;
        import android.widget.Button;
        import android.widget.TextView;

        import com.example.example1.Dialogs.*;
        import com.example.example1.R;

public class SuccessDialog {

    public void showDialog(final Activity activity, String msg){
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.success);

        TextView text = dialog.findViewById(R.id.text_dialog);
        text.setText(msg);

        Button dialogButton =  dialog.findViewById(R.id.btn_dialog);
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Intent i = activity.getPackageManager().
                        getLaunchIntentForPackage(activity.getPackageName());
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            }
        });
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();

    }
}
