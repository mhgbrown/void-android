# Development Journal

# Weeks 1 & 2
## App Ideas

* Bill Splitter
  * Description
      * Split a receipt with friends quickly and easily. Take a picture of a receipt, tell the app which of your facebook friends are present, and assign receipt items to your friends. Charge and pay people through the internet payment serivice Venmo.
  * Motivation
      * I am consistenty and painfully annoyed by multi-person payment situations with complex sub divisions. If everyone has a different method of payment they want to use, the restaurant only takes 2 credit cards, and no one wants to divide the bill evenly, then you have a nightmare on your hands. The calculations get crazy, people get confused, and now you've just spent an extra 30 mintues in the restaurant that could have been used at the bar down the street.
      * From some Googling, it appears (unsurprisingly) that there are a lot of apps that deal with bill splitting. Some even do cool stuff with OCR, like I have proposed. However, none of the apps seem to make use of a payment service, facebook connectivity AND OCR. I think this would be a novel entrance into the market.
  * References
      * [https://venmo.com/payouts](https://venmo.com/payouts)
      * [https://developers.facebook.com/docs/reference/apis/](https://developers.facebook.com/docs/reference/apis/)
      * An app that uses OCR for receipt recognition: [https://play.google.com/store/apps/details?id=com.cleverturtles.splitter&hl=en](https://play.google.com/store/apps/details?id=com.cleverturtles.splitter&hl=en)
* Quick BAC Calculator
    * Description
        * Enter some basic information about yourself and then get a quick estimate of your BAC with an easy to discern go/no-go driving signal.
    * Motivation
        * Am I OK to drive now? How drunk am I really now? How much more can/should I drink? This app attempts to answer all these questions given some simple personal information and the number and type of drinks that have been consumed. The goal is to give people more information to help their potentially drunken information. In addition, it can serve as way to more reliably predict your drunkeness or just as a tool of interest.
        * There are quite a few BAC apps out there, but many of them seem to be a little cluttered/complex. I want to provide an interface that a drunk person could use.
    * References
        * [http://bloodalcoholcalculator.org/](http://bloodalcoholcalculator.org/)
        * [http://aztechbeat.com/2013/05/axxess-interlock-app-helps-to-prevent-drinking-and-driving/](http://aztechbeat.com/2013/05/axxess-interlock-app-helps-to-prevent-drinking-and-driving/)
* Random anonymous photo sender
    * Description
        * Send a photo into the void and receive a random photo from another user. The only information that is sent with the photo is the location of where it was taken. You get a random photo only when you take a photo.
    * Motivation
        * This idea is just sort of an interesting social experiment to see what people send. It would require the construction of some backend service to store and distribute the photos, which I think might be not so hard to construct. It's just sort of like a random blog with photos only.
    * References
        * An app that already does this that markets itself as a way to collect photos from around the world: [https://play.google.com/store/apps/details?id=com.ustwo.rando&hl=en](https://play.google.com/store/apps/details?id=com.ustwo.rando&hl=en)
* Random anonymous "tweet" sender
    * Description
        * A variation of the aforementioned idea that only uses text. it works the same way, but only allows users to share text.
    * Motivation
        * Again, sort of another interesting social experiment. A big player in this space in Whisper, which allows users to anonymously "tweet" with some photo attached. It looks like it allows anyone to just scroll through a list of ther users posts. My variation would only allow you to see another post when you have posted something. This sort of turns the phone into an agent of disclosure, which has been found to beget more disclosure from the recieving party. My variation would also attach location information.
    * References
        * A big presence in this space is this app, which allows you to send random anonymous "tweets": [https://play.google.com/store/apps/details?id=sh.whisper&feature=search_result#?t=W251bGwsMSwyLDEsInNoLndoaXNwZXIiXQ..](https://play.google.com/store/apps/details?id=sh.whisper&feature=search_result#?t=W251bGwsMSwyLDEsInNoLndoaXNwZXIiXQ..)
* "Local" Stackoverflow
    * Description
        * Help the people around you find answers to their questions. The poser of the question has an option to give kudos to another user or even send them money. Using your location, the app discovers other users around you that have posed questions. You'll have the opportunity to answer any of them. The poser can set the scope of their question to the place they are at or an increasing mileage radius.
    * Motivation
        * In a city like San Francisco, there are lots of things to do and see. This app could help tourists ask locals about directions or even recommendations for food, bars, etc. This is a great way to get some solid information for the locals who know the most and in real time (hopefully). Of course, the app isn't restricted to tourists, really any question can be asked.
        * There doesn't seem to be a lot of entries into this market, which makes this idea interesting!
    * References
        * Ask.com looks like they explored this a little bit: [http://techcrunch.com/2010/09/22/ask-iphone/](http://techcrunch.com/2010/09/22/ask-iphone/)
* Last Minute Add On: Microphone App
    * Description
        * Instead of lining up on at a mic to ask a question, use your smartphone to stay where you are.

## Week 3
### Evolved Idea: Void

* A random "media" aggregator with some sort of kudos system
* A combination of "Random anonymous photo sender" and "Random anonymous "tweet" sender" with the possibility of adding audio and video in the future
* Users will have the option to remove posts from their stream

### Research
This app will require a backend service to coordinate the distribution of posts. I propose to use Rails since I am very familiar with it and there is a healthy support communty. Additionally, there are many Rails adapters for 3rd party services and hosting Rails apps in the cloud is a snap ([Heroku](https://www.heroku.com/)). The backend will need to persist its user's data. Simple text posts can be handled by a relational database, but photos are a bit more complex. Ideally, I'd hook up the app to [Amazon S3](http://aws.amazon.com/s3/) or [Google Cloud Storage](https://cloud.google.com/products/cloud-storage). As much as I'd like to stick with Google while developing on Android, there is a prominent attachment plugin for Rails called [Paperclip](https://github.com/thoughtbot/paperclip) which can hook in to Amazon S3 to persist resources.


Crafting a text HTTP POST is pretty straightforward, but sending an image is not. Fortunately, it looks like Android [supports multipart posts](http://stackoverflow.com/questions/2935946/sending-images-using-http-post).


The Rails backend will also need to communicate with its Android clients to notify them that a new post has arrived. Google provides [Google Cloud Messaging](http://developer.android.com/google/gcm/gs.html#android-app) to accommodate such a use case.


Every user is going to need an account (even though their identity is hidden) and will thus need to be able to log in/create an account via the app. Here are some things that might point me in the right direction.

* [http://www.androidhive.info/2012/01/android-login-and-registration-with-php-mysql-and-sqlite/](http://www.androidhive.info/2012/01/android-login-and-registration-with-php-mysql-and-sqlite/)
* [http://stackoverflow.com/questions/9901052/how-to-use-web-services-in-android-application](http://stackoverflow.com/questions/9901052/how-to-use-web-services-in-android-application)
* [http://stackoverflow.com/questions/7086746/android-app-that-consumes-a-webservice-how-to-authenticate-users](http://stackoverflow.com/questions/7086746/android-app-that-consumes-a-webservice-how-to-authenticate-users)
* [http://stackoverflow.com/questions/16276602/android-webservice-authentication](http://stackoverflow.com/questions/7086746/android-app-that-consumes-a-webservice-how-to-authenticate-users)


Maybe there is a way to do this without needing a user to create an account? How can I identify a user uniquely with the information I have access to on the phone? Ooh, this is interesting...


Update: I think I'm going to go without a login. If a user want's to maintain his or her own stream on another device, they can ask the app for their identifying information. I want the barrier for entry to this app to be very low, thoughtless.

### Wireframes
[Here](https://github.com/discom4rt/void-android/blob/master/doc/crude wireframes.pdf)

The main idea is that we have a stream and a screen that can be activated to post things. The stream displays all the random media that you have received with location information. You can double tap the post to give kudos and swipe the media left or right to remove the post (with confirmation). Clicking on the inverted triangle opens the "posting area" where users have the option to take a photo or enter some text. The users location information will be displayed at the bottom left corner of the media that they post. Once a user confirms a post, there is some uploading progress indication and then they are taken back to the stream. The stream will somehow indicate that a new random post is available and the user will pull down the stream to get that post.

### Server
I've created a basic splash page for now and hosted in on Heroku. It's [here](http://void-server.herokuapp.com/). [The server code is hosted on Github](https://github.com/discom4rt/void-server).

### Client
[The code is hosted on Github](https://github.com/discom4rt/void-android). I try to push at every point that I have made reasonable progress.

## Week 4
### Status
This week I started by focusing on the main interaction for the app, which is "opening the void" and taking a picture. As of today (6/12/2013) I have the basics down:

* Push the void button to open the camera interface
* Start the camera preview
* Take a picture
* Return to camera preview
* Return to stream, closing the camera interface

Developing this interaction was more work than I was expecting, but I think it has taught be a lot about animations, using the camera, and layout. There are a few specific steps that need to be taken to start the camera, reinitialize, stop the camera, etc., that are not exactly intuitive. I did not go the intent route, since I wanted to embed the camera in my application. Perhaps in the future I will get to try out the intents.

### Next Steps
I would like to flesh out the rest of the primary action for the remainder of this week. In particular, I want to focus on the following things:

* Get user's location information at the city/town level and display it under the photo preview
* Persisting the photo
* Sending the photo the server
* Saving the "post" on the server
For now I am going to stick with sending pictures. This is the most complex of my media cases so far and I think it will be easy to send some text later. Next week I will move on to the stream.


It would be great if could post videos of my app working while it was in progress. I will try to figure out a way to do that.

### Progress
At the end of this week I was able to get the user's location as <city name>, <country> and display it under the photo preview. One thing I learned about location is that you need an internet connection to determine the user's locality, which means it's hard to test if you are in the back seat of a car driving somewhere. In the future, I might [detect if there is an internet connection available](http://stackoverflow.com/questions/4238921/android-detect-whether-there-is-an-internet-connection-available). This was not as far as I had expected to get, but that's because I ran into some issues with the camera on the actual Droid device. 1) The preview wasn't restarting after "closing the void" and 2) the camera was oriented incorrectly. These were pretty simple fixed, but took some reasearch time.


I also ended up reserching methods to have to get around a user login. The lack of a login generates a sense of anonymity, which goes along with the whole premise of the app. It looks like I might be able to [do something with the device and installation ids](http://stackoverflow.com/questions/2785485/is-there-a-unique-android-device-id).


I also found a great toturial on how to use [Paperclip on Heroku with S3](https://devcenter.heroku.com/articles/paperclip-s3), which is exactly what I need to do! Yay!

## Week 5
### Deliverables
#### Poster Outline

* Void, Share Anonymously, Receive Randomly (Abstract)
* Quick 1, 2, 3 of how the app works (Introduction)
    * 3 Screen shots
    * Taking a picture
    * Getting the signal that you have a new photo
    * Seeing that photo in the stream, make it apparent that it's not yours
* Development/Architecture (Method)
    * Android Studio
    * Rails + Paperclip + Heroku
    * S3
* Learnings (Results)
    * The development environment is nice
    * Hard to test more advanced features on emulator (location, camera)
    * ...
* Future work (Discussion)
    * Add ability to post other media types
    * Clean up the design
    * Improve anonymous user identification (haha)
    * Recover your stream on another device
    * iPhone version :)

#### Video Outline

* Elaborate on the three screenshots on the poster
* Run through the flow
    * Open the void
    * Take a picture
    * Send it
    * Wait for a random one to come in
    * Display it
* Keep it simple and fast

#### Elevator Pictch
Void's tag line is "share anonymously, receive randomly". For now, you can share photos tagged only with your current location. For each photo that you share, you will receive a random one from another user, which is permanently added to your stream. Your stream is ultimately made up of photos from other users.

### Status
During the beginning of this week I want to finish the following things:
Persisting the photo
Sending the photo the server
Saving the "post" on the server
As of the classmeeting, I have the app posting the photo and the location of the photo to the server.
For the future [Eventually I am going to have to render a photo on the phone itself](http://stackoverflow.com/questions/541966/how-do-i-do-a-lazy-load-of-images-in-listview).

### Next Steps
I've made some good progress as of the class meeting, but still have to do a significant amount of work. In particular I need to work on building out the backend to identify and users and save information about the posts that they have created. Simiarly, I have to settle on an identification scheme for the user. I am heavily leaning towards some sort of anonymous random id since it's both easy to implement and sort of goes along with the anonymous nature of the application. [It looks like there are some interesting ways to do this](http://stackoverflow.com/questions/2785485/is-there-a-unique-android-device-id). Another big piece is going to fetching and rendering the user's stream.

### Progress
I've completed the app in its most basic form. The app registers a user with a random UUID, which is used to keep track of their posts and stream for the future. The app displays the user's stream on start or a message to take a photo if there are no items in their feed. Building out the backend to coordinate the user's stream and get random posts was actually fairly easy with Rails. Paperclip made it a breeze to save the photo in S3.


I also had the chance to do some UI work, like [remove the selection animation on list items](http://stackoverflow.com/questions/3506976/what-is-the-default-drawable-for-pressing-a-list-item) and remove the default button styling. The buttons/icons aren't exactly styled as I would like them to be ideally, but they work pretty well. One thing I struggled with and was unable to do anything about was removing the padding from buttons so that I could fit larger font sizes in them. I would really like to fix this in the future.


I've posted a video of me demoing the app, which can be found [here](http://www.youtube.com/watch?v=zQQo_gvWBq8). I might want to try re-doing it so that it isn't oriented incorrectly.

## Week 6
### Deliverables
* Source Code
    * [Server](https://github.com/discom4rt/void-server)
    * [Android Client](https://github.com/discom4rt/void-android)
* Poster
* [Demo video](http://www.youtube.com/watch?v=zQQo_gvWBq8) (excuse the orientation)

### Status
Since this is the final week, I will be focusing on cleaning up the app, funtionally and stylistically. There are actually no big outstanding bugs, but rather just things that might need some polish, internally and externally.

* Remove any code smells
* Play with/fix button styling
* Fill up my database of post with better photos

### Progress
With the help of some friends, I have ironed out a few bugs with the backend and the front end. I fix a problem with the randomization algorithm that would return an offset outside of the bounds of the total number of available posts. I fix some bugs with loading photos on the phone and Exception detection and passing. There is still one bug that alludes me. Sometimes images ill show up in the wrong orientation in the feed...but it seems like only on some devices. This is something I want to fix! I didn't get a chance to improve the styling/iconography much since I've been pretty frustrated the process of doing that.

The latest version of my app can be downloaded [here](https://www.dropbox.com/s/y4945hwxlrwvzrk/Void-debug-unaligned.apk).