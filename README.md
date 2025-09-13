# Nufian Connect
<img width="300" height="300" alt="logo" src="https://github.com/user-attachments/assets/314b90cb-d254-4284-91f9-7ce44c8606a6" />

## Overview

The community of Sekolah Tinggi Teknologi Terpadu Nurul Fikri (STT-NF) continues to grow along with the increasing number of students and alumni; however, internal communication still faces challenges. Platforms such as Telegram groups and websites have not been effective in reaching all members, especially in terms of interaction across departments and academic cohorts. On the other hand, the high usage of smartphones in Indonesia presents an opportunity to build a mobile-based community platform. This research designs an Android-based mobile community application developed using the Design Thinking approach and the Extreme Programming methodology. The application features onboarding, forums, notifications, search, news, and profile management, and is integrated with Firebase. Black-box testing showed that all 59 test scenarios ran successfully. Usability testing with 12 users–consisting of seven students and five alumni–also indicated positive responses, particularly toward the forum, search, and notification features. With a total of 101 positive responses out of 108 received across the nine available features, this application is expected to serve as an integrated communication solution and a foundation for future development of a unified STT-NF system.

Nufian Connect is a community-based mobile application that aims to solve problems in communications and informations faced by students and alumni alike. This app is based on `Kotlin` and utilizes `Firebase backend services.`

## Features
+ Onboarding, Welcomes new users to the app, contains brief description and text about the app and it's goals.
+ Authentication, utilizes `firebase authentication` and secure registering logics, ensuring each accounts are meant only for the desired users.
+ Profile Creation, giving access to newly registered users to complete their profiles with, but not limited to, mandatory identification.
+ Welcome screen, pretty self mandatory, along with texts that changes according to the current timezone the users in.
+ Home/Forum, `main feature` of the app, giving users access to interact with each other via posts. Users can give `likes`, `comments`, and `'direct access to the poster's profile'`.
+ Discover, list of available users registered, along with a search bar.
+ News, currently manually fetched, this feature gives users access to the latest news `nurulfikri.ac.id` can offer.
+ Profile, serves as the face of every user, it allows user to upload `a profile picture`, `upload projects`, `certificates`, and `edit their profiles` with ease.

## Prototype

Prototype of the app can be accessed through the link below:

[🔗 View Nufian Connect Prototype on Figma](https://www.figma.com/proto/Soqe1O4wkGPZdS7M7Su3CZ/Nufian-Connect?node-id=107-770&t=goN2pTU4r9nKNzLQ-1)

## Demo

Due to privacy reasons and limited database quota, currently there are `no build available for public yet.` Though an app demo is possible through a very limited access and strict policy.

## Known Issues

+ Some visual glitches on the main screen on some `Xiaomi devices`, rendering the animation moves rapidly faster than it should.
+ `State switching bug` when switching account without restarting the app, giving all the desired permission on the previous logged in account, but not the current one. This however can be easily solved by relaunching the app.
+ Image and data caching not working as intended, making the app load the exact same thing over and over.


## Future plans (priorities based on the list)

+ Fixing all the known issues, especially the `state switching bug.`
+ Allowing public to access using a restricted account that only allows them to access certain posts and features.
+ Improves visual and entire user experience of the `Discover` feature, which, currently known, is very lacking.
+ Automatically fetches news data from the official STT Terpadu Nurul Fikri's website `nurulfikri.ac.id`.
+ Under the recommendation of STT Terpadu Nurul Fikri's Rector, Dr. Lukman Rosyidi, S.T., M.M., M.T. and my Thesis Supervisor, Ahmad Rio Adriansyah, S.Si. M.Si., this app is planned to be integrated to the `campus online systems` and released on `Google Play Store.`
+ Adding much more features such as chats, Near me locator for users, search based on categories and so on once all the known bugs are resolved.

<br/>

With love and countless cups of iced coffees, Mirezka [@miregitgud](https://www.github.com/miregitgud)
