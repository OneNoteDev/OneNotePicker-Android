OneNotePickerLibrary for Android README
================================

### Version info
Version 1.0

Microsoft Open Technologies, Inc. (MS Open Tech) has built the OneNote Picker for Android, an open source project that strives to help Android developers use the OneNote API from their apps.

###Android requirements for using the library
---------------
* JDK 1.6 or higher
* Works on Android 2.2 (Froyo) and higher
* Targeted for Android 4.4 (KitKat)
* INTERNET permissions in AndroidManifest.xml

### Prerequisites

**Tools and Libraries** you will need to download, install, and configure for your development environment to use the OneNotePickerLibrary. 

Be sure to verify the prerequisites for these too.

* [Google Android Developer Tools bundle](http://developer.android.com/sdk/index.html)
* [JDK 1.6 or higher](http://www.oracle.com/technetwork/java/javase/downloads/index.html)  

**Accounts**

* As the user, you'll need to [have a Microsoft account](http://msdn.microsoft.com/EN-US/library/office/dn575426.aspx) 
so your project can authenticate with the [Microsoft Live connect SDK](https://github.com/liveservices/LiveSDK-for-Android).

###Reference the Library

To compile the source code,...

* Download the repo as a ZIP file to your local computer, and extract the files. Or, clone the repository into a local copy of Git.
* Start Eclipse, if it is not already running.
* Choose "File > Import."
* Expand "Android." Choose "Existing Android Code into Workspace," and then click "Next."
* Choose "Browse" to select a root directory.
* Go to and select this folder within the downloaded project files, and then click "OK."
* In the **Projects** box, make sure both the "OneNotePickerLib" and "android-support-v7-appcompat" projects are selected.
* Make sure that the "Copy projects into workspace" check box is selected. Click "Finish."
  Eclipse adds both projects to the **Package Explorer** pane and then
  compiles the library in the background.
* You can now reference the compiled library from your Eclipse Android projects.

  
To reference the compiled library in an Eclipse Android project,...

* In Eclipse, display the **Package Explorer** pane, if it is not already visible.
* Right-click your project's name, and then choose "Properties."
* In the list of project properties, choose "Android."
* In the Library area, click "Add."
* Click "OneNotePickerLib" and then click "OK."
* Click "OK." Eclipse sets a reference to the compiled library source code
  project, and you can now call the "OneNotePickerActivity" from your own
  Android project.

**Note**: Before you run your project, you must add the Internet permission to your
      project's manifest, as shown in the following steps. If you don't add the
      Internet permission, your app may have problems accessing OneNote
      web services.

To add the Internet permission to your project's manifest,...

* Make sure that your project is open. In the **Package Explorer** pane, open the AndroidManifest.xml file.
* In the editor, click the **Permissions** tab.
* Choose "Add."
* Choose "Uses Permission," and then click "OK."
* In the "Name" list, choose "android.permission.INTERNET."
* Save the AndroidManifest.xml file.

### Using the library

The **OneNotePickerLibrary** provides a class called **OneNotePickerActivity.class** that extends Activity. You implement this class with an [Intent](http://developer.android.com/reference/android/content/Intent.html). To handle return values from this class, you need to implement the [onActivityResult](http://developer.android.com/reference/android/app/Activity.html) method.

####Input

Specify the required and optional properties for the **OneNotePickerActivity.class** by setting these "extras" by using the **putExtra** method on the **Intent**:

*  **ACCESS_TOKEN** (Required):  A **String** that specifies the access token to be used for authentication. See [Authenticate the user for the OneNote API](http://msdn.microsoft.com/en-us/library/office/dn575435(v=office.15).aspx) to learn how to authenticate your product.
*  **NAV_TEXT_COLOR** (Optional): A **Color** object that specifies the color of the properties in the action bar (the text, back arrow, and cancel button). If this is not set, then the default color will be used.
*  **THEME_COLOR** (Optional): An **Enum** value (**OneNotePickerThemeColor.LIGHT** and **OneNotePickerThemeColor.DARK**) that specifies the light or dark theme.

####Output

The activity returns a **resultCode** of either RESULT\_OK, when the user successfully selects a section or RESULT\_CANCELLED when the user either cancels the operation or the operation fails due to an error.

When the **resultCode** value is RESULT\_OK, the **OneNotePickerActivity** class returns the following values through the **getExtras** method on the **data** object returned by your implementation of the **onActivityResult(int requestCode, int resultCode, Intent data)** method:

* **SECTION_ID**: A **String** that specifies the ID of the selected section.
* **SECTION_NAME**: A **String** that specifies the name of the selected section.
* **PAGES_URL**: A **URL** that specifies the REST URL to use to create or get pages in the selected section.
* **CREATED_TIME**: A **Date** object that specifies the date/time when the selected section was created.
* **MODIFIED_TIME**: A **Date** object that specifies the date/time when any page in the selected section was last modified.
* **LAST_MODIFIED_BY**: A **String** that specifies the name of the user who last modified any page in the selected section.

When the **resultCode** value is RESULT\_CANCELLED, the **OneNotePickerActivity** class returns  a Boolean value for USER\_CANCELLED through the **getExtras** method on the **data** object returned by your implementation of **onActivityResult**. This means that the user has cancelled the operation by using the back arrow or the cancel button.

If the value of USER\_CANCELLED is false, the class also returns a Boolean value for API\_ERROR. If the value of API\_ERROR is true, the OneNote API has returned an error code and information about the error.

When the value for API_ERROR is true, the **OneNotePickerActivity** class returns the following values:

* **API\_ERROR_CODE**: A **String** that specifies the value of the error code returned by the OneNote API. See [OneNote API error and warning codes](http://msdn.microsoft.com/en-us/library/office/dn750990(v=office.15).aspx) for a list of possible error codes.
* **API\_ERROR_URL**: A **URL** that specifies the value of the explanatory error URL returned by the OneNote API.
* **API\_ERROR_STRING**: A **String** that contains a description of the error.

If the value of API\_ERROR is false, the platform has thrown an exception because of an error that originates on the device. In this case, the **OneNotePickerActivityClass** returns an **Exception** object.

When the values for USER\_CANCELLED and API_ERROR are both false, the **OneNotePickerActivity** class returns an exception through the following value:

* **SYSTEM_EXCEPTION**: An **Exception** object returned by the platform due to an error on the device.

####Example

The following example shows how to create a new **Intent** with the **OneNotePickerActivity** class, set the required and optional values, and handle errors and exceptions that might be returned:

```java
private void pickOneNoteSection() {
    
    //Create Intent
    Intent oneNotePickerIntent = new Intent(this, OneNotePickerActivity.class);

    //Set Properties
    oneNotePickerIntent.putExtra("ACCESS_TOKEN", "<Insert OAuth Ticket>");
    oneNotePickerIntent.putExtra("NAV_TEXT_COLOR", new Color().rgb(128,57,123));

    //Start Picker - Assumes that a value for "REQUEST_CODE" has already been set
    startActivityForResult(oneNotePickerIntent, REQUEST_CODE);
}   


@Override
protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    
    //PICKER COMPLETES WITH SELECTED SECTION
    if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {

        //LOAD VALUES
        String sectionID = data.getExtras().getString("SECTION_ID");
        String sectionName = data.getExtras().getString("SECTION_NAME");
        URL pagesURL = (URL) data.getExtras().get("PAGES_URL");
        Date createdTime = (Date) data.getExtras().get("CREATED_TIME");
        Date modifiedTime = (Date) data.getExtras().get("MODIFIED_TIME");
        String lastModifiedBy = data.getExtras().getString("LAST_MODIFIED_BY");

        //DO SOMETHING WITH THE INFO
    }

    //PICKER CANCELLED OR generated an error
    else if (resultCode == RESULT_CANCELLED && requestCode == REQUEST_CODE) {
        if (data.getExtras().getBoolean("USER_CANCELLED")) {

            //USER CANCELLED OPERATION.

        } else if (data.getExtras().getBoolean("API_ERROR")) {
            //API BASED ERROR. LOAD ERROR INFO
            String apiErrorCode = data.getExtras().getString("API_ERROR_CODE");
            String apiErrorString = data.getExtras().getString("API_ERROR_STRING");
            URL apiErrorURL = (URL) data.getExtras().get("API_ERROR_URL");

            //DO SOMETHING WITH ERROR INFO

        } else {
            //SYSTEM EXCEPTION. LOAD EXCEPTION
            Exception e = (Exception) data.getExtras().get("SYSTEM_EXCEPTION");

            //HANDLE EXCEPTION

        }
    }
}
```

### OneNote API functionality used by this library

The following aspects of the API are used in this library. You can 
find additional documentation at the links below.

* [GET notebooks to which the user has access](http://msdn.microsoft.com/en-us/library/office/dn769050(v=office.15).aspx)
* [GET section groups to which the user has access](http://msdn.microsoft.com/en-us/library/office/dn769052(v=office.15).aspx)
* [GET a specific section group to which the user has access](http://msdn.microsoft.com/en-us/library/office/dn770192(v=office.15).aspx)
* [GET sections to which the user has access](http://msdn.microsoft.com/en-us/library/office/dn769049(v=office.15).aspx)
* [GET a specific section to which the user has access](http://msdn.microsoft.com/en-us/library/office/dn770191(v=office.15).aspx)
