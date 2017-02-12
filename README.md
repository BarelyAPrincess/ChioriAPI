# Introduction
**Chiori's API** is a common API library used in various sub-projects developed and maintained by Chiori-chan, most notable being Chiori-chan's Web Server. The goal is to provide a flexible, yet durable API that employs the best programming practices and conveniences in one place. This library contains features such as a Application Loader, Config Backend, Plugin Manager, Account Manager, Database Manager, Event Manager, Simple Log, Permission Manager and much more!

Please read our official documentation located at http://docs.chiorichan.com/. It contains great advanced information and tutorials on how to use Chiori's API (and Chiori-chan's Web Server).

[![Build Status](http://jenkins.chiorichan.com/buildStatus/icon?job=ChioriAPI)](http://jenkins.chiorichan.com/job/ChioriAPI/)
[![Build Status](https://travis-ci.org/ChioriGreene/ChioriAPI.svg?branch=master)](https://travis-ci.org/ChioriGreene/ChioriAPI)

# How To Build
You can either build Chiori-chan's Web Server using the Eclipse IDE or preferably by using the include Gradle. Doing so is as simple as executing "./gradlew build" for linux users. Gradle will output the built files to the `build/dist` directory.

# Coding
Our Gradle Build environment uses the CodeStyle plugin to check coding standards in case you make mistakes.

* Please attempt at making your code as easily understandable as possible.
* Leave comments whenever possible. Adding Javadoc is even more appreciated when possible.
* No spaces; use tabs. We like our tabs, sorry.
* No trailing whitespace.
* Brackets should always be on a new line.
* No 80 column limit or 'weird' mid-statement newlines, try to keep your entire statement on one line.

# Pull Request Conventions
* The number of commits in a pull request should be kept to a minimum (squish them into one most of the time - use common sense!).
* No merges should be included in pull requests unless the pull request's purpose is a merge.
* Pull requests should be tested (does it compile? AND does it work?) before submission.
* Any major additions should have documentation ready and provided if applicable (this is usually the case).
* Most pull requests should be accompanied by a corresponding GitHub ticket so we can associate commits with GitHub issues (this is primarily for changelog generation).

# License
Chiori Web Server is licensed under the MIT License. If you decide to use our server or use any of our code (In part or whole), PLEASE, we would love to hear about it. We don't require this but it's generally cool to hear what others do with our stuff.

Copyright (c) 2017 Chiori-chan <me@chiorichan.com>
Copyright (c) 2017 Penoaks Publishing LLC. <development@penoaks.com>

All Rights Reserved.
