package com.example.administrator.cehuamianban;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by Administrator on 2016/11/24.
 */

public class utils {

    private static Toast toast;

    public static void showToast(Context context, String msg){
        if(toast == null){
            toast = Toast.makeText(context,"",Toast.LENGTH_SHORT);
        }
        toast.setText(msg);
        toast.show();
    }
}
