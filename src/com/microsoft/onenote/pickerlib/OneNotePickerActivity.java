//----------------------------------------------------------------------------
//  Copyright (c) Microsoft Open Technologies, Inc. All rights reserved.
//  Licensed under the Apache License, Version 2.0.
//  See License.txt in the project root for license information.
//----------------------------------------------------------------------------
package com.microsoft.onenote.pickerlib;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;


public class OneNotePickerActivity extends ActionBarActivity {

    private int mNavTextColor = -1;
    private String mAccessToken;

    private ControllerFragment mControllerFragment;
    private PickerListFragment mNotebooksFragment;
    private OneNotePickerThemeColor mThemeColor;


    private void setUpActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(false); //remove actionbar icon

        if (mNavTextColor != -1) {
            actionBar.setHomeAsUpIndicator(
                    UiUtility.colorizedDrawable(this, mNavTextColor, R.drawable.ic_back_arrow));
        }

        //default settings
        updateActionBar(getString(R.string.text_notebooks), getString(R.string.text_onenote), false);
    }

    private void readExtras() {
        Intent intent = getIntent();
        mNavTextColor = intent.getIntExtra("NAV_TEXT_COLOR", -1);
        mAccessToken = intent.getStringExtra("ACCESS_TOKEN");
        mThemeColor = (OneNotePickerThemeColor) intent.getSerializableExtra("THEME_COLOR");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Handle uncaught and system exceptions
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread thread, Throwable throwable) {
                handleError(throwable);
            }
        });

        // Use the slide in animation
        overridePendingTransition(R.anim.slide_in, R.anim.hold);

        readExtras();

        // Determine the theme to use
        if (mThemeColor == null || mThemeColor == OneNotePickerThemeColor.LIGHT) {
            setTheme(R.style.OneNotePicker_Light);
        } else if (mThemeColor == OneNotePickerThemeColor.DARK) {
            setTheme(R.style.OneNotePicker_Dark);
        }

        setUpActionBar();

        setContentView(R.layout.activity_onenote_picker);

        // Retrieve the controller fragment
        FragmentManager fm = getSupportFragmentManager();
        mControllerFragment = (ControllerFragment) fm.findFragmentByTag("controller");
        
        if (savedInstanceState == null) {
            mNotebooksFragment = new PickerListFragment(
                    new UiActionBarSettings(
                            getString(R.string.text_notebooks),
                            getString(R.string.text_onenote), false)
            );
            
            mNotebooksFragment.mLoading = true;
            
            // Create the controller fragment the first time
            if (mControllerFragment == null) {
                mControllerFragment = new ControllerFragment(mAccessToken, mNotebooksFragment);
                fm.beginTransaction().add(mControllerFragment, "controller").commit();
            }

            // Show the first fragment
            addFragment(mNotebooksFragment);
        }

    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
            cancelPickerActivity();
        }
        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        // Use the slide out animation
        overridePendingTransition(R.anim.hold, R.anim.slide_out);
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if (isFinishing()) {
            mControllerFragment.cleanup();
            mControllerFragment = null;
            mNotebooksFragment = null;
        }
        super.onDestroy();
    }

    protected void updateActionBar(UiActionBarSettings settings) {
        this.updateActionBar(settings.getTitle(), settings.getSubtitle(), settings.getShowArrow());
    }

    protected void updateActionBarTitle(String title) {
        updateActionBar(title, null, null);
    }

    protected void updateActionBarSubTitle(String subtitle) {
        updateActionBar(null, subtitle, null);
    }

    private void updateActionBar(String title, String subtitle, Boolean displayHomeAsUpEnabled) {
        ActionBar actionBar = getSupportActionBar();

        if (mNavTextColor == -1) {
            if (title != null) {
                actionBar.setSubtitle(title); // Action Bar's Subtitle as our "Main Title"
            }
            if (subtitle != null) {
                actionBar.setTitle(subtitle); // Action Bar's Title as our "Sub Title"
            }
        } else {
            // Display titles with custom text color
            if (title != null) {
                actionBar.setSubtitle(UiUtility.colorizedText(mNavTextColor, title));
            }
            if (subtitle != null) {
                actionBar.setTitle(
                        UiUtility.colorizedText(UiUtility.adjustAlpha(mNavTextColor, 153), subtitle));
            }
        }

        if (displayHomeAsUpEnabled != null) {
            // Display the back arrow or not
            actionBar.setDisplayHomeAsUpEnabled(displayHomeAsUpEnabled);
            actionBar.setHomeButtonEnabled(displayHomeAsUpEnabled);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_onenote_picker, menu);

        if (mNavTextColor != -1) {
            menu.findItem(R.id.action_close).setIcon(
                    UiUtility.colorizedDrawable(this,mNavTextColor, R.drawable.ic_action_close_dark));
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_close) {
            // Cancel & close activity
            cancelPickerActivity();
        } else if (id == android.R.id.home) {
            this.onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    // Drill down on a fragment
    protected void showFragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, fragment).addToBackStack("ONP")
                    .commit();
        }
    }

    // Show the very first fragment
    protected void addFragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, fragment)
                    .commit();
        }
    }

    // Clicking on an item can either return (if it is a section) or drill down (if it is a notebook or section group)
    protected void handlePickerItemClick(PickerListFragment parent, UiPickerItemModel model) {
        if (model.getType() == UiPickerItemType.SECTION) {
            Intent resultIntent = new Intent();
            ApiSectionResponse response = (ApiSectionResponse) model.getResultData();
            resultIntent.putExtra("SECTION_ID", response.getId());
            resultIntent.putExtra("SECTION_NAME", response.getName());
            resultIntent.putExtra("PAGES_URL", response.getPagesUrl());
            resultIntent.putExtra("CREATED_TIME", response.getCreatedTime());
            resultIntent.putExtra("MODIFIED_TIME", response.getModifiedTime());
            resultIntent.putExtra("LAST_MODIFIED_BY", response.getLastModifiedBy());
            closePickerActivity(RESULT_OK, resultIntent);
        } else {
            showFragment(mControllerFragment.getPickerFragment(parent, model));
        }
    }

    // Handle any type of exception that was thrown
    protected void handleError(Throwable exception) {
        boolean apiError;
        Intent resultIntent = new Intent();
        resultIntent.putExtra("USER_CANCELLED", false);
        if (exception instanceof ApiException) {
            apiError = true;
            ApiException apiException = (ApiException) exception;
            resultIntent.putExtra("API_ERROR_CODE", apiException.getErrorCode());
            resultIntent.putExtra("API_ERROR_STRING", apiException.getErrorMessage());
            resultIntent.putExtra("API_ERROR_URL", apiException.getErrorUrl());
        } else {
            apiError = false;
            resultIntent.putExtra("SYSTEM_EXCEPTION", exception);
        }
        resultIntent.putExtra("API_ERROR", apiError);

        closePickerActivity(RESULT_CANCELED, resultIntent);
    }

    private void closePickerActivity(int resultCode, Intent resultIntent) {
        setResult(resultCode, resultIntent);
        if (!isFinishing()) {
            finish();
        }
    }

    private void cancelPickerActivity() {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("USER_CANCELLED", true);
        closePickerActivity(RESULT_CANCELED, resultIntent);
    }

    @SuppressLint("ValidFragment")
    private class ControllerFragment extends RetainedFragment {

        private ApiController mApiController;

        private HashMap<ApiResponse, PickerListFragment>
                mPickerListFragments = new HashMap<ApiResponse, PickerListFragment>();

        private String mAccessToken;

        private PickerListFragment mStartFragment;

        public ControllerFragment(String mAccessToken, PickerListFragment startFragment) {
            this.mAccessToken = mAccessToken;
            this.mStartFragment = startFragment;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            mApiController = new ApiController(mAccessToken) {
            	// On an API response, check for failures, then populate fragments recursively
				@Override
				public void onApiResponse(ApiNotebookResponse[] responses,
						Exception error) {
                	
					if (error != null){
                        ((OneNotePickerActivity) getActivity()).handleError(error);
                        return;
                	}
					
					for (int i = 0; i < responses.length; i++)
	                {	
	                	ApiNotebookResponse notebookResponse = responses[i];
	                    
	                	// Add to start fragment models
	                	mStartFragment.mLoadedModels.add(new UiPickerItemModel(UiPickerItemType.NOTEBOOK, notebookResponse.getName(), notebookResponse));
	                	
						// Cycle through the returned notebooks and populate mPickerListFragments
	                    PickerListFragment notebookFragment = new PickerListFragment(new UiActionBarSettings(notebookResponse.getName(), null, null));
	                    
	                    // Also populate their children sections/sectionGroups
	                    addSectionsToFragment(notebookResponse.sections, notebookFragment);
	                    addSectionGroupsToFragment(notebookResponse.sectionGroups, notebookFragment);
	                    
	                    notebookFragment.updateListAdapterWithModels();
	                    // Add the notebook to mPickerListFragments
	                    mPickerListFragments.put(notebookResponse, notebookFragment);
	                }
	                
	                mStartFragment.updateListAdapterWithModels();
				}

				// Add sections to fragment
				private void addSectionsToFragment(List<ApiSectionResponse> sections, PickerListFragment fragment) {
					for (int k = 0; k < sections.size(); k++)
					{
						ApiSectionResponse sectionResponse = sections.get(k);
						fragment.mLoadedModels.add(new UiPickerItemModel(UiPickerItemType.SECTION, sectionResponse.getName(), sectionResponse));
					}
				}
				
				// Add sectionGroups to a fragment, and to mPickerListFragments
				private void addSectionGroupsToFragment(List<ApiSectionGroupResponse> sectionGroups, PickerListFragment fragment) {
					for (int k = 0; k < sectionGroups.size(); k++)
					{
						ApiSectionGroupResponse sectionGroupResponse = sectionGroups.get(k);
						fragment.mLoadedModels.add(new UiPickerItemModel(UiPickerItemType.SECTION_GROUP, sectionGroupResponse.getName(), sectionGroupResponse));
						
						// This section group should also be its own fragment - Add it to mPickerListFragments
	                    PickerListFragment sectionGroupFragment = new PickerListFragment(new UiActionBarSettings(sectionGroupResponse.getName(), null, null));
	                    // Also populate their children sections/sectionGroups
	                    addSectionsToFragment(sectionGroupResponse.sections, sectionGroupFragment);
	                    addSectionGroupsToFragment(sectionGroupResponse.sectionGroups, sectionGroupFragment);
	                    
	                    sectionGroupFragment.updateListAdapterWithModels();
	                    
	                    mPickerListFragments.put(sectionGroupResponse, sectionGroupFragment);
					}
				}
            };
            mApiController.begin();
        }

        public PickerListFragment getPickerFragment(PickerListFragment parent, UiPickerItemModel model) {
            PickerListFragment fragment = mPickerListFragments.get(model.getResultData());
            if (fragment != null) {
                fragment.getActionBarSettings().setShowArrow(true);
                fragment.getActionBarSettings().setSubtitle(parent.getActionBarSettings().getTitle());
                fragment.updateListAdapterWithModels();
            }
            return fragment;
        }

        public void cleanup() {
            mPickerListFragments.clear();
            mStartFragment = null;
        }
    }

    /**
     * A fragment that acts as an API response controller and a picker list view
     */
    @SuppressLint("ValidFragment")
    private class PickerListFragment extends RetainedFragment implements AdapterView.OnItemClickListener {

        private UiPickerListAdapter mListAdapter = null;
        private List<UiPickerItemModel> mLoadedModels = new ArrayList<UiPickerItemModel>();
        private UiActionBarSettings mActionBarSettings;
        private OneNotePickerActivity mPickerActivity;
        
        // Used exclusively for the progressbar showing/hiding on the first API call
        public boolean mLoading = false;

        public PickerListFragment(UiActionBarSettings actionBarSettings) {
            mActionBarSettings = actionBarSettings;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_picker_list, container, false);

            mPickerActivity = ((OneNotePickerActivity) getActivity());

            // set up parent activity's action bar
            mPickerActivity.updateActionBar(mActionBarSettings);

            // set up list view & adapter
            ListView listView = (ListView) rootView.findViewById(R.id.listView);
            if (mListAdapter == null) {
                mListAdapter = new UiPickerListAdapter(mPickerActivity, new ArrayList<UiPickerItemModel>());
                mListAdapter.setNotifyOnChange(false);
            }
            listView.setAdapter(mListAdapter);

            listView.setOnItemClickListener(this);

            // loading spinner
            final ProgressBar loadingSpinner = (ProgressBar) rootView.findViewById(R.id.progressBar);

            // handle if data has not been loaded
            if (mLoading) {

                loadingSpinner.setVisibility(View.VISIBLE);

                // update the action bar to its original title after data has loaded
                mListAdapter.registerDataSetObserver(new DataSetObserver() {
                    @Override
                    public void onChanged() {
                        super.onChanged();
                        if (PickerListFragment.this.isVisible()) {
                            loadingSpinner.setVisibility(View.GONE);
                        }
                    }
                });
            } else {
                updateListAdapterWithModels();
            }
            return rootView;
        }

        public void updateListAdapterWithModels() {
            if (mLoadedModels != null && mListAdapter != null) {
            	mLoading = false;
            	mListAdapter.clear();
                for (UiPickerItemModel model : mLoadedModels) {
                    mListAdapter.add(model);
                }
                mListAdapter.notifyDataSetChanged();
            }
        }

        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            mPickerActivity.handlePickerItemClick(this, mListAdapter.getItem(i));
        }

        public UiActionBarSettings getActionBarSettings() {
            return mActionBarSettings;
        }
    }

    @SuppressLint("ValidFragment")
    private class RetainedFragment extends Fragment {

        @Override
        public void onDetach() {
            super.onDetach();
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            // retain this fragment
            setRetainInstance(true);
        }
    }
}
