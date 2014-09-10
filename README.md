Download .apk at http://goo.gl/i9SZdK

Functionalities based on screens:
=========

* Currency rates. Shows list of today official currency exchange rates.  When one item picked it compares selected currency with Lithuanian litas.
* Currencies exchange. Shows how much one currency costs in compare to other. 
    * You can swap current amount of currency to another side (from "first currency" to "second currency" and vise-versa) 
	* You can pick desired currencies from a pop-up list.
* History. Displays history of watched currencies.

Technical features:
=========

* Displays on or two panes, depending on screen size.
* Checks network connection at start and at runtime.
    * If no network is available then new activity starts, which waits till network connection is active.
	* Network state changes are received by BroadcastReceiver and app knows if connection is active at run-time. 
	* Connection state is being checked while attaching all fragments.
	* Allows screen rotation on all screens. 
	* Saves conversions, so that they can we view after app close.
		* The conversions are saved at screen rotation, back button pressed or menu item selected (all situations that step though onStop() state)
		* The conversions are saved in file. Because use of Gson (Google JSON) it is simple to save ArrayList of HistoryItems as a string. So basically there is reading a string from ant to file.
		* The HistoryItem, that is candidate to being saved, is checked so that it isn't the last item in ArrayList.

	* Communication between fragments is made through Activities by various solutions: 
		* by implementing interfaces, 
		* storing objects in parent activity and retrieving them in child fragments,
		* saving information in file (i.e. conversion save),
		* passing objects to bundles or intents. The object conversion to string and vice-versa is made by Gson (Google JSON).
	* The currency conversion is handled in static CurrencyConverter class
	* The server response is downloaded using asynchronous tasks. The CSV response is being handled by CSVReader, using "Windows-1257" charset.
	* Selected menu items are highlighted.
