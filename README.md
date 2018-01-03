# ScanSD  -- Author:  Scott Pan
coding quiz for "Write an app that scans all files on the external storage (SD card) and collects information"

Goal:
Write an app that scans all files on the external storage (SD card) and collects following information:

1) Names and sizes of 10 biggest files

2)  Average file size

3)  5 most frequent file extensions (with their frequencies)

============================================================================

Functional Acceptance Criteria:  

1) This information should be displayed in a convenient way.

2) App allows the user to start and stop scanning. 
Hint: Clicking the STOP button will bring up the dialog, with 3 choices (Stop Completely, Pause, Cancel).  Selecting the Stop Completely will change the button to Start, and will clean up all the collected data.  Selecting the Pause will change the button to Resume, which allows the user to click to resume the scanning.

3) The app must display progress of ongoing scan.

4) App contains a button allowing the user to share obtained statistics via standard Android sharing menu (button is not active until the activity receives the statistics from the server). 
Hint:  It should invoke the Android’s default Sharing menu, including Gmail, mail, Facebook, Twitter, etc.   Also, if the user selects Email, the data should be copied from the 3 TABs into the email.

5) App shows a status bar notification while it scans the external storage. 
Hint:  For every 100 files processed, the app should send one new status bar notification to replace the old status bar notification, and it should include “NNN files processed”.

6) UI must handle screen orientation changes.

7) When app is sent to background (by pressing HOME button), the scan should continue.

8) When app is stopped by the user (by pressing BACK button), the scan must be stopped immediately.    

============================================================================

Technical Details:

1) Project must be in Android Studio.

2) Project must use all the recent (new) Android SDK features.

3) Project must support all versions starting from Android 4.0.

4) Accomplished project must be posted on Github for review.



