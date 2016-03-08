# Popular Movies 

View most popular and highest rated  movies provided by TheMovieDB.org.

## Installation
This app requires API key for TMDB.org. You can get it from here [TMDB API](https://www.themoviedb.org/documentation/api "TMDB API")

Before running the app please replace `TMDBAPIKEY` with your TMDB API key value in `app/build.gradle`


```
    buildTypes.each {
        it.buildConfigField 'String', 'TMDB_API_KEY', TMDBAPIKEY
    }

```

## Learn
This app was created as a project under _Udacity, Android Developer Nanodegree_ course.

Things you can learn:

* Fetch movie list from TMDB database and display in grid. 
* Switch between "Most Popular" and "Highest Rated" using overflow menu.
* Mark favourites and store in database. 
* Show videos and trailers for movies.
* Add two pane layout for tablets
* Share intent for video on movie details screen.


## Screens
<img src="https://cloud.githubusercontent.com/assets/13112999/13419148/f42e065a-dfa1-11e5-9420-cb02da1c5d37.png" /> 
<img src="https://cloud.githubusercontent.com/assets/13112999/13419145/f1b0c520-dfa1-11e5-9e55-3bc88eb8a6aa.png" />

## Video
<img src="https://cloud.githubusercontent.com/assets/13112999/13418989/c75ae73e-dfa0-11e5-8cea-8bbd4d3d06f8.gif" />

## Licence 

The MIT License (MIT)

Copyright (c) 2015 Ashesh Bharadwaj

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.