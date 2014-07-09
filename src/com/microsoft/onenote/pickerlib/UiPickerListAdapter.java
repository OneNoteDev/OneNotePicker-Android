//----------------------------------------------------------------------------
//  Copyright (c) Microsoft Open Technologies, Inc. All rights reserved.
//  Licensed under the Apache License, Version 2.0.
//  See License.txt in the project root for license information.
//----------------------------------------------------------------------------
package com.microsoft.onenote.pickerlib;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

class UiPickerListAdapter extends ArrayAdapter<UiPickerItemModel> {

    private final Context mContext;
    private final ArrayList<UiPickerItemModel> mModelList;

    public UiPickerListAdapter(Context context, ArrayList<UiPickerItemModel> modelList) {

        super(context, R.layout.list_item_entry, modelList);

        this.mContext = context;
        this.mModelList = modelList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder;

        // get the model
        UiPickerItemModel model = mModelList.get(position);

        if (convertView == null) {
            // inflate layout
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            convertView = inflater.inflate(R.layout.list_item_entry, parent, false);

            // determine which icon should be displayed
            ImageView icon = findIconView(convertView, model.getType());

            // use a ViewHolder for view caching
            viewHolder = new ViewHolder();

            viewHolder.textView = (TextView) convertView.findViewById(R.id.itemText);
            viewHolder.textView.setText(model.getText());

            viewHolder.iconView = (ImageView) convertView.findViewById(R.id.itemIcon);
            viewHolder.iconView.setImageDrawable(icon.getDrawable());
            viewHolder.iconView.setContentDescription(icon.getContentDescription());

            // bind the viewHolder to the view
            convertView.setTag(viewHolder);

        } else {
            // use cached view
            viewHolder = (ViewHolder) convertView.getTag();

            // update text
            viewHolder.textView.setText(model.getText());

            // update icon
            ImageView icon = findIconView(convertView, model.getType());
            viewHolder.iconView.setImageDrawable(icon.getDrawable());
            viewHolder.iconView.setContentDescription(icon.getContentDescription());
        }

        return convertView;
    }

    private ImageView findIconView(View container, UiPickerItemType type){
        // determine which icon should be displayed
        int iconViewId = R.id.itemIconNotebook;

        switch (type) {
            case NOTEBOOK:
                iconViewId = R.id.itemIconNotebook;
                break;
            case SECTION_GROUP:
                iconViewId = R.id.itemIconSectionGroup;
                break;
            case SECTION:
                iconViewId = R.id.itemIconSection;
                break;
        }
        return ((ImageView) container.findViewById(iconViewId));
    }

    class ViewHolder {
        TextView textView;
        ImageView iconView;
    }
}