🛒 Flipzon - E-Commerce Android App

Flipzon is a modern E-Commerce Android application built using Kotlin and MVVM architecture. It allows users to browse products, 
manage a shopping cart, and perform checkout operations with both online and offline support.

📱 Features
🔐 User Authentication (Login)
🛍️ Product Listing with Pagination
🔍 Product Search with Debounce
🛒 Add to Cart (Offline Support)
🔄 Real-time Cart Updates
💳 Checkout Functionality
📶 Works Offline for Cart Operations
🛠️ Tech Stack

Technology	Purpose
Kotlin - 	Main programming language
MVVM - 	Architecture pattern
XML  - Layouts	UI Design
Retrofit - 	API calls
Room Database - 	Local storage (Cart)
Coroutines + Flow - 	Asynchronous programming
Glide - 	Image loadin
SharedPreferences	 - User session management
ViewBinding	 - Type-safe UI access
Material Components - 	UI components



🏗️ Architecture

This project follows MVVM (Model-View-ViewModel) architecture:

Model → Handles data (API + Database)
View → UI (Activities & Fragments)
ViewModel → Connects Model and View


🔄 Data Flow

View → ViewModel → Repository → API / Database
View observes LiveData from ViewModel
UI updates automatically when data changes


🔐 Login Flow

User enters credentials
LoginActivity → AuthViewModel
ViewModel → Repository → API (dummyjson)
On success → Save user in SharedPreferences
Navigate to Main Screen

🛍️ Product Flow

HomeFragment observes product data
ViewModel requests products from Repository
API call via Retrofit
Data returned → UI updated via LiveData


🛒 Cart Flow (Offline Supported)

Add to cart → Stored in Room DB
Update quantity → Room update
Remove item → Room delete
UI auto-updates using Flow

👉 No internet required for cart operations


💳 Checkout Flow

User clicks checkout
Cart data sent to API
On success → Cart cleared
On failure → Data remains محفوظ (retry later)


📦 API Used

Dummy API: 
Endpoints:
/auth/login
/products
/products/search
/carts/add


📄 Pagination

Uses limit-offset pagination
Example:
?limit=20&skip=0
?limit=20&skip=20
Loads more when user scrolls near bottom


🔍 Search Functionality

Uses debounce (500ms delay)
Prevents multiple API calls
Implemented using Coroutines


💾 Offline Support

Cart is fully stored in Room Database
Works without internet
Sync happens only during checkout
