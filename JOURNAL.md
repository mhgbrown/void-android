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

## Mini II
## Week 7

I was unable to attend this class meeting.

## Week 8
### Deliverables
* IRB Completion Report
* Research resources
    * [http://en.wikipedia.org/wiki/Omegle](http://en.wikipedia.org/wiki/Omegle)
    * [http://en.wikipedia.org/wiki/Chat_Roulette](http://en.wikipedia.org/wiki/Chat_Roulette)
    * [http://en.wikipedia.org/wiki/Snap_Chat](http://en.wikipedia.org/wiki/Snap_Chat)
    * [http://techcrunch.com/2013/03/18/rando/](http://techcrunch.com/2013/03/18/rando/)
    * [http://www.ustwo.co.uk/blog/introducing-rando/](http://www.ustwo.co.uk/blog/introducing-rando/)
    * [http://www.businessinsider.com/whisper-app-secrets-2013-5?op=1](http://www.businessinsider.com/whisper-app-secrets-2013-5?op=1)
    * [http://www.huffingtonpost.com/2013/06/05/whisper-app-reveals-lgbt-_n_3389502.html](http://www.huffingtonpost.com/2013/06/05/whisper-app-reveals-lgbt-_n_3389502.html)
    * [http://en.wikipedia.org/wiki/Tinychat](http://en.wikipedia.org/wiki/Tinychat)
    * [http://en.wikipedia.org/wiki/Zumbl](http://en.wikipedia.org/wiki/Zumbl)
    * [http://en.wikipedia.org/wiki/Text_roulette](http://en.wikipedia.org/wiki/Text_roulette)
    * [http://techcrunch.com/2013/06/17/you-have-no-friends/](http://techcrunch.com/2013/06/17/you-have-no-friends/)

### Status
Working to catch up on deliverables assigned in the previous week as well as ones assigned for next week.
There are also a few things that I want to do to improve my app for this week:
* Improve RESTfulness of service communcation
* Verify image orientation bug

### Progress
On the app side, I have managed to get a few cool things done. I have successfully made the server end points more restful, which helped to clean up some code a bit. I have also added the ability to delete photos from your stream. Photos that have been deleted will not reappear in your stream.

In addition, I have been able to do some reasearch on applications that are similar to Void, like Rando, Snapchat, ChatRoulette etc.

## Week 9
### Deliverables
* [Draft Business Evaluation](https://docs.google.com/document/d/1x8HvxUu1u6RLO9G1TPI1WHIiDlxo7ZSXcEN7M0mXh7Y/edit?usp=sharing)
* Market Exploration
* Android:
    * 64% of global smartphone market as of March 2013
    * "During the third quarter of 2012, Android's worldwide smartphone market share was 75%, with 750 million devices activated in total and 1.5 million activations per day."
    * Of that market share ~98+% are using Android 2.2 or above. Void is designed for 2.2 and above.
    * Seems like conflicting reports, but, it's definitely up there.
    * Android's market share is huge and appears to be growing quite aggressively. A huge market is getting huger. However, not everyone will have
    * http://en.wikipedia.org/wiki/Android_(operating_system)#Market_share_and_rate_of_adoption
    * A very similar App, Rando, was downloaded 40,000 times a month after it was released with 600,000 photos uploaded.
    * http://thenextweb.com/apps/2013/04/11/randos-randomized-photo-sharing-anti-social-network-launches-on-android/
    * The app was only released in March, which might be contributing to my difficulty finding user base numbers for it.
    * In any event, the idea seems viable
* Ways to get to market
    * Networks!
    * Friends working at prominent tech companies who work on/pay attention to new apps tha are coming around
* Values of Market Testing
    * Nail down viability although somewhat proven with Rando
    * The big thing to determine would be if people are motivated enough to send a photo into the void to get one back This is one of the biggest unknowns for the app. Just getting a photo back might not be enough.
    * It would be interesting to explore a system of kudos or some sort of voting for media. I'm not sure how this would affect the posts, but the market might have some ideas. Some initial user testing would be good for this.
    * Could discover if users would like to share other types of media or if theyd like to keep some of the media that they get/post. The point of the app is to no keep it, so probably would not make any changes in that regard.
    * The rate of "abuse" would also be interesting to see. How many people are posting pornographic or inappropriate imagery? How much of a problem is this if you can delete a post? How do user's sentiments change when they see inappropriate content?
    * Is there potential legal trouble with not tying a user to something identifiable (like an email)?
* Name Motivation: Void
    * I don't have any strong feelings that I should change the name.
    * A Void is like an empty space that you can throw things in and not necessarily get things back; a user throws their content into the void, forever losing it
    * Void is also the absence of type, identity, matter, etc. Every user is essentially "void" as their identity is completely irrelevant.
    * The app leverages the idea of opening the void to send things into it with the black photo taking interface
* Righteous Feature Set
    * Sharing a photo and getting a random one back
    * Deleting a photo
    * Flagging a photo
    * Expand media beyond photos. Asked some friends and they indicated that video might be cool. I was thinking text a la Whisper app might also be interesting...although might tie in a weird confessional aspect that I am not going for. Audio clips might also be an interesting thing.
* What do I not know how to do?
    * Scalability might be a little tricky if this thing gets all big. Handling media efficiently is difficult.

### Status
At the beginning of the week, I completed the ability to delete photos from the app. I have also been working on creating some sort of draft business plan to aggregate all this important information about market size, etc.
Progress

At the end of the 3rd week, I have managed to:
* Flesh out research/information pertinent to the business plan
* Plan the initial user test and survey
* Improve the iconography within the app
* Reduce the memory consumed by bitmaps to avoid out of memory errors
* The iconography improvements really help bring a sense of polish to the app that was missing before. I am very happy I could get this accomplished.
* The next thing I want to improve is the onboarding process, no matter how trivial it might turn out. Currently, there is none.

## Week 10
### Deliverables
* Demo (Live)
* [Draft Business Plan](https://docs.google.com/document/d/1x8HvxUu1u6RLO9G1TPI1WHIiDlxo7ZSXcEN7M0mXh7Y/edit?usp=sharing)
* [ User Test Survey](https://docs.google.com/forms/d/1aasriQuquRbGkY_axKmjFSNbuLKwvtdfvhZmLAOOUjs/viewform) | [Responses](https://docs.google.com/spreadsheet/ccc?key=0AgYQh22MtQiQdFlPQ3djQW0zaVEtVTlGZ1U5VzE5b2c&usp=sharing)
* Further research/considerations
    * Based on the fact that "disclosure begets disclosure", Void is an attempt to motivate random users to share photos to receive photos.
    * [Ref](http://books.google.com/books#v=onepage&q=disclosure begets disclosure&f=false?id=osTqcaiflJQC&pg=PA307&lpg=PA307&dq=disclosure+begets+disclosure&source=bl&ots=HISnx60Pf0&sig=zluG2fHbeYAjwSbOaBzC8SuoqFM&hl=en&sa=X&ei=6XrsUai7BqeYigLArYGAAg&ved=0CC0Q6AEwAA)
    * [More](http://www.psychologytoday.com/blog/the-young-and-the-restless/201108/disclose-yourself-how-intimate-disclosure-fosters-attraction)
* How would I make Void survive the long haul?
    * Ultimately, it's going to need to make some money or generate some buzz around an entity that can then make some money
    * If Void becomes some sort of wild success, it could open the doors for the creating company to leverage that network to gather income from a future app
    * You could also have advertisers add random photos to the available streams, while somehow leveraging an enforcement that it is not too obvious or something.
    * Again, users always have the ability to delete a photo from their stream
    * Introducing new types of posts (video, text, etc) might keep attention
    * Or an iPhone version
    * Or a web version
    * Turn into a big anonymous media company
* What might be the business use of Void?
    * An obvious use would be something like an internal photos sharing platform, something like what Yammer does for companies.
    * Would be an interesting way to collect photos from within a company. It might feel a little more relevant since it's all occuring in a place that the user is familiar with.
* Where else might the app be useful?
    * The app might be useful if you could collect random photos from a specific area or event
    * Then you could capture the whole experience of something without having to go all over the place
    * Honestly, there is no utility in it...purposely. It's a fun experimental app
* How is Void not going to be crowded out by other apps? How does void differentiate itself in the market?
    * There is already one app, Rando, which does something very similar to Void, very well and there seems to be a lot of interest/buzz around it
    * It was created by a game company in the Uk, so they already had some leverage in the market
    * There is only one of these apps that I know of
    * Rando requires an email and a password. Void was specifically designed to avoid thess things to lower the barrier to entry.
    * Just take a freaking photo!
    * It also makes things seem more anonymous - an email obviously identifies you
    * One consideration is if/when users switch phones or use void on another device, how will they recover their stream?
    * They will ask the app to tell them who they are, which will be some sort of weird string...or maybe like heroku, come up with some random, but memorable name.
    * [With each step in the flow of using an application, there is a probability that users will drop off.](http://blog.mixpanel.com/2009/06/10/introduction-to-analytics-funnel-analysis/)
    * The ability to trade photos with another user would be interesting.
    * You might initiate a random trade, which pulls up a notification on the receivers device and prompts them to trade
    * You might initiate a targeted trade with another user...but this sort of violates the anonymity thing, but only to the extend that you know the other persons username...but then you probably actually know the person too, so its null and void
    * Rando forces you to take a photo before you receive one. Void could flip this. When a user first installs and opens the app, theyll get a random photo. The onboarding process could use this to prompt them to take another. It might also elicit more desire to share since "disclosure begets disclosure".
    * Incorporate some sort of props system that increases the liklihood of good photos getting distributed
    * [http://www.androidcentral.com/rando](http://www.androidcentral.com/rando)
    * "The quality of the pictures I've received is about as poor as what I've sent. Almost like everyone's sending in crap, hoping to get a bit of skin in return."
* How will you deal with inappropriate imagery?
    * The reality is people are going to post inappropriate things
    * Users already have the ability to delete posts. So, if they came across an inappropriate post, they could delete it
    * It might also be useful to integrate some sort of flagging feature so that one might tag something as inappropriate
    * Then users could opt in to inappropriate content via some sort of setting.
    * There is definitely a potential for abuse; users might flag things as inappropriate that aren't actually...which could lead to normal photos not being distributed to users that have opted out of inappropriate things
    * You could implement some sort of Human review process with mechanical turk or some such thing
    * You might also come up with some algorithm to judge inappropriate posts based on the number of flags and whether that user was opted in to inappropriate content.
    * When humans are involved it's going to be hard.
    * Some companies have algorithms to detect inappropriate content - mostly for pornography by detecting lots of skin tones. Other inappropriate content would pass through this, violence, etc.
    * What other righteous features might I include?
    * Share your feed with others
    * Exchange/trade photos between user's feeds
    * Still must follow the photo for photo rule. Can't just add things arbitratily to other people's feeds.
    * Be able to use Void in a web browser/mobile browser [Chrome on Android now supports WebRTC](http://www.thrupoint.com/2013/03/fusion-web-on-mobile-devices-android-webrtc-for-chrome/)
    * Give kudos to things to get more things like it?
    * I don't like this because it's not random. You can always delete a photo from your stream and get another one.
    * A curated stream is an interesting problem though, I just don't think it has a place with Void.
    * Take short videos
    * Recover the identity of the user so they can move to other devices/platforms and retain their feed

### Status
By class time of the week I have done the following:
* Created a user survey and elicited some responses to it
* Accumulated a user base of 8
* Improved the iconography of the application
* Reduced the memory usage of photos in the stream
* Fixed an issue with duplicate photos in the Stream
* Finished a rough draft of my business plan
* For the rest of the week, I plan to improve my draft business plan with the feed back that I got in class. In addition, I'd like to implement at least some of the improvements that have been suggested to me by my pilot user group. One big problem that I am seeing in detecting and logging errors on devices. It's going to be near impossible to fix the app's problems on different devices if I can't see how it's failing.

### Progress
I finished up week 4 by thinking about the things that I want to complete with my app before the final class. A few important things came to mind:
* Improving the reliabiltity of the camera and picture taking interface
* Feedback and experiences with other devices have revealed some problems
* Fixing the aspect ratio of the camera preview
* The 1:1 ratio appears sqaushed
* A possible solution would be to use the 4:3 ratio and crop the preview and the resultant image
* Tweaking the design based on feedback from a Designer (someone who was involved in G+'s redesigned profile)
* Adding the ability to like photos along with some sort of algorithm to surfaced liked photos more often than not liked photos
* Exploring list view pagination and caching.

## Week 11
### Deliverables
* Presentation to Dean

### Status
A lot of my time has been spent cleaning up the app, mostly with small visual and or functional changes. I spent a lot of time reimplementing the camera preview and camera functions to properly manage and release camera resources. Next, I'd like to move on to getting the liking functionality going. I also plan on redistributing the app to some of my friends so they can put it through its paces again

### Progress
I spent an unfortunate amount of time trying to get a loading animation going for images in the stream and then ended up not using it because of memory concerns. That definitely sucked.

I've tweaked a couple small functional things with the app to make it a little more user friendly. For example, istead of taking you back to the camera preview when an upload fails, I let you just resubmit the photo that you took. I've also completed the initial backend requirements to support liking of photos. This was very straightforward. Later, I began implementing liking on the Android client and got some basic communication going between it and the server, which helped me debug some server issues :)

## Week 12
## FINAL DELIVERABLES
* [Buisness Plan](https://docs.google.com/document/d/1x8HvxUu1u6RLO9G1TPI1WHIiDlxo7ZSXcEN7M0mXh7Y/edit?usp=sharing)
* [Video](http://www.youtube.com/watch?v=lMuTwL10RSA)
* [App Release](https://play.google.com/store/apps/details?id=io.morgan.Void&hl=en)