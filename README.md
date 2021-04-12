# PhotoTrek
Project for our Mobile App Development class: PhotoTrek.
Phototrek provides photographers with a beautiful and easy-to-use interface, so they can organize their photos and see where they were taken. The application is an upgrade from the standard photo gallery, because it provides a map with markers identifying the geolocation of each photo. Phototrek is suitable for any photographer that is interested in keeping geolocation information attached to their photos for easily viewing later.  

### Group members
- Deon Best
- Daniel McCardle
- Alexandre Moreira de Carvalho

### Instructions to run
PhotoTrek requires a Google Maps API key to run. Please copy the key provided in the report submitted to D2L, and paste the following line in Gradle Scripts/local.properties
MAPS_API_KEY=KeyProvidedinTheReport

### Supported API level
Android 23 (Marshmallow) to 28 (Pie).

## Feature List
The following list of features are fully implemented and available in PhotoTrek: 

- Taking a photo from within the app. 
- Selecting an album for the photo to be placed within. 
- Set a description for each photo taken. 
- Deleting photos. 
- Changing photos associated album. 
- Editing a photos description. 
- Creating new albums. 
- Deleting albums. 
- Renaming albums. 
- Viewing an album’s photos and their associated map markers. 
- Viewing a map with a complete set of map markers from every album. 
- Clicking markers opens the associated photo. 
- Viewing a photo shows the date, location, and description of the photo. 
- Viewing photos in fullscreen. 
- Syncing photos taken in PhotoTrek with the gallery. 

## Known Issues
The following is a list of known issues in PhotoTrek that the development team was unable to fix due to time constraints or uncertainty about how to fix: 
- Album cover photo issues: Some unexpected behaviour can occur when editing which album a photo belongs to. In some cases, moving a photo to an album with no photos currently in it will result in the cover image not being updated in the destination album. The cover image is supposed to be the most recent photo added to that album, but when moving photos into and out of empty albums it will not be updated. 

- Map refreshing: When deleting a photo and then going back to the full map view, the marker for that photo will still be present and clickable by the user. If the user clicks on this marker, they will receive an error message via a Toast. This marker will only disappear after the map activity has been restarted or refreshed using the floating action button. 
