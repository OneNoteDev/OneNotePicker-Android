//----------------------------------------------------------------------------
//  Copyright (c) Microsoft Open Technologies, Inc. All rights reserved.
//  Licensed under the Apache License, Version 2.0.
//  See License.txt in the project root for license information.
//----------------------------------------------------------------------------
package com.microsoft.onenote.pickerlib;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;

class UiUtility {

    public static Spanned colorizedText(int color, String text) {
        Spannable spannable = new SpannableString(text);
        spannable.setSpan(new ForegroundColorSpan(color), 0, text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannable;
    }

    public static int adjustAlpha(int color, int alpha) {
        return (alpha << 24) | (0x00FFFFFF & color);
    }

    public static Drawable colorizedDrawable(Context context, int color, int bitmapResource) {

        Canvas canvas = new Canvas();
        Bitmap input = BitmapFactory.decodeResource(context.getResources(), bitmapResource);
        Bitmap result = Bitmap.createBitmap(input.getWidth(), input.getHeight(), Bitmap.Config.ARGB_8888);
        canvas.setBitmap(result);
        Paint paint = new Paint();
        paint.setFilterBitmap(false);
        paint.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.MULTIPLY));
        canvas.drawBitmap(input, 0, 0, paint);
        return new BitmapDrawable(context.getResources(), result);
    }
}
