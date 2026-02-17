# YouTube Analyzer & AI Backend

This is the backend service for the YouTube Analyzer & AI Platform.

It provides REST APIs to:

* Analyze YouTube channels
* Fetch latest uploaded videos
* Generate AI summaries from URLs (video, article, image, etc.)
* Generate YouTube video titles using AI

## Technology Stack

* Java 17
* Spring Boot (Spring MVC architecture)
* Spring Data JPA
* MySQL / H2 Database
* YouTube Data API v3
* Google Gemini API
* RestTemplate
* SLF4J Logging

## Features

### 1. Analyze YouTube Channel

Accepts a YouTube channel link or @handle and fetches:

* Channel Name
* Subscribers Count
* Total Views
* Total Videos

The data is stored in the database and automatically updated on subsequent requests.

### 2. Latest Videos API

* Fetches the latest 10–20 uploaded videos from a channel
* Stores video data in the database
* Automatically deletes older videos
* Always returns the latest videos first

### 3. AI Summary API

Accepts any URL such as:

* YouTube video
* Article
* Image

Process:

* Extracts content from the provided URL
* Sends it to Google Gemini API
* Returns:

  * Summary
  * Key Points

### 4. AI Title Generator

Input:

* Topic
* Style

Output:

* 5 engaging and catchy YouTube titles generated using AI

## API Endpoints

### Analyze Channel

POST `/api/youtube/analyze`

Request Body:

{
  "channelInput": "https://www.youtube.com/@LoLzZzGaming"
}

### Get Channel Videos

GET `/api/youtube/{channelId}/videos`


### AI Summary

POST `/api/ai/summarize`

Request Body:

{
  "url": "https://www.youtube.com/watch?v=abc123"
}

### Generate Titles

POST `/api/ai/generate-titles`

Request Body:
{
  "topic": "BGMI clutch moments",
  "style": "exciting"
}

## Setup Instructions

### 1. Clone Repository

git clone https://github.com/your-username/youtube-backend.git
cd youtube-backend


### 2. Configure application.properties

Add your API keys and database configuration:

youtube.api.key=YOUR_YOUTUBE_API_KEY
youtube.api.base-url=https://www.googleapis.com/youtube/v3

gemini.api.key=YOUR_GEMINI_API_KEY
gemini.api.url=https://generativelanguage.googleapis.com/v1/models/gemini-2.5-flash:generateContent

spring.datasource.url=jdbc:mysql://localhost:3306/youtube_db
spring.datasource.username=root
spring.datasource.password=your_password


### 3. Run Application

Using Maven:

mvn spring-boot:run

Application will start on:

http://localhost:8888

## Logging

* Full API logs enabled using SLF4J
* Includes logs for:

  * Channel API
  * Video API
  * AI API


## Notes

* Likes count may be 0 if the channel hides likes
* Shorts videos may not always return complete statistics
* Gemini API may return HTTP 503 when overloaded. Retry the request after some time


## Author

Developed by Sameer Siddiqui
Backend Developer – Java and Spring Boot

LinkedIn: [https://www.linkedin.com/in/sameer-s-653b7b265/](https://www.linkedin.com/in/sameer-s-653b7b265/)
